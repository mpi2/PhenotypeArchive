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
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'geneGrid' HTML table for genes.
 */
public class SearchGeneTable extends SearchFacetTable {
    
    public static final int COL_INDEX_GENE_ID               = 0;
    public static final int COL_INDEX_GENE_SYMBOL           = 1;
    public static final int COL_INDEX_HUMAN_ORTHOLOG        = 2;
    public static final int COL_INDEX_GENE_NAME             = 3;
    public static final int COL_INDEX_GENE_SYNONYM          = 4;
    public static final int COL_INDEX_PRODUCTION_STATUS     = 5;
    public static final int COL_INDEX_PRODUCTION_HOVER_TEXT = 6;
    public static final int COL_INDEX_PHENOTYPE_STATUS      = 7;
    public static final int COL_INDEX_LAST = COL_INDEX_PHENOTYPE_STATUS;        // Should always point to the last (highest-numbered) index.
    
    private final List<GeneRow> bodyRows = new ArrayList();
    private final GridMap pageData;

    static {
        byHash.put(SearchFacetTable.BY_TABLE, By.xpath("//table[@id='geneGrid']"));
        byHash.put(SearchFacetTable.BY_TABLE_TR, By.xpath("//table[@id='geneGrid']/tbody/tr"));
        byHash.put(SearchFacetTable.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='geneGrid_length']"));
    }
    
    /**
     * Creates a new <code>SearchGeneTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchGeneTable(WebDriver driver, int timeoutInSeconds) {
        super(driver, timeoutInSeconds);
        
        pageData = load();
    }
    
    /**
     * Validates download data against this <code>SearchDiseaseTable</code>
     * instance.
     * 
     * @param downloadDataArray The download data used for comparison
     * @return validation status
     */
    @Override
    public PageStatus validateDownload(String[][] downloadDataArray) {
        final Integer[] pageColumns = {
              COL_INDEX_GENE_ID
            , COL_INDEX_GENE_NAME
            , COL_INDEX_GENE_SYMBOL
            , COL_INDEX_HUMAN_ORTHOLOG
        };
        final Integer[] downloadColumns = {
              DownloadSearchMapGenes.COL_INDEX_GENE_ID
            , DownloadSearchMapGenes.COL_INDEX_GENE_NAME
            , DownloadSearchMapGenes.COL_INDEX_GENE_SYMBOL
            , DownloadSearchMapGenes.COL_INDEX_HUMAN_ORTHOLOG
        };
        
        return validateDownloadInternal(pageData, pageColumns, downloadDataArray, downloadColumns, driver.getCurrentUrl());
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Pulls all rows of data and column access variables from the search page's
     * 'geneGrid' HTML table.
     *
     * @return <code>numRows</code> rows of data and column access variables
     * from the search page's 'geneGrid' HTML table.
     */
    private GridMap load() {
        return load(null);
    }

    /**
     * Pulls <code>numRows</code> rows of search page gene facet data and column
     * access variables from the search page's 'geneGrid' HTML table.
     *
     * @param numRows the number of <code>GridMap</code> table rows to return,
     * including the heading row. To specify all rows, set <code>numRows</code>
     * to null.
     * @return <code>numRows</code> rows of search page gene facet data and
     * column access variables from the search page's 'geneGrid' HTML table.
     */
    private GridMap load(Integer numRows) {
        if (numRows == null)
            numRows = computeTableRowCount();
        
        String[][] pageArray;
        
        // Wait for page.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#geneGrid")));
        int numCols = COL_INDEX_LAST + 1;
        
        pageArray = new String[numRows][numCols];                               // Allocate space for the data.
        for (int i = 0; i < numCols; i++) {
            pageArray[0][i] = "Column_" + i;                                    // Set the headings.
        }
        
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            int sourceRowIndex = 1;
            
            for (WebElement bodyRowElements : bodyRowElementsList) {
                GeneRow geneRow = new GeneRow();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                WebElement titleDivElement = bodyRowElementList.get(0).findElement(By.cssSelector("div.geneCol div.title a"));
                String href = titleDivElement.getAttribute("href");
                int pos = href.lastIndexOf("/");
                geneRow.geneId = href.substring(pos + 1).trim();                                                    // geneId.
                pageArray[sourceRowIndex][COL_INDEX_GENE_ID] = geneRow.geneId;
                geneRow.geneSymbol = titleDivElement.findElement(By.cssSelector("span.gSymbol")).getText().trim();  // geneSymbol.
                pageArray[sourceRowIndex][COL_INDEX_GENE_SYMBOL] = geneRow.geneSymbol;
                
                WebElement geneColElement = bodyRowElementList.get(0).findElement(By.cssSelector("div.geneCol"));
                GeneDetails geneDetails = new GeneDetails(geneColElement);
                geneRow.geneName = geneDetails.name;                                                                // geneName.
                pageArray[sourceRowIndex][COL_INDEX_GENE_NAME] = geneRow.geneName;
                geneRow.humanOrthologs = geneDetails.humanOrthologs;                                                // humanOrtholog list.
                pageArray[sourceRowIndex][COL_INDEX_HUMAN_ORTHOLOG] = StringUtils.join(geneRow.humanOrthologs, "|");
                
                sourceRowIndex++;
                bodyRows.add(geneRow);
            }
        }
        
        return new GridMap(pageArray, driver.getCurrentUrl());
    }
    
    
    // PRIVATE CLASSES
    
    
    /**
     * This class encapsulates the code and data necessary to parse and extract the
     * search page gene details found when you hover the mouse over the gene symbol.
     * There may be any of: [gene] name, human ortholog, and 0 or more synonyms.
     */
    private class GeneDetails {
        private String name = "";
        private List<String> humanOrthologs = new ArrayList();
        
