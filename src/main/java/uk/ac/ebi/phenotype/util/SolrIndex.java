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
package uk.ac.ebi.phenotype.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import uk.ac.ebi.phenotype.web.util.DrupalHttpProxy;

public class SolrIndex  {

	
	private Logger LOG = Logger.getLogger(this.getClass().getCanonicalName());

	private DrupalHttpProxy drupalProxy;
	private String drupalBaseUrl = "";
	private String loggedIn = null;
	private ArrayList<String> interestingGenes = null;

	private String baseUrl = "";
	private String geneSolrBaseUrl = "";
	private String qryStr;
	private String solrCoreName;
	private String gridSolrParams;
	private String mode;
	private int iDisplayStart;
	private int iDisplayLength;
	private String contextPath;
	private JSONObject json;
	private boolean showImgView;
	
	public SolrIndex(String qryStr, String solrCoreName, String mode, Map<String, String> config) {

		this.drupalBaseUrl = config.get("drupalBaseUrl");
		this.baseUrl = config.get("solrUrl");
		this.geneSolrBaseUrl = config.get("geneSolrUrl");

		this.qryStr = qryStr;
		this.solrCoreName = solrCoreName;
		this.mode = mode;

		try {
			this.json = processQueryforJson(composeSolrUrl());
		} catch (MalformedURLException me) {
			// me.printStackTrace();
		}
	}
	
	// handling jQuery dataTable on the server-side
	public SolrIndex(String qryStr, String solrCoreName, String gridSolrParams,
			String mode, int iDisplayStart, int iDisplayLength,
			boolean showImgView, HttpServletRequest request, Map<String, String> config) {

		this.drupalBaseUrl = config.get("drupalBaseUrl");
		this.baseUrl = config.get("solrUrl");
		this.geneSolrBaseUrl = config.get("geneSolrUrl");

		this.qryStr = qryStr;
		this.solrCoreName = solrCoreName;
		this.gridSolrParams = gridSolrParams;
		this.mode = mode;
		this.iDisplayStart = iDisplayStart;
		this.iDisplayLength = iDisplayLength;
		this.showImgView = showImgView;

		this.drupalProxy = new DrupalHttpProxy(request);

		try {
			this.json = processQueryforJson(composeSolrUrl());
		} catch (MalformedURLException me) {
			// me.printStackTrace();
		}
	}

	private String composeSolrUrl() {
		String url = this.baseUrl + "/" + this.solrCoreName + "/select?";
		LOG.debug("GRID PARAMS:" + gridSolrParams);
		if (mode.equals("mpPage")) {
			url += "q=" + this.qryStr;
			url += "&start=0&rows=0&wt=json&qf=auto_suggest&defType=edismax";
		} else if (mode.equals("geneGrid")) {
			url = geneSolrBaseUrl + "/" + this.solrCoreName + "/search?";
			url += gridSolrParams				
				+ "&start=" + iDisplayStart
				+ "&rows=" + iDisplayLength
				+ "&hl=on&hl.field=marker_synonym,marker_name";
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
		// OTHER solrCoreNames to be added here

		return url;
	}
	
	private JSONObject processQueryforJson(String urlString) throws MalformedURLException {
		URLConnection urlConn = null;
		InputStreamReader inStream = null;
		JSONObject j = null;

		LOG.debug("SOLR qryStr: " + this.qryStr);
		try {
			URL url = new URL(urlString.replaceAll(" ", "%20"));
			urlConn = url.openConnection();
			inStream = new InputStreamReader(urlConn.getInputStream());
			j = (JSONObject) JSONSerializer.toJSON(IOUtils.toString(inStream));
			LOG.debug("CHECK json string: " + j.toString());
		} catch (MalformedURLException e) {
			LOG.info("Please check the URL:" + e.toString());
		} catch (IOException e1) {
			LOG.info("Can't read from the Internet: " + e1.toString());
		}
		return j;
	}

	public String fetchDataTableJson(String contextPath) {
		this.contextPath = contextPath;

		String jsonStr = null;
		if (mode.equals("geneGrid")) {
			jsonStr = parseJsonforGeneDataTable();
		} else if (mode.equals("pipelineGrid")) {
			jsonStr = parseJsonforProtocolDataTable();
		} else if (mode.equals("imagesGrid")) {
			jsonStr = parseJsonforImageDataTable();
		} else if (mode.equals("mpGrid")) {
			jsonStr = parseJsonforMpDataTable();
		}

		return jsonStr;
	}

	public String parseJsonforGeneDataTable(){
			
		qryStr = this.json.getJSONObject("responseHeader").getJSONObject("params").getString("q");
		JSONArray docs = this.json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = this.json.getJSONObject("response").getInt("numFound");
		
		LOG.debug("TOTAL GENEs: " + totalDocs);
		
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

			String geneStatus = deriveGeneStatus(doc);
			rowData.add(geneStatus);

			// register of interest
			if (loggedIn()) {
				if (alreadyInterested(doc.getString("mgi_accession_id"))) {
					rowData.add("<a id='"+doc.getString("mgi_accession_id")+"' href='' class='btn primary interest'>Unregister interest</a>");
				} else {
					rowData.add("<a id='"+doc.getString("mgi_accession_id")+"' href='' class='btn primary interest'>Register interest</a>");
				}
			} else {
				rowData.add("<a href='/user/register' class='btn primary'>Login to register interest</a>");
			}

			j.getJSONArray("aaData").add(rowData);
		}
		
		return j.toString();	
	}
	
