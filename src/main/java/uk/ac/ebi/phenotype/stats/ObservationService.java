package uk.ac.ebi.phenotype.stats;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalDataObject;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalSet;

@Service
public class ObservationService {

    @Autowired
    PhenotypePipelineDAO parameterDAO;

    // Definition of the solr fields
    public static final class ExperimentField {

        public final static String ID = "id";
        public final static String PHENOTYPING_CENTER = "phenotyping_center";
        public final static String PHENOTYPING_CENTER_ID = "phenotyping_center_id";
        public final static String GENE_ACCESSION = "gene_accession";
        public final static String GENE_SYMBOL = "gene_symbol";
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
                .addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":"+ organisationId)
                .addFilterQuery(ExperimentField.STRAIN + ":"+ strain.replace(":", "\\:"))
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

    public List<ObservationDTO> getExperimentalUnidimensionalObservationsByParameterGeneAccZygosityOrganisationStrainSex(
            Integer parameterId, String gene, String zygosity,
            Integer organisationId, String strain, SexType sex
    ) throws SolrServerException {

        List<ObservationDTO> resultsDTO;
        SolrQuery query = new SolrQuery()
                .setQuery(ExperimentField.GENE_ACCESSION + ":" + gene.replace(":", "\\:"))
                .addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
                .setStart(0).setRows(10000);

        if (zygosity != null && !zygosity.equalsIgnoreCase("null")) {
            query.addFilterQuery(ExperimentField.ZYGOSITY + ":" + zygosity);
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

        QueryResponse response = solr.query(query);
        resultsDTO = response.getBeans(ObservationDTO.class);
        return resultsDTO;
    }

    /**
     * Return all the parameter ids that have associated data for a given
     * organisation
     *
     * @param organisationId the id of the organisation
     * @return list of integer db keys of the parameter rows
     * @throws SolrServerException
     */
    public List<Integer> getUnidimensionalParameterIdsWithObservationsByOrganisationId(
            Integer organisationId) throws SolrServerException {
        Set<Integer> parameterIds = new HashSet<>();

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
                .addFilterQuery(ExperimentField.OBSERVATION_TYPE + ":unidimensional")
                .setRows(0).addFacetField(ExperimentField.PARAMETER_ID)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1);

        QueryResponse response = solr.query(query);
        List<FacetField> fflist = response.getFacetFields();

        for (FacetField ff : fflist) {

            // If there are no face results, the values will be null
            // skip this facet field in that case
            if (ff.getValues() == null) {
                continue;
            }

            for (Count c : ff.getValues()) {
                parameterIds.add(Integer.parseInt(c.getName()));
            }
        }

        return new ArrayList<>(parameterIds);
    }

    /**
     * Return all the parameter ids that have associated data for a given
     * organisation
     *
     * @param organisationId the id of the organisation
     * @return list of integer db keys of the parameter rows
     * @throws SolrServerException
     */
    public List<Integer> getCategoricalParameterIdsWithObservationsByOrganisationId(
            Integer organisationId) throws SolrServerException {
        Set<Integer> parameterIds = new HashSet<>();

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
                .addFilterQuery(ExperimentField.OBSERVATION_TYPE + ":categorical")
                .setRows(0).addFacetField(ExperimentField.PARAMETER_ID)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1);

        QueryResponse response = solr.query(query);
        List<FacetField> fflist = response.getFacetFields();

        for (FacetField ff : fflist) {

            // If there are no face results, the values will be null
            // skip this facet field in that case
            if (ff.getValues() == null) {
                continue;
            }

            for (Count c : ff.getValues()) {
                parameterIds.add(Integer.parseInt(c.getName()));
            }
        }

