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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'mpGrid' HTML table for phenotypes.
 */
public class SearchPhenotypeTable extends SearchFacetTable {
    
    public static final int COL_INDEX_PHENOTYPE  = 0;
    public static final int COL_INDEX_DEFINITION = 1;
    
    private final List<PhenotypeRow> bodyRows = new ArrayList();
    
    /**
     * Creates a new <code>SearchPhenotypeTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchPhenotypeTable(WebDriver driver, int timeoutInSeconds) {
        super(driver, "//table[@id='mpGrid']", timeoutInSeconds);
        
        parseBodyRows();
    }
    
    /**
     * Validates download data against this <code>SearchPhenotypeTable</code>
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
            
        // Validate the pageHeading.
        String[] expectedHeadingList = {
            "Mammalian phenotype term"
          , "Mammalian phenotype id"
          , "Mammalian phenotype id link"
          , "Mammalian phenotype definition"
          , "Mammalian phenotype synonym"
          , "Mammalian phenotype top level term"
        };
        validateDownloadHeading("PHENOTYPE", status, expectedHeadingList, downloadData[0]);
        
        // This validation gets called with paged data (e.g. only the rows showing in the displayed page)
        // and with all data (the data for all of the pages). As such, the only effective way to validate
        // it is to stuff the download data elements into a hash, then loop through the pageData rows
        // querying the downloadData hash for each value (then removing that value from the hash to handle duplicates).
        for (int i = 1; i < downloadData.length; i++) {
            // Copy all but the pageHeading into the hash.
            String[] row = downloadData[i];
            downloadHash.put(row[DownloadSearchMapPhenotypes.COL_INDEX_PHENOTYPE_TERM], row);
        }
        
        for (PhenotypeRow pageRow : bodyRows) {
            String[] downloadRow = downloadHash.get(pageRow.phenotypeTerm);
            if (downloadRow == null) {
                status.addError("PHENOTYPE MISMATCH: page value phenotypeTerm = '" + pageRow.phenotypeTerm + "' was not found in the download file.");
                continue;
            }
            downloadHash.remove(pageRow.phenotypeTerm);                         // Remove the pageRow from the download hash.
            
            // Verify the components.
            
            // phenotypeId.
            if ( ! pageRow.phenotypeId.equals(downloadRow[DownloadSearchMapPhenotypes.COL_INDEX_PHENOTYPE_ID]))
                status.addError("PHENOTYPE MISMATCH: Phenotype id '" + pageRow.phenotypeTerm + "' page value phenotypeId = '" + pageRow.phenotypeId + "' doesn't match download value '" + downloadRow[DownloadSearchMapPhenotypes.COL_INDEX_PHENOTYPE_ID] + "'.");

            // phenotypeLink.
            if ( ! pageRow.phenotypeIdLink.equals(downloadRow[DownloadSearchMapPhenotypes.COL_INDEX_PHENOTYPE_ID_LINK]))
                status.addError("PHENOTYPE MISMATCH: Phenotype id '" + pageRow.phenotypeTerm + "' page value phenotypeLink = '" + pageRow.phenotypeIdLink + "' doesn't match download value '" + downloadRow[DownloadSearchMapPhenotypes.COL_INDEX_PHENOTYPE_ID] + "'.");

            // definition.
            String downloadValue = downloadRow[DownloadSearchMapPhenotypes.COL_INDEX_DEFINITION].trim();
            if (pageRow.definition.isEmpty()) {
                if ( ! downloadValue.equals(NO_INFO_AVAILABLE)) {
                    status.addError("PHENOTYPE MISMATCH: phenotypeTerm '" + pageRow.phenotypeTerm + "' page value definition is empty. Expected download cell to contain '" + NO_INFO_AVAILABLE + "' but found '" + downloadValue + "'.");
                }
            } else {
                if ( ! pageRow.definition.equals(downloadValue)) {
                    status.addError("PHENOTYPE MISMATCH: phenotypeTerm '" + pageRow.phenotypeTerm + "' page value = '" + pageRow.definition + "' doesn't match download value '" + downloadValue + "'.");
                }
            }

            // synonyms collection.
            HashMap<String, String> downloadSynonymHash = new HashMap();
            String rawSynonymString = downloadRow[DownloadSearchMapPhenotypes.COL_INDEX_SYNONYM];
            if ((rawSynonymString != null) && ( ! rawSynonymString.isEmpty())) {
                String[] downloadSynonyms = rawSynonymString.split("\\|");
                for (String downloadSynonym : downloadSynonyms) {
                    downloadSynonymHash.put(downloadSynonym.trim(), downloadSynonym.trim());
                }
            }
            // If page synonyms is empty, validate that download is empty too.
            if (pageRow.synonyms.isEmpty()) {
                downloadValue = downloadSynonymHash.get(NO_INFO_AVAILABLE);
                if ((downloadValue == null) || ( ! downloadSynonymHash.get(NO_INFO_AVAILABLE).equals(NO_INFO_AVAILABLE))) {
                    status.addError("PHENOTYPE MISMATCH: phenotypeTerm '" + pageRow.phenotypeTerm + "' page has no synonyms but download has " + downloadSynonymHash.size() + ".");
                }
            }
            for (String pageSynonym : pageRow.synonyms) {
                String downloadSynonym = downloadSynonymHash.get(pageSynonym);
                if (downloadSynonym == null) {
                    status.addError("PHENOTYPE MISMATCH: phenotypeTerm '" + pageRow.phenotypeTerm + "' page value synonym = '" + pageSynonym + "' was not found in the download file.");
                }
                downloadSynonymHash.remove(downloadSynonym);
            }
        }

        return status;
    }
    
    
    // PRIVATE METHODS
    
    
    // phenotypeTerm, phenotypeId, phenotypeLink, synonyms(List)
    private void parseBodyRows() {
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            int index = 0;
            for (WebElement bodyRowElements : bodyRowElementsList) {
                PhenotypeRow phenotypeRow = new PhenotypeRow();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                // Sometimes there is only an anchor element (and no mpCol) inside the td.....
                List<WebElement> titleDivElements = bodyRowElementList.get(0).findElements(By.cssSelector("div.mpCol div.title a"));
                WebElement titleDivElement = (titleDivElements.isEmpty() ? bodyRowElementList.get(0).findElement(By.cssSelector("a")) : titleDivElements.get(0));
                phenotypeRow.phenotypeIdLink = titleDivElement.getAttribute("href");                                    // phenotypeIdLink.
                int pos = phenotypeRow.phenotypeIdLink.lastIndexOf("/");
                phenotypeRow.phenotypeId = phenotypeRow.phenotypeIdLink.substring(pos + 1).trim();                      // phenotypeId.
                phenotypeRow.phenotypeTerm = titleDivElement.getText().trim();                                          // phenotypeTerm.
                
                List<WebElement> mpColElements = bodyRowElementList.get(0).findElements(By.cssSelector("div.mpCol"));
                if ( ! mpColElements.isEmpty()) {
                    PhenotypeDetails phenotypeDetails = new PhenotypeDetails(mpColElements.get(0));
                    phenotypeRow.synonyms = phenotypeDetails.synonyms;                                                  // synonym list.
                }
                phenotypeRow.definition = bodyRowElementList.get(1).getText();                                          // definition.
                
//System.out.println("phenotypeRow[ " + index + " ]: " + phenotypeRow.toString());
                index++;
                bodyRows.add(phenotypeRow);
            }
        }
    }
    
    
    // PRIVATE CLASSES
    
    
    /**
     * This class encapsulates the code and data necessary to parse and extract
     * the search page phenotype details found when you hover the mouse over the
     * phenotype term. There should be a phenotype term and 0 or more synonyms.
     */
    private class PhenotypeDetails {
        private List<String> synonyms = new ArrayList();
        
