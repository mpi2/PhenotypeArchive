/**
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
 *
 * This file is intended to contain web tests for graphs - e.g. if there is an
 * IMPC link to a graph (either from a gene page or a phenotype page), there
 * should indeed be a graph present when the link is clicked.
 */

package org.mousephenotype.www;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.www.testing.model.GraphPage;
import org.mousephenotype.www.testing.model.GraphPageCategorical;
import org.mousephenotype.www.testing.model.GraphPageUnidimensional;
import org.mousephenotype.www.testing.model.GridMap;
import org.mousephenotype.www.testing.model.PageStatus;
import org.mousephenotype.www.testing.model.PhenotypeTableGene;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.MpService;
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
public class GraphPageTest {
    
    public GraphPageTest() {
    }
   
    @Autowired
    protected GeneService geneService;
    
    @Autowired
    protected MpService mpService;
    
    @Autowired
    protected GenotypePhenotypeService genotypePhenotypeService;
    
    @Autowired
    protected String baseUrl;
    
    @Autowired
    protected WebDriver driver;
    
    @Autowired
    protected String seleniumUrl;
    
    @Autowired
    protected String solrUrl;
    
    @Autowired
    private PhenotypePipelineDAO phenotypePipelineDAO;
    
    @Autowired
    protected TestUtils testUtils;
    
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    private final int TIMEOUT_IN_SECONDS = 4;
    private final int THREAD_WAIT_IN_MILLISECONDS = 20;
    
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
    
    
    // TESTS
    
    
    @Test
//@Ignore
    public void testCategoricalGraph() {
        String testName = "testCategoricalGraph";
        
        List<TestUtils.GraphData> graphUrls = TestUtils.getGraphUrls(solrUrl, ObservationType.categorical, 1000);
        graphTestEngine(testName, graphUrls);
    }
    
    @Test
//@Ignore
    public void testUnidimensionalGraph() {
        String testName = "testUnidimensionalGraph";
        
        List<TestUtils.GraphData> graphUrls = TestUtils.getGraphUrls(solrUrl, ObservationType.unidimensional, 1000);
        graphTestEngine(testName, graphUrls);
    }
    

    // PRIVATE METHODS
    

    /**
     * Given a test name and a set of gene ids to process, this method loops
     * through the gene pages, testing each one with a graph for proper graph
     * page load.
     * 
     * @param testName the test name
     * @param graphUrls  the collection of graph URLs
     */
    private void graphTestEngine(String testName, List<TestUtils.GraphData> graphUrls) {
        String target = "";
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();

        int targetCount = testUtils.getTargetCount(testName, graphUrls, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + graphUrls.size() + " records.");

        // Loop through the gene pages looking for graphs of the requested type. Test each graph.
        int graphCount = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
        int i = 0;
        for (TestUtils.GraphData graph : graphUrls) {
//if (i == 0) geneId = "MGI:104874";    // Akt2
//if (i == 1) geneId = "MGI:3643284";   // Is valid gene for which there is no page.
//if (i == 1) geneId = "MGI:1924285";
//if (i == 2) timeseriesGraphUrl = "https://dev.mousephenotype.org/data/charts?accession=MGI:104874&allele_accession=EUROALL:19&parameter_stable_id=ESLIM_004_001_002&zygosity=heterozygote&phenotyping_center=WTSI";
            if (graphCount >= targetCount) {
                break;
            }
            
            target = baseUrl + "/genes/" + graph.getGeneId();
            System.out.println("gene[" + i + "] URL: " + target);
            i++;
            
            try {
                // Get the gene page. If not found within the first 20 graphs, move on to the next gene page (so test doesn't get delayed loading pages with lots of graphs).
                driver.get(target);
                PhenotypeTableGene ptGene = new PhenotypeTableGene(driver, wait, target);
                ptGene.load();
                GridMap data = ptGene.getData();
                // Start rowIndex at 1 to skip over heading row.
                for (int rowIndex = 1; rowIndex < data.getBody().length; rowIndex++) {
                    Double pagePvalue = Utils.tryParseDouble(data.getCell(rowIndex, PhenotypeTableGene.COL_INDEX_PHENOTYPES_P_VALUE));
                    String graphUrl = data.getCell(rowIndex, PhenotypeTableGene.COL_INDEX_PHENOTYPES_GRAPH);
                    if ((pagePvalue != null) && (pagePvalue > 0.0)) {
//System.out.println("Comparing '" + pagePvalue + "' to '" + graph.getpValue() + "' (difference: " + (pagePvalue - graph.getpValue()) + ")");
                        if (TestUtils.equals(pagePvalue, graph.getpValue())) {
//System.out.println("Match!");
                            boolean loadPage = true;
                            GraphPage graphPage = new GraphPage(driver, wait, graphUrl, target, phenotypePipelineDAO, baseUrl, loadPage);
                            ObservationType graphType = graphPage.getGraphType();
                            if (graphType == graph.getGraphType()) {
                                switch (graphType) {
                                    case categorical:
                                        GraphPageCategorical graphPageCategorical = graphPage.createGraphPageCategorical();
                                        status = graphPageCategorical.validate();
                                        break;
                                        
                                    case unidimensional:
                                        GraphPageUnidimensional graphPageUnidimensional = graphPage.createGraphPageUnidimensional();
                                        status = graphPageUnidimensional.validate();
                                        break;
                                     
                                    default:
                                        throw new Exception("Validation not implemented yet for graph type '" + graphType.toString());
                                }
                                
                                if (status.hasErrors()) {
                                    System.out.println(status.toStringErrorMessages());
                                    errorList.add("Graph validation failure. URL: " + graphUrl);
                                } else {
                                    successList.add("Graph validation success. URL: " + graphUrl);
                                }
                                
                                graphCount++;
                            }
                        }
                    }
                }
                
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + graph + "(" + target + ") but found none.";
                errorList.add(message);
                TestUtils.sleep(thread_wait_in_ms);
                continue;
            }  catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                exceptionList.add(message);
                break;
            }

            if ( ! exceptionList.isEmpty()) {
                System.out.println(exceptionList.size() + " MGI_ACCESSION_ID records caused exceptions to be thrown:");
                for (String s : exceptionList) {
                    System.out.println("\t" + s);
                }
            }
        }
        
        TestUtils.printEpilogue(testName, start, errorList, exceptionList, successList, targetCount, graphUrls.size());
    }
    
}