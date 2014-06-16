/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.Config;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.axis2.transport.http.util.RESTUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.coode.parsers.oppl.OPPLScript_OPPLParser.query_return;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.dao.OntologyTermDAO;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;

@Controller
public class ImagesController {

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	private final Logger log = LoggerFactory.getLogger(ImagesController.class);

	// Initialize the translation map of solr field names -> English names
	private Set<String> doNotShowFields = new HashSet<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("!expName");
		}
	};

	// Initialize the translation map of solr field names -> English names
	private HashMap<String, String> solrFieldToEnglish = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("expName", "Procedure");
			put("annotated_or_inferred_higherLevelMaTermName", "Anatomy");
			put("annotatedHigherLevelMpTermName", "Phenotype group");
			put("annotationTermId", "Annotation term");
			put("subtype", "Type");
			put("accession", "Accession");
		}
	};

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
			@RequestParam(required = false, defaultValue = "25", value = "length") int length,
			@RequestParam(required = false, defaultValue = "*:*", value = "q") String qIn,
			@RequestParam(required = false, defaultValue = "", value = "phenotype_id") String mpId,
			@RequestParam(required = false, defaultValue = "", value = "gene_id") String geneId,
			@RequestParam(required = false, defaultValue = "", value = "fq") String[] filterField,
			@RequestParam(required = false, defaultValue = "", value = "facet.field") String facetField,
			@RequestParam(required = false, defaultValue = "", value = "qf") String qf,
			@RequestParam(required = false, defaultValue = "", value = "defType") String defType,
			@RequestParam(required = false, defaultValue = "", value = "anatomy_id") String maId,
			HttpServletRequest request, Model model) throws SolrServerException {

		handleImagesRequest(request, start, length, qIn, mpId, geneId,
				filterField, qf, defType, maId, model);

		model.addAttribute("breadcrumbText",
				getBreadcrumbs(request, qIn, mpId, geneId, filterField, maId));

		return "images";
	}

	@RequestMapping("/imagesb*")
	public String allImagesb(
			@RequestParam(required = false, defaultValue = "0", value = "start") int start,
			@RequestParam(required = false, defaultValue = "25", value = "length") int length,
			@RequestParam(required = false, defaultValue = "*:*", value = "q") String qIn,
			@RequestParam(required = false, defaultValue = "", value = "phenotype_id") String mpId,
			@RequestParam(required = false, defaultValue = "", value = "gene_id") String geneId,
			@RequestParam(required = false, defaultValue = "", value = "fq") String[] filterField,
			@RequestParam(required = false, defaultValue = "", value = "facet.field") String facetField,
			@RequestParam(required = false, defaultValue = "", value = "qf") String qf,
			@RequestParam(required = false, defaultValue = "", value = "defType") String defType,
			@RequestParam(required = false, defaultValue = "", value = "anatomy_id") String maId,
			HttpServletRequest request, Model model)
			throws SolrServerException, IOException, URISyntaxException {
		//only need to send the solr query part of the url to solr
		sendQueryStringToSolr(request, model);
//add breadcrumbs using different method than for images.jsp that is called from genes or phenotypes pages
		String emptyString="*:*";
		String queryString="";
		if(filterField.length>0) {
			if(!filterField[0].equals(emptyString)) {
				queryString+=filterField[0];
			}
			
		}
		if(qIn!=null && !qIn.equals(emptyString)) {
			queryString+= " AND search keyword: \"" + qIn + "\"";//URLDecoder.decode(request.getQueryString(), "UTF-8");
		}
		else {
			queryString+= " AND search keyword: \"\""; 
		}
		queryString=queryString.replace("annotatedHigherLevelMpTermName", "phenotype");
		queryString=queryString.replace("annotated_or_inferred_higherLevelMaTermName", "anatomy");
		queryString=queryString.replace("expName", "procedure");
		queryString=queryString.replace("subtype", "gene_subtype");
		model.addAttribute("breadcrumbText", queryString);

		return "imagesb";
	}

	private void sendQueryStringToSolr(HttpServletRequest request, Model model)
			throws IOException, URISyntaxException {

		String queryString = request.getQueryString();
		String startString = "0";
		String rowsString = "25";//the number of images passed back for each solr request
		if (request.getParameter("start") != null) {
			startString = request.getParameter("start");
		}
//		if (request.getParameter("rows") != null) {
//			rowsString = request.getParameter("rows");
//		}

		// Map params = request.getParameterMap();
		// Map newParamsMap=new HashMap<>();
		// newParamsMap.putAll(params);
		// newParamsMap.remove("rows");
		// newParamsMap.remove("start");
		String newQueryString = "";
		Enumeration keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			//System.out.println("key=" + key);

			// To retrieve a single value
			String value = request.getParameter(key);
			//System.out.println("value=" + value);
			//only add to our new query string if not rows or length as we want to set those to specific values in the jsp
			if (!key.equals("rows") && !key.equals("start")) {
				newQueryString += "&" + key + "=" + value;
				// If the same key has multiple values (check boxes)
				String[] valueArray = request.getParameterValues(key);

				for (int i = 0; i > valueArray.length; i++) {
					System.out.println("VALUE ARRAY" + valueArray[i]);
				}
			}
		}
		newQueryString+="&start="+startString+"&rows="+rowsString;
		
		//System.out.println("queryString=" + newQueryString);
		JSONObject imageResults = JSONRestUtil.getResults(config
				.get("internalSolrUrl") + "/images/select?" + newQueryString);
		JSONArray imageDocs = JSONRestUtil.getDocArray(imageResults);
		if (imageDocs != null) {
			model.addAttribute("images", imageDocs);
			int numberFound = JSONRestUtil
					.getNumberFoundFromJsonResponse(imageResults);
			//System.out.println("image count=" + numberFound);
			model.addAttribute("imageCount", numberFound);
			model.addAttribute("q", newQueryString);
			// model.addAttribute("filterQueries", filterQueries);
			// model.addAttribute("filterField", filterField);
			// model.addAttribute("qf", qf);//e.g. auto_suggest
			// //model.addAttribute("filterParam", filterParam);
			// model.addAttribute("queryTerms", queryTerms);
			model.addAttribute("start", Integer.valueOf(startString));
			model.addAttribute("length", Integer.valueOf(rowsString));
			// model.addAttribute("defType", defType);
		} else {
			model.addAttribute("solrImagesError", "");
		}
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
	private String getBreadcrumbs(HttpServletRequest request, String qIn,
			String mpId, String geneId, String[] filterField, String maId) {

		String baseUrl = (String) request.getAttribute("baseUrl");

		ArrayList<String> breadcrumbs = new ArrayList<String>();

		if (!qIn.equals("") && !qIn.contains(":") && !qIn.equals("*")) {
			breadcrumbs.add("Search term: " + qIn);
		}

		if (!geneId.equals("")) {
			// 3 is the MGI database ID
			GenomicFeature gf = gfDAO.getGenomicFeatureByAccessionAndDbId(
					geneId, 3);
			String value = gf.getSymbol();
//			String geneBc = "<a href='" + baseUrl + "/genes/" + geneId + "'>"
//					+ gf.getSymbol() + "</a>";
//			breadcrumbs.add("gene: \"" + geneBc + "\"");
			breadcrumbs.add("gene: \"" + geneId + "\"");
		}

		if (!mpId.equals("")) {
			// 5 is the Mammalian Phenotype database ID
			OntologyTerm mpTerm = otDAO
					.getOntologyTermByAccessionAndDatabaseId(mpId, 5);
			String value = mpTerm.getName();
			String mpBc = "<a href='" + baseUrl + "/phenotypes/" + mpId + "'>"
					+ value + "</a>";
			breadcrumbs.add("phenotype: \"" + mpBc + "\"");
		}

		if (!maId.equals("")) {
			// 5 is the Mammalian Phenotype database ID
			OntologyTerm maTerm = otDAO.getOntologyTermByAccession(maId);
			String value = maTerm.getName();
			String mpBc = "<a href='" + baseUrl + "/phenotypes/" + maId + "'>"
					+ value + "</a>";
			breadcrumbs.add("anatomy: \"" + mpBc + "\"");
		}

		if (!qIn.equals("") && !qIn.equals("*:*") && !qIn.equals("*")
				&& qIn.contains(":")) {
			String[] parts = qIn.split(":");
			String key = solrFieldToEnglish.get(parts[0]);
			String value = parts[1].replaceAll("%20", " ").replaceAll("\"", "");
			breadcrumbs.add(key + ": " + value);
		}

		if (!filterField.equals("")) {
			for (String field : filterField) {

				String formatted = field.replaceAll("%20", " ").replaceAll(
						"\"", "");

				ArrayList<String> orFields = new ArrayList<String>();

				for (String f : formatted.split(" OR ")) {
					String[] parts = f.split(":");
					if (!doNotShowFields.contains(parts[0])) {
						String key = solrFieldToEnglish.get(parts[0]);
						if (key == null) {
							log.error("Cannot find "
									+ parts[0]
									+ " in translation map. Add the mapping in ImagesController static constructor");
							key = parts[0]; // default the key to the solr field
											// name (ugly!)
						}

						String value = parts[1];

						if (key.equals("Anatomy")) {
							value = "<a href='"
									+ baseUrl
									+ "/search#q=*&core=images&fq=annotated_or_inferred_higherLevelMaTermName:\""
									+ value + "\"'>" + value + "</a>";
							orFields.add(key.toLowerCase() + ": \"" + value + "\"");
						} else {
							orFields.add(key.toLowerCase() + ": \"" + value + "\"");
						}
					}
				}
				String bCrumb = org.apache.commons.lang.StringUtils.join(
						orFields, " OR ");

				// Surround the clauses joined with OR by parens
				if (orFields.size() > 1) {
					bCrumb = "(" + bCrumb + ")";
				}

				if (!bCrumb.trim().equals("")) {
					breadcrumbs.add(bCrumb);
				}
			}
		}

		return org.apache.commons.lang.StringUtils.join(breadcrumbs, " AND ");
	}

	private void handleImagesRequest(HttpServletRequest request, int start,
			int length, String q, String mpId, String geneId,
			String[] filterField, String qf, String defType, String maId,
			Model model) throws SolrServerException {

		System.out.println("query string=" + request.getQueryString());
		String queryTerms = ""; // used for a human readable String of the query
								// for display on the results page
		QueryResponse imageDocs = null;
		String filterQueries = "";
		for (String field : filterField) {
			//System.out.println("filterField in controller=" + field);
			filterQueries += "&fq=" + field;
		}
		java.util.List<String> filterList = Arrays.asList(filterField);

		if (!geneId.equals("")) {
			queryTerms = geneId;
			q = "accession:" + geneId.replace("MGI:", "MGI\\:");
			queryTerms = gfDAO.getGenomicFeatureByAccession(geneId).getSymbol();
		}

		if (!mpId.equals("")) {
			queryTerms = mpId;
			q = "annotationTermId:" + mpId.replace("MP:", "MP\\:");
			queryTerms = otDAO.getOntologyTermByAccessionAndDatabaseId(mpId, 5)
					.getName();
		}

		if (!maId.equals("")) {
			queryTerms = maId;
			q = "annotationTermId:" + maId.replace("MA:", "MA\\:");
			queryTerms = otDAO.getOntologyTermByAccession(maId).getName();
			//System.out.println("query term set to:" + queryTerms);
		}

		if (mpId.equals("") && geneId.equals("") && maId.equals("")) {
			queryTerms = "";
		}

		if (!q.equals("*:*")) {
			queryTerms += " "
					+ q.replaceAll("expName:", "").replaceAll("\"", "");
			// q = q + " AND " + qIn;
		}

		if (filterField.length > 0) {

			queryTerms = humanizeStrings(filterField, queryTerms);

		}

		//System.out.println("q=" + q);

		imageDocs = imagesSolrDao.getFilteredDocsForQuery(q, filterList, qf,
				defType, start, length);
		if (imageDocs != null) {
			model.addAttribute("images", imageDocs.getResults());
//			System.out.println("image count="
//					+ imageDocs.getResults().getNumFound());
			model.addAttribute("imageCount", imageDocs.getResults()
					.getNumFound());
			model.addAttribute("q", q);
			model.addAttribute("filterQueries", filterQueries);
			model.addAttribute("filterField", filterField);
			model.addAttribute("qf", qf);// e.g. auto_suggest
			// model.addAttribute("filterParam", filterParam);
			model.addAttribute("queryTerms", queryTerms);
			model.addAttribute("start", start);
			model.addAttribute("length", length);
			model.addAttribute("defType", defType);
		} else {
			model.addAttribute("solrImagesError", "");
		}
	}

	private String humanizeStrings(String[] filterField, String queryTerms) {
		List<String> terms = new ArrayList<String>();
		for (String filter : filterField) {
			// System.out.println("filterField="+filter);
			if (!filter.equals("annotationTermId:M*")) {// dont add M* to human
														// readable form
				terms.add(WordUtils.capitalize(filter.replaceAll(".*:", "")
						.replaceAll("\"", "")));
			}
		}
		queryTerms += ": " + StringUtils.join(terms, ", ");
		return queryTerms;
	}

}
