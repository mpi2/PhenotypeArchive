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
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

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
	private String geneSolrUrl;
	private StringBuffer verificationErrors = new StringBuffer();
	
	private static final String SELENIUM_SERVER_URL ="http://mi-selenium-win.windows.ebi.ac.uk:4444/wd/hub";

	@Before
	public void setUp() throws Exception {
		
		baseUrl = "https://dev.mousephenotype.org";
		geneSolrUrl = baseUrl + "/mi/impc/dev/solr";	
		
	}
	
	
	public SearchPageTest(DesiredCapabilities browser) throws MalformedURLException {
		driver = new RemoteWebDriver(
                new URL(SELENIUM_SERVER_URL), browser);
	System.out.println("browser for testing is:"+browser);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	
	@Parameters
	 public static Collection<Object[]> data() {
	   Object[][] data = new Object[][] { { DesiredCapabilities.firefox() }, { DesiredCapabilities.internetExplorer() }, { DesiredCapabilities.chrome() } };
	   return Arrays.asList(data);
	 }
	
	@Test
	public void testExample() throws Exception {
		// <span class="gSymbol">
		String geneParameterSymbol = "Acp2";
		driver.get(baseUrl + "/data/search?q=" + geneParameterSymbol);
		// //li[contains(@class, 'ui-autocomplete')]/li[1]/a
		String geneSymbol = driver.findElement(By.xpath("//span[contains(@class, 'gSymbol')]")).getText();
		assertEquals(geneSymbol, geneParameterSymbol);
	}

	@Test
	public void testAllGeneSymbols() throws Exception {

		String newQueryString = "/gene/select?q=marker_symbol:*&fl=marker_symbol&wt=json";
		int startIndex = 0;
		int nbRows = 10;
		newQueryString+="&start="+startIndex+"&rows="+nbRows;
		System.out.println("newQueryString=" + newQueryString);

		JSONObject geneResults = JSONRestUtil.getResults(geneSolrUrl + newQueryString);
		JSONArray docs = JSONRestUtil.getDocArray(geneResults);
		if (docs != null) {
			int size = docs.size();
			for (int i=0; i<size; i++) {
				String geneParameterSymbol = docs.getJSONObject(i).getString("marker_symbol");
				System.out.println("Gene Symbol: " + geneParameterSymbol);
				driver.get(baseUrl + "/data/search?q=marker_symbol:\"" + geneParameterSymbol + "\"");
				String geneSymbol = driver.findElement(By.xpath("//span[contains(@class, 'gSymbol')]")).getText();
				assertEquals(geneSymbol, geneParameterSymbol);
			}
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
