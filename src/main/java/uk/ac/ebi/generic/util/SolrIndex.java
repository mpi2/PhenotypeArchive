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
package uk.ac.ebi.generic.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.web.util.DrupalHttpProxy;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

@Service
public class SolrIndex {

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	/**
	 * Return the number of documents found for a specified solr query on a
	 * specified core.
	 * 
	 * @param query
	 *            the query
	 * @param core
	 *            which solr core to query
	 * @param mode
	 *            which configuration mode to operate in
	 * @param solrParams
	 *            the default solr parameters to also restrict the query
	 * @return integer count of the number of matching documents
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Integer getNumFound(String query, String core, String mode,
			String solrParams) throws IOException, URISyntaxException {
		JSONObject json = getResults(composeSolrUrl(core, mode, query,
				solrParams, 0, 0, false));
		return json.getJSONObject("response").getInt("numFound");
	}

	/**
	 * Gets the json string representation for the query.
	 * 
	 * @param query
	 *            the query for which documents
	 * @param core
	 *            the solr core to query
	 * @param gridSolrParams
	 *            the default solr parameters to append to the query
	 * @param mode
	 *            the configuration mode to operate in
	 * @param start
	 *            where to start the offset
	 * @param length
	 *            how many documents to return
	 * @param showImgView
	 *            is this query showing the annotation view of the images
	 *            (true/false)
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getDataTableJson(String query, String core,
			String gridSolrParams, String mode, int start, int length,
			boolean showImgView) throws IOException, URISyntaxException {

		if (gridSolrParams.equals("")) {
			gridSolrParams = "qf=auto_suggest&defType=edismax&wt=json&q=*:*";
		}

		return getResults(composeSolrUrl(core, mode, query, gridSolrParams,
				start, length, showImgView));

	}

	/**
	 * Get rows for saving to an external file.
	 * 
	 * @param core
	 *            the solr core to query
	 * @param gridSolrParams
	 *            the default parameters to use in the query
	 * @param start
	 *            where to start the query
	 * @param gridFields
	 *            the default solr parameters to append to the query
	 * @param length
	 *            how many documents to return
	 * @return json representation of the results of the solr query
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getDataTableExportRows(String core,
			String gridSolrParams, String gridFields, int start, int length)
			throws IOException, URISyntaxException {

		System.out.println("GRID SOLR PARAMS : " + gridSolrParams);
		
		if (core.equals("gene")) {			
			//gridFields += ",imits_report_phenotyping_complete_date,imits_report_genotype_confirmed_date,imits_report_mi_plan_status,escell,ikmc_project,imits_phenotype_started,imits_phenotype_complete,imits_phenotype_status";
		}

		//String newgridSolrParams = gridSolrParams + "&rows=" + length
		gridSolrParams = gridSolrParams.replace("rows=10", "rows="+length);
		String newgridSolrParams = gridSolrParams 		
				+ "&start=" + start + "&fl=" + gridFields;

		String url = composeSolrUrl(core, "", "", newgridSolrParams, start,
				length, false);
		
		log.debug("Export data URL: " + url);
		return getResults(url);
	}

	/**
	 * Prepare a url for querying the solr indexes based on the passed in
	 * arguments.
	 * 
	 * @param core
	 *            which solr core to query
	 * @param mode
	 *            which configuration mode to operate in
	 * @param query
	 *            what to query
	 * @param gridSolrParams
	 *            default parameters to add to the solr query
	 * @param iDisplayStart
	 *            starting point of the query
	 * @param iDisplayLength
	 *            length of the query
	 * @param showImgView
	 *            which image mode to operate in
	 * @return the constructed url including all parameters
	 */
	private String composeSolrUrl(String core, String mode, String query,
			String gridSolrParams, Integer iDisplayStart,
			Integer iDisplayLength, boolean showImgView) {
		
		String internalSolrUrl = config.get("internalSolrUrl");
		String url = internalSolrUrl + "/" + core + "/select?";
						
//		System.out.println(("BASEURL: " + url));
		if (mode.equals("mpPage")) {
			url += "q=" + query;
			url += "&start=0&rows=0&wt=json&qf=auto_suggest&defType=edismax";
		} else if (mode.equals("geneGrid")) {			
			url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
					+ iDisplayLength;
//			System.out.println("GENE PARAMS: " + url);
		} else if (mode.equals("pipelineGrid")) {
			url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
					+ iDisplayLength;
//			System.out.println("PROTOCOL PARAMS: " + url);
		} else if (mode.equals("impc_imagesGrid")) {
			url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
					+ iDisplayLength;
			if (!showImgView) {
				url += "&facet=on&facet.field=symbol_gene&facet.field=procedure_name&facet.mincount=1&facet.limit=-1";
			}
//			System.out.println("IMPC_IMG PARAMS: " + url); 
		} else if (mode.equals("imagesGrid")) {
			url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
					+ iDisplayLength;
			if (!showImgView) {
				url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
			}
//			System.out.println("IMG PARAMS: " + url);
		} else if (mode.equals("mpGrid")) {
			url += gridSolrParams.replaceAll(" ", "%20") + "&start="
					+ iDisplayStart + "&rows=" + iDisplayLength;
//			System.out.println("MP PARAMS: " + url);
		} else if (mode.equals("maGrid")) {
			url += gridSolrParams.replaceAll(" ", "%20") + "&start="
					+ iDisplayStart + "&rows=" + iDisplayLength;
//			System.out.println("MA PARAMS: " + url);
		} else if ( mode.equals("diseaseGrid") ){			
			url += gridSolrParams.replaceAll(" ", "%20") + "&start="
					+ iDisplayStart + "&rows=" + iDisplayLength;
//			System.out.println("DISEASE PARAMS: " + url);
		} else if (mode.equals("ikmcAlleleGrid")) {
			url += "q=" + query;
			url += "&start=0&rows=0&wt=json";
//			System.out.println("IKMC ALLELE PARAMS: " + url);
		} else if (mode.equals("all") || mode.equals("page") || mode.equals("")) { // download search page result
			url += gridSolrParams;
			if (core.equals("images") && !showImgView) {				
				url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
			}
//			System.out.println("GRID DUMP PARAMS - " + core + ": " + url);
		}
		// OTHER solrCoreNames to be added here
		
		return url;
	}



