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
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
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
import uk.ac.ebi.generic.util.Tools;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;

@Controller
public class DataTableController {

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	@Autowired
	private SolrIndex solrIndex;
	
	@Autowired
	private GeneService geneService;
	
	@Resource(name="globalConfiguration")
	private Map<String, String> config;
		
	
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
	
		// use this for pattern matching later, instead of the modified complexphrase q string	
		String queryOri = jParams.getString("qOri");

		String query = "";
		String fqOri = "";
		String mode = jParams.getString("mode");
		String solrParamStr = jParams.getString("params");
		System.out.println("paramstr: " + solrParamStr);
		
		// Get the query string
		String[] pairs = solrParamStr.split("&");		
		for (String pair : pairs) {
			try {
				String[] parts = pair.split("=");				
				if (parts[0].equals("q")) {
					query = parts[1];	
				}
				if (parts[0].equals("fq")) {
					fqOri = "&fq=" + parts[1];				
				}
			}catch (Exception e) {
				log.error("Error getting value of key");			
			}			
		}		
		
		boolean showImgView = false;
		if (jParams.containsKey("showImgView")) {
			showImgView = jParams.getBoolean("showImgView");
		}
		//System.out.println("query: "+ query);
		String content ="";
		if(solrCoreName.equals("gene")){
			QueryResponse rsp = geneService.getSearchGeneDataTableJson(solrParamStr, iDisplayStart, iDisplayLength);
			long numberFound=rsp.getResults().getNumFound();
			List<GeneDTO> geneList=rsp.getBeans(GeneDTO.class);
			content = fetchDataTableJson(numberFound, request, geneList, mode, queryOri, fqOri, iDisplayStart, iDisplayLength, solrParamStr, showImgView, solrCoreName);
		}else{
			JSONObject json = solrIndex.getDataTableJson(query, solrCoreName, solrParamStr, mode, iDisplayStart, iDisplayLength, showImgView);
			content = fetchDataTableJson(request, json, mode, queryOri, fqOri, iDisplayStart, iDisplayLength, solrParamStr, showImgView, solrCoreName);
		}
		
		//System.out.println("JSON: "+ json);
		

		
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

	public String fetchDataTableJson(HttpServletRequest request, JSONObject json, String mode, String query, String fqOri, int start, int length, String solrParams, boolean showImgView, String solrCoreName) throws IOException, URISyntaxException {

		String jsonStr = null;
		if (mode.equals("pipelineGrid")) {
			jsonStr = parseJsonforProtocolDataTable(json, request, solrCoreName, start);
		} 
		else if (mode.equals("imagesGrid")) {
			jsonStr = parseJsonforImageDataTable(json, start, length, solrParams, showImgView, request, query, fqOri, solrCoreName);
		} 
		else if (mode.equals("mpGrid")) {
			jsonStr = parseJsonforMpDataTable(json, request, query, solrCoreName, start);
		}
		else if (mode.equals("maGrid")) {
			jsonStr = parseJsonforMaDataTable(json, request, query, solrCoreName, start);
		}
		else if (mode.equals("diseaseGrid")) {
			jsonStr = parseJsonforDiseaseDataTable(json, request, solrCoreName, start);
		}
		return jsonStr;
	}
	
	public String fetchDataTableJson(long numberFound, HttpServletRequest request, List<GeneDTO> geneList, String mode, String query, String fqOri, int start, int length, String solrParams, boolean showImgView, String solrCoreName) throws IOException, URISyntaxException {

		String jsonStr = null;
		if (mode.equals("geneGrid")) {
			jsonStr = parseJsonforGeneDataTable(numberFound, geneList, request, query, solrCoreName);
		} 
		return jsonStr;
	}

