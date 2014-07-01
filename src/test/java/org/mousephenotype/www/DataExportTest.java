/*
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
package org.mousephenotype.www;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.www.testing.model.DataReader;
import org.mousephenotype.www.testing.model.DataReaderFactory;
import org.mousephenotype.www.testing.model.DataReaderTsv;
import org.mousephenotype.www.testing.model.DataReaderXls;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * These are selenium-based JUnit web tests that are configured (via the pom.xml) not to
 * run with the default profile because they take too long to complete. To run them, 
 * use the 'web-tests' profile.
 * 
 * These selenium tests use selenium's WebDriver protocol and thus need a hub
 * against which to run. The url for the hub is defined in the Test Packages
 * /src/test/resources/testConfig.properties file (driven by /src/test/resources/test-config.xml).
 * 
 * To run these tests, edit /src/test/resources/testConfig.properties, making sure
 * that the properties 'seleniumUrl' and 'desiredCapabilities' are defined. Consult
 * /src/test/resources/test-config.xml for valid desiredCapabilities bean ids.
 * 
 * Examples:
 *      seleniumUrl=http://mi-selenium-win.windows.ebi.ac.uk:4444/wd/hub
 *      desiredCapabilities=firefoxDesiredCapabilities
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class DataExportTest {
    
    @Autowired
    protected GenotypePhenotypeService genotypePhenotypeService;
    

    @Autowired
    protected String baseUrl;
    
    @Autowired
    protected WebDriver driver;
    
    @Autowired
    protected String seleniumUrl;
    
    @Autowired
    protected TestUtils testUtils;
    
    private final int TIMEOUT_IN_SECONDS = 4;
    private final int THREAD_WAIT_IN_MILLISECONDS = 1000;
    
    private int timeout_in_seconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    @Before
    public void setup() {
        if (Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeout_in_seconds = Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            thread_wait_in_ms = Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));
        
        TestUtils.printTestEnvironment(driver, seleniumUrl);
        driver.navigate().refresh();
        try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    
    // PRIVATE METHODS
    
    
    /**
     * Compares <code>genePageData</code> with <code>downloadData</code>, adding
     * any errors to <code>errorList</code>.
     * @param pageData the page data operand
     * @param downloadData the download data operand
     * @param errorList A place to log errors
     * @return the number of records in error
     */
    private int compareLineToTable(String[][] pageData, String[][] downloadData, List<String> errorList, String genePageTarget, String downloadTarget)
    {
        ArrayList<String[]> rowErrors = new ArrayList();
        
        int errorCount = 0;
        if (pageData.length != downloadData.length) {
            errorList.add("ERROR: the number of page data rows (" + pageData.length + ") does not match the number of download rows (" + downloadData.length + ").");
            return 1;
        }
        for (int rowIndex = 0; rowIndex < pageData.length; rowIndex++) {
            if (pageData[rowIndex].length != downloadData[rowIndex].length) {
                errorList.add("ERROR: the number of page columns in row " + rowIndex + " (" + pageData[rowIndex].length + ") does not match the number of download columns (" + downloadData[rowIndex].length + ")");
                return 1;
            }
            
            String[] colErrors;
            
            for (int colIndex = 0; colIndex < pageData[rowIndex].length; colIndex++) {
                String pageCell = pageData[rowIndex][colIndex];
                String downloadCell = downloadData[rowIndex][colIndex];
                if (pageCell.compareTo(downloadCell) != 0) {
                    colErrors = new String[] { "[" + rowIndex + "][" + colIndex + "]", pageCell, downloadCell };
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
            errorList.add("Mismatch.");
        }
        
        return (errorList.isEmpty() ? 0 : 1);
    }
    
    /**
     * Returns <code>maxRows</code> rows of data from the stream created by 
     * invoking <code>target</code>. Supported stream formats are defined in
     * the public enum <code>DataReader.DataType</code>.
     * @param wait valid <code>WebDriverWait</code> instance
     * @param target target url that points to the desired gene page
     * @param maxRows the maximum number of phenotype table rows to return, including
     * any headings.
     * @return a 2-dimensional array containing <code>maxRows</code> rows of data
     * from the stream identified by <code>url</code>, including headings
     */
    private String[][] getDownloadData(String target, int maxRows) {
        String[][] data = new String[maxRows][9];
        DataReader dataReader = null;
        try {
            URL url = new URL(target);
            dataReader = DataReaderFactory.create(url);
            dataReader.open();
            List<String> line;
            
            for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
                line = dataReader.getLine();
                if (line == null)
                    break;
                
                for (int colIndex = 0; colIndex < line.size(); colIndex++) {
                    data[rowIndex][colIndex] = line.get(colIndex);
                }
            }
        } catch (IOException e) {
            System.out.println("EXCEPTION: " + e.getLocalizedMessage());
        } finally {
            try {
                if (dataReader != null)
                    dataReader.close();
            } catch (IOException e) {
                System.out.println("EXCEPTION: " + e.getLocalizedMessage());
            }
        }
        
        return data;
    }

    /**
     * Pulls <code>maxRows</code> rows of data from the gene page's 'phenotypes'
     * HTML table, returning the data a 2-dimensional array.
     * @param wait valid <code>WebDriverWait</code> instance
     * @param target target url that points to the desired gene page
     * @param maxRows the maximum number of phenotype table rows to return, including
     * any headings.
     * @return a 2-dimensional array containing <code>maxRows</code> rows of data
     * from the gene page's 'phenotypes' HTML table
     * @throws NoSuchElementException
     * @throws TimeoutException 
     */
    private String[][] getGenePageData(WebDriverWait wait, String target, int maxRows) /*throws NoSuchElementException, TimeoutException*/ {
        String[][] data = new String[maxRows][9];
        
        // Load and wait for page.
        driver.get(target);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
        List<WebElement> rowElements;
        for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
            if (rowIndex == 0) {
                rowElements = driver.findElements(By.xpath("//table[@id='phenotypes']/thead/tr/*"));
            } else {
                rowElements = driver.findElements(By.xpath("//table[@id='phenotypes']/tbody/tr/*"));
            }
            
            for (int colIndex = 0; colIndex < rowElements.size(); colIndex++) {
                WebElement e = rowElements.get(colIndex);
                data[rowIndex][colIndex] = e.getText();
            }
        }
        
        return data;
    }
    
    
    // TESTS
    
    
    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure there is a page for each. Limit the test
     * to the first MAX_GENE_TEST_PAGE_COUNT by setting it to the limit you want.
     * 
     * NOTE: This test currently only works on chrome. In order to run this test
     * successfully, we need to clear the downloads folder first. A better solution
     * is to find a way to programatically suppress the download dialog and later,
     * when the test is complete, to remove the download file(s).
     * 
     * For now (01-May-2014) we shall mark this test @Ignore.
     * 
     * @throws SolrServerException 
     */
    @Test
