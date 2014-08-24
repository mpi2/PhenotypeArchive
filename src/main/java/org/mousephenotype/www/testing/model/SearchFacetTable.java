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
    private String[][] bodyRows;
    private String[] headings;
    
    /**
     * Initializes the generic components of a <code>SearchFacetTable</code>.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param tableXpath The xpath of the search facet table (e.g.
     * //table[@id='geneGrid'])
     */
    public SearchFacetTable(WebDriver driver, String tableXpath) {
        graphUrl = driver.getCurrentUrl();
        hasTable = false;
        
        WebElement table;
        try {
            table = driver.findElement(By.xpath(tableXpath));
        } catch (Exception e) {
            return;
        }
        hasTable = true;
        
        // Save the heading values.
        List<WebElement> headingElementList = table.findElements(By.cssSelector("thead tr th"));
        headings = new String[headingElementList.size()];
        if ( ! headingElementList.isEmpty()) {
            for (int colIndex = 0; colIndex < headingElementList.size(); colIndex++) {
                WebElement headingElement = headingElementList.get(colIndex);
                headings[colIndex] = headingElement.getText();
            }
        }
        
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        List<String[]> bodyRowsList = new ArrayList();
        if ( ! bodyRowElementsList.isEmpty()) {
            for (WebElement bodyRowElements : bodyRowElementsList) {
                
                String[] row = new String[headingElementList.size()];
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                for (int colIndex = 0; colIndex < headingElementList.size(); colIndex++) {
                    row[colIndex] = bodyRowElementList.get(colIndex).getText();
                }
                bodyRowsList.add(row);
            }
            
            bodyRows = bodyRowsList.toArray(new String[bodyRowsList.size()][headingElementList.size()]);
        }
    }

    public String[][] getBodyRowsList() {
        return bodyRows;
    }

    public String[] getHeadingList() {
        return headings;
    }

    public boolean hasGeneTable() {
        return hasTable;
    }
    
    /**
     * Validates download data against this search table instance.
     * 
     * @param data The download data used for comparison
     * @return validation status
     */
    public abstract PageStatus validateDownload(String[][] data);
}
