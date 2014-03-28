package uk.ac.ebi.phenotype.pojo;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
public class PhenotypeCallSummarySolrImplTest {

	@Autowired
	private PhenotypeCallSummarySolrImpl phenotypeCallSummarySolrImpl;
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPhenotypeCallByGeneAccession() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPhenotypeCallByMPAccession() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPhenotypeCallByMPAccessionAndFilter() {
		try {
			PhenotypeFacetResult result = phenotypeCallSummarySolrImpl.getPhenotypeCallByMPAccessionAndFilter("MP:0005390", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void testGetPhenotypeCallByGeneAccessionAndFilter() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStatisticalResultFor() {
		fail("Not yet implemented");
	}

}