	/*public String deriveLatestPhenotypingStatus(JSONObject doc) {

		// order of status: latest to oldest (IMPORTANT for deriving correct
		// status)

		try {
			// Phenotyping complete
			if (doc.containsKey("imits_phenotype_complete")) {
				JSONArray complete = doc
						.getJSONArray("imits_phenotype_complete");
				for (Object c : complete) {
					if (c.toString().equals("1")) {
						return "Complete";
					}
				}
			}

			// Phenotyping started
			if (doc.containsKey("imits_phenotype_started")) {
				JSONArray started = doc.getJSONArray("imits_phenotype_started");
				for (Object s : started) {
					if (s.toString().equals("1")) {
						return "Started";
					}
				}
			}

			// Phenotype Attempt Registered
			if (doc.containsKey("imits_phenotype_status")) {
				JSONArray statuses = doc.getJSONArray("imits_phenotype_status");
				for (Object s : statuses) {
					if (s.toString().equals("Phenotype Attempt Registered")) {
						return "Attempt Registered";
					}
				}
			}
			if (doc.containsKey("hasQc")) {				
					return "QCed data available";			
			}
			
		} catch (Exception e) {
			log.error("Error getting phenotyping status");
			log.error(e.getLocalizedMessage());
		}

		// if all the above fails: no phenotyping data yet
		//return "Not Applicable";
		return "";
		
	}*/

