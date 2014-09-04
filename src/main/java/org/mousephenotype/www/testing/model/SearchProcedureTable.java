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
        HashMap<String, String[]> downloadHash = new HashMap();
        
        if ((bodyRows.isEmpty()) || (downloadData.length == 0))
            return status;
        
        // This validation gets called with paged data (e.g. only the rows showing in the displayed page)
        // and with all data (the data for all of the pages). As such, the only effective way to validate
        // it is to stuff the download data elements into a hash, then loop through the pageData rows
        // querying the downloadData hash for each value (then removing that value from the hash to handle duplicates).
        for (int i = 1; i < downloadData.length; i++) {
            // Copy all but the pageHeading into the hash.
            String[] row = downloadData[i];
            downloadHash.put(row[DownloadSearchMapProcedures.COL_INDEX_PARAMETER], row);
        }
        
        for (ProcedureRow pageRow : bodyRows) {
            String[] downloadRow = downloadHash.get(pageRow.parameter);
            if (downloadRow == null) {
                status.addError("PARAMETER MISMATCH: page value parameter = '" + pageRow.parameter + "' was not found in the download file.");
                continue;
            }
            downloadHash.remove(pageRow.parameter);                             // Remove the pageRow from the download hash.
            
            // Validate the pageHeading.
            String[] expectedHeadingList = {
                "Parameter"
              , "Procedure"
              , "Procedure Impress link"
              , "Pipeline"
            };
            validateDownloadHeading("PROCEDURE", status, pageRow.parameter, expectedHeadingList, downloadData[0]);
            
            // Verify the components.
            
            // procedure.
            if (pageRow.procedure.isEmpty()) {
                status.addError("PROCEDURE MISSING: Expected procedure but there was none.");
            }
            if ( ! pageRow.procedure.equals(downloadRow[DownloadSearchMapProcedures.COL_INDEX_PROCEDURE]))
                status.addError("PROCEDURE MISMATCH: procedure '" + pageRow.procedure + "' page value procedure = '" + pageRow.procedure + "' doesn't match download value '" + downloadRow[DownloadSearchMapProcedures.COL_INDEX_PROCEDURE] + "'.");

            // Impress Procedure Link.
            if (pageRow.procedureLink.isEmpty()) {
                status.addError("PROCEDURE MISSING: Expected procedure link but there was none.");
            }
            if ( ! pageRow.procedureLink.contains(downloadRow[DownloadSearchMapProcedures.COL_INDEX_IMPRESS_LINK].replace("https", "").replace("http", ""))) {
                status.addError("PROCEDURE MISMATCH: procedure '" + pageRow.procedure + "' page value procedureLink = '" + pageRow.procedureLink + "' doesn't match download value '" + downloadRow[DownloadSearchMapProcedures.COL_INDEX_IMPRESS_LINK] + "'.");
            }
            
            // pipeline.
            if (pageRow.pipeline.isEmpty()) {
                status.addError("PROCEDURE MISSING: Expected pipeline but there was none.");
            }
            if ( ! pageRow.pipeline.equals(downloadRow[DownloadSearchMapProcedures.COL_INDEX_PIPELINE]))
                status.addError("PROCEDURE MISMATCH: procedure '" + pageRow.procedure + "' page value pipeline = '" + pageRow.pipeline + "' doesn't match download value '" + downloadRow[DownloadSearchMapProcedures.COL_INDEX_PIPELINE] + "'.");
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