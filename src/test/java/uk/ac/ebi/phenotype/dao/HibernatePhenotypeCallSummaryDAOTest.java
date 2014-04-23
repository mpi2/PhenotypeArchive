package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;

@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class HibernatePhenotypeCallSummaryDAOTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private PhenotypeCallSummaryDAO phenotypeCallSummaryDAO;

	@Test
	public void testGetPhenotypeCallSummaries() {
		List<PhenotypeCallSummary> summaries = phenotypeCallSummaryDAO.getPhenotypeCallByAccession("MGI:98373", 3);
		assertTrue("Number of summaries", summaries.size() >= 11);
	}

}
