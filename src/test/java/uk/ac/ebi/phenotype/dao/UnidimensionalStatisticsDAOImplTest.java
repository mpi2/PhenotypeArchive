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
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.MouseDataPoint;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
@TransactionConfiguration
@Transactional
public class UnidimensionalStatisticsDAOImplTest {

	@Autowired
	private UnidimensionalStatisticsDAO unidimensionalDAO;

	@Autowired
	private PhenotypePipelineDAO phenotypePipelineDAO;

	@Test
	public void testGetMutantBiologicalModelsByParameterAndGene() {
		Parameter parameter = phenotypePipelineDAO.getParameterByStableIdAndVersion("ESLIM_015_001_018", 1, 0);
		List<BiologicalModel> biologicalModels=unidimensionalDAO.getBiologicalModelsByParameterAndGene(parameter,"MGI:1920000");
		assertTrue(biologicalModels.size() >= 1);
	}

	@Test
	public void testGetControlStats() throws SQLException {
		String parameterId="ESLIM_015_001_018";
		String acc="MGI:1920000";
		Parameter parameter = phenotypePipelineDAO.getParameterByStableIdAndVersion(parameterId, 1, 0);
		List<BiologicalModel> biologicalModels=unidimensionalDAO.getBiologicalModelsByParameterAndGene(parameter, acc);
		
		List<Integer> popIds = unidimensionalDAO.getPopulationIdsByParameterAndMutantBiologicalModel(parameter, biologicalModels.get(0));
		//SexType sexType = unidimensionalDAO.getSexByPopulation(new Integer(popIds.get(0).intValue()));
		List<Float> dataPoints = unidimensionalDAO.getControlDataPointsForPopulation( popIds.get(0));
		
//		BasicStats bs=new BasicStats(dataPoints);
//		Float median=bs.getMedian();
//		Float mean=bs.getMean();
//		Float stdDev=bs.getStdDev();
//		Float variance=bs.getVariance();
//		Float smallest=bs.getSmallest();
//		Float largest=bs.getLargest();
//		System.out.println(bs);
//		assertTrue(bs.getMedian().equals(new Float(1.65)));
//		assertTrue(dataPoints.size()>0);
		assertTrue(dataPoints.size() >= 1);
	}


	@Test
	public void testGetMutantStats() {
		String parameterId="ESLIM_015_001_018";
		String acc="MGI:1920000";
		Parameter parameter = phenotypePipelineDAO.getParameterByStableIdAndVersion(parameterId, 1, 0);
		List<BiologicalModel> biologicalModels=unidimensionalDAO.getBiologicalModelsByParameterAndGene(parameter, acc);
		
		assertTrue(biologicalModels.size() >= 1);
	}

	
	@Test
	public void testgetMinAndMaxForParameter(){
		String paramId="M-G-P_025_001_022";
		Map<String, Float> minMax = null;
		try {
			minMax = unidimensionalDAO.getMinAndMaxForParameter(paramId);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("min="+minMax.get("min")+" max="+minMax.get("max"));
	}
	
	@Test
	public void testGetUnidimensionalResultBy(){
		//to get a list with param and genes that will work use SELECT DISTINCT vw.biological_model_id, vw.parameter_id, bgf.gf_acc FROM stats_mv_experimental_unidimensional_values vw, biological_model_genomic_feature bgf, stats_unidimensional_results sudr where bgf.biological_model_id=vw.biological_model_id and sudr.parameter_id=vw.parameter_id;SELECT DISTINCT vw.biological_model_id, vw.parameter_id, bgf.gf_acc FROM stats_mv_experimental_unidimensional_values vw, biological_model_genomic_feature bgf, stats_unidimensional_results sudr where bgf.biological_model_id=vw.biological_model_id and sudr.parameter_id=vw.parameter_id limit 10;
		String acc="MGI:2684063";
		Parameter parameter2 = phenotypePipelineDAO.getParameterById(1268);
		System.out.println("paramid="+parameter2.getStableId());
		List<BiologicalModel> biologicalModels=unidimensionalDAO.getBiologicalModelsByParameterAndGene(parameter2, acc);
		System.out.println(biologicalModels);
		List<Integer> popIds = unidimensionalDAO.getPopulationIdsByParameterAndMutantBiologicalModel(parameter2, biologicalModels.get(0));
		BiologicalModel controlBiologicalModel=unidimensionalDAO.getControlBiologicalModelByPopulation(popIds.get(0));
		BiologicalModel biologicalModel = biologicalModels.get(0);
		List<UnidimensionalResult> unidimensionalResult = unidimensionalDAO.getUnidimensionalResultByParameterAndBiologicalModel(parameter2,controlBiologicalModel,  biologicalModel);
		System.out.println("result size="+unidimensionalResult.size());
		System.out.println("pValue="+unidimensionalResult.get(0).getpValue());
		System.out.println("male gender effect p="+unidimensionalResult.get(0).getGenderMaleKoPValue());
		System.out.println("femal gender effect pValue="+unidimensionalResult.get(0).getGenderFemaleKoPValue());
	}
	
	@Test
	public void testGetMutantDataPointsWithMouseName(){
		String parameterId="ESLIM_007_001_011";
		String acc="MGI:1916804";
		Parameter parameter = phenotypePipelineDAO.getParameterByStableIdAndVersion(parameterId, 1, 0);
		System.out.println(parameter.getId());
		List<MouseDataPoint> dataPointsWithMouse=unidimensionalDAO.getMutantDataPointsWithMouseName(SexType.male, ZygosityType.heterozygote, parameter,301919);
		
		assertTrue(dataPointsWithMouse.size() >= 1);
		for(MouseDataPoint data: dataPointsWithMouse){
			System.out.println(data);
		}
	}
}
