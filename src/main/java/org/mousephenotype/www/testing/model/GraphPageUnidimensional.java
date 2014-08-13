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

import java.net.URL;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a unidimensional graph page.
 */
public class GraphPageUnidimensional extends GraphPage {
    
    private GraphGlobalTestTable globalTestTable;
    private GraphContinuousTable continuousTable;
        
    /**
     * Creates a new <code>GraphPageUnidimensional</code> instance
     * 
     * @param driver <code>WebDriver</code> instance
     * @param wait <code>WebDriverWait</code> instance
     * @param target this graph's target url
     * @param id id of the associated gene or phenotype
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @param loadPage if true, load the page; otherwise, don't load the page
     */
    public GraphPageUnidimensional(WebDriver driver, WebDriverWait wait, String target, String id, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl, boolean loadPage) {
        super(driver, wait, target, id, phenotypePipelineDAO, baseUrl, loadPage);
        if (graphType != ObservationType.unidimensional)
            throw new RuntimeException("ERROR: Expected unidimensional graph but found " + graphType.name());
    }
    
    /**
     * Creates a new <code>GraphPageUnidimensional</code> instance initialized
     * with the given <code>GraphPage</code> scalars.
     * 
     * @param driver <code>WebDriver</code> instance
     * @param wait <code>WebDriverWait</code> instance
     * @param target this graph's target url
     * @param id id of the associated gene or phenotype
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @param graphPage a parent <code>GraphPage</code> providing scalar
     * initialization values
     * @param loadPage if true, load the page; otherwise, don't load the page
     */
    public GraphPageUnidimensional(WebDriver driver, WebDriverWait wait, String target, String id, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl, GraphPage graphPage, boolean loadPage) {
        super(driver, wait, target, id, phenotypePipelineDAO, baseUrl, loadPage);
        this.title = graphPage.title;
        this.alleleSymbol = graphPage.alleleSymbol;
        this.background = graphPage.background;
        this.geneSymbol = graphPage.geneSymbol;
        this.metadataGroup = graphPage.metadataGroup;
        this.parameterName = graphPage.parameterName;
        this.parameterStableId = graphPage.parameterStableId;
        this.phenotypingCenter = graphPage.phenotypingCenter;
        this.pipelineName = graphPage.pipelineName;
    
        // Database parameter variables
        this.graphType = graphPage.graphType;
        this.parameterObject = graphPage.parameterObject;
        
        if (graphType != ObservationType.unidimensional)
            throw new RuntimeException("ERROR: Expected unidimensional graph but found " + graphType.name());
    }
    
    @Override
    public PageStatus validate() {
        boolean doGraphByDate = true;
        return validate(doGraphByDate);
        
    }
    
    private PageStatus validate(boolean doGraphByDate) {
        // Validate common graph elements: title and graph type.
        PageStatus status = super.validate();
        
        // Validate the HTML table and its contents.
        GraphGlobalTestTable originalGlobalTestTable = getGlobalTestTable();
        if ( ! originalGlobalTestTable.hasGlobalTestTable()) {
            status.addError("ERROR: unidimensional graph has no globalTest table. URL: " + target);
        }
        status.add(originalGlobalTestTable.validate());
        
        // Validate there is an HTML table named continuousTable and validate it.
        if ( ! getContinuousTable().hasContinuousTable()) {
            status.addError("ERROR: unidimensional graph has no continuousTable. URL: " + target);
        }
        
        // Validate 'More statistics' drop-down. Clicking either the arrow or the link should toggle the toggle_table1 div.
        WebElement toggle_table1 = driver.findElement(By.xpath("//div[@id='toggle_table1']"));
        String style = toggle_table1.getAttribute("style");
        if ( ! style.equals("display: none;"))
            status.addError("ERROR: Expected 'More statistics' drop-down to start collapsed.");
        WebElement i = driver.findElement(By.xpath("//i[@id='toggle_table_button1']"));
        i.click();      // Click the link. That should open the toggle.
        style = toggle_table1.getAttribute("style");
        if ( ! style.contains("display: block;"))
            status.addError("ERROR: Expected 'More statistics' drop-down to be expanded.");
        i.click();      // Click the link again. That should close the toggle.
        style = toggle_table1.getAttribute("style");
        if ( ! style.contains("display: none;"))
            status.addError("ERROR: Expected 'More statistics' drop-down to be collapsed.");
        
        // Validate download streams.
        status.add(validateDownload());
        
        if (doGraphByDate) {
            // Validate there is a Graph by date link.
            final String GRAPH_BY_DATE = "Graph by date";
            GraphPageUnidimensional graphByDatePage = null;
            try {
                String graphByDateUrl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='section half']/a[text() = '" + GRAPH_BY_DATE+ "']"))).getAttribute("href");
                boolean loadPage = true;
                graphByDatePage = new GraphPageUnidimensional(driver, wait, graphByDateUrl, id, phenotypePipelineDAO, baseUrl, loadPage);
            } catch (Exception e) {
                status.addError("ERROR: unidimensional graph has no Graph by date link. URL: " + target);
            }

            if (graphByDatePage != null) {
                graphByDatePage.validate(false);
            }
            
            // Unidimensional graphs must have a globalTest table. Compare the two
            // data objects; they should be exactly equal.
            if (graphByDatePage == null)  {
                status.addError("ERROR: Couldn't load graphByDatePage");
            } else {
                if ( ! originalGlobalTestTable.isEqual(graphByDatePage.getGlobalTestTable())) {
                    status.addError("ERROR: this graph's globalTest table and its '" + GRAPH_BY_DATE + "' graph are not equal.\nThis URL: " + target + "\n'" + GRAPH_BY_DATE + "' URL: " + graphByDatePage.target);
                }
            }

            // Unidimensional graphs must have a continuousTable table. Compare the
            // two data objects; they should be exactly equal.
            if (getContinuousTable() == null) {
                status.addError("ERROR: Expected a continuousTable HTML table but found none. " + target);
            } else if (graphByDatePage == null) {
                status.addError("ERROR: Couldn't load graphByDatePage");
            } else {
                if ( ! getContinuousTable().isEqual(graphByDatePage.getContinuousTable())) {
                    status.addError("ERROR: this graph's continuousTable and its '" + GRAPH_BY_DATE + "' graph are not equal.\nThis URL: " + target + "\n'" + GRAPH_BY_DATE + "' URL: " + graphByDatePage.target);
                }
            }
        }
        
