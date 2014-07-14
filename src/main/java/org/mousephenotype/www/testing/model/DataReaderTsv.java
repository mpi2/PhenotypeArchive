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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.mousephenotype.www.testing.model.DataReader.DataType;

/**
 *
 * @author mrelac
 * 
 * This <code>DataReader</code> implementation handles tab-separated streams.
 */
public class DataReaderTsv extends DataReaderImpl {
    private BufferedReader bufferedReader = null;
    
    /**
     * Instantiates a new <code>DataReader</code> that knows how to serve up
     * tab-separated streams
     * 
     * @param url The url defining the input stream
     */
    public DataReaderTsv(URL url) {
        super(url);
    }
    
    /**
     * Opens the stream defined by the url used in the constructor.
     * @throws IOException 
     */
    @Override
    public void open() throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        
        // Test experience has shown that sometimes this method returns before the
        // stream is ready to be read, which causes HTTP response code 503
        // (server not ready).
        // Make sure we can read from the stream first, including a sensible timeout (10 seconds max).
        for (int i = 0; i < 20; i++) {
            if ( ! bufferedReader.ready())
                TestUtils.sleep(500);           // sleep for 500 ms.
            else
                return;
        }
    }
    
    /**
     * Closes the stream defined by the url used in the constructor.
     * 
     * @throws IOException 
     */
    @Override
    public void close() throws IOException {
        if (bufferedReader != null) {
            bufferedReader.close();
            bufferedReader = null;
        }
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
        String sLine = bufferedReader.readLine();
        if (sLine == null)
            return null;
        
        return new ArrayList(Arrays.asList(sLine.split("\t")));
    }
    
    /**
     * Returns the data type
     * @return the data type
     */
    @Override
    public DataType getType() {
        return DataType.TSV;
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