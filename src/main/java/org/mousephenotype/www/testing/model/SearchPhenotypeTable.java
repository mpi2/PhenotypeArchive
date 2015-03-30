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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'mpGrid' HTML table for phenotypes.
 */
public class SearchPhenotypeTable extends SearchFacetTable {
    
    public static final int COL_INDEX_COMP_MAPPED_HP_TERMS = 0;
    public static final int COL_INDEX_DEFINITION           = 1;
    public static final int COL_INDEX_PHENOTYPE_ID         = 2;
    public static final int COL_INDEX_PHENOTYPE_TERM       = 3;
    public static final int COL_INDEX_PHENOTYPE_ID_LINK    = 4;
    public static final int COL_INDEX_SYNONYMS             = 5;
    public static final int COL_INDEX_TOP_LEVEL_MP_TERM    = 6;
    public static final int COL_INDEX_PHENOTYPING_CALLS    = 7;
    public static final int COL_INDEX_LAST = COL_INDEX_PHENOTYPING_CALLS;       // Should always point to the last (highest-numbered) index.
    
    private final List<PhenotypeRow> bodyRows = new ArrayList();
    private final GridMap pageData;
    
    private final static Map<TableComponent, By> map = new HashMap();
    static {
        map.put(TableComponent.BY_TABLE, By.xpath("//table[@id='mpGrid']"));
        map.put(TableComponent.BY_TABLE_TR, By.xpath("//table[@id='mpGrid']/tbody/tr"));
        map.put(TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='mpGrid_length']"));
    }
    
