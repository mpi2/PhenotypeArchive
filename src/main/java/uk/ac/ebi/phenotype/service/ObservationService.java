package uk.ac.ebi.phenotype.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.NamedList;
import org.eclipse.jetty.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.stats.ObservationDTO;
import uk.ac.ebi.phenotype.stats.StackedBarsData;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalDataObject;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalSet;
import uk.ac.ebi.phenotype.web.controller.ChartsController;

@Service
public class ObservationService {

    @Autowired
    PhenotypePipelineDAO parameterDAO;

    private static final Logger LOG = LoggerFactory.getLogger(ObservationService.class);
    // Definition of the solr fields
    public static final class ExperimentField {

        public final static String ID = "id";
        public final static String PHENOTYPING_CENTER = "phenotyping_center";
        public final static String PHENOTYPING_CENTER_ID = "phenotyping_center_id";
        public final static String GENE_ACCESSION = "gene_accession_id";
        public final static String GENE_SYMBOL = "gene_symbol";
        public final static String ALLELE_ACCESSION = "allele_accession_id";
        public final static String ALLELE_SYMBOL = "allele_symbol";
        public final static String ZYGOSITY = "zygosity";
        public final static String SEX = "sex";
        public final static String BIOLOGICAL_MODEL_ID = "biological_model_id";
        public final static String BIOLOGICAL_SAMPLE_ID = "biological_sample_id";
        public final static String BIOLOGICAL_SAMPLE_GROUP = "biological_sample_group";
        public final static String STRAIN = "strain";
        public final static String PIPELINE_NAME = "pipeline_name";
        public final static String PIPELINE_ID = "pipeline_id";
        public final static String PIPELINE_STABLE_ID = "pipeline_stable_id";
        public final static String PROCEDURE_ID = "procedure_id";
        public final static String PROCEDURE_NAME = "procedure_name";
        public final static String PROCEDURE_STABLE_ID = "procedure_stable_id";
        public final static String PARAMETER_ID = "parameter_id";
        public final static String PARAMETER_NAME = "parameter_name";
        public final static String PARAMETER_STABLE_ID = "parameter_stable_id";
        public final static String EXPERIMENT_ID = "experiment_id";
        public final static String EXPERIMENT_SOURCE_ID = "experiment_source_id";
        public final static String OBSERVATION_TYPE = "observation_type";
        public final static String COLONY_ID = "colony_id";
        public final static String DATE_OF_BIRTH = "date_of_birth";
        public final static String DATE_OF_EXPERIMENT = "date_of_experiment";
        public final static String POPULATION_ID = "population_id";
        public final static String EXTERNAL_SAMPLE_ID = "external_sample_id";
        public final static String DATA_POINT = "data_point";
        public final static String ORDER_INDEX = "order_index";
        public final static String DIMENSION = "dimension";
        public final static String TIME_POINT = "time_point";
        public final static String DISCRETE_POINT = "discrete_point";
        public final static String CATEGORY = "category";
        public final static String VALUE = "value";
        public final static String METADATA = "metadata";
        public final static String METADATA_GROUP = "metadata_group";
    }

    private final HttpSolrServer solr;

    public ObservationService() {
        String solrURL = "http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment"; //default
        solr = new HttpSolrServer(solrURL);
    }

    public ObservationService(String solrUrl) {
        solr = new HttpSolrServer(solrUrl);
    }

    
	public Map<String, List<String>> getExperimentKeys(String mgiAccession,
			String parameterStableId, List<String> pipelineStableId,
			List<String> phenotypingCenterParams, 
			List<String> strainParams,
			List<String> metaDataGroups, List<String> alleleAccessions) throws SolrServerException {

		// Example of key
		// String experimentKey = observation.getPhenotypingCenter()
		// + observation.getStrain()
		// + observation.getParameterStableId()
		// + observation.getGeneAccession()
		// + observation.getMetadataGroup();

		Map<String, List<String>> map = new LinkedHashMap<>();

		SolrQuery query = new SolrQuery();

		query.setQuery(ExperimentField.GENE_ACCESSION + ":\"" + mgiAccession + "\"")
			.addFilterQuery(ExperimentField.PARAMETER_STABLE_ID + ":" + parameterStableId)
			.addFacetField(ExperimentField.PHENOTYPING_CENTER)
			.addFacetField(ExperimentField.STRAIN)
			.addFacetField(ExperimentField.METADATA_GROUP)
			.addFacetField(ExperimentField.PIPELINE_STABLE_ID)
                        .addFacetField(ExperimentField.ALLELE_ACCESSION)
			.setRows(0)
			.setFacet(true)
			.setFacetMinCount(1)
			.setFacetLimit(-1).setFacetSort(FacetParams.FACET_SORT_COUNT);

		if (phenotypingCenterParams!= null && !phenotypingCenterParams.isEmpty()){
			List<String>spaceSafeStringsList=new ArrayList<String>();//need to add " to ends of entries to cope with MRC Harwell with space in!
			for(String pCenter: phenotypingCenterParams) {
				if(!pCenter.endsWith("\"") && !pCenter.startsWith("\"")) {//check we haven't got speech marks already
				spaceSafeStringsList.add("\""+pCenter+"\"");
				}
			}
			query.addFilterQuery(ExperimentField.PHENOTYPING_CENTER + ":(" + StringUtils.join(spaceSafeStringsList, " OR ") + ")");
		}

		if (strainParams!= null && !strainParams.isEmpty()){
			query.addFilterQuery(ExperimentField.STRAIN + ":(" + StringUtils.join(strainParams, " OR ").replace(":", "\\:") + ")");
		}

		if (metaDataGroups!= null && !metaDataGroups.isEmpty()){
			query.addFilterQuery(ExperimentField.METADATA_GROUP + ":(" + StringUtils.join(metaDataGroups, " OR ") + ")");
		}

		if (pipelineStableId!= null && !pipelineStableId.isEmpty()){
			query.addFilterQuery(ExperimentField.PIPELINE_STABLE_ID + ":(" + StringUtils.join(pipelineStableId, " OR ") + ")");
		}
                
                if(alleleAccessions!=null && !alleleAccessions.isEmpty()){
                    String alleleFilter=ExperimentField.ALLELE_ACCESSION + ":(" + StringUtils.join(alleleAccessions, " OR ").replace(":", "\\:") + ")";
                    LOG.debug("alleleFilter="+alleleFilter);
                        query.addFilterQuery(alleleFilter);
                    
                }

		QueryResponse response = solr.query(query);
		LOG.debug("experiment key query=" + query);
		List<FacetField> fflist = response.getFacetFields();

		for (FacetField ff : fflist) {

			// If there are no face results, the values will be null
			// skip this facet field in that case
//			if (ff.getValues() == null) {
//				continue;
//			}

			for (Count count : ff.getValues()) {
				if (map.containsKey(ff.getName())) {
					map.get(ff.getName()).add(count.getName());
				} else {
					List<String> newList = new ArrayList<String>();
					newList.add(count.getName());
					map.put(ff.getName(), newList);
				}

			}
		}

		LOG.info("experimentKeys=" + map);
		return map;
	}

    
    /**
     * for testing - not for users
     *
     * @param start
     * @param length
     * @param type
     * @param parameterIds
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws SQLException
     */
    public List<Map<String, String>> getLinksListForStats(Integer start, Integer length, ObservationType type, List<String> parameterIds) throws IOException, URISyntaxException, SQLException {
        if (start == null) {
            start = 0;
        }
        if (length == null) {
            length = 100;
        }

        String url = solr.getBaseURL() + "/select?"
                + "q=" + ObservationService.ExperimentField.OBSERVATION_TYPE + ":" + type
                + " AND " + ObservationService.ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental"
                + "&wt=json&indent=true&start=" + start + "&rows=" + length;

        net.sf.json.JSONObject result = JSONRestUtil.getResults(url);
        JSONArray resultsArray = JSONRestUtil.getDocArray(result);

        List<Map<String, String>> listWithStableId = new ArrayList<>();
        for (int i = 0; i < resultsArray.size(); i++) {
            Map<String, String> map = new HashMap<>();
            net.sf.json.JSONObject exp = resultsArray.getJSONObject(i);
            String statbleParamId = exp.getString(ObservationService.ExperimentField.PARAMETER_STABLE_ID);
            String accession = exp.getString(ObservationService.ExperimentField.GENE_ACCESSION);
            map.put("paramStableId", statbleParamId);
            map.put("accession", accession);
            listWithStableId.add(map);
        }
        return listWithStableId;
    }


	
    /**
     * construct a query to get all observations for a given combination of
     * pipeline, parameter, gene, zygosity, organisation and strain
     * 
     * @param parameterId
     * @param geneAcc
     * @param zygosity
     * @param organisationId
     * @param strain
     * @param sex
     * @return
     * @throws SolrServerException
     */
    public SolrQuery getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(
            Integer parameterId, String geneAcc, String zygosity,
            Integer organisationId, String strain, String sex)
            throws SolrServerException {

        return new SolrQuery()
                .setQuery(
                        "((" + ExperimentField.GENE_ACCESSION + ":"
                                + geneAcc.replace(":", "\\:") + " AND "
                                + ExperimentField.ZYGOSITY + ":" + zygosity
                                + ") OR "
                                + ExperimentField.BIOLOGICAL_SAMPLE_GROUP
                                + ":control) ")
                .addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
                .addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
                .addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
                .addFilterQuery(ExperimentField.SEX + ":" + sex).setStart(0)
                .setRows(10000);
    }

