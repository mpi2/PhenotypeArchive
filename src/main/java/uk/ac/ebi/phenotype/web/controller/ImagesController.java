/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.dao.OntologyTermDAO;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;

@Controller
public class ImagesController {

	private final Logger log = LoggerFactory.getLogger(ImagesController.class);

	// Initialize the translation map of solr field names -> English names
	private HashMap<String,String> solrFieldToEnglish = new HashMap<String,String>() {
		private static final long serialVersionUID = 1L;
		{
			put("expName", "Procedure");
			put("higherLevelMaTermName", "Anatomy");
			put("higherLevelMpTermName", "Phenotype group");
			put("annotationTermId", "Annotation term");
			put("subtype", "Type");
			put("accession", "Accession");
		}};

	@Autowired
	private ImagesSolrDao imagesSolrDao;

	@Autowired
	GenomicFeatureDAO gfDAO;
	
	@Autowired
	OntologyTermDAO otDAO;

	@RequestMapping("/largeImage")
	public String largeImage(Model model) {
		return "largeImage";
	}


	@RequestMapping("/images*")
	public String allImages(
			@RequestParam(required = false, defaultValue = "0", value = "start") int start,
			@RequestParam(required = false, defaultValue = "48", value = "length") int length,
			@RequestParam(required = false, defaultValue = "*:*", value = "q") String qIn,
			@RequestParam(required = false, defaultValue = "", value = "phenotype_id") String mpId,
			@RequestParam(required = false, defaultValue = "", value = "gene_id") String geneId,
			@RequestParam(required = false, defaultValue = "", value = "fq") String []filterField,
			@RequestParam(required = false, defaultValue = "", value = "facet.field") String facetField,
			@RequestParam(required = false, defaultValue = "", value = "qf") String qf,
			@RequestParam(required = false, defaultValue = "", value = "defType") String defType,
			HttpServletRequest request,
			Model model) {

		handleImagesRequest(start, length, qIn, mpId, geneId, filterField, qf, defType, model);
		
		model.addAttribute("breadcrumbText", getBreadcrumbs(request, qIn, mpId, geneId, filterField));

		return "images";
	}	
	
	/**
	 * Returns an HTML string representation of the "last mile" breadcrumb
	 * 
	 * 
	 * @param request
	 *            the request object 			
	 * @param qIn
	 *            query term passed in to the controller
	 * @param mpId
	 *            MP id passed in to the controller
	 * @param geneId
	 *            Gene (usually MGI) ID passed in to the controller
	 * @param filterField
	 *            Array of filter query parameters passed in to the controller
	 * 
	 * @return a raw HTML string containing the pieces of the query assembled
	 *         into a breadcrumb fragment
	 */
	private String getBreadcrumbs(HttpServletRequest request, String qIn, String mpId, String geneId, String[] filterField) {

		String baseUrl = (String) request.getAttribute("baseUrl");

		ArrayList<String> breadcrumbs = new ArrayList<String>();

		if (!qIn.equals("") && !qIn.contains(":") && !qIn.equals("*")) {
			breadcrumbs.add("Search term: " + qIn);
		}

		if (!geneId.equals("")) {
			// 3 is the MGI database ID
			GenomicFeature gf = gfDAO.getGenomicFeatureByAccessionAndDbId(geneId, 3);
			String value = gf.getSymbol();
			String geneBc = "<a href='"+baseUrl+"/genes/"+geneId+"'>"+ gf.getSymbol() + "</a>";
			breadcrumbs.add("Gene: " + geneBc);
		}

		if (!mpId.equals("")) {
			// 5 is the Mammalian Phenotype database ID
			OntologyTerm mpTerm = otDAO.getOntologyTermByAccessionAndDatabaseId(mpId, 5);
			String value = mpTerm.getName();
			String mpBc = "<a href='"+baseUrl+"/phenotypes/"+mpId+"'>"+ value + "</a>";
			breadcrumbs.add("Phenotype: " + mpBc);
		}

		if (!qIn.equals("") && !qIn.equals("*:*") && !qIn.equals("*") && qIn.contains(":")) {
			String[] parts = qIn.split(":");
			String key = solrFieldToEnglish.get(parts[0]);
			String value = parts[1].replaceAll("%20", " ").replaceAll("\"", "");
			breadcrumbs.add(key + ": " + value);
		}

		if (!filterField.equals("")) {
			for (String field : filterField) {

				String formatted = field.replaceAll("%20", " ").replaceAll("\"", "");

				ArrayList<String> orFields = new ArrayList<String>();

				for (String f : formatted.split(" OR ")) {
					String[] parts = f.split(":");
					String key = solrFieldToEnglish.get(parts[0]);
					if (key == null) {
						log.error("Cannot find " + parts[0] + " in translation map. Add the mapping in ImagesController static constructor");
						key = parts[0]; // default the key to the solr field name (ugly!)
					}

					String value = parts[1];
					
					if (key.equals("Anatomy")) {
						value = "<a href='"+baseUrl+"/search#q=*&core=images&fq=higherLevelMaTermName:\""+value+"\"'>"+ value + "</a>";
						orFields.add(key + ": " + value);
					} else {
						orFields.add(key + ": " + value);
					}
				}

				String bCrumb = org.apache.commons.lang.StringUtils.join(orFields, " OR ");
				
				// Surround the clauses joined with OR by parens
				if (orFields.size() > 1) { bCrumb = "(" + bCrumb + ")"; }
				breadcrumbs.add(bCrumb);
			}
		}
		
		// Encase all the breadcrumb pieces in boxes 
		for (int i = 0; i<breadcrumbs.size(); i++) {
			breadcrumbs.set(i, "<span>"+breadcrumbs.get(i)+"</span>");
		}

		return org.apache.commons.lang.StringUtils.join(breadcrumbs, " AND ");
	}

