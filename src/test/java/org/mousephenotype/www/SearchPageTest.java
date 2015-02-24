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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.www.testing.model.PageStatus;
import org.mousephenotype.www.testing.model.SearchImageTable.ImageFacetView;
import org.mousephenotype.www.testing.model.SearchPage;
import org.mousephenotype.www.testing.model.SearchPage.Facet;
import org.mousephenotype.www.testing.model.SearchPage.PageDirective;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.util.Utils;
/**
 *
 * @author ckchen@ebi.ac.uk (private methods)
 *
 * Generic configuration based on Mike's settings as below:
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
 * seleniumUrl=http://mi-selenium-win.windows.ebi.ac.uk:4444/wd/hub
 *      desiredCapabilities=firefoxDesiredCapabilities
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class SearchPageTest {
    
    @Autowired
    protected GeneService geneService;

    @Autowired
    protected String baseUrl;

    @Autowired
    protected WebDriver driver;
    
    @Autowired
    protected String seleniumUrl;
    
    @Autowired
    protected String solrUrl;
    
    @Autowired
    private PhenotypePipelineDAO phenotypePipelineDAO;
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
    
    // These constants define the default number of iterations for each that uses them. -1 means iterate over all.
    private final int MAX_MGI_LINK_CHECK_COUNT = 5;                             // -1 means test all links.
    private final int MAX_PHENOTYPE_TEST_PAGE_COUNT = 10;                       // -1 means test all pages.
    private final int TIMEOUT_IN_SECONDS = 120;
    private final int THREAD_WAIT_IN_MILLISECONDS = 1000;

    // These variables define the actual number of iterations for each test that uses them.
    // They use default values defined above but may be overridden on the command line using -Dxxx.
    private final int max_mgi_link_check_count = MAX_MGI_LINK_CHECK_COUNT;
    private final int max_phenotype_test_page_count = MAX_PHENOTYPE_TEST_PAGE_COUNT;
    private int timeout_in_seconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;
    
    private final StringBuffer verificationErrors = new StringBuffer();

    private final ArrayList<String> errLog = new ArrayList();

    private final HashMap<String, String> params = new HashMap();
    private final List<String> paramList = new ArrayList();
    private final List<String> cores = new ArrayList();
    private final List<String> errorList = new ArrayList();
    private final List<String> exceptionList = new ArrayList();
    private final List<String> successList = new ArrayList();
    private final static List<String> sumErrorList = new ArrayList();
    private final static List<String> sumSuccessList = new ArrayList();
    private static String startTime;
    private static int testCount;
    private WebDriverWait wait;

    @Before
    public void setup() {
        wait = new WebDriverWait(driver, timeout_in_seconds);
        if (Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeout_in_seconds = Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            thread_wait_in_ms = Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));
        
        TestUtils.printTestEnvironment(driver, seleniumUrl);
        
        driver.navigate().refresh();
        driver.manage().timeouts().setScriptTimeout(timeout_in_seconds, TimeUnit.SECONDS);
        try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }

        params.put("gene","fq=*:*");
        params.put("mp", "fq=top_level_mp_term:*");
        params.put("disease", "fq=*:*");
        params.put("ma", "fq=selected_top_level_ma_term:*");
        params.put("impc_images", "fq=*:*");
        params.put("images", "fq=annotationTermId:M* OR expName:* OR symbol:*");

        String commonParam = "qf=auto_suggest&defType=edismax&wt=json&rows=0&q=*:*";
        final String geneParams        = "/gene/select?"        + commonParam + "&" + params.get("gene");
        final String mpParams          = "/mp/select?"          + commonParam + "&" + params.get("mp");
        final String diseaseParams     = "/disease/select?"     + commonParam + "&" + params.get("disease");
        final String maParams          = "/ma/select?"          + commonParam + "&" + params.get("ma");
        final String impc_imagesParams = "/impc_images/select?" + commonParam + "&" + params.get("impc_images");
        final String imagesParams      = "/images/select?"      + commonParam + "&" + params.get("images");

        paramList.add(geneParams);
        paramList.add(mpParams);
        paramList.add(diseaseParams);
        paramList.add(maParams);
        paramList.add(impc_imagesParams);
        paramList.add(imagesParams);

        cores.add("gene");
        cores.add("mp");
        cores.add("disease");
        cores.add("ma");
        cores.add("impc_images");
        cores.add("images");
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeClass
    public static void setUpClass() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            startTime = dateFormat.format(date);
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println();
        System.out.println("SEARCH PAGE" + String.format("%8s", "started") +  " at " + startTime);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String endTime = dateFormat.format(date);

        System.out.println("SEARCH PAGE" + String.format("%8s", "ended") +  " at " + endTime);

        if ( sumErrorList.size() > 0 ){
            System.out.println(sumErrorList.size() + " of " + testCount + " SEARCH PAGE TEST(s) FAILED");
            System.out.println(StringUtils.join(sumErrorList, "\n"));
        }
        else if (sumSuccessList.size() == testCount ) {
            System.out.println("[SUCCESS] - ALL " + testCount + " SEARCH PAGE TESTS OK");
        }
        else {
            System.out.println("[FAILED] - SOME SEARCH PAGE TESTs NOT FINISHED");
        }
    }
    
    
    // TESTS
    
    
    @Test
//@Ignore
    public void testAutosuggestForSpecificKnownGenes() throws Exception {
        testCount++;
        String testName = "testAutosuggestForSpecificKnownGenes";
        System.out.println();
        System.out.println("----- " + testName + " -----");
        Date start = new Date();

        successList.clear();
        errorList.clear();
        
        String[] geneSymbols = {
              "Klk4"
            , "Del(7Gabrb3-Ube3a)1Yhj"
        };

        System.out.println("TESTING autosuggest for specific gene symbols. NOTE: Results don't seem to be ordered, so it's possible the gene is beyond the first 10 shown.");
        String message;
        
        for (String geneSymbol : geneSymbols) {
            String target = baseUrl + "/search";

            SearchPage searchPage = new SearchPage(driver, timeout_in_seconds, target, phenotypePipelineDAO, baseUrl);
            System.out.println("Testing symbol " + geneSymbol + ":\t. URL: " + driver.getCurrentUrl());

            List<SearchPage.AutosuggestRow> autoSuggestions = searchPage.getAutosuggest(geneSymbol);

            boolean found = false;
            for (SearchPage.AutosuggestRow row : autoSuggestions) {
//                    log.info("annotationType: '" + row.annotationType + "'. value: '" + row.value + "'");
                if (row.value.equalsIgnoreCase(geneSymbol)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                System.out.println("[PASSED]");
                successList.add(geneSymbol);
            } else {
                message = "[FAILED]: Expected to find gene id '" + geneSymbol + "' in the autosuggest list but it was not found.";
                for (SearchPage.AutosuggestRow row : autoSuggestions) {
                    message += "\n" + row.toString();
                }
                System.out.println(message);
                errorList.add(message);
            }
        }
        
        TestUtils.printEpilogue(testName, start, errorList, null, successList, geneSymbols.length, geneSymbols.length);

        System.out.println();
    }
    
    @Test
//@Ignore
    public void testAutosuggestMinCharacters() throws Exception {
    	// test that there is a dropdown when at least 3 letters with match are entered into the input box
    	 testCount++;
         System.out.println();
         String testName = "testAutosuggestMinCharacters";
         System.out.println("----- " + testName + " -----");

         String queryStr = baseUrl + "/search";
         //System.out.println(queryStr);
         driver.get(queryStr);
         driver.navigate().refresh();
         String testKw = "mast";
         driver.findElement(By.cssSelector("input#s")).sendKeys(testKw);
         Thread.sleep(2000); // wait until the dropdown list pops up
         
         int numTerms = driver.findElements(By.cssSelector("ul.ui-autocomplete li")).size();
         if ( numTerms > 0){        	 
        	 System.out.println("[PASSED] - " + testName);
        	 System.out.println("Test keyword " + testKw + " found " + numTerms + " suggested terms");
             sumSuccessList.add("passed");
         }
         else {
        	 String msg = "[FAILED] - " + testName + "\n" + "Test keyword: " + testKw + "\n";
        	 System.out.println(msg);
             sumErrorList.add(msg);
             fail("There was one error");         
         }
    }
    
    @Test
//@Ignore
    public void testTickingFacetFilters() throws Exception {
        testCount++;
        System.out.println();
        String testName = "testTickingFacetFilters";
        System.out.println("----- " + testName + " -----");
        System.out.println("TESTING clicking on a facet checkbox will add a filter to the filter summary box");
        System.out.println("TESTING removing a filter on the list will uncheck a corresponding checkbox");

        String message;
        successList.clear();
        errorList.clear();
        String target = baseUrl + "/search";
        logger.debug("target Page URL: " + target);
        SearchPage searchPage = new SearchPage(driver, timeout_in_seconds, target, phenotypePipelineDAO, baseUrl);
        
        // For each core:
        //   Click the first subfacet.
        //   Check that it is selected.
        //   Check that there is a filter matching the selected facet above the Genes facet.
        //   Click the first subfacet again to unselect it.
        //   Check that it is unselected.
        //   Check that there is no filter matching the just-unselected facet above the Genes facet.
        for (String core :  cores) {
            String subfacetCheckboxCssSelector = "li#" + core + " li.fcat input[type='checkbox']";
            String subfacetTextCssSelector = "li#" + core + " li.fcat span.flabel";
            int iterationErrorCount = 0;
            Facet facet = searchPage.getFacetByCoreName(core);
            searchPage.openFacet(facet);                                        // Open facet if it is not alreay opened.
            logger.debug("opening facet " + facet);
            
            WebElement firstSubfacetElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(subfacetCheckboxCssSelector)));
            firstSubfacetElement.click();                                       // Select the first subfacet.
            
            searchPage.openFacet(facet);                                        // Re-open the facet as, by design, it closed after the click() above.
            if ( ! firstSubfacetElement.isSelected()) {                         // Verify that the subfacet is selected.
                iterationErrorCount++;
                message = "Failed to check input filter for " + facet + " facet.";
                errorList.add(message);
                logger.error(message);
            }
            
            // Check that there is a filter matching the selected facet above the Genes facet.
            String facetText = driver.findElement(By.cssSelector(subfacetTextCssSelector)).getText();
            HashMap<Facet, SearchPage.FacetFilter> facetFilterHash = searchPage.getFacetFilter();
            List<String> facetFilterText = facetFilterHash.get(facet).subfacetTexts;
            boolean found = false;
            for (String facetFilter : facetFilterText) {
                if (facetFilter.contains(facetText)) {
                    found = true;
                    break;
                }
            }
            if ( ! found) {
                iterationErrorCount++;
                message = "ERROR: Couldn't find subfacet '" + facetText + "' in facet " + facet;
                errorList.add(message);
                logger.error(message);
            }
            
            searchPage.openFacet(facet);                                        // Open facet if it is not alreay opened.
            firstSubfacetElement.click();                                       // Deselect the first subfacet.
            
            searchPage.openFacet(facet);                                        // Re-open the facet as, by design, it closed after the click() above.
            
            // The page becomes stale after the click() above, so we must re-fetch the WebElement objects.
            firstSubfacetElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(subfacetCheckboxCssSelector)));
            
            if (firstSubfacetElement.isSelected()) {                            // Verify that the subfacet is no longer selected.
                iterationErrorCount++;
                message = "Failed to uncheck input filter for " + facet + " facet.";
                errorList.add(message);
                logger.error(message);
            }
            
            // Check that there are no filters.
            if (searchPage.hasFilters()) {
                iterationErrorCount++;
                message = "ERROR: Expected filters to be cleared, but there were filters in place for facet " + facet;
                errorList.add(message);
                logger.error(message);
            }
            
            if (iterationErrorCount == 0) {
                logger.info("   " + core + " OK");
                successList.add(core);
            }
            
            searchPage.clearFilters();
        }
        System.out.println();
        if ( successList.size() == params.size() ){
            System.out.println("[PASSED] - " + testName);
            sumSuccessList.add("passed");
        }
        else {
            System.out.println("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
            sumErrorList.add("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
            fail("There were " + sumErrorList.size() + " errors.");
        }
        System.out.println();
    }
        
    // Verify that random genes appear in the autosuggest list.
    @Test
//@Ignore
    public void testQueryingRandomGeneSymbols() throws Exception {
        testCount++;
        String testName = "testQueryingRandomGeneSymbols";
        System.out.println();
        System.out.println("----- " + testName + " -----");
        Date start = new Date();

        successList.clear();
        errorList.clear();
        
        Random rn = new Random();
        int startIndex = rn.nextInt(60000 - 0 + 1) + 1;
        int nbRows = 20;
        System.out.println("TESTING " + nbRows + " random gene symbols");

        String target = baseUrl + "/search#fq=*:*&facet=gene";
        logger.info("URL: " + target);
        String queryString = solrUrl + "/gene/select?q=*:*&start=" + startIndex + "&rows=" + nbRows + "&fl=marker_symbol&wt=json&indent=true";
        
        JSONObject geneResults = JSONRestUtil.getResults(queryString);
        JSONArray docs = JSONRestUtil.getDocArray(geneResults);
        String message;
        
        if (docs != null) {
            int size = docs.size();
            for (int i = 0; i<size; i++) {
                String geneSymbol1 = docs.getJSONObject(i).getString("marker_symbol");
                
                SearchPage searchPage = new SearchPage(driver, timeout_in_seconds, target, phenotypePipelineDAO, baseUrl);
                searchPage.submitSearch(geneSymbol1);
                TestUtils.sleep(5000);                                          // Sleep for a bit to allow autocomplete to catch up.

                List<WebElement> elems = driver.findElements(By.cssSelector("ul#ui-id-1 li.ui-menu-item a span b.sugTerm"));
                String geneSymbol2 = null;
                for ( WebElement elem : elems ){
                    String autosuggestGene = elem.getText();
                    if ( autosuggestGene.equals(geneSymbol1) ){
                        geneSymbol2 = elem.getText();
                        break;
                    }
                }
                
                if ( geneSymbol1.equals(geneSymbol2) ){
                    logger.info("[" + i + "] (OK): '" + geneSymbol1 + "'");
                    successList.add(geneSymbol1);
                }
                else {
                    message = "[" + i + "] (FAIL): Expected to find gene id '" + geneSymbol1 + "' in the autosuggest list but it was not found.";
                    logger.info(message);
                    errorList.add(message);
                }
            }
        }
        System.out.println();
        if (successList.size() == nbRows ){
            System.out.println("[PASSED] - " + testName);
            sumSuccessList.add("passed");
        }
        else {
            System.out.println("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
            sumErrorList.add("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
            TestUtils.printEpilogue(testName, start, errorList, null, successList, nbRows,  1);
            fail("There were " + sumErrorList.size() + " errors.");
            
        }
        System.out.println();
    }

    @Test
//@Ignore
    public void testRandomMgiIds() throws Exception {
        testCount++;
        System.out.println();
        String testName = "testRandomMgiIds";
        System.out.println("----- " + testName + " -----");

        successList.clear();
        errorList.clear();

        String newQueryString = "/gene/select?q=mgi_accession_id:*&fq=-marker_symbol:CGI_* AND -marker_symbol:Gm*&fl=mgi_accession_id,marker_symbol&wt=json";
        Random rn = new Random();
        int startIndex = rn.nextInt(40000 - 0 + 1) + 1;
        int nbRows = 20;
        newQueryString+="&start="+startIndex+"&rows="+nbRows;
        //System.out.println("newQueryString=" + newQueryString);
        System.out.println("TESTING " + nbRows + " random MGI IDs");

        JSONObject geneResults = JSONRestUtil.getResults(solrUrl + newQueryString);
        JSONArray docs = JSONRestUtil.getDocArray(geneResults);

        if (docs != null) {
            int size = docs.size();
            int count;
            for (int i=0; i<size; i++) {

                count = i+1;
                String mgiId = docs.getJSONObject(i).getString("mgi_accession_id");
                String symbol = docs.getJSONObject(i).getString("marker_symbol");
                System.out.print("Testing MGI ID " + String.format("%3d", count) + ": "+ String.format("%-10s",mgiId) + "\t=>\t");

                driver.get(baseUrl + "/search?q=" + mgiId);
                driver.navigate().refresh();

                //new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.id("div.geneCol")));
                new WebDriverWait(driver, 25).until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.geneCol")));
                //WebElement geneLink = driver.findElement(By.xpath("//a[@href='/data/genes/" + mgiId + "'"));
                WebElement geneLink = null;
                try {
                    geneLink = driver.findElement(By.cssSelector("div.geneCol a").linkText(symbol));
                    System.out.println("OK");
                    successList.add(mgiId);
                    //Thread.sleep(thread_wait_in_seconds);
                }
                catch(Exception e){
                    System.out.println("FAILED");
                    errorList.add(mgiId);
                    continue;
                }
                try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
            }
            System.out.println();
            if (successList.size() == nbRows ){
                System.out.println("[PASSED] - " + testName);
                sumSuccessList.add("passed");
            }
            else {
                System.out.println("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
                sumErrorList.add("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
                fail("There were " + sumErrorList.size() + " errors.");
            }
            System.out.println();
        }
    }

    @Test
//@Ignore
    public void testPhrase() throws Exception {
        specialStrQueryTest("testPhrase", "grip strength");
    }

    @Test
//@Ignore
    public void testPhraseInQuotes() throws Exception {
        specialStrQueryTest("testPhraseInQuotes", "\"zinc finger protein\"");
    }

    @Test
//@Ignore
    public void testLeadingWildcard() throws Exception {
        specialStrQueryTest("testLeadingWildcard", "*rik");
    }

    @Test
//@Ignore
    public void testTrailingWildcard() throws Exception {
        specialStrQueryTest("testTrailingWildcard", "hox*");
    }
    
    @Test
//@Ignore
    public void testTwist1() throws Exception {
        String testName = "testTwist1";
        String searchString = "twist1";
        
        downloadTestEngine(testName, searchString);
    }
    
    @Test
//@Ignore
    public void testPagination() throws Exception {
        testCount++;
        System.out.println();
        String testName = "testPagination";
        System.out.println("----- " + testName + " -----");
        String target;
        String message;
        final String showing_1 = "Showing 1 to ";
        final String showing_11 = "Showing 11 to ";
        Date start = new Date();
        String expectedShowingPhrase = "";
        String actualShowing = "";
        
        successList.clear();
        errorList.clear();
        exceptionList.clear();
        
        for (String core : cores ){
            target = baseUrl + "/search#" + params.get(core) + "&facet=" + core;
            System.out.println("Testing URL: " + target);
            try {
                SearchPage searchPage = new SearchPage(driver, timeout_in_seconds, target, phenotypePipelineDAO, baseUrl);
                searchPage.clickFacetById(core);
                TestUtils.sleep(2000);
                
                // Upon entry, the 'showing' string should start with 'Showing 1 to 10 of".
                expectedShowingPhrase = showing_1;
                actualShowing = searchPage.getShowing().toString();
                if ( ! actualShowing.contains(expectedShowingPhrase)) {
                    message = "ERROR: Expected '" + expectedShowingPhrase + "' but found '" + actualShowing + "'.";
                    System.out.println(message);
                    errorList.add(message);
                }
                
                if (searchPage.getNumPageButtons() > 3) {                       // Previous, page, Next
                    // Wait for facet page to load, then click the page '2' link. The 'showing' string should start with 'Showing 11 to 20 of".
                    searchPage.clickPageButton(PageDirective.SECOND_NUMBERED);
                    expectedShowingPhrase = showing_11;
                    actualShowing = searchPage.getShowing().toString();
                    if ( ! actualShowing.contains(expectedShowingPhrase)) {
                        message = "ERROR: Expected '" + expectedShowingPhrase + "' but found '" + actualShowing + "'.";
                        System.out.println(message);
                        errorList.add(message);
                    }
                }
                
                if (errorList.isEmpty())
                    successList.add("Success!");
            } catch (Exception e) {
                message = "EXCEPTION: Expected '" + expectedShowingPhrase + "' but found '" + actualShowing + "'. message: " + e.getLocalizedMessage();
                System.out.println(message);
                exceptionList.add(message);
                e.printStackTrace();
            }
        }
        
        if ((errorList.isEmpty()) && (exceptionList.isEmpty())) {
            System.out.println("[PASSED] - " + testName);
            sumSuccessList.add("passed");
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, cores.size(), cores.size());
    }
    
    @Test
//@Ignore
    public void testFacetCountsNoSearchTerm() throws Exception {
        testCount++;
        System.out.println();
        String testName = "testFacetCountsNoSearchTerm";
        System.out.println("----- " + testName + " -----");
        Date start = new Date();
        
        successList.clear();
        errorList.clear();
        
        for (String core : cores) {
            String target = baseUrl + "/search#" + params.get(core) + "&facet=" + core;
            PageStatus status = facetCountEngine(target);
            if (status.hasErrors()) {
                sumErrorList.add("[FAILED] - " + testName + "\n" + status.toStringErrorMessages());
                TestUtils.printEpilogue(testName, start, status.getErrorMessages(), null, successList, paramList.size(), paramList.size());
                fail("There were " + sumErrorList.size() + " errors.");
            } else if (status.hasWarnings()) {
                sumErrorList.add("[WARNINGS] - " + testName + "\n" + status.toStringWarningMessages());
                TestUtils.printEpilogue(testName, start, status.getWarningMessages(), null, successList, paramList.size(), paramList.size());
            } else {
                System.out.println("[PASSED] - " + testName);
                sumSuccessList.add("passed");
            }
                
            System.out.println();
        }
    }

    private class SearchTermGroup {
        private final String pageTarget;
        private final String solrTarget;

        public SearchTermGroup(String pageTarget, String solrTarget) {
            this.pageTarget = pageTarget;
            this.solrTarget = solrTarget;
        }
    }
    // Here's a good site to use for decoding: http://meyerweb.com/eric/tools/dencoder/
    SearchTermGroup[] staticSearchTermGroups = {
          new SearchTermGroup("leprot", "leprot")           // leprot
        , new SearchTermGroup("!",      "!")                // !    %21
        , new SearchTermGroup("@",      "@")                // @    %40
        , new SearchTermGroup("€",      "\\%E2%82%AC")      // €    %E2%82%AC
        , new SearchTermGroup("£",      "\\%C2%A3")         // £    %C2%A3
        , new SearchTermGroup("\\%23",  "\\%23")            // #    %23
        , new SearchTermGroup("$",      "$")                // $    %24
        , new SearchTermGroup("\\%25",  "\\%25")            // %    %25
        , new SearchTermGroup("^",      "^")                // ^    %5E
        , new SearchTermGroup("\\%26",  "\\%26")            // &    %26
        , new SearchTermGroup("\\*",    "\\%2A")            // *    %2A
        , new SearchTermGroup("(",      "(")                // (    %28
        , new SearchTermGroup(")",      ")")                // )    %29
        , new SearchTermGroup("-",      "-")                // -    %2D (hyphen)
        , new SearchTermGroup("_",      "_")                // _    %5F (underscore)
        , new SearchTermGroup("\\=",    "\\=")              // =    %3D
        , new SearchTermGroup("\\%2B",  "\\%2B")            // +    %2B
        , new SearchTermGroup("\\[",    "\\[")              // [    %5B
        , new SearchTermGroup("\\]",    "\\]")              // [    %5D
        , new SearchTermGroup("{",      "\\%7B")            // {    %7B
        , new SearchTermGroup("}",      "\\%7D")            // }    %7D
        , new SearchTermGroup("\\:",    "\\:")              // :    %3A
        , new SearchTermGroup(";",      ";")                // ;    %3B
        , new SearchTermGroup("'",      "'")                // '    %27 (single quote)
        , new SearchTermGroup("\\\"",   "\\\"")             // "    %22 (double quote)
        , new SearchTermGroup("|",      "|")                // |    %7C
        , new SearchTermGroup(",",      ",")                // ,    %2C (comma)
        , new SearchTermGroup(".",      ".")                // .    %2E (period)
        , new SearchTermGroup("<",      "<")                // <    %3C
        , new SearchTermGroup(">",      ">")                // >    %3E
        , new SearchTermGroup("\\%2F",  "\\%2F")            // /    %2F
        , new SearchTermGroup("\\?",    "\\?")              // ?    %3F
        , new SearchTermGroup("`",      "`")                // `    %60 (backtick)
        , new SearchTermGroup("\\~",    "\\~")              // ~    %7E
        , new SearchTermGroup("é",      "\\%C3%A9")         // é    %C3%A9
        , new SearchTermGroup("å",      "\\%C3%A5")         // å    %C3%A5
        , new SearchTermGroup("ç",      "\\%C3%A7")         // ç    %C3%A7
        , new SearchTermGroup("ß",      "\\%C3%9F")         // ß    %C3%9F
        , new SearchTermGroup("č",      "\\%C4%8D")         // č    %C4%8D
        , new SearchTermGroup("ü",      "\\%C3%BC")         // ü    %C3%BC
        , new SearchTermGroup("ö",      "\\%C3%B6")         // ö    %C3%B6
    };
    @Test
//@Ignore
    public void testFacetCountsSpecialCharacters() throws Exception {
        testCount++;
        System.out.println();
        String testName = "testFacetCountsSpecialCharacters";
        System.out.println("----- " + testName + " -----");
        Date start = new Date();
        
        successList.clear();
        errorList.clear();
        
        // Create an array of SearchTermGroup from searchTermGroups that expands the original list by 4,
        // prepending '*', appending '*', and prepending AND appending '*' to the original searchTermGroups values.
        // Example:  "leprot", "leprot" becomes:
        //      "leprot",   "leprot"
        //      "*leprot",  "*leprot"
        //      "leprot*",  "leprot*"
        //      "*leprot*", "*leprot*"
        List<SearchTermGroup> searchTermGroupListWildcard = new ArrayList();
        for (SearchTermGroup staticSearchTermGroup : staticSearchTermGroups) {
            searchTermGroupListWildcard.add(new SearchTermGroup(staticSearchTermGroup.pageTarget, staticSearchTermGroup.solrTarget));
            searchTermGroupListWildcard.add(new SearchTermGroup("*" + staticSearchTermGroup.pageTarget, "*" + staticSearchTermGroup.solrTarget));
            searchTermGroupListWildcard.add(new SearchTermGroup(staticSearchTermGroup.pageTarget + "*", staticSearchTermGroup.solrTarget + "*"));
            searchTermGroupListWildcard.add(new SearchTermGroup("*" + staticSearchTermGroup.pageTarget + "*", "*" + staticSearchTermGroup.solrTarget + "*"));
        }
        SearchTermGroup[] searchTermGroupWildcard = searchTermGroupListWildcard.toArray(new SearchTermGroup[0]);
        
        for (SearchTermGroup searchTermGroup : searchTermGroupWildcard) {
            // logging/debugging statements:
//            Map solrCoreCountMap = getSolrCoreCounts(searchTermGroup);
//            Set<Map.Entry<String, Integer>> entrySet = solrCoreCountMap.entrySet();
//            for (Map.Entry<String, Integer> entry : entrySet) {
//                log.info("Core: " + entry.getKey() + ". Count: " + entry.getValue());
//            }

            // Build the solarUrlCounts.
            String target = baseUrl + "/search?q=" + searchTermGroup.pageTarget;

            PageStatus status = facetCountEngine(target, searchTermGroup);

            if (status.hasErrors()) {
                sumErrorList.add("[FAILED] - " + testName + "\n" + status.toStringErrorMessages());
                TestUtils.printEpilogue(testName, start, status.getErrorMessages(), null, successList, paramList.size(), paramList.size());
                fail("There were " + sumErrorList.size() + " errors.");
            } else if (status.hasWarnings()) {
                sumErrorList.add("[WARNINGS] - " + testName + "\n" + status.toStringWarningMessages());
                TestUtils.printEpilogue(testName, start, status.getWarningMessages(), null, successList, paramList.size(), paramList.size());
            } else {
                System.out.println("[PASSED] - " + testName + " (" + searchTermGroup.pageTarget + ")");
                sumSuccessList.add("passed");
            }
            
            System.out.println();
        }
    }

    /**
     * Test for Jira bug MPII-806: from the search page, searching for the characters
     * "fasting glu" should autosuggest 'fasting glucose'. Click on 'fasting glucose'
     * and verify that the correct phenotype page appears.
     * @throws Exception 
     */
    @Test
