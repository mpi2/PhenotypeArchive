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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mousephenotype.www.testing.exception.GraphTestException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.chart.ChartType;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent a single
 * graph page section containing one graph. A GraphSection contains the
 * following:
 * <ul>
 * <li>title (Required. Begins with 'Allele -')</li>
 * <li>heading (Required. E.g. components such as Background, Phenotyping
 *     Center, Pipeline, etc. Not all components are required)</li>
 * <li>summary (Optional. e.g. categorical graph sections have a table with id
 *     'catTable'. Unidimensional graph sections have a table with id
 *     'continuousTable')</li>
 * </ul>
 */
public class GraphSection {
    protected final WebElement chartElement;
    protected final WebDriver driver;
    protected final String graphUrl;
    protected final PhenotypePipelineDAO phenotypePipelineDAO;
    protected final WebDriverWait wait;
    
    private GraphCatTable catTable = null;
    private GraphContinuousTable continuousTable;
    private Map<TestUtils.DownloadType, String[][]> downloadDataSection = new HashMap();
    private GraphGlobalTestTable globalTestTable;
    private GraphHeading heading;
    private MoreStatisticsLink moreStatisticsLink;
    private ChartType chartType;
    
    /**
     * Creates a new <code>GraphPage</code> instance
     * 
     * @param driver <code>WebDriver</code> instance
     * @param wait <code>WebDriverWait</code> instance
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     * @param graphUrl the graph url
     * @param chartElement <code>WebElement</code> pointing to the HTML
     *                     div.chart element
     * @param chartType the chart type. Used to determine which validator to use.
     * 
     * @throws GraphTestException
     */
    public GraphSection(WebDriver driver, WebDriverWait wait, PhenotypePipelineDAO phenotypePipelineDAO, String graphUrl, WebElement chartElement, ChartType chartType) throws GraphTestException {
        this.driver = driver;
        this.wait = wait;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.graphUrl = graphUrl;
        this.chartElement = chartElement;
        this.chartType = chartType;

        load();
    }
    
    
    // SETTERS AND GETTERS

    
    public WebElement getChartElement() {
        return chartElement;
    }

    public String getGraphUrl() {
        return graphUrl;
    }

    public GraphCatTable getCatTable() {
        return catTable;
    }

    public GraphContinuousTable getContinuousTable() {
        return continuousTable;
    }

    public Map<TestUtils.DownloadType, String[][]> getDownloadDataSection() {
        return downloadDataSection;
    }

    public void setDownloadDataSection(Map<TestUtils.DownloadType, String[][]> downloadDataSection) {
        this.downloadDataSection = downloadDataSection;
    }

    public GraphGlobalTestTable getGlobalTestTable() {
        return globalTestTable;
    }

    public GraphHeading getHeading() {
        return heading;
    }

    public MoreStatisticsLink getMoreStatisticsLink() {
        return moreStatisticsLink;
    }
    
    
    // PRIVATE METHODS

    
    /**
     * Load the section data.
     */
    private void load() throws GraphTestException {
        
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='section']/div[@class='inner']//div[@class='highcharts-container']")));
            
            List<WebElement> elements = chartElement.findElements(By.xpath(".//table[starts-with(@id, 'catTable')]"));
            if ( ! elements.isEmpty()) {
                this.catTable = new GraphCatTable(elements.get(0));
            }
            
            elements = chartElement.findElements(By.xpath("./table[starts-with(@id, 'continuousTable')]"));
            if ( ! elements.isEmpty()) {
                this.continuousTable = new GraphContinuousTable(elements.get(0));
            }
            
            this.heading = new GraphHeading(wait, phenotypePipelineDAO, chartElement, graphUrl, chartType);
            
            elements = chartElement.findElements(By.xpath("./p/a/i[starts-with(@id, 'toggle_table_buttondivChart_')]"));
            if ( ! elements.isEmpty()) {
                moreStatisticsLink = new MoreStatisticsLink(chartElement);
            }
            
            elements = chartElement.findElements(By.xpath("./table[starts-with(@id, 'globalTest')]"));
            if ( ! elements.isEmpty()) {
                this.globalTestTable = new GraphGlobalTestTable(graphUrl, elements.get(0));
            }
            
        } catch (NoSuchElementException | TimeoutException te ) {
            System.out.println("Expected graph page url but found none. Graph URL:\n\t" + graphUrl);
            throw te;
        } catch (Exception e) {
            String message = "EXCEPTION processing page: " + e.getLocalizedMessage() + ". Graph URL:\n\t" + graphUrl;
            System.out.println(message);
            throw new GraphTestException(message, e);
        }
    }
    
    /**
     * This class encapsulates the code and data to represent the
     * 'More statistics' WebElement link.
     */
    public class MoreStatisticsLink {
        private final WebElement chartElement;
        private final String moreStatisticsIXpath   = ".//p/a/i[starts-with(@id, 'toggle_table_buttondivChart_')]"; // xpath to this section's 'more statistics' link.
                                                                                                                    // xpath to this section's 'more statistics' link contents.
        private final String moreStatisticsDivXpath = ".//div[starts-with(@id, 'toggle_tabledivChart_')] | .//div[starts-with(@id, 'toggle_timetabledivChart_')]";
    
        public MoreStatisticsLink(WebElement chartElement) {
            this.chartElement = chartElement;
        }
        
        public PageStatus validate() {
            PageStatus status = new PageStatus();
            List<WebElement> moreStatisticsList = chartElement.findElements(By.xpath(moreStatisticsIXpath));
            if (moreStatisticsList.isEmpty()) {
                status.addError("ERROR: Expected 'More statistics' link but wasn't found. URL: " + graphUrl);
            } else {
                WebElement moreStatisticsIElement = moreStatisticsList.get(0);
                WebElement moreStatisticsDivElement = chartElement.findElement(By.xpath(moreStatisticsDivXpath));
                String style = moreStatisticsDivElement.getAttribute("style");
                if ( ! style.equals("display: none;"))
                    status.addError("ERROR: Expected 'More statistics' drop-down to start collapsed.");

                moreStatisticsIElement.click();
                wait.until(ExpectedConditions.visibilityOf(moreStatisticsDivElement));
                style = moreStatisticsDivElement.getAttribute("style");
                if ( ! style.contains("display: block;"))
                    status.addError("ERROR: Expected 'More statistics' drop-down to be expanded.");

                moreStatisticsIElement.click();
                
                // Sometimes the following 'wait' doesn't wait long enough. Wrap it in a loop.
                for (int i = 0; i < 10; i++) {
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(moreStatisticsDivXpath)));
                    style = moreStatisticsDivElement.getAttribute("style");
                    if (style.contains("display: none;"))
                        break;
                    else
                        TestUtils.sleep(50);
                }
                if ( ! style.contains("display: none;"))
                    status.addError("ERROR: Expected 'More statistics' drop-down to be collapsed.");
            }
            
            return status;
        }
    }
}