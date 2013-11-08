package uk.ac.ebi.phenotype.stats;

import static org.junit.Assert.assertTrue;

import java.util.Date;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
@TransactionConfiguration
@Transactional
public class ObservationServiceTest {

	@Autowired
	private ObservationService os;

	@Test
	@Ignore
	public void testGetControls() throws SolrServerException {
		List<ObservationDTO> test = os.getControls("ESLIM_001_001_158", "MGI:3028467", 7, new Date());
		assertTrue(test.size()>0);
	}

	@Test
	public void testGetControlsDate() throws SolrServerException {
		List<ObservationDTO> test = os.getControls("ESLIM_003_001_012", "MGI:1922257", 8, new Date());
		assertTrue(test.size()>0);
	}

	@Test
	public void testGetUrl() throws SolrServerException {
		
		String url = os.getQueryStringByParameterGeneAccZygosityOrganisationStrain(new Integer(1), "MGI:88255", "homozygous", new Integer(3), "MGI:2159965");
		
		System.out.println(url);
		assertTrue(url.length()>0);
		
	}

	
	@Test
	public void testGetAllUnidimensionalParameterIdsWithObservationsByOrganisation() throws SolrServerException {
		List<Integer> params = os.getAllUnidimensionalParameterIdsWithObservationsByOrganisation("WTSI");
		assertTrue(params.size()>0);
	}
	
	@Test
	public void testGetAllGeneAccessionIdsByParameterIdOrganisationStrainZygosity() throws SolrServerException {
		List<String> genes = os.getAllGeneAccessionIdsByParameterIdOrganisationStrainZygosity(1234, "WTSI","EUROCURATE1983", "homozygote");
		assertTrue(genes.size()>0);

	}
	
	@Test
	public void testGetAllStrainsByParameterIdOrganistion() throws SolrServerException {
		List<String> strains = os.getAllStrainsByParameterIdOrganistion(1234, "WTSI");
		assertTrue(strains.size()>0);
		
	}

}
