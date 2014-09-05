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
package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;
import org.noggit.JSONUtil;

import uk.ac.ebi.phenotype.data.imits.StatusConstants;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;

@Service
public class GeneService {

	private HttpSolrServer solr;

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	public static final class GeneField {
		public final static String LATEST_MOUSE_STATUS = "latest_mouse_status";
		public final static String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
		public final static String LATEST_PHENOTYPING_CENTRE = "latest_phenotyping_centre";
		public final static String LATEST_PRODUCTION_CENTRE = "latest_production_centre";
		public final static String MGI_ACCESSION_ID = "mgi_accession_id";
		public final static String PHENOTYPE_STATUS = "phenotype_status";
		public final static String TOP_LEVEL_MP_ID="top_level_mp_id";
	}

	public static final class GeneFieldValue {
		public final static String CENTRE_WTSI = "WTSI";
		public final static String PHENOTYPE_STATUS_COMPLETE = "Phenotyping Complete";
		public final static String PHENOTYPE_STATUS_STARTED = "Phenotyping Started";
	}

	public GeneService(String solrUrl) {
		solr = new HttpSolrServer(solrUrl);

	}

	/**
	 * Return all genes in the gene core matching latestPhenotypeStatus and
	 * latestProductionCentre.
	 * 
	 * @param latestPhenotypeStatus
	 *            latest phenotype status (i.e. most advanced along the pipeline)
	 * @param latestProductionCentre
	 *            latest production centre (i.e. most advanced along the pipeline)
	 * @return all genes in the gene core matching phenotypeStatus and
	 *         productionCentre.
	 * @throws SolrServerException
	 */
	public Set<String> getGenesByLatestPhenotypeStatusAndProductionCentre(
			String latestPhenotypeStatus,
                        String latestProductionCentre)
			throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		String queryString = "(" + GeneField.LATEST_PHENOTYPE_STATUS + ":\""
				+ latestPhenotypeStatus + "\") AND ("
				+ GeneField.LATEST_PRODUCTION_CENTRE + ":\""
				+ latestProductionCentre + "\")";
		solrQuery.setQuery(queryString);
		solrQuery.setRows(1000000);
		solrQuery.setFields(GeneField.MGI_ACCESSION_ID);
		QueryResponse rsp = null;
		rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allGenes = new HashSet<String>();
		for (SolrDocument doc : res) {
			allGenes.add((String) doc.getFieldValue(GeneField.MGI_ACCESSION_ID));
		}

