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

package uk.ac.ebi.phenotype.solr.indexer.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates an ontology termId, name, termId & name concatenated, and a list
 of synonyms.
 * 
 * @author mrelac
 */
public class OntologyTermBean {
    private String termId;
    private String name;
    private List<String> synonyms;
    private String definition;
    
    /**
     * Create a new, empty <code>OntologyTermBean</code> instance.
     */
    public OntologyTermBean() {
        this("", "", new ArrayList<String>(), "");
    }
    
    /**
     * Create a new<code>OntologyTermBean</code> initialised instance
     * @param termId the ontology name termId
     * @param name the name name
     * @param synonyms a list of synonyms. May be empty or null. If null, a new
     * <code>ArrayList&lt;String&gt;</code> is created, guaranteeing that it is
     * not initialised to null.
     * 
     * NOTE: A concatenation of termId and name, separated by two underscores,
     *       is available as a getter.
     */
    public OntologyTermBean(String termId, String name, List<String> synonyms, String definition) {
        this.termId = termId;
        this.name = name;
        this.synonyms = synonyms;
        if (synonyms == null) {
            this.synonyms = new ArrayList();
        }
        this.definition = definition;
    }

    
    // BEAN SETTERS AND GETTERS
    
    
    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }



    // AUXILIARY METHODS

    
    /**
     * Returns a concatenation of term id and term name.
     * @return a concatenation of term id and term name, separated by a pair of
     * underscores. If either id or name is null, that null component is replaced
     * by an empty string.
     */
    public String getTermIdTermName() {
        String value = "";
        if (termId != null)
            value += termId;
        value += "__";
        if (name != null)
            value += name;
        
        return value;
    }
}
