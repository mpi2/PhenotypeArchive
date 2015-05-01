package uk.ac.ebi.phenotype.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class StatisticalResultServiceTest {

	@Autowired
	private StatisticalResultService statisticalResultService;
	String testGene = "MGI:104874";
        
	@Autowired
	private PhenotypePipelineDAO pDAO;




//	@Test
//	public void testGetTopLevelMPTerms() throws MalformedURLException {
//		HashMap<String, String> summary;
//		
//		try {
//			summary = genotypePhenotypeService.getTopLevelMPTerms(testGene);	
//			System.out.println(summary);
//			assertTrue(summary.size() > 0);	// we're sure there are entries for gene Akt2
//			for (String id : summary.keySet()) { 
//				assertTrue(id.startsWith("MP"));	// these should be only MP ids, not something else
//			}
//		} catch (SolrServerException e) {
//			fail(e.getMessage()); 
//		}
//	}
//	
//	@Test
//	public void testGetPhenotypesForTopLevelTerm() throws MalformedURLException, SolrServerException{
//		HashMap<String, String> summary;
//		summary = genotypePhenotypeService.getTopLevelMPTerms(testGene);	
//		for (String id: summary.keySet()){
//			SolrDocumentList resp = genotypePhenotypeService.getPhenotypesForTopLevelTerm(testGene, id);
//			assertTrue (resp != null);
//		}
//	}
        
//        @Test
//        public void getResultsForGeneHeatMapTest(){
//            String accession="MGI:104874";
//            //Parameter param = pDAO.getParameterByStableId("ESLIM_022_001_707");//check this param exists first
//            List<String> paramIds=new ArrayList<>();
//            List<Parameter> parameters=new ArrayList<>();
//            paramIds.add("ESLIM_021_001_003");
//            paramIds.add("ESLIM_022_001_707");
//            paramIds.add("ESLIM_022_001_708");
//            paramIds.add("ESLIM_005_001_001");
//            paramIds.add("ESLIM_020_001_001");
//            
//            for(String stableId: paramIds){
//            	Parameter parameter = pDAO.getParameterByStableId(stableId);
//            	parameters.add(parameter);
//            }
//            GenomicFeature gf=new GenomicFeature();
//            gf.setSymbol("AKT2");
//            //gf.setAccession();
//            GeneRowForHeatMap row = genotypePhenotypeService.getResultsForGeneHeatMap(accession,gf,  parameters);
//            assertTrue(row.getAccession().equals(accession));
//            assertFalse(row.getXAxisToCellMap().get("ESLIM_022_001_707").getMpTermName().equals(""));
//            assertFalse(row.getXAxisToCellMap().get("ESLIM_022_001_707").getpValue()==null);
//            assertTrue(row.getXAxisToCellMap().get("ESLIM_022_001_707").getpValue() < new Float(1));//this should have a significant p value or does at the moment of writing this test
//               }
		
}
