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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'imagesGrid' HTML table common to all Image facet
 * views.
 */
public class SearchImpcImageTable extends SearchImageTable {
    
    static {
        byHash.put(SearchFacetTable.BY_TABLE, By.xpath("//table[@id='impc_imagesGrid']"));
        byHash.put(SearchFacetTable.BY_TABLE_TR, By.xpath("//table[@id='impc_imagesGrid']/tbody/tr"));
        byHash.put(SearchFacetTable.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='impc_imagesGrid_length']"));
    }
    
    /**
     * Creates a new <code>SearchImageTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchImpcImageTable(WebDriver driver, int timeoutInSeconds) {
        super(driver, timeoutInSeconds);
    }
}