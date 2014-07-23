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

import java.net.URL;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a graph page.
 */
public class GraphPageCategorical extends GraphPage {
    /**
     * 
     * @param driver <code>WebDriver</code> instance
     * @param wait <code>WebDriverWait</code> instance
     * @param target gene or phenotype page target
     * @param id gene or phenotype id
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     */
    public GraphPageCategorical(WebDriver driver, WebDriverWait wait, String target, String id, PhenotypePipelineDAO phenotypePipelineDAO) {
        super(driver, wait, target, id, phenotypePipelineDAO);
    }
    
    /**
     * Validate the download links. This test is noticeably different from the gene
     * and pheno page download link tests in that those pages have phenotype
     * tables that closely match the download stream. The graph page and download
     * stream are much different. For the graph page we must test:
     * <ul><li>that the TSV and XLS links create a download stream</li>
     * <li>that the graph page parameters, such as <code>pipeline name</code>,
     * <code>pipelineStableId</code>, <code>parameterName</code>, etc. match</li>
     * <li>that the HTML graph summary table counts match the sum of the
     * requisite values in the download stream</li></ul>
     * 
     * @param baseUrl the base url from which the download target TSV and XLS
     * are built
     * @return status
     */
////    public PageStatus validateDownload(String baseUrl) {
////        return validateDownloadProtected(baseUrl);
////        try {
////            // Test the TSV.
////            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
////            // getDownloadTargetUrlBase() typically returns a path of the form '/mi/impc/dev/phenotype-archive/export?xxxxxxx...'.
////            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export?' with the baseUrl.
////            String downloadTargetUrlBase = driver.findElement(By.xpath("//div[@id='exportIconsDivGlobal']")).getAttribute("data-exporturl");
////            int pos = downloadTargetUrlBase.indexOf("/export?");
////            downloadTargetUrlBase = downloadTargetUrlBase.substring(pos);
////            String downloadTargetTsv = baseUrl + downloadTargetUrlBase + "tsv";
////            
////            // Get the download stream and statistics.
////            URL url = new URL(downloadTargetTsv);
////            DataReaderTsv dataReaderTsv = new DataReaderTsv(url);
////            String[][] data = dataReaderTsv.getData();                          // Get all of the data
////            validateStaticDownloadData(data);                                   // ... and validate it
////            
////            // Test the graph summary table.
////            validateGraphTable(data);
////            
////            
////            // Test the XLS.
////            String downloadTargetXls = baseUrl + downloadTargetUrlBase + "xls";
////            
////            // Get the download stream and statistics.
////            url = new URL(downloadTargetXls);
////            DataReaderXls dataReaderXls = new DataReaderXls(url);
////            data = dataReaderXls.getData();                                     // Get all of the data
////            validateStaticDownloadData(data);                                   // ... and validate it
////            
////            // Test the graph summary table.
////            validateGraphTable(data);
////        } catch (NoSuchElementException | TimeoutException te) {
////            String message = "Expected page for ID " + id + "(" + target + ") but found none.";
////            status.addError(message);
////        }  catch (Exception e) {
////            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
////            status.addError(message);
////        }
////        
////        return status;
////    }
    
    
    // PROTECTED METHODS
    
    
    
    // PRIVATE METHODS
    
    protected void validateGraphTable(String[][] data) {
        System.out.println("GraphPageCategorical implementation of validateGraphTable.");
    }
    
}
