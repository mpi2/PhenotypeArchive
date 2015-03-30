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
import java.util.Comparator;
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
 * components of a search page 'maGrid' HTML table for anatomy.
 */
public class SearchAnatomyTable extends SearchFacetTable {
    
    public static final int COL_INDEX_ANATOMY_TERM     = 0;
    public static final int COL_INDEX_ANATOMY_ID       = 1;
    public static final int COL_INDEX_ANATOMY_SYNONYMS = 2;
    public static final int COL_INDEX_LAST = COL_INDEX_ANATOMY_SYNONYMS;        // Should always point to the last (highest-numbered) index.
    
    private final List<AnatomyRow> bodyRows = new ArrayList();
    private final GridMap pageData;
    
    private final static Map<TableComponent, By> map = new HashMap();
    static {
        map.put(TableComponent.BY_TABLE, By.xpath("//table[@id='maGrid']"));
        map.put(TableComponent.BY_TABLE_TR, By.xpath("//table[@id='maGrid']/tbody/tr"));
        map.put(TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='maGrid_length']"));
    }
    
    /**
     * Creates a new <code>SearchAnatomyTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchAnatomyTable(WebDriver driver, int timeoutInSeconds) {
        super(driver, timeoutInSeconds, map);
        
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
              COL_INDEX_ANATOMY_ID
            , COL_INDEX_ANATOMY_TERM
        };
        final Integer[] downloadColumns = {
              DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_ID
            , DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM
        };
        
        return validateDownloadInternal(pageData, pageColumns, downloadDataArray, downloadColumns, driver.getCurrentUrl());
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Pulls all rows of data and column access variables from the search page's
     * 'maGrid' HTML table.
     *
     * @return <code>numRows</code> rows of data and column access variables
     * from the search page's 'maGrid' HTML table.
     */
    private GridMap load() {
        return load(null);
    }

    /**
     * Pulls <code>numRows</code> rows of search page gene facet data and column
     * access variables from the search page's 'maGrid' HTML table.
     *
     * @param numRows the number of <code>GridMap</code> table rows to return,
     * including the heading row. To specify all rows, set <code>numRows</code>
     * to null.
     * @return <code>numRows</code> rows of search page gene facet data and
     * column access variables from the search page's 'maGrid' HTML table.
     */
    private GridMap load(Integer numRows) {
        if (numRows == null)
            numRows = computeTableRowCount();
        
        String[][] pageArray;
        
        // Wait for page.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#maGrid")));
        int numCols = COL_INDEX_LAST + 1;
        
        pageArray = new String[numRows][numCols];                               // Allocate space for the data.
        for (int i = 0; i < numCols; i++) {
            pageArray[0][i] = "Column_" + i;                                    // Set the headings.
        }
        
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            int sourceRowIndex = 1;
            
            pageArray[sourceRowIndex][COL_INDEX_ANATOMY_ID] = "";                                   // Insure there is always a non-null value.
            pageArray[sourceRowIndex][COL_INDEX_ANATOMY_TERM] = "";                                 // Insure there is always a non-null value.
            for (WebElement bodyRowElements : bodyRowElementsList) {
                AnatomyRow anatomyRow = new AnatomyRow();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                WebElement element = bodyRowElementList.get(0).findElement(By.cssSelector("a"));
                anatomyRow.anatomyIdLink = element.getAttribute("href");
                int pos = anatomyRow.anatomyIdLink.lastIndexOf("/");
                
                anatomyRow.anatomyId = anatomyRow.anatomyIdLink.substring(pos + 1);                 // anatomyId.
                pageArray[sourceRowIndex][COL_INDEX_ANATOMY_ID] = anatomyRow.anatomyId;
                
                anatomyRow.anatomyTerm = element.getText();                                         // anatomyTerm.
                pageArray[sourceRowIndex][COL_INDEX_ANATOMY_TERM] = anatomyRow.anatomyTerm;
                
                sourceRowIndex++;
                bodyRows.add(anatomyRow);
            }
        }
        
        return new GridMap(pageArray, driver.getCurrentUrl());
    }
    
    
    // PRIVATE CLASSES
    
    
    private class AnatomyRow {
        private String anatomyId = "";
        private String anatomyIdLink = "";
        private String anatomyTerm = "";
        private List<String> synonyms = new ArrayList();
    
        // NOTE: We don't need these (as sorting the arrays does not solve the problem). However, I'm leaving these in
        //       because they are a good example of how to sort String[][] objects.
        // o1 and o2 are of type String[].
        private class PageComparator implements Comparator<String[]> {

            @Override
            public int compare(String[] o1, String[] o2) {
                if ((o1 == null) && (o2 == null))
                    return 0;
                if (o1 == null)
                    return -1;
                if (o2 == null)
                    return 1;

                // We're only interested in the COL_INDEX_ANATOMY_TERM column.
                String op1 = ((String[])o1)[COL_INDEX_ANATOMY_TERM];
                String op2 = ((String[])o2)[COL_INDEX_ANATOMY_TERM];

                if ((op1 == null) && (op2 == null))
                    return 0;
                if (op1 == null)
                    return -1;
                if (op2 == null)
                    return 1;

                return op1.compareTo(op2);
            }
        }

        // o1 and o2 are of type String[].
        private class DownloadComparator implements Comparator<String[]> {

            @Override
            public int compare(String[] o1, String[] o2) {
                if ((o1 == null) && (o2 == null))
                    return 0;
                if (o1 == null)
                    return -1;
                if (o2 == null)
                    return 1;

                // We're only interested in the DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM column.
                String op1 = ((String[])o1)[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM];
                String op2 = ((String[])o2)[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM];

                if ((op1 == null) && (op2 == null))
                    return 0;
                if (op1 == null)
                    return -1;
                if (op2 == null)
                    return 1;

                return op1.compareTo(op2);
            }
        }
    }

}