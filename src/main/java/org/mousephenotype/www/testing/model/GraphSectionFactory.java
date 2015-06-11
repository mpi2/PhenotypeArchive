/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

import org.mousephenotype.www.testing.exception.GraphTestException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

/**
 *
 * @author mrelac
 */
public class GraphSectionFactory {
    
    /**
     * Creates a new <code>GraphPage</code> instance of the type specified
     * by <code>chartType</code>.
     * 
     * @param driver <code>WebDriver</code> instance
     * @param wait <code>WebDriverWait</code> instance
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     * @param graphUrl the graph url
     * @param chartElement The ABR <code>WebElement</code>
     * 
     * @return 
     * 
     * @throws GraphTestException a new <code>GraphPage</code> instance of the type specified
     * by <code>chartType</code>.
     */
    public static GraphSection createGraphSection(WebDriver driver, WebDriverWait wait, PhenotypePipelineDAO phenotypePipelineDAO, String graphUrl, WebElement chartElement) throws GraphTestException {
        ChartType chartType = GraphSection.getChartType(chartElement);
        switch (chartType) {
            case CATEGORICAL_STACKED_COLUMN:
                return new GraphSectionCategorical(driver, wait, phenotypePipelineDAO, graphUrl, chartElement);
                
            case PIE:
                return new GraphSectionPie(driver, wait, phenotypePipelineDAO, graphUrl, chartElement);
                
            case TIME_SERIES_LINE:
            case TIME_SERIES_LINE_BODYWEIGHT:
                return new GraphSectionTimeSeries(driver, wait, phenotypePipelineDAO, graphUrl, chartElement);
                
            case UNIDIMENSIONAL_ABR_PLOT:
                return new GraphSectionABR(driver, wait, phenotypePipelineDAO, graphUrl, chartElement);
                
            case UNIDIMENSIONAL_BOX_PLOT:
            case UNIDIMENSIONAL_SCATTER_PLOT:
                return new GraphSectionUnidimensional(driver, wait, phenotypePipelineDAO, graphUrl, chartElement);
                
            default:
                throw new GraphTestException("Unknown chart type " + chartType);
        }
    }
}
