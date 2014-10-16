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
import java.util.ArrayList;
import java.util.HashMap;
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
        
        // Validate the globalTest HTML table and its contents.
        GraphGlobalTestTable originalGlobalTestTable = getGlobalTestTable();
        if ( ! originalGlobalTestTable.hasGlobalTestTable()) {
            status.addError("ERROR: unidimensional graph has no globalTest table. URL: " + target);
        }
        status.add(originalGlobalTestTable.validate());
        
        // Validate there is an HTML table named continuousTable and validate it.
        if ( ! getContinuousTable().hasContinuousTable()) {
            status.addError("ERROR: unidimensional graph has no continuousTable. URL: " + target);
        }
        
        String moreStatisticsIXpath = "//i[@id='toggle_table_buttondivChart_1']";
        String moreStatisticsDivXpath = "//div[@id='toggle_tabledivChart_1']";
        List<WebElement> moreStatisticsList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(moreStatisticsIXpath)));
        
        if (moreStatisticsList.isEmpty()) {
            status.addError("ERROR: Expected 'More statistics' link but wasn't found.");
        } else {
            WebElement moreStatisticsIElement = moreStatisticsList.get(0);
            WebElement moreStatisticsDivElement = driver.findElement(By.xpath(moreStatisticsDivXpath));
            String style = moreStatisticsDivElement.getAttribute("style");
            if ( ! style.equals("display: none;"))
                status.addError("ERROR: Expected 'More statistics' drop-down to start collapsed.");
            
            moreStatisticsIElement.click();
            wait.until(ExpectedConditions.visibilityOf(moreStatisticsDivElement));
            style = moreStatisticsDivElement.getAttribute("style");
            if ( ! style.contains("display: block;"))
                status.addError("ERROR: Expected 'More statistics' drop-down to be expanded.");
            
            moreStatisticsIElement.click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(moreStatisticsDivXpath)));
            style = moreStatisticsDivElement.getAttribute("style");
            if ( ! style.contains("display: none;"))
                status.addError("ERROR: Expected 'More statistics' drop-down to be collapsed.");
        }
        
        status.add(validateDownload());                     // Validate download streams.
        
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


    private PageStatus validateDownloadCounts(String[][] downloadData) {
        PageStatus status = new PageStatus();
        DownloadGraphMapUnidimensional map = new DownloadGraphMapUnidimensional();
        
        // key = "Control" or "Experimental". value is zygosity hash map.
        HashMap<String, HashMap<String, HashMap<String, Integer>>> groupHash = new HashMap();
        
        // Walk the download stream summing the counts.
        // Layout:      HashMap groupHash
        //                  "Control"
        //                  "Experimental"
        //                                  HashMap zygosity
        //                                      "Control"
        //                                      "Homozygote"
        //                                                      HashMap sex
        //                                                          "Female"
        //                                                          "Male"
        //                                                                          Integer
        
        // Skip over heading (first row). Also, sometimes there are extra blank lines at the end of the stream.
        // lowercase the hash keys on put and use lowercase when retrieving.
        int colCountFirstRow = 0;
        for (int i = 1; i < downloadData.length; i++) {
            if (i == 1)
                colCountFirstRow = downloadData[i].length;                      // Save the column count, then check it each time. Skip rows with mismatched column counts.
            if (downloadData[i].length != colCountFirstRow)
                continue;
            
            String[] row = downloadData[i];
            
            String zygosity = row[map.ZYGOSITY].toLowerCase();
            String sex = row[map.SEX].toLowerCase();
            String group = row[map.GROUP].toLowerCase();
            
            if ( ! groupHash.containsKey(group)) {
                groupHash.put(group, new HashMap<String, HashMap<String, Integer>>());
            }
            HashMap<String, HashMap<String, Integer>> zygosityHash = groupHash.get(group);
            // If this is a control, set 'zygosity' (which is otherwise blank) to 'control'.
            if (group.toLowerCase().equals("control")) {
                zygosity = group.toLowerCase();
            }
            if ( ! zygosityHash.containsKey(zygosity)) {
                zygosityHash.put(zygosity, new HashMap<String, Integer>());
            }
            HashMap<String, Integer> sexHash = zygosityHash.get(zygosity);
            if ( ! sexHash.containsKey(sex)) {
                sexHash.put(sex, 0);
            }
            sexHash.put(sex, sexHash.get(sex) + 1);
        }
        
        // We now have all the counts. Compare them against the page values.
        ArrayList<GraphContinuousTable.Row> rows = getContinuousTable().getBodyRowsList();
        for (GraphContinuousTable.Row row : rows) {                                    // For all of the Control/Hom/Het rows in continuousTable ...
            Integer pageValue = row.count;

            // If this is a control, set 'zygosity' (which is otherwise blank) to 'control'.
            String zygosityKey = (row.group == GraphContinuousTable.Group.CONTROL ? row.group.toString().toLowerCase() : row.zygosity.toLowerCase());

            // Sometimes some of these components are null. Wrap this in a try block.
            Integer downloadValue = null;
            try {
                downloadValue = groupHash
                    .get(row.group.toString().toLowerCase())
                    .get(zygosityKey)
                    .get(row.sex.toString().toLowerCase());
                } catch (Exception e) { }
            downloadValue = (downloadValue == null ? 0 : downloadValue);    // 0 count values on the page have no hash entry (i.e. returned hash value is null).
            if ( ! pageValue.equals(downloadValue)) {
                status.addError("ERROR: validating " + row.group.toString() + "." + row.zygosity + "." + row.sex.toString() + ": " +
                        "page value = '" + pageValue + "'. download value = '" + downloadValue + "'.");
            }
        }
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
            String downloadTargetUrlBase = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='exportIconsDivGlobal']"))).getAttribute("data-exporturl");
            String downloadTargetTsv = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "tsv", "/export?");

            // Get the download stream data.
            URL url = new URL(downloadTargetTsv);
            DataReaderTsv dataReaderTsv = new DataReaderTsv(url);
            String[][] downloadData = dataReaderTsv.getData();                                      // Get all of the data
            status.add(super.validateDownload(downloadData, new DownloadGraphMapUnidimensional())); // ... and validate it
            
            // Validate the counts.
            validateDownloadCounts(downloadData);
            
            
            // Test the XLS.
            String downloadTargetXls = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "xls", "/export?");
            
            // Get the download stream and statistics.
            url = new URL(downloadTargetXls);
            DataReaderXls dataReaderXls = new DataReaderXls(url);
            downloadData = dataReaderXls.getData();                                                 // Get all of the data
            status.add(super.validateDownload(downloadData, new DownloadGraphMapUnidimensional())); // ... and validate it
            
            // Validate the counts.
            status.add(validateDownloadCounts(downloadData));                                               // Specific Unidimensional 'continuousTable' validation
        } catch (NoSuchElementException | TimeoutException te) {
            String message = "ERROR: GraphPageUnidimensional.validateDownload(): Expected page for ID " + id + "(" + target + ") but found none.";
            status.addError(message);
        }  catch (Exception e) {
            String message = "EXCEPTION GraphPageUnidimensional.validateDownload(): processing target URL " + target + ": " + e.getLocalizedMessage();
            status.addError(message);
        }
        
        return status;
    }
    
}