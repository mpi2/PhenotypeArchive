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

import org.apache.solr.client.solrj.SolrServer;
import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.beans.ImpressBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.OrganisationBean;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates the code and data necessary to represent all of the
 * maps used to build the various phenotype archive cores. The intention is that
 * the first caller to any given map will trigger the map to be loaded; subsequent
 * calls will simply return the cached map.
 *
 * @author mrelac
 */
public class IndexerMap {
    private static Map<String, List<SangerImageDTO>> sangerImagesMap = null;
    private static Map<String, List<AlleleDTO>> allelesMap = null;
    private static List<AlleleDTO> alleles = null;
    private static Map<Integer, ImpressBean> pipelineMap = null;
    private static Map<Integer, ImpressBean> procedureMap = null;
    private static Map<Integer, ImpressBean> parameterMap = null;
    private static Map<Integer, OrganisationBean> organisationMap = null;

    private static final Logger logger = LoggerFactory.getLogger(IndexerMap.class);



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
     * Returns a cached map of all sanger image terms associated to all ma ids,
     * indexed by ma term id.
     *
     * @param imagesCore a valid solr connection
     * @return a cached map of all sanger image terms associated to all ma ids,
     * indexed by ma term id.
     * @throws IndexerException
     */
    public static Map<String, List<SangerImageDTO>> getSangerImagesByMA(SolrServer imagesCore) throws IndexerException {
        if (sangerImagesMap == null) {
            sangerImagesMap = SolrUtils.populateSangerImagesMap(imagesCore);
        }
        
        return sangerImagesMap;
    }

    /**
     * Returns a cached map of all IMPReSS pipeline terms, indexed by internal database id.
     *
     * @param connection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a cached list of all impress pipeline terms, indexed by internal database id.
     */
    public static Map<Integer, ImpressBean> getImpressPipelines(Connection connection) throws SQLException {
        if (pipelineMap == null) {
            pipelineMap = OntologyUtils.populateImpressPipeline(connection);
        }
        return pipelineMap;
    }

    /**
     * Returns a cached map of all IMPReSS procedure terms, indexed by internal database id.
     *
     * @param connection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a cached list of all impress procedure terms, indexed by internal database id.
     */
    public static Map<Integer, ImpressBean> getImpressProcedures(Connection connection) throws SQLException {
        if (procedureMap == null) {
            procedureMap = OntologyUtils.populateImpressProcedure(connection);
        }
        return procedureMap;
    }

    /**
     * Returns a cached map of all IMPReSS parameter terms, indexed by internal database id.
     *
     * @param connection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a cached list of all impress parameter terms, indexed by internal database id.
     */
    public static Map<Integer, ImpressBean> getImpressParameters(Connection connection) throws SQLException {
        if (parameterMap == null) {
            parameterMap = OntologyUtils.populateImpressParameter(connection);
        }
        return parameterMap;
    }

    /**
     * Returns a cached map of all organisations, indexed by internal database id.
     *
     * @param connection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a cached list of all impress parameter terms, indexed by internal database id.
     */
    public static Map<Integer, OrganisationBean> getOrganisationMap(Connection connection) throws SQLException {
        if (organisationMap == null) {
            organisationMap = OntologyUtils.populateOrganisationMap(connection);
        }
        return organisationMap;
    }


    // UTILITY METHODS
    
    
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