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
import java.util.HashMap;
import java.util.List;
import org.mousephenotype.www.testing.exception.NoGraphException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import uk.ac.ebi.phenotype.pojo.SexGrouping;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent both the gene
 * and the phenotype page's phenotype HTML table against which selenium web
 * tests are run. It consists primarily of a parser and a validator. The parser
 * serves up interesting parts of the phenotype HTML table in a data grid for
 * easy comparison and validation of values. The validator runs validation
 * rules against the parsed values to compare expected results to actual results.
 */
public class GenePhenotypeRow {
    private final HashMap<String, Integer> rowIndex = new HashMap();            // key = column name. value = 0-relative column index.
    
    private String      gene;
    private String      allele;
    private String      zygosity;
    private SexGrouping sexGrouping;
    private String      phenotype;
    private String      procedure;
    private String      parameter;
    private String      center;
    private String      source;
    private Double      pValue;
    private String      graphHref;
    
    // Column headings, exactly as shown on genes or phenotypes pages.
    private final String GENE         = "Gene / Allele";                        // phenotypes page
    private final String PHENOTYPE    = "Phenotype";                            // genes page & phenotypes page
    private final String ALLELE       = "Allele";                               // genes page
    private final String ZYGOSITY     = "Zygosity";                             // genes page & phenotypes page
    private final String SEX_GROUPING = "Sex";                                  // genes page & phenotypes page
    private final String PROCEDURE    = "Procedure | Parameter";                // genes page & phenotypes page
    private final String CENTER       = "Phenotyping Center";                   // genes page & phenotypes page
    private final String SOURCE       = "Source";                               // genes page & phenotypes page
    private final String PVALUE       = "P Value";                              // genes page & phenotypes page
    private final String GRAPH_HREF   = "Graph";                                // genes page & phenotypes page
    
    public GenePhenotypeRow(String[] headings) {
        setHeadings(headings);
        
        gene = "";
        allele = "";
        zygosity = "";
        sexGrouping = SexGrouping.undefined;
        phenotype = "";
        procedure = "";
        parameter = "";
        center = "";
        source = "";
        pValue = null;
        graphHref = null;
    }

    public GenePhenotypeRow(List<WebElement> headings) {
        setHeadings(headings);
        
        gene = "";
        allele = "";
        zygosity = "";
        sexGrouping = SexGrouping.undefined;
        phenotype = "";
        procedure = "";
        parameter = "";
        center = "";
        source = "";
        pValue = null;
        graphHref = null;
    }
    public GenePhenotypeRow(String[] headings, List<WebElement> webElements) throws NoGraphException {
        setHeadings(headings);
        
        add(webElements);
    }
    public GenePhenotypeRow(List<WebElement> headings, List<WebElement> webElements) throws NoGraphException {
        setHeadings(headings);
        
        add(webElements);
    }
    
    /**
     * Given a list of <code>WebElement</code>, extracts the strings from the 
     * web element and adds them to this <code>GenePhenotypeRow</code> instance.
     * @param webElements the list of web elements containing the row of data
     * @throws NoGraphException if no graph link is found, or the link is invalid
     */
    public final void add(List<WebElement> webElements) throws NoGraphException {
        if (getGeneIndex() != null) {
            gene = webElements.get(getGeneIndex()).findElement(By.cssSelector("td a")).getText();
            allele  = webElements.get(getGeneIndex()).findElement(By.cssSelector("td span")).getText().replace("\"", "").trim();
        }
        
        if (getAlleleIndex() != null) {
            allele = webElements.get(getAlleleIndex()).getText();
        }
        
        if (getZygosityIndex() != null) {
            zygosity  = webElements.get(getZygosityIndex()).getText();
        }
        
        if (getSexGroupingIndex() != null) {
            WebElement sexElement = webElements.get(getSexGroupingIndex());
            boolean hasMale;
            try {
                hasMale = sexElement.findElement(By.cssSelector("[alt=Male]")) != null;
            } catch (Exception e) { hasMale = false;}
            boolean hasFemale;
            try {
                hasFemale = sexElement.findElement(By.cssSelector("[alt=Female]")) != null;
            } catch (Exception e) { hasFemale = false;}
            if (hasMale && hasFemale)
                sexGrouping = SexGrouping.both;
            else if (hasMale)
                sexGrouping = SexGrouping.male;
            else if (hasFemale)
                sexGrouping = SexGrouping.female;
        }
        
        if (getPhenotypeIndex() != null) {
            phenotype = webElements.get(getPhenotypeIndex()).getText();
        }
        
        if (getProcedureIndex() != null) {
            String[] procedureArray = webElements.get(getProcedureIndex()).getText().split(" | ");
            procedure = procedureArray[0].trim();
            parameter = procedureArray[1].trim();
        }
        
        if (getCenterIndex() != null) {
            center    = webElements.get(getCenterIndex()).getText();
        }
        
        if (getSourceIndex() != null) {
            source    = webElements.get(getSourceIndex()).getText();
        }
        
        if (getPvalueIndex() != null) {
            pValue    = Utils.tryParseDouble(webElements.get(getPvalueIndex()).getText());
        }
        
        if (getGraphHrefIndex() != null) {
            try {
                graphHref = webElements.get(getGraphHrefIndex()).findElement(By.cssSelector("a")).getAttribute("href");
            } catch (Exception e) {
                throw new NoGraphException("Missing or invalid graph link");
            }
        }
    }
    
