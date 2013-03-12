package uk.ac.ebi.phenotype.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public interface UnidimensionalStatisticsDAO {

	public List<Float> countControl(SexType sex, Parameter parameter, Integer populationId);
	public List<Float> countMutant(SexType sex, ZygosityType zygosity, Parameter parameter, Integer populationId);
	
	public BiologicalModel getControlBiologicalModelByPopulation(Integer populationId);
	public BiologicalModel getMutantBiologicalModelByPopulation(Integer populationId);

	public List<BiologicalModel> getBiologicalModelsByParameter(Parameter parameter);
	public List<BiologicalModel> getBiologicalModelsByParameterAndGene(Parameter parameter, String accessionId);

	public Double getpValueByParameterAndBiologicalModelAndSexAndZygosity(Parameter parameter, BiologicalModel biologicalModel, SexType sex, ZygosityType zygosity);
	public Double getMaxEffectSizeByParameterAndBiologicalModelAndSexAndZygosity(Parameter parameter, BiologicalModel biologicalModel, SexType sex, ZygosityType zygosity);
	
	public Organisation getOrganisationByPopulation(Integer populationId);
	public List<Integer> getPopulationIdsByParameter(Parameter parameter);
	public List<Integer> getPopulationIdsByParameterAndMutantBiologicalModel(Parameter parameter, BiologicalModel biologicalModel);

	public void saveCategoricalResult(CategoricalResult result);

	public SexType getSexByPopulation(Integer populationId);
	public List<ZygosityType> getZygositiesByPopulation(Integer populationId);

	public void deleteCategoricalResultByParameter(Parameter parameter) throws HibernateException, SQLException;
	
}
