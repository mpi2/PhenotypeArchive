package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.ebi.phenotype.pojo.GenomicFeature;

/**
 * Unit test for the Hibernate-based genomicFeature manager implementation. Tests application behavior to verify the Account
 * Hibernate mapping is correct.
 */

@ContextConfiguration( locations={ "classpath:app-config.xml" })
public class HibernateGenomicFeatureDAOTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private GenomicFeatureDAO genomicFeatureDAO;

	@Test
	@Ignore
	public void testGetAllGenomicFeatures() {
		List<GenomicFeature> genomicFeatures = genomicFeatureDAO.getAllGenomicFeatures();
		assertTrue("Wrong number of genomicFeatures", 83786<= genomicFeatures.size());
	}

	@Test
	public void testGetGenomicFeatureByName() {
		
		GenomicFeature genomicFeature = genomicFeatureDAO.getGenomicFeatureByName("T cell receptor alpha, variable 4.7");
		// assert the returned genomicFeature contains what you expect given the state
		// of the database
		assertNotNull("genomicFeature should never be null", genomicFeature);
		assertEquals("wrong genomicFeature", "T cell receptor alpha, variable 4.7", genomicFeature.getName());
		assertEquals("wrong genomicFeature biotype","gene", genomicFeature.getBiotype().getName().toLowerCase());
	}
	
	@Test
	public void testGetGenomicFeatureByAccession() {
		
		GenomicFeature genomicFeature = genomicFeatureDAO.getGenomicFeatureByAccession("MGI:3030231");
		// assert the returned genomicFeature contains what you expect given the state
		// of the database
		assertNotNull("genomicFeature should never be null", genomicFeature);
		assertEquals("wrong genomicFeature symbol", "Olfr397", genomicFeature.getSymbol());
		assertEquals("wrong genomicFeature name", "olfactory receptor 397", genomicFeature.getName());
		assertEquals("wrong genomicFeature biotype","gene", genomicFeature.getBiotype().getName().toLowerCase());
	}
	
}
