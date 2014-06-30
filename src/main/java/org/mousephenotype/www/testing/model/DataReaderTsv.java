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

/**
 *
 * @author mrelac
 * 
 * This <code>DataReader</code> implementation handles tab-separated streams.
 */
public class DataReaderTsv implements DataReader {
    private final URL url;
    private BufferedReader bufferedReader = null;
    
    /**
     * Instantiates a new <code>DataReader</code> that knows how to serve up
     * tab-separated streams
     * 
     * @param url The url defining the input stream
     */
    public DataReaderTsv(URL url) {
        this.url = url;
    }
    
    /**
     * Opens the stream defined by the url used in the constructor.
     * @throws IOException 
     */
    @Override
    public void open() throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
    }
    
    /**
     * Closes the stream defined by the url used in the constructor.
     * 
     * @throws IOException 
     */
    @Override
    public void close() throws IOException {
        bufferedReader.close();
        bufferedReader = null;
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
    
    
    // GETTERS
    
    
    /**
     * Returns the url
     * @return the url
     */
    public URL getUrl() {
        return url;
    }
    
    
}
