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
package org.mousephenotype.www;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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
 * @author Gautier Koscielny
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
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    // These constants define the maximum number of iterations for each given test. -1 means iterate over all.
    public final int MAX_GENE_TEST_PAGE_COUNT = 10;                             // -1 means test all pages.
    private final int TIMEOUT_IN_SECONDS = 4;
    private final int THREAD_WAIT_IN_MILLISECONDS = 1000;
    
    // These variables define the actual number of iterations for each test that uses them.
    // They use default values defined above but may be overridden on the command line using -Dxxx.
    private int max_gene_test_page_count = MAX_GENE_TEST_PAGE_COUNT;
    private int timeout_in_seconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    @Before
    public void setup() {
        if (Utils.tryParseInt(System.getProperty("MAX_GENE_TEST_PAGE_COUNT")) != null)
            max_gene_test_page_count = Utils.tryParseInt(System.getProperty("MAX_GENE_TEST_PAGE_COUNT"));
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
        
        log.info("\nTESTING AGAINST " + browserName + " version " + version + " on platform " + platform);
        log.info("seleniumUrl: " + seleniumUrl);
    }

    /**
     * Finds all MGI_ACCESSION_IDs in the genotype-phenotype
     * core that do not start with 'MGI'.
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testForBadGeneIds() throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> geneIds = geneService.getAllNonConformingGenes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;

        if (geneIds.isEmpty())
            return;
        
        int targetCount = geneIds.size();
        log.info(dateFormat.format(start) + ": testForBadGeneIds started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all non-conforming genes, testing each one for valid page load (they will likely fail).
        for (String geneId : geneIds) {
            target = baseUrl + "/genes/" + geneId;

            WebElement mpTermIdLink = null;

            try {
                driver.get(target);
                mpTermIdLink = (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(geneId)));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                continue;
            }

            if (mpTermIdLink != null) {
                message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". Target URL: " + target;
                successList.add(message);
            }
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }
        
        log.info(dateFormat.format(new Date()) + ": testForBadGeneIds finished.");
        
        if ( ! errorList.isEmpty()) {
            log.info(errorList.size() + " MGI_ACCESSION_ID records failed:");
            for (String s : errorList) {
                log.info("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            log.info(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                log.info("\t" + s);
            }
        }
        
        stop = new Date();
        log.info(dateFormat.format(stop) + ": " + successList.size() + " MGI_ACCESSION_ID records processed successfully in " + Tools.dateDiff(start, stop) + ".");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure there is a page for each. Limit the test
     * to the first MAX_GENE_TEST_PAGE_COUNT by setting it to the limit you want.
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testRandomPageForGeneIds() throws SolrServerException {
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

        int targetCount = (max_gene_test_page_count >= 0 ? Math.min(max_gene_test_page_count, geneIds.size()) : geneIds.size());
        log.info(dateFormat.format(start) + ": testRandomPageForGeneIds started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each one for valid page load.
        Random rand = new Random();
        int max = geneIdArray.length;
        int min = 0;
        int i = 0;
        while (true) {
            int index = rand.nextInt((max - min) + 1) + min;
            String geneId = geneIdArray[index];
            if (i < 10) {
                log.info("gene[" + i + "]: " + geneId);
            }
            
            if ((max_gene_test_page_count != -1) && (i >= max_gene_test_page_count)) {
                break;
            }

            target = baseUrl + "/genes/" + geneId;

            WebElement mpTermIdLink = null;

            try {
                driver.get(target);
                mpTermIdLink = (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(geneId)));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            }  catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                continue;
            }

            if (mpTermIdLink != null) {
                message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". Target URL: " + target;
                successList.add(message);
            }
            
            i++;
            if (i % 1000 == 0)
                log.info(dateFormat.format(new Date()) + ": " + i + " records processed so far.");
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }
        
        log.info(dateFormat.format(new Date()) + ": testRandomPageForGeneIds finished.");
        
        if ( ! errorList.isEmpty()) {
            log.info(errorList.size() + " MGI_ACCESSION_ID records failed:");
            for (String s : errorList) {
                log.info("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            log.info(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                log.info("\t" + s);
            }
        }
        
        stop = new Date();
        log.info(dateFormat.format(stop) + ": " + successList.size() + " MGI_ACCESSION_ID records processed successfully in " + Tools.dateDiff(start, stop) + ".");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure there is a page for each. Limit the test
     * to the first MAX_GENE_TEST_PAGE_COUNT by setting it to the limit you want.
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testPageForGeneIds() throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> geneIds = geneService.getAllGenes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;

        int targetCount = (max_gene_test_page_count >= 0 ? Math.min(max_gene_test_page_count, geneIds.size()) : geneIds.size());
        log.info(dateFormat.format(start) + ": testPageForGeneIds started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        for (String geneId : geneIds) {
            if ((max_gene_test_page_count != -1) && (i >= max_gene_test_page_count)) {
                break;
            }

            target = baseUrl + "/genes/" + geneId;

            WebElement mpTermIdLink = null;

            try {
                driver.get(target);
                mpTermIdLink = (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(geneId)));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                continue;
            }

            if (mpTermIdLink != null) {
                message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". Target URL: " + target;
                successList.add(message);
            }
            
            i++;
            if (i % 1000 == 0)
                log.info(dateFormat.format(new Date()) + ": " + i + " records processed so far.");
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }
        
        log.info(dateFormat.format(new Date()) + ": testPageForGeneIds finished.");
        
        if ( ! errorList.isEmpty()) {
            log.info(errorList.size() + " MGI_ACCESSION_ID records failed:");
            for (String s : errorList) {
                log.info("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            log.info(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                log.info("\t" + s);
            }
        }
        
        stop = new Date();
        log.info(dateFormat.format(stop) + ": " + successList.size() + " MGI_ACCESSION_ID records processed successfully in " + Tools.dateDiff(start, stop) + ".");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure there is a page for each. Limit the test
     * to the first MAX_GENE_TEST_PAGE_COUNT by setting it to the limit you want.
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testPageForGenesByPhenotypeStatusCompletedAndProductionCentreWTSI() throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Set<String> geneIds = geneService.getGenesByPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.PRODUCTION_CENTRE_WTSI);
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;
        
        int targetCount = geneIds.size();
        log.info(dateFormat.format(start) + ": testPageForGenesByPhenotypeStatusCompletedAndProductionCentreWTSI started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        for (String geneId : geneIds) {
            target = baseUrl + "/genes/" + geneId;

            WebElement mpTermIdLink = null;

            try {
                driver.get(target);
                mpTermIdLink = (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(geneId)));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                continue;
            }

            if (mpTermIdLink != null) {
                message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". Target URL: " + target;
                successList.add(message);
            }
            
            i++;
            if (i % 1000 == 0)
                log.info(dateFormat.format(new Date()) + ": " + i + " records processed so far.");
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }
        
        log.info(dateFormat.format(new Date()) + ": testPageForGenesByPhenotypeStatusCompletedAndProductionCentreWTSI finished.");
        
        if ( ! errorList.isEmpty()) {
            log.info(errorList.size() + " MGI_ACCESSION_ID records failed:");
            for (String s : errorList) {
                log.info("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            log.info(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                log.info("\t" + s);
            }
        }
        
        stop = new Date();
        log.info(dateFormat.format(stop) + ": " + successList.size() + " MGI_ACCESSION_ID records processed successfully in " + Tools.dateDiff(start, stop) + ".");
        
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
//@Ignore
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
        
        log.info(dateFormat.format(start) + ": testInvalidGeneId started.");
        
        boolean found = false;
        target = baseUrl + "/genes/" + geneId;
            
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
        
        stop = new Date();
        log.info(dateFormat.format(stop) + ": testInvalidGeneId finished.");
        
        if ( ! errorList.isEmpty()) {
            log.info(errorList.size() + " MGI_ACCESSION_ID records failed:");
            for (String s : errorList) {
                log.info("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            log.info(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                log.info("\t" + s);
            }
        }
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
        
        log.info(dateFormat.format(new Date()) + ": 1 invalid MGI_ACCESSION_ID record processed successfully in " + Tools.dateDiff(start, stop) + ".");
    }
    
    @Test
//@Ignore
    public void testAkt2() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date start = new Date();
        Date stop;
        // <span class="gSymbol">
        String mgiGeneAcc = "MGI:104874";
        String url = baseUrl + "/genes/" + mgiGeneAcc;
        
        log.info(dateFormat.format(start) + ": testAkt2 started. Expecting to process 1 record.");
        
        log.info("test Akt2 url=" + url);
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.get(url);
        //String title = driver.findElement(By.xpath("//*[contains(concat(\" \", normalize-space(@class), \" \"), \"title document\")]")).getText();
        String topTextString = driver.findElement(By.id("top")).getText();
        log.info("top title=" + topTextString);
        assertTrue(topTextString.contains("Akt2"));
        Thread.currentThread().sleep(3000);
        WebElement enu = driver.findElement(By.id("enu"));

        log.info("enu text=" + enu.getText());
        assertTrue(enu.getText().contains("ENU"));

        List<WebElement> sectionTitles = driver.findElements(By.className("title"));
        log.info("section titles size=" + sectionTitles.size());
        assertTrue(sectionTitles.size() == 6);//should be five sections visible for Akt2 which have title classes including the gene one at the top
        String[] listOfSectionTitles = {"Gene: Akt2", "Phenotype associations for Akt2", "Phenotype Associated Images", "Expression", "Order Mouse and ES Cells", "Pre-QC phenotype heatmap"};
        List<String> sectionTitleCheckFor = new ArrayList<String>(Arrays.asList(listOfSectionTitles));
        for (WebElement webElement : sectionTitles) {
            String text = webElement.getText();
            log.info("section titles=|" + text + "|");
            assertTrue(sectionTitleCheckFor.contains(text));
        }
        List<WebElement> buttons = driver.findElements(By.className("btn"));
        assertTrue("Expected 3 buttons but found " + buttons.size(), buttons.size() > 1);//should be 3 buttons: 'Login to register interest', 'order', and 'KOMP'
        for (WebElement webElement : buttons) {
            String text = webElement.getText();
            log.info("button text=" + text);
            assertTrue(text.equals("Login to register interest") || text.equals("Order") || (text.equals("KOMP")));
        }
        
        //check the phenotype associations box
        WebElement abnormalities = driver.findElement(By.className("abnormalities"));// we have abnormalities for akt2?
        assertTrue(abnormalities != null);
        assertTrue(abnormalities.findElements(By.className("filterTrigger")).size() > 4);//check the size filterTrigger class elements - which equates to the ass phenotypes

        //top_level_mp_term_name check this filter exists
        Select selectTopLevel = new Select(driver.findElement(By.id("top_level_mp_term_name")));
        int optionCount = selectTopLevel.getOptions().size();
        assertTrue("Expected 7 options but found only " + optionCount, optionCount == 7);//currently 7 options exist for this gene
        Select selectResource = new Select(driver.findElement(By.id("resource_fullname")));
        assertTrue(selectResource.getOptions().size() == 3);//currently 7 options exist for this gene
//		select.deselectAll();
//		select.selectByVisibleText("Edam");

		//check we have the image sections we expect?
        //get the accordion headings seems the easiest way rather than complicated css
        List<WebElement> accordions = driver.findElements(By.className("accordion-heading"));
        log.info("accordions size=" + accordions.size());
        String[] listOfAccordionHeaders = {"Xray (167)", "Tail Epidermis Wholemount (5)", "Musculoskeletal System (2)", "Nervous System (2)", "Adipose Tissue (1)", "Cardiovascular System (1)", "Digestive System (1)", "Integumental System (1)", "Renal/urinary System (1)", "Reproductive System (1)", "Respiratory System (1)"};
        List<String> accHeaderStrings = new ArrayList(Arrays.asList(listOfAccordionHeaders));
        for (WebElement webElement : accordions) {
            String text = webElement.getText();
            log.info("accordion heading text=" + text);
            assertTrue(accHeaderStrings.contains(text));
        }

        //test that the order mouse and es cells content from viveks team exists on the page
        WebElement orderAlleleDiv = driver.findElement(By.id("allele"));//this div is in the ebi jsp which should be populated but without the ajax call success will be empty.
        assertTrue(orderAlleleDiv.getText().length() > 100);//check there is some content in the panel div
        
        stop = new Date();
        log.info(dateFormat.format(new Date()) + ": 1 Akt2 record processed successfully in " + Tools.dateDiff(start, stop) + ".");
    }
    
}
