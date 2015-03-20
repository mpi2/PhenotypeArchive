/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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
 * This class provides access to the ontology detail:
 * <ul><li>ids list</li>
 * <li>definitions list</li>
 * <li>names list</li>
 * <li>id_name concatenations</li>
 * <li>synonyms list</li></ul>
 */
public class OntologyDetail {
    private final List<String> ids = new ArrayList(0);
    private final List<String> definitions = new ArrayList(0);
    private final List<String> names = new ArrayList(0);
    private final List<String> id_name_concatenations = new ArrayList(0);
    private List<String> synonyms = new ArrayList(0);

    public OntologyDetail(List<OntologyTermBean> beans) {
        for (OntologyTermBean bean : beans) {
            definitions.add(bean.getDefinition());
            ids.add(bean.getId());
            names.add(bean.getName());
            id_name_concatenations.add(bean.getId() + "__" + bean.getName());
            synonyms = bean.getSynonyms();
        }
    }

    public List<String> getIds() {
        return ids;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public List<String> getNames() {
        return names;
    }

    public List<String> getId_name_concatenations() {
        return id_name_concatenations;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }
}
