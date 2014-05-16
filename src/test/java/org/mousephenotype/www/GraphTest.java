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
 *
 * This file is intended to contain web tests for graphs - e.g. if there is an
 * IMPC link to a graph (either from a gene page or a phenotype page), there
 * should indeed be a graph present when the link is clicked.
 */

package org.mousephenotype.www;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.www.model.GenePage;
import org.mousephenotype.www.model.GraphParsingStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.generic.util.Tools;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.service.GeneService;
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
public class GraphTest {
    
    public GraphTest() {
    }
   
    @Autowired
    protected GeneService geneService;
    
    @Autowired
    protected String baseUrl;
    
    @Autowired
    protected WebDriver driver;
    
    @Autowired
    protected String seleniumUrl;
    
    @Autowired
    private PhenotypePipelineDAO phenotypePipelineDAO;
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    // These constants define the maximum number of iterations for each given test. -1 means iterate over all.
    public final int MAX_GENE_TEST_PAGE_COUNT = 1;                             // -1 means test all pages.
    private final int TIMEOUT_IN_SECONDS = 4;
    private final int THREAD_WAIT_IN_MILLISECONDS = 1000;
    
    // These variables define the actual number of iterations for each test that uses them.
    // They use default values defined above but may be overridden on the command line using -Dxxx.
    private int max_gene_test_page_count = MAX_GENE_TEST_PAGE_COUNT;
    private int timeoutInSeconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @Before
    public void setup() {
        if (Utils.tryParseInt(System.getProperty("MAX_GENE_TEST_PAGE_COUNT")) != null)
            max_gene_test_page_count = Utils.tryParseInt(System.getProperty("MAX_GENE_TEST_PAGE_COUNT"));
        if (Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeoutInSeconds = Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            thread_wait_in_ms = Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));
        
        printTestEnvironment();
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
    
    private void printTestEnvironment() {
        String browserName = "<Unknown>";
        String version = "<Unknown>";
        String platform = "<Unknown>";
        if (driver instanceof RemoteWebDriver) {
            RemoteWebDriver remoteWebDriver = (RemoteWebDriver)driver;
            browserName = remoteWebDriver.getCapabilities().getBrowserName();
            version = remoteWebDriver.getCapabilities().getVersion();
            platform = remoteWebDriver.getCapabilities().getPlatform().name();
        }
        
        System.out.println("\nTESTING AGAINST " + browserName + " version " + version + " on platform " + platform);
        System.out.println("seleniumUrl: " + seleniumUrl);
    }

