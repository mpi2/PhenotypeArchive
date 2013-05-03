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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import uk.ac.ebi.phenotype.data.imits.ColonyStatus;
import uk.ac.ebi.phenotype.web.util.DrupalHttpProxy;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

public class SolrIndex  {
	
	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	private DrupalHttpProxy drupalProxy;
	private RegisterInterestDrupalSolr regInterest;
	private String loggedIn = null;
	private List<String> interestingGenes = null;

	private String baseUrl = "";
	private String drupalBaseUrl = "";
	private String mediaBaseUrl = "";
	private String qryStr;
	private String solrCoreName;
	private String gridSolrParams;
	private String mode;
	private int iDisplayStart;
	private int iDisplayLength;
	private String contextPath;
	private JSONObject json;
	private boolean showImgView;
	private String serverName;
	private int serverPort;
	
	public SolrIndex( Map<String, String> config){
		this.baseUrl = config.get("internalSolrUrl");
	}
	
	public SolrIndex(String qryStr, String solrCoreName, String mode, Map<String, String> config) throws IOException, URISyntaxException {

		this.drupalBaseUrl = config.get("drupalBaseUrl");
		this.baseUrl = config.get("internalSolrUrl");
		this.mediaBaseUrl = config.get("mediaBaseUrl");

		this.qryStr = qryStr;
		this.solrCoreName = solrCoreName;
		this.mode = mode;

		
			this.json = processQueryforJson(composeSolrUrl());
		
	}
	
	// populating jQuery dataTable on the server-side
	public SolrIndex(String qryStr, String solrCoreName, String gridSolrParams,
			String mode, int iDisplayStart, int iDisplayLength,
			boolean showImgView, HttpServletRequest request, Map<String, String> config) throws IOException, URISyntaxException {

		this.drupalBaseUrl = config.get("drupalBaseUrl");
		this.baseUrl = config.get("internalSolrUrl");
		this.mediaBaseUrl = config.get("mediaBaseUrl");

		this.qryStr = qryStr;
		this.solrCoreName = solrCoreName;
		this.gridSolrParams = gridSolrParams;
		this.mode = mode;
		this.iDisplayStart = iDisplayStart;
		this.iDisplayLength = iDisplayLength;
		this.showImgView = showImgView;
		
		this.drupalProxy = new DrupalHttpProxy(request);
		this.regInterest = new RegisterInterestDrupalSolr(config, request);
		this.json = processQueryforJson(composeSolrUrl());
		
	}
	
	// handling saving jQuery dataTable data to external files 
	public SolrIndex(String fileType, String solrCoreName, String gridSolrParams, int rowStart,
			boolean showImgView, String dumpMode, String gridFields, HttpServletRequest request, 
			Map<String, String> config, String contextPath, String serverName, int serverPort) {

		this.drupalBaseUrl = config.get("drupalBaseUrl");
		this.baseUrl = config.get("internalSolrUrl");		
		this.mediaBaseUrl = config.get("mediaBaseUrl");
		
		this.contextPath = contextPath;	
		this.serverName = serverName;	
		this.serverPort = serverPort;	
		this.solrCoreName = solrCoreName;	
		this.gridSolrParams = gridSolrParams;
		this.mode = dumpMode;
		this.iDisplayStart = rowStart;
		this.iDisplayLength = dumpMode.equals("all") ? 500000 : 10; 
		this.showImgView = showImgView;
		
		if ( solrCoreName.equals("gene") ) {
			gridFields += ",imits_report_phenotyping_complete_date,imits_report_genotype_confirmed_date,imits_report_mi_plan_status,escell,ikmc_project";
		}
		
		this.gridSolrParams = gridSolrParams + "&rows=" + iDisplayLength + "&start=" + rowStart + "&fl=" + gridFields;
		
		this.drupalProxy = new DrupalHttpProxy(request);
		this.regInterest = new RegisterInterestDrupalSolr(config, request);


		try {						
			this.json = fetchJsonforDataTableExport(composeSolrUrl(), solrCoreName);					
		} 
		catch (MalformedURLException me) {
			log.error(me.getLocalizedMessage());
		}
	}
	
