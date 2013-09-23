package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

	
	public List<ObservationDTO> getUnidimensionalObservationsByParameterGeneAccZygosityOrganisationStrain(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain) throws SolrServerException {
		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();

		//parameterId:1116%20AND%20geneAccession:MGI\:1923523%20AND%20zygosity:homozygote%20AND%20organisationId:9%20AND%20colonyId:HEPD0550_6_G09%20AND%20gender:female
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

	
	public List<ObservationDTO> getCategoricalObservationsByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain, String gender) throws SolrServerException {

		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();
		
//parameterId:1116%20AND%20geneAccession:MGI\:1923523%20AND%20zygosity:homozygote%20AND%20organisationId:9%20AND%20colonyId:HEPD0550_6_G09%20AND%20gender:female
		SolrQuery query = new SolrQuery()
	    	.setQuery("((geneAccession:"+gene.replace(":", "\\:") + " AND zygosity:"+zygosity+") OR biologicalSampleGroup:control) ")
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("organisationId:"+organisationId)
	    	.addFilterQuery("strain:"+strain.replace(":", "\\:"))
	    	.addFilterQuery("gender:"+gender)
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
	 */
	Set<ExperimentDTO> getExpermentDTO(Integer parameterId, String geneAccession) {
		return new HashSet<ExperimentDTO>();
	}
}
