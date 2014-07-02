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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.service.ObservationService.ExperimentField;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;

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

	private String derivePhenotypingStatus(SolrDocument doc) {

		String field = "latest_phenotype_status";
		try {
			// Phenotyping complete
			if (doc.containsKey(field)
					&& !doc.getFirstValue(field).toString().equals("")) {
				String val = doc.getFirstValue(field).toString();
				if (val.equals("Phenotyping Started")
						|| val.equals("Phenotyping Complete")) {
					return "available";
				}
			}

			// for legacy data: indexed through experiment core (so not want
			// Sanger Gene or Allele cores)
			if (doc.containsKey("hasQc")) {
				return "QCed data available";
			}
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
			phenStatus = derivePhenotypingStatus(doc).equals("") ? ""
					: "<a class='status done' title='Scroll down for phenotype associations.'><span>phenotype data available</span></a>";

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

					if (mouseStatusStr.equals("Mice Produced")) {
						String alleleName = alleleNames.get(i).toString();
						Matcher matcher = pattern.matcher(alleleName);
						if (matcher.find()) {
							String alleleType = matcher.group(1);
							miceStatus += "<span class='status done' title='"
									+ mouseStatusStr + "' >"
									+ "	<span>Mice<br>" + alleleType
									+ "</span>" + "</span>";
						}
						order = true;
					} else if (mouseStatusStr
							.equals("Assigned for Mouse Production and Phenotyping")) {
						String alleleName = alleleNames.get(i).toString();
						Matcher matcher = pattern.matcher(alleleName);
						if (matcher.find()) {
							String alleleType = matcher.group(1);
							miceStatus += "<span class='status inprogress' title='Mice production in progress' >"
									+ "	<span>Mice<br>"
									+ alleleType
									+ "</span>" + "</span>";
						}
					}
				}
				// if no mice status found but there is already allele produced,
				// mark it as "mice produced planned"
				for (int j = 0; j < alleleNames.size(); j++) {
					String alleleName = alleleNames.get(j).toString();
					if (!alleleName.equals("") && !alleleName.equals("None")
							&& mouseStatus.get(j).toString().equals("")) {
						Matcher matcher = pattern.matcher(alleleName);
						if (matcher.find()) {
							String alleleType = matcher.group(1);
							miceStatus += "<span class='status none' title='Mice production planned' >"
									+ "	<span>Mice<br>"
									+ alleleType
									+ "</span>" + "</span>";
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
					esCellStatus = "<a class='status done' href='' title='ES Cells produced' >"
							+ " <span>ES cells</span>" + "</a>";
					order = true;
				} else if (text.equals("ES Cell Production in Progress")) {
					esCellStatus = "<span class='status inprogress' title='ES cells production in progress' >"
							+ "	<span>ES Cell</span>" + "</span>";
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
	 * Get the mouse production status for gene (not allele) for geneHeatMap implementation for idg for each of 300 odd genes
	 * @param geneIds
	 * @return
	 * @throws SolrServerException
	 */
	public Map<String, String> getProductionStatusForGeneSet(Set<String> geneIds)
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
			String productionStatus="No";
			
			String accession = (String)doc.getFieldValue(GeneField.MGI_ACCESSION_ID);//each doc should have an accession
			if (doc.containsKey(GeneField.LATEST_MOUSE_STATUS)) {
				String field = (String)doc.getFieldValue(GeneField.LATEST_MOUSE_STATUS);
				//productionStatus=this.getMouseProducedForGene(field);
				
				String prodStatusIcons = "Neither production nor phenotyping status available ";
				
				

					Map<String, String> prod = this.getProductionStatus(accession);
					prodStatusIcons = ( prod.get("icons").equalsIgnoreCase("") ) ? prodStatusIcons : prod.get("icons") ;
					//model.addAttribute("orderPossible" , prod.get("orderPossible"));
				
				//model.addAttribute("prodStatusIcons" , prodStatusIcons);
					//System.out.println(prodStatusIcons);
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

}
