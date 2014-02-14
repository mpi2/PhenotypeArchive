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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.generic.util.RegisterInterestDrupalSolr;
import uk.ac.ebi.generic.util.SolrIndex;

@Controller
public class DataTableController {

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	@Autowired
	private SolrIndex solrIndex;

	@Resource(name="globalConfiguration")
	private Map<String, String> config;

	private List<String> alleleTypes_mi = new ArrayList<String>();
	private List<String> alleleTypes_pa = new ArrayList<String>();
	
	
	/**
	 * <p>
	 * Return jQuery dataTable from server-side for lazy-loading.
	 * </p>
	 * 
	 * @param bRegex
	 *            =false bRegex_0=false bRegex_1=false bRegex_2=false
	 *            bSearchable_0=true bSearchable_1=true bSearchable_2=true
	 *            bSortable_0=true bSortable_1=true bSortable_2=true
	 *            iColumns=3
	 *            for paging: iDisplayLength=10 iDisplayStart=0
	 *            for sorting: iSortCol_0=0 iSortingCols=1
	 *            for filtering: sSearch= sSearch_0= sSearch_1= sSearch_2=
	 *            mDataProp_0=0 mDataProp_1=1 mDataProp_2=2
	 *            sColumns= sEcho=1
	 *            sSortDir_0=asc
	 *
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */

