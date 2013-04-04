package uk.ac.ebi.phenotype.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalView;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public interface UnidimensionalStatisticsDAO {

	public List<Float> countControl(SexType sex, Parameter parameter, Integer populationId);
	public List<Float> countMutant(SexType sex, ZygosityType zygosity, Parameter parameter, Integer populationId);
	
	public List<BiologicalModel> getBiologicalModelsByParameterAndGene(Parameter parameter, String accessionId);
	public BiologicalModel getControlBiologicalModelByPopulation(Integer populationId);
	public BiologicalModel getMutantBiologicalModelByPopulation(Integer populationId);

	public Double getpValueByParameterAndBiologicalModel(Parameter parameter, BiologicalModel biologicalModel);
	
	public List<Integer> getPopulationIdsByParameterAndMutantBiologicalModel(Parameter parameter, BiologicalModel biologicalModel);

	public SexType getSexByPopulation(Integer populationId);
	public List<ZygosityType> getZygositiesByPopulation(Integer populationId);

	public void deleteUnidimensionalResultByParameter(Parameter parameter) throws HibernateException, SQLException;
	public void saveUnidimensionalResult(UnidimensionalResult result);
	public Set<UnidimensionalView> getUnidimensionalData(Parameter parameter, Organisation organization, String colonyId);
	
}
