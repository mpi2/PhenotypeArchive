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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.DatasourceEntityId;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.Project;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.web.controller.OverviewChartsController;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;
import uk.ac.ebi.phenotype.web.pojo.HeatMapCell;

@Service
public class GenotypePhenotypeService extends BasicService {

	@Autowired
	PhenotypePipelineDAO pipelineDAO;

	private HttpSolrServer solr;


	public GenotypePhenotypeService(String solrUrl) {

		solr = new HttpSolrServer(solrUrl);
	}


	/**
	 * Returns a list of a all colonies
	 * 
	 * @param phenotypeResourceName
	 * @return
	 * @throws SolrServerException
	 */
	public List<Map<String, String>> getAllMPByPhenotypingCenterAndColonies(String phenotypeResourceName, String mpTermAcc, String mpTermName)
	throws SolrServerException {

		SolrQuery query = new SolrQuery().setQuery("*:*").addFilterQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + phenotypeResourceName).setRows(MAX_NB_DOCS).setFields(GenotypePhenotypeDTO.PHENOTYPING_CENTER + "," + mpTermAcc + "," + mpTermName + "," + GenotypePhenotypeDTO.COLONY_ID + "," + GenotypePhenotypeDTO.MARKER_SYMBOL + "," + GenotypePhenotypeDTO.MARKER_ACCESSION_ID);

		QueryResponse response = solr.query(query);
		SolrDocumentList results = response.getResults();

		List<Map<String, String>> lmap = new ArrayList<Map<String, String>>();

		for (SolrDocument doc : results) {

			String phenotypingCenter = (String) doc.getFieldValue(GenotypePhenotypeDTO.PHENOTYPING_CENTER);
			String colonyID = (String) doc.getFieldValue(GenotypePhenotypeDTO.COLONY_ID);
			String markerSymbol = (String) doc.getFieldValue(GenotypePhenotypeDTO.MARKER_SYMBOL);
			String markerAccession = (String) doc.getFieldValue(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);

			if (mpTermAcc.equals(GenotypePhenotypeDTO.MP_TERM_ID)) {

				Map<String, String> r = new HashMap<String, String>();
				r.put(GenotypePhenotypeDTO.PHENOTYPING_CENTER, phenotypingCenter);
				r.put(GenotypePhenotypeDTO.COLONY_ID, colonyID);
				r.put(GenotypePhenotypeDTO.MARKER_SYMBOL, markerSymbol);
				r.put(GenotypePhenotypeDTO.MARKER_ACCESSION_ID, markerAccession);
				r.put(mpTermAcc, (String) doc.getFieldValue(GenotypePhenotypeDTO.MP_TERM_ID));
				r.put(mpTermName, (String) doc.getFieldValue(GenotypePhenotypeDTO.MP_TERM_NAME));

				lmap.add(r);

			} else {

				ArrayList<String> mpTermIds = (ArrayList<String>) doc.getFieldValue(mpTermAcc);
				ArrayList<String> mpTermNames = (ArrayList<String>) doc.getFieldValue(mpTermName);

				for (int i = 0; i < mpTermIds.size(); i++) {
					Map<String, String> r = new HashMap<String, String>();
					r.put(GenotypePhenotypeDTO.PHENOTYPING_CENTER, phenotypingCenter);
					r.put(GenotypePhenotypeDTO.COLONY_ID, colonyID);
					r.put(GenotypePhenotypeDTO.MARKER_SYMBOL, markerSymbol);
					r.put(GenotypePhenotypeDTO.MARKER_ACCESSION_ID, markerAccession);
					r.put(mpTermAcc, mpTermIds.get(i));
					r.put(mpTermName, mpTermNames.get(i));
					lmap.add(r);
				}
			}
		}