	public JSONObject fetchJsonforDataTableExport(String urlString, String solrCoreName) throws MalformedURLException {
	
		System.out.println("TRYING TO GET CONTENT FROM: "+urlString);
		JSONObject j = null;
		
		try {
			// Use the http(s) proxy
			String content = drupalProxy.getContent(new URL(urlString));
			log.info("GOT CONTENT FROM: "+urlString);
			j = (JSONObject) JSONSerializer.toJSON(content);		
		} 
		catch (MalformedURLException e) {
			log.info("Please check the URL:" + e.toString());
		} catch (IOException e1) {
			log.info("Can't read from the Internet: " + e1.toString());
		} catch (URISyntaxException e) {
			log.info("Please check the URL:" + e.toString());
		}	
		return j;	
	}
	
	public String[][] composeXlsTableData(List<String> rows) {
		int rowNum = rows.size() - 1; // omit title row
		int colNum = rows.get(0).split("\t").length;
		
		String[][] tableData = new String[rowNum][colNum];
		
		for( int i=0; i<rowNum; i++ ){
			String[] colVals = rows.get(i+1).split("\t"); // omit title row
			for ( int j=0; j<colVals.length; j++){				
				tableData[i][j] = colVals[j];
			}
		}
		return tableData;
	}
	
	public List<String> composeDataTableExportRows(String solrCoreName){
		List<String> rows = null;
		if (solrCoreName.equals("gene") ){
			rows = composeGeneDataTableRows();
		}
		else if ( solrCoreName.equals("mp") ){			
			rows = composeMpDataTableRows();
		}
		else if ( solrCoreName.equals("pipeline") ){
			rows = composeProcedureDataTableRows();
		}
		else if ( solrCoreName.equals("images") ){
			rows = composeImageDataTableRows();
		}
		return rows;
	}
	private List<String> composeProcedureDataTableRows(){
		//System.out.println("CHECK json string: " + this.json.toString());
		JSONArray docs = this.json.getJSONObject("response").getJSONArray("docs");	
		//System.out.println("num genes found: " + docs.size());
		
		List<String> rowData = new ArrayList<String>();
		rowData.add("Parameter\tProcedure\tPipeline"); // column names	
		
		for (int i=0; i<docs.size(); i++) {			
			List<String> data = new ArrayList<String>();
			JSONObject doc = docs.getJSONObject(i);
			data.add(doc.getString("parameter_name"));
			data.add(doc.getString("procedure_name"));
			data.add(doc.getString("pipeline_name"));
			rowData.add(StringUtils.join(data, "\t"));
		}
		return rowData;
	}
	
