package uk.ac.ebi.phenotype.stats;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.dao.UnidimensionalStatisticsDAO;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalDataObject;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalResultAndCharts;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalSet;
import uk.ac.ebi.phenotype.util.PhenotypeGeneSummaryDTO;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

@Service
public class ObservationService {

	@Autowired
	UnidimensionalStatisticsDAO uDAO;
	
	@Autowired
	PhenotypePipelineDAO parameterDAO;


	@Resource(name="globalConfiguration")
	private Map<String, String> config;
	
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

	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00");

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

	private  List<ObservationDTO> getControlsBySex(Integer parameterId, String strain, Integer organisationId, Date max, Boolean showAll, String sex, int resultsMaxSize) throws SolrServerException{

		List<ObservationDTO> results = new ArrayList<ObservationDTO>();

		QueryResponse responseb = new QueryResponse();
		QueryResponse responsea = new QueryResponse();

		Date today = new Date();
		Date minDate = new Date();

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
//		System.out.println("------Got before : " + sizeB );
//		System.out.println("------Return total " + results.size());

//		System.out.println("returning : " + results.size() + " for " + sex);
//		System.out.println("---- I RETURN :" + results.size() + " , " + responsea.getResults().size() + " + " + responseb.getResults().size()) ;
		return results;
	}

	// list <b,a>
	private ArrayList<ObservationDTO> getClosest (long datInMs, ObservationDTO aObj, ObservationDTO bObj){
		ArrayList<ObservationDTO> res = new ArrayList<>();
		long distanceA = aObj.getDateOfExperiment().getTime() - datInMs;
		long distanceB = datInMs - bObj.getDateOfExperiment().getTime();
		if (distanceA == distanceB){
			res.add(aObj);
			res.add(bObj);
		}
		else if (distanceA < distanceB){
			res.add(aObj);
			res.add(null);
		}
		else if (distanceB < distanceA){
			res.add(null);
			res.add(bObj);
		}
		return res;
	}
	
	/**
	 * Return all the unidimensional observations for a given
	 * combination of parameter, gene, zygosity, organisation and strain
	 *   
	 * ex solr query: parameterId:1116%20AND%20geneAccession:MGI\:1923523%20AND%20zygosity:homozygote%20AND%20organisationId:9%20AND%20colonyId:HEPD0550_6_G09%20AND%20gender:female
	 * 
	 * @param parameterId
	 * @param geneAcc
	 * @param zygosity
	 * @param organisationId
	 * @param strain
	 * @return
	 * @throws SolrServerException
	 */
	public List<ObservationDTO> getUnidimensionalObservationsByParameterGeneAccZygosityOrganisationStrain(Integer parameterId, String geneAcc, String zygosity, Integer organisationId, String strain) throws SolrServerException {
		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();

		SolrQuery query = new SolrQuery()
	    	.setQuery(ExperimentField.GENE_ACCESSION + ":" + geneAcc.replace(":", "\\:"))
			.addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
	    	.addFilterQuery(ExperimentField.ZYGOSITY + ":" + zygosity)
			.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
			.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
	    	.setStart(0)
	    	.setRows(1000);
	
	    QueryResponse response = solr.query(query);
	    resultsDTO = response.getBeans(ObservationDTO.class);
	    
	    Date recentExperimentDate = new Date(0L); //epoch
	    for (ObservationDTO o : resultsDTO) {
	    	if (o.getDateOfExperiment().after(recentExperimentDate)) {
	    		recentExperimentDate=o.getDateOfExperiment();
	    	}
	    }

		resultsDTO.addAll(getControls(parameterId, strain, organisationId, recentExperimentDate));
	    
		return resultsDTO;
	}

	public String getQueryStringByParameterGeneAccZygosityOrganisationStrain(Integer parameterId, String geneAcc, String zygosity, Integer organisationId, String strain) throws SolrServerException {

		SolrQuery query = new SolrQuery()
	    	.setQuery(ExperimentField.GENE_ACCESSION + ":" + geneAcc.replace(":", "\\:"))
	    	.addFilterQuery(ExperimentField.ZYGOSITY + ":" + zygosity)
			.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
			.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
			.addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
	    	.setStart(0)
	    	.setRows(1000);
		return query.toString();
	}

