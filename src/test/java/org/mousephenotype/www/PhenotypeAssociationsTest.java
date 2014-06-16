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
public class PhenotypeAssociationsTest {
    
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
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    private final int TIMEOUT_IN_SECONDS = 4;
    private final int THREAD_WAIT_IN_MILLISECONDS = 1000;
    
    private final String ANCHOR_STRING = "Total number of results: ";
    
    private int timeout_in_seconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    private final List<String> errorList = new ArrayList();
    private final List<String> successList = new ArrayList();
    private final List<String> exceptionList = new ArrayList();
    
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
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure there is a page for each. Limit the test by
     * adding a value to testIterations.properties with the test name on the
     * left and the number of iterations on the right (-1 means run all).
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testTotalsCountRandom() throws SolrServerException {
        String testName = "testTotalsCountRandom";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> geneIds = geneService.getAllGenes();
        String[] geneIdArray = geneIds.toArray(new String[geneIds.size()]);
        Date start = new Date();

        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each one for valid page load.
        Random rand = new Random();
        int max = geneIdArray.length;
        int min = 0;
        int i = 0;
        while (true) {
            int index = rand.nextInt((max - min) + 1) + min;
            String geneId = geneIdArray[index];
// if (i == 0) geneId = "MGI:104874";
// if (i == 0) geneId = "MGI:2443601";        // This one doesn't exist.
//System.out.println("gene[" + i + "]: " + geneId);
            if (i >= targetCount) {
                break;
            }
            i++;
            
            processRow(geneId, i);

            if (i % 1000 == 0)
                System.out.println(dateFormat.format(new Date()) + ": " + i + " records processed so far.");
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        printSummary(testName, start);
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }
    
    private void processRow(String geneId, int index) {
        String message;
        String target = baseUrl + "/genes/" + geneId;
        System.out.println("gene[" + index + "] URL: " + target);

        int sumOfPhenotypeCounts = 0;
        int expectedMinimumResultCount = -1;
        try {
            driver.get(target);
            (new WebDriverWait(driver, timeout_in_seconds))
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h1#top")));
            
            // Make sure this page has phenotype associations.
            List<WebElement> phenotypeAssociationElements = driver.findElements(By.cssSelector("div.inner ul li a.filterTrigger"));
            if ((phenotypeAssociationElements == null) || (phenotypeAssociationElements.isEmpty()))
                return;         // This gene page has no phenotype associations.
            
            // Get the expected result count.
            List<WebElement> resultElements = driver.findElements(By.cssSelector("p.resultCount"));
            WebElement expectedMinimumResultElement = TestUtils.find(resultElements, ANCHOR_STRING);
            if (expectedMinimumResultElement != null) {
                String resultCountString = expectedMinimumResultElement.getText().trim().replace(ANCHOR_STRING, "");
                Integer niResultCount = Utils.tryParseInt(resultCountString);
                if (niResultCount != null)
                    expectedMinimumResultCount = niResultCount;
            }
            
            // Loop through the phenotype links, extracting and summing the counts.
            for (WebElement phenotypeAssociationElement : phenotypeAssociationElements) {
                Integer thisLinkCount = Utils.tryParseInt(phenotypeAssociationElement.getText());
                if (thisLinkCount != null)
                    sumOfPhenotypeCounts += thisLinkCount;
            }
            
            if (expectedMinimumResultCount > sumOfPhenotypeCounts) {
                errorList.add("ERROR: Expected minimum result count of " + expectedMinimumResultCount + " but actual sum of phenotype counts was " + sumOfPhenotypeCounts + " for " + driver.getCurrentUrl());
            } else {
                successList.add("SUCCESS! expectedMinimumResultCount: " + expectedMinimumResultCount + ". sumOfPhenotypeCounts: " + sumOfPhenotypeCounts + ". URL: " + driver.getCurrentUrl());
            }
        } catch (NoSuchElementException | TimeoutException te) {
            message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
            errorList.add(message);
        }  catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            exceptionList.add(message);
        }
    }
    
    private void printSummary(String testName, Date start) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        
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
        
        if ( ! successList.isEmpty()) {
            System.out.println(successList.size() + " SUCCESSFUL RECORDS PROCESSED:");
            for (String s : successList) {
                System.out.println("\t" + s);
            }
        }
        
        Date stop = new Date();
        System.out.println(dateFormat.format(stop) + ": " + successList.size() + " MGI_ACCESSION_ID records processed successfully in " + Tools.dateDiff(start, stop) + ".");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }
    
    
    
 
    
}
