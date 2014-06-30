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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.www.testing.model.DataReader;
import org.mousephenotype.www.testing.model.DataReaderTsv;
import org.mousephenotype.www.testing.model.DataReaderXls;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;
import uk.ac.ebi.phenotype.util.Utils;

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
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class DataExportTest {
    
    @Autowired
    protected GenotypePhenotypeService genotypePhenotypeService;
    

    @Autowired
    protected String baseUrl;
    
    @Autowired
    protected WebDriver driver;
    
    @Autowired
    protected String seleniumUrl;
    
    @Autowired
    protected TestUtils testUtils;
    
    private final int TIMEOUT_IN_SECONDS = 4;
    private final int THREAD_WAIT_IN_MILLISECONDS = 1000;
    
    private int timeout_in_seconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    @Before
    public void setup() {
        if (Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeout_in_seconds = Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            thread_wait_in_ms = Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));
        
        TestUtils.printTestEnvironment(driver, seleniumUrl);
        driver.navigate().refresh();
        try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
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
//@Ignore
    public void testDownloadTsv() throws SolrServerException {
        String testName = "testDownloadTsv";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        DataReader dataReader = null;
        
geneIds.add("MGI:1921354");
        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
target = "http://dev.mousephenotype.org/data/export?mpId=%22MP%3A0005266%22&externalDbId=3&fileName=gene_variants_with_phen_MP_0005266&solrCoreName=genotype-phenotype&dumpMode=all&baseUrl=http%3A%2F%2Fdev.mousephenotype.org%2Fdata%2Fphenotypes%2FMP%3A0005266&page=phenotype&gridFields=marker_symbol%2Callele_symbol%2Czygosity%2Csex%2Cprocedure_name%2Cresource_name%2Cphenotyping_center%2Cparameter_stable_id%2Cmp_term_name%2Cmarker_accession_id%2C+parameter_name&params=qf%3Dauto_suggest%26defType%3Dedismax%26wt%3Djson%26rows%3D100000%26q%3D*%3A*%26fq%3D(mp_term_id%3A%22MP%3A0005266%22%2BOR%2Btop_level_mp_term_id%3A%22MP%3A0005266%22)&fileType=tsv&_=1403882094799";

            System.out.println("gene[" + i + "] URL: " + target);

            try {
                URL url = new URL(target);
                dataReader = new DataReaderTsv(url);
                dataReader.open();
                List<String> line;
                while ((line = dataReader.getLine()) != null) {
                    for (int index = 0; index < line.size(); index++) {
                        if (index > 0)
                            System.out.print("\t");
                        System.out.print(line.get(index));
                    }
                    System.out.println();
                }
            } catch (IOException e) {
                System.out.println("EXCEPTION: " + e.getLocalizedMessage());
            } finally {
                try {
                    if (dataReader != null)
                        dataReader.close();
                } catch (IOException e) {
                    System.out.println("EXCEPTION: " + e.getLocalizedMessage());
                }
            }
            
            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
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
//@Ignore
    public void testDownloadXls() throws SolrServerException {
        String testName = "testDownloadXls";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList();
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        DataReader dataReader = null;
        
geneIds.add("MGI:1921354");
        int targetCount = testUtils.getTargetCount(testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");
        
        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;
            
            target = baseUrl + "/genes/" + geneId;
target = "http://dev.mousephenotype.org/data/export?mpId=%22MP%3A0005266%22&externalDbId=3&fileName=gene_variants_with_phen_MP_0005266&solrCoreName=genotype-phenotype&dumpMode=all&baseUrl=http%3A%2F%2Fdev.mousephenotype.org%2Fdata%2Fphenotypes%2FMP%3A0005266&page=phenotype&gridFields=marker_symbol%2Callele_symbol%2Czygosity%2Csex%2Cprocedure_name%2Cresource_name%2Cphenotyping_center%2Cparameter_stable_id%2Cmp_term_name%2Cmarker_accession_id%2C+parameter_name&params=qf%3Dauto_suggest%26defType%3Dedismax%26wt%3Djson%26rows%3D100000%26q%3D*%3A*%26fq%3D(mp_term_id%3A%22MP%3A0005266%22%2BOR%2Btop_level_mp_term_id%3A%22MP%3A0005266%22)&fileType=xls&_=1403882094801 ";
            System.out.println("gene[" + i + "] URL: " + target);

            try {
                URL url = new URL(target);
                dataReader = new DataReaderXls(url);
                dataReader.open();
                List<String> line;
                while ((line = dataReader.getLine()) != null) {
                    for (int index = 0; index < line.size(); index++) {
                        if (index > 0)
                            System.out.print("\t");
                        System.out.print(line.get(index));
                    }
                    System.out.println();
                }
            } catch (IOException e) {
                System.out.println("EXCEPTION: " + e.getLocalizedMessage());
            } finally {
                try {
                    if (dataReader != null)
                        dataReader.close();
                } catch (IOException e) {
                    System.out.println("EXCEPTION: " + e.getLocalizedMessage());
                }
            }
            
            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            
            TestUtils.sleep(thread_wait_in_ms);
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, geneIds.size());
    }
    
}
