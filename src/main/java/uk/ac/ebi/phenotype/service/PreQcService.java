package uk.ac.ebi.phenotype.service;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;


public class PreQcService extends AbstractGenotypePhenotypeService {
	
	public PreQcService(String solrUrl, PhenotypePipelineDAO pipelineDao) {
		solr = new HttpSolrServer(solrUrl);
		pipelineDAO = pipelineDao;
		isPreQc = true; 
	}
	
	public PreQcService() {
		super();
	}
}
