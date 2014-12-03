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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrServer;
import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;

/**
 * This class encapsulates the code and data necessary to represent all of the
 * maps used to build the various phenotype archive cores. The intention is that
 * the first caller to any given map will trigger the map to be loaded; subsequent
 * calls will simply return the cached map.
 *
 * @author mrelac
 */
public class IndexerMap {
    private static Map<String, List<String>> maTermSubsetsMap = null;
    private static Map<String, List<OntologyTermBean>> maTermChildTermsMap = null;
    private static Map<String, List<OntologyTermBean>> maTermParentTermsMap = null;
    private static Map<String, List<String>> maTermSynonymsMap = null;
    private static Map<String, List<SangerImageDTO>> sangerImagesMap = null;
    private static Map<String, List<AlleleDTO>> allelesMap = null;
    private static Map<String, Map<String, String>> mpToHpTermsMap = null;
	private static List<AlleleDTO> alleles=null;
    
    
    // PRIVATE METHODS
    
    
    private static Map<String, List<String>> populateMaTermSubsets(Connection ontoDbConnection) throws SQLException {
        Map<String, List<String>> map = new HashMap();
        String query = 
                  "SELECT\n"
                + "  term_id\n"
                + ", subset\n"
                + "FROM ma_term_subsets mts\n";

        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
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
     * Fetch a map of ma synonyms indexed by ma term id
     * 
     * @param ontoDbConnection active database connection to table named
     *     'ma_synonyms'.
     * 
     * @throws SQLException when a database exception occurs
     * @return a map, indexed by 
     */
    private static Map<String, List<String>> populateMaTermSynonyms(Connection ontoDbConnection) throws SQLException {
        Map<String, List<String>> map = new HashMap();
        String query = "SELECT\n"
                + "  term_id\n"
                + ", syn_name\n"
                + "FROM ma_synonyms\n";

        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                String termId = resultSet.getString("term_id");
                String synName = resultSet.getString("syn_name");
                if ( ! map.containsKey(termId)) {
                    map.put(termId, new ArrayList<String>());
                }

                map.get(termId).add(synName);   
            }
        }
        
