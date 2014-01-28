package uk.ac.ebi.phenotype.stats;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
@TransactionConfiguration
@Transactional
public class ObservationServiceTest {

	@Autowired
	private ObservationService os;
	
	@Autowired
	private PhenotypePipelineDAO parameterDAO;
	
	@Autowired
	private OrganisationDAO organisationDAO;

	
	@Test
	public void testGetExperimentKeys() {
		Map<String,List<String>> keys=null;
		//http://localhost:8080/phenotype-archive/stats/genes/MGI:1922257?parameterId=ESLIM_003_001_004&zygosity=homozygote
			try {
				keys = os.getExperimentKeys("MGI:1922257","ESLIM_003_001_004");
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		assertTrue(keys.size()>0);
	}
	
	@Test
	public void testGetAllGeneAccessionIdsByParameterIdOrganisationStrainZygosity() throws SolrServerException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion("M-G-P_009_001_002", 1, 1);
		List<String> genes = os.getAllGeneAccessionIdsByParameterIdOrganisationIdStrainZygosity(p.getId(), 3,"EUROCURATE1983", "homozygote");
		assertTrue(genes.size()>0);

	}
	
	@Test
	public void testGetAllStrainsByParameterIdOrganistion() throws SolrServerException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion("M-G-P_009_001_002", 1, 1);
		List<String> strains = os.getStrainsByParameterIdOrganistionId(p.getId(), 3);
		assertTrue(strains.size()>0);
		
	}
	
	@Test
	public void testGetAllOrganisationIdsWithObservations() throws SolrServerException {
		List<Integer> organisationIds = os.getAllOrganisationIdsWithObservations();
		assertTrue(organisationIds.size()>0);
	}

}