    /**
     * Creates a new <code>SearchPhenotypeTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchPhenotypeTable(WebDriver driver, int timeoutInSeconds) {
        super(driver, timeoutInSeconds, map);
        
        pageData = load();
    }
    
    /**
     * Validates download data against this <code>SearchPhenotypeTable</code>
     * instance.
     * 
     * @param downloadDataArray The download data used for comparison
     * @return validation status
     */
    @Override
    public PageStatus validateDownload(String[][] downloadDataArray) {
        final Integer[] pageColumns = {
              COL_INDEX_PHENOTYPE_TERM
            , COL_INDEX_PHENOTYPE_ID
            , COL_INDEX_DEFINITION
            , COL_INDEX_PHENOTYPE_ID_LINK
            , COL_INDEX_SYNONYMS
            , COL_INDEX_COMP_MAPPED_HP_TERMS
            , COL_INDEX_PHENOTYPING_CALLS
        };
        final Integer[] downloadColumns = {
              DownloadSearchMapPhenotypes.COL_INDEX_PHENOTYPE_TERM
            , DownloadSearchMapPhenotypes.COL_INDEX_PHENOTYPE_ID
            , DownloadSearchMapPhenotypes.COL_INDEX_DEFINITION
            , DownloadSearchMapPhenotypes.COL_INDEX_PHENOTYPE_ID_LINK
            , DownloadSearchMapPhenotypes.COL_INDEX_SYNONYMS
            , DownloadSearchMapPhenotypes.COL_INDEX_COMP_MAPPED_HP_TERMS
            , DownloadSearchMapPhenotypes.COL_INDEX_PHENOTYPING_CALLS
        };
        final Integer[] sortColumns = {
              DownloadSearchMapPhenotypes.COL_INDEX_SYNONYMS
            , DownloadSearchMapPhenotypes.COL_INDEX_COMP_MAPPED_HP_TERMS
        };
        
        downloadDataArray = TestUtils.sortDelimitedArray(downloadDataArray, "|", Arrays.asList(sortColumns));
        return validateDownloadInternal(pageData, pageColumns, downloadDataArray, downloadColumns, driver.getCurrentUrl());
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Pulls all rows of data and column access variables from the search page's
     * 'mpGrid' HTML table.
     *
     * @return <code>numRows</code> rows of data and column access variables
     * from the search page's 'mpGrid' HTML table.
     */
    private GridMap load() {
        return load(null);
    }

    /**
     * Pulls <code>numRows</code> rows of search page gene facet data and column
     * access variables from the search page's 'mpGrid' HTML table.
     *
     * @param numRows the number of <code>GridMap</code> table rows to return,
     * including the heading row. To specify all rows, set <code>numRows</code>
     * to null.
     * @return <code>numRows</code> rows of search page gene facet data and
     * column access variables from the search page's 'mpGrid' HTML table.
     */
    private GridMap load(Integer numRows) {
        if (numRows == null)
            numRows = computeTableRowCount();
        
        String[][] pageArray;
        
        // Wait for page.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#mpGrid")));
        int numCols = COL_INDEX_LAST + 1;
        
        pageArray = new String[numRows][numCols];                               // Allocate space for the data.
        for (int i = 0; i < numCols; i++) {
            pageArray[0][i] = "Column_" + i;                                    // Set the headings.
        }
        
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            int sourceRowIndex = 1;
            
            pageArray[sourceRowIndex][COL_INDEX_COMP_MAPPED_HP_TERMS] = "";
            pageArray[sourceRowIndex][COL_INDEX_DEFINITION] = "";
            pageArray[sourceRowIndex][COL_INDEX_PHENOTYPE_ID] = "";
            pageArray[sourceRowIndex][COL_INDEX_PHENOTYPE_TERM] = "";
            pageArray[sourceRowIndex][COL_INDEX_PHENOTYPE_ID_LINK] = "";
            pageArray[sourceRowIndex][COL_INDEX_SYNONYMS] = "";
            pageArray[sourceRowIndex][COL_INDEX_PHENOTYPING_CALLS] = "";
            
            for (WebElement bodyRowElements : bodyRowElementsList) {
                PhenotypeRow phenotypeRow = new PhenotypeRow();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                // Sometimes there is only an anchor element (and no mpCol) inside the td.....
                List<WebElement> titleDivElements = bodyRowElementList.get(0).findElements(By.cssSelector("div.mpCol div.title a"));
                WebElement titleDivElement = (titleDivElements.isEmpty() ? bodyRowElementList.get(0).findElement(By.cssSelector("a")) : titleDivElements.get(0));
                phenotypeRow.phenotypeIdLink = titleDivElement.getAttribute("href");                                    // phenotypeIdLink.
                pageArray[sourceRowIndex][COL_INDEX_PHENOTYPE_ID_LINK] = phenotypeRow.phenotypeIdLink;
                
                int pos = phenotypeRow.phenotypeIdLink.lastIndexOf("/");
                phenotypeRow.phenotypeId = phenotypeRow.phenotypeIdLink.substring(pos + 1).trim();                      // phenotypeId.
                pageArray[sourceRowIndex][COL_INDEX_PHENOTYPE_ID] = phenotypeRow.phenotypeId;
                
                phenotypeRow.phenotypeTerm = titleDivElement.getText().trim();                                          // phenotypeTerm.
                pageArray[sourceRowIndex][COL_INDEX_PHENOTYPE_TERM] = phenotypeRow.phenotypeTerm;
                
                List<WebElement> mpColElements = bodyRowElementList.get(0).findElements(By.cssSelector("div.mpCol"));
                if ( ! mpColElements.isEmpty()) {
                    PhenotypeDetails phenotypeDetails = new PhenotypeDetails(mpColElements.get(0));
                    phenotypeRow.synonyms = phenotypeDetails.synonyms;                                                  // synonym list.
                    pageArray[sourceRowIndex][COL_INDEX_SYNONYMS] = phenotypeRow.toStringSynonyms();
                    
                    phenotypeRow.hpTerms  = phenotypeDetails.hpTerms;                                                   // hp terms.
                    pageArray[sourceRowIndex][COL_INDEX_COMP_MAPPED_HP_TERMS] = phenotypeRow.toStringHpTerms();
                }
                phenotypeRow.definition = bodyRowElementList.get(1).getText();                                          // definition.
                pageArray[sourceRowIndex][COL_INDEX_DEFINITION] = phenotypeRow.definition;
                
                Integer iCalls = Utils.tryParseInt(bodyRowElementList.get(2).getText());
                phenotypeRow.phenotypeCalls = (iCalls == null ? 0 : iCalls);
                pageArray[sourceRowIndex][COL_INDEX_PHENOTYPING_CALLS] = Integer.toString(phenotypeRow.phenotypeCalls);
                
                sourceRowIndex++;
                bodyRows.add(phenotypeRow);
            }
        }
        
        return new GridMap(pageArray, driver.getCurrentUrl());
    }
    
    
    // PRIVATE CLASSES
    
    
    /**
     * This class encapsulates the code and data necessary to parse and extract
     * the search page phenotype details found when you hover the mouse over the
     * phenotype term. There should be a phenotype term and 0 or more synonyms.
     */
    private class PhenotypeDetails {
        public final List<String> synonyms = new ArrayList();
        public final List<String> hpTerms  = new ArrayList();
        
