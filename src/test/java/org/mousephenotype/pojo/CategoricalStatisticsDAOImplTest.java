package org.mousephenotype.pojo;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import javax.validation.constraints.AssertTrue;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.dao.CategoricalStatisticsDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
@TransactionConfiguration
@Transactional
public class CategoricalStatisticsDAOImplTest {

	Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	@Autowired
	private CategoricalStatisticsDAO cStatsDAO;

	@Autowired
	private PhenotypePipelineDAO pipelineDAO;

	
	@Test
	public void testGetDerviedParameter() throws SQLException{
		Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion("ESLIM_009_001_703", 1, 0);
		// male ESLIM_001_001_004 increased length3209
		System.out.println(parameter.getDerivedFlag());
		assertTrue(parameter.getDerivedFlag());
	}
	
	
	
	@Test
	public void test() {

		Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion("ESLIM_001_001_007", 1, 0);
		List<Integer> popIds = cStatsDAO.getPopulationIdsByParameter(parameter);
		Integer populationId = popIds.get(0);
		BiologicalModel bmControl = cStatsDAO.getControlBiologicalModelByPopulation(populationId);
		BiologicalModel bmMutant = cStatsDAO.getMutantBiologicalModelByPopulation(populationId);
		Organisation organisation = cStatsDAO.getOrganisationByPopulation(populationId);

		assert(bmControl != null);
		assert(bmMutant != null);
		assert(organisation != null);

	}
	
	@Test
	public void testBiologicalModel() {
		Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion("ESLIM_001_001_007", 1, 0);
		List<BiologicalModel> bms = cStatsDAO.getMutantBiologicalModelsByParameterAndGene(parameter, "MGI:103006");
		assert(bms.size() > 0);
	}
	
	@Test
	public void testBiologicalModelFromParameter() {
		Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion("ESLIM_001_001_007", 1, 0);
		List<BiologicalModel> bms = cStatsDAO.getMutantBiologicalModelsByParameterAndGene(parameter, "MGI:103006");
		BiologicalModel biologicalModel = bms.get(0);
		List<Integer> popId = cStatsDAO.getPopulationIdsByParameterAndMutantBiologicalModel(parameter, biologicalModel);

		// At least one population for this 
		assertTrue(popId.size()>=1);
	}
	
	@Test
	public void testGetSexFromParameter() {
		Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion("ESLIM_001_001_007", 1, 0);
		List<Integer> popIds = cStatsDAO.getPopulationIdsByParameter(parameter);
		Integer populationId = popIds.get(0);
		SexType sex = cStatsDAO.getSexByPopulation(populationId);
		assertTrue(sex != null);
	}

	@Test
	public void testGetOrganisationFromParameter() {
		Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion("ESLIM_001_001_007", 1, 0);
		List<Integer> popIds = cStatsDAO.getPopulationIdsByParameter(parameter);
		Integer populationId = popIds.get(0);
		Organisation organisation = cStatsDAO.getOrganisationByPopulation(populationId);
		assertTrue(organisation != null);
	}
	
	@Test
	public void testCountControl() throws SQLException{
		Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion("ESLIM_001_001_004", 1, 0);
		// male ESLIM_001_001_004 increased length3209
		Long count = null;
		count = cStatsDAO.countControl(SexType.male, parameter, "wt distribution", 3209);
		assertTrue(count > 0);
	}

}
