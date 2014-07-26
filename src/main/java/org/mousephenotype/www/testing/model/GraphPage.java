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
 * This abstract class encapsulates the code and data necessary to represent the important,
 * common components of a graph page, such as: allele, background, phenotyping center,
 * pipeline name, metadata group, procedure name and parameter name, and
 * parameterStableId.
 * 
 * Currently there are no collections or links shared by all
 * graphs; for those, consult subclasses such as
 * GraphPageCategorical and GraphPageUnidimensional.
 */
public class GraphPage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final String target;
    protected final String id;
    protected final PhenotypePipelineDAO phenotypePipelineDAO;
    protected final String baseUrl;
    
    // Page variables common to all graphs
    protected String title;
    protected String alleleSymbol;
    protected String background;
    protected String geneSymbol;
    protected String metadataGroup;
    protected String parameterName;
    protected String parameterStableId;
    protected String phenotypingCenter;
    protected String pipelineName;
    
    // Database parameter variables
    protected ObservationType graphType;
    protected Parameter parameterObject;
    
    /**
     * Creates a new <code>GraphPage</code> instance
     * 
     * @param driver <code>WebDriver</code> instance
     * @param wait <code>WebDriverWait</code> instance
     * @param target graph page target to load
     * @param id id of the associated gene or phenotype
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     */
    public GraphPage(WebDriver driver, WebDriverWait wait, String target, String id, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
////////////target = "http://ves-ebi-d0:8080/mi/impc/dev/phenotype-archive/charts?accession=MGI:2150037&zygosity=homozygote&allele_accession=MGI:4842891&parameter_stable_id=ESLIM_006_001_027&pipeline_stable_id=ESLIM_001&phenotyping_center=WTSI";
////////////target = "https://dev.mousephenotype.org/data/charts?accession=MGI:2444584&zygosity=homozygote&allele_accession=MGI:4362924&parameter_stable_id=ESLIM_015_001_018&pipeline_stable_id=ESLIM_002&phenotyping_center=HMGU";
        this.target = target;
        this.id = id;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.baseUrl = baseUrl;
        loadScalar();
    }

    /**
     * 
     * @return a new instance of <code>GraphPageCategorical</code> created from the
     * scalars on this <code>GraphPage</code>.
     * @throws Exception if this page is not of <code>ObservationType</code>
     * <i>categorical</i>
     */
    public GraphPageCategorical createGraphPageCategorical() throws Exception {
        return new GraphPageCategorical(driver, wait, target, id, phenotypePipelineDAO, baseUrl, this);
    }
    
    /**
     * 
     * @return a new instance of <code>GraphPageUnidimensional</code> created from the
     * scalars on this <code>GraphPage</code>.
     * @throws Exception if this page is not of <code>ObservationType</code>
     * <i>unidimensional</i>
     */
    public GraphPageUnidimensional createGraphPageUnidimensional() throws Exception {
        return new GraphPageUnidimensional(driver, wait, target, id, phenotypePipelineDAO, baseUrl, this);
    }
    
    /**
     * Validates the basic, scalar components of the page. Does not validate page
     * collections.
     * 
     * @return status
     */
    public PageStatus validateScalar() {
        PageStatus status = new PageStatus();
        // Verify title contains 'Allele'.
        if ( ! title.contains("Allele")) {
            status.addError("ERROR: expected title to contain 'Allele'. Title is '" + title + "'. URL: " + target);
        }
        
        // Verify parameter name on graph matches that in the Parameter instance.
        // NOTE: Time Series graphs have the string 'MEAN ' prepended to the parameter name!!! Test accordingly.
        String expectedParameterName = parameterObject.getName().trim();
        if (graphType == ObservationType.time_series)
            expectedParameterName = "MEAN " + expectedParameterName;
        if (expectedParameterName.compareTo(parameterName) != 0) {
            status.addError("ERROR: parameter name mismatch. parameter on graph: '" + parameterName + "'. from parameterDAO: " + parameterObject.getName() + ": " + target);
        }
        
        return status;
    }
    
    
    // PROTECTED METHODS
    
    /**
     * This protected method validates the scalar parts of the download - those
     * parts that appear in the graph heading, such as <i>allele, gene,
     * background, phenotyping center</i> etc., that are common to calling
     * subtypes.
     * 
     * @param data the download stream data
     * @param graphMap the graph column index map defining the column offsets in
     * <code>data</code>
     * @return validation status
     */
    protected PageStatus validateScalarDownload(String[][] data, DownloadGraphMap graphMap) {
        PageStatus status = new PageStatus();
        
        // Test graph page parameters against first [non-heading] download stream row.
        if (data.length < 2) {
            status.addError(("ERROR: Expected at least one row of data."));
        } else {
            String cellValue = data[1][graphMap.getColIndexAlleleSymbol()];
            if (getAlleleSymbol().compareTo(cellValue) != 0) {
                status.addError("ERROR: mismatch: page alleleSymbol: '" + getAlleleSymbol() + "'. Download alleleSymbol: '" + cellValue + "'");
            }
            cellValue = data[1][graphMap.getColIndexBackground()];
            if (getBackground().compareTo(cellValue) != 0) {
                status.addError("ERROR: mismatch: page background: '" + getBackground() + "'. Download background: '" + cellValue + "'");
            }
            cellValue = data[1][graphMap.getColIndexGeneSymbol()];
            if (getGeneSymbol().compareTo(cellValue) != 0) {
                status.addError("ERROR: mismatch: page geneSymbol: '" + getGeneSymbol() + "'. Download geneSymbol: '" + cellValue + "'");
            }
            cellValue = data[1][graphMap.getColIndexMetadataGroup()];
            if (getMetadataGroup().compareTo(cellValue) != 0) {
                status.addError("ERROR: mismatch: page metadataGroup: '" + getMetadataGroup() + "'. Download metadataGroup: '" + cellValue + "'");
            }
            cellValue = data[1][graphMap.getColIndexParameterName()];
            if (getParameterName().compareTo(cellValue) != 0) {
                status.addError("ERROR: mismatch: page parameterName: '" + getParameterName() + "'. Download parameterName: '" + cellValue + "'");
            }
            cellValue = data[1][graphMap.getColIndexParameterStableId()];
            if (getParameterStableId().compareTo(cellValue) != 0) {
                status.addError("ERROR: mismatch: page parameterStableId: '" + getParameterStableId() + "'. Download parameterStableId: '" + cellValue + "'");
            }
            cellValue = data[1][graphMap.getColIndexPhenotypingCenter()];
            if (getPhenotypingCenter().compareTo(cellValue) != 0) {
                status.addError("ERROR: mismatch: page phenotypingCenter: '" + getPhenotypingCenter() + "'. Download phenotypingCenter: '" + cellValue + "'");
            }
            cellValue = data[1][graphMap.getColIndexPipelineName()];
            if (getPipelineName().compareTo(cellValue) != 0) {
                status.addError("ERROR: mismatch: page pipelineName: '" + getPipelineName() + "'. Download pipelineName: '" + cellValue + "'");
            }
        }
        
        return status;
    }
    
    
    // GETTERS AND SETTERS
    
    
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

    public ObservationType getGraphType() {
        return graphType;
    }

    public Parameter getParameterObject() {
        return parameterObject;
    }

    public String getTitle() {
        return title;
    }

    
    // PRIVATE METHODS

    
    /**
     * Load the page and its scalar variables (not collections).
     */
    protected final void loadScalar() {
        try {
            // Wait for page to loadScalar.
            driver.get(target);
            
            // Initialize page component variables read from page.
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@data-exporturl]")));
            
            WebElement titleElement = null;
            // Sometimes the chart isn't loaded when the 'wait()' ends, so try a few times.
            for (int i = 0; i < 10; i++) {
                try {
                    titleElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@id='section-associations']")));
                    break;
                } catch (Exception e) {
                    System.out.println("Waiting " + (i + 1 * 10) + " milliseconds.");
                    TestUtils.sleep(10);
                }
                if (titleElement != null)
                    break;
            }
            
            if (titleElement == null) {
                throw new RuntimeException("GraphPage.loadScalar(): Wait to get chart timed out!");
            }
            this.title = titleElement.getText().trim();
            String rawAllele = title.replace("Allele - ", "");
            String sup = titleElement.findElement(By.xpath("//sup")).getText();
            AlleleParser alleleParser = new AlleleParser(rawAllele, sup);
            Line1Parser line1Parser = new Line1Parser();
            Line2Parser line2Parser = new Line2Parser();
            SvgDivParser svgDigParser = new SvgDivParser();
            
            this.alleleSymbol = alleleParser.toString();
            this.background = line1Parser.background;
            this.geneSymbol = alleleParser.gene;
            this.metadataGroup = line2Parser.metadataGroup;
            this.parameterName = svgDigParser.parameterName;
            this.parameterStableId = svgDigParser.parameterStableId;
            this.phenotypingCenter = line1Parser.phenotypingCenter;
            this.pipelineName = line1Parser.pipelineName;
            this.parameterObject = svgDigParser.parameterObject;
            
            // Set the graph type from the parameterDAO.
            graphType = Utilities.checkType(parameterObject, parameterObject.getDatatype());
            
