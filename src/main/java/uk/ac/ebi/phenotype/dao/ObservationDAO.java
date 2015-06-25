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

/**
 * experimental observation manager interface
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import uk.ac.ebi.phenotype.pojo.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public interface ObservationDAO extends HibernateDAO {

	public List<Observation> getObservationsBySampleIdAndParameterStableId(int sampleId, String parameterStableId);
	
	public void saveExperiment(Experiment experiment);
	public void saveObservation(Observation observation);
	
	public Observation createSimpleObservation(
			ObservationType observationType, 
			String simpleValue,
			Parameter parameter, 
			BiologicalSample sample,
			Datasource datasource,
			Experiment experiment, String parameterStatus);
	
	public Observation createObservation(
			ObservationType observationType, 
			String firstDimensionValue, 
			String secondDimensionValue,
			String secondDimensionUnit,
			Parameter parameter, 
			BiologicalSample sample, 
			Datasource datasource,
			Experiment experiment, String parameterStatus);
	
	public Observation createTimeSeriesObservationWithOriginalDate(
			ObservationType observationType, 
			String firstDimensionValue, 
			String secondDimensionValue,
			String actualTimepoint,
			String secondDimensionUnit,
			Parameter parameter, 
			BiologicalSample sample, 
			Datasource datasource,
			Experiment experiment, String parameterStatus);
	
	public int deleteAllExperimentsByOrganisationAndDatasource(Organisation organisation, Datasource datasource);
	public int deleteAllCategoricalObservationsByOrganisationAndDatasource(Organisation organisation, Datasource datasource);
	public int deleteAllTimeSeriesObservationsByOrganisationAndDatasource(Organisation organisation, Datasource datasource);
	public int deleteAllUnidimensionalObservationsByOrganisationAndDatasource(Organisation organisation, Datasource datasource);
	public int deleteAllMetadataObservationsByOrganisationAndDatasource(Organisation organisation, Datasource datasource);
	
	public int deleteAllExperimentsByDatasource(Datasource datasource);
	public int deleteAllCategoricalObservationsByDatasource(Datasource datasource);
	public int deleteAllTimeSeriesObservationsByDatasource(Datasource datasource);
	public int deleteAllUnidimensionalObservationsByDatasource(Datasource datasource);
	public int deleteAllMetadataObservationsByDatasource(Datasource datasource);
	
	public int deleteAllCategoricalObservationsWithoutExperimentByDatasource(Datasource datasource);
	public int deleteAllTimeSeriesObservationsWithoutExperimentByDatasource(Datasource datasource);
	public int deleteAllUnidimensionalObservationsWithoutExperimentByDatasource(Datasource datasource);
	public int deleteAllMetadataObservationsWithoutExperimentByDatasource(Datasource datasource);
	
	public int deleteAllExperimentsWithoutObservationByDatasource(Datasource datasource);

	public List<Parameter> getAllParametersWithObservations();
	public List<Integer> getAllParameterIdsWithObservationsByOrganisation(Organisation organisation) throws SQLException;
	public List<Integer> getAllCategoricalParameterIdsWithObservationsByOrganisation(Organisation organisation) throws SQLException;

	public List<Observation> getAllObservationsByParameter(Parameter parameter);
	public List<ImageRecordObservation> getAllImageObservations();

	public Observation getObservationById(Integer obsId);

	public List<Organisation> getAllOrganisationsWithObservations();
	public List<Integer> getAllOrganisationIdsWithObservations() throws SQLException;

	public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadata() throws SQLException;

    /**
     * Fetch count of records NOT missing but with not null/empty parameter_status or parameter_status_message.
     * @return count, interesting fields
     * @throws SQLException 
     */
    public List<String[]> getNotMissingNotEmpty() throws SQLException;
    
    /**
     * Fetch count of records missing that have a null/empty parameter_status.
     * @return count, interesting fields
     * @throws SQLException 
     */
    public List<String[]> getMissingEmpty() throws SQLException;

    /**
     * Fetch list of observation.parameter_status that is not in IMPC ontology_term.acc.
     * @return list of missing ontology_term.acc used by observation.parameter_status
     * @throws SQLException 
     */
    public List<String[]> getMissingOntologyTerms() throws SQLException;
    	
        
}
