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
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a graph page 'globalTest' HTML table.
 */
public class GraphGlobalTestTable {
    private Double mpAssociationPvalue = null;
    private final List<String> sexEffectPvalues = new ArrayList();
    private final List<String> sexEffects = new ArrayList();
    public final String graphUrl;
    private boolean hasGlobalTestTable;
    
    /**
     * Creates a new <code>GraphGlobalTestTable</code> instance initialized with
     * the given headings
     * @param driver A <code>WebDriver</code> instance pointing to the graph page
     * with the globalTest table with thead and tbody definitions.
     */
    public GraphGlobalTestTable(WebDriver driver) {
        graphUrl = driver.getCurrentUrl();
        hasGlobalTestTable = false;
        
        WebElement tableElement;
        try {
            tableElement = driver.findElement(By.xpath("//table[@id='globalTest']"));
        } catch (Exception e) {
            return;
        } 
        hasGlobalTestTable = true;
        
        // Get the MP Association p-value.
        WebElement mpAssociationPvalueElement;
        try {
            mpAssociationPvalueElement = driver.findElement(By.xpath("//table[@id='globalTest']/tbody/tr/td[@class='globalTestValue']"));
            mpAssociationPvalue = Utils.tryParseDouble(mpAssociationPvalueElement.getText());
        } catch (Exception e) {
            return;
        }
        
        // Get the sex components: p-value and effect
        List<WebElement> wePvalues = tableElement.findElements(By.cssSelector("tbody tr td.pvalue"));
        for (WebElement we : wePvalues) {
            sexEffectPvalues.add(we.getText());
        }
        List<WebElement> weEffects = tableElement.findElements(By.cssSelector("tbody tr td.effect"));
        for (WebElement we : weEffects) {
            sexEffects.add(we.getText());
        }
    }

    /**
     * Validates this <code>GraphRow</code> instance
     * @return a new <code>GraphParsingStatus</code> status instance containing
     * failure counts and messages.
     */
    public final PageStatus validate() {
        PageStatus status = new PageStatus();
        
        // Verify that there is an MP Association p-value.
        if (mpAssociationPvalue == null) {
            status.addError("ERROR: Expected MP Association p-value.");
        }
        
        boolean hasError = false;
        // Verify that there is one MP association P Value and a sex Effect for every body row in the table.
        for (String pvalue : sexEffectPvalues) {
            if (pvalue.trim().isEmpty()) {
                hasError = true;
            }
        }
        for (String effect : sexEffects) {
            if (effect.trim().isEmpty()) {
                hasError = true;
            }
        }
        if (hasError) {
            status.addError("ERROR: Expected an Effect and a P Value for every result. URL: " + graphUrl);
        }
        
        return status;
    }
    
    /**
     * returns true if the sexEffects and sexEffectPvalues of this table and <code>otherTable
     * </code> are the same in count and value; false otherwise
     * @param otherTable the other table to compare against
     * @return true if the sexEffects and sexEffectPvalues of this table and <code>otherTable
     * </code> are the same in count and value; false otherwise
     */
    public boolean isEqual(GraphGlobalTestTable otherTable) {
        if (sexEffects.size() != otherTable.sexEffects.size())
            return false;
        if (sexEffectPvalues.size() != otherTable.sexEffectPvalues.size())
            return false;
        for (int i = 0; i < sexEffects.size(); i++) {
            if ( ! sexEffects.get(i).equals(otherTable.sexEffects.get(i)))
                return false;
        }
        for (int i = 0; i < sexEffectPvalues.size(); i++) {
            if ( ! sexEffectPvalues.get(i).equals(otherTable.sexEffectPvalues.get(i)))
                return false;
        }
        
        return true;
    }
    
    // GETTERS AND SETTERS
    
    
    public Double getMpAssociationPvalue() {
        return mpAssociationPvalue;
    }

    public List<String> getSexEffectPvalues() {
        return sexEffectPvalues;
    }

    public List<String> getSexEffects() {
        return sexEffects;
    }

    public String getGraphUrl() {
        return graphUrl;
    }

    public boolean hasGlobalTestTable() {
        return hasGlobalTestTable;
    }
    
}