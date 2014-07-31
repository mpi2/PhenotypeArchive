package uk.ac.ebi.phenotype.service;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.FacetParams;

import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.service.dto.ImpressDTO;

/**
 * Wrapper around the pipeline core. 
 * @author tudose
 *
 */

public class ImpressService {


	@Resource(name="globalConfiguration")
	private Map<String, String> config;

	private final HttpSolrServer solr;


	public ImpressService() {
		this("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment"); // default
	}


	public ImpressService(String solrUrl) {
		solr = new HttpSolrServer(solrUrl);
	}
	
	
	public String getProcedureStableKey(String prcedureStableId){

		try {
			SolrQuery query = new SolrQuery();
			query.setQuery(ImpressDTO.PROCEDURE_STABLE_ID + ":\"" + prcedureStableId + "\"");

			QueryResponse response = solr.query(query);
			return response.getResults().get(0).getFieldValue(ImpressDTO.PROCEDURE_STABLE_KEY).toString();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getProcedureUrl(String procedureStabeId){
		return config.get("drupalBaseUrl") + "/impress/impress/displaySOP/" + getProcedureStableKey(procedureStabeId);
	}
}