	/**
	 * Generates a map of label, field, link representing a facet field
	 * 
	 * @param names
	 *            an array of strings representing the facet ID and the name of
	 *            the facet
	 * @param baseUrl
	 *            the base url of the generated links
	 * @return a map represneting the facet, facet label and link
	 */
	public Map<String, String> renderFacetField(String[] names, HttpServletRequest request) {

		String hostName = request.getAttribute("mappedHostname").toString();
		String baseUrl =  request.getAttribute("baseUrl").toString(); 
		// key: display label, value: facetField
		Map<String, String> hm = new HashMap<String, String>();
		String name = names[0];
		String id = names[1];
		
		if (id.startsWith("MP:")) {
			String url = baseUrl + "/phenotypes/" + id;
			hm.put("label", "MP");
			hm.put("field", "annotationTermName");
			//hm.put("field", "mpTermName");
			hm.put("fullLink", hostName + url);
			hm.put("link", "<a href='" + url + "'>" + name + "</a>");
		} else if (id.startsWith("MA:")) {
			String url = baseUrl + "/anatomy/" + id;
			hm.put("label", "MA");
			hm.put("field", "annotationTermName");	
			hm.put("fullLink", hostName + url);
			hm.put("link", "<a href='" + url + "'>" + name + "</a>");
		} else if (id.equals("exp")) {
			hm.put("label", "Procedure");
			hm.put("field", "expName");
			hm.put("link", name);
		} else if (id.startsWith("MGI:")) {
			String url = baseUrl + "/genes/" + id;
			hm.put("label", "Gene");
			hm.put("field", "symbol");
			hm.put("fullLink", hostName + url);
			hm.put("link", "<a href='" + url + "'>" + name + "</a>");
		}
		hm.put("id", id);	
		return hm;
	}

