package uk.ac.ebi.phenotype.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import uk.ac.ebi.phenotype.dao.HibernateDAO;
import uk.ac.ebi.phenotype.dao.HibernateDAOImpl;

public interface PhenotypeSummaryDAO {
	
	public abstract HashMap<String, String> getTopLevelMPTerms (String gene) throws SolrServerException ;
	
	public abstract SolrDocumentList getPhenotypesForTopLevelTerm(String gene, String mpID) throws SolrServerException;
	
	// returns one of {male, female, both sexess} for each set of phenotypes 
	public abstract String getSexesRepresentationForPhenotypesSet(
			SolrDocumentList resp);
	
	// Returns a string concatenation of all data sources for a given set
	public abstract HashSet<String> getDataSourcesForPhenotypesSet(
			SolrDocumentList resp);
	
	public abstract String getSummary (
			String gene) throws SolrServerException ;
	
	public abstract PhenotypeSummaryBySex getSummaryObjects(String gene) throws Exception;
	
	public void instantiateSolrServer();
	
}