@Ignore
    public void testDownloadTsv() throws SolrServerException {
        String testName = "testDownloadTsv";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        DataReader dataReader = null;
        
geneIds.add("MGI:1921354");
        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
target = "http://dev.mousephenotype.org/data/export?mpId=%22MP%3A0005266%22&externalDbId=3&fileName=gene_variants_with_phen_MP_0005266&solrCoreName=genotype-phenotype&dumpMode=all&baseUrl=http%3A%2F%2Fdev.mousephenotype.org%2Fdata%2Fphenotypes%2FMP%3A0005266&page=phenotype&gridFields=marker_symbol%2Callele_symbol%2Czygosity%2Csex%2Cprocedure_name%2Cresource_name%2Cphenotyping_center%2Cparameter_stable_id%2Cmp_term_name%2Cmarker_accession_id%2C+parameter_name&params=qf%3Dauto_suggest%26defType%3Dedismax%26wt%3Djson%26rows%3D100000%26q%3D*%3A*%26fq%3D(mp_term_id%3A%22MP%3A0005266%22%2BOR%2Btop_level_mp_term_id%3A%22MP%3A0005266%22)&fileType=tsv&_=1403882094799";

            System.out.println("gene[" + i + "] URL: " + target);

            try {
                URL url = new URL(target);
                dataReader = new DataReaderTsv(url);
                dataReader.open();
                List<String> line;
                while ((line = dataReader.getLine()) != null) {
                    for (int index = 0; index < line.size(); index++) {
                        if (index > 0)
                            System.out.print("\t");
                        System.out.print(line.get(index));
                    }
                    System.out.println();
                }
            } catch (IOException e) {
                System.out.println("EXCEPTION: " + e.getLocalizedMessage());
            } finally {
                try {
                    if (dataReader != null)
                        dataReader.close();
                } catch (IOException e) {
                    System.out.println("EXCEPTION: " + e.getLocalizedMessage());
                }
            }
            
            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }
    
    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure there is a page for each. Limit the test
     * to the first MAX_GENE_TEST_PAGE_COUNT by setting it to the limit you want.
     * 
     * NOTE: This test currently only works on chrome. In order to run this test
     * successfully, we need to clear the downloads folder first. A better solution
     * is to find a way to programatically suppress the download dialog and later,
     * when the test is complete, to remove the download file(s).
     * 
     * For now (01-May-2014) we shall mark this test @Ignore.
     * 
     * @throws SolrServerException 
     */
    @Test