		log.debug("getGenesByLatestPhenotypeStatusAndProductionCentre: solrQuery = "
				+ queryString);
		return allGenes;
	}

	/**
	 * Return all genes in the gene core matching latestPhenotypeStatus and
	 * latestPhenotypeCentre.
	 * 
	 * @param latestPhenotypeStatus
	 *            latest phenotype status (i.e. most advanced along the pipeline)
	 * @param latestPhenotypeCentre
	 *            latest phenotype centre (i.e. most advanced along the pipeline)
	 * @return all genes in the gene core matching phenotypeStatus and
	 *         productionCentre.
	 * @throws SolrServerException
	 */
	public Set<String> getGenesByLatestPhenotypeStatusAndPhenotypeCentre(
			String latestPhenotypeStatus,
                        String latestPhenotypeCentre)
			throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		String queryString = "(" + GeneField.LATEST_PHENOTYPE_STATUS + ":\""
				+ latestPhenotypeStatus + "\") AND ("
				+ GeneField.LATEST_PHENOTYPING_CENTRE + ":\""
				+ latestPhenotypeCentre + "\")";
		solrQuery.setQuery(queryString);
		solrQuery.setRows(1000000);
		solrQuery.setFields(GeneField.MGI_ACCESSION_ID);
		QueryResponse rsp = null;
		rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allGenes = new HashSet<String>();
		for (SolrDocument doc : res) {
			allGenes.add((String) doc.getFieldValue(GeneField.MGI_ACCESSION_ID));
		}

		log.debug("getGenesByLatestPhenotypeStatusAndPhenotypeCentre: solrQuery = "
				+ queryString);
		return allGenes;
	}

	/**
	 * Return all genes from the gene core.
	 * 
	 * @return all genes from the gene core.
	 * @throws SolrServerException
	 */
	public Set<String> getAllGenes() throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(GeneField.MGI_ACCESSION_ID + ":*");
		solrQuery.setRows(1000000);
		solrQuery.setFields(GeneField.MGI_ACCESSION_ID);
		QueryResponse rsp = null;
		rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allGenes = new HashSet<String>();
		for (SolrDocument doc : res) {
			allGenes.add((String) doc.getFieldValue(GeneField.MGI_ACCESSION_ID));
		}
		return allGenes;
	}

	/**
	 * Return all genes from the gene core whose MGI_ACCESSION_ID does not start
	 * with 'MGI'.
	 * 
	 * @return all genes from the gene core whose MGI_ACCESSION_ID does not
	 *         start with 'MGI'.
	 * @throws SolrServerException
	 */
	public Set<String> getAllNonConformingGenes() throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("-" + GeneField.MGI_ACCESSION_ID + ":MGI*");
		solrQuery.setRows(1000000);
		solrQuery.setFields(GeneField.MGI_ACCESSION_ID);
		QueryResponse rsp = null;
		rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allGenes = new HashSet<String>();
		for (SolrDocument doc : res) {
			allGenes.add((String) doc.getFieldValue(GeneField.MGI_ACCESSION_ID));
		}
		return allGenes;
	}

	// returns ready formatted icons
	public Map<String, String> getProductionStatus(String geneId, HttpServletRequest request)
			throws SolrServerException {

		SolrQuery query = new SolrQuery();
		query.setQuery("mgi_accession_id:\"" + geneId + "\"");
		QueryResponse response = solr.query(query);
		SolrDocument doc = response.getResults().get(0);

		return getStatusFromDoc(doc, request);

	}

	/**
	 * Get the latest phenotyping status for a document.
	 * 
	 * @param doc
	 *            represents a gene with imits status fields
	 * @return the latest status (Complete or Started or Phenotype Attempt
	 *         Registered) as appropriate for this gene
	 */
	public String getPhenotypingStatus(JSONObject doc, HttpServletRequest request, boolean toExport, boolean legacyOnly) {
		
		String mgiId = doc.getString("mgi_accession_id");
		String geneUrl = request.getAttribute("baseUrl") + "/genes/" + mgiId;

		final String statusField = GeneDTO.LATEST_PHENOTYPE_STATUS ;
		String phenotypeStatusHTMLRepresentation = "";
		String webStatus = "";
		
		try {	
		
			log.debug("getPhenotypingStatus :" + doc.getString(statusField));
			log.debug("hasQC :" + doc.containsKey(GeneDTO.HAS_QC));
			
			String hostName = request.getAttribute("mappedHostname").toString();
			
			/*
			 * 1. Check we have preQC/postQC IMPC data (started or completed) 		
			 */
			if ( legacyOnly ){
				webStatus = StatusConstants.WEB_MOUSE_PHENOTYPING_LEGACY_DATA_AVAILABLE;
				// <a class='status done' title='Scroll down for phenotype associations.'><span>phenotype data available</span></a>
				
				if ( toExport ){
					phenotypeStatusHTMLRepresentation = hostName + geneUrl+ "#section-associations" + "|" + webStatus;
				}
				else {
					phenotypeStatusHTMLRepresentation = "<a class='status qc' href='" + geneUrl + "#section-associations' title='Click for phenotype associations'><span>"+webStatus+"</span></a>";
				}	
			}
			else if ( doc.containsKey(statusField) && !doc.getString(statusField).isEmpty() ) {
				String val = doc.getString(statusField);
				
				if ( val.equals(StatusConstants.IMITS_MOUSE_PHENOTYPING_STARTED) || 
					 val.equals(StatusConstants.IMITS_MOUSE_PHENOTYPING_COMPLETE) ) {
					webStatus = StatusConstants.WEB_MOUSE_PHENOTYPING_DATA_AVAILABLE;
					
					if ( toExport ){
						phenotypeStatusHTMLRepresentation = hostName + geneUrl+ "#section-associations" + "|" + webStatus;
					}
					else {
						phenotypeStatusHTMLRepresentation = "<a class='status done' href='" + geneUrl + "#section-associations'><span>"+webStatus+"</span></a>";
					}
				}
			}
	
			/*
			 * 2. If there is no preQC/postQC IMPC data we may still have legacy
			 *    data in the back-end.
			 *    This has been indexed with the hasQC field from the experimental
			 *    core. 
			 */
			else if (doc.containsKey(GeneDTO.HAS_QC)) {	
			//else if (doc.containsKey(GeneDTO.LEGACY_PHENOTYPE_STATUS)) {
				webStatus = StatusConstants.WEB_MOUSE_PHENOTYPING_LEGACY_DATA_AVAILABLE;
				// <a class='status done' title='Scroll down for phenotype associations.'><span>phenotype data available</span></a>
				
				if ( toExport ){
					phenotypeStatusHTMLRepresentation = hostName + geneUrl+ "#section-associations" + "|" + webStatus;
				}
				else {
					phenotypeStatusHTMLRepresentation = "<a class='status qc' href='" + geneUrl + "#section-associations' title='Click for phenotype associations'><span>"+webStatus+"</span></a>";
				}			

				//System.out.println(phenotypeStatusHTMLRepresentation);
			}	
			
		}		
		catch (Exception e) {
			log.error("Error getting phenotyping status");
			log.error(e.getLocalizedMessage());
		}
			
		return phenotypeStatusHTMLRepresentation;
	}
	
	/**
	 * Get the latest production status of ES cells for a document.
	 * 
	 * @param doc
	 *            represents a gene with imits status fields
	 * @return the latest status at the gene level for ES cells as a string
	 */
	public String getEsCellStatus(JSONObject doc, HttpServletRequest request, boolean toExport){
		
		String mgiId = doc.getString("mgi_accession_id");
		String geneUrl = request.getAttribute("baseUrl") + "/genes/" + mgiId;
				
		String status = null;
		
		String esCellStatus = "";	
		String exportEsCellStatus = "";	
		try {	
			final String field = GeneDTO.LATEST_ES_CELL_STATUS; //"latest_es_cell_status"; 
			// ES cell production status
			
			if ( doc.containsKey(field)  ){
				// blue es cell status				
				status = doc.getString(field);
				if ( status.equals(StatusConstants.IMPC_ES_CELL_STATUS_PRODUCTION_DONE) ){
						esCellStatus = "<a class='status done' href='" + geneUrl + "#order" + "' title='"+StatusConstants.WEB_ES_CELL_STATUS_PRODUCTION_DONE+"'>"
									 + " <span>ES Cells</span>"
									 + "</a>";
						
						exportEsCellStatus += StatusConstants.WEB_ES_CELL_STATUS_PRODUCTION_DONE;
				}
				else if ( esCellStatus.equals(StatusConstants.IMPC_ES_CELL_STATUS_PRODUCTION_IN_PROGRESS) ){
						esCellStatus = "<span class='status inprogress' title='"+StatusConstants.WEB_ES_CELL_STATUS_PRODUCTION_IN_PROGRESS+"'>"
						   	 		 +  "	<span>ES Cells</span>"
						   	 		 +  "</span>";
						
						exportEsCellStatus += StatusConstants.WEB_ES_CELL_STATUS_PRODUCTION_IN_PROGRESS;
				}
				else {
					esCellStatus = "";
					exportEsCellStatus = StatusConstants.WEB_ES_CELL_STATUS_PRODUCTION_NONE;
				}
			}	
		}
		catch (Exception e) {
			log.error("Error getting ES cell/Mice status");
			log.error(e.getLocalizedMessage());
		}
			
		if ( toExport ){
			return exportEsCellStatus;
		}
		return esCellStatus; 
	}
	
	/**
	 * Get the simplified production status of ES cells/mice for a document.
	 * 
	 * @param doc
	 *            represents a gene with imits status fields
	 * @return the latest status at the gene level for both ES cells and alleles
	 */
	public String getLatestProductionStatusForEsCellAndMice(JSONObject doc, HttpServletRequest request, boolean toExport, String geneLink){		
		
		//ObjectMapper mapper = new ObjectMapper();
		
		//GeneDTO gene = mapper.readValue(doc.toString(), GeneDTO.class);
		
		String esCellStatus = getEsCellStatus(doc, request, toExport);
		
		String miceStatus = "";		
		final List<String> exportMiceStatus = new ArrayList<String>();
				
		Map<String, String> sh = new HashMap<String, String>();
				
		try {		
						
			// mice production status			
			// Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele docs) 
			if ( doc.containsKey("mouse_status") ){
				
				JSONArray alleleNames = doc.getJSONArray("allele_name");				
				JSONArray mouseStatus = doc.getJSONArray("mouse_status");
				
				for ( int i=0; i< mouseStatus.size(); i++ ) {		
					String mouseStatusStr = mouseStatus.get(i).toString();					
					sh.put(mouseStatusStr, "yes");
				}		
								
				// if no mice status found but there is already allele produced, mark it as "mice produced planned"				
				for ( int j=0; j< alleleNames.size(); j++ ) {
					String alleleName = alleleNames.get(j).toString();
					//System.out.println("allele: " + alleleName);
					if ( !alleleName.equals("") && !alleleName.equals("None") && !alleleName.contains("tm1e") && mouseStatus.get(j).toString().equals("") ){	
						sh.put("mice production planned", "yes");						
					}				
				}
				
				if ( sh.containsKey("Mice Produced") ){
					miceStatus = "<a class='status done' oldtitle='Mice Produced' title='' href='" + geneLink + "#order'>"
							   +  "<span>Mice</span>"
							   +  "</a>";
						
					exportMiceStatus.add("mice produced");
				}
				else if ( sh.containsKey("Assigned for Mouse Production and Phenotyping") ){
					miceStatus = "<a class='status inprogress' oldtitle='Mice production in progress' title=''>"
							   +  "<span>Mice</span>"
							   +  "</a>";
					exportMiceStatus.add("mice production in progress");
				}
				else if ( sh.containsKey("mice production planned") ){
					miceStatus = "<a class='status none' oldtitle='Mice production planned' title=''>"
							   +  "<span>Mice</span>"
							   +  "</a>";
					exportMiceStatus.add("mice production in progress");
				}				
			}
		} 
		catch (Exception e) {
			log.error("Error getting ES cell/Mice status");
			log.error(e.getLocalizedMessage());
		}
		
		if ( toExport ){
			exportMiceStatus.add(0, esCellStatus); // want to keep this at front
			return StringUtils.join(exportMiceStatus, ", ");
		}
		return esCellStatus + miceStatus;
		
	}
	
	/**
	 * Generates a map of buttons for ES Cell and Mice status
	 * @param doc a SOLR Document
	 * @return
	 */
	private Map<String, String> getStatusFromDoc(SolrDocument doc, HttpServletRequest request) {
		
		String miceStatus = "";
		String esCellStatusHTMLRepresentation = "";
		String phenotypingStatusHTMLRepresentation = "";
		Boolean order = false;
		JSONObject jsondoc = JSONObject.fromObject(org.noggit.JSONUtil.toJSON(doc));

		try {
					
			log.debug("getStatusFromDoc - " + jsondoc);
			
			/* ******** mice production status ******** */
			String patternStr = "(tm.*)\\(.+\\).+"; // allele name pattern
			Pattern pattern = Pattern.compile(patternStr);

			// Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele
			// docs)
			if (doc.containsKey("mouse_status")) {

				ArrayList<String> alleleNames = (ArrayList<String>) doc
						.getFieldValue("allele_name");
				ArrayList<String> mouseStatus = (ArrayList<String>) doc
						.getFieldValue("mouse_status");

				for (int i = 0; i < mouseStatus.size(); i++) {
					String mouseStatusStr = mouseStatus.get(i).toString();

					
					
					if (mouseStatusStr.equals(StatusConstants.IMPC_MOUSE_STATUS_PRODUCTION_DONE)) {
						String alleleName = alleleNames.get(i).toString();
						Matcher matcher = pattern.matcher(alleleName);
						if (matcher.find()) {
							String alleleType = matcher.group(1);
							miceStatus += "<span class='status done' title='"
									+ StatusConstants.WEB_MOUSE_STATUS_PRODUCTION_DONE + "' >"
									+ "	<span>Mice<br>" + alleleType
									+ "</span>" + "</span>";
						}
						
					} else if (mouseStatusStr
							.equals(StatusConstants.IMPC_MOUSE_STATUS_PRODUCTION_IN_PROGRESS)) {
						String alleleName = alleleNames.get(i).toString();
						Matcher matcher = pattern.matcher(alleleName);
						if (matcher.find()) {
							String alleleType = matcher.group(1);
							miceStatus += "<span class='status inprogress' title='"+StatusConstants.WEB_MOUSE_STATUS_PRODUCTION_IN_PROGRESS+"' >"
									+ "	<span>Mice<br>"
									+ alleleType
									+ "</span>" + "</span>";
						}
					}
				}

			}
			
			/*
			 * Get the HTML representation of the ES Cell status
			 */
			esCellStatusHTMLRepresentation = getEsCellStatus(jsondoc, request, false);
			
			/*
			 * Get the HTML representation of the phenotyping status
			 */
			phenotypingStatusHTMLRepresentation = getPhenotypingStatus(jsondoc, request, false, false);
			
			/*
			 * Order flag is separated from HTML generation code
			 */
			order = checkOrderProducts(doc);
			
		} catch (Exception e) {
			log.error("Error getting ES cell/Mice status");
			log.error(e.getLocalizedMessage());
		}
		
		HashMap<String, String> res = new HashMap<>();
		res.put("icons", esCellStatusHTMLRepresentation + miceStatus + phenotypingStatusHTMLRepresentation);
		res.put("orderPossible", order.toString());
		return res;
	}
	
	public boolean checkOrderProducts(SolrDocument doc) {
		
		return checkOrderMice(doc) || checkOrderESCells(doc);
	}
	
	public boolean checkOrderESCells(SolrDocument doc) {

		String status = null;
		boolean order = false;

		try {	
			final String field = GeneDTO.LATEST_ES_CELL_STATUS;
			if ( doc.containsKey(field) ) {		

				status = doc.getFirstValue(field).toString();

				if ( status.equals(StatusConstants.IMPC_ES_CELL_STATUS_PRODUCTION_DONE) ){
					order = true;
				}
			}
		}
		catch (Exception e) {
			log.error("Error getting ES cell/Mice status");
			log.error(e.getLocalizedMessage());
		}

		return order;
	}

	public boolean checkOrderMice(SolrDocument doc) {
		
		boolean order = false;
		if (doc.containsKey(GeneDTO.MOUSE_STATUS)) {

			ArrayList<String> alleleNames = (ArrayList<String>) doc
					.getFieldValue(GeneDTO.ALLELE_NAME);
			ArrayList<String> mouseStatus = (ArrayList<String>) doc
					.getFieldValue(GeneDTO.MOUSE_STATUS);

			for (int i = 0; i < mouseStatus.size(); i++) {
				String mouseStatusStr = mouseStatus.get(i).toString();
	
				if (mouseStatusStr.equals(StatusConstants.IMPC_MOUSE_STATUS_PRODUCTION_DONE)) {
					
					order = true;
					break;
				}
			}
		}
		
		return order;

	}
	