	private void handleImagesRequest(int start, int length, String qIn,
			String mpId, String geneId, String[] filterField, String qf,
			String defType, Model model) {
		String q = qIn;
		String queryTerms = ""; //used for a human readable String of the query for display on the results page
		QueryResponse imageDocs = null;
		String filterQueries="";
		for(String field:filterField){
			System.out.println("filterField in controller="+field);
			filterQueries+="&fq="+field;
		}
		java.util.List <String>filterList = Arrays.asList(filterField);

		if (!geneId.equals("")) {
			queryTerms = geneId;
			q = "accession:" + geneId.replace("MGI:", "MGI\\:");
			queryTerms = gfDAO.getGenomicFeatureByAccession(geneId).getSymbol();
		}

		if (!mpId.equals("")) {
			queryTerms = mpId;
			q = "annotationTermId:" + mpId.replace("MP:", "MP\\:");
			queryTerms = otDAO.getOntologyTermByAccessionAndDatabaseId(mpId, 5).getName();
		}

		if (mpId.equals("") && geneId.equals("")) {
			queryTerms = "";
		}

		if(!qIn.equals("*:*")) {
			queryTerms = q.replaceAll("expName:", "").replaceAll("\"", "");
			q = q + " AND " + qIn;
		}

		if(filterField.length>0) {
		
				queryTerms = humanizeStrings(filterField, queryTerms);
			
		}

		System.out.println("q="+q);
		
		imageDocs = imagesSolrDao.getFilteredDocsForQuery(q,
				filterList, qf,defType,  start, length);
		if(imageDocs!=null){
		model.addAttribute("images", imageDocs.getResults());
		System.out.println("image count="+imageDocs.getResults().getNumFound());
		model.addAttribute("imageCount", imageDocs.getResults().getNumFound());
		model.addAttribute("q", q);
		model.addAttribute("filterQueries", filterQueries);
		model.addAttribute("filterField", filterField);
		model.addAttribute("qf", qf);//e.g. auto_suggest
		//model.addAttribute("filterParam", filterParam);
		model.addAttribute("queryTerms", queryTerms);
		model.addAttribute("start", start);
		model.addAttribute("length", length);
		model.addAttribute("defType", defType);
		}else{
			model.addAttribute("solrImagesError", "");
		}
	}

	private String humanizeStrings(String[] filterField, String queryTerms) {
		List<String> terms = new ArrayList<String>();
		for(String filter: filterField) {
			//System.out.println("filterField="+filter);
			if(!filter.equals("annotationTermId:M*")){//dont add M* to human readable form
			terms.add(WordUtils.capitalize(filter.replaceAll(".*:", "").replaceAll("\"", "")));
			}
		}
		queryTerms += ": " + StringUtils.join(terms, ", ");
		return queryTerms;
	}

	
	@RequestMapping("/smallImagesFragment")
	public String smallImagesFragment(
			@RequestParam(required = false, defaultValue = "0", value = "start") int start,
			@RequestParam(required = false, defaultValue = "4", value = "length") int length,
			@RequestParam(required = false, defaultValue = "*:*", value = "q") String qIn,
			@RequestParam(required = false, defaultValue = "", value = "mpid") String mpId,
			@RequestParam(required = false, defaultValue = "", value = "gene_id") String geneId,
			@RequestParam(required = false, defaultValue = "", value = "fq") String []filterField,
			@RequestParam(required = false, defaultValue = "", value = "facet.field") String facetField,
			@RequestParam(required = false, defaultValue = "", value = "qf") String qf,
			@RequestParam(required = false, defaultValue = "", value = "defType") String defType,
			Model model) {
		
		//currently this is pretty much the same logic as the/ images handler/response but with short default length so we could just have a param in the images request that says - get me small images?
		//then return the smallImagesFragment view...
		handleImagesRequest(start, length, qIn, mpId, geneId, filterField, qf,
				defType, model);
		
		return "smallImagesFragment";

	}

}
