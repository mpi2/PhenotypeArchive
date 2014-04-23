package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.ebi.phenotype.pojo.Datasource;

/**
 * Unit test for the Hibernate-based labcode manager implementation. Tests application behavior to verify the Account
 * Hibernate mapping is correct.
 */

@ContextConfiguration( locations={ "classpath:test-config.xml" })
public class HibernateDatasourceDAOTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private DatasourceDAO datasourceDAO;

	@Test
	public void testGetAllDatasources() {
		List<Datasource> datasources = datasourceDAO.getAllDatasources();
		for (Datasource data: datasources) {
			assertTrue(data.toString() != null);
		}
		assertTrue("not enough labcodes",datasources.size() > 14);
	}

}
