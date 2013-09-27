package uk.ac.ebi.phenotype.stats;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.UnidimensionalStatisticsDAO;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

@Service
public class ObservationService {

	@Autowired
	UnidimensionalStatisticsDAO uDAO;

	private String solrURL = "http://localhost:8080/solr/experiment";
	private HttpSolrServer solr;

	public ObservationService() {
		solrURL = "http://localhost:8080/solr/experiment";
		solr = new HttpSolrServer(solrURL);
	}

	public ObservationService(String solrUrl) {
		this.solrURL=solrUrl;
		solr = new HttpSolrServer(solrURL);
	}


	protected List<ObservationDTO> getControls(Integer parameterId,
			String strain, Integer organisationId) throws SolrServerException {
		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();

		SolrQuery query = new SolrQuery()
	    	.setQuery("*:*")
	    	.addFilterQuery("biologicalSampleGroup:control")
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("organisationId:"+organisationId)
	    	.addFilterQuery("strain:"+strain.replace(":", "\\:"))
	    	;
	    query.setStart(0).setRows(10000);
	
	    QueryResponse response = solr.query(query);
	    resultsDTO = response.getBeans(ObservationDTO.class);

		return resultsDTO;
	}

	/**
	 * 
	 * construct a query to get all the unidimensional observations for a given
	 * combination of parameter, gene, zygosity, organisation and strain
	 *   
	 * ex solr query: parameterId:1116%20AND%20geneAccession:MGI\:1923523%20AND%20zygosity:homozygote%20AND%20organisationId:9%20AND%20colonyId:HEPD0550_6_G09%20AND%20gender:female
	 * 
	 * @param parameterId
	 * @param gene
	 * @param zygosity
	 * @param organisationId
	 * @param strain
	 * @return
	 * @throws SolrServerException
	 */
	public List<ObservationDTO> getUnidimensionalObservationsByParameterGeneAccZygosityOrganisationStrain(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain) throws SolrServerException {
		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();

		SolrQuery query = new SolrQuery()
	    	.setQuery("geneAccession:"+gene.replace(":", "\\:") + " AND zygosity:"+zygosity+"")
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("organisationId:"+organisationId)
	    	.addFilterQuery("strain:"+strain.replace(":", "\\:"))
	    	;
	    query.setStart(0).setRows(1000);
	
	    QueryResponse response = solr.query(query);
	    resultsDTO = response.getBeans(ObservationDTO.class);
	    
	    Date max = new Date(0L); //epoch
	    for (ObservationDTO o : resultsDTO) {
	    	if (o.getDateOfExperiment().after(max)) {
	    		max=o.getDateOfExperiment();
	    	}
	    }

	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00");

		query = new SolrQuery()
			.setQuery("biologicalSampleGroup:control AND dateOfExperiment:["+df.format(new Date(0L))+"Z TO "+df.format(max)+"Z]")
			.addFilterQuery("parameterId:"+parameterId)
			.addFilterQuery("organisationId:"+organisationId)
			.addFilterQuery("strain:"+strain.replace(":", "\\:"))
			.setSortField("dateOfExperiment", ORDER.desc)
			;
		query.setStart(0).setRows(1000);
		response = solr.query(query);

		resultsDTO.addAll(response.getBeans(ObservationDTO.class));

	    
		return resultsDTO;
	}

