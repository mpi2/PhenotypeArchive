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
import java.util.Iterator;
import java.util.Map.Entry;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a categorical graph page.
 */
public class GraphPageCategorical extends GraphPage {

    private GraphCatTable catTable;
    
    /**
     * Creates a new <code>GraphPageCategorical</code> instance
     * 
     * @param driver <code>WebDriver</code> instance
     * @param wait <code>WebDriverWait</code> instance
     * @param target this graph's target url
     * @param id gene or phenotype id
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @param loadPage if true, load the page; otherwise, don't load the page
     */
    public GraphPageCategorical(WebDriver driver, WebDriverWait wait, String target, String id, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl, boolean loadPage) {
        super(driver, wait, target, id, phenotypePipelineDAO, baseUrl, loadPage);
        if (graphType != ObservationType.categorical)
            throw new RuntimeException("ERROR: Expected categorical graph but found " + graphType.name());
    }
    
    /**
     * Creates a new <code>GraphPageCategorical</code> instance initialized
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
     * @param loadPage if true, load the page; otherwise, don't load the page
     * initialization values
     */
    public GraphPageCategorical(WebDriver driver, WebDriverWait wait, String target, String id, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl, GraphPage graphPage, boolean loadPage) {
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
        
        if (graphType != ObservationType.categorical)
            throw new RuntimeException("ERROR: Expected categorical graph but found " + graphType.name());
    }
    