        return new ArrayList<>(parameterIds);
    }

    /**
     * Return all the gene accession ids that have associated data for a given
     * organisation, strain, and zygosity
     *
     * @param parameterId the parameter DB id
     * @param organisationId the id of the organisation
     * @param strain the strain
     * @param zygosity the zygosity
     * @return list of gene accession ids
     * @throws SolrServerException
     */
    public List<String> getAllGeneAccessionIdsByParameterIdOrganisationIdStrainZygosity(
            Integer parameterId, Integer organisationId, String strain,
            String zygosity) throws SolrServerException {
        Set<String> genes = new HashSet<>();

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
                .addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
                .addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
                .addFilterQuery(ExperimentField.ZYGOSITY + ":" + zygosity)
                .setRows(0).addFacetField(ExperimentField.GENE_ACCESSION)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1);

        QueryResponse response = solr.query(query);
        List<FacetField> fflist = response.getFacetFields();

        for (FacetField ff : fflist) {

            // If there are no face results, the values will be null
            // skip this facet field in that case
            if (ff.getValues() == null) {
                continue;
            }

            for (Count c : ff.getValues()) {
                genes.add(c.getName());
            }
        }

        return new ArrayList<>(genes);
    }

    /**
     * Return all the strain accession ids that have associated data for a given
     * organisation ID and parameter ID
     *
     * @param organisationId the database id of the organisation
     * @param parameterId the database id of the parameter
     * @return list of strain accession ids
     * @throws SolrServerException
     */
    public List<String> getStrainsByParameterIdOrganistionId(
            Integer parameterId, Integer organisationId)
            throws SolrServerException {
        Set<String> strains = new HashSet<>();

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":"+ organisationId)
                .addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
                .setRows(0).addFacetField(ExperimentField.STRAIN)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1);

        QueryResponse response = solr.query(query);
        List<FacetField> fflist = response.getFacetFields();

        for (FacetField ff : fflist) {

            // If there are no face results, the values will be null
            // skip this facet field in that case
            if (ff.getValues() == null) {
                continue;
            }

            for (Count c : ff.getValues()) {
                strains.add(c.getName());
            }
        }

        return new ArrayList<>(strains);

    }

    /**
     * Return all the organisation ids that have associated observations
     *
     * @param organisation the name of the organisation
     * @return list of organisation database ids
     * @throws SolrServerException
     */
    public List<Integer> getAllOrganisationIdsWithObservations()
            throws SolrServerException {
        List<Integer> organisations = new ArrayList<Integer>();

        SolrQuery query = new SolrQuery().setQuery("*:*").setRows(0)
                .addFacetField(ExperimentField.PHENOTYPING_CENTER_ID)
                .setFacet(true).setFacetMinCount(1).setFacetLimit(-1);

        QueryResponse response = solr.query(query);
        List<FacetField> fflist = response.getFacetFields();

        for (FacetField ff : fflist) {

            // If there are no face results, the values will be null
            // skip this facet field in that case
            if (ff.getValues() == null) {
                continue;
            }

            for (Count c : ff.getValues()) {
                organisations.add(Integer.parseInt(c.getName()));
            }
        }

        return organisations;
    }

    /**
     * Returns all Gene Accession Ids By ParameterId,  OrganisationId, Strain,
     * Zygosity, and Sex
     * 
     * @param parameterId
     * @param organisationId
     * @param strain
     * @param zygosity
     * @param sex
     * @return
     * @throws SolrServerException 
     */
    public List<String> getAllGeneAccessionIdsByParameterIdOrganisationIdStrainZygositySex(
            Integer parameterId, Integer organisationId, String strain,
            String zygosity, String sex) throws SolrServerException {
        Set<String> genes = new HashSet<>();

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP+ ":experimental")
                .addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":"+ organisationId)
                .addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
                .addFilterQuery(ExperimentField.STRAIN + ":"+ strain.replace(":", "\\:"))
                .addFilterQuery(ExperimentField.ZYGOSITY + ":" + zygosity)
                .addFilterQuery(ExperimentField.SEX + ":" + sex).setRows(0)
                .addFacetField(ExperimentField.GENE_ACCESSION).setFacet(true)
                .setFacetMinCount(1).setFacetLimit(-1);

        QueryResponse response = solr.query(query);
        List<FacetField> fflist = response.getFacetFields();

        for (FacetField ff : fflist) {

            // If there are no face results, the values will be null
            // skip this facet field in that case
            if (ff.getValues() == null) {
                continue;
            }

            for (Count c : ff.getValues()) {
                genes.add(c.getName());
            }
        }

        return new ArrayList<>(genes);
    }

	// gets categorical data for graphs on phenotype page
	public Map<String, List<DiscreteTimePoint>> getTimeSeriesMutantData(
			String parameter, List<String> genes, ArrayList<String> strains)
			throws SolrServerException {

		Map<String, List<DiscreteTimePoint>> finalRes = new HashMap<String, List<DiscreteTimePoint>>(); // <allele_accession,
																										// timeSeriesData>

		SolrQuery query = new SolrQuery().addFilterQuery(
				ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
				.addFilterQuery(
						ExperimentField.PARAMETER_STABLE_ID + ":" + parameter);

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
			ArrayList<String> strains) throws SolrServerException {

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

		query.setQuery(q);
		query.set("group.field", ExperimentField.DISCRETE_POINT);
		query.set("group", true);
		query.set("fl", ExperimentField.DATA_POINT + ","
				+ ExperimentField.DISCRETE_POINT);
		query.set("group.limit", 100000); // number of documents to be returned
											// per group
		query.set("sort", ExperimentField.DISCRETE_POINT + " asc");
		query.setRows(10000);

//		System.out.println("+_+_+ " + solr.getBaseURL() + "/select?" + query);
		List<Group> groups = solr.query(query).getGroupResponse().getValues()
				.get(0).getValues();
		boolean rounding = false;
		// decide if binning is needed i.e. is the increment points are too
		// scattered, as for calorimetry
		if (groups.size() > 30) { // arbitrary value, just piced it because it
									// seems reasonable for the size of our
									// graphs
			if (Float.valueOf(groups.get(groups.size() - 1).getGroupValue())
					- Float.valueOf(groups.get(0).getGroupValue()) <= 30) { // then
																			// rounding
																			// will
																			// be
																			// enough
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
						|| groups.indexOf(gr) == groups.size() - 1) { // finished
																		// the
																		// groups
																		// of
																		// filled
																		// the
																		// bin
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

	public Map<String, List<Double>> getUnidimensionalData(Parameter p,
			List<String> genes, ArrayList<String> strains,
			String biologicalSample) throws SolrServerException {

		List<Integer> res = new ArrayList<Integer>();
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

		query.setQuery(q);
		query.setRows(1000000);
		// query.set("sort", ExperimentField.DATA_POINT + " asc");
		query.setFields(ExperimentField.GENE_ACCESSION, ExperimentField.DATA_POINT);
		query.set("group", true);
		query.set("group.field", ExperimentField.COLONY_ID);
		query.set("group.limit", 10000); // number of documents to be returned
											// per group
		System.out.println("--- look --- " + solr.getBaseURL() + "/select?" + query);

		// for each colony get the mean & put it in the array of data to plot
		List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
		double[] meansArray = new double[groups.size()];
		String[] allelesArray = new String[groups.size()];
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
			allelesArray[i] = (String) resDocs.get(0).get(
					ExperimentField.GENE_ACCESSION);
			meansArray[i] = sum / total;
			i++;
		}

		// we do the binning for all the data but fill the bins after that to
		// keep tract of phenotype associations
		int binCount = Math.min((int) Math.floor((double) groups.size() / 2),
				20);

		List<Double> upperBounds = new ArrayList<Double>();
		EmpiricalDistribution distribution = new EmpiricalDistribution(binCount);
		if (meansArray.length > 0){
			distribution.load(meansArray);
			int k = 0;
			for (double bound : distribution.getUpperBounds())
				upperBounds.add(bound);
			// we we need to distribute the control mutants and the
			// phenotype-mutants in the bins
			List<Double> controlM = new ArrayList<Double>();
			List<Double> phenMutants = new ArrayList<Double>();
	
			for (int j = 0; j < upperBounds.size(); j++) {
				controlM.add((double) 0);
				phenMutants.add((double) 0);
			}
	
			for (int j = 0; j < groups.size(); j++) {
				// find out the proper bin
				int binIndex = getBin(upperBounds, meansArray[j]);
				if (genes.contains(allelesArray[j])) {
					phenMutants.set(binIndex, 1 + phenMutants.get(binIndex));
				} else { // treat as control because they don't have this phenotype association
					
					controlM.set(binIndex, 1 + controlM.get(binIndex));
				}
			}
	//		System.out.println(" Mutants list " + phenMutants);
	
			Map<String, List<Double>> map = new HashMap<String, List<Double>>();
			map.put("labels", upperBounds);
			map.put("control", controlM);
			map.put("mutant", phenMutants);
			return map;
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
			ArrayList<String> strains) throws SolrServerException, SQLException {

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

		query.setQuery(q);
		query.set("group.field", ExperimentField.CATEGORY);
		query.set("group", true);
		query.setRows(100); // shouldn't have more then 10 categories for one
							// parameter!!

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
	 * @param sex if null, both sexes are returned
	 * @param metadataGroup when metadataGroup is empty string, force solr to search for metadata_group:""
	 * @param experimentDate 
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
			.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
			.addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
			.setStart(0)
			.setRows(5000)
		;

		if(metadataGroup == null || metadataGroup.isEmpty()) {
			query.addFilterQuery(ExperimentField.METADATA_GROUP + ":\"\"");
		} else {
			query.addFilterQuery(ExperimentField.METADATA_GROUP + ":" + metadataGroup);
		}

		if(sex != null) {
			query.addFilterQuery(ExperimentField.SEX + ":" + sex);
		}

		// Filter starting on the experiment date through the prior 6 months
		if(experimentDate != null) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00");

			Date epoch = new Date(0L); // Start date

			// Add one day because midnight refers to the beginning of the day to solr (I guess T24:00:00Z would work too, but meh. potayto-potahto)
			String dateFilter = df.format(epoch)+"Z TO "+df.format(DateUtils.addDays(experimentDate, 1))+"Z";
			query.addFilterQuery(ExperimentField.DATE_OF_EXPERIMENT + ":[" + dateFilter + "]");
		}
		
		response = solr.query(query);
System.out.println(" +++ SOLR QUERY FOR CONTROLS: http://ves-ebi-d0:8090/jenkins_dev_komp2/experiment/select?"+query);
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
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00");

		List<ObservationDTO> results = new ArrayList<ObservationDTO>();

		// DEFAULT
		// Use any control mouse ON THE SAME DATE as concurrent control
		String dateFilter = df.format(DateUtils.addDays(experimentDate,-1))+"Z TO "+df.format(DateUtils.addDays(experimentDate, 1))+"Z";

		if(organisationId == 3) {
			// WTSI rules
			// Use any mouse WITHIN A WEEK as concurrent control
			// Week is deined as Sunday to Saturday (inclusive) surrounding
			// the date of experiment
			

			// Sunday = DOW 0, subtract num returned from getDay from experiment Date
			// to get to the previous Sunday
			Date startWeekDate = DateUtils.addDays(experimentDate, (-1 * experimentDate.getDay()));			

			// Saturday = DOW 6 (zero based week), subtract from 6 num returned
			// from getDay to experiment Date to get to the next Saturday
			Date endWeekDate = DateUtils.addDays(experimentDate, (6 - experimentDate.getDay()));

			dateFilter = df.format(startWeekDate)+"Z TO "+df.format(endWeekDate)+"Z";
			
		}
		
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

		if(metadataGroup==null || metadataGroup.isEmpty()) {
			query.addFilterQuery(ExperimentField.METADATA_GROUP + ":\"\"");
		} else {
			query.addFilterQuery(ExperimentField.METADATA_GROUP + ":" + metadataGroup);
		}

		response = solr.query(query);		
		results = response.getBeans(ObservationDTO.class);
		
		return results;
	}

}