	private List<String> composeImageDataTableRows(){
		//System.out.println("CHECK json string: " + this.json.toString());
		//System.out.println("CHECK imgView: " + this.showImgView);		
		
		String baseName = this.serverPort != 80 ? this.serverName + ":" + this.serverPort + this.contextPath : 
			this.serverName + "/" + this.contextPath;		
		
		List<String> rowData = new ArrayList<String>();
		
		if (this.showImgView){
			JSONArray docs = this.json.getJSONObject("response").getJSONArray("docs");	
			rowData.add("Annotation_term\tAnnotation_id\tProcedure\tGene_Symbol\tImage_path"); // column names	
			
			for (int i=0; i<docs.size(); i++) {			
				List<String> data = new ArrayList<String>();
				JSONObject doc = docs.getJSONObject(i);
								
				String[] fields = {"annotationTermName", "annotationTermId", "expName", "symbol_gene"};
				for( String fld : fields ){
					if(doc.has(fld)) {
						List<String> lists = new ArrayList<String>();
						JSONArray list = doc.getJSONArray(fld);
						for(int l=0; l<list.size();l++) {					
							lists.add(list.getString(l));
						}
						data.add(StringUtils.join(lists, "|")); 
					}
					else {
						data.add("NA");
					}				
				}
				
				data.add(mediaBaseUrl + "/" + doc.getString("largeThumbnailFilePath"));
				/*data.add(doc.getString("procedure_name"));
				data.add(doc.getString("pipeline_name"));*/
				rowData.add(StringUtils.join(data, "\t"));
			}
		}
		else {
			// annotation view
			// annotation view: images group by annotationTerm per row
			rowData.add("Annotation_type\tAnnotation_name\tRelated_image_count\tUrl_to_images"); // column names	
			JSONObject facetFields = this.json.getJSONObject("facet_counts").getJSONObject("facet_fields");
						
			JSONArray sumFacets = mergeFacets(facetFields);
						
			int numFacets = sumFacets.size();		
			int quotient = (numFacets/2)/iDisplayLength -((numFacets/2)%iDisplayLength) / iDisplayLength;
			int remainder = (numFacets/2) % iDisplayLength;
			int start = iDisplayStart*2;  // 2 elements(name, count), hence multiply by 2
	        int end =  iDisplayStart == quotient*iDisplayLength ? (iDisplayStart+remainder)*2 : (iDisplayStart+iDisplayLength)*2;  
				        
			for (int i=start; i<end; i=i+2){
				List<String> data = new ArrayList<String>();
				// array element is an alternate of facetField and facetCount	
				
				String[] names = sumFacets.get(i).toString().split("_");
				if (names.length == 2 ){  // only want facet value of xxx_yyy
					String annotName = names[0];
					HashMap<String, String> hm = renderFacetField(names); //MA:xxx, MP:xxx, MGI:xxx, exp					
									
					data.add(hm.get("label").toString());
					data.add(annotName);
					//data.add(hm.get("link").toString());
									
					String imgCount = sumFacets.get(i+1).toString();	
					data.add(imgCount);
					
					String facetField = hm.get("field").toString();
					String imgSubSetLink = "http://" + baseName + "/images?" + this.gridSolrParams + "q=*:*&fq=" + facetField + ":\"" + names[0] + "\"";						
					data.add(imgSubSetLink);
					rowData.add(StringUtils.join(data, "\t"));
				}
			}	
		}		
		
		return rowData;
	}
	
	private List<String> composeMpDataTableRows(){
		//System.out.println("CHECK json string: " + this.json.toString());
		JSONArray docs = this.json.getJSONObject("response").getJSONArray("docs");	
		//System.out.println("num genes found: " + docs.size());
		
		List<String> rowData = new ArrayList<String>();
		rowData.add("MP_term\tMP_id\tMP_definition\tTop_level_MP_term"); // column names	
		
		for (int i=0; i<docs.size(); i++) {			
			List<String> data = new ArrayList<String>();
			JSONObject doc = docs.getJSONObject(i);
			
			data.add(doc.getString("mp_term"));
			data.add(doc.getString("mp_id"));				
					
			if(doc.has("mp_definition")) {				
				data.add(doc.getString("mp_definition"));					
			}
			else {
				data.add("NA");
			}
			
			if(doc.has("top_level_mp_term")) {
				List<String> tops = new ArrayList<String>();
				JSONArray top = doc.getJSONArray("top_level_mp_term");
				for(int t=0; t<top.size();t++) {					
					tops.add(top.getString(t));
				}
				data.add(StringUtils.join(tops, "|")); 			
			}
			else {
				data.add("NA");
			}
			
			rowData.add(StringUtils.join(data, "\t"));
		}
		return rowData;
	}
	
	private List<String> composeGeneDataTableRows(){
		//System.out.println("CHECK json string: " + this.json.toString());
				
		JSONArray docs = this.json.getJSONObject("response").getJSONArray("docs");		
				
		//System.out.println("num genes found: " + docs.size());
		List<String> rowData = new ArrayList<String>();
		rowData.add("Marker symbol\tMaker name\tSynonym\tMouse production status\tPhenotyping status"); // column names		
		
		for (int i=0; i<docs.size(); i++) {			
			List<String> data = new ArrayList<String>();
			JSONObject doc = docs.getJSONObject(i);
			
			data.add(doc.getString("marker_symbol"));
			
			// Sanger problem, they should have use string for marker_name and not array
			//data.add(doc.getJSONArray("marker_name").getString(0));
			// now corrected using httpdatasource in dataImportHandler
			data.add(doc.getString("marker_name"));
			
			if(doc.has("marker_synonym")) {
				List<String> synData = new ArrayList<String>();
				JSONArray syn = doc.getJSONArray("marker_synonym");
				for(int s=0; s<syn.size();s++) {					
					synData.add(syn.getString(s));
				}
				data.add(StringUtils.join(synData, "|")); // use | as a multiValue separator in CSV output
			}
			else {
				data.add("NA");
			}
						
			// mouse production status
			data.add(doc.getString("status"));			
			
			// phenotyping status
			data.add(deriveLatestPhenotypingStatus(doc));
			
			// put together as tab delimited
			rowData.add(StringUtils.join(data, "\t"));			
		}		
		
		return rowData;		
	}
	
