/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package uk.ac.ebi.phenotype.service;

import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates the code and data necessary to represent a Mouse
 * Pathology ontology.
 * 
 * @author mrelac
 */
public class MpathOntologyService extends OntologyService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public MpathOntologyService() throws SQLException {
        
    }
    
    /**
     * Returns the set of descendent graphs for the given id.
     * @param id the mpath id to query
     * @return the set of descendent graphs for the given id.
     */
    @Override
    public final List<List<String>> getDescendentGraphs(String id) {
        String nodeIds = StringUtils.join(id2nodesMap.get(id), ",");
        String query =
            "SELECT *\n"
          + "FROM mpath_node_subsumption_fullpath_concat\n"
          + "WHERE node_id IN (" + nodeIds + ")\n";
        
        return getDescendentGraphsInternal(query);
    }
    
    
    // PROTECTED METHODS
    
    
    /**
     * Populate all terms, keyed by id.
     * 
     * Side Effects: this method populates a map, indexed by id, of each id's 
     *               node ids, which is later used to create the ancestor list.
     * 
     * @throws SQLException 
     */
    @Override
    protected final void populateAllTerms() throws SQLException {
        String query =
            "SELECT\n"
          + "  n2t.term_id               AS termId\n"
          + ", GROUP_CONCAT(n2t.node_id) AS nodes\n"
          + ", ti.name                   AS termName\n"
          + ", ti.definition             AS termDefinition\n"
          + "FROM mpath_node2term n2t\n"
          + "LEFT OUTER JOIN mpath_term_infos ti ON ti.term_id = n2t.term_id\n"
          + "WHERE n2t.term_id != 'MPATH:0'\n"
          + "GROUP BY n2t.term_id\n"
          + "ORDER BY n2t.term_id, n2t.node_id\n";
        
        populateAllTerms(query);
    }
    
    /**
     * Populates each node's ancestor hash.
     * 
     * @throws SQLException 
     */
    @Override
    protected void populateAncestorMap() throws SQLException {
        String query =
            "SELECT *\n"
          + "FROM mpath_node_backtrace_fullpath\n";
        
        populateAncestorMap(query);
    }
    
    /**
     * Populates the node2term hash with the term matching each node.
     * 
     * @throws SQLException 
     */
    @Override
    protected void populateNode2TermMap() throws SQLException {
        String query =
            "SELECT *\n"
          + "FROM mpath_node2term\n"
          + "ORDER BY term_id\n";
        
        populateNode2TermMap(query);
    }
    
    /**
     * Query the database, returning a map of all synonyms indexed by term id
     *
     * @throws SQLException when a database exception occurs
     */
    @Override
    protected final void populateSynonyms() throws SQLException {
        String query =
            "SELECT\n"
          + "  term_id\n"
          + ", syn_name\n"
          + "FROM mpath_synonyms";
        
        populateSynonyms(query);
    }
}