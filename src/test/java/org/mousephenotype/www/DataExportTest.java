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
import java.util.Arrays;
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
import org.mousephenotype.www.testing.model.GraphPageUnidimensional;
import org.mousephenotype.www.testing.model.PageStatus;
import org.mousephenotype.www.testing.model.PhenoPage;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;
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
    protected PhenotypePipelineDAO phenotypePipelineDAO;
    
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
    
    
    /**
     * 
     * @param phenoIds the source list of pheno ids
     * @param graphType the desired graph type
     * @param count the number of graphs desired
     * @return a list of <code>count</code> <code>GraphLinkDetail</code> objects
     * sourced from <code>phenoIds</code> containing graphs of type <code>graphType</code>.
     */
    private List<PhenoPage.GraphLinkDetail> getGraphs(List<String> phenoIds, WebDriverWait wait, ObservationType graphType, int count) {
        List<PhenoPage.GraphLinkDetail> graphDetails = new ArrayList();
        
        for (String phenoId : phenoIds) {
            String target = baseUrl + "/phenotypes/" + phenoId;
            PhenoPage phenoPage = new PhenoPage(driver, wait, target, phenoId); // Load the page. NOTE: Limit the number of graphs from each page to insure a good mix.
            int graphLimit = 10;
            List<PhenoPage.GraphLinkDetail> graphs = phenoPage.getGraphLinksAndDetails(baseUrl, phenotypePipelineDAO, graphLimit);
            for (PhenoPage.GraphLinkDetail graph : graphs) {
                if (graph.getGraphType() == graphType) {
                    if (graphDetails.size() < count) {
                        graphDetails.add(graph);
                    } else {
                        return graphDetails;
                    }
                }
            }
        }
        
        return graphDetails;
    }
    
    
    // TESTS
    
    
    /**
     * Test downloads from the genes page.
     * @throws SolrServerException 
     */
    @Test
//@Ignore
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
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        
        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each tsv and xls download link.
        int i = 0;
        
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            int errorCount = 0;
            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);
            
            GenePage genePage = new GenePage(driver, wait, target, geneId);     // Load the page.
            status = genePage.validateDownload(baseUrl);                        // Validate the download links.
            
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
        Date start = new Date();
        PageStatus status;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        
        int targetCount = testUtils.getTargetCount(testName, phenoIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " of a total of " + phenoIds.size() + " records.");
        
        // Loop through all phenotypes, testing each tsv and xls download link.
        int i = 0;
        
        for (String phenoId : phenoIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            int errorCount = 0;
            
            target = baseUrl + "/phenotypes/" + phenoId;
            System.out.println("pheno[" + i + "] URL: " + target);
            
            PhenoPage phenoPage = new PhenoPage(driver, wait, target, phenoId); // Load the page.
            
            String message = "";
            
            // If this page doesn't have any associations, it must have images.
            // If it does, log a warning and skip this page.
            // If it doesn't, log an error and skip this page.
            boolean hasAssociations = false;
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='exportIconsDiv'][@data-exporturl]")));
                hasAssociations = true;
            } catch (Exception e) {
                // If there are images, this is not an error, but it is not a useful candidate; try the next mp.
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@id='section']/following-sibling::div[@class='inner']/div[@class='accordion-group']")));
                    System.out.println("\tWARNING: phenoId " + phenoId + ": has no gene association but has images. Skipping this pheno.");
                } catch (Exception e2) {
                    message = "ERROR: phenoId " + phenoId + ": has neither a gene association nor any images. Skipping this pheno.";
                    System.out.println("\t" + message);                         // This pheno page has neither a gene association nor any images. Log an error.
                    errorCount++;
                }
            }
            
            if (hasAssociations) {
                status = phenoPage.validateDownload(baseUrl);                   // Test the download links. Dump the status into a temp variable then merge with existing status.
            
                if (status.hasWarnings()) {
                    System.out.println(status.toStringWarningMessages());
                }
                if (status.hasErrors()) {
                    System.out.println(status.toStringErrorMessages());
                    errorCount++;
                }
            } else {
                targetCount++;                                                  // Add an extra pheno to account for the one without an association.
            }

            if (errorCount == 0) {
                message = "SUCCESS: MP_TERM_ID " + phenoId + ". URL: " + target;
                successList.add(message);
            } else {
                if (message.isEmpty())
                    message = "ERROR: pheno id " + phenoId + ": mismatch between pheno page and download.";
                errorList.add(message);
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, null, successList, targetCount, phenoIds.size());
    }
    
    /**
     * Test unidimensional graph page downloads.
     * @throws SolrServerException 
     */
    @Test
