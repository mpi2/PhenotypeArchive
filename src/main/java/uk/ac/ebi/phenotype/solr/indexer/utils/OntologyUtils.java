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

package uk.ac.ebi.phenotype.solr.indexer.utils;

import uk.ac.ebi.phenotype.solr.indexer.beans.ImpressBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.OrganisationBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author mrelac
 */
public class OntologyUtils {
    
    public static final int MAX_ROWS = 1000000;
    public static final int BATCH_SIZE = 50;
    
    private static Map<String, List<String>> maSynonymsMap = null;
    private static Map<String, List<String>> mpSynonymsMap = null;
    
    /**
     * Dumps out the <code>OntologyTermBean</code> map, prepending the <code>
     * what</code> string for map identification.
     * @param map the map to dump
     * @param what a string identifying the map, prepended to the output.
     */
    protected static void dumpOntologyMaTermMap(Map<String, List<OntologyTermBean>> map, String what) {
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
                System.out.format("%10.10s\t%10.10s\t", recordIndex == 0 ? record.key : "", bean.getTermId());
                System.out.print(bean.getName() + "\t");
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

    /**
     * Query the database, returning a map of all selected ma top-level terms,
     * indexed by ma term id. Terry has hand-picked these intermediate-level
     * terms to be treated as top-level terms.
     *
     * @param ontoDbConnection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a map of all selected top-level ma terms, indexed by ma term id
     */
    protected static Map<String, List<OntologyTermBean>> populateMaSelectedTopLevelTerms(Connection ontoDbConnection) throws SQLException {
        Map<String, List<OntologyTermBean>> map = new HashMap();
        String query =
            "SELECT DISTINCT\n"
                + "  ti.term_id           AS maTermId\n"
                + ", ti.name              AS maTermName\n"
                + ", ti2.term_id          AS selectedMaTermId\n"
                + ", ti2.name             AS selectedMaTermName\n"
                + ", ti2.definition       AS selectedMaTermNameDefinition\n"
                + "FROM ma_term_infos ti\n"
                + "JOIN ma_node2term nt                        ON nt.term_id           = ti .term_id\n"
                + "JOIN ma_node_2_selected_top_level_mapping m ON m .node_id           = nt .node_id\n"
                + "JOIN ma_term_infos ti2                      ON m .top_level_term_id = ti2.term_id\n"
                + "ORDER BY ti.term_id, ti2.term_id\n";

        try (final PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                String mapKey = resultSet.getString("maTermId");
                if ( ! map.containsKey(mapKey)) {
                    map.put(mapKey, new ArrayList<OntologyTermBean>());
                }
                String termId = resultSet.getString("selectedMaTermId");
                String name = resultSet.getString("selectedMaTermName");
                String definition = resultSet.getString("selectedMaTermNameDefinition");
                List<String> synonyms = getMaSynonyms(ontoDbConnection, termId);
                OntologyTermBean bean = new OntologyTermBean(termId, name, synonyms, definition);
                map.get(mapKey).add(bean);
            }
        }
        
        return map;
    }

    /**
     * Query the database, returning a map of all ma synonyms indexed by ma term id
     *
     * @param ontoDbConnection active database connection to table named
     *     'ma_synonyms'.
     *
     * @throws SQLException when a database exception occurs
     * @return a map, indexed by ma term id
     */
    protected static Map populateMaSynonyms(Connection ontoDbConnection) throws SQLException {
        Map<String, List<String>> map = new HashMap();
        String synonymsQuery =
            "SELECT\n" +
            "  term_id\n" +
            ", syn_name\n" +
            "FROM ma_synonyms ms";
        
        try (PreparedStatement ps = ontoDbConnection.prepareStatement(synonymsQuery)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String termId = resultSet.getString("term_id");
                String synonym = resultSet.getString("syn_name");
                if ( ! map.containsKey(termId)) {
                    map.put(termId, new ArrayList<String>());
                }
                List<String> synonyms = map.get(termId);
                if ( ! synonyms.contains(synonym)) {
                    synonyms.add(synonym);
                }
            }
            
            ps.close();
        }
        
