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
    private final String graphUrl;
    private final String[][] data;
    
    /**
     * Creates a new <code>ContinuousGraphTable</code> instance initialized with
     * data.
     * @param driver A <code>WebDriver</code> instance pointing to the graph page
     * with the continuousTable with thead and tbody definitions.
     */
    public GraphContinuousTable(WebDriver driver) {
        WebElement table = driver.findElement(By.xpath("//table[@id='continuousTable]'"));
        List<WebElement> bodyRowsList = table.findElements(By.xpath("//tbody/tr"));
        
        graphUrl = driver.getCurrentUrl();                                      // Get graphUrl.
        
        WebElement headerRow = table.findElement(By.xpath("//thead/tr"));       // Get the header row.
        List<WebElement> colsList = headerRow.findElements(By.xpath("//td"));
        data = new String[1 + bodyRowsList.size()][colsList.size()];            // Allocate space for the array elements (and one for the header).

        int rowIndex = 1;
        for (WebElement bodyRow : bodyRowsList) {
            List<WebElement> bodyColsList = bodyRow.findElements(By.xpath("//td"));
            int colIndex = 0;
            for (WebElement bodyCell : bodyColsList) {
                data[rowIndex][colIndex] = bodyCell.getText();
                colIndex++;
            }
            
            rowIndex++;
        }
    }

    public String getGraphUrl() {
        return graphUrl;
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
 
}