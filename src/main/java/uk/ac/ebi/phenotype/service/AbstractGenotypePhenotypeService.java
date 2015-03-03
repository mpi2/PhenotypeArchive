package uk.ac.ebi.phenotype.service;

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
import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.analytics.bean.AggregateCountXYBean;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.*;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.web.controller.OverviewChartsController;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;
import uk.ac.ebi.phenotype.web.pojo.HeatMapCell;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import org.apache.solr.client.solrj.SolrServer;

public abstract class AbstractGenotypePhenotypeService extends BasicService {

    protected PhenotypePipelineDAO pipelineDAO;

    protected HttpSolrServer solr;

    protected Boolean isPreQc;

    /**
     * @param zygosity - optional (pass null if not needed)
     * @return Map <String, Long> : <top_level_mp_name, number_of_annotations>
     * @author tudose
     */
    public TreeMap<String, Long> getDistributionOfAnnotationsByMPTopLevel(ZygosityType zygosity, String resourceName) {

        SolrQuery query = new SolrQuery();

        if (zygosity != null) {
            query.setQuery(GenotypePhenotypeDTO.ZYGOSITY + ":" + zygosity.getName());
        } else if (resourceName != null){
            query.setFilterQueries(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + resourceName);
        }else {
            query.setQuery("*:*");
        }

        query.setFacet(true);
        query.setFacetLimit(-1);
        query.setFacetMinCount(1);
        query.setRows(0);
        query.addFacetField(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);

        try {
            QueryResponse response = solr.query(query);
            TreeMap<String, Long> res = new TreeMap<>();
            res.putAll(getFacets(response).get(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME));
            return res;
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<AggregateCountXYBean> getAggregateCountXYBean(TreeMap<String, TreeMap<String, Long>> map) {
        List<AggregateCountXYBean> res = new ArrayList<>();

        for (String category : map.navigableKeySet()) {
            for (String bin : map.get(category).navigableKeySet()) {
                AggregateCountXYBean bean = new AggregateCountXYBean(map.get(category).get(bin).intValue(), bin, bin, "xAttribute", category, category, "yAttribute");
                res.add(bean);
            }
        }
        return res;
    }

    /**
     * Returns a list of a all colonies
     *
     * @param phenotypeResourceName
     * @return
     * @throws SolrServerException
     */
    public List<GenotypePhenotypeDTO> getAllMPByPhenotypingCenterAndColonies(String phenotypeResourceName)
            throws SolrServerException {

        List<String> fields = Arrays.asList(GenotypePhenotypeDTO.PHENOTYPING_CENTER, GenotypePhenotypeDTO.MP_TERM_ID,
                                            GenotypePhenotypeDTO.MP_TERM_NAME, GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID, GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME,
                                            GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID, GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_NAME,
                                            GenotypePhenotypeDTO.COLONY_ID, GenotypePhenotypeDTO.MARKER_SYMBOL, GenotypePhenotypeDTO.MARKER_ACCESSION_ID);

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + phenotypeResourceName)
                .setRows(MAX_NB_DOCS)
                .setFields(StringUtils.join(fields, ","));

        QueryResponse response = solr.query(query);
        return response.getBeans(GenotypePhenotypeDTO.class);

    }

    /**
     *
     * @param mpId
     * @return List of parameters that led to at least one association to the
     * given parameter or some class in its subtree
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

    public List<Group> getGenesBy(String mpId, String sex, boolean onlyB6N)
            throws SolrServerException {

        // males only
        SolrQuery q = new SolrQuery().setQuery("(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " + 
        	GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\" OR " + 
        	GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + mpId + "\")");
        if (onlyB6N){
        	q.setFilterQueries("(" + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsController.OVERVIEW_STRAINS, "\" OR " + 
        	GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"") + "\")");
        }
        q.setRows(10000000);
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

        List<String> res = new ArrayList();
        SolrQuery query = new SolrQuery().setQuery("(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + phenotype_id + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\""
                + phenotype_id + "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + phenotype_id + "\") AND ("
                + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsController.OVERVIEW_STRAINS, "\" OR " + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"") + "\") AND "
                + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":\"" + parameterStableId + "\"").setRows(100000000);
        query.set("group.field", GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        query.set("group", true);
        List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
        for (Group gr : groups) {
            if ( ! res.contains((String) gr.getGroupValue())) {
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
     * phenotype associations.
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
     * associations.
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
        HashSet<String> allPhenotypes = new HashSet<String>();
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
        HashSet<String> allTopLevelPhenotypes = new HashSet<String>();
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
        HashSet<String> allIntermediateLevelPhenotypes = new HashSet<String>();
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
    public SolrDocumentList getPhenotypesForTopLevelTerm(String gene, String mpID, ZygosityType zygosity)
            throws SolrServerException {

        String query;
        if (gene.equalsIgnoreCase("*")) {
            query = GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":" + gene + " AND ";
        } else {
            query = GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\" AND ";

        }

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpID + "\"");
        solrQuery.setRows(1000000);
        if (zygosity != null) {
            solrQuery.setFilterQueries(GenotypePhenotypeDTO.ZYGOSITY + ":" + zygosity.getName());
        }
        SolrDocumentList result = solr.query(solrQuery).getResults();
        // mpID might be in mp_id instead of top level field
        if (result.size() == 0 || result == null) // result = runQuery("marker_accession_id:" + gene.replace(":",
        // "\\:") + " AND mp_term_id:" + mpID.replace(":", "\\:"));
        {
            result = runQuery(query + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpID + "\"");// AND
        }		// -" + GenotypePhenotypeDTO.RESOURCE_NAME + ":IMPC");
        return result;
    }

    public SolrDocumentList getPhenotypes(String gene)
            throws SolrServerException {

        SolrDocumentList result = runQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\"");
        return result;
    }

    public List<GenotypePhenotypeDTO> getPhenotypeDTOs(String gene) throws SolrServerException {
        SolrQuery query = new SolrQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\"")
                .setRows(Integer.MAX_VALUE);

        return solr.query(query).getBeans(GenotypePhenotypeDTO.class);

    }

    private SolrDocumentList runQuery(String q)
            throws SolrServerException {

        SolrQuery solrQuery = new SolrQuery().setQuery(q);
        solrQuery.setRows(1000000);
        QueryResponse rsp = null;
        rsp = solr.query(solrQuery);
        return rsp.getResults();
    }

    public HashMap<String, String> getTopLevelMPTerms(String gene, ZygosityType zyg)
            throws SolrServerException {

        HashMap<String, String> tl = new HashMap<String, String>();

        SolrQuery query = new SolrQuery();
        if (gene.equalsIgnoreCase("*")) {
            query.setQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":" + gene);
        } else {
            query.setQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\"");
        }
        query.setRows(10000000);
        if (zyg != null) {
            query.setFilterQueries(GenotypePhenotypeDTO.ZYGOSITY + ":" + zyg.getName());
        }

        SolrDocumentList result = solr.query(query).getResults();

        if (result.size() > 0) {
            for (int i = 0; i < result.size(); i ++) {
                SolrDocument doc = result.get(i);
                if (doc.getFieldValue(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID) != null) {
                    ArrayList<String> tlTermIDs = (ArrayList<String>) doc.getFieldValue(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID);
                    ArrayList<String> tlTermNames = (ArrayList<String>) doc.getFieldValue(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);
                    int len = tlTermIDs.size();
                    for (int k = 0; k < len; k ++) {
                        tl.put(tlTermIDs.get(k), tlTermNames.get(k));
                    }
                } else {// it seems that when the term id is a top level term
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
//		System.out.println("solr url for stats results=" + solrUrl);
        List<? extends StatisticalResult> statisticalResult = this.createStatsResultFromSolr(solrUrl, observationType);
        return statisticalResult;
    }

    /**
     * Returns a PhenotypeFacetResult object given a list of genes
     *
     * @param genomicFeatures list of marker accession
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public PhenotypeFacetResult getPhenotypeFacetResultByGenomicFeatures(Set<String> genomicFeatures)
            throws IOException, URISyntaxException {

        String solrUrl = solr.getBaseURL();
        // build OR query from a list of genes (assuming they have MGI ids
        StringBuilder geneClause = new StringBuilder(genomicFeatures.size() * 15);
        boolean start = true;
        for (String genomicFeatureAcc : genomicFeatures) {
            geneClause.append((start) ? genomicFeatureAcc : "\" OR \"" + genomicFeatureAcc);
            start = false;
        }

        solrUrl += "/select/?q=" + GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":(\"" + geneClause.toString() + "\")" + "&facet=true" + "&facet.field=" + GenotypePhenotypeDTO.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeDTO.PROCEDURE_NAME + "&facet.field=" + GenotypePhenotypeDTO.MARKER_SYMBOL + "&facet.field=" + GenotypePhenotypeDTO.MP_TERM_NAME + "&sort=p_value%20asc" + "&rows=10000000&version=2.2&start=0&indent=on&wt=json";
//		System.out.println("\n\n\n SOLR URL = " + solrUrl);
        return this.createPhenotypeResultFromSolrResponse(solrUrl, isPreQc);
    }

    /**
     * Returns a PhenotypeFacetResult object given a phenotyping center and a
     * pipeline stable id
     *
     * @param phenotypingCenter a short name for a phenotyping center
     * @param pipelineStableId a stable pipeline id
     * @return a PhenotypeFacetResult instance containing a list of
     * PhenotypeCallSummary objects.
     * @throws IOException
     * @throws URISyntaxException
     */
    public PhenotypeFacetResult getPhenotypeFacetResultByPhenotypingCenterAndPipeline(String phenotypingCenter, String pipelineStableId)
            throws IOException, URISyntaxException {

        String solrUrl = solr.getBaseURL();// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
//		System.out.println("SOLR URL = " + solrUrl);

        solrUrl += "/select/?q=" + GenotypePhenotypeDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"" + "&fq=" + GenotypePhenotypeDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId + "&facet=true" + "&facet.field=" + GenotypePhenotypeDTO.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeDTO.PROCEDURE_NAME + "&facet.field=" + GenotypePhenotypeDTO.MARKER_SYMBOL + "&facet.field=" + GenotypePhenotypeDTO.MP_TERM_NAME + "&sort=p_value%20asc" + "&rows=10000000&version=2.2&start=0&indent=on&wt=json";
//		System.out.println("SOLR URL = " + solrUrl);
        return this.createPhenotypeResultFromSolrResponse(solrUrl, isPreQc);
    }

    public PhenotypeFacetResult getMPByGeneAccessionAndFilter(String accId, String queryString)
            throws IOException, URISyntaxException {

        String solrUrl = solr.getBaseURL() + "/select/?q=" + GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + accId + "\""
                + "&rows=10000000&version=2.2&start=0&indent=on&wt=json&facet.mincount=1&facet=true&facet.field=" + GenotypePhenotypeDTO.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME + "";
        if (queryString.startsWith("&")) {
            solrUrl += queryString;
        } else {// add an ampersand parameter splitter if not one as we need
            // one to add to the already present solr query string
            solrUrl += "&" + queryString;
        }
        solrUrl += "&sort=p_value%20asc";
        return createPhenotypeResultFromSolrResponse(solrUrl, isPreQc);
    }

    public PhenotypeFacetResult getMPCallByMPAccessionAndFilter(String phenotype_id, String queryString)
            throws IOException, URISyntaxException {

        String solrUrl = solr.getBaseURL() + "/select/?q=(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + phenotype_id + "\"+OR+" + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + phenotype_id + "\"+OR+" + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + phenotype_id + "\")"
                + "&rows=1000000&version=2.2&start=0&indent=on&wt=json&facet.mincount=1&facet=true&facet.field=" + GenotypePhenotypeDTO.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeDTO.PROCEDURE_NAME + "&facet.field=" + GenotypePhenotypeDTO.MARKER_SYMBOL + "&facet.field=" + GenotypePhenotypeDTO.MP_TERM_NAME + "";
        if (queryString.startsWith("&")) {
            solrUrl += queryString;
        } else {// add an ampersand parameter splitter if not one as we need
            // one to add to the already present solr query string
            solrUrl += "&" + queryString;
        }
        solrUrl += "&sort=p_value%20asc";
        return createPhenotypeResultFromSolrResponse(solrUrl, isPreQc);

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

    public PhenotypeFacetResult createPhenotypeResultFromSolrResponse(String url, Boolean isPreQc)
            throws IOException, URISyntaxException {

        PhenotypeFacetResult facetResult = new PhenotypeFacetResult();
        List<PhenotypeCallSummary> list = new ArrayList<PhenotypeCallSummary>();

        JSONObject results = new JSONObject();
        results = JSONRestUtil.getResults(url);

        JSONArray docs = results.getJSONObject("response").getJSONArray("docs");
        for (Object doc : docs) {

            list.add(createSummaryCall(doc, isPreQc));
        }

		// get the facet information that we can use to create the buttons /
        // dropdowns/ checkboxes
        JSONObject facets = results.getJSONObject("facet_counts").getJSONObject("facet_fields");

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

    public PhenotypeCallSummary createSummaryCall(Object doc, Boolean preQc) {
        JSONObject phen = (JSONObject) doc;
        JSONArray topLevelMpTermNames;
        JSONArray topLevelMpTermIDs;
        String mpTerm = phen.getString(GenotypePhenotypeDTO.MP_TERM_NAME);
        String mpId = phen.getString(GenotypePhenotypeDTO.MP_TERM_ID);
        PhenotypeCallSummary sum = new PhenotypeCallSummary();
        OntologyTerm phenotypeTerm = new OntologyTerm();
        DatasourceEntityId mpEntity = new DatasourceEntityId();

        mpEntity.setAccession(mpId);
        phenotypeTerm.setId(mpEntity);
        phenotypeTerm.setName(mpTerm);
        phenotypeTerm.setDescription(mpTerm);
        sum.setPhenotypeTerm(phenotypeTerm);

		// Set the Gid field required for linking to phenoview, which is stored in the
        // datafile in the external id field
        if (phen.containsKey(GenotypePhenotypeDTO.EXTERNAL_ID)) {
            sum.setgId(phen.getString(GenotypePhenotypeDTO.EXTERNAL_ID));
        }

        sum.setPreQC(preQc);

        // check the top level categories
        if (phen.containsKey(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID)) {
            topLevelMpTermNames = phen.getJSONArray(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);
            topLevelMpTermIDs = phen.getJSONArray(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID);
        } else { // a top level term is directly associated
            topLevelMpTermNames = new JSONArray();
            topLevelMpTermNames.add(phen.getString(GenotypePhenotypeDTO.MP_TERM_NAME));
            topLevelMpTermIDs = new JSONArray();
            topLevelMpTermIDs.add(phen.getString(GenotypePhenotypeDTO.MP_TERM_ID));
        }
        List<OntologyTerm> topLevelPhenotypeTerms = new ArrayList<OntologyTerm>();

        for (int i = 0; i < topLevelMpTermNames.size(); i ++) {
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
//TODO remove comment out//		project.setDescription(phen.getString(GenotypePhenotypeDTO.PROJECT_FULLNAME)); 
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
//TODO remove comment out	//		procedure.setStableKey(Integer.valueOf(phen.getString(GenotypePhenotypeDTO.PROCEDURE_STABLE_KEY)));
            procedure.setName(phen.getString(GenotypePhenotypeDTO.PROCEDURE_NAME));
            sum.setProcedure(procedure);
        } else {
            System.err.println("procedure_stable_id");
        }
        return sum;
    }

    /**
     *
     * @return map <colony_id, occurences>
     */
    public HashMap<String, Long> getAssociationsDistribution(String mpTermName, String resource) {

        String query = GenotypePhenotypeDTO.MP_TERM_NAME + ":\"" + mpTermName + "\"";
        if (resource != null) {
            query += " AND " + GenotypePhenotypeDTO.RESOURCE_NAME + ":" + resource;
        }

        SolrQuery q = new SolrQuery();
        q.setQuery(query);
        q.setFacet(true);
        q.setFacetMinCount(1);
        q.addFacetField(GenotypePhenotypeDTO.COLONY_ID);

        try {
            return (getFacets(solr.query(q))).get(GenotypePhenotypeDTO.COLONY_ID);
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Set<String> getFertilityAssociatedMps() {

        SolrQuery q = new SolrQuery();
        q.setQuery(GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":*_FER_*");
        q.setFacet(true);
        q.setFacetMinCount(1);
        q.addFacetField(GenotypePhenotypeDTO.MP_TERM_NAME);

        try {
            return (getFacets(solr.query(q))).get(GenotypePhenotypeDTO.MP_TERM_NAME).keySet();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        return null;
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
                if (mps != null &&  ! mps.isEmpty()) {
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
            }
            xAxisToCellMap.put(xAxisBean.getId(), cell);
        }
        row.setXAxisToCellMap(xAxisToCellMap);

        return row;
    }

    public SolrServer getSolrServer() {
        return solr;
    }
}
