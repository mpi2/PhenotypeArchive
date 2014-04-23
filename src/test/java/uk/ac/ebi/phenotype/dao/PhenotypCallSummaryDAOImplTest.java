package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
@TransactionConfiguration
@Transactional
public class PhenotypCallSummaryDAOImplTest {

	@Autowired
	private PhenotypeCallSummaryDAO phenotypeCallSummaryDAO;
	
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
	public void testGetAllPhenotypeCallSummaries() {
		List<PhenotypeCallSummary> summaries = phenotypeCallSummaryDAO.getAllPhenotypeCallSummaries();
		assertTrue(summaries.size() > 100);
	}

	@Test
	public void testGetPhenotypeCallByAccession() {
		List<PhenotypeCallSummary> summaries = phenotypeCallSummaryDAO.getPhenotypeCallByAccession("MGI:98373");
		assertTrue(summaries.size() > 0);
		for (PhenotypeCallSummary summary : summaries) {
			assertTrue(summary.getParameter().getName() != null);
		}
	}

	@Test
	public void testGetPhenotypeCallByMPAccession() {
		List<PhenotypeCallSummary> summaries = phenotypeCallSummaryDAO.getPhenotypeCallByMPAccession("MP:0001304", 5);
		assertTrue(summaries.size() > 0);
		for (PhenotypeCallSummary summary : summaries) {
			assertTrue(summary.getParameter().getName() != null);
		}
	}

}
