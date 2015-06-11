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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to validate a Pie graph 
 * section.
 */
public class GraphSectionPie extends GraphSection {
    
    /**
     * Creates a new <code>GraphSectionPie</code> instance
     * 
     * @param driver <code>WebDriver</code> instance
     * @param wait <code>WebDriverWait</code> instance
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     * @param graphUrl the graph url
     * @param chartElement <code>WebElement</code> pointing to the HTML
     *                     div.chart element of the pie chart section.
     * 
     * @throws GraphTestException
     */
    public GraphSectionPie(WebDriver driver, WebDriverWait wait, PhenotypePipelineDAO phenotypePipelineDAO, String graphUrl, WebElement chartElement) throws GraphTestException {
        super(driver, wait, phenotypePipelineDAO, graphUrl, chartElement);
    }
    
    @Override
    public PageStatus validate() throws GraphTestException {
        PageStatus status = super.validate();                                   // Validate common components.
        
        return status;
    }
}