package uk.ac.ebi.phenotype.service;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.AssertTrue;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.dao.OrganisationDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
@TransactionConfiguration
@Transactional
public class ObservationServiceTest {

    @Autowired
    private ObservationService os;
    
    @Autowired
    private PhenotypePipelineDAO parameterDAO;
    
    @Autowired
    private OrganisationDAO organisationDAO;

    @Test
    public void testGetDistinctPipelineAlleleCenterListByGeneAccession() {
        
        // http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
        List<Map<String,String>> dataset = null;
        String genomicFeatureAcc = "MGI:104874"; // Akt2
            try {
                dataset = os.getDistinctPipelineAlleleCenterListByGeneAccession(genomicFeatureAcc);
//              for (Map<String, String> map: dataset) {
//                  for (String key: map.keySet()) {
//                      System.out.println(key + ":" + map.get(key));
//                  }
//                  System.out.println();
//              }
            } catch (SolrServerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        assertTrue(dataset.size()>0);
    }
    
    @Test
    public void testGetDistinctProcedureListByPipelineAlleleCenter() {

        // http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
        List<String> dataset = null;
        String phenotypingCenter = "WTSI"; 
        String pipelineStableId = "ESLIM_001";
        String alleleAccession = "EUROALL:19";

        try {
            dataset = os.getDistinctProcedureListByPipelineAlleleCenter(pipelineStableId, alleleAccession, phenotypingCenter);
//          for (String key: dataset) {
//              System.out.println(key);
//
//          }
        } catch (SolrServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertTrue(dataset.size()>0);
    }

    @Test
    public void testGetDistinctParameterListByPipelineAlleleCenter() {
        
        // http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
        List<Map<String,String>> dataset = null;
        String phenotypingCenter = "WTSI"; 
        String pipelineStableId = "ESLIM_001";
        String alleleAccession = "EUROALL:19";
        
            try {
                dataset = os.getDistinctParameterListByPipelineAlleleCenter(pipelineStableId, alleleAccession, phenotypingCenter, null);
//              for (Map<String, String> map: dataset) {
//                  for (String key: map.keySet()) {
//                      System.out.println(key + ":" + map.get(key));
//                  }
//                  System.out.println();
//              }
            } catch (SolrServerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        assertTrue(dataset.size()>0);
    }
    @Test
    public void testGetDistinctParameterListByPipelineAlleleCenterWithSpaceInName() {
        
        // http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
        List<Map<String,String>> dataset = null;
        String phenotypingCenter = "MRC Harwell"; 
        String pipelineStableId = "ESLIM_001";
        String alleleAccession = "MGI:4435468";
        
            try {
                dataset = os.getDistinctParameterListByPipelineAlleleCenter(pipelineStableId, alleleAccession, phenotypingCenter, null);
//              for (Map<String, String> map: dataset) {
//                  for (String key: map.keySet()) {
//                      System.out.println(key + ":" + map.get(key));
//                  }
//                  System.out.println();
//              }
            } catch (SolrServerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        assertTrue(dataset.size()>0);
    }   
    
    @Test
    public void testGetExperimentKeys() {
        Map<String,List<String>> keys=null;
        //http://localhost:8080/phenotype-archivecharts?accession=MGI:1922257?parameterId=ESLIM_003_001_004&zygosity=homozygote
        List<String> phenotypingCenterParamsList=Arrays.asList("WTSI");
        List<String> strainStrings=Arrays.asList("MGPCURATE2");
        List<String> metaDataGoupsList=Arrays.asList("45a983be46dc06a6a3ed8663d3d673ed");
        List<String> pipelineIds=Arrays.asList("ESLIM_001");
        
            try {
                keys = os.getExperimentKeys("MGI:104874", "ESLIM_005_001_005", pipelineIds, phenotypingCenterParamsList, strainStrings, metaDataGoupsList, null);
            } catch (SolrServerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//          System.out.println("test result keys="+keys);
        assertTrue(keys.size()>0);
    }

    @Test
    public void testGetDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadata() throws SolrServerException {
        List<Map<String, String>> results = os.getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadata();
        
        for (Map<String, String> result : results) {
            if(result.get("gene_accession_id").equals("MGI:1914982")) {
                for (String k : result.keySet()) {
                    System.out.println("k: "+k);
                    System.out.println("v: "+result.get(k));
                }
                break;              
            }
        }
        
    }

    
    @Test
    public void testGetObservationTypeForParameterStableId(){
    	try {
			assertTrue(os.getObservationTypeForParameterStableId("GMC_920_001_009") == ObservationType.unidimensional);
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
    }
 
//    @Test
//    public void testGetAllGenesWithMeasuresForParameter(){
//    	Future<ArrayList<String>> res = null;
//        System.out.println("About to run...");
//        try{
//        	res = os.getAllGenesWithMeasuresForParameter("ESLIM_003_001_011", null);
//        	System.out.println("And the result of get() is " + res.get());
//        }catch(Exception e){
//        	e.printStackTrace();
//        }
//        Assert.assertTrue(res != null);
// 
//    }
	
}

