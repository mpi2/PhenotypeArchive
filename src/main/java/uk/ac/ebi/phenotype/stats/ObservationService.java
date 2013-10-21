package uk.ac.ebi.phenotype.stats;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.dao.UnidimensionalStatisticsDAO;
import uk.ac.ebi.phenotype.pojo.Parameter;

@Service
public class ObservationService {

	@Autowired
	UnidimensionalStatisticsDAO uDAO;
	
	@Autowired
	PhenotypePipelineDAO parameterDAO;
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00");

	private HttpSolrServer solr;

	public ObservationService() {
		String solrURL = "http://localhost:8080/solr/experiment"; //default
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
	protected List<ObservationDTO> getControls(Integer parameterId, String strain, Integer organisationId, Date max) throws SolrServerException {

		SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery("biologicalSampleGroup:control")
			.addFilterQuery("dateOfExperiment:["+df.format(new Date(0L))+"Z TO "+df.format(max)+"Z]")
			.addFilterQuery("parameterId:"+parameterId)
			.addFilterQuery("organisationId:"+organisationId)
			.addFilterQuery("strain:"+strain.replace(":", "\\:"))
			.setSortField("dateOfExperiment", ORDER.desc)
			;
		query.setStart(0).setRows(1000);
		QueryResponse response = solr.query(query);

		return response.getBeans(ObservationDTO.class);
	}

	/**
	 * Return all the unidimensional observations for a given
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

		resultsDTO.addAll(getControls(parameterId, strain, organisationId, max));
	    
		return resultsDTO;
	}

	public String getUnidimensionalQueryStringByParameterGeneAccZygosityOrganisationStrain(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain) throws SolrServerException {

		SolrQuery query = new SolrQuery()
	    	.setQuery("geneAccession:"+gene.replace(":", "\\:") + " AND zygosity:"+zygosity+"")
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("organisationId:"+organisationId)
	    	.addFilterQuery("strain:"+strain.replace(":", "\\:"))
	    	.setStart(0)
	    	.setRows(1000);
		return query.toString();
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

	public List<ObservationDTO> getExperimentalUnidimensionalObservationsByParameterGeneAcc(Integer parameterId, String geneAccession) throws SolrServerException {

		SolrQuery query = new SolrQuery()
	    	.setQuery("geneAccession:"+geneAccession.replace(":", "\\:"))
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("biologicalSampleGroup:experimental")
	    	.setStart(0)
	    	.setRows(10000);

		return solr.query(query).getBeans(ObservationDTO.class);
	}

	

}
