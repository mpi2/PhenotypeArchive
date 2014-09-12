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

import java.net.URLEncoder;
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
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
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
    
    // These constants define the default number of iterations for each that uses them. -1 means iterate over all.
    private final int MAX_MGI_LINK_CHECK_COUNT = 5;                             // -1 means test all links.
    private final int MAX_PHENOTYPE_TEST_PAGE_COUNT = 10;                       // -1 means test all pages.
    private final int TIMEOUT_IN_SECONDS = 15;
    private final int THREAD_WAIT_IN_MILLISECONDS = 1000;

    // These variables define the actual number of iterations for each test that uses them.
    // They use default values defined above but may be overridden on the command line using -Dxxx.
    private int max_mgi_link_check_count = MAX_MGI_LINK_CHECK_COUNT;
    private int max_phenotype_test_page_count = MAX_PHENOTYPE_TEST_PAGE_COUNT;
    private int timeout_in_seconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;
    
    private StringBuffer verificationErrors = new StringBuffer();

    private ArrayList<String> errLog = new ArrayList<String>();

    private HashMap<String, String> params = new HashMap<String, String>();
    private List<String> paramList = new ArrayList<String>();
    private List<String> cores = new ArrayList<String>();
    private List<String> errorList = new ArrayList();
    private List<String> exceptionList = new ArrayList();
    private List<String> successList = new ArrayList();
    private static List<String> sumErrorList = new ArrayList();
    private static List<String> sumSuccessList = new ArrayList();
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

        params.put("gene","fq=marker_type:* -marker_type:\"heritable phenotypic marker\"&core=gene");
        params.put("mp", "fq=*:*&core=mp");
        params.put("disease", "fq=type:disease&core=disease");
        params.put("ma", "fq=ontology_subset:IMPC_Terms AND selected_top_level_ma_term:*&core=ma");
        params.put("pipeline", "fq=pipeline_stable_id:*&core=pipeline");
        params.put("images", "fq=annotationTermId:M* OR expName:* OR symbol:*&core=images");

        String commonParam = "qf=auto_suggest&defType=edismax&wt=json&rows=0&q=*:*";
        final String geneParams      = "/gene/select?" + commonParam + "&" + params.get("gene");
        final String mpParams        = "/mp/select?" + commonParam + "&" + params.get("mp");
        final String diseaseParams   = "/disease/select?" + commonParam + "&" + params.get("disease");
        final String maParams        = "/ma/select?" + commonParam + "&" + params.get("ma");
        final String pipelineParams  = "/pipeline/select?" + commonParam + "&" + params.get("pipeline");
        final String imagesParams    = "/images/select?" + commonParam + "&" + params.get("images");

        paramList.add(geneParams);
        paramList.add(mpParams);
        paramList.add(diseaseParams);
        paramList.add(maParams);
        paramList.add(pipelineParams);
        paramList.add(imagesParams);

        cores.add("gene");
        cores.add("mp");
        cores.add("disease");
        cores.add("ma");
        cores.add("pipeline");
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
    public void autosuggestTest() throws Exception {
    	// test that there is a dropdown when at least 3 letters with match are entered into the input box
    	 testCount++;
         System.out.println();
         String testName = "autosuggestTest";
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
        String queryStr = "";
        
        for (Map.Entry entry : params.entrySet()) {
            String facet = entry.getKey().toString();


            queryStr = baseUrl + "/search#" + entry.getValue();
            //System.out.println(queryStr);
            driver.get(queryStr);
            driver.navigate().refresh();

            // input element of a subfacet
            String elem1 = "div.flist li#" + facet + " li.fcat input";
            String filterVals1 = null;
            try {
                new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.flist")));
                filterVals1 = driver.findElement(By.cssSelector(elem1)).getAttribute("rel");
            }
            catch(Exception e){
                message = "Failed to find facet checkbox filter for " + facet + " facet on " + testName
                        + ":\n\tURL: " + queryStr + "\n\telem1 = " + elem1;
                errorList.add(message);
                continue;
            }

            driver.findElement(By.cssSelector(elem1)).click();
            if ( ! driver.findElement(By.cssSelector(elem1)).isSelected() ){
                //System.out.println(facet + " filter checked");
                message = "Failed to check input filter for " + facet + " facet on " + testName;
                errorList.add(message);
            }

            String elem2 = "ul#facetFilter li li.ftag a";
            String filterVals2;
            try {
                filterVals2 = driver.findElement(By.cssSelector(elem2)).getAttribute("rel");
            }
            catch (Exception e){
                message = "Failed to find filter on filter box for " + facet + " facet on " + testName;
                //System.out.println("   " + message);
                errorList.add(message);
                continue;
            }
            // compare input with filter on filter summary box
            if ( filterVals1.equals(filterVals2) ){

                // now tests removing filter also unchecks inputbox
                driver.findElement(By.cssSelector(elem2)).click();
                if ( ! driver.findElement(By.cssSelector(elem1)).isSelected() ){
                    //System.out.println("   " + facet + " OK");
                    successList.add(facet);
                }
                else {
                    message = "Failed to uncheck input filter for " + facet + " facet on " + testName + ". URL: " + driver.getCurrentUrl();
                    errorList.add(message);
                }
            }
            else {
                message = "[FAILED]: " + facet + " facet on " + testName;
                errorList.add(message);
                continue;
            }
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
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

        String newQueryString = "/gene/select?q=marker_symbol:*&fq=-marker_symbol:CGI_* AND -marker_symbol:Gm*&fl=marker_symbol&wt=json";
        Random rn = new Random();
        int startIndex = rn.nextInt(40000 - 0 + 1) + 1;
        int nbRows = 20;
        System.out.println("TESTING " + nbRows + " random gene symbols");

        newQueryString+="&start="+startIndex+"&rows="+nbRows;

        JSONObject geneResults = JSONRestUtil.getResults(solrUrl + newQueryString);
        JSONArray docs = JSONRestUtil.getDocArray(geneResults);
        String message;
        
        if (docs != null) {
            int size = docs.size();
            for (int i=0; i<size; i++) {
                int count = i+1;
                String geneSymbol1 = docs.getJSONObject(i).getString("marker_symbol");
                
                driver.get(baseUrl + "/search?q="+geneSymbol1);
                driver.navigate().refresh();
                System.out.println("Testing symbol " + String.format("%3d", count) + ": "+ String.format("%-15s",geneSymbol1) + "\t=>\t. URL: " + driver.getCurrentUrl());

                //new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.geneCol")));
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.geneCol")));
                //String geneSymbol2 = driver.findElement(By.xpath("//span[contains(@class, 'gSymbol')]")).getText();
                
                List<WebElement> elems = driver.findElements(By.xpath("//span[contains(@class, 'gSymbol')]"));
                String geneSymbol2 = null;
                for ( WebElement elem : elems ){
                    if ( elem.getText().equals(geneSymbol1) ){
                        geneSymbol2 = elem.getText();
                        break;
                    }
                }
                
                //System.out.println("symbol2: "+ geneSymbol2);
                if ( geneSymbol1.equals(geneSymbol2) ){
                    System.out.println("OK");
                    successList.add(geneSymbol1);
                    //Thread.sleep(thread_wait_in_seconds);
                }
                else {
                    message = "ERROR: Expected to find gene id '" + geneSymbol1 + "' in the autosuggest list but it was not found.";
                    System.out.println(message);
                    errorList.add(message);
                }
                TestUtils.sleep(100);
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
            TestUtils.printEpilogue(testName, start, errorList, null, successList, 1,  1);
            fail("There were " + sumErrorList.size() + " errors.");
            
        }
        System.out.println();
    }

    @Test
//@Ignore
    public void testQueryingSpecificGeneSymbols() throws Exception {
        testCount++;
        String testName = "testQueryingSpecificGeneSymbols";
        System.out.println();
        System.out.println("----- " + testName + " -----");
        Date start = new Date();

        successList.clear();
        errorList.clear();

        String newQueryString = "/gene/select?q=marker_symbol:*&fq=-marker_symbol:CGI_* AND -marker_symbol:Gm*&fl=marker_symbol&wt=json";
        Random rn = new Random();
        int startIndex = rn.nextInt(40000 - 0 + 1) + 1;
        int nbRows = 1;
        System.out.println("TESTING " + nbRows + " random gene symbols");

        newQueryString+="&start="+startIndex+"&rows="+nbRows;


        JSONObject geneResults = JSONRestUtil.getResults(solrUrl + newQueryString);
        JSONArray docs = JSONRestUtil.getDocArray(geneResults);
        String message;
        
        if (docs != null) {
            int size = docs.size();
            for (int i=0; i<size; i++) {
                int count = i+1;
                String geneSymbol1 = docs.getJSONObject(i).getString("marker_symbol");
geneSymbol1 = "Del(7Gabrb3-Ube3a)1Yhj";
                driver.get(baseUrl + "/search?q="+geneSymbol1);
                driver.navigate().refresh();
                System.out.println("Testing symbol " + String.format("%3d", count) + ": "+ String.format("%-15s",geneSymbol1) + "\t=>\t. URL: " + driver.getCurrentUrl());

                //new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.geneCol")));
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.geneCol")));
                //String geneSymbol2 = driver.findElement(By.xpath("//span[contains(@class, 'gSymbol')]")).getText();
                
                List<WebElement> elems = driver.findElements(By.xpath("//span[contains(@class, 'gSymbol')]"));
                String geneSymbol2 = null;
                for ( WebElement elem : elems ){
                    if ( elem.getText().equals(geneSymbol1) ){
                        geneSymbol2 = elem.getText();
                        break;
                    }
                }
                
                //System.out.println("symbol2: "+ geneSymbol2);
                if ( geneSymbol1.equals(geneSymbol2) ){
                    System.out.println("OK");
                    successList.add(geneSymbol1);
                    //Thread.sleep(thread_wait_in_seconds);
                }
                else {
                    message = "ERROR: Expected to find gene id '" + geneSymbol1 + "' in the autosuggest list but it was not found.";
                    System.out.println(message);
                    errorList.add(message);
                }
                TestUtils.sleep(100);
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
            TestUtils.printEpilogue(testName, start, errorList, null, successList, 1,  1);
            fail("There were " + sumErrorList.size() + " errors.");
            
        }
        System.out.println();
    }

    /**
     * Certain gene symbols generated by 'testQueryingRandomGeneSymbols' fail
     * even though they are proper marker_symbols (i.e. they are not synonyms).
     * This test is a placeholder for those gene symbols known to not work at
     * some point in the PhenotypeArchive development cycle.
     * 
     * @throws Exception 
     */
    @Test
//@Ignore
    public void testQueryingSpecificGeneSymbolsUsingSearchPage() throws Exception {
        testCount++;
        String testName = "testQueryingSpecificGeneSymbolsUsingSearchPage";
        System.out.println();
        System.out.println("----- " + testName + " -----");
        Date start = new Date();

        successList.clear();
        errorList.clear();

        String[] geneIds = new String[] {
            "Del(7Gabrb3-Ube3a)1Yhj"
        };
        for (String expectedGeneId : geneIds) {
            String target = baseUrl + "/search";
            SearchPage searchPage = new SearchPage(driver, timeout_in_seconds, target, phenotypePipelineDAO, baseUrl);
            searchPage.submitSearch(expectedGeneId);
            boolean found = false;
            String message = "";
            try {
                List<WebElement> autosuggestElements = driver.findElements(By.xpath("//span[contains(@class, 'gSymbol')]"));
                // Walk the list of autosuggest elements, looking for a gene match.
                for (WebElement autosuggestElement : autosuggestElements) {
                    if (autosuggestElement.getText().equals(expectedGeneId)) {
                        found = true;
                        break;
                    }
                }
            } catch (Exception e) {
                message = "ERROR: Expected to find gene id '" + expectedGeneId + "' in the autosuggest list but the autosuggest list was empty.";
                errorList.add(message);
            }
            
            if (found) {
                message = "OK: Found gene '" + expectedGeneId + "' in autosuggest results.";
                successList.add(message);
            } else {
                message = "ERROR: Expected to find gene id '" + expectedGeneId + "' in the autosuggest list but it was not found.";
                errorList.add(message);
            }
                
            System.out.println(message);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, null, successList, 1,  1);
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
            int count = 0;
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
    public void testPagination() throws Exception {
        testCount++;
        System.out.println();
        String testName = "testPagination";
        System.out.println("----- " + testName + " -----");
        String url;
        String message;
        String expectedElement = "";
        final String showing_1 = "Showing 1 to 10 of";
        final String showing_11 = "Showing 11 to 20 of";
        String actualResult = "";
        Date start = new Date();

        successList.clear();
        errorList.clear();
        exceptionList.clear();
        
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        for (String core : cores ){
            url = baseUrl + "/search#" + params.get(core);
            try {
                driver.navigate().refresh();
                driver.get(url);
                expectedElement = "div#" + core + "Grid_info";
                
                // Wait for gene page to load, then click the page '2' link.
                wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(expectedElement), showing_1));
                driver.findElement(By.cssSelector("div.dataTables_paginate a").linkText("2")).click();
                
                // Wait for the 2nd page to load, then check for the expectedResult.
                wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(expectedElement), showing_11));
//                String s = (new WebDriverWait(driver, timeout_in_seconds))
//                                      .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(expectedElement))).getText();
//                System.out.println("***************************************************************************** s = " + s);
                message = "Success: found '" + showing_11 + "'";
                successList.add(message);
            } catch (NoSuchElementException | TimeoutException te) {
                message = "ERROR: expectedElement = '" + expectedElement + "'. URL = " + url
                        + "\n\texpected result = '" + showing_11 + "'"
                        + "\n\tactualResult    = '" + actualResult + "'";
                errorList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }  catch (Exception e) {
                message = "EXCEPTION processing target URL " + url + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        if ((errorList.size() > 0) || (exceptionList.size() > 0)) {
            sumErrorList.add("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
            fail("There were " + sumErrorList.size() + " errors.");
        }
        else {
            System.out.println("[PASSED] - " + testName);
            sumSuccessList.add("passed");
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, cores.size(), cores.size());
    }

    @Test
//@Ignore
    public void testFacetCounts() throws Exception {
        testCount++;
        System.out.println();
        String testName = "testFacetCounts";
        System.out.println("----- " + testName + " -----");
        Date start = new Date();
        
        successList.clear();
        errorList.clear();

        for (String s : paramList ){

            try {
                JSONObject geneResults = JSONRestUtil.getResults(solrUrl + s);

                int facetCountFromSolr = geneResults.getJSONObject("response").getInt("numFound");
                String core = geneResults.getJSONObject("responseHeader").getJSONObject("params").getString("core");
                //String fq = geneResults.getJSONObject("responseHeader").getJSONObject("params").getString("fq");
                //System.out.println(core + " num found: "+ facetCountFromSolr);

                String url = baseUrl + "/search#" + params.get(core);
                driver.get(url);
                driver.navigate().refresh();
                //System.out.println(baseUrl + "/search#" + params.get(core));

                // wait for ajax response before doing the test
                new WebDriverWait(driver, 45).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span#resultCount a")));

                // test facet panel loaded ok
                int facetCountFromPage = Integer.parseInt(driver.findElement(By.cssSelector("div.flist li#" + core + " span.fcount")).getText());
                //System.out.println("facet panel test for " + core + " core: " + facetCountFromSolr + " vs " + facetCountFromPage);
                String message = "URL: " + url;
                assertEquals(message, facetCountFromSolr, facetCountFromPage);
                //System.out.println("OK: facet counts for " + core);

                // test dataTable loaded ok
                //System.out.println("facet count check found : " + driver.findElement(By.cssSelector("span#resultCount a")).getText());
                String[] parts = driver.findElement(By.cssSelector("span#resultCount a")).getText().split(" ");
                //System.out.println("check: " + parts[0]);
                int dataTableFoundCount = Integer.parseInt(parts[0]);

                if ( facetCountFromSolr == dataTableFoundCount){
                    System.out.println("OK: comparing facet counts for " + core);
                    successList.add(core);
                }
                else {
                    errorList.add(core);
                }
            }
            catch(Exception e){
                e.printStackTrace();
                sumErrorList.add("EXCEPTION in testFacetCounts(): " + e.getLocalizedMessage());
            }
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
        }

        if (successList.size() == paramList.size() ){
            System.out.println("[PASSED] - " + testName);
            sumSuccessList.add("passed");
        }
        else {
            sumErrorList.add("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
            TestUtils.printEpilogue(testName, start, errorList, null, successList, paramList.size(), paramList.size());
            fail("There were " + sumErrorList.size() + " errors.");
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
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
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
        String xpathSelector = "//ul[@id=\"ui-id-1\"]/li/a";
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
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, 1, 1);
    }

    @Test
//@Ignore
    public void testSpecialCharacters() throws Exception {
        Date start = new Date();
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        PageStatus status = new PageStatus();
        
        successList.clear();
        errorList.clear();
        testCount++;
        System.out.println();
        String testName = "testSpecialCharacters";
        System.out.println("----- " + testName + " -----");
        
        try {
            checkSpecialPhraseWildcard(wait, status, "leprot");
            checkSpecialPhraseWildcard(wait, status, "!");
            checkSpecialPhraseWildcard(wait, status, "@");
            checkSpecialPhraseWildcard(wait, status, "€");
            checkSpecialPhraseWildcard(wait, status, "£");
            checkSpecialPhraseWildcard(wait, status, "#");
            checkSpecialPhraseWildcard(wait, status, "$");
            checkSpecialPhraseWildcard(wait, status, "%");
            checkSpecialPhraseWildcard(wait, status, "^");
            checkSpecialPhraseWildcard(wait, status, "&");
            checkSpecialPhraseWildcard(wait, status, "*");
            checkSpecialPhraseWildcard(wait, status, "(");
            checkSpecialPhraseWildcard(wait, status, ")");
            checkSpecialPhraseWildcard(wait, status, "-");
            checkSpecialPhraseWildcard(wait, status, "_");
            checkSpecialPhraseWildcard(wait, status, "=");
            checkSpecialPhraseWildcard(wait, status, "+");
            checkSpecialPhraseWildcard(wait, status, "[");
            checkSpecialPhraseWildcard(wait, status, "]");
            checkSpecialPhraseWildcard(wait, status, "{");
            checkSpecialPhraseWildcard(wait, status, "}");
            checkSpecialPhraseWildcard(wait, status, ";");
            checkSpecialPhraseWildcard(wait, status, ":");
            checkSpecialPhraseWildcard(wait, status, "'");
            checkSpecialPhraseWildcard(wait, status, "\"");
            checkSpecialPhraseWildcard(wait, status, "\\");
            checkSpecialPhraseWildcard(wait, status, "|");
            checkSpecialPhraseWildcard(wait, status, ",");
            checkSpecialPhraseWildcard(wait, status, "<");
            checkSpecialPhraseWildcard(wait, status, ".");
            checkSpecialPhraseWildcard(wait, status, ">");
            checkSpecialPhraseWildcard(wait, status, "/");
            checkSpecialPhraseWildcard(wait, status, "?");
            checkSpecialPhraseWildcard(wait, status, "`");
            checkSpecialPhraseWildcard(wait, status, "~");
            checkSpecialPhraseWildcard(wait, status, "é");
            checkSpecialPhraseWildcard(wait, status, "å");
            checkSpecialPhraseWildcard(wait, status, "ç");
            checkSpecialPhraseWildcard(wait, status, "ß");
            checkSpecialPhraseWildcard(wait, status, "č");
            checkSpecialPhraseWildcard(wait, status, "ü");
            checkSpecialPhraseWildcard(wait, status, "ö");
        } catch (Exception e) {
            System.out.println("EXCEPTION: SearchPageTest.testSpecialCharacters(): Message: " + e.getLocalizedMessage());
        } finally {
            if (status.hasErrors()) {
                errorList.add(status.toStringErrorMessages());
            }

            TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, 1, 1);
        }
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
            searchPage.clickPageButton();
//searchPage.clickPageButton(SearchPage.PageDirective.LAST);
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
    
    
    // rawPhrase is the phrase without any preceeding or trailing '*' wildcard characters. cookedPhrase is with '*' wildcard(s).
    private void checkSpecialPhrase(WebDriverWait wait, PageStatus status, String rawPhrase, String cookedPhrase) {
        String queryStr = baseUrl + "/search";
        
        try {
            driver.get(queryStr);
        } catch (Exception e) {
            errorList.add("EXCEPTION: " + e.getLocalizedMessage() + "\nqueryString: '" + queryStr + "'");
            return;
        }
        
        WebElement weInput = driver.findElement(By.cssSelector("input#s"));
        weInput.clear();
        weInput.sendKeys(cookedPhrase + "\n");
        System.out.println("\n\nChecking search for special phrase '" + cookedPhrase + "'");
        String xpathSelector = "//table[@id='geneGrid']/tbody/tr";
        List<WebElement> elements;
        
        try {
            elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpathSelector)));
        } catch (Exception e) {
            errorList.add("Exception waiting for sendKeys table results. Skipping ...  Error message: " + e.getLocalizedMessage());
            return;
        }
        
        // Issue the solr query and get the match result count per core.
        HashMap<String, Integer> coreCounts = querySolr(cookedPhrase, status);
        
        // Scrape the 'match result count per core' from the page and compare it against the solr results.
        SearchPage searchPage = new SearchPage(driver, timeout_in_seconds, phenotypePipelineDAO, baseUrl);
        
        int totalCount = 0;
        if (searchPage.getFacetCount(SearchPage.Facet.ANATOMY) != coreCounts.get(SearchPage.ANATOMY_CORE)) {
            errorList.add("Expected " + coreCounts.get(SearchPage.ANATOMY_CORE) + " Anatomy core result(s) for search term '" + cookedPhrase + "' but found " + searchPage.getFacetCount(SearchPage.Facet.ANATOMY) + ".");
            totalCount += coreCounts.get(SearchPage.ANATOMY_CORE);
        }
        if (searchPage.getFacetCount(SearchPage.Facet.DISEASES) != coreCounts.get(SearchPage.DISEASE_CORE)) {
            errorList.add("Expected " + coreCounts.get(SearchPage.DISEASE_CORE) + " Disease core result(s) for search term '" + cookedPhrase + "' but found " + searchPage.getFacetCount(SearchPage.Facet.DISEASES) + ".");
            totalCount += coreCounts.get(SearchPage.DISEASE_CORE);
        }
        if (searchPage.getFacetCount(SearchPage.Facet.GENES) != coreCounts.get(SearchPage.GENE_CORE)) {
            errorList.add("Expected " + coreCounts.get(SearchPage.GENE_CORE) + " Gene core result(s) for search term '" + cookedPhrase + "' but found " + searchPage.getFacetCount(SearchPage.Facet.GENES) + ".");
            totalCount += coreCounts.get(SearchPage.GENE_CORE);
        }
        if (searchPage.getFacetCount(SearchPage.Facet.IMAGES) != coreCounts.get(SearchPage.IMAGES_CORE)) {
            errorList.add("Expected " + coreCounts.get(SearchPage.IMAGES_CORE) + " images core result(s) for search term '" + cookedPhrase + "' but found " + searchPage.getFacetCount(SearchPage.Facet.IMAGES) + ".");
            totalCount += coreCounts.get(SearchPage.IMAGES_CORE);
        }
        if (searchPage.getFacetCount(SearchPage.Facet.PHENOTYPES) != coreCounts.get(SearchPage.PHENOTYPE_CORE)) {
            errorList.add("Expected " + coreCounts.get(SearchPage.PHENOTYPE_CORE) + " Phenotype core result(s) for search term '" + cookedPhrase + "' but found " + searchPage.getFacetCount(SearchPage.Facet.PHENOTYPES) + ".");
            totalCount += coreCounts.get(SearchPage.PHENOTYPE_CORE);
        }
        if (searchPage.getFacetCount(SearchPage.Facet.PROCEDURES) != coreCounts.get(SearchPage.PROCEDURES_CORE)) {
            errorList.add("Expected " + coreCounts.get(SearchPage.PROCEDURES_CORE) + " Procedures core result(s) for search term '" + cookedPhrase + "' but found " + searchPage.getFacetCount(SearchPage.Facet.PROCEDURES) + ".");
            totalCount += coreCounts.get(SearchPage.PROCEDURES_CORE);
        }
        
        // If there are expected results, check the first 10 for presence of phrase.
        if (totalCount > 0) {
            for (int i = 0; i < Math.min(elements.size(), 10); i++) {
                WebElement aTr = elements.get(i);
                if ( ! containsPhrase(rawPhrase)) {
                    errorList.add("Expected result to contain '" + rawPhrase + "' but it didn't. Result: '" + aTr.getText() + "'");
                }
            }
        }

        System.out.println("gene count:       " + coreCounts.get(SearchPage.GENE_CORE));
        System.out.println("phenotype count:  " + coreCounts.get(SearchPage.PHENOTYPE_CORE));
        System.out.println("disease count:    " + coreCounts.get(SearchPage.DISEASE_CORE));
        System.out.println("anatomy count:    " + coreCounts.get(SearchPage.ANATOMY_CORE));
        System.out.println("procedures count: " + coreCounts.get(SearchPage.PROCEDURES_CORE));
        System.out.println("images count:     " + coreCounts.get(SearchPage.IMAGES_CORE));
        
        TestUtils.sleep(100);
    }
    
    // 'None', 'Pre', 'Post', and 'Both' refer to the count/position of the wildcard '*' character(s).
    private void checkSpecialPhraseWildcard(WebDriverWait wait, PageStatus status, String phrase) {
        checkSpecialPhrase(wait, status, phrase, phrase);
        if ( ! phrase.equals("*")) {                                            // Special case: for "*", test only "*".
            checkSpecialPhrase(wait, status, phrase, "*" + phrase);
            checkSpecialPhrase(wait, status, phrase, phrase + "*");
            checkSpecialPhrase(wait, status, phrase, "*" + phrase + "*");
        }
    }
    
    private boolean containsPhrase(String rawPhrase) {
        boolean found = false;
        
        // In order to see the contents of the span, we need to hover over the gene first.
        Actions builder = new Actions(driver);
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        
        try {
            WebElement geneElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id='geneGrid']/tbody/tr/td[1]/div[@class='geneCol']/div[@class='subinfo']")));

            Actions hoverOverGene = builder.moveToElement(geneElement);
            hoverOverGene.perform();
            List<WebElement> spanElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@id='geneGrid']/tbody/tr/td[1]/div[@class='geneCol']/div[@class='subinfo']/span[@class='subMatch']")));

            for (WebElement spanElement : spanElements) {
                String spanText = spanElement.getText().toLowerCase();
                if (spanText.contains(rawPhrase.toLowerCase())) {
                    found = true;
                    break;
                }
             }
        } catch (Exception e) {
            System.out.println("EXCEPTION: SearchPageTest.containsPhrase() while waiting to hover. Error message: " + e.getLocalizedMessage());
        }
        
        return found;
    }
    
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
                searchPage.submitSearch(searchString);
            }

            SearchPage.Facet[] facets = {
                  SearchPage.Facet.ANATOMY
                , SearchPage.Facet.DISEASES
                , SearchPage.Facet.GENES
                , SearchPage.Facet.IMAGES
                , SearchPage.Facet.PHENOTYPES
                , SearchPage.Facet.PROCEDURES
            };

            for (SearchPage.Facet facet : facets) {
                searchPage.clickFacet(facet);
                searchPage.clickPageButton();
//searchPage.clickPageButton(SearchPage.PageDirective.FIRST);
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
    
    private final int GENE_INDEX = 0;
    private final int PHENOTYPE_INDEX = 1;
    private final int DISEASE_INDEX = 2;
    private final int ANATOMY_INDEX = 3;
    private final int PROCEDURES_INDEX = 4;
    private final int IMAGES_INDEX = 5;
        
    private HashMap<String, Integer> querySolr(String cookedPhrase, PageStatus status) {
        HashMap<String, Integer> coreCountHash = new HashMap();
        final String[] coreNames = { SearchPage.GENE_CORE, SearchPage.PHENOTYPE_CORE, SearchPage.DISEASE_CORE, SearchPage.ANATOMY_CORE, SearchPage.PROCEDURES_CORE, SearchPage.IMAGES_CORE };
        final int[] counts = { 0, 0, 0, 0, 0, 0 };

        String initialWildcard = "";
        String trailingWildcard = "";

        // In preparation for escaping characters, *don't* escape leading and/or trailing wildcard characters.
        String rawPhrase = cookedPhrase;
        if (cookedPhrase.length() > 1) {
            if (cookedPhrase.startsWith("*")) {
                initialWildcard = "*";
                rawPhrase = cookedPhrase.substring(1);
            }
            if (rawPhrase.endsWith("*")) {
                trailingWildcard = "*";
                rawPhrase = rawPhrase.substring(0, rawPhrase.length() - 1);
            }
        }
        
        // Before URLEncoding, escape any phrase characters that solr requires be escaped: + - && || ! ( ) { } [ ] ^ " ~ * ? : \
        String escapedRawPhrase = rawPhrase.replace("\\", "\\\\");              // Escape the '\\' separately to avoid double-escaping.
        
        escapedRawPhrase = escapedRawPhrase
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("&&", "\\&\\&")
                .replace("||", "\\|\\|")
                .replace("!", "\\!")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("^", "\\^")
                .replace("\"", "\\\\")
                .replace("~", "\\~")
                .replace("*", "\\*")
                .replace("?", "\\?")
                .replace(":", "\\:");
            
        String escapedEncodedRawPhrase = escapedRawPhrase;
        try { escapedEncodedRawPhrase = URLEncoder.encode(escapedEncodedRawPhrase, "UTF-8"); } catch (Exception e) { }
        
        String escapedEncodedCookedPhrase = initialWildcard + escapedEncodedRawPhrase + trailingWildcard;
System.out.println("escapedEncodedCookedPhrase = '" + escapedEncodedCookedPhrase + "'");
        String newQueryString = "/autosuggest/select?q=auto_suggest:" + escapedEncodedCookedPhrase + "&wt=json&rows=1000000";
        JSONObject jsonData;
        JSONArray docs;
        try {
            
            jsonData = JSONRestUtil.getResults(solrUrl + newQueryString);
            docs = JSONRestUtil.getDocArray(jsonData);
        } catch (Exception e) {
            status.addError("ERROR: JSON results are null for phrase '" + cookedPhrase + "', which probably means the character wasn't properly escaped. Local error message:\n" + e.getLocalizedMessage());
            return coreCountHash;
        }
        
        for (int i = 0; i < docs.size(); i++) {
            String docType = docs.getJSONObject(i).getString("docType");
            switch (docType) {
                case SearchPage.GENE_CORE:
                    counts[GENE_INDEX]++;
                    break;

                case SearchPage.PHENOTYPE_CORE:
                    counts[PHENOTYPE_INDEX]++;
                    break;

                case SearchPage.DISEASE_CORE:
                    counts[DISEASE_INDEX]++;
                    break;

                case SearchPage.ANATOMY_CORE:
                    counts[ANATOMY_INDEX]++;
                    break;

                case SearchPage.PROCEDURES_CORE:
                    counts[PROCEDURES_INDEX]++;
                    break;

                case SearchPage.IMAGES_CORE:
                    counts[IMAGES_INDEX]++;
                    break;

            }
        }
        
        for (int i = 0; i < coreNames.length; i++) {
            coreCountHash.put(coreNames[i], counts[i]);
        }
        
System.out.println("URL: " + solrUrl + newQueryString);
        return coreCountHash;
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