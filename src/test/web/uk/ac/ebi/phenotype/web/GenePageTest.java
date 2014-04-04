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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.zookeeper.Op.Check;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

import uk.ac.ebi.generic.util.JSONRestUtil;

/**
 * @author Gautier Koscielny
 * Selenium test for graph query coverage ensuring each graph display work for 
 * any given gene accession/parameter/zygosity from the Solr core
 */

@RunWith(value = Parameterized.class)
public class GenePageTest {
	
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
	
	public GenePageTest(DesiredCapabilities browser) throws MalformedURLException {
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
	public void testAkt2() throws Exception {
		// <span class="gSymbol">
		String mgiGeneAcc = "MGI:104874";
		driver.get(baseUrl + "/data/genes/"+mgiGeneAcc);
		//String title = driver.findElement(By.xpath("//*[contains(concat(\" \", normalize-space(@class), \" \"), \"title document\")]")).getText();
		String topTextString=driver.findElement(By.id("top")).getText();
		System.out.println("top title="+topTextString);
		assertTrue(topTextString.contains("Akt2"));
		
		List<WebElement> sectionTitles = driver.findElements(By.xpath("//*[contains(concat(\" \", normalize-space(@class), \" \"), \"title\")]"));
		assertTrue(sectionTitles.size()==5);//should be five sections visible for Akt2 which have title classes including the gene one at the top
		String [] listOfSectionTitles= {"Gene: Akt2","Phenotype associations for Akt2","Phenotype Associated Images","Expression","Order Mouse and ES Cells"};
		List<String> sectionTitleCheckFor=new ArrayList<String>(Arrays.asList(listOfSectionTitles));
//		section titles=Gene: Akt2   
//				section titles=Phenotype associations for Akt2
//				section titles=Phenotype Associated Images
//				section titles=Expression
//				section titles=Order Mouse and ES Cells
		for(WebElement webElement: sectionTitles) {
			String text=webElement.getText();
			System.out.println("section titles=|"+text+"|");
			assertTrue(sectionTitleCheckFor.contains(text));
		}
		List<WebElement> buttons=driver.findElements(By.className("btn"));
		assertTrue(buttons.size()>1);//should be at least 2 buttons "register interest" and "order"
		for(WebElement webElement: buttons) {
			String text=webElement.getText();
			System.out.println("button text="+text);
			assertTrue(text.equals("Login to register interest") || text.equals("Order"));
		}
		
		//check the phenotype associations box
		WebElement abnormalities=driver.findElement(By.className("abnormalities"));// we have abnormalities for akt2?
		assertTrue(abnormalities.findElements(By.className("filterTrigger")).size()>4);//check the size filterTrigger class elements - which equates to the ass phenotypes
		assertTrue(abnormalities!=null);
		
		//top_level_mp_term_name check this filter exists
		Select selectTopLevel = new Select(driver.findElement(By.id("top_level_mp_term_name")));
		assertTrue(selectTopLevel.getOptions().size()==7);//currently 7 options exist for this gene
		Select selectResource = new Select(driver.findElement(By.id("resource_fullname")));
		assertTrue(selectResource.getOptions().size()==3);//currently 7 options exist for this gene
//		select.deselectAll();
//		select.selectByVisibleText("Edam");
		
		
		//check we have the image sections we expect?
		//get the accordion headings seems the easiest way rather than complicated css
		List<WebElement> accordions=driver.findElements(By.className("accordion-heading"));
		System.out.println("accordions size="+accordions.size());
		String [] listOfAccordionHeaders= {"Xray (167)","Tail Epidermis Wholemount (5)","Musculoskeletal System (2)","Nervous System (2)","Adipose Tissue (1)","Cardiovascular System (1)","Digestive System (1)","Integumental System (1)","Renal/urinary System (1)","Reproductive System (1)","Respiratory System (1)"};
		List<String> accHeaderStrings=new ArrayList<String>(Arrays.asList(listOfAccordionHeaders));
		for(WebElement webElement: accordions) {
			String text=webElement.getText();
			System.out.println("accordion heading text="+text);
			assertTrue(accHeaderStrings.contains(text));
		}
		//currently this is what we have as accordion headers
//		accordion heading text=Xray (167)
//				accordion heading text=Tail Epidermis Wholemount (5)
//				accordion heading text=Musculoskeletal System (2)
//				accordion heading text=Nervous System (2)
//				accordion heading text=Adipose Tissue (1)
//				accordion heading text=Cardiovascular System (1)
//				accordion heading text=Digestive System (1)
//				accordion heading text=Integumental System (1)
//				accordion heading text=Renal/urinary System (1)
//				accordion heading text=Reproductive System (1)
//				accordion heading text=Respiratory System (1)
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
