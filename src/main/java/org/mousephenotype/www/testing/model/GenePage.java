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
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
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
 * Archive gene page for Selenium testing.
 */
public class GenePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String target;
    private final String geneId;
    private final PhenotypePipelineDAO phenotypePipelineDAO;
    private final String baseUrl;
    private final GeneTable geneTable;
    
    private boolean hasImages;
    private boolean hasGraphs;
    private boolean hasGenesTable;
    private int resultsCount;
    
    /**
     * Creates a new <code>GenePage</code> instance
     * @param driver A valid <code>WebDriver</code> instance
     * @param wait A valid <code>WebDriverWait</code> instance
     * @param target This page's target url
     * @param geneId This page's gene id
     * @param phenotypePipelineDAO a <code>PhenotypePipelineDAO</code> instance
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     */
    public GenePage(WebDriver driver, WebDriverWait wait, String target, String geneId, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.target = target;
        this.geneId = geneId;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.baseUrl = baseUrl;
        this.geneTable = new GeneTable(driver, wait, target);
        
        load();
    }
    
    /**
     * 
     * @return A list of top level MP terms.
     */
    public List<String> getAssociatedImageSections() {
        List<String> associatedImageSections = new ArrayList();
        List<WebElement> associatedImageSectionElements = driver.findElements(By.className("accordion-heading"));
        for (WebElement associatedImageSectionElement : associatedImageSectionElements) {
            associatedImageSections.add(associatedImageSectionElement.getText());
        }
        
        return associatedImageSections;      
    }
    
    /**
     * 
     * @return All of the enabled abnormality strings (those that start with the
     * class name 'sprite').
     */
    public List<String> getEnabledAbnormalities() {
        List<String> abnormalityStrings = new ArrayList();
        
        List<WebElement> enabledAbnormalityElementList = driver.findElements(By.xpath("//div[@class='inner']/div[@class='abnormalities']/div[ not(contains(@class, 'no-sprite'))]"));
     
        for (WebElement enabledAbnormalityElement : enabledAbnormalityElementList) {
            String abnormality = enabledAbnormalityElement.getAttribute("oldtitle");
            if ((abnormality != null) && ( ! abnormality.isEmpty()))
                abnormalityStrings.add(abnormality);
        }
        
        return abnormalityStrings;
    }
    
    /**
     * 
     * @return the base url
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 
     * @return all button labels in a <code>List</code>.
     */
    public List<String> getButtonLabels() {
        List<String> buttonLabels = new ArrayList();
        List<WebElement> buttons = driver.findElements(By.className("btn"));
        for (WebElement button : buttons) {
            buttonLabels.add(button.getText());
        }
        
        return buttonLabels;
    }

    /**
     * 
     * @return the gene ID
     */
    public String getGeneId() {
        return geneId;
    }
    
    /**
     * 
     * @return a <code>List&lt;String&gt;</code> of this page's graph urls. The
     * list will be empty if this page doesn't have any graph urls.
     */
    public List<String> getGraphUrls() {
        List<String> urls = new ArrayList();
        
        if (hasGraphs) {
            if (geneTable.genesTableIsNotEmpty()) {
                geneTable.load();
                GridMap map = geneTable.getData();
                for (int i = 0; i < map.getBody().length; i++) {
                    urls.add(map.getCell(i, GeneTable.COL_INDEX_GENES_GRAPH));
                }
            }
        }
        
        return urls;
    }
    
    /**
     * @return the number at the end of the gene page string 'Total number of results: xxxx'
     */
    public int getResultsCount() {
        return resultsCount;
    }

    /**
     * 
     * @return all section titles in a <code>List</code>.
     */
    public List<String> getSectionTitles() {
        List<String> sectionTitles = new ArrayList();
        List<WebElement> sections = driver.findElements(By.cssSelector(".title"));
        
        for (WebElement sectionElement : sections) {
            String text = sectionElement.getText().trim();
            if ((text != null) && ( ! text.isEmpty()))
                sectionTitles.add(sectionElement.getText());
        }
        
        return sectionTitles;
    }
    
    /**
     * 
     * @return The target URL
     */
    public String getTarget() {
        return target;
    }
    
    /**
     * 
     * @return the title ('Gene: Akt2')
     */
    public String getTitle() {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("top")));
        return element.getText();
    }
    
    /**
     * 
     * @return A list of top level MP terms.
     */
    public List<String> getTopLevelMPs() {
        List<String> topLevelMPs = new ArrayList();
        Select selectTopLevel = new Select(driver.findElement(By.id("top_level_mp_term_name")));
        for (WebElement option : selectTopLevel.getOptions()) {
            topLevelMPs.add(option.getAttribute("value"));
        }
        
        return topLevelMPs;      
    }
    
    /**
     * 
     * @return true if this page has graphs; false otherwise.
     */
    public boolean hasGraphs() {
        return hasGraphs;
    }
    
    /**
     * 
     * @return true if this page has images; false otherwise.
     */
    public boolean hasImages() {
        return hasImages;
    }
    
    /**
     * 
     * @return true if this page has a <b><i>genes</i></b> HTML table;
     * false otherwise.
     */
    public boolean hasGenesTable() {
        return hasGenesTable;
    }
    
    /**
     * Validates that:
     * <ul>
     *     <li>There is a <b><i>Phenotype Association</i></b> section.</li>
     *     <li>Gene page title starts with <b><i>Gene:</i></b></li>
     *     <li>If there is a <b><i>genes</i></b> HTML table, validates that:
     *         <ul>
     *             <li>Each row has a p-value</li>
     *             <li>Each row has a valid graph link (the graph pages themselves
     *                 are not checked here as they take too long)</li>
     *             <li>The sex icon count matches <i>Total number of results</i> count</li>
     *             <li><b><i>TSV</i></b> and <b><i>XLS</i></b> downloads are valid</li>
     *         </ul>
     *     </li>
     *     <li>A <b><i>genes</i></b> HTML table is present if <code>
     *         genesTableRequired</code> is <code>true</code></li>
     *     <li>There are 3 buttons:</li>
     *     <ul>
     *         <li><b><i>Login to register interest</i></b></li>
     *         <li><b><i>Order</i></b></li>
     *         <li><b><i>KOMP</i></b></li>
     *     </ul>
     * </ul>
     * @param genesTableRequired If set to true, there must be a phenotype
     * HTML table or an error is logged. If false, no error is logged if there
     * is no phenotype HTML table.
     * @return validation status
     */
    public PageStatus validate(boolean genesTableRequired) {
        PageStatus status = new PageStatus();
        
        // Validate title starts with 'Gene:'
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[@id='top']")));
        if ( ! element.getText().startsWith("Gene:")) {
            status.addError("Expected gene page title to start with 'Gene:'.");
        }
        
        // Validate there is a 'Phenotype Association' section.
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@id='section-associations']")));
        } catch (Exception e) {
            status.addError("Expected 'Phenotype Association' section.");
        }
        
        // If there is a 'genes' HTML table, validate it.
        if (hasGenesTable) {
            // Validate that there is a 'pheontypes' HTML table by loading it.
            geneTable.load();                                                      // Load all of the genes table pageMap data. Use preAndPostQcList.
            List<List<String>> preAndPostQcList = geneTable.getPreAndPostQcList();
            int sexIconCount = 0;
            String cell;
            int i = 0;
            for (List<String> row : preAndPostQcList) {
                if (i++ == 0)
                    continue;
                cell = row.get(GeneTable.COL_INDEX_GENES_SEX);
                if ((cell.equals("male")) || (cell.equals("female")))
                    sexIconCount++;
                else if (cell.equals("both"))
                    sexIconCount += 2;

                //   Verify p value.
                cell = row.get(GeneTable.COL_INDEX_GENES_P_VALUE);
                if (cell == null) {
                    status.addError("Missing or invalid P Value. URL: " + target);
                }

                cell = row.get(GeneTable.COL_INDEX_GENES_GRAPH);
                if ((cell == null) || (cell.trim().isEmpty())) {
                    status.addError("Missing graph link. URL: " + target);
                }
            }

            // Verify resultsCount on page against the phenotype table's count of Sex icons.
            if (sexIconCount != resultsCount) {
                status.addError("Result counts don't match. Result count = " + resultsCount + " but Sex icon count = " + sexIconCount);
            }

            // Validate the download links.
            status = validateDownload();
        } else {
            if (genesTableRequired) {
                status.addError("Expected genes HTML table but found none.");
            }
        }
        
        // Buttons
        List<WebElement> buttons = driver.findElements(By.className("btn"));
        // ... count
        if (buttons.size() != 3) {
            status.addError("Expected 3 buttons but found " + buttons.size());
        }
        // ... Button text
        String[] buttonTitlesArray = { "Login to register interest", "Order", "KOMP" };
        List<String> buttonTitles = new ArrayList(Arrays.asList(buttonTitlesArray));
        for (WebElement webElement : buttons) {
            String buttonText = webElement.getText();
            if ( ! buttonTitles.contains(buttonText)) {
                status.addError("Expected button with title '" + buttonText + "' but none was found.");
            }
        }
        
        return status;
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Waits for the gene page to load.
     */
    private void load() {
        driver.get(target);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));

        List<WebElement> elements;
        // Determine if this page has images.
        elements = driver.findElements(By.xpath("//h2[@id='section-images']"));
        hasImages = ! elements.isEmpty();
 
        // Determine if this page has phenotype associations. If it does, get the results count.
        try {
            elements = driver.findElements(By.xpath("//table[@id='genes']"));
            hasGenesTable = ! elements.isEmpty();
            if (hasGenesTable) {
                elements = driver.findElements(By.xpath("//div[@id='phenotypesDiv']/div[@class='container span12']/p[@class='resultCount']"));
                String s = elements.get(0).getText().replace("Total number of results: ", "");
                resultsCount = Utils.tryParseInt(s);
            }
        } catch (Exception e) {
            throw new RuntimeException("GenePage.load(): page appears to have a 'genes' HTML table but it was not found.");
        }
        
        hasGraphs = (resultsCount > 0);
    }
    
    /**
     * Compares a single row of a pageMap grid selected by pageMapIndex to a
     * single row of a downloadData grid selected by downloadIndex.
     * @param pageMap genes HTML table store
     * @param downloadData download data store
     * @param pageMapIndex pageMap row index
     * @param downloadIndex download row index
     * @return 
     */
    private int compareRowData(GridMap pageMap, GridMap downloadData, int pageMapIndex, int downloadIndex) {
        
        // Validate the page's genes HTML table values against the first row of the download values.
        // If sex = "both", validate against the second download row as well.
        int errorCount = 0;
        List<String> colErrors = new ArrayList();
        String downloadCell;
        String pageCell;

        pageCell = pageMap.getCell(1, GeneTable.COL_INDEX_GENES_PHENOTYPE);
        downloadCell = downloadData.getCell(1, DownloadGeneMap.COL_INDEX_PHENOTYPE).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: phenotype mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = StringEscapeUtils.unescapeHtml4(pageMap.getCell(1, GeneTable.COL_INDEX_GENES_ALLELE));
        downloadCell = downloadData.getCell(1, DownloadGeneMap.COL_INDEX_ALLELE).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: allele mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(1, GeneTable.COL_INDEX_GENES_ZYGOSITY);
        downloadCell = downloadData.getCell(1, DownloadGeneMap.COL_INDEX_ZYGOSITY).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: zygosity mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        // Special case: if the page sex is "both", use "female" to compare against the download, as "female" is sorted first in the download.
        pageCell = pageMap.getCell(1, GeneTable.COL_INDEX_GENES_SEX);
        pageCell = (pageCell.compareTo("both") == 0 ? "female" : pageCell);
        downloadCell = downloadData.getCell(1, DownloadGeneMap.COL_INDEX_SEX).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: sex mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(1, GeneTable.COL_INDEX_GENES_PROCEDURE_PARAMETER);
        downloadCell = downloadData.getCell(1, DownloadGeneMap.COL_INDEX_PROCEDURE_PARAMETER).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: procedure | parameter mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(1, GeneTable.COL_INDEX_GENES_PHENOTYPING_CENTER);
        downloadCell = downloadData.getCell(1, DownloadGeneMap.COL_INDEX_PHENOTYPING_CENTER).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: phenotyping center mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(1, GeneTable.COL_INDEX_GENES_SOURCE);
        downloadCell = downloadData.getCell(1, DownloadGeneMap.COL_INDEX_SOURCE).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: source mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        pageCell = pageMap.getCell(1, GeneTable.COL_INDEX_GENES_P_VALUE);
        downloadCell = downloadData.getCell(1, DownloadGeneMap.COL_INDEX_P_VALUE).trim();
        if ( ! pageCell.equals(downloadCell))
            colErrors.add("ERROR: p value mismatch. Page: '" + pageCell + "'. Download: '" + downloadCell + "'");

        // When testing using http, the download link compare fails because the page url uses http
        // but the download graph link uses https. Ignore the protocol (but not the hostname).
        pageCell = TestUtils.removeProtocol(pageMap.getCell(1, GeneTable.COL_INDEX_GENES_GRAPH).trim());
        
        downloadCell = TestUtils.removeProtocol(downloadData.getCell(1, DownloadGeneMap.COL_INDEX_GRAPH).trim());
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
            String message = "Expected page for ID " + geneId + "(" + target + ") but found none.";
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
     * @param targetUrlBase The page's download target url, such as /mi/impc/dev/phenotype-archive/export?xxxxxxx...'
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
            String message = "Expected page for ID " + geneId + "(" + target + ") but found none.";
            status.addError(message);
        }  catch (Exception e) {
            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            status.addError(message);
        }
        
        return new GridMap(data, target);
    }
    
    /**
     * Compares the first non-heading row of data from this class's 'genes'
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
    private PageStatus validateDownload() {
        PageStatus status = new PageStatus();
        GridMap pageMap = geneTable.load();                                        // Load all of the genes table pageMap data.
        
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
     * Internal validation comparing a loaded <code>pageMap</code> store with a
     * loaded <code>downloadData</code> store
     * @param pageMap A loaded genes table store
     * @param downloadData a loaded download store
     * @return status
     */
    private PageStatus validateDownload(GridMap pageMap, GridMap downloadData) {
        PageStatus status = new PageStatus();
        
        // If the downloadData body has no data, no more validation need be done.
        if (downloadData.getBody().length == 0)
            return status;
        
        // Check that the genes table page line count equals the download stream line count.
        // Since the genes table contains a single row for both sexes but the download file
        // contains a row for every sex, use the genes table's sex count rather than the row count.
        int sexIconCount = TestUtils.getSexIconCount(pageMap, GeneTable.COL_INDEX_GENES_SEX);
        int bufferedSexIconCount = (int)Math.round(Math.floor(sexIconCount * 1.5));
        
        // If the genes sex count is not equal to the download row count, then:
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
        String downloadCell = downloadData.getCell(0, DownloadGeneMap.COL_INDEX_SEX).trim();
        String pageCell = pageMap.getCell(0, GeneTable.COL_INDEX_GENES_SEX).trim();
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