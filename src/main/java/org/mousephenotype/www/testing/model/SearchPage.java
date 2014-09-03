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
import java.util.Random;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page.
 */
public class SearchPage {
    private final WebDriver            driver;
    private final int                  timeoutInSeconds;
    private final PhenotypePipelineDAO phenotypePipelineDAO;
    private final String               baseUrl;
    private final WebDriverWait        wait;
    private String                     target;
    private Random                     random = new Random();
    
    private SearchGeneTable      geneTable;
    private SearchPhenotypeTable phenotypeTable;
    private SearchDiseaseTable   diseaseTable;
    private SearchAnatomyTable   anatomyTable;
    private SearchProcedureTable procedureTable;
    private SearchImageTable     imageTable;
    
    
    // These are the core names.
    public static final String GENE_CORE       = "gene";
    public static final String PHENOTYPE_CORE  = "phenotype";
    public static final String DISEASE_CORE    = "disease";
    public static final String ANATOMY_CORE    = "anatomy";
    public static final String PROCEDURES_CORE = "procedures";
    public static final String IMAGES_CORE     = "images";
    
    // The facets shown on the left.
    public enum Facet {
        GENES,
        PHENOTYPES,
        DISEASES,
        ANATOMY,
        PROCEDURES,
        IMAGES
    }
    
    // Page directives (i.e. pagination buttons)
    public enum PageDirective {
        PREVIOUS,
        FIRST_NUMBERED,
        SECOND_NUMBERED,
        THIRD_NUMBERED,
        FOURTH_NUMBERED,
        FIFTH_NUMBERED,
        ELLIPSIS,
        LAST,
        NEXT
    }
    
    public enum DownloadType {
        PAGINATED_TSV,
        PAGINATED_XLS,
        ALL_TSV,
        ALL_XLS
    }
    
    /**
     * Creates a new <code>SearchPage</code> instance. No web page is loaded.
     * @param driver Web driver
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     * @param phenotypePipelineDAO
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     */
    public SearchPage(WebDriver driver, int timeoutInSeconds, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl) {
        this(driver, timeoutInSeconds, null, phenotypePipelineDAO, baseUrl);
        this.target = driver.getCurrentUrl();
    }
    
    /**
     * Creates a new <code>SearchPage</code> instance attempting to load the
     * search web page at <code>target</code>.
     * @param driver Web driver
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     * @param target target search URL
     * @param phenotypePipelineDAO
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @throws RuntimeException If the target cannot be set
     */
    public SearchPage(WebDriver driver, int timeoutInSeconds, String target, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl) throws RuntimeException {
        this.driver = driver;
        this.timeoutInSeconds = timeoutInSeconds;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.baseUrl = baseUrl;
        wait = new WebDriverWait(driver, timeoutInSeconds);
        
        if ((target != null) && ( ! target.isEmpty())) {
            try {
                driver.get(target);
            } catch (Exception e) {
                throw new RuntimeException("EXCEPTION: " + e.getLocalizedMessage() + "\ntarget: '" + target + "'");
            }
            this.target = target;
        }
    }
    
    public void clickDownloadButton(DownloadType downloadType) {
        // show the toolbox if it is not already showing.
        String style = driver.findElement(By.xpath("//div[@id='toolBox']")).getAttribute("style");
        if ( ! style.contains("block;")) {
            driver.findElement(By.xpath("//span[@id='dnld']")).click();
        }
        
        String className = "";
        
        switch (downloadType) {
            case PAGINATED_TSV: className = "tsv_grid"; break;
                
            case PAGINATED_XLS: className = "xls_grid"; break;
                
            case ALL_TSV:       className = "all_tsv";  break;
                
            case ALL_XLS:       className = "all_xls";  break;
        }
        
        driver.findElement(By.xpath("//button[contains(@class, '" + className + "')]")).click();
    }
    
