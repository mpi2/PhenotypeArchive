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

import uk.ac.ebi.phenotype.dao.OrganisationDAO;
import uk.ac.ebi.phenotype.pojo.Organisation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
@TransactionConfiguration
@Transactional
public class ObservationServiceTest {

	@Autowired
	private ObservationService os;
	
	@Autowired
	private OrganisationDAO organisationDAO;

	@Test
	public void testGetControls() throws SolrServerException {
		List<ObservationDTO> test = os.getControls("ESLIM_003_001_011", "MGI:2159873", 9, new Date());
		assertTrue(test.size()>0);
	}

	@Test
	public void testGetMutants() throws SolrServerException {
		List<ObservationDTO> test = os.getControls("ESLIM_003_001_011", "MGI:2159873", 9, new Date());
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
		Organisation org = organisationDAO.getOrganisationByName("WTSI");
		List<Integer> params = os.getUnidimensionalParameterIdsWithObservationsByOrganisationId(org.getId());
		assertTrue(params.size()>0);
	}
	
	
	
	
	@Test
	public void testGetAllGeneAccessionIdsByParameterIdOrganisationStrainZygosity() throws SolrServerException {
		List<String> genes = os.getAllGeneAccessionIdsByParameterIdOrganisationIdStrainZygosity(2192, 3,"EUROCURATE1983", "homozygote");
		assertTrue(genes.size()>0);

	}
	
	@Test
	public void testGetAllStrainsByParameterIdOrganistion() throws SolrServerException {
		List<String> strains = os.getStrainsByParameterIdOrganistionId(2192, 3);
		assertTrue(strains.size()>0);
		
	}
	
	@Test
	public void testGetAllOrganisationIdsWithObservations() throws SolrServerException {
		List<Integer> organisationIds = os.getAllOrganisationIdsWithObservations();
		assertTrue(organisationIds.size()>0);
	}

}
