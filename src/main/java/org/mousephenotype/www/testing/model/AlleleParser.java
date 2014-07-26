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
     * Given an allele string with '&lt;' and '&gt;' delimiters, e.g.
     * <b>Sirt2&lt;tm1a(EUCOMM)Wtsi&gt;</b>, this method parses the string into:
     * <ul>
     * <li>the gene part without the '&lt;' and '&gt;' delimiters, e.g.
     * <b>Sirt2</b>, and</li>
     * <li>the allele superscript part without the '&lt;' and '&gt;' delimiters,
     * e.g. <b>tm1a(EUCOMM)Wtsi</b>
     * </ul>
     * @param cookedAllele The allele string, with '&lt;' and '&gt;' delimiters,
     * to parse
     */
    public AlleleParser(String cookedAllele) {
        String[] sA = cookedAllele.split("<");
        if ((sA == null) || (sA.length < 2)) {
            gene = "";
            alleleSub = "";
        } else {
            gene = sA[0];
            alleleSub = sA[1].replace(">", "");
        }
    }

    /**
     * Given a string that looks like a raw allele including gene but without
     * the "&lt;" and "&gt;" delimiters, e.g. <b>Sirt2tm1a(EUCOMM)Wtsi</b>, and
     * a string containing the allele superscript part, e.g.
     * <b>tm1a(EUCOMM)Wtsi</b>, this method parses the pair of strings into:
     * <ul>
     * <li>the gene part without the '&lt;' and '&gt;' delimiters, e.g.
     * <b>Sirt2</b>, and</li>
     * <li>the allele superscript part without the '&lt;' and '&gt;' delimiters,
     * e.g. <b>tm1a(EUCOMM)Wtsi</b> (which is the same as <code>sup</code>)
     * </ul>
     *
     * @param rawAllele The allele string, without '&lt;' and '&gt;'
     * delimiters, but with the sup following the gene, to parse
     * @param sup The allele superscript string, without '&lt;' and '&gt;'
     * delimiters, to parse
     */
    public AlleleParser(String rawAllele, String sup) {
        gene = rawAllele.replace(sup, "");
        alleleSub = sup;
    }

    /**
     * 
     * @return the gene part, e.g. given the string <b>Sirt2&lt;tm1a(EUCOMM)Wtsi&gt;</b>,
     * returns <b>Sirt2</b>
     */
    public String getGene() {
        return gene;
    }

    /**
     * 
     * @return the alleleSub part, without the prepended gene and without the
 "&lt;" and "&gt;" delimiters, e.g. given the string <b>Sirt2&lt;tm1a(EUCOMM)Wtsi&gt;</b>,
     * returns <b>tm1a(EUCOMM)Wtsi</b>)
     */
    public String getAlleleSub() {
        return alleleSub;
    }
    
    /**
     * 
     * @return the formatted, cooked allele string, e.g. 
     * <b>Sirt2&lt;tm1a(EUCOMM)Wtsi&gt;</b>
     */
    @Override
    public String toString() {
        return (gene + "<" + alleleSub + ">");
    }

}