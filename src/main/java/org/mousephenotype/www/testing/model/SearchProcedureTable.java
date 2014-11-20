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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'pipelineGrid' HTML table for procedures.
 */
public class SearchProcedureTable extends SearchFacetTable {
    
    public static final int COL_INDEX_PARAMETER = 0;
    public static final int COL_INDEX_PROCEDURE = 1;
    public static final int COL_INDEX_PIPELINE  = 2;
    
    private final List<ProcedureRow> bodyRows = new ArrayList();
    
    /**
     * Creates a new <code>SearchProcedureTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchProcedureTable(WebDriver driver, int timeoutInSeconds) {
        super(driver, "//table[@id='pipelineGrid']", timeoutInSeconds);
        
        parseBodyRows();
    }
    
    /**
     * Validates download data against this <code>SearchProcedureTable</code>
     * instance.
     * 
     * @param downloadData The download data used for comparison
     * @return validation status
     */
    @Override
    public PageStatus validateDownload(String[][] downloadData) {
        PageStatus status = new PageStatus();
        
        if ((bodyRows.isEmpty()) || (downloadData.length == 0))
            return status;
            
        // Validate the pageHeading.
        String[] expectedHeadingList = {
            "Parameter"
          , "Procedure"
          , "Procedure Impress link"
          , "Pipeline"
        };
        SearchFacetTable.validateDownloadHeading("PROCEDURE", status, expectedHeadingList, downloadData[0]);

        for (int i = 0; i < bodyRows.size(); i++) {
            String[] downloadRow = downloadData[i + 1];                         // Skip over heading row.
            ProcedureRow pageRow = bodyRows.get(i);
        
            // Verify the components.
            
            // procedure.
            if (pageRow.procedure.isEmpty()) {
                status.addError("PROCEDURE MISSING: Expected procedure but there was none. URL: " + driver.getCurrentUrl());
            }
            if ( ! pageRow.procedure.equals(downloadRow[DownloadSearchMapProcedures.COL_INDEX_PROCEDURE]))
                status.addError("PROCEDURE MISMATCH: procedure '" + pageRow.procedure + "' page value procedure = '" + pageRow.procedure + "' doesn't match download value '" + downloadRow[DownloadSearchMapProcedures.COL_INDEX_PROCEDURE] + "'. URL: " + driver.getCurrentUrl());

            // Impress Procedure Link.
            if (pageRow.procedureLink.isEmpty()) {
                status.addError("PROCEDURE MISSING: Expected procedure link but there was none. URL: " + driver.getCurrentUrl());
            }
            if ( ! pageRow.procedureLink.contains(downloadRow[DownloadSearchMapProcedures.COL_INDEX_IMPRESS_LINK].replace("https", "").replace("http", ""))) {
                status.addError("PROCEDURE MISMATCH: procedure '" + pageRow.procedure + "' page value procedureLink = '" + pageRow.procedureLink + "' doesn't match download value '" + downloadRow[DownloadSearchMapProcedures.COL_INDEX_IMPRESS_LINK] + "'. URL: " + driver.getCurrentUrl());
            }
            
            // pipeline.
            if (pageRow.pipeline.isEmpty()) {
                status.addError("PROCEDURE MISSING: Expected pipeline but there was none. URL: " + driver.getCurrentUrl());
            }
            if ( ! pageRow.pipeline.equals(downloadRow[DownloadSearchMapProcedures.COL_INDEX_PIPELINE]))
                status.addError("PROCEDURE MISMATCH: procedure '" + pageRow.procedure + "' page value pipeline = '" + pageRow.pipeline + "' doesn't match download value '" + downloadRow[DownloadSearchMapProcedures.COL_INDEX_PIPELINE] + "'. URL: " + driver.getCurrentUrl());
        }

        return status;
    }
    
    
    // PRIVATE METHODS
    
    
    // parameter, procedure, procedureLink, pipeline
    private void parseBodyRows() {
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            int index = 0;
            for (WebElement bodyRowElements : bodyRowElementsList) {
                ProcedureRow procedureRow = new ProcedureRow();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                procedureRow.parameter = bodyRowElementList.get(0).getText();
                WebElement anchorElement = bodyRowElementList.get(1).findElement(By.cssSelector("a"));
                procedureRow.procedure = anchorElement.getText();
                procedureRow.procedureLink = anchorElement.getAttribute("href");
                procedureRow.pipeline = bodyRowElementList.get(2).getText();
                
//System.out.println("procedureRow[ " + index + " ]: " + procedureRow.toString());
                index++;
                bodyRows.add(procedureRow);
            }
        }
    }
    
    
    // PRIVATE CLASSES
    
    
    private class ProcedureRow {
        private String parameter     = "";
        private String procedure     = "";
        private String procedureLink = "";
        private String pipeline      = "";
        
        @Override
        public String toString() {
            return "parameter: '" + parameter
                 + "'  procedure: '" + procedure
                 + "'  procedureLink: '" + procedureLink
                 + "'  pipeline: '" + pipeline + "'";
        }
    }
}