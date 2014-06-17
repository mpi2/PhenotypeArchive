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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.generic.util.Tools;
import uk.ac.ebi.phenotype.service.ObservationService;
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
    protected String internalSolrUrl;
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    // These constants define the default number of iterations for each that uses them. -1 means iterate over all.
    private final int MAX_MGI_LINK_CHECK_COUNT = 5;                             // -1 means test all links.
    private final int MAX_PHENOTYPE_TEST_PAGE_COUNT = 10;                       // -1 means test all pages.
    private final int TIMEOUT_IN_SECONDS = 5;
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
    private List<String> successList = new ArrayList();
    private static List<String> sumErrorList = new ArrayList();
    private static List<String> sumSuccessList = new ArrayList();
    private static String startTime;
    private static int testCount;

    @Before
    public void setup() {
        if (Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeout_in_seconds = Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            thread_wait_in_ms = Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));
        
        TestUtils.printTestEnvironment(driver, seleniumUrl);
        
        driver.navigate().refresh();
        driver.manage().timeouts().setScriptTimeout(timeout_in_seconds, TimeUnit.SECONDS);
        try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }

        params.put("gene","fq=marker_type:* -marker_type:\"heritable phenotypic marker\"&core=gene");
        params.put("mp", "fq=ontology_subset:*&core=mp");
        params.put("disease", "fq=type:disease&core=disease");
        params.put("ma", "fq=ontology_subset:IMPC_Terms AND selected_top_level_ma_term:*&core=ma");
        params.put("pipeline", "fq=pipeline_stable_id:*&core=pipeline");
        params.put("images", "fq=annotationTermId:M* OR expName:* OR symbol:* OR annotated_or_inferred_higherLevelMaTermName:* OR annotatedHigherLevelMpTermName:*&core=images");

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

    @Test
    ////@Ignore
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

        for (Map.Entry entry : params.entrySet()) {
            String facet = entry.getKey().toString();


            String queryStr = baseUrl + "/search#" + entry.getValue();
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
                //System.out.println("   " + facet + " FAILED");
                message = "Failed to find facet checkbox filter for " + facet + " facet on " + testName;
                //System.out.println("   " + message);
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
            String filterVals2 = null;
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
                    message = "Failed to uncheck input filter for " + facet + " facet on " + testName;
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

        successList.clear();
        errorList.clear();

        String newQueryString = "/gene/select?q=marker_symbol:*&fq=-marker_symbol:CGI_* AND -marker_symbol:Gm*&fl=marker_symbol&wt=json";
        Random rn = new Random();
        int startIndex = rn.nextInt(40000 - 0 + 1) + 1;
        int nbRows = 20;
        System.out.println("TESTING " + nbRows + " random gene symbols");

        newQueryString+="&start="+startIndex+"&rows="+nbRows;


        JSONObject geneResults = JSONRestUtil.getResults(internalSolrUrl + newQueryString);
        JSONArray docs = JSONRestUtil.getDocArray(geneResults);

        if (docs != null) {
            int size = docs.size();
            for (int i=0; i<size; i++) {
                int count = i+1;
                String geneSymbol1 = docs.getJSONObject(i).getString("marker_symbol");
                System.out.print("Testing symbol " + String.format("%3d", count) + ": "+ String.format("%-15s",geneSymbol1) + "\t=>\t");

                driver.get(baseUrl + "/search?q="+geneSymbol1);
                driver.navigate().refresh();

                //new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.geneCol")));
                new WebDriverWait(driver, 25).until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.geneCol")));
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
                    System.out.println("FAILED");
                    errorList.add(geneSymbol1);
                }
                try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
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

        JSONObject geneResults = JSONRestUtil.getResults(internalSolrUrl + newQueryString);
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

        successList.clear();
        errorList.clear();

        for (String core : cores ){
        	System.out.println("TESTING core: "+ core);
            System.out.println(baseUrl + "/search#" + params.get(core));

            driver.get(baseUrl + "/search#" + params.get(core));
            driver.navigate().refresh();

            String paginationInfo = null;
            String expectStr = null;

            try {
                // wait for ajax call
                new WebDriverWait(driver, 25).until(ExpectedConditions.elementToBeClickable(By.id(core+"Grid_info")));
                //new WebDriverWait(driver, 25).until(ExpectedConditions.presenceOfElementLocated(By.id(core+"Grid_info")));

                String wantedPath = "//div[contains(@class, 'dataTables_paginate')]/descendant::li[a/text()='2']";
                //System.out.println("Expected text: " + driver.findElement(By.xpath(wantedPath)).getText());

                WebElement pageLink = driver.findElement(By.xpath(wantedPath));
                pageLink.click();
                
                Thread.sleep(thread_wait_in_ms);
                new WebDriverWait(driver, 25).until(ExpectedConditions.elementToBeClickable(By.id(core+"Grid")));

                // move to its parent
                WebElement liElem = driver.findElement(By.xpath("//div[contains(@class, 'dataTables_paginate')]/descendant::li[contains(@class, 'active')]"));
                //System.out.println(liElem.getText());
                if ( liElem.getText().equals("1") ){
                    System.out.println("click again");
                    pageLink.click(); // try again
                }

                String wantedElement = core.equals("images") ? "span#annotCount" : "span#resultCount a";

                paginationInfo = driver.findElement(By.cssSelector("div#"+core+"Grid_info")).getText();
                System.out.println(paginationInfo);
                String[] parts = driver.findElement(By.cssSelector(wantedElement)).getText().split(" ");
                int dataTalbeFoundCount = Integer.parseInt(parts[0]);

                expectStr = "Showing 11 to 20 of " + NumberFormat.getNumberInstance(Locale.US).format(dataTalbeFoundCount) + " entries";
                System.out.println(expectStr);

                if ( paginationInfo.equals(expectStr) ){
                    System.out.println(core + " OK");
                    successList.add(core);
                }
                else {
                    System.out.println(core + " FAILED");
                    errorList.add(core);
                }

                // try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
            }
            catch(Exception e){
                e.printStackTrace();
                sumErrorList.add("EXCEPTION in testPagination() for " + core + ": " + e.getLocalizedMessage());
            }
        }

        if (successList.size() == cores.size() ){
            System.out.println("[PASSED] - " + testName);
            sumSuccessList.add("passed");
        }
        else {
            //System.out.println("FAILED - " + testName + "\n" + StringUtils.join(errorList, "\n"));
            sumErrorList.add("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
            fail("There were " + sumErrorList.size() + " errors.");
        }
    }

    @Test
    //@Ignore
    public void testFacetCounts() throws Exception {
        testCount++;
        System.out.println();
        String testName = "testFacetCounts";
        System.out.println("----- " + testName + " -----");

        successList.clear();
        errorList.clear();

        for (String s : paramList ){

            try {
                JSONObject geneResults = JSONRestUtil.getResults(internalSolrUrl + s);

                int facetCountFromSolr = geneResults.getJSONObject("response").getInt("numFound");
                String core = geneResults.getJSONObject("responseHeader").getJSONObject("params").getString("core");
                //String fq = geneResults.getJSONObject("responseHeader").getJSONObject("params").getString("fq");
                //System.out.println(core + " num found: "+ facetCountFromSolr);

                driver.get(baseUrl + "/search#" + params.get(core));
                driver.navigate().refresh();
                //System.out.println(baseUrl + "/search#" + params.get(core));

                // test facet panel loaded ok
                int facetCountFromPage = Integer.parseInt(driver.findElement(By.cssSelector("div.flist li#" + core + " span.fcount")).getText());
                //System.out.println("facet panel test for " + core + " core: " + facetCountFromSolr + " vs " + facetCountFromPage);
                assertEquals(facetCountFromSolr, facetCountFromPage);
                //System.out.println("OK: facet counts for " + core);

                // wait for ajax response before doing the test
                new WebDriverWait(driver, 45).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span#resultCount a")));

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
            //System.out.println("FAILED - " + testName + "\n" + StringUtils.join(errorList, "\n"));
            sumErrorList.add("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
            fail("There were " + sumErrorList.size() + " errors.");
        }

    }

    public void specialStrQueryTest(String testName, String qry) throws Exception {
        testCount++;
        System.out.println();
        System.out.println("----- " + testName + " -----");

        successList.clear();
        errorList.clear();

        driver.get(baseUrl + "/search?q=" + qry);

        //new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.id("geneGrid_info")));
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