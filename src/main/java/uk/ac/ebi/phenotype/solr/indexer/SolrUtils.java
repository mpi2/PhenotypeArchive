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
package uk.ac.ebi.phenotype.solr.indexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;
import static uk.ac.ebi.phenotype.solr.indexer.OntologyUtil.BATCH_SIZE;

/**
 *
 * @author mrelac
 */
public class SolrUtils {

    private static final Logger logger = LoggerFactory.getLogger(SolrUtils.class);

    /**
     * Fetch a map of image terms indexed by ma id
     *
     * @param imagesCore a valid solr connection
     * @return a map, indexed by child ma id, of all parent terms with
     * associations
     * @throws SolrServerException to child terms
     */
    public static Map<String, List<SangerImageDTO>> populateImageBean(SolrServer imagesCore) throws SolrServerException {
        if (logger.isDebugEnabled()) {
            String url = "";
            try {
                // This forces an exception. Fortunately, the url we want is embedded in the exception message!
                // Exception message is: "Server at http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/images returned non ok status:500, message:Internal Server Error"
                url = imagesCore.ping().getRequestUrl();
            } catch (Exception e) {
                url = e.getLocalizedMessage().replaceFirst("Server at ", "");
                int endIndex = url.indexOf(" returned non ok");
                url = url.substring(0, endIndex);
            }
            logger.debug("USING CORE AT: '" + url + "'");
        }

        Map<String, List<SangerImageDTO>> map = new HashMap();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("maTermId:*");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = imagesCore.query(query);
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

    public static void dumpTerms(Map<String, List<SangerImageDTO>> map, String what, Integer maxIterations) {
        if ((maxIterations == null) || (maxIterations < 1)) {
            maxIterations = map.size();
        }

        System.out.println(what);

        Iterator<Entry<String, List<SangerImageDTO>>> it = map.entrySet().iterator();
        while ((it.hasNext()) && (maxIterations -- > 0)) {
            Entry<String, List<SangerImageDTO>> entry = it.next();
            System.out.println("KEY: " + entry.getKey());
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

                System.out.println("\tlegacy_phenotype_status:\t" + dto.getLegacyPhenotypeStatus());
                printItemList("latest_production_centre:", dto.getLatestProductionCentre());
                printItemList("latest_phenotyping_centre:", dto.getLatestPhenotypingCentre());

                printItemList("allele_name:", dto.getAlleleName());
                System.out.println();
            }

            System.out.println();
        }
    }

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

    public static Map<String, AlleleDTO> getAlleles(SolrServer alleleCore) throws SolrServerException {
        Map<String, AlleleDTO> alleles = new HashMap<>();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("*:*");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = alleleCore.query(query);
            total = response.getResults().getNumFound();
            List<AlleleDTO> alleleList = response.getBeans(AlleleDTO.class);
            for (AlleleDTO allele : alleleList) {
                alleles.put(allele.getMgiAccessionId(), allele);
            }
            pos += BATCH_SIZE;
        }
        logger.debug("Loaded {} alleles", alleles.size());

        return alleles;
    }

    public static Map<String, Map<String, String>> populateMpToHpTermsMap(SolrServer phenodigm_core)
            throws SolrServerException {

		// url="q=mp_id:&quot;${nodeIds.term_id}&quot;&amp;rows=999&amp;fq=type:mp_hp&amp;fl=hp_id,hp_term"
        // processor="XPathEntityProcessor" >
        //
        // <field column="hp_id" xpath="/response/result/doc/str[@name='hp_id']"
        // />
        // <field column="hp_term"
        // xpath="/response/result/doc/str[@name='hp_term']" />
        Map<String, Map<String, String>> mpToHp = new HashMap<>();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("mp_id:*");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = phenodigm_core.query(query);
            total = response.getResults().getNumFound();
            SolrDocumentList solrDocs = response.getResults();
            for (SolrDocument doc : solrDocs) {
                if (doc.containsKey("hp_id")) {
                    String hp = (String) doc.get("hp_id");
                    if (doc.containsKey("mp_id")) {
                        String mp = (String) doc.get("mp_id");
                        Map<String, String> entryMap = new HashMap<>();
                        entryMap.put("hp_id", hp);
                        if (doc.containsKey("hp_term")) {
                            String hpTerm = (String) doc.get("hp_term");
                            entryMap.put("hp_term", hpTerm);
                        }
                        mpToHp.put(mp, entryMap);
                    }
                }

			}
			pos += BATCH_SIZE;
		}
		System.out.println("mpToHp map size="+mpToHp.size());
		return mpToHp;
	}

}