	private String composeSolrUrl() {
		
		//String qryMode = this.solrCoreName.equals("gene") ? "/search?" : "/select?";
		//String url = this.baseUrl + "/" + this.solrCoreName + qryMode;
		String url = this.baseUrl + "/" + this.solrCoreName + "/select?";
		
		//LOG.debug("GRID PARAMS:" + gridSolrParams);
	
		if (mode.equals("mpPage")) {
			url += "q=" + this.qryStr;
			url += "&start=0&rows=0&wt=json&qf=auto_suggest&defType=edismax";
		} 
		else if (mode.equals("geneGrid")) {			
			url += gridSolrParams				
				+ "&start=" + iDisplayStart
				+ "&rows=" + iDisplayLength;
				//+ "&hl=on&hl.field=marker_synonym,marker_name";
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
			url += "q=" + this.qryStr;
			url += "&start=0&rows=0&wt=json&defType=edismax";
			System.out.println("IKMC ALLELE PARAMS: " + url);
		}
		else if ( mode.equals("all") || mode.equals("page") ) {
			url += gridSolrParams;
			if ( solrCoreName.equals("images") && !showImgView ){
				url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
			}
			System.out.println("GRID DUMP PARAMS - " + solrCoreName + ": " + url);
		}
		// OTHER solrCoreNames to be added here
		
		return url;
	}
	
	private JSONObject processQueryforJson(String urlString) throws IOException, URISyntaxException {

		log.debug("SOLR qryStr: " + this.qryStr);
		log.debug("GETTING CONTENT FROM: "+urlString);

		String content = "";

		if (drupalProxy != null) {
		
				content = drupalProxy.getContent(new URL(urlString));
			
		} else {
			HttpProxy proxy = new HttpProxy();
			
				content = proxy.getContent(new URL(urlString));
			
		}

		return (JSONObject) JSONSerializer.toJSON(content);
	}

	public String fetchDataTableJson(String contextPath) throws IOException, URISyntaxException {
		this.contextPath = contextPath;

		String jsonStr = null;
		if (mode.equals("geneGrid")) {
			jsonStr = parseJsonforGeneDataTable();
		} 
		else if (mode.equals("pipelineGrid")) {
			jsonStr = parseJsonforProtocolDataTable();
		} 
		else if (mode.equals("imagesGrid")) {
			jsonStr = parseJsonforImageDataTable();
		} 
		else if (mode.equals("mpGrid")) {
			jsonStr = parseJsonforMpDataTable();
		}

		return jsonStr;
	}
	
	public String parseJsonforGeneDataTable(){
			
		qryStr = this.json.getJSONObject("responseHeader").getJSONObject("params").getString("q");
		JSONArray docs = this.json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = this.json.getJSONObject("response").getInt("numFound");
				
		log.debug("TOTAL GENEs: " + totalDocs);
		
		int quotient = (totalDocs)/iDisplayLength -((totalDocs)%iDisplayLength) / iDisplayLength;
		int remainder = (totalDocs) % iDisplayLength;
		int start = 0; 
        int end =  iDisplayStart == quotient*iDisplayLength ? remainder : iDisplayLength; 
        
        JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		
		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		
		for (int i = start; i < end; i++) {

			List<String> rowData = new ArrayList<String>();

			JSONObject doc = docs.getJSONObject(i);
						
			String geneInfo = concateGeneInfo(doc);
			rowData.add(geneInfo);

			// mouse production status
			String geneStatus = doc.getString("status");
			rowData.add(geneStatus);
			
			// phenotyping status			
			rowData.add(deriveLatestPhenotypingStatus(doc));			
			
			// register of interest
			if (regInterest.loggedIn()) {
				if (regInterest.alreadyInterested(doc.getString("mgi_accession_id"))) {
					rowData.add("<a id='"+doc.getString("mgi_accession_id")+"' href='' class='btn primary interest'>Unregister interest</a>");
				} else {
					rowData.add("<a id='"+doc.getString("mgi_accession_id")+"' href='' class='btn primary interest'>Register interest</a>");
				}
			} else {				
				rowData.add("<a href='/user/register' class='btn primary'>Login to register interest</a>");
			}
			//System.out.println("TEST: " + rowData);
			j.getJSONArray("aaData").add(rowData);			
		}
		
		//System.out.println("json: " + j.toString());
		return j.toString();	
	}
	