		return lmap;

	}


	/**
	 * 
	 * @param mpId
	 * @return List of parameters that led to at least one association to the
	 *         given parameter or some class in its subtree
	 * @throws SolrServerException
	 * @author tudose
	 */
	public ArrayList<Parameter> getParametersForPhenotype(String mpId)
	throws SolrServerException {

		ArrayList<Parameter> res = new ArrayList<>();
		SolrQuery q = new SolrQuery().setQuery("(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " 
			+ GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" 
			+ mpId + "\") AND (" + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsController.OVERVIEW_STRAINS, "\" OR " + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"") + "\")").setRows(0);
		q.set("facet.field", "" + GenotypePhenotypeDTO.PARAMETER_STABLE_ID);
		q.set("facet", true);
		q.set("facet.limit", -1);
		q.set("facet.mincount", 1);
		QueryResponse response = solr.query(q);
		for (Count parameter : response.getFacetField(GenotypePhenotypeDTO.PARAMETER_STABLE_ID).getValues()) {
			// fill genes for each of them
			// if (parameter.getCount() > 0){
			res.add(pipelineDAO.getParameterByStableId(parameter.getName()));
			// }
		}
		return res;
	}


	public List<Group> getGenesBy(String mpId, String sex)
	throws SolrServerException {

		// males only
		SolrQuery q = new SolrQuery().setQuery("(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + mpId + "\") AND (" 
		 + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsController.OVERVIEW_STRAINS, "\" OR " + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"") + "\")").setRows(10000);
		q.set("group.field", "" + GenotypePhenotypeDTO.MARKER_SYMBOL);
		q.set("group", true);
		q.set("group.limit", 0);
		if (sex != null) {
			q.addFilterQuery(GenotypePhenotypeDTO.SEX + ":" + sex);
		}
		QueryResponse results = solr.query(q);
		return results.getGroupResponse().getValues().get(0).getValues();
	}


	public List<String> getGenesAssocByParamAndMp(String parameterStableId, String phenotype_id)
	throws SolrServerException {

		List<String> res = new ArrayList<String>();
		SolrQuery query = new SolrQuery().setQuery("(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + phenotype_id + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" 
			+ phenotype_id + "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + phenotype_id + "\") AND (" 
			+ GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsController.OVERVIEW_STRAINS, "\" OR " + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"") + "\") AND " 
			+ GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":\"" + parameterStableId + "\"").setRows(-1);
		query.set("group.field", GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
		query.set("group", true);
		List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
		for (Group gr : groups) {
			if (!res.contains((String) gr.getGroupValue())) {
				res.add((String) gr.getGroupValue());
			}
		}
		return res;
	}


	/**
	 * Returns a set of MARKER_ACCESSION_ID strings of all genes that have
	 * phenotype associations.
	 * 
	 * @return a set of MARKER_ACCESSION_ID strings of all genes that have
	 *         phenotype associations.
	 * @throws SolrServerException
	 */
	public Set<String> getAllGenesWithPhenotypeAssociations()
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":*");
		solrQuery.setRows(1000000);
		solrQuery.setFields(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
		QueryResponse rsp = null;
		rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allGenes = new HashSet<String>();
		for (SolrDocument doc : res) {
			allGenes.add((String) doc.getFieldValue(GenotypePhenotypeDTO.MARKER_ACCESSION_ID));
		}
		return allGenes;
	}


	/**
	 * Returns a set of MP_TERM_ID strings of all phenotypes that have gene
	 * associations.
	 * 
	 * @return a set of MP_TERM_ID strings of all phenotypes that have gene
	 *         associations.
	 * @throws SolrServerException
	 */
	public Set<String> getAllPhenotypesWithGeneAssociations()
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(GenotypePhenotypeDTO.MP_TERM_ID + ":*");
		solrQuery.setRows(1000000);
		solrQuery.setFields(GenotypePhenotypeDTO.MP_TERM_ID);
		QueryResponse rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allPhenotypes = new HashSet();
		for (SolrDocument doc : res) {
			allPhenotypes.add((String) doc.getFieldValue(GenotypePhenotypeDTO.MP_TERM_ID));
		}

		return allPhenotypes;
	}


	/**
	 * Returns a set of MP_TERM_ID strings of all top-level phenotypes.
	 * 
	 * @return a set of MP_TERM_ID strings of all top-level phenotypes.
	 * @throws SolrServerException
	 */
	public Set<String> getAllTopLevelPhenotypes()
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":*");
		solrQuery.setRows(1000000);
		solrQuery.setFields(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID);
		QueryResponse rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allTopLevelPhenotypes = new HashSet();
		for (SolrDocument doc : res) {
			ArrayList<String> ids = (ArrayList<String>) doc.getFieldValue(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID);
			for (String id : ids) {
				allTopLevelPhenotypes.add(id);
			}
		}

		return allTopLevelPhenotypes;
	}


	/**
	 * Returns a set of MP_TERM_ID strings of all intermediate-level phenotypes.
	 * 
	 * @return a set of MP_TERM_ID strings of all intermediate-level phenotypes.
	 * @throws SolrServerException
	 */
	public Set<String> getAllIntermediateLevelPhenotypes()
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":*");
		solrQuery.setRows(1000000);
		solrQuery.setFields(GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID);
		QueryResponse rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allIntermediateLevelPhenotypes = new HashSet();
		for (SolrDocument doc : res) {
			ArrayList<String> ids = (ArrayList<String>) doc.getFieldValue(GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID);
			for (String id : ids) {
				allIntermediateLevelPhenotypes.add(id);
			}
		}

		return allIntermediateLevelPhenotypes;
	}


	/*
	 * Methods used by PhenotypeSummaryDAO
	 */

	public SolrDocumentList getPhenotypesForTopLevelTerm(String gene, String mpID)
	throws SolrServerException {

		String query;
		if (gene.equalsIgnoreCase("*")) {
			query = GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":" + gene + " AND ";
		} else {
			query = GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\" AND ";

		}

		SolrDocumentList result = runQuery(query + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpID + "\"");
		// mpID might be in mp_id instead of top level field
		if (result.size() == 0 || result == null)
		// result = runQuery("marker_accession_id:" + gene.replace(":",
		// "\\:") + " AND mp_term_id:" + mpID.replace(":", "\\:"));
			result = runQuery(query + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpID + "\"");// AND
		// -" + GenotypePhenotypeDTO.RESOURCE_NAME + ":IMPC");
		return result;
	}


	public SolrDocumentList getPhenotypes(String gene)
	throws SolrServerException {

		SolrDocumentList result = runQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\"");
		return result;
	}


	private SolrDocumentList runQuery(String q)
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery().setQuery(q);
		solrQuery.setRows(1000000);
		QueryResponse rsp = null;
		rsp = solr.query(solrQuery);
		return rsp.getResults();
	}


	public HashMap<String, String> getTopLevelMPTerms(String gene)
	throws SolrServerException {

		HashMap<String, String> tl = new HashMap<String, String>();
		// SolrDocumentList result = runQuery("marker_accession_id:" +
		// gene.replace(":", "\\:"));
		SolrDocumentList result;
		if (gene.equalsIgnoreCase("*")) {
			result = runQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":" + gene);
		} else {
			result = runQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\"");
		}

		if (result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				SolrDocument doc = result.get(i);
				if (doc.getFieldValue(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID) != null) {
					ArrayList<String> tlTermIDs = (ArrayList<String>) doc.getFieldValue(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID);
					ArrayList<String> tlTermNames = (ArrayList<String>) doc.getFieldValue(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);
					int len = tlTermIDs.size();
					for (int k = 0; k < len; k++) {
						tl.put(tlTermIDs.get(k), tlTermNames.get(k));
					}
					// tl.put((String)
					// doc.getFieldValue("top_level_mp_term_id"), (String)
					// doc.getFieldValue("top_level_mp_term_name"));
				} else { // it seems that when the term id is a top level term
							// itself the top level term field
					tl.put((String) doc.getFieldValue(GenotypePhenotypeDTO.MP_TERM_ID), (String) doc.getFieldValue(GenotypePhenotypeDTO.MP_TERM_NAME));
				}
			}
		}
		return tl;
	}


	/*
	 * End of Methods for PhenotypeSummaryDAO
	 */

	/*
	 * Methods for PipelineSolrImpl
	 */
	public Parameter getParameterByStableId(String paramStableId, String queryString)
	throws IOException, URISyntaxException {

		String solrUrl = solr.getBaseURL() + "/select/?q=" + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":\"" + paramStableId + "\"&rows=10000000&version=2.2&start=0&indent=on&wt=json";
		if (queryString.startsWith("&")) {
			solrUrl += queryString;
		} else {// add an ampersand parameter splitter if not one as we need
			// one to add to the already present solr query string
			solrUrl += "&" + queryString;
		}
		return createParameter(solrUrl);
	}


	private Parameter createParameter(String url)
	throws IOException, URISyntaxException {

		Parameter parameter = new Parameter();
		JSONObject results = null;
		results = JSONRestUtil.getResults(url);

		JSONArray docs = results.getJSONObject("response").getJSONArray("docs");
		for (Object doc : docs) {
			JSONObject paramDoc = (JSONObject) doc;
			String isDerivedInt = paramDoc.getString("parameter_derived");
			boolean derived = false;
			if (isDerivedInt.equals("true")) {
				derived = true;
			}
			parameter.setDerivedFlag(derived);
			parameter.setName(paramDoc.getString(GenotypePhenotypeDTO.PARAMETER_NAME));
			// we need to set is derived in the solr core!
			// pipeline core parameter_derived field
			parameter.setStableId(paramDoc.getString("" + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ""));
			if (paramDoc.containsKey(GenotypePhenotypeDTO.PROCEDURE_STABLE_KEY)) {
				parameter.setStableKey(Integer.parseInt(paramDoc.getString(GenotypePhenotypeDTO.PROCEDURE_STABLE_KEY)));
			}

		}
		return parameter;
	}


	/*
	 * End of method for PipelineSolrImpl
	 */

	/*
	 * Methods used by PhenotypeCallSummarySolrImpl
	 */
	public List<? extends StatisticalResult> getStatsResultFor(String accession, String parameterStableId, ObservationType observationType, String strainAccession, String alleleAccession)
	throws IOException, URISyntaxException {

		String solrUrl = solr.getBaseURL();// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
		solrUrl += "/select/?q=" + GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + accession + "\"" + "&fq=" + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":" + parameterStableId + "&fq=" + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + strainAccession + "\"" + "&fq=" + GenotypePhenotypeDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"&rows=10000000&version=2.2&start=0&indent=on&wt=json";
		System.out.println("solr url for stats results=" + solrUrl);
		List<? extends StatisticalResult> statisticalResult = this.createStatsResultFromSolr(solrUrl, observationType);
		return statisticalResult;
	}


	/**
	 * Returns a PhenotypeFacetResult object given a phenotyping center and a
	 * pipeline stable id
	 * 
	 * @param phenotypingCenter
	 *            a short name for a phenotyping center
	 * @param pipelineStableId
	 *            a stable pipeline id
	 * @return a PhenotypeFacetResult instance containing a list of
	 *         PhenotypeCallSummary objects.
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public PhenotypeFacetResult getPhenotypeFacetResultByPhenotypingCenterAndPipeline(String phenotypingCenter, String pipelineStableId)
	throws IOException, URISyntaxException {

		String solrUrl = solr.getBaseURL();// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
		System.out.println("SOLR URL = " + solrUrl);

		solrUrl += "/select/?q=" + GenotypePhenotypeDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"" + "&fq=" + GenotypePhenotypeDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId + "&facet=true" + "&facet.field=" + GenotypePhenotypeDTO.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeDTO.PROCEDURE_NAME + "&facet.field=" + GenotypePhenotypeDTO.MARKER_SYMBOL + "&facet.field=" + GenotypePhenotypeDTO.MP_TERM_NAME + "&sort=p_value%20asc" + "&rows=10000000&version=2.2&start=0&indent=on&wt=json";
		System.out.println("SOLR URL = " + solrUrl);
		return this.createPhenotypeResultFromSolrResponse(solrUrl);
	}


	public PhenotypeFacetResult getMPByGeneAccessionAndFilter(String accId, String queryString)
	throws IOException, URISyntaxException {

		String solrUrl = solr.getBaseURL() + "/select/?q=" + GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + accId + "\""
		// + "&fq=-" + GenotypePhenotypeDTO.RESOURCE_NAME + ":IMPC"
		+ "&rows=10000000&version=2.2&start=0&indent=on&wt=json&facet=true&facet.field=" + GenotypePhenotypeDTO.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME + "";
		if (queryString.startsWith("&")) {
			solrUrl += queryString;
		} else {// add an ampersand parameter splitter if not one as we need
			// one to add to the already present solr query string
			solrUrl += "&" + queryString;
		}
		solrUrl += "&sort=p_value%20asc";// sort by pValue by default so we get
											// most sig calls at top of tables
		return createPhenotypeResultFromSolrResponse(solrUrl);
	}


	public PhenotypeFacetResult getMPCallByMPAccessionAndFilter(String phenotype_id, String queryString)
	throws IOException, URISyntaxException {

		// http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype/select/?q=(mp_term_id:"MP:0002896"+OR+top_level_mp_term_id:"MP:0002896"+OR+intermediate_mp_term_id:"MP:0002896")&rows=1000000&version=2.2&start=0&indent=on&wt=json&facet=true&facet.field=resource_fullname&facet.field=procedure_name&facet.field=marker_symbol&facet.field=mp_term_name&
		String solrUrl = solr.getBaseURL() + "/select/?q=(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + phenotype_id + "\"+OR+" + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + phenotype_id + "\"+OR+" + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + phenotype_id + "\")"
		// + "&fq=-" + GenotypePhenotypeDTO.RESOURCE_NAME + ":IMPC" +
		+ "&rows=1000000&version=2.2&start=0&indent=on&wt=json&facet=true&facet.field=" + GenotypePhenotypeDTO.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeDTO.PROCEDURE_NAME + "&facet.field=" + GenotypePhenotypeDTO.MARKER_SYMBOL + "&facet.field=" + GenotypePhenotypeDTO.MP_TERM_NAME + "";
		// if (!filterString.equals("")) {
		if (queryString.startsWith("&")) {
			solrUrl += queryString;
		} else {// add an ampersand parameter splitter if not one as we need
			// one to add to the already present solr query string
			solrUrl += "&" + queryString;
		}
		solrUrl += "&sort=p_value%20asc";
		// }
		System.out.println("solr url for sorting pvalues=" + solrUrl);
		return createPhenotypeResultFromSolrResponse(solrUrl);

	}


	private List<? extends StatisticalResult> createStatsResultFromSolr(String url, ObservationType observationType)
	throws IOException, URISyntaxException {

		// need some way of determining what type of data and therefor what type
		// of stats result object to create default to unidimensional for now
		List<StatisticalResult> results = new ArrayList<>();
		// StatisticalResult statisticalResult=new StatisticalResult();

		JSONObject resultsj = null;
		resultsj = JSONRestUtil.getResults(url);
		JSONArray docs = resultsj.getJSONObject("response").getJSONArray("docs");

		if (observationType == ObservationType.unidimensional) {
			for (Object doc : docs) {
				UnidimensionalResult unidimensionalResult = new UnidimensionalResult();
				JSONObject phen = (JSONObject) doc;
				String pValue = phen.getString(GenotypePhenotypeDTO.P_VALUE);
				String sex = phen.getString(GenotypePhenotypeDTO.SEX);
				String zygosity = phen.getString(GenotypePhenotypeDTO.ZYGOSITY);
				String effectSize = phen.getString(GenotypePhenotypeDTO.EFFECT_SIZE);
				String phenoCallSummaryId = phen.getString(GenotypePhenotypeDTO.ID);

				// System.out.println("pValue="+pValue);
				if (pValue != null) {
					unidimensionalResult.setId(Integer.parseInt(phenoCallSummaryId));
					// one id for each document and for each sex
					unidimensionalResult.setpValue(Double.valueOf(pValue));
					unidimensionalResult.setZygosityType(ZygosityType.valueOf(zygosity));
					unidimensionalResult.setEffectSize(new Double(effectSize));
					unidimensionalResult.setSexType(SexType.valueOf(sex));
				}
				results.add(unidimensionalResult);
			}
			return results;
		}

		if (observationType == ObservationType.categorical) {

			for (Object doc : docs) {
				CategoricalResult catResult = new CategoricalResult();
				JSONObject phen = (JSONObject) doc;
				// System.out.println("pValue="+pValue);
				String pValue = phen.getString(GenotypePhenotypeDTO.P_VALUE);
				String sex = phen.getString(GenotypePhenotypeDTO.SEX);
				String zygosity = phen.getString(GenotypePhenotypeDTO.ZYGOSITY);
				String effectSize = phen.getString(GenotypePhenotypeDTO.EFFECT_SIZE);
				String phenoCallSummaryId = phen.getString(GenotypePhenotypeDTO.ID);

				// System.out.println("pValue="+pValue);
				// if(pValue!=null) {
				catResult.setId(Integer.parseInt(phenoCallSummaryId));
				// one id for each document and for each sex
				catResult.setpValue(Double.valueOf(pValue));
				catResult.setZygosityType(ZygosityType.valueOf(zygosity));
				catResult.setEffectSize(new Double(Double.valueOf(effectSize)));
				catResult.setSexType(SexType.valueOf(sex));
				// System.out.println("adding sex="+SexType.valueOf(sex));
				// }
				results.add(catResult);
			}
			return results;
		}
		return results;
	}


	private PhenotypeFacetResult createPhenotypeResultFromSolrResponse(String url)
	throws IOException, URISyntaxException {

		PhenotypeFacetResult facetResult = new PhenotypeFacetResult();
		List<PhenotypeCallSummary> list = new ArrayList<PhenotypeCallSummary>();

		JSONObject results = null;
		results = JSONRestUtil.getResults(url);

		JSONArray docs = results.getJSONObject("response").getJSONArray("docs");
		for (Object doc : docs) {
			JSONObject phen = (JSONObject) doc;
			String mpTerm = phen.getString(GenotypePhenotypeDTO.MP_TERM_NAME);
			String mpId = phen.getString(GenotypePhenotypeDTO.MP_TERM_ID);
			PhenotypeCallSummary sum = new PhenotypeCallSummary();
			OntologyTerm phenotypeTerm = new OntologyTerm();
			phenotypeTerm.setName(mpTerm);
			phenotypeTerm.setDescription(mpTerm);

			DatasourceEntityId mpEntity = new DatasourceEntityId();
			mpEntity.setAccession(mpId);
			phenotypeTerm.setId(mpEntity);
			sum.setPhenotypeTerm(phenotypeTerm);

			// check the top level categories
			JSONArray topLevelMpTermNames = phen.getJSONArray(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);
			JSONArray topLevelMpTermIDs = phen.getJSONArray(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID);
			List<OntologyTerm> topLevelPhenotypeTerms = new ArrayList<OntologyTerm>();

			for (int i = 0; i < topLevelMpTermNames.size(); i++) {
				OntologyTerm toplevelTerm = new OntologyTerm();
				toplevelTerm.setName(topLevelMpTermNames.getString(i));
				toplevelTerm.setDescription(topLevelMpTermNames.getString(i));
				DatasourceEntityId tlmpEntity = new DatasourceEntityId();
				tlmpEntity.setAccession(topLevelMpTermIDs.getString(i));
				toplevelTerm.setId(tlmpEntity);
				topLevelPhenotypeTerms.add(toplevelTerm);
			}
			sum.setTopLevelPhenotypeTerms(topLevelPhenotypeTerms);

			sum.setPhenotypingCenter(phen.getString(GenotypePhenotypeDTO.PHENOTYPING_CENTER));
			if (phen.containsKey(GenotypePhenotypeDTO.ALLELE_SYMBOL)) {
				Allele allele = new Allele();
				allele.setSymbol(phen.getString(GenotypePhenotypeDTO.ALLELE_SYMBOL));
				GenomicFeature alleleGene = new GenomicFeature();
				DatasourceEntityId alleleEntity = new DatasourceEntityId();
				alleleEntity.setAccession(phen.getString(GenotypePhenotypeDTO.ALLELE_ACCESSION_ID));
				allele.setId(alleleEntity);
				alleleGene.setId(alleleEntity);
				alleleGene.setSymbol(phen.getString(GenotypePhenotypeDTO.MARKER_SYMBOL));
				allele.setGene(alleleGene);
				sum.setAllele(allele);
			}
			if (phen.containsKey(GenotypePhenotypeDTO.MARKER_SYMBOL)) {
				GenomicFeature gf = new GenomicFeature();
				gf.setSymbol(phen.getString(GenotypePhenotypeDTO.MARKER_SYMBOL));
				DatasourceEntityId geneEntity = new DatasourceEntityId();
				geneEntity.setAccession(phen.getString(GenotypePhenotypeDTO.MARKER_ACCESSION_ID));
				gf.setId(geneEntity);
				sum.setGene(gf);
			}
			if (phen.containsKey(GenotypePhenotypeDTO.PHENOTYPING_CENTER)) {
				sum.setPhenotypingCenter(phen.getString(GenotypePhenotypeDTO.PHENOTYPING_CENTER));
			}
			// GenomicFeature gene=new GenomicFeature();
			// gene.
			// allele.setGene(gene);
			String zygosity = phen.getString(GenotypePhenotypeDTO.ZYGOSITY);
			ZygosityType zyg = ZygosityType.valueOf(zygosity);
			sum.setZygosity(zyg);
			String sex = phen.getString(GenotypePhenotypeDTO.SEX);
			SexType sexType = SexType.valueOf(sex);
			sum.setSex(sexType);
			String provider = phen.getString(GenotypePhenotypeDTO.RESOURCE_NAME);
			Datasource datasource = new Datasource();
			datasource.setName(provider);
			sum.setDatasource(datasource);

			// "parameter_stable_id":"557",
			// "parameter_name":"Bone Mineral Content",
			// "procedure_stable_id":"41",
			// "procedure_stable_key":"41",
			Parameter parameter = new Parameter();
			if (phen.containsKey(GenotypePhenotypeDTO.PARAMETER_STABLE_ID)) {
				parameter = pipelineDAO.getParameterByStableId(phen.getString(GenotypePhenotypeDTO.PARAMETER_STABLE_ID));
			} else {
				System.err.println("parameter_stable_id missing");
			}
			sum.setParameter(parameter);

			Pipeline pipeline = new Pipeline();
			if (phen.containsKey(GenotypePhenotypeDTO.PARAMETER_STABLE_ID)) {
				pipeline = pipelineDAO.getPhenotypePipelineByStableId(phen.getString(GenotypePhenotypeDTO.PIPELINE_STABLE_ID));
			} else {
				System.err.println("pipeline stable_id missing");
			}
			sum.setPipeline(pipeline);

			Project project = new Project();
			project.setName(phen.getString(GenotypePhenotypeDTO.PROJECT_NAME));
			project.setDescription(phen.getString(GenotypePhenotypeDTO.PROJECT_FULLNAME)); // is
																							// this
																							// right
																							// for
																							// description?
																							// no
																							// other
																							// field
																							// in
																							// solr
																							// index!!!
			if (phen.containsKey(GenotypePhenotypeDTO.PROJECT_EXTERNAL_ID)) {
				sum.setExternalId(phen.getInt(GenotypePhenotypeDTO.PROJECT_EXTERNAL_ID));
			}

			if (phen.containsKey(GenotypePhenotypeDTO.P_VALUE)) {
				sum.setpValue(new Float(phen.getString(GenotypePhenotypeDTO.P_VALUE)));
				// get the effect size too
				sum.setEffectSize(new Float(phen.getString(GenotypePhenotypeDTO.EFFECT_SIZE)));
			}

			sum.setProject(project);
			// "procedure_stable_id":"77",
			// "procedure_stable_key":"77",
			// "procedure_name":"Plasma Chemistry",
			Procedure procedure = new Procedure();
			if (phen.containsKey(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID)) {
				procedure.setStableId(phen.getString(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID));
				procedure.setStableKey(Integer.valueOf(phen.getString(GenotypePhenotypeDTO.PROCEDURE_STABLE_KEY)));
				procedure.setName(phen.getString(GenotypePhenotypeDTO.PROCEDURE_NAME));
				sum.setProcedure(procedure);
			} else {
				System.err.println("procedure_stable_id");
			}
			list.add(sum);
		}

		// if (!filterString.equals("")) {//only run facet code if there is a
		// facet in the query!!
		// get the facet information that we can use to create the buttons /
		// dropdowns/ checkboxes
		JSONObject facets = results.getJSONObject("facet_counts").getJSONObject("facet_fields");
		// System.out.println("\n\nFacet count : " +
		// results.getJSONObject("facet_counts")
		// .getJSONObject("facet_fields") );
		Iterator<String> ite = facets.keys();
		Map<String, Map<String, Integer>> dropdowns = new HashMap<String, Map<String, Integer>>();
		while (ite.hasNext()) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			String key = (String) ite.next();
			JSONArray array = (JSONArray) facets.get(key);
			int i = 0;
			while (i + 1 < array.size()) {
				String facetString = array.get(i).toString();
				int number = array.getInt(i + 1);
				if (number != 0) {// only add if some counts to filter on!
					map.put(facetString, number);
				}
				i += 2;
				// System.out.println("i="+i);
			}
			dropdowns.put(key, map);
		}
		facetResult.setFacetResults(dropdowns);

		// }
		facetResult.setPhenotypeCallSummaries(list);
		return facetResult;
	}


	/*
	 * End of method for PhenotypeCallSummarySolrImpl
	 */
	public GeneRowForHeatMap getResultsForGeneHeatMap(String accession, GenomicFeature gene, List<BasicBean> xAxisBeans, Map<String, List<String>> geneToTopLevelMpMap) {

		GeneRowForHeatMap row = new GeneRowForHeatMap(accession);
		if (gene != null) {
			row.setSymbol(gene.getSymbol());
		} else {
			System.err.println("error no symbol for gene " + accession);
		}

		Map<String, HeatMapCell> xAxisToCellMap = new HashMap<>();
		for (BasicBean xAxisBean : xAxisBeans) {
			HeatMapCell cell = new HeatMapCell();
			if (geneToTopLevelMpMap.containsKey(accession)) {

				List<String> mps = geneToTopLevelMpMap.get(accession);
				// cell.setLabel("No Phenotype Detected");
				if (mps != null && !mps.isEmpty()) {
					if (mps.contains(xAxisBean.getId())) {
						cell.setxAxisKey(xAxisBean.getId());
						cell.setLabel("Data Available");
						cell.setStatus("Data Available");
					} else {
						cell.setStatus("No MP");
					}
				} else {
					// System.err.println("mps are null or empty");
					cell.setStatus("No MP");
				}
			} else {
				// if no doc found for the gene then no data available
				cell.setStatus("No Data Available");
				// System.err.println("!!!!!!!!!!!!!!NO top level found for gene");
			}
			xAxisToCellMap.put(xAxisBean.getId(), cell);
		}
		row.setXAxisToCellMap(xAxisToCellMap);

		return row;
	}
}
