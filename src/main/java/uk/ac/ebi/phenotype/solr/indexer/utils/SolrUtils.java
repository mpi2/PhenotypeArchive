/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor. /** Copyright © 2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.ebi.phenotype.solr.indexer.utils;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.MpDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import static uk.ac.ebi.phenotype.service.OntologyService.BATCH_SIZE;

/**
 *
 * @author mrelac
 */
public class SolrUtils {

    private static final Logger logger = LoggerFactory.getLogger(SolrUtils.class);

    // PRIVATE METHODS
    private static void printItemList(String label, List<String> itemList) {
        System.out.print("\t" + label);
        int itemCount = 0;
        if (itemList == null) {
            System.out.print("\t[null]");
        } else {
            for (String item : itemList) {
                if (itemCount == 0) {
                    System.out.print("\t[");
                } else {
                    System.out.print(", ");
                }
                itemCount ++;
                System.out.print(item);
                if (itemCount == itemList.size()) {
                    System.out.print("]");
                }
            }
        }

        System.out.println();
    }

    // UTILITY METHODS
    /**
     * Extract the <code>HttpSolrServer</code> from the <code>SolrServer</code>,
     * if there is one. Most SolrServer implementations contain an <code>
     * HttpSolrServer</code> instance. If the supplied solrServer does, that
     * instance is returned; otherwise, null is returned. The method is
     * synchronized to insure thread safety.
     *
     * @param solrServer the <code>SolrServer</code> instance
     * @return the embedded <code>HttpSolrServer</code>, if there is one; null
     * otherwise
     */
    public static synchronized HttpSolrServer getHttpSolrServer(SolrServer solrServer) {
        if (solrServer instanceof HttpSolrServer) {
            return (HttpSolrServer) solrServer;
        }

        HttpSolrServer httpSolrServer = null;
        try {
            Field[] fieldList = solrServer.getClass().getDeclaredFields();
            for (Field field : fieldList) {
                field.setAccessible(true);
                Object o = field.get(solrServer);
                if (o instanceof HttpSolrServer) {
                    httpSolrServer = (HttpSolrServer) o;
                    return httpSolrServer;
                }
            }
        } catch (Exception e) {
            logger.error("Exception while trying to extract HttpSolrServer from SolrServer: " + e.getLocalizedMessage());
        }

        return httpSolrServer;
    }

    /**
     * Extract the SOLR base URL from the <code>SolrServer</code> instance
     *
     * @param solrServer the <code>SolrServer</code> instance
     * @return the SOLR server base URL, if it can be found; or an empty string
     * if it cannot.
     */
    public static String getBaseURL(SolrServer solrServer) {
        HttpSolrServer httpSolrServer
                = (solrServer instanceof HttpSolrServer
                ? (HttpSolrServer) solrServer
                        : getHttpSolrServer(solrServer));
        if (httpSolrServer != null) {
            return httpSolrServer.getBaseURL();
        }

        return "";
    }

    // POPULATE METHODS
    /**
     * Fetch a map of image terms indexed by ma id
     *
     * @param imagesCore a valid solr connection
     * @return a map, indexed by child ma id, of all parent terms with
     * associations
     * @throws IndexerException
     */
    public static Map<String, List<SangerImageDTO>> populateSangerImagesMap(SolrServer imagesCore) throws IndexerException {
        Map<String, List<SangerImageDTO>> map = new HashMap();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("maTermId:*");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = null;
            try {
                response = imagesCore.query(query);
            } catch (Exception e) {
                throw new IndexerException("Unable to query images core", e);
            }

            total = response.getResults().getNumFound();
            List<SangerImageDTO> imageList = response.getBeans(SangerImageDTO.class);

            for (SangerImageDTO image : imageList) {
                for (String termId : image.getMaTermId()) {
                    if ( ! map.containsKey(termId)) {
                        map.put(termId, new ArrayList<SangerImageDTO>());
                    }
                    String imageId = image.getId();
                    List<SangerImageDTO> sangerImageList = map.get(termId);

                    boolean imageFound = false;
                    for (SangerImageDTO dto : sangerImageList) {
                        if (dto.getId().equalsIgnoreCase(imageId)) {
                            imageFound = true;
                            break;
                        }
                    }
                    // Don't add duplicate images.
                    if ( ! imageFound) {
                        map.get(termId).add(image);
                    }
                }
            }
            pos += BATCH_SIZE;
        }