//@Ignore
    public void testJiraMPII_806() throws Exception {
        Date start = new Date();
        successList.clear();
        errorList.clear();
        testCount++;
        System.out.println();
        String testName = "testJiraMPII_806";
        System.out.println("----- " + testName + " -----");

         String queryStr = baseUrl + "/search";
         driver.get(queryStr);
         driver.navigate().refresh();
         String characters = "fasting glu";
         driver.findElement(By.cssSelector("input#s")).sendKeys(characters);
         
         // Wait for dropdown list to appear with 'blood glucose'.
        String xpathSelector = "//ul[@id='ui-id-1']/li[@class='ui-menu-item']/a";
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathSelector)));
        if ( ! element.getText().contains("fasting glucose")) {
            errorList.add("ERROR: Expected 'fasting glucose' but found '" + element.getText() + "'");
        } else {
            element.click();
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='resultMsg']")));
            if (element.getText().contains("Found") == false) {
                errorList.add("ERROR: Expected 'Found xxx genes' message. Text = '" + element.getText() + "'");
            }
        }
        
        if ((errorList.isEmpty() && (exceptionList.isEmpty())))
            successList.add((testName + ": OK"));
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, 1, 1);
    }

    @Test
//@Ignore
    public void testDefaultDownload() throws Exception {
        String testName = "testDefaultDownload";
        String searchString = null;
        
        downloadTestEngine(testName, searchString);
    }
    
    @Test
