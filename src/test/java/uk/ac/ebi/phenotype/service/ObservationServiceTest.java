package uk.ac.ebi.phenotype.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Assert;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

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
		List<Map<String, String>> dataset = null;
		String genomicFeatureAcc = "MGI:104874"; // Akt2
		try {
			dataset = os.getDistinctPipelineAlleleCenterListByGeneAccession(genomicFeatureAcc);
			// for (Map<String, String> map: dataset) {
			// for (String key: map.keySet()) {
			// System.out.println(key + ":" + map.get(key));
			// }
			// System.out.println();
			// }
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(dataset.size() > 0);
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
			// for (String key: dataset) {
			// System.out.println(key);
			//
			// }
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(dataset.size() > 0);
	}


	@Test
	public void testGetDistinctParameterListByPipelineAlleleCenter() {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
		List<Map<String, String>> dataset = null;
		String phenotypingCenter = "WTSI";
		String pipelineStableId = "ESLIM_001";
		String alleleAccession = "EUROALL:19";

		try {
			dataset = os.getDistinctParameterListByPipelineAlleleCenter(pipelineStableId, alleleAccession, phenotypingCenter, null);
			// for (Map<String, String> map: dataset) {
			// for (String key: map.keySet()) {
			// System.out.println(key + ":" + map.get(key));
			// }
			// System.out.println();
			// }
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(dataset.size() > 0);
	}


	@Test
	public void testGetDistinctParameterListByPipelineAlleleCenterWithSpaceInName() {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id%3A%22MGI%3A104874%22%20AND%20biological_sample_group:experimental&wt=json&start=0&rows=0&indent=true&facet=true&facet.pivot=pipeline_id,phenotyping_center,allele_accession&facet.limit=-1
		List<Map<String, String>> dataset = null;
		String phenotypingCenter = "MRC Harwell";
		String pipelineStableId = "ESLIM_001";
		String alleleAccession = "MGI:4435468";

		try {
			dataset = os.getDistinctParameterListByPipelineAlleleCenter(pipelineStableId, alleleAccession, phenotypingCenter, null);
			// for (Map<String, String> map: dataset) {
			// for (String key: map.keySet()) {
			// System.out.println(key + ":" + map.get(key));
			// }
			// System.out.println();
			// }
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(dataset.size() > 0);
	}


	@Test
	public void testGetExperimentKeys() {

		Map<String, List<String>> keys = null;
		// http://localhost:8080/phenotype-archivecharts?accession=MGI:1922257?parameterId=ESLIM_003_001_004&zygosity=homozygote
		List<String> phenotypingCenterParamsList = Arrays.asList("WTSI");
		List<String> strainStrings = Arrays.asList("MGPCURATE2");
		List<String> metaDataGoupsList = Arrays.asList("45a983be46dc06a6a3ed8663d3d673ed");
		List<String> pipelineIds = Arrays.asList("ESLIM_001");

		try {
			keys = os.getExperimentKeys("MGI:104874", "ESLIM_005_001_005", pipelineIds, phenotypingCenterParamsList, strainStrings, metaDataGoupsList, null);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("test result keys="+keys);
		assertTrue(keys.size() > 0);
	}


	@Test
	public void testGetDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadata()
	throws SolrServerException {

		List<Map<String, String>> results = os.getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadata();

		for (Map<String, String> result : results) {
			if (result.get("gene_accession_id").equals("MGI:1914982")) {
				for (String k : result.keySet()) {
					System.out.println("k: " + k);
					System.out.println("v: " + result.get(k));
				}
				break;
			}
		}

	}


	@Test
	public void testGetObservationTypeForParameterStableId() {

		try {
			assertTrue(os.getObservationTypeForParameterStableId("GMC_920_001_009") == ObservationType.unidimensional);
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void testGetDistinctStatisticalCandidates()
	throws SolrServerException {

		List<String> phenotypingCenter = Arrays.asList("WTSI", "TCP");
		List<String> pipelineStableId = null;
		List<String> procedureStub = null;
		List<String> parameterStableId = null;
		List<String> alleleAccessionId = null;

		List<Map<String, String>> results = os.getDistinctStatisticalCandidates(phenotypingCenter, pipelineStableId, procedureStub, parameterStableId, alleleAccessionId);
		System.out.println(String.format("Center: %s, Pipeline: %s, Procedure: %s, Parameter: %s, Allele: %s - %s", phenotypingCenter, pipelineStableId, procedureStub, parameterStableId, alleleAccessionId, results.size()));

		pipelineStableId = Arrays.asList("MGP_001", "TCP_001");
		results = os.getDistinctStatisticalCandidates(phenotypingCenter, pipelineStableId, procedureStub, parameterStableId, alleleAccessionId);
		System.out.println(String.format("Center: %s, Pipeline: %s, Procedure: %s, Parameter: %s, Allele: %s - %s", phenotypingCenter, pipelineStableId, procedureStub, parameterStableId, alleleAccessionId, results.size()));

		procedureStub = Arrays.asList("IMPC_CBC", "IMPC_XRY");
		results = os.getDistinctStatisticalCandidates(phenotypingCenter, pipelineStableId, procedureStub, parameterStableId, alleleAccessionId);
		System.out.println(String.format("Center: %s, Pipeline: %s, Procedure: %s, Parameter: %s, Allele: %s - %s", phenotypingCenter, pipelineStableId, procedureStub, parameterStableId, alleleAccessionId, results.size()));

		parameterStableId = Arrays.asList("IMPC_CBC_001_001", "IMPC_XRY_001_001");
		results = os.getDistinctStatisticalCandidates(phenotypingCenter, pipelineStableId, procedureStub, parameterStableId, alleleAccessionId);
		System.out.println(String.format("Center: %s, Pipeline: %s, Procedure: %s, Parameter: %s, Allele: %s - %s", phenotypingCenter, pipelineStableId, procedureStub, parameterStableId, alleleAccessionId, results.size()));

		alleleAccessionId = Arrays.asList("MGI:4820357", "MGI:5548389");
		results = os.getDistinctStatisticalCandidates(phenotypingCenter, pipelineStableId, procedureStub, parameterStableId, alleleAccessionId);
		System.out.println(String.format("Center: %s, Pipeline: %s, Procedure: %s, Parameter: %s, Allele: %s - %s", phenotypingCenter, pipelineStableId, procedureStub, parameterStableId, alleleAccessionId, results.size()));


		Assert.assertTrue(results != null);

	}

	@Test
	public void testGetDistinctCategoricalOrgPipelineParamStrainZygositySexGeneAccessionAlleleAccessionMetadata() throws SolrServerException {

		List<Map<String, String>> dataMapList = os.getDistinctCategoricalOrgPipelineParamStrainZygositySexGeneAccessionAlleleAccessionMetadata();
		assert(dataMapList.size() > 0);
		System.out.println("Data map list is "+ dataMapList.size() + " units long");

	}
}