        return map;
    }

    /**
     * Query the database, returning a map of all mp synonyms indexed by mp term id
     *
     * @param ontoDbConnection active database connection to table named
     *     'mp_synonyms'.
     *
     * @throws SQLException when a database exception occurs
     * @return a map, indexed by mp term id
     */
    protected static Map populateMpSynonyms(Connection ontoDbConnection) throws SQLException {
        Map<String, List<String>> map = new HashMap();
        String synonymsQuery =
            "SELECT\n" +
            "  term_id\n" +
            ", syn_name\n" +
            "FROM ma_synonyms ms";
        
        try (PreparedStatement ps = ontoDbConnection.prepareStatement(synonymsQuery)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String termId = resultSet.getString("term_id");
                String synonym = resultSet.getString("syn_name");
                if ( ! map.containsKey(termId)) {
                    map.put(termId, new ArrayList<String>());
                }
                List<String> synonyms = map.get(termId);
                if ( ! synonyms.contains(synonym)) {
                    synonyms.add(synonym);
                }
            }
            
            ps.close();
        }
        
        return map;
    }
    
    /**
     * Fetch a map of all child terms indexed by parent ma id
     * 
     * @param ontoDbConnection a valid database connection
     * @return a map of all child terms associated with parent terms, indexed by
     * parent ma id
     * @throws SQLException 
     */
    protected static Map<String, List<OntologyTermBean>> populateMaTermChildTerms(Connection ontoDbConnection) throws SQLException {
        Map<String, List<OntologyTermBean>> map = new HashMap();
        String childTermQuery =
            "SELECT DISTINCT\n" +
                "  ti.term_id     AS maTermId\n" +
                ", ti2.term_id    AS child_ma_id\n" +
                ", ti2.name       AS child_ma_term\n" +
                ", ti2.definition AS child_ma_definition\n" +
                "FROM ma_term_infos ti\n" +
                "INNER JOIN ma_node2term       nt  ON ti.term_id       = nt.term_id\n" +
                "INNER JOIN ma_parent_children pc  ON nt.node_id       = pc.parent_node_id\n" +
                "INNER JOIN ma_node2term       nt2 ON pc.child_node_id = nt2.node_id\n" +
                "INNER JOIN ma_term_infos      ti2 ON nt2.term_id      = ti2.term_id\n" +
                "ORDER BY ti.term_id, ti2.term_id, ti2.name";
        
        try (PreparedStatement ps = ontoDbConnection.prepareStatement(childTermQuery)) {
            ps.setMaxRows(MAX_ROWS);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String mapKey = resultSet.getString("maTermId");
                if ( ! map.containsKey(mapKey)) {
                    map.put(mapKey, new ArrayList<OntologyTermBean>());
                }
                String termId = resultSet.getString("child_ma_id");
                String name = resultSet.getString("child_ma_term");
                String definition = resultSet.getString("child_ma_definition");
                List<String> synonyms = getMaSynonyms(ontoDbConnection, mapKey);
                OntologyTermBean bean = new OntologyTermBean(termId, name, synonyms, definition);
                map.get(mapKey).add(bean);
            }
        }
        
        return map;
    }
    
    /**
     * Query the database, returning a map of all parent terms indexed by child ma id
     * 
     * @param ontoDbConnection a valid database connection
     * @return a map of all parent terms associated with child terms, indexed by
     * child ma id
     * @throws SQLException 
     */
    protected static Map<String, List<OntologyTermBean>> populateMaTermParentTerms(Connection ontoDbConnection) throws SQLException {
        Map<String, List<OntologyTermBean>> map = new HashMap();
        String parentTermQuery =
            "SELECT DISTINCT\n" +
                "  n2node.term_id AS maTermId\n" +
                ", mt.term_id AS parent_ma_id\n" +
                ", mt.name AS parent_ma_term\n" +
                ", mt.definition AS parent_ma_definition\n" +
                "FROM ma_term_infos mt\n" +
                "INNER JOIN ma_node2term n2term ON n2term.term_id = mt.term_id\n" +
                "INNER JOIN ma_node_top_level tln ON tln.top_level_node_id = n2term.node_id\n" +
                "INNER JOIN ma_node2term n2node ON n2node.node_id = tln.node_id\n" +
                "ORDER BY n2node.term_id, mt.term_id, mt.name\n";
        
        try (PreparedStatement ps = ontoDbConnection.prepareStatement(parentTermQuery)) {
            ps.setMaxRows(MAX_ROWS);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String mapKey = resultSet.getString("maTermId");
                if ( ! map.containsKey(mapKey)) {
                    map.put(mapKey, new ArrayList<OntologyTermBean>());
                }
                String termId = resultSet.getString("parent_ma_id");
                String name = resultSet.getString("parent_ma_term");
                String definition = resultSet.getString("parent_ma_definition");
                List<String> synonyms = getMaSynonyms(ontoDbConnection, mapKey);
                OntologyTermBean bean = new OntologyTermBean(termId, name, synonyms, definition);
                map.get(mapKey).add(bean);
            }
        }
        
        return map;
    }

    /**
     * Query the database, returning a map of all ma term subsets indexed by
     * ma term id
     * @param ontoDbConnection
     * @return a map of all ma term subsets indexed by ma term id
     * @throws SQLException 
     */
    protected static Map<String, List<String>> populateMaTermSubsets(Connection ontoDbConnection) throws SQLException {
        Map<String, List<String>> map = new HashMap();
        String query = "SELECT\n" + "  term_id\n" + ", subset\n" + "FROM ma_term_subsets mts\n";
        try (final PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                String termId = resultSet.getString("term_id");
                String subset = resultSet.getString("subset");
                if ( ! map.containsKey(termId)) {
                    map.put(termId, new ArrayList<String>());
                }
                map.get(termId).add(subset);
            }
        }
        return map;
    }

    /**
     * Query the database, returning a map of all mp top-level terms,
     * indexed by mp term id
     *
     * @param ontoDbConnection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a map of all top-level mp terms, indexed by mp term id
     */
    protected static Map<String, List<OntologyTermBean>> populateMpTopLevelTerms(Connection ontoDbConnection) throws SQLException {
        Map<String, List<OntologyTermBean>> map = new HashMap();
        String query =
            "SELECT DISTINCT\n"
                + "  ti.term_id     AS mpTermId\n"
                + ", ti.name        AS mpTermName\n"
                + ", ti2.term_id    AS mpTopLevelId\n"
                + ", ti2.name       AS mpTopLevelName\n"
                + ", ti2.definition AS mpTopLevelDefinition\n"
                + "FROM mp_term_infos     AS ti\n"
                + "JOIN mp_node2term      AS nt  ON nt .term_id = ti .term_id\n"
                + "JOIN mp_node_top_level AS m   ON m  .node_id = nt .node_id\n"
                + "JOIN mp_node2term      AS nt2 ON nt2.node_id = m  .top_level_node_id\n"
                + "JOIN mp_term_infos     AS ti2 ON ti2.term_id = nt2.term_id\n"
                + "ORDER BY ti.term_id, ti2.term_id\n";
                
        try (final PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                String mapKey = resultSet.getString("mpTermId");
                if ( ! map.containsKey(mapKey)) {
                    map.put(mapKey, new ArrayList<OntologyTermBean>());
                }
                String termId = resultSet.getString("mpTopLevelId");
                String name = resultSet.getString("mpTopLevelName");
                String definition = resultSet.getString("mpTopLevelDefinition");
                List<String> synonyms = getMpSynonyms(ontoDbConnection, termId);
                OntologyTermBean bean = new OntologyTermBean(termId, name, synonyms, definition);
                map.get(mapKey).add(bean);
            }
        }
        
        return map;
    }

    /**
     * Query the database, returning a map of all mp top-level terms,
     * indexed by mp term id
     *
     * @param ontoDbConnection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a map of all top-level mp terms, indexed by mp term id
     */
    protected static Map<String, List<OntologyTermBean>> populateMpIntermediateTerms(Connection ontoDbConnection) throws SQLException {
        Map<String, List<OntologyTermBean>> map = new HashMap();
        String query = "SELECT DISTINCT " +
            "ti2.term_id    AS mpIntermediateId, " +
            "ti2.name       AS mpIntermediateName, " +
            "ti2.definition AS mpIntermediateDefinition, " +
            "ti.term_id     AS mpTermId, " +
            "ti.name        AS mpTermName " +
            "FROM mp_node2term            AS nt " +
            "INNER JOIN mp_node_subsumption_fullpath AS f ON nt.node_id = f.child_node_id " +
            "INNER JOIN mp_term_infos     AS ti ON ti.term_id = nt.term_id " +
            "INNER JOIN mp_node2term      AS nt2 ON nt2.node_id = f.node_id " +
            "INNER JOIN mp_term_infos     AS ti2 ON ti2.term_id = nt2.term_id " +
            "WHERE f.node_id != nt.node_id ";

        try (final PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                String mapKey = resultSet.getString("mpTermId");
                if ( ! map.containsKey(mapKey)) {
                    map.put(mapKey, new ArrayList<OntologyTermBean>());
                }
                String termId = resultSet.getString("mpIntermediateId");
                String name = resultSet.getString("mpIntermediateName");
                String definition = resultSet.getString("mpIntermediateDefinition");
                List<String> synonyms = getMpSynonyms(ontoDbConnection, termId);
                OntologyTermBean bean = new OntologyTermBean(termId, name, synonyms, definition);
                map.get(mapKey).add(bean);
            }
        }

        return map;
    }


    // PRIVATE METHODS
    
    
    /**
     * Returns a cached map of synonyms matching <code>maTermId</code>
     * @param ontoDbConnection
     * @param maTermId the ma term id to match
     * @return a list of ma synonyms matching <code>maTermId</code>
     * @throws SQLException 
     */
    protected static List<String> getMaSynonyms(Connection ontoDbConnection, String maTermId) throws SQLException {
        if (maSynonymsMap == null) {
            maSynonymsMap = populateMaSynonyms(ontoDbConnection);
        }
        
        return maSynonymsMap.get(maTermId);
    }
    
    /**
     * Returns a cached map of synonyms matching <code>mpTermId</code>
     * @param ontoDbConnection
     * @param mpTermId the mp term id to match
     * @return a list of mp synonyms matching <code>mpTermId</code>
     * @throws SQLException 
     */
    protected static List<String> getMpSynonyms(Connection ontoDbConnection, String mpTermId) throws SQLException {
        if (mpSynonymsMap == null) {
            mpSynonymsMap = populateMpSynonyms(ontoDbConnection);
        }
        
        return mpSynonymsMap.get(mpTermId);
    }


    /**
     * Return relevant impress pipeline map
     *
     * @throws java.sql.SQLException when a database exception occurs
     */
    public static Map<Integer, ImpressBean> populateImpressPipeline(Connection connection) throws SQLException {

        Map<Integer, ImpressBean> impressMap;

        String query = "SELECT id, stable_key, name, stable_id FROM phenotype_pipeline";
        try (PreparedStatement p = connection.prepareStatement(query)) {

            impressMap = populateImpressMap(p);
        }

        return impressMap;
    }



    /**
     * Return relevant impress procedure map
     *
     * @throws java.sql.SQLException when a database exception occurs
     */
    public static Map<Integer, ImpressBean> populateImpressProcedure(Connection connection) throws SQLException {

        Map<Integer, ImpressBean> impressMap;

        String query = "SELECT id, stable_key, name, stable_id FROM phenotype_procedure";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            impressMap = populateImpressMap(p);
        }

        return impressMap;
    }

    /**
     * Return relevant impress parameter map
     *
     * @throws java.sql.SQLException when a database exception occurs
     */
    public static Map<Integer, ImpressBean> populateImpressParameter(Connection connection) throws SQLException {

        Map<Integer, ImpressBean> impressMap;

        String query = "SELECT id, stable_key, name, stable_id FROM phenotype_parameter";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            impressMap = populateImpressMap(p);
        }

        return impressMap;
    }


    /**
     * Populate the map with organisation beans
     *
     * @param connection
     * @return the populated map of organisation beans indexed by internal DB id
     * @throws SQLException when a database error occurs
     */
    public static Map<Integer, OrganisationBean> populateOrganisationMap(Connection connection) throws SQLException {

        Map<Integer, OrganisationBean> map = new HashMap<>();
        String query = "SELECT id, name, fullname, country FROM organisation";
        try (PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {

                OrganisationBean b = new OrganisationBean();

                b.setId(resultSet.getInt("id"));
                b.setName(resultSet.getString("name"));
                b.setFullname(resultSet.getString("fullname"));
                b.setCountry(resultSet.getString("country"));
                map.put(b.getId(), b);
            }
        }

        return map;

    }

    /**
     * Query the database, returning a map of all mp terms,
     * indexed by mp accession id
     *
     * @param ontoDbConnection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a map of all mp terms, indexed by mp term id
     */
    protected static Map<String, OntologyTermBean> populateMpTerms(Connection ontoDbConnection) throws SQLException {

        Map<String, OntologyTermBean> map = new HashMap();

        String query = "SELECT DISTINCT term_id, name, definition FROM mp_term_infos";

        try (final PreparedStatement p = ontoDbConnection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                String termId = resultSet.getString("term_id");
                String name = resultSet.getString("name");
                String definition = resultSet.getString("definition");
                List<String> synonyms = getMpSynonyms(ontoDbConnection, termId);
                OntologyTermBean bean = new OntologyTermBean(termId, name, synonyms, definition);
                map.put(termId, bean);
            }

        }

        return map;
    }



    // INTERNAL METHODS

    private static Map<Integer, ImpressBean> populateImpressMap(PreparedStatement p) throws SQLException {

        Map<Integer, ImpressBean> impressMap = new HashMap<>();

        ResultSet resultSet = p.executeQuery();

        while (resultSet.next()) {

            ImpressBean b = new ImpressBean();

            b.id = resultSet.getInt("id");
            b.stableKey = resultSet.getString("stable_key");
            b.stableId = resultSet.getString("stable_id");
            b.name = resultSet.getString("name");

            impressMap.put(resultSet.getInt("id"), b);
        }

        return impressMap;
    }


    // INTERNAL CLASSES


    public static class OntologyTermRecord {
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
            if (thisBean.getTermId().equalsIgnoreCase(thatBean.getTermId())) {
                if (thisBean.getName().equalsIgnoreCase(thatBean.getName())) {
                    return 0;
                } else {
                    return thisBean.getName().compareTo(thatBean.getName());
                }
            } else {
                return thisBean.getTermId().compareTo(thatBean.getTermId());
            }
        }
    }
    
}
