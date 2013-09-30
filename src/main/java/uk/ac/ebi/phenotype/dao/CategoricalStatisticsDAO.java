/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalGroupKey;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public interface CategoricalStatisticsDAO extends StatisticsDAO {

	public List<String> getCategories(Parameter parameter);

	public Long countControl(SexType sex, Parameter parameter, String category, Integer populationId) throws SQLException;
	public Long countMutant(SexType sex, ZygosityType zygosity, Parameter parameter, String category, Integer populationId) throws SQLException;
	
	public BiologicalModel getControlBiologicalModelByPopulation(Integer populationId);
	public BiologicalModel getMutantBiologicalModelByPopulation(Integer populationId);

	public List<BiologicalModel> getMutantBiologicalModelsByParameterAndGene(Parameter parameter, String accessionId);
	
	public List<BiologicalModel> getBiologicalModelsByParameter(Parameter parameter) ;

	public Double getpValueByParameterAndControlBiologicalModelAndSexAndZygosity(Parameter parameter, BiologicalModel biologicalModel, SexType sex, ZygosityType zygosity);
	
	public List<Double> getpValueByParameterAndMutantBiologicalModelAndSexAndZygosity(Parameter parameter, BiologicalModel biologicalModel, SexType sex, ZygosityType zygosity);
	
	public List<Double> getMaxEffectSizeByParameterAndMutantBiologicalModelAndSexAndZygosity(Parameter parameter, BiologicalModel biologicalModel, SexType sex, ZygosityType zygosity);
	
	public Organisation getOrganisationByPopulation(Integer populationId);
	public List<Integer> getPopulationIdsByParameter(Parameter parameter);
	public List<Integer> getPopulationIdsByParameterAndMutantBiologicalModel(Parameter parameter, BiologicalModel biologicalModel);

	public SexType getSexByPopulation(Integer populationId);
	public List<ZygosityType> getZygositiesByPopulation(Integer populationId);

	public void deleteCategoricalResultByParameter(Parameter parameter) throws HibernateException, SQLException;

	public Integer getPopulationIdByColonyParameterZygositySex(String colonyId, Parameter parameter, ZygosityType zygosity,SexType sex) throws SQLException;

	public List<CategoricalGroupKey> getControlCategoricalDataByParameter(Parameter parameter) throws SQLException;
	public List<CategoricalGroupKey> getMutantCategoricalDataByParameter(Parameter parameter);

	public Map<Integer, Integer> getOrganisationsByParameter(Parameter parameter) throws SQLException;
	
	public  List<Map<String,String>>  getListOfUniqueParametersAndGenes(int start, int length) throws SQLException;

	public List<CategoricalResult> getCategoricalResultByParameter(Parameter parameter, int i, SexType sexType);
	

}
