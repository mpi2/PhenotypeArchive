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
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
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
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    // These constants define the default number of iterations for each that uses them. -1 means iterate over all.
    private final int MAX_MGI_LINK_CHECK_COUNT = 5;                             // -1 means test all links.
    private final int MAX_PHENOTYPE_TEST_PAGE_COUNT = 10;                       // -1 means test all pages.
    private final int TIMEOUT_IN_SECONDS = 4;
    private final int THREAD_WAIT_IN_MILLISECONDS = 1000;

    // These variables define the actual number of iterations for each test that uses them.
    // They use default values defined above but may be overridden on the command line using -Dxxx.
    private int max_mgi_link_check_count = MAX_MGI_LINK_CHECK_COUNT;
    private int max_phenotype_test_page_count = MAX_PHENOTYPE_TEST_PAGE_COUNT;
    private int timeout_in_seconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;
    
    @Before
    public void setup() {
        if (Utils.tryParseInt(System.getProperty("MAX_MGI_LINK_CHECK_COUNT")) != null)
            max_mgi_link_check_count = Utils.tryParseInt(System.getProperty("MAX_MGI_LINK_CHECK_COUNT"));
        if (Utils.tryParseInt(System.getProperty("MAX_PHENOTYPE_TEST_PAGE_COUNT")) != null)
            max_phenotype_test_page_count = Utils.tryParseInt(System.getProperty("MAX_PHENOTYPE_TEST_PAGE_COUNT"));
        if (Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeout_in_seconds = Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
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
     * Checks the MGI links for the first MAX_MGI_LINK_CHECK_COUNT phenotype ids
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testMGILinksAreValid() throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> phenotypeIds = genotypePhenotypeService.getAllPhenotypes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;
        
        int targetCount = (max_mgi_link_check_count >= 0 ? Math.min(max_mgi_link_check_count, phenotypeIds.size()) : phenotypeIds.size());
        System.out.println(dateFormat.format(start) + ": testMGILinksAreValid started. Expecting to process " + targetCount + " of a total of " + phenotypeIds.size() + " records.");
        
        // Loop through first MAX_MGI_LINK_CHECK_COUNT phenotype MGI links, testing each one for valid page load.
        int i = 0;
        for (String phenotypeId : phenotypeIds) {
            if ((max_mgi_link_check_count != -1) && (i++ >= max_mgi_link_check_count)) {
                break;
            }
            
            WebElement phenotypeLink = null;
            boolean found = false;
            target = baseUrl + "/phenotypes/" + phenotypeId;
            try {
                driver.get(target);
                phenotypeLink = (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(phenotypeId)));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
                errorList.add(message);
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
                message = "SUCCESS: MGI link OK for " + phenotypeId + ". Target URL: " + target;
                successList.add(message);
            }
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }
          
        System.out.println(dateFormat.format(new Date()) + ": testMGILinksAreValid finished.");
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " MGI links failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(exceptionList.size() + " MGI links caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
        
        stop = new Date();
        System.out.println(dateFormat.format(stop) + ": " + successList.size() + " MGI links processed successfully in " + Tools.dateDiff(start, stop) + ".");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }
    
    /**
     * Fetches all phenotype IDs from the genotype-phenotype core and
     * tests to make sure there is a page for each.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testPageForEveryMPTermId() throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> phenotypeIds = genotypePhenotypeService.getAllPhenotypes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;
        
        int targetCount = (max_phenotype_test_page_count >= 0 ? Math.min(max_phenotype_test_page_count, phenotypeIds.size()) : phenotypeIds.size());
        System.out.println(dateFormat.format(start) + ": testPageForEveryMPTermId started. Expecting to process " + targetCount + " of a total of " + phenotypeIds.size() + " records.");
        
        // Loop through the phenotypes, testing each one for valid page load.
        int i = 0;
        for (String phenotypeId : phenotypeIds) {
            if ((max_phenotype_test_page_count != -1) && (i++ >= max_phenotype_test_page_count)) {
                break;
            }

            WebElement phenotypeLink = null;
            target = baseUrl + "/phenotypes/" + phenotypeId;
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
                message = "SUCCESS: MP_TERM_ID " + phenotypeId + ". Target URL: " + target;
                successList.add(message);
            }
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }
        
        System.out.println(dateFormat.format(new Date()) + ": testPageForEveryMPTermId finished.");
        
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
        
        stop = new Date();
        System.out.println(dateFormat.format(stop) + ": " + successList.size() + " MP_TERM_ID records processed successfully in " + Tools.dateDiff(start, stop) + ".");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }
    
    /**
     * Fetches all top-level phenotype IDs from the genotype-phenotype core and
     * tests to make sure there is a page for each.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testPageForEveryTopLevelMPTermId() throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> phenotypeIds = genotypePhenotypeService.getAllTopLevelPhenotypes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;

        int targetCount = (max_phenotype_test_page_count >= 0 ? Math.min(max_phenotype_test_page_count, phenotypeIds.size()) : phenotypeIds.size());
        System.out.println(dateFormat.format(start) + ": testPageForEveryTopLevelMPTermId started. Expecting to process " + targetCount + " of a total of " + phenotypeIds.size() + " records.");
        
        // Loop through all phenotypes, testing each one for valid page load.
        int i = 0;
        for (String phenotypeId : phenotypeIds) {
            if ((max_phenotype_test_page_count != -1) && (i++ >= max_phenotype_test_page_count)) {
                break;
            }

            WebElement topLevelPhenotypeLink = null;
            target = baseUrl + "/phenotypes/" + phenotypeId;
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
            message = "SUCCESS: TOP_LEVEL_MP_TERM_ID " + phenotypeId + ". Target URL: " + target;
                successList.add(message);
            }
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }
        
        System.out.println(dateFormat.format(new Date()) + ": testPageForEveryTopLevelMPTermId finished.");
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " TOP_LEVEL_MP_TERM_ID records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(exceptionList.size() + " TOP_LEVEL_MP_TERM_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
        
        stop = new Date();
        System.out.println(dateFormat.format(stop) + ": " + successList.size() + " TOP_LEVEL_MP_TERM_ID records processed successfully in " + Tools.dateDiff(start, stop) + ".");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }
    
    /**
     * Tests that a sensible page is returned for an invalid phenotype id.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testInvalidMpTermId() throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;
        String phenotypeId = "junkBadPhenotype";
        final String EXPECTED_ERROR_MESSAGE = "Oops! junkBadPhenotype is not a valid mammalian phenotype identifier.";
        
        System.out.println(dateFormat.format(start) + ": testInvalidMpTermId started. Expecting to process 1 of a total of 1 records.");
        
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
        System.out.println(dateFormat.format(stop) + ": testInvalidMpTermId finished.");
        
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