        return map;
    }

    /**
     * Fetch a map of image terms indexed by ma id
     *
     * @param imagesCore a valid solr connection
     * @return a map, indexed by child ma id, of all parent terms with
     * associations
     * @throws IndexerException
     */
    protected static Map<String, List<SangerImageDTO>> populateSangerImagesByMgiAccession(SolrServer imagesCore) throws IndexerException {
        Map<String, List<SangerImageDTO>> map = new HashMap();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("mgi_accession_id:*");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = null;
            try {
                response = imagesCore.query(query);
            } catch (Exception e) {
                throw new IndexerException("Unable to query images core", e);
            }
            total = response.getResults().getNumFound();
            List<SangerImageDTO> imageList = response.getBeans(SangerImageDTO.class);
            for (SangerImageDTO image : imageList) {
                if ( ! map.containsKey(image.getAccession())) {
                    map.put(image.getAccession(), new ArrayList<SangerImageDTO>());
                }
                String imageId = image.getId();
                List<SangerImageDTO> sangerImageList = map.get(image.getAccession());

                boolean imageFound = false;
                for (SangerImageDTO dto : sangerImageList) {
                    if (dto.getId().equalsIgnoreCase(imageId)) {
                        imageFound = true;
                        break;
                    }
                }
                // Don't add duplicate images.
                if ( ! imageFound) {
                    map.get(image.getAccession()).add(image);
                }

            }
            pos += BATCH_SIZE;
        }

        return map;
    }

    /**
     * Fetch all alleles
     *
     * @param alleleCore a valid solr connection
     * @return a list of all alleles
     *
     * @throws IndexerException
     */
    protected static List<AlleleDTO> getAllAlleles(SolrServer alleleCore) throws IndexerException {
        List<AlleleDTO> alleleList = new ArrayList<>();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("*:*");

        query.setRows(Integer.MAX_VALUE);
        QueryResponse response = null;
        try {
            response = alleleCore.query(query);
        } catch (SolrServerException sse) {
            throw new IndexerException(sse);
        }
        total = response.getResults().getNumFound();
        logger.info("total alleles=" + total);
        alleleList = response.getBeans(AlleleDTO.class);

        logger.debug("Loaded {} alleles", alleleList.size());

        return alleleList;
    }

    /**
     * Fetch a map of mgi accessions to alleles
     *
     * @param alleleCore a valid solr connection
     * @return a map, indexed by MGI Accession id, of all alleles
     *
     * @throws IndexerException
     */
    protected static Map<String, List<AlleleDTO>> populateAllelesMap(SolrServer alleleCore)
            throws IndexerException {

        Map<String, List<AlleleDTO>> alleles = new HashMap<>();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("*:*");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = null;
            try {
                response = alleleCore.query(query);
            } catch (Exception e) {
                throw new IndexerException("Unable to query allele core in SolrUtils.populateAllelesMap()", e);
            }
            total = response.getResults().getNumFound();
            List<AlleleDTO> alleleList = response.getBeans(AlleleDTO.class);
            for (AlleleDTO allele : alleleList) {
                String key = allele.getMgiAccessionId();
                if ( ! alleles.containsKey(key)) {
                    alleles.put(key, new ArrayList<AlleleDTO>());
                }
                alleles.get(key).add(allele);
            }
            pos += BATCH_SIZE;
        }
        logger.debug("Loaded {} alleles", alleles.size());

        return alleles;
    }

    /**
     * Fetch a map of mp terms associated to hp terms, indexed by mp id.
     *
     * @param phenodigm_core a valid solr connection
     * @return a map, indexed by mp id, of all hp terms
     *
     * @throws IndexerException
     */
    public static Map<String, List<Map<String, String>>> populateMpToHpTermsMap(SolrServer phenodigm_core)
            throws IndexerException {

		// url="q=mp_id:&quot;${nodeIds.term_id}&quot;&amp;rows=999&amp;fq=type:mp_hp&amp;fl=hp_id,hp_term"
        // processor="XPathEntityProcessor" >
        //
        // <field column="hp_id" xpath="/response/result/doc/str[@name='hp_id']"
        // />
        // <field column="hp_term"
        // xpath="/response/result/doc/str[@name='hp_term']" />
        Map<String, List<Map<String, String>>> mpToHp = new HashMap<>();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("mp_id:*");
        query.addFilterQuery("type:mp_hp");// &amp;fl=hp_id,hp_term);
        query.add("fl=hp_id,hp_term");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = null;
            try {
                response = phenodigm_core.query(query);
            } catch (Exception e) {
                throw new IndexerException("Unable to query phenodigm_core in SolrUtils.populateMpToHpTermsMap()", e);
            }
            total = response.getResults().getNumFound();
            SolrDocumentList solrDocs = response.getResults();
            for (SolrDocument doc : solrDocs) {
                if (doc.containsKey("hp_id")) {
                    String hp = (String) doc.get("hp_id");
                    if (doc.containsKey("mp_id")) {

                        String mp = (String) doc.get("mp_id");
                        List<Map<String, String>> mapList = new ArrayList<>();
                        Map<String, String> entryMap = new HashMap<>();
                        if (mpToHp.containsKey(mp)) {
                            mapList = mpToHp.get(mp);
                        }
                        entryMap.put("hp_id", hp);
                        if (doc.containsKey("hp_term")) {
                            String hpTerm = (String) doc.get("hp_term");
                            entryMap.put("hp_term", hpTerm);
                        }
                        mapList.add(entryMap);
                        mpToHp.put(mp, mapList);
                    }
                }

            }
            pos += BATCH_SIZE;
        }

        return mpToHp;
    }

    /**
     * Get a map of MpDTOs by key mgiAccesion
     *
     * @param mpSolrServer
     * @return the map
     * @throws IndexerException
     */
    public static Map<String, List<MpDTO>> populateMgiAccessionToMp(SolrServer mpSolrServer)
            throws IndexerException {

        Map<String, List<MpDTO>> mps = new HashMap<>();
        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("mgi_accession_id:*");
        //query.add("fl=mp_id,mp_term,mp_definition,mp_term_synonym,ontology_subset,hp_id,hp_term,top_level_mp_id,top_level_mp_term,top_level_mp_term_synonym,intermediate_mp_id,intermediate_mp_term,intermediate_mp_term_synonym,child_mp_id,child_mp_term,child_mp_term_synonym,inferred_ma_id,inferred_ma_term,inferred_ma_term_synonym,inferred_selected_top_level_ma_id,inferred_selected_top_level_ma_term,inferred_selected_top_level_ma_term_synonym,inferred_child_ma_id,inferred_child_ma_term,inferred_child_ma_term_synonym");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = null;
            try {
                response = mpSolrServer.query(query);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IndexerException("Unable to query phenodigm_core in SolrUtils.populateMpToHpTermsMap()", e);
            }
            total = response.getResults().getNumFound();
            List<MpDTO> mpBeans = response.getBeans(MpDTO.class);

            for (MpDTO mp : mpBeans) {
                if (mp.getMgiAccessionId() != null &&  ! mp.getMgiAccessionId().equals("")) {
                    for (String geneAccession : mp.getMgiAccessionId()) {

                        if (mps.containsKey(geneAccession)) {
                            mps.get(geneAccession).add(mp);
                        } else {
                            List<MpDTO> mpListPerGene = new ArrayList<>();
                            mpListPerGene.add(mp);
                            mps.put(geneAccession, mpListPerGene);
                        }
                    }
                }
            }
            pos += BATCH_SIZE;
        }
        return mps;
    }

    /**
     * Get a map of MpDTOs by key mgiAccesion
     *
     * @param mpSolrServer
     * @return the map
     * @throws IndexerException
     */
    public static Map<String, MpDTO> populateMpTermIdToMp(SolrServer mpSolrServer)
            throws IndexerException {

        Map<String, MpDTO> mps = new HashMap<>();
        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("*:*");
        //query.add("fl=mp_id,mp_term,mp_definition,mp_term_synonym,ontology_subset,hp_id,hp_term,top_level_mp_id,top_level_mp_term,top_level_mp_term_synonym,intermediate_mp_id,intermediate_mp_term,intermediate_mp_term_synonym,child_mp_id,child_mp_term,child_mp_term_synonym,inferred_ma_id,inferred_ma_term,inferred_ma_term_synonym,inferred_selected_top_level_ma_id,inferred_selected_top_level_ma_term,inferred_selected_top_level_ma_term_synonym,inferred_child_ma_id,inferred_child_ma_term,inferred_child_ma_term_synonym");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = null;
            try {
                response = mpSolrServer.query(query);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IndexerException("Unable to query phenodigm_core in SolrUtils.populateMpToHpTermsMap()", e);
            }
            total = response.getResults().getNumFound();
            List<MpDTO> mpBeans = response.getBeans(MpDTO.class);

            for (MpDTO mp : mpBeans) {
                if (mp.getMpId() != null &&  ! mp.getMpId().equals("")) {

                    mps.put(mp.getMpId(), mp);

                }
            }
            pos += BATCH_SIZE;
        }
        return mps;
    }

    /**
     * Dumps out the list of <code>SangerImageDTO</code>, prepending the <code>
     * what</code> string for map identification.
     *
     * @param map the map to dump
     * @param what a string identifying the map, prepended to the output.
     * @param maxIterations The maximum number of iterations to dump. Any value
     * not greater than 0 (including null) will dump the entire map.
     */
    protected static void dumpSangerImagesMap(Map<String, List<SangerImageDTO>> map, String what, Integer maxIterations) {

        if ((maxIterations == null) || (maxIterations < 1)) {
            maxIterations = map.size();
        }

        logger.info(what);

        Iterator<Entry<String, List<SangerImageDTO>>> it = map.entrySet().iterator();
        while ((it.hasNext()) && (maxIterations-- > 0)) {
            Entry<String, List<SangerImageDTO>> entry = it.next();
            logger.info("KEY: " + entry.getKey());
            List<SangerImageDTO> dtoList = entry.getValue();
            for (SangerImageDTO dto : dtoList) {
                printItemList("procedure_name:", dto.getProcedureName());
                printItemList("expName:", dto.getExpName());
                printItemList("expName_exp:", dto.getExpNameExp());
                printItemList("symbol_gene:", dto.getSymbolGene());

                printItemList("mgi_accession_id:", dto.getMgiAccessionId());
                printItemList("marker_symbol:", dto.getMarkerSymbol());
                printItemList("marker_name:", dto.getMarkerName());
                printItemList("marker_synonym:", dto.getMarkerSynonym());
                printItemList("marker_type:", dto.getMarkerType());
                printItemList("human_gene_symbol:", dto.getHumanGeneSymbol());

                printItemList("status:", dto.getStatus());
                printItemList("imits_phenotype_started:", dto.getImitsPhenotypeStarted());
                printItemList("imits_phenotype_complete:", dto.getImitsPhenotypeComplete());
                printItemList("imits_phenotype_status:", dto.getImitsPhenotypeStatus());

                printItemList("latest_phenotype_status:", dto.getLatestPhenotypeStatus());

                logger.info("\tlegacy_phenotype_status:\t" + dto.getLegacyPhenotypeStatus());
                printItemList("latest_production_centre:", dto.getLatestProductionCentre());
                printItemList("latest_phenotyping_centre:", dto.getLatestPhenotypingCentre());

                printItemList("allele_name:", dto.getAlleleName());
            }

            System.out.println();
        }
    }

}
