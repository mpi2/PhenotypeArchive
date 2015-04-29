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

import org.mousephenotype.www.testing.exception.GraphTestException;

/**
 * This abstract class encapsulates the common code and data necessary to
 * validate a graph section. Subclasses handle validation for specific graph
 * types.
 * 
 * @author mrelac
 */

public abstract class GraphValidator {
    protected GraphSection pageSection;
    public static final String IMPC_PIPELINE = "IMPC Pipeline";
    public GraphValidator() {
        
    }

    public GraphSection getPageSection() {
        return pageSection;
    }

    public void setPageSection(GraphSection pageSection) {
        this.pageSection = pageSection;
    }
    
    
    public PageStatus validate() throws GraphTestException {
        return pageSection.getHeading().validate();
    }
}
