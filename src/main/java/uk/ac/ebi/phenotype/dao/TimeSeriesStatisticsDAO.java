package uk.ac.ebi.phenotype.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public interface TimeSeriesStatisticsDAO extends StatisticsDAO {

	public List<DiscreteTimePoint> getControlStats(SexType sex, Parameter parameter, Integer populationId);
	public List<DiscreteTimePoint> getMutantStats(SexType sex, ZygosityType zygosity, Parameter parameter, Integer populationId);
	
	public BiologicalModel getControlBiologicalModelByPopulation(Integer populationId);
	public BiologicalModel getMutantBiologicalModelByPopulation(Integer populationId);

	public List<BiologicalModel> getBiologicalModelsByParameter(Parameter parameter);
	public List<BiologicalModel> getBiologicalModelsByParameterAndGene(Parameter parameter, String accessionId);

	public Double getpValueByParameterAndBiologicalModelAndSexAndZygosity(Parameter parameter, BiologicalModel biologicalModel, SexType sex, ZygosityType zygosity);
	public Double getMaxEffectSizeByParameterAndBiologicalModelAndSexAndZygosity(Parameter parameter, BiologicalModel biologicalModel, SexType sex, ZygosityType zygosity);
	
	public Organisation getOrganisationByPopulation(Integer populationId);
	public List<Integer> getPopulationIdsByParameter(Parameter parameter);
	public List<Integer> getPopulationIdsByParameterAndMutantBiologicalModel(Parameter parameter, BiologicalModel biologicalModel);

	public SexType getSexByPopulation(Integer populationId);
	public List<ZygosityType> getZygositiesByPopulation(Integer populationId);
	
	public  List<Map<String,String>>  getListOfUniqueParametersAndGenes(int start, int length) throws SQLException;
	
	public Map<String, Float> getMinAndMaxForParameter(String paramStableId) throws SQLException;
}
