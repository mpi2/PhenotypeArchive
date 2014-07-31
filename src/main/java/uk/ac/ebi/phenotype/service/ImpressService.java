package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import uk.ac.ebi.phenotype.service.dto.ImpressDTO;

/**
 * Wrapper around the pipeline core.
 * 
 * @author tudose
 * 
 */

public class ImpressService {

	private final HttpSolrServer solr;


	public ImpressService() {

		this("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment"); // default
	}


	public ImpressService(String solrUrl) {

		solr = new HttpSolrServer(solrUrl);
	}


	public List<ImpressDTO> getObjectsByProcedureStableId(String prcedureStableId) {

		try {
			SolrQuery query = new SolrQuery();
			query.setQuery(ImpressDTO.PROCEDURE_STABLE_ID + ":\"" + prcedureStableId + "\"");

			return solr.query(query).getBeans(ImpressDTO.class);

		} catch (SolrServerException e) {
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * 
	 * @param pipelineStableId
	 * @return first 10 objects
	 */
	public List<ImpressDTO> getObjectsByPipelineStableId(String pipelineStableId) {

		try {
			SolrQuery query = new SolrQuery();
			query.setQuery(ImpressDTO.PIPELINE_STABLE_ID + ":\"" + pipelineStableId + "\"");
			return solr.query(query).getBeans(ImpressDTO.class);
		} catch (SolrServerException e) {
			e.printStackTrace();
		}

		return null;
	}

}
