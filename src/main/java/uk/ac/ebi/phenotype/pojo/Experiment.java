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
package uk.ac.ebi.phenotype.pojo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "experiment")
public class Experiment extends SourcedEntry {

	@Column(name = "external_id")
	private String externalId;

	@Column(name = "sequence_id")
	private String sequenceId;

	@Column(name = "date_of_experiment")
	private Date dateOfExperiment;

	@Column(name = "colony_id")
	private String colonyId;

	@Column(name = "metadata_group")
	private String metadataGroup;

	@Column(name = "metadata_combined")
	private String metadataCombined;

	@Column(name = "procedure_status")
	private String procedureStatus;

	@Column(name = "procedure_status_message")
	private String procedureStatusMessage;


	@OneToOne
	@JoinColumn(name = "pipeline_id")
	private Pipeline pipeline;

	@Column(name = "pipeline_stable_id")
	private String pipelineStableId;

	@OneToOne
	@JoinColumn(name = "procedure_id")
	private Procedure procedure;

	@Column(name = "procedure_stable_id")
	private String procedureStableId;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
		name = "experiment_observation",
		joinColumns = @JoinColumn(name = "experiment_id"),
		inverseJoinColumns = @JoinColumn(name = "observation_id"))
	private List<Observation> observations;

	@OneToOne
	@JoinColumn(name = "organisation_id")
	private Organisation organisation;

	@OneToOne
	@JoinColumn(name = "biological_model_id")
	private BiologicalModel model;


	@OneToOne
	@JoinColumn(name = "project_id")
	private Project project;


	public Project getProject() {
		return project;
	}


	public void setProject(Project project) {
		this.project = project;
	}


	public String getSequenceId() {
		return sequenceId;
	}


	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}


	public String getProcedureStatus() {
		return procedureStatus;
	}


	public String getProcedureStatusMessage() {
		return procedureStatusMessage;
	}


	public void setProcedureStatus(String procedureStatus) {
		this.procedureStatus = procedureStatus;
	}


	public void setProcedureStatusMessage(String procedureStatusMessage) {
		this.procedureStatusMessage = procedureStatusMessage;
	}


	/**
	 * @return the pipeline
	 */
	public Pipeline getPipeline() {
		return pipeline;
	}


	/**
	 * @param pipeline the pipeline to set
	 */
	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}


	/**
	 * @return the pipelineStableId
	 */
	public String getPipelineStableId() {
		return pipelineStableId;
	}


	/**
	 * @param pipelineStableId the pipelineStableId to set
	 */
	public void setPipelineStableId(String pipelineStableId) {
		this.pipelineStableId = pipelineStableId;
	}


	public Procedure getProcedure() {
		return procedure;
	}


	public String getProcedureStableId() {
		return procedureStableId;
	}


	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}


	public void setProcedureStableId(String procedureStableId) {
		this.procedureStableId = procedureStableId;
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


	public String getMetadataGroup() {
		return metadataGroup;
	}


	public void setMetadataGroup(String metadataGroup) {
		this.metadataGroup = metadataGroup;
	}


	public String getMetadataCombined() {
		return metadataCombined;
	}


	public void setMetadataCombined(String metadataCombined) {
		this.metadataCombined = metadataCombined;
	}


	public String getColonyId() {
		return colonyId;
	}


	public void setColonyId(String colonyId) {
		this.colonyId = colonyId;
	}


	public BiologicalModel getModel() {
		return model;
	}


	public void setModel(BiologicalModel model) {
		this.model = model;
	}
}
