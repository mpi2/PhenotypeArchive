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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'maGrid' HTML table for anatomy.
 */
public class SearchAnatomyTable extends SearchFacetTable {
    
    public static final int COL_INDEX_ANATOMY_TERM     = 0;
    public static final int COL_INDEX_ANATOMY_ID       = 1;
    public static final int COL_INDEX_ANATOMY_SYNONYMS = 2;
    
    private final List<AnatomyRow> bodyRows = new ArrayList();
    
    /**
     * Creates a new <code>SearchAnatomyTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchAnatomyTable(WebDriver driver, int timeoutInSeconds) {
        super(driver, "//table[@id='maGrid']", timeoutInSeconds);
        
        parseBodyRows();
    }
    
    /**
     * Validates download data against this <code>SearchAnatomyTable</code>
     * instance.
     * 
     * @param downloadData The download data used for comparison
     * @return validation status
     */
    @Override
    public PageStatus validateDownload(String[][] downloadData) {
        PageStatus status = new PageStatus();
        HashMap<String, String[]> downloadHash = new HashMap();
        
        if ((bodyRows.isEmpty()) || (downloadData.length == 0))
            return status;
        
        // This validation gets called with paged data (e.g. only the rows showing in the displayed page)
        // and with all data (the data for all of the pages). As such, the only effective way to validate
        // it is to stuff the download data elements into a hash, then loop through the pageData rows
        // querying the downloadData hash for each value (then removing that value from the hash to handle duplicates).
        for (int i = 1; i < downloadData.length; i++) {     
            // Copy all but the pageHeading into the hash.// Copy all but the pageHeading into the hash.
            String[] row = downloadData[i];
            downloadHash.put(row[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM], row);
        }
        
        for (AnatomyRow pageRow : bodyRows) {
            String[] downloadRow = downloadHash.get(pageRow.anatomyTerm);
            if (downloadRow == null) {
                status.addError("ANATOMY MISMATCH: page value anatomyName = '" + pageRow.anatomyTerm + "' was not found in the download file.");
                continue;
            }
            downloadHash.remove(pageRow.anatomyTerm);
            
            // Validate the pageHeading.
            String[] expectedHeadingList = {
                "Mouse adult gross anatomy term"
              , "Mouse adult gross anatomy id"
              , "Mouse adult gross anatomy synonym"
            };
            validateDownloadHeading(status, pageRow.anatomyTerm, expectedHeadingList, downloadData[0]);
            
            // Verify the components.
            
            // anatomyId.
            if ( ! pageRow.anatomyId.equals(downloadRow[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_ID]))
                status.addError("ANATOMY MISMATCH: page value anatomyId = '" + pageRow.anatomyId + "' doesn't match download value '" + downloadRow[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_ID] + "'.");
            
            // anatomyTerm.
            if ( ! pageRow.anatomyTerm.equals(downloadRow[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM]))
                status.addError("ANATOMY MISMATCH: page value anatomyTerm = '" + pageRow.anatomyTerm + "' doesn't match download value '" + downloadRow[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM] + "'.");
            
            // synonyms collection.
            HashMap<String, String> downloadSynonymHash = new HashMap();
            String rawSynonymString = downloadRow[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_SYNONYMS];
            if ((rawSynonymString != null) && ( ! rawSynonymString.isEmpty())) {
                String[] downloadSynonyms = rawSynonymString.split(",");
                for (String downloadSynonym : downloadSynonyms) {
                    downloadSynonymHash.put(downloadSynonym.trim(), downloadSynonym.trim());
                }
            }
            for (String pageSynonym : pageRow.synonyms) {
                String downloadSynonym = downloadSynonymHash.get(pageSynonym);
                if (downloadSynonym == null) {
                    status.addError("ANATOMY MISMATCH: page value synonym = '" + pageSynonym + "' was not found in the download file.");
                }
                downloadSynonymHash.remove(downloadSynonym);
            }
            
        }

        return status;
    }
    
    
    // PRIVATE METHODS
    
    
    private void parseBodyRows() {
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            for (WebElement bodyRowElements : bodyRowElementsList) {
                AnatomyRow anatomyRow = new AnatomyRow();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                WebElement element = bodyRowElementList.get(0).findElement(By.cssSelector("a"));
                String href = element.getAttribute("href");
                int pos = href.lastIndexOf("/");
                anatomyRow.anatomyId = href.substring(pos + 1); 
                anatomyRow.anatomyTerm = element.getText();
                // Synonyms are optional.
                List<WebElement> synonymElements = element.findElements(By.cssSelector("div.maCol > div.subinfo"));
                if ( ! synonymElements.isEmpty()) {
                    String[] synonyms = synonymElements.get(0).getText().replace("&nbsp;", "").split(",");
                    Collections.addAll(anatomyRow.synonyms, synonyms);
                }
                
                bodyRows.add(anatomyRow);
            }
        }
    }
    
    
    // PRIVATE CLASSES
    
    
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
            
            // We're only interested in the COL_INDEX_ANATOMY_TERM column.
            String op1 = ((String[])o1)[COL_INDEX_ANATOMY_TERM];
            String op2 = ((String[])o2)[COL_INDEX_ANATOMY_TERM];
            
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
            
            // We're only interested in the DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM column.
            String op1 = ((String[])o1)[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM];
            String op2 = ((String[])o2)[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM];
            
            if ((op1 == null) && (op2 == null))
                return 0;
            if (op1 == null)
                return -1;
            if (op2 == null)
                return 1;
            
            return op1.compareTo(op2);
        }
    }    
    
    private class AnatomyRow {
        private String anatomyId = "";
        private String anatomyTerm = "";
        private List<String> synonyms = new ArrayList();
        
        
        // anatomyId, anatomyTerm, anatomySynonyms
        public AnatomyRow() { }
        
        public String getAnatomyId() {
            return anatomyId;
        }

        public String getAnatomyTerm() {
            return anatomyTerm;
        }

        public List<String> getSynonyms() {
            return synonyms;
        }
    }

}