    /**
     * Clicks the facet and returns the result count. This has the side effect of
     * waiting for the page to finish loading.
     * 
     * @param facet desired facet to click
     * @return the [total] results count
     */
    public int clickFacet(Facet facet) {
        driver.findElement(By.xpath("//li[@id='" + getFacetId(facet) + "']")).click();
        
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'dataTable')]")));              // Wait for facet to load.
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'dataTables_paginate')]")));    // Wait for page buttons to load.
        } catch (Exception e) {
            System.out.println("SearchPage.clickFacet: wait timed out: " + e.getLocalizedMessage());
        }
        
        return getResultCount();
    }
    
    /**
     * Clicks the given page button. This has the side effect of waiting for
     * the page to finish loading. <b>Note:</b> The pageButton may be disabled
     * or it may be the ellipsis, in which case the form simply won't change.
     * @param pageButton the page button to click
     * @throws Exception if no such button exists
     */
    public void clickPageButton(PageDirective pageButton) throws Exception {
        List<WebElement> ulElements = driver.findElements(By.xpath("//div[contains(@class, 'dataTables_paginate')]/ul/li"));
        try {
            switch (pageButton) {
                case PREVIOUS:          ulElements.get(0).click();      break;

                case FIRST_NUMBERED:    ulElements.get(1).click();      break;

                case SECOND_NUMBERED:   ulElements.get(2).click();      break;

                case THIRD_NUMBERED:    ulElements.get(3).click();      break;

                case FOURTH_NUMBERED:   ulElements.get(4).click();      break;

                case FIFTH_NUMBERED:    ulElements.get(5).click();      break;
                    
                case ELLIPSIS:                                          break;

                case LAST:              ulElements.get(7).click();      break;

                case NEXT:              ulElements.get(8).click();      break;
            }
        } catch (Exception e) {
            System.out.println("SearchPage.clickPageButton exception: " + e.getLocalizedMessage());
            throw e;
        }
        
        getResultCount();                                                       // Called purely to wait for the page to finish loading.
    }
    
    /**
     * Clicks on a random page button, within the scope of available pages. Note
     * that the button clicked may be disabled or may be the ellipsis, in which
     * case the already-selected page won't change. Calling this method has the
     * side effect of waiting for the page to finish loading.
     * 
     * There are a minimum of 3 and a maximum of 9 buttons, distributed as follows:
     * <ul><li>3 for 'previous', '1', 'next'</li>
     * <li>3 for 'previous', '1', '2','next'</li>
     * <li>4 for 'previous', '1', '2', '3', 'next'</li>
     * <li>9 for 'previous', '1', '2', '3', '4', '5', '...', '4852', 'next'</li></ul>
     * 
     * @throws Exception if no such button exists
     * @return the <code>PageDirective</code> of the clicked button
     */
    public PageDirective clickPageButton() throws Exception {
        int max = getNumPageButtons();
        int randomPageNumber = random.nextInt(max);
        
        WebElement element = getButton(randomPageNumber);
        if (element.getAttribute("class").contains("disabled")) {
            if (randomPageNumber == 0) {
                System.out.println("Changing randomPageNumber from 0 to 1.");
                randomPageNumber++; 
            } else {
                System.out.println("Changing randomPageNumber from " + randomPageNumber + " to " + (randomPageNumber - 1) + ".");
                randomPageNumber--;
            }
        } else if (element.getText().contains("...")) {
            System.out.println("Changing randomPageNumber from " + randomPageNumber + " to " + (randomPageNumber - 1) + ".");
            randomPageNumber--;
        }
        
        PageDirective pageDirective = getPageDirective(randomPageNumber);
        System.out.println("SearchPage.clickPageButton(): max = " + max + ". randomPageNumber = " + randomPageNumber + ". Clicking " + pageDirective + " button.");
        clickPageButton(pageDirective);
        
        getResultCount();                                                       // Called purely to wait for the page to finish loading.
        
        return pageDirective;
    }
    
    /**
     * @return Returns the anatomy table [maGrid], if there is one; or an
     * empty one if there is not
     */
    public SearchAnatomyTable getAnatomyTable() {
        if (hasAnatomyTable()) {
            if (anatomyTable == null) {
                anatomyTable = new SearchAnatomyTable(driver, timeoutInSeconds);
            }
        }
        
        return anatomyTable;
    }
    
    /**
     * Returns the <code>WebElement</code> button matching the given buttonIndex.
     * @param buttonIndex 0-relative button index
     * @return The <code>WebElement</code> matching <code>buttonIndex</code>
     * @throws IndexOutOfBoundsException if <code>buttonIndex</code> lies outside
     * the bounds of the range of page buttons
     */
    public WebElement getButton(int buttonIndex) throws IndexOutOfBoundsException {
        List<WebElement> ulElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'dataTables_paginate')]/ul/li")));
        if (buttonIndex < ulElements.size()) {
            return ulElements.get(buttonIndex);
        } else {
            throw new IndexOutOfBoundsException("SearchPage.getPage(int pageIndex): pageIndex: " + buttonIndex + ". # elements: " + ulElements.size());
        }
    }
    
    /**
     * @return Returns the disease table [diseaseGrid], if there is one; or an
     * empty one if there is not
     */
    public SearchDiseaseTable getDiseaseTable() {
        if (hasDiseaseTable()) {
            if (diseaseTable == null) {
                diseaseTable = new SearchDiseaseTable(driver, timeoutInSeconds);
            }
        }
        
        return diseaseTable;
    }
    
    /**
     * 
     * @param facet desired facet to click
     * @return the desired facet count
     */
    public int getFacetCount(Facet facet) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@id='" + getFacetId(facet) + "']/span[@class='fcount' or @class='fcount grayout']")));
        Integer niCount = Utils.tryParseInt(element.getText());
        
        return (niCount == null ? 0 : niCount);
    }
    
    /**
     * @return Returns the gene table [geneGrid], if there is one; or an
     * empty one if there is not
     */
    public SearchGeneTable getGeneTable() {
        if (hasGeneTable()) {
            if (geneTable == null) {
                geneTable = new SearchGeneTable(driver, timeoutInSeconds);
            }
        }
        
        return geneTable;
    }
    
    /**
     * @return Returns the image table [imagesGrid], if there is one; or an
     * empty one if there is not
     */
    public SearchImageTable getImageTable() {
        if (hasImageTable()) {
            if (imageTable == null) {
                imageTable = new SearchImageTable(driver, timeoutInSeconds);
            }
        }
        
        return imageTable;
    }
    
    /**
     * 
     * @return The number of pagination buttons displayed. e.g.:
     * <ul><li>3 for 'previous', '1', 'next'</li>
     * <li>3 for 'previous', '1', '2','next'</li>
     * <li>4 for 'previous', '1', '2', '3', 'next'</li>
     * <li>9 for 'previous', '1', '2', '3', '4', '5', '...', '4852', 'next'</li></ul>
     */
    public int getNumPageButtons() {
        return driver.findElements(By.xpath("//div[contains(@class, 'dataTables_paginate')]/ul/li")).size();
    }
    
    public class Showing {
        public final int first;
        public final int last;
        public final int total;
        
        public Showing(){
            String[] showing = driver.findElement(By.xpath("//div[@id='geneGrid_info']")).getText().split(" ");
            first = Utils.tryParseInt(showing[1]);
            last = Utils.tryParseInt(showing[3]);
            total = Utils.tryParseInt(showing[5]);
        }
    }
    
    /**
     * 
     * @return A <code>Showing</code> instance with the interesting inteter
     * parts of the <i>Showing</i> page results string.
     */
    public Showing getShowing() {
        return new Showing();
    }
    
    /**
     * Return the matching <code>PageDirective</code>
     * 
     * @param buttonIndex 0-relative button index
     * @return the matching <code>PageDirective</code>
     * @throws IndexOutOfBoundsException if <code>buttonIndex</code> lies outside
     * the bounds of the range of page buttons
     * 
     * Depending on the number of results, the button array can look like any
     * of the following:
     * <ul>
     * <li>'previous'  1  'next'                         (e.g. search for akt2)</li>
     * <li>'previous'  1  2  'next'                      (e.g. search for head)</li>
     * <li>'previous'  1  2  3  'next'                   (e.g. search for tail)</li>
     * <li>'previous'  1  2  3  4  'next'                (e.g. search for leg, click on Diseases facet)</li>
     * <li>'previous'  1  2  3  4  5  'next'             (e.g. search for bladder)</li>
     * <li>'previous'  1  2  3  4  5  ...  4356  'next'  (e.g. no search criteria)</li>
     * </ul>
     */
    public PageDirective getPageDirective(int buttonIndex) throws IndexOutOfBoundsException {
//        System.out.println("SearchPage.getPageDirective(): buttonIndex = " + buttonIndex + ". numPageButtons = " + getNumPageButtons());
        switch (getNumPageButtons()) {
            case 3:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.NEXT;
                }
            case 4:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.SECOND_NUMBERED;
                    case 3:     return PageDirective.NEXT;
                }
                
            case 5:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.SECOND_NUMBERED;
                    case 3:     return PageDirective.THIRD_NUMBERED;
                    case 4:     return PageDirective.NEXT;
                }
                
            case 6:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.SECOND_NUMBERED;
                    case 3:     return PageDirective.THIRD_NUMBERED;
                    case 4:     return PageDirective.FOURTH_NUMBERED;
                    case 5:     return PageDirective.NEXT;
                }
                
            case 7:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.SECOND_NUMBERED;
                    case 3:     return PageDirective.THIRD_NUMBERED;
                    case 4:     return PageDirective.FOURTH_NUMBERED;
                    case 5:     return PageDirective.FIFTH_NUMBERED;
                    case 6:     return PageDirective.NEXT;
                }
                
            case 9:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.SECOND_NUMBERED;
                    case 3:     return PageDirective.THIRD_NUMBERED;
                    case 4:     return PageDirective.FOURTH_NUMBERED;
                    case 5:     return PageDirective.FIFTH_NUMBERED;
                    case 6:     return PageDirective.ELLIPSIS;
                    case 7:     return PageDirective.LAST;
                    case 8:     return PageDirective.NEXT;
                }
        }
        
        throw new IndexOutOfBoundsException("SearchPage.getPageDirective: buttonIndex = " + buttonIndex + ". # buttons: " + getNumPageButtons());
    }
    
    /**
     * @return Returns the phenotype table [mpGrid], if there is one; or an
     * empty one if there is not
     */
    public SearchPhenotypeTable getPhenotypeTable() {
        if (hasPhenotypeTable()) {
            if (phenotypeTable == null) {
                phenotypeTable = new SearchPhenotypeTable(driver, timeoutInSeconds);
            }
        }
        
        return phenotypeTable;
    }
    
    /**
     * @return Returns the procedure table [pipelineGrid], if there is one; or an
     * empty one if there is not
     */
    public SearchProcedureTable getProcedureTable() {
        if (hasProcedureTable()) {
            if (procedureTable == null) {
                procedureTable = new SearchProcedureTable(driver, timeoutInSeconds);
            }
        }
        
        return procedureTable;
    }
    
    /**
     * @return The result count. This has the side effect of waiting for the
     * page to finish loading.
     */
    public int getResultCount() {
        // Sometimes, even though we wait, the element text is still empty. Eventually it arrives.
        WebElement element;
        
        int i = 0;
        while ((element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='resultMsg']/span[@id='resultCount']/a")))).getText().isEmpty()) {
            System.out.println("WAITING[" + i + "]");
            TestUtils.sleep(100);
            i++;
            if (i > 20)
                return -1;
        }

        int pos = element.getText().indexOf(" ");
        String sCount = element.getText().substring(0, pos);
        Integer niCount = Utils.tryParseInt(sCount);
        
        return (niCount == null ? 0 : niCount);
    }
    
    /**
     * 
     * @return The timeout, in seconds
     */
    public long getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    /**
     * 
     * @return The base url
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * 
     * @return true if this search page has a maGrid HTML table; false
     * otherwise
     */
    public boolean hasAnatomyTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='maGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 
     * @return true if this search page has a diseaseGrid HTML table; false
     * otherwise
     */
    public boolean hasDiseaseTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='diseaseGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 
     * @return true if this search page has a geneGrid HTML table; false
     * otherwise
     */
    public boolean hasGeneTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='geneGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 
     * @return true if this search page has a imagesGrid HTML table; false
     * otherwise
     */
    public boolean hasImageTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='imagesGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 
     * @return true if this search page has a mpGrid HTML table; false
     * otherwise
     */
    public boolean hasPhenotypeTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='mpGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 
     * @return true if this search page has a pipelineGrid HTML table; false
     * otherwise
     */
    public boolean hasProcedureTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='pipelineGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Submits the string in <code>searchString</code> to the server. This has
     * the side effect of waiting for the page to finish loading.
     * 
     * @param searchString The keystrokes to be sent to the server
     * @return the result count.
     */
    public int submitSearch(String searchString) {
        WebElement weInput = driver.findElement(By.cssSelector("input#s"));
        weInput.clear();
        weInput.sendKeys(searchString + "\n");
        
        return getResultCount();
    }
    
    
    // PRIVATE METHODS
    
    
    private String getFacetId(Facet facet) {
        String id = "";
        
        switch (facet) {
            case GENES:
                id = "gene";
                break;
                
            case PHENOTYPES:
                id = "mp";
                break;
                
            case DISEASES:
                id = "disease";
                break;
                
            case ANATOMY:
                id = "ma";
                break;
                
            case PROCEDURES:
                id = "pipeline";
                break;
                
            case IMAGES:
                id = "images";
                break;
        }
        
        return id;
    }
    
    /**
     * Get the full data store matching the download type
     * 
     * @param downloadType The download button type (e.g. page/all, tsv/xls)
     * @param baseUrl A fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @param downloadUrlBase The page's download base url, such as /mi/impc/dev/phenotype-archive/export?xxxxxxx...'
     * @param status Indicates the success or failure of the operation
     * @return the full TSV data store
     */
    private String[][] getDownload(DownloadType downloadType, String baseUrl) {
        String[][] data = new String[0][0];
        String downloadUrlBase = getDownloadUrlBase(downloadType);
        
        try {
            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
            // getDownloadTargetUrlBase() typically returns a path of the form '/mi/impc/dev/phenotype-archive/export?xxxxxxx...'.
            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export?' with the baseUrl.
            int pos = downloadUrlBase.indexOf("/export?");

            downloadUrlBase = downloadUrlBase.substring(pos);
            String downloadTarget = baseUrl + downloadUrlBase;
            URL downloadUrl = new URL(downloadTarget);

            switch (downloadType) {
                case ALL_TSV:
                case PAGINATED_TSV:
                    // Get the download stream and statistics for the TSV stream.
                    DataReaderTsv dataReaderTsv = new DataReaderTsv(downloadUrl);
                    data = dataReaderTsv.getData();
                    break;
                    
                case ALL_XLS:
                case PAGINATED_XLS:
                    // Get the download stream and statistics for the XLS stream.
                    DataReaderXls dataReaderXls = new DataReaderXls(downloadUrl);
                    data = dataReaderXls.getData();
                    break;
            }
        } catch (NoSuchElementException | TimeoutException te) {
            throw new RuntimeException("SearchPage.getDownload: Expected page for target: " + target + ".");
        } catch (IllegalArgumentException iae) {
            // This is thrown when the GENE download stream is large (e.g. 48k rows) on an unfiltered gene list (ALL_XLS).
            System.out.println("EXCEPTION: SearchPage.getDownload(): " + iae.getLocalizedMessage());
        } catch (Exception e) {
            String message = "EXCEPTION: SearchPage.getDownload: processing target URL " + target + ": " + e.getLocalizedMessage();
            System.out.println(message);
            e.printStackTrace();
            try { throw e; } catch (Exception ee) { throw new RuntimeException(message); }
        }
        
        return data;
    }
    
    /**
     * Return the download url base based on download type
     * @param downloadType The download button type (e.g. page/all, tsv/xls)
     * @return the download url base embedded in the <i>downloadType</i> button.
     */
    private String getDownloadUrlBase(DownloadType downloadType) {
        // show the toolbox if it is not already showing.
        String style = driver.findElement(By.xpath("//div[@id='toolBox']")).getAttribute("style");
        if ( ! style.contains("block;")) {
            driver.findElement(By.xpath("//span[@id='dnld']")).click();
        }
        
        String className = "";
        
        switch (downloadType) {
            case PAGINATED_TSV: className = "tsv_grid"; break;
            case PAGINATED_XLS: className = "xls_grid"; break;
            case ALL_TSV:       className = "tsv_all";  break;
            case ALL_XLS:       className = "xls_all";  break;
        }
        
        return driver.findElement(By.xpath("//button[contains(@class, '" + className + "')]")).getAttribute("data-exporturl");
    }
    
    /**
     * Compares each facet's grid (on the right-hand side of the search page)
     * with each of the four download data streams (page/all and tsv/xls). Any
     * errors are returned in the <code>PageStatus</code> instance.

     * @param facet facet
     * @return page status instance
     */
    public PageStatus validateDownload(Facet facet) {
        PageStatus status = new PageStatus();
        
        DownloadType[] downloadTypes = {
              DownloadType.PAGINATED_TSV
            , DownloadType.PAGINATED_XLS
    // Don't test the 'ALL_xxx' download types as they are known to be broken in both production and testing; the server rejects streams that are too long.
//            , DownloadType.ALL_TSV
//            , DownloadType.ALL_XLS
        };
        
        String[][] data;
        // Validate the download types for this facet.
        for (DownloadType downloadType : downloadTypes) {
            data = getDownload(downloadType, baseUrl);                          // Get the data for this download type.
            SearchFacetTable table = getFacetTable(facet);                      // Get the facet table.
            status.add(table.validateDownload(data));                           // Validate it.
            
            if (status.hasErrors()) {
                System.out.println("VALIDATION ERRORS:\n" + status.toStringErrorMessages());
            }
        }
        
        return status;
    }
    
    /**
     * Given a facet, returns the matching generic <code>SearchFacetTable</code>.
     * @param facet facet
     * @return The matching generic <code>SearchFacetTable</code>.
     */
    private SearchFacetTable getFacetTable(Facet facet) {
        switch (facet) {
            case ANATOMY:       return getAnatomyTable();
            case DISEASES:      return getDiseaseTable();
            case GENES:         return getGeneTable();
            case IMAGES:        return getImageTable();
            case PHENOTYPES:    return getPhenotypeTable();
            case PROCEDURES:    return getProcedureTable();
        }
        
        throw new RuntimeException("SearchPage.getFacetTable(): Invalid facet " + facet + ".");
    }
    
}
