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
import uk.ac.ebi.phenotype.util.Utils;

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
    private ArrayList<Row> bodyRowsList = new ArrayList();
    private ArrayList<String> headingList = new ArrayList();
    
    // Column offsets
    public final int COL_INDEX_CONTROL_HOM_HET = 0;
    public final int COL_INDEX_MEAN = 1;
    public final int COL_INDEX_STANDARD_ERROR = 2;
    public final int COL_INDEX_COUNT = 3;
    
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
        
        // Save the heading values.
        List<WebElement> headingElementList = table.findElements(By.cssSelector("thead tr th"));
        if ( ! headingElementList.isEmpty()) {
            for (WebElement headingElement : headingElementList) {
                headingList.add(headingElement.getText());
            }
        }
        
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            for (WebElement bodyRowElements : bodyRowElementsList) {
                ArrayList<String> row = new ArrayList();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                for (WebElement bodyRowElement : bodyRowElementList) {
                    row.add(bodyRowElement.getText());
                }
                bodyRowsList.add(new Row(row));
            }
        }
    }

    public String[][] getData() {
        return data;
    }

    public ArrayList<Row> getBodyRowsList() {
        return bodyRowsList;
    }

    public ArrayList<String> getHeadingList() {
        return headingList;
    }
    
    /**
     *
     * @return true if a continuous table was found and initialized; false otherwise.
     */
    public boolean hasContinuousTable() {
        return hasContinuousTable;
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
    
    public class Row {
        public final ArrayList<String> row;
        public final Sex sex;
        public final Group group;
        public final String zygosity;
        public final int count;
        
        public Row(ArrayList<String> row) {
            this.row = row;
            
            String[] controlHomHet = row.get(0).split(" ");
            switch (controlHomHet[0]) {
                case "Female":
                    sex = Sex.FEMALE;
                    break;
                    
                default:
                    sex = Sex.MALE;
            }
            switch (controlHomHet[1]) {
                case "Control":
                    group = Group.CONTROL;
                    zygosity = "";
                    break;
                    
                default:
                    group = Group.EXPERIMENTAL;
                    zygosity = controlHomHet[1];
            }
            
            Integer tempCount = Utils.tryParseInt(row.get(COL_INDEX_COUNT));
            count = (tempCount == null ? 0 : tempCount);
        }
    }
    
    public enum Sex {
        FEMALE,
        MALE
    }
    
    public enum Group {
        CONTROL,
        EXPERIMENTAL
    }
    
}