        return map;
    }
    
    
    // PUBLIC METHODS
    
    
    /**
     * Fetch a map of AlleleDTOs terms indexed by mgi_accession_id
     *
     * @param alleleCore a valid solr connection
     * @return a map, indexed by MGI Accession id, of all alleles
     * 
     * @throws IndexerException
     */
    public static Map<String, List<AlleleDTO>> getGeneToAlleles(SolrServer alleleCore) throws IndexerException {
        if (allelesMap == null) {
            allelesMap = SolrUtils.populateAllelesMap(alleleCore);
        }
        
        return allelesMap;
    }
    
    /**
     * Fetch a map of AlleleDTOs terms indexed by mgi_accession_id
     *
     * @param alleleCore a valid solr connection
     * @return a map, indexed by MGI Accession id, of all alleles
     * 
     * @throws IndexerException
     */
    public static List<AlleleDTO> getAlleles(SolrServer alleleCore) throws IndexerException {
        if (alleles== null) {
            alleles = SolrUtils.getAllAlleles(alleleCore);
        }
        
        return alleles;
    }
    
    /**
     * Fetch a map of image terms indexed by ma id
     *
     * @param phenodigm_core a valid solr connection
     * @return a map, indexed by mp id, of all hp terms
     * 
     * @throws IndexerException
     */
    public static Map<String, Map<String, String>> getMpToHpTerms(SolrServer phenodigm_core) throws IndexerException {
        if (mpToHpTermsMap == null) {
            mpToHpTermsMap = SolrUtils.populateMpToHpTermsMap(phenodigm_core);
        }
        
        return mpToHpTermsMap;
    }
    
    /**
     * Fetch a map of image terms indexed by ma id
     *
     * @param imagesCore a valid solr connection
     * @return a map, indexed by child ma id, of all parent terms with
     * associations
     * @throws IndexerException
     */
    public static Map<String, List<SangerImageDTO>> getSangerImagesByMA(SolrServer imagesCore) throws IndexerException {
        if (sangerImagesMap == null) {
            sangerImagesMap = SolrUtils.populateSangerImagesMap(imagesCore);
        }
        
        return sangerImagesMap;
    }
    
    /**
     * Queries the ma_term_subsets table, returning a map
     *
     * @param ontoDbConnection active database connection to table named
     *     'ma_term_subsets'.
     * 
     * @throws SQLException when a database exception occurs
     * @return the populated map, indexed by term id.
     */
    public static Map<String, List<String>> getMaTermSubsets(Connection ontoDbConnection) throws SQLException {
        if (maTermSubsetsMap == null) {
            maTermSubsetsMap = populateMaTermSubsets(ontoDbConnection);
        }
        
        return maTermSubsetsMap;
    }
    
    /**
     * Fetch a map of child terms indexed by parent ma id
     * 
     * @param ontoDbConnection a valid database connection
     * @return a map, indexed by parent ma id, of all child ma terms with
     * associations to child terms
     * @throws SQLException 
     */
    public static Map<String, List<OntologyTermBean>> getMaTermChildTerms(Connection ontoDbConnection) throws SQLException {
        if (maTermChildTermsMap == null) {
            maTermChildTermsMap = OntologyUtils.populateMaTermChildTerms(ontoDbConnection);
        }
        
        return maTermChildTermsMap;
    }
    
    /**
     * Fetch a map of parent terms indexed by child ma id
     * 
     * @param ontoDbConnection a valid database connection
     * @return a map, indexed by child ma id, of all parent ma terms with
     * associations to child terms
     * @throws SQLException 
     */
    public static Map<String, List<OntologyTermBean>> getMaTermParentTerms(Connection ontoDbConnection) throws SQLException {
        if (maTermParentTermsMap == null) {
            maTermParentTermsMap = OntologyUtils.populateMaTermParentTerms(ontoDbConnection);
        }
        
        return maTermParentTermsMap;
    }
    
    /**
     * Fetch a map of ma synonyms indexed by ma term id
     * 
     * @param ontoDbConnection active database connection to table named
     *     'ma_synonyms'.
     * 
     * @throws SQLException when a database exception occurs
     * @return a map, indexed by 
     */
    public static Map<String, List<String>> getMaTermSynonyms(Connection ontoDbConnection) throws SQLException {
        if (maTermSynonymsMap == null) {
            maTermSynonymsMap = populateMaTermSynonyms(ontoDbConnection);
        }
        
        return maTermSynonymsMap;
    }
    
    
    // UTILITY METHODS
    
    
    /**
     * Dumps out the <code>OntologyTermBean</code> map, prepending the <code>
     * what</code> string for map identification.
     * @param map the map to dump
     * @param what a string identifying the map, prepended to the output.
     */
    public static void dumpOntologyMaTermMap(Map<String, List<OntologyTermBean>> map, String what) {
        OntologyUtils.dumpOntologyMaTermMap(map, what);
    }
    
    /**
     * Dumps out the list of <code>SangerImageDTO</code>, prepending the <code>
     * what</code> string for map identification.
     * @param map the map to dump
     * @param what a string identifying the map, prepended to the output.
     * @param maxIterations The maximum number of iterations to dump. Any value
     * not greater than 0 (including null) will dump the entire map.
     */
    public static void dumpSangerImagesMap(Map<String, List<SangerImageDTO>> map, String what, Integer maxIterations) {
        SolrUtils.dumpSangerImagesMap(map, what, maxIterations);
    }

	public static Map<String, List<SangerImageDTO>> getSangerImagesByMgiAccession(SolrServer imagesCore) throws IndexerException {

		Map<String, List<SangerImageDTO>> map = SolrUtils.populateSangerImagesByMgiAccession(imagesCore);
		return map;
	}
    
}
