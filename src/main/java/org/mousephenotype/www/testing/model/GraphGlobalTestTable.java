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
    private final String graphUrl;
    private Double mpAssociationPvalue = null;
    private final List<String> sexEffectPvalues = new ArrayList();
    private final List<String> sexEffects = new ArrayList();
    
    /**
     * Creates a new <code>GraphGlobalTestTable</code> instance. Some have a
     * globalTestValue and some don't, depending on the calculation algorithm
     * that was used.
     * 
     * @param graphUrl the graph url
     * @param globalTestTableElement A <code>WebElement</code> instance pointing
     * to this section's globalTest table with thead and tbody definitions.
     */
    public GraphGlobalTestTable(String graphUrl, WebElement globalTestTableElement) {
        this.graphUrl = graphUrl;

        // Try to get the MP Association p-value. Loop around a few times ... sometimes it's not quite finished loading when this block gets hit.
        List<WebElement> mpAssociationPvalueElements = globalTestTableElement.findElements(By.xpath("./tbody/tr/td[@class='globalTestValue']"));
        if ( ! mpAssociationPvalueElements.isEmpty()) {
            mpAssociationPvalue = Utils.tryParseDouble(mpAssociationPvalueElements.get(0).getText());
        }
        
        // Get the sex components: p-value and effect
        List<WebElement> wePvalues = globalTestTableElement.findElements(By.cssSelector("tbody tr td.pvalue"));
        for (WebElement we : wePvalues) {
            sexEffectPvalues.add(we.getText());
        }
        List<WebElement> weEffects = globalTestTableElement.findElements(By.cssSelector("tbody tr td.effect"));
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
        
        if ((sexEffectPvalues.isEmpty()) || sexEffectPvalues.size() != sexEffects.size()) {
            status.addError("ERROR: pvalue/effect is empty/missing. URL: " + graphUrl);
        }
        
        for (int i = 0; i < sexEffectPvalues.size(); i++) {
            String pvalue = sexEffectPvalues.get(i);
            String effect = sexEffects.get(i);
            if ((pvalue.isEmpty()) || effect.isEmpty()) {
                status.addError("ERROR: Expected an Effect and a P Value for every result. URL: " + graphUrl);
            }
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
}