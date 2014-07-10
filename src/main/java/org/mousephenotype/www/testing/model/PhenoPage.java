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
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 
 * This class encapsulates the code and data necessary to represent a Phenotype
 * Archive phenotype page for Selenium testing.
 */
public class PhenoPage {
    private final String phenoPageTarget;
    private final WebDriverWait wait;
    private final WebDriver driver;
    private final String phenoId;
    
    public PhenoPage(WebDriver driver, WebDriverWait wait, String phenoPageTarget, String phenoId) {
        this.driver = driver;
        this.wait = wait;
        this.phenoPageTarget = phenoPageTarget;
        this.phenoId = phenoId;
        
        load();
    }
    
    /**
     * Compares <code>phenoPageData</code> with <code>downloadData</code>, adding
     * any errors to <code>errorList</code>.
     * 
     * NOTE: All flavours of the download stream need some special modifications
     * for comparison testing:
     * <ul><li>The 'Allele' column needs to have &lt; and &lt; characters stripped,
     * as they never exist in the pheno page</li>
     * <li>When comparing the graph url, start with the first occurrence of
     * "/charts'. When testing on localhost, baseUrl is the localhost url, but
     * the download stream has a dev url (for historical, complicated reasons).</li></ul>
     * 
     * @param downloadData the download data operand
     * @param downloadTarget The download target url (for informational purposes)
     * @return the number of records in error
     */
    public PageStatus compare(String[][] downloadData, String downloadTarget) {
        PageStatus status = new PageStatus();
        String[][] pageData = getPhenotypeTableData(downloadData.length);
        DownloadStructurePheno dsPheno = new DownloadStructurePheno();
        PhenotypeTablePheno ptPheno = new PhenotypeTablePheno();
        
        if (pageData.length != downloadData.length) {
            status.addFail("ERROR: the number of page data rows (" + pageData.length + ") does not match the number of download rows (" + downloadData.length + ").");
            return status;
        }
        
        ArrayList<String[]> rowErrors = new ArrayList();
        
        int errorCount = 0;
        for (int rowIndex = 0; rowIndex < pageData.length; rowIndex++) {
            String[] colErrors;
            for (int colIndexPage = 0; colIndexPage < pageData[rowIndex].length; colIndexPage++) {
                String pageCell = pageData[rowIndex][colIndexPage].trim();              // Ignore leading/trailing whitespace.
                int downloadCellIndex = dsPheno.getColIndex(colIndexPage);              // Given the page column index, look up the download column index.
                String downloadCell = downloadData[rowIndex][downloadCellIndex].trim(); // Ignore leading/trailing whitespace.
                if ((pageCell == null) && (downloadCell == null))
                    continue;                                                   // both values are null (and equal).
                if ((pageCell == null) || (downloadCell == null)) {
                    colErrors = new String[] { "[" + rowIndex + "][" + colIndexPage + "]", (pageCell == null ? "<null>" : pageCell), (downloadCell == null ? "<null>" : downloadCell) };
                    rowErrors.add(colErrors);
                    errorCount++;
                    continue;
                }
                
                if (rowIndex > 0) {                                             // If this is a non-header row ...
                    if (colIndexPage == ptPheno.getColIndexGraph()) {           // ... if this is a graph url column, remove everything before the
                        int idx = pageCell.indexOf("/charts");                  //     '/charts' part of the url on both components to be compared.
                        
                        String deletePart = pageCell.substring(0, idx);
                        pageCell = pageCell.replaceFirst(deletePart, "");

                        idx = downloadCell.indexOf("/charts");
                        deletePart = downloadCell.substring(0, idx);
                        downloadCell = downloadCell.replaceFirst(deletePart, "");
                    }
                }
                
                // The pheno download breaks the page's "Gene / Allele" [single] column into two columns: "Gene" and "Allele".
                // For page column 0, do a 'startsWith' compare to the allele column (column 1).
                if (colIndexPage == 0) {
                    if ( ! pageCell.contains(downloadCell)) {
                        colErrors = new String[] { "[" + rowIndex + "][" + colIndexPage + "]", "'" + pageCell + "'", "'" + downloadCell + "'" };
                        rowErrors.add(colErrors);
                        errorCount++;
                    }
                } else {
                    if (pageCell.compareTo(downloadCell) != 0) {
                        colErrors = new String[] { "[" + rowIndex + "][" + colIndexPage + "]", "'" + pageCell + "'", "'" + downloadCell + "'" };
                        rowErrors.add(colErrors);
                        errorCount++;
                    }
                }
            }
        }
        
        if ( ! rowErrors.isEmpty()) {
            System.out.println("\n" + errorCount + " errors:");
            System.out.println("PHENO PAGE DATA: " + phenoPageTarget);
            System.out.println("DOWNLOAD DATA:   " + downloadTarget);
            System.out.println();
            String format = "%-20s   %-50s   %-50s\n";
            System.out.printf(format, "OFFSET", "PHENO PAGE DATA", "DOWNLOAD DATA");
            for (String[] row : rowErrors) {
                System.out.printf(format, row[0], row[1], row[2]);
            }
            status.addFail("Mismatch.");
        }
        
        return status;
    }
    
    public String[][] getPhenotypeTableData(int maxRows) {
        PhenotypeTablePheno ptPheno = new PhenotypeTablePheno();
        return ptPheno.getData(driver, wait, phenoPageTarget, maxRows);
    }
    
    /**
     * @return the number at the end of the pheno page string 'Total number of results: xxxx'
     */
    public final int getResultsCount() {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='phenotypesDiv']/div[@class='container span12']/p[@class='resultCount']")));
        String s = element.getText().replace("Total number of results: ", "");
        Integer i = Utils.tryParseInt(s);
        
        return (i == null ? 0 : i);
    }
    
    
    // PRIVATE METHODS
    
    
    private PageStatus load() {
        String message;
        PageStatus status = new PageStatus();
        
        // Wait for page to load.
        try {
            driver.get(phenoPageTarget);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(phenoId)));
        } catch (NoSuchElementException | TimeoutException te ) {
            message = "Expected page for MP_TERM_ID " + phenoId + "(" + phenoPageTarget + ") but found none.";
            status.addFail(message);
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + phenoPageTarget + ": " + e.getLocalizedMessage();
            status.addFail(message);
        }
        
        return status;
    }
    
}