@Ignore
    public void testUnidimensionalGraphDownloads() throws SolrServerException {
        String testName = "testUnidimensionalGraphDownloads";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> phenoIds = new ArrayList(mpService.getAllPhenotypes());
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        PageStatus status;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        
        int targetCount = testUtils.getTargetCount(testName, phenoIds, 10);

        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " unidimensional graphs.");
        
        List<PhenoPage.GraphLinkDetail> graphs = getGraphs(phenoIds, wait, ObservationType.unidimensional, targetCount);

        // Loop through the graphs, testing each one.
        int errorCount = 0;
        for (PhenoPage.GraphLinkDetail graph : graphs) {
            
            System.out.println("\nGRAPH URL: " + graph.getUrl());
            
            GraphPageUnidimensional graphPageUnidimensional = new GraphPageUnidimensional(driver, wait, graph.getUrl(), graph.getPhenoId(), phenotypePipelineDAO);
            status = graphPageUnidimensional.validateDownload(baseUrl);
            
            if (status.hasWarnings()) {
                System.out.println(status.toStringWarningMessages());
            }
            if (status.hasErrors()) {
                System.out.println(status.toStringErrorMessages());
                errorCount++;
            }
        }
        
        if (errorCount == 0) {
            message = "SUCCESS";
            successList.add(message);
        } else {
            errorList.add("ERROR: mismatch between graph page and download.");
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, phenoIds.size());
    }
    
//    /**
//     * Test categorical graph page downloads.
//     * @throws SolrServerException 
//     */
//    @Test
//@Ignore
//    public void testCategoricalGraphDownloads() throws SolrServerException {
//        String testName = "testCategoricalGraphDownloads";
//        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
//        List<String> phenoIds = new ArrayList(mpService.getAllPhenotypes());
//        List<String> errorList = new ArrayList();
//        List<String> successList = new ArrayList();
//        List<String> exceptionList = new ArrayList();
//        String message;
//        Date start = new Date();
//        PageStatus status;
//        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
//        
//        int targetCount = testUtils.getTargetCount(testName, phenoIds, 10);
//        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " categorical graphs.");
//        
//        List<PhenoPage.GraphLinkDetail> graphs = getGraphs(phenoIds, wait, ObservationType.categorical, targetCount);
//        
//        // Loop through the graphs, testing each one.
//        int i = 0;
//        
//        int errorCount = 0;
//        for (PhenoPage.GraphLinkDetail graph : graphs) {
//            GraphPageCategorical graphPageCategorical = new GraphPageCategorical(driver, wait, graph.getUrl(), graph.getPhenoId(), phenotypePipelineDAO);
//            status = graphPageCategorical.validateDownload(baseUrl);
//            
//            if (status.hasWarnings()) {
//                System.out.println(status.toStringWarningMessages());
//            }
//            if (status.hasErrors()) {
//                System.out.println(status.toStringErrorMessages());
//                errorCount++;
//            }
//        }
//        
//        if (errorCount == 0) {
//            message = "SUCCESS";
//            successList.add(message);
//        } else {
//            errorList.add("ERROR: mismatch between graph page and download.");
//        }
//        
//        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, phenoIds.size());
//    }
}