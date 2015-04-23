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

import edu.emory.mathcs.backport.java.util.Arrays;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.mousephenotype.www.testing.exception.GraphTestException;
import uk.ac.ebi.phenotype.service.dto.GraphTestDTO;
import org.mousephenotype.www.testing.model.GenePage;
import org.mousephenotype.www.testing.model.GraphPage;
import org.mousephenotype.www.testing.model.GraphSection;
import org.mousephenotype.www.testing.model.GraphValidator;
import org.mousephenotype.www.testing.model.GraphValidatorCategorical;
import org.mousephenotype.www.testing.model.GraphValidatorPreqc;
import org.mousephenotype.www.testing.model.GraphValidatorUnidimensional;
import org.mousephenotype.www.testing.model.PageStatus;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.chart.ChartType;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.MpService;
import uk.ac.ebi.phenotype.service.PostQcService;
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
    @Qualifier("postqcService")
    protected PostQcService genotypePhenotypeService;
    
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
    
    private WebDriverWait wait; // = new WebDriverWait(driver, timeout_in_seconds);
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    private final int TIMEOUT_IN_SECONDS = 120;         // Increased timeout from 4 to 120 secs as some of the graphs take a long time to load.
    private final int THREAD_WAIT_IN_MILLISECONDS = 20;
    
    private int timeout_in_seconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;

    private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

    @Before
    public void setup() {
        if (Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeout_in_seconds = Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            thread_wait_in_ms = Utils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));
        
        TestUtils.printTestEnvironment(driver, seleniumUrl);
        wait = new WebDriverWait(driver, timeout_in_seconds);
        
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
    
    
    // PRIVATE METHODS
    
    
    private PageStatus graphValidator(String graphUrl, GraphValidator validator) throws GraphTestException {
        PageStatus status = new PageStatus();
        
        GraphPage graphPage = new GraphPage(driver, wait, phenotypePipelineDAO, graphUrl, baseUrl);
        for (GraphSection pageSection : graphPage.getDownloadSections()) {
            validator.setPageSection(pageSection);
            status.add(validator.validate());
        }
        
        return status;
    }
    
    private void testEngine(String testName, List<GraphTestDTO> geneGraphs, /*ChartType chartType, */GraphValidator validator) throws GraphTestException {
        String target;
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus statuses = new PageStatus();
        
        int targetCount = testUtils.getTargetCount(testName, geneGraphs, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " graph pages.");
        
        for (int i = 0; i < targetCount; i++) {
            GraphTestDTO geneGraph = geneGraphs.get(i);
            target = baseUrl + "/genes/" + geneGraph.getMgiAccessionId();
            GenePage genePage = new GenePage(driver, wait, target, geneGraph.getMgiAccessionId(), phenotypePipelineDAO, baseUrl);
            genePage.selectGenesLength(100);
            List<String> graphUrls = genePage.getGraphUrls(geneGraph.getProcedureName(), geneGraph.getParameterName());
            if ( ! graphUrls.isEmpty()) {
                PageStatus status = graphValidator(graphUrls.get(0), validator);
                if ( ! status.hasErrors())
                    status.incrementSuccessCount();
                statuses.add(status);
            }
        }
        
        TestUtils.printEpilogue(testName, start, statuses, targetCount, geneGraphs.size());
        System.out.println();
    }
    
    
    // TESTS
    
    
    // Tests known graph URLs that have historically been broken or are interesting cases, such as 2 graphs per page.
    @Test
//@Ignore
    public void testKnownGraphs() throws GraphTestException {
        String testName = "testKnownGraphs";
        Date start = new Date();
        GraphValidatorCategorical graphValidatorCategorical = new GraphValidatorCategorical();
        PageStatus status = new PageStatus();
        
        List<String> graphUrls = Arrays.asList( new String[] {
            "http://dev.mousephenotype.org/data/charts?accession=MGI:1920093&zygosity=homozygote&allele_accession=MGI:5548625&parameter_stable_id=IMPC_CSD_033_001&pipeline_stable_id=HRWL_001&phenotyping_center=MRC%20Harwell"
          , "http://dev.mousephenotype.org/data/charts?accession=MGI:1100883&allele_accession=MGI:2668337&zygosity=heterozygote&parameter_stable_id=ESLIM_001_001_087&pipeline_stable_id=ESLIM_001&phenotyping_center=MRC%20Harwell"
//          , "https://dev.mousephenotype.org/data/charts?accession=MGI:1100883&allele_accession=MGI:1862019&zygosity=heterozygote&parameter_stable_id=ESLIM_022_001_713&pipeline_stable_id=ESLIM_001&phenotyping_center=MRC%20Harwell"
//          , "http://ves-ebi-d0:8080/mi/impc/dev/phenotype-archive/charts?accession=MGI:98216&allele_accession=EUROALL:15&zygosity=homozygote&parameter_stable_id=ESLIM_021_001_005&pipeline_stable_id=ESLIM_001&phenotyping_center=ICS"
        });
        List<GraphValidator> validators = Arrays.asList( new GraphValidator[] {
            graphValidatorCategorical
          , graphValidatorCategorical
                
        });
        
        for (int i = 0; i < graphUrls.size(); i++) {
            status.add(graphValidator(graphUrls.get(i), validators.get(i)));
        }
            
        TestUtils.printEpilogue(testName, start, status, graphUrls.size(), graphUrls.size());
        System.out.println();
    }
    
    @Test
//@Ignore
    public void testPreQcGraphs() throws GraphTestException {
        String testName = "testPreQcGraphs";
        List<GraphTestDTO> geneGraphs = testUtils.getGeneGraphs(ChartType.PREQC, 100);
        String target;
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus statuses = new PageStatus();
        
        int targetCount = testUtils.getTargetCount(testName, geneGraphs, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " graphs.");
        
        for (int i = 0; i < targetCount; i++) {
            GraphTestDTO geneGraph = geneGraphs.get(i);
            target = baseUrl + "/genes/" + geneGraph.getMgiAccessionId();
            GenePage genePage = new GenePage(driver, wait, target, geneGraph.getMgiAccessionId(), phenotypePipelineDAO, baseUrl);
            genePage.selectGenesLength(100);
            GraphValidatorPreqc validator = new GraphValidatorPreqc();
            PageStatus status = validator.validate(driver, genePage, geneGraph);
            if ( ! status.hasErrors())
                status.incrementSuccessCount();
            statuses.add(status);
        }
        
        TestUtils.printEpilogue(testName, start, statuses, targetCount, geneGraphs.size());
        System.out.println();
    }
    
    @Test
//@Ignore
    public void testCategoricalGraphs() throws GraphTestException {
        String testName = "testCategoricalGraphs";
        
        List<GraphTestDTO> geneGraphs = testUtils.getGeneGraphs(ChartType.CATEGORICAL_STACKED_COLUMN, 100);
        testEngine(testName, geneGraphs, new GraphValidatorCategorical());
    }
    
    @Test
//@Ignore
    public void testUnidimensionalGraphs() throws GraphTestException {
        String testName = "testUnidimensionalGraphs";
        
        List<GraphTestDTO> geneGraphs = testUtils.getGeneGraphs(ChartType.UNIDIMENSIONAL_BOX_PLOT, 100);
        testEngine(testName, geneGraphs, new GraphValidatorUnidimensional());
    }
}