//  System.out.println("title:             '" + this.title + "'");
//  System.out.println("alleleSymbol:      '" + this.alleleSymbol + "'");
//  System.out.println("background:        '" + this.background + "'");
//  System.out.println("geneSymbol:        '" + this.geneSymbol + "'");
//  System.out.println("metadataGroup:     '" + this.metadataGroup + "'");
//  System.out.println("parameterName:     '" + this.parameterName + "'");
//  System.out.println("parameterStableId: '" + this.parameterStableId + "'");
//  System.out.println("phenotypingCenter: '" + this.phenotypingCenter + "'");
//  System.out.println("pipelineName:      '" + this.pipelineName + "'");
//  System.out.println("graphType:         '" + this.graphType + "'");
            
        } catch (NoSuchElementException | TimeoutException te ) {
            System.out.println("Expected page for ID " + id + "(" + target + ") but found none. URL: " + target);
            throw te;
        } catch (Exception e) {
            System.out.println("EXCEPTION processing page: " + e.getLocalizedMessage() + "\nURL: " + target);
            throw e;
        }
    }
    
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
                throw e;
            }
        }
    }
    
    private class Line2Parser {
        private String metadataGroup;
        
        public Line2Parser() {
            final String match = "Metadata Group -";
            try {
                String[] line2A;
                WebElement alleleElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='chart'][@graphurl]")));
                line2A = alleleElement.getText().split("\n");
                for (String line2 : line2A) {
                    if (line2.startsWith(match)) {
                        this.metadataGroup = line2.replace(match, "").trim();
                    }
                }
            } catch (Exception e) {
                System.out.println("Line2Parser threw exception: " + e.getLocalizedMessage());
                throw e;
            }
        }
    }
    
    private class SvgDivParser {
        private String parameterName;
        private Parameter parameterObject;
        private String parameterStableId;
        
        public SvgDivParser() {
            WebElement svgElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='highcharts-0']/*[local-name() = 'svg']")));

            // svg elements are not easily accessible by selinium using xpath or css. The incantation "*[local-name() = 'svg'"
            // successfully fetches the element. Trying to select further children using css or xpath does not work. What DOES
            // work is to cast the returned WebElement to a RemoteWebElement, then fetch the 'text' tags. They are just an
            // array of strings, and the component offsets are not always the same; e.g. most of the time 'parameterName' is
            // entry[2], but when metadataGroup is missing, it is entry[1]!! To get a reliable parameterStableId we might have
            // to try the call to get the parameterObject multiple times, with different offsets
            // 
            // Most of the time 'parameterName'is element[2] and 'parameterStableId' is element[3].
            // Sometimes,       'parameterName is element[1] and 'parameterStableId' is element[2].
            RemoteWebElement rwe = (RemoteWebElement)svgElement;
            List<WebElement> aList = rwe.findElementsByTagName("text");
            
            parameterName = aList.get(2).getText();                             // Try as offset 2 first.
            parameterStableId = aList.get(3).getText();                         // Try as offset 3 first.
            parameterObject = phenotypePipelineDAO.getParameterByStableId(parameterStableId);
            if (parameterObject == null) {
                parameterName = aList.get(1).getText();                         // Try as offset 1.
                parameterStableId = aList.get(2).getText();                     // Try as offset 2.
                parameterObject = phenotypePipelineDAO.getParameterByStableId(parameterStableId);
            }

            // For debugging:
//            System.out.println("svg:\n");
//            for (WebElement we : aList) {
//                System.out.println(we.getTagName() + ": " + we.getText());
//            } 
        }
    }
}
