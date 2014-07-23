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
import uk.ac.ebi.phenotype.pojo.SexGrouping;
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
    private final PhenotypePipelineDAO phenoPipelineDAO;
    private final PhenotypeTablePheno ptPheno;
        
    /**
     * Creates a new <code>GenePage</code> instance
     * @param driver A valid <code>WebDriver</code> instance
     * @param wait A valid <code>WebDriverWait</code> instance
     * @param target This page's target url
     * @param phenoId This page's phenotype id
     * @param phenoPipelineDAO a <code>PhenotypePipelineDAO</code> instance
     */
    public PhenoPage(WebDriver driver, WebDriverWait wait, String target, String phenoId, PhenotypePipelineDAO phenoPipelineDAO) {
        this.driver = driver;
        this.wait = wait;
        this.target = target;
        this.phenoId = phenoId;
        this.phenoPipelineDAO = phenoPipelineDAO;
        this.ptPheno = new PhenotypeTablePheno(driver, wait, target);
        
        driver.get(target);
        waitForPage();
    }
    
    /**
     * Compares the first non-heading row of data from this class's 'phenotypes'
     * HTML table to the first non-heading row of <code>downloadData</code>. Any
     * errors are returned in the <code>PageStatus</code> instance.
     * 
     * NOTE: All flavours of the download stream need some special modifications
     * for comparison testing:
     * <ul>
     * <li>When comparing the graph url, start with the first occurrence of
     * "/charts'. When testing on localhost, baseUrl is the localhost url, but
     * the download stream has a dev url (for historical, complicated reasons).</li></ul>
     * 
     * @param baseUrl A fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @return page status instance
     */
    public PageStatus validateDownload(String  baseUrl) {
        PageStatus status = new PageStatus();
        GridMap pageMap = ptPheno.load();                                       // Load all of the phenotypes table pageMap data.
        
        // Test the TSV.
        GridMap downloadData = getDownloadTsv(baseUrl, status);
        if (status.hasErrors()) {
            return status;
        }
        
        status = validateDownload(pageMap, downloadData);
        if (status.hasErrors()) {
            return status;
        }
        
        // Test the XLS.
        downloadData = getDownloadXls(baseUrl, status);
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
     * Validates all the graph links on the [already loaded] pheno page. Any
     * errors are returned in the <code>PageStatus</code> instance.
     * 
     * NOTE: All flavours of the download stream need some special modifications
     * for comparison testing:
     * <ul>
     * <li>When comparing the graph url, start with the first occurrence of
     * "/charts'. When testing on localhost, baseUrl is the localhost url, but
     * the download stream has a dev url (for historical, complicated reasons).</li></ul>
     * 
     * @param baseUrl A fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @return page status instance
     */
    public PageStatus validateGraphLinks(String  baseUrl) {
        PageStatus status = new PageStatus();
        PhenotypeTablePheno ptPheno = new PhenotypeTablePheno(driver, wait, target);
        GridMap pageMap = ptPheno.load();                                       // Load all of the phenotypes table pageMap data.
        
        // Verify that every data row has a valid graph link, then validate each link. Count the Sex icons along the way for later check.
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
                status.addError("Missing or invalid P Value. URL: " + target);
            }
            
            // Validate the graph link.
            //   Verify that link is not missing.
            String graphUrl = row[PhenotypeTablePheno.COL_INDEX_PHENOTYPES_GRAPH];
            if ((cell == null) || (cell.trim().isEmpty())) {
                status.addError("Missing graph link. URL: " + target);
            }
            
            //   Verify that the graph link loads a graph page.
            try {
                // The graph url is in the form "/data/charts?....". Patch the url to prepend the baseurl for a correct link to the graph.
                graphUrl = TestUtils.patchUrl(baseUrl, graphUrl, "/charts?");       // Patch the url.
                driver.get(graphUrl);
    //                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h2#section-associations"))).getText().contains("Allele");
                GraphPage graphPage = new GraphPage(driver, wait, target, phenoId, phenoPipelineDAO);
                graphPage.validate
                
                
                        new GraphPage(driver, wait, target, id, phenoPipelineDAO);
                graphPage.parse(status);
                graphPage.validate(status);
                i++;
            } catch (Exception e) {
                status.addFail("Couldn't load graph. " + row.toString() + "\nReason: " + e.getLocalizedMessage());
            }
        }
        
        
        
        
        
        
        int i = 0;
        for (GenePhenotypeRow row : data) {
            // Count the Sex icons.
            if ((row.getSexGrouping() == SexGrouping.male) || (row.getSexGrouping() == SexGrouping.female))
                sexIconCount++;
            else if (row.getSexGrouping() == SexGrouping.both)
                sexIconCount += 2;
            
            // Validate the graph link.
            status = row.validate(status);
            try {
                String target = row.getGraphHref();
                target = TestUtils.patchUrl(, url, "/charts?")
                driver.get(target);
//                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h2#section-associations"))).getText().contains("Allele");
                GraphPage graphPage = new GraphPage(driver, wait, target, id, phenoPipelineDAO);
                graphPage.parse(status);
                graphPage.validate(status);
                i++;
            } catch (Exception e) {
                status.addFail("Couldn't load graph. " + row.toString() + "\nReason: " + e.getLocalizedMessage());
            }
        }
        
        // Verify resultCount on page against the phenotype table's count of Sex icons.
        if (sexIconCount != resultCount) {
            status.addFail("Result counts don't match. Result count = " + resultCount + " but Sex icon count = " + sexIconCount);
        }
        
        
        
        
        
        
        
        
        
        // Test the TSV.
        GridMap downloadData = getDownloadTsv(baseUrl, status);
        if (status.hasErrors()) {
            return status;
        }
        
        status = validateDownload(pageMap, downloadData);
        if (status.hasErrors()) {
            return status;
        }
        
        // Test the XLS.
        downloadData = getDownloadXls(baseUrl, status);
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
     * Validates the basic components of the page. Does not validate page
     * collections.
     * 
     * @param baseUrl A fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @return status
     */
    public PageStatus validatePage(String baseUrl) {
        PageStatus status = new PageStatus();
        
        return status;
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
        String downloadCell = downloadData.getCell(0, DownloadPhenoMap.COL_INDEX_SEX).trim();
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
        
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenoMap.COL_INDEX_GENE).trim();
        if ( ! ap.gene.equals(downloadCell))
            colErrors.add("ERROR: gene mismatch. Page: '" + ap.gene + "'. Download: '" + downloadCell + "'");
        
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenoMap.COL_INDEX_ALLELE).trim();
        if ( ! ap.toString().equals(downloadCell))
            colErrors.add("ERROR: allele mismatch. Page: '" + ap.alleleSub + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_ZYGOSITY);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenoMap.COL_INDEX_ZYGOSITY).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: zygosity mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");
        
        // Special case: if the page sex is "both", use "female" to compare against the download, as "female" is sorted first in the download.
        pageCell = pageMap.getCell(1, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_SEX);
        pageCell = (pageCell.compareTo("both") == 0 ? "female" : pageCell);
        downloadCell = downloadData.getCell(1, DownloadPhenoMap.COL_INDEX_SEX).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: sex mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");
        
        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_PHENOTYPE);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenoMap.COL_INDEX_PHENOTYPE).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: phenotype mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_PROCEDURE_PARAMETER);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenoMap.COL_INDEX_PROCEDURE_PARAMETER).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: procedure | parameter mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_PHENOTYPING_CENTER);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenoMap.COL_INDEX_PHENOTYPING_CENTER).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: phenotyping center mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_SOURCE);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenoMap.COL_INDEX_SOURCE).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: source mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(pageMapIndex, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_P_VALUE);
        downloadCell = downloadData.getCell(downloadIndex, DownloadPhenoMap.COL_INDEX_P_VALUE).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: p value mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        // When testing using http, the download link compare fails because the page url uses http
        // but the download graph link uses https. Ignore the protocol (but not the hostname).
        pageCell = TestUtils.removeProtocol(pageMap.getCell(1, PhenotypeTablePheno.COL_INDEX_PHENOTYPES_GRAPH).trim());
        
        // When using the testing url 'ves-ebi-d0:8080, translate it to the correct
        // url used for dev in the download stream: dev.mousephenotype.org.
        pageCell = TestUtils.patchVesEbiD0(pageCell);
        
        downloadCell = TestUtils.removeProtocol(downloadData.getCell(1, DownloadPhenoMap.COL_INDEX_GRAPH).trim());
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
     * @param baseUrl A fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @param downloadUrlBase The page's download target url, such as /mi/impc/dev/phenotype-archive/export?xxxxxxx...'
     * @param status Indicates the success or failure of the operation
     * @return the full TSV data store
     */
    private GridMap getDownloadTsv(String baseUrl, PageStatus status) {
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
    
    /**
     * Get the full XLS data store
     * @param baseUrl A fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @param downloadUrlBase The page's download target url, such as /mi/impc/dev/phenotype-archive/export?xxxxxxx...'
     * @param status Indicates the success or failure of the operation
     * @return the full XLS data store
     */
    private GridMap getDownloadXls(String baseUrl, PageStatus status) {
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

    public String getTarget() {
        return target;
    }

    public String getPhenoId() {
        return phenoId;
    }
    
    public String getDownloadUrlBase() {
        return driver.findElement(By.xpath("//div[@id='exportIconsDiv']")).getAttribute("data-exporturl");
    }
    
    
    

    
    
    
    
    
    
//    /**
//     * @return the gene page's target download url base (identified by a div
//     * containing the 'data-exporturl' attribute).
//     */
////////    @Override
//    public String getDownloadTargetUrlBase() {
//        return driver.findElement(By.xpath("//div[@id='exportIconsDiv']")).getAttribute("data-exporturl");
//    }
    
    /**
     * @param baseUrl the graph's base url
     * @param numRows the number of rows to load
     * @return A list of this gene page's graph links and details.
     */
    public List<GraphLinkDetail> getGraphLinksAndDetails(String baseUrl, Integer numRows) {
        ArrayList<GraphLinkDetail> graphLinkDetails = new ArrayList();
        
        System.out.println("\nURL: " + target);
        GridMap phenoMap = ptPheno.load(numRows);
        
        for (String[] sA : phenoMap.getBody()) {
            GraphLinkDetail detail = new GraphLinkDetail(phenoId, baseUrl, sA[PhenotypeTablePheno.COL_INDEX_PHENOTYPES_GRAPH], phenoPipelineDAO);
            graphLinkDetails.add(detail);
        }
        
        return graphLinkDetails;
    }

    public class GraphLinkDetail {
        private ObservationType graphType;
        private String phenoId;
        private String parameterStableId;
        private String url;
        
        /**
         * Creates a new <code>GraphLinkDetail</code> instance
         * @param phenoId the phenoid
         * @param baseUrl the base url, prepended to the chart URL
         * @param url the chart url
         * @param pipelineDAO a valid <code>PipelineDAO</code> instance
         */
        public GraphLinkDetail(String phenoId, String baseUrl, String url) {
            this.phenoId = phenoId;
            this.url = TestUtils.patchUrl(baseUrl, url, "/charts?");
            String[] sA = url.split("&");
            for (String s : sA) {
                if (s.startsWith("parameter_stable_id")) {
                    String[] token = s.split("=");
                    parameterStableId = token[1];
                    Parameter parameter = phenoPipelineDAO.getParameterByStableId(parameterStableId);
                    graphType = Utilities.checkType(parameter);
                    break;
                }
            }
        }

        public String getPhenoId() {
            return phenoId;
        }

        public String getParameterStableId() {
            return parameterStableId;
        }

        public String getUrl() {
            return url;
        }

        public ObservationType getGraphType() {
            return graphType;
        }
    }
    
//    public String[][] getPhenotypeTableData() {
//        return getPhenotypeTableData(null);
//    }
//    
//    /**
//     * Get the pheno page's html phenotype table data
//     * @param numRows the number of rows of data to fetch. To specify all rows,
//     * set <code>numRows</code> to null.
//     * @return the html phenotype table data, in a 2-d array of <code>String</code>
//     */
//////////    @Override
//    public String[][] getPhenotypeTableData(Integer numRows) {
//        if (numRows == null)
//            numRows = getResultsCount();
//        PhenotypeTablePheno ptPheno = new PhenotypeTablePheno(driver, wait, target);
//        return ptPheno.getData(numRows);
//    }
    
    /**
     * @return the number at the end of the pheno page string 'Total number of results: xxxx'
     */
////////    @Override
    public final int getResultsCount() {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='phenotypesDiv']/div[@class='container span12']/p[@class='resultCount']")));
        String s = element.getText().replace("Total number of results: ", "");
        Integer i = Utils.tryParseInt(s);
        
        return (i == null ? 0 : i);
    }
    
    /**
     * Waits for the pheno page to load.
     */
    public final void waitForPage() {
        try {
            // Try to wait for a page with a data export. The caller may not care, so just ignore any timeouts. This provides adequate time for the page to load.
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='exportIconsDiv'][@data-exporturl]")));
        } catch (Exception e) { }
    }
    
    
    // PRIVATE METHODS
    
