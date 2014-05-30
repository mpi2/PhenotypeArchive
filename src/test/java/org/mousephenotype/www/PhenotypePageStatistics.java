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
 * This selenium test walks through all phenotype pages, compiling a list of phenotype pages with:
 * <ul>
 * <li>only phenotype table (and no graphs)</li>
 * <li>only images (and no phenotype table)</li>
 * <li>both a phenotype table and one or more images</li>
 * <li>no phenotype table and no images</li>
 * </ul>
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
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.generic.util.Tools;
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
public class PhenotypePageStatistics {
    
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
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    private final int TIMEOUT_IN_SECONDS = 4;
    private final int THREAD_WAIT_IN_MILLISECONDS = 1000;

    private int timeout_in_seconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;
    
    private final String NO_PHENOTYPE_ASSOCIATIONS = "Phenotype associations to genes and alleles will be available once data has completed quality control.";
    
    @Before
    public void setup() {
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
     * Walks the phenotype core collecting the count of: [phenotype] table only,
     * image(s) only, both, and none.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testCollectTableAndImageStatistics() throws SolrServerException {
        String testName = "testCollectTableAndImageStatistics";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> phenotypeIds = mpService.getAllPhenotypes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        
        List<String> phenotypeTableOnly = new ArrayList();
        List<String> imagesOnly = new ArrayList();
        List<String> both = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;
        
        int pagesWithPhenotypeTableCount = 0;
        int pagesWithImageCount = 0;
        int pagesWithBoth = 0;
        List<String> urlsWithNeitherPhenotypeTableNorImage = new ArrayList();
        
        int targetCount = testUtils.getTargetCount(testName, phenotypeIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + phenotypeIds.size() + " records.");
        
        // Loop through first targetCount phenotype MGI links, testing each one for valid page load.
        int i = 0;
        for (String phenotypeId : phenotypeIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            boolean found = false;
//if (i == 1) phenotypeId = "MP:0001304";
            target = baseUrl + "/phenotypes/" + phenotypeId;
            try {
                driver.get(target);
                (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h1#top")));
                found = true;
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
                errorList.add(message);
                continue;
            }
            try {
                boolean hasPhenotypeTable = false;
                boolean hasImage = false;
                
                // Are there any phenotype associations?
                List<WebElement> elementList = driver.findElements(By.cssSelector("div.alert"));
                
                hasPhenotypeTable = ! TestUtils.contains(elementList, NO_PHENOTYPE_ASSOCIATIONS);
                
                // Are there any images?
                elementList = driver.findElements(By.cssSelector("h2#section"));
                if (TestUtils.contains(elementList, "Images")) {
                    List<WebElement> imagesAccordion = driver.findElements(By.cssSelector("div.accordion-body ul li"));
                    if (imagesAccordion.isEmpty()) {
                        message = "ERROR: Found Image tag but there were no image links";
                        errorList.add(message);
                    } else {
                        hasImage = true;
                    }
                }
                
                if (hasPhenotypeTable && hasImage) {
                    pagesWithBoth++;
                    both.add(driver.getCurrentUrl());
                } else if (hasPhenotypeTable) {
                    pagesWithPhenotypeTableCount++;
                    phenotypeTableOnly.add(driver.getCurrentUrl());
                } else if (hasImage) {
                    pagesWithImageCount++;
                    imagesOnly.add(driver.getCurrentUrl());
                } else {
                    urlsWithNeitherPhenotypeTableNorImage.add(driver.getCurrentUrl());
                }
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
            }
            
            if ( ! found) {
                message = "h1 with id 'top' not found.";
                errorList.add(message);
            } else {
                message = "SUCCESS: MGI link OK for " + phenotypeId + ". Target URL: " + target;
                successList.add(message);
            }
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }
          
        System.out.println(dateFormat.format(new Date()) + ": " + testName + " finished.");
        
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

        System.out.println("\nPhenotype pages with tables but no images: " + pagesWithPhenotypeTableCount);
        for (String s : phenotypeTableOnly) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("Phenotype pages with images but no tables: " + pagesWithImageCount);
        for (String s : imagesOnly) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("Phenotype pages with both tables and images: " + pagesWithBoth);
        for (String s : both) {
            System.out.println(s);
        }
        System.out.println();
        
        if ( ! urlsWithNeitherPhenotypeTableNorImage.isEmpty()) {
            System.out.println("WARNING: The following urls had neither phenotype table nor images:");
            for (String s : urlsWithNeitherPhenotypeTableNorImage) {
                System.out.println("\t" + s);
            }
        }
        System.out.println();
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }

}