//	private Map<String, String> getStatusFromDoc(SolrDocument doc) {
//		String miceStatus = "";
//		String esCellStatus = "";
//		String phenStatus = "";
//		Boolean order = false;
//
//		try {
//
//			/* ******** phenotype status ******** */
//			phenStatus = derivePhenotypingStatus(doc).equals("") ? ""
//					: "<a class='status done' title='Scroll down for phenotype associations.'><span>phenotype data available</span></a>";
//
//			/* ******** mice production status ******** */
//			String patternStr = "(tm.*)\\(.+\\).+"; // allele name pattern
//			Pattern pattern = Pattern.compile(patternStr);
//
//			// Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele
//			// docs)
//			if (doc.containsKey("mouse_status")) {
//
//				ArrayList<String> alleleNames = (ArrayList<String>) doc
//						.getFieldValue("allele_name");
//				ArrayList<String> mouseStatus = (ArrayList<String>) doc
//						.getFieldValue("mouse_status");
//
//				for (int i = 0; i < mouseStatus.size(); i++) {
//					String mouseStatusStr = mouseStatus.get(i).toString();
//
//					if (mouseStatusStr.equals("Mice Produced")) {
//						String alleleName = alleleNames.get(i).toString();
//						Matcher matcher = pattern.matcher(alleleName);
//						if (matcher.find()) {
//							String alleleType = matcher.group(1);
//							miceStatus += "<span class='status done' title='"
//									+ mouseStatusStr + "' >"
//									+ "	<span>Mice<br>" + alleleType
//									+ "</span>" + "</span>";
//						}
//						order = true;
//					} else if (mouseStatusStr
//							.equals("Assigned for Mouse Production and Phenotyping")) {
//						String alleleName = alleleNames.get(i).toString();
//						Matcher matcher = pattern.matcher(alleleName);
//						if (matcher.find()) {
//							String alleleType = matcher.group(1);
//							miceStatus += "<span class='status inprogress' title='Mice production in progress' >"
//									+ "	<span>Mice<br>"
//									+ alleleType
//									+ "</span>" + "</span>";
//						}
//					}
//				}
//				// if no mice status found but there is already allele produced,
//				// mark it as "mice produced planned"
//				for (int j = 0; j < alleleNames.size(); j++) {
//					String alleleName = alleleNames.get(j).toString();
//					if (!alleleName.equals("") && !alleleName.equals("None")
//							&& mouseStatus.get(j).toString().equals("")) {
//						Matcher matcher = pattern.matcher(alleleName);
//						if (matcher.find()) {
//							String alleleType = matcher.group(1);
//							miceStatus += "<span class='status none' title='Mice production planned' >"
//									+ "	<span>Mice<br>"
//									+ alleleType
//									+ "</span>" + "</span>";
//						}
//					}
//				}
//
//			}
//
//			/* ******** ES cell production status ******** */
//			String field = "latest_es_cell_status";
//			if (doc.containsKey(field)) {
//				// blue es cell status
//				String text = doc.getFirstValue(field).toString();
//				if (text.equals("ES Cell Targeting Confirmed")) {
//					esCellStatus = "<a class='status done' href='' title='ES Cells produced' >"
//							+ " <span>ES cells</span>" + "</a>";
//					order = true;
//				} else if (text.equals("ES Cell Production in Progress")) {
//					esCellStatus = "<span class='status inprogress' title='ES cells production in progress' >"
//							+ "	<span>ES Cell</span>" + "</span>";
//				}
//
//			}
//
//		} catch (Exception e) {
//			log.error("Error getting ES cell/Mice status");
//			log.error(e.getLocalizedMessage());
//		}
//		HashMap<String, String> res = new HashMap<>();
//		res.put("icons", esCellStatus + miceStatus + phenStatus);
//		res.put("orderPossible", order.toString());
//		return res;
//	}

	public Boolean checkPhenotypeStarted(String geneAcc) {

		SolrQuery query = new SolrQuery();
		query.setQuery("mgi_accession_id:\"" + geneAcc + "\"");

		QueryResponse response;
		try {
			response = solr.query(query);
			if (response.getResults().size() > 0) {// check we have results
													// before we try and access
													// them
				SolrDocument doc = response.getResults().get(0);
				// phenotype_status
				if (doc.containsKey("phenotype_status")) {
					ArrayList<String> statuses = (ArrayList<String>) doc
							.getFieldValue("phenotype_status");
					for (String status : statuses) {
						if (status.equalsIgnoreCase("Phenotyping Started") || status.equalsIgnoreCase("Phenotyping Complete")) {
							return true;
						}
					}
				}
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Get the production status of ES cells/mice for a document.
	 * 
	 * @param doc
	 *            represents a gene with imits status fields
	 * @return the latest status at the gene level for ES cells and all statuses at the allele level for mice as a comma separated string
	 */
	public String getProductionStatusForEsCellAndMice(JSONObject doc, HttpServletRequest request, boolean toExport){		
		
		String esCellStatus = getEsCellStatus(doc, request, toExport);		
		String miceStatus = "";		
		final List<String> exportMiceStatus = new ArrayList<String>();
		
		String patternStr = "(tm.*)\\(.+\\).+"; // allele name pattern
		Pattern pattern = Pattern.compile(patternStr);
		
		try {		
						
			// mice production status
			
			// Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele docs) 
			if ( doc.containsKey("mouse_status") ){
				
				JSONArray alleleNames = doc.getJSONArray("allele_name");
				JSONArray mouseStatus = doc.getJSONArray("mouse_status");
				
				for ( int i=0; i< mouseStatus.size(); i++ ) {		
					String mouseStatusStr = mouseStatus.get(i).toString();	
					
					if ( mouseStatusStr.equals("Mice Produced") ){
						String alleleName = alleleNames.getString(i).toString();						
						Matcher matcher = pattern.matcher(alleleName);
						//System.out.println(matcher.toString());
							
						if (matcher.find()) {
							String alleleType = matcher.group(1);						
							miceStatus += "<span class='status done' oldtitle='" + mouseStatusStr + "' title=''>"
									+  "<span>Mice<br>" + alleleType + "</span>"
									+  "</span>";
							
							exportMiceStatus.add(alleleType + " mice produced");
						}
					}
					else if (mouseStatusStr.equals("Assigned for Mouse Production and Phenotyping") ){
						String alleleName = alleleNames.getString(i).toString();						
						Matcher matcher = pattern.matcher(alleleName);
						//System.out.println(matcher.toString());
							
						if (matcher.find()) {
							String alleleType = matcher.group(1);						
							miceStatus += "<span class='status inprogress' oldtitle='Mice production in progress' title=''>"
									+  "<span>Mice<br>" + alleleType + "</span>"
									+  "</span>";
							exportMiceStatus.add(alleleType + " mice production in progress");
						}						
					}					
				}	
				// if no mice status found but there is already allele produced, mark it as "mice produced planned"				
				for ( int j=0; j< alleleNames.size(); j++ ) {
					String alleleName = alleleNames.get(j).toString();
					if ( !alleleName.equals("") && !alleleName.equals("None") && mouseStatus.get(j).toString().equals("") ){	
						Matcher matcher = pattern.matcher(alleleName);
						//System.out.println(matcher.toString());
							
						if (matcher.find()) {
							String alleleType = matcher.group(1);						
							miceStatus += "<span class='status none' oldtitle='Mice production planned' title=''>"
									+  "<span>Mice<br>" + alleleType + "</span>"
									+  "</span>";
							
							exportMiceStatus.add(alleleType + " mice production planned");
						}	
					}						
				}
			}
		} 
		catch (Exception e) {
			log.error("Error getting ES cell/Mice status");
			log.error(e.getLocalizedMessage());
		}
		
		if ( toExport ){
			exportMiceStatus.add(0, esCellStatus); // want to keep this at front
			return StringUtils.join(exportMiceStatus, ", ");
		}
		return esCellStatus + miceStatus;
		
	}
	
	
	/**
	 * Get the mouse production status for gene (not allele) for geneHeatMap implementation for idg for each of 300 odd genes
	 * @param geneIds
	 * @return
	 * @throws SolrServerException
	 */
	public Map<String, String> getProductionStatusForGeneSet(Set<String> geneIds, HttpServletRequest request)
			throws SolrServerException {
		Map<String, String> geneToStatusMap = new HashMap<>();
		SolrQuery solrQuery = new SolrQuery();
		String query="*:*";
		solrQuery.setQuery(query);
		solrQuery.setFilterQueries(GeneField.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneIds, " OR ").replace(":", "\\:") + ")");
		solrQuery.setRows(100000);
		solrQuery.setFields(GeneField.MGI_ACCESSION_ID,GeneField.LATEST_MOUSE_STATUS);

		QueryResponse rsp = solr.query(solrQuery);
		System.out.println("solr query in basicbean=" + solrQuery);
		SolrDocumentList res = rsp.getResults();
		for (SolrDocument doc : res) {
			
			String accession = (String)doc.getFieldValue(GeneField.MGI_ACCESSION_ID);//each doc should have an accession
			if (doc.containsKey(GeneField.LATEST_MOUSE_STATUS)) {
				// String field = (String)doc.getFieldValue(GeneField.LATEST_MOUSE_STATUS);
				// productionStatus=this.getMouseProducedForGene(field);
				String prodStatusIcons = "Neither production nor phenotyping status available ";
				Map<String, String> prod = this.getProductionStatus(accession, request);
				prodStatusIcons = ( prod.get("icons").equalsIgnoreCase("") ) ? prodStatusIcons : prod.get("icons") ;
				// model.addAttribute("orderPossible" , prod.get("orderPossible"));
				// model.addAttribute("prodStatusIcons" , prodStatusIcons);
				// System.out.println(prodStatusIcons);
				geneToStatusMap.put(accession,prodStatusIcons);
							
			}
		}
		return geneToStatusMap;
	}
	
	/**
	 * Get the mouse top level mp associations for gene (not allele) for geneHeatMap implementation for idg for each of 300 odd genes
	 * @param geneIds
	 * @return
	 * @throws SolrServerException
	 */
	public Map<String, List<String>> getTopLevelMpForGeneSet(Set<String> geneIds)
			throws SolrServerException {
		Map<String, List<String>> geneToStatusMap = new HashMap<>();
		SolrQuery solrQuery = new SolrQuery();
		String query="*:*";
		solrQuery.setQuery(query);
		solrQuery.setFilterQueries(GeneField.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneIds, " OR ").replace(":", "\\:") + ")");
		solrQuery.setRows(100000);
		solrQuery.setFields(GeneField.MGI_ACCESSION_ID,GeneField.TOP_LEVEL_MP_ID);

		QueryResponse rsp = solr.query(solrQuery);
		System.out.println("solr query in basicbean=" + solrQuery);
		SolrDocumentList res = rsp.getResults();
		for (SolrDocument doc : res) {
		String accession = (String)doc.getFieldValue(GeneField.MGI_ACCESSION_ID);//each doc should have an accession
		List<String> topLevelMpIds=Collections.emptyList();
			if (doc.containsKey("top_level_mp_id")) {
				Collection<Object> topLevels = doc.getFieldValues(GeneField.TOP_LEVEL_MP_ID);
				topLevelMpIds=new ArrayList(topLevels);
			}
			
			geneToStatusMap.put(accession,topLevelMpIds);
		}
		return geneToStatusMap;
	}
	/**
	 * Get the mouse production status for gene (not allele) for geneHeatMap implementation for idg
	 * @param latestMouseStatus
	 * @return
	 */
	public String getMouseProducedForGene(String latestMouseStatus){
		//logic taken from allele core which has latest meaning gene level not allele
		// http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/gene/select?q=*:*&facet.field=latest_mouse_status&facet=true&rows=0
		
		 if ( latestMouseStatus .equals( "Chimeras obtained")
		 || latestMouseStatus .equals( "Micro-injection in progress")
		 || latestMouseStatus .equals( "Cre Excision Started")
		 || latestMouseStatus .equals( "Rederivation Complete")
		 || latestMouseStatus .equals( "Rederivation Started" )){
			 //latestMouseStatus = "Assigned for Mouse Production and Phenotyping"; // orange
			 latestMouseStatus = "In Progress"; 
		 }
		 else if (latestMouseStatus .equals( "Genotype confirmed")
		 || latestMouseStatus .equals( "Cre Excision Complete")
		 || latestMouseStatus .equals( "Phenotype Attempt Registered") ){
			 //latestMouseStatus = "Mice Produced"; // blue
			 latestMouseStatus = "Yes"; 
		 }else{
			 latestMouseStatus="No";
		 }
		 return  latestMouseStatus;
		 
	}

	public GeneDTO getGeneById(String mgiId) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery()
			.setQuery(GeneDTO.MGI_ACCESSION_ID + ":\"" + mgiId + "\"")
			.setRows(1)
			.setFields(GeneDTO.MGI_ACCESSION_ID,GeneDTO.TOP_LEVEL_MP_ID, GeneDTO.MARKER_SYMBOL);

		QueryResponse rsp = solr.query(solrQuery);
		if (rsp.getResults().getNumFound() > 0) {
			return rsp.getBeans(GeneDTO.class).get(0);
		}
		return null;
	}
	
	/**
	 * 
	 * @param geneIds
	 * @return Number of genes (from the provided list) in each status of interest.
	 */
	public HashMap<String, Long> getStatusCount(Set<String> geneIds){
		
		HashMap<String, Long> res = new HashMap<>();
		
		// build query for these genes
		String geneQuery = GeneField.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneIds, " OR ").replace(":", "\\:") + ")";
		System.out.println("geneQuery: " + geneQuery);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(geneQuery)
			.setRows(1)
			.setFacet(true);
		QueryResponse solrResponse;
		try {
			// add facet for latest_project_status 
			solrQuery.addFacetField(GeneDTO.LATEST_ES_CELL_STATUS);
			System.out.println("---- " + solr.getBaseURL() + "/select?" + solrQuery);
			solrResponse = solr.query(solrQuery);
			// put all values in the hash
			for (Count c : solrResponse.getFacetField(GeneDTO.LATEST_ES_CELL_STATUS).getValues()){
				res.put(c.getName(), c.getCount());
			}
			
			// add facet latest_es_cell_status
			solrQuery.removeFacetField(GeneDTO.LATEST_ES_CELL_STATUS);
			solrResponse = solr.query(solrQuery.addFacetField(GeneDTO.LATEST_PROJECT_STATUS));
			// put all values in the hash
			for (Count c : solrResponse.getFacetField(GeneDTO.LATEST_PROJECT_STATUS).getValues()){
				res.put(c.getName(), c.getCount());
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		return res;
	}
}
