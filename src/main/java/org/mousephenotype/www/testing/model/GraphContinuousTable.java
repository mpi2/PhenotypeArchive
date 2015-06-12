/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.www.testing.model;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the
 * important components of a graph page 'continuousTable' HTML table. NOTE: A
 * unidimensional graph page can contain more than one GraphContinuousTable.
 */
public class GraphContinuousTable {
    private String[][] data;
    private final List<Row> bodyRowsList = new ArrayList();
    private final List<String> headingList = new ArrayList();
    
    // Column offsets
    public final int COL_INDEX_CONTROL_HOM_HET = 0;
    public final int COL_INDEX_MEAN = 1;
    public final int COL_INDEX_STANDARD_ERROR = 2;
    public final int COL_INDEX_COUNT = 3;
    
    /**
     * Creates a new <code>ContinuousGraphTable</code> instance initialized with
     * data.
     * 
     * @param continuousTableElement A <code>WebElement</code> instance pointing
     * to an HTML table with id 'continuousTable' with thead and tbody
     * definitions.
     */
    public GraphContinuousTable(WebElement continuousTableElement) {
        data = new String[0][0];
        List<WebElement> bodyRowList = continuousTableElement.findElements(By.cssSelector("tbody tr"));

        WebElement headingRow = continuousTableElement.findElement(By.cssSelector("thead tr"));  // Get the heading row.
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
        List<WebElement> headingElementList = continuousTableElement.findElements(By.cssSelector("thead tr th"));
        if ( ! headingElementList.isEmpty()) {
            for (WebElement headingElement : headingElementList) {
                headingList.add(headingElement.getText());
            }
        }

        // Save the body values.
        List<WebElement> bodyRowElementsList = continuousTableElement.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            for (WebElement bodyRowElements : bodyRowElementsList) {
                ArrayList<String> row = new ArrayList();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                for (WebElement bodyRowElement : bodyRowElementList) {
                    row.add(bodyRowElement.getText());
                }
                bodyRowsList.add(new Row(row, headingList));
            }
        }
    }

    public List<Row> getBodyRowsList() {
        return bodyRowsList;
    }

    public List<String> getHeadingList() {
        return headingList;
    }
    
    public class Row {
        public final ArrayList<String> row;
        public final Sex sex;
        public final Group group;
        public final String zygosity;
        public final int count;
        
        public Row(ArrayList<String> row, List<String> headingList) {
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