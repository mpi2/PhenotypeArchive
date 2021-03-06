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
package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.phenotype.service.dto.DiseaseDTO;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.DiseaseBean;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.ValidationException;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author Jeremy
 */
public class DiseaseIndexer extends AbstractIndexer {

    @Autowired
    @Qualifier("geneIndexing")
    private SolrServer geneCore;

    @Autowired
    @Qualifier("diseaseIndexing")
    private SolrServer diseaseCore;

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    private static final Logger logger = LoggerFactory.getLogger(DiseaseIndexer.class);
    public static final int MAX_DISEASES = 10000;

    // Map disease ID to list of gene data objects
    private static Map<String, GeneData> geneLookup = new HashMap<>();

    private SolrServer phenodigmCore;

    public DiseaseIndexer() {

    }

    @Override
    public void validateBuild() throws IndexerException {
        Long numFound = getDocumentCount(diseaseCore);
        
        if (numFound <= MINIMUM_DOCUMENT_COUNT)
            throw new IndexerException(new ValidationException("Actual disease document count is " + numFound + "."));
        
        if (numFound != documentCount)
            logger.warn("WARNING: Added " + documentCount + " disease documents but SOLR reports " + numFound + " documents.");
        else
            logger.info("validateBuild(): Indexed " + documentCount + " disease documents.");
    }

