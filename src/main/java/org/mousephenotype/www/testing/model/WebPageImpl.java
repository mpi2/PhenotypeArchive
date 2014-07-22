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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author mrelac
 * 
 * This abstract class implements the common parts of the WebPage interface, 
 * leaving the page-specific implementations derived from this class to implement
 * page-specific functionality.
 */
public abstract class WebPageImpl implements WebPage {
    protected final String pageTarget;
    protected final WebDriverWait wait;
    protected final WebDriver driver;
    protected final String id;
    
    public abstract String getDownloadTargetUrlBase();
    
    public WebPageImpl(WebDriver driver, WebDriverWait wait, String pageTarget, String id) {
        this.driver = driver;
        this.wait = wait;
        this.pageTarget = pageTarget;
        this.id = id;
    }
    
    /**
     * Test the download links
     * @param baseUrl the base url from which the download target TSV and XLS
     * are built
     * @return status
     */
    @Override
    public PageStatus testDownload(String baseUrl) {
        PageStatus status = new PageStatus();
            
        try {
            String downloadTargetUrlBase = getDownloadTargetUrlBase();
            int pageResultsCount = getResultsCount();                           // Get the results count off the page (does NOT include headings).

            // Test the TSV.
            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
            // getDownloadTargetUrlBase() typically returns a path of the form '/mi/impc/dev/phenotype-archive/export?xxxxxxx...'.
            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export?' with the baseUrl.
            int pos = downloadTargetUrlBase.indexOf("/export?");
            downloadTargetUrlBase = downloadTargetUrlBase.substring(pos);
            String downloadTargetTsv = baseUrl + downloadTargetUrlBase + "tsv";

            // Get the download stream and statistics for the TSV stream.
            URL url = new URL(downloadTargetTsv);
            DataReaderTsv dataReaderTsv = new DataReaderTsv(url);

            // Check that the phenotypes table page line count equals the download stream line count.
            // If the download stream line count is no more than 50% greater than the phenotypes table
            // page line count, issue a warning; otherwise, flag an error.
            int downloadDataLineCount = dataReaderTsv.lineCount() - 1;      // subtract 1 from download row count to account for heading.
            if (pageResultsCount != downloadDataLineCount) {
                if (downloadDataLineCount <= Math.floor(pageResultsCount * 1.5)) {
                    status.addWarning("WARNING: page results count (" + (pageResultsCount) + ") is is greater than"
                                 + " the TSV download data line count (" + downloadDataLineCount + ").\n URL: " + pageTarget);
                } else {
                    status.addError("ERROR: page results count (" + (pageResultsCount) + ") is is greater than"
                                 + " the TSV download data line count (" + downloadDataLineCount + ").\n URL: " + pageTarget);
                }
            }
            
            // Check that the phenotypes table data equals the download stream data.
            String[][] downloadData = dataReaderTsv.getData(2);
            status = compare(downloadData, downloadTargetTsv);
            if (status.hasErrors()) {
                status.addError(status.getErrorMessages());
            }
            if (status.hasWarnings()) {
                status.addWarning(status.getWarningMessages());
            }
            
            // Test the XLS.
            String downloadTargetXls = baseUrl + downloadTargetUrlBase + "xls";

            // Get the download stream and statistics for the TSV stream.
            url = new URL(downloadTargetXls);
            DataReaderXls dataReaderXls = new DataReaderXls(url);

            // Check that the table page line count equals the download stream line count.
            // If the download stream line count is no more than 50% greater than the phenotypes table
            // page line count, issue a warning; otherwise, flag an error.
            downloadDataLineCount = dataReaderXls.lineCount() - 1;      // subtract 1 from download row count to account for heading.
            if (pageResultsCount != downloadDataLineCount) {
                if (downloadDataLineCount <= Math.floor(pageResultsCount * 1.5)) {
                    status.addWarning("WARNING: page results count (" + (pageResultsCount) + ") is is greater than"
                                 + " the XLS download data line count (" + downloadDataLineCount + ").\n URL: " + pageTarget);
                } else {
                    status.addError("ERROR: page results count (" + (pageResultsCount) + ") is is greater than"
                                 + " the XLS download data line count (" + downloadDataLineCount + ").\n URL: " + pageTarget);
                }
            }
            
            // Check that the table data equals the download stream data.
            downloadData = dataReaderXls.getData(2);
            status = compare(downloadData, downloadTargetXls);
            if (status.hasErrors()) {
                status.addError(status.getErrorMessages());
            }
            if (status.hasWarnings()) {
                status.addWarning(status.getWarningMessages());
            }
        } catch (NoSuchElementException | TimeoutException te) {
            String message = "Expected page for ID " + id + "(" + pageTarget + ") but found none.";
            status.addError(message);
        }  catch (Exception e) {
            String message = "EXCEPTION processing target URL " + pageTarget + ": " + e.getLocalizedMessage();
            status.addError(message);
        }
        
        return status;
    }
}
