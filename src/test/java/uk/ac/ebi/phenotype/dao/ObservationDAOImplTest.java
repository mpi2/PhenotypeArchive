package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Parameter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
@TransactionConfiguration
@Transactional
public class ObservationDAOImplTest {

	@Autowired
	ObservationDAO observationDAO;
	
	@Test
	public void testGetAllParametersWithObservations() {
		List<Parameter> parameters = observationDAO.getAllParametersWithObservations();
		assertTrue("There must be at least 20 parameters loaded", 20 < parameters.size());
	}

}