    @Override
    public void run() throws IndexerException {

        long startTime = new Date().getTime();

        try {

            initializeSolrCores();

            logger.info("Populating lookups");
            populateGenesLookup();
            logger.info("Populated genes lookup, {} records", geneLookup.size());

            logger.info("Removing existing documents from index");
            diseaseCore.deleteByQuery("*:*");
            diseaseCore.commit();

            // Fields from the phenodigm core to bring back
            String fields = StringUtils.join(Arrays.asList(DiseaseBean.DISEASE_ID,
                                                           DiseaseBean.TYPE,
                                                           DiseaseBean.DISEASE_SOURCE,
                                                           DiseaseBean.DISEASE_TERM,
                                                           DiseaseBean.DISEASE_ALTS,
                                                           DiseaseBean.DISEASE_CLASSES,
                                                           DiseaseBean.HUMAN_CURATED,
                                                           DiseaseBean.MOUSE_CURATED,
                                                           DiseaseBean.MGI_PREDICTED,
                                                           DiseaseBean.IMPC_PREDICTED,
                                                           DiseaseBean.MGI_PREDICTED_KNOWN_GENE,
                                                           DiseaseBean.IMPC_PREDICTED_KNOWN_GENE,
                                                           DiseaseBean.MGI_NOVEL_PREDICTED_IN_LOCUS,
                                                           DiseaseBean.IMPC_NOVEL_PREDICTED_IN_LOCUS), ",");

            logger.info("Querying external PhenoDigm index");
            SolrQuery query = new SolrQuery("*:*");
            query.addFilterQuery("type:disease");
            query.setFields(fields);
            query.setRows(MAX_DISEASES);
            List<DiseaseBean> diseases = phenodigmCore.query(query).getBeans(DiseaseBean.class);

            int count = 0;
            for (DiseaseBean phenDisease : diseases) {

                DiseaseDTO disease = new DiseaseDTO();

                // Populate Phenodigm data
                disease.setDataType(phenDisease.getType());
                disease.setType(phenDisease.getType());
                disease.setDiseaseId(phenDisease.getDiseaseId());
                disease.setDiseaseSource(phenDisease.getDiseaseSource());
                disease.setDiseaseTerm(phenDisease.getDiseaseTerm());
                disease.setDiseaseAlts(phenDisease.getDiseaseAlts());
                disease.setDiseaseClasses(phenDisease.getDiseaseClasses());
                disease.setHumanCurated(phenDisease.isHumanCurated());
                disease.setMouseCurated(phenDisease.isMouseCurated());
                disease.setMgiPredicted(phenDisease.isMgiPredicted());
                disease.setImpcPredicted(phenDisease.isImpcPredicted());
                disease.setMgiPredictedKnownGene(phenDisease.isMgiPredictedKnownGene());
                disease.setImpcPredictedKnownGene(phenDisease.isImpcPredictedKnownGene());
                disease.setMgiNovelPredictedInLocus(phenDisease.isMgiNovelPredictedInLocus());
                disease.setImpcNovelPredictedInLocus(phenDisease.isImpcNovelPredictedInLocus());

                // Populate gene data
                GeneData gene = geneLookup.get(phenDisease.getDiseaseId());

                if (gene != null) {
                    disease.setMgiAccessionId(gene.MGI_ACCESSION_ID);
                    disease.setMarkerSymbol(gene.MARKER_SYMBOL);
                    disease.setMarkerName(gene.MARKER_NAME);
                    disease.setMarkerSynonym(gene.MARKER_SYNONYM);
                    disease.setMarkerType(gene.MARKER_TYPE);
                    disease.setHumanGeneSymbol(gene.HUMAN_GENE_SYMBOL);
                    disease.setStatus(gene.STATUS);
                    disease.setLatestProductionCentre(gene.LATEST_PRODUCTION_CENTRE);
                    disease.setLatestPhenotypingCentre(gene.LATEST_PHENOTYPING_CENTRE);
                    disease.setLatestPhenotypeStatus(gene.LATEST_PHENOTYPE_STATUS);
                    disease.setLegacyPhenotypeStatus(gene.LEGACY_PHENOTYPE_STATUS);
                    disease.setAlleleName(gene.ALLELE_NAME);
                    disease.setMpId(gene.MP_ID);
                    disease.setMpTerm(gene.MP_TERM);
                    disease.setMpTermDefinition(gene.MP_DEFINITION);
                    disease.setMpTermSynonym(gene.MP_SYNONYM);
                    disease.setTopLevelMpId(gene.TOP_LEVEL_MP_ID);
                    disease.setTopLevelMpTerm(gene.TOP_LEVEL_MP_TERM);
                    disease.setTopLevelMpTermSynonym(gene.TOP_LEVEL_MP_TERM_SYNONYM);
                    disease.setIntermediateMpId(gene.INTERMEDIATE_MP_ID);
                    disease.setIntermediateMpTerm(gene.INTERMEDIATE_MP_TERM);
                    disease.setIntermediateMpTermSynonym(gene.INTERMEDIATE_MP_TERM_SYNONYM);
                    disease.setChildMpId(gene.CHILD_MP_ID);
                    disease.setChildMpTerm(gene.CHILD_MP_TERM);
                    disease.setChildMpTermSynonym(gene.CHILD_MP_TERM_SYNONYM);
                    disease.setOntologySubset(gene.ONTOLOGY_SUBSET);

                }
                documentCount++;
                diseaseCore.addBean(disease, 60000);

                count ++;
            }

            logger.info("Indexed {} records", count);

            diseaseCore.commit();

        } catch (SolrServerException | IOException e) {

            // Catch and rethrow exception
            throw new IndexerException(e);

        }

        logger.debug("Complete - took {}ms", (new Date().getTime() - startTime));
    }