    public String getQueryStringByParameterGeneAccZygosityOrganisationStrainSex(
            Integer parameterId, String geneAcc,
            String zygosity, Integer organisationId, String strain, SexType sex)
            throws SolrServerException {

        return getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(parameterId, geneAcc, zygosity, organisationId, strain, sex.name()).toString();

    }

    public List<ObservationDTO> getObservationsByParameterGeneAccZygosityOrganisationStrainSex(
            Integer parameterId, String gene,
            String zygosity, Integer organisationId, String strain, SexType sex)
            throws SolrServerException {

        SolrQuery query = getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(parameterId, gene, zygosity, organisationId, strain, sex.name());

        return solr.query(query).getBeans(ObservationDTO.class);

    }

    /**
     * Unwrap results from a facet pivot solr query and return the flattened
     * list of maps of results
     * 
     * @param response
     *            list of maps
     * @return
     */
    public List<Map<String, String>> getFacetPivotResults(QueryResponse response) {
        List<Map<String, String>> results = new LinkedList<Map<String, String>>();
        NamedList<List<PivotField>> facetPivot = response.getFacetPivot();

        if (facetPivot != null && facetPivot.size() > 0) {
            for (int i = 0; i < facetPivot.size(); i++) {

                String name = facetPivot.getName(i); // in this case only one of
                                                     // them
                LOG.debug("facetPivot name" + name);
                List<PivotField> pivotResult = facetPivot.get(name);

                // iterate on results
                for (int j = 0; j < pivotResult.size(); j++) {

                    // create a HashMap to store a new triplet of data

                    PivotField pivotLevel = pivotResult.get(j);
                    List<Map<String, String>> lmap = getLeveledFacetPivotValue(pivotLevel, null);
                    results.addAll(lmap);
                }
            }
        }

        return results;
    }

