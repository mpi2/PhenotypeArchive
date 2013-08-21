package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.ebi.phenotype.pojo.Organisation;

/**
 * Unit test for the Hibernate-based organisation manager implementation. Tests application behavior to verify the Account
 * Hibernate mapping is correct.
 */

@ContextConfiguration( locations={ "classpath:app-config.xml" })
public class HibernateOrganisationDAOTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private OrganisationDAO organisationDAO;

	@Test
	public void testGetOrganisation() throws UnsupportedEncodingException {
		
		Organisation organisation = organisationDAO.getOrganisationByName("Transgenose CNRS");
		
		byte[] bytes = organisation.getFullname().getBytes("UTF-8");
		String test = new String(bytes);
		System.out.println(test);
		bytes = "Institut De Transgenose CNRS Orléans".getBytes("UTF-8");
		String test2 = new String(bytes);
		System.out.println(test2);
	
		// assert the returned organisation contains what you expect given the state
		// of the database
		assertNotNull("organisation should never be null", organisation);
		assertEquals("wrong organisation name", "Transgenose CNRS", organisation.getName());
		assertEquals("wrong organisation fullname", "Institut De Transgenose CNRS Orléans", organisation.getFullname());
		assertEquals("wrong country", "France", organisation.getCountry());

		organisation = organisationDAO.getOrganisationByName("HZI");
		assertNotNull("organisation should never be null", organisation);
		assertEquals("wrong organisation name", "HZI", organisation.getName());
		assertEquals("wrong organisation fullname", "Das Helmholtz Zentrum für Infektionsforschung", organisation.getFullname());
		assertEquals("wrong country", "Germany", organisation.getCountry());
	}

}
