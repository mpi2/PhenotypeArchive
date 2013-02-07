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

import java.util.List;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public interface CategoricalStatisticsDAO {

	public List<String> getCategories(Parameter parameter);

	public Long countControl(SexType sex, Parameter parameter, String category, Integer populationId);
	public Long countMutant(SexType sex, ZygosityType zygosity, Parameter parameter, String category, Integer populationId);
	
	public BiologicalModel getControlBiologicalModelByPopulation(Integer populationId);
	public BiologicalModel getMutantBiologicalModelByPopulation(Integer populationId);

	public Organisation getOrganisationByPopulation(Integer populationId);
	public List<Integer> getPopulationIdsByParameter(Parameter parameter);

	public void saveCategoricalResult(CategoricalResult result);

	public SexType getSexByPopulation(Integer populationId);
	public ZygosityType getZygosityByPopulation(Integer populationId);

}
