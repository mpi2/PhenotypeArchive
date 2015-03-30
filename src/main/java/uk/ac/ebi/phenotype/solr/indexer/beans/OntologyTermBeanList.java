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

import java.util.List;
import uk.ac.ebi.phenotype.service.OntologyService;

/**
 * This class encapsulates the methods necessary to serve up individual lists of
 * ma <code>OntologyTermBean</code> components. Without this wrapper, ontology
 * detail data, such as synonyms, subsets, parents, children, etc. in an
 * <code>OntologyTermBean</code> list must be picked out individually, each in
 * its own loop.
 * 
 * @author mrelac
 */
public class OntologyTermBeanList {
    protected final String id;
    
    protected OntologyService ontologyService;
    
    public OntologyTermBeanList(OntologyService ontologyService, String id) {
        this.ontologyService = ontologyService;
        this.id = id;
    }

    public List<String> getSynonyms() {
        return ontologyService.getSynonyms(id);
    }
    
    /**
     * Returns this term's top-level terms.
     * 
     * @return this term's top-level terms.
     */
    public OntologyDetail getTopLevels() {
        List<OntologyTermBean> beans = ontologyService.getTopLevel(id);
        OntologyDetail detail = new OntologyDetail(beans);
        
        return detail;
    }
    
    /**
     * Returns this term's top-level terms at level <code>level</code>.
     * 
     * @param level the 1-relative level below the top level (i.e. 1 = top
     * level, 2 = top-level - 1, etc.)
     * 
     * @return this term's top-level terms at level <code>level</code>
     */
    public OntologyDetail getTopLevels(int level) {
        List<OntologyTermBean> beans = ontologyService.getTopLevel(id, level);
        OntologyDetail detail = new OntologyDetail(beans);
        
        return detail;
    }
    
    /**
     * Returns this term's ancestors.
     * 
     * @return this term's ancestors.
     */
    public OntologyDetail getAncestors() {
        List<OntologyTermBean> beans = ontologyService.getAncestors(id);
        OntologyDetail detail = new OntologyDetail(beans);
        
        return detail;
    }
    
    /**
     * Returns this term's parents.
     * 
     * @return this term's parents.
     */
    public OntologyDetail getParents() {
        List<OntologyTermBean> beans = ontologyService.getParents(id);
        OntologyDetail detail = new OntologyDetail(beans);
        
        return detail;
    }
    
    /**
     * Returns this term's intermediates.
     * 
     * @return this term's intermediates.
     */
    public OntologyDetail getIntermediates() {
        List<OntologyTermBean> beans = ontologyService.getIntermediates(id);
        OntologyDetail detail = new OntologyDetail(beans);
        
        return detail;
    }
    
    /**
     * Returns this term's children.
     * 
     * @return this term's children.
     */
    public OntologyDetail getChildren() {
        List<OntologyTermBean> beans = ontologyService.getChildren(id);
        OntologyDetail detail = new OntologyDetail(beans);
        
        return detail;
    }
    
    /**
     * Returns this term's descendents.
     * 
     * @return this term's descendents.
     */
    public OntologyDetail getDescendents() {
        List<OntologyTermBean> beans = ontologyService.getDescendents(id);
        OntologyDetail detail = new OntologyDetail(beans);
        
        return detail;
    }
    
    /**
     * Returns this term's descendents at level <code>level</code>.
     * 
     * @param level the 1-relative level below this term (i.e. 1 = descendent-
     * level 1, 2 = descendent-level - 1, etc.)
     * 
     * @return this term's descendents at level <code>level</code>
     */
    public OntologyDetail getDescendents(int level) {
        List<OntologyTermBean> beans = ontologyService.getDescendents(id, level);
        OntologyDetail detail = new OntologyDetail(beans);
        
        return detail;
    }
}
