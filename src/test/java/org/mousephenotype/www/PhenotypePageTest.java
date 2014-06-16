 /**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
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
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.generic.util.Tools;
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
public class PhenotypePageTest {
    
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
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    private final int TIMEOUT_IN_SECONDS = 4;
    private final int THREAD_WAIT_IN_MILLISECONDS = 1000;

    private int timeout_in_seconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;
    
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
    
    /**
     * Checks the MGI links for the first MAX_MGI_LINK_CHECK_COUNT phenotype ids
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testMGILinksAreValid() throws SolrServerException {
        String testName = "testMGILinksAreValid";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> phenotypeIds = genotypePhenotypeService.getAllPhenotypes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        
        int targetCount = testUtils.getTargetCount(testName, phenotypeIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + phenotypeIds.size() + " records.");
        
        // Loop through first targetCount phenotype MGI links, testing each one for valid page load.
        int i = 0;
        for (String phenotypeId : phenotypeIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            WebElement phenotypeLink;
            boolean found = false;
            
            target = baseUrl + "/phenotypes/" + phenotypeId;
            System.out.println("phenotype[" + i + "] URL: " + target);
            
            try {
                driver.get(target);
                phenotypeLink = (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(phenotypeId)));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
                errorList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }
            try {
                phenotypeLink.click();
                String idString = "[" + phenotypeId + "]";
                found = driver.findElement(By.cssSelector("div[id='templateBodyInsert']")).getText().contains(idString);
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
            }
            
            if ( ! found) {
                message = "div id 'templateBodyInsert' not found.";
                errorList.add(message);
            } else {
                message = "SUCCESS: MGI link OK for " + phenotypeId + ". URL: " + target;
                successList.add(message);
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, phenotypeIds.size());
    }
    
    /**
     * Fetches all phenotype IDs from the genotype-phenotype core and
     * tests to make sure there is a page for each.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testPageForEveryMPTermId() throws SolrServerException {
        String testName = "testPageForEveryMPTermId";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> phenotypeIds = genotypePhenotypeService.getAllPhenotypes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        
        int targetCount = testUtils.getTargetCount(testName, phenotypeIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + phenotypeIds.size() + " records.");
        
        // Loop through the phenotypes, testing each one for valid page load.
        int i = 0;
        for (String phenotypeId : phenotypeIds) {
            if (i >= targetCount) {
                break;
            }
            i++;

            WebElement phenotypeLink = null;
            
            target = baseUrl + "/phenotypes/" + phenotypeId;
            System.out.println("phenotype[" + i + "] URL: " + target);
            
            try {
                driver.get(target);
                phenotypeLink = (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(phenotypeId)));
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
            }
            
            if (phenotypeLink == null) {
                message = "Expected page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
                errorList.add(message);
            } else {
                message = "SUCCESS: MP_TERM_ID " + phenotypeId + ". URL: " + target;
                successList.add(message);
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, phenotypeIds.size());
    }
    
    /**
     * Fetches all top-level phenotype IDs from the genotype-phenotype core and
     * tests to make sure there is a page for each.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testPageForEveryTopLevelMPTermId() throws SolrServerException {
        String testName = "testPageForEveryTopLevelMPTermId";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> phenotypeIds = genotypePhenotypeService.getAllTopLevelPhenotypes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();

        int targetCount = testUtils.getTargetCount(testName, phenotypeIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + phenotypeIds.size() + " records.");
        
        // Loop through all phenotypes, testing each one for valid page load.
        int i = 0;
        for (String phenotypeId : phenotypeIds) {
            if (i >= targetCount) {
                break;
            }
            i++;

            WebElement topLevelPhenotypeLink = null;
            
            target = baseUrl + "/phenotypes/" + phenotypeId;
            System.out.println("phenotype[" + i + "] URL: " + target);
            
            try {
                driver.get(target);
                topLevelPhenotypeLink = (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(phenotypeId)));
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
            }
            
            if (topLevelPhenotypeLink == null) {
                message = "Expected page for TOP_LEVEL_MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
                    errorList.add(message);
            } else {
                message = "SUCCESS: TOP_LEVEL_MP_TERM_ID " + phenotypeId + ". URL: " + target;
                successList.add(message);
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, phenotypeIds.size());
    }
    
    /**
     * Fetches all intermediate-level phenotype IDs from the genotype-phenotype core and
     * tests to make sure there is a page for each.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testPageForEveryIntermediateLevelMPTermId() throws SolrServerException {
        String testName = "testPageForEveryIntermediateLevelMPTermId";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> phenotypeIds = genotypePhenotypeService.getAllIntermediateLevelPhenotypes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();

        int targetCount = testUtils.getTargetCount(testName, phenotypeIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + phenotypeIds.size() + " records.");
        
        // Loop through all phenotypes, testing each one for valid page load.
        int i = 0;
        for (String phenotypeId : phenotypeIds) {
            if (i >= targetCount) {
                break;
            }
            i++;

            WebElement intermediateLevelPhenotypeLink = null;
            
            target = baseUrl + "/phenotypes/" + phenotypeId;
            System.out.println("phenotype[" + i + "] URL: " + target);
            
            try {
                driver.get(target);
                intermediateLevelPhenotypeLink = (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(phenotypeId)));
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
            }
            
            if (intermediateLevelPhenotypeLink == null) {
                message = "Expected page for INTERMEDIATE_MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
                    errorList.add(message);
            } else {
                message = "SUCCESS: INTERMEDIATE_MP_TERM_ID " + phenotypeId + ". URL: " + target;
                successList.add(message);
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, phenotypeIds.size());
    }
    
    /**
     * Tests that a sensible page is returned for an invalid phenotype id.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testInvalidMpTermId() throws SolrServerException {
        String testName = "testInvalidMpTermId";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;
        String phenotypeId = "junkBadPhenotype";
        final String EXPECTED_ERROR_MESSAGE = "Oops! junkBadPhenotype is not a valid mammalian phenotype identifier.";
        
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process 1 of a total of 1 records.");
        
        boolean found = false;
        target = baseUrl + "/phenotypes/" + phenotypeId;
        
        try {
            driver.get(target);
            List<WebElement> phenotypeLinks = (new WebDriverWait(driver, timeout_in_seconds))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.node h1")));
            if (phenotypeLinks == null) {
                message = "Expected error page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
                errorList.add(message);
            }
            for (WebElement div : phenotypeLinks) {
                if (div.getText().equals(EXPECTED_ERROR_MESSAGE)) {
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            message = "Timeout: Expected error page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
            errorList.add(message);
        }
         
        if ( ! found) {
            message = "Expected error page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
            errorList.add(message);
        }
        
        stop = new Date();
        System.out.println(dateFormat.format(stop) + ": " + testName + " finished.");
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " MP_TERM_ID records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(exceptionList.size() + " MP_TERM_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
        
        System.out.println(dateFormat.format(new Date()) + ": 1 invalid MP_TERM_ID record processed successfully in " + Tools.dateDiff(start, stop) + ".");
    }

}