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

import java.util.List;
import org.mousephenotype.www.exception.NoGraphException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the gene
 * page's phenotype HTML table against which selenium web tests may interact. It
 * consists primarily of a parser and a validator. The parser serves up interesting
 * parts of the phenotype HTML table in a data grid for easy comparison and
 * validation of values. The validator runs validation rules against the parsed
 * values to compare expected results to actual results.
 */
public class GeneRow {
    private String phenotype;
    private String allele;
    private String zygosity;
    private Sex    sex;
    private String procedure;
    private String parameter;
    private String center;
    private String source;
    private Double pValue;
    private String graphHref;
     
    // These are the column indices as ordered on the gene page.
    protected final int PHENOTYPE_INDEX = 0;
    protected final int ALLELE_INDEX = 1;
    protected final int ZYGOSITY_INDEX = 2;
    protected final int SEX_INDEX = 3;
    protected final int PROCEDURE_INDEX = 4;
    protected final int CENTER_INDEX = 5;
    protected final int SOURCE_INDEX = 6;
    protected final int PVALUE_INDEX = 7;
    protected final int GRAPH_INDEX = 8;
        
    public GeneRow() {
        phenotype = "";
        allele = "";
        zygosity = "";
        sex = Sex.NONE;
        procedure = "";
        parameter = "";
        center = "";
        source = "";
        pValue = null;
        graphHref = null;
    }
    public GeneRow(List<WebElement> webElements) throws NoGraphException {
        add(webElements);
    }
        
    /**
     * Given a list of <code>WebElement</code>, extracts the strings from the 
     * web element and adds them to this <code>GeneRow</code> instance.
     * @param webElements the list of web elements containing the row of data
     * @throws NoGraphException if no graph link is found, or the link is invalid
     */
    public final void add(List<WebElement> webElements) throws NoGraphException {
        phenotype = webElements.get(PHENOTYPE_INDEX).getText();
        allele    = webElements.get(ALLELE_INDEX).getText();
        zygosity  = webElements.get(ZYGOSITY_INDEX).getText();
        WebElement sexElement = webElements.get(SEX_INDEX);
        boolean hasMale;
        try {
            hasMale = sexElement.findElement(By.cssSelector("[alt=Male]")) != null;
        } catch (Exception e) { hasMale = false;}
        boolean hasFemale;
        try {
            hasFemale = sexElement.findElement(By.cssSelector("[alt=Female]")) != null;
        } catch (Exception e) { hasFemale = false;}
        if (hasMale && hasFemale)
            sex = Sex.BOTH;
        else if (hasMale)
            sex = Sex.MALE;
        else if (hasFemale)
            sex = Sex.FEMALE;
        String[] procedureArray = webElements.get(PROCEDURE_INDEX).getText().split(" / ");
        procedure = procedureArray[0].trim();
        parameter = procedureArray[1].trim();
        center    = webElements.get(CENTER_INDEX).getText();
        source    = webElements.get(SOURCE_INDEX).getText();
        pValue    = Utils.tryParseDouble(webElements.get(PVALUE_INDEX).getText());
        try {
            graphHref = webElements.get(GRAPH_INDEX).findElement(By.cssSelector("a")).getAttribute("href");
        } catch (Exception e) {
            throw new NoGraphException("Missing or invalid graph link");
        }
    }
    
    /**
     * Validates this <code>GeneRow</code> instance
     * @return a new <code>GraphParsingStatus</code> status instance containing
     * failure counts and messages.
     */
    public final GraphParsingStatus validate() {
        return validate(new GraphParsingStatus());
    }
    
    /**
     * Validates this <code>GeneRow</code> instance, using the caller-provided
     * status instance
     * @param status caller-supplied status instance to be used
     * @return the passed-in <code>GraphParsingStatus</code> status, updated with
     * any failure counts and messages.
     */
    public final GraphParsingStatus validate(GraphParsingStatus status) {
        int errorCount = 0;
        
        // Verify that link is not missing.
        if (graphHref == null) {
            status.addFail("Missing graph link. " + toString());
            errorCount++;
        }
        if (pValue == null) {
            status.addFail("Missing or invalid pValue. " + toString());
            errorCount++;
        }
        if (errorCount == 0) {
            status.addPass("PASS: " + toString());
        }
        
        return status;
    }

    public String getPhenotype() {
        return phenotype;
    }

    public String getAllele() {
        return allele;
    }

    public String getZygosity() {
        return zygosity;
    }

    public Sex getSex() {
        return sex;
    }

    public String getProcedure() {
        return procedure;
    }

    public String getParameter() {
        return parameter;
    }

    public String getCenter() {
        return center;
    }

    public String getSource() {
        return source;
    }

    public Double getpValue() {
        return pValue;
    }

    public String getGraphHref() {
        return graphHref;
    }

    @Override
    public String toString() {
        return "Phenotype: " + phenotype
             + ", Allele: "    + allele
             + ", Zygosity: "  + zygosity
             + ", Procedure: " + procedure
             + ", Center:"     + center
             + ", Analysis: "  + source;
    }
    
    public enum Sex {
        MALE,
        FEMALE,
        BOTH,
        NONE
    }
    
    
}
