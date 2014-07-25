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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
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
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.service.dto.GeneDTO;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

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
		public final static String TOP_LEVEL_MP_ID = "top_level_mp_id";
	}

	public static final class GeneFieldValue {

		public final static String CENTRE_WTSI = "WTSI";
		public final static String PHENOTYPE_STATUS_COMPLETE = "Phenotyping Complete";
		public final static String PHENOTYPE_STATUS_STARTED = "Phenotyping Started";
	}


	public GeneService(String solrUrl) {

		solr = new HttpSolrServer(solrUrl);

	}


	private String derivePhenotypingStatus(SolrDocument doc) {

		String field = "latest_phenotype_status";
		try {
			// Phenotyping complete
			if (doc.containsKey(field) && !doc.getFirstValue(field).toString().equals("")) {
				String val = doc.getFirstValue(field).toString();
				if (val.equals("Phenotyping Started") || val.equals("Phenotyping Complete")) { return "available"; }
			}

			// for legacy data: indexed through experiment core (so not want
			// Sanger Gene or Allele cores)
			if (doc.containsKey("hasQc")) { return "QCed data available"; }
		} catch (Exception e) {
			log.error("Error getting phenotyping status");
			log.error(e.getLocalizedMessage());
		}

		return "";
	}


	/**
	 * Return all genes in the gene core matching latestPhenotypeStatus and
	 * latestProductionCentre.
	 * 
	 * @param latestPhenotypeStatus
	 *            latest phenotype status (i.e. most advanced along the
	 *            pipeline)
	 * @param latestProductionCentre
	 *            latest production centre (i.e. most advanced along the
	 *            pipeline)
	 * @return all genes in the gene core matching phenotypeStatus and
	 *         productionCentre.
	 * @throws SolrServerException
	 */
	public Set<String> getGenesByLatestPhenotypeStatusAndProductionCentre(String latestPhenotypeStatus, String latestProductionCentre)
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		String queryString = "(" + GeneField.LATEST_PHENOTYPE_STATUS + ":\"" + latestPhenotypeStatus + "\") AND (" + GeneField.LATEST_PRODUCTION_CENTRE + ":\"" + latestProductionCentre + "\")";
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

		log.debug("getGenesByLatestPhenotypeStatusAndProductionCentre: solrQuery = " + queryString);
		return allGenes;
	}


	/**
	 * Return all genes in the gene core matching latestPhenotypeStatus and
	 * latestPhenotypeCentre.
	 * 
	 * @param latestPhenotypeStatus
	 *            latest phenotype status (i.e. most advanced along the
	 *            pipeline)
	 * @param latestPhenotypeCentre
	 *            latest phenotype centre (i.e. most advanced along the
	 *            pipeline)
	 * @return all genes in the gene core matching phenotypeStatus and
	 *         productionCentre.
	 * @throws SolrServerException
	 */
	public Set<String> getGenesByLatestPhenotypeStatusAndPhenotypeCentre(String latestPhenotypeStatus, String latestPhenotypeCentre)
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		String queryString = "(" + GeneField.LATEST_PHENOTYPE_STATUS + ":\"" + latestPhenotypeStatus + "\") AND (" + GeneField.LATEST_PHENOTYPING_CENTRE + ":\"" + latestPhenotypeCentre + "\")";
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

		log.debug("getGenesByLatestPhenotypeStatusAndPhenotypeCentre: solrQuery = " + queryString);
		return allGenes;
	}


	/**
	 * Return all genes from the gene core.
	 * 
	 * @return all genes from the gene core.
	 * @throws SolrServerException
	 */
	public Set<String> getAllGenes()
	throws SolrServerException {

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
	public Set<String> getAllNonConformingGenes()
	throws SolrServerException {

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
	public Map<String, String> getProductionStatus(String geneId)
	throws SolrServerException {

		SolrQuery query = new SolrQuery();
		query.setQuery("mgi_accession_id:\"" + geneId + "\"");
		QueryResponse response = solr.query(query);
		SolrDocument doc = response.getResults().get(0);
		return getStatusFromDoc(doc);

	}


	private Map<String, String> getStatusFromDoc(SolrDocument doc) {

		String miceStatus = "";
		String esCellStatus = "";
		String phenStatus = "";
		Boolean order = false;

		try {

			/* ******** phenotype status ******** */
			phenStatus = derivePhenotypingStatus(doc).equals("") ? "" : "<a class='status done' title='Scroll down for phenotype associations.'><span>phenotype data available</span></a>";

			/* ******** mice production status ******** */
			String patternStr = "(tm.*)\\(.+\\).+"; // allele name pattern
			Pattern pattern = Pattern.compile(patternStr);

			// Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele
			// docs)
			if (doc.containsKey("mouse_status")) {

				ArrayList<String> alleleNames = (ArrayList<String>) doc.getFieldValue("allele_name");
				ArrayList<String> mouseStatus = (ArrayList<String>) doc.getFieldValue("mouse_status");

				for (int i = 0; i < mouseStatus.size(); i++) {
					String mouseStatusStr = mouseStatus.get(i).toString();

					if (mouseStatusStr.equals("Mice Produced")) {
						String alleleName = alleleNames.get(i).toString();
						Matcher matcher = pattern.matcher(alleleName);
						if (matcher.find()) {
							String alleleType = matcher.group(1);
							miceStatus += "<span class='status done' title='" + mouseStatusStr + "' >" + "	<span>Mice<br>" + alleleType + "</span>" + "</span>";
						}
						order = true;
					} else if (mouseStatusStr.equals("Assigned for Mouse Production and Phenotyping")) {
						String alleleName = alleleNames.get(i).toString();
						Matcher matcher = pattern.matcher(alleleName);
						if (matcher.find()) {
							String alleleType = matcher.group(1);
							miceStatus += "<span class='status inprogress' title='Mice production in progress' >" + "	<span>Mice<br>" + alleleType + "</span>" + "</span>";
						}
					}
				}

			}

			/* ******** ES cell production status ******** */
			String field = "latest_es_cell_status";
			if (doc.containsKey(field)) {
				// blue es cell status
				String text = doc.getFirstValue(field).toString();
				if (text.equals("ES Cell Targeting Confirmed")) {
					esCellStatus = "<a class='status done' href='' title='ES Cells produced' >" + " <span>ES cells</span>" + "</a>";
					order = true;
				} else if (text.equals("ES Cell Production in Progress")) {
					esCellStatus = "<span class='status inprogress' title='ES cells production in progress' >" + "	<span>ES Cell</span>" + "</span>";
				}

			}

		} catch (Exception e) {
			log.error("Error getting ES cell/Mice status");
			log.error(e.getLocalizedMessage());
		}
		HashMap<String, String> res = new HashMap<>();
		res.put("icons", esCellStatus + miceStatus + phenStatus);
		res.put("orderPossible", order.toString());
		return res;
	}


	// private Map<String, String> getStatusFromDoc(SolrDocument doc) {
	// String miceStatus = "";
	// String esCellStatus = "";
	// String phenStatus = "";
	// Boolean order = false;
	//
	// try {
	//
	// /* ******** phenotype status ******** */
	// phenStatus = derivePhenotypingStatus(doc).equals("") ? ""
	// :
	// "<a class='status done' title='Scroll down for phenotype associations.'><span>phenotype data available</span></a>";
	//
	// /* ******** mice production status ******** */
	// String patternStr = "(tm.*)\\(.+\\).+"; // allele name pattern
	// Pattern pattern = Pattern.compile(patternStr);
	//
	// // Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele
	// // docs)
	// if (doc.containsKey("mouse_status")) {
	//
	// ArrayList<String> alleleNames = (ArrayList<String>) doc
	// .getFieldValue("allele_name");
	// ArrayList<String> mouseStatus = (ArrayList<String>) doc
	// .getFieldValue("mouse_status");
	//
	// for (int i = 0; i < mouseStatus.size(); i++) {
	// String mouseStatusStr = mouseStatus.get(i).toString();
	//
	// if (mouseStatusStr.equals("Mice Produced")) {
	// String alleleName = alleleNames.get(i).toString();
	// Matcher matcher = pattern.matcher(alleleName);
	// if (matcher.find()) {
	// String alleleType = matcher.group(1);
	// miceStatus += "<span class='status done' title='"
	// + mouseStatusStr + "' >"
	// + "	<span>Mice<br>" + alleleType
	// + "</span>" + "</span>";
	// }
	// order = true;
	// } else if (mouseStatusStr
	// .equals("Assigned for Mouse Production and Phenotyping")) {
	// String alleleName = alleleNames.get(i).toString();
	// Matcher matcher = pattern.matcher(alleleName);
	// if (matcher.find()) {
	// String alleleType = matcher.group(1);
	// miceStatus +=
	// "<span class='status inprogress' title='Mice production in progress' >"
	// + "	<span>Mice<br>"
	// + alleleType
	// + "</span>" + "</span>";
	// }
	// }
	// }
	// // if no mice status found but there is already allele produced,
	// // mark it as "mice produced planned"
	// for (int j = 0; j < alleleNames.size(); j++) {
	// String alleleName = alleleNames.get(j).toString();
	// if (!alleleName.equals("") && !alleleName.equals("None")
	// && mouseStatus.get(j).toString().equals("")) {
	// Matcher matcher = pattern.matcher(alleleName);
	// if (matcher.find()) {
	// String alleleType = matcher.group(1);
	// miceStatus +=
	// "<span class='status none' title='Mice production planned' >"
	// + "	<span>Mice<br>"
	// + alleleType
	// + "</span>" + "</span>";
	// }
	// }
	// }
	//
	// }
	//
	// /* ******** ES cell production status ******** */
	// String field = "latest_es_cell_status";
	// if (doc.containsKey(field)) {
	// // blue es cell status
	// String text = doc.getFirstValue(field).toString();
	// if (text.equals("ES Cell Targeting Confirmed")) {
	// esCellStatus =
	// "<a class='status done' href='' title='ES Cells produced' >"
	// + " <span>ES cells</span>" + "</a>";
	// order = true;
	// } else if (text.equals("ES Cell Production in Progress")) {
	// esCellStatus =
	// "<span class='status inprogress' title='ES cells production in progress' >"
	// + "	<span>ES Cell</span>" + "</span>";
	// }
	//
	// }
	//
	// } catch (Exception e) {
	// log.error("Error getting ES cell/Mice status");
	// log.error(e.getLocalizedMessage());
	// }
	// HashMap<String, String> res = new HashMap<>();
	// res.put("icons", esCellStatus + miceStatus + phenStatus);
	// res.put("orderPossible", order.toString());
	// return res;
	// }

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
					ArrayList<String> statuses = (ArrayList<String>) doc.getFieldValue("phenotype_status");
					for (String status : statuses) {
						if (status.equalsIgnoreCase("Phenotyping Started") || status.equalsIgnoreCase("Phenotyping Complete")) { return true; }
					}
				}
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Get the mouse production status for gene (not allele) for geneHeatMap
	 * implementation for idg for each of 300 odd genes
	 * 
	 * @param geneIds
	 * @return
	 * @throws SolrServerException
	 */
	public Map<String, String> getProductionStatusForGeneSet(Set<String> geneIds)
	throws SolrServerException {

		Map<String, String> geneToStatusMap = new HashMap<>();
		SolrQuery solrQuery = new SolrQuery();
		String query = "*:*";
		solrQuery.setQuery(query);
		solrQuery.setFilterQueries(GeneField.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneIds, " OR ").replace(":", "\\:") + ")");
		solrQuery.setRows(100000);
		solrQuery.setFields(GeneField.MGI_ACCESSION_ID, GeneField.LATEST_MOUSE_STATUS);

		QueryResponse rsp = solr.query(solrQuery);
		System.out.println("solr query in basicbean=" + solrQuery);
		SolrDocumentList res = rsp.getResults();
		for (SolrDocument doc : res) {

			String accession = (String) doc.getFieldValue(GeneField.MGI_ACCESSION_ID);// each
																						// doc
																						// should
																						// have
																						// an
																						// accession
			if (doc.containsKey(GeneField.LATEST_MOUSE_STATUS)) {
				// String field =
				// (String)doc.getFieldValue(GeneField.LATEST_MOUSE_STATUS);
				// productionStatus=this.getMouseProducedForGene(field);
				String prodStatusIcons = "Neither production nor phenotyping status available ";
				Map<String, String> prod = this.getProductionStatus(accession);
				prodStatusIcons = (prod.get("icons").equalsIgnoreCase("")) ? prodStatusIcons : prod.get("icons");
				// model.addAttribute("orderPossible" ,
				// prod.get("orderPossible"));
				// model.addAttribute("prodStatusIcons" , prodStatusIcons);
				// System.out.println(prodStatusIcons);
				geneToStatusMap.put(accession, prodStatusIcons);

			}
		}
		return geneToStatusMap;
	}


	/**
	 * Get the mouse top level mp associations for gene (not allele) for
	 * geneHeatMap implementation for idg for each of 300 odd genes
	 * 
	 * @param geneIds
	 * @return
	 * @throws SolrServerException
	 */
	public Map<String, List<String>> getTopLevelMpForGeneSet(Set<String> geneIds)
	throws SolrServerException {

		Map<String, List<String>> geneToStatusMap = new HashMap<>();
		SolrQuery solrQuery = new SolrQuery();
		String query = "*:*";
		solrQuery.setQuery(query);
		solrQuery.setFilterQueries(GeneField.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneIds, " OR ").replace(":", "\\:") + ")");
		solrQuery.setRows(100000);
		solrQuery.setFields(GeneField.MGI_ACCESSION_ID, GeneField.TOP_LEVEL_MP_ID);

		QueryResponse rsp = solr.query(solrQuery);
		System.out.println("solr query in basicbean=" + solrQuery);
		SolrDocumentList res = rsp.getResults();
		for (SolrDocument doc : res) {
			String accession = (String) doc.getFieldValue(GeneField.MGI_ACCESSION_ID);// each
																						// doc
																						// should
																						// have
																						// an
																						// accession
			List<String> topLevelMpIds = Collections.emptyList();
			if (doc.containsKey("top_level_mp_id")) {
				Collection<Object> topLevels = doc.getFieldValues(GeneField.TOP_LEVEL_MP_ID);
				topLevelMpIds = new ArrayList(topLevels);
			}

			geneToStatusMap.put(accession, topLevelMpIds);
		}
		return geneToStatusMap;
	}


	/**
	 * Get the mouse production status for gene (not allele) for geneHeatMap
	 * implementation for idg
	 * 
	 * @param latestMouseStatus
	 * @return
	 */
	public String getMouseProducedForGene(String latestMouseStatus) {

		// logic taken from allele core which has latest meaning gene level not
		// allele
		// http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/gene/select?q=*:*&facet.field=latest_mouse_status&facet=true&rows=0

		if (latestMouseStatus.equals("Chimeras obtained") || latestMouseStatus.equals("Micro-injection in progress") || latestMouseStatus.equals("Cre Excision Started") || latestMouseStatus.equals("Rederivation Complete") || latestMouseStatus.equals("Rederivation Started")) {
			// latestMouseStatus =
			// "Assigned for Mouse Production and Phenotyping"; // orange
			latestMouseStatus = "In Progress";
		} else if (latestMouseStatus.equals("Genotype confirmed") || latestMouseStatus.equals("Cre Excision Complete") || latestMouseStatus.equals("Phenotype Attempt Registered")) {
			// latestMouseStatus = "Mice Produced"; // blue
			latestMouseStatus = "Yes";
		} else {
			latestMouseStatus = "No";
		}
		return latestMouseStatus;

	}


	public GeneDTO getGeneById(String mgiId)
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery().setQuery(GeneDTO.MGI_ACCESSION_ID + ":\"" + mgiId + "\"").setRows(1).setFields(GeneDTO.MGI_ACCESSION_ID, GeneDTO.TOP_LEVEL_MP_ID, GeneDTO.MARKER_SYMBOL);

		QueryResponse rsp = solr.query(solrQuery);
		if (rsp.getResults().getNumFound() > 0) { return rsp.getBeans(GeneDTO.class).get(0); }
		return null;

	}


	/**
	 * Gets the json string representation for the query.
	 * 
	 * @param gridSolrParams
	 *            the default solr parameters to append to the query
	 * @param start
	 *            where to start the offset
	 * @param length
	 *            how many documents to return
	 * 
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public QueryResponse getSearchGeneDataTableJson(String gridSolrParams, int start, int length)
	throws IOException, URISyntaxException {

		if (gridSolrParams.equals("")) {
			gridSolrParams = "qf=auto_suggest&defType=edismax&wt=json&q=*:*";
		}
		// return getResults(composeSolrUrl(core, mode, query, gridSolrParams,
		// start, length, showImgView));
		try {
			return getGeneResponseForSearchDataTableSolrUrl(composeSolrUrlForSearchGeneDataTable(gridSolrParams, start, length));
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

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
	private String composeSolrUrlForSearchGeneDataTable(String gridSolrParams, Integer iDisplayStart, Integer iDisplayLength) {

		String url = "";
		url += gridSolrParams + "&start=" + iDisplayStart + "&rows=" + iDisplayLength;
		System.out.println("GENE PARAMS: " + url);
		return url;
	}


	/**
	 * 
	 * @param solrUrl
	 * @return
	 * @throws SolrServerException
	 */
	public QueryResponse getGeneResponseForSearchDataTableSolrUrl(String solrUrl)
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		String[] paramsKeyValues = solrUrl.split("&");
		for (String paramKV : paramsKeyValues) {
			System.out.println("paramKV=" + paramKV);
			String[] keyValue = paramKV.split("=");
			if (keyValue.length > 1) {
				String key = keyValue[0];
				String value = keyValue[1];
				System.out.println("param=" + key + " value=" + value);
				solrQuery.setParam(key, value);
			}

		}
		// solrQuery.setQuery(solrUrl).setRows(10);
		return getQueryResponseForSolrQuery(solrQuery);
	}


	private QueryResponse getQueryResponseForSolrQuery(SolrQuery solrQuery)
	throws SolrServerException {

		QueryResponse rsp = solr.query(solrQuery);
		return rsp;
	}


	/**
	 * Get the latest production status of ES cells for a document.
	 * 
	 * @param doc
	 *            represents a gene with imits status fields
	 * @return the latest status at the gene level for ES cells as a string
	 */
	public String fetchHtmlEsCellStatusForSearchDataTable(GeneDTO doc, HttpServletRequest request, boolean toExport) {
		this.fetchEsCellStatus(doc);
		String mgiId = doc.getMgiAccessionId();
		String geneUrl = request.getAttribute("baseUrl") + "/genes/" + mgiId;

		String esCellStatus = "";
		String exportEsCellStatus = "";
		try {
			// ES cell production status
			if (doc.getLatestEsCellStatus() != null && doc.getLatestEsCellStatus() != "") {
				// blue es cell status
				esCellStatus = doc.getLatestEsCellStatus();
				if (esCellStatus.equals("ES Cells Produced")) {
					esCellStatus = "<a class='status done' href='" + geneUrl + "' oldtitle='ES Cells produced' title=''>" + " <span>ES Cells</span>" + "</a>";

					exportEsCellStatus += "ES cells produced";
				} else if (esCellStatus.equals("Assigned for ES Cell Production")) {
					esCellStatus = "<span class='status inprogress' oldtitle='ES cells production in progress' title=''>" + "	<span>ES Cells</span>" + "</span>";

					exportEsCellStatus += "ES cells production in progress";
				} else {
					esCellStatus = "";
					exportEsCellStatus = "No ES cell produced";
				}
			}
		} catch (Exception e) {
			log.error("Error getting ES cell Status");
			log.error(e.getLocalizedMessage());
		}

		if (toExport) { return exportEsCellStatus; }
		return esCellStatus;
	}

	public String fetchEsCellStatus(GeneDTO doc) {
//		String rawESCellStatus = "";
//		String labelForESCellStatus = "";
		String label="";
		try {
			// ES cell production status
//			if (doc.getLatestEsCellStatus() != null && doc.getLatestEsCellStatus() != "") {
				// blue es cell status
				ESCellStatus cellStatusEnum =doc.getLatestESCellGeneStatus();
				
				label=cellStatusEnum.getLabel();
//				if (rawESCellStatus.equals(ESCellStatus.ES_CELLS_PRODUCED.getLabel())) {
//					labelForESCellStatus += "ES cells produced";
//				} else if (rawESCellStatus.equals("Assigned for ES Cell Production")) {
//					labelForESCellStatus += "ES cells production in progress";
//				} else {
//					labelForESCellStatus = "No ES cell produced";
//				}
//			}
		} catch (Exception e) {
			log.error("Error getting ES cell Status");
			log.error(e.getLocalizedMessage());
		}
//System.out.println(label);
//System.out.println(labelForESCellStatus);
		//return labelForESCellStatus;
		return label;
	}

	/**
	 * Get the simplified production status of ES cells/mice for a document.
	 * 
	 * @param doc
	 *            represents a gene with imits status fields
	 * @return the latest status at the gene level for both ES cells and alleles
	 */
	public String deriveLatestProductionStatusForEsCellAndMice(GeneDTO doc, HttpServletRequest request, boolean toExport, String geneLink) {

		String exportMiceStatus = "";

		Map<String, String> sh = new HashMap<String, String>();
		String miceStatus = "";
		try {

			// mice production status
			// Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele
			// docs)
			if (doc.getMouseStatus() != null && !doc.getMouseStatus().equals("")) {

				List<String> alleleNames = doc.getAlleleName();
				List<String> mouseStatus = doc.getMouseStatus();

				for (int i = 0; i < mouseStatus.size(); i++) {
					String mouseStatusStr = mouseStatus.get(i).toString();
					sh.put(mouseStatusStr, "yes");
				}

				// if no mice status found but there is already allele produced,
				// mark it as "mice produced planned"
				for (int j = 0; j < alleleNames.size(); j++) {
					String alleleName = alleleNames.get(j).toString();
					// System.out.println("allele: " + alleleName);
					if (!alleleName.equals("") && !alleleName.equals("None") && !alleleName.contains("tm1e") && mouseStatus.get(j).toString().equals("")) {
						sh.put("mice production planned", "yes");
					}
				}

				if (sh.containsKey("Mice Produced")) {
					miceStatus = "<a class='status done' oldtitle='Mice Produced' title='' href='" + geneLink + "#order'>" + "<span>Mice</span>" + "</a>";

					exportMiceStatus = "mice produced";
				} else if (sh.containsKey("Assigned for Mouse Production and Phenotyping")) {
					miceStatus = "<a class='status inprogress' oldtitle='Mice production in progress' title=''>" + "<span>Mice</span>" + "</a>";
					exportMiceStatus = "mice production in progress";
				} else if (sh.containsKey("mice production planned")) {
					miceStatus = "<a class='status none' oldtitle='Mice production planned' title=''>" + "<span>Mice</span>" + "</a>";
					exportMiceStatus = "mice production in progress";
				}
			}

		} catch (Exception e) {
			log.error("Error getting Mice status");
			log.error(e.getLocalizedMessage());
		}
		if (toExport) {
			return exportMiceStatus;
		} else {
			return miceStatus;
		}
	}


	/**
	 * Get the latest phenotyping status for a document.
	 * 
	 * @param doc
	 *            represents a gene with imits status fields
	 * @return the latest status (Complete or Started or Phenotype Attempt
	 *         Registered) as appropriate for this gene
	 */
	public String deriveLatestPhenotypingStatus(GeneDTO doc) {

		final String field = "latest_phenotype_status";
		try {
			// Phenotyping complete
			if (doc.getLatestPhenotypeStatus() != null && !doc.getLatestPhenotypeStatus().equals("")) {
				String val = doc.getLatestPhenotypeStatus();
				if (val.equals("Phenotyping Started") || val.equals("Phenotyping Complete")) { return "Available"; }
			}

			// for legacy data: indexed through experiment core (so not want
			// Sanger Gene or Allele cores)
			if (doc.getHasQc() != null) { return "QC"; }
		} catch (Exception e) {
			log.error("Error getting phenotyping status");
			log.error(e.getLocalizedMessage());
		}

		return "NA";
	}
}
