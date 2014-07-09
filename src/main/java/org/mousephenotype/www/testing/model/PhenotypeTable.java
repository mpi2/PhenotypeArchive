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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author mrelac
 * 
 * This class defines methods to fetch data from any page with an HTML table
 * with id <code>phenotypes</code>. Currently both the gene and phenotype pages
 * contain such a table whose contents are similar but whose columns differ
 * slightly in both name and order. Common processing code should go in this
 * superclass while individual page implementation details, such as the column
 * count and indexes should go into derived classes. These derived classes are
 * the ones that should be instantiated.
 */
public abstract class PhenotypeTable {
    public abstract int getTableColumnCount();
    public abstract int getColIndexPhenotype();
    public abstract int getColIndexZygosity();
    public abstract int getColIndexSex();
    public abstract int getColIndexProcedureParameter();
    public abstract int getColIndexPhenotypingCenter();
    public abstract int getColIndexSource();
    public abstract int getColIndexPvalue();
    public abstract int getColIndexGraph();

    /**
     * Pulls <code>maxRows</code> rows of data from the 'phenotypes' HTML table,
     * returning the data in a 2-dimensional array.
     * <b>NOTE: It is expected that the page containing the phenotype table is
     * currently loaded. If it is not, a timeout will be thrown.</b>
     * 
     * @param driver valid <code>WebDriver</code>instance pointing to the target
     * page to be loaded.
     * @param wait valid <code>WebDriverWait</code> instance
     * @param target target url that points to the desired gene page
     * @param maxRows the maximum number of phenotype table rows to return, including
     * any headings.
     * @return a 2-dimensional array containing <code>maxRows</code> rows of data
     * from the gene page's 'phenotypes' HTML table
     * @throws NoSuchElementException
     * @throws TimeoutException 
     */
    public String[][] getData(WebDriver driver, WebDriverWait wait, String target, int maxRows) {
        String[][] data = new String[maxRows][9];
        
        // Wait for page.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='exportIconsDiv'][@data-exporturl]")));
        List<WebElement> rowElements;
        for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
            if (rowIndex == 0) {
                rowElements = driver.findElements(By.xpath("//table[@id='phenotypes']/thead/tr/*"));
            } else {
                rowElements = driver.findElements(By.xpath("//table[@id='phenotypes']/tbody/tr/*"));
            }
            
            for (int colIndex = 0; colIndex < getTableColumnCount(); colIndex++) {
                if (colIndex >= rowElements.size()) {
                    throw new RuntimeException("EXCEPTION: Expected " + getTableColumnCount() + " columns but found " + colIndex + 1
                                               + ". URL: " + target);
                }
                
                WebElement tdElement = rowElements.get(colIndex);
                
                if (rowIndex == 0) {
                    data[rowIndex][colIndex] = tdElement.getText();
                } else {
                    // If this is not the heading row, determine the correct strings for sex and graph link.
                    if (colIndex == getColIndexSex()) {
                        // Translate the male/female symbol into a string.
                        data[rowIndex][colIndex] = tdElement.findElement(By.xpath("img[@alt='Male' or @alt='Female']")).getAttribute("alt").toLowerCase();
                    } else if (colIndex == getColIndexGraph()) {
                        // Extract the graph url from the <a> anchor and decode it.
                        String graphUrl = "";
                        String href = tdElement.findElement(By.xpath("a")).getAttribute("href");
                        try { graphUrl = java.net.URLDecoder.decode(href, "UTF-8"); } catch (UnsupportedEncodingException e) { }
                        data[rowIndex][colIndex] = graphUrl;
                    } else {
                        String graphUrl = "";
                        try { graphUrl = URLDecoder.decode(tdElement.getText(), "UTF-8"); } catch (UnsupportedEncodingException e) { }
                        data[rowIndex][colIndex] = graphUrl;
                    }
                }
            }
        }
        
        return data;
    }

}
