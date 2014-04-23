package uk.ac.ebi.phenotype.stats;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.dao.OrganisationDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
@TransactionConfiguration
@Transactional
public class ExperimentServiceTest {

	@Autowired
	private ExperimentService es;
	
	@Autowired
	private PhenotypePipelineDAO pDAO;

	@Autowired
	private OrganisationDAO orgDAO;

	@Ignore
	@Test
	public void testGetExperimentDTO() throws SolrServerException, IOException, URISyntaxException {
		System.out.println("\nexecuting testGetExperimentDTO");
		List<ExperimentDTO> exp = es.getExperimentDTO("ESLIM_001_001_158", null, "MGI:1920194");
		System.out.println(exp);
		assertTrue(exp.size()>0);
		System.out.println(exp.get(0));
		
		exp = es.getExperimentDTO(2043, null, "MGI:1349215");
		System.out.println(exp);
		assertTrue(exp.size()>0);
		System.out.println(exp.get(0));
		
	}

	@Test
	public void testGetExperimentDTO2() throws SolrServerException, IOException, URISyntaxException {
		System.out.println("\nexecuting testGetExperimentDTO2");
		List<ExperimentDTO> exp = es.getExperimentDTO("ESLIM_015_001_012", null, "MGI:2444584");
		System.out.println(exp);
		assertTrue(exp.size()>0);
		System.out.println(exp.get(0));
		
	}

	
	@Test
	public void testGetExperimentDTOESLIM_015_001_012() throws SolrServerException, IOException, URISyntaxException {

		Logger.getRootLogger().setLevel(Level.INFO);

		System.out.println("\nexecuting testGetExperimentDTOESLIM_015_001_012");
		Parameter p = pDAO.getParameterByStableId("ESLIM_015_001_012");
		Pipeline pipe = pDAO.getPhenotypePipelineByStableId("ESLIM_002");
		Organisation org = orgDAO.getOrganisationByName("MRC Harwell");
		
		List<String> zygs = new ArrayList<>();
		zygs.add(ZygosityType.heterozygote.name());
		List<ExperimentDTO> experimentList = es.getExperimentDTO(p.getId(), pipe.getId(), "MGI:2444584", null, org.getId(), zygs, "MGI:4830588", "9180596a255e4e612acae74beb22b506", false);

        System.out.println("EXP list is: "+experimentList);
        System.out.println("Size is: "+experimentList.size());

	}

	@Test
	public void testControlSelectionStrategy() throws SolrServerException, IOException, URISyntaxException {

		List<String> sexes = new ArrayList<>();

		List<String> zygs = new ArrayList<>();

		zygs.add(ZygosityType.heterozygote.name());

		//List<ExperimentDTO> experimentList = es.getExperimentDTO("ESLIM_011_001_004", "MGI:1928760", sexes, zygs, 8);
        //System.out.println("EXP list is: "+experimentList);
        //System.out.println("Size is: "+experimentList.size());
	}

	
/*	@Test
	public void testGetGraphUrls() {
		GraphUtils graphUtils=new GraphUtils(es);
		try {
			graphUtils.getGraphUrls("MGI:1922257", "ESLIM_003_001_004");
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/

}
