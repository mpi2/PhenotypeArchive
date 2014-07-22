/*
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
import java.util.List;

/**
 *
 * @author mrelac
 * 
 * This interface defines a contract for implementing a data reader that can
 * read URL-originated streams. Since plain-text streams such as comma- and tab-
 * separated formats can be very different from Excel streams in their command-set,
 * this interface aims to describe a consistent, easy-to-use, and easy-to-maintain
 * and enhance approach.
 * 
 * New stream types can be easily implemented by simply implementing these
 * methods.
 * 
 */
public interface DataReader {
    public void open() throws IOException;
    public void close() throws IOException;
    public List<String> getLine() throws IOException;
    public DataType getType();
    public String[][] getData(int maxRows);
    public int lineCount();
    
    public enum DataType {
        TSV,
        XLS
    }
    
}
