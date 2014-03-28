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

import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import uk.ac.ebi.generic.util.JSONRestUtil;

/**
 * @author Gautier Koscielny
 * Selenium test for graph query coverage ensuring each graph display work for 
 * any given gene accession/parameter/zygosity from the Solr core
 */

public class StatsPageTest {
	private WebDriver driver;
	private String baseUrl;
	private String geneSolrUrl;
	private StringBuffer verificationErrors = new StringBuffer();

	@Before
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		baseUrl = "https://dev.mousephenotype.org";
		geneSolrUrl = baseUrl + "/mi/impc/dev/solr";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
	//@Test
	public void testExample() throws Exception {
		// <span class="gSymbol">
		String mgiGeneAcc = "MGI:2444584";
		String impressParameter = "ESLIM_001_001_004";
		String zygosity= "homozygote";
		String geneSymbol = "Mysm1";
		// <div class='topic'>Gene: Mysm1</div>
		driver.get(baseUrl + "/data/charts?accession=" + mgiGeneAcc + "&parameterId=" + impressParameter + "&zygosity=" + zygosity);
		String title = driver.findElement(By.xpath("//div[contains(@class, 'topic')]")).getText();
		assertEquals(title, "Gene: " + geneSymbol);
	}

	@Test
	public void testAllGraphs() throws Exception {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=biologicalSampleGroup%3A+%22experimental%22&fl=geneAccession,parameterStableId,zygosity,geneSymbol&rows=20&wt=json&indent=true&facet=on&&facet.pivot=geneAccession,parameterStableId,zygosity,geneSymbol
		//http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=biologicalSampleGroup%3A+%22experimental%22&fl=geneAccession,parameterStableId,zygosity,geneSymbol&rows=20&wt=json&indent=true
		
		String newQueryString = "/experiment/select?q=biologicalSampleGroup%3A+%22experimental%22&fl=geneAccession,parameterStableId,zygosity,geneSymbol&rows=20&wt=json&indent=true";
		int startIndex = 0;
		int nbRows = 10;
		newQueryString+="&start="+startIndex+"&rows="+nbRows;
		System.out.println("newQueryString=" + newQueryString);

		JSONObject result = JSONRestUtil.getResults(geneSolrUrl + newQueryString);
		JSONArray docs = JSONRestUtil.getDocArray(result);
		System.out.println(docs.size());
		
		if (docs != null) {
			int size = docs.size();
			for (int i=0; i<size; i++) {
				String mgiGeneAcc = docs.getJSONObject(i).getString("geneAccession");
				String impressParameter = docs.getJSONObject(i).getString("parameterStableId");
				String zygosity= docs.getJSONObject(i).getString("zygosity");
				String geneSymbol = docs.getJSONObject(i).getString("geneSymbol");
				
				System.out.println(geneSymbol + "\t" + baseUrl + "/data/charts?accession=" + mgiGeneAcc + "?parameterId=" + impressParameter + "&zygosity=" + zygosity);
	
				driver.get(baseUrl + "/data/charts?accession=" + mgiGeneAcc + "&parameterId=" + impressParameter + "&zygosity=" + zygosity);
				String title = driver.findElement(By.xpath("//div[contains(@class, 'topic')]")).getText();
				assertEquals(title, "Gene: " + geneSymbol);
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
