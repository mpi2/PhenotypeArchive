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

/**
 *
 * Representation of a phenotype pipeline along with its procedures.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * @see PipelineEntry
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "phenotype_pipeline")
public class Pipeline extends PipelineEntry {

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "phenotype_pipeline_procedure",
            joinColumns = @JoinColumn(name = "pipeline_id"),
            inverseJoinColumns = @JoinColumn(name = "procedure_id")
    )
    private Set<Procedure> procedures= new HashSet<>();

    public Pipeline() {
        super();
    }

    public void addProcedure(Procedure procedure) {
        procedures.add(procedure);
    }

    /**
     * @return the procedures
     */
    public Set<Procedure> getProcedures() {
        return procedures;
    }

    /**
     * @param procedures the procedures to set
     */
    public void setProcedures(Set<Procedure> procedures) {
        this.procedures = procedures;
    }

}