    /**
     * Selects a random selection of gene pages by gene ID (count configurable
     * by the system property MAX_GENE_TEST_PAGE_COUNT). For each such gene page
     * this test looks for graph links matching Analysis type 'IMPC' and, for
     * each such link found, clicks the link, validating that each link shows
     * a valid graph page
     * 
     * @throws SolrServerException 
     */
    @Test
@Ignore
    public void testRandomGraphsByGene() throws SolrServerException {
        final String testName = "testRandomGraphsByGene";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> geneIds = geneService.getAllGenes();
        String[] geneIdArray = geneIds.toArray(new String[geneIds.size()]);
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;

        int targetCount = (max_gene_test_page_count >= 0 ? Math.min(max_gene_test_page_count, geneIds.size()) : geneIds.size());
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through the genes, testing each one with an IMPC graph for valid page load.
        Random rand = new Random();
        int max = geneIdArray.length;
        int min = 0;
        int allGenePagesCount = 0;                                  
        int genePagesWithGraphsCount = 0;
        int graphPagesTestedCount = 0;
        while (true) {
            int index = rand.nextInt((max - min) + 1) + min;
            String geneId = geneIdArray[index];
//if (allGenePagesCount == 0) geneId = "MGI:104874";
//if (allGenePagesCount == 1) geneId = "MGI:1924285";
//if (allGenePagesCount == 2) timeseriesGraphUrl = "https://dev.mousephenotype.org/data/charts?accession=MGI:104874&allele_accession=EUROALL:19&parameter_stable_id=ESLIM_004_001_002&zygosity=heterozygote&phenotyping_center=WTSI";
            if ((max_gene_test_page_count != -1) && (allGenePagesCount >= max_gene_test_page_count)) {
                break;
            }
            
            target = baseUrl + "/genes/" + geneId;

            try {
                // Get the gene page.
                driver.get(target);
                (new WebDriverWait(driver, timeoutInSeconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(geneId)));
                
                GenePage genePage = new GenePage(driver, timeoutInSeconds, phenotypePipelineDAO);
                GraphParsingStatus status = genePage.parse();
                
                // Skip over genes with no pheno association. They have no graph links to check.
                if ( ! genePage.hasPhenotypeAssociations()) {
                    try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
                    continue;
                }
                
                allGenePagesCount++;
                
                if (allGenePagesCount < 10) {
                    System.out.println("gene[" + allGenePagesCount + "]: " + geneId);
                }
                
                genePage.validate(status);
                
                if (status.getPass() > 0) {
                    successList.add("SUCCESS: MGI_ACCESSION_ID " + geneId + ". Target URL: " + target);
                }
                if (status.getFail() > 0) {
                    System.out.println(status.getFail() + " graphs failed for gene " + genePage.getUrl() + ":");
                    for (String s : status.getFailMessages()) {
                        System.out.println("\t" + s);
                    }
                    errorList.add("FAIL: MGI_ACCESSION_ID " + geneId + ". Target URL: " + target);
                }
                graphPagesTestedCount += status.getTotal();
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            }  catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                continue;
            }

            genePagesWithGraphsCount++;
            
            if ( ! exceptionList.isEmpty()) {
                System.out.println(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
                for (String s : exceptionList) {
                    System.out.println("\t" + s);
                }
            }
            
            if (allGenePagesCount % 100 == 0)
                System.out.println(dateFormat.format(new Date()) + ": " + allGenePagesCount + " records processed so far.");
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }
        
        System.out.println(dateFormat.format(new Date()) + ": " + testName + " finished.");
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " MGI_ACCESSION_ID records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
        
        stop = new Date();
        System.out.println(dateFormat.format(stop) + ": " + successList.size() + " MGI_ACCESSION_ID records with " + graphPagesTestedCount + " graphs processed successfully in " + Tools.dateDiff(start, stop) + ".");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests the graph links and pages. Limit the test to the first
     * MAX_GENE_TEST_PAGE_COUNT by setting it to the limit you want.
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testGraphPagesForGenesByPhenotypeStatusCompletedAndProductionCentreWTSI() throws SolrServerException {
        final String testName = "testGraphPagesForGenesByPhenotypeStatusCompletedAndProductionCentreWTSI";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> geneIds = geneService.getGenesByPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.PRODUCTION_CENTRE_WTSI);
        String[] geneIdArray = geneIds.toArray(new String[geneIds.size()]);
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;

        int targetCount = (max_gene_test_page_count >= 0 ? Math.min(max_gene_test_page_count, geneIds.size()) : geneIds.size());
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        int max = geneIdArray.length;
        int min = 0;
        int allGenePagesCount = 0;                                  
        int genePagesWithGraphsCount = 0;
        int graphPagesTestedCount = 0;
        int i = 0;
        for (String geneId : geneIds) {
//if (allGenePagesCount == 0) geneId = "MGI:104874";      // undimensional
if (allGenePagesCount == 0) geneId = "MGI:2384936";     // categorical
//if (allGenePagesCount == 2) geneId = "MGI:1924285";     // another unidimensional
//if (allGenePagesCount == 2) timeseriesGraphUrl = "https://dev.mousephenotype.org/data/charts?accession=MGI:104874&allele_accession=EUROALL:19&parameter_stable_id=ESLIM_004_001_002&zygosity=heterozygote&phenotyping_center=WTSI";
            if ((max_gene_test_page_count != -1) && (allGenePagesCount >= max_gene_test_page_count)) {
                break;
            }
            
            target = baseUrl + "/genes/" + geneId;

            try {
                // Get the gene page.
                driver.get(target);
                (new WebDriverWait(driver, timeoutInSeconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(geneId)));
                
                GenePage genePage = new GenePage(driver, timeoutInSeconds, phenotypePipelineDAO);
                GraphParsingStatus status = genePage.parse();
                
                // Skip over genes with no pheno association. They have no graph links to check.
                if ( ! genePage.hasPhenotypeAssociations()) {
                    try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
                    continue;
                }
                
                allGenePagesCount++;
                
                if (allGenePagesCount < 10) {
                    System.out.println("gene[" + allGenePagesCount + "]: " + geneId);
                }
                
                genePage.validate(status);
                
                if (status.getPass() > 0) {
                    successList.add("SUCCESS: MGI_ACCESSION_ID " + geneId + ". Target URL: " + target);
                }
                if (status.getFail() > 0) {
                    System.out.println(status.getFail() + " graphs failed for gene " + genePage.getUrl() + ":");
                    for (String s : status.getFailMessages()) {
                        System.out.println("\t" + s);
                    }
                    errorList.add("FAIL: MGI_ACCESSION_ID " + geneId + ". Target URL: " + target);
                }
                graphPagesTestedCount += status.getTotal();
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            }  catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                continue;
            }

            genePagesWithGraphsCount++;
            
            if ( ! exceptionList.isEmpty()) {
                System.out.println(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
                for (String s : exceptionList) {
                    System.out.println("\t" + s);
                }
            }
            
            if (allGenePagesCount % 100 == 0)
                System.out.println(dateFormat.format(new Date()) + ": " + allGenePagesCount + " records processed so far.");
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }
        
        System.out.println(dateFormat.format(new Date()) + ": " + testName + " finished.");
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " MGI_ACCESSION_ID records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
        
        stop = new Date();
        System.out.println(dateFormat.format(stop) + ": " + successList.size() + " MGI_ACCESSION_ID records with " + graphPagesTestedCount + " graphs processed successfully in " + Tools.dateDiff(start, stop) + ".");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }
    
}

