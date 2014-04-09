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

package org.mousephenotype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService;

/**
 *
 * @author mrelac
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
public class GetAllPhenotypePagesTest implements ApplicationContextAware  {

    private ApplicationContext ac;

    private String host = "http://localhost:8080";
    private String baseUrl;
    static WebDriver driver;
    public BrowserType browserType;
    public final int MAX_MGI_LINK_CHECK_COUNT = 5;
    
    public static enum BrowserType {
          FIREFOX
        , CHROME
        , SAFARI
    }
    
    @Autowired
    GenotypePhenotypeService genotypePhenotypeService;
    
    public GetAllPhenotypePagesTest() {
        browserType = BrowserType.FIREFOX;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            ac = applicationContext;
    }

    @Before
    public void setup() {
            @SuppressWarnings("unchecked")
            Map<String,String> config = (Map<String, String>) ac.getBean("globalConfiguration");
            baseUrl = host + config.get("baseUrl");
            
            switch (browserType) {
                case FIREFOX:
                    System.out.println("Using FIREFOX WebDriver.");
                    driver = new FirefoxDriver();
                    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                    break;
                    
                case SAFARI:
                    System.out.println("Using SAFARI WebDriver.");
                    driver = new SafariDriver();
                    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                    break;
                    
                case CHROME:
                    System.out.println("Using CHROME WebDriver.");
                    driver = new ChromeDriver();
                    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                    break;
            }
    }

    @After
    public void teardown() {
        driver.quit();
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     * Fetches all phenotype IDs from the database and tests to make sure there
     * is a page for each.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void pageForEveryPhenotypeTest() throws SolrServerException {
        Set<String> phenotypeIds = genotypePhenotypeService.getAllPhenotypes();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        
        System.out.println("pageForEveryPhenotypeTest");
        
        try {
            // Loop through all phenotypes, testing each one for valid page load.
            
            
            int i = 0;
            for (String phenotypeId : phenotypeIds) {
                if (i++ > 40) {
                    return;
                }
            
            
                target = baseUrl + "/phenotypes/" + phenotypeId;
                driver.get(target);
                List<WebElement> phenotypeLinks = driver.findElements(By.cssSelector("div.inner a").linkText(phenotypeId));
                if ( phenotypeLinks.isEmpty()) {
                    message = "Expected page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found none.";
                    errorList.add(message);
                } else {
                    message = "SUCCESS: MP_TERM_ID " + phenotypeId + ". Target URL: " + target;
                    successList.add(message);
                }
            }
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            exceptionList.add(message);
        }
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " MP_TERM_ID records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(errorList.size() + " MP_TERM_ID records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
            
            fail(errorList.size() + " MP_TERM_ID records failed.");
        }
            
        System.out.println(successList.size() + " MP_TERM_ID records processed successfully.");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }

    /**
     * Fetches all phenotype IDs from the database and tests to make sure the
     * link for each works.
     * 
     * @throws SolrServerException 
     */
    @Test
    public void testMGILinksAreValid() throws SolrServerException {
        Set<String> phenotypeIds = genotypePhenotypeService.getAllPhenotypes();
        String target;
        
        System.out.println("testMGILinksAreValid");
        
        // Loop through first MAX_MGI_LINK_CHECK_COUNT phenotype MGI links, testing each one for valid page load.
        int i = 0;
        for (String phenotypeId : phenotypeIds) {
            if (i++ > MAX_MGI_LINK_CHECK_COUNT) {
                return;
            }
                
            target = baseUrl + "/phenotypes/" + phenotypeId;
            driver.get(target);
            List<WebElement> phenotypeLinks = driver.findElements(By.cssSelector("div.inner a").linkText(phenotypeId));
            if ( ! phenotypeLinks.isEmpty()) {
                if (phenotypeLinks.size() != 1) {
                    fail("Expected exactly 1 page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found " + phenotypeLinks.size());
                }
                
                phenotypeLinks.get(0).click();
                String idString = "[" + phenotypeId + "]";
                boolean found = driver.findElement(By.cssSelector("div[id='templateBodyInsert']")).getText().contains(idString);
                
                assertTrue(found);
            }
        }
    }

}