    /**
     * Return a list of a all data candidates for deletion prior to statistical
     * analysis
     * 
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctOrganisaionPipelineParameter() throws SolrServerException {

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .setRows(0)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1)
                .addFacetPivotField( // needs at least 2 fields
                ExperimentField.PHENOTYPING_CENTER_ID + "," +
                        ExperimentField.PIPELINE_ID + "," +
                        ExperimentField.PARAMETER_ID);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response);
    }

    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for a specific procedure
     * 
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByProcedure(String procedureStableId) throws SolrServerException {

        SolrQuery query = new SolrQuery()
                .setQuery(ExperimentField.PROCEDURE_STABLE_ID + ":" + procedureStableId)
                .addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ExperimentField.OBSERVATION_TYPE + ":unidimensional")
                .setRows(0)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1)
                .addFacetPivotField( // needs at least 2 fields
                ExperimentField.PHENOTYPING_CENTER_ID + "," +
                        ExperimentField.PIPELINE_ID + "," +
                        ExperimentField.PARAMETER_ID + "," +
                        ExperimentField.STRAIN + "," +
                        ExperimentField.ZYGOSITY + "," +
                        ExperimentField.METADATA_GROUP + "," +
                        ExperimentField.ALLELE_ACCESSION + "," +
                        ExperimentField.GENE_ACCESSION);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response);

    }

    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for all specified procedures
     * 
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByProcedure(List<String> procedureStableIds) throws SolrServerException {

        // Build the SOLR query string
        String field = ExperimentField.PROCEDURE_STABLE_ID;
        String q = (procedureStableIds.size() > 1) ?
                "(" + field + ":\"" + StringUtils.join(procedureStableIds.toArray(), "\" OR " + field + ":\"") + "\")" :
                field + ":\"" + procedureStableIds.get(0) + "\"";

        SolrQuery query = new SolrQuery()
                .setQuery(q)
                .addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ExperimentField.OBSERVATION_TYPE + ":unidimensional")
                .setRows(0)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1)
                .addFacetPivotField( // needs at least 2 fields
                ExperimentField.PHENOTYPING_CENTER_ID + "," +
                        ExperimentField.PIPELINE_ID + "," +
                        ExperimentField.PARAMETER_ID + "," +
                        ExperimentField.STRAIN + "," +
                        ExperimentField.ZYGOSITY + "," +
                        ExperimentField.METADATA_GROUP + "," +
                        ExperimentField.ALLELE_ACCESSION + "," +
                        ExperimentField.GENE_ACCESSION);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response);

    }

    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for all specified parameter
     * 
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByParameter(List<String> parameterStableIds) throws SolrServerException {

        // Build the SOLR query string
        String field = ExperimentField.PARAMETER_STABLE_ID;
        String q = (parameterStableIds.size() > 1) ?
                "(" + field + ":\"" + StringUtils.join(parameterStableIds.toArray(), "\" OR " + field + ":\"") + "\")" :
                field + ":\"" + parameterStableIds.get(0) + "\"";

        SolrQuery query = new SolrQuery()
                .setQuery(q)
                .addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ExperimentField.OBSERVATION_TYPE + ":unidimensional")
                .setRows(0)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1)
                .addFacetPivotField( // needs at least 2 fields
                ExperimentField.PHENOTYPING_CENTER_ID + "," +
                        ExperimentField.PIPELINE_ID + "," +
                        ExperimentField.PARAMETER_ID + "," +
                        ExperimentField.STRAIN + "," +
                        ExperimentField.ZYGOSITY + "," +
                        ExperimentField.METADATA_GROUP + "," +
                        ExperimentField.ALLELE_ACCESSION + "," +
                        ExperimentField.GENE_ACCESSION);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response);

    }

    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for a specific parameter
     * 
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByParameter(String parameterStableId) throws SolrServerException {

        SolrQuery query = new SolrQuery()
                .setQuery(ExperimentField.PARAMETER_STABLE_ID + ":" + parameterStableId)
                .addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ExperimentField.OBSERVATION_TYPE + ":unidimensional")
                .setRows(0)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1)
                .addFacetPivotField( // needs at least 2 fields
                ExperimentField.PHENOTYPING_CENTER_ID + "," +
                        ExperimentField.PIPELINE_ID + "," +
                        ExperimentField.PARAMETER_ID + "," +
                        ExperimentField.STRAIN + "," +
                        ExperimentField.ZYGOSITY + "," +
                        ExperimentField.METADATA_GROUP + "," +
                        ExperimentField.ALLELE_ACCESSION + "," +
                        ExperimentField.GENE_ACCESSION);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response);

    }

    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis
     * 
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadata() throws SolrServerException {

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ExperimentField.OBSERVATION_TYPE + ":unidimensional")
                .setRows(0)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1)
                .addFacetPivotField( // needs at least 2 fields
                ExperimentField.PHENOTYPING_CENTER_ID + "," +
                        ExperimentField.PIPELINE_ID + "," +
                        ExperimentField.PARAMETER_ID + "," +
                        ExperimentField.STRAIN + "," +
                        ExperimentField.ZYGOSITY + "," +
                        ExperimentField.METADATA_GROUP + "," +
                        ExperimentField.ALLELE_ACCESSION + "," +
                        ExperimentField.GENE_ACCESSION);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response);

    }

    /**
     * Return a list of a all data candidates for statistical analysis
     * 
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctCategoricalOrgPipelineParamStrainZygositySexGeneAccessionAlleleAccessionMetadata() throws SolrServerException {

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ExperimentField.OBSERVATION_TYPE + ":categorical")
                .setRows(0)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1)
                .addFacetPivotField( // needs at least 2 fields
                ExperimentField.PHENOTYPING_CENTER_ID + "," +
                        ExperimentField.PIPELINE_ID + "," +
                        ExperimentField.PARAMETER_ID + "," +
                        ExperimentField.STRAIN + "," +
                        ExperimentField.ZYGOSITY + "," +
                        ExperimentField.SEX + "," +
                        ExperimentField.METADATA_GROUP + "," +
                        ExperimentField.ALLELE_ACCESSION + "," +
                        ExperimentField.GENE_ACCESSION);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response);

    }

    /**
     * Recursive method to fill a map with multiple combination of pivot fields.
     * Each pivot level can have multiple children. Hence, each level should
     * pass back to the caller a list of all possible combination
     * 
     * @param pivotLevel
     * @param map
     */
    private List<Map<String, String>> getLeveledFacetPivotValue(PivotField pivotLevel, PivotField parentPivot) {

        List<Map<String, String>> results = new ArrayList<Map<String, String>>();

        List<PivotField> pivotResult = pivotLevel.getPivot();
        if (pivotResult != null) {
            for (int i = 0; i < pivotResult.size(); i++) {
                List<Map<String, String>> lmap = getLeveledFacetPivotValue(pivotResult.get(i), pivotLevel);
                // add the parent pivot
                if (parentPivot != null) {
                    for (Map<String, String> map : lmap) {
                        map.put(parentPivot.getField(), parentPivot.getValue().toString());
                    }
                }
                results.addAll(lmap);
            }
        } else {
            Map<String, String> map = new HashMap<String, String>();
            map.put(pivotLevel.getField(), pivotLevel.getValue().toString());

            // add the parent pivot
            if (parentPivot != null) {
                map.put(parentPivot.getField(), parentPivot.getValue().toString());
            }
            results.add(map);
        }
        //
        return results;
    }

