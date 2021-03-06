package uk.ac.ebi.phenotype.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class GenotypePhenotypeServiceTest {

	@Autowired
	@Qualifier("postqcService")
	private PostQcService genotypePhenotypeService;
	String testGene = "MGI:104874";
        
         @Autowired
	private PhenotypePipelineDAO pDAO;
		
	//@Test
	public void testGetTopLevelMPTerms() throws MalformedURLException {
		HashMap<String, String> summary;
		
		try {
			summary = genotypePhenotypeService.getTopLevelMPTerms(testGene, null);	
			System.out.println(summary);
			assertTrue(summary.size() > 0);	// we're sure there are entries for gene Akt2
			for (String id : summary.keySet()) { 
				assertTrue(id.startsWith("MP"));	// these should be only MP ids, not something else
			}
		} catch (SolrServerException e) {
			fail(e.getMessage()); 
		}
	}
	
	//@Test
	public void testGetPhenotypesForTopLevelTerm() throws MalformedURLException, SolrServerException{
		HashMap<String, String> summary;
		summary = genotypePhenotypeService.getTopLevelMPTerms(testGene, null);	
		for (String id: summary.keySet()){
			SolrDocumentList resp = genotypePhenotypeService.getPhenotypesForTopLevelTerm(testGene, id, null);
			assertTrue (resp != null);
		}
	}
	
	@Test
	public void testGetAllTopLevelsByPhenotypingCenterAndColonies() throws SolrServerException {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
		//GenotypePhenotypeDTO.PHENOTYPING_CENTER, mpTermAcc, mpTermName, GenotypePhenotypeDTO.COLONY_ID, GenotypePhenotypeDTO.MARKER_SYMBOL, GenotypePhenotypeDTO.MARKER_ACCESSION_ID

		List<GenotypePhenotypeDTO> dataset = null;
		String resource = "IMPC";
		dataset = genotypePhenotypeService.getAllMPByPhenotypingCenterAndColonies(resource);
		System.out.println("Top level terms");
		for (GenotypePhenotypeDTO map: dataset) {
			System.out.println(" Phenotyping center:" + map.getPhenotypingCenter());
			System.out.println(" Marker accession id:" + map.getMarkerAccessionId());
			System.out.println(" Marker symbol:" + map.getMarkerSymbol());
			System.out.println(" Colony ID:" + map.getColonyId());
			System.out.println();
		}
		assertTrue(dataset.size()>0);
	}
	
	@Test
	public void testGetIntermediateTopLevelsByPhenotypingCenterAndColonies() throws SolrServerException {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
		List<GenotypePhenotypeDTO> dataset = null;
		String resource = "IMPC";
		dataset = genotypePhenotypeService.getAllMPByPhenotypingCenterAndColonies(resource);
		System.out.println("Intermediate level terms");
		for (GenotypePhenotypeDTO map: dataset) {
			System.out.println(" Phenotyping center:" + map.getPhenotypingCenter());
			System.out.println(" Marker accession id:" + map.getMarkerAccessionId());
			System.out.println(" Marker symbol:" + map.getMarkerSymbol());
			System.out.println(" Colony ID:" + map.getColonyId());
			System.out.println();
		}

		assertTrue(dataset.size()>0);
	}	
        
	
	@Test
	public void testGetAllMPLeavesByPhenotypingCenterAndColonies() throws SolrServerException {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
		List<GenotypePhenotypeDTO> dataset = null;
		String resource = "IMPC";
		dataset = genotypePhenotypeService.getAllMPByPhenotypingCenterAndColonies(resource);
		System.out.println("Mp terms");
		for (GenotypePhenotypeDTO map: dataset) {
			System.out.println(" Phenotyping center:" + map.getPhenotypingCenter());
			System.out.println(" Marker accession id:" + map.getMarkerAccessionId());
			System.out.println(" Marker symbol:" + map.getMarkerSymbol());
			System.out.println(" Colony ID:" + map.getColonyId());
			System.out.println();
		}
		assertTrue(dataset.size()>0);
	}	
	
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
//            GeneRowForHeatMap row = genotypePhenotypeService.getResultsForGeneHeatMap(accession,gf,  paramIds);
//            assertTrue(row.getAccession().equals(accession));
//            assertFalse(row.getXAxisToCellMap().get("ESLIM_022_001_707").getMpTermName().equals(""));
//            assertFalse(row.getXAxisToCellMap().get("ESLIM_022_001_707").getpValue()==null);
//            assertTrue(row.getXAxisToCellMap().get("ESLIM_022_001_707").getpValue() < new Float(1));//this should have a significant p value or does at the moment of writing this test
//            //http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype/select/?q=marker_accession_id:%22MGI:104874%22&rows=10000000&version=2.2&start=0&indent=on&wt=json&sort=p_value%20asc
//        }
	
	 @Test
	    public void testGetAllPhenotypes() throws SolrServerException {
	        System.out.println("run testGetAllPhenotypes");
	        
	        Set<String> phenotypes =  genotypePhenotypeService.getAllPhenotypesWithGeneAssociations();
	        
	        if (phenotypes == null) {
	            fail("GenotypePhenotypeService.getAllPhenotypes() returned null!");
	        } else {
	            System.out.println("testGetAllPhenotypes: " + phenotypes.size() + " phenotypes found.");
	            assertTrue("Expected at least 100 genotypes.", phenotypes.size() >= 100);
	        }
	    }
}
