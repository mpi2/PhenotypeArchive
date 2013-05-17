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
package uk.ac.ebi.generic.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.data.imits.ColonyStatus;
import uk.ac.ebi.phenotype.web.util.DrupalHttpProxy;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

@Service
public class SolrIndex {
	
	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	@Resource(name="globalConfiguration")
	private Map<String, String> config;
	
	/**
	 * Return the number of documents found for a specifed solr query on a
	 * specified core
	 * @param query the query
	 * @param core which solr core to query
	 * @param mode which configuration mode to operate in
	 * @param solrParams the default solr params to also restricy the query
	 * @return integer count of the number of matching documents
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Integer getNumFound(String query, String core, String mode, String solrParams) throws IOException, URISyntaxException {
		JSONObject json = getResults(composeSolrUrl(core, mode, query, solrParams, 0, 0, false));
		JSONObject response = json.getJSONObject("response");
		return response.getInt("numFound");
	}

	/**
	 * Gets the json string representation for the query 
	 * @param query the query for which documents
	 * @param core the solr core to query
	 * @param gridSolrParams the default solr parameters to append to the query
	 * @param mode the configuration mode to operate in
	 * @param iDisplayStart where to start the offset 
	 * @param iDisplayLength how many documents to return
	 * @param showImgView is this query showing the annotation view of the images (true/false) 
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getDataTableJson(String query, String core,
			String gridSolrParams, String mode, int iDisplayStart,
			int iDisplayLength, boolean showImgView)
			throws IOException, URISyntaxException {

		if (gridSolrParams.equals("")) {
			gridSolrParams = "qf=auto_suggest&defType=edismax&wt=json&q=*:*";
		}

		return getResults(composeSolrUrl(core, mode, query, gridSolrParams, iDisplayStart, iDisplayLength, showImgView));

	}

	// populating jQuery dataTable on the server-side
	// handling saving jQuery dataTable data to external files
	public JSONObject getDataTableExportRows(String fileType, String core,
			String gridSolrParams, int rowStart, boolean showImgView,
			String gridFields, HttpServletRequest request, Integer iDisplayLength)
			throws IOException, URISyntaxException {

		if (core.equals("gene")) {
			gridFields += ",imits_report_phenotyping_complete_date,imits_report_genotype_confirmed_date,imits_report_mi_plan_status,escell,ikmc_project";
		}

		String newgridSolrParams = gridSolrParams + "&rows=" + iDisplayLength + "&start=" + rowStart + "&fl=" + gridFields;		

		String url = composeSolrUrl(core, "", "", newgridSolrParams, rowStart, iDisplayLength, false);
		System.out.println(url);
		return fetchJsonforDataTableExport(url, core);
	}

	public JSONObject fetchJsonforDataTableExport(String urlString, String solrCoreName) throws IOException, URISyntaxException {
	
		JSONObject j = getResults(urlString);

		return j;	
	}
	
	public String[][] composeXlsTableData(List<String> rows) {

		int rowNum = rows.size();// - 1; // omit title row
		int colNum = (rows.size() > 0) ? rows.get(0).split("\t").length : 0;
		
		String[][] tableData = new String[rowNum][colNum];
		
		// add one to omit title row
		for( int i=0; i<rowNum; i++ ){

			String[] colVals = rows.get(i).split("\t");

			for (int j=0; j<colVals.length; j++) {				
				tableData[i][j] = colVals[j];
			}
		}
		return tableData;
	}
	

	
	private String composeSolrUrl(String core, String mode, String query, String gridSolrParams, Integer iDisplayStart, Integer iDisplayLength, boolean showImgView) {
		String internalSolrUrl = config.get("internalSolrUrl");

		String url = internalSolrUrl + "/" + core + "/select?";
		
		if (mode.equals("mpPage")) {
			url += "q=" + query;
			url += "&start=0&rows=0&wt=json&qf=auto_suggest&defType=edismax";
		} 
		else if (mode.equals("geneGrid")) {
			url += gridSolrParams
				+ "&start=" + iDisplayStart
				+ "&rows=" + iDisplayLength;
			System.out.println("GENE PARAMS: " + url);
		}
		else if (mode.equals("pipelineGrid")){
			url += gridSolrParams					
				+ "&start=" + iDisplayStart
				+ "&rows=" + iDisplayLength;				
			System.out.println("PROTOCOL PARAMS: " + url);
		}
		else if (mode.equals("imagesGrid")){
			url += gridSolrParams					
				+ "&start=" + iDisplayStart
				+ "&rows=" + iDisplayLength;
			if ( !showImgView ){
				url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
			}	
			System.out.println("IMG PARAMS: " + url);
		}
		else if (mode.equals("mpGrid")){
			url += gridSolrParams.replaceAll(" ", "%20")
				+ "&start=" + iDisplayStart
				+ "&rows=" + iDisplayLength;	
			System.out.println("MP PARAMS: " + url);
		}
		else if (mode.equals("ikmcAlleleGrid")){
			url += "q=" + query;
			url += "&start=0&rows=0&wt=json&defType=edismax";
			System.out.println("IKMC ALLELE PARAMS: " + url);
		}
		else if ( mode.equals("all") || mode.equals("page") || mode.equals("") ) {
			url += gridSolrParams;
			if ( core.equals("images") && !showImgView ){
				url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
			}
			System.out.println("GRID DUMP PARAMS - " + core + ": " + url);
		}
		// OTHER solrCoreNames to be added here
		
		return url;
	}
	

	
	
	public String deriveLatestPhenotypingStatus(JSONObject doc){	
		
		// order of status: latest to oldest (IMPORTANT for deriving correct status)
		// returns the latest status (Complete or Started or Phenotype Attempt Registered)		
		
		//Phenotyping complete
		try {
			if ( doc.getJSONArray("imits_phenotype_complete").size() > 0) {
				JSONArray complete = doc.getJSONArray("imits_phenotype_complete");
				for(Object c : complete){
					if ( c.toString().equals("1") ){
						return "Complete";
					}					
				}				
			}
		} 
		catch (Exception e) {		   		
			//e.printStackTrace();
		}
		
		//Phenotyping started
		try {
			if ( doc.getJSONArray("imits_phenotype_started").size() > 0) {	 
				JSONArray started = doc.getJSONArray("imits_phenotype_started");
				for(Object s : started){
					if ( s.toString().equals("1") ){
						return "Started";
					}					
				}
			}
		} 
		catch (Exception e) {		  			
		    //e.printStackTrace();
		}
		
		//Phenotype Attempt Registered
		try {	
			if ( doc.getJSONArray("imits_phenotype_status").size() > 0 ){
				JSONArray statuses = doc.getJSONArray("imits_phenotype_status");
				for(Object s : statuses){
					if ( s.toString().equals("Phenotype Attempt Registered") ){
						return "Attempt Registered";
					}					
				}							
			}
		} 
		catch (Exception e) {		 			
		    //e.printStackTrace();
		}	
		
		// if all the above fails: no phenotyping data yet
		return "Not Applicable";
	}

	public HashMap<String, String> renderFacetField(String[] names, HttpServletRequest request){				
			
		HashMap<String, String> hm = new HashMap<String, String>(); // key: display label, value: facetField
		String name = names[0];
		String id = names[1];	
		
		if ( id.startsWith("MP:")){	
			String url = request.getAttribute("baseUrl") + "/phenotypes/" + id;
			hm.put("label", "MP");
			hm.put("field", "annotationTermName");
			hm.put("link", "<a href='" + url + "'>"+ name + "</a>");			
		}
		else if ( id.startsWith("MA:")){	
			hm.put("label", "MA");
			hm.put("field","annotationTermName");
			hm.put("link", name);
		}
		else if (id.equals("exp")){ 
			hm.put("label", "Procedure");			
			hm.put("field", "expName");
			hm.put("link", name);
		}
		else if (id.startsWith("MGI:")){
			String url = request.getAttribute("baseUrl") + "/genes/" + id;
			hm.put("label", "Gene");
			hm.put("field", "symbol");
			hm.put("link", "<a href='" + url + "'>"+ name + "</a>");
		}	
		return hm;
	}
	
	public JSONArray mergeFacets(JSONObject facetFields){

		JSONArray fields = new JSONArray();
		
		List<String> facetNames = new ArrayList<String>() {{
			add("symbol_gene");
			add("expName_exp");
			add("mpTermName");
			add("maTermName");
		}};

		for (String facet : facetNames){
			JSONArray arr = facetFields.getJSONArray(facet);

			for (int i=0; i < arr.size(); i=i+2) {
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

	/**
	 * Get the IMPC status for a gene identified by accession id.
	 * 
	 * @param accession the MGI id of the gene in question
	 * @return the status
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String getGeneStatus(String accession) throws IOException, URISyntaxException {
		String url = config.get("internalSolrUrl") + 
			"/gene/select?wt=json&q=mgi_accession_id:" + 
			accession.replace(":", "\\:");

		log.info("url for geneDao=" + url);

		JSONObject jsonObject = getResults(url);
		JSONArray docs = jsonObject.getJSONObject("response").getJSONArray("docs");

		if (docs.size() > 1) {
			log.error("Error, Only expecting 1 document from an accession/gene request");
		}

		String geneStatus = docs.getJSONObject(0).getString("status");
		log.debug("gene status=" + geneStatus);

		return geneStatus;
	}


	/**
	 * Get the results of a query from the provided url.
	 * 
	 * @param url the URL from which to get the content
	 * @return a JSONObject representing the result of the query
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getResults(String url) throws IOException, URISyntaxException {
		String content = "";

		log.debug("GETTING CONTENT FROM: "+url);

		HttpProxy proxy = new HttpProxy();
		content = proxy.getContent(new URL(url));

		return (JSONObject) JSONSerializer.toJSON(content);
	}

	/**
	 * Get the results of a query from the provided url.
	 * 
	 * @param url the URL from which to get the content
	 * @return a JSONObject representing the result of the query
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public JSONObject getResults(DrupalHttpProxy drupalProxy, String url) throws IOException, URISyntaxException {
		String content = "";

		log.debug("GETTING CONTENT FROM: "+url);

		if (drupalProxy != null) {
			content = drupalProxy.getContent(new URL(url));
		} else {
			return getResults(url);
		}

		return (JSONObject) JSONSerializer.toJSON(content);
	}

	/**
	 * This method returns a list of phenotype status for every allele in IMPC
	 * @param accession
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public List<ColonyStatus> getGeneColonyStatus(String accession) throws IOException, URISyntaxException {

		// The proxy must have access to the current request because it will
		// send the DRUPAL cookie along
		String url = config.get("internalSolrUrl") + "/" + "gene" + "/select?"
				+ "q=mgi_accession_id:" + accession.replace(":", "\\:")
				+ "&wt=json";
		log.info("url for geneDao=" + url);
		JSONObject jsonObject = getResults(url);
		JSONArray docs = jsonObject.getJSONObject("response").getJSONArray(
				"docs");
		if (docs.size() > 1) {
			log.error("Error, Only expecting 1 document from an accession/gene request");
		}
		
		// TODO
		// query the allele core provided by vivek
		// find the one with allele_name = 'GeneSymbol<sup>tm1a' 
		// and product_type = "Mouse"
		// and mgi_accession_id accession
		// e.g. Cib2<sup>tm1a(EUCOMM)Wtsi</sup>"
		// this will be the reference to retrieve the other alleles
		
		String tm1aAlleleName = "";
		
		// Multiple alleles / multiple colonies
		
		JSONArray colonies = docs.getJSONObject(0).getJSONArray("colony_prefix");
		JSONArray phenotypeColonies = docs.getJSONObject(0).getJSONArray("imits_phenotype_colony_name");
		JSONArray phenotypeAlleleTypes = docs.getJSONObject(0).getJSONArray("imits_phenotype_allele_type");
		JSONArray phenotypeStarted = docs.getJSONObject(0).getJSONArray("imits_phenotype_started");
		JSONArray phenotypeCompleted = docs.getJSONObject(0).getJSONArray("imits_phenotype_complete");
		JSONArray phenotypeStatus = docs.getJSONObject(0).getJSONArray("imits_phenotype_status");
		
		List<ColonyStatus> results = new ArrayList<ColonyStatus>();
		log.debug("Gene " + accession + " contains " + phenotypeColonies.size() + " phenotyped colonies");
		for (int cursor = 0; cursor < phenotypeColonies.size(); cursor++) {
			String currentPhenotypeStatus = (phenotypeStatus.size() >= (cursor+1)) ? phenotypeStatus.getString(cursor) : "";
			int phenotypeStartedValue = (phenotypeStarted.size() >= (cursor+1)) ? phenotypeStarted.getInt(cursor) : 0;
			int phenotypeCompletedValue = (phenotypeCompleted.size() >= (cursor+1)) ? phenotypeCompleted.getInt(cursor) : 0;
			String alleleType = (phenotypeAlleleTypes.size() >= (cursor+1)) ? phenotypeAlleleTypes.getString(cursor) : "";
			log.debug(phenotypeColonies.getString(cursor) + " " + currentPhenotypeStatus);
			// TODO add production status
			ColonyStatus current = new ColonyStatus(phenotypeColonies.getString(cursor), currentPhenotypeStatus, "", alleleType, phenotypeStartedValue, phenotypeCompletedValue);
			
			// change theallele type
			String currentAlleleName =  tm1aAlleleName.replace("tm1a", "tm1"+alleleType);
			// retrieve it from the allele core
			// allele_name = currentAlleleName
			// and product_type = "Mouse"
			// and mgi_accession_id accession
			// and get the strain name for this allele
			// check the colony because multiple colonies could have been derived
			String backgroundStrain = "";
			current.setAlleleName(currentAlleleName);
			current.setBackgroundStrain(backgroundStrain);
			
			results.add(current);
		}
		
		return results;
	}

	public JSONObject getMpData(String phenotype_id) throws IOException, URISyntaxException {
		System.out.println(this.config);
		String url = config.get("internalSolrUrl") + "/mp/select?";
		url += "q=" + phenotype_id + "&wt=json&qf=auto_suggest&defType=edismax";
		return getResults(url);

	}
	
}	
