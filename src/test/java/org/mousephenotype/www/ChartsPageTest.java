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

package org.mousephenotype.www;


import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.generic.util.JSONRestUtil;

/**
 * @author Gautier Koscielny
 * Selenium test for graph query coverage ensuring each graph display work for 
 * any given gene accession/parameter/zygosity from the Solr core
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class ChartsPageTest {
	
    @Autowired
    protected String baseUrl;
    
    @Autowired
    protected WebDriver driver;
    static protected WebDriver staticDriver;
    
    @Autowired
    protected String seleniumUrl;
    
    @Resource(name="globalConfiguration")
    private Map<String, String> config;
    
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    //String baseUrl = "https://dev.mousephenotype.org";
    
	
	@Before
	public void setUp() throws Exception {
		printTestEnvironment();
                staticDriver = driver;	
	}
        
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
        
        System.out.println("\nTESTING AGAINST " + browserName + " version " + version + " on platform " + platform);
        System.out.println("seleniumUrl: " + seleniumUrl);
    }
	
	
	 
	@Test
	public void testExampleCategorical() throws Exception {
		// <span class="gSymbol">

                String mgiGeneAcc = "MGI:2444584";
		String impressParameter = "ESLIM_001_001_004";
		String zygosity= "homozygote";
		String geneSymbol = "Mysm1";
                driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		// <div class='topic'>Gene: Mysm1</div>
                String tempUrl=baseUrl+"/charts?accession=" + mgiGeneAcc + "&parameter_stable_id=" + impressParameter + "&zygosity=" + zygosity;
                System.out.println("tempUrl="+tempUrl);
		driver.get(tempUrl);
		String title = driver.findElement(By.className("title")).getText();
                System.out.println("title="+title+"  geneSymbol="+geneSymbol);
		assertTrue(title.contains(geneSymbol));

                //test another example
                //http://localhost:8080/phenotype-archive/charts?accession=MGI:2444584&parameter_stable_id=ESLIM_001_001_004&zygosity=homozygote
                
		
	}
        
        @Test
	public void testExampleCategorical2() throws Exception {
		// <span class="gSymbol">

                String mgiGeneAcc = "MGI:98373";
		String impressParameter = "M-G-P_014_001_001";
		String zygosity= "homozygote";
		String geneSymbol = "Sparc";
                driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		// <div class='topic'>Gene: Mysm1</div>
                String tempUrl=baseUrl+"/charts?accession=" + mgiGeneAcc + "&parameter_stable_id=" + impressParameter + "&zygosity=" + zygosity;
                System.out.println("tempUrl="+tempUrl);
		driver.get(tempUrl);
		String title = driver.findElement(By.className("title")).getText();
                System.out.println("title="+title+"  geneSymbol="+geneSymbol);
		assertTrue(title.contains(geneSymbol));

                //test another example
                //http://localhost:8080/phenotype-archive/charts?accession=MGI:98373&parameter_stable_id=M-G-P_014_001_001&zygosity=homozygote&phenotyping_center=WTSI&pipeline_stable_id=M-G-P_001
                
		
	}

        @Ignore
	@Test
	public void testAllGraphs() throws Exception {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=biologicalSampleGroup%3A+%22experimental%22&fl=geneAccession,parameterStableId,zygosity,geneSymbol&rows=20&wt=json&indent=true&facet=on&&facet.pivot=geneAccession,parameterStableId,zygosity,geneSymbol
		//http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=biologicalSampleGroup%3A+%22experimental%22&fl=geneAccession,parameterStableId,zygosity,geneSymbol&rows=20&wt=json&indent=true
		
		String newQueryString = "/experiment/select?q=biological_sample_group%3A+%22experimental%22&fl=gene_accession_id,parameter_stable_id,zygosity,gene_symbol&rows=20&wt=json&indent=true";
		int startIndex = 0;
		int nbRows = 10;
		newQueryString+="&start="+startIndex+"&rows="+nbRows;
		System.out.println("newQueryString=" + newQueryString);
String url=config.get("internalSolrUrl")+ newQueryString;
System.out.println("solr url="+url);
JSONObject result = JSONRestUtil.getResults(url);
		JSONArray docs = JSONRestUtil.getDocArray(result);
		System.out.println(docs.size());
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		
			int size = docs.size();
			//all docs are the same for this at the mo so just try one - need to alter this to test different graph types categorical, unidimensional, time_series and scatter
			for (int i=0; i<1; i++) {
				String mgiGeneAcc = docs.getJSONObject(i).getString("gene_accession_id");
				String impressParameter = docs.getJSONObject(i).getString("parameter_stable_id");
				String zygosity= docs.getJSONObject(i).getString("zygosity");
				String geneSymbol = docs.getJSONObject(i).getString("gene_symbol");
				String graphUl=baseUrl + "/data/charts?accession=" + mgiGeneAcc + "&parameter_stable_id=" + impressParameter + "&zygosity=" + zygosity;
				System.out.println(geneSymbol + "\t" + graphUl);
				driver.get(graphUl);
				String title = driver.findElement(By.className("title")).getText();
				//for reasoning on xpath identifier see http://stackoverflow.com/questions/8808921/selecting-a-css-class-with-xpath
				//*[contains(concat(" ", normalize-space(@class), " "), " foo ")]
				assertTrue(title.contains(geneSymbol));
			}
		
		
	}

	
}