        return status;
    }
    
    
    // SETTERS AND GETTERS
    
    
    /**
     * 
     * @return The <code>GraphGlobalTestTable</code> instance, loaded with data.
     */
    public GraphGlobalTestTable getGlobalTestTable() {
        if (globalTestTable == null) {
            globalTestTable = new GraphGlobalTestTable(driver);
        }
        
        return globalTestTable;
    }

    /**
     * 
     * @return The <code>GraphContinuousTable</code> instance, loaded with data.
     */
    public GraphContinuousTable getContinuousTable() {
        if (continuousTable == null) {
            continuousTable = new GraphContinuousTable(driver);
        }
        
        return continuousTable;
    }
    
    
    // PRIVATE METHODS


    private PageStatus validateCounts(String[][] downloadData) {
        PageStatus status = new PageStatus();
//        status.addError("GraphPageUnidimensional.validateCounts() Not Implemented Yet.");

        
        
        System.out.println("GraphPageUnidimensional implementation of validateGraphTable.");
        
        return status;
    }
    
    /**
     * Validates what is displayed on the page with the TSV and XLS download
     * streams. Any errors are returned in a new <code>PageStatus</code> instance.
     * Unidimensional graphs need to test the following:
     * <ul><li>that the TSV and XLS links create a download stream</li>
     * <li>that the graph page parameters, such as <code>pipeline name</code>,
     * <code>pipelineStableId</code>, <code>parameterName</code>, etc. match</li>
     * <li>that the HTML graph summary table row counts match the values in the
     * continuousTable's <code>Count</code> column</li></ul>
     * 
     * @return validation results
     */
    private PageStatus validateDownload() {
        PageStatus status = new PageStatus();
        
        // Validate that the [required] <code>continuousTable</code> HTML table exists.
        if ( ! getContinuousTable().hasContinuousTable()) {
            status.addError("ERROR: unidimensional graph has no continuousTable.");
        }
        
        try {
            // Test the TSV.
            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
            // getDownloadTargetUrlBase() typically returns a path of the form '/mi/impc/dev/phenotype-archive/export?xxxxxxx...'.
            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export?' with the baseUrl.
            String downloadTargetUrlBase = driver.findElement(By.xpath("//div[@id='exportIconsDivGlobal']")).getAttribute("data-exporturl");
            String downloadTargetTsv = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "tsv", "/export?");

            // Get the download stream data.
            URL url = new URL(downloadTargetTsv);
            DataReaderTsv dataReaderTsv = new DataReaderTsv(url);
            String[][] downloadData = dataReaderTsv.getData();                                      // Get all of the data
            status.add(super.validateDownload(downloadData, new DownloadGraphMapUnidimensional())); // ... and validate it
            
            // Validate the counts.
            validateCounts(downloadData);
            
            
            // Test the XLS.
            String downloadTargetXls = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "xls", "/export?");
            
            // Get the download stream and statistics.
            url = new URL(downloadTargetXls);
            DataReaderXls dataReaderXls = new DataReaderXls(url);
            downloadData = dataReaderXls.getData();                                                 // Get all of the data
            status.add(super.validateDownload(downloadData, new DownloadGraphMapUnidimensional())); // ... and validate it
            
            // Validate the counts.
            validateCounts(downloadData);                                               // Specific Unidimensional 'continuousTable' validation
        } catch (NoSuchElementException | TimeoutException te) {
            String message = "Expected page for ID " + id + "(" + target + ") but found none.";
            status.addError(message);
        }  catch (Exception e) {
            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            status.addError(message);
        }
        
        return status;
    }
    
}