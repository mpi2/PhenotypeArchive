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
package uk.ac.ebi.phenotype.pojo;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "experiment")
public class Experiment extends SourcedEntry {

	@Column(name = "external_id")
	private String externalId;
	
	@Column(name = "date_of_experiment")
	private Date dateOfExperiment;
	
	@OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="experiment_observation",
            joinColumns = @JoinColumn( name="experiment_id"),
            inverseJoinColumns = @JoinColumn( name="observation_id")
    )
	private List<Observation> observations;

	@OneToOne
	@JoinColumn(name = "organisation_id")
	private Organisation organisation;

	@OneToOne
	@JoinColumn(name = "project_id")
	private Project project;
	
	
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * @return the dateOfExperiment
	 */
	public Date getDateOfExperiment() {
		return dateOfExperiment;
	}

	/**
	 * @param dateOfExperiment the dateOfExperiment to set
	 */
	public void setDateOfExperiment(Date dateOfExperiment) {
		this.dateOfExperiment = dateOfExperiment;
	}

	/**
	 * @return the observations
	 */
	public List<Observation> getObservations() {
		return observations;
	}

	/**
	 * @param observations the observations to set
	 */
	public void setObservations(List<Observation> observations) {
		this.observations = observations;
	}

	/**
	 * @return the organisation
	 */
	public Organisation getOrganisation() {
		return organisation;
	}

	/**
	 * @param organisation the organisation to set
	 */
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	
	
}