    @Override
    public PageStatus validate() {
        PageStatus status = super.validate();               // Validate common graph elements: title and graph type.
        
        // Validate that the [required] <code>catTable</code> HTML table exists.
        if ( ! getCatTable().hasCatTable()) {
            status.addError("ERROR: categorical graph has no catTable. URL: " + target);
        }
        
        status.add(validateDownload());                     // Validate download streams.
        
        return status;
    }
    
    
    // SETTERS AND GETTERS
    
    
    public GraphCatTable getCatTable() {
        if (catTable == null) {
            catTable = new GraphCatTable(driver);
        }
        
        return catTable;
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Validates download counts against categorical graph page totals.
     * @param data download data, including heading
     * @return validation status
     * 
     */
    private PageStatus validateDownloadCounts(String[][] data) {
        PageStatus status = new PageStatus();
        DownloadGraphMapCategorical map = new DownloadGraphMapCategorical();
        
        // key = "Control" or "Experimental". value is zygosity hash map.
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> groupHash = new HashMap();
        
        // Walk the download stream summing the counts.
        // Layout:      HashMap groupHash
        //                  "Control"
        //                  "Experimental"
        //                                  HashMap zygosity
        //                                      "Control"
        //                                      "Homozygote"
        //                                                      HashMap category
        //                                                          "Absent"
        //                                                          "Present"
        //                                                                          HashMap sex
        //                                                                              "Female"
        //                                                                              "Male"
        //                                                                                          Integer
        
        // Skip over heading (first row). Also, sometimes there are extra blank lines at the end of the stream.
        // lowercase the hash keys on put and use lowercase when retrieving.
        int colCountFirstRow = 0;
        for (int i = 1; i < data.length; i++) {
            if (i == 1)
                colCountFirstRow = data[i].length;                              // Save the column count, then check it each time. Skip rows with mismatched column counts.
            if (data[i].length != colCountFirstRow)
                continue;
            
            String[] row = data[i];
            
            String zygosity = row[map.ZYGOSITY].toLowerCase();
            String sex = row[map.SEX].toLowerCase();
            String group = row[map.GROUP].toLowerCase();
            String category = row[map.CATEGORY].toLowerCase();
            
            if ( ! groupHash.containsKey(group)) {
                groupHash.put(group, new HashMap<String, HashMap<String, HashMap<String, Integer>>>());
            }
            HashMap<String, HashMap<String, HashMap<String, Integer>>> zygosityHash = groupHash.get(group);
            // If this is a control, set 'zygosity' (which is otherwise blank) to 'control'.
            if (group.toLowerCase().equals("control"))
                zygosity = group.toLowerCase();
            if ( ! zygosityHash.containsKey(zygosity)) {
                zygosityHash.put(zygosity, new HashMap<String, HashMap<String, Integer>>());
            }
            HashMap<String, HashMap<String, Integer>> categoryHash = zygosityHash.get(zygosity);
            if ( ! categoryHash.containsKey(category)) {
                categoryHash.put(category, new HashMap<String, Integer>());
            }
            HashMap<String, Integer> sexHash = categoryHash.get(category);
            if ( ! sexHash.containsKey(sex)) {
                sexHash.put(sex, 0);
            }
            sexHash.put(sex, sexHash.get(sex) + 1);
        }
        // We now have all the counts. Compare them against the page values.
        ArrayList<GraphCatTable.Row> rows = getCatTable().getBodyRowsList();
        for (GraphCatTable.Row row : rows) {                                    // For all of the Control/Hom/Het rows in catTable ...
            Iterator<Entry<String, String>> categoryIt = row.getCategoryHash().entrySet().iterator();
            while (categoryIt.hasNext()) {                                      // ... For all of the categories ...
                Entry<String, String> entry = categoryIt.next();
                Integer pageValue = Utils.tryParseInt(entry.getValue());
                
                // If this is a control, set 'zygosity' (which is otherwise blank) to 'control'.
                String zygosityKey = (row.group == GraphCatTable.Group.CONTROL ? row.group.toString().toLowerCase() : row.zygosity.toLowerCase());
                Integer downloadValue = groupHash
                        .get(row.group.toString().toLowerCase())
                        .get(zygosityKey)
                        .get(entry.getKey().toLowerCase())
                        .get(row.sex.toString().toLowerCase());
                downloadValue = (downloadValue == null ? 0 : downloadValue);    // 0 count values on the page have no hash entry (i.e. returned hash value is null).
                if ( ! pageValue.equals(downloadValue)) {
                    status.addError("ERROR: validating " + row.group.toString() + "." + row.zygosity + "." + entry.getKey() + "." + row.sex.toString() + ": " +
                            "page value = '" + pageValue + "'. download value = '" + downloadValue + "'.");
                }
            }
        }
        
        return status;
    }
    
    /**
     * Validates what is displayed on the page with the TSV and XLS download
     * streams. Any errors are returned in a new <code>PageStatus</code> instance.
     * 
     * Categorical graphs need to test the following:
     * <ul><li>that the TSV and XLS links create a download stream</li>
     * <li>that the graph page parameters, such as <code>pipeline name</code>,
     * <code>pipelineStableId</code>, <code>parameterName</code>, etc. match</li>
     * <li>that the <code>catTable</code> HTML table counts match the sum of the
     * requisite values in the download stream</li></ul>
     * 
     * @return validation results
     */
    private PageStatus validateDownload() {
        PageStatus status = new PageStatus();
        
        // Validate that the [required] <code>catTable</code> HTML table exists.
        if ( ! getCatTable().hasCatTable()) {
            status.addError("ERROR: categorical graph has no catTable. URL: " + target);
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
            status.add(super.validateDownload(downloadData, new DownloadGraphMapCategorical()));    // ... and validate it
            
            // Validate the counts.
            status.add(validateDownloadCounts(downloadData));
            
            
            // Test the XLS.
            String downloadTargetXls = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "xls", "/export?");
            
            // Get the download stream data.
            url = new URL(downloadTargetXls);
            DataReaderXls dataReaderXls = new DataReaderXls(url);
            downloadData = dataReaderXls.getData();                                                 // Get all of the data
            status.add(super.validateDownload(downloadData, new DownloadGraphMapCategorical()));    // ... and validate it
            
            // Validate the counts.
            status.add(validateDownloadCounts(downloadData));
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