    /**
     * Validates this <code>GenePhenotypeRow</code> instance
     * @return a new <code>GraphParsingStatus</code> status instance containing
     * failure counts and messages.
     */
    public final GraphParsingStatus validate() {
        return validate(new GraphParsingStatus());
    }
    
    /**
     * Validates this <code>GenePhenotypeRow</code> instance, using the caller-provided
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
            status.addFail("Missing or invalid P Value. Check P Value column heading is '" + PVALUE + "'. " + toString());
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

    public SexGrouping getSexGrouping() {
        return sexGrouping;
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
        if (gene != null) {
            return "Gene: "          + gene
                 + ", Allele: "      + allele
                 + ", Zygosity: "    + zygosity
                 + ", SexGrouping: " + sexGrouping
                 + ", Phenotype"     + phenotype
                 + ", Procedure: "   + procedure
                 + ", Parameter"     + parameter
                 + ", Center:"       + center
                 + ", Source: "      + source
                 + ", P Value "      + pValue
                 + ", Graph URL "    + graphHref;
        } else {
            return "Phenotype: "     + phenotype
                 + ", Allele: "      + allele
                 + ", Zygosity: "    + zygosity
                 + ", SexGrouping: " + sexGrouping
                 + ", Procedure: "   + procedure
                 + ", Parameter"     + parameter
                 + ", Center:"       + center
                 + ", Source: "      + source
                 + ", P Value "      + pValue
                 + ", Graph URL "    + graphHref;
        }
    }
    
    
    // PRIVATE METHODS
    
    
    private void setHeadings(List<WebElement> headings) {
        ArrayList<String> headingList = new ArrayList();
        for (WebElement element : headings) {
            headingList.add(element.getText());
        }
        setHeadings(headingList.toArray(new String[headingList.size()]));
    }
    
    private void setHeadings(String[] headings) {
        for (int i = 0; i < headings.length; i++) {
            rowIndex.put(headings[i], i);
        }
    }
        
    private Integer getGeneIndex() {
        return (rowIndex.containsKey(GENE) ? rowIndex.get(GENE) : null);
    }
    
    private Integer getAlleleIndex() {
        return (rowIndex.containsKey(ALLELE) ? rowIndex.get(ALLELE) : null);
    }
    
    private Integer getZygosityIndex() {
        return (rowIndex.containsKey(ZYGOSITY) ? rowIndex.get(ZYGOSITY) : null);
    }
    
    private Integer getSexGroupingIndex() {
        return (rowIndex.containsKey(SEX_GROUPING) ? rowIndex.get(SEX_GROUPING) : null);
    }
    
    private Integer getPhenotypeIndex() {
        return (rowIndex.containsKey(PHENOTYPE) ? rowIndex.get(PHENOTYPE) : null);
    }
    
    private Integer getProcedureIndex() {
        return (rowIndex.containsKey(PROCEDURE) ? rowIndex.get(PROCEDURE) : null);
    }
    
    private Integer getCenterIndex() {
        return (rowIndex.containsKey(CENTER) ? rowIndex.get(CENTER) : null);
    }
    
    private Integer getSourceIndex() {
        return (rowIndex.containsKey(SOURCE) ? rowIndex.get(SOURCE) : null);
    }
    
    private Integer getPvalueIndex() {
        return (rowIndex.containsKey(PVALUE) ? rowIndex.get(PVALUE) : null);
    }
    
    private Integer getGraphHrefIndex() {
        return (rowIndex.containsKey(GRAPH_HREF) ? rowIndex.get(GRAPH_HREF) : null);
    }

}

