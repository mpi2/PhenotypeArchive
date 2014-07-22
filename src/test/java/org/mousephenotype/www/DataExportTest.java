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
import org.mousephenotype.www.testing.model.GenePage;
import org.mousephenotype.www.testing.model.PageStatus;
import org.mousephenotype.www.testing.model.PhenoPage;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;
import uk.ac.ebi.phenotype.service.MpService;
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
    protected MpService mpService;

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
    
    
    // TESTS
    
    
    /**
     * Test downloads from the genes page.
     * @throws SolrServerException 
     */
    @Test
@Ignore
    public void testGeneDownloads() throws SolrServerException {
        String testName = "testGeneDownloads";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList(genotypePhenotypeService.getAllGenesWithPhenotypeAssociations());
        String target;
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        PageStatus status;
        
        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each tsv and xls download link.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            int errorCount = 0;
            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);
            
            GenePage genePage = new GenePage(driver, wait, target, geneId); // Load the page.
            status = genePage.testDownload(baseUrl);                        // Test the download links.
            if (status.hasWarnings()) {
                System.out.println(status.toStringWarningMessages());
            }
            if (status.hasErrors()) {
                System.out.println(status.toStringErrorMessages());
                errorCount++;
            }

            if (errorCount == 0) {
                message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
                successList.add(message);
            } else {
                errorList.add("ERROR: gene id " + geneId + ": mismatch between gene page and download.");
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }
    
    /**
     * Test downloads from the genes page.
     * @throws SolrServerException 
     */
    @Test
@Ignore
    public void testPhenoDownloads() throws SolrServerException {
        String testName = "testPhenoDownloads";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> phenoIds = new ArrayList(mpService.getAllPhenotypes());
        String target;
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        String message;
        Date start = new Date();
        PageStatus status;
        
        int targetCount = testUtils.getTargetCount(testName, phenoIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " of a total of " + phenoIds.size() + " records.");
        
        // Loop through all phenotypes, testing each tsv and xls download link.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        
        for (String phenoId : phenoIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            int errorCount = 0;
            target = baseUrl + "/phenotypes/" + phenoId;
            System.out.println("pheno[" + i + "] URL: " + target);
            
            PhenoPage phenoPage = new PhenoPage(driver, wait, target, phenoId); // Load the page.
            status = phenoPage.testDownload(baseUrl);                           // Test the download links.
            if (status.hasWarnings()) {
                System.out.println(status.toStringWarningMessages());
            }
            if (status.hasErrors()) {
                System.out.println(status.toStringErrorMessages());
                errorCount++;
            }

            if (errorCount == 0) {
                message = "SUCCESS: MP_TERM_ID " + phenoId + ". URL: " + target;
                successList.add(message);
            } else {
                errorList.add("ERROR: pheno id " + phenoId + ": mismatch between pheno page and download.");
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, null, successList, targetCount, phenoIds.size());
    }
    
    /**
     * Test downloads from the graphs page.
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testGraphDownloads() throws SolrServerException {
        String testName = "testGraphDownloads";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> phenoIds = new ArrayList(mpService.getAllPhenotypes());
        String target;
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        String message;
        Date start = new Date();
        PageStatus status;
        
        int targetCount = testUtils.getTargetCount(testName, phenoIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " of a total of " + phenoIds.size() + " records.");
        
        // Loop through all phenotypes, testing each tsv and xls download link.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        
        for (String phenoId : phenoIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            int errorCount = 0;
            target = baseUrl + "/phenotypes/" + phenoId;
            System.out.println("pheno[" + i + "] URL: " + target);
            
            PhenoPage phenoPage = new PhenoPage(driver, wait, target, phenoId); // Load the page.
            status = phenoPage.testDownload(baseUrl);                           // Test the download links.
            if (status.hasWarnings()) {
                System.out.println(status.toStringWarningMessages());
            }
            if (status.hasErrors()) {
                System.out.println(status.toStringErrorMessages());
                errorCount++;
            }

            if (errorCount == 0) {
                message = "SUCCESS: MP_TERM_ID " + phenoId + ". URL: " + target;
                successList.add(message);
            } else {
                errorList.add("ERROR: pheno id " + phenoId + ": mismatch between pheno page and download.");
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, null, successList, targetCount, phenoIds.size());
    }
    
}