	public String parseJsonforGeneDataTable(long numberFound, List<GeneDTO> json, HttpServletRequest request, String qryStr, String solrCoreName){	
				
		RegisterInterestDrupalSolr registerInterest = new RegisterInterestDrupalSolr(config, request);
		
		//replace this!!!!!!!
		//int totalDocs = 100;//json.getJSONObject("response").getInt("numFound");
				
		log.debug("TOTAL GENEs: " + numberFound);
		
        JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		
		j.put("iTotalRecords", numberFound);
		j.put("iTotalDisplayRecords", numberFound);

		for (int i = 0; i < json.size(); i++) {

			List<String> rowData = new ArrayList<String>();

			GeneDTO doc = json.get(i);
				
			String geneInfo = concateGeneInfo(doc, qryStr, request);
			rowData.add(geneInfo);

			
			// phenotyping status			
			String mgiId = doc.getMgiAccessionId();			
			String geneLink = request.getAttribute("baseUrl") + "/genes/" + mgiId;	
						
			// ES cell/mice production status	
			boolean toExport =false;//ind datatable controller always false in exportController is set to true
			//String esCellProdStatus=doc.getESCellProductionStatus();
			String esCellProdStatus=geneService.fetchHtmlEsCellStatusForSearchDataTable(doc, request, toExport);
			System.out.println("esCellProdStatus="+esCellProdStatus);
			//String mouseProdStatus=doc.getMouseProductionStatus();
			String mouseProdStatus = geneService.deriveLatestProductionStatusForEsCellAndMice(doc, request, toExport, geneLink);			
			System.out.println("mouseProdStatus="+mouseProdStatus);
			String usefulString=esCellProdStatus+mouseProdStatus;
			
			rowData.add(usefulString);
			
			String phenoStatus = null;
			if ( geneService.deriveLatestPhenotypingStatus(doc).equals("NA") ){
				phenoStatus = "";
			}
			else if ( geneService.deriveLatestPhenotypingStatus(doc).equals("QC") ){
				phenoStatus = "<a class='status qc' href='" + geneLink + "'><span>legacy data available</span></a>";
			}
			else {
				phenoStatus = "<a class='status done' href='" + geneLink + "#section-associations'><span>phenotype data available</span></a>";
			}
			
			rowData.add(phenoStatus);
			
			// register of interest
			if (registerInterest.loggedIn()) {
				if (registerInterest.alreadyInterested(doc.getMgiAccessionId())) {
					String uinterest = "<div class='registerforinterest' oldtitle='Unregister interest' title=''>"
							+ "<i class='fa fa-sign-out'></i>"
							+ "<a id='"+doc.getMgiAccessionId()+"' class='regInterest primary interest' href=''>&nbsp;Unregister interest</a>"
							+ "</div>";
					
					rowData.add(uinterest);					
					//rowData.add("<a id='"+doc.getString("mgi_accession_id")+"' href='' class='btn primary interest'>Unregister interest</a>");					
				} 
				else {
					String rinterest = "<div class='registerforinterest' oldtitle='Register interest' title=''>"
							+ "<i class='fa fa-sign-in'></i>"
							+ "<a id='"+doc.getMgiAccessionId()+"' class='regInterest primary interest' href=''>&nbsp;Register interest</a>"
							+ "</div>";
					
					rowData.add(rinterest);					
					//rowData.add("<a id='"+doc.getString("mgi_accession_id")+"' href='' class='btn primary interest'>Register interest</a>");
				}
			} 
			else {	
				String interest = "<div class='registerforinterest' oldtitle='Login to register interest' title=''>"
								+ "<i class='fa fa-sign-in'></i>"
								+ "<a class='regInterest' href='/user/register'>&nbsp;Interest</a>"
								//+ "<a class='regInterest' href='#'>Interest</a>"
								+ "</div>";
				
				rowData.add(interest);
				//rowData.add("<a href='/user/register' class='btn primary'>Interest</a>");
			}
			
			j.getJSONArray("aaData").add(rowData);			
		}
		
		return j.toString();	
	}
			
