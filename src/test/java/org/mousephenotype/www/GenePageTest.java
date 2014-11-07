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
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.www.testing.model.GenePage;
import org.mousephenotype.www.testing.model.PageStatus;
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
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
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
    private PhenotypePipelineDAO phenotypePipelineDAO;
    
    @Autowired
    protected String baseUrl;
    
    @Autowired
    protected WebDriver driver;
    
    @Autowired
    protected String seleniumUrl;
    
    @Autowired
    protected TestUtils testUtils;
    
    private final int TIMEOUT_IN_SECONDS = 4;
    private final int THREAD_WAIT_IN_MILLISECONDS = 20;
    
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
        List<String> geneIds = new ArrayList(geneService.getAllGenes());
        
        geneIdsTestEngine(testName, geneIds);
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
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndPhenotypeCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.CENTRE_WTSI));
        
        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Started", null, GeneService.GeneFieldValue.CENTRE_WTSI);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());
        
        geneIdsTestEngine(testName, geneIds);
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
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.CENTRE_WTSI));

        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Started", GeneService.GeneFieldValue.CENTRE_WTSI, null);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());
        
        geneIdsTestEngine(testName, geneIds);
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
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndPhenotypeCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_COMPLETE, GeneService.GeneFieldValue.CENTRE_WTSI));
        
        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Complete", null, GeneService.GeneFieldValue.CENTRE_WTSI);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());
        
        geneIdsTestEngine(testName, geneIds);
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
     * @throws SolrServerException [
     */
    @Test
//@Ignore
    public void testPageForGenesByLatestPhenotypeStatusCompleteAndProductionCentreWTSI() throws SolrServerException {
        String testName = "testPageForGenesByLatestPhenotypeStatusCompleteAndProductionCentreWTSI";
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_COMPLETE, GeneService.GeneFieldValue.CENTRE_WTSI));
        
        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Complete", GeneService.GeneFieldValue.CENTRE_WTSI, null);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());
        
        geneIdsTestEngine(testName, geneIds);
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
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        int sectionErrorCount;
        int numOccurrences;
        
        PageStatus status;
        String message;
        Date start = new Date();
        
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of 1 records.");
        
        String geneId = "MGI:104874";
        String target = baseUrl + "/genes/" + geneId;
        System.out.println("URL: " + target);
        GenePage genePage;
        
        try {
            genePage = new GenePage(driver, wait, target, geneId, phenotypePipelineDAO, baseUrl);
        } catch (Exception e) {
            message = "ERROR: Failed to load gene page URL: " + target;
            System.out.println(message);
            fail(message);
            return;
        }

        // Title
        String title = genePage.getTitle();
        if (title.contains("Akt2")) {
            System.out.println("Title: [PASSED]\n");
        } else {
            message = "Title: [FAILED]: Expected title to contain 'Akt2' but it was not found. Title: '" + title + "'";
            errorList.add(message);
            System.out.println(message + "\n");
        }

        // Section Titles: count and values (e.g. 'Gene: Akt2', 'Phenotype associations for Akt2', 'Pre-QC phenotype heatmap', etc.)
        // ... count
        sectionErrorCount = 0;
        String[] sectionTitlesArray = {"Gene: Akt2",
                                       "Phenotype associations for Akt2",
                                       "Pre-QC phenotype heatmap",
                                       "Phenotype Associated Images",
                                       "Expression",
                                       "Potential Disease Models",
                                       "Order Mouse and ES Cells",};
        List<String> expectedSectionTitles = Arrays.asList(sectionTitlesArray);
        List<String> actualSectionTitles = genePage.getSectionTitles();
        if (actualSectionTitles.size() != sectionTitlesArray.length) {
            sectionErrorCount++;
            message = "Section Titles (count): [FAILED]. Expected " + sectionTitlesArray.length + " section titles but found " + actualSectionTitles.size() + ".";
            errorList.add(message);
            System.out.println(message + "\n");
        } else {
            System.out.println("Section Titles (count): [PASSED]\n");
        }
        // ... values
        status = new PageStatus();
        for (String expectedSectionTitle : expectedSectionTitles) {
            if ( ! actualSectionTitles.contains(expectedSectionTitle)) {
                message = "Section Titles (values): [FAILED]. Mismatch: Expected section named '" + expectedSectionTitle + "' but wasn't found.";
                status.addError(message);
                sectionErrorCount++;
            }
        }
        for (String actualSectionTitle : actualSectionTitles) {
            if ( ! expectedSectionTitles.contains(actualSectionTitle)) {
                message = "Section Titles (values): [FAILED]. Mismatch: Found section named '" + actualSectionTitle + "' but wasn't expected.";
                status.addError(message);
                sectionErrorCount++;
            } else {
                numOccurrences = TestUtils.count(actualSectionTitles, actualSectionTitle);
                if (numOccurrences > 1) {
                    message = "Section Titles (values): [FAILED]. " + numOccurrences + " occurrences of '" + actualSectionTitle + "' were found.";
                    status.addError(message);
                    sectionErrorCount++;
                }
            }
        }
        if (sectionErrorCount == 0) {
            System.out.println("Section Titles (values): [PASSED]\n");
        } else {
            // Dump out all titles.
            for (int i = 0; i < actualSectionTitles.size(); i++) {
                String sectionTitle = actualSectionTitles.get(i);
                System.out.println("\t[" + i + "]: " + sectionTitle);
            }
            
            // Dump out the missing ones.
            System.out.println(status.toStringErrorMessages());
            
            // Add missing ones to error list.
            errorList.addAll(status.getErrorMessages());
        }
        
        // Buttons: count and labels
        // ... count
        sectionErrorCount = 0;
        String[] buttonLabelsArray = {"Login to register interest",
                                      "Order",
                                      "KOMP",
                                      "EUMMCR",};
        List<String> expectedButtonLabels = Arrays.asList(buttonLabelsArray);
        List<String> actualButtonLabels = genePage.getButtonLabels();
        if (actualButtonLabels.size() != buttonLabelsArray.length) {
            sectionErrorCount++;
            message = "Buttons (count): [FAILED]. Expected " + buttonLabelsArray.length + " buttons but found " + actualButtonLabels.size() + ".";
            errorList.add(message);
            System.out.println(message + "\n");
        } else {
            System.out.println("Buttons (count): [PASSED]\n");
        }
        // ... values
        status = new PageStatus();
        for (String expectedSectionTitle : expectedButtonLabels) {
            if ( ! actualButtonLabels.contains(expectedSectionTitle)) {
                message = "Buttons (values): [FAILED]. Mismatch: Expected button named '" + expectedSectionTitle + "' but wasn't found.";
                status.addError(message);
                sectionErrorCount++;
            }
        }
        for (String actualButtonLabel : actualButtonLabels) {
            if ( ! expectedButtonLabels.contains(actualButtonLabel)) {
                message = "Buttons (values): [FAILED]. Mismatch: Found button named '" + actualButtonLabel + "' but wasn't expected.";
                status.addError(message);
                sectionErrorCount++;
            } else {
                numOccurrences = TestUtils.count(actualButtonLabels, actualButtonLabel);
                if (numOccurrences > 1) {
                    message = "Buttons (values): [FAILED]. " + numOccurrences + " occurrences of '" + actualButtonLabel + "' were found.";
                    status.addError(message);
                    sectionErrorCount++;
                }
            }
        }
        if (sectionErrorCount == 0) {
            System.out.println("Buttons (values): [PASSED]\n");
        } else {
            // Dump out all buttons.
            for (int i = 0; i < actualButtonLabels.size(); i++) {
                String sectionTitle = actualButtonLabels.get(i);
                System.out.println("\t[" + i + "]: " + sectionTitle);
            }
            
            // Dump out the missing ones.
            System.out.println(status.toStringErrorMessages());
            
            // Add missing ones to error list.
            errorList.addAll(status.getErrorMessages());
        }
        
        // Enabled Abnormalities: count and strings
        // ... count
        sectionErrorCount = 0;
        numOccurrences = 0;
        String[] expectedAbnormalitiesArray = { 
                "growth/size/body phenotype"
              , "homeostasis/metabolism phenotype or adipose tissue phenotype"
              , "behavior/neurological phenotype or nervous system phenotype"
              , "skeleton phenotype"
              , "immune system phenotype or hematopoietic system phenotype" };
        List<String> expectedAbnormalities = Arrays.asList(expectedAbnormalitiesArray);
        List<String> actualAbnormalities = genePage.getEnabledAbnormalities();
        if (actualAbnormalities.size() != expectedAbnormalitiesArray.length) {
            sectionErrorCount++;
            message = "Enabled Abnormalities (count): [FAILED]. Expected " + expectedAbnormalitiesArray.length + " strings but found " + actualAbnormalities.size() + ".";
            errorList.add(message);
            System.out.println(message + "\n");
        } else {
            System.out.println("Enabled Abnormalities (count): [PASSED]\n");
        }
        // ... values
        status = new PageStatus();
        for (String expectedAbnormality : expectedAbnormalities) {
            if ( ! actualAbnormalities.contains(expectedAbnormality)) {
                message = "Enabled Abnormalities (values): [FAILED]. Mismatch: Expected enabled abnormality named '" + expectedAbnormality + "' but wasn't found.";
                status.addError(message);
                sectionErrorCount++;
            }
        }
        for (String actualAbnormality : actualAbnormalities) {
            if ( ! expectedAbnormalities.contains(actualAbnormality)) {
                message = "Enabled Abnormalities (values): [FAILED]. Mismatch: Found enabled abnormality named '" + actualAbnormality + "' but wasn't expected.";
                status.addError(message);
                sectionErrorCount++;
            } else {
                numOccurrences = TestUtils.count(actualAbnormalities, actualAbnormality);
                if (numOccurrences > 1) {
                    message = "Enabled Abnormalities (values): [FAILED]. " + numOccurrences + " occurrences of '" + actualAbnormality + "' were found.";
                    status.addError(message);
                    sectionErrorCount++;
                }
            }
        }
        if (sectionErrorCount == 0) {
            System.out.println("Enabled Abnormalities (values): [PASSED]\n");
        } else {
            // Dump out all enabled abnormalities.
            for (int i = 0; i < actualAbnormalities.size(); i++) {
                String actualAbnormality = actualAbnormalities.get(i);
                System.out.println("\t[" + i + "]: " + actualAbnormality);
            }
            
            // Dump out the missing/duplicated ones.
            System.out.println(status.toStringErrorMessages());
            
            // Add missing titles to error list.
            errorList.addAll(status.getErrorMessages());
        }
        
        // Phenotype Associated Images and Expression sections: count. Since the data can
        // change over time, don't compare individual strings; just look for at least a count of 12.
        // ... count
        sectionErrorCount = 0;
        numOccurrences = 0;
        
        final int expectedAssociatedImageSize = 12;
        List<String> actualAssociatedImageSections = genePage.getAssociatedImageSections();
        if (actualAssociatedImageSections.size() < expectedAssociatedImageSize) {
            sectionErrorCount++;
            message = "Associated Image Sections (count): [FAILED]. Expected at least 12 strings but found " + actualAssociatedImageSections.size() + ".";
            errorList.add(message);
            System.out.println(message + "\n");
        } else {
            System.out.println("Associate Image Sections (count): [PASSED]\n");
        }
        
        if (sectionErrorCount == 0) {
            System.out.println("Associated Image Sections (values): [PASSED]\n");
        } else {
            // Dump out all associated image sections.
            for (int i = 0; i < actualAssociatedImageSections.size(); i++) {
                String actualAssociatedImageSection = actualAssociatedImageSections.get(i);
                System.out.println("\t[" + i + "]: " + actualAssociatedImageSection);
            }
            
            // Dump out the missing/duplicated ones.
            System.out.println(status.toStringErrorMessages());
        }
        
        //test that the order mouse and es cells content from viveks team exists on the page
        WebElement orderAlleleDiv = driver.findElement(By.id("allele2"));//this div is in the ebi jsp which should be populated but without the ajax call success will be empty.
        // This used to be called id="allele". That id still exists but is empty and causes the test to fail here. Now they use id="allele2".
        String text = orderAlleleDiv.getText();
        if (text.length() < 100) {
            message = "Order Mouse content: [FAILED]. less than 100 characters: \n\t'" + text + "'";
            errorList.add(message);
            sectionErrorCount++;
        } else {
            System.out.println("Order Mouse content: [PASSED]\n");
        }
        
        if ((errorList.isEmpty() && (exceptionList.isEmpty()))) {
            successList.add("Akt2 test: [PASSED]");
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, 1);
    }
    

    // PRIVATE METHODS
    
    
    private void geneIdsTestEngine(String testName, List<String> geneIds) throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        
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
                GenePage genePage = new GenePage(driver, wait, target, geneId, phenotypePipelineDAO, baseUrl);
                boolean phenotypesTableRequired = false;
                genePage.validate(phenotypesTableRequired);
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
    
}