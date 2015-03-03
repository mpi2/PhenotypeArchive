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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    
    /**
     * Creates a <code>GridMap</code> instance
     * 
     * @param dataList the data store
     * @param target the target URL of the page containing the data
     */
    public GridMap(List<List<String>> dataList, String target) {
        data = new String[dataList.size()][dataList.get(0).size()];
        for (int rowIndex = 0; rowIndex <  dataList.size(); rowIndex++) {
            List<String> row = dataList.get(rowIndex);
            for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                String cellValue = row.get(colIndex);
                data[rowIndex][colIndex] = cellValue;
            }
        }
        
        for (String colHeading : data[0]) {
            colNameHash.put(colHeading, 0);
        }
        
        this.target = target;
    }
    
    /**
     * Creates a set from <code>input</code> using <code>colIndexes</code>, using
     * the underscore character as a column delimiter. Each value is first trimmed,
     * then lowercased.
     * 
     * Example: input.body[][] = "a", "b", "c", "d", "e"
     *                           "f", "g", "h", "i", "j"
     * 
     * colIndexes = 1, 3, 4
     * 
     * produces a set that looks like:  "b_d_e_"
     *                                  "g_i_j_"
     * @param colIndexes indexes of columns to be copied
     * @return a set containing the concatenated values.
     */
    public Set<String> createSet(Integer[] colIndexes) {
        return TestUtils.createSet(this, colIndexes);
    }
    
    public GridMap urlDecode(List<Integer> colIndexes) {
        if ((data == null) || data.length == 0) {
            return this;
        }
        
        String[][] localData = new String[data.length][data[0].length];
        for (int rowIndex = 0; rowIndex < localData.length; rowIndex++) {
            String[] row = data[rowIndex];
            for (int colIndex = 0; colIndex < row.length; colIndex++) {
                String cell = row[colIndex];
                if (colIndexes.contains(colIndex)) {
                    cell = TestUtils.urlDecode(cell);
                }
                localData[rowIndex][colIndex] = cell;
            }
        }
        
        GridMap retVal = new GridMap(localData, target);
        
        return retVal;
    }
    
    /**
     * Decodes <code>url</code>, into UTF-8, making it suitable to use as a link.
     * Invalid url strings are ignored and the original string is returned.
     * @param url the url to decode
     * @param colIndexes indexes of columns to be copied
     * @return the decoded url
     */
    public static Set<String> urlDecode(Set<String> url, String[] colIndexes) {
        Set<String> retVal = new HashSet();
        try {
            Iterator<String> it = url.iterator();
            while (it.hasNext()) {
                retVal.add(TestUtils.urlDecode(it.next()));
            }
        } catch (Exception e) {
            System.out.println("Decoding of value '" + (url == null ? "<null>" : url) + "' failed: " + e.getLocalizedMessage());
        }
        
        return retVal;
    }
    
    
    // GETTERS AND SETTERS
    
    
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
