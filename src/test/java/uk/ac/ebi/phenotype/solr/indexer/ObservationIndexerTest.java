package uk.ac.ebi.phenotype.solr.indexer;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.solr.indexer.ObservationIndexer;

import javax.sql.DataSource;

import java.sql.Connection;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-config.xml"})
@TransactionConfiguration
@Transactional
public class ObservationIndexerTest {

	private static final Logger logger = LoggerFactory.getLogger(ObservationIndexerTest.class);

	@Autowired
	private DataSource ds;

	private Connection connection;

	@Before
	public void setUp() throws Exception {
		connection = ds.getConnection();
	}


	@Test
	public void testPopulateBiologicalDataMap() throws Exception {
		ObservationIndexer e = new ObservationIndexer(connection);

		e.populateBiologicalDataMap();
		Map<String, ObservationIndexer.BiologicalDataBean> bioDataMap = e.getBiologicalData();
		Assert.assertTrue(bioDataMap.size() > 1000);

		logger.info("Size of biological data map {}", bioDataMap.size());

	}

	@Test
	public void testPopulateLineBiologicalDataMap() throws Exception {
		ObservationIndexer e = new ObservationIndexer(connection);

		e.populateLineBiologicalDataMap();
		Map<String, ObservationIndexer.BiologicalDataBean> bioDataMap = e.getLineBiologicalData();
		Assert.assertTrue(bioDataMap.size() > 50);

		logger.info("Size of line level biological data map {}", bioDataMap.size());

	}


	@Test
	public void testImpressDataMaps() throws Exception {
		ObservationIndexer e = new ObservationIndexer(connection);

		e.populateImpressDataMap();
		Map<Integer, ObservationIndexer.ImpressBean> bioDataMap;

		// Pipelines
		bioDataMap = e.getPipelineMap();
		Assert.assertTrue(bioDataMap.size() > 5);
		logger.info("Size of pipeline data map {}", bioDataMap.size());

		//Procedures
		bioDataMap = e.getProcedureMap();
		Assert.assertTrue(bioDataMap.size() > 20);
		logger.info("Size of procedure data map {}", bioDataMap.size());

		//Parameters
		bioDataMap = e.getParameterMap();
		Assert.assertTrue(bioDataMap.size() > 500);
		logger.info("Size of parameter data map {}", bioDataMap.size());

	}

	@Test
	public void testDatasourceDataMaps() throws Exception {
		ObservationIndexer e = new ObservationIndexer(connection);

		e.populateDatasourceDataMap();
		Map<Integer, ObservationIndexer.DatasourceBean> bioDataMap;

		// Project
		bioDataMap = e.getProjectMap();
		Assert.assertTrue(bioDataMap.size() > 5);
		logger.info("Size of project data map {}", bioDataMap.size());

		//Datasource
		bioDataMap = e.getDatasourceMap();
		Assert.assertTrue(bioDataMap.size() > 10);
		logger.info("Size of datasource data map {}", bioDataMap.size());

	}

	@Test
	public void testpopulateCategoryNamesDataMap() throws Exception {
		ObservationIndexer e = new ObservationIndexer(connection);

		e.populateCategoryNamesDataMap();
		Map<String, Map<String, String>> bioDataMap = e.getTranslateCategoryNames();

		Assert.assertTrue(bioDataMap.size() > 5);
		logger.info("Size of translated category map {}", bioDataMap.size());

		Assert.assertTrue(bioDataMap.containsKey("M-G-P_008_001_020"));
		logger.info("Translated map contains key for M-G-P_008_001_020");

		Assert.assertTrue(bioDataMap.get("M-G-P_008_001_020").get("0").equals("Present"));
		logger.info("M-G-P_008_001_020 correctly mapped '0' to 'Present'");

		Assert.assertTrue(bioDataMap.get("M-G-P_008_001_020").get("1").equals("Absent"));
		logger.info("M-G-P_008_001_020 correctly mapped '1' to 'Absent'");

		Assert.assertTrue(bioDataMap.get("ESLIM_008_001_014").get("0").equals("No response"));
		logger.info("ESLIM_008_001_014 correctly mapped '0' to 'No response'");

		Assert.assertTrue(bioDataMap.get("ESLIM_008_001_014").get("1").equals("Response to touch"));
		logger.info("ESLIM_008_001_014 correctly mapped '1' to 'Response to touch'");

		Assert.assertTrue(bioDataMap.get("ESLIM_008_001_014").get("2").equals("Flees prior to touch"));
		logger.info("ESLIM_008_001_014 correctly mapped '2' to 'Flees prior to touch'");

		Assert.assertTrue(bioDataMap.get("M-G-P_008_001_007").get("0").equals("Extended Freeze(over 5 seconds)"));
		logger.info("ESLIM_008_001_014 correctly mapped '0' to 'Extended Freeze(over 5 seconds)'");

		Assert.assertTrue(bioDataMap.get("M-G-P_008_001_007").get("1").equals("Brief freeze followed by movement"));
		logger.info("ESLIM_008_001_014 correctly mapped '1' to 'Brief freeze followed by movement'");

		Assert.assertTrue(bioDataMap.get("M-G-P_008_001_007").get("2").equals("Immediate movement"));
		logger.info("ESLIM_008_001_014 correctly mapped '2' to 'Immediate movement'");

	}

}
