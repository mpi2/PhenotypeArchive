package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.generic.util.SolrIndex;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
@TransactionConfiguration
@Transactional
public class SolrIndexTest {

	@Autowired
	private SolrIndex solrIndex;

	@Resource(name="globalConfiguration")
	private Map<String, String> config;


	@Test
	public void testGetGeneStatus() throws IOException, URISyntaxException {
		String status = null;
		status = solrIndex.getGeneStatus("MGI:1891374");
		assertTrue("Mice Produced".equals(status));
	}

}