    public List<ObservationDTO> getExperimentalObservationsByParameterPipelineGeneAccZygosityOrganisationStrainSexSexAndMetaDataGroupAndAlleleAccession(
            Integer parameterId, Integer pipelineId, String gene, List<String> zygosities, Integer organisationId, String strain, SexType sex, String metaDataGroup, String alleleAccession) throws SolrServerException {

        List<ObservationDTO> resultsDTO;
        SolrQuery query = new SolrQuery()
                .setQuery(ExperimentField.GENE_ACCESSION + ":" + gene.replace(":", "\\:"))
                .addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
                .setStart(0).setRows(10000);


        if (pipelineId != null) {
            query.addFilterQuery(ExperimentField.PIPELINE_ID + ":" + pipelineId);
        }

        if (zygosities != null && zygosities.size() > 0 && zygosities.size()!=3) {
        	if(zygosities.size()==2) {
        		query.addFilterQuery(ExperimentField.ZYGOSITY + ":(" + zygosities.get(0)+" OR "+zygosities.get(1)+")");
        	}else {
        		if (!zygosities.get(0).equalsIgnoreCase("null"))
        			query.addFilterQuery(ExperimentField.ZYGOSITY + ":" + zygosities.get(0));//only option is one left
        			
        	}

        }
        if (strain != null) {
            query.addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"));
        }
        if (organisationId != null) {
            query.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId);
        }
        if (sex != null) {
            query.addFilterQuery(ExperimentField.SEX + ":" + sex);
        }
        if(metaDataGroup!=null) {
        	query.addFilterQuery(ExperimentField.METADATA_GROUP + ":\"" + metaDataGroup + "\"");
        }
        if(alleleAccession!=null){
            query.addFilterQuery(ExperimentField.ALLELE_ACCESSION + ":" + alleleAccession.replace(":", "\\:"));
        }
        LOG.debug("observation  service query = "+query);
        QueryResponse response = solr.query(query);
        resultsDTO = response.getBeans(ObservationDTO.class);
        return resultsDTO;
    }





    
    /**
     * Return a list of a triplets of pipeline stable id, phenotyping center
     * and allele accession
 
     *
     * @param genomicFeatureAcc a gene accession
     * @return list of triplets
     * @throws SolrServerException
     */
    public List<Map<String,String>> getDistinctPipelineAlleleCenterListByGeneAccession(String genomicFeatureAcc) throws SolrServerException {
    	
        List<Map<String,String>> results = new LinkedList<Map<String, String>>();

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ExperimentField.GENE_ACCESSION + ":" + "\""+genomicFeatureAcc+"\"")
                .addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .setRows(0)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1)
                .addFacetPivotField( // needs at least 2 fields
                		ExperimentField.PIPELINE_STABLE_ID + "," +
                		ExperimentField.PIPELINE_NAME + "," +
                		ExperimentField.PHENOTYPING_CENTER + "," +
                		ExperimentField.ALLELE_ACCESSION + "," +
                		ExperimentField.ALLELE_SYMBOL);  

        QueryResponse response = solr.query(query);

        NamedList<List<PivotField>> facetPivot = response.getFacetPivot();

        if (facetPivot != null && facetPivot.size() > 0) {
        	for (int i = 0; i < facetPivot.size(); i++) {

        		String name = facetPivot.getName(i); // in this case only one of them
        		LOG.debug("facetPivot name" + name);
        		List<PivotField> pivotResult = facetPivot.get(name);

        		// iterate on results
        		for (int j = 0; j < pivotResult.size(); j++) {

        			// create a HashMap to store a new triplet of data
        			
        			PivotField pivotLevel = pivotResult.get(j);
        			List<Map<String,String>> lmap = getLeveledFacetPivotValue(pivotLevel, null);
        			results.addAll(lmap);
        		}


        	}
        }

        return results;
    }
    
    /**
     * Return a list of parameters measured for a particular pipeline, allele 
     * and center combination. A list of filters (meaning restriction to some
     * specific procedures is passed.
     * @param genomicFeatureAcc a gene accession
     * @return list of triplets
     * @throws SolrServerException
     */
    public List<Map<String,String>> getDistinctParameterListByPipelineAlleleCenter(
    		String pipelineStableId, 
    		String alleleAccession,
    		String phenotypingCenter,
    		List<String> procedureFilters) throws SolrServerException {
    	
        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ExperimentField.PIPELINE_STABLE_ID + ":" + pipelineStableId)
                .addFilterQuery(ExperimentField.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"")
                .addFilterQuery(ExperimentField.ALLELE_ACCESSION + ":\"" + alleleAccession + "\"");
        
        int index = 0;
        if (procedureFilters != null && procedureFilters.size() > 0) {
        	StringBuilder queryBuilder = new StringBuilder(ExperimentField.PROCEDURE_STABLE_ID + ":(");
        
        	for (String procedureFilter: procedureFilters) {
        		if (index == 0) {
        			queryBuilder.append(procedureFilter);
        		} else {
        			queryBuilder.append(" OR " + procedureFilter);
        		}
        		index++;
        	}
        	queryBuilder.append(")");
        	query.addFilterQuery(queryBuilder.toString());
        }
        
        query.setRows(0)
        .setFacet(true).setFacetMinCount(1).setFacetLimit(-1)
        .addFacetPivotField( // needs at least 2 fields
        		ExperimentField.PROCEDURE_STABLE_ID + "," +
        		ExperimentField.PROCEDURE_NAME + "," +
        		ExperimentField.PARAMETER_STABLE_ID + "," +
        		ExperimentField.PARAMETER_NAME + "," +
        		ExperimentField.OBSERVATION_TYPE + "," +
        		ExperimentField.ZYGOSITY);

        System.out.println(query.toString());
        
        QueryResponse response = solr.query(query);

        NamedList<List<PivotField>> facetPivot = response.getFacetPivot();

        List<Map<String,String>> results = new LinkedList<Map<String, String>>();
        
        if (facetPivot != null && facetPivot.size() > 0) {
        	for (int i = 0; i < facetPivot.size(); i++) {

        		String name = facetPivot.getName(i); // in this case only one of them
        		System.out.println("facetPivot name" + name);
        		
        		List<PivotField> pivotResult = facetPivot.get(name);

        		// iterate on results
        		for (int j = 0; j < pivotResult.size(); j++) {
        			
        			// create a HashMap to store a new triplet of data
        			PivotField pivotLevel = pivotResult.get(j);
        			System.out.println("TEST " + pivotLevel.getField() + " " + pivotLevel.getCount());
        			List<Map<String,String>> lmap = getLeveledFacetPivotValue(pivotLevel, null);
        			results.addAll(lmap);
        		}


        	}
        }

        return results;
    }
    
    /**
     * Return a list of procedures effectively performed given pipeline stable id, 
     * phenotyping center and allele accession
     *
     * @param genomicFeatureAcc a gene accession
     * @return list of integer db keys of the parameter rows
     * @throws SolrServerException
     */
    public List<String> getDistinctProcedureListByPipelineAlleleCenter(
    		String pipelineStableId, 
    		String alleleAccession,
    		String phenotypingCenter) throws SolrServerException {
    	
        List<String> results = new LinkedList<String>();

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ExperimentField.PIPELINE_STABLE_ID + ":" + pipelineStableId)
                .addFilterQuery(ExperimentField.PHENOTYPING_CENTER + ":" + phenotypingCenter)
                .addFilterQuery(ExperimentField.ALLELE_ACCESSION + ":\"" + alleleAccession + "\"")
                .setRows(0)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1)
                .addFacetField(ExperimentField.PROCEDURE_STABLE_ID);
                

        QueryResponse response = solr.query(query);
        List<FacetField> fflist = response.getFacetFields();

        for (FacetField ff : fflist) {

            // If there are no face results, the values will be null
            // skip this facet field in that case
            if (ff.getValues() == null) {
                continue;
            }

            for (Count c : ff.getValues()) {
            	results.add(c.getName());
            }
        }

        return results;
    }    
    




	// gets categorical data for graphs on phenotype page
	public Map<String, List<DiscreteTimePoint>> getTimeSeriesMutantData(
			String parameter, List<String> genes, ArrayList<String> strains,  String[] center, String[] sex)
			throws SolrServerException {

		Map<String, List<DiscreteTimePoint>> finalRes = new HashMap<String, List<DiscreteTimePoint>>(); // <allele_accession,
																										// timeSeriesData>

		SolrQuery query = new SolrQuery().addFilterQuery(
				ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
				.addFilterQuery(
						ExperimentField.PARAMETER_STABLE_ID + ":" + parameter);

        String q = (strains.size() > 1) ? 
                "(" + ExperimentField.STRAIN+ ":\"" + StringUtils.join(strains.toArray(), "\" OR " + ExperimentField.STRAIN + ":\"") + "\")" : 
                    ExperimentField.STRAIN + ":\"" + strains.get(0) + "\"";

		if (genes != null && genes.size() > 0) {
			q += " AND (";
			q += (genes.size() > 1) ? ExperimentField.GENE_ACCESSION
					+ ":\""
					+ StringUtils.join(genes.toArray(), "\" OR "
							+ ExperimentField.GENE_ACCESSION + ":\"") + "\""
					: ExperimentField.GENE_ACCESSION + ":\"" + genes.get(0)
							+ "\"";
			q += ")";
		}
		
		if (center != null && center.length > 0) {
			q += " AND (";
			q += (center.length > 1) ? ExperimentField.PHENOTYPING_CENTER
					+ ":\""
					+ StringUtils.join(center, "\" OR "
							+ ExperimentField.PHENOTYPING_CENTER + ":\"") + "\""
					: ExperimentField.PHENOTYPING_CENTER + ":\"" + center[0]
							+ "\"";
			q += ")";
		}

		if (sex != null && sex.length == 1){
			q += " AND " + ExperimentField.SEX + ":\"" + sex[0] + "\"";
		}
		

		query.setQuery(q);
		query.set("group.field", ExperimentField.GENE_SYMBOL);
		query.set("group", true);
		query.set("fl", ExperimentField.DATA_POINT + ","
				+ ExperimentField.DISCRETE_POINT);
		query.set("group.limit", 100000); // number of documents to be returned
											// per group
		query.set("group.sort", ExperimentField.DISCRETE_POINT + " asc");
		query.setRows(10000);

//		System.out.println("+_+_+ " + solr.getBaseURL() + "/select?" + query);
		List<Group> groups = solr.query(query).getGroupResponse().getValues()
				.get(0).getValues();
		// for mutants it doesn't seem we need binning
		// groups are the alleles
		for (Group gr : groups) {
			SolrDocumentList resDocs = gr.getResult();
			DescriptiveStatistics stats = new DescriptiveStatistics();
			float discreteTime = (float) resDocs.get(0).getFieldValue(
					ExperimentField.DISCRETE_POINT);
			ArrayList<DiscreteTimePoint> res = new ArrayList<DiscreteTimePoint>();
			for (int i = 0; i < resDocs.getNumFound(); i++) {
				SolrDocument doc = resDocs.get(i);
				stats.addValue((float) doc
						.getFieldValue(ExperimentField.DATA_POINT));
				if (discreteTime != (float) doc.getFieldValue(ExperimentField.DISCRETE_POINT) || 
						i == resDocs.getNumFound() - 1) { // we are at the end of the document list
					// add to list
					float discreteDataPoint = (float) stats.getMean();
					DiscreteTimePoint dp = new DiscreteTimePoint(discreteTime,
							discreteDataPoint, new Float(
									stats.getStandardDeviation()));
					List<Float> errorPair = new ArrayList<>();
					double std = stats.getStandardDeviation();
					Float lower = new Float(discreteDataPoint);
					Float higher = new Float(discreteDataPoint);
					errorPair.add(lower);
					errorPair.add(higher);
					dp.setErrorPair(errorPair);
					res.add(dp);
					// update discrete point
					discreteTime = Float.valueOf(doc.getFieldValue(
							ExperimentField.DISCRETE_POINT).toString());
					// update stats
					stats = new DescriptiveStatistics();
				}
			}
			// add list
			finalRes.put(gr.getGroupValue(), res);
		}
		return finalRes;
	}

	// gets categorical data for graphs on phenotype page
	public List<DiscreteTimePoint> getTimeSeriesControlData(String parameter,
			ArrayList<String> strains,  String[] center, String[] sex) throws SolrServerException {

		ArrayList<DiscreteTimePoint> res = new ArrayList<DiscreteTimePoint>();
		SolrQuery query = new SolrQuery().addFilterQuery(
				ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":control")
				.addFilterQuery(
						ExperimentField.PARAMETER_STABLE_ID + ":" + parameter);
		String q = (strains.size() > 1) ? "("
				+ ExperimentField.STRAIN
				+ ":\""
				+ StringUtils.join(strains.toArray(), "\" OR "
						+ ExperimentField.STRAIN + ":\"") + "\")"
				: ExperimentField.STRAIN + ":\"" + strains.get(0) + "\"";

		
		if (center != null && center.length > 0) {
			q += " AND (";
			q += (center.length > 1) ? ExperimentField.PHENOTYPING_CENTER
					+ ":\""
					+ StringUtils.join(center, "\" OR "
							+ ExperimentField.PHENOTYPING_CENTER + ":\"") + "\""
					: ExperimentField.PHENOTYPING_CENTER + ":\"" + center[0]
							+ "\"";
			q += ")";
		}

		if (sex != null && sex.length == 1){
			q += " AND " + ExperimentField.SEX + ":\"" + sex[0] + "\"";
		}
		
		query.setQuery(q);
		query.set("group.field", ExperimentField.DISCRETE_POINT);
		query.set("group", true);
		query.set("fl", ExperimentField.DATA_POINT + ","
				+ ExperimentField.DISCRETE_POINT);
		query.set("group.limit", 100000); // number of documents to be returned per group
		query.set("sort", ExperimentField.DISCRETE_POINT + " asc");
		query.setRows(10000);

//		System.out.println("+_+_+ " + solr.getBaseURL() + "/select?" + query);
		List<Group> groups = solr.query(query).getGroupResponse().getValues()
				.get(0).getValues();
		boolean rounding = false;
		// decide if binning is needed i.e. is the increment points are too
		// scattered, as for calorimetry
		if (groups.size() > 30) { // arbitrary value, just piced it because it seems reasonable for the size of our graphs
			if (Float.valueOf(groups.get(groups.size() - 1).getGroupValue())
					- Float.valueOf(groups.get(0).getGroupValue()) <= 30) { //then rounding will be enough
				rounding = true;
			}
		}
		if (rounding) {
			int bin = Math.round(Float.valueOf(groups.get(0).getGroupValue()));
			for (Group gr : groups) {
				int discreteTime = Math
						.round(Float.valueOf(gr.getGroupValue()));
				// for calormetry ignore what's before -5 and after 16
				if (parameter.startsWith("IMPC_CAL")
						|| parameter.startsWith("ESLIM_003_001")
						|| parameter.startsWith("M-G-P_003_001")) {
					if (discreteTime < -5) {
						continue;
					} else if (discreteTime > 16) {
						break;
					}
				}
				float sum = 0;
				SolrDocumentList resDocs = gr.getResult();
				DescriptiveStatistics stats = new DescriptiveStatistics();
				for (SolrDocument doc : resDocs) {
					sum += (float) doc
							.getFieldValue(ExperimentField.DATA_POINT);
					stats.addValue((float) doc
							.getFieldValue(ExperimentField.DATA_POINT));
				}
				if (bin < discreteTime
						|| groups.indexOf(gr) == groups.size() - 1) { // finished the groups of filled the bin
					float discreteDataPoint = sum / resDocs.getNumFound();
					DiscreteTimePoint dp = new DiscreteTimePoint(
							(float) discreteTime, discreteDataPoint, new Float(
									stats.getStandardDeviation()));
					List<Float> errorPair = new ArrayList<>();
					double std = stats.getStandardDeviation();
					Float lower = new Float(discreteDataPoint - std);
					Float higher = new Float(discreteDataPoint + std);
					errorPair.add(lower);
					errorPair.add(higher);
					dp.setErrorPair(errorPair);
					res.add(dp);
					bin = discreteTime;
				}
			}
		} else {
			for (Group gr : groups) {
				Float discreteTime = Float.valueOf(gr.getGroupValue());
				float sum = 0;
				SolrDocumentList resDocs = gr.getResult();
				DescriptiveStatistics stats = new DescriptiveStatistics();
				for (SolrDocument doc : resDocs) {
					sum += (float) doc
							.getFieldValue(ExperimentField.DATA_POINT);
					stats.addValue((float) doc
							.getFieldValue(ExperimentField.DATA_POINT));
				}
				float discreteDataPoint = sum / resDocs.getNumFound();
				DiscreteTimePoint dp = new DiscreteTimePoint(discreteTime,
						discreteDataPoint, new Float(
								stats.getStandardDeviation()));
				List<Float> errorPair = new ArrayList<>();
				double std = stats.getStandardDeviation();
				Float lower = new Float(discreteDataPoint - std);
				Float higher = new Float(discreteDataPoint + std);
				errorPair.add(lower);
				errorPair.add(higher);
				dp.setErrorPair(errorPair);
				res.add(dp);
			}
		}
		return res;
	}
	
	/**
	 * 
	 * @param p
	 * @param genes
	 * @param strains
	 * @param biologicalSample
	 * @return list of centers and sexes for the given parameters
	 * @throws SolrServerException 
	 */
	
	public Set<String> getCenters(Parameter p,
			List<String> genes, ArrayList<String> strains,
			String biologicalSample) throws SolrServerException{ // this method should work for all types of data.
		Set<String> centers = new HashSet<String>();
		
		SolrQuery query = new SolrQuery().addFilterQuery(
				ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":"
						+ biologicalSample).addFilterQuery(
				ExperimentField.PARAMETER_STABLE_ID + ":" + p.getStableId());

		String q = (strains.size() > 1) ? "("
				+ ExperimentField.STRAIN
				+ ":\""
				+ StringUtils.join(strains.toArray(), "\" OR "
						+ ExperimentField.STRAIN + ":\"") + "\")"
				: ExperimentField.STRAIN + ":\"" + strains.get(0) + "\"";

		if (genes != null && genes.size() > 0) {
			q += " AND (";
			q += (genes.size() > 1) ? ExperimentField.GENE_ACCESSION
					+ ":\""
					+ StringUtils.join(genes.toArray(), "\" OR "
							+ ExperimentField.GENE_ACCESSION + ":\"") + "\""
					: ExperimentField.GENE_ACCESSION + ":\"" + genes.get(0)
							+ "\"";
			q += ")";
		}
		
		query.setQuery(q);
		query.setRows(1000000);
		// query.set("sort", ExperimentField.DATA_POINT + " asc");
		query.setFields(ExperimentField.GENE_ACCESSION, ExperimentField.DATA_POINT);
		query.set("group", true);
		query.set("group.field", ExperimentField.PHENOTYPING_CENTER);
		
//		System.out.println("------HEERE-----\n"+query);
		
		List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
		for (Group gr : groups) {
			centers.add((String)gr.getGroupValue());
		}
//		System.out.println("CENTERS: " + centers);
		return centers;
	}

	public StackedBarsData getUnidimensionalData(Parameter p,
			List<String> genes, ArrayList<String> strains,
			String biologicalSample,  String[] center, String[] sex) throws SolrServerException {

		List<Integer> res = new ArrayList<Integer>();
		String urlParams = "";
		
		SolrQuery query = new SolrQuery().addFilterQuery(
				ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":"
						+ biologicalSample).addFilterQuery(
				ExperimentField.PARAMETER_STABLE_ID + ":" + p.getStableId());

		String q = (strains.size() > 1) ? "("
				+ ExperimentField.STRAIN
				+ ":\""
				+ StringUtils.join(strains.toArray(), "\" OR "
						+ ExperimentField.STRAIN + ":\"") + "\")"
				: ExperimentField.STRAIN + ":\"" + strains.get(0) + "\"";
		if (strains.size() > 0) {
			urlParams += urlParams += "&strain=" + StringUtils.join(strains.toArray(), "&strain=");
		}
		
		if (center != null && center.length > 0) {
			q += " AND (";
			q += (center.length > 1) ? ExperimentField.PHENOTYPING_CENTER
					+ ":\""
					+ StringUtils.join(center, "\" OR "
							+ ExperimentField.PHENOTYPING_CENTER + ":\"") + "\""
					: ExperimentField.PHENOTYPING_CENTER + ":\"" + center[0]
							+ "\"";
			q += ")";
			urlParams += "&phenotyping_center=" + StringUtils.join(center, "&phenotyping_center=");
		}

		if (sex != null && sex.length == 1){
			q += " AND " + ExperimentField.SEX + ":\"" + sex[0] + "\"";
			urlParams += "&gender=" + sex[0];
		}
		
		query.setQuery(q);
		query.setRows(1000000);
		query.set("sort", ExperimentField.DATA_POINT + " asc");
		query.setFields(ExperimentField.GENE_ACCESSION, ExperimentField.DATA_POINT, ExperimentField.GENE_SYMBOL);
		query.set("group", true);
		query.set("group.field", ExperimentField.COLONY_ID);
		query.set("group.limit", 200);
		// per group

//		System.out.println("--- unidimensional : " + solr.getBaseURL() + "/select?" + query);
		
		// for each colony get the mean & put it in the array of data to plot
		List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
		double[] meansArray = new double[groups.size()];
		String[] genesArray = new String[groups.size()];
		String [] geneSymbolArray = new String[groups.size()];
		int i = 0;
		for (Group gr : groups) {
			double sum = 0;
			double total = 0;
			SolrDocumentList resDocs = gr.getResult();
			for (SolrDocument doc : resDocs) {
				sum += (double) 0
						+ (float) doc.getFieldValue(ExperimentField.DATA_POINT);
				total++;
			}
			genesArray[i] = (String) resDocs.get(0).get(
					ExperimentField.GENE_ACCESSION);
			geneSymbolArray[i] = (String) resDocs.get(0).get(
					ExperimentField.GENE_SYMBOL);
			
			meansArray[i] = sum / total;
			i++;
		}
		
		System.out.println("genesArray " + genesArray.length );
		System.out.println("geneSymbolArray " + geneSymbolArray.length);

		// we do the binning for all the data but fill the bins after that to
		// keep tract of phenotype associations
		int binCount = Math.min((int) Math.floor((double) groups.size() / 2),
				20);
		ArrayList<String> mutantGenes = new ArrayList<String>();
		ArrayList<String> controlGenes = new ArrayList<String>();
		ArrayList<String> mutantGeneAcc = new ArrayList<String>();
		ArrayList<String> controlGeneAcc = new ArrayList<String>();
		ArrayList<Double> upperBounds = new ArrayList<Double>();
		EmpiricalDistribution distribution = new EmpiricalDistribution(binCount);
		if (meansArray.length > 0){
			distribution.load(meansArray);
			int k = 0;
			for (double bound : distribution.getUpperBounds())
				upperBounds.add(bound);
			// we we need to distribute the control mutants and the
			// phenotype-mutants in the bins
			ArrayList<Double> controlM = new ArrayList<Double>();
			ArrayList<Double> phenMutants = new ArrayList<Double>();
	
			for (int j = 0; j < upperBounds.size(); j++) {
				controlM.add((double) 0);
				phenMutants.add((double) 0);
				controlGenes.add("");
				mutantGenes.add("");
				controlGeneAcc.add("");
				mutantGeneAcc.add("");
			}
	
			for (int j = 0; j < groups.size(); j++) {
				// find out the proper bin
				int binIndex = getBin(upperBounds, meansArray[j]);
				if (genes.contains(genesArray[j])) {
					phenMutants.set(binIndex, 1 + phenMutants.get(binIndex));
					String genesString = mutantGenes.get(binIndex);
					if (!genesString.contains(geneSymbolArray[j])){
						if (genesString.equals("")){
							mutantGenes.set(binIndex, geneSymbolArray[j]);
							mutantGeneAcc.set(binIndex,  "accession="+genesArray[j]);
						}
						else {
							mutantGenes.set(binIndex, genesString + ", " + geneSymbolArray[j]);
							mutantGeneAcc.set(binIndex, mutantGeneAcc.get(binIndex) + "&accession="+ genesArray[j]);
						}
					}
				} else { // treat as control because they don't have this phenotype association
					String genesString = controlGenes.get(binIndex);
					if (!genesString.contains(geneSymbolArray[j])){
						if(genesString.equalsIgnoreCase("")){
							controlGenes.set(binIndex, geneSymbolArray[j]);	
							controlGeneAcc.set(binIndex,  "accession="+genesArray[j]);	
						}
						else {
							controlGenes.set(binIndex, genesString + ", " + geneSymbolArray[j]);
							controlGeneAcc.set(binIndex, controlGeneAcc.get(binIndex) + "&accession="+  genesArray[j]);	
						}
					}
					controlM.set(binIndex, 1 + controlM.get(binIndex));
				}
			}
	//		System.out.println(" Mutants list " + phenMutants);
	
			// add the rest of parameters to the graph urls
			for (int t = 0; t < controlGeneAcc.size(); t++){
				controlGeneAcc.set(t, controlGeneAcc.get(t) + urlParams);
				mutantGeneAcc.set(t, mutantGeneAcc.get(t) + urlParams);
			}
			
			StackedBarsData data = new StackedBarsData();
			data.setUpperBounds(upperBounds);
			data.setControlGenes(controlGenes);
			data.setControlMutatns(controlM);
			data.setMutantGenes(mutantGenes);
			data.setPhenMutants(phenMutants);
			data.setControlGeneAccesionIds(controlGeneAcc);
			data.setMutantGeneAccesionIds(mutantGeneAcc);
			return data;
		}
		
		return null;

		/*
		 * SolrDocumentList resDocs =solr.query(query).getResults();
		 * 
		 * double[] data = new double[(int)resDocs.getNumFound()]; int pos = 0;
		 * for (SolrDocument doc : resDocs){ data[pos++] = (double)0 +
		 * (float)doc.getFieldValue(ExperimentField.DATA_POINT); }
		 * 
		 * List<Long> histogram = new ArrayList<Long>();
		 * org.apache.commons.math3.random.EmpiricalDistribution distribution =
		 * new org.apache.commons.math3.random.EmpiricalDistribution(binCount);
		 * 
		 * distribution.load(data); int k = 0;
		 * for(org.apache.commons.math3.stat.descriptive.SummaryStatistics
		 * stats: distribution.getBinStats()) { histogram.add(stats.getN());
		 * System.out.println("--- stats-- " + stats.getSummary().toString()); }
		 */
	}

	private int getBin(List<Double> bins, Double valueToBin) {
		for (Double upperBound : bins) {
			if (valueToBin < upperBound)
				return bins.indexOf(upperBound);
		}
		return bins.size() - 1;
	}

	// gets categorical data for graphs on phenotype page
	public CategoricalSet getCategories(Parameter parameter,
			ArrayList<String> genes, String biologicalSampleGroup,
			ArrayList<String> strains,  String[] center, String[] sex) throws SolrServerException, SQLException {

		CategoricalSet resSet = new CategoricalSet();
		resSet.setName(biologicalSampleGroup);
		SolrQuery query = new SolrQuery().addFilterQuery(
				ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":"
						+ biologicalSampleGroup).addFilterQuery(
				ExperimentField.PARAMETER_STABLE_ID + ":" + parameter.getStableId());

		String q = (strains.size() > 1) ? "("
				+ ExperimentField.STRAIN
				+ ":\""
				+ StringUtils.join(strains.toArray(), "\" OR "
						+ ExperimentField.STRAIN + ":\"") + "\")"
				: ExperimentField.STRAIN + ":\"" + strains.get(0) + "\"";

		if (genes != null && genes.size() > 0) {
			q += " AND (";
			q += (genes.size() > 1) ? ExperimentField.GENE_ACCESSION
					+ ":\""
					+ StringUtils.join(genes.toArray(), "\" OR "
							+ ExperimentField.GENE_ACCESSION + ":\"") + "\""
					: ExperimentField.GENE_ACCESSION + ":\"" + genes.get(0)
							+ "\"";
			q += ")";
		}
		
		if (center != null && center.length > 0) {
			q += " AND (";
			q += (center.length > 1) ? ExperimentField.PHENOTYPING_CENTER
					+ ":\""
					+ StringUtils.join(center, "\" OR "
							+ ExperimentField.PHENOTYPING_CENTER + ":\"") + "\""
					: ExperimentField.PHENOTYPING_CENTER + ":\"" + center[0]
							+ "\"";
			q += ")";
		}

		if (sex != null && sex.length == 1){
			q += " AND " + ExperimentField.SEX + ":\"" + sex[0] + "\"";
		}
		
		query.setQuery(q);
		query.set("group.field", ExperimentField.CATEGORY);
		query.set("group", true);
		query.setRows(100); // shouldn't have more then 10 categories for one
							// parameter!!

//		System.out.println("-- get categories: " + solr.getBaseURL() + "/select?" + query);
		
		List<String> categories = new ArrayList<String>();
		List<Group> groups = solr.query(query).getGroupResponse().getValues()
				.get(0).getValues();
		for (Group gr : groups) {
			categories.add((String) gr.getGroupValue());
			CategoricalDataObject catObj = new CategoricalDataObject();
			catObj.setCount((long) gr.getResult().getNumFound());
			String catLabel = parameterDAO.getCategoryDescription(parameter.getId(), gr.getGroupValue());
			catObj.setCategory(catLabel);
			resSet.add(catObj);
		}
		return resSet;
	}

	public int getTestedGenes(String phenotypeId, String sex,
			List<String> parameters) throws SolrServerException {

		List<String> genes = new ArrayList<String>();
		for (String parameter : parameters) {
			SolrQuery query = new SolrQuery()
					.setQuery(
							ExperimentField.PARAMETER_STABLE_ID + ":"
									+ parameter)
					.addField(ExperimentField.GENE_ACCESSION)
					.setFilterQueries(
							ExperimentField.STRAIN + ":\"MGI:2159965\" OR "
									+ ExperimentField.STRAIN
									+ ":\"MGI:2164831\"").setRows(10000);
			query.set("group.field", ExperimentField.GENE_ACCESSION);
			query.set("group", true);
			if (sex != null) {
				query.addFilterQuery(ExperimentField.SEX + ":" + sex);
			}
			// I need to add the genes to a hash in case some come up multiple
			// times from different parameters
			// System.out.println("=====" + solr.getBaseURL() + query);
			List<Group> groups = solr.query(query).getGroupResponse()
					.getValues().get(0).getValues();
			for (Group gr : groups) {
				// System.out.println(gr.getGroupValue());
				if (!genes.contains((String) gr.getGroupValue())) {
					genes.add((String) gr.getGroupValue());
				}
			}
		}
		return genes.size();
	}

	/**
	 * Get all controls for a specified set of center, strain, parameter, 
	 * (optional) sex, and metadata group.
	 * 
	 * @param parameterId
	 * @param strain
	 * @param organisationId
	 * @param experimentDate date of experiment 
	 * @param sex if null, both sexes are returned
	 * @param metadataGroup when metadataGroup is empty string, force solr to search for metadata_group:""
	 * @return list of control observationDTOs that conform to the search criteria
	 * @throws SolrServerException
	 */
	public List<ObservationDTO> getAllControlsBySex(Integer parameterId, String strain, Integer organisationId, Date experimentDate, String sex, String metadataGroup) throws SolrServerException {

		List<ObservationDTO> results = new ArrayList<ObservationDTO>();

		QueryResponse response = new QueryResponse();
		
		SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":control")
			.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
			.addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
			.setStart(0)
			.setRows(5000)
		;
		if (organisationId!= null){
			query.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId);
		}
		
		if(metadataGroup == null){
                }else if( metadataGroup.isEmpty())
                 {
			query.addFilterQuery(ExperimentField.METADATA_GROUP + ":\"\"");
		} else {
			query.addFilterQuery(ExperimentField.METADATA_GROUP + ":" + metadataGroup);
		}

		if(sex != null) {
			query.addFilterQuery(ExperimentField.SEX + ":" + sex);
		}

		// Filter starting at 2000-01-01 and going through the end 
		// of day on the experiment date
		if(experimentDate != null) {
			
			// Set time range to the last possible time on the day for SOLR 
			// range query to include all observations on the same day
			Calendar cal = Calendar.getInstance();
			cal.setTime(DateUtils.addDays(experimentDate, 1));
			cal.set(Calendar.HOUR_OF_DAY, 23);        
			cal.set(Calendar.MINUTE, 59); 
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			Date maxDate = cal.getTime();

			Date beginning = new Date(946684800000L); // Start date (Jan 1 2000)
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			String dateFilter = df.format(beginning)+"Z TO "+df.format(maxDate)+"Z";
			query.addFilterQuery(ExperimentField.DATE_OF_EXPERIMENT + ":[" + dateFilter + "]");
		}
		response = solr.query(query);
		results = response.getBeans(ObservationDTO.class);
		
		return results;
	}

	/**
	 * Get all controls for a specified set of center, strain, parameter, 
	 * (optional) sex, and metadata group that occur on the same day as
	 * passed in (or in WTSI case, the same week).
	 * 
	 * @param parameterId
	 * @param strain
	 * @param organisationId
	 * @param experimentDate the date of interest
	 * @param sex if null, both sexes are returned
	 * @param metadataGroup when metadataGroup is empty string, force solr to search for metadata_group:""
	 * @return list of control observationDTOs that conform to the search criteria
	 * @throws SolrServerException
	 */
	public List<ObservationDTO> getConcurrentControlsBySex(Integer parameterId, String strain, Integer organisationId, Date experimentDate, String sex, String metadataGroup) throws SolrServerException {

		List<ObservationDTO> results = new ArrayList<ObservationDTO>();

		// Use any control mouse ON THE SAME DATE as concurrent control
		// Set min and max time ranges to encompass the whole day
		Calendar cal = Calendar.getInstance();
		cal.setTime(experimentDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);        
		cal.set(Calendar.MINUTE, 0); 
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date minDate = cal.getTime();

		cal.set(Calendar.HOUR_OF_DAY, 23);        
		cal.set(Calendar.MINUTE, 59); 
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		Date maxDate = cal.getTime();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String dateFilter = df.format(minDate)+"Z TO "+df.format(maxDate)+"Z";

		QueryResponse response = new QueryResponse();
	
		SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":control")
			.addFilterQuery(ExperimentField.DATE_OF_EXPERIMENT + ":[" + dateFilter + "]")
			.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
			.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
			.addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
			.addFilterQuery(ExperimentField.SEX + ":" + sex)
			.setStart(0)
			.setRows(5000)
		;

		if(metadataGroup==null) {
			// don't add a metadata group filter
		} else if (metadataGroup.isEmpty()) {
			query.addFilterQuery(ExperimentField.METADATA_GROUP + ":\"\"");
		} else {
			query.addFilterQuery(ExperimentField.METADATA_GROUP + ":" + metadataGroup);
		}

		response = solr.query(query);		
		results = response.getBeans(ObservationDTO.class);
		
		return results;
	}

}
