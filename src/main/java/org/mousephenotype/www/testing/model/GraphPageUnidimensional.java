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
import java.util.List;
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
     */
    public GraphPageUnidimensional(WebDriver driver, WebDriverWait wait, String target, String id, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl) {
        super(driver, wait, target, id, phenotypePipelineDAO, baseUrl);
        super.loadScalar();
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
     */
    public GraphPageUnidimensional(WebDriver driver, WebDriverWait wait, String target, String id, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl, GraphPage graphPage) {
        super(driver, wait, target, id, phenotypePipelineDAO, baseUrl);
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
    public PageStatus validateDownload() {
        PageStatus status = new PageStatus();
        
        List<WebElement> weList;
        
        // Validate that the [required] <code>continuousTable</code> HTML table exists.
        weList = driver.findElements(By.xpath("table[@id='catTable']"));
        if (weList.isEmpty()) {
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
            String[][] downloadData = dataReaderTsv.getData();                          // Get all of the data
            validateScalarDownload(downloadData, new DownloadGraphMapUnidimensional()); // ... and validate it

            // Validate the counts.
            validateCounts(downloadData);
            
            
            // Test the XLS.
            String downloadTargetXls = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "xls", "/export?");
            
            // Get the download stream and statistics.
            url = new URL(downloadTargetXls);
            DataReaderXls dataReaderXls = new DataReaderXls(url);
            downloadData = dataReaderXls.getData();                                     // Get all of the data
            validateScalarDownload(downloadData, new DownloadGraphMapUnidimensional()); // ... and validate it
            
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

    public PageStatus validateGraphByDate() {
        PageStatus status = new PageStatus();
        final String GRAPH_BY_DATE = "Graph by date";
        GraphPageUnidimensional graphByDatePage = null;
        
        try {
            String graphByDateUrl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='section half']/a[text() = '" + GRAPH_BY_DATE+ "']"))).getAttribute("href");
            graphByDatePage = new GraphPageUnidimensional(driver, wait, graphByDateUrl, id, phenotypePipelineDAO, baseUrl);
            status.add(graphByDatePage.validateScalar());
            if (title.compareTo(graphByDatePage.getTitle()) != 0) {
                status.addError("ERROR: Expected title '" + title + "' but found '" + graphByDatePage.getTitle() + "'.");
            }
            // NOTE: Time Series graphs have the string 'MEAN ' prepended to the parameter name!!! Test accordingly.
            // USE 'contains' because of prepended 'MEAN ' for time series graphs.
            if ( ! parameterName.contains(graphByDatePage.getParameterName())) {
                status.addError("ERROR: Expected parameter '" + parameterName + "' but found '" + graphByDatePage.getParameterName() + "'.");
            }
            if (parameterStableId.compareTo(graphByDatePage.getParameterStableId()) != 0) {
                status.addError("ERROR: Expected parameterStableId '" + parameterStableId + "' but found '" + graphByDatePage.getParameterStableId() + "'.");
            }
        } catch (Exception e) {
            status.addError("ERROR: This graph's '" + GRAPH_BY_DATE + "' threw an exception: " + e.getLocalizedMessage() + ".\nURL: " + target);
        }

        // Unidimensional graphs must have a globalTest table. Compare the two
        // data objects; they should be exactly equal.
        if (globalTestTable == null) {
            status.addError("ERROR: Expected a globalTest HTML table but found none. " + target);
        } else if (graphByDatePage == null)  {
            status.addError("ERROR: Couldn't load graphByDatePage");
        } else {
            if ( ! globalTestTable.isEqual(graphByDatePage.globalTestTable)) {
                status.addError("ERROR: this graph's globalTest table and its '" + GRAPH_BY_DATE + "' graph are not equal.\nThis URL: " + target + "\n'" + GRAPH_BY_DATE + "' URL: " + graphByDatePage.target);
            }
        }

        // Unidimensional graphs must have a continuousTable table. Compare the
        // two data objects; they should be exactly equal.
        if (continuousTable == null) {
            status.addError("ERROR: Expected a continuousTable HTML table but found none. " + target);
        } else if (graphByDatePage == null) {
            status.addError("ERROR: Couldn't load graphByDatePage");
            if ( ! continuousTable.isEqual(graphByDatePage.continuousTable)) {
                status.addError("ERROR: this graph's continuousTable and its '" + GRAPH_BY_DATE + "' graph are not equal.\nThis URL: " + target + "\n'" + GRAPH_BY_DATE + "' URL: " + graphByDatePage.target);
            }
        }
        
        return status;
    }
    
    
    // SETTERS AND GETTERS
    
    
    public GraphGlobalTestTable getGlobalTestTable() {
        if (globalTestTable == null) {
            globalTestTable = new GraphGlobalTestTable(driver);
        }
        
        return globalTestTable;
    }

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
    

}