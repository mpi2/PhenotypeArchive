package uk.ac.ebi.phenotype.stats;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
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

import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummarySolrImpl;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
@TransactionConfiguration
@Transactional
public class PhenotypeCallSummarySolrTest {

	@Autowired
	PhenotypeCallSummarySolrImpl dao;

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPhenotypeCallByAccession() throws IOException, URISyntaxException {
		
		String markerAccession="MGI:104874";
		PhenotypeFacetResult phenotypesResult = dao.getPhenotypeCallByGeneAccession(markerAccession);
		List<PhenotypeCallSummary> phenotypes = phenotypesResult.getPhenotypeCallSummaries();
		System.out.println(phenotypes.size());
		System.out.println(phenotypes.get(0).getPhenotypeTerm().getDescription());
		

	}

}
