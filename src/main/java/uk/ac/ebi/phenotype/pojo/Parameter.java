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
 * A concrete representation of phenotype parameter within a pipeline.
 * 
 * A parameter has a name and stable id. A collection of meta information is
 * attached to the procedure.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * @see PipelineEntry
 */

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.apache.commons.lang.StringUtils;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import uk.ac.ebi.phenotype.stats.categorical.CategoriesExclude;

@Entity
@Table(name = "phenotype_parameter")
public class Parameter extends PipelineEntry {

	@Column(name = "unit")
	private String unit;	

	@Column(name = "parameter_type")
	private String type;
	
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
	
	@Column(name = "data_analysis")
	private boolean requiredForDataAnalysisFlag;

	@Column(name = "data_analysis_notes")
	private String dataAnalysisNotes;
	
	/**
	 * bi-directional
	 */
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	@JoinTable(name="phenotype_procedure_parameter",
	joinColumns = @JoinColumn(name="parameter_id"),
	inverseJoinColumns = @JoinColumn(name="procedure_id")
			)
	private Procedure procedure;

	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinTable(
			name="phenotype_parameter_lnk_option",
			joinColumns = @JoinColumn( name="parameter_id"),
			inverseJoinColumns = @JoinColumn( name="option_id")
			)
	private List<ParameterOption> options;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="phenotype_parameter_lnk_increment",
			joinColumns = @JoinColumn( name="parameter_id"),
			inverseJoinColumns = @JoinColumn( name="increment_id")
			)
	private List<ParameterIncrement> increments;

	/**
	 * Annotation (like MP terms) can be associated to parameter 
	 * under certain conditions. The rules are defined in this table
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinTable(
			name="phenotype_parameter_lnk_ontology_annotation",
			joinColumns = @JoinColumn( name="parameter_id"),
			inverseJoinColumns = @JoinColumn( name="annotation_id")
			)
	private List<ParameterOntologyAnnotation> annotations;

	/**
	 * Entity-Quality annotations (like MA / PATO terms) can be associated to parameter 
	 * under certain conditions. The rules are defined in this table
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinTable(
			name="phenotype_parameter_lnk_eq_annotation",
			joinColumns = @JoinColumn( name="parameter_id"),
			inverseJoinColumns = @JoinColumn( name="annotation_id")
			)
	private List<ParameterEntityQualityAnnotation> eqAnnotations;	
	
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
	public boolean getDerivedFlag() {
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}


	/**
	 * @return the requiredForDataAnalysisFlag
	 */
	public boolean isRequiredForDataAnalysisFlag() {
		return requiredForDataAnalysisFlag;
	}


	/**
	 * @param requiredForDataAnalysisFlag the requiredForDataAnalysisFlag to set
	 */
	public void setRequiredForDataAnalysisFlag(boolean requiredForDataAnalysisFlag) {
		this.requiredForDataAnalysisFlag = requiredForDataAnalysisFlag;
	}


	/**
	 * @return the dataAnalysisNotes
	 */
	public String getDataAnalysisNotes() {
		return dataAnalysisNotes;
	}


	/**
	 * @param dataAnalysisNotes the dataAnalysisNotes to set
	 */
	public void setDataAnalysisNotes(String dataAnalysisNotes) {
		this.dataAnalysisNotes = dataAnalysisNotes;
	}


	/**
	 * @param increment add an increment to set
	 */
	public void addOption(ParameterOption option) {
		if (options == null) {
			options = new ArrayList<ParameterOption>();
		}
		this.options.add(option);
	}

	/**
	 * @return the options
	 */
	public List<ParameterOption> getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(List<ParameterOption> options) {
		this.options = options;
	}

	/**
	 * @param increment add an increment to set
	 */
	public void addIncrement(ParameterIncrement increment) {
		if (increments == null) {
			increments = new ArrayList<ParameterIncrement>();
		}
		this.increments.add(increment);
	}

	/**
	 * @return the increment
	 */
	public List<ParameterIncrement> getIncrement() {
		return increments;
	}

	/**
	 * @param increment the increment to set
	 */
	public void setIncrement(List<ParameterIncrement> increments) {
		this.increments = increments;
	}

	/**
	 * @param increment add an increment to set
	 */
	public void addAnnotation(ParameterOntologyAnnotation annotation) {
		if (annotations == null) {
			annotations = new ArrayList<ParameterOntologyAnnotation>();
		}
		this.annotations.add(annotation);
	}

	/**
	 * @return the annotations
	 */
	public List<ParameterOntologyAnnotation> getAnnotations() {
		return annotations;
	}


	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(List<ParameterOntologyAnnotation> annotations) {
		this.annotations = annotations;
	}

	/**
	 * @param increment add an increment to set
	 */
	public void addEqAnnotation(ParameterEntityQualityAnnotation eqAnnotation) {
		if (eqAnnotations == null) {
			eqAnnotations = new ArrayList<ParameterEntityQualityAnnotation>();
		}
		this.eqAnnotations.add(eqAnnotation);
	}

	/**
	 * @return the EqAnnotations
	 */
	public List<ParameterEntityQualityAnnotation> getEqAnnotations() {
		return eqAnnotations;
	}


	/**
	 * @param EqAnnotations the EqAnnotations to set
	 */
	public void setEqAnnotations(List<ParameterEntityQualityAnnotation> eqAnnotations) {
		this.eqAnnotations = eqAnnotations;
	}
	
	/**
	 * Check what units are stored for each of this parameter
	 * dimension.
	 * @return a string representing a unit
	 */
	
	/**
	 * Check what units are stored for each of this parameter dimension.
	 * @return an array of units, dimension by dimension
	 */
	public String[] checkParameterUnits() {
		
		String[] units = null;
		
		if (isIncrementFlag()) {
			units = new String[2];
			for (ParameterIncrement increment: increments) {
				// one is not enough
				if (increment.getValue().length() > 0 || increments.size() == 1) {
					units[0] = increment.getUnit();
					break;
				}
			}
			units[1] = unit;
			
		} else {
			units = new String[1];
			units[0] = unit;
		}
		
		return units;
	}
	
	/**
	 * Check what unit is stored for this parameter for this dimension (1,2, etc.)
	 * @param dimension the parameter dimension (starting from 1)
	 * @return the data dimension unit
	 */
	public String checkParameterUnit(int dimension) {
		String cunit = null;
		String[] units = checkParameterUnits();
		if (dimension <= units.length) {
			return units[dimension-1];
		}
		return cunit;
	}




	@Override
	public String toString() {
		return "Parameter [stableId=" + stableId + ", stableKey=" + stableKey
				+ "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (annotateFlag ? 1231 : 1237);
		result = prime * result
				+ ((annotations == null) ? 0 : annotations.hashCode());
		result = prime * result
				+ ((datatype == null) ? 0 : datatype.hashCode());
		result = prime * result + (derivedFlag ? 1231 : 1237);
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
		result = prime * result + (importantFlag ? 1231 : 1237);
		result = prime * result + (incrementFlag ? 1231 : 1237);
		result = prime * result
				+ ((increments == null) ? 0 : increments.hashCode());
		result = prime * result + (mediaFlag ? 1231 : 1237);
		result = prime * result + (metaDataFlag ? 1231 : 1237);
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result + (optionsFlag ? 1231 : 1237);
		result = prime * result
				+ ((procedure == null) ? 0 : procedure.hashCode());
		result = prime * result + (requiredFlag ? 1231 : 1237);
		result = prime * result + sequence;
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Parameter other = (Parameter) obj;
		if (annotateFlag != other.annotateFlag) {
			return false;
		}
		if (annotations == null) {
			if (other.annotations != null) {
				return false;
			}
		} else if (!annotations.equals(other.annotations)) {
			return false;
		}
		if (datatype == null) {
			if (other.datatype != null) {
				return false;
			}
		} else if (!datatype.equals(other.datatype)) {
			return false;
		}
		if (derivedFlag != other.derivedFlag) {
			return false;
		}
		if (formula == null) {
			if (other.formula != null) {
				return false;
			}
		} else if (!formula.equals(other.formula)) {
			return false;
		}
		if (importantFlag != other.importantFlag) {
			return false;
		}
		if (incrementFlag != other.incrementFlag) {
			return false;
		}
		if (increments == null) {
			if (other.increments != null) {
				return false;
			}
		} else if (!increments.equals(other.increments)) {
			return false;
		}
		if (mediaFlag != other.mediaFlag) {
			return false;
		}
		if (metaDataFlag != other.metaDataFlag) {
			return false;
		}
		if (options == null) {
			if (other.options != null) {
				return false;
			}
		} else if (!options.equals(other.options)) {
			return false;
		}
		if (optionsFlag != other.optionsFlag) {
			return false;
		}
		if (procedure == null) {
			if (other.procedure != null) {
				return false;
			}
		} else if (!procedure.equals(other.procedure)) {
			return false;
		}
		if (requiredFlag != other.requiredFlag) {
			return false;
		}
		if (sequence != other.sequence) {
			return false;
		}
		if (unit == null) {
			if (other.unit != null) {
				return false;
			}
		} else if (!unit.equals(other.unit)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Use for use interface categories for categorical parameter- excludes things like "no data" "image only" and "not defined"
	 * @return  usable categories List<String> which will be empty if not categorical
	 */
	public List<String> getCategoriesUserInterfaceFreindly(){
		List<ParameterOption> options = this.getOptions();
		List<String> categories = new ArrayList<String>();
                boolean useDescription=false;
                if(options.size()>0){
                    String name=options.get(0).getName();
                    if(StringUtils.isNumeric(name)){
                        useDescription=true;
                    }
                }
		for (ParameterOption option : options) {
                    String label=option.getName();
                    //this is a hack as impress holds some numeric categories which shouldn't be????
                    if(useDescription==true && !option.getDescription().equals("") && !option.getDescription().equals("")){
                       label=option.getDescription();
                    }
			categories.add(label);
		}
		//exclude - "no data", "not defined" etc	
		List<String>okCategoriesList=CategoriesExclude.getInterfaceFreindlyCategories(categories);	
		return okCategoriesList;
	}
	
}
