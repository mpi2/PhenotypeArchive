package uk.ac.ebi.phenotype.stats;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.stats.graphs.GraphUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
@TransactionConfiguration
@Transactional
public class ExperimentServiceTest {

	@Autowired
	private ExperimentService es;

	@Ignore
	@Test
	public void testGetExperimentDTO() throws SolrServerException, IOException, URISyntaxException {
		List<ExperimentDTO> exp = es.getExperimentDTO("ESLIM_001_001_158", "MGI:1920194");
		System.out.println(exp);
		assertTrue(exp.size()>0);
		System.out.println(exp.get(0));
		
		exp = es.getExperimentDTO(2043, "MGI:1349215");
		System.out.println(exp);
		assertTrue(exp.size()>0);
		System.out.println(exp.get(0));
		
	}

	@Test
	public void testGetExperimentDTO2() throws SolrServerException, IOException, URISyntaxException {
		List<ExperimentDTO> exp = es.getExperimentDTO("ESLIM_003_001_011", "MGI:1922257");
		System.out.println(exp);
		assertTrue(exp.size()>0);
		System.out.println(exp.get(0));
		
	}
	
//	@Test
//	public void testGetGraphUrls() {
//		GraphUtils graphUtils=new GraphUtils(es);
//		try {
//			graphUtils.getGraphUrls("MGI:1922257", "ESLIM_003_001_004");
//		} catch (SolrServerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
