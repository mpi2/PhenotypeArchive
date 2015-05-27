/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.ebi.phenotype.web.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.generic.util.RegisterInterestDrupalSolr;
import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.generic.util.SolrIndex.AnnotNameValCount;
import uk.ac.ebi.generic.util.Tools;
import uk.ac.ebi.phenotype.dao.ReferenceDAO;
import uk.ac.ebi.phenotype.ontology.SimpleOntoTerm;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.MpService;
import uk.ac.ebi.phenotype.service.dto.ReferenceDTO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Controller
public class DataTableController {

    private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @Autowired
    private SolrIndex solrIndex;

    @Autowired
    private GeneService geneService;

    @Autowired
    private MpService mpService;

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Autowired
    @Qualifier("admintoolsDataSource")
    private DataSource admintoolsDataSource;

    @Autowired
    @Qualifier("komp2DataSource")
    private DataSource komp2DataSource;

    private String IMG_NOT_FOUND = "Image coming soon<br>";
    private String NO_INFO_MSG = "No information available";
    
    @Autowired
    private ReferenceDAO referenceDAO;

    /**
     <p>
     * deals with batchQuery
     * Return jQuery dataTable from server-side for lazy-loading.
     * </p>
     * @throws SolrServerException 
     * 
     */
    
    @RequestMapping(value = "/dataTable_bq", method = RequestMethod.POST)
    public ResponseEntity<String> bqDataTableJson(
    		@RequestParam(value = "idlist", required = true) String idlist,
    		@RequestParam(value = "fllist", required = true) String fllist,
    		@RequestParam(value = "corename", required = true) String solrCoreName,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SolrServerException {

    	System.out.println("idlist: "+ idlist);
    	System.out.println("fllist: "+ fllist);
    	System.out.println("corename: "+ solrCoreName);
    	
    	String content = null;
    	
    	if ( solrCoreName.equals("hp") ){
    		// need to combine query results from phenodigm and mp cores
    		
    		// fetch all mp ids mapped to the hp ids requested
    		try {
    			QueryResponse solrResponse = solrIndex.getBatchQueryJson(idlist, "hp_id,hp_term,mp_id", "phenodigm");
    			JSONObject j = prepareHpMpMapping(solrResponse);

        		String mpIdlist = j.get("idlist").toString();
        		QueryResponse solrResponse2 = solrIndex.getBatchQueryJson(mpIdlist, fllist, "mp");
        		Map<String, List<String>> hp2mp = (Map<String, List<String>>) j.get("map");
        		
        		content = fetchBatchQueryDataTableJson2(solrResponse2, fllist, hp2mp);
    		}
    		catch (Exception e){
    			System.out.println(e.getMessage());
    		}
    	}
    	else {
    		QueryResponse solrResponse = solrIndex.getBatchQueryJson(idlist, fllist, solrCoreName);
    		content = fetchBatchQueryDataTableJson(solrResponse, fllist);
    	}
    	
    	return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
    }
		
    private String fetchBatchQueryDataTableJson2( QueryResponse solrResponse, String fllist, Map<String, List<String>> hp2mp ) {
    	
    	SolrDocumentList results = solrResponse.getResults();
    	Map<String, MpAnnotations> mpAnnotations = new HashMap<>();
    	
    	List<String> fields = new ArrayList<String>(Arrays.asList(fllist.split(",")));
    	fields.subList(0, 3).clear();  // remove first 3 fields: hp_id, hp_term, mp_id
		
    	for (int i = 0; i < results.size(); ++i) {
			SolrDocument doc = results.get(i);
			Map<String, Object> docMap = doc.getFieldValueMap();
			
			String mp_id = docMap.get("mp_id").toString();
			mpAnnotations.put(mp_id, new MpAnnotations());
			
			for ( String fieldName : fields ){
				String value = docMap.get(fieldName) != null ? docMap.get(fieldName).toString() : "";
				if ( fieldName.equals("mp_term") ){
					mpAnnotations.get(mp_id).mp_term = value;
				}
				else if ( fieldName.equals("mp_definition") ){
					mpAnnotations.get(mp_id).mp_definition = value;
				}
				else if ( fieldName.equals("top_level_mp_id") ){
					mpAnnotations.get(mp_id).top_level_mp_id = value;
				}
				else if ( fieldName.equals("top_level_mp_term") ){
					mpAnnotations.get(mp_id).top_level_mp_term = value;
				}
				else if ( fieldName.equals("mgi_accession_id") ){
					mpAnnotations.get(mp_id).mgi_accession_id = value;
				}
				else if ( fieldName.equals("marker_symbol") ){
					mpAnnotations.get(mp_id).marker_symbol = value;
				}
				else if ( fieldName.equals("human_gene_symbol") ){
					mpAnnotations.get(mp_id).human_gene_symbol = value;
				}
				else if ( fieldName.equals("disease_id") ){
					mpAnnotations.get(mp_id).disease_id = value;
				}
				else if ( fieldName.equals("disease_term") ){
					mpAnnotations.get(mp_id).disease_term = value;
				}
			}
    	}

    	int totalDocs = hp2mp.size();
    	
    	JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		
		for (Map.Entry<String, List<String>> entry : hp2mp.entrySet()){
		    List<String> mpids = entry.getValue();
		    
		    String[] hpIdTerm = entry.getKey().split("_");
		    String hp_id = hpIdTerm[0];
		    String hp_term = hpIdTerm[1];
		    
		    List<String> rowData = new ArrayList<String>();
		    
		    rowData.add(hp_id);
		    rowData.add(hp_term);
		    rowData.add(StringUtils.join(mpids, "|"));
		    
		    for ( String fieldName : fields ){
		    	List<String> vals = new ArrayList<>();
			    for ( String mp_id : mpids){
			    	//System.out.println("mpid:" + mp_id + " - field" + fieldName);
			    	if ( fieldName.equals("mp_term") ){
			    		vals.add(mpAnnotations.get(mp_id).mp_term);	
					}
					else if ( fieldName.equals("mp_definition") ){
						vals.add(mpAnnotations.get(mp_id).mp_definition);	
					}
					else if ( fieldName.equals("top_level_mp_id") ){
						vals.add(mpAnnotations.get(mp_id).top_level_mp_id);
					}
					else if ( fieldName.equals("top_level_mp_term") ){
						vals.add(mpAnnotations.get(mp_id).top_level_mp_term);
					}
					else if ( fieldName.equals("mgi_accession_id") ){
						vals.add(mpAnnotations.get(mp_id).mgi_accession_id);
					}
					else if ( fieldName.equals("marker_symbol") ){
						vals.add(mpAnnotations.get(mp_id).marker_symbol);
					}
					else if ( fieldName.equals("human_gene_symbol") ){
						vals.add(mpAnnotations.get(mp_id).human_gene_symbol);
					}
					else if ( fieldName.equals("disease_id") ){
						vals.add(mpAnnotations.get(mp_id).disease_id);
					}
					else if ( fieldName.equals("disease_term") ){
						vals.add(mpAnnotations.get(mp_id).disease_term);
					}
			    }
			    rowData.add(StringUtils.join(vals, "|"));
		    } 
		    j.getJSONArray("aaData").add(rowData);
		}

    	return j.toString();
    }
    
    private JSONObject prepareHpMpMapping(QueryResponse solrResponse) {
    	
    	JSONObject j = new JSONObject();
    	//Map<String, List<HpTermMpId>> hp2mp = new HashMap<>();
    	Map<String, List<String>> hp2mp = new HashMap<>();
    	Set<String> mpids = new HashSet<>();

    	SolrDocumentList results = solrResponse.getResults();
		for (int i = 0; i < results.size(); ++i) {
			SolrDocument doc = results.get(i);
			
			Map<String, Object> docMap = doc.getFieldValueMap();
			//String hp_id = (String) docMap.get("hp_id");
			//String hp_term = (String) docMap.get("hp_term");
			String hpidTerm = (String) docMap.get("hp_id") + "_" + (String) docMap.get("hp_term");
			String mp_id = (String) docMap.get("mp_id");
			mpids.add("\"" + mp_id + "\"");
			
			if ( ! hp2mp.containsKey(hpidTerm) ){
				hp2mp.put(hpidTerm, new ArrayList<String>());
			}
			hp2mp.get(hpidTerm).add(mp_id);
		}
    	
		j.put("map", hp2mp);
		
		List<String> ids = new ArrayList<>();
		ids.addAll(mpids);
		String idlist = StringUtils.join(ids, ",");
		j.put("idlist",  idlist);
		
    	return j;
    }
    
    public String fetchBatchQueryDataTableJson(QueryResponse solrResponse, String fllist) {
    	
    	String[] flList = StringUtils.split(fllist, ",");
    	
    	SolrDocumentList results = solrResponse.getResults();
    	int totalDocs = results.size();
    	
    	JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		
		//System.out.println("totaldocs:" + totalDocs);
		for (int i = 0; i < results.size(); ++i) {
			SolrDocument doc = results.get(i);
		
			List<String> rowData = new ArrayList<String>();
			
			Map<String, Object> docMap = doc.getFieldValueMap();
			
			//for (String fieldName : doc.getFieldNames()) {
			for ( int k=0; k<flList.length; k++ ){
				String fieldName = flList[k];
				//System.out.println(fieldName + " - value: " + docMap.get(fieldName));
				
				if ( docMap.get(fieldName) == null ){
					rowData.add("");
				}
				else {
					try {
						String value = docMap.get(fieldName).toString();
						//System.out.println("row " + i + ": field: " + k + " -- " + fieldName + " - " + value);
						rowData.add(value);
					} catch(Exception e){
						if ( e.getMessage().equals("java.lang.Integer cannot be cast to java.lang.String") ){
							int val = (int) docMap.get(fieldName);
							String value = Integer.toString(val);
							rowData.add(value);
						}
					}
				}
			}
			j.getJSONArray("aaData").add(rowData);
			
		}
		 
		//System.out.println(j.toString());
		return j.toString();
    }
    
    /**
     * <p>
     * Return jQuery dataTable from server-side for lazy-loading.
     * </p>
     *
     * @param bRegex =false bRegex_0=false bRegex_1=false bRegex_2=false
     * bSearchable_0=true bSearchable_1=true bSearchable_2=true bSortable_0=true
     * bSortable_1=true bSortable_2=true iColumns=3 for paging:
     * iDisplayLength=10 iDisplayStart=0 for sorting: iSortCol_0=0
     * iSortingCols=1 for filtering: sSearch= sSearch_0= sSearch_1= sSearch_2=
     * mDataProp_0=0 mDataProp_1=1 mDataProp_2=2 sColumns= sEcho=1
     * sSortDir_0=asc
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
            Model model) throws IOException, URISyntaxException {

        JSONObject jParams = (JSONObject) JSONSerializer.toJSON(solrParams);

        String solrCoreName = jParams.containsKey("solrCoreName") ? jParams.getString("solrCoreName") : jParams.getString("facetName");
        // use this for pattern matching later, instead of the modified complexphrase q string	
        String queryOri = jParams.getString("qOri");

        String query = "";
        String fqOri = "";
        String mode = jParams.getString("mode");
        String solrParamStr = jParams.getString("params");

        boolean legacyOnly = jParams.getBoolean("legacyOnly");
        String evidRank = jParams.containsKey("evidRank") ? jParams.getString("evidRank") : "";

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
            } catch (Exception e) {
                log.error("Error getting value of key");
            }
        }

        boolean showImgView = false;
        if (jParams.containsKey("showImgView")) {
            showImgView = jParams.getBoolean("showImgView");
        }
        JSONObject json = solrIndex.getQueryJson(query, solrCoreName, solrParamStr, mode, iDisplayStart, iDisplayLength, showImgView);
        String content = fetchDataTableJson(request, json, mode, queryOri, fqOri, iDisplayStart, iDisplayLength, solrParamStr, showImgView, solrCoreName, legacyOnly, evidRank);

        return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<String> getSolrErrorResponse(Exception e) {
        e.printStackTrace();
        String bootstrap = "<div class=\"alert\"><strong>Warning!</strong>  Error: Search functionality is currently unavailable</div>";
        String errorJSON = "{'aaData':[[' " + bootstrap + "','  ', ' ']], 'iTotalRecords':1,'iTotalDisplayRecords':1}";
        JSONObject errorJson = (JSONObject) JSONSerializer.toJSON(errorJSON);
        return new ResponseEntity<String>(errorJson.toString(), createResponseHeaders(), HttpStatus.CREATED);
    }

    private HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    public String fetchDataTableJson(HttpServletRequest request, JSONObject json, String mode, String query, String fqOri, int start, int length, String solrParams, boolean showImgView, String solrCoreName, boolean legacyOnly, String evidRank) throws IOException, URISyntaxException {

        String jsonStr = null;
        if (mode.equals("geneGrid")) {
            jsonStr = parseJsonforGeneDataTable(json, request, query, solrCoreName, legacyOnly);
        } else if (mode.equals("pipelineGrid")) {
            jsonStr = parseJsonforProtocolDataTable(json, request, solrCoreName, start);
        } else if (mode.equals("impc_imagesGrid")) {
            jsonStr = parseJsonforImpcImageDataTable(json, start, length, solrParams, showImgView, request, query, fqOri, solrCoreName);
        } else if (mode.equals("imagesGrid")) {
            jsonStr = parseJsonforImageDataTable(json, start, length, solrParams, showImgView, request, query, fqOri, solrCoreName);
        } else if (mode.equals("mpGrid")) {
            jsonStr = parseJsonforMpDataTable(json, request, query, solrCoreName, start);
        } else if (mode.equals("maGrid")) {
            jsonStr = parseJsonforMaDataTable(json, request, query, solrCoreName, start);
        } else if (mode.equals("diseaseGrid")) {
            jsonStr = parseJsonforDiseaseDataTable(json, request, solrCoreName, start);
        } else if (mode.equals("gene2go")) {
            jsonStr = parseJsonforGoDataTable(json, request, solrCoreName, evidRank);
        }
        
        return jsonStr;
    }
    
    public String parseJsonforGoDataTable(JSONObject json, HttpServletRequest request, String solrCoreName, String evidRank) {

        String hostName = request.getAttribute("mappedHostname").toString();
        String baseUrl = request.getAttribute("baseUrl").toString();

        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        int totalDocs = json.getJSONObject("response").getInt("numFound");

        log.debug("TOTAL GENE2GO: " + totalDocs);

        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);

        //GO evidence code ranking mapping
        Map<String, Integer> codeRank = SolrIndex.getGoCodeRank();

        for (int i = 0; i < docs.size(); i ++) {

            JSONObject doc = docs.getJSONObject(i);
            String marker_symbol = doc.getString("marker_symbol");
            String gId = doc.getString("mgi_accession_id");
            String glink = "<a href='" + hostName + baseUrl + "/" + gId + "'>" + marker_symbol + "</a>";

            String phenoStatus = doc.getString("latest_phenotype_status");

            String NOINFO = "no info available";

            // has GO
            if (doc.containsKey("go_count")) {
                List<String> rowData = new ArrayList<String>();
                rowData.add(glink);
                rowData.add(phenoStatus);
                rowData.add(Integer.toString(doc.getInt("go_count")));
                rowData.add("<i class='fa fa-plus-square'></i>");
                j.getJSONArray("aaData").add(rowData);
            } else {
                // No GO
                List<String> rowData = new ArrayList<String>();

                rowData.add(glink);
                rowData.add(phenoStatus);
                rowData.add(NOINFO);
                rowData.add("");

                j.getJSONArray("aaData").add(rowData);
            }
        }
        return j.toString();
    }

    public String parseJsonforGeneDataTable(JSONObject json, HttpServletRequest request, String qryStr, String solrCoreName, boolean legacyOnly) {

        RegisterInterestDrupalSolr registerInterest = new RegisterInterestDrupalSolr(config.get("drupalBaseUrl"), request);

        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        int totalDocs = json.getJSONObject("response").getInt("numFound");

        log.debug("TOTAL GENEs: " + totalDocs);

        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);

        for (int i = 0; i < docs.size(); i ++) {

            List<String> rowData = new ArrayList<String>();

            JSONObject doc = docs.getJSONObject(i);
            String geneInfo = concateGeneInfo(doc, json, qryStr, request);
            rowData.add(geneInfo);

            // phenotyping status			
            String mgiId = doc.getString("mgi_accession_id");
            String geneLink = request.getAttribute("baseUrl") + "/genes/" + mgiId;

            // ES cell/mice production status	
            boolean toExport = false;

            String prodStatus = geneService.getLatestProductionStatusForEsCellAndMice(doc, request, toExport, geneLink);
            rowData.add(prodStatus);

            String phenotypeStatusHTMLRepresentation = geneService.getPhenotypingStatus(doc, request, toExport, legacyOnly);
            rowData.add(phenotypeStatusHTMLRepresentation);

            // register of interest
            if (registerInterest.loggedIn()) {
                if (registerInterest.alreadyInterested(doc.getString("mgi_accession_id"))) {
                    String uinterest = "<div class='registerforinterest' oldtitle='Unregister interest' title=''>"
                            + "<i class='fa fa-sign-out'></i>"
                            + "<a id='" + doc.getString("mgi_accession_id") + "' class='regInterest primary interest' href=''>&nbsp;Unregister interest</a>"
                            + "</div>";

                    rowData.add(uinterest);
                    //rowData.add("<a id='"+doc.getString("mgi_accession_id")+"' href='' class='btn primary interest'>Unregister interest</a>");					
                } else {
                    String rinterest = "<div class='registerforinterest' oldtitle='Register interest' title=''>"
                            + "<i class='fa fa-sign-in'></i>"
                            + "<a id='" + doc.getString("mgi_accession_id") + "' class='regInterest primary interest' href=''>&nbsp;Register interest</a>"
                            + "</div>";

                    rowData.add(rinterest);
                    //rowData.add("<a id='"+doc.getString("mgi_accession_id")+"' href='' class='btn primary interest'>Register interest</a>");
                }
            } else {
				// use the login link instead of register link to avoid user clicking on tab which
                // will strip out destination link that we don't want to see happened
                String interest = "<div class='registerforinterest' oldtitle='Login to register interest' title=''>"
                        + "<i class='fa fa-sign-in'></i>"
                        + "<a class='regInterest' href='/user/login?destination=data/search#fq=*:*&facet=gene'>&nbsp;Interest</a>"
                        //+ "<a class='regInterest' href='#'>Interest</a>"
                        + "</div>";

                rowData.add(interest);
                //rowData.add("<a href='/user/register' class='btn primary'>Interest</a>");
            }

            j.getJSONArray("aaData").add(rowData);
        }

        return j.toString();
    }

    public String parseJsonforProtocolDataTable(JSONObject json, HttpServletRequest request, String solrCoreName, int start) {

        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        int totalDocs = json.getJSONObject("response").getInt("numFound");

        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);

        String impressBaseUrl = request.getAttribute("drupalBaseUrl") + "/impress/impress/displaySOP/";

        for (int i = 0; i < docs.size(); i ++) {
            List<String> rowData = new ArrayList<String>();

            JSONObject doc = docs.getJSONObject(i);

            String parameter = doc.getString("parameter_name");
            rowData.add(parameter);

            // a parameter can belong to multiple procedures
            JSONArray procedures = doc.getJSONArray("procedure_name");
            JSONArray procedure_stable_keys = doc.getJSONArray("procedure_stable_key");

            List<String> procedureLinks = new ArrayList<String>();
            for (int p = 0; p < procedures.size(); p ++) {
                String procedure = procedures.get(p).toString();
                String procedure_stable_key = procedure_stable_keys.get(p).toString();
                procedureLinks.add("<a href='" + impressBaseUrl + procedure_stable_key + "'>" + procedure + "</a>");
            }

            rowData.add(StringUtils.join(procedureLinks, "<br>"));

            String pipeline = doc.getString("pipeline_name");
            rowData.add(pipeline);

            j.getJSONArray("aaData").add(rowData);
        }

        return j.toString();
    }

    public String parseJsonforMpDataTable(JSONObject json, HttpServletRequest request, String qryStr, String solrCoreName, int start) {

        RegisterInterestDrupalSolr registerInterest = new RegisterInterestDrupalSolr(config.get("drupalBaseUrl"), request);
        String baseUrl = request.getAttribute("baseUrl") + "/phenotypes/";

        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        int totalDocs = json.getJSONObject("response").getInt("numFound");

        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);

        for (int i = 0; i < docs.size(); i ++) {
            List<String> rowData = new ArrayList<String>();

            // array element is an alternate of facetField and facetCount			
            JSONObject doc = docs.getJSONObject(i);

            String mpId = doc.getString("mp_id");
            String mpTerm = doc.getString("mp_term");
            String mpLink = "<a href='" + baseUrl + mpId + "'>" + mpTerm + "</a>";
            String mpCol = null;

            if (doc.containsKey("mp_term_synonym") || doc.containsKey("hp_term")) {

                mpCol = "<div class='title'>" + mpLink + "</div>";

                if (doc.containsKey("mp_term_synonym")) {
                    List<String> mpSynonyms = doc.getJSONArray("mp_term_synonym");
                    List<String> prefixSyns = new ArrayList();

                    for (String sn : mpSynonyms) {
                        prefixSyns.add(Tools.highlightMatchedStrIfFound(qryStr, sn, "span", "subMatch"));
                    }

                    String syns = null;
                    if (prefixSyns.size() > 1) {
                        syns = "<ul class='synonym'><li>" + StringUtils.join(prefixSyns, "</li><li>") + "</li></ul>";
                    } else {
                        syns = prefixSyns.get(0);
                    }

//					mpCol = "<div class='mpCol'><div class='title'>" 
//							+ mpLink 
//							+ "</div>"
//							+ "<div class='subinfo'>" 
//							+ "<span class='label'>synonym</span>: " + syns
//							+ "</div>";
                    //rowData.add(mpCol);
                    mpCol += "<div class='subinfo'>"
                            + "<span class='label'>synonym</span>: "
                            + syns
                            + "</div>";
                }
                if (doc.containsKey("hp_term")) {

					// MP -> HP computational mapping
                    Set<SimpleOntoTerm> hpTerms = mpService.getComputationalHPTerms(doc);
                    String mappedHpTerms = "";

                    if (hpTerms.size() > 1) {
                        for (SimpleOntoTerm term : hpTerms) {
                            if ( ! term.getTermName().equals("")) {
                                mappedHpTerms += "<li>" + term.getTermName() + "</li>";
                            }
                        }
                        mappedHpTerms = "<ul class='hpTerms'>" + mappedHpTerms + "</ul>";
                    } else {
                        Iterator hi = hpTerms.iterator();
                        SimpleOntoTerm term = (SimpleOntoTerm) hi.next();
                        mappedHpTerms = term.getTermName();
                    }
                    mpCol += "<div class='subinfo'>"
                            + "<span class='label'>computationally mapped HP term</span>: "
                            + mappedHpTerms
                            + "</div>";
                }
                mpCol = "<div class='mpCol'>" + mpCol + "</div>";
                rowData.add(mpCol);
            } else {
                rowData.add(mpLink);
            }

            // some MP do not have definition
            String mpDef = "No definition data available";
            try {
                //mpDef = doc.getString("mp_definition");
                mpDef = Tools.highlightMatchedStrIfFound(qryStr, doc.getString("mp_definition"), "span", "subMatch");
            } catch (Exception e) {
                //e.printStackTrace();
            }
            rowData.add(mpDef);

            // number of genes annotated to this MP
            int numCalls = doc.containsKey("pheno_calls") ? doc.getInt("pheno_calls") : 0;
            rowData.add(Integer.toString(numCalls));

            // register of interest
            if (registerInterest.loggedIn()) {
                if (registerInterest.alreadyInterested(mpId)) {
                    String uinterest = "<div class='registerforinterest' oldtitle='Unregister interest' title=''>"
                            + "<i class='fa fa-sign-out'></i>"
                            + "<a id='" + mpId + "' class='regInterest primary interest' href=''>&nbsp;Unregister interest</a>"
                            + "</div>";

                    rowData.add(uinterest);
                } else {
                    String rinterest = "<div class='registerforinterest' oldtitle='Register interest' title=''>"
                            + "<i class='fa fa-sign-in'></i>"
                            + "<a id='" + mpId + "' class='regInterest primary interest' href=''>&nbsp;Register interest</a>"
                            + "</div>";

                    rowData.add(rinterest);
                }
            } else {
				// use the login link instead of register link to avoid user clicking on tab which
                // will strip out destination link that we don't want to see happened
                String interest = "<div class='registerforinterest' oldtitle='Login to register interest' title=''>"
                        + "<i class='fa fa-sign-in'></i>"
                        + "<a class='regInterest' href='/user/login?destination=data/search#fq=*:*&facet=mp'>&nbsp;Interest</a>"
                        //+ "<a class='regInterest' href='#'>Interest</a>"
                        + "</div>";

                rowData.add(interest);
            }

            j.getJSONArray("aaData").add(rowData);
        }

        return j.toString();
    }

    public String parseJsonforMaDataTable(JSONObject json, HttpServletRequest request, String qryStr, String solrCoreName, int start) {

        String baseUrl = request.getAttribute("baseUrl") + "/anatomy/";

        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        int totalDocs = json.getJSONObject("response").getInt("numFound");

        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);

        for (int i = 0; i < docs.size(); i ++) {
            List<String> rowData = new ArrayList<String>();

            // array element is an alternate of facetField and facetCount                        
            JSONObject doc = docs.getJSONObject(i);
            String maId = doc.getString("ma_id");
            String maTerm = doc.getString("ma_term");
            String maLink = "<a href='" + baseUrl + maId + "'>" + maTerm + "</a>";

            if (doc.containsKey("ma_term_synonym")) {
                List<String> maSynonyms = doc.getJSONArray("ma_term_synonym");
                List<String> prefixSyns = new ArrayList();

                for (String sn : maSynonyms) {
                    prefixSyns.add(Tools.highlightMatchedStrIfFound(qryStr, sn, "span", "subMatch"));
                }

                String syns = null;
                if (prefixSyns.size() > 1) {
                    syns = "<ul class='synonym'><li>" + StringUtils.join(prefixSyns, "</li><li>") + "</li></ul>";
                } else {
                    syns = prefixSyns.get(0);
                }

                String maCol = "<div class='maCol'><div class='title'>"
                        + maLink
                        + "</div>"
                        + "<div class='subinfo'>"
                        + "<span class='label'>synonym: </span>" + syns
                        + "</div>";
                rowData.add(maCol);
            } else {
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

    public String parseJsonforImpcImageDataTable(JSONObject json, int start, int length, String solrParams, boolean showImgView, HttpServletRequest request, String query, String fqOri, String solrCoreName) throws IOException, URISyntaxException {

        String baseUrl = (String) request.getAttribute("baseUrl");
        //String mediaBaseUrl = config.get("mediaBaseUrl");
        String mediaBaseUrl = baseUrl + "/impcImages/images?";
		//https://dev.mousephenotype.org/data/impcImages/images?q=observation_type:image_record&fq=%28biological_sample_group:experimental%29%20AND%20%28procedure_name:%22Combined%20SHIRPA%20and%20Dysmorphology%22%29%20AND%20%28gene_symbol:Cox19%29
        //System.out.println("baseurl: "+ baseUrl);

        if (showImgView) {
        	
            // image view: one image per row
            JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
            int totalDocs = json.getJSONObject("response").getInt("numFound");

            JSONObject j = new JSONObject();
            j.put("aaData", new Object[0]);

            j.put("iTotalRecords", totalDocs);
            j.put("iTotalDisplayRecords", totalDocs);

			//String imgBaseUrl = mediaBaseUrl + "/"; 
            for (int i = 0; i < docs.size(); i ++) {

                List<String> rowData = new ArrayList<String>();
                JSONObject doc = docs.getJSONObject(i);
                String annots = "";
                
                String imgLink = null;

                if (doc.containsKey("jpeg_url")) {

                    String fullSizePath = doc.getString("jpeg_url"); //http://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_image/7257/
                    String thumbnailPath = fullSizePath.replace("render_image", "render_thumbnail");
                    String smallThumbNailPath = thumbnailPath + "/200";  //width in pixel
                    String largeThumbNailPath = thumbnailPath + "/800";  //width in pixel
                    String img = "<img src='" + smallThumbNailPath + "'/>";
                    if(doc.getString("download_url").contains("annotation")){
                    	imgLink = "<a href='" + doc.getString("download_url") + "'>" + img + "</a>";
                    }else{
                    	imgLink = "<a class='fancybox' fullres='" + fullSizePath +  "' href='" + largeThumbNailPath + "'>" + img + "</a>";
                    }
                } else {
                    imgLink = IMG_NOT_FOUND;
                }

                try {
					//ArrayList<String> mp = new ArrayList<String>();
                	ArrayList<String> ma = new ArrayList<String>();
                    ArrayList<String> procedures = new ArrayList<String>();

                    int counter = 0;

//					if (doc.has("annotationTermId")) {
//						JSONArray termIds   = doc.getJSONArray("annotationTermId");
//						JSONArray termNames = doc.getJSONArray("annotationTermName");
//						for( Object s : termIds ){														
//							if ( s.toString().contains("MA")){
//								log.debug(i + " - MA: " + termNames.get(counter).toString());
//								String name = termNames.get(counter).toString();
//								String maid = termIds.get(counter).toString();	
//								String url = request.getAttribute("baseUrl") + "/anatomy/" + maid;
//								ma.add("<a href='" + url + "'>" + name + "</a>");
//							}
//							else if ( s.toString().contains("MP") ){
//								log.debug(i+ " - MP: " + termNames.get(counter).toString());
//								log.debug(i+ " - MP: " + termIds.get(counter).toString());								
//								String mpid = termIds.get(counter).toString();							
//								String name = termNames.get(counter).toString();							
//								String url = request.getAttribute("baseUrl") + "/phenotypes/" + mpid;
//								mp.add("<a href='" + url + "'>" + name + "</a>");
//							}
//							counter++;
//						}
//					}	
					
					if (doc.has("ma_id")) {
						JSONArray termIds   = doc.getJSONArray("ma_id");
						JSONArray termNames = doc.getJSONArray("ma_term");
						for( Object s : termIds ){														
							log.info(i + " - MA: " + termNames.get(counter).toString());
							log.debug(i + " - MA: " + termNames.get(counter).toString());
							String name = termNames.get(counter).toString();
							String maid = termIds.get(counter).toString();	
							String url = request.getAttribute("baseUrl") + "/anatomy/" + maid;
							ma.add("<a href='" + url + "'>" + name + "</a>");
							
							counter++;
						}
					}	
					
                    if (doc.has("procedure_name")) {
                        String procedureName = doc.getString("procedure_name");
                        procedures.add(procedureName);
                    }

//					if ( mp.size() == 1 ){
//						annots += "<span class='imgAnnots'><span class='annotType'>MP</span>: " + StringUtils.join(mp, ", ") + "</span>";
//					}
//					else if ( mp.size() > 1 ){
//						String list = "<ul class='imgMp'><li>" + StringUtils.join(mp, "</li><li>") + "</li></ul>";
//						annots += "<span class='imgAnnots'><span class='annotType'>MP</span>: " + list + "</span>";
//					}
//					
//					
					if ( ma.size() == 1 ){
						annots += "<span class='imgAnnots'><span class='annotType'>MA</span>: " + StringUtils.join(ma, ", ") + "</span>";
					}
					else if ( ma.size() > 1 ){
						String list = "<ul class='imgMa'><li>" + StringUtils.join(ma, "</li><li>") + "</li></ul>";
						annots += "<span class='imgAnnots'><span class='annotType'>MA</span>: " + list + "</span>";
					}
                    if (procedures.size() == 1) {
                        annots += "<span class='imgAnnots'><span class='annotType'>Procedure</span>: " + StringUtils.join(procedures, ", ") + "</span>";
                    } else if (procedures.size() > 1) {
                        String list = "<ul class='imgProcedure'><li>" + StringUtils.join(procedures, "</li><li>") + "</li></ul>";
                        annots += "<span class='imgAnnots'><span class='annotType'>Procedure</span>: " + list + "</span>";
                    }

                    // gene link
                    if (doc.has("gene_symbol")) {
                        String geneSymbol = doc.getString("gene_symbol");
                        String geneAccessionId = doc.getString("gene_accession_id");
                        String url = baseUrl + "/genes/" + geneAccessionId;
                        String geneLink = "<a href='" + url + "'>" + geneSymbol + "</a>";

                        annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + geneLink + "</span>";
                    }

//					ArrayList<String> gene = fetchImgGeneAnnotations(doc, request);
//					if ( gene.size() == 1 ){						
//						annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + StringUtils.join(gene, ",") + "</span>";
//					}
//					else if ( gene.size() > 1 ){
//						String list = "<ul class='imgGene'><li>" + StringUtils.join(gene, "</li><li>") + "</li></ul>";
//						annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + list + "</span>";
//					}
                    rowData.add(annots);
                    rowData.add(imgLink);

                    j.getJSONArray("aaData").add(rowData);
                } catch (Exception e) {
                    // some images have no annotations					
                    rowData.add("No information available");
                    rowData.add(imgLink);
                    j.getJSONArray("aaData").add(rowData);
                }
            }

            return j.toString();
        } else {
            // annotation view: images group by annotationTerm per row
            String fqStr = fqOri;

            String defaultQStr = "observation_type:image_record&qf=auto_suggest&defType=edismax";

            if (query != "") {
                defaultQStr = "q=" + query + " AND " + defaultQStr;
            } else {
                defaultQStr = "q=" + defaultQStr;
            }

            String defaultFqStr = "fq=(biological_sample_group:experimental)";

            if ( ! fqOri.contains("fq=*:*")) {
                fqStr = fqStr.replace("&fq=", "");
                defaultFqStr = defaultFqStr + " AND " + fqStr;
            }

            List<AnnotNameValCount> annots = solrIndex.mergeImpcFacets(json, baseUrl);
            int numAnnots = annots.size();

            JSONObject j = new JSONObject();
            j.put("aaData", new Object[0]);

            j.put("iTotalRecords", numAnnots);
            j.put("iTotalDisplayRecords", numAnnots);

            int end = start + length > numAnnots ? numAnnots : start + length;

            for (int i = start; i < end; i = i + 1) {

                List<String> rowData = new ArrayList<String>();

                AnnotNameValCount annot = annots.get(i);
               
                String displayAnnotName = annot.name;
                String annotVal = annot.val;
                String annotId = annot.id;

                String link = annot.link != null ? annot.link : "";
                String valLink = "<a href='" + link + "'>" + annotVal + "</a>";

                String thisFqStr = defaultFqStr + " AND " + annot.facet + ":\"" + annotVal + "\"";
                
                //https://dev.mousephenotype.org/data/impcImages/images?q=observation_type:image_record&fq=biological_sample_group:experimental"
                String imgSubSetLink = null;
                String thisImgUrl = null;
                List pathAndImgCount = solrIndex.fetchImpcImagePathByAnnotName(query, thisFqStr);
               
                int imgCount = (int) pathAndImgCount.get(1);
                
                String unit = imgCount > 1 ? "images" : "image";
                
                if (imgCount == 0) {
                    imgSubSetLink = imgCount + " " + unit;
                } else {
                    String currFqStr = null;
                    if (displayAnnotName.equals("Gene")) {
                        currFqStr = defaultFqStr + " AND gene_symbol:\"" + annotVal + "\"";
                    } 
                    else if (displayAnnotName.equals("Procedure")) {
                        currFqStr = defaultFqStr + " AND procedure_name:\"" + annotVal + "\"";
                    }
                    else if  (displayAnnotName.equals("MA")) {
                        currFqStr = defaultFqStr + " AND ma_id:\"" + annotId + "\"";
                    }

                    //String thisImgUrl = mediaBaseUrl + defaultQStr + " AND (" + query + ")&" + defaultFqStr;
                    thisImgUrl = mediaBaseUrl + defaultQStr + '&' + currFqStr;
                    
                    imgSubSetLink = "<a href='" + thisImgUrl + "'>" + imgCount + " " + unit + "</a>";
                }
                
                rowData.add("<span class='annotType'>" + displayAnnotName + "</span>: " + valLink + " (" + imgSubSetLink + ")");
                rowData.add(pathAndImgCount.get(0).toString());

                j.getJSONArray("aaData").add(rowData);

            }

            return j.toString();
        }
    }

    public String parseJsonforImageDataTable(JSONObject json, int start, int length, String solrParams, boolean showImgView, HttpServletRequest request, String query, String fqOri, String solrCoreName) throws IOException, URISyntaxException {

        String mediaBaseUrl = config.get("mediaBaseUrl");

        if (showImgView) {
            // image view: one image per row
            JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
            int totalDocs = json.getJSONObject("response").getInt("numFound");

            JSONObject j = new JSONObject();
            j.put("aaData", new Object[0]);

            j.put("iTotalRecords", totalDocs);
            j.put("iTotalDisplayRecords", totalDocs);

            String imgBaseUrl = mediaBaseUrl + "/";

            for (int i = 0; i < docs.size(); i ++) {

                List<String> rowData = new ArrayList<String>();
                JSONObject doc = docs.getJSONObject(i);
                String annots = "";

                String largeThumbNailPath = imgBaseUrl + doc.getString("largeThumbnailFilePath");
                String img = "<img src='" + imgBaseUrl + doc.getString("smallThumbnailFilePath") + "'/>";
                String fullSizePath = largeThumbNailPath.replace("tn_large", "full");
                String imgLink = "<a class='fancybox' fullres='" + fullSizePath + "' href='" + largeThumbNailPath + "'>" + img + "</a>";

                try {
                    ArrayList<String> mp = new ArrayList<String>();
                    ArrayList<String> ma = new ArrayList<String>();
                    ArrayList<String> exp = new ArrayList<String>();

                    int counter = 0;

                    if (doc.has("annotationTermId")) {
                        JSONArray termIds = doc.getJSONArray("annotationTermId");
                        JSONArray termNames = doc.getJSONArray("annotationTermName");
                        for (Object s : termIds) {
                            if (s.toString().contains("MA")) {
                                log.debug(i + " - MA: " + termNames.get(counter).toString());
                                String name = termNames.get(counter).toString();
                                String maid = termIds.get(counter).toString();
                                String url = request.getAttribute("baseUrl") + "/anatomy/" + maid;
                                ma.add("<a href='" + url + "'>" + name + "</a>");
                            } else if (s.toString().contains("MP")) {
                                log.debug(i + " - MP: " + termNames.get(counter).toString());
                                log.debug(i + " - MP: " + termIds.get(counter).toString());
                                String mpid = termIds.get(counter).toString();
                                String name = termNames.get(counter).toString();
                                String url = request.getAttribute("baseUrl") + "/phenotypes/" + mpid;
                                mp.add("<a href='" + url + "'>" + name + "</a>");
                            }
                            counter ++;
                        }
                    }

                    if (doc.has("expName")) {
                        JSONArray expNames = doc.getJSONArray("expName");
                        for (Object s : expNames) {
                            log.debug(i + " - expTERM: " + s.toString());
                            exp.add(s.toString());
                        }
                    }

                    if (mp.size() == 1) {
                        annots += "<span class='imgAnnots'><span class='annotType'>MP</span>: " + StringUtils.join(mp, ", ") + "</span>";
                    } else if (mp.size() > 1) {
                        String list = "<ul class='imgMp'><li>" + StringUtils.join(mp, "</li><li>") + "</li></ul>";
                        annots += "<span class='imgAnnots'><span class='annotType'>MP</span>: " + list + "</span>";
                    }

                    if (ma.size() == 1) {
                        annots += "<span class='imgAnnots'><span class='annotType'>MA</span>: " + StringUtils.join(ma, ", ") + "</span>";
                    } else if (ma.size() > 1) {
                        String list = "<ul class='imgMa'><li>" + StringUtils.join(ma, "</li><li>") + "</li></ul>";
                        annots += "<span class='imgAnnots'><span class='annotType'>MA</span>: " + list + "</span>";
                    }

                    if (exp.size() == 1) {
                        annots += "<span class='imgAnnots'><span class='annotType'>Procedure</span>: " + StringUtils.join(exp, ", ") + "</span>";
                    } else if (exp.size() > 1) {
                        String list = "<ul class='imgProcedure'><li>" + StringUtils.join(exp, "</li><li>") + "</li></ul>";
                        annots += "<span class='imgAnnots'><span class='annotType'>Procedure</span>: " + list + "</span>";
                    }

                    ArrayList<String> gene = fetchImgGeneAnnotations(doc, request);
                    if (gene.size() == 1) {
                        annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + StringUtils.join(gene, ",") + "</span>";
                    } else if (gene.size() > 1) {
                        String list = "<ul class='imgGene'><li>" + StringUtils.join(gene, "</li><li>") + "</li></ul>";
                        annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + list + "</span>";
                    }

                    rowData.add(annots);
                    rowData.add(imgLink);
                    j.getJSONArray("aaData").add(rowData);
                } catch (Exception e) {
                    // some images have no annotations					
                    rowData.add("No information available");
                    rowData.add(imgLink);
                    j.getJSONArray("aaData").add(rowData);
                }
            }

            return j.toString();
        } else {
			// annotation view: images group by annotationTerm per row

            String fqStr = fqOri;
            if (fqStr.equals("&fq=annotationTermId:M*%20OR%20expName:*%20OR%20symbol:*%20OR%20annotated_or_inferred_higherLevelMaTermName:*%20OR%20annotatedHigherLevelMpTermName:*")) {
                solrParams = solrParams.replace(fqOri, "");
                fqStr = "";
            }

            String imgUrl = request.getAttribute("baseUrl") + "/imagesb?" + solrParams;

            JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");

            JSONArray facets = solrIndex.mergeFacets(facetFields);

            int numFacets = facets.size();

			//System.out.println("Number of facets: " + numFacets);
            JSONObject j = new JSONObject();
            j.put("aaData", new Object[0]);

            j.put("iTotalRecords", numFacets / 2);
            j.put("iTotalDisplayRecords", numFacets / 2);

            int end = start + length;
			//System.out.println("Start: "+start*2+", End: "+end*2); 

			// The facets array looks like:
            //   [0] = facet name
            //   [1] = facet count for [0]
            //   [n] = facet name
            //   [n+1] = facet count for [n]
            // So we start at 2 times the start to skip over all the n+1
            // and increase the end similarly.
            for (int i = start * 2; i < end * 2; i = i + 2) {

                if (facets.size() <= i) {
                    break;
                }//stop when we hit the end

                String[] names = facets.get(i).toString().split("_");

                if (names.length == 2) {  // only want facet value of xxx_yyy

                    List<String> rowData = new ArrayList<String>();

                    Map<String, String> hm = solrIndex.renderFacetField(names, request); //MA:xxx, MP:xxx, MGI:xxx, exp				
                    String displayAnnotName = "<span class='annotType'>" + hm.get("label").toString() + "</span>: " + hm.get("link").toString();
                    String facetField = hm.get("field").toString();

                    String imgCount = facets.get(i + 1).toString();
                    String unit = Integer.parseInt(imgCount) > 1 ? "images" : "image";

                    imgUrl = imgUrl.replaceAll("&q=.+&", "&q=" + query + " AND " + facetField + ":\"" + names[0] + "\"&");
                    String imgSubSetLink = "<a href='" + imgUrl + "'>" + imgCount + " " + unit + "</a>";

                    rowData.add(displayAnnotName + " (" + imgSubSetLink + ")");

					// messy here, as ontodb (the latest term name info) may not have the terms in ann_annotation table
                    // so we just use the name from ann_annotation table
                    String thisFqStr = "";
                    String fq = "";
                    if (facetField == "annotationTermName") {
                        fq = "(" + facetField + ":\"" + names[0] + "\" OR annotationTermName:\"" + names[0] + "\")";
                    } else {
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

    public String parseJsonforDiseaseDataTable(JSONObject json, HttpServletRequest request, String solrCoreName, int start) {

        String baseUrl = request.getAttribute("baseUrl") + "/disease/";

        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        int totalDocs = json.getJSONObject("response").getInt("numFound");

        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);

        for (int i = 0; i < docs.size(); i ++) {
            List<String> rowData = new ArrayList<String>();

            // disease link			
            JSONObject doc = docs.getJSONObject(i);
            //System.out.println(" === JSON DOC IN DISEASE === : " + doc.toString());
            String diseaseId = doc.getString("disease_id");
            String diseaseTerm = doc.getString("disease_term");
            String diseaseLink = "<a href='" + baseUrl + diseaseId + "'>" + diseaseTerm + "</a>";
            rowData.add(diseaseLink);

            // disease source
            String src = doc.getString("disease_source");
            rowData.add(src);

            // curated data: human/mouse	
            String human = "<span class='status done'>human</span>";
            String mice = "<span class='status done'>mice</span>";

            // predicted data: impc/mgi
            String impc = "<span class='status done'>IMPC</span>";
            String mgi = "<span class='status done'>MGI</span>";

            /*var oSubFacets2 = {'curated': {'label':'With Curated Gene Associations', 
             'subfacets':{'human_curated':'From human data (OMIM, Orphanet)', 
             'mouse_curated':'From mouse data (MGI)',
             'impc_predicted_known_gene':'From human data with IMPC prediction',
             'mgi_predicted_known_gene':'From human data with MGI prediction'}
             },
             'predicted':{'label':'With Predicted Gene Associations by Phenotype', 
             'subfacets': {'impc_predicted':'From IMPC data',
             'impc_novel_predicted_in_locus':'Novel IMPC prediction in linkage locus',
             'mgi_predicted':'From MGI data',
             'mgi_novel_predicted_in_locus':'Novel MGI prediction in linkage locus'}
             }
             };
             */
            try {
                //String isHumanCurated = doc.getString("human_curated").equals("true") ? human : "";			
                String isHumanCurated = doc.getString("human_curated").equals("true")
                        || doc.getString("impc_predicted_known_gene").equals("true")
                        || doc.getString("mgi_predicted_known_gene").equals("true") ? human : "";

                String isMouseCurated = doc.getString("mouse_curated").equals("true") ? mice : "";
                rowData.add(isHumanCurated + isMouseCurated);

				//rowData.add("test1" + "test2");
                //String isImpcPredicted = (doc.getString("impc_predicted").equals("true") || doc.getString("impc_predicted_in_locus").equals("true")) ? impc : "";				
                //String isMgiPredicted = (doc.getString("mgi_predicted").equals("true") || doc.getString("mgi_predicted_in_locus").equals("true")) ? mgi : "";
                String isImpcPredicted = (doc.getString("impc_predicted").equals("true") || doc.getString("impc_novel_predicted_in_locus").equals("true")) ? impc : "";
                String isMgiPredicted = (doc.getString("mgi_predicted").equals("true") || doc.getString("mgi_novel_predicted_in_locus").equals("true")) ? mgi : "";

                rowData.add(isImpcPredicted + isMgiPredicted);
				//rowData.add("test3" + "test4");		
                //System.out.println("DOCS: " + rowData.toString());
                j.getJSONArray("aaData").add(rowData);
            } catch (Exception e) {
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
                    gene.add("<a href='" + url + "'>" + names[0] + "</a>");
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return gene;
    }

    public String fetchImagePathByAnnotName(String query, String fqStr) throws IOException, URISyntaxException {

        String mediaBaseUrl = config.get("mediaBaseUrl");

        final int maxNum = 4; // max num of images to display in grid column

        String queryUrl = config.get("internalSolrUrl")
                + "/images/select?qf=auto_suggest&defType=edismax&wt=json&q=" + query
                + "&" + fqStr
                + "&rows=" + maxNum;
        //System.out.println("URL: " +queryUrl );
        List<String> imgPath = new ArrayList<String>();

        JSONObject thumbnailJson = solrIndex.getResults(queryUrl);
        JSONArray docs = thumbnailJson.getJSONObject("response").getJSONArray("docs");

        int dataLen = docs.size() < 5 ? docs.size() : maxNum;

        for (int i = 0; i < dataLen; i ++) {
            JSONObject doc = docs.getJSONObject(i);
            String largeThumbNailPath = mediaBaseUrl + "/" + doc.getString("largeThumbnailFilePath");
            String fullSizePath = largeThumbNailPath.replace("tn_large", "full");
            String img = "<img src='" + mediaBaseUrl + "/" + doc.getString("smallThumbnailFilePath") + "'/>";
            String link = "<a class='fancybox' fullres='" + fullSizePath + "' href='" + largeThumbNailPath + "'>" + img + "</a>";

            imgPath.add(link);
        }

        return StringUtils.join(imgPath, "");
    }

    private String concateGeneInfo(JSONObject doc, JSONObject json, String qryStr, HttpServletRequest request) {

        List<String> geneInfo = new ArrayList<String>();

        String markerSymbol = "<span class='gSymbol'>" + doc.getString("marker_symbol") + "</span>";
        String mgiId = doc.getString("mgi_accession_id");
        //System.out.println(request.getAttribute("baseUrl"));
        String geneUrl = request.getAttribute("baseUrl") + "/genes/" + mgiId;
        //String markerSymbolLink = "<a href='" + geneUrl + "' target='_blank'>" + markerSymbol + "</a>";
        String markerSymbolLink = "<a href='" + geneUrl + "'>" + markerSymbol + "</a>";

        String[] fields = {"marker_name", "human_gene_symbol", "marker_synonym"};
        for (int i = 0; i < fields.length; i ++) {
            try {
				//"highlighting":{"MGI:97489":{"marker_symbol":["<em>Pax</em>5"],"synonym":["<em>Pax</em>-5"]},

                //System.out.println(qryStr);
                String field = fields[i];
                List<String> info = new ArrayList<String>();

                if (field.equals("marker_name")) {
                    //info.add(doc.getString(field));
                    info.add(Tools.highlightMatchedStrIfFound(qryStr, doc.getString(field), "span", "subMatch"));
                } else if (field.equals("human_gene_symbol")) {
                    JSONArray data = doc.getJSONArray(field);
                    for (Object h : data) {
                        //info.add(h.toString());
                        info.add(Tools.highlightMatchedStrIfFound(qryStr, h.toString(), "span", "subMatch"));
                    }
                } else if (field.equals("marker_synonym")) {
                    JSONArray data = doc.getJSONArray(field);
                    for (Object d : data) {
                        info.add(Tools.highlightMatchedStrIfFound(qryStr, d.toString(), "span", "subMatch"));
                    }
                }

                field = field == "human_gene_symbol" ? "human ortholog" : field.replace("marker_", " ");
                String ulClass = field == "human ortholog" ? "ortholog" : "synonym";

                //geneInfo.add("<span class='label'>" + field + "</span>: " + StringUtils.join(info, ", "));
                if (info.size() > 1) {
                    String fieldDisplay = "<ul class='" + ulClass + "'><li>" + StringUtils.join(info, "</li><li>") + "</li></ul>";
                    geneInfo.add("<span class='label'>" + field + "</span>: " + fieldDisplay);
                } else {
                    geneInfo.add("<span class='label'>" + field + "</span>: " + StringUtils.join(info, ", "));
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        //return "<div class='geneCol'>" + markerSymbolLink + StringUtils.join(geneInfo, "<br>") + "</div>";
        return "<div class='geneCol'><div class='title'>"
                + markerSymbolLink
                + "</div>"
                + "<div class='subinfo'>"
                + StringUtils.join(geneInfo, "<br>")
                + "</div>";

    }

	// allele reference stuff
    @RequestMapping(value = "/dataTableAlleleRefCount", method = RequestMethod.GET)
    public @ResponseBody
    int updateReviewed(
            @RequestParam(value = "filterStr", required = true) String sSearch,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SQLException {

        return fetchAlleleRefCount(sSearch);
    }

    public int fetchAlleleRefCount(String sSearch) throws SQLException {

        Connection conn = admintoolsDataSource.getConnection();

        String like = "%" + sSearch + "%";
        String query = null;

        if (sSearch != "") {
            query = "select count(*) as count from allele_ref where "
                    + " acc like ?"
                    + " or symbol like ?"
                    + " or pmid like ?"
                    + " or date_of_publication like ?"
                    + " or grant_id like ?"
                    + " or agency like ?"
                    + " or acronym like ?";
        } else {
            query = "select count(*) as count from allele_ref";
        }
        int rowCount = 0;
        try (PreparedStatement p1 = conn.prepareStatement(query)) {
            if (sSearch != "") {
                for (int i = 1; i < 8; i ++) {
                    p1.setString(i, like);
                }
            }
            ResultSet resultSet = p1.executeQuery();

            while (resultSet.next()) {
                rowCount = Integer.parseInt(resultSet.getString("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowCount;
    }

    @RequestMapping(value = "/dataTableAlleleRef", method = RequestMethod.POST)
    public @ResponseBody
    String updateReviewed(
            @RequestParam(value = "value", required = true) String value,
            @RequestParam(value = "id", required = true) int dbid,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SQLException {

        // store new value to database
        return setAlleleSymbol(dbid, value);
    }

    public String setAlleleSymbol(int dbid, String alleleSymbol) throws SQLException {

    	Connection connKomp2 = komp2DataSource.getConnection();
        Connection conn = admintoolsDataSource.getConnection();
        
        JSONObject j = new JSONObject();

        // when symbol is set to be empty, change reviewed status, too
        if ( alleleSymbol.equals("") ){
        	
        	// update acc, gacc and reviewed field
        	String uptSql = "UPDATE allele_ref SET symbol='', reviewed='no', acc='', gacc='' WHERE dbid=?";
            PreparedStatement stmt = conn.prepareStatement(uptSql);
            stmt.setInt(1, dbid);
            stmt.executeUpdate();

            j.put("reviewed", "no");
            j.put("symbol", "");
    		return j.toString();
    	}

        String sqla = "SELECT acc, gf_acc FROM allele WHERE symbol=?";

        // if there are multiple allele symbols, it should have been separated by comma
        // IN PROGRESS
        if (alleleSymbol.contains(",")) {
            int symCount = alleleSymbol.split(",").length;
            List<String> placeHolders = new ArrayList<>();
            
            for (int c = 0; c < symCount; c ++) {
                placeHolders.add("?");
            }
            String placeHolderStr = StringUtils.join(placeHolders, ",");
            /*List<String> syms = new ArrayList<>();
             for ( int s=0; s<symbols.length; s++ ){
             syms.add("\"" + symbols[s] + "\"");
             }
             alleleSymbol = StringUtils.join(syms, ",");*/
            sqla = "SELECT acc, gf_acc FROM allele WHERE symbol in (" + placeHolderStr + ")";
            //System.out.println("multiples: " + sqla);
        }

		// fetch allele id, gene id of this allele symbol
        // and update acc and gacc fields of allele_ref table
        //System.out.println("set allele: " + sqla);

        String alleleAcc = "";
        String geneAcc = "";

        try (PreparedStatement p = connKomp2.prepareStatement(sqla)) {
            p.setString(1, alleleSymbol);
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                alleleAcc = resultSet.getString("acc");
                geneAcc = resultSet.getString("gf_acc");
                //System.out.println(alleleSymbol + ": " + alleleAcc + " --- " + geneAcc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 

        try {
	        String uptSql = "UPDATE allele_ref SET symbol=?, acc=?, reviewed=?, gacc=? WHERE dbid=?";
	        PreparedStatement stmt = conn.prepareStatement(uptSql);
	        stmt.setString(1, alleleAcc.equals("") ? "" : alleleSymbol);
	        stmt.setString(2, alleleAcc);
	        stmt.setString(3, alleleAcc.equals("") ? "no" : "yes");
	        stmt.setString(4, geneAcc);
	        stmt.setInt(5, dbid);
	        stmt.executeUpdate();
        
        	if ( alleleAcc.equals("") ){
        		// update acc, gacc and reviewed field
        		
        		j.put("reviewed", "no");
                j.put("symbol", "");
                j.put("alleleIdNotFound", "yes");
        	}
        	else {
	            j.put("reviewed", "yes");
	            j.put("symbol", alleleSymbol);
        	}
            //System.out.println("sql: " + sql);
            //System.out.println("setting acc and gacc -> " + alleleAcc + " --- " + geneAcc);
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            j.put("reviewed", "no");
            j.put("symbol", "ERROR: setting symbol failed");

        } finally {
            conn.close();
            connKomp2.close();
        }

        return j.toString();
    }

    // allele reference stuff
    @RequestMapping(value = "/dataTableAlleleRefEdit", method = RequestMethod.GET)
    public ResponseEntity<String> dataTableAlleleRefEditJson(
            @RequestParam(value = "iDisplayStart", required = false) Integer iDisplayStart,
            @RequestParam(value = "iDisplayLength", required = false) Integer iDisplayLength,
            @RequestParam(value = "sSearch", required = false) String sSearch,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SQLException {

    	String content = fetch_allele_ref_edit(iDisplayLength, iDisplayStart, sSearch);
        return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);

    }

    @RequestMapping(value = "/dataTableAlleleRef", method = RequestMethod.GET)
    public ResponseEntity<String> dataTableAlleleRefJson(
            @RequestParam(value = "iDisplayStart", required = false, defaultValue = "0") int iDisplayStart,
            @RequestParam(value = "iDisplayLength", required = false, defaultValue = "-1") int iDisplayLength,
            @RequestParam(value = "sSearch", required = false) String sSearch,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SQLException {

        String content = fetch_allele_ref(iDisplayLength, iDisplayStart, sSearch);
        return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);

    }

    // allele reference stuff
    @RequestMapping(value = "/alleleRefLogin", method = RequestMethod.POST)
    public @ResponseBody
    boolean checkPassCode(
            @RequestParam(value = "passcode", required = true) String passcode,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException, SQLException {

        return checkPassCode(passcode);
    }

    public boolean checkPassCode(String passcode) throws SQLException {

        Connection conn = admintoolsDataSource.getConnection();

        // prevent sql injection
        String query = "select password = md5(?) as status from users where name='ebi'";
        boolean match = false;

        try (PreparedStatement p = conn.prepareStatement(query)) {
            p.setString(1, passcode);
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                match = resultSet.getBoolean("status");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }

        return match;
    }

    public String fetch_allele_ref_edit(int iDisplayLength, int iDisplayStart, String sSearch) throws SQLException {

        Connection conn = admintoolsDataSource.getConnection();

        //String likeClause = " like '%" + sSearch + "%'";
        String like = "%" + sSearch + "%";
        String query = null;

        if (sSearch != "") {
            query = "select count(*) as count from allele_ref where "
                    + " acc like ?"
                    + " or symbol like ?"
                    + " or pmid like ?"
                    + " or date_of_publication like ?"
                    + " or grant_id like ?"
                    + " or agency like ?"
                    + " or acronym like ?";
        } else {
            query = "select count(*) as count from allele_ref";
        }
        int rowCount = 0;
        try (PreparedStatement p1 = conn.prepareStatement(query)) {
            if (sSearch != "") {
                for (int i = 1; i < 8; i ++) {
                    p1.setString(i, like);
                }
            }
            ResultSet resultSet = p1.executeQuery();

            while (resultSet.next()) {
                rowCount = Integer.parseInt(resultSet.getString("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

		//System.out.println("Got " + rowCount + " rows");
        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        j.put("iTotalRecords", rowCount);
        j.put("iTotalDisplayRecords", rowCount);

        String query2 = null;

        if (sSearch != "") {
            query2 = "select * from allele_ref where"
                    + " acc like ?"
                    + " or symbol like ?"
                    + " or pmid like ?"
                    + " or date_of_publication like ?"
                    + " or grant_id like ?"
                    + " or agency like ?"
                    + " or acronym like ?"
                    + " order by reviewed desc"
                    + " limit ?, ?";
        } else {
            query2 = "select * from allele_ref order by reviewed desc limit ?,?";
        }

		//System.out.println("query: "+ query);
        //System.out.println("query2: "+ query2);
        String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";

        try (PreparedStatement p2 = conn.prepareStatement(query2)) {
            if (sSearch != "") {
                for (int i = 1; i < 10; i ++) {
                    p2.setString(i, like);
                    if (i == 8) {
                        p2.setInt(i, iDisplayStart);
                    } else if (i == 9) {
                        p2.setInt(i, iDisplayLength);
                    }
                }
            } else {
                p2.setInt(1, iDisplayStart);
                p2.setInt(2, iDisplayLength);
            }

            ResultSet resultSet = p2.executeQuery();

            while (resultSet.next()) {

                List<String> rowData = new ArrayList<String>();

                int dbid = resultSet.getInt("dbid");
                String gacc = resultSet.getString("gacc");

                rowData.add(resultSet.getString("reviewed"));

                //rowData.add(resultSet.getString("acc"));
                String alleleSymbol = Tools.superscriptify(resultSet.getString("symbol"));
                String alLink = "<a target='_blank' href='" + impcGeneBaseUrl + resultSet.getString("gacc") + "'>" + alleleSymbol + "</a>";
                rowData.add(alLink);

                //rowData.add(resultSet.getString("name"));
                String pmid = "<span id=" + dbid + ">" + resultSet.getString("pmid") + "</span>";
                rowData.add(pmid);
                
                
                
                
                rowData.add(resultSet.getString("date_of_publication"));
                
                
                rowData.add(resultSet.getString("grant_id"));
                rowData.add(resultSet.getString("agency"));
                rowData.add(resultSet.getString("acronym"));
                String[] urls = resultSet.getString("paper_url").split(",");
                List<String> links = new ArrayList<>();
                for (int i = 0; i < urls.length; i ++) {
                    links.add("<a target='_blank' href='" + urls[i] + "'>paper</a>");
                }
                rowData.add(StringUtils.join(links, "<br>"));

                j.getJSONArray("aaData").add(rowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return j.toString();
    }
    
    public String fetch_allele_ref(int iDisplayLength, int iDisplayStart, String sSearch) throws SQLException {
        final int DISPLAY_THRESHOLD = 4;
        List<ReferenceDTO> references = referenceDAO.getReferenceRows(sSearch);
        
        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);
        j.put("iTotalRecords", references.size());
        j.put("iTotalDisplayRecords", references.size());
        
        // MMM to digit conversion
        Map<String, String> m2d = new HashMap<>();
        // the digit part is set as such to work with the default non-natural sort behavior so that
        // 9 will not be sorted after 10
        m2d.put("Jan","11");
        m2d.put("Feb","12");
        m2d.put("Mar","13");
        m2d.put("Apr","14");
        m2d.put("May","15");
        m2d.put("Jun","16");
        m2d.put("Jul","17");
        m2d.put("Aug","18");
        m2d.put("Sep","19");
        m2d.put("Oct","20");
        m2d.put("Nov","21");
        m2d.put("Dec","22");
        
        for (ReferenceDTO reference : references) {

        	List<String> rowData = new ArrayList<>();
        	Map<String,String> alleleSymbolinks = new LinkedHashMap<String,String>();
            
            int alleleAccessionIdCount = reference.getAlleleAccessionIds().size();
            for (int i = 0; i < alleleAccessionIdCount; i++) {

                String symbol = Tools.superscriptify(reference.getAlleleSymbols().get(i));
                String alleleLink;
                String cssClass = "class='" +  (alleleSymbolinks.size() < DISPLAY_THRESHOLD ? "showMe" : "hideMe") + "'";
                
                if (i < reference.getImpcGeneLinks().size()) {
                		alleleLink = "<div " + cssClass + "><a target='_blank' href='" + reference.getImpcGeneLinks().get(i) + "'>" + symbol + "</a></div>";
                } else {
                    if (i > 0) {
                    	alleleLink = "<div " + cssClass + "><a target='_blank' href='" + reference.getImpcGeneLinks().get(0) + "'>" + symbol + "</a></div>";
                    } else {
                    	alleleLink = alleleLink = "<div " + cssClass + ">" + symbol + "</div>";
                    }
                }
                alleleSymbolinks.put(symbol, alleleLink);
            }
            
            if (alleleSymbolinks.size() > 5){
            	int num = alleleSymbolinks.size();
            	alleleSymbolinks.put("toggle", "<div class='alleleToggle' rel='" + num + "'>Show all " + num + " alleles ...</div>");
            }
            
            List<String> alLinks = new ArrayList<>();
            Iterator it = alleleSymbolinks.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                alLinks.add(pair.getValue().toString());
                it.remove(); // avoids a ConcurrentModificationException
            }
            
            rowData.add(StringUtils.join(alLinks, ""));

            rowData.add(reference.getTitle());
            rowData.add(reference.getJournal());
            
            String oriPubDate = reference.getDateOfPublication();
          
            String altStr = null;
            oriPubDate = oriPubDate.trim();
            if ( oriPubDate.matches("^\\d+$") ){
            	altStr = oriPubDate + "-23"; // so that YYYY will be sorted after YYYY MMM
            }
            else {
            	String[] parts = oriPubDate.split(" ");
            	altStr = parts[0] + "-" + m2d.get(parts[1]);
            }
            
            // alt is for alt-string sorting in dataTable for date_of_publication field
            // The format is either YYYY or YYYY Mmm (2012 Jul, eg)
            // I could not get sorting to work with this column using dataTable datetime-moment plugin (which supports self-defined format)
            // but I managed to get it to work with alt-string
            rowData.add("<span alt='" + altStr + "'>" + oriPubDate + "</span>");
            
            List<String> agencyList = new ArrayList();
            int agencyCount = reference.getGrantAgencies().size();
            
            for (int i = 0; i < agencyCount; i++) {
                String cssClass = "class='" +  (i < DISPLAY_THRESHOLD ? "showMe" : "hideMe") + "'";
                String grantAgency = reference.getGrantAgencies().get(i);
                if ( ! grantAgency.isEmpty()) {
                    agencyList.add("<li " + cssClass + ">" + grantAgency + "</li>");
                }
            }
            rowData.add("<ul>" + StringUtils.join(agencyList, "") + "</ul>");
            
            int pmid = Integer.parseInt(reference.getPmid());
            List<String> paperLinks = new ArrayList<>();
            List<String> paperLinksOther = new ArrayList<>();
            List<String> paperLinksPubmed = new ArrayList<>();
            List<String> paperLinksEuroPubmed = new ArrayList<>();
            String[] urlList = reference.getPaperUrls().toArray(new String[0]);
            
            for (int i = 0; i < urlList.length; i ++) {
                String[] urls = urlList[i].split(",");

                int pubmedSeen = 0;
                int eupubmedSeen = 0;
                int otherSeen = 0;

                for (int k = 0; k < urls.length; k ++) {
                    String url = urls[k];

                    if (pubmedSeen != 1) {
                        if (url.startsWith("http://www.pubmedcentral.nih.gov") && url.endsWith("pdf")) {
                            paperLinksPubmed.add("<li><a target='_blank' href='" + url + "'>Pubmed Central</a></li>");
                            pubmedSeen ++;
                        } else if (url.startsWith("http://www.pubmedcentral.nih.gov") && url.endsWith(Integer.toString(pmid))) {
                            paperLinksPubmed.add("<li><a target='_blank' href='" + url + "'>Pubmed Central</a></li>");
                            pubmedSeen ++;
                        }
                    }
                    if (eupubmedSeen != 1) {
                        if (url.startsWith("http://europepmc.org/") && url.endsWith("pdf=render")) {
                            paperLinksEuroPubmed.add("<li><a target='_blank' href='" + url + "'>Europe Pubmed Central</a></li>");
                            eupubmedSeen ++;
                        } else if (url.startsWith("http://europepmc.org/")) {
                            paperLinksEuroPubmed.add("<li><a target='_blank' href='" + url + "'>Europe Pubmed Central</a></li>");
                            eupubmedSeen ++;
                        }
                    }
                    if (otherSeen != 1 &&  ! url.startsWith("http://www.pubmedcentral.nih.gov") &&  ! url.startsWith("http://europepmc.org/")) {
                        paperLinksOther.add("<li><a target='_blank' href='" + url + "'>Non-pubmed source</a></li>");
                        otherSeen ++;
                    }
                }
            }

            // ordered
            paperLinks.addAll(paperLinksEuroPubmed);
            paperLinks.addAll(paperLinksPubmed);
            paperLinks.addAll(paperLinksOther);
            rowData.add(StringUtils.join(paperLinks, ""));
            
            j.getJSONArray("aaData").add(rowData);
        }

        //System.out.println("Got " + rowCount + " rows");
        return j.toString();
    }
    
    public class MpAnnotations {
    	public String mp_id;
    	public String mp_term;
    	public String mp_definition;
    	public String top_level_mp_id;
    	public String top_level_mp_term;
    	public String mgi_accession_id;
    	public String marker_symbol;
    	public String human_gene_symbol;
    	public String disease_id;
    	public String disease_term;
		
    }
}
