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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a graph page 'catTable' HTML table.
 */
public class GraphCatTable {
    private String globalTestValue = "";
    private final List<String> pvalues = new ArrayList();
    private final List<String> effects = new ArrayList();
    private int numBodyRows;
    private final String graphUrl;
    
    /**
     * Creates a new <code>GraphGlobalTestTable</code> instance initialized with
     * the given headings
     * @param driver A <code>WebDriver</code> instance pointing to the graph page
     * with the globalTest table with thead and tbody definitions.
     */
    public GraphCatTable(WebDriver driver) {
        WebElement table = driver.findElement(By.xpath("//table[@class='globalTest]'"));
        List<WebElement> bodyRowsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowsList.isEmpty()) {
            numBodyRows = bodyRowsList.size();
        }
        
        graphUrl = driver.getCurrentUrl();                                      // Get graphUrl.
        
        List<WebElement> globalTestValueList = table.findElements(By.cssSelector("tbody tr td.globalTestValue"));
        if ( ! globalTestValueList.isEmpty()) {
            globalTestValue = globalTestValueList.get(0).getText();
        }
        List<WebElement> wePvalues = table.findElements(By.cssSelector("tbody tr td.pvalue"));
        for (WebElement we : wePvalues) {
            pvalues.add(we.getText());
        }
        List<WebElement> weEffects = table.findElements(By.cssSelector("tbody tr td.effect"));
        for (WebElement we : weEffects) {
            effects.add(we.getText());
        }
    }

    /**
     * Validates this <code>GraphRow</code> instance
     * @return a new <code>GraphParsingStatus</code> status instance containing
     * failure counts and messages.
     */
    public final PageStatus validate() {
        return validate(new PageStatus());
    }
    
    /**
     * Validates this <code>GraphRow</code> instance, using the caller-provided
     * status instance.
     * Validation Rules:
     * <ul>
     * <li>All graphs should have at least one Global Test value.</li>
     * <li>All summary lines in a graph should have an Effect value.</li>
     * <li>All summary lines in a graph should have a P Value.</li>
     * </ul>
     * 
     * @param status caller-supplied status instance to be used
     * @return the passed-in <code>PageStatus</code> status, updated with
     * any failure counts and messages.
     */
    public final PageStatus validate(PageStatus status) {
        // Verify that there is at least one global test value.
        if (globalTestValue.isEmpty()) {
            status.addError("ERROR: Expected global test value.");
        }
        
        boolean hasError = false;
        // Verify that there is a P Value and an Effect for every body row in the table.
        if ((numBodyRows != pvalues.size()) && (numBodyRows != effects.size())) {
            hasError = true;
        }
        for (String pvalue : pvalues) {
            if (pvalue.trim().isEmpty()) {
                hasError = true;
            }
        }
        for (String effect : effects) {
            if (effect.trim().isEmpty()) {
                hasError = true;
            }
        }
        if (hasError) {
            status.addError("ERROR: Expected an Effect and a P Value for every result. URL: " + graphUrl);
        }
        
        return status;
    }

    public int getNumBodyRows() {
        return numBodyRows;
    }
    
    
    public String getGlobalTestValue() {
        return globalTestValue;
    }


    public List<String> getPvalues() {
        return pvalues;
    }

    public List<String> getEffects() {
        return effects;
    }
    
    /**
     * returns true if the effects and pvalues of this table and <code>otherTable
     * </code> are the same in count and value; false otherwise
     * @param otherTable the other table to compare against
     * @return true if the effects and pvalues of this table and <code>otherTable
     * </code> are the same in count and value; false otherwise
     */
    public boolean isEqual(GraphCatTable otherTable) {
        if (effects.size() != otherTable.effects.size())
            return false;
        if (pvalues.size() != otherTable.pvalues.size())
            return false;
        for (int i = 0; i < effects.size(); i++) {
            if ( ! effects.get(i).equals(otherTable.effects.get(i)))
                return false;
        }
        for (int i = 0; i < pvalues.size(); i++) {
            if ( ! pvalues.get(i).equals(otherTable.pvalues.get(i)))
                return false;
        }
        
        return true;
    }

}