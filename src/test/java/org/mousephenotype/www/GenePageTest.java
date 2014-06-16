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
    
    /**
     * Finds all MGI_ACCESSION_IDs in the genotype-phenotype
     * core that do not start with 'MGI'.
     * 
     * @throws SolrServerException 
     */
    @Test
//@Ignore
    public void testForBadGeneIds() throws SolrServerException {
        String testName = "testForBadGeneIds";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        Set<String> geneIds = geneService.getAllNonConformingGenes();
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
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);

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
                continue;
            }

            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
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
    public void testRandomPageForGeneIds() throws SolrServerException {
        String testName = "testRandomPageForGeneIds";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        Set<String> geneIds = geneService.getAllGenes();
        String[] geneIdArray = geneIds.toArray(new String[geneIds.size()]);
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
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
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);
            
            try {
                driver.get(target);
                (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
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
        String testName = "testPageForGeneIds";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        Set<String> geneIds = geneService.getAllGenes();
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
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;

            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);

            try {
                driver.get(target);
                (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
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
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure there is a page for each. Limit the test
     * to the first MAX_GENE_TEST_PAGE_COUNT by setting it to the limit you want.
     * 
     * @throws SolrServerException 
     */
    @Test
@Ignore
    public void testPageForGenesByPhenotypeStatusCompletedAndProductionCentreWTSI() throws SolrServerException {
        String testName = "testPageForGenesByPhenotypeStatusCompletedAndProductionCentreWTSI";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        Set<String> geneIds = geneService.getGenesByPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.PRODUCTION_CENTRE_WTSI);
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
                (new WebDriverWait(driver, timeout_in_seconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
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
            fail("Expected 6 section titles but found " + sections.size() + ".");
        }
        // ... Section titles
        String[] sectionTitlesArray = {"Gene: Akt2", "Phenotype associations for Akt2", "Phenotype Associated Images", "Expression", "Order Mouse and ES Cells", "Pre-QC phenotype heatmap"};
        List<String> sectionTitles = new ArrayList(Arrays.asList(sectionTitlesArray));
        for (WebElement webElement : sections) {
            String text = webElement.getText();
            if ( ! sectionTitles.contains(text)) {
                fail("Expected section named '" + text + "' but wasn't found.");
            }
        }
        
        // Buttons
        List<WebElement> buttons = driver.findElements(By.className("btn"));
        // ... count
        if (buttons.size() != 3) {
            fail("Expected 3 buttons but found " + buttons.size());
        }
        // ... Button text
        String[] buttonTitlesArray = { "Login to register interest", "Order", "KOMP" };
        List<String> buttonTitles = new ArrayList(Arrays.asList(buttonTitlesArray));
        for (WebElement webElement : buttons) {
            String buttonText = webElement.getText();
            if ( ! buttonTitles.contains(buttonText)) {
                fail("Expected button with title '" + buttonText + "' but none was found.");
            }
        }
        
        // Phenotype association icons
        WebElement abnormalities = driver.findElement(By.className("abnormalities"));
        List<WebElement> anchors = abnormalities.findElements(By.className("filterTrigger"));
        // ... count
        if (anchors.size() != 5) {
            fail("Expected 5 'abnormalities' icons but found " + anchors.size());
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
                fail("Expected abnormalities icon with id '" + id + "' but none was found.");
            }
        }
        
        // "Top level MP: All" drop-down
        Select selectTopLevel = new Select(driver.findElement(By.id("top_level_mp_term_name")));
        // ... count
        if (selectTopLevel.getOptions().size() != 5) {
            fail("Expected 5 \"Top level MP: All\" options but found " + selectTopLevel.getOptions().size() + ".");
        }
        // ... values
        String[] topLevelValuesArray = { "behavior/neurological phenotype"
                                       , "growth/size/body phenotype"
                                       , "hematopoietic system phenotype"
                                       , "homeostasis/metabolism phenotype"
                                       , "skeleton phenotype"  };
        List<String> topLevelValues = new ArrayList(Arrays.asList(topLevelValuesArray));
        for (WebElement option : selectTopLevel.getOptions()) {
            if ( ! topLevelValues.contains(option.getAttribute("value")))
                fail ("Expected Top level MP: All option \"" + option.getAttribute("value") + "\" but none was found.");
        }

        // Phenotype Associated Images and Expression sections
        List<WebElement> imagesAndExpression = driver.findElements(By.className("accordion-heading"));
        // ... count
        if (imagesAndExpression.size() != 11) {
            fail("Expected 2 \"Phenotype Associated Images\" values and 9 \"Expression\" values (11 total) but found " + imagesAndExpression.size());
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
        
        message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
        successList.add(message);
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, 1);
    }
    
}