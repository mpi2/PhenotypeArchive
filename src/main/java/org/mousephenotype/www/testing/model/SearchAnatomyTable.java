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

import java.util.Comparator;
import java.util.HashMap;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'maGrid' HTML table for anatomy.
 */
public class SearchAnatomyTable extends SearchFacetTable {
    
    public static final int COL_INDEX_ANATOMY = 0;
    
    /**
     * Creates a new <code>SearchAnatomyTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     */
    public SearchAnatomyTable(WebDriver driver) {
        super(driver, "//table[@id='maGrid']");
    }
    
    /**
     * Validates download data against this <code>SearchAnatomyTable</code>
     * instance.
     * 
     * @param data The download data used for comparison
     * @return validation status
     */
    @Override
    public PageStatus validateDownload(String[][] data) {
        PageStatus status = new PageStatus();
        String[][] pageData = getBodyRowsList();
        HashMap<String, String> downloadHash = new HashMap();
        
        if ((pageData.length == 0) || (data.length == 0))
            return status;
        
        // This validation gets called with paged data (e.g. only the rows showing in the displayed page)
        // and with all data (the data for all of the pages). As such, the only effective way to validate
        // it is to stuff the download data elements into a hash, then loop through the pageData rows
        // querying the downloadData hash for each value (then removing that value from the hash to handle duplicates).
        for (int i = 1; i < data.length; i++) {                             // Copy all but the heading into the hash.
            String value = data[i][DownloadSearchMapAnatomy.COL_INDEX_MA_TERM];
            downloadHash.put(value, value);
        }
        
        for (String[] row : pageData) {
            String pageValue = row[COL_INDEX_ANATOMY];
            if ( ! downloadHash.containsKey(pageValue)) {
                status.addError("ANATOMY MISMATCH: page value = '" + pageValue + "' was not found in the download file.");
                continue;
            }
            downloadHash.remove(pageValue);
        }

        return status;
    }
    
    // NOTE: We don't need these (as sorting the arrays does not solve the problem). However, I'm leaving these in
    //       because they are a good example of how to sort String[][] objects.
    // o1 and o2 are of type String[].
    private class PageComparator implements Comparator<String[]> {

        @Override
        public int compare(String[] o1, String[] o2) {
            if ((o1 == null) && (o2 == null))
                return 0;
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            
            // We're only interested in the COL_INDEX_ANATOMY column.
            String op1 = ((String[])o1)[COL_INDEX_ANATOMY];
            String op2 = ((String[])o2)[COL_INDEX_ANATOMY];
            
            if ((op1 == null) && (op2 == null))
                return 0;
            if (op1 == null)
                return -1;
            if (op2 == null)
                return 1;
            
            return op1.compareTo(op2);
        }
    }
    
    // o1 and o2 are of type String[].
    private class DownloadComparator implements Comparator<String[]> {
        
        @Override
        public int compare(String[] o1, String[] o2) {
            if ((o1 == null) && (o2 == null))
                return 0;
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            
            // We're only interested in the DownloadSearchMapAnatomy.COL_INDEX_MA_TERM column.
            String op1 = ((String[])o1)[DownloadSearchMapAnatomy.COL_INDEX_MA_TERM];
            String op2 = ((String[])o2)[DownloadSearchMapAnatomy.COL_INDEX_MA_TERM];
            
            if ((op1 == null) && (op2 == null))
                return 0;
            if (op1 == null)
                return -1;
            if (op2 == null)
                return 1;
            
            return op1.compareTo(op2);
        }
    }

}