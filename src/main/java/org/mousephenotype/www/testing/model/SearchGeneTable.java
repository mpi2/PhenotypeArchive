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
 * components of a search page 'geneGrid' HTML table for genes.
 */
public class SearchGeneTable extends SearchFacetTable {
    
    public static final int COL_INDEX_GENE_ID           = 0;
    public static final int COL_INDEX_GENE_SYMBOL       = 1;
    public static final int COL_INDEX_HUMAN_ORTHOLOG    = 2;
    public static final int COL_INDEX_GENE_NAME         = 3;
    public static final int COL_INDEX_GENE_SYNONYM      = 4;
    public static final int COL_INDEX_PRODUCTION_STATUS = 5;
    public static final int COL_INDEX_PHENOTYPE_STATUS  = 6;
    
    private final List<GeneRow> bodyRows = new ArrayList();
    
    /**
     * Creates a new <code>SearchGeneTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchGeneTable(WebDriver driver, int timeoutInSeconds) {
        super(driver, "//table[@id='geneGrid']", timeoutInSeconds);
        
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
            downloadHash.put(row[DownloadSearchMapGenes.COL_INDEX_GENE_SYMBOL], row);
        }
        
        for (GeneRow pageRow : bodyRows) {
            String[] downloadRow = downloadHash.get(pageRow.geneSymbol);
            if (downloadRow == null) {
                status.addError("GENE MISMATCH: page value geneSymbol = '" + pageRow.geneSymbol + "' was not found in the download file.");
                continue;
            }
            downloadHash.remove(pageRow.geneSymbol);                            // Remove the pageRow from the download hash.
            
            // Validate the pageHeading.
            String[] expectedHeadingList = {
                "Gene symbol"
              , "Human ortholog"
              , "Gene Id"
              , "Gene name"
              , "Gene synonym"
              , "Production status"
              , "Phenotype status"
              , "Phenotype status link"
            };
            validateDownloadHeading(status, pageRow.geneSymbol, expectedHeadingList, downloadData[0]);
            
            // Verify the components.
            
            // geneId.
            if ( ! pageRow.geneId.equals(downloadRow[DownloadSearchMapGenes.COL_INDEX_GENE_ID]))
                status.addError("GENE MISMATCH: Gene symbol " + pageRow.geneId + " page value geneId = '" + pageRow.geneId + "' doesn't match download value '" + downloadRow[DownloadSearchMapGenes.COL_INDEX_GENE_ID] + "'.");

            // geneName.
            String downloadValue = downloadRow[DownloadSearchMapGenes.COL_INDEX_GENE_NAME].trim();
            if (pageRow.geneName.isEmpty()) {
                if ( ! downloadValue.equals(NO_INFO_AVAILABLE)) {
                    status.addError("GENE MISMATCH: Gene symbol " + pageRow.geneSymbol + " page value geneName is empty. Expected download cell to contain '" + NO_INFO_AVAILABLE + "' but found '" + downloadValue + "'.");
                }
            } else {
                if ( ! pageRow.geneName.equals(downloadValue)) {
                    status.addError("GENE MISMATCH: Gene symbol " + pageRow.geneSymbol + " page value geneName = '" + pageRow.geneName + "' doesn't match download value '" + downloadValue + "'.");
                }
            }
            
            // humanOrtholog.
            downloadValue = downloadRow[DownloadSearchMapGenes.COL_INDEX_HUMAN_ORTHOLOG].trim();
            if (pageRow.humanOrtholog.isEmpty()) {
                if ( ! downloadValue.equals(NO_INFO_AVAILABLE)) {
                    status.addError("GENE MISMATCH: Gene symbol " + pageRow.geneSymbol + " page value humanOrtholog is empty. Expected download cell to contain '" + NO_INFO_AVAILABLE + "' but found '" + downloadValue + "'.");
                }
            } else {
                if ( ! pageRow.humanOrtholog.equals(downloadValue)) {
                    status.addError("GENE MISMATCH: Gene symbol " + pageRow.geneSymbol + " page value humanOrtholog = '" + pageRow.humanOrtholog + "' doesn't match download value '" + downloadValue + "'.");
                }
            }
            
            // synonyms collection.
            HashMap<String, String> downloadSynonymHash = new HashMap();
            String rawSynonymString = downloadRow[DownloadSearchMapGenes.COL_INDEX_SYNONYM];
            if ((rawSynonymString != null) && ( ! rawSynonymString.isEmpty())) {
                String[] downloadSynonyms = rawSynonymString.split("\\|");
                for (String downloadSynonym : downloadSynonyms) {
                    downloadSynonymHash.put(downloadSynonym.trim(), downloadSynonym.trim());
                }
            }
            for (String pageSynonym : pageRow.synonyms) {
                String downloadSynonym = downloadSynonymHash.get(pageSynonym);
                if (downloadSynonym == null) {
                    status.addError("GENE MISMATCH: Gene symbol " + pageRow.geneSymbol + " page value synonym = '" + pageSynonym + "' was not found in the download file.");
                }
                downloadSynonymHash.remove(downloadSynonym);
            }
            
            // productionStatus collection.
            downloadValue = downloadRow[DownloadSearchMapGenes.COL_INDEX_PRODUCTION_STATUS].trim().toLowerCase();
            for (PhenotypeArchiveStatus pageProductionStatus : pageRow.productionStatus) {
                // Verify that the download productionStatus string contains the pageProductionStatus name.
                if ( ! downloadValue.contains(pageProductionStatus.mpName.toLowerCase())) {
                    status.addError("GENE MISMATCH: Gene symbol " + pageRow.geneSymbol + " download value productionStatus = '" + downloadValue + "' doesn't contain page value '" +  pageProductionStatus.mpName + "'.");
                }
            }
            // phenotypeStatus.     Verify that the download phenotypeStatus string contains the pagePhenotypStatus name.
            downloadValue = downloadRow[DownloadSearchMapGenes.COL_INDEX_PHENOTYPE_STATUS].trim().toLowerCase();
            if ( ! downloadValue.contains(pageRow.phenotypeStatus.mpName.toLowerCase())) {
                status.addError("GENE MISMATCH: Gene symbol " + pageRow.geneSymbol + " download value phenotypeStatus = '" + downloadValue + "' doesn't contain page value '" +  pageRow.phenotypeStatus.mpName + "'.");
            }
        }

        return status;
    }
    
    
    // PRIVATE METHODS
    
    
    // geneId, geneSymbol, humanOrtholog, geneName, synonyms(List), productionStatus(List), phenotypeStatus
    private void parseBodyRows() {
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            int index = 0;
            for (WebElement bodyRowElements : bodyRowElementsList) {
                GeneRow geneRow = new GeneRow();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                WebElement titleDivElement = bodyRowElementList.get(0).findElement(By.cssSelector("div.geneCol div.title a"));
                String href = titleDivElement.getAttribute("href");
                int pos = href.lastIndexOf("/");
                geneRow.geneId = href.substring(pos + 1).trim();                                                    // geneId.
                geneRow.geneSymbol = titleDivElement.findElement(By.cssSelector("span.gSymbol")).getText().trim();  // geneSymbol.
                
                WebElement geneColElement = bodyRowElementList.get(0).findElement(By.cssSelector("div.geneCol"));
                GeneDetails geneDetails = new GeneDetails(geneColElement);
                geneRow.geneName = geneDetails.name;                                                                // geneName.
                geneRow.humanOrtholog = geneDetails.humanOrtholog;                                                  // humanOrtholog.
                geneRow.synonyms = geneDetails.synonyms;                                                            // synonym list.
                
                List<WebElement> anchorElements = bodyRowElementList.get(1).findElements(By.cssSelector("a"));
                for (WebElement anchorElement : anchorElements) {
                    geneRow.productionStatus.add(new PhenotypeArchiveStatus(anchorElement));                          // productionStatus list.
                }
                
                anchorElements = bodyRowElementList.get(2).findElements(By.cssSelector("a"));
                if ( ! anchorElements.isEmpty()) {
                    geneRow.phenotypeStatus = new PhenotypeArchiveStatus(anchorElements.get(0));
                    geneRow.phenotypeStatusLink = 
                            bodyRowElementList.get(2).findElement(By.cssSelector("a")).getAttribute("href");        // phenotypeStatusLink.
                }
                
//System.out.println("geneRow[ " + index + " ]: " + geneRow.toString());
                index++;
                bodyRows.add(geneRow);
            }
        }
    }
    
    
    // PRIVATE CLASSES
    
    
    /**
     * This class encapsulates the code and data necessary to parse and extract the
     * search page gene details found when you hover the mouse over the gene symbol.
     * There may be any of: [gene] name, human ortholog, and 0 or more synonyms.
     */
    private class GeneDetails {
        private String name = "";
        private String humanOrtholog = "";
        private List<String> synonyms = new ArrayList();
        
