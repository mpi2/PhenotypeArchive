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

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

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
    protected final String tableXpath;
    
    public static final String NO_INFO_AVAILABLE    = "No information available";
    public static final String NO_ES_CELLS_PRODUCED = "No ES Cell produced";
    
    /**
     * Initializes the generic components of a <code>SearchFacetTable</code>.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param tableXpath The xpath of the search facet table (e.g.
     * //table[@id='geneGrid'])
     * @param timeoutInSeconds
     */
    public SearchFacetTable(WebDriver driver, String tableXpath, int timeoutInSeconds) {
        graphUrl = driver.getCurrentUrl();
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeoutInSeconds);
        this.timeoutInSeconds = timeoutInSeconds;
        this.tableXpath = tableXpath;
        setTable(driver.findElement(By.xpath(tableXpath)));
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
     * 
     * @return The page heading, as a String array
     */
    public String[] getPageHeading() {
        return pageHeading;
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
            table = driver.findElement(By.xpath(tableXpath));
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
     * Validates download data against this search table instance.
     * 
     * @param data The download data used for comparison
     * @return validation status
     */
    public abstract PageStatus validateDownload(String[][] data);
    
    /**
     * Validates the download heading
     * @param facetName the [displayable] name of the facet, for identifying errors
     * @param status validation status
     * @param expectedHeadingList expected download heading column list
     * @param actualHeadingList actual download heading column list
     */
    public static void validateDownloadHeading(String facetName, PageStatus status, String[] expectedHeadingList, String[] actualHeadingList) {
        if (expectedHeadingList.length != actualHeadingList.length) {
            status.addError(facetName + " DOWNLOAD HEADING MISMATCH: expected heading column count: " + expectedHeadingList.length + ". "
                          + "Actual heading count: " + actualHeadingList.length + ". Headings were not compared.");
            return;
        }
        
        for (int i = 0; i < actualHeadingList.length; i++) {
            if ( ! actualHeadingList[i].equals(expectedHeadingList[i])) {
                status.addError(facetName + " DOWNLOAD HEADING MISMATCH: heading[" + i + "] should be '"
                        + expectedHeadingList[i] + "' but actual heading was '" + actualHeadingList[i] + "'.");
            }
        }
    }
}