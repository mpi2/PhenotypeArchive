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

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 *
 * @author mrelac
 * 
 * This abstract class implements code common to derived classes, leaving 
 * stream-specific  customizations to the derived classes.
 */
public abstract class DataReader {
    
    protected final URL url;
    
    public DataReader(URL url) {
        this.url = url;
    }
    
    public abstract void open() throws IOException;
    public abstract void close() throws IOException;
    public abstract List<String> getLine() throws IOException;
    public abstract DataType getType();
    
    /**
     * @return  All rows of data from the stream created by 
     * invoking the url provided with the constructor. Supported stream formats
     * are defined in the public enum <code>DataReader.DataType</code>.
     */
    public String[][] getData() {
        return getData(null);
    }
    
    /**
     * @param maxRows the maximum number of download stream rows to return, including
     * any headings. To specify all rows, set <code>maxRows</code> to null.
     * @return <code>maxRows</code> rows of data (including headings) from the stream created by 
     * invoking the url provided with the constructor. Supported stream formats
     * are defined in the public enum <code>DataReader.DataType</code>.
     */
    public String[][] getData(Integer maxRows) {
        
        if (maxRows == null)
            maxRows = lineCount();
        
        String[][] data = new String[maxRows][];
        DataReader dataReader = null;
        try {
            dataReader = DataReaderFactory.create(url);
 //System.out.println("After create()");
            dataReader.open();
 //System.out.println("After open()");
            List<String> line;
            for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
                line = dataReader.getLine();
                if (line == null)
                    break;
                
                data[rowIndex] = new String[line.size()];
                for (int colIndex = 0; colIndex < line.size(); colIndex++) {
                    data[rowIndex][colIndex] = line.get(colIndex);
                }
            }
        } catch (IOException e) {
            System.out.println("EXCEPTION: " + e.getLocalizedMessage());
        } finally {
            try {
                if (dataReader != null)
                    dataReader.close();
            } catch (IOException e) {
                System.out.println("EXCEPTION: " + e.getLocalizedMessage());
            }
        }
        
        return data;
    }
    
    /**
     * @return the number of lines in this <code>DataReader</code> stream,
     * including headings.
     */
    public int lineCount() {
        int lineCount = 0;
        DataReader dataReader = null;
        try {
            dataReader = DataReaderFactory.create(url);
            dataReader.open();
            
            while ((dataReader.getLine()) != null) {
                lineCount++;
            }
        } catch (IOException e) {
            System.out.println("EXCEPTION: " + e.getLocalizedMessage());
        } finally {
            try {
                if (dataReader != null)
                    dataReader.close();
            } catch (IOException e) {
                System.out.println("EXCEPTION: " + e.getLocalizedMessage());
            }
        }
        
        return lineCount;
    }
    
    public enum DataType {
        TSV,
        XLS
    }
    

}