	/**
	 * is this user logged in
	 * 
	 * This checks an external URL on the drupal instance to see if the
	 * user is logged in.  It abuses the toggleflagfromjs exposed URL
	 * and relies on the different error messages being returned when the
	 * user is not logged in.
	 * 
	 * TODO: not optimal. should use a single sign on (not drupal)
	 * 
	 * @return true if the user is logged in, false if not
	 */
	private boolean loggedIn() {
		if (this.loggedIn == null) {
			try {
				URL url = new URL(drupalBaseUrl + "/roles");
				String content = drupalProxy.getContent(url);
				if (content.contains("authenticated user")) {
					this.loggedIn = "true";
				} else {
					this.loggedIn = "false";
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.loggedIn = "false";
			}
		}

		return this.loggedIn == "true" ? true : false;
	}

	/**
	 * is this user interested in the passed in gene
	 * 
	 * the user can have a set of genes that they are interested in.  this 
	 * method checks an external URL to get the JSON list of MGI IDs that the
	 * user has already expressed interest in and returns a true/false for
	 * the gene indicated in the geneid parameter
	 *  
	 * @param geneid the gene id to check
	 * 
	 * @return true if the user has expressed interest in the gene, false
	 * if not
	 */
	private boolean alreadyInterested(String geneid) {
		if (this.interestingGenes == null) {

			this.interestingGenes = new ArrayList<String>();

			try {
				URL url = new URL(drupalBaseUrl + "/genesofinterest");
				String content = drupalProxy.getContent(url);
				JSONObject j = (JSONObject) JSONSerializer.toJSON(content);
				if(j.has("MGIGeneid")) {
					JSONArray ar = j.getJSONArray("MGIGeneid");
					for(int i = 0; i<ar.size();i++) {
						interestingGenes.add(ar.getString(i));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return interestingGenes.contains(geneid);
	}


	private String concateGeneInfo(JSONObject doc){
				
		List<String> geneInfo = new ArrayList<String>();	
		
		String markerSymbol = "<span class='gSymbol'>" + doc.getString("marker_symbol") + "</span>";		
		String mgiId = doc.getString("mgi_accession_id");
		String geneUrl = contextPath + "/geneDetails?gene_id=" + mgiId;		
		String markerSymbolLink = "<a href='" + geneUrl + "' target='_blank'>" + markerSymbol + "</a>";			
				
		String[] fields = {"marker_synonym","marker_name"};			
		for( int i=0; i<fields.length; i++){		
			try {				
				//"highlighting":{"MGI:97489":{"marker_symbol":["<em>Pax</em>5"],"synonym":["<em>Pax</em>-5"]},
				
				String field = fields[i];
				List<String> info = new ArrayList<String>();
				if ( doc.getJSONArray(field).size() > 0) {
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
	
	private String deriveGeneStatus(JSONObject doc) {
				
		// order of status: latest to oldest (IMPORTANT for deriving correct status)
		// returns the latest status (6 statuses available)		
		
		//Phenotype Data Available
		try {
			if ( doc.getJSONArray("imits_report_phenotyping_complete_date").size() > 0) {	           
				return "Phenotype Data Available";
			}
		} 
		catch (Exception e) {		   		
			//e.printStackTrace();
		}
		
		//Mice Produced
		try {
			if ( doc.getJSONArray("imits_report_genotype_confirmed_date").size() > 0) {	           
				return "Mice Produced";
			}
		} 
		catch (Exception e) {		  			
		    //e.printStackTrace();
		}
		
		//Assigned for Mouse Production and Phenotyping
		try {			
			boolean nonAssignedStatuses = false;
			boolean assignedStatuses = false;
					
			if ( doc.getJSONArray("imits_report_mi_plan_status").size() > 0 ){
				JSONArray plans = doc.getJSONArray("imits_report_mi_plan_status");
				for(Object p : plans){
					if ( p.toString().equals("Inactive") || p.toString().equals("Withdrawn") ){
						nonAssignedStatuses = true;
					}
					else {
						assignedStatuses = true;
					}
				}
				if ( !nonAssignedStatuses && assignedStatuses ) {
					return "Assigned for Mouse Production and Phenotyping";
		        }				
			}
		} 
		catch (Exception e) {		 			
		    //e.printStackTrace();
		}
				
		//ES Cells Produced
		try {
			if ( doc.getJSONArray("escell").size() > 0 ){
				return "ES Cells Produced";
			}
		} 
		catch (Exception e) {		   			
		    //e.printStackTrace();
		}
		
		//Assigned for ES Cell Production
		try {
			if ( doc.getJSONArray("ikmc_project").size() > 0 ){
				return "Assigned for ES Cell Production";
			}
		} 
		catch (Exception e) {		   			
		    //e.printStackTrace();
		}		
		
		// gets the oldest/initial status if none of the above applies
		return "Not Assigned for ES Cell Production";
	}
	
	public String parseJsonforProtocolDataTable(){
		
		JSONArray docs = this.json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = this.json.getJSONObject("response").getInt("numFound");
		int numDocs = docs.size();				
		
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
			String procedureLink = "<a target='_blank' href='" + impressBaseUrl + procedure_stable_key + "'>" + procedure + "</a>";			
			rowData.add(procedureLink);				
			
			String pipeline = doc.getString("pipeline_name");
			rowData.add(pipeline);
			
			j.getJSONArray("aaData").add(rowData);
		} 
		
		return j.toString();	
	}
	
	public String parseJsonforMpDataTable(){
		
		String baseUrl = contextPath + "/mp?mpid=";
		
		JSONArray docs = this.json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = this.json.getJSONObject("response").getInt("numFound");
		int numDocs = docs.size();
				
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
			String mpLink = "<a target='_blank' href='" + baseUrl + mpId + "'>" + mpTerm + "</a>";			
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

	public String parseJsonforImageDataTable(){
		
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
			
			final String imgBaseUrl = contextPath + "/media/images/"; 
			
			for (int i=start; i<end; i++){
				
				List<String> rowData = new ArrayList<String>();
				JSONObject doc = docs.getJSONObject(i);				
				//System.out.println("TEST: " + doc.toString());
				try {
					ArrayList<String> mp = new ArrayList<String>();
					ArrayList<String> ma = new ArrayList<String>();
					ArrayList<String> exp = new ArrayList<String>();					
					
					JSONArray termIds = doc.getJSONArray("annotationTermId");
					JSONArray termNames = doc.getJSONArray("annotationTermName");								
					JSONArray expNames = doc.getJSONArray("expName");						
					
					int counter = 0;
					for( Object s : termIds ){				
						if ( s.toString().contains("MA")){
							LOG.debug(i + " - MA: " + termNames.get(counter).toString());
							String acc = termIds.get(counter).toString();
							String name = termNames.get(counter).toString();
							//ma.add("<a href='/maid' target='_blank'>" + name + "</a>");
							ma.add(name);
						}
						else if ( s.toString().contains("MP") ){
							LOG.debug(i+ " - MP: " + termNames.get(counter).toString());
							LOG.debug(i+ " - MP: " + termIds.get(counter).toString());
							String mpid = termIds.get(counter).toString();							
							String name = termNames.get(counter).toString();							
							String url = contextPath + "/mp?mpid=" + mpid;
							mp.add("<a href='" + url + "' target='_blank'>" + name + "</a>");
						}
						counter++;
					}
														
					for( Object s : expNames ){
						LOG.debug(i + " - expTERM: " + s.toString());
						exp.add(s.toString());
					}
					
					String annots = "";
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
					
					String largeThumbNailPath = imgBaseUrl + doc.getString("largeThumbnailFilePath");
					String img = "<img src='" +  imgBaseUrl + doc.getString("smallThumbnailFilePath") + "'/>";
					String imgLink = "<a target='_blank' href='" + largeThumbNailPath +"'>" + img + "</a>";
					rowData.add(imgLink);
					
					j.getJSONArray("aaData").add(rowData);
				}
				catch (Exception e){
					//e.printStackTrace();
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
				String annotName = names[0];
				HashMap hm = renderFacetField(names); //MA:xxx, MP:xxx, MGI:xxx, exp				
				String displayAnnotName = "<span class='annotType'>" + hm.get("label").toString() + "</span>: " + hm.get("link").toString();
				String facetField = hm.get("field").toString();
						
				String imgCount = sumFacets.get(i+1).toString();	
				String unit = Integer.parseInt(imgCount) > 1 ? "images" : "image";				
				String imgSubSetLink = "<a target='_blank' href='" + baseUrl+ "&fq=" + facetField + ":\"" + names[0] + "\"" + "'>" + imgCount + " " + unit+ "</a>";
				System.out.println(imgSubSetLink);		
				
				rowData.add(displayAnnotName + " (" + imgSubSetLink + ")"); 
				rowData.add(fetchImagePathByAnnotName(facetField, names[0], contextPath));
				
				j.getJSONArray("aaData").add(rowData);
			}	
			
			return j.toString();	
		}
	}
	
	private HashMap renderFacetField(String[] names){				
				
		HashMap hm = new HashMap(); // key: display label, value: facetField
		String name = names[0];
		String id = names[1];		
		
		String facetField = null;
		if ( id.startsWith("MP:")){	
			String url = contextPath + "/mp?mpid=" + id;
			hm.put("label", "MP");
			hm.put("field", "annotationTermName");
			hm.put("link", "<a href='" + url + "' target='_blank'>"+ name + "</a>");
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
			String url = contextPath + "/geneDetails?gene_id=" + id;
			hm.put("label", "Gene");
			hm.put("field", "symbol");
			hm.put("link", "<a href='" + url + "' target='_blank'>"+ name + "</a>");
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
			JSONArray geneSymbols = doc.getJSONArray("symbol_gene");
			for (Object s : geneSymbols) {
				String[] names = s.toString().split("_");
				System.out.println("symbol: " + names[0]);
				String url = contextPath + "/geneDetails?gene_id=" + names[1];
				gene.add("<a target='_blank' href='" + url +"'>" + names[0] + "</a>");				
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return gene;
	}
	
	private String fetchImagePathByAnnotName(String facetField, String annotName, String contextPath){
	
		//annotName = annotName.replace(" ","%20");
		final int maxNum = 4; // max num of images to display in grid column 
		String thumbnailUrl = this.baseUrl+ "/" + this.solrCoreName + "/select?";
		//System.out.println("TEST: " + annotName);
		
		String params = gridSolrParams + "&fq=" +  facetField + ":\"" + annotName + "\"&rows=" + maxNum;
		
		//System.out.println("TEST2: " + params);

		thumbnailUrl += params;

		final String imgBaseUrl = contextPath + "/media/images/";

		List<String> imgPath = new ArrayList<String>();

		try {
			JSONObject thumbnailJson = processQueryforJson(thumbnailUrl);
			JSONArray docs = thumbnailJson.getJSONObject("response").getJSONArray("docs");

			int dataLen = docs.size() < 5 ? docs.size() : maxNum;

			for (int i = 0; i < dataLen; i++) {
				JSONObject doc = docs.getJSONObject(i);
				String largeThumbNailPath = imgBaseUrl + doc.getString("largeThumbnailFilePath");
				String img = "<img src='" + imgBaseUrl + doc.getString("smallThumbnailFilePath") + "'/>";
				String link = "<a target='_blank' href='" + largeThumbNailPath + "'>" + img + "</a>";
				imgPath.add(link);
			}
		} catch (MalformedURLException me) {
			// me.printStackTrace();
		}

		return StringUtils.join(imgPath, "");
	}

	public int fetchNumFound() {
		JSONObject response = this.json.getJSONObject("response");
		return response.getInt("numFound");
	}

	
}	