@Ignore
    public void testDownloadXls() {
        String testName = "testDownloadXls";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        DataReader dataReader = null;
        
geneIds.add("MGI:1921354");
        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
target = "http://dev.mousephenotype.org/data/export?mpId=%22MP%3A0005266%22&externalDbId=3&fileName=gene_variants_with_phen_MP_0005266&solrCoreName=genotype-phenotype&dumpMode=all&baseUrl=http%3A%2F%2Fdev.mousephenotype.org%2Fdata%2Fphenotypes%2FMP%3A0005266&page=phenotype&gridFields=marker_symbol%2Callele_symbol%2Czygosity%2Csex%2Cprocedure_name%2Cresource_name%2Cphenotyping_center%2Cparameter_stable_id%2Cmp_term_name%2Cmarker_accession_id%2C+parameter_name&params=qf%3Dauto_suggest%26defType%3Dedismax%26wt%3Djson%26rows%3D100000%26q%3D*%3A*%26fq%3D(mp_term_id%3A%22MP%3A0005266%22%2BOR%2Btop_level_mp_term_id%3A%22MP%3A0005266%22)&fileType=xls&_=1403882094801 ";
            System.out.println("gene[" + i + "] URL: " + target);

            try {
                URL url = new URL(target);
                dataReader = new DataReaderXls(url);
                dataReader.open();
                List<String> line;
                while ((line = dataReader.getLine()) != null) {
                    for (int index = 0; index < line.size(); index++) {
                        if (index > 0)
                            System.out.print("\t");
                        System.out.print(line.get(index));
                    }
                    System.out.println();
                }
            } catch (IOException e) {
                System.out.println("EXCEPTION: " + e.getLocalizedMessage());
            } finally {
                try {
                    if (dataReader != null)
                        dataReader.close();
                } catch (IOException e) {
                    System.out.println("EXCEPTION: " + e.getLocalizedMessage());
                }
            }
            
            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }
    
    @Test
    public void testGeneDownloads() {
        String testName = "testGeneDownloads";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList();
        String genePageTarget = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        
geneIds.add("MGI:1921354");
        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each tsv and xls download link.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        
        
        
        
        
        
        int errorCount = 0;
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            genePageTarget = baseUrl + "/genes/" + geneId;
System.out.println("gene[" + i + "] URL: " + genePageTarget);

            String[][] pageData = getGenePageData(wait, genePageTarget, 2);
            
            
            
            
            
            
//	if (driver instanceof JavascriptExecutor) {
//		((JavascriptExecutor) driver).executeScript("hello();");
//		((JavascriptExecutor) driver).executeScript("return geneId;");
//	}
        
        
        
        
        
        
String downloadTarget = "http://dev.mousephenotype.org/data/export?mpId=%22MP%3A0005266%22&externalDbId=3&fileName=gene_variants_with_phen_MP_0005266&solrCoreName=genotype-phenotype&dumpMode=all&baseUrl=http%3A%2F%2Fdev.mousephenotype.org%2Fdata%2Fphenotypes%2FMP%3A0005266&page=phenotype&gridFields=marker_symbol%2Callele_symbol%2Czygosity%2Csex%2Cprocedure_name%2Cresource_name%2Cphenotyping_center%2Cparameter_stable_id%2Cmp_term_name%2Cmarker_accession_id%2C+parameter_name&params=qf%3Dauto_suggest%26defType%3Dedismax%26wt%3Djson%26rows%3D100000%26q%3D*%3A*%26fq%3D(mp_term_id%3A%22MP%3A0005266%22%2BOR%2Btop_level_mp_term_id%3A%22MP%3A0005266%22)&fileType=tsv&_=1403882094799";
            String[][] downloadData = getDownloadData(downloadTarget, 2);
            errorCount += compareLineToTable(pageData, downloadData, errorList, genePageTarget, downloadTarget);

downloadTarget = "http://dev.mousephenotype.org/data/export?mpId=%22MP%3A0005266%22&externalDbId=3&fileName=gene_variants_with_phen_MP_0005266&solrCoreName=genotype-phenotype&dumpMode=all&baseUrl=http%3A%2F%2Fdev.mousephenotype.org%2Fdata%2Fphenotypes%2FMP%3A0005266&page=phenotype&gridFields=marker_symbol%2Callele_symbol%2Czygosity%2Csex%2Cprocedure_name%2Cresource_name%2Cphenotyping_center%2Cparameter_stable_id%2Cmp_term_name%2Cmarker_accession_id%2C+parameter_name&params=qf%3Dauto_suggest%26defType%3Dedismax%26wt%3Djson%26rows%3D100000%26q%3D*%3A*%26fq%3D(mp_term_id%3A%22MP%3A0005266%22%2BOR%2Btop_level_mp_term_id%3A%22MP%3A0005266%22)&fileType=xls&_=1403882094801 ";
            downloadData = getDownloadData(downloadTarget, 2);
            errorCount += compareLineToTable(pageData, downloadData, errorList, genePageTarget, downloadTarget);
            
            if (errorList.isEmpty()) {
                message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + genePageTarget;
                successList.add(message);
            } else {
                errorList.clear();
                errorList.add("ERROR: gene id " + geneId + ": mismatch between gene page and download.");
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }
    
    
}
