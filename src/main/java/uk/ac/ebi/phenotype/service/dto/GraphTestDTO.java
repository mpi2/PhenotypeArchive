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

package uk.ac.ebi.phenotype.service.dto;

/**
 * This class encapsulates the code and data necessary to represent the data
 * required to build a gene page link containing graph links for specific
 * chart types, such as UNIDIMENSIONAL_SCATTER_PLOT, UNIDIMENSIONAL_BOX_PLOT,
 * UNIDIMENSIONAL_ABR_PLOT, CATEGORICAL_STACKED_COLUMN, TIME_SERIES_LINE, PIE,
 * and TIME_SERIES_LINE_BODYWEIGHT, PREQC. Functionally, it is a façade around
 * the GeneDTO exposing only those fields required for graph testing.
 * 
 * The impetus for this class is a need to make a request by chart type to
 * produce a list of gene pages most likely to contain that chart type.
 * 
 * @author mrelac
 */
public class GraphTestDTO {
    private String mgiAccessionId;
    private String procedureName;
    private String parameterStableId;
    private String parameterName;
    
    public GraphTestDTO() {
    }

    public String getMgiAccessionId() {
        return mgiAccessionId;
    }

    public void setMgiAccessionId(String mgiAccessionId) {
        this.mgiAccessionId = mgiAccessionId;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getParameterStableId() {
        return parameterStableId;
    }

    public void setParameterStableId(String parameterStableId) {
        this.parameterStableId = parameterStableId;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }
    
    public String getProcedureParameterName() {
        return getParameterName() + " | " + getProcedureName();
    }

    @Override
    public String toString() {
        return "GraphTestDTO{" + "mgiAccessionId=" + mgiAccessionId + ", procedureName=" + procedureName + ", parameterStableId=" + parameterStableId + ", parameterName=" + parameterName + '}';
    }

}