	private String deriveLatestPhenotypingStatus(JSONObject doc){	
		
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

	private String concateGeneInfo(JSONObject doc){
				
		List<String> geneInfo = new ArrayList<String>();	
		
		String markerSymbol = "<span class='gSymbol'>" + doc.getString("marker_symbol") + "</span>";		
		String mgiId = doc.getString("mgi_accession_id");
		String geneUrl = contextPath + "/genes/" + mgiId;		
		//String markerSymbolLink = "<a href='" + geneUrl + "' target='_blank'>" + markerSymbol + "</a>";
		String markerSymbolLink = "<a href='" + geneUrl + "'>" + markerSymbol + "</a>";
				
		String[] fields = {"marker_synonym","marker_name"};			
		for( int i=0; i<fields.length; i++){		
			try {				
				//"highlighting":{"MGI:97489":{"marker_symbol":["<em>Pax</em>5"],"synonym":["<em>Pax</em>-5"]},
				
				String field = fields[i];				
				List<String> info = new ArrayList<String>();
				
				if ( field.equals("marker_name") ){
					info.add(doc.getString(field));
				}
				else if ( doc.getJSONArray(field).size() > 0) {					
					JSONArray data = doc.getJSONArray(field);
					
					//use SOLR highlighted string if available
					if ( !qryStr.equals("*") ){
						info = checkMatched(mgiId, field, info); 
					}	
					if ( info.size() == 0 ){					
						for( Object d : data ){							
							info.add(d.toString());
						}
					}					
				}				
				geneInfo.add("<span class='gNameSyn'>" + field.replace("marker_", " ") + "</span>: " + StringUtils.join(info, ", "));
			} 
			catch (Exception e) {		   		
			    //e.printStackTrace();
			}
		}				
		return "<div class='geneCol'>" + markerSymbolLink + StringUtils.join(geneInfo, "<br>") + "</div>";
	}
	
	private List<String> checkMatched(String mgiId, String field, List<String> info){
		
		if ( field.equals("marker_synonym") ){
			field = "synonym";
		}
		//"highlighting":{"MGI:97489":{"marker_symbol":["<em>Pax</em>5"],"synonym":["<em>Pax</em>-5"]},
		JSONObject hl = this.json.getJSONObject("highlighting");		
		try {
			JSONArray matches = hl.getJSONObject(mgiId).getJSONArray(field);
			for( Object m : matches ){
				info.add(m.toString());
			}
		}
		catch(Exception e) {		   		
		    //e.printStackTrace();			
		}		
		return info;
	}
	
	
	
	
	public String parseJsonforProtocolDataTable(){
		
		JSONArray docs = this.json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = this.json.getJSONObject("response").getInt("numFound");
		
		int quotient = (totalDocs)/iDisplayLength -((totalDocs)%iDisplayLength) / iDisplayLength;
		int remainder = (totalDocs) % iDisplayLength;
		int start = 0; 
        int end =  iDisplayStart == quotient*iDisplayLength ? remainder : iDisplayLength; 
        
        JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		
		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		
		String impressBaseUrl = "http://beta.mousephenotype.org/impress/impress/displaySOP/";
		
		for (int i=start; i<end; i++){
			List<String> rowData = new ArrayList<String>();
					
			JSONObject doc = docs.getJSONObject(i);
						
			String parameter = doc.getString("parameter_name");
			rowData.add(parameter);
			
			String procedure = doc.getString("procedure_name");
			String procedure_stable_key = doc.getString("procedure_stable_key");			
			String procedureLink = "<a href='" + impressBaseUrl + procedure_stable_key + "'>" + procedure + "</a>";			
			rowData.add(procedureLink);				
			
			String pipeline = doc.getString("pipeline_name");
			rowData.add(pipeline);
			
			j.getJSONArray("aaData").add(rowData);
		} 
		
		return j.toString();	
	}
	
	public String parseJsonforMpDataTable(){
		
		String baseUrl = contextPath + "/phenotypes/";
		
		JSONArray docs = this.json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = this.json.getJSONObject("response").getInt("numFound");
				
		int quotient = (totalDocs)/iDisplayLength -((totalDocs)%iDisplayLength) / iDisplayLength;
		int remainder = (totalDocs) % iDisplayLength;
		int start = 0; 
        int end =  iDisplayStart == quotient*iDisplayLength ? remainder : iDisplayLength; 
        
        JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		
		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		
		for (int i=start; i<end; i++){
			List<String> rowData = new ArrayList<String>();

			// array element is an alternate of facetField and facetCount			
			JSONObject doc = docs.getJSONObject(i);
			String mpId = doc.getString("mp_id");
			String mpTerm = doc.getString("mp_term");
			String mpLink = "<a href='" + baseUrl + mpId + "'>" + mpTerm + "</a>";			
			rowData.add(mpLink);
			
			// some MP do not have definition
			String mpDef = "not applicable";
			try {
				mpDef = doc.getString("mp_definition");
			} 
			catch (Exception e) {			 			
			    //e.printStackTrace();
			}
			rowData.add(mpDef);	
			
			j.getJSONArray("aaData").add(rowData);
		} 
		
		return j.toString();	
	}

	public String parseJsonforImageDataTable() throws IOException, URISyntaxException{
		
		String baseUrl = contextPath + "/images?" + gridSolrParams;
		
		if ( showImgView ){			
			// image view: one image per row
			JSONArray docs = this.json.getJSONObject("response").getJSONArray("docs");
			int totalDocs = this.json.getJSONObject("response").getInt("numFound");
						
			int quotient = (totalDocs)/iDisplayLength -((totalDocs)%iDisplayLength) / iDisplayLength;
			int remainder = (totalDocs) % iDisplayLength;
			int start = 0; 
	        int end =  iDisplayStart == quotient*iDisplayLength ? remainder : iDisplayLength; 
	        
			JSONObject j = new JSONObject();
			j.put("aaData", new Object[0]);
			
			j.put("iTotalRecords", totalDocs);
			j.put("iTotalDisplayRecords", totalDocs);
			
			final String imgBaseUrl = mediaBaseUrl + "/"; 
			
			for (int i=start; i<end; i++){
				
				List<String> rowData = new ArrayList<String>();
				JSONObject doc = docs.getJSONObject(i);				
				String annots = "";
				
				String largeThumbNailPath = imgBaseUrl + doc.getString("largeThumbnailFilePath");
				String img = "<img src='" +  imgBaseUrl + doc.getString("smallThumbnailFilePath") + "'/>";
				String imgLink = "<a href='" + largeThumbNailPath +"'>" + img + "</a>";
				
				try {
					ArrayList<String> mp = new ArrayList<String>();
					ArrayList<String> ma = new ArrayList<String>();
					ArrayList<String> exp = new ArrayList<String>();					
					
					int counter = 0;
					
					if (doc.has("annotationTermId")) {
						JSONArray termIds   = doc.getJSONArray("annotationTermId");
						JSONArray termNames = doc.getJSONArray("annotationTermName");
						for( Object s : termIds ){				
							if ( s.toString().contains("MA")){
								log.debug(i + " - MA: " + termNames.get(counter).toString());
								String name = termNames.get(counter).toString();
								//ma.add("<a href='/maid' target='_blank'>" + name + "</a>");
								ma.add(name);
							}
							else if ( s.toString().contains("MP") ){
								log.debug(i+ " - MP: " + termNames.get(counter).toString());
								log.debug(i+ " - MP: " + termIds.get(counter).toString());
								String mpid = termIds.get(counter).toString();							
								String name = termNames.get(counter).toString();							
								String url = contextPath + "/phenotypes/" + mpid;
								mp.add("<a href='" + url + "'>" + name + "</a>");
							}
							counter++;
						}
					}	
					
					if (doc.has("expName")) {
						JSONArray expNames  = doc.getJSONArray("expName");
						for( Object s : expNames ){
							log.debug(i + " - expTERM: " + s.toString());
							exp.add(s.toString());
						}						
					}					
					
					if ( mp.size() > 0){
						annots += "<span class='imgAnnots'><span class='annotType'>MP</span>: " + StringUtils.join(mp, ", ") + "</span>";
					}
					if ( ma.size() > 0){
						annots += "<span class='imgAnnots'><span class='annotType'>MA</span>: " + StringUtils.join(ma, ", ") + "</span>";
					}
					if ( exp.size() > 0){
						annots += "<span class='imgAnnots'><span class='annotType'>Procedure</span>: " + StringUtils.join(exp, ", ") + "</span>";
					}
					
					ArrayList<String> gene = fetchImgGeneAnnotations(doc);
					if ( gene.size() > 0){						
						annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + StringUtils.join(gene, ", ") + "</span>";
					}
									
					rowData.add(annots);
					
					//String largeThumbNailPath = imgBaseUrl + doc.getString("largeThumbnailFilePath");
					//String img = "<img src='" +  imgBaseUrl + doc.getString("smallThumbnailFilePath") + "'/>";
					//String imgLink = "<a target='_blank' href='" + largeThumbNailPath +"'>" + img + "</a>";
					rowData.add(imgLink);
					
					j.getJSONArray("aaData").add(rowData);
				}
				catch (Exception e){
					//e.printStackTrace();
					// some images have no annotations					
					rowData.add("Not available");
					rowData.add(imgLink);
					j.getJSONArray("aaData").add(rowData);
				}
			} 	
			
			return j.toString();		
		}
		else {
			// annotation view: images group by annotationTerm per row			
			JSONObject facetFields = this.json.getJSONObject("facet_counts").getJSONObject("facet_fields");
			
			JSONArray sumFacets = mergeFacets(facetFields);
						
			int numFacets = sumFacets.size();		
			int quotient = (numFacets/2)/iDisplayLength -((numFacets/2)%iDisplayLength) / iDisplayLength;
			int remainder = (numFacets/2) % iDisplayLength;
			int start = iDisplayStart*2;  // 2 elements(name, count), hence multiply by 2
	        int end =  iDisplayStart == quotient*iDisplayLength ? (iDisplayStart+remainder)*2 : (iDisplayStart+iDisplayLength)*2;			
	        	        
	        JSONObject j = new JSONObject();
			j.put("aaData", new Object[0]);
			
			j.put("iTotalRecords", numFacets/2);
			j.put("iTotalDisplayRecords", numFacets/2);
			
			for (int i=start; i<end; i=i+2){
				List<String> rowData = new ArrayList<String>();
				// array element is an alternate of facetField and facetCount	
				String[] names = sumFacets.get(i).toString().split("_");
				
				if (names.length == 2 ){  // only want facet value of xxx_yyy
					HashMap<String, String> hm = renderFacetField(names); //MA:xxx, MP:xxx, MGI:xxx, exp				
					String displayAnnotName = "<span class='annotType'>" + hm.get("label").toString() + "</span>: " + hm.get("link").toString();
					String facetField = hm.get("field").toString();
							
					String imgCount = sumFacets.get(i+1).toString();	
					String unit = Integer.parseInt(imgCount) > 1 ? "images" : "image";	
										
					String imgSubSetLink = "<a href='" + baseUrl+ "&fq=" + facetField + ":\"" + names[0] + "\"" + "'>" + imgCount + " " + unit+ "</a>";
										
					rowData.add(displayAnnotName + " (" + imgSubSetLink + ")"); 
					rowData.add(fetchImagePathByAnnotName(facetField, names[0], contextPath));
					
					j.getJSONArray("aaData").add(rowData);
				}
			}	
			
			return j.toString();	
		}
	}
	
	private HashMap<String, String> renderFacetField(String[] names){				
			
		HashMap<String, String> hm = new HashMap<String, String>(); // key: display label, value: facetField
		String name = names[0];
		String id = names[1];	
		
		if ( id.startsWith("MP:")){	
			String url = contextPath + "/phenotypes/" + id;
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
			String url = contextPath + "/genes/" + id;
			hm.put("label", "Gene");
			hm.put("field", "symbol");
			hm.put("link", "<a href='" + url + "'>"+ name + "</a>");
		}	
		return hm;
	}
	
	private JSONArray mergeFacets(JSONObject facetFields){
				
		JSONArray gene = facetFields.getJSONArray("symbol_gene");
		JSONArray exp = facetFields.getJSONArray("expName_exp");
		JSONArray mp = facetFields.getJSONArray("mpTermName");
		JSONArray ma = facetFields.getJSONArray("maTermName");	
			
		gene.addAll(exp);
		gene.addAll(mp);
		gene.addAll(ma);		
		return gene;
	}

	private ArrayList<String> fetchImgGeneAnnotations(JSONObject doc) {
		
		ArrayList<String> gene = new ArrayList<String>();		
		
		try {
			if (doc.has("symbol_gene")) {
				JSONArray geneSymbols = doc.getJSONArray("symbol_gene");				
				for (Object s : geneSymbols) {
					String[] names = s.toString().split("_");				
					String url = contextPath + "/genes/" + names[1];
					gene.add("<a href='" + url +"'>" + names[0] + "</a>");				
				}
			}
		} 
		catch (Exception e) {
			// e.printStackTrace();
		}		
		return gene;
	}
	
	private String fetchImagePathByAnnotName(String facetField, String annotName, String contextPath) throws IOException, URISyntaxException{
	
		//annotName = annotName.replace(" ","%20");
		final int maxNum = 4; // max num of images to display in grid column 
		String thumbnailUrl = this.baseUrl+ "/" + this.solrCoreName + "/select?";
				
		String params = gridSolrParams + "&fq=" +  facetField + ":\"" + annotName + "\"&rows=" + maxNum;
		
		thumbnailUrl += params;

		final String imgBaseUrl = mediaBaseUrl + "/";

		List<String> imgPath = new ArrayList<String>();

	
			JSONObject thumbnailJson = processQueryforJson(thumbnailUrl);
			JSONArray docs = thumbnailJson.getJSONObject("response").getJSONArray("docs");

			int dataLen = docs.size() < 5 ? docs.size() : maxNum;

			for (int i = 0; i < dataLen; i++) {
				JSONObject doc = docs.getJSONObject(i);
				String largeThumbNailPath = imgBaseUrl + doc.getString("largeThumbnailFilePath");
				String img = "<img src='" + imgBaseUrl + doc.getString("smallThumbnailFilePath") + "'/>";
				String link = "<a href='" + largeThumbNailPath + "'>" + img + "</a>";
				imgPath.add(link);
			}
		return StringUtils.join(imgPath, "");
	}

	public int fetchNumFound() {
		JSONObject response = this.json.getJSONObject("response");
		return response.getInt("numFound");
	}

	
	public String getGeneStatus(String accession) throws IOException, URISyntaxException {

		// The proxy must have access to the current request because it will
		// send the DRUPAL cookie along
		String url = this.baseUrl + "/" + "gene" + "/select?"
				+ "q=mgi_accession_id:" + accession.replace(":", "\\:")
				+ "&wt=json";
		log.info("url for geneDao=" + url);
		JSONObject jsonObject = processQueryforJson(url);
		JSONArray docs = jsonObject.getJSONObject("response").getJSONArray(
				"docs");
		if (docs.size() > 1) {
			log.error("Error, Only expecting 1 document from an accession/gene request");
		}
		String geneStatus = docs.getJSONObject(0).getString("status");
		log.debug("gene status=" + geneStatus);
		return geneStatus;
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
		String url = this.baseUrl + "/" + "gene" + "/select?"
				+ "q=mgi_accession_id:" + accession.replace(":", "\\:")
				+ "&wt=json";
		log.info("url for geneDao=" + url);
		JSONObject jsonObject = processQueryforJson(url);
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
		String url = this.baseUrl + "/mp/select?";
		url += "q=" + phenotype_id + "&wt=json&qf=auto_suggest&defType=edismax";
		return processQueryforJson(url);

	}
	
}	
