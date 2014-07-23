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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
 * 
 * testAkt2() - @author Gautier Koscielny
 * Selenium test for graph query coverage ensuring each graph display works for 
 * any given gene accession/parameter/zygosity from the Solr core
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class GenePageTest {
    
    @Autowired
    protected GeneService geneService;
    
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
    
    
    private void geneIdsTest(String testName) throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList(geneService.getAllGenes());
        
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();

        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
            
        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);
            
            try {
                driver.get(target);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
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

            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }
    
    private void tick(String phenoStatus, String prodCentre, String phenoCentre) {
        // If no parameters were specified, set target to the default search page.
        String target = baseUrl + "/search";
        String fields = "";
        if ( ! ((phenoStatus == null) && (prodCentre == null) && (phenoCentre == null))) {
            target += "#fq=";
            if (phenoStatus != null) {
                switch(phenoStatus) {
                    case "Complete":
                        fields += "(latest_phenotype_status:\"Phenotyping Complete\")";
                        break;

                    case "Started":
                        fields += "(latest_phenotype_status:\"Phenotyping Started\")";
                        break;

                    case "Attempt Registered":
                        fields += "(latest_phenotype_status:\"Phenotype Attempt Registered\")";
                        break;
                        
                    default:
                        throw new RuntimeException("tick(): unknown phenotyping status '" + phenoStatus + "'.");
                }
            }
            
            if (prodCentre != null) {
                if ( ! fields.isEmpty()) {
                    fields += " AND ";
                fields += "(latest_production_centre:\"" + prodCentre + "\")";
                }
            }
            
            if (phenoCentre != null) {
                if ( ! fields.isEmpty()) {
                    fields += " AND ";
                fields += "(latest_phenotyping_centre:\"" + phenoCentre + "\")";
                }
            }
            
            target += fields + "&facet=gene";
        }
            
        driver.get(target);
                
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        String xpathSelector = "//span[@id=\"resultCount\"]/a";
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathSelector)));
    }
    
    /**
     * Given that the current page is the gene search page, returns the number
     * sandwiched between the 'Found' and 'genes' terms; e.g., given the string
     * 'Found 5 genes', returns the number 5. Returns 0 if there is no number
     * or no such formatted string.
     * @return gene count if found; 0 otherwise
     */
    private int getGeneCount() {
        WebElement element = driver.findElement(By.xpath("//div[@id=\"resultMsg\"]/span[@id=\"resultCount\"]/a"));
        
        String s = element.getText().replace(" genes", "");
        Integer i = Utils.tryParseInt(s);
        return (i == null ? 0 : i);
    }
    
    
    // TESTS
    
    
    /**
     * Finds all MGI_ACCESSION_IDs in the genotype-phenotype
     * core that do not start with 'MGI'.
     * 
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testForBadGeneIds() throws SolrServerException {
        String testName = "testForBadGeneIds";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList(geneService.getAllNonConformingGenes());
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();

        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all non-conforming genes, testing each one for valid page load (they will likely fail).
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);

            try {
                driver.get(target);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                continue;
            }

            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure there is a page for each.
     * 
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testPageForGeneIds() throws SolrServerException {
        String testName = "testPageForGeneIds";
        geneIdsTest(testName);
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the gene core with 
     * phenotype status 'started' and phenotype centre 'WTSI' and tests to
     * make sure there is a page for each.
     * 
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testPageForGenesByLatestPhenotypeStatusStartedAndPhenotypeCentreWTSI() throws SolrServerException {
        String testName = "testPageForGenesByLatestPhenotypeStatusStartedAndPhenotypeCentreWTSI";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndPhenotypeCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.CENTRE_WTSI));
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        
        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Started", null, GeneService.GeneFieldValue.CENTRE_WTSI);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());
        
        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);

            // Wait for page to load.
            try {
                driver.get(target);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }
            
            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the gene core with 
     * phenotype status 'started' and production centre 'WTSI' and tests to
     * make sure there is a page for each.
     * 
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testPageForGenesByLatestPhenotypeStatusStartedAndProductionCentreWTSI() throws SolrServerException {
        String testName = "testPageForGenesByLatestPhenotypeStatusStartedAndProductionCentreWTSI";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.CENTRE_WTSI));
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        
        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");

        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Started", GeneService.GeneFieldValue.CENTRE_WTSI, null);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());
        
        // Loop through all genes, testing each one for valid page load.
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        int i = 0;
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);

            // Wait for page to load.
            try {
                driver.get(target);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }
            
            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }
    
    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the gene core with 
     * phenotype status 'complete' and phenotype centre 'WTSI' and tests to
     * make sure there is a page for each.
     * 
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testPageForGenesByLatestPhenotypeStatusCompleteAndPhenotypeCentreWTSI() throws SolrServerException {
        String testName = "testPageForGenesByLatestPhenotypeStatusCompleteAndPhenotypeCentreWTSI";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndPhenotypeCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_COMPLETE, GeneService.GeneFieldValue.CENTRE_WTSI));
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        
        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Complete", null, GeneService.GeneFieldValue.CENTRE_WTSI);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());
        
        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " complete. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);

            // Wait for page to load.
            try {
                driver.get(target);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }
            
            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the gene core with 
     * phenotype status 'complete' and production centre 'WTSI' and tests to
     * make sure there is a page for each.
     * 
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testPageForGenesByLatestPhenotypeStatusCompleteAndProductionCentreWTSI() throws SolrServerException {
        String testName = "testPageForGenesByLatestPhenotypeStatusCompleteAndProductionCentreWTSI";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_COMPLETE, GeneService.GeneFieldValue.CENTRE_WTSI));
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        
        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " complete. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Complete", GeneService.GeneFieldValue.CENTRE_WTSI, null);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());
        
        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);

            // Wait for page to load.
            try {
                driver.get(target);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }
            
            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }
    
    /**
     * Tests that a sensible page is returned for an invalid gene id.
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testInvalidGeneId() throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        String testName = "testInvalidGeneId";
        String target = "";
        int targetCount = 1;
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        String geneId = "junkBadGene";
        final String EXPECTED_ERROR_MESSAGE = "Oops! junkBadGene is not a valid MGI gene identifier.";
        
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of 1 records.");
        
        boolean found = false;
        target = baseUrl + "/genes/" + geneId;
        System.out.println("URL: " + target);
            
        try {
            driver.get(target);
            List<WebElement> geneLinks = (new WebDriverWait(driver, timeout_in_seconds))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.node h1")));
            
            if (geneLinks == null) {
                message = "Expected error page for MP_TERM_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            }

            for (WebElement element : geneLinks) {
                if (element.getText().compareTo(EXPECTED_ERROR_MESSAGE) == 0) {
                    found = true;
                    break;
                }
            }

            if ( ! found) {
                message = "Expected error page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            }
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            exceptionList.add(message);
        }
        
        message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
        successList.add(message);
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, 1);
    }
    
    @Test
//@Ignore
    public void testAkt2() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        String testName = "testAkt2";
        int targetCount = 1;
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of 1 records.");
        
        String geneId = "MGI:104874";
        String target = baseUrl + "/genes/" + geneId;
        System.out.println("URL: " + target);
        
        // Wait for page to load.
        try {
            driver.get(target);
            (new WebDriverWait(driver, timeout_in_seconds))
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
        } catch (NoSuchElementException | TimeoutException te) {
            message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
            errorList.add(message);
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            exceptionList.add(message);
        }
        
        // Title
        assertTrue("Expected top id title 'Akt2'", driver.findElement(By.id("top")).getText().contains("Akt2"));
        
        // Section titles (e.g. 'Gene: Akt2', 'Phenotype associations for Akt2', 'Pre-QC phenotype heatmap', etc.)
        List<WebElement> sections = driver.findElements(By.className("title"));
        // ... count
        if (sections.size() != 6) {
            System.out.println("section titles size=" + sections.size());
            message = "Expected 6 section titles but found " + sections.size() + ".";
            errorList.add(message);
        }
        // ... Section titles
        String[] sectionTitlesArray = {"Gene: Akt2", "Phenotype associations for Akt2", "Phenotype Associated Images", "Expression", "Order Mouse and ES Cells", "Pre-QC phenotype heatmap"};
        List<String> sectionTitles = new ArrayList(Arrays.asList(sectionTitlesArray));
        for (WebElement webElement : sections) {
            String text = webElement.getText();
            if ( ! sectionTitles.contains(text)) {
                message = "Expected section named '" + text + "' but wasn't found.";
                errorList.add(message);
            }
        }
        
        // Buttons
        List<WebElement> buttons = driver.findElements(By.className("btn"));
        // ... count
        if (buttons.size() != 3) {
            message = "Expected 3 buttons but found " + buttons.size();
            errorList.add(message);
        }
        // ... Button text
        String[] buttonTitlesArray = { "Login to register interest", "Order", "KOMP" };
        List<String> buttonTitles = new ArrayList(Arrays.asList(buttonTitlesArray));
        for (WebElement webElement : buttons) {
            String buttonText = webElement.getText();
            if ( ! buttonTitles.contains(buttonText)) {
                message = "Expected button with title '" + buttonText + "' but none was found.";
                errorList.add(message);
            }
        }
        
        // Phenotype association icons
        WebElement abnormalities = driver.findElement(By.className("abnormalities"));
        List<WebElement> anchors = abnormalities.findElements(By.className("filterTrigger"));
        // ... count
        if (anchors.size() != 5) {
            message = "Expected 5 'abnormalities' icons but found " + anchors.size();
            errorList.add(message);
        }
        // ids
        String[] iconIdsArray = { "phenIconsBox_skeleton phenotype"
                                , "phenIconsBox_behavior/neurological phenotype or nervous system phenotype"
                                , "phenIconsBox_homeostasis/metabolism phenotype or adipose tissue phenotype"
                                , "phenIconsBox_immune system phenotype or hematopoietic system phenotype"
                                , "phenIconsBox_growth/size/body phenotype" };
        List<String> iconIds = new ArrayList(Arrays.asList(iconIdsArray));
        for (WebElement webElement : anchors) {
            String id = webElement.getAttribute("id");
            if ( ! iconIds.contains(id)) {
                message = "Expected abnormalities icon with id '" + id + "' but none was found.";
                errorList.add(message);
            }
        }
        
        // "Top level MP: All" drop-down
        Select selectTopLevel = new Select(driver.findElement(By.id("top_level_mp_term_name")));
        // ... count
        if (selectTopLevel.getOptions().size() != 5) {
            message = "Expected 5 \"Top level MP: All\" options but found " + selectTopLevel.getOptions().size() + ".";
            errorList.add(message);
        }
        // ... values
        String[] topLevelValuesArray = { "behavior/neurological phenotype"
                                       , "growth/size/body phenotype"
                                       , "hematopoietic system phenotype"
                                       , "homeostasis/metabolism phenotype"
                                       , "skeleton phenotype"  };
        List<String> topLevelValues = new ArrayList(Arrays.asList(topLevelValuesArray));
        for (WebElement option : selectTopLevel.getOptions()) {
            if ( ! topLevelValues.contains(option.getAttribute("value"))) {
                message = "Expected Top level MP: All option \"" + option.getAttribute("value") + "\" but none was found.";
                errorList.add(message);
            }
        }

        // Phenotype Associated Images and Expression sections
        List<WebElement> imagesAndExpression = driver.findElements(By.className("accordion-heading"));
        // ... count
        if (imagesAndExpression.size() != 11) {
            message = "Expected 2 \"Phenotype Associated Images\" values and 9 \"Expression\" values (11 total) but found " + imagesAndExpression.size();
            errorList.add(message);
        }
        // ... values
        String[] phenotypeAssociatedImagesArray = {
                  "Xray (167)"
                , "Tail Epidermis Wholemount (5)"
                , "Musculoskeletal System (2)"
                , "Nervous System (2)"
                , "Adipose Tissue (1)"
                , "Cardiovascular System (1)"
                , "Digestive System (1)"
                , "Integumental System (1)"
                , "Renal/urinary System (1)"
                , "Reproductive System (1)"
                , "Respiratory System (1)"};
        List<String> accHeaderStrings = new ArrayList(Arrays.asList(phenotypeAssociatedImagesArray));
        for (WebElement webElement : imagesAndExpression) {
            String text = webElement.getText();
            assertTrue("Expected Phenotype Associated Images / Expression value \"" + text + "\" but none was found", accHeaderStrings.contains(text));
        }

        //test that the order mouse and es cells content from viveks team exists on the page
        WebElement orderAlleleDiv = driver.findElement(By.id("allele"));//this div is in the ebi jsp which should be populated but without the ajax call success will be empty.
        assertTrue(orderAlleleDiv.getText().length() > 100);//check there is some content in the panel div
        
        if (errorList.isEmpty()) {
            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
        } else {
            
        }
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, 1);
    }
    

}
    