package uk.ac.ebi.phenotype.stats;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAOImpl;

public class GenotypePhenotypeServiceTest {

	@Autowired
	private GenotypePhenotypeService phenotypeSummary;
	String testGene = "MGI:104874";
		
	@Test
	public void testGetTopLevelMPTerms() throws MalformedURLException {
		HashMap<String, String> summary;
		
		try {
			summary = phenotypeSummary.getTopLevelMPTerms(testGene);	
			System.out.println(summary);
			assertTrue(summary.size() > 0);	// we're sure there are entries for gene Akt2
			for (String id : summary.keySet()) { 
				assertTrue(id.startsWith("MP"));	// these should be only MP ids, not something else
			}
		} catch (SolrServerException e) {
			fail(e.getMessage()); 
		}
	}
	
	@Test
	public void testGetPhenotypesForTopLevelTerm() throws MalformedURLException, SolrServerException{
		HashMap<String, String> summary;
		summary = phenotypeSummary.getTopLevelMPTerms(testGene);	
		for (String id: summary.keySet()){
			SolrDocumentList resp = phenotypeSummary.getPhenotypesForTopLevelTerm(testGene, id);
			assertTrue (resp != null);
		}
	}
		
}
