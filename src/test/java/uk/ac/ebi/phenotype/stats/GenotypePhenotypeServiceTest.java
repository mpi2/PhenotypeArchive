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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAOImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
public class GenotypePhenotypeServiceTest {

	@Autowired
	private GenotypePhenotypeService genotypePhenotypeService;
	String testGene = "MGI:104874";
		
	@Test
	public void testGetTopLevelMPTerms() throws MalformedURLException {
		HashMap<String, String> summary;
		
		try {
			summary = genotypePhenotypeService.getTopLevelMPTerms(testGene);	
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
		summary = genotypePhenotypeService.getTopLevelMPTerms(testGene);	
		for (String id: summary.keySet()){
			SolrDocumentList resp = genotypePhenotypeService.getPhenotypesForTopLevelTerm(testGene, id);
			assertTrue (resp != null);
		}
	}
		
}
