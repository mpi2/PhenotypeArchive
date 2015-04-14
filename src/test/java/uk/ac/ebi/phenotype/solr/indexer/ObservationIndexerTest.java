package uk.ac.ebi.phenotype.solr.indexer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.phenotype.solr.indexer.beans.ImpressBean;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-config.xml"})
//@TransactionConfiguration
//@Transactional
public class ObservationIndexerTest {

    @Autowired
    private ObservationIndexer observationIndexer;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("komp2DataSource")
    private DataSource ds;

    @Before
    public void setUp() throws Exception {
    }

    @Test
//@Ignore
    public void testPopulateBiologicalDataMap() throws Exception {
        String args[] = { "--context=index-config_DEV.xml" };
        observationIndexer.initialise(args);

        observationIndexer.populateBiologicalDataMap();
        Map<String, ObservationIndexer.BiologicalDataBean> bioDataMap = observationIndexer.getBiologicalData();
        Assert.assertTrue(bioDataMap.size() > 1000);

        logger.info("Size of biological data map {}", bioDataMap.size());

    }

    @Test
//@Ignore
    public void testPopulateLineBiologicalDataMap() throws Exception {
        String args[] = { "--context=index-config_DEV.xml" };
        observationIndexer.initialise(args);

        observationIndexer.populateLineBiologicalDataMap();
        Map<String, ObservationIndexer.BiologicalDataBean> bioDataMap = observationIndexer.getLineBiologicalData();
        Assert.assertTrue(bioDataMap.size() > 50);

        logger.info("Size of line level biological data map {}", bioDataMap.size());

    }

    @Test
//@Ignore
    public void testImpressDataMaps() throws Exception {
        Map<Integer, ImpressBean> bioDataMap;
        Connection connection = ds.getConnection();

        // Pipelines
        bioDataMap = IndexerMap.getImpressPipelines(connection);
        Assert.assertTrue(bioDataMap.size() > 5);
        logger.info("Size of pipeline data map {}", bioDataMap.size());

        //Procedures
        bioDataMap = IndexerMap.getImpressProcedures(connection);
        Assert.assertTrue(bioDataMap.size() > 20);
        logger.info("Size of procedure data map {}", bioDataMap.size());

        //Parameters
        bioDataMap = IndexerMap.getImpressParameters(connection);
        Assert.assertTrue(bioDataMap.size() > 500);
        logger.info("Size of parameter data map {}", bioDataMap.size());

    }

    @Test
//@Ignore
    public void testDatasourceDataMaps() throws Exception {
        String args[] = { "--context=index-config_DEV.xml" };
        observationIndexer.initialise(args);

        observationIndexer.populateDatasourceDataMap();
        Map<Integer, ObservationIndexer.DatasourceBean> bioDataMap;

        // Project
        bioDataMap = observationIndexer.getProjectMap();
        Assert.assertTrue(bioDataMap.size() > 5);
        logger.info("Size of project data map {}", bioDataMap.size());

        //Datasource
        bioDataMap = observationIndexer.getDatasourceMap();
        Assert.assertTrue(bioDataMap.size() > 10);
        logger.info("Size of datasource data map {}", bioDataMap.size());

    }

    @Test
//@Ignore
    public void testpopulateCategoryNamesDataMap() throws Exception {
        String args[] = { "--context=index-config_DEV.xml" };
        observationIndexer.initialise(args);

        observationIndexer.populateCategoryNamesDataMap();
        Map<String, Map<String, String>> bioDataMap = observationIndexer.getTranslateCategoryNames();

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
