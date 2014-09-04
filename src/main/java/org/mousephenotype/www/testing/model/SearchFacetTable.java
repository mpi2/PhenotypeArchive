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
        this.timeoutInSeconds = timeoutInSeconds;
        hasTable = false;
        
        try {
            table = driver.findElement(By.xpath(tableXpath));
        } catch (Exception e) {
            pageHeading = null;
            table = null;
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

    public String[] getPageHeading() {
        return pageHeading;
    }

    public boolean hasTable() {
        return hasTable;
    }

    public WebElement getTable() {
        return table;
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
     * @param term the [unique] identifying term on the page
     * @param expectedHeadingList expected download heading column list
     * @param actualHeadingList actual download heading column list
     */
    protected void validateDownloadHeading(String facetName, PageStatus status, String term, String[] expectedHeadingList, String[] actualHeadingList) {
        if (expectedHeadingList.length != actualHeadingList.length) {
            status.addError(facetName + " DOWNLOAD HEADING MISMATCH for term: " + term + ": expected heading column count: " + expectedHeadingList.length + ". "
                          + "Actual heading count: " + actualHeadingList.length + ". Headings were not compared.");
            return;
        }
        
        for (int i = 0; i < actualHeadingList.length; i++) {
            if ( ! actualHeadingList[i].equals(expectedHeadingList[i])) {
                status.addError(facetName + " DOWNLOAD HEADING MISMATCH for term: " + term + ": heading[" + i + "] should be '"
                        + expectedHeadingList[i] + "' but actual heading was '" + actualHeadingList[i] + "'.");
            }
        }
    }
}