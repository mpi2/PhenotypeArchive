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
import org.mousephenotype.www.testing.model.GenePhenotypePage;
import org.mousephenotype.www.testing.model.GraphParsingStatus;
import org.mousephenotype.www.testing.model.TestUtils;
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
public class GraphTest {
    
    public GraphTest() {
    }
   
    @Autowired
    protected GeneService geneService;
    
    @Autowired
    protected GenotypePhenotypeService genotypePhenotypeService;
    
    @Autowired
    protected String baseUrl;
    
    @Autowired
    protected WebDriver driver;
    
    @Autowired
    protected String seleniumUrl;
    
    @Autowired
    private PhenotypePipelineDAO phenotypePipelineDAO;
    
    @Autowired
    protected TestUtils testUtils;
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    private final int TIMEOUT_IN_SECONDS = 4;
    private final int THREAD_WAIT_IN_MILLISECONDS = 1000;
    
    private int timeoutInSeconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @Before
    public void setup() {
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
//@Ignore
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

        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through the genes, testing each one with an IMPC graph for valid page load.
        Random rand = new Random();
        int max = geneIdArray.length;
        int min = 0;
        int allGenePagesCount = 0;
        int graphPagesTestedCount = 0;
        
        int i = 0;
        while (true) {
            int index = rand.nextInt((max - min) + 1) + min;
            String geneId = geneIdArray[index];
//if (allGenePagesCount == 0) geneId = "MGI:104874";
//if (allGenePagesCount == 1) geneId = "MGI:1924285";
//if (allGenePagesCount == 2) timeseriesGraphUrl = "https://dev.mousephenotype.org/data/charts?accession=MGI:104874&allele_accession=EUROALL:19&parameter_stable_id=ESLIM_004_001_002&zygosity=heterozygote&phenotyping_center=WTSI";
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;

            try {
                // Get the gene page.
                driver.get(target);
                (new WebDriverWait(driver, timeoutInSeconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(geneId)));
                
                GenePhenotypePage page = new GenePhenotypePage(driver, timeoutInSeconds, phenotypePipelineDAO);
                GraphParsingStatus status = page.parse();
                
                // Skip over genes with no pheno association. They have no graph links to check.
                if ( ! page.hasPhenotypeAssociations()) {
                    try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
                    continue;
                }
                
                allGenePagesCount++;
                
                if (allGenePagesCount < 10) {
                    System.out.println("gene[" + allGenePagesCount + "]: " + geneId);
                }
                
                page.validate(status);
                
                if (status.getPass() > 0) {
                    successList.add("SUCCESS: MGI_ACCESSION_ID " + geneId + ". Target URL: " + target);
                }
                if (status.getFail() > 0) {
                    System.out.println(status.getFail() + " graphs failed for gene " + page.getUrl());
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
     * Fetches all phenotype IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests the graph links and pages.
     * 
     * @throws SolrServerException 
     */
    @Test
@Ignore
    public void testRandomGraphsByPhenotype() throws SolrServerException {
        final String testName = "testRandomGraphsByPhenotype";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> phenotypeIds = genotypePhenotypeService.getAllPhenotypes();
        String[] phenotypeIdArray = phenotypeIds.toArray(new String[phenotypeIds.size()]);
        String target;
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;
        
        int targetCount = testUtils.getTargetCount(testName, phenotypeIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + phenotypeIds.size() + " records.");
        
        // Loop through the genes, testing each one with an IMPC graph for valid page load.
        Random rand = new Random();
        int max = phenotypeIds.size();
        int min = 0;
        int allPagesCount = 0;
        int graphPagesTestedCount = 0;
        
        int i = 0;
        while (true) {
            int index = rand.nextInt((max - min) + 1) + min;
            String phenotypeId = phenotypeIdArray[index];
//if (allPagesCount == 0) phenotypeId = "MP:0010119";      // undimensional
//if (allPagesCount == 1) phenotypeId = "MP:0002092";      // categorical
//if (allGenePagesCount == 0) geneId = "MGI:104874";
//if (allGenePagesCount == 1) geneId = "MGI:1924285";
//if (allGenePagesCount == 2) timeseriesGraphUrl = "https://dev.mousephenotype.org/data/charts?accession=MGI:104874&allele_accession=EUROALL:19&parameter_stable_id=ESLIM_004_001_002&zygosity=heterozygote&phenotyping_center=WTSI";
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/phenotypes/" + phenotypeId;

            try {
                // Get the page.
                driver.get(target);
                (new WebDriverWait(driver, timeoutInSeconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(phenotypeId)));
                
                GenePhenotypePage page = new GenePhenotypePage(driver, timeoutInSeconds, phenotypePipelineDAO);
                GraphParsingStatus status = page.parse();
                
                // Skip over pages with no pheno association. They have no graph links to check.
                if ( ! page.hasPhenotypeAssociations()) {
                    try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
                    continue;
                }
                
                allPagesCount++;
                
                if (allPagesCount < 10) {
                    System.out.println("phenotype page[" + allPagesCount + "]: " + phenotypeId);
                }
                
                page.validate(status);
                
                if (status.getPass() > 0) {
                    successList.add("SUCCESS: MGI_ACCESSION_ID " + phenotypeId + ". Target URL: " + target);
                }
                if (status.getFail() > 0) {
                    System.out.println(status.getFail() + " graphs failed for phenotype page " + page.getUrl());
                    for (String s : status.getFailMessages()) {
                        System.out.println("\t" + s);
                    }
                    errorList.add("FAIL: MGI_ACCESSION_ID " + phenotypeId + ". Target URL: " + target);
                }
                graphPagesTestedCount += status.getTotal();
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + phenotypeId + "(" + target + ") but found none.";
                errorList.add(message);
            }  catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                continue;
            }

            if ( ! exceptionList.isEmpty()) {
                System.out.println(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
                for (String s : exceptionList) {
                    System.out.println("\t" + s);
                }
            }
            
            if (allPagesCount % 100 == 0)
                System.out.println(dateFormat.format(new Date()) + ": " + allPagesCount + " records processed so far.");
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
     * core and tests the graph links and pages.
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testGraphPagesForGenesByPhenotypeStatusCompletedAndProductionCentreWTSI() throws SolrServerException {
        final String testName = "testGraphPagesForGenesByPhenotypeStatusCompletedAndProductionCentreWTSI";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> geneIds = geneService.getGenesByPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.PRODUCTION_CENTRE_WTSI);
        String target;
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;

        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        int allPagesCount = 0;
        int graphPagesTestedCount = 0;
        int i = 0;
        for (String geneId : geneIds) {
//if (allPagesCount == 0) geneId = "MGI:104874";      // unidimensional
//if (allPagesCount == 0) geneId = "MGI:3028035";     // unidimensional
//if (allPagesCount == 1) geneId = "MGI:2384936";     // categorical
//if (allPagesCount == 2) geneId = "MGI:1924285";     // another unidimensional
//if (allPagesCount == 3) timeseriesGraphUrl = "https://dev.mousephenotype.org/data/charts?accession=MGI:104874&allele_accession=EUROALL:19&parameter_stable_id=ESLIM_004_001_002&zygosity=heterozygote&phenotyping_center=WTSI";
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;

            try {
                // Get the page.
                driver.get(target);
                (new WebDriverWait(driver, timeoutInSeconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(geneId)));
                
                GenePhenotypePage page = new GenePhenotypePage(driver, timeoutInSeconds, phenotypePipelineDAO);
                GraphParsingStatus status = page.parse();
                
                // Skip over genes with no pheno association. They have no graph links to check.
                if ( ! page.hasPhenotypeAssociations()) {
                    try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
                    continue;
                }
                
                allPagesCount++;
                
                if (allPagesCount < 10) {
                    System.out.println("page[" + allPagesCount + "]: " + geneId);
                }
                
                page.validate(status);
                
                if (status.getPass() > 0) {
                    successList.add("SUCCESS: MGI_ACCESSION_ID " + geneId + ". Target URL: " + target);
                }
                if (status.getFail() > 0) {
                    System.out.println(status.getFail() + " graphs failed for gene page " + page.getUrl());
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

            if ( ! exceptionList.isEmpty()) {
                System.out.println(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
                for (String s : exceptionList) {
                    System.out.println("\t" + s);
                }
            }
            
            if (allPagesCount % 100 == 0)
                System.out.println(dateFormat.format(new Date()) + ": " + allPagesCount + " records processed so far.");
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

