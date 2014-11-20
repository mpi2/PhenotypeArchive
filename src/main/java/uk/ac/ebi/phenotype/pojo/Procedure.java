/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.ebi.phenotype.pojo;

/**
 *
 * A concrete representation of phenotype procedure within a pipeline.
 *
 * A procedure has a name and stable id. A collection of meta information is
 * attached to the procedure.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * @see PipelineEntry
 * @see Pipeline
 */
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "phenotype_procedure")
public class Procedure extends PipelineEntry implements Comparable, Serializable {

    @Column(name = "is_mandatory")
    boolean isMandatory;

    /**
     * There is no cascade option on an ElementCollection, the target objects
     * are always persisted, merged, removed with their parent.
     */
    @ElementCollection
    @CollectionTable(name = "phenotype_procedure_meta_data", joinColumns = @JoinColumn(name = "procedure_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "name",
                column = @Column(name = "meta_name")),
        @AttributeOverride(name = "value",
                column = @Column(name = "meta_value"))
    })
    private Set<MetaData> metaDataSet;

    /**
     * bi-directional
     */
    /*@ManyToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
     @JoinTable(name="phenotype_pipeline_procedure",
     joinColumns = @JoinColumn(name="procedure_id"),
     inverseJoinColumns = @JoinColumn(name="pipeline_id")
     )*/
    @ManyToMany(mappedBy = "procedures")
    private Set<Pipeline> pipelines = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "phenotype_procedure_parameter",
            joinColumns = @JoinColumn(name = "procedure_id"),
            inverseJoinColumns = @JoinColumn(name = "parameter_id")
    )
    private Set<Parameter> parameters= new HashSet<>();
    
    @Column(name = "stage")
    private String stage;
    
    @Column(name = "stage_label")
    private String stageLabel;

    @Column(name = "level")
    private String level;
    
    public Procedure() {
        super();
    }

    /**
     * @return the isMandatory
     */
    public boolean isMandatory() {
        return isMandatory;
    }

    /**
     * @param isMandatory the isMandatory to set
     */
    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    /**
     * @return the metaDataSet
     */
    public Set<MetaData> getMetaDataSet() {
        return metaDataSet;
    }

    /**
     * @param metaDataSet the metaDataSet to set
     */
    public void setMetaDataSet(Set<MetaData> metaDataSet) {
        this.metaDataSet = metaDataSet;
    }

    /**
     * @param metaData the metaDataSet to add
     */
    public void addMetaData(MetaData metaData) {
        if (metaDataSet == null) {
            metaDataSet = new HashSet<MetaData>();
        }
        this.metaDataSet.add(metaData);
    }

    /**
     * @return the parameters
     */
    public Set<Parameter> getParameters() {
        return parameters;
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(Set<Parameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the pipeline
     */
    public Set<Pipeline> getPipelines() {
        return pipelines;
    }

    /**
	 * @return the stage
	 */
	public String getStage() {
		return stage;
	}

	/**
	 * @param stage the stage to set
	 */
	public void setStage(String stage) {
		this.stage = stage;
	}

	/**
	 * @return the stageLabel
	 */
	public String getStageLabel() {
		return stageLabel;
	}

	/**
	 * @param stageLabel the stageLabel to set
	 */
	public void setStageLabel(String stageLabel) {
		this.stageLabel = stageLabel;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
     * @param pipelines the pipelines to set
     */
    public void setPipelines(Set<Pipeline> pipelines) {
        this.pipelines = pipelines;
    }

    public void addPipeline(Pipeline pipeline) {
        this.pipelines.add(pipeline);
    }

    public int compareTo(Object o) {
        if (!o.getClass().equals(this.getClass())) {
            return 0;
        }
        Procedure p = (Procedure) o;
        return this.getName().compareTo(p.getName());
    }

}