	public String parseJsonforProtocolDataTable(JSONObject json, HttpServletRequest request, String solrCoreName, int start){
		
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
	
	public String parseJsonforMpDataTable(JSONObject json, HttpServletRequest request, String qryStr, String solrCoreName, int start){
				
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
			
			if ( doc.containsKey("mp_term_synonym") ){
				List<String> mpSynonyms = doc.getJSONArray("mp_term_synonym");
				List<String> prefixSyns = new ArrayList();
				int count = 0;
				for ( String sn : mpSynonyms ){
					count++;
					//prefixSyns.add("synonym: "+ sn);
					sn = count == 1 ? sn : "&nbsp;&nbsp;&nbsp;" + sn;
					//prefixSyns.add(sn);
					prefixSyns.add(Tools.highlightMatchedStrIfFound(qryStr, sn, "span", "subMatch"));
				}
				
				String mpCol = "<div class='mpCol'><div class='title'>" 
						+ mpLink 
						+ "</div>"
						+ "<div class='subinfo'>" 
						+ "<b>synonym</b>: " + StringUtils.join(prefixSyns, ",<br>") 
						+ "</div>";
				rowData.add(mpCol);
			}
			else {
				rowData.add(mpLink);
			}
			
			// some MP do not have definition
			String mpDef = "No definition data available";
			try {
				//mpDef = doc.getString("mp_definition");
				mpDef =Tools.highlightMatchedStrIfFound(qryStr, doc.getString("mp_definition"), "span", "subMatch");
			} 
			catch (Exception e) {			 			
			    //e.printStackTrace();
			}
			rowData.add(mpDef);	
			
			j.getJSONArray("aaData").add(rowData);
		} 
		
		return j.toString();	
	}
	public String parseJsonforMaDataTable(JSONObject json, HttpServletRequest request, String qryStr, String solrCoreName, int start){
        
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
                                
                if ( doc.containsKey("ma_term_synonym") ){
    				List<String> maSynonyms = doc.getJSONArray("ma_term_synonym");
    				List<String> prefixSyns = new ArrayList();
    				int count = 0;
    				for ( String sn : maSynonyms ){
    					count++;
    					//prefixSyns.add("synonym: "+ sn);
    					sn = count == 1 ? sn : "&nbsp;&nbsp;&nbsp;" + sn;
    					//prefixSyns.add(sn);
    					prefixSyns.add(Tools.highlightMatchedStrIfFound(qryStr, sn, "span", "subMatch"));
    				}
    				
    				String maCol = "<div class='maCol'><div class='title'>" 
    						+ maLink 
    						+ "</div>"
    						+ "<div class='subinfo'>" 
    						+  "<b>synonym: " + StringUtils.join(prefixSyns, ",<br>") 
    						+ "</div>";
    				rowData.add(maCol);
    			}
    			else {
    				rowData.add(maLink);
    			}
                
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
	
	
	public String parseJsonforImageDataTable(JSONObject json, int start, int length, String solrParams, boolean showImgView, HttpServletRequest request, String query, String fqOri, String solrCoreName) throws IOException, URISyntaxException{
		
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
				String fullSizePath = largeThumbNailPath.replace("tn_large", "full");								
				String imgLink = "<a class='fancybox' fullres='" + fullSizePath + "' href='" + largeThumbNailPath+ "'>" + img + "</a>";				
				
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
						annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + StringUtils.join(gene, ",") + "</span>";
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
			if ( fqStr.equals("&fq=annotationTermId:M*%20OR%20expName:*%20OR%20symbol:*%20OR%20annotated_or_inferred_higherLevelMaTermName:*%20OR%20annotatedHigherLevelMpTermName:*") ){				
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
	
	public String parseJsonforDiseaseDataTable(JSONObject json, HttpServletRequest request, String solrCoreName, int start){
		
		String baseUrl = request.getAttribute("baseUrl") + "/disease/";
		
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
			String fullSizePath = largeThumbNailPath.replace("tn_large", "full");
			String img = "<img src='" + mediaBaseUrl + "/" + doc.getString("smallThumbnailFilePath") + "'/>";					
			String link = "<a class='fancybox' fullres='" + fullSizePath + "' href='" + largeThumbNailPath + "'>" + img + "</a>";
			imgPath.add(link);
		}

		return StringUtils.join(imgPath, "");
	}

	private String concateGeneInfo(GeneDTO doc, String qryStr, HttpServletRequest request){
		
		List<String> geneInfo = new ArrayList<String>();	
		
		String markerSymbol = "<span class='gSymbol'>" + doc.getMarkerSymbol() + "</span>";		
		String mgiId = doc.getMgiAccessionId();
		//System.out.println(request.getAttribute("baseUrl"));
		String geneUrl = request.getAttribute("baseUrl") + "/genes/" + mgiId;		
		//String markerSymbolLink = "<a href='" + geneUrl + "' target='_blank'>" + markerSymbol + "</a>";
		String markerSymbolLink = "<a href='" + geneUrl + "'>" + markerSymbol + "</a>";
				
		String[] fields = {GeneDTO.MARKER_NAME, GeneDTO.HUMAN_GENE_SYMBOL,GeneDTO.MARKER_SYNONYM};			
		for( int i=0; i<fields.length; i++){		
			try {				
				//"highlighting":{"MGI:97489":{"marker_symbol":["<em>Pax</em>5"],"synonym":["<em>Pax</em>-5"]},
				
				//System.out.println(qryStr);
				String field = fields[i];				
				List<String> info = new ArrayList<String>();
				
				if ( field.equals(GeneDTO.MARKER_NAME) ){
					//info.add(doc.getString(field));
					info.add(Tools.highlightMatchedStrIfFound(qryStr, doc.getMarkerName(), "span", "subMatch"));
				}
				else if ( field.equals(GeneDTO.HUMAN_GENE_SYMBOL) ){					
					List<String> data = doc.getHumanGeneSymbol();					
					for( String h : data ){							
						//info.add(h.toString());
						info.add(Tools.highlightMatchedStrIfFound(qryStr, h, "span", "subMatch"));
					}							
				}
				else if ( doc.getMarkerSynonym().size() > 0) {					
					List<String> data = doc.getMarkerSynonym();
					
					for( String d : data ){
						info.add(Tools.highlightMatchedStrIfFound(qryStr, d, "span", "subMatch"));
					}
					
				}
				
				field = field == "human_gene_symbol" ? "human ortholog" : field.replace("marker_", " ");
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
	
	
}
