package uk.ac.ebi.phenotype.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.UnidimensionalRecordDTO;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.MouseDataPoint;

public interface UnidimensionalStatisticsDAO extends StatisticsDAO {

	public List<Float> getControlDataPointsForPopulation(Integer populationId) throws SQLException;

	public List<Float> getMutantDataPoints(SexType sex, ZygosityType zygosity, Parameter parameter, Integer populationId) throws SQLException;

	public List<BiologicalModel> getBiologicalModelsByParameterAndGene(Parameter parameter, String accessionId);

	public BiologicalModel getControlBiologicalModelByPopulation(Integer populationId);

	public BiologicalModel getMutantBiologicalModelByPopulation(Integer populationId);

	public List<Integer> getPopulationIdsByParameterAndMutantBiologicalModel(Parameter parameter, BiologicalModel biologicalModel);

	public SexType getSexByPopulation(Integer populationId);

	public List<ZygosityType> getZygositiesByPopulation(Integer populationId);

	public void deleteUnidimensionalResultByParameter(Parameter parameter) throws HibernateException, SQLException;

	public Set<UnidimensionalRecordDTO> getUnidimensionalData(Parameter parameter, Organisation organization, String colonyId, ZygosityType zygosity) throws SQLException;

	public Set<ZygosityType> getZygosities(Parameter parameter, Organisation organisation, String colonyId) throws SQLException;

	public Set<Integer> getPopulationIds(Parameter parameter, Organisation organisation, String colonyId, ZygosityType zygosity) throws SQLException;

	public Set<String> getColoniesByParameter(Parameter parameter) throws SQLException;

	public List<Organisation> getOrganisationsByColonyAndParameter(String colony, Parameter parameter);

	public  List<Map<String,String>>  getListOfUniqueParametersAndGenes(int start, int length) throws SQLException;
	
	public List<Map<String,String>> getListOfUniqueParametersAndGenes(int start, int length, String parameterId)throws SQLException;
	
	public Map<String, Float> getMinAndMaxForParameter(String paramStableId) throws SQLException;

	public List<UnidimensionalResult> getUnidimensionalResultByParameterAndBiologicalModel(
			Parameter parameter, BiologicalModel controlBiologicalModel,
			BiologicalModel biologicalModel);
	
	public List<UnidimensionalResult> getUnidimensionalResultByParameterIdAndBiologicalModelIds(
			Integer parameter, Integer controlBiologicalId,
			Integer biologicalId);
	
	
	public List<MouseDataPoint> getMutantDataPointsWithMouseName(SexType sex, ZygosityType zygosity, Parameter parameter,  Integer populationId);
	public List<MouseDataPoint> getControlDataPointsWithMouseName(Integer populationId);

	public UnidimensionalResult getStatsForPhenotypeCallSummaryId(
			int phenotypeCallSummaryId) throws SQLException;

}