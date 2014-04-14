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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService;

/**
 *
 * @author mrelac
 */
//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
public class GetAllPhenotypePagesTest implements ApplicationContextAware  {

    private ApplicationContext ac;

    private String host;
    private String baseUrl;
    static WebDriver driver;
    public BrowserType browserType;
    public final int MAX_MGI_LINK_CHECK_COUNT = 5;                              // -1 means test all links.
    public final int MAX_PHENOTYPE_TEST_PAGE_COUNT = -1;                        // -1 means test all pages.
    public DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private TestContextManager testContextManager;
    private Process process = null;
    
    public static enum BrowserType {
          CHROME
        , FIREFOX
        , SAFARI
    }
    
    @Autowired
    GenotypePhenotypeService genotypePhenotypeService;
    
    public GetAllPhenotypePagesTest(BrowserType browserType) {
        this.browserType = browserType;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ac = applicationContext;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
          { BrowserType.CHROME  }
        , { BrowserType.FIREFOX  }
        , { BrowserType.SAFARI  }
        });
    }
    @Before
    public void setup() throws Exception {
        this.testContextManager = new TestContextManager(getClass());
        this.testContextManager.prepareTestInstance(this);
        
        @SuppressWarnings("unchecked")
        Map<String,String> config = (Map<String, String>) ac.getBean("globalConfiguration");
        host = "http://dev.mousephenotype.org";
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
                java.net.URL url = new java.net.URL("http://localhost:9515");
                process = runChromeDriver("/Applications/selenium/chromedriver");
                driver = new RemoteWebDriver(url, DesiredCapabilities.chrome());
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                break;
        }
    }

    @After
    public void teardown() {
        switch (browserType) {
            case CHROME:
                process.destroy();
        }
        
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
        
        System.out.println(dateFormat.format(new Date()) + ": pageForEveryPhenotypeTest started.");
        
        try {
            // Loop through all phenotypes, testing each one for valid page load.
            int i = 0;
            for (String phenotypeId : phenotypeIds) {
                if ((MAX_PHENOTYPE_TEST_PAGE_COUNT != -1) && (i++ >= MAX_PHENOTYPE_TEST_PAGE_COUNT)) {
                    break;
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
        }
            
        System.out.println(dateFormat.format(new Date()) + ": " + successList.size() + " MP_TERM_ID records processed successfully.\n\n");
        
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
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        
        System.out.println(dateFormat.format(new Date()) + ": testMGILinksAreValid started.");
        
        try {
            // Loop through first MAX_MGI_LINK_CHECK_COUNT phenotype MGI links, testing each one for valid page load.
            int i = 0;
            for (String phenotypeId : phenotypeIds) {
                if ((MAX_MGI_LINK_CHECK_COUNT != -1) && (i++ >= MAX_MGI_LINK_CHECK_COUNT)) {
                    break;
                }

                target = baseUrl + "/phenotypes/" + phenotypeId;
                driver.get(target);
                List<WebElement> phenotypeLinks = driver.findElements(By.cssSelector("div.inner a").linkText(phenotypeId));
                if (phenotypeLinks.isEmpty()) {
                    message = "No page found for MP_TERM_ID " + phenotypeId + "(" + target + ")";
                    errorList.add(message);
                    continue;
                }
                    
                if (phenotypeLinks.size() != 1) {
                    message = "Expected exactly 1 page for MP_TERM_ID " + phenotypeId + "(" + target + ") but found " + phenotypeLinks.size();
                    errorList.add(message);
                }

                phenotypeLinks.get(0).click();
                String idString = "[" + phenotypeId + "]";
                boolean found = driver.findElement(By.cssSelector("div[id='templateBodyInsert']")).getText().contains(idString);
                if ( ! found) {
                    message = "div id 'templateBodyInsert' not found.";
                    errorList.add(message);
                } else {
                    message = "SUCCESS: MGI link OK for " + phenotypeId + ". Target URL: " + target;
                    successList.add(message);
                }
            }
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            exceptionList.add(message);
        }
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " MGI links failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(errorList.size() + " MGI links caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
            
        System.out.println(dateFormat.format(new Date()) + ": " + successList.size() + " MGI links processed successfully.\n\n");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }
    
    
    // PRIVATE METHODS
    
    
    private static Process runChromeDriver(String command) {
        Process process = null;
        
        try {
                process = Runtime.getRuntime().exec(command);

        } catch (Exception e) {
                e.printStackTrace();
        }
        
        return process;
    }

}
