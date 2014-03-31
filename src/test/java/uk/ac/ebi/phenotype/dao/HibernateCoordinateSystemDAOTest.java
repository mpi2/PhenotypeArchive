package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.ebi.phenotype.pojo.CoordinateSystem;


@ContextConfiguration( locations={ "classpath:app-config.xml" })
public class HibernateCoordinateSystemDAOTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private CoordinateSystemDAO coordinateSystemDAO;

	@Test
	public void testGetAllCoordinateSystems() {
		List<CoordinateSystem> coordinateSystems = coordinateSystemDAO.getAllCoordinateSystems();
		assertEquals("Wrong number of coordinateSystems", 2, coordinateSystems.size());
	}

	@Test
	public void testGetCoordinateSystem() {
		
		CoordinateSystem coordinateSystem = coordinateSystemDAO.getCoordinateSystemByName("chromosome");
		// assert the returned coordinateSystem contains what you expect given the state
		// of the database
		assertNotNull("coordinateSystem should never be null", coordinateSystem);
		assertEquals("wrong coordinateSystem", "chromosome", coordinateSystem.getName());
		assertEquals("wrong coordinateSystem datasource","Mouse Genome Assembly", coordinateSystem.getDatasource().getName());
		assertEquals("wrong coordinateSystem datasource","GRCm38", coordinateSystem.getDatasource().getVersion());
		assertEquals("wrong coordinateSystem strain", "C57BL/6J", coordinateSystem.getStrain().getName());

	}

}
