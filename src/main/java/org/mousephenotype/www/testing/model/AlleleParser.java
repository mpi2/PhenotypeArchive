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

/**
 *
 * @author mrelac
 */
public class AlleleParser {
    String gene;
    String alleleSub;

    /**
     * Given a string that looks like an alleleSub (e.g. Sirt2&lt;tm1a(EUCOMM)Wtsi&gt;),
 parses the string into the gene part (Sirt2) and the alleleSub part without
 the gene part and without the &lt; &gt; entities.
     * (tm1a(EUCOMM)Wtsi)
     * 
     * @param geneAllele The alleleSub string to parse
     */
    public AlleleParser(String geneAllele) {
        String[] sA = geneAllele.split("<");
        if ((sA == null) || (sA.length < 2)) {
            gene = "";
            alleleSub = "";
        } else {
            gene = sA[0];
            alleleSub = sA[1].replace(">", "");
        }
    }
    
    /**
     * Given a string that looks like an alleleSub, but without the "&lt;" and 
     * "&gt;" HTML entities, and a &lt;sup&gt; string (without the &lt; &gt;
     * characters - e.g. "Sirt2tm1a(EUCOMM)Wtsi", with sup "tm1a(EUCOMM)Wtsi"),
 parses the string into the gene part (Sirt2) and the alleleSub part without
 the gene part and without the &lt; &gt; entities.
     * (tm1a(EUCOMM)Wtsi)
     * @param geneAllele
     * @param sup 
     */
    public AlleleParser(String geneAllele, String sup) {
        gene = geneAllele.replace(sup, "");
        alleleSub = sup;
    }

    /**
     * 
     * @return the gene part, (e.g. given the string "Sirt2&lt;tm1a(EUCOMM)Wtsi&gt;",
     * returns "Sirt2")
     */
    public String getGene() {
        return gene;
    }

    /**
     * 
     * @return the alleleSub part, without the prepended gene and without the
 "&lt;" and "&gt;" HTML entities (e.g. given the string "Sirt2&lt;tm1a(EUCOMM)Wtsi&gt;",
     * returns "tm1a(EUCOMM)Wtsi")
     */
    public String getAlleleSub() {
        return alleleSub;
    }
    
    /**
     * 
     * @return the 
     */
    @Override
    public String toString() {
        return (gene + "<" + alleleSub + ">");
    }

}
