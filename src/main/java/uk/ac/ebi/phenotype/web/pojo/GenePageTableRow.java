/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.phenotype.web.pojo;

import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;

import java.util.Map;


/**
 *
 * @author mrelac
 * This class encapsulates the code and data necessary to represent a row in the
 * Gene page's 'phenotypes' HTML table.
 */
public class GenePageTableRow extends DataTableRow {

    public GenePageTableRow() {
        super();
    }
    
    public GenePageTableRow(PhenotypeCallSummary pcs, String baseUrl, Map<String, String> config) {
        super(pcs, baseUrl, config);
    }
    
    /**
     * Sort by:
     * <ul>
     * <li>sex</li>
     * <li>p-value</li>
     * <li>phenotype</li>
     * <li>procedure</li>
     * <li>parameter</li>
     * <li>phenotyping center</li>
     * <li>source</li>
     * </ul>
     * 
     * @param o operand to compare against
     * @return 
     */
    @Override
    public int compareTo(DataTableRow o) {
        if (o.phenotypeTerm == null || this.phenotypeTerm == null) {
            return -1;
        }
              
        // Gene Page sorting
        int pvalueOp = this.pValue.compareTo(o.pValue);
        if (pvalueOp == 0) {
            int phenotypeOp = this.phenotypeTerm.getName().compareTo(o.phenotypeTerm.getName());
            if (phenotypeOp == 0) {
             	int procedureOp = this.procedure.getName().compareTo(o.procedure.getName());
                if (procedureOp == 0) {
                	System.out.println(this.toString());
                	System.out.println("GenePageTableRow: MP: " + this.phenotypeTerm.getName() + ". P-VALUE: " + getPrValueAsString() + " parameter " + o.parameter.getId());
                    int parameterOp = this.parameter.getName().compareTo(o.parameter.getName());
                    if (parameterOp == 0) {
                        int phenotypingCenterOp = this.phenotypingCenter.compareTo(o.phenotypingCenter);
                        if (phenotypingCenterOp == 0) {
                            return this.dataSourceName.compareTo(o.dataSourceName);
                        } else {
                            return phenotypingCenterOp;
                        }
                    } else {
                        return parameterOp;
                    }
                } else {
                    return procedureOp;
                }
            } else {
                return phenotypeOp;
            }
        } else {
            return pvalueOp;
        }
    }
}
