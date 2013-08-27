package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.ebi.phenotype.pojo.Strain;

/**
 * Unit test for the Hibernate-based strain DAO implementation. 
 * Tests application behavior to verify the Hibernate mapping is correct.
 */

@ContextConfiguration( locations={ "classpath:app-config.xml" })
public class HibernateStrainDAOTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private StrainDAO strainDAO;

	@Test
	public void testGetAllStrains() {
		List<Strain> strains = strainDAO.getAllStrains();

		assertTrue("There must be at least 20 strains loaded", 20 <= strains.size());
	}

}