        public GeneDetails(WebElement geneColElement) {        
            
            // In order to see the contents of the span, we need to first bring
            // the gene symbol into view, then hover over it.
            Actions builder = new Actions(driver);

            try {
                TestUtils.scrollToTop(driver, geneColElement, -50);             // Scroll gene symbol into view.
                Actions hoverOverGene = builder.moveToElement(geneColElement);
                hoverOverGene.perform();                                        // Hover over the gene symbol.
                
                List<WebElement> humanOrthologElements = geneColElement.findElements(By.cssSelector("div.subinfo ul.ortholog li"));
                if ( ! humanOrthologElements.isEmpty()) {
                    for (WebElement humanOrthologElement : humanOrthologElements) {
                        humanOrthologs.add(humanOrthologElement.getText());
                    }
                } else {
                    String[] rawHumanOrthologStrings = geneColElement.findElement(By.cssSelector("div.subinfo")).getText().split("\n");
                    for (String humanOrthologString : rawHumanOrthologStrings) {
                        String[] humanOrthologParts = humanOrthologString.split(":");
                        if (humanOrthologParts[0].trim().equals("human ortholog")) {
                            humanOrthologs.add(humanOrthologParts[1].trim());
                            break;
                        }
                    }
                }

                String subinfoDivText = geneColElement.getText();
                String[] subinfoDivLines = subinfoDivText.split("\n");
                for (String subinfoDivLine : subinfoDivLines) {
                    String[] textParts = subinfoDivLine.split(":");
                    switch (textParts[0].trim().toLowerCase()) {
                        case "name":
                            this.name = textParts[1].trim();                    // geneName.
                            break;
                            
                        default:
                            break;
                    }
                 }
            } catch (Exception e) {
                System.out.println("EXCEPTION: SearchGeneTable.GeneDetails.GeneDetails() while waiting to hover. Error message: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
    
    private class GeneRow {
        private String geneId = "";
        private String geneSymbol = "";
        private List<String> humanOrthologs = new ArrayList();
        private String geneName = "";
        private List<String> synonyms = new ArrayList();
        private List<PhenotypeArchiveStatus> productionStatus = new ArrayList();
        private PhenotypeArchiveStatus phenotypeStatus;
        private String phenotypeStatusLink = "";
        
        @Override
        public String toString() {
            return "geneId: '" + geneId
                 + "'  geneSymbol: '" + geneSymbol
                 + "'  humanOrtholog: '" + toStringHumanOrthologs()
                 + "'  geneName: '" + geneName
                 + "'  synonyms: '" + toStringSynonyms()
                 + "'  productionStatus: '" + toStringProductionStatus()
                 + "'  phenotypeStatus: " + (phenotypeStatus == null ? "<null>" : "'" + phenotypeStatus.mpName + "[" + phenotypeStatus.mpClass + "]'")
                 + "'  phenotypeStatusLink: '" + phenotypeStatusLink + "'";
        }
        
        public String toStringHumanOrthologs() {
            String retVal = "";
            
            for (int i = 0; i < humanOrthologs.size(); i++) {
                if (i > 0)
                    retVal += ", ";
                retVal += humanOrthologs.get(i);
            }
            
            return retVal;
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
        private String mpHoverText;
        
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
            mpHoverText = null;
            
            
            try {
                if (anchorElement != null) {
                    mpName = anchorElement.findElement(By.cssSelector("span")).getText();
                    mpClass = PhenotypeArchiveStatusClass.valueOf(anchorElement.getAttribute("class").split(" ")[1]);
                    mpHoverText = anchorElement.getAttribute("oldtitle");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}