	/**
	 * 
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
	public List<ObservationDTO> getCategoricalObservationsByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain, String sex) throws SolrServerException {

		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();

		SolrQuery query = new SolrQuery()
	    	.setQuery("((geneAccession:"+gene.replace(":", "\\:") + " AND zygosity:"+zygosity+") OR biologicalSampleGroup:control) ")
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("organisationId:"+organisationId)
	    	.addFilterQuery("strain:"+strain.replace(":", "\\:"))
	    	.addFilterQuery("gender:"+sex)
	    	;
	    query.setStart(0).setRows(10000);    

	    QueryResponse response = solr.query(query);
	    resultsDTO = response.getBeans(ObservationDTO.class);

		return resultsDTO;
	}

	
	/**
	 * Method to return all the experiments for a given combination of parameter and gene organised into
	 * discrete experiments by strain, origanisation, etc.
	 * 
	 * @param parameterId
	 * @param geneAccession
	 * @return set of experiment DTOs
	 * @throws SolrServerException 
	 */
	public List<ExperimentDTO> getExperimentDTO(Integer parameterId, String geneAccession) throws SolrServerException {

		List<ObservationDTO> results = new ArrayList<ObservationDTO>();
		SolrQuery query = new SolrQuery()
	    	.setQuery("geneAccession:"+geneAccession.replace(":", "\\:"))
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("biologicalSampleGroup:experimental")
	    	.setStart(0)
	    	.setRows(10000);
	    results = solr.query(query).getBeans(ObservationDTO.class);

	    // 
	    Map<String, ExperimentDTO> experimentsMap = new HashMap<String, ExperimentDTO>();

	    for (ObservationDTO observation : results) {

	    	// collect all the strains, organisations, sexes, and zygosities 
	    	// combinations of the mutants to get the controls later

	    	// Experiment ID is a combination of 
	    	// - organisation
	    	// - strain
	    	// - parameter
	    	// - gene
	    	// - zygosity
	    	ExperimentDTO experiment;
	    	
	    	String experimentKey = observation.getOrganisation()
	    			+ observation.getStrain()
	    			+ observation.getParameterStableId()
	    			+ observation.getGeneAccession();

	    	if (experimentsMap.containsKey(experimentKey)) {
	    		experiment = experimentsMap.get(experimentKey);
	    	} else {
	    		experiment = new ExperimentDTO();
	    		experiment.setExperimentId(experimentKey);
	    		experiment.setHomozygoteMutants(new HashSet<ObservationDTO>());
	    		experiment.setHeterozygoteMutants(new HashSet<ObservationDTO>());

	    		//Tree sets to keep "female" before "male" and "hetero" before "hom"
	    		experiment.setSexes(new TreeSet<SexType>());
	    		experiment.setZygosities(new TreeSet<ZygosityType>());
	    	}

	    	if (experiment.getGeneMarker() == null) {
	    		experiment.setGeneMarker(observation.getGeneSymbol());
	    	}

	    	if (experiment.getParameterStableId() == null) {
	    		experiment.setParameterStableId(observation.getParameterStableId());
	    	}

	    	if (experiment.getOrganisation() == null) {
	    		experiment.setOrganisation(observation.getOrganisation());
	    	}

	    	if (experiment.getStrain() == null) {
	    		experiment.setStrain(observation.getStrain());
	    	}
	    	if(experiment.getExperimentalBiologicalModelId()==null) {
	    		experiment.setExperimentalBiologicalModelId(observation.getBiologicalModelId());
	    	}

    		experiment.getZygosities().add(ZygosityType.valueOf(observation.getZygosity()));
     		experiment.getSexes().add(SexType.valueOf(observation.getSex()));
    	
     		if (experiment.getResult()==null && experiment.getControlBiologicalModelId()==null && experiment.getExperimentalBiologicalModelId()==null) {
     			experiment.setResult((StatisticalResult) uDAO.getUnidimensionalResultByParameterIdAndBiologicalModelIds(observation.getParameterId(), experiment.getControlBiologicalModelId(), experiment.getExperimentalBiologicalModelId()));
     		}
	    	
	    	if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.heterozygote) || ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.hemizygote)) {
	    		// NOTE: in the stats analysis we collapse hom and hemi together
		    	experiment.getHeterozygoteMutants().add(observation);	    		
	    	} else if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.homozygote)) {
	    		experiment.getHomozygoteMutants().add(observation);
	    	}

	    	if (experiment.getControls() == null) {
	    		experiment.setControls(new HashSet<ObservationDTO>());
	    		List<ObservationDTO> controls = getControls(observation.getParameterId(), observation.getStrain(), observation.getOrganisationId() );
	    		experiment.getControls().addAll(controls);
	    		
	    		if(experiment.getControlBiologicalModelId()==null && controls.size()>0) {
		    		experiment.setControlBiologicalModelId(controls.get(0).getBiologicalModelId());
		    	}
	    	}

	    	experimentsMap.put(experimentKey, experiment);

	    }
	    
		return new ArrayList<ExperimentDTO>(experimentsMap.values());	
	}

}
