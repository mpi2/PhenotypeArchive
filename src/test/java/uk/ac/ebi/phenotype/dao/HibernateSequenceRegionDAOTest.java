package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.ebi.phenotype.pojo.SequenceRegion;

@ContextConfiguration( locations={ "classpath:test-config.xml" })
public class HibernateSequenceRegionDAOTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private SequenceRegionDAO sequenceRegionDAO;

	@Test
	public void testGetAllSequenceRegions() {
		List<SequenceRegion> sequenceRegions = sequenceRegionDAO.getAllSequenceRegions();
		assertTrue("Wrong number of sequenceRegions", sequenceRegions.size() >= 23);
	}

	@Test
	public void testGetSequenceRegion() {
		
		SequenceRegion sequenceRegion = sequenceRegionDAO.getSequenceRegionByName("XY");

		// assert the returned sequenceRegion contains what you expect given the state
		// of the database
		assertNotNull("sequenceRegion should never be null", sequenceRegion);
		assertEquals("wrong sequenceRegion", "XY", sequenceRegion.getName());
		assertEquals("wrong sequenceRegion coordinate system","par", sequenceRegion.getCoordinateSystem().getName());
	}

}
