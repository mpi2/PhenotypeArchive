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

package org.mousephenotype.www.model;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;

/**
 *
 * @author mrelac
 */
public class GraphPage {
    private List<GraphRow> data;
    private final WebDriver driver;
    private final long timeoutInSeconds;
    private final PhenotypePipelineDAO phenotypePipelineDAO;
    private final String url;
    
    private ObservationType observationType;
    private String title;
    private String parameter;
    private String parameterStableId;
    private String graphByDateHref;
    private Parameter parameterObject;
    
    private final String GRAPH_BY_DATE = "Graph by date";
    
    public GraphPage(WebDriver driver, long timeoutInSeconds, PhenotypePipelineDAO phenotypePipelineDAO) {
        this.data = new ArrayList();
        this.driver = driver;
        this.timeoutInSeconds = timeoutInSeconds;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.url = driver.getCurrentUrl();
        this.observationType = null;
        this.title = "";
        this.parameter = null;
        this.parameterStableId = null;
        this.graphByDateHref = null;
        this.parameterObject = null;
    }

    public List<GraphRow> getData() {
        return data;
    }

    public long getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    public String getUrl() {
        return url;
    }

    public ObservationType getObservationType() {
        return observationType;
    }

    public String getTitle() {
        return title;
    }

    public String getParameter() {
        return parameter;
    }

    public String getParameterStableId() {
        return parameterStableId;
    }

    public String getGraphByDateHref() {
        return graphByDateHref;
    }

    /**
     * Uses the <code>WebDriver</code> driver, provided via the constructor to
     * parse the graph page (which must currently be showing).
     * @return a new <code>GraphParsingStatus</code> status instance containing
     * failure counts and messages.
     */
    public GraphParsingStatus parse() {
        return parse(new GraphParsingStatus());
    }
    
