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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the abstract
 * parent of the search page facet tables. It contains code and data common to
 * all such search page facet tables. Subclasses need only implement <code>
 * validateDownload(String[][] data)</code>.
 */
public abstract class SearchFacetTable {
    public final String graphUrl;
    private boolean hasTable;
    protected String[] pageHeading;
    protected WebElement table;
    protected final WebDriver driver;
    protected final int timeoutInSeconds;
    protected final WebDriverWait wait;
    
    public static final String NO_INFO_AVAILABLE    = "No information available";
    public static final String NO_ES_CELLS_PRODUCED = "No ES Cell produced";
    
    // byHash String keys:
    protected final static String BY_TABLE              = "byTable";
    protected final static String BY_TABLE_TR           = "byTableTr";
    protected final static String BY_SELECT_GRID_LENGTH = "bySelectGridLength";
    protected static final HashMap<String, By> byHash = new HashMap();
    
    /**
     * Initializes the generic components of a <code>SearchFacetTable</code>.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * <code>By</code> definitions for: table, tabletr, and selectxxGridLength.
     * @param timeoutInSeconds timeout
     */
    public SearchFacetTable(WebDriver driver, int timeoutInSeconds) {
        graphUrl = driver.getCurrentUrl();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeoutInSeconds);
        this.timeoutInSeconds = timeoutInSeconds;
        setTable(driver.findElement(byHash.get(BY_TABLE)));
    }
    
    public enum EntriesSelect {
        _10(10),
        _25(25),
        _50(50),
        _100(100);
        
        private final int value;
        
        private EntriesSelect(final int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    /**
     *
     * @return the number of rows in the "xxGrid" table. Always include 1
     * extra for the heading.
     */
    public int computeTableRowCount() {
        // Wait for page.
        List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(byHash.get(BY_TABLE_TR)));
        return elements.size() + 1;
    }
    
    /**
     * Return the number of entries currently showing in the 'entries' drop-down
     * box.
     *
     * @return the number of entries currently showing in the 'entries'
     * drop-down box.
     */
    public int getNumEntries() {
        Select select = new Select(driver.findElement(byHash.get(BY_SELECT_GRID_LENGTH)));
        try {
            return Utils.tryParseInt(select.getFirstSelectedOption().getText());
        } catch (NullPointerException npe) {
            return 0;
        }
    }
    
    /**
     * Set the number of entries in the 'entries' drop-down box.
     * 
     * @param entriesSelect The new value for the number of entries to show.
     */
    public void setNumEntries(EntriesSelect entriesSelect) {
        Select select = new Select(driver.findElement(byHash.get(BY_SELECT_GRID_LENGTH)));
        select.selectByValue(Integer.toString(entriesSelect.getValue()));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(byHash.get(BY_SELECT_GRID_LENGTH), Integer.toString(entriesSelect.getValue())));
    }

    /**
     * 
     * @return true if this page has a searchFaceTable; false otherwise
     */
    public boolean hasTable() {
        return hasTable;
    }

    /**
     * 
     * @return The searchFacetTable <code>WebElement</code>
     */
    public WebElement getTable() {
        return table;
    }

    /**
     * Set the  <code>WebElement</code> search facet table
     * @param table WebElement New instance to set to
     */
    public final void setTable(WebElement table) {
        hasTable = false;
        this.table = table;
        try {
            table = driver.findElement(byHash.get(BY_TABLE));
        } catch (Exception e) {
            pageHeading = null;
            this.table = null;
            return;
        }
        hasTable = true;
        
        // Save the pageHeading values.
        List<WebElement> headingElementList = table.findElements(By.cssSelector("thead tr th"));
        pageHeading = new String[headingElementList.size()];
        if ( ! headingElementList.isEmpty()) {
            for (int colIndex = 0; colIndex < headingElementList.size(); colIndex++) {
                WebElement headingElement = headingElementList.get(colIndex);
                pageHeading[colIndex] = headingElement.getText();
            }
        }
    }

    /**
     * Click the toolbox (the download link that shows/hides the download popup)
     * @param desiredWindowState Open or Close
     */
    public void clickToolbox(SearchPage.WindowState desiredWindowState) {
        String style = driver.findElement(By.xpath("//div[@id='toolBox']")).getAttribute("style");
        switch (desiredWindowState) {
            case CLOSED:
                if (style.contains("block;"))
                    driver.findElement(By.xpath("//span[@id='dnld']")).click();
                break;
                
            case OPEN:
                if (style.contains("none;"))
                    driver.findElement(By.xpath("//span[@id='dnld']")).click();
                break;
        }
    }
    
    /**
     * @return The window state (open or close)
     */
    public SearchPage.WindowState getToolboxState() {
        String style = driver.findElement(By.xpath("//div[@id='toolBox']")).getAttribute("style");
        return (style.contains("block;") ? SearchPage.WindowState.OPEN : SearchPage.WindowState.CLOSED);
    }
    
    /**
     * Validates download data against this search table instance.
     * 
     * @param data The download data used for comparison
     * @return validation status
     */
    public abstract PageStatus validateDownload(String[][] data);
    
    
    // PROTECTED METHODS
    

    /**
     * Validates download data against this search table instance.
     * 
     * @param pageData The page data used for comparison
     * @param pageColumns The page columns used in the comparison
     * @param downloadDataArray The download data used for comparison
     * @param downloadColumns The download columns used in the comparison
     * @param downloadUrl The download stream URL
     * @return validation status
     */
    protected PageStatus validateDownloadInternal(GridMap pageData, Integer[] pageColumns, String[][] downloadDataArray,  Integer[] downloadColumns, String downloadUrl) {
        PageStatus status = new PageStatus();
        List<List<String>> downloadDataList = new ArrayList();
        for (String[] row : downloadDataArray) {
            List rowList = Arrays.asList(row);
            downloadDataList.add(rowList);
        } 
       
        GridMap downloadData = new GridMap(downloadDataList, driver.getCurrentUrl());
        
        // Do a set difference between the rows on the first displayed page
        // and the rows in the download file. The difference should be empty.
        int errorCount = 0;
        
        // Create a pair of sets: one from the page, the other from the download.
        GridMap patchedPageData = TestUtils.patchEmptyFields(pageData);
        Set pageSet = TestUtils.createSet(patchedPageData, pageColumns);
        Set downloadSet = TestUtils.createSet(downloadData, downloadColumns);
        Set difference = TestUtils.cloneStringSet(pageSet);
        difference.removeAll(downloadSet);
        if ( ! difference.isEmpty()) {
            System.out.println("SearchFacetTable.validateDownloadInternal(): Page data not found in download:");
            Iterator it = difference.iterator();
            int i = 0;
            while (it.hasNext()) {
                String value = (String)it.next();
                System.out.println("[" + i + "]:\t page data: " + value);
                System.out.println("\t download data: " + TestUtils.closestMatch(downloadSet, value) + "\n");
                i++;
                errorCount++;
            }
        }

        if (errorCount > 0) {
            status.addError("Mismatch.");
        }
        
        return status;
    }
}