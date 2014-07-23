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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author mrelac
 * 
 * This <code>DataReader</code> implementation handles Microsoft Excel streams.
 */
public class DataReaderXls extends DataReader {
    
    private Iterator<Row> rowIterator = null;
    Workbook workbook;
    
    /**
     * Instantiates a new <code>DataReader</code> that knows how to serve up
     * Microsoft Excel XLS streams
     * 
     * @param url The url defining the input stream
     */
    public DataReaderXls(URL url) {
        super(url);
    }
    
    /**
     * Opens the stream defined by the url used in the constructor.
     * @throws IOException 
     */
    @Override
    public void open() throws IOException {
        try {
            try (InputStream inputStream = url.openStream()) {
                workbook = WorkbookFactory.create(inputStream);
            }
            Sheet sheet = workbook.getSheetAt(0);
            rowIterator = sheet.rowIterator();
        } catch (InvalidFormatException e) {
            System.out.println("InvalidFormatException: " + e.getLocalizedMessage());
            throw new IOException(e);
        }
    }
    
    /**
     * Closes the stream defined by the url used in the constructor.
     * 
     * @throws IOException 
     */
    @Override
    public void close() throws IOException {
        // nothing to do.
    }
    
    /**
     * Returns the next line as a <code>List</code> of <code>String</code> if 
     * there is still data; null otherwise
     * @return  the next line as a <code>List</code> of <code>String</code> if 
     * there is still data; null otherwise
     * 
     * @throws IOException
     */
    @Override
    public List<String> getLine() throws IOException {
        List<String> line = new ArrayList();
        if ( ! rowIterator.hasNext())
            return null;
        
        Row row = rowIterator.next();
        
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String cellValue = cell.getStringCellValue();
            line.add(cellValue);
        }
        return line;
    }
    
    /**
     * Returns the data type
     * @return the data type
     */
    @Override
    public DataType getType() {
        return DataType.XLS;
    }
    
    // GETTERS
    
    
    /**
     * Returns the url
     * @return the url
     */
    public URL getUrl() {
        return url;
    }
    
}