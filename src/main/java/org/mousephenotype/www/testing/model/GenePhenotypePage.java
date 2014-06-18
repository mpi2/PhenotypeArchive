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

import java.util.ArrayList;
import java.util.List;
import org.mousephenotype.www.testing.exception.NoGraphException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.SexGrouping;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This abstract class encapsulates the code and data necessary to represent
 * the common parts of both a gene page (/genes/MGI:xxx) and a phenotype page
 * (/phenotypes/MP:xxxx), against which selenium web tests may interact. It
 * consists primarily of a parser and a validator. The parser serves up
 * interesting parts of the gene page in a data grid for easy comparison and
 * validation of values. The validator runs validation rules against the parsed
 * values to compare expected results to actual results.
 * 
 * Since both pages are almost identical and differ only in their column layout,
 * they can be easily tested and verified by this class.
 */
public class GenePhenotypePage {
    private List<GenePhenotypeRow> data;
    private final WebDriver driver;
    private final long timeoutInSeconds;
    private final PhenotypePipelineDAO phenotypePipelineDAO;
    private final String url;
    
    private boolean hasPhenotypeAssociations;
    private int resultCount;
    
    private final String NO_PHENO_ASSOCIATIONS = "There are currently no phenotype associations for the gene";
    private final String RESULT_TEXT_DISCARD = "Total number of results: ";
    
    public GenePhenotypePage(WebDriver driver, long timeoutInSeconds, PhenotypePipelineDAO phenotypePipelineDAO) {
        this.data = new ArrayList();
        this.driver = driver;
        this.timeoutInSeconds = timeoutInSeconds;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.hasPhenotypeAssociations = false;
        this.resultCount = 0;
        this.url = driver.getCurrentUrl();
    }

    public String getUrl() {
        return url;
    }

    /**
     * Parses the gene page (which must currently be showing).
     * @return a new <code>GraphParsingStatus</code> status instance containing
     * failure counts and messages.
     */
    public GraphParsingStatus parse() {
        return parse(new GraphParsingStatus());
    }
    
    /**
     * Uses the <code>WebDriver</code> driver, provided via the constructor to
     * parse the gene page (which must currently be showing).
     * @param status caller-supplied status instance to be used
     * @return the passed-in <code>GraphParsingStatus</code> status, updated with
     * any failure counts and messages.
     */
    public GraphParsingStatus parse(GraphParsingStatus status) {
        try {
            long shortTimeoutInSeconds = 1;
            
            // Look for the 'No Phenotypes' message. If found, 'hasPhenotypeAssociations' is false; otherwise, it is true.
            hasPhenotypeAssociations =  ! (new WebDriverWait(driver, shortTimeoutInSeconds))
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner div.alert"))).getText().contains(NO_PHENO_ASSOCIATIONS);
        } catch (Exception e) {
            hasPhenotypeAssociations = true;
        }
        
        if (hasPhenotypeAssociations) {
            // Populate resultCount from the 'Total number of results' tag on the page.
            try {
                String sResultCount = (new WebDriverWait(driver, timeoutInSeconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("p.resultCount"))).getText().replace(RESULT_TEXT_DISCARD, "");
                Integer niResultCount = Utils.tryParseInt(sResultCount);
                if (niResultCount == null) {
                    status.addFail("Failed to convert result count '" + sResultCount + "' to integer.");
                }
                this.resultCount = niResultCount;
            } catch (Exception e) {
                status.addFail("Expected to find result count but didn't.");
            }
            
            try {
                WebElement phenotypesTable = (new WebDriverWait(driver, timeoutInSeconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#phenotypes")));
                
                // Grab the headings.
                List<WebElement> headings = phenotypesTable.findElements(By.cssSelector("thead tr th"));
                    
                // Loop through all of the tr objects for this page, gathering the data.
                data = new ArrayList();
                for (WebElement phenotypesRow : phenotypesTable.findElements(By.cssSelector("tbody tr"))) {
                    List<WebElement> dataRow = phenotypesRow.findElements(By.cssSelector("td"));
                    data.add(new GenePhenotypeRow(headings, dataRow));
                }
            } catch (NoGraphException nge) {
                status.addFail("Error while trying to find and parse the phenotypes HTML table:\n" + nge.getLocalizedMessage());
            }
        }
        
        return status;
    }

    /**
     * Validates this <code>GenePhenotypePage</code> instance
     * @return a new <code>GraphParsingStatus</code> status instance containing
     * failure counts and messages.
     */
    public final GraphParsingStatus validate() {
        return validate(new GraphParsingStatus());
    }
    
    /**
     * Validates this <code>GenePhenotypePage</code> instance, using the caller-provided
     * status instance
     * @param status caller-supplied status instance to be used
     * @return the passed-in <code>GraphParsingStatus</code> status, updated with
     * any failure counts and messages.
     */
    public final GraphParsingStatus validate(GraphParsingStatus status) {
        // Verify that every data row has a valid graph link, then validate each link. Count the Sex icons along the way for later check.
        int sexIconCount = 0;
        for (GenePhenotypeRow row : data) {
            // Count the Sex icons.
            if ((row.getSexGrouping() == SexGrouping.male) || (row.getSexGrouping() == SexGrouping.female))
                sexIconCount++;
            else if (row.getSexGrouping() == SexGrouping.both)
                sexIconCount += 2;
            
            // Validate the graph link.
            status = row.validate(status);
            try {
                driver.get(row.getGraphHref());
                (new WebDriverWait(driver, timeoutInSeconds))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h2#section-associations"))).getText().contains("Allele");
                GraphPage graphPage = new GraphPage(driver, timeoutInSeconds, phenotypePipelineDAO);
                graphPage.parse(status);
                graphPage.validate(status);
            } catch (Exception e) {
                status.addFail("Couldn't load graph. " + row.toString() + "\nReason: " + e.getLocalizedMessage());
            }
        }
        
        // Verify resultCount on page against the phenotype table's count of Sex icons.
        if (sexIconCount != resultCount) {
            status.addFail("Result counts don't match. Result count = " + resultCount + " but Sex icon count = " + sexIconCount);
        }
        
        return status;
    }

    public int getResultCount() {
        return resultCount;
    }

    public List<GenePhenotypeRow> getData() {
        return data;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public boolean hasPhenotypeAssociations() {
        return hasPhenotypeAssociations;
    }
    
}
