package uk.ac.ebi.phenotype.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.phenotype.pojo.ImageRecordObservation;
import uk.ac.ebi.phenotype.pojo.Parameter;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
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

	@Test
	public void testGetAllImageObservations() {
		List<ImageRecordObservation> imageObservations = observationDAO.getAllImageObservations();
		for(ImageRecordObservation obs: imageObservations){
			System.out.println("observation="+obs.getDownloadFilePath());
		}

	}

	@Test
	public void testGetDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadata() throws SQLException {

		List<Map<String, String>> datasets = observationDAO.getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadata();

		System.out.println("number of datasets in database: " + datasets.size());
		System.out.println(datasets.get(0));

	}


}

