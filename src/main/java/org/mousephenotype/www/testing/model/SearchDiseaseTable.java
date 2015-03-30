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
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'diseaseGrid' HTML table for diseases.
 */
public class SearchDiseaseTable extends SearchFacetTable {
    
    public static final int COL_INDEX_DISEASE_ID      = 0;
    public static final int COL_INDEX_DISEASE_NAME    = 1;
    public static final int COL_INDEX_SOURCE          = 2;
    public static final int COL_INDEX_CURATED_HUMAN   = 3;
    public static final int COL_INDEX_CURATED_MICE    = 4;
    public static final int COL_INDEX_CANDIDATE_IMPC  = 5;
    public static final int COL_INDEX_CANDIDATE_MGI   = 6;
    public static final int COL_INDEX_LAST = COL_INDEX_CANDIDATE_MGI;           // Should always point to the last (highest-numbered) index.
    
    private final List<DiseaseRow> bodyRows = new ArrayList();
    private final GridMap pageData;

    private final static Map<TableComponent, By> map = new HashMap();
    static {
        map.put(TableComponent.BY_TABLE, By.xpath("//table[@id='diseaseGrid']"));
        map.put(TableComponent.BY_TABLE_TR, By.xpath("//table[@id='diseaseGrid']/tbody/tr"));
        map.put(TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='diseaseGrid_length']"));
    }
    
    /**
     * Creates a new <code>SearchDiseaseTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchDiseaseTable(WebDriver driver, int timeoutInSeconds) {
        super(driver, timeoutInSeconds, SearchDiseaseTable.map);
        
        pageData = load();
    }
    
    /**
     * Validates download data against this <code>SearchAnatomyTable</code>
     * instance.
     * 
     * @param downloadDataArray The download data used for comparison
     * @return validation status
     */
    @Override
    public PageStatus validateDownload(String[][] downloadDataArray) {
        final Integer[] pageColumns = {
              COL_INDEX_DISEASE_ID
            , COL_INDEX_DISEASE_NAME
            , COL_INDEX_SOURCE
        };
        final Integer[] downloadColumns = {
              DownloadSearchMapDiseases.COL_INDEX_DISEASE_ID
            , DownloadSearchMapDiseases.COL_INDEX_DISEASE_NAME
            , DownloadSearchMapDiseases.COL_INDEX_SOURCE
        };
        
        return validateDownloadInternal(pageData, pageColumns, downloadDataArray, downloadColumns, driver.getCurrentUrl());
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Pulls all rows of data and column access variables from the search page's
     * 'diseaseGrid' HTML table.
     *
     * @return <code>numRows</code> rows of data and column access variables
     * from the search page's 'diseaseGrid' HTML table.
     */
    private GridMap load() {
        return load(null);
    }

    /**
     * Pulls <code>numRows</code> rows of search page gene facet data and column
     * access variables from the search page's 'diseaseGrid' HTML table.
     *
     * @param numRows the number of <code>GridMap</code> table rows to return,
     * including the heading row. To specify all rows, set <code>numRows</code>
     * to null.
     * @return <code>numRows</code> rows of search page gene facet data and
     * column access variables from the search page's 'diseaseGrid' HTML table.
     */
    private GridMap load(Integer numRows) {
        if (numRows == null)
            numRows = computeTableRowCount();
        
        String[][] pageArray;
        
        // Wait for page.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#diseaseGrid")));
        int numCols = COL_INDEX_LAST + 1;
        
        pageArray = new String[numRows][numCols];                               // Allocate space for the data.
        for (int i = 0; i < numCols; i++) {
            pageArray[0][i] = "Column_" + i;                                    // Set the headings.
        }
        
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            int sourceRowIndex = 1;
        
            pageArray[sourceRowIndex][COL_INDEX_DISEASE_ID] = "";               // Insure there is always a non-null value.
            pageArray[sourceRowIndex][COL_INDEX_DISEASE_NAME] = "";             // Insure there is always a non-null value.
            pageArray[sourceRowIndex][COL_INDEX_SOURCE] = "";                   // Insure there is always a non-null value.
            for (WebElement bodyRowElements : bodyRowElementsList) {                                    // diseaseId, diseaseName, source, curatedHuman, curatedMice, candidateIMPC, candidateMGI
                DiseaseRow diseaseRow = new DiseaseRow();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                WebElement element = bodyRowElementList.get(0).findElement(By.cssSelector("a"));        // Get 'Disease' element.
                diseaseRow.diseaseIdLink = element.getAttribute("href");
                int pos = diseaseRow.diseaseIdLink.lastIndexOf("/");
                
                diseaseRow.diseaseId = diseaseRow.diseaseIdLink.substring(pos + 1);                     // Add diseaseId   to row element 0 from 'Disease' element.
                pageArray[sourceRowIndex][COL_INDEX_DISEASE_ID] = diseaseRow.diseaseId;
                
                diseaseRow.diseaseName = element.getText();   
                pageArray[sourceRowIndex][COL_INDEX_DISEASE_NAME] = diseaseRow.diseaseName;
                
                diseaseRow.source = bodyRowElementList.get(1).getText();     
                pageArray[sourceRowIndex][COL_INDEX_SOURCE] = diseaseRow.source;                           // Add source      to row element 2 from 'Source' element.
                
                sourceRowIndex++;
                bodyRows.add(diseaseRow);
            }
        }
        
        return new GridMap(pageArray, driver.getCurrentUrl());
    }
    
    
    
    // PRIVATE CLASSES
    
    
    private class DiseaseRow {
        private String diseaseId = "";
        private String diseaseIdLink = "";
        private String diseaseName = "";
        private String source = "";
    }

}