        public GeneDetails(WebElement geneColElement) {        
            
            // In order to see the contents of the span, we need to first bring
            // the gene symbol into view, then hover over it.
            Actions builder = new Actions(driver);

            try {
                TestUtils.scrollToTop(driver, geneColElement, -50);             // Scroll gene symbol into view.
                Actions hoverOverGene = builder.moveToElement(geneColElement);
                hoverOverGene.perform();                                        // Hover over the gene symbol.
                
                List<WebElement> synonymElements = geneColElement.findElements(By.cssSelector("div.subinfo ul li"));
                for (WebElement synonymElement : synonymElements) {
                    synonyms.add(synonymElement.getText());
                }
                
                String subinfoDivText = geneColElement.getText();
                String[] subinfoDivLines = subinfoDivText.split("\n");
                for (String subinfoDivLine : subinfoDivLines) {
                    String[] textParts = subinfoDivLine.split(":");
                    switch (textParts[0].trim().toLowerCase()) {
                        case "name":
                            this.name = textParts[1].trim();                    // geneName.
                            break;
                            
                        case "human ortholog":
                            this.humanOrtholog = textParts[1].trim();           // humanOrtholog.
                            break;
                            
                        default:
                            break;
                    }
                 }
            } catch (Exception e) {
                System.out.println("EXCEPTION: SearchGeneTable.GeneDetails.GeneDetails() while waiting to hover. Error message: " + e.getLocalizedMessage());
            }
        }
    }
    
