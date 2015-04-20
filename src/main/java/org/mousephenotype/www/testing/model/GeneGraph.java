/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.www.testing.model;

/**
 * This class encapsulates the code and data necessary to represent the data
 * required to build a gene page link containing graph links for specific
 * chart types, such as UNIDIMENSIONAL_SCATTER_PLOT, UNIDIMENSIONAL_BOX_PLOT,
 * UNIDIMENSIONAL_ABR_PLOT, CATEGORICAL_STACKED_COLUMN, TIME_SERIES_LINE, PIE,
 * and TIME_SERIES_LINE_BODYWEIGHT, PREQC;
 * 
 * The impetus for this class is a need to make a request by chart type to
 * produce a list of gene pages most likely to contain that chart type.
 * 
 * @author mrelac
 */
public class GeneGraph {
    private String geneAccessionId;
    private String procedureName;
    private String parameterName;
    private String parameterStableId;
    
    public GeneGraph() {
        
    }

    public String getGeneAccessionId() {
        return geneAccessionId;
    }

    public void setGeneAccessionId(String geneAccessionId) {
        this.geneAccessionId = geneAccessionId;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterStableId() {
        return parameterStableId;
    }

    public void setParameterStableId(String parameterStableId) {
        this.parameterStableId = parameterStableId;
    }
    
    public String getProcedureParameterName() {
        return this.procedureName + " | " + this.parameterName;
    }

    @Override
    public String toString() {
        return "GeneGraph{" + "geneAccessionId=" + geneAccessionId + ", procedureName=" + procedureName + ", parameterName=" + parameterName + ", parameterStableId=" + parameterStableId + '}';
    }
}
