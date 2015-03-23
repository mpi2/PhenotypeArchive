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

package uk.ac.ebi.phenotype.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates the code and data necessary to serve a Mouse
 * Anatomy ontology.
 * 
 * @author mrelac
 */
public class MaOntologyService extends OntologyService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final Map<String, String> subsets = new HashMap();

    public MaOntologyService() throws SQLException {
        
    }
    
    /**
     * Returns the set of descendent graphs for the given id.
     * 
     * @param id the ma id to query
     * 
     * @return the set of descendent graphs for the given id.
     */
    @Override
    public final List<List<String>> getDescendentGraphs(String id) {
        String nodeIds = StringUtils.join(id2nodesMap.get(id), ",");
        String query =
            "SELECT *\n"
          + "FROM ma_node_subsumption_fullpath_concat\n"
          + "WHERE node_id IN (" + nodeIds + ")\n";
        
        return getDescendentGraphsInternal(query);
    }
    
    /**
     * Methods annotated with @PostConstruct are executed just after the constructor
     * is run and spring is initialised.
     * 
     * @throws RuntimeException PostConstruct forbids throwing checked exceptions,
     * so SQLException is re-mapped to a RuntimeException if a failure occurs.
     */
    @Override
    @PostConstruct
    public void initialize() throws RuntimeException {
        super.initialize();
        
        try {
            populateSubsets();
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Returns a list of ma subsets, indexed by ma id.
     * 
     * @return a list of ma subsets, indexed by ma id.
     */
    public Map<String, String> getSubsets() {
        return subsets;
    }
    
    /**
     * Returns the ma subset for the given id.
     * 
     * @param id the ma id to query
     * 
     * @return the ma subset for the given id.
     */
    public String getSubset(String id) {
        return subsets.get(id);
    }
    
    /**
     * Returns the ma subset for the given id in the provided list.
     * 
     * @param id the ma id to query
     * 
     * @param list the list to which the subset is appended.
     * 
     * @return the ma subset for the given id.
     */
    public List<String> getSubset(String id, List<String> list) {
        list.add(subsets.get(id));
        
        return list;
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
                + ", ti.name                   AS termName \n"
                + ", ti.definition             AS termDefinition\n"
                + "FROM ma_node2term n2t\n"
                + "LEFT OUTER JOIN ma_term_infos ti ON ti.term_id = n2t.term_id\n"
                + "WHERE n2t.term_id != 'MA:0000001'\n"
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
          + "FROM ma_node_backtrace_fullpath\n";
        
        populateAncestorMap(query);

        // Remove all nodes above Terry's selected top-level.
        query =
            "SELECT \n"
          + "  node_id\n"
          + ", top_level_node_id\n"
          + "FROM ma_node_2_selected_top_level_mapping\n";
             
        HashMap<Integer, Integer> node_topLevelNodeMap = new HashMap();
        try (final PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int node = resultSet.getInt("node_id");
                int selectedNode = resultSet.getInt("top_level_node_id");
                if (selectedNode == 0) {
                    logger.warn("node " + node + " in ma_node_2_selected_top_level_mapping has no top-level node.");
                    continue;
                }
                node_topLevelNodeMap.put(node, selectedNode);
            }
            
            ps.close();
        }
            
        int missingFromFullpath = 0;
        int notIn_ma_node_2_selected_top_level_mapping = 0;
        // Loop through the ancestor hash, removing all nodes above the selected top level.
        // For each node in the ancestor hash, query its value (a single
        // string with space-delimited node ids, leftmost is top, rightmost
        // is leaf node), replacing the current top with the one defined in 
        // top_level_node_id.
        for (Entry<Integer, String> entry : ancestorMap.entrySet()) {
            int ancestorNode = entry.getKey();
            String[] fullpath = entry.getValue().split(" ");
            List<Integer> graph = new ArrayList();
            for (String sNode : fullpath) {
                int node = Integer.parseInt(sNode);
                if (node > 0) {
                    graph.add(node);
                }
            }

            if (node_topLevelNodeMap.containsKey(ancestorNode)) {
                int newSelectedTopLevelNode = node_topLevelNodeMap.get(ancestorNode);
                int newTopNodeIndex = graph.indexOf(newSelectedTopLevelNode);
                if (newTopNodeIndex >= 0) {
                    // New top level was found in fullpath (i.e. entry.getValue()).
                    graph.subList(0, newTopNodeIndex).clear();
                    String newFullpath = "";
                    for (Integer node : graph) {
                        if ( ! newFullpath.isEmpty())
                            newFullpath += " ";
                        newFullpath += Integer.toString(node);
                    }
                    
                    ancestorMap.put(ancestorNode, newFullpath);
                } else {
                    String ancestorId = node2termMap.get(Integer.toString(ancestorNode));
                    missingFromFullpath++;
logger.warn("MA ancestor node " + ancestorNode + " (" + ancestorId + ") (" + allTermsMap.get(ancestorId).getName() + ") is in topLevelNodeMap but not in the fullpath.");
                    // New top level not found in fullpath. Replace fullpath with ancestor node.
                    ancestorMap.put(ancestorNode, Integer.toString(ancestorNode));
                }
            } else {
                    String ancestorId = node2termMap.get(Integer.toString(ancestorNode));
                    notIn_ma_node_2_selected_top_level_mapping++;
logger.warn("MA ancestor node " + ancestorNode + " (" + ancestorId + ") (" + allTermsMap.get(ancestorId).getName() + ") was not found in the node_topLevelNodeMap!");
            }
        }
logger.warn("Missing from fullpath: " + missingFromFullpath + ".\tNot in ma_node_2_selected_top_level_mapping:_" + notIn_ma_node_2_selected_top_level_mapping + ".");
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
          + "FROM ma_node2term\n"
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
          + "FROM ma_synonyms";
        
        populateSynonyms(query);
    }

    
    // PRIVATE METHODS
    
   
    /**
     * Query the database, returning a map of all ma subsets, indexed by term id.
     *
     * @throws SQLException when a database exception occurs
     */
    private void populateSubsets() throws SQLException {
        String query =
            "SELECT DISTINCT *\n"
          + "FROM ma_term_subsets\n"
          + "ORDER BY term_id\n";
        
        try (final PreparedStatement ps = connection.prepareStatement(query)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                subsets.put(resultSet.getString("term_id"), resultSet.getString("subset"));
            }
            
            ps.close();
        }
    }
}