	/**
	 * Merge all the facets together based on whether they include an underscore
	 * because underscore facet names means that the solr field name represents
	 * a facet and it's identifier, which are the ones we are concerned with.
	 * 
	 * Each facet is formatted as an array where, starting at the zeroth
	 * element, it alternates between facet name and count for that facet
	 * 
	 * @param facetFields
	 *            the json representation of all the facets as arrays
	 * @return a json array which combined all the passed in facets filtered for
	 *         inclusion of underscore
	 */
	public JSONArray mergeFacets(JSONObject facetFields) {

		JSONArray fields = new JSONArray();

		// Initialize a list on creation using an inner anonymous class
		List<String> facetNames = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("symbol_gene");
				add("expName_exp");
				add("mpTermName");
				add("maTermName");
			}
		};
		for (String facet : facetNames) {

			JSONArray arr = facetFields.getJSONArray(facet);
			for (int i = 0; i < arr.size(); i = i + 2) {
				// We only want facet fields that contain an underscore
				// as it contains ID info we want
				if (((String) arr.get(i)).contains("_")) {
					fields.add(arr.get(i));
					fields.add(arr.get(i + 1));
				}
			}
		}

		return fields;
	}
	
	public class AnnotNameValCount {
		public String name;
		public String facet;
		public String val;
		public String link;
		public int imgCount;
	}
	
	public List<AnnotNameValCount> mergeImpcFacets(JSONObject json, String baseUrl) {
		
		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
	
		List<AnnotNameValCount> annots = new ArrayList<>();
		
		Map<String, String> hm = new HashMap<String, String>();
		hm.put("symbol_gene", "Gene");
		hm.put("procedure_name", "Procedure");
		
		// Initialize a list on creation using an inner anonymous class
		List<String> facetNames = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("symbol_gene");  // facet field name
				add("procedure_name");
				//add("mpTermName");
				//add("maTermName");
			}
		};
		for (String facet : facetNames) {
			
			//JSONObject arr = facetFields.getJSONArray(facet);
			JSONArray arr = json.getJSONObject("facet_counts").getJSONObject("facet_fields").getJSONArray(facet);
			for (int i = 0; i < arr.size(); i = i + 2) {
				
				AnnotNameValCount annotNameValCount = new AnnotNameValCount();
				
				annotNameValCount.name     = hm.get(facet);
				annotNameValCount.facet    = facet;
				annotNameValCount.val      = arr.get(i).toString();
				
				if ( facet.equals("symbol_gene") ){
					annotNameValCount.facet = "gene_symbol"; // query field name
					String[] fields = annotNameValCount.val.split("_");
					annotNameValCount.val = fields[0];
					annotNameValCount.link = baseUrl + "/genes/" + fields[1];
				}
				annotNameValCount.imgCount = Integer.parseInt(arr.get(i+1).toString());
				annots.add(annotNameValCount);
			}
		}
		
		return annots;
	}
	/**
	 * Get the IMPC status for a gene identified by accession id.
	 * 
	 * @param accession
	 *            the MGI id of the gene in question
	 * @return the status
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String getGeneStatus(String accession) throws IOException,
			URISyntaxException {

		String url = config.get("internalSolrUrl")
				+ "/gene/select?wt=json&q=mgi_accession_id:"
				+ accession.replace(":", "\\:");

		log.info("url for geneDao=" + url);

		JSONObject json = getResults(url);
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");

		if (docs.size() > 1) {
			log.error("Error, Only expecting 1 document from an accession/gene request");
		}

		String geneStatus = docs.getJSONObject(0).getString("status");

		log.debug("gene status=" + geneStatus);

		return geneStatus;
	}

/*	public List<Map<String, String>> getGenesWithPhenotypeStartedFromAll()
			throws IOException, URISyntaxException {
		List<Map<String, String>> geneStatuses = new ArrayList<Map<String, String>>();
		String url = config.get("internalSolrUrl")
				+ "/gene/select?wt=json&q=*%3A*&version=2.2&start=0&rows=100";// 2147483647";//max
																				// size
																				// of
																				// int
																				// to
																				// make
																				// sure
																				// we
																				// get
																				// back
																				// all
																				// the
																				// rows
																				// in
																				// index

		log.info("url for geneDao=" + url);

		JSONObject json = getResults(url);
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		for (Object doc : docs) {
			JSONObject jsonObject = (JSONObject) doc;
			String geneStatus = this.deriveLatestPhenotypingStatus(jsonObject);
			if (geneStatus.equals("Started")) {
				String mgi = jsonObject.getString("mgi_accession_id");
				String symbol = jsonObject.getString("marker_symbol");
				Map map = new HashMap<String, String>();
				map.put("mgi", mgi);
				map.put("symbol", symbol);
				geneStatuses.add(map);
			}
		}
		return geneStatuses;
	}*/

	/**
	 * Get the results of a query from the provided url.
	 * 
	 * @param url
	 *            the URL from which to get the content
	 * @return a JSONObject representing the result of the query
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getResults(String url) throws IOException,
			URISyntaxException {
		
		log.debug("GETTING CONTENT FROM: " + url);
		
		HttpProxy proxy = new HttpProxy();
		
		try {
			String content = proxy.getContent(new URL(url));
			return (JSONObject) JSONSerializer.toJSON(content);
		} 
		catch (Exception e) {
        }
		return null;
	}

	/**
	 * Get the results of a query from the provided url using a proxy that
	 * includes the drupal session cookie.
	 * 
	 * @param url
	 *            the URL from which to get the content
	 * @return a JSONObject representing the result of the query
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getResults(DrupalHttpProxy drupalProxy, String url)
			throws IOException, URISyntaxException {
		String content = "";

		log.debug("GETTING CONTENT FROM: " + url);
//		System.out.println("CHK URL: " + url);
		if (drupalProxy != null) {
			content = drupalProxy.getContent(new URL(url));
		} else {
			return getResults(url);
		}

		return (JSONObject) JSONSerializer.toJSON(content);
	}

	/**
	 * Get the MP solr document associated to a specific MP term. The document
	 * contains the relations to MA terms and sibling MP terms
	 * 
	 * @param phenotype_id
	 *            the MP term in question
	 * @return a (json) solr document with the results of doing a solr query on
	 *         the mp index for the mp term in question
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getMpData(String phenotype_id) throws IOException,
			URISyntaxException {
		String url = config.get("internalSolrUrl")
				+ "/mp/select?wt=json&qf=mp_id&defType=edismax&q="
				+ phenotype_id;

		return getResults(url);
	}
        
	public JSONObject getImageInfo(int imageId) throws SolrServerException,
			IOException, URISyntaxException {

		String url = config.get("internalSolrUrl")
				+ "/images/select?wt=json&q=id:" + imageId;
		JSONObject json = getResults(url);
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");

		if (docs.size() > 1) {
			log.error("Error, Only expecting 1 document from an accession/gene request");
		}
		if(docs.size()<1) {//if nothing returned return an empty json object
			return new JSONObject();
		}

		JSONObject imageInfo = docs.getJSONObject(0);
		return imageInfo;

	}

	public Map<String, JSONObject> getExampleImages(int controlImageId,
			int expImageId) throws SolrServerException, IOException,
			URISyntaxException {
		Map<String, JSONObject> map = new HashMap<String, JSONObject>();
		JSONObject controlDocument = this.getImageInfo(controlImageId);
		JSONObject expDocument = this.getImageInfo(expImageId);

		map.put("control", controlDocument);
		map.put("experimental", expDocument);
		return map;
	}
	
	public Map<String, Map<String, JSONArray>> getGO2ImpcGeneAnnotationStats() throws IOException, URISyntaxException{
	//public void getGO2ImpcGeneAnnotationStats() throws IOException, URISyntaxException{
		String internalBaseSolrUrl = config.get("internalSolrUrl") + "/gene/select?";
		
		
		Map<String, Map<String, JSONArray>> statusEvidCount = new LinkedHashMap<String, Map<String, JSONArray>>();
		
		List<String> phenoStatuses = new ArrayList<String>();
		phenoStatuses.add("Phenotyping Complete");
		phenoStatuses.add("Phenotyping Started");
		
		for ( String status : phenoStatuses ){
			String goParams = "q=latest_phenotype_status:\"" + status + "\" AND go_term_id:* AND go_term_evid:*&wt=json&rows=0&facet=on&facet.limit=-1&facet.field=go_term_evid";
			String goQuery = internalBaseSolrUrl + goParams;
			
			String noGoParams = "q=latest_phenotype_status:\"" + status + "\" AND -go_term_id:*&wt=json&rows=0";
			String noGoQuery = internalBaseSolrUrl + noGoParams;
			
			Map<String, String> annotUrls = new LinkedHashMap<>();
			String noGo  = "w/o GO";
			String hasGo = "w/  GO";
			
			annotUrls.put(noGo, noGoQuery);
			annotUrls.put(hasGo, goQuery);
			
			Map<String, JSONArray> annotCounts = new LinkedHashMap<>();
			
			Iterator it = annotUrls.entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        String annot = pairs.getKey().toString();
		        String query = pairs.getValue().toString();
		        it.remove(); // avoids a ConcurrentModificationException
		        JSONObject json = getResults(query);
		        //System.out.println("QUERY: " + query);
		        
		        if ( annot.equals(hasGo) ){ 
		        	JSONArray jfacet = json.getJSONObject("facet_counts").getJSONObject("facet_fields").getJSONArray("go_term_evid");
		        	annotCounts.put(annot, jfacet);
		        }
		        else {
		        	int numFound = json.getJSONObject("response").getInt("numFound");
		        	JSONArray ja = new JSONArray();
		        	ja.add(numFound);
		        	annotCounts.put(annot, ja);
		        }
			}
			
			statusEvidCount.put(status, annotCounts);
			
		}
		return statusEvidCount;
	}

}