	/**
	 * construct a query to get all the categortical observations for a given
	 * combination of parameter, gene, zygosity, organisation and strain
	 *   
	 * ex solr query: parameterId:1116%20AND%20geneAccession:MGI\:1923523%20AND%20zygosity:homozygote%20AND%20organisationId:9%20AND%20colonyId:HEPD0550_6_G09%20AND%20gender:female
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
	public SolrQuery getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String geneAcc, String zygosity, Integer organisationId, String strain, String sex) throws SolrServerException {

		return new SolrQuery()
	    	.setQuery("((" + ExperimentField.GENE_ACCESSION + ":" + geneAcc.replace(":", "\\:") 
	    			+ " AND " + ExperimentField.ZYGOSITY + ":" + zygosity 
	    			+ ") OR " + ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":control) ")
			.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
			.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
			.addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
			.addFilterQuery(ExperimentField.SEX + ":" + sex)
	    	.setStart(0)
	    	.setRows(10000);
	}

	public String getQueryStringByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String geneAcc, String zygosity, Integer organisationId, String strain, SexType sex) throws SolrServerException {
		return getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(parameterId, geneAcc, zygosity, organisationId, strain, sex.name()).toString();
	}
	
	public List<ObservationDTO> getObservationsByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain, SexType sex) throws SolrServerException {
        SolrQuery query = getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(parameterId, gene, zygosity, organisationId, strain, sex.name());
        return solr.query(query).getBeans(ObservationDTO.class);
	}


	public List<ObservationDTO> getExperimentalUnidimensionalObservationsByParameterGeneAcc(Integer parameterId, String geneAccession) throws SolrServerException {

		SolrQuery query = new SolrQuery()
	    	.setQuery(ExperimentField.GENE_ACCESSION + ":" + geneAccession.replace(":", "\\:"))
			.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
	    	.addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
	    	.setStart(0)
	    	.setRows(10000);
		
		return solr.query(query).getBeans(ObservationDTO.class);
	}

	public List<ObservationDTO> getExperimentalUnidimensionalObservationsByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain, SexType sex, String phenotypingCenter) throws SolrServerException {
		
		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();
		SolrQuery query = new SolrQuery()
			.setQuery(ExperimentField.GENE_ACCESSION + ":" + gene.replace(":", "\\:"))
			.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
			.setStart(0)
			.setRows(10000);

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
		//MRC Harwell spaces need to be handled with quotes
		if (phenotypingCenter != null) {
			query.addFilterQuery(ExperimentField.PHENOTYPING_CENTER + ":\"" + phenotypingCenter+"\"");
		}

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
	public List<Integer> getUnidimensionalParameterIdsWithObservationsByOrganisationId(Integer organisationId) throws SolrServerException {
		Set<Integer> parameterIds = new HashSet<Integer>();

		SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
			.addFilterQuery(ExperimentField.OBSERVATION_TYPE + ":unidimensional")
			.setRows(0)
			.addFacetField(ExperimentField.PARAMETER_ID)
			.setFacet(true)
			.setFacetMinCount(1)
			.setFacetLimit(-1);

		QueryResponse response = solr.query(query);
		List<FacetField> fflist = response.getFacetFields();

		for(FacetField ff : fflist){

			// If there are no face results, the values will be null
			// skip this facet field in that case
			if(ff.getValues()==null) { continue;}

		    for(Count c : ff.getValues()){
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
	public List<Integer> getCategoricalParameterIdsWithObservationsByOrganisationId(Integer organisationId) throws SolrServerException {
		Set<Integer> parameterIds = new HashSet<Integer>();

		SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
			.addFilterQuery(ExperimentField.OBSERVATION_TYPE + ":categorical")
			.setRows(0)
			.addFacetField(ExperimentField.PARAMETER_ID)
			.setFacet(true)
			.setFacetMinCount(1)
			.setFacetLimit(-1);

		QueryResponse response = solr.query(query);
		List<FacetField> fflist = response.getFacetFields();

		for(FacetField ff : fflist){

			// If there are no face results, the values will be null
			// skip this facet field in that case
			if(ff.getValues()==null) { continue;}

		    for(Count c : ff.getValues()){
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
	public List<String> getAllGeneAccessionIdsByParameterIdOrganisationIdStrainZygosity(Integer parameterId, Integer organisationId, String strain, String zygosity) throws SolrServerException {
		Set<String> genes = new HashSet<String>();

		SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
			.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
			.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
			.addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
	    	.addFilterQuery(ExperimentField.ZYGOSITY + ":" + zygosity)
			.setRows(0)
			.addFacetField(ExperimentField.GENE_ACCESSION)
			.setFacet(true)
			.setFacetMinCount(1)
			.setFacetLimit(-1);

		QueryResponse response = solr.query(query);
		List<FacetField> fflist = response.getFacetFields();

		for(FacetField ff : fflist){

			// If there are no face results, the values will be null
			// skip this facet field in that case
			if(ff.getValues()==null) { continue;}

			for(Count c : ff.getValues()){
				genes.add(c.getName());
		    }
		}

		return new ArrayList<String>(genes);
	}
	
	/**
	 * Return all the strain accession ids that have associated data for a given
	 * organisation ID and parameter ID
	 * 
	 * @param organisation ID
	 *            the database id of the organisation
	 * @param parameterId
	 *            the database id of the parameter 
	 * @return list of strain accession ids
	 * @throws SolrServerException
	 */
	public List<String> getStrainsByParameterIdOrganistionId(Integer parameterId, Integer organisationId) throws SolrServerException {
		Set<String> strains = new HashSet<String>();

		SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
			.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
			.setRows(0)
			.addFacetField(ExperimentField.STRAIN)
			.setFacet(true)
			.setFacetMinCount(1)
			.setFacetLimit(-1);

		QueryResponse response = solr.query(query);
		List<FacetField> fflist = response.getFacetFields();

		for(FacetField ff : fflist){

			// If there are no face results, the values will be null
			// skip this facet field in that case
			if(ff.getValues()==null) { continue;}

		    for(Count c : ff.getValues()){
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
	public List<Integer> getAllOrganisationIdsWithObservations() throws SolrServerException {
		List<Integer> organisations = new ArrayList<Integer>();

		SolrQuery query = new SolrQuery()
		.setQuery("*:*")
		.setRows(0)
		.addFacetField(ExperimentField.PHENOTYPING_CENTER_ID)
		.setFacet(true)
		.setFacetMinCount(1)
		.setFacetLimit(-1);

	QueryResponse response = solr.query(query);
	List<FacetField> fflist = response.getFacetFields();

	for(FacetField ff : fflist){

		// If there are no face results, the values will be null
		// skip this facet field in that case
		if(ff.getValues()==null) { continue;}

	    for(Count c : ff.getValues()){
	    	organisations.add(Integer.parseInt(c.getName()));
	    }
	}

		return organisations;
	}

	public List<String> getAllGeneAccessionIdsByParameterIdOrganisationIdStrainZygositySex(Integer parameterId, Integer organisationId, String strain, String zygosity, String sex) throws SolrServerException {
		Set<String> genes = new HashSet<String>();

		SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
			.addFilterQuery(ExperimentField.PHENOTYPING_CENTER_ID + ":" + organisationId)
			.addFilterQuery(ExperimentField.PARAMETER_ID + ":" + parameterId)
			.addFilterQuery(ExperimentField.STRAIN + ":" + strain.replace(":", "\\:"))
	    	.addFilterQuery(ExperimentField.ZYGOSITY + ":" + zygosity)
			.addFilterQuery(ExperimentField.SEX + ":" + sex)
			.setRows(0)
			.addFacetField(ExperimentField.GENE_ACCESSION)
			.setFacet(true)
			.setFacetMinCount(1)
			.setFacetLimit(-1);

		QueryResponse response = solr.query(query);
		List<FacetField> fflist = response.getFacetFields();

		for(FacetField ff : fflist){

			// If there are no face results, the values will be null
			// skip this facet field in that case
			if(ff.getValues()==null) { continue;}

			for(Count c : ff.getValues()){
				genes.add(c.getName());
		    }
		}

		return new ArrayList<String>(genes);
	}

	// gets categorical data for graphs on phenotype page 
	public CategoricalSet getCategories(String parameter, ArrayList<String >genes, String biologicalSampleGroup, ArrayList<String>  strains) throws SolrServerException{

		CategoricalSet resSet = new CategoricalSet();
		resSet.setName(biologicalSampleGroup);
		SolrQuery query = new SolrQuery()
			.addFilterQuery(ExperimentField.BIOLOGICAL_SAMPLE_GROUP + ":" + biologicalSampleGroup)
			.addFilterQuery(ExperimentField.PARAMETER_STABLE_ID + ":" + parameter);

		String q = (strains.size() > 1) ? "("+ExperimentField.STRAIN+":\"" + StringUtils.join(strains.toArray(), "\" OR "+ExperimentField.STRAIN+":\"") + "\")" : ExperimentField.STRAIN+":\"" + strains.get(0) + "\"";

		if (genes != null && genes.size() > 0){
			q += " AND (";
			q += (genes.size() > 1) ? ExperimentField.GENE_ACCESSION+":\"" + StringUtils.join(genes.toArray(), "\" OR "+ExperimentField.GENE_ACCESSION+":\"") + "\"" : ExperimentField.GENE_ACCESSION+":\"" + genes.get(0) + "\"";
			q += ")";
		}

		query.setQuery(q);
		query.set("group.field", ExperimentField.CATEGORY);
		query.set("group", true);
		
		List<String> categories = new ArrayList<String> ();
		List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
		for (Group gr : groups){
			categories.add((String) gr.getGroupValue());
			CategoricalDataObject catObj = new CategoricalDataObject();
			catObj.setCount((long) gr.getResult().getNumFound());
			catObj.setCategory(gr.getGroupValue());
			resSet.add(catObj);
		}
		
		return resSet;
	}
	
	public List<String> getGenesAssocByParamAndMp (String parameterStableId, String phenotype_id, HttpSolrServer solr) throws SolrServerException{
		List<String> res = new ArrayList<String>();
		SolrQuery query = new SolrQuery()
		.setQuery("(mp_term_id:\"" + phenotype_id + "\" OR top_level_mp_term_id:\"" + phenotype_id + "\") AND (strain_accession_id:\"MGI:2159965\" OR strain_accession_id:\"MGI:2164831\") AND parameter_stable_id:\"" + parameterStableId+"\"")
		.setFilterQueries("resource_fullname:EuroPhenome")
		.setRows(10000);	
		query.set("group.field", "marker_accession_id");
		query.set("group", true);
		List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
		for (Group gr : groups){
			if (!res.contains((String)gr.getGroupValue())){
				res.add((String) gr.getGroupValue());
			}
		}
		return res;
	}
		
	public PhenotypeGeneSummaryDTO getPercentages(String phenotype_id) throws SolrServerException{ // <sex, percentage>
		PhenotypeGeneSummaryDTO pgs = new PhenotypeGeneSummaryDTO();
		
		int total = 0;
		int nominator = 0;
		SolrQuery query = new SolrQuery()
		.setQuery("(mp_term_id:\"" + phenotype_id + "\" OR top_level_mp_term_id:\"" + phenotype_id + "\") AND (strain_accession_id:\"MGI:2159965\" OR strain_accession_id:\"MGI:2164831\")")
		.setFilterQueries("resource_fullname:EuroPhenome")
		.setRows(10000);	
		query.set("group.field", "marker_symbol");
		query.set("group", true);
		HttpSolrServer solrgp = getSolrInstance();
		List<String> parameters = parameterDAO.getParameterStableIdsByPhenotypeTerm(phenotype_id);
		// males & females
		QueryResponse results = solrgp.query(query);		
		nominator = results.getGroupResponse().getValues().get(0).getValues().size();
 		total = getTestedGenes(phenotype_id, null, solr, parameters);
 		pgs.setTotalPercentage(100*(float)nominator/(float)total);
		pgs.setTotalGenesAssociated(nominator);
		pgs.setTotalGenesTested(total);
		
		boolean display = (total > 0 && nominator > 0) ? true : false;
		pgs.setDisplay(display);		
		
		if (display){
			//females only
			query.addFilterQuery("sex:female");
			results = solrgp.query(query);
			nominator = results.getGroupResponse().getValues().get(0).getValues().size();

			total = getTestedGenes(phenotype_id, "female", solr, parameters);
			pgs.setFemalePercentage(100*(float)nominator/(float)total);
			pgs.setFemaleGenesAssociated(nominator);
			pgs.setFemaleGenesTested(total);
			
			//males only
			SolrQuery q = new SolrQuery()
			.setQuery("(mp_term_id:\"" + phenotype_id + "\" OR top_level_mp_term_id:\"" + phenotype_id + "\") AND (strain_accession_id:\"MGI:2159965\" OR strain_accession_id:\"MGI:2164831\")")
			.setFilterQueries("resource_fullname:EuroPhenome")
			.setRows(10000);
			q.set("group.field", "marker_symbol");
			q.set("group", true);
			q.addFilterQuery("sex:male");
			results = solrgp.query(q);
			nominator = results.getGroupResponse().getValues().get(0).getValues().size();
			
			total = getTestedGenes(phenotype_id, "male", solr, parameters);
			pgs.setMalePercentage(100*(float)nominator/(float)total);
			pgs.setMaleGenesAssociated(nominator);
			pgs.setMaleGenesTested(total);
		}
		return pgs;
	}
	
	private int getTestedGenes(String phenotypeId, String sex, HttpSolrServer solr, List<String> parameters) throws SolrServerException{

	    List<String> genes = new ArrayList<String>();
		for (String parameter : parameters){
			SolrQuery query = new SolrQuery()
			.setQuery("parameter_stable_id:" + parameter)
			.addField("gene_accession")
			.setFilterQueries("strain:\"MGI:2159965\" OR strain:\"MGI:2164831\"")
			.setRows(10000);
			query.set("group.field", "gene_accession");
			query.set("group", true);
			if (sex != null){
				query.addFilterQuery("sex:"+sex);
			}
			// I need to add the genes to a hash in case some come up multiple times from different parameters
//			System.out.println("=====" + solr.getBaseURL() + query);
			List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
			for (Group gr : groups){
//				System.out.println(gr.getGroupValue());
				if (!genes.contains((String)gr.getGroupValue())){
					genes.add((String) gr.getGroupValue());
				}
			}
		}		
//		System.out.println("tested genes: " + genes.size());
		return genes.size();
	}

	

	public List<CategoricalResultAndCharts> getCategoricalDataOverviewCharts(String mpId, Model model) throws SolrServerException, IOException, URISyntaxException, SQLException{
		List<CategoricalResultAndCharts> listOfChartsAndResults = new ArrayList<>();//one object for each parameter

		List<String> parameters = parameterDAO.getParameterStableIdsByPhenotypeTerm(mpId);
		long time = System.currentTimeMillis();
		List<ExperimentDTO> experimentList = new ArrayList<ExperimentDTO> ();
		CategoricalChartAndTableProvider cctp = new CategoricalChartAndTableProvider();
		HttpSolrServer solr = getSolrInstance();
		ArrayList<String> strains = new ArrayList<>();
		strains.add("MGI:2159965");
		strains.add("MGI:2164831");
		for (String parameter : parameters) {
			// get all genes associated with mpId because of parameter
			Parameter p = parameterDAO.getParameterByStableIdAndVersion(parameter, 1, 0);
			if(p != null && Utilities.checkType(p).equals(ObservationType.categorical)){
				List<String> genes = getGenesAssocByParamAndMp(parameter, mpId, solr);
				if (genes.size() > 0){
					CategoricalSet controlSet = getCategories(parameter, null , "control", strains);
					controlSet.setName("Control");
					CategoricalSet mutantSet = getCategories(parameter, (ArrayList<String>) genes, "experimental", strains);
					mutantSet.setName("Mutant");
					listOfChartsAndResults.add(cctp.doCategoricalDataOverview(controlSet, mutantSet, model, parameter, p.getName()+" ("+parameter+")"));
				}
			}
		}
//		log.info("Generating the overview graphs took " + (System.currentTimeMillis() - time) + " milliseconds.") ;
		return listOfChartsAndResults;
	}
	private HttpSolrServer getSolrInstance(){
		String solrBaseUrl = config.get("internalSolrUrl") + "/" + "genotype-phenotype";
		Proxy proxy; 
		HttpSolrServer server = null;
		try {
			proxy = (new HttpProxy()).getProxy(new URL(solrBaseUrl));
			if (proxy != null) {
				DefaultHttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				server = new HttpSolrServer(solrBaseUrl, client);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if(server == null){
			server = new HttpSolrServer(solrBaseUrl);
		}
		return server;
	}
}