//    /**
//     * Load <code>numRows</code> rowphenotype page data:
//     * <ul><li>phenotypes table data
//     * @return all rows of data and column access variables from
//     * the pheno page's 'phenotypes' HTML table.
//     */
//    public PageStatus load(Integer numRows) {
//        String message;
//        PageStatus status = new PageStatus();
//        
//        // Wait for page to load.
//        try {
//            phenotypesGridmap = ptPheno.load(numRows);
//            
//        } catch (NoSuchElementException | TimeoutException te ) {
//            message = "Expected page for MP_TERM_ID " + phenoId + "(" + target + ") but found none.";
//            status.addError(message);
//        } catch (Exception e) {
//            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
//            status.addError(message);
//        }
//        
//        return status;
//    }
    
//    public class GeneAlleleParser {
//        private String alleleSub;
//        private String gene;
//        
//        public GeneAlleleParser(String geneAllele) {
//            String[] sA = geneAllele.split(" / ");
//            gene = (sA != null && sA.length > 1 ? sA[0] : "");
//            alleleSub = (sA != null && sA.length > 1 ? sA[1] : "");
//  if ((gene.isEmpty()) && (alleleSub.isEmpty())) { System.out.println("GeneAlleleParser: geneAllele: " + geneAllele); }
//        }
//
//        @Override
//        public String toString() {
//            return ("Gene: " + gene + ", Allele: " + alleleSub);
//        }
//    }
    
}
