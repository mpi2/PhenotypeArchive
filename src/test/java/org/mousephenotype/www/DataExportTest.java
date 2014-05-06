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

import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;

/**
 *
 * @author mrelac
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
public class DataExportTest {
    
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
    public void setup() {
        printTestEnvironment();
        staticDriver = driver;
    }

    @After
    public void teardown() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
//        if (staticDriver != null) {
//            System.out.println("Closing driver.");
//            staticDriver.close();
//        }
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

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure there is a page for each. Limit the test
     * to the first MAX_GENE_TEST_PAGE_COUNT by setting it to the limit you want.
     * 
     * NOTE: This test currently only works on chrome. In order to run this test
     * successfully, we need to clear the downloads folder first. A better solution
     * is to find a way to programatically suppress the download dialog and later,
     * when the test is complete, to remove the download file(s).
     * 
     * For now (01-May-2014) we shall mark this test @Ignore.
     * 
     * @throws SolrServerException 
     */
    @Test
@Ignore
    public void testPageForGeneIds() throws SolrServerException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        Date stop;
        
        System.out.println(dateFormat.format(start) + ": testPageForGeneIds started.");
        

        target = "https://dev.mousephenotype.org/data/charts?accession=MGI:1921354&parameter_stable_id=IMPC_CBC_014_001&zygosity=homozygote&phenotyping_center=WTSI&pipeline_stable_id=MGP_001";

        try {
                driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
                driver.get(target);
                driver.navigate().refresh();
                driver.findElement(By.cssSelector("button.tsv_phenoAssoc")).click();
                
                Thread.currentThread().sleep(5000);
                
                System.out.println("Done waiting " );

                driver.navigate().to("file:///C:/Users/local_admin/Downloads/graphDataDump_MGI_1921354.tsv");
                
                if (! driver.getPageSource().contains("experimental\t")){
                	message = "Expected experimental or control data for graph download at " + target + " but found none.";
                    errorList.add(message);
                } 
                if ( driver.getPageSource().split("\n").length <= 1 ){
                	message = "Expected experimental data for graph download at " + target + " but found none.";
                    errorList.add(message);
                }
            	System.out.println(driver.getPageSource().split("\n").length);
                
        } catch (Exception e) {
        	message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
        	exceptionList.add(message);
        }
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " MARKER_ACCESSION_ID records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }
    
}
