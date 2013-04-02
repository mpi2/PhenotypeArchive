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

/**
 * experimental observation manager interface
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import java.util.List;

import uk.ac.ebi.phenotype.pojo.BiologicalSample;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.Experiment;
import uk.ac.ebi.phenotype.pojo.Observation;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;

public interface ObservationDAO {

	public List<Observation> getObservationsBySampleIdAndParameterStableId(int sampleId, String parameterStableId);
	
	public void saveExperiment(Experiment experiment);
	public void saveObservation(Observation observation);
	
	public Observation createSimpleObservation(
			ObservationType observationType, 
			String simpleValue,
			Parameter parameter, 
			BiologicalSample sample,
			int populationId,
			Datasource datasource,
			Experiment experiment);
	
	public Observation createObservation(
			ObservationType observationType, 
			String firstDimensionValue, 
			String secondDimensionValue,
			String secondDimensionUnit,
			Parameter parameter, 
			BiologicalSample sample, 
			int populationId,
			Datasource datasource,
			Experiment experiment);
	
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
	
}
