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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;

/**
 * @author Gautier Koscielny
 * Selenium test for graph query coverage ensuring each graph display work for 
 * any given gene accession/parameter/zygosity from the Solr core
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class GenePageTest {
	
    @Autowired
    protected GenotypePhenotypeService genotypePhenotypeService;
    
    @Autowired
    protected String baseUrl;
    
    @Autowired
    protected WebDriver driver;
    static protected WebDriver staticDriver;
    
    @Autowired
    protected String seleniumUrl;
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    

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
        
        System.out.println("seleniumUrl: " + seleniumUrl);
        System.out.println("TESTING AGAINST " + browserName + " version " + version + " on platform " + platform);
        }
	 
	@Test
	public void testAkt2() throws Exception {
		// <span class="gSymbol">
		String mgiGeneAcc = "MGI:104874";
                String url=baseUrl + "/genes/"+mgiGeneAcc;
                System.out.println("test Akt2 url="+url);
                driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		driver.get(url);
		//String title = driver.findElement(By.xpath("//*[contains(concat(\" \", normalize-space(@class), \" \"), \"title document\")]")).getText();
		String topTextString=driver.findElement(By.id("top")).getText();
		System.out.println("top title="+topTextString);
		assertTrue(topTextString.contains("Akt2"));
                Thread.currentThread().sleep(3000);
                WebElement enu=driver.findElement(By.id("enu"));
    
                System.out.println("enu text="+enu.getText());
                assertTrue(enu.getText().contains("ENU"));
		
		List<WebElement> sectionTitles = driver.findElements(By.className("title"));
                System.out.println("section titles size="+sectionTitles.size());
		assertTrue(sectionTitles.size()==6);//should be five sections visible for Akt2 which have title classes including the gene one at the top
		String [] listOfSectionTitles= {"Gene: Akt2","Phenotype associations for Akt2","Phenotype Associated Images","Expression","Order Mouse and ES Cells","Pre-QC phenotype heatmap"};
		List<String> sectionTitleCheckFor=new ArrayList<String>(Arrays.asList(listOfSectionTitles));
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
		
		//test that the order mouse and es cells content from viveks team exists on the page
		WebElement orderAlleleDiv=driver.findElement(By.id("allele"));//this div is in the ebi jsp which should be populated but without the ajax call success will be empty.
		assertTrue(orderAlleleDiv.getText().length()>100);//check there is some content in the panel div
	}

//	@After
//	public void tearDown() throws Exception {
//		driver.quit();
//	}
}
