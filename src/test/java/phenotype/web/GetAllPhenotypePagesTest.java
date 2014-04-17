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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.test.context.ContextConfiguration;

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

//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
public class GetAllPhenotypePagesTest extends AbstractJunit4Tester {
    // These constants define the maximum number of iterations for each given test. -1 means iterate over all.
    public final int MAX_MGI_LINK_CHECK_COUNT = 5;                              // -1 means test all links.
    public final int MAX_PHENOTYPE_TEST_PAGE_COUNT = -1;                        // -1 means test all pages.
    
    @Parameters
    public static Collection<Object[]> data() throws MalformedURLException {
String seleniumUrl = "http://mi-selenium-win.windows.ebi.ac.uk:4444/wd/hub";
        Object[][] data = new Object[][]{
            { new RemoteWebDriver(new URL(seleniumUrl), DesiredCapabilities.chrome()) }
//          , { new RemoteWebDriver(new URL(seleniumUrl), DesiredCapabilities.firefox()) }
 //         , { new RemoteWebDriver(new URL(seleniumUrl), DesiredCapabilities.internetExplorer()) }
//          , { new RemoteWebDriver(new URL(seleniumUrl), DesiredCapabilities.safari()) }
        };
        return Arrays.asList(data);
    }

    /**
     * This constructor gets called once before every new WebDriver parameter.
     * JUnit doesn't provide a hook that is called after all of the tests [for
     * one WebDriver] have been executed, so there is no handy place to close
     * each browser after an execution of this test file for a given WebDriver.
     * If you need to explicitly close the WebDriver browser window, you can
     * test the 'driver' instance variable for null and, if it is not null, call
     * driver.close().
     * 
     * @param driver the next parameterized driver instance
     * @throws Exception 
     */
    public GetAllPhenotypePagesTest(RemoteWebDriver driver) throws Exception {
        // If you need to close the browser, do it here before the driver is overwritten.
        
        super(driver);      // Let the parent class initialise the driver and baseUrl.
    }
    
    @Before
    public void setup() {
    }

    @After
    public void teardown() {
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
@Ignore
    public void testInvalidMpTermId() throws SolrServerException {
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
            List<WebElement> topLevelMPTermIdLink = driver.findElements(By.partialLinkText("junkBadPhenotype"));
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