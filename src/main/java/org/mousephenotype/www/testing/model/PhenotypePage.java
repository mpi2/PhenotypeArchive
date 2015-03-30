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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 
 * This class encapsulates the code and data necessary to represent a Phenotype
 * Archive phenotype page for Selenium testing.
 */
public class PhenotypePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String target;
    private final String phenotypeId;
    private final PhenotypePipelineDAO phenotypePipelineDAO;
    private final String baseUrl;
    private final PhenotypeTable ptPhenotype;
    
    private boolean hasGraphs;
    private boolean hasImages;
    private boolean hasPhenotypesTable;
    private int resultsCount;
        
    /**
     * Creates a new <code>GenePage</code> instance
     * @param driver A valid <code>WebDriver</code> instance
     * @param wait A valid <code>WebDriverWait</code> instance
     * @param target This page's target url
     * @param phenotypeId This page's phenotype id
     * @param phenotypePipelineDAO a <code>PhenotypePipelineDAO</code> instance
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     */
    public PhenotypePage(WebDriver driver, WebDriverWait wait, String target, String phenotypeId, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.target = target;
        this.phenotypeId = phenotypeId;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.baseUrl = baseUrl;
        this.ptPhenotype = new PhenotypeTable(driver, wait, target);
        
        load();
    }

    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * 
     * @return The definition string
     */
    public String getDefinition() {
        String definition = "";
        
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='inner']/p[@class='with-label']")));
            if ( ! element.getText().isEmpty()) {
                if (element.findElement(By.cssSelector("span.label")).getText().trim().equals("Definition")) {
                    definition = element.getText();
                }
            }
        } catch (Exception e) { }
        
        return definition;
    }
    
    /**
     * 
     * @return a <code>List&lt;String&gt;</code> of this page's graph urls. The
     * list will be empty if this page doesn't have any graph urls.
     */
    public List<String> getGraphUrls() {
        List<String> urls = new ArrayList();
        
        if (hasGraphs) {
            ptPhenotype.load();
            GridMap map = ptPhenotype.getData();
            for (int i = 0; i < map.getBody().length; i++) {
                urls.add(map.getCell(i, PhenotypeTable.COL_INDEX_PHENOTYPES_GRAPH_LINK));
            }
        }
        
        return urls;
    }
    
    /**
     * @return the phenotype id 
     */
    public String getPhenotypeId() {
        return phenotypeId;
    }
    
    /**
     * @return the number at the end of the string "Total number of results: xxxx" found just before the "phenotypes" HTML table.
     */
    public int getResultsCount() {
        return resultsCount;
    }
    
    /**
     * 
     * @return A list of synonyms. The list will be empty if there are no synonyms.
     */
    public List<String> getSynonyms() {
        List<String> synonymList = new ArrayList();
        
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='inner']/p[@class='with-label']/following-sibling::p")));
            if ( ! element.getText().isEmpty()) {
                if (element.findElement(By.cssSelector("span.label")).getText().trim().equals("Synonyms")) {
                    String[] synonymArray = element.getText().replace("Synonyms", "").split(",");
                    for (String synonym : synonymArray) {
                        synonymList.add(synonym.trim());
                    }
                }
            }
        } catch (Exception e) { }
        
        return synonymList;
    }

    /**
     * 
     * @return this page's url
     */
    public String getTarget() {
        return target;
    }
    
    public boolean hasGraphs() {
        return hasGraphs;
    }
    
    public boolean hasImages() {
        return hasImages;
    }
    
    /**
     * 
     * @return true if this page has a <b><i>phenotypes</i></b> HTML table;
     * false otherwise.
     */
    public boolean hasPhenotypesTable() {
        return hasPhenotypesTable;
    }
    
    /**
     * Validates that:
     * <ul>
     *     <li>MGI MP browser has a link, and title starts with <b><i>Phenotype</i></b></li>
     *     <li>There is either a <b><i>Phenotype Association</i></b> section
     *         or an <b><i>Images</i></b> or both.</li>
     *     <li>If there is a <b><i>phenotypes</i></b> HTML table, validates that:
     *         <ul>
     *             <li>Each row has a p-value</li>
     *             <li>Each row has a valid graph link (the graph pages themselves
     *                 are not checked here as they take too long)</li>
     *             <li>The sex icon count matches <i>Total number of results</i> count</li>
     *             <li><b><i>TSV</i></b> and <b><i>XLS</i></b> downloads are valid</li>
     *         </ul>
     *     </li>
     * </ul>
     * @return validation status
     */
    public PageStatus validate() {
        PageStatus status = new PageStatus();
        
        // Validate title starts with 'Phenotype:'
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[@id='top']")));
        if ( ! element.getText().startsWith("Phenotype:")) {
            status.addError("Expected phenotype page title to start with 'Phenotype:'.");
        }
        
        // Validate there is a 'Phenotype Association' section or at least one image.
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@id='gene-variants']")));
        } catch (Exception e) {
            if ( ! hasImages()) {
                status.addError("Expected either 'Phenotype Association' section or 'Images' section or both.");
            }
        }
        
        // If there is a 'phenotypes' HTML table, validate it.
        if (hasPhenotypesTable) {
            // Validate that there is a 'pheontypes' HTML table by loading it.
            ptPhenotype.load();                                                 // Load all of the phenotypes table pageMap data.
            List<List<String>> preAndPostQcList = ptPhenotype.getPreAndPostQcList();
            String cell;
            int i = 0;
            for (List<String> row : preAndPostQcList) {
                if (i++ == 0)
                    continue;

                //   Verify p value.
                cell = row.get(PhenotypeTable.COL_INDEX_PHENOTYPES_P_VALUE);
                if (cell == null) {
                    status.addError("Missing or invalid P Value. URL: " + target);
                }

                // Validate that the graph link is not missing.
                cell = row.get(PhenotypeTable.COL_INDEX_PHENOTYPES_GRAPH_LINK);
                if ((cell == null) || (cell.trim().isEmpty())) {
                    status.addError("Missing graph link. URL: " + target);
                }
            }

            // Validate the download links.
            status = validateDownload();
        }
        
        return status;
    }
    
    public int getPhenotypesLength() {
        Select select = new Select(driver.findElement(By.xpath("//select[@name='phenotypes_length']")));
        return Utils.tryParseInt(select.getFirstSelectedOption());
    }
    
    public void selectPhenotypesLength(Integer resultCount) {
        Select select = new Select(driver.findElement(By.xpath("//select[@name='phenotypes_length']")));
        select.selectByValue(resultCount.toString());
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Get the full TSV data store
     * @param status Indicates the success or failure of the operation
     * @return the full TSV data store
     */
    private GridMap getDownloadTsv(PageStatus status) {
        String[][] data = new String[0][0];
        String downloadUrlBase = getDownloadUrlBase();
        
        try {
            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
            // getDownloadTargetUrlBase() typically returns a path of the form '/mi/impc/dev/phenotype-archive/export?xxxxxxx...'.
            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export?' with the baseUrl.
            int pos = downloadUrlBase.indexOf("/export?");
            downloadUrlBase = downloadUrlBase.substring(pos);
            String downloadTarget = baseUrl + downloadUrlBase + "tsv";

            // Get the download stream and statistics for the TSV stream.
            URL url = new URL(downloadTarget);
            DataReaderTsv dataReaderTsv = new DataReaderTsv(url);
            
            data = dataReaderTsv.getData();
        } catch (NoSuchElementException | TimeoutException te) {
            String message = "Expected page for ID " + phenotypeId + "(" + target + ") but found none.";
            status.addError(message);
        }  catch (Exception e) {
            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            status.addError(message);
        }
        
        return new GridMap(data, target);
    }
    
    /**
     * Return the download url base
     * @return the download url base embedded in div.
     */
    private String getDownloadUrlBase() {
        return driver.findElement(By.xpath("//div[@id='exportIconsDiv']")).getAttribute("data-exporturl");
    }
    
    /**
     * Get the full XLS data store
     * @param baseUrl A fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @param downloadUrlBase The page's download target url, such as /mi/impc/dev/phenotype-archive/export?xxxxxxx...'
     * @param status Indicates the success or failure of the operation
     * @return the full XLS data store
     */
    private GridMap getDownloadXls(PageStatus status) {
        String[][] data = new String[0][0];
        String downloadUrlBase = getDownloadUrlBase();
        
        try {
            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
            // getDownloadTargetUrlBase() typically returns a path of the form '/mi/impc/dev/phenotype-archive/export?xxxxxxx...'.
            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export?' with the baseUrl.
            int pos = downloadUrlBase.indexOf("/export?");
            downloadUrlBase = downloadUrlBase.substring(pos);
            String downloadTarget = baseUrl + downloadUrlBase + "xls";

            // Get the download stream and statistics for the XLS stream.
            URL url = new URL(downloadTarget);
            DataReaderXls dataReaderXls = new DataReaderXls(url);
            
            data = dataReaderXls.getData();
        } catch (NoSuchElementException | TimeoutException te) {
            String message = "Expected page for ID " + phenotypeId + "(" + target + ") but found none.";
            status.addError(message);
        }  catch (Exception e) {
            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            status.addError(message);
        }
        
        return new GridMap(data, target);
    }
    
    /**
     * Waits for the pheno page to load.
     */
    private void load() {
        driver.get(target);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@id='gene-variants']")));
        
        // Get results count. [NOTE: pages with no matches don't have totals]
        Integer i;
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='phenotypesDiv']/div[@class='container span12']/p[@class='resultCount']")));
            String totResultsString = element.getText();
            int index = totResultsString.lastIndexOf(":");
            String count = totResultsString.substring(index + 1);
            i = Utils.tryParseInt(count);
        } catch (Exception e) {
            i = null;
        }
        
        // Determine if this page has images.
        try {
            WebElement we = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='inner']/div[@class='accordion-group']/div[@class='accordion-heading']")));
            hasImages = (we.getText().trim().equals("Phenotype Associated Images"));
        } catch (Exception e) {
            hasImages = false;
        }
        
        // Determine if this page has phenotype associations.
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@id='phenotypes']")));
            hasPhenotypesTable = true;
        } catch (Exception e) {
            hasPhenotypesTable = false;
        }
        
        resultsCount = (i == null ? 0 : i);
        hasGraphs = (resultsCount > 0);
    }
    
    /**
     * Validate the page data against the download data. This is a difficult task,
     * as there is more detail data in the download file that simply doesn't exist
     * on the page. Metadata splits complicate matters. And, on the page, there is
     * a single row for male and female; in the download, there are always two such
     * rows - one for each sex. Then there is the newly added pagination that
     * does not serve up all of the page data in a single gulp any more.
     * 
     * So validation will be simple:
     * <li>Check that the number of rows in the download file is at least as
     *     many rows as the number of [non-preqc] sex icons shown on the first page.</li>
     * <li>Do a set difference between the rows on the first displayed page
     *     and the rows in the download file. The difference should be empty.</li></ul>
     * Any errors are returned in the <code>PageStatus</code> instance.
     * 
     * @return page status instance
     */
    private PageStatus validateDownload() {
        PageStatus status = new PageStatus();
        GridMap pageMap = ptPhenotype.load();                                   // Load all of the phenotypes table pageMap data.
        
        // Test the TSV.
        GridMap downloadData = getDownloadTsv(status);
        if (status.hasErrors()) {
            return status;
        }
        
        status = validateDownload(pageMap, downloadData);
        if (status.hasErrors()) {
            return status;
        }
        
        // Test the XLS.
        downloadData = getDownloadXls(status);
        if (status.hasErrors()) {
            return status;
        }
        
        status = validateDownload(pageMap, downloadData);
        if (status.hasErrors()) {
            return status;
        }
        
        return status;
    }
    
    /**
     * Internal validation comparing a loaded <code>pageMap</code> store with a
     * loaded <code>downloadData</code> store
     * @param pageData A loaded phenotypes table store
     * @param downloadData a loaded download store
     * @return status
     */
    private PageStatus validateDownload(GridMap pageData, GridMap downloadData) {
        PageStatus status = new PageStatus();
        int downloadDataLineCount = downloadData.getBody().length;
        
        // Check that the number of rows in the download file is at least as
        // many rows as the number of [non-preqc] sex icons shown on the first page.
        int sexIconCount = TestUtils.getSexIconCount(pageData, PhenotypeTable.COL_INDEX_PHENOTYPES_SEX,
                                                               PhenotypeTable.COL_INDEX_PHENOTYPES_GRAPH_LINK);
        if (downloadDataLineCount < sexIconCount) {
            status.addError("ERROR: download data line count (" + downloadDataLineCount + ") is LESS THAN the sex icon count (" +
                    sexIconCount + ").");
        }
        
        // Do a set difference between the rows on the first displayed page
        // and the rows in the download file. The difference should be empty.
        int errorCount = 0;

        final Integer[] pageColumns = {
              PhenotypeTable.COL_INDEX_PHENOTYPES_GENE_ALLELE
            , PhenotypeTable.COL_INDEX_PHENOTYPES_ZYGOSITY
            , PhenotypeTable.COL_INDEX_PHENOTYPES_PHENOTYPE
            , PhenotypeTable.COL_INDEX_PHENOTYPES_PROCEDURE_PARAMETER
            , PhenotypeTable.COL_INDEX_PHENOTYPES_PHENOTYPING_CENTER
            , PhenotypeTable.COL_INDEX_PHENOTYPES_SOURCE
            , PhenotypeTable.COL_INDEX_PHENOTYPES_GRAPH_LINK
        };
        final Integer[] downloadColumns = {
              DownloadPhenotypeMap.COL_INDEX_ALLELE
            , DownloadPhenotypeMap.COL_INDEX_ZYGOSITY
            , DownloadPhenotypeMap.COL_INDEX_PHENOTYPE
            , DownloadPhenotypeMap.COL_INDEX_PROCEDURE_PARAMETER
            , DownloadPhenotypeMap.COL_INDEX_PHENOTYPING_CENTER
            , DownloadPhenotypeMap.COL_INDEX_SOURCE
            , DownloadPhenotypeMap.COL_INDEX_GRAPH_LINK
        };
        final Integer[] decodeColumns = {
            DownloadPhenotypeMap.COL_INDEX_GRAPH_LINK
        };
        
        // Create a pair of sets: one from the page, the other from the download.
        Set<String> pageSet = TestUtils.createSet(pageData, pageColumns);
        Set<String> downloadSet = downloadData.urlDecode(Arrays.asList(decodeColumns)).createSet(downloadColumns);
        
        Set<String> difference = TestUtils.cloneStringSet(pageSet);
        difference.removeAll(downloadSet);
        if ( ! difference.isEmpty()) {
            System.out.println("ERROR: The following data was found on the page but not in the download:");
            Iterator it = difference.iterator();
            int i = 0;
            while (it.hasNext()) {
                String value = (String)it.next();
                System.out.println("[" + i++ + "]: " + value);
                errorCount++;
            }
        }

        if (errorCount > 0) {
            status.addError("Mismatch.");
        }
        
        return status;
    }
    
}