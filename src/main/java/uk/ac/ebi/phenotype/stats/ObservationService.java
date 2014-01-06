package uk.ac.ebi.phenotype.stats;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
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
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.eclipse.jetty.util.log.Log;
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
	}


	private HttpSolrServer solr;

	public ObservationService() {
		String solrURL = "http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment"; //default
		solr = new HttpSolrServer(solrURL);
	}

	public ObservationService(String solrUrl) {
		solr = new HttpSolrServer(solrUrl);
	}
	
	/**
	 * Wrapper to get the parameter ID when passed a parameter object.  This
	 * calls the getControls method with an integer for a parameterID
	 * 
	 * @param parameterStableId the stable identifier of the parameter in question
	 * @param strain the strain
	 * @param organisationId the organisation
	 * @param max the date at which to cut off results (i.e. no results after date "max" will be returned) 
	 * @return list of observations 
	 * @throws SolrServerException when solr has a troubled mind/heart/body
	 */
	protected List<ObservationDTO> getControls(String parameterStableId, String strain, Integer organisationId, Date max) throws SolrServerException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion(parameterStableId, 1, 0);
		return getControls(p.getId(), strain, organisationId, max);
	}

	protected List<ObservationDTO> getControls(Integer parameterId, String strain, Integer organisationId, Date max) throws SolrServerException {

		return getControls(parameterId, strain, organisationId, max, Boolean.FALSE, null);
	}
	
	

	/**
	 * get control data observations for the combination of parameters passed
	 * in.
	 * 
	 * @param parameterStableId the stable identifier of the parameter in question
	 * @param strain the strain
	 * @param organisationId the organisation
	 * @param max the date at which to cut off results (i.e. no results after date "max" will be returned) 
	 * @return list of observations 
	 * @throws SolrServerException when solr has a troubled mind/heart/body
	 */
	protected List<ObservationDTO> getControls(Integer parameterId, String strain, Integer organisationId, Date max, Boolean showAll, String sex) throws SolrServerException {

		System.out.println("--- Calling getControls with : " + parameterId + " " + strain + " " +  organisationId + " " + sex);
		int n = 1000;		
		if (showAll){
			n = 10000000;
		}
		List<ObservationDTO> results = new ArrayList<ObservationDTO>();
		if (sex == null){
			results.addAll(getControlsBySex(parameterId, strain, organisationId, max, showAll, SexType.female.name(), n/2));
			results.addAll(getControlsBySex(parameterId, strain, organisationId, max, showAll, SexType.male.name(), n/2));
		}
		else { 
			results.addAll(getControlsBySex(parameterId, strain, organisationId, max, showAll, sex, n));
			
		}
		return results;
	}

	/**
	 * for testing - not for users
	 * @param start
	 * @param length
	 * @param model
	 * @param type
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public List<Map<String, String>> getLinksListForStats(Integer start, Integer length, ObservationType type, List<String>parameterIds) throws IOException, URISyntaxException, SQLException {
		if(start==null)start=0;
		if(length==null)length=100;

		String url = solr.getBaseURL() + "/select?"
			+ "q=" + ObservationService.ExperimentField.OBSERVATION_TYPE + ":" + type
			+ " AND " + ObservationService.ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental"
			+ "&wt=json&indent=true&start=" + start + "&rows=" + length;

		net.sf.json.JSONObject result = JSONRestUtil.getResults(url);
//		System.out.println(result.toString());
		JSONArray resultsArray=JSONRestUtil.getDocArray(result);

//		System.out.println("start="+start+" end="+length);

		List<Map<String, String>> listWithStableId=new ArrayList<Map<String, String>>();
		for(int i=0; i<resultsArray.size(); i++){
			Map<String,String> map=new HashMap<String,String>();
			net.sf.json.JSONObject exp=resultsArray.getJSONObject(i);
			String statbleParamId=exp.getString(ObservationService.ExperimentField.PARAMETER_STABLE_ID);
			String accession=exp.getString(ObservationService.ExperimentField.GENE_ACCESSION);
//			System.out.println(accession+" parameter="+statbleParamId);
			map.put("paramStableId", statbleParamId);
			map.put("accession",accession);
			listWithStableId.add(map);
		}
		return listWithStableId;
	}

	
	
	private  List<ObservationDTO> getControlsBySex(Integer parameterId, String strain, Integer organisationId, Date max, Boolean showAll, String sex, int resultsMaxSize) throws SolrServerException{

		List<ObservationDTO> results = new ArrayList<ObservationDTO>();

		QueryResponse responseb = new QueryResponse();
		QueryResponse responsea = new QueryResponse();

		Date today = new Date();
		Date minDate = new Date();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00");
		SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");

		try {
			minDate = f.parse("01-Jan-2000");
		} catch (ParseException e) {
			e.printStackTrace();
		} // Jan 1, 2000 UTC
		
		SolrQuery queryb = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":control")
			.addFilterQuery(ExperimentField.DATE_OF_EXPERIMENT + ":["+df.format(minDate)+"Z TO "+df.format(max)+"Z]")
			.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
			.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
			.addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
			.addFilterQuery(ExperimentField.SEX + ":" + sex)
			.setSortField(ExperimentField.DATE_OF_EXPERIMENT, ORDER.desc) // good for dates before the date of the experiment
		;
		queryb.setStart(0).setRows(resultsMaxSize);
		//System.out.println(queryb.toString());
		
		SolrQuery querya = new SolrQuery()
		.setQuery("*:*")
		.addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":control")
		.addFilterQuery(ExperimentField.DATE_OF_EXPERIMENT + ":["+df.format(DateUtils.addDays(max, -1))+"Z TO "+df.format(today)+"Z]")
		.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
		.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
		.addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
		.addFilterQuery(ExperimentField.SEX + ":" + sex)
		.setSortField(ExperimentField.DATE_OF_EXPERIMENT, ORDER.asc) // good for dates after the date of the experiment
		;
		querya.setStart(0).setRows(resultsMaxSize);

		responseb = solr.query(queryb);
		responsea = solr.query(querya);
		
		List<ObservationDTO> resA = responsea.getBeans(ObservationDTO.class); // hits AFTER the dateOfExperiment passes
		List<ObservationDTO> resB = responseb.getBeans(ObservationDTO.class); // hits BEFRE the dateOfExperiment passes
		ArrayList<ObservationDTO> closest = new ArrayList<ObservationDTO>();
		long datInMs = max.getTime();
		
		while (results.size() < resultsMaxSize && resA.size() > 0 && resB.size() > 0){
			closest = getClosest(datInMs, resA.get(0), resB.get(0));
			// (b, a)
			if (closest.get(0) != null){
				results.add(closest.get(0));
				resA.remove(0);
			}
			if (closest.get(1) != null){
				results.add(closest.get(1));
				resB.remove(0);
			}
		}

		
		if (results.size() < resultsMaxSize && resA.size() > 0){
			results.addAll(resA);
		}
			
		if (results.size() < resultsMaxSize && resB.size() > 0){
			results.addAll(resB);
		}
		
//		results.addAll(responsea.getBeans(ObservationDTO.class).subList(0, neededAfter));
//		results.addAll(responseb.getBeans(ObservationDTO.class).subList(0, neededBefore));

//		System.out.println(solr.getBaseURL() + querya);
//		System.out.println(solr.getBaseURL() + queryb);
		
//		System.out.println("------Got after : " + sizeA );
// System.out.println("------Got before : " + sizeB );
		// System.out.println("------Return total " + results.size());

		// System.out.println("returning : " + results.size() + " for " + sex);
		// System.out.println("---- I RETURN :" + results.size() + " , " +
		// responsea.getResults().size() + " + " +
		// responseb.getResults().size()) ;
		return results;
	}

	// list <b,a>
	private ArrayList<ObservationDTO> getClosest(long datInMs,
			ObservationDTO aObj, ObservationDTO bObj) {
		ArrayList<ObservationDTO> res = new ArrayList<>();
		long distanceA = aObj.getDateOfExperiment().getTime() - datInMs;
		long distanceB = datInMs - bObj.getDateOfExperiment().getTime();
		if (distanceA == distanceB) {
			res.add(aObj);
			res.add(bObj);
		} else if (distanceA < distanceB) {
			res.add(aObj);
			res.add(null);
		} else if (distanceB < distanceA) {
			res.add(null);
			res.add(bObj);
		}
		return res;
	}

	/**
	 * Return all the unidimensional observations for a given combination of
	 * parameter, gene, zygosity, organisation and strain
	 * 
	 * ex solr query:
	 * parameterId:1116%20AND%20geneAccession:MGI\:1923523%20AND%20
	 * zygosity:homozygote
	 * %20AND%20organisationId:9%20AND%20colonyId:HEPD0550_6_G09
	 * %20AND%20gender:female
	 * 
	 * @param parameterId
	 * @param geneAcc
	 * @param zygosity
	 * @param organisationId
	 * @param strain
	 * @return
	 * @throws SolrServerException
	 */
	public List<ObservationDTO> getUnidimensionalObservationsByParameterGeneAccZygosityOrganisationStrain(
			Integer parameterId, String geneAcc, String zygosity,
			Integer organisationId, String strain) throws SolrServerException {
		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();

		SolrQuery query = new SolrQuery()
				.setQuery(
						ExperimentField.GENE_ACCESSION + ":"
								+ geneAcc.replace(":", "\\:"))
				.addFilterQuery(
						ExperimentField.STRAIN + ":"
								+ strain.replace(":", "\\:"))
				.addFilterQuery(ExperimentField.ZYGOSITY + ":" + zygosity)
				.addFilterQuery(
						ExperimentField.PARAMETER_ID + ":" + parameterId)
				.addFilterQuery(
						ExperimentField.PHENOTYPING_CENTER_ID + ":"
								+ organisationId).setStart(0).setRows(1000);

		QueryResponse response = solr.query(query);
		resultsDTO = response.getBeans(ObservationDTO.class);

		Date recentExperimentDate = new Date(0L); // epoch
		for (ObservationDTO o : resultsDTO) {
			if (o.getDateOfExperiment().after(recentExperimentDate)) {
				recentExperimentDate = o.getDateOfExperiment();
			}
		}

		resultsDTO.addAll(getControls(parameterId, strain, organisationId,
				recentExperimentDate));

		return resultsDTO;
	}

	public String getQueryStringByParameterGeneAccZygosityOrganisationStrain(
			Integer parameterId, String geneAcc, String zygosity,
			Integer organisationId, String strain) throws SolrServerException {

		SolrQuery query = new SolrQuery()
				.setQuery(
						ExperimentField.GENE_ACCESSION + ":"
								+ geneAcc.replace(":", "\\:"))
				.addFilterQuery(ExperimentField.ZYGOSITY + ":" + zygosity)
				.addFilterQuery(
						ExperimentField.PARAMETER_ID + ":" + parameterId)
				.addFilterQuery(
						ExperimentField.PHENOTYPING_CENTER_ID + ":"
								+ organisationId)
				.addFilterQuery(
						ExperimentField.STRAIN + ":"
								+ strain.replace(":", "\\:")).setStart(0)
				.setRows(1000);
		return query.toString();
	}

	/**
	 * construct a query to get all the categortical observations for a given
	 * combination of parameter, gene, zygosity, organisation and strain
	 * 
	 * ex solr query:
	 * parameterId:1116%20AND%20geneAccession:MGI\:1923523%20AND%20
	 * zygosity:homozygote
	 * %20AND%20organisationId:9%20AND%20colonyId:HEPD0550_6_G09
	 * %20AND%20gender:female
	 * 
	 * @param parameterId
	 * @param gene
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
				.addFilterQuery(
						ExperimentField.PARAMETER_ID + ":" + parameterId)
				.addFilterQuery(
						ExperimentField.PHENOTYPING_CENTER_ID + ":"
								+ organisationId)
				.addFilterQuery(
						ExperimentField.STRAIN + ":"
								+ strain.replace(":", "\\:"))
				.addFilterQuery(ExperimentField.SEX + ":" + sex).setStart(0)
				.setRows(10000);
	}

	public String getQueryStringByParameterGeneAccZygosityOrganisationStrainSex(
			Integer parameterId, String geneAcc, String zygosity,
			Integer organisationId, String strain, SexType sex)
			throws SolrServerException {
		return getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(
				parameterId, geneAcc, zygosity, organisationId, strain,
				sex.name()).toString();
	}

	public List<ObservationDTO> getObservationsByParameterGeneAccZygosityOrganisationStrainSex(
			Integer parameterId, String gene, String zygosity,
			Integer organisationId, String strain, SexType sex)
			throws SolrServerException {
		SolrQuery query = getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(
				parameterId, gene, zygosity, organisationId, strain, sex.name());
		return solr.query(query).getBeans(ObservationDTO.class);
	}

	public List<ObservationDTO> getExperimentalUnidimensionalObservationsByParameterGeneAcc(
			Integer parameterId, String geneAccession)
			throws SolrServerException {

		SolrQuery query = new SolrQuery()
				.setQuery(
						ExperimentField.GENE_ACCESSION + ":"
								+ geneAccession.replace(":", "\\:"))
				.addFilterQuery(
						ExperimentField.PARAMETER_ID + ":" + parameterId)
				.addFilterQuery(
						ExperimentField.BIOLOGICAL_SAMPLE_GROUP
								+ ":experimental").setStart(0).setRows(10000);

		return solr.query(query).getBeans(ObservationDTO.class);
	}

	public List<ObservationDTO> getExperimentalUnidimensionalObservationsByParameterGeneAccZygosityOrganisationStrainSex(
			Integer parameterId, String gene, String zygosity,
			Integer organisationId, String strain, SexType sex
			) throws SolrServerException {

		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();
		SolrQuery query = new SolrQuery()
				.setQuery(
						ExperimentField.GENE_ACCESSION + ":"
								+ gene.replace(":", "\\:"))
				.addFilterQuery(
						ExperimentField.PARAMETER_ID + ":" + parameterId)
				.setStart(0).setRows(10000);

		if (zygosity != null && !zygosity.equalsIgnoreCase("null")) {
			query.addFilterQuery(ExperimentField.ZYGOSITY + ":" + zygosity);
		}
		if (strain != null) {
			query.addFilterQuery(ExperimentField.STRAIN + ":"
					+ strain.replace(":", "\\:"));
		}
		if (organisationId != null) {
			query.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":"
					+ organisationId);
		}
		if (sex != null) {
			query.addFilterQuery(ExperimentField.SEX + ":" + sex);
		}
		// MRC Harwell spaces need to be handled with quotes

		QueryResponse response = solr.query(query);
		resultsDTO = response.getBeans(ObservationDTO.class);
		return resultsDTO;
	}

	/**
	 * Return all the parameter ids that have associated data for a given
	 * organisation
	 * 
	 * @param organisation
	 *            the name of the organisation
	 * @return list of integer db keys of the parameter rows
	 * @throws SolrServerException
	 */
	public List<Integer> getUnidimensionalParameterIdsWithObservationsByOrganisationId(
			Integer organisationId) throws SolrServerException {
		Set<Integer> parameterIds = new HashSet<Integer>();

		SolrQuery query = new SolrQuery()
				.setQuery("*:*")
				.addFilterQuery(
						ExperimentField.PHENOTYPING_CENTER_ID + ":"
								+ organisationId)
				.addFilterQuery(
						ExperimentField.OBSERVATION_TYPE + ":unidimensional")
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

		return new ArrayList<Integer>(parameterIds);
	}

	/**
	 * Return all the parameter ids that have associated data for a given
	 * organisation
	 * 
	 * @param organisation
	 *            the name of the organisation
	 * @return list of integer db keys of the parameter rows
	 * @throws SolrServerException
	 */
	public List<Integer> getCategoricalParameterIdsWithObservationsByOrganisationId(
			Integer organisationId) throws SolrServerException {
		Set<Integer> parameterIds = new HashSet<Integer>();

		SolrQuery query = new SolrQuery()
				.setQuery("*:*")
				.addFilterQuery(
						ExperimentField.PHENOTYPING_CENTER_ID + ":"
								+ organisationId)
				.addFilterQuery(
						ExperimentField.OBSERVATION_TYPE + ":categorical")
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

		return new ArrayList<Integer>(parameterIds);
	}

	/**
	 * Return all the gene accession ids that have associated data for a given
	 * organisation, strain, and zygosity
	 * 
	 * @param organisation
	 *            the name of the organisation
	 * @param strain
	 *            the strain
	 * @param zygosity
	 *            the zygosity
	 * @return list of gene accession ids
	 * @throws SolrServerException
	 */
	public List<String> getAllGeneAccessionIdsByParameterIdOrganisationIdStrainZygosity(
			Integer parameterId, Integer organisationId, String strain,
			String zygosity) throws SolrServerException {
		Set<String> genes = new HashSet<String>();

		SolrQuery query = new SolrQuery()
				.setQuery("*:*")
				.addFilterQuery(
						ExperimentField.BIOLOGICAL_SAMPLE_GROUP
								+ ":experimental")
				.addFilterQuery(
						ExperimentField.PHENOTYPING_CENTER_ID + ":"
								+ organisationId)
				.addFilterQuery(
						ExperimentField.PARAMETER_ID + ":" + parameterId)
				.addFilterQuery(
						ExperimentField.STRAIN + ":"
								+ strain.replace(":", "\\:"))
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

		return new ArrayList<String>(genes);
	}

	/**
	 * Return all the strain accession ids that have associated data for a given
	 * organisation ID and parameter ID
	 * 
	 * @param organisation
	 *            ID the database id of the organisation
	 * @param parameterId
	 *            the database id of the parameter
	 * @return list of strain accession ids
	 * @throws SolrServerException
	 */
	public List<String> getStrainsByParameterIdOrganistionId(
			Integer parameterId, Integer organisationId)
			throws SolrServerException {
		Set<String> strains = new HashSet<String>();

		SolrQuery query = new SolrQuery()
				.setQuery("*:*")
				.addFilterQuery(
						ExperimentField.PHENOTYPING_CENTER_ID + ":"
								+ organisationId)
				.addFilterQuery(
						ExperimentField.PARAMETER_ID + ":" + parameterId)
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

		return new ArrayList<String>(strains);

	}

	/**
	 * Return all the organisation ids that have associated observations
	 * 
	 * @param organisation
	 *            the name of the organisation
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

	public List<String> getAllGeneAccessionIdsByParameterIdOrganisationIdStrainZygositySex(
			Integer parameterId, Integer organisationId, String strain,
			String zygosity, String sex) throws SolrServerException {
		Set<String> genes = new HashSet<String>();

		SolrQuery query = new SolrQuery()
				.setQuery("*:*")
				.addFilterQuery(
						ExperimentField.BIOLOGICAL_SAMPLE_GROUP
								+ ":experimental")
				.addFilterQuery(
						ExperimentField.PHENOTYPING_CENTER_ID + ":"
								+ organisationId)
				.addFilterQuery(
						ExperimentField.PARAMETER_ID + ":" + parameterId)
				.addFilterQuery(
						ExperimentField.STRAIN + ":"
								+ strain.replace(":", "\\:"))
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

		return new ArrayList<String>(genes);
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
//			if (meansArray[i] > 20)
//				System.out.println(p.getStableId() + " for colony id :" + gr.getGroupValue() + " (allele : " + allelesArray[i]  + ") 	 mean value = " + meansArray[i]);
			i++;
		System.out.println("adding : " + sum / total);
		}

		// we do the binning for all the data but fill the bins after that to
		// keep tract of phenotype associations
		int binCount = Math.min((int) Math.floor((double) groups.size() / 2),
				20);

		List<Double> upperBounds = new ArrayList<Double>();
		EmpiricalDistribution distribution = new EmpiricalDistribution(binCount);
		System.out.println("--- meansArray: " + meansArray.length);
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
	 * Get all controls for a specified set of center, strain, parameter, and
	 * (optional) sex.
	 * 
	 * @param parameterId
	 * @param strain
	 * @param organisationId
	 * @param sex if null, both sexes are returned
	 * @return
	 * @throws SolrServerException
	 */
	public List<ObservationDTO> getAllControlsBySex(Integer parameterId, String strain, Integer organisationId, String sex) throws SolrServerException {

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

		if(sex!=null) {
			query.addFilterQuery(ExperimentField.SEX + ":" + sex);
		}

		response = solr.query(query);		
		results = response.getBeans(ObservationDTO.class);
		
		return results;
	}

	public List<ObservationDTO> getConcurrentControlsBySex(Integer parameterId, String strain, Integer organisationId, Date experimentDate, String sex) throws SolrServerException {
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

		response = solr.query(query);		
		results = response.getBeans(ObservationDTO.class);
		
		return results;
	}

}