	@RequestMapping(value = "/dataTable", method = RequestMethod.GET)
	public ResponseEntity<String> dataTableJson(
			@RequestParam(value = "iDisplayStart", required = false) int iDisplayStart,
			@RequestParam(value = "iDisplayLength", required = false) int iDisplayLength,
			@RequestParam(value = "solrParams", required = false) String solrParams,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException  {
		//System.out.println("solr params: " + solrParams);
		JSONObject jParams = (JSONObject) JSONSerializer.toJSON(solrParams);
		
		String solrCoreName = jParams.containsKey("solrCoreName") ? jParams.getString("solrCoreName") : jParams.getString("facetName");	
				
		List<String> filters = jParams.containsKey("filters") ? jParams.getJSONArray("filters") : null;	
		System.out.println("FILTERS: " + filters);
		String query = "";
		String fqOri = "";
		String mode = jParams.getString("mode");
		String solrParamStr = jParams.getString("params");
		
		// Get the query string
		String[] pairs = jParams.getString("params").split("&");
		for (String pair : pairs) {
			String[] parts = pair.split("=");
			if (parts[0].equals("q")) {
				query = parts[1];				
			}
			if (parts[0].equals("fq")) {
				fqOri = "&fq=" + parts[1];				
			}			
		}		
		
		boolean showImgView = false;
		if (jParams.containsKey("showImgView")) {
			showImgView = jParams.getBoolean("showImgView");
		}
		//System.out.println("query: "+ query);
		JSONObject json = solrIndex.getDataTableJson(query, solrCoreName, solrParamStr, mode, iDisplayStart, iDisplayLength, showImgView);
		//System.out.println("JSON: "+ json);
		
		String content = fetchDataTableJson(request, json, mode, query, fqOri, iDisplayStart, iDisplayLength, solrParamStr, showImgView, solrCoreName, filters);
		
		return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
	}

	
	@ExceptionHandler(Exception.class)
	private ResponseEntity<String> getSolrErrorResponse(Exception e) {
		e.printStackTrace();
		String bootstrap="<div class=\"alert\"><strong>Warning!</strong>  Error: Search functionality is currently unavailable</div>";
		String errorJSON="{'aaData':[[' "+bootstrap+"','  ', ' ']], 'iTotalRecords':1,'iTotalDisplayRecords':1}";
		JSONObject errorJson = (JSONObject) JSONSerializer.toJSON(errorJSON);
		return new ResponseEntity<String>(errorJson.toString(), createResponseHeaders(), HttpStatus.CREATED);
	}

	
	private HttpHeaders createResponseHeaders(){
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}

	public String fetchDataTableJson(HttpServletRequest request, JSONObject json, String mode, String query, String fqOri, int start, int length, String solrParams, boolean showImgView, String solrCoreName, List<String> filters) throws IOException, URISyntaxException {

		String jsonStr = null;
		if (mode.equals("geneGrid")) {
			jsonStr = parseJsonforGeneDataTable(request, json, query, solrCoreName, filters);
		} 
		else if (mode.equals("pipelineGrid")) {
			jsonStr = parseJsonforProtocolDataTable(json, request, solrCoreName, filters, start);
		} 
		else if (mode.equals("imagesGrid")) {
			jsonStr = parseJsonforImageDataTable(json, start, length, solrParams, showImgView, request, query, fqOri, solrCoreName, filters);
		} 
		else if (mode.equals("mpGrid")) {
			jsonStr = parseJsonforMpDataTable(json, request, solrCoreName, filters, start);
		}
		else if (mode.equals("maGrid")) {
			jsonStr = parseJsonforMaDataTable(json, request, solrCoreName, filters, start);
		}
		else if (mode.equals("diseaseGrid")) {
			jsonStr = parseJsonforDiseaseDataTable(json, request, solrCoreName, filters, start);
		}
		return jsonStr;
	}

	public String parseJsonforGeneDataTable(HttpServletRequest request, JSONObject json, String qryStr, String solrCoreName, List<String> filters){	
		
		// mi_attempt, phenotype_attempt allele types
		alleleTypes_mi.add("tm1");
		alleleTypes_mi.add("tm1a");
		alleleTypes_mi.add("tm1e");		
		
		alleleTypes_pa.add("tm1.1");
		alleleTypes_pa.add("tm1b");
		alleleTypes_pa.add("tm1e.1");
		
		RegisterInterestDrupalSolr registerInterest = new RegisterInterestDrupalSolr(config, request);

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");
				
		log.debug("TOTAL GENEs: " + totalDocs);
		
        JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		
		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);

		for (int i = 0; i < docs.size(); i++) {

			List<String> rowData = new ArrayList<String>();

			JSONObject doc = docs.getJSONObject(i);
						
			String geneInfo = concateGeneInfo(doc, json, qryStr, request);
			rowData.add(geneInfo);

			// ES cell/mice production status	
			String prodStatus = deriveProductionStatusForEsCellAndMice(doc, request);			
			rowData.add(prodStatus);
			
			// phenotyping status			
			//String phenoStatus = solrIndex.deriveLatestPhenotypingStatus(doc).equals("") ? "" : "<a class='status done'><span>phenotype data available</span></a>";
			String phenoStatus = derivePhenotypingStatus(doc).equals("") ? "" : "<a class='status done'><span>phenotype data available</span></a>";
			rowData.add(phenoStatus);
			
			// register of interest
			if (registerInterest.loggedIn()) {
				if (registerInterest.alreadyInterested(doc.getString("mgi_accession_id"))) {
					String uinterest = "<div class='registerforinterest' oldtitle='Unregister interest' title=''>"
							+ "<i class='fa fa-sign-out'></i>"
							+ "<a id='"+doc.getString("mgi_accession_id")+"' class='regInterest primary interest' href=''>Unregister interest</a>"
							+ "</div>";
					
					rowData.add(uinterest);					
					//rowData.add("<a id='"+doc.getString("mgi_accession_id")+"' href='' class='btn primary interest'>Unregister interest</a>");					
				} 
				else {
					String rinterest = "<div class='registerforinterest' oldtitle='Register interest' title=''>"
							+ "<i class='fa fa-sign-in'></i>"
							+ "<a id='"+doc.getString("mgi_accession_id")+"' class='regInterest primary interest' href=''>Register interest</a>"
							+ "</div>";
					
					rowData.add(rinterest);					
					//rowData.add("<a id='"+doc.getString("mgi_accession_id")+"' href='' class='btn primary interest'>Register interest</a>");
				}
			} 
			else {	
				String interest = "<div class='registerforinterest' oldtitle='Login to register interest' title=''>"
								+ "<i class='fa fa-sign-in'></i>"
								+ "<a class='regInterest' href='/user/register'>Interest</a>"
								//+ "<a class='regInterest' href='#'>Interest</a>"
								+ "</div>";
				
				rowData.add(interest);
				//rowData.add("<a href='/user/register' class='btn primary'>Interest</a>");
			}
			
			j.getJSONArray("aaData").add(rowData);			
		}
		
		return j.toString();	
	}
	public String derivePhenotypingStatus(JSONObject doc){
		
		// Vivek email to ckchen on 07/02/14 11:57
		List<String> phenos = new ArrayList<String>() {
			  {
				add("mi_phenotyping_status");
				add("pa_phenotyping_status");				
			   }
		};
		try {
			for (String p : phenos) {
			// Phenotyping complete			
				if (doc.containsKey(p)) {
					JSONArray status = doc.getJSONArray(p);
					for (Object s : status) {
						if (s.toString().equals("Phenotyping Started") || s.toString().equals("Phenotyping Complete") ) {
							return "available";
						}
					}
				}
			}	

			// for legacy data: indexed through experiment core (so not want Sanger Gene or Allele cores)
			if (doc.containsKey("hasQc")) {				
				return "QCed data available";			
			}
		}		
		catch (Exception e) {
			log.error("Error getting phenotyping status");
			log.error(e.getLocalizedMessage());
		}
				
		return "";
	}
	public String deriveProductionStatusForEsCellAndMice(JSONObject doc, HttpServletRequest request){
		
		String mgiId = doc.getString("mgi_accession_id");
		String geneUrl = request.getAttribute("baseUrl") + "/genes/" + mgiId;
				
		String esCellStatus = "";	
		String miceStatus = "";	
		try {	
			
			// ES cell production status
			if ( doc.containsKey("es_allele_name") ){
				// blue es cell status
				esCellStatus = "<a class='status done' href='" + geneUrl + "' oldtitle='ES Cells produced' title=''>"
					   		 + " <span>ES cells</span>"
					   		 + "</a>";
			}
			else if ( !doc.containsKey("es_allele_name") && doc.containsKey("gene_type") ){		
				esCellStatus = "<span class='status inprogress' oldtitle='ES cells production in progress' title=''>"
						   	 +  "	<span>ES Cell</span>"
						   	 +  "</span>";
			}
						
			// mice production status
			
			// Mice: blue tm1.1/tm1b/tm1e.1 mice (depending on how many allele docs) 
			if ( doc.containsKey("pa_allele_type") ){
				// blue es cell status
				miceStatus += parseAlleleType(doc, "done", "B");
			}
			// Mice: blue tm1/tm1a/tm1e mice (depending on how many allele docs) 
			else if ( doc.containsKey("mi_allele_type") ){
				// blue es cell status
				miceStatus += parseAlleleType(doc, "done", "A");
			}
			else if ( doc.containsKey("es_allele_name") && doc.containsKey("gene_type_status")  ){
				if ( doc.getString("gene_type_status").equals("Microinjection in progress") ){					
					// draw orange tm1/tm1a/tm1e mice with given alleles
					miceStatus += parseAlleleType(doc, "inprogress", "A");					
				}	
				else if (doc.getString("gene_type_status").equals("") ){
					miceStatus += parseAlleleType(doc, "none", "A");  // mouse production planned	
				}
			}
			else if ( doc.containsKey("es_allele_name") && !doc.containsKey("gene_type_status") ){
				// grey mice status: 
				miceStatus += parseAlleleType(doc, "none", "A");  // mouse production planned	
			}			
			
		} catch (Exception e) {
			log.error("Error getting ES cell/Mice status");
			log.error(e.getLocalizedMessage());
		}
		
		return esCellStatus + miceStatus;
		
	}
	/*public String deriveProductionStatusForEsCellAndMice2(JSONObject doc, HttpServletRequest request){		
		
		String geneStatus = doc.getString("status");
		String prodStatus = "";
		String miceStr = "";
		
		String mgiId = doc.getString("mgi_accession_id");
		String geneUrl = request.getAttribute("baseUrl") + "/genes/" + mgiId;
		
		// ES cell
		String esCellStatus = "<a class='status done' href='" + geneUrl + "' oldtitle='ES Cells produced' title=''>"
					   		+ " <span>ES cells</span>"
					   		+ "</a>";		
		
		if ( geneStatus.equals("Mice Produced") ){	
			// ES cell
			prodStatus = esCellStatus;					
			miceStr = parseAlleleType(doc, "done");			
		}
		else if ( geneStatus.equals("Assigned for Mouse Production and Phenotyping") ){
			prodStatus = esCellStatus; // ES cell
			miceStr = parseAlleleType(doc, "inprogress");			
		}
		else if ( geneStatus.equals("ES Cells Produced") ){		
			prodStatus = esCellStatus; // ES cell
						
			List<String> alleleNames = doc.getJSONArray("es_allele_name");					
	        
			Map<String,Integer> seenMap = new HashMap<String,Integer>();	      
			seenMap.put("tm1", 0);
			seenMap.put("tm1a", 0);
			seenMap.put("tm1e", 0);
			
			for (int i=0; i< alleleNames.size(); i++) {
				String alName = alleleNames.get(i);
				//System.out.println("ALLELE NAME: " + alName);
				
				for (String alleleType : alleleTypes_mi){				    
					if ( alName.contains(alleleType+"(") ){					
						seenMap.put(alleleType, seenMap.get(alleleType)+1);
						//tm1seen++;
						if ( seenMap.get(alleleType) == 1 ){
							miceStr += "<span class='status none' oldtitle='Mice production planned' title=''>"
									+  "<span>Mice<br>" + alleleType + "</span>"
									+  "</span>";	
						}
						break;
					}					
				}				
			}			
		}
		else if ( geneStatus.equals("Assigned for ES Cell Production") ){
			prodStatus = "<span class='status inprogress' oldtitle='ES cells production in progress' title=''>"
					   +  "	<span>ES Cell</span>"
					   +  "</span>";
		}	
		return prodStatus + miceStr;
	}*/
	public String parseAlleleType(JSONObject doc, String prodStatus, String type){		
		
		String miceStr = "";			
		String hoverTxt = null;		
		if ( prodStatus.equals("done") ){
			hoverTxt = "Mice produced";			
		}
		else  if (prodStatus.equals("inprogress") ) {
			hoverTxt = "Mice production in progress";
		}
		else if ( prodStatus.equals("none") ){
			hoverTxt = "Mice production planned";
		}
		
		
		//tm1/tm1a/tm1e mice	
		if ( type.equals("A") ){	
			
			Map<String,Integer> seenMap = new HashMap<String,Integer>();	      
			seenMap.put("tm1", 0);
			seenMap.put("tm1a", 0);
			seenMap.put("tm1e", 0);			
			
			for (String alleleType : alleleTypes_mi){	
				
				String key = prodStatus.equals("inprogress") ? "es_allele_name" : "mi_allele_name";				
				
				JSONArray alleleNames = doc.getJSONArray(key);
				for (Object an : alleleNames) {				
					if ( an.toString().contains(alleleType+"(") ){						
						seenMap.put(alleleType, seenMap.get(alleleType)+1);
						//tm1seen++;
						if ( seenMap.get(alleleType) == 1 ){
							miceStr += "<span class='status " + prodStatus + "' oldtitle='" + hoverTxt + "' title=''>"
								+  "	<span>Mice<br>" + alleleType + "</span>"
								+  "</span>";					
							break;
						}					
					}
				}	
			}	
		}
		//tm1.1/tm1b/tm1e.1 mice	
		else if ( type.equals("B") ){	
			
			Map<String,Integer> seenMap = new HashMap<String,Integer>();	      
			seenMap.put("tm1.1", 0);
			seenMap.put("tm1b", 0);
			seenMap.put("tm1e.1", 0);
			
			for (String alleleType : alleleTypes_pa){	
				
				JSONArray alleleNames = doc.getJSONArray("pa_allele_name");
				for (Object an : alleleNames) {				
					if ( an.toString().contains(alleleType+"(") ){					
						seenMap.put(alleleType, seenMap.get(alleleType)+1);
						if ( seenMap.get(alleleType) == 1 ){
							miceStr += "<span class='status " + prodStatus + "' oldtitle='" + hoverTxt + "' title=''>"
									+  "	<span>Mice<br>" + alleleType + "</span>"
									+  "</span>";	
							break;
						}	
					}	
				}				
			}	
		}		
		
		return miceStr;
	}
	public String parseJsonforProtocolDataTable(JSONObject json, HttpServletRequest request, String solrCoreName, List<String> filters, int start){
		
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");
		
        JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		
		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		
		String impressBaseUrl = request.getAttribute("drupalBaseUrl") + "/impress/impress/displaySOP/";
		
		for (int i=0; i<docs.size(); i++){
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
	
	public String parseJsonforMpDataTable(JSONObject json, HttpServletRequest request, String solrCoreName, List<String> filters, int start){
				
		String baseUrl = request.getAttribute("baseUrl") + "/phenotypes/";		
		
		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);			
		
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");
					
		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		
		for (int i=0; i<docs.size(); i++){
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
	public String parseJsonforMaDataTable(JSONObject json, HttpServletRequest request, String solrCoreName, List<String> filters, int start){
        
        String baseUrl = request.getAttribute("baseUrl") + "/anatomy/";
        
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        int totalDocs = json.getJSONObject("response").getInt("numFound");
                        
        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);
        
        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);
        
        for (int i=0; i<docs.size(); i++){
                List<String> rowData = new ArrayList<String>();

                // array element is an alternate of facetField and facetCount                        
                JSONObject doc = docs.getJSONObject(i);
                String maId = doc.getString("ma_id");
                String maTerm = doc.getString("ma_term");
                String maLink = "<a href='" + baseUrl + maId + "'>" + maTerm + "</a>";                        
                rowData.add(maLink);
                
                // some MP do not have definition
                /*String mpDef = "not applicable";
                try {
                        maDef = doc.getString("ma_definition");
                }
                catch (Exception e) {                                                 
                 //e.printStackTrace();
                }
                rowData.add(mpDef);*/        
                
                j.getJSONArray("aaData").add(rowData);
        }
        
        return j.toString();        
	}
	
	
	public String parseJsonforImageDataTable(JSONObject json, int start, int length, String solrParams, boolean showImgView, HttpServletRequest request, String query, String fqOri, String solrCoreName, List<String> filters) throws IOException, URISyntaxException{
		
		String mediaBaseUrl = config.get("mediaBaseUrl");

		if ( showImgView ){			
			// image view: one image per row
			JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
			int totalDocs = json.getJSONObject("response").getInt("numFound");
	        
			JSONObject j = new JSONObject();
			j.put("aaData", new Object[0]);
			
			j.put("iTotalRecords", totalDocs);
			j.put("iTotalDisplayRecords", totalDocs);
			
			String imgBaseUrl = mediaBaseUrl + "/"; 
			
			for (int i=0; i<docs.size(); i++){
				
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
								String url = request.getAttribute("baseUrl") + "/phenotypes/" + mpid;
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
					
					ArrayList<String> gene = fetchImgGeneAnnotations(doc, request);
					if ( gene.size() > 0){						
						annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + StringUtils.join(gene, ", ") + "</span>";
					}
									
					rowData.add(annots);
					rowData.add(imgLink);
					j.getJSONArray("aaData").add(rowData);
				}
				catch (Exception e){
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
			
			String fqStr = fqOri;		
			if ( fqStr.equals("&fq=annotationTermId:M*%20OR%20expName:*%20OR%20symbol:*%20OR%20annotated_or_inferred_higherLevelMaTermName:*%20OR%20annotated_or_inferred_higherLevelMpTermName:*") ){				
				solrParams = solrParams.replace(fqOri, "");	
				fqStr = "";
			}
			
			String baseUrl = request.getAttribute("baseUrl") + "/imagesb?" + solrParams;
			//System.out.println("THE PARAMs: "+ solrParams);
			
			JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
			
			JSONArray facets = solrIndex.mergeFacets(facetFields);

			int numFacets = facets.size();

			//System.out.println("Number of facets: " + numFacets);

	        JSONObject j = new JSONObject();
			j.put("aaData", new Object[0]);
			
			j.put("iTotalRecords", numFacets/2);
			j.put("iTotalDisplayRecords", numFacets/2);
			
			int end = start+length;
			//System.out.println("Start: "+start*2+", End: "+end*2); 

			// The facets array looks like:
			//   [0] = facet name
			//   [1] = facet count for [0]
			//   [n] = facet name
			//   [n+1] = facet count for [n]
			// So we start at 2 times the start to skip over all the n+1
			// and increase the end similarly.
			for (int i=start*2; i<end*2; i=i+2){

				if (facets.size()<=i) {break;}//stop when we hit the end

				String[] names = facets.get(i).toString().split("_");
				
				if (names.length == 2 ){  // only want facet value of xxx_yyy

					List<String> rowData = new ArrayList<String>();

					Map<String, String> hm = solrIndex.renderFacetField(names, (String)request.getAttribute("baseUrl")); //MA:xxx, MP:xxx, MGI:xxx, exp				
					String displayAnnotName = "<span class='annotType'>" + hm.get("label").toString() + "</span>: " + hm.get("link").toString();
					String facetField = hm.get("field").toString();

					String imgCount = facets.get(i+1).toString();	
					String unit = Integer.parseInt(imgCount) > 1 ? "images" : "image";	

					//String imgSubSetLink = "<a href='" + baseUrl+ "&fq=" + facetField + ":\"" + names[0] + "\"" + "'>" + imgCount + " " + unit+ "</a>";
					String imgSubSetLink = "<a href='" + baseUrl+ " AND " + facetField + ":\"" + names[0] + "\"" + "'>" + imgCount + " " + unit+ "</a>";
									
					rowData.add(displayAnnotName + " (" + imgSubSetLink + ")");
					
					// messy here, as ontodb (the latest term name info) may not have the terms in ann_annotation table
					// so we just use the name from ann_annotation table
					String thisFqStr = "";
					String fq = "";
					if (facetField == "annotationTermName") {
						fq = "(" + facetField + ":\"" + names[0] + "\" OR annotationTermName:\"" + 	names[0] + "\")";	
					}
					else {					
						fq = facetField + ":\"" + names[0] + "\"";
					}
					
					thisFqStr = fqStr.equals("") ? "fq=" + fq : fqStr + " AND " + fq;	
					
					
					rowData.add(fetchImagePathByAnnotName(query, thisFqStr));

					j.getJSONArray("aaData").add(rowData);
				}
			}	
			
			return j.toString();	
		}
	}
	
	public String parseJsonforDiseaseDataTable(JSONObject json, HttpServletRequest request, String solrCoreName, List<String> filters, int start){
		
		String baseUrl = request.getAttribute("baseUrl") + "/phenodigm/disease/";
		
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");
				
        JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		
		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		
		for (int i=0; i<docs.size(); i++){
			List<String> rowData = new ArrayList<String>();

			// disease link			
			JSONObject doc = docs.getJSONObject(i);
			String diseaseId = doc.getString("disease_id");
			String diseaseTerm = doc.getString("disease_term");
			String diseaseLink = "<a href='" + baseUrl + diseaseId + "'>" + diseaseTerm + "</a>";			
			rowData.add(diseaseLink);						
			
			// disease source
			String src = doc.getString("disease_source");
			rowData.add(src);
			
			// curated data: human/mouse	
			String human = "<span class='status done'>human</span>";
			String mice  = "<span class='status done'>mice</span>";
			
			// predicted data: impc/mgi
			String impc  = "<span class='status done'>IMPC</span>";
			String mgi   = "<span class='status done'>MGI</span>";
			
			try {
				String isHumanCurated = doc.getString("human_curated").equals("true") ? human : "";			
				String isMouseCurated = doc.getString("mouse_curated").equals("true") ? mice : "";
				rowData.add(isHumanCurated + isMouseCurated);
				//rowData.add("test1" + "test2");
				String isImpcPredicted = (doc.getString("impc_predicted").equals("true") || doc.getString("impc_predicted_in_locus").equals("true")) ? impc : "";				
				String isMgiPredicted = (doc.getString("mgi_predicted").equals("true") || doc.getString("mgi_predicted_in_locus").equals("true")) ? mgi : "";
				rowData.add(isImpcPredicted + isMgiPredicted);
				//rowData.add("test3" + "test4");		
				//System.out.println("DOCS: " + rowData.toString());
				j.getJSONArray("aaData").add(rowData);							
			}			
			catch (Exception e) {
				log.error("Error getting disease curation values");
				log.error(e.getLocalizedMessage());
			}
		}
		return j.toString();			
	}
	
	private ArrayList<String> fetchImgGeneAnnotations(JSONObject doc, HttpServletRequest request) {
		
		ArrayList<String> gene = new ArrayList<String>();		
		
		try {
			if (doc.has("symbol_gene")) {
				JSONArray geneSymbols = doc.getJSONArray("symbol_gene");				
				for (Object s : geneSymbols) {
					String[] names = s.toString().split("_");				
					String url = request.getAttribute("baseUrl") + "/genes/" + names[1];
					gene.add("<a href='" + url +"'>" + names[0] + "</a>");				
				}
			}
		} 
		catch (Exception e) {
			// e.printStackTrace();
		}		
		return gene;
	}

	
	public String fetchImagePathByAnnotName(String query, String fqStr) throws IOException, URISyntaxException{
	
		String mediaBaseUrl = config.get("mediaBaseUrl");
		
		final int maxNum = 4; // max num of images to display in grid column
				
		String queryUrl = config.get("internalSolrUrl") 
				+ "/images/select?qf=auto_suggest&defType=edismax&wt=json&q=" + query			
				+ "&" + fqStr
				+ "&rows=" + maxNum;
		System.out.println("URL: " +queryUrl );
		List<String> imgPath = new ArrayList<String>();
	
		JSONObject thumbnailJson = solrIndex.getResults(queryUrl);
		JSONArray docs = thumbnailJson.getJSONObject("response").getJSONArray("docs");

		int dataLen = docs.size() < 5 ? docs.size() : maxNum;

		for (int i = 0; i < dataLen; i++) {
			JSONObject doc = docs.getJSONObject(i);
			String largeThumbNailPath = mediaBaseUrl + "/" + doc.getString("largeThumbnailFilePath");
			String img = "<img src='" + mediaBaseUrl + "/" + doc.getString("smallThumbnailFilePath") + "'/>";
			String link = "<a href='" + largeThumbNailPath + "'>" + img + "</a>";
			imgPath.add(link);
		}

		return StringUtils.join(imgPath, "");
	}

	private String concateGeneInfo(JSONObject doc, JSONObject json, String qryStr, HttpServletRequest request){
		
		List<String> geneInfo = new ArrayList<String>();	
		
		String markerSymbol = "<span class='gSymbol'>" + doc.getString("marker_symbol") + "</span>";		
		String mgiId = doc.getString("mgi_accession_id");
		//System.out.println(request.getAttribute("baseUrl"));
		String geneUrl = request.getAttribute("baseUrl") + "/genes/" + mgiId;		
		//String markerSymbolLink = "<a href='" + geneUrl + "' target='_blank'>" + markerSymbol + "</a>";
		String markerSymbolLink = "<a href='" + geneUrl + "'>" + markerSymbol + "</a>";
				
		String[] fields = {"marker_synonym","marker_name", "human_gene_symbol"};			
		for( int i=0; i<fields.length; i++){		
			try {				
				//"highlighting":{"MGI:97489":{"marker_symbol":["<em>Pax</em>5"],"synonym":["<em>Pax</em>-5"]},
				
				String field = fields[i];				
				List<String> info = new ArrayList<String>();
				
				if ( field.equals("marker_name") ){
					info.add(doc.getString(field));
				}
				else if ( field.equals("human_gene_symbol") ){					
					JSONArray data = doc.getJSONArray(field);					
					for( Object h : data ){							
						info.add(h.toString());
					}							
				}
				else if ( doc.getJSONArray(field).size() > 0) {					
					JSONArray data = doc.getJSONArray(field);
					
					//use SOLR highlighted string if available
					info = checkMatched(mgiId, field, info, json);
					if ( info.size() == 0 ){					
						for( Object d : data ){							
							info.add(d.toString());
						}
					}					
				}
				
				field = field == "human_gene_symbol" ? "human ortholog" : field.replace("marker_", " ");
				//geneInfo.add("<span class='gNameSyn'>" + field + "</span>: " + StringUtils.join(info, ", "));
				geneInfo.add("<span class='label'>" + field + "</span>: " + StringUtils.join(info, ", "));
			} 
			catch (Exception e) {		   		
			    //e.printStackTrace();
			}
		}				
		//return "<div class='geneCol'>" + markerSymbolLink + StringUtils.join(geneInfo, "<br>") + "</div>";
		return "<div class='geneCol'><div class='title'>" 
			+ markerSymbolLink 
			+ "</div>"
			+ "<div class='subinfo'>" 
			+  StringUtils.join(geneInfo, "<br>") 
			+ "</div>";
			
	}

	private List<String> checkMatched(String mgiId, String field, List<String> info, JSONObject json){
		
		if ( field.equals("marker_synonym") ){
			field = "synonym";
		}
		//"highlighting":{"MGI:97489":{"marker_symbol":["<em>Pax</em>5"],"synonym":["<em>Pax</em>-5"]},
		JSONObject hl = json.getJSONObject("highlighting");		
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
}
