/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.phenotype.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import uk.ac.ebi.generic.util.JSONRestUtil;

/**
 * @author Gautier Koscielny
 * Selenium test for gene query coverage ensuring the search result work for 
 * any gene symbol from the Solr core
 */
@RunWith(value = Parameterized.class)
public class SearchPageTest {
	private WebDriver driver;
	
	private String baseUrl;
	private String solrPath;
	private StringBuffer verificationErrors = new StringBuffer();
	private static final String SELENIUM_SERVER_URL ="http://mi-selenium-win.windows.ebi.ac.uk:4443/wd/hub";
	
	private ArrayList<String> errLog = new ArrayList<String>();
	
	private HashMap<String, String> params = new HashMap<String, String>();
	private List<String> paramList = new ArrayList<String>();
	private List<String> cores = new ArrayList<String>();
	
	@Before
	public void setUp() throws Exception {
		
		baseUrl = "https://dev.mousephenotype.org";		
		solrPath = baseUrl + "/mi/impc/dev/solr";
		
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
	
	public SearchPageTest(DesiredCapabilities browser) throws MalformedURLException {
		driver = new RemoteWebDriver(
                new URL(SELENIUM_SERVER_URL), browser);
		System.out.println("browser for testing is:"+browser);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	
	@Parameters
	 public static Collection<Object[]> data() {
	  // Object[][] data = new Object[][] { { DesiredCapabilities.firefox() }, { DesiredCapabilities.internetExplorer() }, { DesiredCapabilities.chrome() } };
	   Object[][] data = new Object[][] { { DesiredCapabilities.firefox() }};
	   return Arrays.asList(data);
	 }
	
	@Test
	//@Ignore
	public void testExample() throws Exception {
		// <span class="gSymbol">
		String geneParameterSymbol = "Acp2";
		driver.get(baseUrl + "/data/search?q=" + geneParameterSymbol);
		
		// //li[contains(@class, 'ui-autocomplete')]/li[1]/a
		String geneSymbol = driver.findElement(By.xpath("//span[contains(@class, 'gSymbol')]")).getText();
		assertEquals(geneSymbol, geneParameterSymbol);		
	}
	
	@Test
	@Ignore
	public void testTickingFacetFilters() throws Exception {
		
		for (Map.Entry entry : params.entrySet()) {		  
		    		    
		    String facet = entry.getKey().toString();
		    String queryStr = baseUrl + "/data/search#" + entry.getValue();	
		    //System.out.println(queryStr);
		    driver.get(queryStr);		
		    driver.navigate().refresh();	
		    
			// first input element of a subfacet
			new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.flist")));
			String filterVals1 = driver.findElement(By.cssSelector("div.flist li#" + facet + " li.fcat input")).getAttribute("rel");
			
			String elem1 = "div.flist li#" + facet + " li.fcat input";
			driver.findElement(By.cssSelector(elem1)).click();
			if ( driver.findElement(By.cssSelector(elem1)).isSelected() ){
				//System.out.println(facet + " filter checked");
			}
			
			String elem2 = "ul#facetFilter li li.ftag a";
			String filterVals2 = driver.findElement(By.cssSelector(elem2)).getAttribute("rel");
			
			assertEquals(filterVals1, filterVals2);			
			
			// now tests unchecking filemotionter also unchecks inputbox
			driver.findElement(By.cssSelector(elem2)).click();
			if ( ! driver.findElement(By.cssSelector(elem1)).isSelected() ){
				//System.out.println(facet + " filter unchecked");
			}
			
			System.out.println(facet + " OK: behavioral test - input filter invokes filter listing");
		}		
	}
		
	@Test
	//@Ignore
	public void testAllGeneSymbols() throws Exception {

		String newQueryString = "/gene/select?q=marker_symbol:*&fl=marker_symbol&wt=json";
		int startIndex = 0;
		int nbRows = 10;
		newQueryString+="&start="+startIndex+"&rows="+nbRows;
		System.out.println("newQueryString=" + newQueryString);

		JSONObject geneResults = JSONRestUtil.getResults(solrPath + newQueryString);
		JSONArray docs = JSONRestUtil.getDocArray(geneResults);
		if (docs != null) {
			int size = docs.size();
			for (int i=0; i<size; i++) {
				String geneParameterSymbol = docs.getJSONObject(i).getString("marker_symbol");
				
				driver.get(baseUrl + "/data/search?q=marker_symbol:\"" + geneParameterSymbol + "\"");
				String geneSymbol = driver.findElement(By.xpath("//span[contains(@class, 'gSymbol')]")).getText();
				assertEquals(geneSymbol, geneParameterSymbol);
				System.out.println("OK querying by gene Symbol: " + geneParameterSymbol);
			}
		}
	}
	
	@Test	
	@Ignore
	public void testSelectedMgiIds() throws Exception {
				
		String newQueryString = "/gene/select?q=mgi_accession_id:*&fl=mgi_accession_id,marker_symbol&wt=json";
		int startIndex = 0;
		int nbRows = 1;
		newQueryString+="&start="+startIndex+"&rows="+nbRows;
		System.out.println("newQueryString=" + newQueryString);

		JSONObject geneResults = JSONRestUtil.getResults(solrPath + newQueryString);
		System.out.println(geneResults);
		JSONArray docs = JSONRestUtil.getDocArray(geneResults);
			
		if (docs != null) {
			int size = docs.size();
			for (int i=0; i<size; i++) {
				String mgiId = docs.getJSONObject(i).getString("mgi_accession_id");
				String symbol = docs.getJSONObject(i).getString("marker_symbol");				
				driver.get(baseUrl + "/data/search?q=" + mgiId);
				//WebElement geneLink = driver.findElement(By.xpath("//a[@href='/data/genes/" + mgiId + "'"));				
				WebElement geneLink = driver.findElement(By.cssSelector("div.geneCol a").linkText(symbol)); 
				assert(geneLink != null);	
				System.out.println("OK: query by MGI accession id: " + mgiId);
			}
		}
	}
	
	@Test
	@Ignore
	public void testPhrase() throws Exception {
				
		driver.get(baseUrl + "/data/search?q=grip strength");	
			
		new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.id("geneGrid_info"))); 
		System.out.println("OK: checking phrease grip strength. Found: " + driver.findElement(By.cssSelector("span#resultCount a")).getText());
	}
	
