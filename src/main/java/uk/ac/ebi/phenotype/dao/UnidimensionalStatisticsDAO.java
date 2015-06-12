/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
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
import uk.ac.ebi.phenotype.pojo.ZygosityType;

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



}