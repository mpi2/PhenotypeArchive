package uk.ac.ebi.phenotype.stats;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;


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
	
	@Test
	public void testThisThing() throws SolrServerException, IOException, URISyntaxException {
		List<String> sexes = new ArrayList<>();
		sexes.add(SexType.male.name());
		List<String> zygs = new ArrayList<>();
		zygs.add(ZygosityType.homozygote.name());
        List<ExperimentDTO> experimentList = es.getExperimentDTO(1594, "MGI:1922257", sexes, zygs, 8);
        System.out.println("EXP list is: "+experimentList);
        System.out.println("Size is: "+experimentList.size());


	}

	@Test
	public void testControlSelectionStrategy() throws SolrServerException, IOException, URISyntaxException {

		List<String> sexes = new ArrayList<>();

		List<String> zygs = new ArrayList<>();

		zygs.add(ZygosityType.heterozygote.name());

		List<ExperimentDTO> experimentList = es.getExperimentDTO("ESLIM_011_001_004", "MGI:1922257", sexes, zygs, 8);
        System.out.println("EXP list is: "+experimentList);
        System.out.println("Size is: "+experimentList.size());
	}

}
