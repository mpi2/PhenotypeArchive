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
 *      seleniumUrl=http://mi-selenium-win.windows.ebi.ac.uk:4444/wd/hub
 *      desiredCapabilities=firefoxDesiredCapabilities
 */

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class SearchPageTest {    
    
    @Autowired
    protected String baseUrl;
    
    @Autowired
    protected WebDriver driver;
    static protected WebDriver staticDriver;
    private String solrPath;
	private StringBuffer verificationErrors = new StringBuffer();
	private static final String SELENIUM_SERVER_URL ="http://mi-selenium-win.windows.ebi.ac.uk:4443/wd/hub";
	
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
	
    @Autowired
    protected String seleniumUrl;
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    // These constants define the maximum number of iterations for each given test. -1 means iterate over all.
    public final int MAX_GENE_TEST_PAGE_COUNT = 5000;                           // -1 means test all pages.

	    
    @Before
    public void setup() {
        printTestEnvironment();
        staticDriver = driver;
        
        baseUrl = "https://dev.mousephenotype.org";		
		solrPath = baseUrl + "/mi/impc/dev/solr";
		
		params.put("gene","fq=marker_type:* -marker_type:\"heritable phenotypic marker\"&core=gene");
		params.put("mp", "fq=ontology_subset:*&core=mp");
		params.put("disease", "fq=type:disease&core=disease");
		params.put("ma", "fq=ontology_subset:IMPC_Terms AND selected_top_level_ma_term:*&core=ma");
		params.put("pipeline", "fq=pipeline_stable_id:*&core=pipeline");		
		params.put("images", "fq=annotationTermId:M* OR expName:* OR symbol:* OR annotated_or_inferred_higherLevelMaTermName:* OR annotated_or_inferred_higherLevelMpTermName:*&core=images");
		
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
    }
    
    @BeforeClass
    public static void setUpClass() {
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 	 
 	   	Date date = new Date();
 	   	startTime = dateFormat.format(date); 	  
    }
    
    @AfterClass
    public static void tearDownClass() {
//        if (staticDriver != null) {
//            System.out.println("Closing driver.");
//            staticDriver.close();
//        }
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
    
    @Test
	//@Ignore
	public void testTickingFacetFilters() throws Exception {
    	testCount++;
    	System.out.println();   
    	String testName = "FACET CLICKING BEHAVIORAL TESTS";
    	System.out.println("----- " + testName + " -----");
       	System.out.println("TESTING clicking on a facet checkbox will add a filter to the filter summary box");
    	System.out.println("TESTING removing a filter on the list will uncheck a corresponding checkbox");
    	
    	String message;
    	successList.clear();
    	errorList.clear();
    	
		for (Map.Entry entry : params.entrySet()) {	
			String facet = entry.getKey().toString();
			
			   
		    String queryStr = baseUrl + "/data/search#" + entry.getValue();	
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
				message = "[FAILED]: " + facet + " facet on " +  testName;
				errorList.add(message);
				continue;
			}
		}	
		System.out.println();	
		if ( successList.size() == params.size() ){
			System.out.println("[PASSED] - " + testName);	
			sumSuccessList.add("passed");
		}
		else {
			System.out.println("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
			sumErrorList.add("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
		}
		System.out.println();
	}
      
	@Test
	//@Ignore
	public void testQueryingRandomGeneSymbols() throws Exception {
		testCount++;
		String testName = "RANDOM GENE SYMBOL QUERY TESTS";
		System.out.println();		
    	System.out.println("----- " + testName + " -----");
    	    	
    	successList.clear();
    	errorList.clear();
    	
		String newQueryString = "/gene/select?q=marker_symbol:*&fq=-marker_symbol:CGI_* AND -marker_symbol:Gm*&fl=marker_symbol&wt=json";
		Random rn = new Random();	
		int startIndex = rn.nextInt(30000 - 0 + 1) + 1;
		int nbRows = 10;
		System.out.println("TESTING " + nbRows + " random gene symbols");
		
		newQueryString+="&start="+startIndex+"&rows="+nbRows;
		
		
		JSONObject geneResults = JSONRestUtil.getResults(solrPath + newQueryString);
		JSONArray docs = JSONRestUtil.getDocArray(geneResults);
		
		if (docs != null) {
			int size = docs.size();		
			for (int i=0; i<size; i++) {
				int count = i+1;
				String geneSymbol1 = docs.getJSONObject(i).getString("marker_symbol");
				System.out.print("Testing symbol " + String.format("%3d", count) + ": "+ String.format("%-15s",geneSymbol1) + "\t=>\t");
				
				driver.get(baseUrl + "/data/search?q="+geneSymbol1);
				driver.navigate().refresh();
				
				//new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.geneCol")));
				new WebDriverWait(driver, 25).until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.geneCol")));
				String geneSymbol2 = driver.findElement(By.xpath("//span[contains(@class, 'gSymbol')]")).getText();
				
				//System.out.println("symbol2: "+ geneSymbol2);
				if ( geneSymbol1.equals(geneSymbol2) ){
					System.out.println("OK");
					successList.add(geneSymbol1);
					//Thread.sleep(1000);
				}
				else {		
					System.out.println("FAILED");
					errorList.add(geneSymbol1);
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
		}	
		System.out.println();
	}
		
	@Test	
	//@Ignore
	public void testRandomMgiIds() throws Exception {
		testCount++;
		System.out.println();
		String testName = "RANDOM MGI ID QUERY TESTS";
    	System.out.println("----- " + testName + " -----");
    	
    	successList.clear();
    	errorList.clear();
    	
		String newQueryString = "/gene/select?q=mgi_accession_id:*&fq=-marker_symbol:CGI_* AND -marker_symbol:Gm*&fl=mgi_accession_id,marker_symbol&wt=json";
		Random rn = new Random();	
		int startIndex = 4517;//rn.nextInt(30000 - 0 + 1) + 1;
		int nbRows = 30;
		newQueryString+="&start="+startIndex+"&rows="+nbRows;
		//System.out.println("newQueryString=" + newQueryString);
		System.out.println("TESTING " + nbRows + " random MGI IDs");
		
		JSONObject geneResults = JSONRestUtil.getResults(solrPath + newQueryString);		
		JSONArray docs = JSONRestUtil.getDocArray(geneResults);
		
		if (docs != null) {
			int size = docs.size();
			int count = 0;
			for (int i=0; i<size; i++) {
				
				count = i+1;
				String mgiId = docs.getJSONObject(i).getString("mgi_accession_id");
				String symbol = docs.getJSONObject(i).getString("marker_symbol");				
				System.out.print("Testing MGI ID " + String.format("%3d", count) + ": "+ String.format("%-10s",mgiId) + "\t=>\t");
				
				driver.get(baseUrl + "/data/search?q=" + mgiId);
				driver.navigate().refresh();
				
				//new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.id("div.geneCol"))); 
				new WebDriverWait(driver, 25).until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.geneCol")));
				//WebElement geneLink = driver.findElement(By.xpath("//a[@href='/data/genes/" + mgiId + "'"));
				WebElement geneLink = null;			
				try {
					geneLink = driver.findElement(By.cssSelector("div.geneCol a").linkText(symbol));
					System.out.println("OK");
					successList.add(mgiId);
					//Thread.sleep(1000);
				}
				catch(Exception e){					
					System.out.println("FAILED");
					errorList.add(mgiId);
					continue;
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
			}
			System.out.println();
		}		
	}
		
	@Test
	//@Ignore
	public void testPhrase() throws Exception {		
		specialStrQueryTest("PHRASE QUERY TESTS", "grip strength");		
	}
	
	@Test
	//@Ignore
	public void testPhraseInQuotes() throws Exception {		
		specialStrQueryTest("PHRASE IN QUOTES QUERY TESTS", "\"zinc finger protein\"");	
	}

	@Test
	//@Ignore
	public void testLeadingWildcard() throws Exception {
		specialStrQueryTest("LEADING WILDCARD QUERY TESTS", "*rik");			
	}
	
	@Test
	//@Ignore
	public void testTrailingWildcard() throws Exception {
		specialStrQueryTest("TRAILING WILDCARD QUERY TESTS", "hox*");			
	}
		
	@Test
	//@Ignore
	public void testPagination() throws Exception {	
		testCount++;
		System.out.println();
		String testName = "PAGINATION CLICK TESTS";
    	System.out.println("----- " + testName + " -----");
    	
    	successList.clear();
    	errorList.clear();
    	
		for (String core : cores ){		
			System.out.println("TESTING core: "+ core);
			System.out.println(baseUrl + "/data/search#" + params.get(core));
			
			driver.get(baseUrl + "/data/search#" + params.get(core));		
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
				
				//Thread.sleep(2000);
			}
			catch(Exception e){
				System.out.println("Could not get " + core + " to work...");
				continue;
			}
		}
		
		if (successList.size() == cores.size() ){
			System.out.println("[PASSED] - " + testName);
			sumSuccessList.add("passed");
		}
		else {
			//System.out.println("FAILED - " + testName + "\n" + StringUtils.join(errorList, "\n"));
			sumErrorList.add("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
		}
	}		

	@Test
	//@Ignore
	public void testFacetCounts() throws Exception {
		testCount++;
		System.out.println();
		String testName = "FACET COUNT TESTS";
    	System.out.println("----- " + testName + " -----");
    	
    	successList.clear();
    	errorList.clear();
    	
		for (String s : paramList ){	
			
			try {
				JSONObject geneResults = JSONRestUtil.getResults(solrPath + s);	
				
				int facetCountFromSolr = geneResults.getJSONObject("response").getInt("numFound");			
				String core = geneResults.getJSONObject("responseHeader").getJSONObject("params").getString("core");
				//String fq = geneResults.getJSONObject("responseHeader").getJSONObject("params").getString("fq");
				//System.out.println(core + " num found: "+ facetCountFromSolr);
				
				driver.get(baseUrl + "/data/search#" + params.get(core));	
				driver.navigate().refresh();		
				//System.out.println(baseUrl + "/data/search#" + params.get(core));
				
				// test facet panel loaded ok			
				int facetCountFromPage = Integer.parseInt(driver.findElement(By.cssSelector("div.flist li#" + core + " span.fcount")).getText());
				//System.out.println("facet panel test for " + core + " core: " + facetCountFromSolr + " vs " + facetCountFromPage);
				assertEquals(facetCountFromSolr, facetCountFromPage);
				//System.out.println("OK: facet counts for " + core);
				
				// wait for ajax response before doing the test
				new WebDriverWait(driver, 45).until(ExpectedConditions.visibilityOfElementLocated(By.id(core+"Grid")));
									
				// test dataTable loaded ok			
				//System.out.println("facet count check found : " + driver.findElement(By.cssSelector("span#resultCount a")).getText());
				String[] parts = driver.findElement(By.cssSelector("span#resultCount a")).getText().split(" ");	
				//System.out.println("check: " + parts[0]);
				int dataTalbeFoundCount = Integer.parseInt(parts[0]);				
				
				if ( facetCountFromSolr == dataTalbeFoundCount){
					System.out.println("OK: comparing facet counts for " + core);
					successList.add(core);	
				}
				else {
					errorList.add(core);	
				}
			}
			catch(Exception e){
				System.out.println("Encountered inermitten error");
			}
		}
				
		if (successList.size() == paramList.size() ){
			System.out.println("[PASSED] - " + testName);
			sumSuccessList.add("passed");
		}
		else {
			//System.out.println("FAILED - " + testName + "\n" + StringUtils.join(errorList, "\n"));
			sumErrorList.add("[FAILED] - " + testName + "\n" + StringUtils.join(errorList, "\n"));
		}
		
	}
	
	public void specialStrQueryTest(String testName, String qry) throws Exception {
		testCount++;
		System.out.println();		
    	System.out.println("----- " + testName + " -----");
    	
    	successList.clear();
    	errorList.clear();
    	
		driver.get(baseUrl + "/data/search?q=" + qry);	
			
		//new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.id("geneGrid_info")));		
		new WebDriverWait(driver, 25).until(ExpectedConditions.elementToBeClickable(By.id("geneGrid_info")));	
		String foundMsg = driver.findElement(By.cssSelector("span#resultCount a")).getText();
		if ( foundMsg.isEmpty() ){
			System.out.println("[FAILED] - queried " + qry);
			sumErrorList.add("[FAILED] - queried " + qry);			
		}
		else {
			System.out.println("[PASSED] - queried " + qry + ". Found " + foundMsg);
			sumSuccessList.add("passed");
		}
		System.out.println();
	}
    
}
