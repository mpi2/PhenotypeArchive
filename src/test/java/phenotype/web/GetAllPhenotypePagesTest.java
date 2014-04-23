/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

package phenotype.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService;

/**
 *
 * @author mrelac
 * 
 * Requirements for running these tests:
 * 1. Must have 'globalConfiguration' bean defined, typically found in WEB-INF/app-config.xml. This allows us to
 *    pick up Spring without having to use @RunWith(SpringJUnit4ClassRunner.class). Within that bean, the following
 *    properties must be defined:
 *    - 
 * 2. Must have the following properties defined in both the test and source copies of app-config.xml and appConfig.properties:
 * 2a.   baseUrl (the phenotype archive web applicatin instance. For DEV, it's http://dev.mousephenotype.org/data)
 * 2b.   seleniumUrl (the selenium WebServer server. Typically http://mi-selenium-win.windows.ebi.ac.uk:4444/wd/hub, but can be easily changed)
 * 2c.   seleniumDrivers (the drivers you want the tests run against - e.g. chrome, firefox, iexplore, safari (case insensitive))
 * These properties must be defined in both the source and test app-config.xml and appConfig.properties files
 * (although only the test version's values are used).
 */

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class GetAllPhenotypePagesTest {
    // These constants define the maximum number of iterations for each given test. -1 means iterate over all.
    
    @Autowired
    protected GenotypePhenotypeService genotypePhenotypeService;
    
    @Autowired
    protected String baseUrl;
    
    @Autowired
    protected WebDriver driver;
    
    private static WebDriver staticDriver = null;
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    public final int MAX_MGI_LINK_CHECK_COUNT = 5;                              // -1 means test all links.
    public final int MAX_PHENOTYPE_TEST_PAGE_COUNT = -1;                        // -1 means test all pages.

    @Before
    public void setup() {
        staticDriver = driver;
        printTestEnvironment();
    }

    @After
    public void teardown() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
        if (staticDriver != null) {
            staticDriver.close();
        }
    }
    
    // PRIVATE METHODS
    
    private static void printTestEnvironment() {
        String browserName = "<Unknown>";
        String version = "<Unknown>";
        String platform = "<Unknown>";
        if (staticDriver instanceof RemoteWebDriver) {
            RemoteWebDriver remoteWebDriver = (RemoteWebDriver)staticDriver;
            browserName = remoteWebDriver.getCapabilities().getBrowserName();
            version = remoteWebDriver.getCapabilities().getVersion();
            platform = remoteWebDriver.getCapabilities().getPlatform().name();
        }
        
        System.out.println("TESTING AGAINST " + browserName + " version " + version + " on platform " + platform);
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
        
        System.out.println(dateFormat.format(new Date()) + ": testMGILinksAreValid started.");
        
        try {
            // Loop through first MAX_MGI_LINK_CHECK_COUNT phenotype MGI links, testing each one for valid page load.
            int i = 0;
            for (String phenotypeId : phenotypeIds) {
                if ((MAX_MGI_LINK_CHECK_COUNT != -1) && (i++ >= MAX_MGI_LINK_CHECK_COUNT)) {
                    break;
                }

                target = baseUrl + "/phenotypes/" + phenotypeId;
                driver.get(target);
                List<WebElement> phenotypeLinks = driver.findElements(By.cssSelector("div.inner a").linkText(phenotypeId));
                if (phenotypeLinks.isEmpty()) {
                    message = "No page found for MP_TERM_ID " + phenotypeId + "(" + target + ")";
                    errorList.add(message);
                    continue;
                }
                    
                if (phenotypeLinks.size() != 1) {
                    message = "Expected exactly 1 page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found " + phenotypeLinks.size();
                    errorList.add(message);
                }

                phenotypeLinks.get(0).click();
                String idString = "[" + phenotypeId + "]";
                boolean found = driver.findElement(By.cssSelector("div[id='templateBodyInsert']")).getText().contains(idString);
                if ( ! found) {
                    message = "div id 'templateBodyInsert' not found.";
                    errorList.add(message);
                } else {
                    message = "SUCCESS: MGI link OK for " + phenotypeId + ". Target URL: " + target;
                    successList.add(message);
                }
            }
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            exceptionList.add(message);
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
            
        System.out.println(dateFormat.format(new Date()) + ": " + successList.size() + " MGI links processed successfully.\n\n");
        
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
        
        System.out.println(dateFormat.format(new Date()) + ": testPageForEveryMPTermId started.");
        
        try {
            // Loop through all phenotypes, testing each one for valid page load.
            int i = 0;
            for (String phenotypeId : phenotypeIds) {
                if ((MAX_PHENOTYPE_TEST_PAGE_COUNT != -1) && (i++ >= MAX_PHENOTYPE_TEST_PAGE_COUNT)) {
                    break;
                }
            
                target = baseUrl + "/phenotypes/" + phenotypeId;
                driver.get(target);
                List<WebElement> mpTermIdLink = driver.findElements(By.cssSelector("div.inner a").linkText(phenotypeId));
                if ( mpTermIdLink.isEmpty()) {
                    message = "Expected page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
                    errorList.add(message);
                } else {
                    message = "SUCCESS: MP_TERM_ID " + phenotypeId + ". Target URL: " + target;
                    successList.add(message);
                }
            }
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            exceptionList.add(message);
        }
        
        System.out.println(dateFormat.format(new Date()) + ": testPageForEveryMPTermId finished.");
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " MP_TERM_ID records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(errorList.size() + " MP_TERM_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
            
        System.out.println(dateFormat.format(new Date()) + ": " + successList.size() + " MP_TERM_ID records processed successfully.\n\n");
        
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
        

        System.out.println(dateFormat.format(new Date()) + ": testPageForEveryTopLevelMPTermId started.");
        
        try {
            // Loop through all phenotypes, testing each one for valid page load.
            int i = 0;
            for (String phenotypeId : phenotypeIds) {
                if ((MAX_PHENOTYPE_TEST_PAGE_COUNT != -1) && (i++ >= MAX_PHENOTYPE_TEST_PAGE_COUNT)) {
                    break;
                }
            
                target = baseUrl + "/phenotypes/" + phenotypeId;
                driver.get(target);
                List<WebElement> topLevelMPTermIdLink = driver.findElements(By.cssSelector("div.inner a").linkText(phenotypeId));
                if ( topLevelMPTermIdLink.isEmpty()) {
                    message = "Expected page for TOP_LEVEL_MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
                    errorList.add(message);
                } else {
                    message = "SUCCESS: TOP_LEVEL_MP_TERM_ID " + phenotypeId + ". Target URL: " + target;
                    successList.add(message);
                }
            }
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            exceptionList.add(message);
            if (driver != null)
                driver.quit();
        }
        
        
        
        System.out.println(dateFormat.format(new Date()) + ": testPageForEveryTopLevelMPTermId finished.");
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " TOP_LEVEL_MP_TERM_ID records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(errorList.size() + " TOP_LEVEL_MP_TERM_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
            
        System.out.println(dateFormat.format(new Date()) + ": " + successList.size() + " TOP_LEVEL_MP_TERM_ID records processed successfully.\n\n");
        
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
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        String phenotypeId = "junkBadPhenotype";
        final String EXPECTED_ERROR_MESSAGE = "junkBadPhenotype is not a valid mammalian phenotype identifier.";
        
        System.out.println(dateFormat.format(new Date()) + ": testInvalidMpTermId started.");
        
        try {
            target = baseUrl + "/phenotypes/" + phenotypeId;
            driver.get(target);
            List<WebElement> topLevelMPTermIdLink = driver.findElements(By.partialLinkText(EXPECTED_ERROR_MESSAGE));
            if ( topLevelMPTermIdLink.isEmpty()) {
                message = "Expected error page for TOP_LEVEL_MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
                errorList.add(message);
            } else {
                message = "SUCCESS: TOP_LEVEL_MP_TERM_ID " + phenotypeId + ". Target URL: " + target;
                successList.add(message);
            }
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            exceptionList.add(message);
        }
        
        System.out.println(dateFormat.format(new Date()) + ": testInvalidMpTermId finished.");
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " TOP_LEVEL_MP_TERM_ID records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(errorList.size() + " TOP_LEVEL_MP_TERM_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
            
        System.out.println(dateFormat.format(new Date()) + ": " + successList.size() + " TOP_LEVEL_MP_TERM_ID records processed successfully.\n\n");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }

}