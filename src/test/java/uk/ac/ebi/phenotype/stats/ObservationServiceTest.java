package uk.ac.ebi.phenotype.stats;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
@TransactionConfiguration
@Transactional
public class ObservationServiceTest {

	@Autowired
	private ObservationService os;

	@Test
	public void testGetControls() throws SolrServerException {
		List<ObservationDTO> test = os.getControls(1267, "MGI:3028467", 7, new Date(0L));
		assertTrue(test.size()>0);
	}

	@Test
	public void testGetExperimentDTO() throws SolrServerException {
		List<ExperimentDTO> exp = os.getExperimentDTO(1267, "MGI:1931053");
		System.out.println(exp);
		assertTrue(exp.size()>0);
		System.out.println(exp.get(0));
		
		exp = os.getExperimentDTO(2043, "MGI:1349215");
		System.out.println(exp);
		assertTrue(exp.size()>0);
		System.out.println(exp.get(0));
		
	}

}