	@Test
	@Ignore
	public void testPhraseInQuotes() throws Exception {
				
		driver.get(baseUrl + "/data/search?q=\"zinc finger protein\"");	
			
		new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.id("geneGrid_info"))); 
		System.out.println("OK: query by phrease in quotes for \"zinc finger protein\". Found: " + driver.findElement(By.cssSelector("span#resultCount a")).getText());
	}

	@Test
	@Ignore
	public void testLeadingWildcard() throws Exception {
				
		driver.get(baseUrl + "/data/search?q=*rik");	
			
		new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.id("geneGrid_info"))); 
		System.out.println("OK: query by leading wildcard for *rik. Found: " + driver.findElement(By.cssSelector("span#resultCount a")).getText());		
	}
	
	@Test
	@Ignore
	public void testTrailingWildcard() throws Exception {
				
		driver.get(baseUrl + "/data/search?q=hox*");	
			
		new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.id("geneGrid_info"))); 
		System.out.println("OK: query by trailing wildcard for hox*. Found: " + driver.findElement(By.cssSelector("span#resultCount a")).getText());		
	}
	
	@Test
	@Ignore
	public void testPaginatino() throws Exception {	
				
		for (String core : cores ){		
			System.out.println("TESTING core: "+ core);
			//System.out.println(baseUrl + "/data/search#" + params.get(core));
			
			driver.get(baseUrl + "/data/search#" + params.get(core));		
			driver.navigate().refresh();			
			driver.findElement(By.xpath("//div[contains(@class, 'dataTables_paginate')]/descendant::li[a/text()='2']")).click();
			
			// wait for ajax call			
			new WebDriverWait(driver, 25).until(ExpectedConditions.visibilityOfElementLocated(By.id(core+"Grid_info")));
			String wantedElement = core.equals("images") ? "span#annotCount" : "span#resultCount a";
			
			//driver.findElement(By.xpath("//div[contains(@class, 'dataTables_paginate')]/descendant::li[a/text()='2']")).click();
			//System.out.println("CHK1: " +  driver.findElement(By.cssSelector(wantedElement)).getText());
			//System.out.println("CHK2: " +  driver.findElement(By.cssSelector("div#"+core+"Grid_info")).getText());			
			String paginationInfo = driver.findElement(By.cssSelector("div#"+core+"Grid_info")).getText();
								
			String[] parts = driver.findElement(By.cssSelector(wantedElement)).getText().split(" ");			
			int dataTalbeFoundCount = Integer.parseInt(parts[0]);
					
			assertEquals(paginationInfo, "Showing 11 to 20 of " + NumberFormat.getNumberInstance(Locale.US).format(dataTalbeFoundCount) + " entries");
			System.out.println(core + " OK");		
		}
	}
	
	@Test
	@Ignore
	public void testFacetCounts() throws Exception {	
				
		for (String s : paramList ){	
			
			JSONObject geneResults = JSONRestUtil.getResults(solrPath + s);	
			
			int facetCountFromSolr = geneResults.getJSONObject("response").getInt("numFound");			
			String core = geneResults.getJSONObject("responseHeader").getJSONObject("params").getString("core");
			//String fq = geneResults.getJSONObject("responseHeader").getJSONObject("params").getString("fq");
			System.out.println(core + " num found: "+ facetCountFromSolr);
			
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
			assertEquals(facetCountFromSolr, dataTalbeFoundCount);
			System.out.println("OK: comparing facet counts for " + core);			
		}
	}
	
	@After
	public void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
}
