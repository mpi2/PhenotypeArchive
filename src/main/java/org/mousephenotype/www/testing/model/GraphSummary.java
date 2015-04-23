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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent a graph
 * section summary (i.e. the table beneath the graph. E.g. categorical graph
 * sections have a summary HTML table with id 'catTable'; unidimensional graph
 * sections have a summary HTML table with id 'continuousTable', etc.)
 */
public class GraphSummary {
    private final List<Row> bodyRowsList = new ArrayList();
    private final List<String> headingList = new ArrayList();
    
    /**
     * Creates a new <code>GraphCatTable</code> instance.
     *
     * @param table A <code>WebElement</code> instance pointing to an HTML table
     * with id 'catTable' with thead and tbody definitions.
     */
    public GraphSummary(WebElement table) {
        
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
        private final HashMap<String, String> categoryHash;                     // key = category name. Value = category value.
        
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
            
            categoryHash = new HashMap();
            // Save each category and its value to the categoryHash. Skip these columns:
            //     "Control/Hom/Het" (col 0), "P Value" (col n - 2), and "Effect Size" (col n - 1).
            for (int i = 1; i < row.size() - 2; i++) {
                categoryHash.put(headingList.get(i), row.get(i));
            }
        }

        public HashMap<String, String> getCategoryHash() {
            return categoryHash;
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