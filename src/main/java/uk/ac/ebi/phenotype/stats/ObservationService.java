package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

public class ObservationService {

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
	    	.setQuery("((geneAccession:"+gene.replace(":", "\\:") + " AND zygosity:"+zygosity+") OR biologicalSampleGroup:control) ")
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
	Set<ExperimentDTO> getExpermentDTO(Integer parameterId, String geneAccession) throws SolrServerException {
		Set<ExperimentDTO> experiments = new HashSet<ExperimentDTO>();

		List<ObservationDTO> results = new ArrayList<ObservationDTO>();
		SolrQuery query = new SolrQuery()
	    	.setQuery("geneAccession:"+geneAccession.replace(":", "\\:"))
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("biologicalSampleGroup:experiment")
	    	.setStart(0)
	    	.setRows(10000);
	    results = solr.query(query).getBeans(ObservationDTO.class);

	    // 
	    Map<Integer, ExperimentDTO> experimentsMap = new HashMap<Integer, ExperimentDTO>();

	    for (ObservationDTO observation : results) {
	    	// collect all the strains, organisations, sexes, and zygosities 
	    	// combinations of the mutants to get the controls later

	    	ExperimentDTO experiment;

	    	if (experimentsMap.containsKey(observation.getExperimentId())) {
	    		experiment = experimentsMap.get(observation.getExperimentId());
	    	} else {
	    		experiment = new ExperimentDTO();
	    		experiment.setExperimentId(observation.getExperimentId());
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

	    }

		return experiments;
	}
}
