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

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'diseaseGrid' HTML table for diseases.
 */
public class SearchDiseaseTable extends SearchFacetTable {
    
    public static final int COL_INDEX_DISEASE_ID      = 0;
    public static final int COL_INDEX_DISEASE_NAME    = 1;
    public static final int COL_INDEX_SOURCE          = 2;
    public static final int COL_INDEX_CURATED_HUMAN   = 3;
    public static final int COL_INDEX_CURATED_MICE    = 4;
    public static final int COL_INDEX_CANDIDATE_IMPC  = 5;
    public static final int COL_INDEX_CANDIDATE_MGI   = 6;
    
    private final List<DiseaseRow> bodyRows = new ArrayList();
    
    /**
     * Creates a new <code>SearchDiseaseTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchDiseaseTable(WebDriver driver, int timeoutInSeconds) {
        super(driver, "//table[@id='diseaseGrid']", timeoutInSeconds);
        
        parseBodyRows();
    }
    
    /**
     * Validates download data against this <code>SearchDiseaseTable</code>
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
            // Copy all but the pageHeading into the hash.
            String[] row = downloadData[i];
            downloadHash.put(row[DownloadSearchMapDiseases.COL_INDEX_DISEASE_NAME], row);
        }
        
        for (DiseaseRow pageRow : bodyRows) {
            String[] downloadRow = downloadHash.get(pageRow.diseaseName);
            if (downloadRow == null) {
                status.addError("DISEASE MISMATCH: page value diseaseName = '" + pageRow.diseaseName + "' was not found in the download file.");
                continue;
            }
            downloadHash.remove(pageRow.diseaseName);                           // Remove the pageRow from the download hash.
            
            // Validate the pageHeading.
            String[] expectedHeadingList = {
                "Disease id"
              , "Disease name"
              , "Source"
              , "Curated genes in human"
              , "Curated genes in mouse (MGI)"
              , "Candidate genes by phenotype (IMPC)"
              , "Candidate genes by phenotype (MGI)"
            };
            validateDownloadHeading(status, pageRow.diseaseName, expectedHeadingList, downloadData[0]);
            
            // Verify the components.
            
            if ( ! pageRow.diseaseId.equals(downloadRow[DownloadSearchMapDiseases.COL_INDEX_DISEASE_ID]))
                status.addError("DISEASE MISMATCH: page value diseaseId = '" + pageRow.diseaseId + "' doesn't match download value " + downloadRow[DownloadSearchMapDiseases.COL_INDEX_DISEASE_ID]);
            
            if ( ! pageRow.source.equals(downloadRow[DownloadSearchMapDiseases.COL_INDEX_SOURCE]))
                status.addError("DISEASE MISMATCH: page value source = '" + pageRow.source + "' doesn't match download value " + downloadRow[DownloadSearchMapDiseases.COL_INDEX_SOURCE]);
            
            if ( ! pageRow.hasCuratedGenesInHuman.equals(downloadRow[DownloadSearchMapDiseases.COL_INDEX_CURATED_HUMAN]))
                status.addError("DISEASE MISMATCH: page value hasCuratedGenesInHuman = '" + pageRow.hasCuratedGenesInHuman + "' doesn't match download value " + downloadRow[DownloadSearchMapDiseases.COL_INDEX_CURATED_HUMAN]);
            
            if ( ! pageRow.hasCuratedGenesInMice.equals(downloadRow[DownloadSearchMapDiseases.COL_INDEX_CURATED_MICE]))
                status.addError("DISEASE MISMATCH: page value hasCuratedGenesInMice = '" + pageRow.hasCuratedGenesInMice + "' doesn't match download value " + downloadRow[DownloadSearchMapDiseases.COL_INDEX_CURATED_MICE]);
            
            if ( ! pageRow.hasCandidateGenesByPhenotypeIMPC.equals(downloadRow[DownloadSearchMapDiseases.COL_INDEX_CANDIDATE_IMPC]))
                status.addError("DISEASE MISMATCH: page value hasCandidateGenesByPhenotypeIMPC = '" + pageRow.hasCandidateGenesByPhenotypeIMPC + "' doesn't match download value " + downloadRow[DownloadSearchMapDiseases.COL_INDEX_CANDIDATE_IMPC]);
            
            if ( ! pageRow.hasCandidateGenesByPhenotypeMGI.equals(downloadRow[DownloadSearchMapDiseases.COL_INDEX_CANDIDATE_MGI]))
                status.addError("DISEASE MISMATCH: page value hasCandidateGenesByPhenotypeMGI = '" + pageRow.hasCandidateGenesByPhenotypeMGI + "' doesn't match download value " + downloadRow[DownloadSearchMapDiseases.COL_INDEX_CANDIDATE_MGI]);
        }

        return status;
    }
    
    
    // PRIVATE METHODS
    
    
    private void parseBodyRows() {
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            for (WebElement bodyRowElements : bodyRowElementsList) {                                    // diseaseId, diseaseName, source, curatedHuman, curatedMice, candidateIMPC, candidateMGI
                DiseaseRow diseaseRow = new DiseaseRow();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                WebElement element = bodyRowElementList.get(0).findElement(By.cssSelector("a"));        // Get 'Disease' element.
                String href = element.getAttribute("href");
                int pos = href.lastIndexOf("/");
                diseaseRow.diseaseId = href.substring(pos + 1);                                         // Add diseaseId   to row element 0 from 'Disease' element.
                diseaseRow.diseaseName = element.getText();                                             // Add diseaseName to row element 1 from 'Disease' element.
                diseaseRow.source = bodyRowElementList.get(1).getText();                                // Add source      to row element 2 from 'Source' element.
                
                element = bodyRowElementList.get(2);                                                                            // Get 'Curated Genes' element.
                List<WebElement> elementList = element.findElements(By.cssSelector("span"));
                if (elementList.isEmpty()) {                                                                                    // There are no curated genes...
                    diseaseRow.hasCuratedGenesInHuman = "false";                                                                //    No human curated genes...
                    diseaseRow.hasCuratedGenesInMice = "false";                                                                 //    No mice curated genes...
                } else {
                    if (elementList.size() == 2) {
                        diseaseRow.hasCuratedGenesInHuman = "true";                                                             // Human curated genes found.
                        diseaseRow.hasCuratedGenesInMice = "true";                                                              // Mice curated genes found.
                    } else {
                        diseaseRow.hasCuratedGenesInHuman = (elementList.get(0).getText().equals("human") ? "true" : "false");  // human curated genes [only] found.
                        diseaseRow.hasCuratedGenesInMice = (elementList.get(0).getText().equals("mice")  ? "true" : "false");   // mice curated genes [only] found.
                    }
                }
                
                element = bodyRowElementList.get(3);                                                                                        // Get 'Candidate Genes' element.
                elementList = element.findElements(By.cssSelector("span"));
                if (elementList.isEmpty()) {                                                                                                // There are no candidate genes...
                    diseaseRow.hasCandidateGenesByPhenotypeMGI = "false";                                                                   //    No MGI candidate genes...
                    diseaseRow.hasCandidateGenesByPhenotypeIMPC = "false";                                                                  //    No IMPC candidate genes...
                } else {
                    if (elementList.size() == 2) {
                        diseaseRow.hasCandidateGenesByPhenotypeMGI = "true";                                                                // MGI candidate genes found.
                        diseaseRow.hasCandidateGenesByPhenotypeIMPC = "true";                                                               // IMPC candidate genes found.
                    } else {
                        diseaseRow.hasCandidateGenesByPhenotypeMGI = (elementList.get(0).getText().equals("MGI") ? "true" : "false");      // MGI candidate genes [only] found.
                        diseaseRow.hasCandidateGenesByPhenotypeIMPC = (elementList.get(0).getText().equals("IMPC")  ? "true" : "false");     // IMPC candidate genes [only] found.
                    }
                }
                
                bodyRows.add(diseaseRow);
            }
        }
    }
    
    
    // PRIVATE CLASSES
    
    
    private class DiseaseRow {
        private String diseaseId = "";
        private String diseaseName = "";
        private String source = "";
        private String hasCuratedGenesInHuman = "";
        private String hasCuratedGenesInMice = "";
        private String hasCandidateGenesByPhenotypeIMPC = "";
        private String hasCandidateGenesByPhenotypeMGI = "";

        public String getDiseaseId() {
            return diseaseId;
        }

        public String getDiseaseName() {
            return diseaseName;
        }

        public String getSource() {
            return source;
        }

        public String hasCuratedGenesInHuman() {
            return hasCuratedGenesInHuman;
        }

        public String hasCuratedGenesInMice() {
            return hasCuratedGenesInMice;
        }

        public String hasCandidateGenesByPhenotypeIMPC() {
            return hasCandidateGenesByPhenotypeIMPC;
        }

        public String hasCandidateGenesByPhenotypeMGI() {
            return hasCandidateGenesByPhenotypeMGI;
        }
        

    }

}