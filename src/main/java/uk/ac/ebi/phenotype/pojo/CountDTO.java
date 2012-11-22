/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.phenotype.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;


/**
 * Simple wrapper entity work around for the limited hibernate pure native query support. Where an HQL query is not possible
 */
/**
 * In native SQL, you have to declare the ‘resultClass‘ to let Hibernate know 
 * what is the return type, failed to do it will caused the exception 
 * “org.hibernate.cfg.NotYetImplementedException: Pure native scalar queries are not yet supported“.
 */
@Entity
@NamedNativeQueries({
	@NamedNativeQuery(
			name = "deleteBiologicalModelSample",
			query = "delete from biological_model where db_id = :dbID)",
			resultClass = CountDTO.class
	),
	@NamedNativeQuery(
			name = "deleteLiveSamples",
			query = "DELETE biological_model_sample, biological_sample, live_sample FROM biological_model INNER JOIN biological_model_sample INNER JOIN biological_sample INNER JOIN live_sample WHERE biological_model.id = biological_model_sample.biological_model_id AND biological_model_sample.biological_sample_id=biological_sample.id AND biological_sample.id = live_sample.id AND biological_model.db_id = :dbID",
			resultClass = CountDTO.class
	),
	@NamedNativeQuery(
			name = "deleteBiologicalSamples",
			query = "DELETE biological_model_sample, biological_sample FROM biological_model INNER JOIN biological_model_sample INNER JOIN biological_sample WHERE biological_model.id = biological_model_sample.biological_model_id AND biological_model_sample.biological_sample_id=biological_sample.id AND biological_model.db_id = :dbID",
			resultClass = CountDTO.class
	),	
	@NamedNativeQuery(
			name = "deleteBiologicalModelGenomicFeatures",
			query = "DELETE biological_model_genomic_feature FROM biological_model INNER JOIN biological_model_genomic_feature WHERE biological_model.id = biological_model_genomic_feature.biological_model_id AND biological_model.db_id = :dbID",
			resultClass = CountDTO.class
	),	
	@NamedNativeQuery(
			name = "deleteBiologicalModels",
			query = "DELETE biological_model FROM biological_model WHERE biological_model.db_id = :dbID",
			resultClass = CountDTO.class
	),	
	@NamedNativeQuery(
			name = "deleteAllUnidimensionalObservationsByOrganisationAndDatasource",
			query = "DELETE observation, unidimensional_observation FROM experiment INNER JOIN experiment_observation INNER JOIN observation INNER JOIN unidimensional_observation WHERE experiment.id = experiment_observation.experiment_id AND experiment_observation.observation_id = observation_id AND unidimensional_observation.id = observation.id AND experiment.organisation_id = :organisationID AND experiment.db_id = :dbID",
			resultClass = CountDTO.class
	),
	@NamedNativeQuery(
			name = "deleteAllCategoricalObservationsByOrganisationAndDatasource",
			query = "DELETE observation, categorical_observation FROM observation INNER JOIN categorical_observation INNER JOIN experiment_observation INNER JOIN experiment WHERE observation.id = categorical_observation.id AND observation.id = experiment_observation.observation_id AND experiment_observation.experiment_id = experiment.id AND experiment.organisation_id = :organisationID AND experiment.db_id = :dbID",
			resultClass = CountDTO.class
	),
	@NamedNativeQuery(
			name = "deleteExperimentByOrganisationAndDatasource",
			query = "DELETE FROM experiment WHERE experiment.organisation_id = :organisationID AND experiment.db_id = :dbID",
			resultClass = CountDTO.class
	)
})
public class CountDTO {
	@Id
	@Column(name = "COUNT")
	private Long count;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}