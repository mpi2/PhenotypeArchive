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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
            "SELECT DISTINCT\n" +
            "  ti.term_id  AS parent_ma_id\n" +
            ", ti2.term_id AS child_ma_id\n" +
            ", ti2.name    AS child_ma_term\n" +
            ", CONCAT(ti2.term_id, '__', ti2.name) AS termId_termName\n" +
            "FROM ma_term_infos ti\n" +
            "INNER JOIN ma_node2term       nt  ON ti.term_id       = nt.term_id\n" +
            "INNER JOIN ma_parent_children pc  ON nt.node_id       = pc.parent_node_id\n" +
            "INNER JOIN ma_node2term       nt2 ON pc.child_node_id = nt2.node_id\n" +
            "INNER JOIN ma_term_infos      ti2 ON nt2.term_id      = ti2.term_id\n" +
            "ORDER BY ti.term_id, ti2.term_id, ti2.name";
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
                String mapKey = resultSet.getString("parent_ma_id");
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
                
                if ( ! map.containsKey(mapKey)) {
                    map.put(mapKey, new ArrayList<OntologyTermBean>());
                }
                map.get(mapKey).add(bean);
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
            "SELECT DISTINCT\n" +
            "  n2node.term_id AS child_ma_id\n" +
            ", mt.term_id AS parent_ma_id\n" +
            ", mt.name AS parent_ma_term\n" +
            "FROM ma_term_infos mt\n" +
            "INNER JOIN ma_node2term n2term ON n2term.term_id = mt.term_id\n" +
            "INNER JOIN ma_node_top_level tln ON tln.top_level_node_id = n2term.node_id\n" +
            "INNER JOIN ma_node2term n2node ON n2node.node_id = tln.node_id\n" +
            "ORDER BY n2node.term_id, mt.term_id, mt.name\n";
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
                String mapKey = resultSet.getString("child_ma_id");
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
                
                if ( ! map.containsKey(mapKey)) {
                    map.put(mapKey, new ArrayList<OntologyTermBean>());
                }
                
                map.get(mapKey).add(bean);
            }
        }
        
        return map;
    }
    
    public static void dumpTerms(Map<String, List<OntologyTermBean>> map, String what) {
        List<OntologyTermRecord> termList = new ArrayList();
        Iterator<Entry<String, List<OntologyTermBean>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, List<OntologyTermBean>> entry = it.next();
            OntologyTermRecord rec = new OntologyTermRecord(entry);
            termList.add(rec);
        }
        
        Collections.sort(termList, new OntologyTermRecordComparator());
        
        Comparator<OntologyTermBean> c = new OntologyTermBeanComparator();
        System.out.println(what);
        System.out.format("%10.10s\t%10.10s\t%s\t%s\n", "KEY", "TERM_ID", "TERM_NAME", "[SYNONYMS]");
        for (OntologyTermRecord record : termList) {
            int recordIndex = 0;
            
            for (OntologyTermBean bean : record.value) {
                System.out.format("%10.10s\t%10.10s\t", recordIndex == 0 ? record.key : "", bean.getId());
                System.out.print(bean.getTerm() + "\t");
                List<String> synonyms = bean.getSynonyms();
                int synonymCount = 0;
                for (String synonym : synonyms) {
                    if (synonymCount == 0) {
                        System.out.print("[");
                    } else {
                        System.out.print(", ");
                    }
                    synonymCount++;
                    System.out.print(synonym);
                    if (synonymCount == synonyms.size()) {
                        System.out.print("]");
                    }
                }
                recordIndex++;
            
                System.out.println();
            }
            
            System.out.println();
        }
    }
    
    public static class OntologyTermRecord /*implements Comparator<OntologyTermRecord>*/ {
        public String key;
        public List<OntologyTermBean> value;
        
        public OntologyTermRecord(Entry<String, List<OntologyTermBean>> entry) {
            key = entry.getKey();
            value = entry.getValue();
        }
    }
    
    public static class OntologyTermRecordComparator implements Comparator<OntologyTermRecord> {

        @Override
        public int compare(OntologyTermRecord thisTerm, OntologyTermRecord thatTerm) {
            if (thisTerm.key.equalsIgnoreCase(thatTerm.key)) {
                OntologyTermBeanComparator ontologyTermBeanComparator = new OntologyTermBeanComparator();
                Collections.sort(thisTerm.value, ontologyTermBeanComparator);
                Collections.sort(thatTerm.value, ontologyTermBeanComparator);
                return 0;
            } else {
                return thisTerm.key.compareTo(thatTerm.key);
            }
        }
    }
    
    public static class OntologyTermBeanComparator implements Comparator<OntologyTermBean> {
        @Override
        public int compare(OntologyTermBean thisBean, OntologyTermBean thatBean) {
            if (thisBean.getId().equalsIgnoreCase(thatBean.getId())) {
                if (thisBean.getTerm().equalsIgnoreCase(thatBean.getTerm())) {
                    return 0;
                } else {
                    return thisBean.getTerm().compareTo(thatBean.getTerm());
                }
            } else {
                return thisBean.getId().compareTo(thatBean.getId());
            }
        }
    }
    
}