    /**
     * Initialize the phenodigm core -- using a proxy if configured.
     * <p/>
     * A proxy is specified by supplying two JVM variables - externalProxyHost
     * the host (not including the protocol) - externalProxyPort the integer
     * port number
     */
    private void initializeSolrCores() {

        final String PHENODIGM_URL = config.get("phenodigm.solrserver");

        // Use system proxy if set for external solr servers
        if (System.getProperty("externalProxyHost") != null && System.getProperty("externalProxyPort") != null) {

            String PROXY_HOST = System.getProperty("externalProxyHost");
            Integer PROXY_PORT = Integer.parseInt(System.getProperty("externalProxyPort"));

            HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();

            logger.info("Using Proxy Settings: " + PROXY_HOST + " on port: " + PROXY_PORT);

            this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL, client);

        } else {

            this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL);

        }
    }

    /**
     * Create lookup using disease ID as keys, and a GeneData structure that
     * contains the relevant gene information.
     * <p/>
     * Populates the geneLookup map
     *
     * @throws SolrServerException when solr does something unexpected
     */
    private void populateGenesLookup() throws SolrServerException {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.setRows(Integer.MAX_VALUE);
        List<GeneDTO> genes = geneCore.query(solrQuery).getBeans(GeneDTO.class);

        for (GeneDTO gene : genes) {
            if (gene.getDiseaseId() != null) {
                for (String d : gene.getDiseaseId()) {

                    if ( ! geneLookup.containsKey(d)) {
                        geneLookup.put(d, new GeneData());
                    }

                    if (gene.getMgiAccessionId() != null) {
                        geneLookup.get(d).MGI_ACCESSION_ID.add(gene.getMgiAccessionId());
                    }
                    if (gene.getMarkerSymbol() != null) {
                        geneLookup.get(d).MARKER_SYMBOL.add(gene.getMarkerSymbol());
                    }
                    if (gene.getMarkerName() != null) {
                        geneLookup.get(d).MARKER_NAME.add(gene.getMarkerName());
                    }
                    if (gene.getMarkerSynonym() != null) {
                        geneLookup.get(d).MARKER_SYNONYM.addAll(gene.getMarkerSynonym());
                    }
                    if (gene.getMarkerType() != null) {
                        geneLookup.get(d).MARKER_TYPE.add(gene.getMarkerType());
                    }
                    if (gene.getHumanGeneSymbol() != null) {
                        geneLookup.get(d).HUMAN_GENE_SYMBOL.addAll(gene.getHumanGeneSymbol());
                    }
                    if (gene.getStatus() != null) {
                        geneLookup.get(d).STATUS.add(gene.getStatus());
                    }
                    if (gene.getLatestProductionCentre() != null) {
                        geneLookup.get(d).LATEST_PRODUCTION_CENTRE.addAll(gene.getLatestProductionCentre());
                    }
                    if (gene.getLatestPhenotypingCentre() != null) {
                        geneLookup.get(d).LATEST_PHENOTYPING_CENTRE.addAll(gene.getLatestPhenotypingCentre());
                    }
                    if (gene.getLatestPhenotypeStatus() != null) {
                        geneLookup.get(d).LATEST_PHENOTYPE_STATUS.add(gene.getLatestPhenotypeStatus());
                    }
                    if (gene.getLegacy_phenotype_status() != null) {
                        geneLookup.get(d).LEGACY_PHENOTYPE_STATUS.add(gene.getLegacy_phenotype_status());
                    }
                    if (gene.getAlleleName() != null) {
                        geneLookup.get(d).ALLELE_NAME.addAll(gene.getAlleleName());
                    }
                    if (gene.getMpId() != null) {
                        geneLookup.get(d).MP_ID.addAll(gene.getMpId());
                    }
                    if (gene.getMpTerm() != null) {
                        geneLookup.get(d).MP_TERM.addAll(gene.getMpTerm());
                    }
                    if (gene.getMpTermDefinition() != null) {
                        geneLookup.get(d).MP_DEFINITION.addAll(gene.getMpTermDefinition());
                    }
                    if (gene.getTopLevelMpId() != null) {
                        geneLookup.get(d).TOP_LEVEL_MP_ID.addAll(gene.getTopLevelMpId());
                    }
                    if (gene.getTopLevelMpTerm() != null) {
                        geneLookup.get(d).TOP_LEVEL_MP_TERM.addAll(gene.getTopLevelMpTerm());
                    }
                    if (gene.getTopLevelMpTermSynonym() != null) {
                        geneLookup.get(d).TOP_LEVEL_MP_TERM_SYNONYM.addAll(gene.getTopLevelMpTermSynonym());
                    }
                    if (gene.getIntermediateMpId() != null) {
                        geneLookup.get(d).INTERMEDIATE_MP_ID.addAll(gene.getIntermediateMpId());
                    }
                    if (gene.getIntermediateMpTerm() != null) {
                        geneLookup.get(d).INTERMEDIATE_MP_TERM.addAll(gene.getIntermediateMpTerm());
                    }
                    if (gene.getIntermediateMpTermSynonym() != null) {
                        geneLookup.get(d).INTERMEDIATE_MP_TERM_SYNONYM.addAll(gene.getIntermediateMpTermSynonym());
                    }
                    if (gene.getChildMpId() != null) {
                        geneLookup.get(d).CHILD_MP_ID.addAll(gene.getChildMpId());
                    }
                    if (gene.getChildMpTerm() != null) {
                        geneLookup.get(d).CHILD_MP_TERM.addAll(gene.getChildMpTerm());
                    }
                    if (gene.getChildMpTermSynonym() != null) {
                        geneLookup.get(d).CHILD_MP_TERM_SYNONYM.addAll(gene.getChildMpTermSynonym());
                    }
                    if (gene.getOntologySubset() != null) {
                        geneLookup.get(d).ONTOLOGY_SUBSET.addAll(gene.getOntologySubset());
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IndexerException {

        DiseaseIndexer main = new DiseaseIndexer();
        main.initialise(args);
        main.run();
        main.validateBuild();

        logger.info("Process finished.  Exiting.");
    }

    @Override
    protected Logger getLogger() {

        return logger;
    }


    /*
     Internal class which collects the gene information per disease
     Uses sets to prevent duplicates since the frontend will only
     filter if a disease is associated to particular search facet
     dimensions (in other words we don't need to maintain the
     correlation between the sub gene information)
     */
    private class GeneData {

        Set<String> MGI_ACCESSION_ID = new HashSet<>();
        Set<String> MARKER_SYMBOL = new HashSet<>();
        Set<String> MARKER_NAME = new HashSet<>();
        Set<String> MARKER_SYNONYM = new HashSet<>();
        Set<String> MARKER_TYPE = new HashSet<>();
        Set<String> HUMAN_GENE_SYMBOL = new HashSet<>();
        Set<String> STATUS = new HashSet<>();
        Set<String> LATEST_PRODUCTION_CENTRE = new HashSet<>();
        Set<String> LATEST_PHENOTYPING_CENTRE = new HashSet<>();
        Set<String> LATEST_PHENOTYPE_STATUS = new HashSet<>();
        Set<Integer> LEGACY_PHENOTYPE_STATUS = new HashSet<>();
        Set<String> ALLELE_NAME = new HashSet<>();
        Set<String> MP_ID = new HashSet<>();
        Set<String> MP_TERM = new HashSet<>();
        Set<String> MP_DEFINITION = new HashSet<>();
        Set<String> MP_SYNONYM = new HashSet<>();
        Set<String> TOP_LEVEL_MP_ID = new HashSet<>();
        Set<String> TOP_LEVEL_MP_TERM = new HashSet<>();
        Set<String> TOP_LEVEL_MP_TERM_SYNONYM = new HashSet<>();
        Set<String> INTERMEDIATE_MP_ID = new HashSet<>();
        Set<String> INTERMEDIATE_MP_TERM = new HashSet<>();
        Set<String> INTERMEDIATE_MP_TERM_SYNONYM = new HashSet<>();
        Set<String> CHILD_MP_ID = new HashSet<>();
        Set<String> CHILD_MP_TERM = new HashSet<>();
        Set<String> CHILD_MP_TERM_SYNONYM = new HashSet<>();
        Set<String> ONTOLOGY_SUBSET = new HashSet<>();
    }
}