//@Ignore
    public void testBoneDownload() throws Exception {
        String testName = "testBoneDownload";
        String searchString = "bone";
        
        downloadTestEngine(testName, searchString);
    }
    
    @Test
//@Ignore
    public void testBrachydactyly() throws Exception {
        String testName = "testBrachydactyly";
        String searchString = "brachydactyly";
        
        downloadTestEngine(testName, searchString);
    }
    
    @Test
//@Ignore
    public void testLegDownload() throws Exception {
        String testName = "testLegDownload";
        String searchString = "leg";
        
        downloadTestEngine(testName, searchString);
    }
    
    // This test doesn't use the download test engine as it requires an extra
    // click to switch to the Image facet's 'Image' view.
    @Test
//@Ignore
    public void testImageFacetImageView() throws Exception {
        String testName = "testImageFacetImageView";
        String searchString = "";
        Date start = new Date();
        PageStatus status = new PageStatus();
        
        System.out.println();
        System.out.println("----- " + testName + " -----");
        
        try {
            String target = baseUrl + "/search";
// target = "https://dev.mousephenotype.org/data/search?q=ranbp2#fq=*:*&facet=gene";
            SearchPage searchPage = new SearchPage(driver, timeout_in_seconds, target, phenotypePipelineDAO, baseUrl);
            Facet facet = Facet.IMAGES;    
            searchPage.clickFacet(facet);
            searchPage.getImageTable().setCurrentView(ImageFacetView.IMAGE_VIEW);
//            searchPage.clickPageButton();
//searchPage.clickPageButton(SearchPage.PageDirective.LAST);
//TestUtils.sleep(2000);
            System.out.println("Testing " + facet + " facet. Search string: '" + searchString + "'. URL: " + driver.getCurrentUrl());
            status.add(searchPage.validateDownload(facet));
        } catch (Exception e) {
            String message = "EXCEPTION: SearchPageTest." + testName + "(): Message: " + e.getLocalizedMessage();
            System.out.println(message);
            e.printStackTrace();
            status.addError(message);
        } finally {
            if (status.hasErrors()) {
                errorList.add(status.toStringErrorMessages());
            } else {
                successList.add(testName + ": SUCCESS.");
            }

            TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, 1, 1);
        }
    }
    
    // This test was spawned from testImageFacetImageView() when it came across
    // a 500 response from the server when the last page was selected.
    @Test
