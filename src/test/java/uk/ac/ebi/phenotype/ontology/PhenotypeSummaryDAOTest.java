package uk.ac.ebi.phenotype.ontology;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class PhenotypeSummaryDAOTest  extends AbstractTransactionalJUnit4SpringContextTests{
	

	@Autowired
	private PhenotypeSummaryDAO phenotypeSummary;
	@Autowired 
	GenotypePhenotypeService gpService;
	String testGene = "MGI:104874";
	
//	@Test 
	public void testPhenotypeSummaryForAllGenes(){
		System.out.println( ">> testPhenotypeSummaryForAllGenes");
		try {
			System.out.println(phenotypeSummary.getSummaryObjects("*").getFemalePhenotypes().size());
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		System.out.println(">> done.");
	}
	

	@Test
	public void testGetSexesRepresentationForPhenotypesSet() throws MalformedURLException, SolrServerException{
		HashMap<String, String> summary;
		summary = gpService.getTopLevelMPTerms(testGene);	
		for (String id: summary.keySet()){
			
			SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(testGene, id);
			String sex = phenotypeSummary.getSexesRepresentationForPhenotypesSet(resp);
			assertTrue(sex != null);
			assertTrue(sex.equalsIgnoreCase("male") || sex.equalsIgnoreCase("female") || sex.equalsIgnoreCase("both sexes"));
			}
		
	}

	@Test
	public void testGetDataSourcesForPhenotypesSet() throws MalformedURLException, SolrServerException{
		HashMap<String, String> summary;
		summary = gpService.getTopLevelMPTerms(testGene);	
		for (String id: summary.keySet()){
			SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(testGene, id);
			HashSet<String> dataSources = phenotypeSummary.getDataSourcesForPhenotypesSet(resp);
			assertTrue(dataSources != null);
		}
	}
	
	@Test
	public void testNonExistingGeneName() throws SolrServerException, MalformedURLException{
		System.out.println("Testing inexisting gene name...");
		String gene = "ilincasMadeUpGene";
		phenotypeSummary = new PhenotypeSummaryDAOImpl();
		try {
			assertTrue(phenotypeSummary.getSummaryObjects(gene)==null);
//			assertFalse(phenotypeSummary.getSummaryObjects(gene).getBothPhenotypes().size() > 0 ||
//					phenotypeSummary.getSummaryObjects(gene).getMalePhenotypes().size() > 0 ||
//					phenotypeSummary.getSummaryObjects(gene).getFemalePhenotypes().size() > 0);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	
	//removed this test until the mammalian phenotype top level issue is resolved
//	@Test
//	public void testGetSummaryObjectsForAllGenesInSolr() throws MalformedURLException, SolrServerException{
//
//		String topLevelsMP = "MP:0001186$MP:0002006$MP:0002873$MP:0003012$MP:0003631$MP:0005367$MP:0005369$MP:0005370$MP:0005371$MP:0005375$MP:0005376$MP:0005"
//				+ "377$MP:0005378$MP:0005379$MP:0005380$MP:0005381$MP:0005382$MP:0005384$MP:0005385$MP:0005386$MP:0005387$MP:0005388$MP:0005389$MP:000"
//				+ "5390$MP:0005391$MP:0005394$MP:0005395$MP:0005397$MP:0010768$MP:0010771$";
//		
//		int noSexDocs_temp = 0;
//		long allDocs = 0;
//		// put the genes in a hashSet to get rid of duplicates
//		
//		HashMap<String, String> summary;
//		for (String gene: gpService.getAllGenes()){
//			System.out.println("Test gene: " + gene);			
//			//test getTopLevelMPTerms
//			summary = gpService.getTopLevelMPTerms(gene);	
//			assertTrue(summary.size() > 0);	// we're sure there are entries for gene Akt2
//			for (String id : summary.keySet()) { 
//				assertTrue("MP top level id must start with \'MP\'", id.startsWith("MP"));	// these should be only MP ids, not something else
//				// check it is indeed a top level term
//				assertTrue(gene+" MP id returned as top level seems it is actually not top level: " + id , topLevelsMP.contains(id));
//			}
//			// test getPhenotypesForTopLevelTerm
//			for (String id: summary.keySet()){
//				SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id);
//				assertTrue (resp != null);
//				assertTrue (resp.size() > 0);
//			}
//			// test getSexesRepresentationForPhenotypesSet
//			for (String id: summary.keySet()){
//				SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id);
//				String sex = phenotypeSummary.getSexesRepresentationForPhenotypesSet(resp);
//				if (sex == null){
//					System.out.println("Sex field missing: " + gene + " " + id);
//					noSexDocs_temp += resp.getNumFound();
//				assertTrue(sex != null);
//				assertTrue(sex.equalsIgnoreCase("male") || sex.equalsIgnoreCase("female") || sex.equalsIgnoreCase("both sexes"));	
//					System.out.println("+++" + noSexDocs_temp);
//				}
//			}
//			// test getDataSourcesForPhenotypesSet
//			for (String id: summary.keySet()){
//				SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id);
//				HashSet<String> dataSources = phenotypeSummary.getDataSourcesForPhenotypesSet(resp);
//				assertTrue(dataSources != null);
//			}
//			
//			// test getSummaryObjects for all
//			try {
//				phenotypeSummary.getSummaryObjects(gene);
//			} catch (Exception e) {
//				e.printStackTrace();
//				fail();
//			}
//		}
//
//	}	
	
}
