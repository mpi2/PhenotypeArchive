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
 * Archive gene page for Selenium testing.
 */
public class GenePage {
    private final String genePageTarget;
    private final WebDriverWait wait;
    private final WebDriver driver;
    private final String geneId;
    
    public GenePage(WebDriver driver, WebDriverWait wait, String genePageTarget, String geneId) {
        this.driver = driver;
        this.wait = wait;
        this.genePageTarget = genePageTarget;
        this.geneId = geneId;
        
        load();
    }
    
    /**
     * Compares <code>genePageData</code> with <code>downloadData</code>, adding
     * any errors to <code>errorList</code>.
     * 
     * NOTE: All flavours of the download stream need some special modifications
     * for comparison testing:
     * <ul><li>The 'Allele' column needs to have &lt; and &lt; characters stripped,
     * as they never exist in the gene page</li>
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
        DownloadStructureGene dsGene = new DownloadStructureGene();
        
        if (pageData.length != downloadData.length) {
            status.addFail("ERROR: the number of page data rows (" + pageData.length + ") does not match the number of download rows (" + downloadData.length + ").");
            return status;
        }
        
        ArrayList<String[]> rowErrors = new ArrayList();
        
        int errorCount = 0;
        for (int rowIndexPage = 0; rowIndexPage < pageData.length; rowIndexPage++) {
            String[] colErrors;
            
            for (int colIndexPage = 0; colIndexPage < pageData[rowIndexPage].length; colIndexPage++) {
                String pageCell = pageData[rowIndexPage][colIndexPage].trim();                  // Ignore leading/trailing whitespace.
                int downloadCellIindex = dsGene.getColIndex(colIndexPage);                      // Given the page column index, look up the download column index.
                String downloadCell = downloadData[rowIndexPage][downloadCellIindex].trim();    // Ignore leading/trailing whitespace.
                if ((pageCell == null) && (downloadCell == null))
                    continue;                                                   // both values are null (and equal).
                if ((pageCell == null) || (downloadCell == null)) {
                    colErrors = new String[] { "[" + rowIndexPage + "][" + colIndexPage + "]", (pageCell == null ? "<null>" : pageCell), (downloadCell == null ? "<null>" : downloadCell) };
                    rowErrors.add(colErrors);
                    errorCount++;
                    continue;
                }
                
                if (rowIndexPage > 0) {                                         // If this is a non-header row ...
                    PhenotypeTableGene ptGene = new PhenotypeTableGene();
        
                    if (colIndexPage == ptGene.getColIndexGraph()) {            // ... if this is a graph url column, remove everything before the
                        int idx = pageCell.indexOf("/charts");                  //     '/charts' part of the url on both components to be compared.
                        
                        String deletePart = pageCell.substring(0, idx);
                        pageCell = pageCell.replaceFirst(deletePart, "");

                        idx = downloadCell.indexOf("/charts");
                        deletePart = downloadCell.substring(0, idx);
                        downloadCell = downloadCell.replaceFirst(deletePart, "");
                    }
                    
                    if (colIndexPage == ptGene.getColIndexAllele()) {
                        downloadCell = downloadCell                             // ... Remove the '<' and '>' from the download cell.
                                .replace("<", "")
                                .replace(">", "");
                    }
                }
                
                if (pageCell.compareTo(downloadCell) != 0) {
                    colErrors = new String[] { "[" + rowIndexPage + "][" + colIndexPage + "]", "'" + pageCell + "'", "'" + downloadCell + "'" };
                    rowErrors.add(colErrors);
                    errorCount++;
                }
            }
        }
        
        if ( ! rowErrors.isEmpty()) {
            System.out.println("\n" + errorCount + " errors:");
            System.out.println("GENE PAGE DATA: " + genePageTarget);
            System.out.println("DOWNLOAD DATA:  " + downloadTarget);
            System.out.println();
            String format = "%-15s   %-50s   %-50s\n";
            System.out.printf(format, "OFFSET", "GENE PAGE DATA", "DOWNLOAD DATA");
            for (String[] row : rowErrors) {
                System.out.printf(format, row[0], row[1], row[2]);
            }
            status.addFail("Mismatch.");
        }
        
        return status;
    }
    
    /**
     * 
     * @param maxRows the number of data rows to return
     * @return the first <code>maxRows</code> of data from the phenotype HTML table.
     */
    public String[][] getPhenotypeTableData(int maxRows) {
        PhenotypeTableGene ptGene = new PhenotypeTableGene();
        return ptGene.getData(driver, wait, genePageTarget, maxRows);
    }
    
    /**
     * @return the number at the end of the gene page string 'Total number of results: xxxx'
     */
    public final int getResultsCount() {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='phenotypesDiv']/div[@class='container span12']/p[@class='resultCount']")));
        String s = element.getText().replace("Total number of results: ", "");
        Integer i = Utils.tryParseInt(s);
        
        return (i == null ? 0 : i);
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Load the page.
     * @return <code>PageStatus</code> telling whether or not the load was 
     * successful.
     */
    private PageStatus load() {
        String message;
        PageStatus status = new PageStatus();
        
        // Wait for page to load.
        try {
            driver.get(genePageTarget);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
        } catch (NoSuchElementException | TimeoutException te ) {
            message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + genePageTarget + ") but found none.";
            status.addFail(message);
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + genePageTarget + ": " + e.getLocalizedMessage();
            status.addFail(message);
        }
        
        return status;
    }
    
}
