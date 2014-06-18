/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
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

/**
 * 
 * An experimental observation on a sample.
 * The observation is defined in a procedure and the unit and value
 * of the observation is defined by a parameter. The observation can be 
 * empty (missing flag). Different types of observation coexists.
 * 
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 *  @since February 2012
 *  @see BiologicalSample
 *  @see ObservationType
 *  @see Experiment
 *  @see Parameter
 */

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name = "observation")
public class Observation extends SourcedEntry {

	@OneToOne
	@JoinColumn(name = "biological_sample_id")
	private BiologicalSample sample;
	
	@OneToOne
	@JoinColumn(name = "parameter_id")
	private Parameter parameter;
	
	@Column(name = "parameter_stable_id")	
	private String parameterStableId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "observation_type")
	private ObservationType type;
	
	@Column(name = "missing")
	private boolean missingFlag;

	@Column(name = "population_id")
	private int populationId;
	
    @Column(name = "parameter_status")
    private String parameterStatus;
    
    @Column(name = "parameter_status_message")
    private String parameterStatusMessage;
    
	
	public String getParameterStatus() {
		return parameterStatus;
	}

	public void setParameterStatus(String parameterStatus) {
		this.parameterStatus = parameterStatus;
	}

	/**
	 * bi-directional
	 */
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinTable(name="experiment_observation",
        joinColumns = @JoinColumn(name="observation_id"),
        inverseJoinColumns = @JoinColumn(name="experiment_id")
    )
    private Experiment experiment;	
	
	/**
	 * @return the sample
	 */
	public BiologicalSample getSample() {
		return sample;
	}

	/**
	 * @param sample the sample to set
	 */
	public void setSample(BiologicalSample sample) {
		this.sample = sample;
	}

	/**
	 * @return the parameter
	 */
	public Parameter getParameter() {
		return parameter;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the type
	 */
	public ObservationType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ObservationType type) {
		this.type = type;
	}

	/**
	 * @return the missingFlag
	 */
	public boolean isMissingFlag() {
		return missingFlag;
	}

	/**
	 * @param missingFlag the missingFlag to set
	 */
	public void setMissingFlag(boolean missingFlag) {
		this.missingFlag = missingFlag;
	}

	/**
	 * @return the experiment
	 */
	public Experiment getExperiment() {
		return experiment;
	}

	/**
	 * @param experiment the experiment to set
	 */
	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	/**
	 * @return the populationId
	 */
	public int getPopulationId() {
		return populationId;
	}

	/**
	 * @param populationId the populationId to set
	 */
	public void setPopulationId(int populationId) {
		this.populationId = populationId;
	}

	public String getParameterStableId() {
		return parameterStableId;
	}

	public void setParameterStableId(String parameterStableId) {
		this.parameterStableId = parameterStableId;
	}

    
    public String getParameterStatusMessage() {
        return parameterStatusMessage;
    }

    
    public void setParameterStatusMessage(String parameterStatusMessage) {
        this.parameterStatusMessage = parameterStatusMessage;
    }
	
	
	
}
