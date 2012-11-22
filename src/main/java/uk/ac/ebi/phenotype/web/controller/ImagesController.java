/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.dao.OntologyTermDAO;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;

@Controller
public class ImagesController {

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
			@RequestParam(required = false, defaultValue = "", value = "mpid") String mpId,
			@RequestParam(required = false, defaultValue = "", value = "gene_id") String geneId,
			@RequestParam(required = false, defaultValue = "", value = "fq") String []filterField,
			@RequestParam(required = false, defaultValue = "", value = "facet.field") String facetField,
			@RequestParam(required = false, defaultValue = "", value = "qf") String qf,
			@RequestParam(required = false, defaultValue = "", value = "defType") String defType,
			Model model) {

		handleImagesRequest(start, length, qIn, mpId, geneId, filterField, qf,
				defType, model);
		

		return "imageResults";
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
		}

		if(filterField.length>0) {
		
				queryTerms = humanizeStrings(filterField, queryTerms);
			
		}

		System.out.println("q="+q);
		
		imageDocs = imagesSolrDao.getFilteredDocsForQuery(q,
				filterList, qf,defType,  start, length);

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
