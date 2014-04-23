package uk.ac.ebi.phenotype.stats;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.Pipeline;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
@TransactionConfiguration
@Transactional
public class PipelineProcedureTablesCreatorTest {
	
	@Autowired
	private GenomicFeatureDAO genesDao;

	@Autowired
	private PhenotypeCallSummaryDAO phenotypeCallSummaryDAO;

	@Autowired
	private PhenotypePipelineDAO pipelineDAO;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testcreateArraysForTables() {
		String acc="MGI:104874";
		GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
		
		List<PhenotypeCallSummary> allPhenotypeSummaries = phenotypeCallSummaryDAO.getPhenotypeCallByAccession(acc);
		

		//a method here to get a general page for Gene and all procedures associated
		List<Pipeline> pipelines = pipelineDAO.getAllPhenotypePipelines();
		PipelineProcedureTablesCreator creator=new PipelineProcedureTablesCreator();
		List<PipelineProcedureData> dataForTables=creator.createArraysForTables(pipelines, allPhenotypeSummaries, gene);

		assertTrue(allPhenotypeSummaries.size() > 0);
		assertTrue(dataForTables.size() > 0);

	}

}
