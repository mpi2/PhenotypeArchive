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

package uk.ac.ebi.phenotype.solr.indexer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;

/**
 *
 * @author mrelac
 */
public class OntologyUtil {
    
    public static final int MAX_ROWS = 1000000;
    public static final int BATCH_SIZE = 50;

    /**
     * Fetch a map of child terms indexed by parent ma id
     * 
     * @param ontoDbConnection a valid database connection
     * @return a map, indexed by parent ma id, of all child terms with associations
     * to parent terms
     * @throws SQLException 
     */
    public static Map<String, List<OntologyTermBean>> populateChildTerms(Connection ontoDbConnection) throws SQLException {
        Map<String, List<OntologyTermBean>> map = new HashMap();
        String childTermQuery =
            "SELECT\n" +
            "  ti.term_id  AS parent_ma_id\n" +
            ", ti2.term_id AS child_ma_id\n" +
            ", ti2.name    AS child_ma_term\n" +
            ", CONCAT(ti2.term_id, '__', ti2.name) AS termId_termName\n" +
            "FROM ma_term_infos ti\n" +
            "INNER JOIN ma_node2term       nt  ON ti.term_id       = nt.term_id\n" +
            "INNER JOIN ma_parent_children pc  ON nt.node_id       = pc.parent_node_id\n" +
            "INNER JOIN ma_node2term       nt2 ON pc.child_node_id = nt2.node_id\n" +
            "INNER JOIN ma_term_infos      ti2 ON nt2.term_id      = ti2.term_id";
        String childTermSynonymsQuery =
            "SELECT\n" +
            "  term_id\n" +
            ", syn_name\n" +
            "FROM ma_synonyms ms\n" +
            "WHERE term_id = ?";
        
        try (PreparedStatement ps = ontoDbConnection.prepareStatement(childTermQuery)) {
            ps.setMaxRows(MAX_ROWS);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                OntologyTermBean bean = new OntologyTermBean();
                bean.setId(resultSet.getString("child_ma_id"));
                bean.setTerm(resultSet.getString("child_ma_term"));
                bean.setIdTerm(resultSet.getString("termId_termName"));
                bean.setSynonyms(new ArrayList<String>());
                try (PreparedStatement p2 = ontoDbConnection.prepareStatement(childTermSynonymsQuery)) {
                    p2.setString(1, bean.getId());
                    ResultSet resultSet2 = p2.executeQuery();
                    while (resultSet2.next()) {
                        String synonym = resultSet2.getString("syn_name");
                        bean.getSynonyms().add(synonym);
                    }
                    p2.close();
                }
                
                if ( ! map.containsKey(bean.getId())) {
                    map.put(bean.getId(), new ArrayList<OntologyTermBean>());
                }
                map.get(bean.getId()).add(bean);
            }
        }
        
        return map;
    }
    
    /**
     * Fetch a map of parent terms indexed by child ma id
     * 
     * @param ontoDbConnection a valid database connection
     * @return a map, indexed by child ma id, of all parent terms with associations
     * to child terms
     * @throws SQLException 
     */
    public static Map<String, List<OntologyTermBean>> populateParentTerms(Connection ontoDbConnection) throws SQLException {
        Map<String, List<OntologyTermBean>> map = new HashMap();
        String parentTermQuery =
            "SELECT\n" +
            "  n2node.term_id AS child_ma_id\n" +
            ", mt.term_id AS parent_ma_id\n" +
            ", mt.name AS parent_ma_term\n" +
            "FROM ma_term_infos mt\n" +
            "INNER JOIN ma_node2term n2term ON n2term.term_id = mt.term_id\n" +
            "INNER JOIN ma_node_top_level tln ON tln.top_level_node_id = n2term.node_id\n" +
            "INNER JOIN ma_node2term n2node ON n2node.node_id = tln.node_id";
        String parentTermSynonymsQuery =
            "SELECT\n" +
            "  term_id\n" +
            ", syn_name\n" +
            "FROM ma_synonyms ms\n" +
            "WHERE term_id = ?";
        
        try (PreparedStatement ps = ontoDbConnection.prepareStatement(parentTermQuery)) {
            ps.setMaxRows(MAX_ROWS);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                OntologyTermBean bean = new OntologyTermBean();
                bean.setId(resultSet.getString("parent_ma_id"));
                bean.setTerm(resultSet.getString("parent_ma_term"));
                bean.setIdTerm("");
                bean.setSynonyms(new ArrayList<String>());
                try (PreparedStatement p2 = ontoDbConnection.prepareStatement(parentTermSynonymsQuery)) {
                    p2.setString(1, bean.getId());
                    ResultSet resultSet2 = p2.executeQuery();
                    while (resultSet2.next()) {
                        String synonym = resultSet2.getString("syn_name");
                        bean.getSynonyms().add(synonym);
                    }
                    p2.close();
                }
                
                if ( ! map.containsKey(bean.getId())) {
                    map.put(bean.getId(), new ArrayList<OntologyTermBean>());
                }
                map.get(bean.getId()).add(bean);
            }
        }
        
        return map;
    }
    
    /**
     * Fetch a map of image terms indexed by ma id
     * 
     * @param imagesCore a valid solr connection
     * @return a map, indexed by child ma id, of all parent terms with associations
     * to child terms
     */
    public static  Map<String, List<ImageDTO>> populateImageBean(SolrServer imagesCore) throws SolrServerException {
        Map<String, List<ImageDTO>> map = new HashMap();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("q=maTermId:*");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = imagesCore.query(query);
            total = response.getResults().getNumFound();
            List<ImageDTO> imageList = response.getBeans(ImageDTO.class);
            for (ImageDTO image : imageList) {
                
                

                if ( ! map.containsKey(image.getMaTermId())) {
                    map.put(image.getMaTermId(), new ArrayList<ImageDTO>());
                }
                map.get(image.getMaTermId()).add(image);
            }
            pos += BATCH_SIZE;
        }

        return map;
    }
}
