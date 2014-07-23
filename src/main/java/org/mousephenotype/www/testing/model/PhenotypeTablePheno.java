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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary for access to the phenotype
 * page's "phenotypes" HTML table.
 */
public class PhenotypeTablePheno {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String target;
    private GridMap data;

    public static final int COL_INDEX_PHENOTYPES_GENE_ALLELE         =  0;
    public static final int COL_INDEX_PHENOTYPES_ZYGOSITY            =  1;
    public static final int COL_INDEX_PHENOTYPES_SEX                 =  2;
    public static final int COL_INDEX_PHENOTYPES_PHENOTYPE           =  3;
    public static final int COL_INDEX_PHENOTYPES_PROCEDURE_PARAMETER =  4;
    public static final int COL_INDEX_PHENOTYPES_PHENOTYPING_CENTER  =  5;
    public static final int COL_INDEX_PHENOTYPES_SOURCE              =  6;
    public static final int COL_INDEX_PHENOTYPES_P_VALUE             =  7;
    public static final int COL_INDEX_PHENOTYPES_GRAPH               =  8;
    
    public static final String COL_PHENOTYPES_GENE_ALLELE         = "Gene / Allele";
    public static final String COL_PHENOTYPES_ZYGOSITY            = "Zygosity";
    public static final String COL_PHENOTYPES_SEX                 = "Sex";
    public static final String COL_PHENOTYPES_PHENOTYPE           = "Phenotype";
    public static final String COL_PHENOTYPES_PROCEDURE_PARAMETER = "Procedure | Parameter";
    public static final String COL_PHENOTYPES_PHENOTYPING_CENTER  = "Phenotyping Center";
    public static final String COL_PHENOTYPES_SOURCE              = "Source";
    public static final String COL_PHENOTYPES_P_VALUE             = "P Value";
    public static final String COL_PHENOTYPES_GRAPH               = "Graph";
    
    /**
     * Creates a new, empty <code>PhenotypeTablePheno</code> instance. Call <code>
     * load(<b>numRows</b>)</code> to load <code>numRows</code> rows of data.
     * @param driver <code>WebDriver</code> instance
     * @param wait<code>WebDriverWait</code> instance
     * @param target the calling page's target url
     */
    public PhenotypeTablePheno(WebDriver driver, WebDriverWait wait, String target) {
        this.driver = driver;
        this.wait = wait;
        this.target = target;
        this.data = null;
    }
    
    /**
     * @return a <code>GridMap</code> containing the data and column access
     * variables that were loaded by the last call to <code>load()</code>.
     */
    public GridMap getData() {
        return data;
    }

    /**
     * Load phenotype table data
     *
     * @return all rows of data and column access variables from
     * the pheno page's 'phenotypes' HTML table.
     */
    public GridMap load() {
        return load(null);
    }

    /**
     * Pulls <code>numRows</code> rows of data and column access variables from
     * the pheno page's 'phenotypes' HTML table.
     *
     * @param numRows the number of phenotype table rows to return, including
     * the heading row. To specify all rows, set <code>numRows</code> to null.
     * @return <code>numRows</code> rows of data and column access variables
     * from the pheno page's 'phenotypes' HTML table.
     */
    public GridMap load(Integer numRows) {
        if (numRows == null)
            numRows = computeTableRowCount();
        
        String[][] dataArray;
        String value;
        
        // Wait for page.
        WebElement phenotypesTable = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#phenotypes")));

        // Grab the headings.
        List<WebElement> headings = phenotypesTable.findElements(By.cssSelector("thead tr th"));
        numRows = Math.min(computeTableRowCount(), numRows);                        // Take the lesser of: actual row count in HTML table (including heading), or requested numRows.
        int numCols = headings.size();

        dataArray = new String[numRows][numCols];                               // Allocate space for the data.
        int colIndex = 0;
        for (WebElement heading : headings) {                                   // Copy the heading values.
            dataArray[0][colIndex] = heading.getText();
            colIndex++;
        }

        // Loop through all of the tr objects for this page, gathering the data.
        int rowIndex = 1;
        for (WebElement row : phenotypesTable.findElements(By.xpath("//table[@id='phenotypes']/tbody/tr"))) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            colIndex = 0;
            for (WebElement cell : cells) {
                if (colIndex == COL_INDEX_PHENOTYPES_GENE_ALLELE) {
                    String sup = cell.findElement(By.cssSelector("sup")).getText();
                    value = cell.findElement(By.cssSelector("a")).getText();
                    AlleleParser ap = new AlleleParser(value, sup);
                    value = ap.toString();
                } else if (colIndex == COL_INDEX_PHENOTYPES_PHENOTYPE) {
                    value = cell.findElement(By.cssSelector("a")).getText();    // Get the phenotype text.
                } else if (colIndex == COL_INDEX_PHENOTYPES_SEX) {                      // Translate the male/female symbol into a string: 'male', 'female', or 'both'.
                    List<WebElement> sex = cell.findElements(By.xpath("img[@alt='Male' or @alt='Female']"));
                    if (sex.size() == 2) {
                        value = "both";
                    } else {
                        value = sex.get(0).getAttribute("alt").toLowerCase();
                    }
                } else if (colIndex == COL_INDEX_PHENOTYPES_GRAPH) {                    // Extract the graph url from the <a> anchor and decode it.
                    value = cell.findElement(By.cssSelector("a")).getAttribute("href");
                    value = TestUtils.urlDecode(value);
                } else {
                    value = cell.getText();
                }
                
                dataArray[rowIndex][colIndex] = value;
                colIndex++;
            }

            rowIndex++;
//System.out.println();
            if (rowIndex >= numRows) {                                          // Return when we have the number of requested rows.
                data = new GridMap(dataArray, target);
                return data;
            }
        }

        data = new GridMap(dataArray, target);
        
        return data;
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * 
     * @return the number of rows in the "phenotypes" table. Always include 1 extra for the heading.
     */
    private int computeTableRowCount() {
        // Wait for page.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='exportIconsDiv'][@data-exporturl]")));
        
        List<WebElement> elements = driver.findElements(By.xpath("//table[@id='phenotypes']/tbody/tr"));
        return elements.size() + 1;
    }
    
}
