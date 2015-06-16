/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.www.testing.model;

import java.util.List;
import org.mousephenotype.www.testing.exception.GraphTestException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.chart.ChartType;
import static uk.ac.ebi.phenotype.chart.ChartType.CATEGORICAL_STACKED_COLUMN;
import static uk.ac.ebi.phenotype.chart.ChartType.PIE;
import static uk.ac.ebi.phenotype.chart.ChartType.TIME_SERIES_LINE;
import static uk.ac.ebi.phenotype.chart.ChartType.TIME_SERIES_LINE_BODYWEIGHT;
import static uk.ac.ebi.phenotype.chart.ChartType.UNIDIMENSIONAL_ABR_PLOT;
import static uk.ac.ebi.phenotype.chart.ChartType.UNIDIMENSIONAL_BOX_PLOT;
import static uk.ac.ebi.phenotype.chart.ChartType.UNIDIMENSIONAL_SCATTER_PLOT;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent a single
 * graph page section's heading containing the following:
 * <ul>
 * <li>title (Required. Begins with 'Allele -')</li>
 * <li>heading (Required. E.g. components such as Background, Phenotyping
 *     Center, Pipeline, etc. Not all components are required)</li>
 * </ul>
 */
public class GraphHeading {
    protected final WebElement chartElement;
    protected final PhenotypePipelineDAO phenotypePipelineDAO;
    protected final WebDriverWait wait;

    private final Utilities impressUtils = new Utilities();

    // Heading variables
    protected String title = "";
    protected String alleleSymbol = "";
    protected String geneticBackground = "";
    protected String geneSymbol = "";
    protected String metadataGroup = "";
    protected String parameterName = "";
    protected String parameterStableId = "";
    protected String phenotypingCenter = "";
    protected String pipelineName = "";
    protected String procedureName = "";
    protected String graphUrl = "";
    protected String mutantKey = "";
    protected String controlKey = "";
    
    protected WebElement pipelineLinkElement;
    protected WebElement sopLinkElement;
    protected ChartType chartType;
    
    // Database parameter variables
    protected ObservationType observationType;
    protected Parameter parameterObject;

    /**
     * Creates a new <code>GraphPage</code> instance
     * 
     * @param wait <code>WebDriverWait</code> instance
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     * @param chartElement <code>WebElement</code> pointing to the HTML
     *                     div.chart element
     * @param graphUrl the url of the graph page (used for error/warning reporting)
     * @param chartType the chart type. Used to determine which validator to use.
     * 
     * @throws GraphTestException
     */
    public GraphHeading(WebDriverWait wait, PhenotypePipelineDAO phenotypePipelineDAO, WebElement chartElement, String graphUrl, ChartType chartType) throws GraphTestException {
        this.wait = wait;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.chartElement = chartElement;
        this.graphUrl = graphUrl;
        this.chartType = chartType;
        
        parse(chartType);
        setKeys();
    }
    
    /**
     * Validates the elements common to all graph headings.
     * 
     * @return status
     * 
     * @throws GraphTestException
     */
    public PageStatus validate() throws GraphTestException {
        PageStatus status = new PageStatus();
        boolean validatePipeline;
        boolean validateSop;
        
        switch (chartType) {
            case CATEGORICAL_STACKED_COLUMN:
                validatePipeline = true;
                validateSop = true;
                break;
                
            case PIE:
                validatePipeline = true;
                validateSop = false;
                break;
                
            case TIME_SERIES_LINE:
            case TIME_SERIES_LINE_BODYWEIGHT:
                validatePipeline = true;
                validateSop = false;
                break;
                
            case UNIDIMENSIONAL_ABR_PLOT:
                validatePipeline = true;
                validateSop = true;
                break;
                
            case UNIDIMENSIONAL_BOX_PLOT:
            case UNIDIMENSIONAL_SCATTER_PLOT:
                validatePipeline = true;
                validateSop = true;
                break;
                
            default:
                throw new GraphTestException("Unknown chart type " + chartType);
        }
        
        if (validatePipeline) {
            // 1. Check pipeline link is not null. Error if it is.
            // 2. Check it contains '/impress/'.
            if (pipelineLinkElement == null) {
                status.addError("ERROR: pipeline link element is missing.");
            } else {
                String url = pipelineLinkElement.getAttribute("href");
                if ( ! url.contains("/impress/")) {
                    status.addError("ERROR: expected pipeline link URL to contain 'impress'. Graph URL: " + graphUrl + "\n\tpipeline link URL: " + url);
                }
            }
        }
        
        if (validateSop) {
            // 1. Check if sopLink is not null. Error if it is.
            // 2. Check it contains '/impress/'.
            if (sopLinkElement == null) {
                    status.addError("ERROR: sop link element is missing. Graph URL: " + graphUrl);
            } else {
                String url = sopLinkElement.getAttribute("href");
                if ( ! url.contains("/impress/")) {
                    status.addError("ERROR: expected sop link URL to contain 'impress'. Graph URL: " + graphUrl + "\n\tsop URL: " + url);
                }
            }
        }
        
        return status;
    }
    
    
    // GETTERS AND SETTERS

