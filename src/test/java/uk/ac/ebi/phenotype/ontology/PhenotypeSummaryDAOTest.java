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
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;

public class PhenotypeSummaryDAOTest {
	
	@Autowired
	private PhenotypeSummaryDAOImpl phenotypeSummaryDAO;
	String testGene = "MGI:104874";
	
	@Test
	public void testConnection() {
		try {
			phenotypeSummaryDAO = new PhenotypeSummaryDAOImpl("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype");
		} catch (MalformedURLException e) {
			fail("connection to the server failed");
		}
		assertTrue(phenotypeSummaryDAO != null);
	}
	
	@Test
	public void testGetTopLevelMPTerms() throws MalformedURLException {
		HashMap<String, String> summary;
		phenotypeSummaryDAO = new PhenotypeSummaryDAOImpl("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype");
		try {
			summary = phenotypeSummaryDAO.getTopLevelMPTerms(testGene);	
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
		phenotypeSummaryDAO = new PhenotypeSummaryDAOImpl("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype");
		summary = phenotypeSummaryDAO.getTopLevelMPTerms(testGene);	
		for (String id: summary.keySet()){
			SolrDocumentList resp = phenotypeSummaryDAO.getPhenotypesForTopLevelTerm(testGene, id);
			assertTrue (resp != null);
		}
	}
	
	@Test
	public void testGetSexesRepresentationForPhenotypesSet() throws MalformedURLException, SolrServerException{
		HashMap<String, String> summary;
		phenotypeSummaryDAO = new PhenotypeSummaryDAOImpl("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype");
		summary = phenotypeSummaryDAO.getTopLevelMPTerms(testGene);	
		for (String id: summary.keySet()){
			SolrDocumentList resp = phenotypeSummaryDAO.getPhenotypesForTopLevelTerm(testGene, id);
			String sex = phenotypeSummaryDAO.getSexesRepresentationForPhenotypesSet(resp);
			assertTrue(sex != null);
			assertTrue(sex.equalsIgnoreCase("male") || sex.equalsIgnoreCase("female") || sex.equalsIgnoreCase("both sexes"));
		}
	}
	
	@Test
	public void testGetDataSourcesForPhenotypesSet() throws MalformedURLException, SolrServerException{
		HashMap<String, String> summary;
		phenotypeSummaryDAO = new PhenotypeSummaryDAOImpl("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype");
		summary = phenotypeSummaryDAO.getTopLevelMPTerms(testGene);	
		for (String id: summary.keySet()){
			SolrDocumentList resp = phenotypeSummaryDAO.getPhenotypesForTopLevelTerm(testGene, id);
			HashSet<String> dataSources = phenotypeSummaryDAO.getDataSourcesForPhenotypesSet(resp);
			assertTrue(dataSources != null);
		}
	}
	
	@Test
	public void testGetSummaryObjectsForAllGenesInSolr() throws MalformedURLException, SolrServerException{

		phenotypeSummaryDAO = new PhenotypeSummaryDAOImpl("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype");
		String topLevelsMP = "MP:0001186$MP:0002006$MP:0002873$MP:0003012$MP:0003631$MP:0005367$MP:0005369$MP:0005370$MP:0005371$MP:0005375$MP:0005376$MP:0005"
				+ "377$MP:0005378$MP:0005379$MP:0005380$MP:0005381$MP:0005382$MP:0005384$MP:0005385$MP:0005386$MP:0005387$MP:0005388$MP:0005389$MP:000"
				+ "5390$MP:0005391$MP:0005394$MP:0005395$MP:0005397$MP:0010768$MP:0010771$";
		// get list of all genes
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("marker_accession_id:*");
		solrQuery.setRows(1000000);
		solrQuery.setFields("marker_accession_id");
		QueryResponse rsp = null;
		SolrServer server = phenotypeSummaryDAO.getServer();
		rsp = server.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		assertTrue(res.size() > 0); // otherwise maybe field name has changed
		
		int noSexDocs_temp = 0;
		long allDocs = 0;
		// put the genes in a hashSet to get rid of duplicates
		HashSet<String> allGenes = new HashSet<String>();
		for (SolrDocument doc: res){
			allGenes.add((String) doc.getFieldValue("marker_accession_id"));
		}
		HashMap<String, String> summary;
		for (String gene: allGenes){
			System.out.println("Test gene: " + gene);			
			//test getTopLevelMPTerms
			summary = phenotypeSummaryDAO.getTopLevelMPTerms(gene);	
			assertTrue(summary.size() > 0);	// we're sure there are entries for gene Akt2
			for (String id : summary.keySet()) { 
				assertTrue("MP top level id must start with \'MP\'", id.startsWith("MP"));	// these should be only MP ids, not something else
				// check it is indeed a top level term
				assertTrue("MP id returned as top level seems it is actually not top level: " + id , topLevelsMP.contains(id));
			}
			// test getPhenotypesForTopLevelTerm
			for (String id: summary.keySet()){
				SolrDocumentList resp = phenotypeSummaryDAO.getPhenotypesForTopLevelTerm(gene, id);
				assertTrue (resp != null);
				assertTrue (resp.size() > 0);
			}
			// test getSexesRepresentationForPhenotypesSet
			for (String id: summary.keySet()){
				SolrDocumentList resp = phenotypeSummaryDAO.getPhenotypesForTopLevelTerm(gene, id);
				String sex = phenotypeSummaryDAO.getSexesRepresentationForPhenotypesSet(resp);
//				if (sex == null){
//					System.out.println("Sex field missing: " + gene + " " + id);
//					noSexDocs_temp += resp.getNumFound();
////				assertTrue(sex != null);
////				assertTrue(sex.equalsIgnoreCase("male") || sex.equalsIgnoreCase("female") || sex.equalsIgnoreCase("both sexes"));	
//					System.out.println("+++" + noSexDocs_temp);
//				}
			}
			// test getDataSourcesForPhenotypesSet
			for (String id: summary.keySet()){
				SolrDocumentList resp = phenotypeSummaryDAO.getPhenotypesForTopLevelTerm(gene, id);
				HashSet<String> dataSources = phenotypeSummaryDAO.getDataSourcesForPhenotypesSet(resp);
				assertTrue(dataSources != null);
			}
			
			// test getSummaryObjects for all
			try {
				phenotypeSummaryDAO.getSummaryObjects(gene);
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}

	}	
	
	@Test
	public void testNonExistingGeneName() throws SolrServerException, MalformedURLException{
		System.out.println("Testing inexisting gene name...");
		String gene = "ilincasMadeUpGene";
		phenotypeSummaryDAO = new PhenotypeSummaryDAOImpl("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype");
		try {
			assertTrue(phenotypeSummaryDAO.getSummaryObjects(gene).getBothPhenotypes().size() > 0 ||
					phenotypeSummaryDAO.getSummaryObjects(gene).getMalePhenotypes().size() > 0 ||
					phenotypeSummaryDAO.getSummaryObjects(gene).getFemalePhenotypes().size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
