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

package org.mousephenotype.www.testing.model;

import org.openqa.selenium.WebDriver;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'imagesGrid' HTML table for images.
 */
public class SearchImageTable extends SearchFacetTable {
    
    public static final int COL_INDEX_NAME           = 0;
    public static final int COL_INDEX_EXAMPLE_IMAGES = 1;
    
    /**
     * Creates a new <code>SearchDiseaseTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     */
    public SearchImageTable(WebDriver driver) {
        super(driver, "//table[@id='imagesGrid']");
    }
    
    /**
     * Validates download data against this <code>SearchImageTable</code>
     * instance.
     * 
     * @param data The download data used for comparison
     * @return validation status
     */
    @Override
    public PageStatus validateDownload(String[][] data) {
        PageStatus status = new PageStatus();
        
        status.addError("Not implemented yet.");
        
        return status;
    }

}