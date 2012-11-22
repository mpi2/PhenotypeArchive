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

/**
 * 
 * A concrete representation of phenotype parameter within a pipeline.
 * 
 * A parameter has a name and stable id. A collection of meta information is
 * attached to the procedure.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * @see PipelineEntry
 */

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "phenotype_parameter")
public class Parameter extends PipelineEntry {
	
	@Column(name = "unit")
	private String unit;	

	@Column(name = "datatype")
	private String datatype;	
	
	@Column(name = "formula")
	private String formula;

	@Column(name = "derived")
	private boolean derivedFlag;

	@Column(name = "sequence")
	private int sequence;
	
	@Column(name = "required")
	private boolean requiredFlag;

	@Column(name = "metadata")
	private boolean metaDataFlag;	

	@Column(name = "important")
	private boolean importantFlag;

	@Column(name = "annotate")
	private boolean annotateFlag;

	@Column(name = "increment")
	private boolean incrementFlag;

	@Column(name = "options")
	private boolean optionsFlag;
	
	@Column(name = "media")
	private boolean mediaFlag;
	
	/**
	 * bi-directional
	 */
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinTable(name="phenotype_procedure_parameter",
        joinColumns = @JoinColumn(name="parameter_id"),
        inverseJoinColumns = @JoinColumn(name="procedure_id")
    )
    private Procedure procedure;
	
	@ElementCollection
	   @CollectionTable(name="phenotype_parameter_option", joinColumns=@JoinColumn(name="parameter_id"))
	   @AttributeOverrides({
		@AttributeOverride(name="name", 
						   column=@Column(name="name")),
		@AttributeOverride(name="description", 
		   column=@Column(name="description"))
		})
	private Set<ParameterOption> options;
	
	@ElementCollection
	   @CollectionTable(name="phenotype_parameter_increment", joinColumns=@JoinColumn(name="parameter_id"))
	@AttributeOverrides({
		@AttributeOverride(name="value", 
		   column=@Column(name="value")),
		@AttributeOverride(name="dataType", 
		   column=@Column(name="datatype")),
		@AttributeOverride(name="unit", 
		   column=@Column(name="unit")),
		@AttributeOverride(name="minimum", 
		   column=@Column(name="minimum"))			   
		})
	private Set<ParameterIncrement> increment;
	
	public Parameter() {
		super();
	}


	/**
	 * @return the procedure
	 */
	public Procedure getProcedure() {
		return procedure;
	}


	/**
	 * @param procedure the procedure to set
	 */
	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}


	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the datatype
	 */
	public String getDatatype() {
		return datatype;
	}

	/**
	 * @param datatype the datatype to set
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	/**
	 * @return the formula
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 * @param formula the formula to set
	 */
	public void setFormula(String formula) {
		this.formula = formula;
	}

	/**
	 * @return the derivedFlag
	 */
	public boolean isDerivedFlag() {
		return derivedFlag;
	}

	/**
	 * @param derivedFlag the derivedFlag to set
	 */
	public void setDerivedFlag(boolean derived) {
		this.derivedFlag = derived;
	}

	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the requiredFlag
	 */
	public boolean isRequiredFlag() {
		return requiredFlag;
	}

	/**
	 * @param requiredFlag the requiredFlag to set
	 */
	public void setRequiredFlag(boolean requiredFlag) {
		this.requiredFlag = requiredFlag;
	}

	/**
	 * @return the metaDataFlag
	 */
	public boolean isMetaDataFlag() {
		return metaDataFlag;
	}

	/**
	 * @param metaDataFlag the metaDataFlag to set
	 */
	public void setMetaDataFlag(boolean metaDataFlag) {
		this.metaDataFlag = metaDataFlag;
	}

	/**
	 * @return the importantFlag
	 */
	public boolean isImportantFlag() {
		return importantFlag;
	}

	/**
	 * @param importantFlag the importantFlag to set
	 */
	public void setImportantFlag(boolean importantFlag) {
		this.importantFlag = importantFlag;
	}

	/**
	 * @return the annotateFlag
	 */
	public boolean isAnnotateFlag() {
		return annotateFlag;
	}

	/**
	 * @param annotateFlag the annotateFlag to set
	 */
	public void setAnnotateFlag(boolean annotateFlag) {
		this.annotateFlag = annotateFlag;
	}

	/**
	 * @return the incrementFlag
	 */
	public boolean isIncrementFlag() {
		return incrementFlag;
	}

	/**
	 * @param incrementFlag the incrementFlag to set
	 */
	public void setIncrementFlag(boolean incrementFlag) {
		this.incrementFlag = incrementFlag;
	}

	/**
	 * @return the optionsFlag
	 */
	public boolean isOptionsFlag() {
		return optionsFlag;
	}

	/**
	 * @param optionsFlag the optionsFlag to set
	 */
	public void setOptionsFlag(boolean optionsFlag) {
		this.optionsFlag = optionsFlag;
	}

	/**
	 * @return the mediaFlag
	 */
	public boolean isMediaFlag() {
		return mediaFlag;
	}

	/**
	 * @param mediaFlag the mediaFlag to set
	 */
	public void setMediaFlag(boolean mediaFlag) {
		this.mediaFlag = mediaFlag;
	}

	/**
	 * @return the options
	 */
	public Set<ParameterOption> getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(Set<ParameterOption> options) {
		this.options = options;
	}

	/**
	 * @return the increment
	 */
	public Set<ParameterIncrement> getIncrement() {
		return increment;
	}

	/**
	 * @param increment the increment to set
	 */
	public void setIncrement(Set<ParameterIncrement> increment) {
		this.increment = increment;
	}

}
