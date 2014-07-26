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
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 
 * This class encapsulates the code and data necessary to represent a Phenotype
 * Archive phenotype page for Selenium testing.
 */
public class PhenoPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String target;
    private final String phenoId;
    private final PhenotypePipelineDAO phenotypePipelineDAO;
    private final String baseUrl;
    private final PhenotypeTablePheno ptPheno;
    
    private boolean hasGraphs;
    private boolean hasImages;
    private int resultsCount;
        
    /**
     * Creates a new <code>GenePage</code> instance
     * @param driver A valid <code>WebDriver</code> instance
     * @param wait A valid <code>WebDriverWait</code> instance
     * @param target This page's target url
     * @param phenoId This page's phenotype id
     * @param phenotypePipelineDAO a <code>PhenotypePipelineDAO</code> instance
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     */
    public PhenoPage(WebDriver driver, WebDriverWait wait, String target, String phenoId, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.target = target;
        this.phenoId = phenoId;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.baseUrl = baseUrl;
        this.ptPheno = new PhenotypeTablePheno(driver, wait, target);
        
        load();
    }

    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * 
     * @return a <code>List&lt;String&gt;</code> of this page's graph urls. The
     * list will be empty if this page doesn't have any graph urls.
     */
    public List<String> getGraphUrls() {
        List<String> urls = new ArrayList();
        
        if (hasGraphs) {
            ptPheno.load();
            GridMap map = ptPheno.getData();
            for (int i = 0; i < map.getBody().length; i++) {
                urls.add(map.getCell(i, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_GRAPH));
            }
        }
        
        return urls;
    }
    
    /**
     * @return the phenotype id 
     */
    public String getPhenoId() {
        return phenoId;
    }
    
    /**
     * @return the number at the end of the string "Total number of results: xxxx" found just before the "phenotypes" HTML table.
     */
    public int getResultsCount() {
        return resultsCount;
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
     * Compares the first non-heading row of data from this class's 'phenotypes'
     * HTML table to the first non-heading row of <code>downloadData</code>. Any
     * errors are returned in the <code>PageStatus</code> instance.
     * 
     * @return page status instance
     */
    public PageStatus validateDownload() {
        PageStatus status = new PageStatus();
        GridMap pageMap = ptPheno.load();                                       // Load all of the phenotypes table pageMap data.
        
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
     * Validates all the graph links on the [already loaded] pheno page. Validates
     * that the graphs load and that their TSV and XLS download streams are correct. Any
     * errors are returned in a new <code>PageStatus</code> instance.
     * 
     * @return page status instance
     */
    public PageStatus validateGraphLinks() {
        PageStatus phenoStatus = new PageStatus();
        GridMap pageMap = ptPheno.load();                                       // Load all of the phenotypes table pageMap data.
        
        int sexIconCount = 0;
        String cell;
        for (String[] row : pageMap.getBody()) {
            cell = row[PhenotypeTablePheno.COL_INDEX_PHENOTYPES_SEX];
            if ((cell.equals("male")) || (cell.equals("female")))
                sexIconCount++;
            else if (cell.equals("both"))
                sexIconCount += 2;
            
            //   Verify p value.
            cell = row[PhenotypeTablePheno.COL_INDEX_PHENOTYPES_P_VALUE];
            if (cell == null) {
                phenoStatus.addError("Missing or invalid P Value. URL: " + target);
            }
            
            // Validate that the graph link is not missing.
            cell = row[PhenotypeTablePheno.COL_INDEX_PHENOTYPES_GRAPH];
            if ((cell == null) || (cell.trim().isEmpty())) {
                phenoStatus.addError("Missing graph link. URL: " + target);
            }
        }
        
        // Verify resultsCount on page against the phenotype table's count of Sex icons.
        if (sexIconCount != resultsCount) {
            phenoStatus.addError("Result counts don't match. Result count = " + resultsCount + " but Sex icon count = " + sexIconCount + ". URL: " + target);
        }
            
        // Validate the graphs.
        for (String[] row : pageMap.getBody()) {
            String graphUrl = row[PhenotypeTablePheno.COL_INDEX_PHENOTYPES_GRAPH];
            System.out.println("\tValidating graph. URL: " + graphUrl);
            GraphPage graphPage = new GraphPage(driver, wait, graphUrl, phenoId, phenotypePipelineDAO, baseUrl);
            PageStatus graphStatus = graphPage.validateScalar();
            if ( ! graphStatus.hasErrors()) {
                ObservationType graphType = graphPage.getGraphType();
                switch (graphType) {
                    case categorical:
                        
                        
    Parameter parameterObject = phenotypePipelineDAO.getParameterByStableId(graphPage.parameterStableId);

    // Set the graph type from the parameterDAO.
    graphType = Utilities.checkType(parameterObject, parameterObject.getDatatype());
    System.out.println("graphType: " + graphType);
                        
                        GraphPageCategorical graphPageCategorical =
                                new GraphPageCategorical(driver, wait, graphUrl, phenoId, phenotypePipelineDAO, baseUrl, graphPage);
                        graphStatus.add(graphPageCategorical.validateDownload());
                        break;
                        
                    case unidimensional:
                        GraphPageUnidimensional graphPageUnidimensional =
                                new GraphPageUnidimensional(driver, wait, graphUrl, phenoId, phenotypePipelineDAO, baseUrl, graphPage);
                        graphStatus.add(graphPageUnidimensional.validateDownload());
 System.out.println("TESTING GRAPH-BY-DATE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        graphStatus.add(graphPageUnidimensional.validateGraphByDate());
                        break;
                        
                    default:
                        throw new RuntimeException("PhenoPage: Unsupported graph type '" + graphType + "'");
                }
            }
        
            phenoStatus.add(graphStatus);
        }
        
        return phenoStatus;
    }
    
    
    // PRIVATE METHODS
    
    
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
            String s = element.getText().replace("Total number of results: ", "");
            i = Utils.tryParseInt(s);
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
        
        resultsCount = (i == null ? 0 : i);
        hasGraphs = (resultsCount > 0);
    }
    
    /**
     * Compares a single row of a pageMap grid selected by pageMapIndex to a
     * single row of a downloadData grid selected by downloadIndex.
     * @param pageMap phenotypes HTML table store
     * @param downloadData download data store
     * @param pageMapIndex pageMap row index
     * @param downloadIndex download row index
     * @return 
     */
    private int compareRowData(GridMap pageMap, GridMap downloadData, int pageMapIndex, int downloadIndex) {
        // Validate the page's phenotypes HTML table values against the first row of the download values.
        // If sex = "both", validate against the second download row as well.
        int errorCount = 0;
        List<String> colErrors = new ArrayList();
        String downloadCell;
        String pageCell;

        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_GENE_ALLELE).trim();
        AlleleParser ap = new AlleleParser(pageCell);
        
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenotypeMap.COL_INDEX_GENE).trim();
        if ( ! ap.gene.equals(downloadCell))
            colErrors.add("ERROR: gene mismatch. Page: '" + ap.gene + "'. Download: '" + downloadCell + "'");
        
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenotypeMap.COL_INDEX_ALLELE).trim();
        if ( ! ap.toString().equals(downloadCell))
            colErrors.add("ERROR: allele mismatch. Page: '" + ap.alleleSub + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_ZYGOSITY);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenotypeMap.COL_INDEX_ZYGOSITY).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: zygosity mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");
        
        // Special case: if the page sex is "both", use "female" to compare against the download, as "female" is sorted first in the download.
        pageCell = pageMap.getCell(1, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_SEX);
        pageCell = (pageCell.compareTo("both") == 0 ? "female" : pageCell);
        downloadCell = downloadData.getCell(1, DownloadPhenotypeMap.COL_INDEX_SEX).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: sex mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");
        
        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_PHENOTYPE);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenotypeMap.COL_INDEX_PHENOTYPE).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: phenotype mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_PROCEDURE_PARAMETER);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenotypeMap.COL_INDEX_PROCEDURE_PARAMETER).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: procedure | parameter mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_PHENOTYPING_CENTER);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenotypeMap.COL_INDEX_PHENOTYPING_CENTER).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: phenotyping center mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_SOURCE);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenotypeMap.COL_INDEX_SOURCE).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: source mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_P_VALUE);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenotypeMap.COL_INDEX_P_VALUE).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: p value mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        // When testing using http, the download link compare fails because the page url uses http
        // but the download graph link uses https. Ignore the protocol (but not the hostname).
        pageCell = TestUtils.removeProtocol(pageMap.getCell(1, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_GRAPH).trim());
        
        // When using the testing url 'ves-ebi-d0:8080, translate it to the correct
        // url used for dev in the download stream: dev.mousephenotype.org.
        pageCell = TestUtils.patchVesEbiD0(pageCell);
        
        downloadCell = TestUtils.removeProtocol(downloadData.getCell(1, DownloadPhenotypeMap.COL_INDEX_GRAPH).trim());
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: graph link mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");
        
        if ( ! colErrors.isEmpty()) {
            System.out.println(colErrors.size() + " errors:");
            for (String colError : colErrors) {
                System.out.println("\t" + colError);
            }

            errorCount++;
        }
        
        return errorCount;
    }
    
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
            String message = "Expected page for ID " + phenoId + "(" + target + ") but found none.";
            status.addError(message);
        }  catch (Exception e) {
            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            status.addError(message);
        }
        
        return new GridMap(data, target);
    }
    
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
            String message = "Expected page for ID " + phenoId + "(" + target + ") but found none.";
            status.addError(message);
        }  catch (Exception e) {
            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            status.addError(message);
        }
        
        return new GridMap(data, target);
    }
    
    /**
     * Internal validation comparing a loaded <code>pageMap</code> store with a
     * loaded <code>downloadData</code> store
     * @param pageMap A loaded phenotypes table store
     * @param downloadData a loaded download store
     * @return status
     */
    private PageStatus validateDownload(GridMap pageMap, GridMap downloadData) {
        PageStatus status = new PageStatus();
        
        // Check that the phenotypes table page line count equals the download stream line count.
        // Since the phenotypes table contains a single row for both sexes but the download file
        // contains a row for every sex, use the phenotypes table's sex count rather than the row count.
        int sexIconCount = TestUtils.getSexIconCount(pageMap, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_SEX);
        int bufferedSexIconCount = (int)Math.round(Math.floor(sexIconCount * 1.5));
        
        // If the phenotypes sex count is not equal to the download row count, then:
        //     If the download line count is > the sex icon count but <= sex icon count + 50%, issue a warning
        //     else throw an error.
        int downloadDataLineCount = downloadData.getBody().length;
        
        if (sexIconCount != downloadDataLineCount) {
            if (downloadDataLineCount > sexIconCount) {
                if (downloadDataLineCount <= bufferedSexIconCount) {
                    status.addWarning("WARNING: download data line count (" + downloadDataLineCount + ") is GREATER THAN the page sex icon count (" + sexIconCount + ") but LESS THAN OR EQUAL TO the buffered sex icon count ( " + bufferedSexIconCount + ")");
                } else {
                    status.addError("ERROR: download data line count (" + downloadDataLineCount + ") is GREATER THAN the buffered sex icon count ( " + bufferedSexIconCount + ")");
                }
            } else {
                    status.addError("ERROR: download data line count (" + downloadDataLineCount + ") is LESS THAN the buffered sex icon count ( " + bufferedSexIconCount + ")");
            }
        }
        
        int errorCount = 0;

        // If the sex is "both", compare the first pheno row to the first ("female") download row
        // and the second ("male") download row.
        String downloadCell = downloadData.getCell(0, DownloadPhenotypeMap.COL_INDEX_SEX).trim();
        String pageCell = pageMap.getCell(0, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_SEX).trim();
        if (pageCell.equals("both")) {
            if (downloadCell.equals("female")) {
                errorCount += compareRowData(pageMap, downloadData, 1, 1);      // download data is ordered 'female', then 'male'. Always check in the order "female", then "male".
                errorCount += compareRowData(pageMap, downloadData, 1, 2);
            } else {
                errorCount += compareRowData(pageMap, downloadData, 1, 2);      // download data is ordered 'male', then 'female'. Always check in the order "female", then "male".
                errorCount += compareRowData(pageMap, downloadData, 1, 1);
            }
        } else {
            errorCount += compareRowData(pageMap, downloadData, 1, 1);
        }
        
        if (errorCount > 0) {
            status.addError("Mismatch.");
        }
        
        return status;
    }
    
}