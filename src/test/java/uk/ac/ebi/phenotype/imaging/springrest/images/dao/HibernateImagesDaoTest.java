package uk.ac.ebi.phenotype.imaging.springrest.images.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


@ContextConfiguration( locations={ "classpath:app-config.xml" })
public class HibernateImagesDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

	
	@Autowired
	private ImagesDao imagsDao;

	
	@Test
	public void testGetAllImages() throws Exception {
		assertTrue(imagsDao.getAllImages(0, 2, "").getLength()==2);
	}

	@Test
	public void testGetImageWithId() {
		assertTrue(imagsDao.getImageWithId(71886)!=null);
	}

	@Test
	public void testGetNumberOfImages() {
		// At least a thousand images
		assertTrue(imagsDao.getTotalNumberOfImages()>1000);
	}
	
	@Test
	public void testGetImageWithNeverPublish() {
		//we always want this to come back with no results so
		assertTrue(imagsDao.getImageWithId(70346)==null);
	}

}
