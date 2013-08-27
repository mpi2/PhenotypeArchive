package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
@TransactionConfiguration
@Transactional
public class TimeSeriesStatisticsDAOImplTest {

	@Autowired
	private TimeSeriesStatisticsDAO timeSeriesStatisticsDAO;

	@Autowired
	private PhenotypePipelineDAO phenotypePipelineDAO;


	@Test
	public void testControlStats() {
		String parameterId = "ESLIM_004_001_002";
		String acc = "MGI:1920000";
		Parameter parameter = phenotypePipelineDAO.getParameterByStableIdAndVersion(parameterId, 1, 0);
		List<BiologicalModel> biologicalModels = timeSeriesStatisticsDAO.getBiologicalModelsByParameterAndGene(parameter, acc);

		List<Integer> popIds = timeSeriesStatisticsDAO.getPopulationIdsByParameterAndMutantBiologicalModel(parameter, biologicalModels.get(0));
		SexType sexType = timeSeriesStatisticsDAO.getSexByPopulation(new Integer(popIds.get(0).intValue()));
		List<DiscreteTimePoint> statsData = timeSeriesStatisticsDAO.getControlStats(sexType, parameter, popIds.get(0));

		assertTrue(statsData.size() > 0);
	}

	@Test
	public void testMutantStats() {
		String parameterId = "ESLIM_004_001_002";
		String acc = "MGI:1920000";
		Parameter parameter = phenotypePipelineDAO.getParameterByStableIdAndVersion(parameterId, 1, 0);
		List<BiologicalModel> biologicalModels = timeSeriesStatisticsDAO.getBiologicalModelsByParameterAndGene(parameter, acc);

		List<Integer> popIds = timeSeriesStatisticsDAO.getPopulationIdsByParameterAndMutantBiologicalModel(parameter,biologicalModels.get(0));
		SexType sexType = timeSeriesStatisticsDAO.getSexByPopulation(new Integer(popIds.get(0).intValue()));
		List<ZygosityType> zygosities = timeSeriesStatisticsDAO.getZygositiesByPopulation(popIds.get(0));
		List<DiscreteTimePoint> statsData = timeSeriesStatisticsDAO.getMutantStats(sexType, zygosities.get(0), parameter, popIds.get(0));

		assertTrue(statsData.size() > 0);
	}

	@Test
	public void testPopulationIdsByParameter() {
		String parameterId = "ESLIM_004_001_002";
		Parameter parameter = phenotypePipelineDAO.getParameterByStableIdAndVersion(parameterId, 1, 0);
		List<Integer> populationIds = timeSeriesStatisticsDAO.getPopulationIdsByParameter(parameter);
		assertTrue("Parameter must have at least one population", populationIds.size() >= 1);
	}

	@Test
	public void testOrganisationByPopulation() {
		String parameterId = "ESLIM_004_001_002";
		Parameter parameter = phenotypePipelineDAO.getParameterByStableIdAndVersion(parameterId, 1, 0);
		List<Integer> populationIds = timeSeriesStatisticsDAO.getPopulationIdsByParameter(parameter);
		Organisation organisation = timeSeriesStatisticsDAO.getOrganisationByPopulation(populationIds.get(0));
		assertTrue("Population must have an organisation", organisation != null);
	}
	
	@Test
	public void testgetMinAndMaxForParameter(){
		String paramId="ESLIM_009_001_001";
		Map<String, Float> minMax = null;
		try {
			minMax = timeSeriesStatisticsDAO.getMinAndMaxForParameter(paramId);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("min="+minMax.get("min")+" max="+minMax.get("max"));
	}

}