    /**
     * Uses the <code>WebDriver</code> driver, provided via the constructor to
     * parse the graph page (which must currently be showing).
     * @param status caller-supplied status instance to be used
     * @return the passed-in <code>GraphParsingStatus</code> status, updated with
     * any failure counts and messages.
     */
    public GraphParsingStatus parse(GraphParsingStatus status) {
        // Save title.
        title = driver.findElement(By.cssSelector("h2#section-associations")).getText();
        
        // Save parameter and parameterStableId.
        List<WebElement> textElementList = driver.findElements(By.cssSelector("div[id*='chart'] text.highcharts-title"));
        if (textElementList.size() > 0)
            parameter = textElementList.get(0).getText().trim();
        textElementList = driver.findElements(By.cssSelector("div[id*='chart'] text.highcharts-subtitle"));
        if (textElementList.size() > 0)
            parameterStableId = textElementList.get(0).getText().trim();
        
        // Save other link(s) of interest.
        List<WebElement> otherLinksList = driver.findElements(By.cssSelector("div.section a").linkText(GRAPH_BY_DATE));
        if ( ! otherLinksList.isEmpty())
            graphByDateHref = otherLinksList.get(0).getAttribute("href");
        
        // Determine if there is a summary table, and if so, add its data.
        //     Unidimensionals have a table with id "continuousTable", and a scatter link
        //         with a table with id "continuousTable".
        //     Categoricals have a table with id "catTable" and do not have a scatter link.
        //     Timeseries do not have a summary table.
        WebElement summaryTable = null;
        // Unidimensional (and Scatter view).
        List<WebElement> uniTableList = driver.findElements(By.cssSelector("table#continuousTable"));
        if ( ! uniTableList.isEmpty()) {
            summaryTable = uniTableList.get(0);
        }
        // Categorical
        List<WebElement> catTableList = driver.findElements(By.cssSelector("table#catTable"));
        if ( ! catTableList.isEmpty()) {
            summaryTable = catTableList.get(0);
        }
        
        // Use the parameterStableId to get the Parameter object.
        parameterObject = phenotypePipelineDAO.getParameterByStableId(parameterStableId);

        if (summaryTable != null) {
            List<WebElement> weHeaderTr = summaryTable.findElements(By.cssSelector("thead tr th"));
            if ( ! weHeaderTr.isEmpty()) {
                data = new ArrayList();
                List<WebElement> weTrList = summaryTable.findElements(By.cssSelector("tbody tr"));
                for (WebElement summaryRow : weTrList) {
                    List<WebElement> weValueTr = summaryRow.findElements(By.cssSelector("td"));
                    data.add(new GraphRow(weHeaderTr, weValueTr));
                }
            }
        }
        
        // Set the graph type from the parameterDAO.
        observationType = Utilities.checkType(parameterObject, parameterObject.getDatatype());
        
        return status;
    }

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
            status.addFail("ERROR: expected title to contain 'Allele'. Title is '" + title + "'. " + getUrl());
        }
        
        // Verify parameter name on graph matches that in the Parameter instance.
        // NOTE: Time Series graphs have the string 'MEAN ' prepended to the parameter name!!! Test accordingly.
        String expectedParameterName = parameterObject.getName().trim();
        if (observationType == ObservationType.time_series)
            expectedParameterName = "MEAN " + expectedParameterName;
        if (expectedParameterName.compareTo(parameter) != 0) {
            status.addFail("ERROR: parameter name mismatch. parameter on graph: '" + parameter + "'. from parameterDAO: " + parameterObject.getName() + ": " + getUrl());
        }
        
        // If there is a summary table (i.e. data is not null/empty), verify:
        //   1) that there is at least 1 p-value, and
        //   2) that there is an Effect Size for each p-value.
        int pvalueCount = 0;
        if (data != null) {
            for (GraphRow row : data) {
                row.validate(status);
                pvalueCount += row.getPvalueCount();
            }
        }
        if (pvalueCount == 0) {
            status.addFail("ERROR: Expected at least 1 " + GraphRow.PVALUE_HEADING + " but none were found. URL: " + getUrl());
        }
        
        // If this is a Time Series graph or a Unidimensional graph, there must be a 'Graph by date' link.
        // Invoke it, parse it, and compare the two title, parameter, and parameterStableId values. You can't
        // check the Box Plot link because there is no href - just an id named 'goBack'.
        if ((observationType == ObservationType.time_series) || (observationType == ObservationType.unidimensional)) {
            if (graphByDateHref == null) {
                status.addFail("ERROR: Expected '" + GRAPH_BY_DATE + "' link but none found. " + getUrl());
            }
            GraphPage graphByDatePage = null;
            driver.get(graphByDateHref);
            try {
                (new WebDriverWait(driver, timeoutInSeconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h2#section-associations"))).getText().contains("Allele");
                graphByDatePage = new GraphPage(driver, timeoutInSeconds, phenotypePipelineDAO);
                graphByDatePage.parse(status);
                if (title.compareTo(graphByDatePage.getTitle()) != 0) {
                    status.addFail("ERROR: Expected title '" + title + "' but found '" + graphByDatePage.getTitle() + "'.");
                }
                // NOTE: Time Series graphs have the string 'MEAN ' prepended to the parameter name!!! Test accordingly.
                // USE 'contains' because of prepended 'MEAN ' for time series graphs.
                if ( ! parameter.contains(graphByDatePage.getParameter())) {
                    status.addFail("ERROR: Expected parameter '" + parameter + "' but found '" + graphByDatePage.getParameter() + "'.");
                }
                if (title.compareTo(graphByDatePage.getTitle()) != 0) {
                    status.addFail("ERROR: Expected parameterStableId '" + parameterStableId + "' but found '" + graphByDatePage.getParameterStableId() + "'.");
                }
            } catch (Exception e) {
                status.addFail("ERROR: This graph's '" + GRAPH_BY_DATE + "' is bad. This URL: " + url);
            }

            if (observationType == ObservationType.unidimensional) {
                // If the original graph (represented by the 'this' object') is a Unidimensional graph,
                // it must have a summary table. Compare the two data objects; they should be exactly equal.
                if (graphByDatePage == null) {
                    status.addFail("ERROR: Expected a summary page but found none. " + getUrl());
                } else {
                    GraphParsingStatus tempStatus = new GraphParsingStatus();
                    if ( ! isSummaryEqual(graphByDatePage.getData(), tempStatus)) {
                        status.addFail("ERROR: this graph and its '" + GRAPH_BY_DATE + "' graph are not equal.\nThis URL: " + url + "\n'" + GRAPH_BY_DATE + "' URL: " + graphByDatePage.getUrl());
                        status.addFail(tempStatus.getFailMessages());
                    }
                }
            }
        }
        
        return status;
    }
    
    /**
     * Compares the row, column count, and data values of this data instance
     * with <code>otherData</code>. Returns true if they are all the same;
     * false otherwise.
     * @param otherData the other instance to compare against
     * @return true if they are all the same; false otherwise.
     */
    private boolean isSummaryEqual(List<GraphRow> otherData, GraphParsingStatus status) {
        if (data.size() != otherData.size())
            return false;
        
        for (int i = 0; i < data.size(); i++) {
            GraphRow row      = data.get(i);
            GraphRow otherRow = otherData.get(i);
            if ((row.getHeading().size() != otherRow.getHeading().size()) || (row.getRow().size() != otherRow.getRow().size())) {
                status.addFail("The number of heading and data columns does not match.");
                return false;
            }
            for (String cellHeading : row.getRow().keySet()) {
                String value = row.getRow().get(cellHeading);
                String otherValue = otherRow.getRow().get(cellHeading);
                if (value.compareTo(otherValue) != 0) {
                    status.addFail("Mismatched values: " + value + ", " + otherValue);
                    return false;
                }
            }
        }
        
        return true;
    }
    
}