        public PhenotypeDetails(WebElement mpColElement) {        
            
            // In order to see the contents of the span, we need to first bring
            // the term into view, then hover over it.
            Actions builder = new Actions(driver);

            try {
                TestUtils.scrollToTop(driver, mpColElement, -50);               // Scroll term into view.
                Actions hoverOverTerm = builder.moveToElement(mpColElement);
                hoverOverTerm.perform();                                        // Hover over the term.
                
                // synonyms and hpterms are both optional. If present, they are held within 'div.subinfo'.
                List<WebElement> subinfoElements = mpColElement.findElements(By.cssSelector("div.subinfo"));
                for (WebElement subinfoElement : subinfoElements) {
                    WebElement labelElement = subinfoElement.findElement(By.cssSelector("span.label"));
                    List<WebElement> synonymElements;
                    switch (labelElement.getText()) {
                        case "synonym":
                            synonymElements = subinfoElement.findElements(By.cssSelector("ul.synonym li"));
                            if ( ! synonymElements.isEmpty()) {
                                for (WebElement synonymElement : synonymElements) {
                                    synonyms.add(synonymElement.getText());
                                }
                            } else {
                                // A pheno entry with a single synonym appears as text in the subinfo div.
                                synonymElements = subinfoElement.findElements(By.cssSelector("span.label"));
                                if ( ! synonymElements.isEmpty()) {
                                    String ss = subinfoElement.getText().trim().replaceFirst("synonym: ", "");
                                    synonyms.add(ss);
                                }
                            }
                            break;
                            
                        case "computationally mapped HP term":
                            List<WebElement> hpTermElements = subinfoElement.findElements(By.cssSelector("div.subinfo ul.hpTerms li"));
                            if ( ! hpTermElements.isEmpty()) {
                                for (WebElement hpTermElement : hpTermElements) {
                                    hpTerms.add(hpTermElement.getText());
                                }
                            } else {
                                // A pheno entry with a single hpterm appears as text in the subinfo div.
                                hpTermElements = subinfoElement.findElements(By.cssSelector("span.label"));
                                if ( ! hpTermElements.isEmpty()) {
                                    String ss = subinfoElement.getText().trim().replaceFirst("computationally mapped HP term: ", "");
                                    hpTerms.add(ss);
                                }
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println("EXCEPTION: SearchPhenotypeTable.PhenotypeDetails.PhenotypeDetails() while waiting to hover. Error message: " + e.getLocalizedMessage());
            }
        }
        
        @Override
        public String toString() {
            return "'  synonyms: '"      + toStringSynonyms() + "'"
                 + "'  hpTerms:  '"      + toStringHpTerms() + "'"
                    ;
        }
        
        public String toStringSynonyms() {
            String retVal = "";
            Collections.sort(synonyms);
            for (int i = 0; i < synonyms.size(); i++) {
                if (i > 0)
                    retVal += "|";
                retVal += synonyms.get(i);
            }
            
            return retVal;
        }
        
        public String toStringHpTerms() {
            String retVal = "";
            Collections.sort(hpTerms);
            for (int i = 0; i < hpTerms.size(); i++) {
                if (i > 0)
                    retVal += "|";
                retVal += hpTerms.get(i);
            }
            
            return retVal;
        }
    }
    
    private class PhenotypeRow {
        private String phenotypeTerm   = "";
        private String phenotypeId     = "";
        private String phenotypeIdLink = "";
        private String definition      = "";
        private int    phenotypeCalls  = 0;
        private List<String> synonyms   = new ArrayList();
        private List<String> hpTerms   = new ArrayList();
        
        @Override
        public String toString() {
            return "phenotypeTerm: '"     + phenotypeTerm
                 + "'  phenotypeId: '"    + phenotypeId
                 + "'  phenotypeLink: '"  + phenotypeIdLink
                 + "'  definition: '"     + definition
                 + "'  phenotypeCalls: '" + phenotypeCalls
                 + "'  synonyms: '"       + toStringSynonyms() + "'"
                 + "'  hpTerms: '"        + toStringHpTerms() + "'"
                    ;
        }
        
        public String toStringSynonyms() {
            String retVal = "";
            Collections.sort(synonyms);
            for (int i = 0; i < synonyms.size(); i++) {
                if (i > 0)
                    retVal += "|";
                retVal += synonyms.get(i);
            }
            
            return retVal;
        }
        
        public String toStringHpTerms() {
            String retVal = "";
            Collections.sort(hpTerms);
            for (int i = 0; i < hpTerms.size(); i++) {
                if (i > 0)
                    retVal += "|";
                retVal += hpTerms.get(i);
            }
            
            return retVal;
        }
    }

}