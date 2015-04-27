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
 */

package org.mousephenotype.www.testing.model;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mousephenotype.www.testing.exception.DownloadException;
import org.mousephenotype.www.testing.exception.GraphTestException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;

/**
 *
 * @author mrelac
 * 
 * This abstract class encapsulates the code and data necessary to represent the important,
 * common components of a graph page, such as: allele, background, phenotyping center,
 * pipeline name, metadata group, procedure name and parameter name, and
 * parameterStableId.
 * 
 * Currently there are no collections or links shared by all
 * graphs; for those, consult subclasses such as
 * GraphPageCategorical and GraphPageUnidimensional.
 */
public class GraphPage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final PhenotypePipelineDAO phenotypePipelineDAO;
    protected final String graphUrl;
    protected final String baseUrl;
    protected final List<GraphSection> downloadSections = new ArrayList();
    
    /**
     * Creates a new <code>GraphPage</code> instance
     * 
     * @param driver <code>WebDriver</code> instance
     * @param wait <code>WebDriverWait</code> instance
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     * @param graphUrl url of graph page to load
     * @param baseUrl the base url pointing to the downloads
     * @throws GraphTestException
     */
    public GraphPage(WebDriver driver, WebDriverWait wait, PhenotypePipelineDAO phenotypePipelineDAO, String graphUrl, String baseUrl) throws GraphTestException {
        this.driver = driver;
        this.wait = wait;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.graphUrl = graphUrl;
        this.baseUrl = baseUrl;
        
        load();
    }
    
    
    // GETTERS AND SETTERS

    public List<GraphSection> getDownloadSections() {
        return downloadSections;
    }
    
    
    // PRIVATE METHODS

    
    /**
     * Parse the tsv and xls download files. When successfully completed, the
     * map will contain two key/value pairs: one keyed "tsv" and one keyed
     * "xls". Each value contains a list of download data by section, where a
     * section is identified as starting with a column heading.
     *
     * @return two key/value pairs: one keyed "tsv" and one keyed "xls". Each
     * value contains a list of download data by section, where a section is
     * identified as starting with a column heading.
     * 
     * @throws DownloadException
     */
    private Map<TestUtils.DownloadType, List<String[][]>> getAllDownloadData() throws DownloadException {
        Map<TestUtils.DownloadType, List<String[][]>> retVal = new HashMap();
        
        // Extract the TSV data.
        // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
        // getDownloadTargetUrlBase() typically returns a path of the form '/mi/impc/dev/phenotype-archive/export?xxxxxxx...'.
        // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export?' with the baseUrl.
        String downloadTargetUrlBase = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='exportIconsDivGlobal']"))).getAttribute("data-exporturl");
        String downloadTargetTsv = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "tsv", "/export?");

        // Get the download stream data.
        try {
            URL url = new URL(downloadTargetTsv);
            DataReaderTsv dataReaderTsv = new DataReaderTsv(url);
            String[][] allGraphData = dataReaderTsv.getData();
            retVal.put(TestUtils.DownloadType.TSV, parseDownloadStream(allGraphData));
        } catch (IOException e) {
            throw new DownloadException("Error parsing TSV", e);
        }
        // Extract the XLS data.
        String downloadTargetXls = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "xls", "/export?");
        
        try {
            URL url = new URL(downloadTargetXls);
            DataReaderXls dataReaderXls = new DataReaderXls(url);
            String[][] allGraphData = dataReaderXls.getData();
            retVal.put(TestUtils.DownloadType.XLS, parseDownloadStream(allGraphData));
        } catch (IOException e) {
            throw new DownloadException("Error parsing XLS", e);
        }
        
        return retVal;
    }
    
    /**
     * Load the page and its section and tsv/xls download data.
     */
    private void load() throws GraphTestException {
        driver.get(graphUrl);
        // Wait for page to loadScalar. Sometimes the chart isn't loaded when the 'wait()' ends, so try a few times.
        for (int i = 0; i < 10; i++) {
            try {
                WebElement titleElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@id='section-associations']")));
                if (titleElement != null)
                    break;
            } catch (Exception e) {
//                    System.out.println("Waiting " + ((i * 10) + 10) + " milliseconds.");
                TestUtils.sleep(10);
            }
        }

        // Populate download data. The map has two keys: "tsv" and "xls".
        // Each map's data is a list of each section's data.
        Map<TestUtils.DownloadType, List<String[][]>>downloadDataSections = new HashMap();
        try {
            downloadDataSections = getAllDownloadData();
        } catch (DownloadException e) {
            throw new GraphTestException("Exception. URL: " + graphUrl,  e);
        }
        
        // Populate download downloadSections data.
        String chartXpath = "//div[@class='section']/div[@class='inner']/div[@class='chart']";
        List<WebElement> chartElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(chartXpath)));

        for (int i = 0; i < chartElements.size(); i++) {
            WebElement chartElement = chartElements.get(i);
            GraphSection downloadSection = new GraphSection(driver, wait, phenotypePipelineDAO, graphUrl, chartElement);
            
            List<String[][]> allTsvSectionData = downloadDataSections.get(TestUtils.DownloadType.TSV);
            List<String[][]> allXlsSectionData = downloadDataSections.get(TestUtils.DownloadType.XLS);
            
            downloadSection.getDownloadDataSection().put(TestUtils.DownloadType.TSV, allTsvSectionData.get(i));
            downloadSection.getDownloadDataSection().put(TestUtils.DownloadType.XLS, allXlsSectionData.get(i));
            
            downloadSections.add(downloadSection);
        }
        
        if (chartElements.size() != downloadSections.size()) {
            throw new GraphTestException("Size mismatch: Graph page size: " + chartElements.size() + ". Download size: " + downloadDataSections.size() + ". URL: " + graphUrl);
        }
    }
    
    /**
     * Given the full download data set, this method parses it, separating it
     * into a separate dataset for each graph. Each graph's dataset is preceeded
     * by row of column headings.
     * 
     * @param allGraphData the full download data set
     * 
     * @return a list of download data set chunks, one for every graph
     */
    private List<String[][]> parseDownloadStream(String[][] allGraphData) {
        List<String[][]> retVal = new ArrayList();
        List<String[]> grid = new ArrayList();
        
        for (String[] row : allGraphData) {
            if (row[0].equals("pipeline name")) {
                if ( ! grid.isEmpty()) {
                    retVal.add(grid.toArray(new String[0][0]));
                }
                grid = new ArrayList();
            }
            
            if (row.length > 0)
                grid.add(row);      // Skip empty lines.
        }
        
        retVal.add(grid.toArray(new String[0][0]));
        
        return retVal;
    }
}