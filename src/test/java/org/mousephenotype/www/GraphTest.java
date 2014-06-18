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

import edu.emory.mathcs.backport.java.util.Collections;
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
import org.mousephenotype.www.testing.model.GenePhenotypePage;
import org.mousephenotype.www.testing.model.GraphParsingStatus;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
     * Given a test name and a set of gene ids to process, this method loops
     * through the genes, testing each one with an IMPC graph for proper graph
     * page load.
     * 
     * @param testName the test name
     * @param geneIds  the set of gene ids
     */
    private void process(String testName, List<String> geneIds) {
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        System.out.println("Some of these genes might have no phenotype associations; thus the resulting processed row count may be less than expected.");
        System.out.println("Each processed row that is displayed is guaranteed to have phenotype associations.");
        
        // Loop through the genes, testing each one with an IMPC graph for valid page load.
        int graphsWithGenePagesCount = 0;
        int i = 0;
        for (String geneId : geneIds) {
//if (i == 0) geneId = "MGI:104874";    // Akt2
//if (i == 1) geneId = "MGI:3643284";   // Is valid gene for which there is no page.
//if (i == 1) geneId = "MGI:1924285";
//if (i == 2) timeseriesGraphUrl = "https://dev.mousephenotype.org/data/charts?accession=MGI:104874&allele_accession=EUROALL:19&parameter_stable_id=ESLIM_004_001_002&zygosity=heterozygote&phenotyping_center=WTSI";
            if (graphsWithGenePagesCount >= targetCount) {
                break;
            }
            
            target = baseUrl + "/genes/" + geneId;
            i++;
            
            try {
                // Get the gene page.
                driver.get(target);
                (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
                
                GenePhenotypePage page = new GenePhenotypePage(driver, timeout_in_seconds, phenotypePipelineDAO);
                GraphParsingStatus status = page.parse();
                
                // Skip over genes with no pheno association. They have no graph links to check.
                if ( ! page.hasPhenotypeAssociations()) {
//                    System.out.println("gene[" + i + "] URL: " + target + " - SKIPPED (no graphs)");
                    TestUtils.sleep(thread_wait_in_ms);
                    continue;
                }
            
                graphsWithGenePagesCount++;
                System.out.println("gene[" + graphsWithGenePagesCount + "]  (" + i + ") URL: " + target);
                
                page.validate(status);
                
                if (status.getFail() > 0) {
                    System.out.println(status.getFail() + " graphs failed for gene " + page.getUrl());
                    for (String s : status.getFailMessages()) {
                        System.out.println("\t" + s);
                    }
                    errorList.add("FAIL: MGI_ACCESSION_ID " + geneId + ". URL: " + target);
                } else {
                    successList.add("SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target);
                }
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }  catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }

            if ( ! exceptionList.isEmpty()) {
                System.out.println(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
                for (String s : exceptionList) {
                    System.out.println("\t" + s);
                }
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }
    
    
    // TESTS
    
    
    /**
     * Fetches all phenotype IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests the graph links and pages.
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testRandomGraphsByPhenotype() throws SolrServerException {
        final String testName = "testRandomGraphsByPhenotype";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        List<String> phenotypeIds = new ArrayList(genotypePhenotypeService.getAllPhenotypes());
        Collections.shuffle(phenotypeIds);                                      // Randomize the collection.
        
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();

        int targetCount = testUtils.getTargetCount(testName, phenotypeIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + phenotypeIds.size() + " records.");
        
        // Loop through the genes, testing each one with an IMPC graph for valid page load.
        int i = 0;
        for (String phenotypeId : phenotypeIds) {

//if (i == 0) phenotypeId = "MP:0010119";      // undimensional
//if (i == 1) phenotypeId = "MP:0002092";      // categorical
            if (targetCount >= i)
                break;
            i++;
            
            target = baseUrl + "/phenotypes/" + phenotypeId;

            try {
                // 
                // Get the phenotype page.
                driver.get(target);
                (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#phenotypes")));
                
                GenePhenotypePage page = new GenePhenotypePage(driver, timeout_in_seconds, phenotypePipelineDAO);
                GraphParsingStatus status = page.parse();
                
                System.out.println("gene[" + i + "] URL: " + target);
                
                page.validate(status);
                
                if (status.getFail() > 0) {
                    System.out.println(status.getFail() + " graphs failed for gene " + page.getUrl());
                    for (String s : status.getFailMessages()) {
                        System.out.println("\t" + s);
                    }
                    errorList.add("FAIL: MGI_ACCESSION_ID " + phenotypeId + ". URL: " + target);
                } else {
                    successList.add("SUCCESS: MGI_ACCESSION_ID " + phenotypeId + ". URL: " + target);
                }
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + phenotypeId + "(" + target + ") but found none.";
                errorList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }  catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }

            if ( ! exceptionList.isEmpty()) {
                System.out.println(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
                for (String s : exceptionList) {
                    System.out.println("\t" + s);
                }
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, phenotypeIds.size());
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
        
        List<String> geneIds = new ArrayList(geneService.getAllGenes());
        Collections.shuffle(geneIds);
        
        process(testName, geneIds);
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests the graph links and pages.
     * 
     * @throws SolrServerException 
     */
    @Test
// @Ignore
    public void testGraphPagesForGenesByPhenotypeStatusStartedAndProductionCentreWTSI() throws SolrServerException {
        final String testName = "testGraphPagesForGenesByPhenotypeStatusCompletedAndProductionCentreWTSI";

        List<String> geneIds = new ArrayList(geneService.getGenesByPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.PRODUCTION_CENTRE_WTSI));
        
        process(testName, geneIds);
    }
    
}