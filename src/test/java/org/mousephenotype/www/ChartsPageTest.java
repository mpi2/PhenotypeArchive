/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mousephenotype.www;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.After;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author Gautier Koscielny Selenium test for graph query coverage ensuring
 * each graph display work for any given gene accession/parameter/zygosity from
 * the Solr core
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-config.xml"})
public class ChartsPageTest {

    @Autowired
    protected String baseUrl;

    @Autowired
    protected WebDriver driver;

    @Autowired
    protected String seleniumUrl;

    @Before
    public void setUp() throws Exception {
        TestUtils.printTestEnvironment(driver, seleniumUrl);

        driver.navigate().refresh();
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testExampleCategorical() throws Exception {
        String testName = "testExampleCategorical";
        
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        Date start = new Date();
        int targetCount = 1;

        String mgiGeneAcc = "MGI:2444584";
        String impressParameter = "ESLIM_001_001_004";
        String zygosity = "homozygote";
        String geneSymbol = "Mysm1";
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        // <div class='topic'>Gene: Mysm1</div>
        String tempUrl = baseUrl + "/charts?accession=" + mgiGeneAcc + "&parameter_stable_id=" + impressParameter + "&zygosity=" + zygosity;
//        System.out.println("tempUrl=" + tempUrl);
        driver.get(tempUrl);
        String title = driver.findElement(By.className("title")).getText();
//        System.out.println("title=" + title + "  geneSymbol=" + geneSymbol);
        if ( ! title.contains(geneSymbol)) {
            errorList.add("ERROR: Expected title to contain '" + geneSymbol + "' but was '" + title + "'.  URL: " + tempUrl + "'");
        } else {
            successList.add("OK");
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList,  targetCount, 1);
        System.out.println();
    }

    @Test
    public void testExampleCategorical2() throws Exception {
        String testName = "testExampleCategorical2";
        
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        Date start = new Date();
        int targetCount = 1;

        String mgiGeneAcc = "MGI:98373";
        String impressParameter = "M-G-P_014_001_001";
        String zygosity = "homozygote";
        String geneSymbol = "Sparc";
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        // <div class='topic'>Gene: Mysm1</div>
        String tempUrl = baseUrl + "/charts?accession=" + mgiGeneAcc + "&parameter_stable_id=" + impressParameter + "&zygosity=" + zygosity;
//        System.out.println("tempUrl=" + tempUrl);
        driver.get(tempUrl);
        String title = driver.findElement(By.className("title")).getText();
//        System.out.println("title=" + title + "  geneSymbol=" + geneSymbol);
        if ( ! title.contains(geneSymbol)) {
            errorList.add("ERROR: Expected title to contain '" + geneSymbol + "' but was '" + title + "'.  URL: " + tempUrl + "'");
        } else {
            successList.add("OK");
        }
            
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList,  targetCount, 1);
        System.out.println();
    }

}