//@Ignore
    public void testImageFacetImageViewLastPage() throws Exception {
        String testName = "testImageFacetImageViewLastPage";
        String searchString = "";
        Date start = new Date();
        PageStatus status = new PageStatus();
        
        System.out.println();
        System.out.println("----- " + testName + " -----");
        
        try {
            String target = baseUrl + "/search";
// target = "https://dev.mousephenotype.org/data/search?q=ranbp2#fq=*:*&facet=gene";
            SearchPage searchPage = new SearchPage(driver, timeout_in_seconds, target, phenotypePipelineDAO, baseUrl);
            Facet facet = Facet.IMAGES;    
            searchPage.clickFacet(facet);
            searchPage.getImageTable().setCurrentView(ImageFacetView.IMAGE_VIEW);
            searchPage.clickPageButton(SearchPage.PageDirective.LAST);
            TestUtils.sleep(2000);
            System.out.println("Testing " + facet + " facet. Search string: '" + searchString + "'. URL: " + driver.getCurrentUrl());
            status.add(searchPage.validateDownload(facet));
        } catch (Exception e) {
            String message = "EXCEPTION: SearchPageTest." + testName + "(): Message: " + e.getLocalizedMessage();
            System.out.println(message);
            e.printStackTrace();
            status.addError(message);
        } finally {
            if (status.hasErrors()) {
                errorList.add(status.toStringErrorMessages());
            } else {
                successList.add(testName + ": SUCCESS.");
            }

            TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, 1, 1);
        }
    }

    
    // PRIVATE METHODS
    
    
    /**
     * Executes download verification. <code>searchPhrase</code> is used to
     * specify the search characters to send to the server. It may be null or empty.
     * @param testName the test name
     * @param facet the facet to test. If null, no facet is selected before the test.
     * @param searchString the search characters to be sent to the server. May
     *        be null or empty. If not empty, must be terminated by a trailing
     *        forward slash.
     * @return status
     */
    private void downloadTestEngine(String testName, String searchString) throws Exception {
        Date start = new Date();
        PageStatus status = new PageStatus();
        
        if (searchString == null)
            searchString = "";
        
        System.out.println();
        System.out.println("----- " + testName + " -----");
        
        try {
            // Apply searchPhrase. Click on this facet. Click on a random page. Click on each download type: Compare page values with download stream values.
            String target = baseUrl + "/search";
// target = "https://dev.mousephenotype.org/data/search?q=ranbp2#fq=*:*&facet=gene";
            SearchPage searchPage = new SearchPage(driver, timeout_in_seconds, target, phenotypePipelineDAO, baseUrl);

            if (! searchString.isEmpty()) {
                searchPage.submitSearch(searchString + "\n");
            }

            SearchPage.Facet[] facets = {
                  SearchPage.Facet.ANATOMY
                , SearchPage.Facet.DISEASES
                , SearchPage.Facet.GENES
                , SearchPage.Facet.IMAGES
                , SearchPage.Facet.IMPC_IMAGES
                , SearchPage.Facet.PHENOTYPES
            };

            for (SearchPage.Facet facet : facets) {
//                searchPage.clickFacet(facet);
//                searchPage.clickPageButton();
//searchPage.clickPageButton(SearchPage.PageDirective.FIFTH_NUMBERED);
//TestUtils.sleep(1000);
                System.out.println("Testing " + facet + " facet. Search string: '" + searchString + "'. URL: " + driver.getCurrentUrl());
                status.add(searchPage.validateDownload(facet));
            }
        } catch (Exception e) {
            String message = "EXCEPTION: SearchPageTest." + testName + "(): Message: " + e.getLocalizedMessage();
            System.out.println(message);
            e.printStackTrace();
            status.addError(message);
        } finally {
            if (status.hasErrors()) {
                errorList.add(status.toStringErrorMessages());
            } else {
                successList.add(testName + ": SUCCESS.");
            }

            TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, 1, 1);
        }
    }


    /**
     * Invokes the facet count engine with no search term.
     * @param target the page target URL
     * @return page status
     */
    private PageStatus facetCountEngine(String target) {
        return facetCountEngine(target, null);
    }
    
    /**
     * Invokes the facet count engine with the specified, [already escaped if necessary] search term.
     * @param target the page target URL
     * @param searchTerm the desired search term
     * @return page status
     */
    private PageStatus facetCountEngine(String target, SearchTermGroup searchTermGroup) {
        PageStatus status = new PageStatus();
        String message;
        
        System.out.println("Page target: " + target);
        
        // Get the solarUrlCounts.
        Map solrCoreCountMap = getSolrCoreCounts(searchTermGroup);
        if (solrCoreCountMap == null) {
            message = "FAIL: Unable to get facet count from Solr.";
            status.addError(message);
            System.out.println(message);
            return status;
        }

        System.out.println("facetCountEngine(): Page target URL: " + target);
        SearchPage searchPage = new SearchPage(driver, timeout_in_seconds, target, phenotypePipelineDAO, baseUrl);

        // Verify that the core counts returned by solr match the facet counts on the page.
        for (String core : cores) {
            int facetCountFromPage = searchPage.getFacetCount(core);
            int facetCountFromSolr = (int)solrCoreCountMap.get(core);

            if (facetCountFromSolr != facetCountFromPage) {
                message = "FAIL: facet count from Solr: " + facetCountFromSolr + ". facetCountFromPage: " + facetCountFromPage + ". URL: " + target;
                status.addError(message);
                System.out.println(message);
            }
        }
        
        return status;
    }
    
    /**
     * Queries each of the six search solr cores for the number of occurrences
     * of <code>searchPhrase</code> (which may be null), returning a
     * <code>Map</code> keyed by core name containing the occurrence count for
     * each core.
     *
     * @param searchTermGroup The search phrase to use when querying the cores. If
     * null, the count is unfiltered.
     * @return the <code>searchPhrase</code> occurrence count
     */
    private Map<String, Integer> getSolrCoreCounts(SearchTermGroup searchTermGroup) {
        Map<String, Integer> solrCoreCountMap = new HashMap();
        
        for (int i = 0; i < paramList.size(); i++) {
            String solrQueryString = paramList.get(i);
            try {
                if (searchTermGroup != null) {
                    solrQueryString = solrQueryString.replace("&q=*:*", "&q=" + searchTermGroup.solrTarget);
                }
        
                String fqSolrQueryString = solrUrl + solrQueryString;
                
                JSONObject geneResults = JSONRestUtil.getResults(fqSolrQueryString);
                int facetCountFromSolr = geneResults.getJSONObject("response").getInt("numFound");
                String facet = cores.get(i);
                solrCoreCountMap.put(facet, facetCountFromSolr);
            } catch (TimeoutException te) {
                System.out.println("ERROR: SearchPageTest.getSolrCoreCounts() timeout!");
                return null;
            }
            catch(Exception e){
                System.out.println("ERROR: SearchPageTest.getSolrCoreCounts(): " + e.getLocalizedMessage());
                return null;
            }
        }
        
        return solrCoreCountMap;
    }

    private void specialStrQueryTest(String testName, String qry) throws Exception {
        testCount++;
        System.out.println();
        System.out.println("----- " + testName + " -----");

        successList.clear();
        errorList.clear();

        driver.get(baseUrl + "/search?q=" + qry);

        new WebDriverWait(driver, 25).until(ExpectedConditions.elementToBeClickable(By.id("geneGrid_info")));
        String foundMsg = driver.findElement(By.cssSelector("span#resultCount a")).getText();
        if ( foundMsg.isEmpty() ){
            System.out.println("[FAILED] - queried " + qry);
            sumErrorList.add("[FAILED] - queried " + qry);
            fail("There were " + sumErrorList.size() + " errors.");
        }
        else {
            System.out.println("[PASSED] - queried " + qry + ". Found " + foundMsg);
            sumSuccessList.add("passed");
        }
        System.out.println();
    }
    
}