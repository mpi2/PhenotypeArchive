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
package phenotype.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.generic.util.Tools;
import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService;

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
public class GetGenePagesTest {
    
    @Autowired
    protected GenotypePhenotypeService genotypePhenotypeService;
    
    @Autowired
    protected String baseUrl;
    
    @Autowired
    protected WebDriver driver;
    static protected WebDriver staticDriver;
    
    @Autowired
    protected String seleniumUrl;
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    // These constants define the maximum number of iterations for each given test. -1 means iterate over all.
    public final int MAX_GENE_TEST_PAGE_COUNT = 5;                           // -1 means test all pages.

    @Before
    public void setup() {
        printTestEnvironment();
        staticDriver = driver;
    }

    @After
    public void teardown() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
//        if (staticDriver != null) {
//            System.out.println("Closing driver.");
//            staticDriver.close();
//        }
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
        
        System.out.println("seleniumUrl: " + seleniumUrl);
        System.out.println("TESTING AGAINST " + browserName + " version " + version + " on platform " + platform);
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure there is a page for each. Limit the test
     * to the first MAX_GENE_TEST_PAGE_COUNT by setting it to the limit you want.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testPageForGeneIds() throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> geneIds = genotypePhenotypeService.getAllGenes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;
        
        System.out.println(dateFormat.format(start) + ": testPageForGeneIds started.");
        
        // Loop through all phenotypes, testing each one for valid page load.
        int i = 0;
        for (String geneId : geneIds) {
            if ((MAX_GENE_TEST_PAGE_COUNT != -1) && (i++ >= MAX_GENE_TEST_PAGE_COUNT)) {
                break;
            }

            target = baseUrl + "/genes/" + geneId;

            List<WebElement> mpTermIdLink;

            try {
                driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
                driver.get(target);
                driver.navigate().refresh();
                mpTermIdLink = driver.findElements(By.cssSelector("div.inner a").linkText(geneId));
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                continue;
            }

            if ( mpTermIdLink.isEmpty()) {
                message = "Expected page for MARKER_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            } else {
                message = "SUCCESS: MARKER_ACCESSION_ID " + geneId + ". Target URL: " + target;
                successList.add(message);
            }
            
            if (i % 1000 == 0)
                System.out.println(dateFormat.format(new Date()) + ": " + i + " records processed so far.");
        }
        
        System.out.println(dateFormat.format(new Date()) + ": testPageForGeneIds finished.");
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " MARKER_ACCESSION_ID records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(exceptionList.size() + " MARKER_ACCESSION_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
        
        stop = new Date();
        System.out.println(dateFormat.format(stop) + ": " + successList.size() + " MARKER_ACCESSION_ID records processed successfully in " + Tools.dateDiff(start, stop) + ".\n\n");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }
    
    /**
     * Tests that a sensible page is returned for an invalid gene id.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testInvalidGeneId() throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;
        String geneId = "junkBadGene";
        final String EXPECTED_ERROR_MESSAGE = "Oops! junkBadGene is not a valid MGI gene identifier.";
        
        System.out.println(dateFormat.format(start) + ": testInvalidGeneId started.");
        
        try {
            target = baseUrl + "/genes/" + geneId;
            driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
            driver.get(target);
            driver.navigate().refresh();
            boolean found = false;
            
            List<WebElement> geneLinks = driver.findElements(By.cssSelector("div.node h1"));
            if (geneLinks.isEmpty()) {
                message = "No page found for MARKER_ACCESSION_ID " + geneId + "(" + target + ")";
                errorList.add(message);
            }

            for (WebElement element : geneLinks) {
                if (element.getText().compareTo(EXPECTED_ERROR_MESSAGE) == 0) {
                    found = true;
                    break;
                }
            }

            if ( ! found) {
                message = "Expected error page for MARKER_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            }
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            exceptionList.add(message);
        }
        
        stop = new Date();
        System.out.println(dateFormat.format(stop) + ": testInvalidGeneId finished.");
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " MARKER_ACCESSION_ID records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(exceptionList.size() + " MARKER_ACCESSION_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
        
        System.out.println(dateFormat.format(new Date()) + ": 1 invalid MARKER_ACCESSION_ID record processed successfully in " + Tools.dateDiff(start, stop) + ".\n\n");
    }
    
}
