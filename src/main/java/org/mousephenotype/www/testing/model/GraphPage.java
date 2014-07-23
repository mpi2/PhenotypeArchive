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

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a graph page.
 */
public class GraphPage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final String target;
    protected final String id;
    protected PageStatus status;
    
    private GraphGlobalTestTable graphTable;
    protected final PhenotypePipelineDAO phenotypePipelineDAO;
    
    // Page variables common to all graphs
    private String alleleSymbol;
    private String background;
    private String geneSymbol;
    private String metadataGroup;
    private String parameterName;
    private String parameterStableId;
    private String phenotypingCenter;
    private String pipelineName;
    private String title;
    
    // Page variables exclusive to unidimensional graphs
    private String graphByDateUrl;
    private String[][] globalTestTable;
    private String[][] continuousTable;
    
    // Page variables exclusive to categorical graphs
    private String[][] catTable;
    
    // Database parameter variables
    private ObservationType graphType;
    private Parameter parameterObject;
    
    private final String GRAPH_BY_DATE = "Graph by date";

    /**
     * 
     * @param driver <code>WebDriver</code> instance
     * @param wait <code>WebDriverWait</code> instance
     * @param target gene or phenotype page target
     * @param id gene or phenotype id
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     */
    public GraphPage(WebDriver driver, WebDriverWait wait, String target, String id, PhenotypePipelineDAO phenotypePipelineDAO) {
        this.driver = driver;
        this.wait = wait;
        this.target = target;
        this.id = id;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        status = new PageStatus();
        
        load();
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
    
    
    
    public WebDriver getDriver() {
        return driver;
    }

    public WebDriverWait getWait() {
        return wait;
    }

    public String getTarget() {
        return target;
    }

    public String getId() {
        return id;
    }

    public PageStatus getStatus() {
        return status;
    }

    public GraphGlobalTestTable getGraphTable() {
        return graphTable;
    }

    public PhenotypePipelineDAO getPhenotypePipelineDAO() {
        return phenotypePipelineDAO;
    }

    public String getAlleleSymbol() {
        return alleleSymbol;
    }

    public String getBackground() {
        return background;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public String getMetadataGroup() {
        return metadataGroup;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getParameterStableId() {
        return parameterStableId;
    }

    public String getPhenotypingCenter() {
        return phenotypingCenter;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public String getTitle() {
        return title;
    }

    public String getGraphByDateUrl() {
        return graphByDateUrl;
    }

    public String[][] getGlobalTestTable() {
        return globalTestTable;
    }

    public String[][] getContinuousTable() {
        return continuousTable;
    }

    public String[][] getCatTable() {
        return catTable;
    }

    public ObservationType getGraphType() {
        return graphType;
    }

    public Parameter getParameterObject() {
        return parameterObject;
    }

    public String getGRAPH_BY_DATE() {
        return GRAPH_BY_DATE;
    }

////////    /**
////////     * Uses the <code>WebDriver</code> driver, provided via the constructor to
////////     * parse the graph page (which must currently be showing).
////////     * @return a new <code>GraphParsingStatus</code> status instance containing
////////     * failure counts and messages.
////////     */
////////    public GraphParsingStatus parse() {
////////        return parse(new GraphParsingStatus());
////////    }
    
////////    /**
////////     * Uses the <code>WebDriver</code> driver, provided via the constructor to
////////     * parse the graph page (which must currently be showing).
////////     * @param status caller-supplied status instance to be used
////////     * @return the passed-in <code>GraphParsingStatus</code> status, updated with
////////     * any failure counts and messages.
////////     */
////////    public GraphParsingStatus parse(GraphParsingStatus status) {
//////////        // Save title.
//////////        try {
//////////            title = driver.findElement(By.cssSelector("h2#section-associations")).getText();
//////////        } catch (Exception e) {
//////////            status.addFail("ERROR: parse() unable to get 'Allele' title.");
//////////        }
//////////        
//////////        // Save parameter and parameterStableId.
//////////        List<WebElement> textElementList = driver.findElements(By.cssSelector("div[id*='chart'] text.highcharts-title"));
//////////        if (textElementList.size() > 0)
//////////            parameterName = textElementList.get(0).getText().trim();
//////////        textElementList = driver.findElements(By.cssSelector("div[id*='chart'] text.highcharts-subtitle"));
//////////        if (textElementList.size() > 0)
//////////            parameterStableId = textElementList.get(0).getText().trim();
//////////        
//////////        // Save other link(s) of interest.
//////////        List<WebElement> otherLinksList = driver.findElements(By.cssSelector("div.section a").linkText(GRAPH_BY_DATE));
//////////        if ( ! otherLinksList.isEmpty())
//////////            graphByDateUrl = otherLinksList.get(0).getAttribute("href");
////////        
////////        // Use the parameterStableId to get the Parameter object.
////////        parameterObject = phenotypePipelineDAO.getParameterByStableId(parameterStableId);
////////        
////////        // Set the graph type from the parameterDAO.
////////        graphType = Utilities.checkType(parameterObject, parameterObject.getDatatype());
////////        
////////        graphType = Utilities.checkType(parameterObject, parameterObject.getDatatype());
////////        
////////        
////////        
////////        // Determine if there is a summary table, and if so, add its data.
////////        // Summary tables are identified by the table class 'globalTest'.
////////        WebElement summaryTable = null;
////////        // Unidimensional (and Scatter view).
////////        List<WebElement> uniTableList = driver.findElements(By.cssSelector("table.globalTest"));
////////        if ( ! uniTableList.isEmpty()) {
////////            summaryTable = uniTableList.get(0);
////////        }
////////        if (summaryTable != null) {
////////            graphTable = new GraphGlobalTestTable(summaryTable, driver.getCurrentUrl());
////////        }
////////        
////////        return status;
////////    }
    
    /**
     * Validates this <code>GraphPage</code> instance
     * @return a new <code>GraphParsingStatus</code> status instance containing
     * success and failure counts and messages.
     */
    public final GraphParsingStatus validate() {
        return validate(new GraphParsingStatus());
    }
    
    /**
     * Validates this <code>GraphPage</code> instance, using the caller-provided
     * status instance
     * @param status caller-supplied status instance to be used
     * @return the passed-in <code>GraphParsingStatus</code> status, updated with
     * any success and failure counts and messages.
     */
    public final GraphParsingStatus validate(GraphParsingStatus status) {
        // Verify title contains 'Allele'.
        if ( ! title.contains("Allele")) {
            status.addFail("ERROR: expected title to contain 'Allele'. Title is '" + title + "'. URL: " + target);
        }
        
        // Verify parameter name on graph matches that in the Parameter instance.
        // NOTE: Time Series graphs have the string 'MEAN ' prepended to the parameter name!!! Test accordingly.
        String expectedParameterName = parameterObject.getName().trim();
        if (graphType == ObservationType.time_series)
            expectedParameterName = "MEAN " + expectedParameterName;
        if (expectedParameterName.compareTo(parameterName) != 0) {
            status.addFail("ERROR: parameter name mismatch. parameter on graph: '" + parameterName + "'. from parameterDAO: " + parameterObject.getName() + ": " + target);
        }
        
        // If there is a summary table (i.e. data is not null/empty), validate it.
        if (graphTable != null) {
            graphTable.validate(status);
        }

        // If this is a Time Series graph or a Unidimensional graph, there must be a 'Graph by date' link.
        // Invoke it, parse it, and compare the two title, parameter, and parameterStableId values. You can't
        // check the Box Plot link because there is no href - just an id named 'goBack'.
        if ((graphType == ObservationType.time_series) || (graphType == ObservationType.unidimensional)) {
            if (graphByDateUrl == null) {
                status.addFail("ERROR: Expected '" + GRAPH_BY_DATE + "' link but none found. URL: " + target);
            }
            GraphPage graphByDatePage = null;
            driver.get(graphByDateUrl);
            try {
                graphByDatePage = new GraphPage(driver, wait, graphByDateUrl, id, phenotypePipelineDAO);
                graphByDatePage.parse(status);
                if (title.compareTo(graphByDatePage.getTitle()) != 0) {
                    status.addFail("ERROR: Expected title '" + title + "' but found '" + graphByDatePage.getTitle() + "'.");
                }
                // NOTE: Time Series graphs have the string 'MEAN ' prepended to the parameter name!!! Test accordingly.
                // USE 'contains' because of prepended 'MEAN ' for time series graphs.
                if ( ! parameterName.contains(graphByDatePage.getParameterName())) {
                    status.addFail("ERROR: Expected parameter '" + parameterName + "' but found '" + graphByDatePage.getParameterName() + "'.");
                }
                if (parameterStableId.compareTo(graphByDatePage.getParameterStableId()) != 0) {
                    status.addFail("ERROR: Expected parameterStableId '" + parameterStableId + "' but found '" + graphByDatePage.getParameterStableId() + "'.");
                }
            } catch (Exception e) {
                status.addFail("ERROR: This graph's '" + GRAPH_BY_DATE + "' is bad. This URL: " + target);
            }

            if (graphType == ObservationType.unidimensional) {
                // If the original graph (represented by the 'this' object') is a Unidimensional graph,
                // it must have a summary table. Compare the two data objects; they should be exactly equal.
                if (graphByDatePage == null) {
                    status.addFail("ERROR: Expected a summary page but found none. " + target);
                } else {
                    if ( ! graphTable.isEqual(graphByDatePage.graphTable)) {
                        status.addFail("ERROR: this graph and its '" + GRAPH_BY_DATE + "' graph are not equal.\nThis URL: " + target + "\n'" + GRAPH_BY_DATE + "' URL: " + graphByDatePage.target);
                    }
                }
            }
        }
        
        return status;
    }
    
//    protected void validateStaticDownloadData(String[][] data, DownloadStructureUnidimensional dsGraph) {
//        // Test graph page parameters against first [non-heading] download stream row.
//        if (data.length < 2) {
//            status.addError(("ERROR: Expected at least one row of data."));
//        } else {
//            String cellValue = data[1][dsGraph.getColIndexAllele()];
//            if (alleleSymbol.compareTo(cellValue) != 0) {
//                status.addError("ERROR: mismatch: page alleleSymbol: '" + alleleSymbol + "'. Download alleleSymbol: '" + cellValue + "'");
//            }
//            cellValue = data[1][dsGraph.getColIndexBackground()];
//            if (background.compareTo(cellValue) != 0) {
//                status.addError("ERROR: mismatch: page background: '" + background + "'. Download background: '" + cellValue + "'");
//            }
//            cellValue = data[1][dsGraph.getColIndexGeneSymbol()];
//            if (geneSymbol.compareTo(cellValue) != 0) {
//                status.addError("ERROR: mismatch: page geneSymbol: '" + geneSymbol + "'. Download geneSymbol: '" + cellValue + "'");
//            }
//            cellValue = data[1][dsGraph.getColIndexMetadataGroup()];
//            if (metadataGroup.compareTo(cellValue) != 0) {
//                status.addError("ERROR: mismatch: page metadata: '" + metadataGroup + "'. Download metadata: '" + cellValue + "'");
//            }
//            cellValue = data[1][dsGraph.getColIndexParameterName()];
//            if (parameterName.compareTo(cellValue) != 0) {
//                status.addError("ERROR: mismatch: page parameterName: '" + parameterName + "'. Download parameterName: '" + cellValue + "'");
//            }
//            cellValue = data[1][dsGraph.getColIndexParameterStableId()];
//            if (parameterStableId.compareTo(cellValue) != 0) {
//                status.addError("ERROR: mismatch: page parameterStableId: '" + parameterStableId + "'. Download parameterStableId: '" + cellValue + "'");
//            }
//            cellValue = data[1][dsGraph.getColIndexPhenotypingCenter()];
//            if (phenotypingCenter.compareTo(cellValue) != 0) {
//                status.addError("ERROR: mismatch: page phenotypingCenter: '" + phenotypingCenter + "'. Download phenotypingCenter: '" + cellValue + "'");
//            }
//            cellValue = data[1][dsGraph.getColIndexPipelineName()];
//            if (pipelineName.compareTo(cellValue) != 0) {
//                status.addError("ERROR: mismatch: page pipelineName: '" + pipelineName + "'. Download pipelineName: '" + cellValue + "'");
//            }
//        }
//    }
    
//    public void validateDynamicDownloadCategorical() {
//        List<WebElement> weList;
//        
//        // Validate that the graph type has the expected components.
//        if (getGraphType() != ObservationType.categorical) {
//            status.addError("ERROR: Expected categorical graph. URL: " + target);
//        }
//                
//        weList = driver.findElements(By.xpath("table[@id='catTable']"));
//        if (weList.isEmpty()) {
//            status.addError("ERROR: categorical table has no catTable.");
//        }
//    }
    
//    public void validateDynamicDownloadUnidimensional() {
//        List<WebElement> weList = null;
//        // Validate that the graph type has the expected components.
//        switch (getGraphType()) {
//            case categorical:
//                weList = driver.findElements(By.xpath("table[@id='catTable']"));
//                if (weList.isEmpty()) {
//                    status.addError("ERROR: categorical table has no catTable.");
//                }
//                break;
//                
//            case unidimensional:
//                if (graphByDateUrl == null) {
//                    status.addError("ERROR: unidimensional graph has no 'Graph by date' link.");
//                }
//                weList = driver.findElements(By.xpath("//table[@class='globalTest']"));
//                if (weList.isEmpty()) {
//                    status.addError("ERROR: unidimensional graph has no 'globalTest' table.");
//                }
//                weList = driver.findElements(By.xpath("//table[@id='continuousTable']"));
//                if (weList.isEmpty()) {
//                    status.addError("ERROR: unidimensional graph has no 'continuousTable' table.");
//                }
//                
//            default:
//                break;
//        }
//    }
    
    
//    protected void validateGraphTable(String[][] data) {
//        System.out.println("BAD!");
//    }
    
////    /**
////     * Test the download links. This test is noticeably different from the gene
////     * and pheno page download link tests in that those pages have phenotype
////     * tables that closely match the download stream. The graph page and download
////     * stream are much different. For the graph page we must test:
////     * <ul><li>that the TSV and XLS links create a download stream</li>
////     * <li>that the graph page parameters, such as <code>pipeline name</code>,
////     * <code>pipelineStableId</code>, <code>parameterName</code>, etc. match</li>
////     * <li>that the HTML graph summary table counts match the sum of the
////     * requisite values in the download stream</li></ul>
////     * 
////     * @param baseUrl the base url from which the download target TSV and XLS
////     * are built
////     * @return status
////     */
////    protected PageStatus validateDownloadProtected(String baseUrl) {
////        try {
////            // Test the TSV.
////            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
////            // getDownloadTargetUrlBase() typically returns a path of the form '/mi/impc/dev/phenotype-archive/export?xxxxxxx...'.
////            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export?' with the baseUrl.
////            String downloadTargetUrlBase = driver.findElement(By.xpath("//div[@id='exportIconsDivGlobal']")).getAttribute("data-exporturl");
////            
////            String downloadTargetTsv = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "tsv", "/export?");
////            
//////            
//////            int pos = downloadTargetUrlBase.indexOf("/export?");
//////            downloadTargetUrlBase = downloadTargetUrlBase.substring(pos);
//////            String downloadTargetTsv = baseUrl + downloadTargetUrlBase + "tsv";
////            
////            // Get the download stream and statistics.
////            URL url = new URL(downloadTargetTsv);
////            DataReaderTsv dataReaderTsv = new DataReaderTsv(url);
////            String[][] data = dataReaderTsv.getData();                          // Get all of the data
////            validateStaticDownloadData(data);                                   // ... and validate it
////            
////            // Test the graph summary table.
////            validateGraphTable(data);
////            
////            
////            // Test the XLS.
////            String downloadTargetXls = TestUtils.patchUrl(baseUrl, downloadTargetUrlBase + "xls", "/export?");
////            
////            // Get the download stream and statistics.
////            url = new URL(downloadTargetXls);
////            DataReaderXls dataReaderXls = new DataReaderXls(url);
////            data = dataReaderXls.getData();                                     // Get all of the data
////            validateStaticDownloadData(data);                                   // ... and validate it
////            
////            // Test the graph summary table.
////            validateGraphTable(data);
////        } catch (NoSuchElementException | TimeoutException te) {
////            String message = "Expected page for ID " + id + "(" + target + ") but found none.";
////            status.addError(message);
////        }  catch (Exception e) {
////            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
////            status.addError(message);
////        }
////        
////        return status;
////    }
    
    
//////    /**
//////     * Test the download links. This test is noticeably different from the gene
//////     * and pheno page download link tests in that those pages have phenotype
//////     * tables that closely match the download stream. The graph page and download
//////     * stream are much different. For the graph page we must test:
//////     * <ul><li>that the TSV and XLS links create a download stream</li>
//////     * <li>that the graph page parameters, such as <code>pipeline name</code>,
//////     * <code>pipelineStableId</code>, <code>parameterName</code>, etc. match</li>
//////     * <li>that the HTML graph summary table counts match the sum of the
//////     * requisite values in the download stream</li></ul>
//////     * 
//////     * @param baseUrl the base url from which the download target TSV and XLS
//////     * are built
//////     * @return status
//////     */
//////    public PageStatus testDownload(String baseUrl) {
//////        PageStatus status = new PageStatus();
//////            
//////        try {
//////            // Test the TSV.
//////            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
//////            // getDownloadTargetUrlBase() typically returns a path of the form '/mi/impc/dev/phenotype-archive/export?xxxxxxx...'.
//////            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export?' with the baseUrl.
//////            String downloadTargetUrlBase = driver.findElement(By.xpath("//div[@id='exportIconsDivGlobal']")).getAttribute("data-exporturl");
//////            int pos = downloadTargetUrlBase.indexOf("/export?");
//////            downloadTargetUrlBase = downloadTargetUrlBase.substring(pos);
//////            String downloadTargetTsv = baseUrl + downloadTargetUrlBase + "tsv";
//////            String downloadTargetXls = baseUrl + downloadTargetUrlBase + "xls";
//////            
//////            // Get the download stream and statistics.
//////            URL url = new URL(downloadTargetTsv);
//////            DataReaderTsv dataReaderTsv = new DataReaderTsv(url);
//////            
//////            
//////            String[][] data = dataReaderTsv.getData();                          // Get all of the data
//////            status = validateStaticDownloadData(data);                          // ... and validate it
//////        } catch (NoSuchElementException | TimeoutException te) {
//////            String message = "Expected page for ID " + id + "(" + target + ") but found none.";
//////            status.addError(message);
//////        }  catch (Exception e) {
//////            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
//////            status.addError(message);
//////        }
//////        
//////        return status;
//////    }
    
    
    // PROTECTED METHODS
    
    
    
    // PRIVATE METHODS
    
    /**
     * Load the page.
     * @return <code>PageStatus</code> telling whether or not the load was 
     * successful.
     */
    private void load() {
        String message;
        
        // Wait for page to load.
        try {
            driver.get(target);
            WebElement baseElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='exportIconsDivGlobal']/p/button[1]")));

            // Initialize page component variables read from page.
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='chart'][@graphurl]/h2[@id='section-associations']")));
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='chart'][@graphurl]/h2[@id='section-associations']")));
            AlleleParser alleleParser = new AlleleParser()
            Line1Parser line1Parser = new Line1Parser();
            Line2Parser line2Parser = new Line2Parser();
            SvgDivParser svgDigParser = new SvgDivParser();
            
            this.alleleSymbol = alleleParser.allele;
            this.background = line1Parser.background;
            this.geneSymbol = alleleParser.gene;
            List<WebElement> weList = baseElement.findElements(By.xpath("//div[@class='section half']/a"));
            if ( ! weList.isEmpty())
                this.graphByDateUrl = weList.get(0).getAttribute(("href"));
            this.metadataGroup = line2Parser.metadataGroup;
            this.parameterName = svgDigParser.parameterName;
            this.parameterStableId = svgDigParser.parameterStableId;
            this.phenotypingCenter = line1Parser.phenotypingCenter;
            this.pipelineName = line1Parser.pipelineName;
            this.title = alleleParser.title;
            
            
  System.out.println("alleleSymbol:      '" + this.alleleSymbol + "'");
  System.out.println("background:        '" + this.background + "'");
  System.out.println("geneSymbol:        '" + this.geneSymbol + "'");
  System.out.println("graphByDateUrl:    '" + this.graphByDateUrl + "'");
  System.out.println("metadataGroup:     '" + this.metadataGroup + "'");
  System.out.println("parameterName:     '" + this.parameterName + "'");
  System.out.println("parameterStableId: '" + this.parameterStableId + "'");
  System.out.println("phenotypingCenter: '" + this.phenotypingCenter + "'");
  System.out.println("pipelineName:      '" + this.pipelineName + "'");
  System.out.println("title:             '" + this.title + "'");
            
            // Initialize database component. Use the parameterStableId to get the Parameter object.
            parameterObject = phenotypePipelineDAO.getParameterByStableId(parameterStableId);

            // Set the graph type from the parameterDAO.
            graphType = Utilities.checkType(parameterObject, parameterObject.getDatatype());
        } catch (NoSuchElementException | TimeoutException te ) {
            message = "Expected page for ID " + id + "(" + target + ") but found none. URL: " + target;
            status.addError(message);
        } catch (Exception e) {
            message = "EXCEPTION processing page: " + e.getLocalizedMessage() + "\nURL: " + target;
            status.addError(message);
        }
    }
    
//    private class AlleleParser {
//        String gene;
//        String allele;
//        String title;
//
//        public AlleleParser() {
//            try {
//                WebElement alleleElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='chart'][@graphurl]/h2[@id='section-associations']")));
//                title = alleleElement.getText().trim();
//                String alleleSup = alleleElement.findElement(By.xpath("//sup")).getText().trim();
//                gene = title.replace("Allele - ", "").replace(alleleSup, "");
//                allele = gene + "<" + alleleSup + ">";
//            } catch (Exception e) {
//                System.out.println("AlleleParser threw exception: " + e.getLocalizedMessage());
//            }
//        }
//    }
    
    private class Line1Parser {
        private String background;
        private String phenotypingCenter;
        private String pipelineName;
        
        public Line1Parser() {
            try {
                WebElement alleleElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='chart'][@graphurl]/p")));

                String line1 = alleleElement.getText();
                String[] part1 = line1.split("Phenotyping Center -");
                this.background = part1[0].replace("Background - involves:", "").replace("&nbsp;", "").trim();
                String[] part2 = part1[1].split("Pipeline - ");
                this.phenotypingCenter = part2[0].replace("Phenotyping Center -", "").replace("&nbsp;", "").trim();
                this.pipelineName = part2[1].replace("Pipeline - ", "").replace("&nbsp;", "").trim();
            } catch (Exception e) {
                System.out.println("Line1Parser threw exception: " + e.getLocalizedMessage());
            }
        }
    }
    
    private class Line2Parser {
        private String metadataGroup;
        
        public Line2Parser() {
            try {
                String line2;
                WebElement alleleElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='chart'][@graphurl]")));
                line2 = alleleElement.getText();

                int idx = line2.indexOf("Metadata Group - ") + "MetadataGroup - ".length();
                line2 = line2.substring(idx).trim();
                idx = line2.indexOf("\n");
                this.metadataGroup = line2.substring(0, idx);
            } catch (Exception e) {
                System.out.println("Line2Parser threw exception: " + e.getLocalizedMessage());
            }
        }
    }
    
    private class SvgDivParser {
        private final String parameterName;
        private final String parameterStableId;
        
        public SvgDivParser() {
            WebElement svgElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='highcharts-0']/*[local-name() = 'svg']")));
           
            // svg elements are not easily accessible by selinium using xpath or css. The incantation "*[local-name() = 'svg'"
            // successfully fetches the element. Trying to select further children using css or xpath does not work, but what
            // DOES work is to cast the returned WebElement to a RemoteWebElement, then fetch the 'text' tags. 'parameterName'
            // is the 2nd entry. 'parameterStableId' is the 3rd.
            RemoteWebElement rwe = (RemoteWebElement)svgElement;
            List<WebElement> aList = rwe.findElementsByTagName("text");
            this.parameterName = aList.get(2).getText();
            this.parameterStableId = aList.get(3).getText();
//            System.out.println("svg:\n");
//            for (WebElement we : aList) {
//                System.out.println(we.getTagName() + ": " + we.getText());
//            } 
        }
    }
}
