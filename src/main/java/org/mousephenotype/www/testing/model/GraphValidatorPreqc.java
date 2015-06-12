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

package org.mousephenotype.www.testing.model;

import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import uk.ac.ebi.phenotype.service.dto.GraphTestDTO;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to validate a preqc
 * graph. Since this is not an EBI graph, there is no need to extend from
 * GraphValidator.
 */
public class GraphValidatorPreqc {
    
    private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
    
    public PageStatus validate(WebDriver driver, GenePage genePage, GraphTestDTO geneGraph) {
        PageStatus status = new PageStatus();
        String message;
        
        List<String> urls = genePage.getGraphUrls(geneGraph.getProcedureName(), geneGraph.getParameterName());
        for (String url : urls) {
            if ( ! TestUtils.isPreQcLink(url)) {
                logger.info("Not a preqc graph. Continuing...: Gene Page URL: " + genePage.getTarget() + ". Graph URL: " + url);
                continue;
            }
            
            // If the graph page doesn't load, log it.
            driver.get(url);
            // Make sure there is a div.viz-tools.
            List<WebElement> elements = driver.findElements(By.xpath("//div[@class='viz-tools' or @class='phenodcc-heatmap']"));
            if (elements.isEmpty()) {
                message = "ERROR: " + "\n\tpreQc graph[ " + geneGraph.getProcedureParameterName() + "] URL: " + url + ". Gene page: " + genePage.getTarget() + "\n[FAILED]";
                status.addError(message);
            }
        }
        
        return status;
    }
}