    private class GeneRow {
        private String geneId = "";
        private String geneSymbol = "";
        private String humanOrtholog = "";
        private String geneName = "";
        private List<String> synonyms = new ArrayList();
        private List<PhenotypeArchiveStatus> productionStatus = new ArrayList();
        private PhenotypeArchiveStatus phenotypeStatus;
        private String phenotypeStatusLink = "";
        
        @Override
        public String toString() {
            return "geneId: " + geneId
                 + "  geneSymbol: " + geneSymbol
                 + "  humanOrtholog: " + humanOrtholog
                 + "  geneName: " + geneName
                 + "  synonyms: " + toStringSynonyms()
                 + "  productionStatus: " + toStringProductionStatus()
                 + "  phenotypeStatus: " + phenotypeStatus.mpName + "[" + phenotypeStatus.mpClass + "]"
                 + "  phenotypeStatusLink: " + phenotypeStatusLink;
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
        
        public String toStringProductionStatus() {
            String retVal = "";
            
            for (int i = 0; i < productionStatus.size(); i++) {
                if (i > 0)
                    retVal += ", ";
                retVal += productionStatus.get(i).mpName + "[" + productionStatus.get(i).mpClass + "]";
            }
            
            return retVal;
        }
    }
    
    /**
     * This enum is meant to emulate the css classes used for production and
     * phenotype statuses. They are lowercase to exactly reflect the css class
     * names.
     */
    public enum PhenotypeArchiveStatusClass {
        done,
        inprogress,
        none,
        qc
    }
    
    /**
     * This class is meant to encapsulate the code and data necessary to represent
     * mouse phenotype status and corresponding css class (which determines button/
     * label display color).
     */
    private class PhenotypeArchiveStatus {
        private String mpName;
        private PhenotypeArchiveStatusClass mpClass;
        
        /**
         * This constructor takes a <code>WebElement</code> instance pointing to
         * the status <b>a</b> anchor element, if there is one, parsing it and
         * returning a properly initialized object containing the status name
         * and css class.
         *
         * @param anchorElement The phenotype status <b>a</b> element.
         * <p>
         * NOTE: If this <b>a</b> element has no status, mpName and mpClass will be
         * initialized to null.
         */
        public PhenotypeArchiveStatus(WebElement anchorElement) {
            mpName = null;
            mpClass = null;
            
            try {
                if (anchorElement != null) {
                    mpName = anchorElement.findElement(By.cssSelector("span")).getText();
                    
                    String classList = anchorElement.getAttribute("class");
                    String[] classes = classList.split(" ");
                    mpClass = PhenotypeArchiveStatusClass.valueOf(anchorElement.getAttribute("class").split(" ")[1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}