    public WebElement getChartElement() {
        return chartElement;
    }

    public PhenotypePipelineDAO getPhenotypePipelineDAO() {
        return phenotypePipelineDAO;
    }

    public WebDriverWait getWait() {
        return wait;
    }

    public Utilities getImpressUtils() {
        return impressUtils;
    }

    public String getTitle() {
        return title;
    }

    public String getAlleleSymbol() {
        return alleleSymbol;
    }

    public String getGeneticBackground() {
        return geneticBackground;
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

    public WebElement getPipelineLinkElement() {
        return pipelineLinkElement;
    }

    public WebElement getSopLinkElement() {
        return sopLinkElement;
    }

    public ObservationType getObservationType() {
        return observationType;
    }

    public Parameter getParameterObject() {
        return parameterObject;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public String getGraphUrl() {
        return graphUrl;
    }

    public String getMutantKey() {
        return mutantKey;
    }

    public String getControlKey() {
        return controlKey;
    }

    public ChartType getChartType() {
        return chartType;
    }

    
    // PRIVATE METHODS

    
    /**
     * Parse the heading.
     */
    private void parse(ChartType chartType) throws GraphTestException {
        // Wait for all charts to load.
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='section']/div[@class='inner']//div[@class='highcharts-container']")));
        
        List<WebElement> titleElements = chartElement.findElements(By.xpath("./h2[@id='section-associations']"));
        if (titleElements.isEmpty()) {
            throw new RuntimeException("GraphHeading.parse(): Wait to get chart timed out!");
        }
        WebElement titleElement = titleElements.get(0);
        this.title = titleElement.getText().trim();
        String rawAllele = title.replace("Allele - ", "");
        List<WebElement> supElements = titleElement.findElements(By.xpath("./sup"));
        String sup = (supElements.isEmpty() ? "" : titleElement.findElement(By.xpath("./sup")).getText());
        AlleleParser alleleParser = new AlleleParser(rawAllele, sup);
        Line1Parser line1Parser = new Line1Parser();
        Line2Parser line2Parser = new Line2Parser();
        ParameterParser parameterParser = new ParameterParser(chartType);

        this.alleleSymbol = alleleParser.toString();
        this.geneticBackground = line1Parser.background;
        this.geneSymbol = alleleParser.gene;
        this.metadataGroup = line2Parser.metadataGroup;
        this.parameterName = parameterParser.getParameterName();
        this.parameterStableId = parameterParser.getParameterStableId();
        this.phenotypingCenter = line1Parser.phenotypingCenter;
        this.pipelineName = line1Parser.pipelineName;
        this.pipelineLinkElement = line1Parser.pipelineLinkElement;
        this.parameterObject = parameterParser.getParameterObject();
        this.sopLinkElement = parameterParser.getSopLinkElement();

        // Set the graph type from the parameterDAO.
        if (parameterObject != null) {
            observationType = impressUtils.checkType(parameterObject);
        }
    }
    
    /**
     * Set the mutant and control keys based on chart type.
     * 
     * @throws GraphTestException
     */
    private void setKeys() throws GraphTestException {
                
        switch (chartType) {
            case CATEGORICAL_STACKED_COLUMN:
                mutantKey = TestUtils.makeKey(alleleSymbol,      geneticBackground, geneSymbol,
                                              metadataGroup,     parameterName,     parameterStableId,
                                              phenotypingCenter, pipelineName);
                
                controlKey = TestUtils.makeKey(                   geneticBackground, 
                                               metadataGroup,     parameterName,     parameterStableId,
                                               phenotypingCenter);
                break;
                
            case PIE:
                break;
                
            case TIME_SERIES_LINE:
            case TIME_SERIES_LINE_BODYWEIGHT:
                mutantKey = TestUtils.makeKey(alleleSymbol,      geneticBackground, geneSymbol,
                                              metadataGroup,     
                                              phenotypingCenter, pipelineName);
                
                controlKey = TestUtils.makeKey(                   geneticBackground, 
                                               metadataGroup,     
                                               phenotypingCenter);
                break;
                
            case UNIDIMENSIONAL_ABR_PLOT:
                mutantKey = TestUtils.makeKey(alleleSymbol,      geneticBackground, geneSymbol,
                                              metadataGroup,
                                              phenotypingCenter, pipelineName,
                                              procedureName);
                
                controlKey = TestUtils.makeKey(                   geneticBackground,
                                               metadataGroup,
                                               phenotypingCenter,
                                               procedureName);
                break;
                
            case UNIDIMENSIONAL_BOX_PLOT:
            case UNIDIMENSIONAL_SCATTER_PLOT:
                mutantKey = TestUtils.makeKey(alleleSymbol,      geneticBackground, geneSymbol,
                                              metadataGroup,     parameterName,     parameterStableId,
                                              phenotypingCenter, pipelineName);
                
                controlKey = TestUtils.makeKey(                   geneticBackground, 
                                               metadataGroup,     parameterName,     parameterStableId,
                                               phenotypingCenter);
                break;
                
            default:
                throw new GraphTestException("Unknown chart type " + chartType);
        }
    }
    
    
    // PRIVATE CLASSES
    
    
    protected class Line1Parser {
        public final String background;
        public final String phenotypingCenter;
        public final String pipelineName;
        public final WebElement pipelineLinkElement;
        
        public Line1Parser() {
            try {
                WebElement line1Element = chartElement.findElement(By.xpath("./p"));

                String line1 = line1Element.getText();
                String[] part1 = line1.split("Phenotyping Center - ");
                
                // If <sup> tag found, wrap it in "<  >".
                List<WebElement> elements = line1Element.findElements(By.xpath(".//sup"));
                if ( ! elements.isEmpty()) {
                    String sup = elements.get(0).getText();
                    background = part1[0].replace("Background -", "").replace("&nbsp;", "").trim().replace(sup, "<" + sup + ">");
                } else {
                    background = part1[0].replace("Background -", "").replace("&nbsp;", "").trim();
                }
                String[] part2 = part1[1].split("Pipeline -");
                phenotypingCenter = part2[0].replace("Phenotyping Center -", "").replace("&nbsp;", "").trim();
                pipelineName = part2[1].replace("Pipeline -", "").replace("&nbsp;", "").trim();
                elements = line1Element.findElements(By.xpath(".//a"));
                if (elements.isEmpty()) {
                    pipelineLinkElement = null;
                } else {
                    pipelineLinkElement = elements.get(0);
                }
            } catch (Exception e) {
                System.out.println("Line1Parser threw exception: " + e.getLocalizedMessage());
                throw e;
            }
        }

        @Override
        public String toString() {
            return "Line1Parser{" + "background=" + background + ", phenotypingCenter=" + phenotypingCenter + ", pipelineName=" + pipelineName + ", pipelineLinkElement=" + pipelineLinkElement + '}';
        }
    }
    
    protected class Line2Parser {
        public String metadataGroup = "";
        
        public Line2Parser() {
            final String match = "Metadata Group -";
            try {
                String[] line2A;
                
                line2A = chartElement.getText().split("\n");
                for (String line2 : line2A) {
                    if (line2.startsWith(match)) {
                        metadataGroup = line2.replace(match, "").trim();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Line2Parser threw exception: " + e.getLocalizedMessage());
                throw e;
            }
        }

        @Override
        public String toString() {
            return "Line2Parser{" + "metadataGroup=" + metadataGroup + '}';
        }
    }
    
    protected class ParameterParser {
        private String parameterName = "";
        private Parameter parameterObject = null;
        private String parameterStableId = "";
        private WebElement sopLinkElement = null;
        
        public ParameterParser(ChartType chartType) throws GraphTestException {
            List<WebElement> elements;
            WebElement element;
            
            switch (chartType) {
                case CATEGORICAL_STACKED_COLUMN:
                case UNIDIMENSIONAL_BOX_PLOT:
                case UNIDIMENSIONAL_SCATTER_PLOT:
                    elements = chartElement.findElements(By.xpath(".//span[@data-parameterstableid]"));
                    element = elements.get(0);
                    parameterName = element.getText();
                    parameterStableId = element.getAttribute("data-parameterstableid");
                    parameterObject = phenotypePipelineDAO.getParameterByStableId(parameterStableId);
                    elements = chartElement.findElements(By.xpath(".//span[@class='highcharts-subtitle']//a | ./p[@class='chartSubtitle']//a"));
                    if (elements.isEmpty()) {
                        sopLinkElement = null;
                    } else {
                        sopLinkElement = elements.get(0);
                    }
                    break;
                    
                case PIE:
                    // Nothing to do.
                    break;
                    
                case UNIDIMENSIONAL_ABR_PLOT:
                    elements = chartElement.findElements(By.xpath(".//span[@class='highcharts-subtitle']//a"));
                    if (elements.isEmpty()) {
                        sopLinkElement = null;
                    } else {
                        sopLinkElement = elements.get(0);
                        procedureName = sopLinkElement.getText();
                    }
                    break;
                    
                case TIME_SERIES_LINE:
                case TIME_SERIES_LINE_BODYWEIGHT:
                    elements = chartElement.findElements(By.xpath(".//*[@class='highcharts-subtitle']"));
                    if ( ! elements.isEmpty()) {
                        parameterStableId = elements.get(0).getText();
                        parameterObject = phenotypePipelineDAO.getParameterByStableId(parameterStableId);
                    }
                    break;
                    
                default:
                    throw new GraphTestException("Unknown chart type " + chartType);
            }
        }
            

        @Override
        public String toString() {
            return "ParameterParser{" + "parameterName=" + parameterName + ", parameterObject=" + parameterObject + ", parameterStableId=" + parameterStableId + ", sopLinkElement=" + sopLinkElement + '}';
        }

        public String getParameterName() {
            return parameterName;
        }

        public Parameter getParameterObject() {
            return parameterObject;
        }

        public String getParameterStableId() {
            return parameterStableId;
        }

        public WebElement getSopLinkElement() {
            return sopLinkElement;
        }
        
        
    }
    
    private class SvgDivParser {
        
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
            
            // 07-Aug-2014 (mrelac) Graphs have been refactored so that the parameterStableId and parameter name no longer come from the svg;
            //                      They come from a span following the svg. I'm leaving this svg code here, commented out, in case we need to
            //                      parse the svg later on and need a reminder of 'How-To'.
//            parameterName = aList.get(2).getText();                             // Try as offset 2 first.
//            parameterStableId = aList.get(3).getText();                         // Try as offset 3 first.
//            parameterObject = phenotypePipelineDAO.getParameterByStableId(parameterStableId);
//            if (parameterObject == null) {
//                parameterName = aList.get(1).getText();                         // Try as offset 1.
//                parameterStableId = aList.get(2).getText();                     // Try as offset 2.
//                parameterObject = phenotypePipelineDAO.getParameterByStableId(parameterStableId);
//            }

            // For debugging:
//            System.out.println("svg:\n");
//            for (WebElement we : aList) {
//                System.out.println(we.getTagName() + ": " + we.getText());
//            } 
        }
    }
}