        public PhenotypeDetails(WebElement mpColElement) {        
            
            // In order to see the contents of the span, we need to first bring
            // the term into view, then hover over it.
            Actions builder = new Actions(driver);

            try {
                TestUtils.scrollToTop(driver, mpColElement, -50);               // Scroll term into view.
                Actions hoverOverTerm = builder.moveToElement(mpColElement);
                hoverOverTerm.perform();                                        // Hover over the term.
                
                List<WebElement> synonymElements = mpColElement.findElements(By.cssSelector("div.subinfo ul.synonym li"));
                if ( ! synonymElements.isEmpty()) {
                    for (WebElement synonymElement : synonymElements) {
                        synonyms.add(synonymElement.getText());
                    }
                } else {
                    // Egad. synonyms can have colons as text.
                    synonyms.add(mpColElement.findElement(By.cssSelector("div.subinfo")).getText().trim().replaceFirst("synonym: ", ""));
                }
            } catch (Exception e) {
                System.out.println("EXCEPTION: SearchPhenotypeTable.PhenotypeDetails.PhenotypeDetails() while waiting to hover. Error message: " + e.getLocalizedMessage());
            }
        }
    }
    
    private class PhenotypeRow {
        private String phenotypeTerm   = "";
        private String phenotypeId    = "";
        private String phenotypeIdLink = "";
        private String definition      = "";
        private List<String> synonyms   = new ArrayList();
        
        @Override
        public String toString() {
            return "phenotypeTerm: '"    + phenotypeTerm
                 + "'  phenotypeId: '"   + phenotypeId
                 + "'  phenotypeLink: '" + phenotypeIdLink
                 + "'  definition: '"    + definition
                 + "'  synonyms: '"      + toStringSynonyms() + "'";
        }
        
        public String toStringSynonyms() {
            String retVal = "";
            
            for (int i = 0; i < synonyms.size(); i++) {
                if (i > 0)
                    retVal += ", ";
                retVal += synonyms.get(i);
            }
            
            return retVal;
        }
        
    }

}