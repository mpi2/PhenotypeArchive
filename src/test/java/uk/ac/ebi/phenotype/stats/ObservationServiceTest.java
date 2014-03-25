package uk.ac.ebi.phenotype.stats;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
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
		
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
		List<Map<String,String>> dataset = null;
		String genomicFeatureAcc = "MGI:104874"; // Akt2
			try {
				dataset = os.getDistinctPipelineAlleleCenterListByGeneAccession(genomicFeatureAcc);
				for (Map<String, String> map: dataset) {
					for (String key: map.keySet()) {
						System.out.println(key + ":" + map.get(key));
					}
					System.out.println();
				}
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		assertTrue(dataset.size()>0);
	}
	
	@Test
	public void testGetDistinctProcedureListByPipelineAlleleCenter() {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
		List<String> dataset = null;
		String phenotypingCenter = "WTSI"; 
		String pipelineStableId = "ESLIM_001";
		String alleleAccession = "EUROALL:19";

		try {
			dataset = os.getDistinctProcedureListByPipelineAlleleCenter(pipelineStableId, alleleAccession, phenotypingCenter);
			for (String key: dataset) {
				System.out.println(key);

			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(dataset.size()>0);
	}

	@Test
	public void testGetDistinctParameterListByPipelineAlleleCenter() {
		
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
		List<Map<String,String>> dataset = null;
		String phenotypingCenter = "WTSI"; 
		String pipelineStableId = "ESLIM_001";
		String alleleAccession = "EUROALL:19";
		
			try {
				dataset = os.getDistinctParameterListByPipelineAlleleCenter(pipelineStableId, alleleAccession, phenotypingCenter, null);
				for (Map<String, String> map: dataset) {
					for (String key: map.keySet()) {
						System.out.println(key + ":" + map.get(key));
					}
					System.out.println();
				}
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
		List phenotypingCenterParamsList=Arrays.asList("WTSI");
		List<String> strainStrings=Arrays.asList("MGI:2164831");
		List<String> metaDataGoupsList=Arrays.asList("a9dde727aa843954ce9e35ec95495025");
		
			try {
				keys = os.getExperimentKeys("MGI:1924893","ESLIM_010_001_002", phenotypingCenterParamsList, strainStrings, metaDataGoupsList);
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		assertTrue(keys.size()>0);
	}
	
	@Test
	public void testGetAllGeneAccessionIdsByParameterIdOrganisationStrainZygosity() throws SolrServerException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion("M-G-P_009_001_002", 1, 1);
		List<String> genes = os.getAllGeneAccessionIdsByParameterIdOrganisationIdStrainZygosity(p.getId(), 3,"EUROCURATE1983", "homozygote");
		assertTrue(genes.size()>0);

	}
	
	@Test
	public void testGetAllStrainsByParameterIdOrganistion() throws SolrServerException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion("M-G-P_009_001_002", 1, 1);
		List<String> strains = os.getStrainsByParameterIdOrganistionId(p.getId(), 3);
		assertTrue(strains.size()>0);
		
	}
	
	@Test
	public void testGetAllOrganisationIdsWithObservations() throws SolrServerException {
		List<Integer> organisationIds = os.getAllOrganisationIdsWithObservations();
		assertTrue(organisationIds.size()>0);
	}

}
