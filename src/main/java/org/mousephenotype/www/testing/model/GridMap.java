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

import java.util.HashMap;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent access functionality
 * to a two-dimensional data grid composed of type <code>String</code>. Callers
 * pass in a <code>String[][]</code> containing the data store where the first
 * line is a heading. The class then serves up access to the data by row index
 * and either column index or column name, which must match exactly. No 
 * IndexOutOfBounds exception checking is done.
 */
public class GridMap {
    private final String[][] data;
    private final String target;
    private final HashMap<String, Integer> colNameHash = new HashMap();
    
    /**
     * Creates a <code>GridMap</code> instance
     * 
     * @param data the data store
     * @param target the target url of the page containing the data
     */
    public GridMap(String[][] data, String target) {
        this.data = data;
        this.target = target;
        
        int i = 0;
        for (String colHeading : data[0]) {
            colNameHash.put(colHeading, i);
        }
    }
    
    public String[][] getData() {
        return data;
    }
    
    public String[] getHeading() {
        if ((data != null) && (data.length > 0)) {
            return data[0];
        }
        
        return new String[0];
    }
    
    public String[][] getBody() {
        if ((data != null) && (data.length > 1)) {
            String[][] clone = new String[data.length - 1][data[0].length];
            for (int i = 0; i < data.length - 1; i++) {
                clone[i] = data[i + 1];
            }
            
            return clone;
        }
        
        return new String[0][0];
    }
    
    public String getCell(int rowIndex, int colIndex) {
        if (data != null)
            return data[rowIndex][colIndex];
        
        return "";
    }
    
    public String getCell(int rowIndex, String colName) {
        if ( ! colNameHash.containsKey(colName))
            throw new RuntimeException("ERROR: Column " + colName + " was not found.");
        
        return data[rowIndex][colNameHash.get(colName)];
    }

    public String getTarget() {
        return target;
    }

}
