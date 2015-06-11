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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    protected final List<GraphSection> graphSections = new ArrayList();
    
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
        
        driver.get(graphUrl);
        load();
    }
    
    public PageStatus validate() throws GraphTestException {
        PageStatus status = new PageStatus();
        
        for (GraphSection graphSection : graphSections) {
            status.add(graphSection.validate());
        }
        
        return status;
    }
    
    
    // GETTERS AND SETTERS

    public List<GraphSection> getGraphSections() {
        return graphSections;
    }
    
    
    // PRIVATE METHODS

    
    private boolean hasDownloadLinks() {
        List<WebElement> elements = driver.findElements(By.xpath("//div[@id='exportIconsDivGlobal']"));
        return ( ! elements.isEmpty());
    }
    
    /**
     * Load the page and its section and tsv/xls download data.
     */
    private void load() throws GraphTestException {
        String chartXpath = "//div[@class='section']/div[@class='inner']/div[@class='chart']";
        List<WebElement> chartElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(chartXpath)));
        
        String message;
        
        // Wait for page to load. Sometimes the chart isn't loaded when the 'wait()' ends, so try a few times.
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
        
        // Load the GraphSections.
        for (WebElement chartElement : chartElements) {
            GraphSection graphSection = GraphSectionFactory.createGraphSection(driver, wait, phenotypePipelineDAO, graphUrl, chartElement);
            graphSections.add(graphSection);
        }
        
        // If the page has download links, parse each download section into its
        // own DownloadSection. Each such section contains a map with two keys:
        // "tsv" and "xls". Each value points to its [tsv or xls] data.
        if (hasDownloadLinks()) {
            List<DownloadSection> downloadSections;
            try {
                downloadSections = loadAllDownloadData();
            } catch (Exception e) {
                message = "Exception. URL: " + graphUrl;
                System.out.println(message);
                throw new GraphTestException(message,  e);
            }
            
            // For each GraphSection, compare the heading's pageKey with the set
            // of keys for each download section until found. If found, bind
            // that download section to the graph section; otherwise, throw an
            // exception indicating the expected key wasn't found.
            for (GraphSection graphSection : graphSections) {
                GraphHeading heading = graphSection.getHeading();
                graphSection.setDownloadSection(null);
                String pageKey = heading.getMutantKey();
                
                List<Set<String>> downloadKeysSet = new ArrayList();
                for (DownloadSection downloadSection : downloadSections) {
                    Set<String> downloadKeys = downloadSection.getKeys(heading.chartType, TestUtils.DownloadType.XLS);
                    if (downloadKeys.contains(pageKey)) {
                        graphSection.setDownloadSection(downloadSection);
                        break;
                    } else {
                        downloadKeysSet.add(downloadKeys);
                    }
                }
                if (graphSection.getDownloadSection() == null) {
                    String setContents = "";
                    for (Set<String> downloadKeys : downloadKeysSet) {
                        if ( ! setContents.isEmpty())
                            setContents += "\n\n";
                        setContents += TestUtils.dumpSet(downloadKeys);
                    }
                    message = "ERROR: target " + graphUrl + "\nExpected page mutantKey '" + pageKey
                            + "' but was not found. Set:\n" + setContents;
                    System.out.println(message);
                    throw new GraphTestException(message);
                }
            }
        }
    }

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
    public List<DownloadSection> loadAllDownloadData() throws DownloadException {
        List<DownloadSection> retVal = new ArrayList();
        
        // Extract the TSV data.
        // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
        // getDownloadTargetUrlBase() typically returns a path of the form '/mi/impc/dev/phenotype-archive/export?xxxxxxx...'.
        // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export?' with the baseUrl.
        String downloadTargetUrlBase = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='exportIconsDivGlobal']"))).getAttribute("data-exporturl");
        String downloadTargetTsv = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "tsv", "/export?");

        // Get the download stream data.
        List<List<List<String>>> downloadBlockTsv = new ArrayList();
        List<List<List<String>>> downloadBlockXls = new ArrayList();
        
        try {
            URL url = new URL(downloadTargetTsv);
            DataReaderTsv dataReaderTsv = new DataReaderTsv(url);
            String[][] allGraphData = dataReaderTsv.getData();
            downloadBlockTsv = parseDownloadStream(allGraphData);
            
        } catch (IOException e) {
            throw new DownloadException("Error parsing TSV", e);
        }
        // Extract the XLS data.
        String downloadTargetXls = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "xls", "/export?");
        
        try {
            URL url = new URL(downloadTargetXls);
            DataReaderXls dataReaderXls = new DataReaderXls(url);
            String[][] allGraphData = dataReaderXls.getData();
            downloadBlockXls = parseDownloadStream(allGraphData);
            
        } catch (IOException e) {
            throw new DownloadException("Error parsing XLS", e);
        }
        
        for (int i = 0; i < downloadBlockTsv.size(); i++) {
            Map<TestUtils.DownloadType, List<List<String>>> downloadDataMap = new HashMap();
            
            downloadDataMap.put(TestUtils.DownloadType.TSV, downloadBlockTsv.get(i));
            downloadDataMap.put(TestUtils.DownloadType.XLS, downloadBlockXls.get(i));
            DownloadSection downloadSection = new DownloadSection(downloadDataMap);
            retVal.add(downloadSection);
        }
        
        return retVal;
    }
    /**
     * Given the full download data set, this method parses it, separating it
     * into a separate dataset for each graph. Each graph's dataset is preceeded
     * by row of column headings.
     * 
     * Dependency: This code depends on the first column of the first row
     *             matching the string 'pipeline name'.
     * 
     * @param allGraphData the full download data set
     * 
     * @return a list of download data set chunks, one for every graph
     */
    private List<List<List<String>>> parseDownloadStream(String[][] allGraphData) {
        List<List<List<String>>> retVal = new ArrayList();
        List<List<String>> dataBlock = new ArrayList();
        
        for (String[] row : allGraphData) {
            if (isHeading(row)) {
                if ( ! dataBlock.isEmpty()) {
                    retVal.add(dataBlock);
                    dataBlock = new ArrayList();
                }
            }
        
            if ( ! isBlank(row)) {                                              // Skip blank lines.
                dataBlock.add(Arrays.asList(row));
            }
        }
        
        if ( ! dataBlock.isEmpty()) {
            retVal.add(dataBlock);
        }
        
        return retVal;
    }
    
    /**
     * Returns true if the line described by <code>row</code> is a heading;
     * false otherwise.
     * 
     * @param row the line to be queried for a heading
     * 
     * @return true if the line described by <code>row</code> is a heading;
     *         false otherwise.
     */
    public static boolean isHeading(String[] row) {
        return (row[0].equals("pipeline name"));
    }
    
    /**
     * Returns true if the line described by <code>row</code> is a heading;
     * false otherwise.
     * 
     * @param row the line to be queried for a heading
     * 
     * @return true if the line described by <code>row</code> is a heading;
     *         false otherwise.
     */
    public static boolean isHeading(List<String> row) {
        return isHeading(row.toArray(new String[0]));
    }
    
    public static boolean isBlank(String[] row) {
        return ((row == null) || (row[0].isEmpty()));
    }
}