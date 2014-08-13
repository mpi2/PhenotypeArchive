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
 * This class encapsulates the code and data necessary to represent the important
 * components of a graph page 'continuousTable' HTML table.
 */
public class GraphContinuousTable {
    public final String graphUrl;
    private String[][] data;
    private boolean hasContinuousTable;
    
    /**
     * Creates a new <code>ContinuousGraphTable</code> instance initialized with
     * data.
     * @param driver A <code>WebDriver</code> instance pointing to the graph page
     * with the continuousTable with thead and tbody definitions.
     */
    public GraphContinuousTable(WebDriver driver) {
        graphUrl = driver.getCurrentUrl();
        data = new String[0][0];
        hasContinuousTable = false;
        
        WebElement table;
        try {
            table = driver.findElement(By.xpath("//table[@id='continuousTable']"));
        } catch (Exception e) {
            return;
        }
        hasContinuousTable = true;
        
        List<WebElement> bodyRowList = table.findElements(By.cssSelector("tbody tr"));
        
        WebElement headingRow = table.findElement(By.cssSelector("thead tr"));  // Get the heading row.
        List<WebElement> headingElements = headingRow.findElements(By.cssSelector("th"));
        data = new String[1 + bodyRowList.size()][headingElements.size()];      // Allocate space for the array elements (and one for the header).
        
        int colIndex = 0;
        for (WebElement headingElement : headingElements) {                     // Save the column headeings.
            data[0][colIndex] = headingElement.getText();
            colIndex++;
        }

        int rowIndex = 1;
        for (WebElement bodyRow : bodyRowList) {
            List<WebElement> bodyElements = bodyRow.findElements(By.cssSelector("td"));
            colIndex = 0;
            for (WebElement bodyElement : bodyElements) {                       // Save the column values.
                data[rowIndex][colIndex] = bodyElement.getText();
                colIndex++;
            }
            
            rowIndex++;
        }
    }

    public String[][] getData() {
        return data;
    }
    
    /**
     * 
     * @param otherTable the other table to compare against
     * @return true if this table's row and column values match <code>otherTable
     * </code> in count and value; false otherwise
     */
    public boolean isEqual(GraphContinuousTable otherTable) {
        if (otherTable == null)
            return false;
        if (data.length != otherTable.data.length)
            return false;
        for (int i = 0; i < data.length; i++) {
            if (data[i].length != otherTable.data[i].length)
                return false;
            for (int j = 0; j < data[i].length; j++) {
                if ((data[i][j] == null) || (otherTable.data[i][j] == null))
                    return false;
                if ( ! data[i][j].equals(otherTable.data[i][j]))
                    return false;
            }
        }
        
        return true;
    }
    
    // Column offset getters
    public int getColIndexControlHomHet() { return 0; }
    public int getColIndexMean() { return 1; }
    public int getColIndexSd() { return 2; }
    public int getColIndexCount() { return 3; }
 
    
    // SETTERS AND GETTERS
    
    
    public boolean hasContinuousTable() {
        return hasContinuousTable;
    }
    
}