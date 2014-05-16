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
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.WebElement;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent a single line
 * of data for any graph that has summary rows. The data is accessed by using
 * the column name as a hash key.
 */
public class GraphRow {
    private int pvalueCount = 0;
    private final List<String> heading = new ArrayList();                       // The ordered column heading list.
    private final HashMap<String, String> row = new HashMap();                  // key = column name. value = data value.
    
    public static final String PVALUE_HEADING = "P Value";                      // The text of the p-value column heading.
    public static final String EFFECT_HEADING = "Effect Size";                  // The text of the effect size column heading.

    /**
     * Creates a new <code>GraphRow</code> instance initialized with the given
     * <code>summaryTableThRow</code> and <code>summaryTableTrRow</code> values.
     * @param summaryTableThRow a list of <code>WebElement</code> containing
     *        the summary table column headers, used as hash keys
     * @param summaryTableTrRow a list of <code>WebElement</code> containing
     *        the summary table column values to be used as hash values
     */
    public GraphRow(List<WebElement> summaryTableThRow, List<WebElement> summaryTableTrRow) {
        add(summaryTableThRow, summaryTableTrRow);
    }
        
    /**
     * Adds each <code>summaryTableTrRow</code> value to the internal hash
     * representing this row, using the <code>summaryTableThRow</code> value as
     * the hash key.
     * @param summaryTableThRow a list of <code>WebElement</code> containing
     *        the summary table column <code>th</code> headers, used as hash keys
     * @param summaryTableTdList a list of <code>WebElement</code> containing
     *        the summary table column <code>td</code> values used as hash values
     */
    public final void add(List<WebElement> summaryTableThRow, List<WebElement> summaryTableTdList) {
        if (summaryTableThRow.size() != summaryTableTdList.size()) {
            throw new RuntimeException("ERROR: summaryTableThRow.size() = " + summaryTableThRow.size() + ". summaryTableTrRow.size() = " + summaryTableTdList.size());
        }
        
        for (int i = 0; i < summaryTableThRow.size(); i++) {
            heading.add(summaryTableThRow.get(i).getText());
            row.put(heading.get(i), summaryTableTdList.get(i).getText());
        }
    }
    
    /**
     * Validates this <code>GraphRow</code> instance
     * @return a new <code>GraphParsingStatus</code> status instance containing
     * failure counts and messages.
     */
    public final GraphParsingStatus validate() {
        return validate(new GraphParsingStatus());
    }
    
    /**
     * Validates this <code>GraphRow</code> instance, using the caller-provided
     * status instance
     * @param status caller-supplied status instance to be used
     * @return the passed-in <code>GraphParsingStatus</code> status, updated with
     * any failure counts and messages.
     */
    public final GraphParsingStatus validate(GraphParsingStatus status) {
        
        
        String s = row.get(PVALUE_HEADING);
        Double d = Utils.tryParseDouble(s);
        
        Double pvalue = Utils.tryParseDouble(row.get(PVALUE_HEADING));
        if (pvalue != null) {
            pvalueCount++;
            Double effect = Utils.tryParseDouble(row.get(EFFECT_HEADING));
            if (effect == null) {
                status.addFail("ERROR: " + GraphRow.PVALUE_HEADING + " has no " + GraphRow.EFFECT_HEADING + ". " + row.toString());
            }
        }
        
        return status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < heading.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(heading.get(i));
            sb.append(": ");
            sb.append(row.get(heading.get(i)));
        }
        return sb.toString();
    }

    /**
     * returns the P Value count
     * @return the P Value count
     */
    public int getPvalueCount() {
        return pvalueCount;
    }

    /**
     * return the ordered heading list
     * @return the ordered heading list
     */
    public List<String> getHeading() {
        return heading;
    }

    /**
     * Return the row values in a <code>HashMap</code> keyed by heading
     * @return the row values in a <code>HashMap</code> keyed by heading
     */
    public HashMap<String, String> getRow() {
        return row;
    }
    
    
}
