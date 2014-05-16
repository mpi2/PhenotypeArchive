package uk.ac.ebi.phenotype.stats;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;

import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAOImpl;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class GenotypePhenotypeServiceTest {

	@Autowired
	private GenotypePhenotypeService genotypePhenotypeService;
	String testGene = "MGI:104874";
        
         @Autowired
	private PhenotypePipelineDAO pDAO;
		
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
        
        @Test
        public void getResultsForGeneHeatMapTest(){
            String accession="MGI:104874";
            Parameter param = pDAO.getParameterByStableId("ESLIM_022_001_707");//check this param exists first
            List<String> params=new ArrayList<>();
            params.add(param.getStableId());
            assertTrue(genotypePhenotypeService.getResultsForGeneHeatMap(accession, params).getAccession().equals(accession));
            //http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype/select/?q=marker_accession_id:%22MGI:104874%22&rows=10000000&version=2.2&start=0&indent=on&wt=json&sort=p_value%20asc
        }
		
}
