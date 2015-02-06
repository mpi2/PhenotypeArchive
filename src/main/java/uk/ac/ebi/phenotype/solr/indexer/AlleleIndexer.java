/**
 * Copyright (c) 2014 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.DiseaseBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.SangerAlleleBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.SangerGeneBean;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Index the allele core from the sanger allele2 core
 *
 * @author Matt
 * @author jmason
 *
 */
public class AlleleIndexer extends AbstractIndexer {

    private static final Logger logger = LoggerFactory.getLogger(AlleleIndexer.class);
    public static final int PHENODIGM_BATCH_SIZE = 50000;
    private static Connection connection;

    private static final int BATCH_SIZE = 2500;

    // Map gene MGI ID to sanger allele bean
    private static Map<String, List<SangerAlleleBean>> statusLookup = new HashMap<>();

    // Map gene MGI ID to human symbols
    private static Map<String, Set<String>> humanSymbolLookup = new HashMap<>();

    // Map gene MGI ID to disease bean
    private static Map<String, List<DiseaseBean>> diseaseLookup = new HashMap<>();

    // Set of MGI IDs that have legacy projects
    private static Map<String, Integer> legacyProjectLookup = new HashMap<>();

    private static final Map<String, String> ES_CELL_STATUS_MAPPINGS = new HashMap<>();

    // Set of MGI IDs that have GO annotation(s)
    private static Map<String, Set<GoAnnotations>> goTermLookup = new HashMap<>();

    static {
        ES_CELL_STATUS_MAPPINGS.put("No ES Cell Production", "Not Assigned for ES Cell Production");
        ES_CELL_STATUS_MAPPINGS.put("ES Cell Production in Progress", "Assigned for ES Cell Production");
        ES_CELL_STATUS_MAPPINGS.put("ES Cell Targeting Confirmed", "ES Cells Produced");
    }

    private static final Map<String, String> MOUSE_STATUS_MAPPINGS = new HashMap<>();

    static {
        MOUSE_STATUS_MAPPINGS.put("Chimeras obtained", "Assigned for Mouse Production and Phenotyping");
        MOUSE_STATUS_MAPPINGS.put("Micro-injection in progress", "Assigned for Mouse Production and Phenotyping");
        MOUSE_STATUS_MAPPINGS.put("Cre Excision Started", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Rederivation Complete", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Rederivation Started", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Genotype confirmed", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Cre Excision Complete", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Phenotype Attempt Registered", "Mice Produced");
    }

    private SolrServer sangerAlleleCore;
    private SolrServer phenodigmCore;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @Autowired
    @Qualifier("goaproDataSource")
    DataSource goaproDataSource;
    
    @Autowired
    @Qualifier("alleleIndexing")
    private SolrServer alleleCore;

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    public AlleleIndexer() {

    }

    @Override
    public void validateBuild() throws IndexerException {
        Long numFound = getDocumentCount(alleleCore);
        
        if (numFound <= MINIMUM_DOCUMENT_COUNT)
            throw new IndexerException(new ValidationException("Actual allele document count is " + numFound + "."));
        
        if (numFound != documentCount)
            logger.warn("WARNING: Added " + documentCount + " allele documents but SOLR reports " + numFound + " documents.");
        else
            logger.info("validateBuild(): Indexed " + documentCount + " allele documents.");
    }

    @Override
    protected Logger getLogger() {

        return logger;
    }

    @Override
    public void run() throws IndexerException {

        int start = 0;
        long rows = 0;
        long startTime = new Date().getTime();

        try {
            connection = komp2DataSource.getConnection();

            initializeSolrCores();

            SolrQuery query = new SolrQuery("mgi_accession_id:*");
            query.addFilterQuery("feature_type:* AND -feature_type:Pseudogene AND -feature_type:\"heritable+phenotypic+marker\" AND type:gene");
            query.setRows(BATCH_SIZE);

            logger.info("Populating lookups");

            populateStatusLookup();
            logger.info("Populated status lookup, {} records", statusLookup.size());

            populateHumanSymbolLookup();
            logger.info("Populated human symbol lookup, {} records", humanSymbolLookup.size());

            populateDiseaseLookup();
            logger.info("Populated disease lookup, {} records", diseaseLookup.size());

            populateLegacyLookup();
            logger.info("Populated legacy project lookup, {} records", legacyProjectLookup.size());

            // GoTerm from Ensembl Biomart: MGI gene id to GO term mapping
            populateGoTermLookup();
            logger.info("Populated go terms lookup, {} records", goTermLookup.size());

            alleleCore.deleteByQuery("*:*");
            alleleCore.commit();

            while (start <= rows) {
                query.setStart(start);
                QueryResponse response = sangerAlleleCore.query(query);
                rows = response.getResults().getNumFound();
                List<SangerGeneBean> sangerGenes = response.getBeans(SangerGeneBean.class);

                // Convert to Allele DTOs
                Map<String, AlleleDTO> alleles = convertSangerGeneBeans(sangerGenes);

                // Look up the marker synonyms
                lookupMarkerSynonyms(alleles);

                // Look up the human mouse symbols
                lookupHumanMouseSymbols(alleles);

                // Look up the ES cell status
                lookupEsCellStatus(alleles);

                // Look up the disease data
                lookupDiseaseData(alleles);

                // Look uup the GO Term data
                lookupGoData(alleles);

                // Now index the alleles
                documentCount += alleles.size();
                indexAlleles(alleles);

                start += BATCH_SIZE;

                logger.info("Indexed {} records", start);

            }

            alleleCore.commit();

        } catch (SQLException | SolrServerException | IOException | ClassNotFoundException e) {
            throw new IndexerException(e);
        }

        logger.debug("Complete - took {}ms", (new Date().getTime() - startTime));
    }

    private void initializeSolrCores() {

        final String SANGER_ALLELE_URL = config.get("imits.solrserver");
        final String PHENODIGM_URL = config.get("phenodigm.solrserver");

        // Use system proxy if set for external solr servers
        if (System.getProperty("externalProxyHost") != null && System.getProperty("externalProxyPort") != null) {

            String PROXY_HOST = System.getProperty("externalProxyHost");
            Integer PROXY_PORT = Integer.parseInt(System.getProperty("externalProxyPort"));

            HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();

            logger.info("Using Proxy Settings: " + PROXY_HOST + " on port: " + PROXY_PORT);

            this.sangerAlleleCore = new HttpSolrServer(SANGER_ALLELE_URL, client);
            this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL, client);

        } else {

            this.sangerAlleleCore = new HttpSolrServer(SANGER_ALLELE_URL);
            this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL);

        }
    }

    public class GoAnnotations {

        public String goTermId;
        public String goTermName;
        public String goTermDef;    // not populated for now
        public String goTermEvid; 	
        public String goTermDomain;   
		public String mgiSymbol;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            GoAnnotations that = (GoAnnotations) o;

            if (goTermDomain != null ?  ! goTermDomain.equals(that.goTermDomain) : that.goTermDomain != null) {
                return false;
            }
            /*if (goTermDef != null ?  ! goTermDef.equals(that.goTermDef) : that.goTermDef != null) {
                return false;
            }*/
            if (goTermEvid != null ?  ! goTermEvid.equals(that.goTermEvid) : that.goTermEvid != null) {
                return false;
            }
            if (goTermId != null ?  ! goTermId.equals(that.goTermId) : that.goTermId != null) {
                return false;
            }
            if (goTermName != null ?  ! goTermName.equals(that.goTermName) : that.goTermName != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = goTermId != null ? goTermId.hashCode() : 0;
            result = 31 * result + (goTermName != null ? goTermName.hashCode() : 0);
           // result = 31 * result + (goTermDef != null ? goTermDef.hashCode() : 0);
            result = 31 * result + (goTermEvid != null ? goTermEvid.hashCode() : 0);
            result = 31 * result + (goTermDomain != null ? goTermDomain.hashCode() : 0);
            return result;
        }
    }
    
    private void populateGoTermLookup() throws IOException, SQLException, ClassNotFoundException {
		
	    String queryString = "select distinct m.gene_name, a.go_id, t.name as go_name, t.category as go_domain, evi.go_evidence "
	    		+ "from go.eco2evidence evi "
	    		+ "join "
	    		+ "go.v_manual_annotations a on (evi.eco_id = a.eco_id) "
	    		+ "join " 
	    		+ "go.terms t on (t.go_id = a.go_id) "
	    		+ "join "  
	    		+ "go.uniprot_protein_metadata m on (m.accession = a.canonical_id) "  
	    		+ "join "
	    		+ "go.terms t on (t.go_id = a.go_id) "
	    		+ "where "
	    		+ "gene_name is not null "
	    		+ "and a.is_public = 'Y' "
	    		+ "and m.tax_id = 10090 "
	    		+ "and evi.go_evidence in ('EXP', 'IDA', 'IPI', 'IMP', 'IGI', 'ISO', 'ISS') "
	    		+ "and t.category in ('F', 'P') "
	    		+ "and a.source in ('MGI','GOC')";
	    
	    Connection conn = goaproDataSource.getConnection();

	    try (PreparedStatement p = conn.prepareStatement(queryString)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
            	
            	GoAnnotations ga = new GoAnnotations();
            	
    			ga.mgiSymbol  = resultSet.getString("gene_name");
    			ga.goTermId   = resultSet.getString("go_id");
    			ga.goTermName = resultSet.getString("go_name");
    			ga.goTermEvid = resultSet.getString("go_evidence");
    			ga.goTermDomain = resultSet.getString("go_domain").toString().equals("F") ? "molecular_function" : "biological_process";

            	if ( ! goTermLookup.containsKey(ga.mgiSymbol)) {
            		goTermLookup.put(ga.mgiSymbol, new HashSet<GoAnnotations>());
            	}
            	
    			goTermLookup.get(ga.mgiSymbol).add(ga);
            }
            
            System.out.println(goTermLookup.size());
            logger.info("Populated goTerm lookup, {} records", goTermLookup.size());
        } 
	    catch (Exception e) {
            e.printStackTrace();
        }
	}

    private void populateLegacyLookup() throws SolrServerException {

        String query = "SELECT DISTINCT project_id, gf_acc FROM phenotype_call_summary WHERE p_value < 0.0001 AND (project_id = 1 OR project_id = 8)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                legacyProjectLookup.put(rs.getString("gf_acc"), 1);

            }
        } catch (SQLException e) {
            logger.error("SQL Exception looking up legacy projects: {}", e.getMessage());
        }

    }

    private void populateStatusLookup() throws SolrServerException {

        SolrQuery query = new SolrQuery("*:*");
        query.setRows(Integer.MAX_VALUE);
        query.addFilterQuery("type:allele");

        QueryResponse response = sangerAlleleCore.query(query);
        List<SangerAlleleBean> sangerAlleles = response.getBeans(SangerAlleleBean.class);
        for (SangerAlleleBean allele : sangerAlleles) {
            if ( ! statusLookup.containsKey(allele.getMgiAccessionId())) {
                statusLookup.put(allele.getMgiAccessionId(), new ArrayList<SangerAlleleBean>());
            }
            statusLookup.get(allele.getMgiAccessionId()).add(allele);
        }
    }

    private void populateHumanSymbolLookup() throws IOException {

        File file = new File(config.get("human2mouseFilename"));
        List<String> lines = FileUtils.readLines(file, "UTF-8");

        for (String line : lines) {
            String[] pieces = line.trim().split("\t");

            if (pieces.length < 5) {
                continue;
            }

            String humanSymbol = pieces[0];
            String mgiId = pieces[4].trim();
            if ( ! mgiId.startsWith("MGI:")) {
                continue;
            }

            if ( ! humanSymbolLookup.containsKey(mgiId)) {
                humanSymbolLookup.put(mgiId, new HashSet<String>());
            }

            (humanSymbolLookup.get(mgiId)).add(humanSymbol);

        }

    }

    private void populateDiseaseLookup() throws SolrServerException {

        int docsRetrieved = 0;
        int numDocs = getDiseaseDocCount();

        // Fields in the solr core to bring back
        String fields = StringUtils.join(Arrays.asList(DiseaseBean.DISEASE_ID,
                                                       DiseaseBean.MGI_ACCESSION_ID,
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

		// The solrcloud instance cannot give us all results back at once,
        // we must batch up the calls and build it up piece at a time
        while (docsRetrieved < numDocs + PHENODIGM_BATCH_SIZE) {

            SolrQuery query = new SolrQuery("*:*");
            query.addFilterQuery("type:disease_gene_summary");
            query.setFields(fields);
            query.setStart(docsRetrieved);
            query.setRows(PHENODIGM_BATCH_SIZE);
            query.setSort(DiseaseBean.DISEASE_ID, SolrQuery.ORDER.asc);

            QueryResponse response = phenodigmCore.query(query);
            List<DiseaseBean> diseases = response.getBeans(DiseaseBean.class);
            for (DiseaseBean disease : diseases) {
                if ( ! diseaseLookup.containsKey(disease.getMgiAccessionId())) {
                    diseaseLookup.put(disease.getMgiAccessionId(), new ArrayList<DiseaseBean>());
                }
                diseaseLookup.get(disease.getMgiAccessionId()).add(disease);
            }

            docsRetrieved += PHENODIGM_BATCH_SIZE;
            logger.info("Processed {} documents from phenodigm. {} genes in the index", docsRetrieved, diseaseLookup.size());

        }
    }

    private int getDiseaseDocCount() throws SolrServerException {

        SolrQuery query = new SolrQuery("*:*");
        query.setRows(0);
        query.addFilterQuery("type:disease_gene_summary");

        QueryResponse response = phenodigmCore.query(query);
        return (int) response.getResults().getNumFound();
    }

    private Map<String, AlleleDTO> convertSangerGeneBeans(List<SangerGeneBean> beans) {

        Map<String, AlleleDTO> map = new HashMap<>(beans.size());

        for (SangerGeneBean bean : beans) {
            String id = bean.getMgiAccessionId();
            AlleleDTO dto = new AlleleDTO();

            // Copy the fields
            dto.setMgiAccessionId(id);
            dto.setMarkerType(bean.getFeatureType());
            dto.setMarkerSymbol(bean.getMarkerSymbol());
            dto.setGeneLatestEsCellStatus(bean.getLatestEsCellStatus());
            dto.setGeneLatestMouseStatus(bean.getLatestMouseStatus());
            dto.setImitsPhenotypeStarted(bean.getLatestPhenotypeStarted());
            dto.setImitsPhenotypeComplete(bean.getLatestPhenotypeComplete());
            dto.setLatestPhenotypeStatus(bean.getLatestPhenotypeStatus());
            dto.setLatestProductionCentre(bean.getLatestProductionCentre());
            dto.setLatestPhenotypingCentre(bean.getLatestPhenotypingCentre());
            dto.setLatestProjectStatus(bean.getLatestProjectStatus());

            String latestEsStatus = ES_CELL_STATUS_MAPPINGS.containsKey(bean.getLatestEsCellStatus()) ? ES_CELL_STATUS_MAPPINGS.get(bean.getLatestEsCellStatus()) : bean.getLatestEsCellStatus();
            dto.setLatestProductionStatus(latestEsStatus);
            dto.setLatestEsCellStatus(latestEsStatus);

            if (StringUtils.isNotEmpty(bean.getLatestMouseStatus())) {
                String latestMouseStatus = MOUSE_STATUS_MAPPINGS.containsKey(bean.getLatestMouseStatus()) ? MOUSE_STATUS_MAPPINGS.get(bean.getLatestMouseStatus()) : bean.getLatestMouseStatus();
                dto.setLatestProductionStatus(latestMouseStatus);
                dto.setLatestMouseStatus(latestMouseStatus);
            }

            if (legacyProjectLookup.containsKey(bean.getMgiAccessionId())) {
                dto.setLegacyPhenotypeStatus(1);
            }

            // Do the additional mappings
            dto.setDataType(AlleleDTO.ALLELE_DATA_TYPE);

            map.put(id, dto);
        }

        return map;
    }

    private void lookupMarkerSynonyms(Map<String, AlleleDTO> alleles) {
        // Build the lookup string
        String lookup = buildIdQuery(alleles.keySet());

        String query = "select s.acc as id, s.symbol as marker_synonym, gf.name as marker_name "
                + "from synonym s, genomic_feature gf "
                + "where s.acc=gf.acc "
                + "and gf.acc IN (" + lookup + ")";
        try {
            logger.debug("Starting marker synonym lookup");
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                AlleleDTO allele = alleles.get(id);
                if (allele.getMarkerSynonym() == null) {
                    allele.setMarkerSynonym(new ArrayList<String>());
                }
                allele.getMarkerSynonym().add(rs.getString("marker_synonym"));
                allele.setMarkerName(rs.getString("marker_name"));
            }
            logger.debug("Finished marker synonym lookup");
        } catch (SQLException sqle) {
            logger.error("SQL Exception looking up marker symbols: {}", sqle.getMessage());
        }
    }

    private void lookupHumanMouseSymbols(Map<String, AlleleDTO> alleles) {

        for (String id : alleles.keySet()) {
            AlleleDTO dto = alleles.get(id);

            if (humanSymbolLookup.containsKey(id)) {
                dto.setHumanGeneSymbol(new ArrayList<>(humanSymbolLookup.get(id)));
            }

        }

        logger.debug("Finished human marker symbol lookup");
    }

    private String buildIdQuery(Collection<String> ids) {

        StringBuilder lookup = new StringBuilder();
        int i = 0;
        for (String id : ids) {
            if (i > 0) {
                lookup.append(",");
            }
            lookup.append("'").append(id).append("'");
            i ++;
        }
        return lookup.toString();
    }

    private void lookupEsCellStatus(Map<String, AlleleDTO> alleles) {

        for (String id : alleles.keySet()) {
            AlleleDTO dto = alleles.get(id);

            if ( ! statusLookup.containsKey(id)) {
                continue;
            }

            for (SangerAlleleBean sab : statusLookup.get(id)) {

                dto.getAlleleName().add(sab.getAlleleName());
                dto.getPhenotypeStatus().add(sab.getPhenotypeStatus());
                dto.getProductionCentre().add(sab.getProductionCentre());
                dto.getPhenotypingCentre().add(sab.getPhenotypingCentre());

                String esCellStat = ES_CELL_STATUS_MAPPINGS.containsKey(sab.getEsCellStatus()) ? ES_CELL_STATUS_MAPPINGS.get(sab.getEsCellStatus()) : sab.getEsCellStatus();
                dto.getEsCellStatus().add(esCellStat);

                if (StringUtils.isNotEmpty(sab.getMouseStatus())) {
                    String mouseStatus = MOUSE_STATUS_MAPPINGS.containsKey(sab.getMouseStatus()) ? MOUSE_STATUS_MAPPINGS.get(sab.getMouseStatus()) : sab.getMouseStatus();
                    dto.getMouseStatus().add(mouseStatus);
                } else {
                    dto.getMouseStatus().add("");
                }

//				dto.setImitsEsCellStatus(sab.getEsCellStatus());
//				dto.setImitsMouseStatus(sab.getMouseStatus());
            }
        }

        logger.debug("Finished ES cell status lookup");
    }

    private void lookupDiseaseData(Map<String, AlleleDTO> alleles) {

        logger.debug("Starting disease data lookup");
        for (String id : alleles.keySet()) {

            AlleleDTO dto = alleles.get(id);

            if ( ! diseaseLookup.containsKey(id)) {
                continue;
            }

            for (DiseaseBean db : diseaseLookup.get(id)) {
                dto.getDiseaseId().add(db.getDiseaseId());
                dto.getDiseaseSource().add(db.getDiseaseSource());
                dto.getDiseaseTerm().add(db.getDiseaseTerm());
                if (db.getDiseaseAlts() != null) {
                    dto.getDiseaseAlts().addAll(db.getDiseaseAlts());
                }
                if (db.getDiseaseClasses() != null) {
                    dto.getDiseaseClasses().addAll(db.getDiseaseClasses());
                }
                dto.getHumanCurated().add(db.isHumanCurated());
                dto.getMouseCurated().add(db.isMouseCurated());
                dto.getMgiPredicted().add(db.isMgiPredicted());
                dto.getImpcPredicted().add(db.isImpcPredicted());
                dto.getMgiPredictedKnownGene().add(db.isMgiPredictedKnownGene());
                dto.getImpcPredictedKnownGene().add(db.isImpcPredictedKnownGene());
                dto.getMgiNovelPredictedInLocus().add(db.isMgiNovelPredictedInLocus());
                dto.getImpcNovelPredictedInLocus().add(db.isImpcNovelPredictedInLocus());
            }

        }
        logger.debug("Finished disease data lookup");
    }

    private void lookupGoData(Map<String, AlleleDTO> alleles) {
        logger.debug("Starting GO data lookup");

        for (String id : alleles.keySet()) {

            AlleleDTO dto = alleles.get(id);

            // use GO is populated based on gene symbol
            if ( ! goTermLookup.containsKey(dto.getMarkerSymbol())) {
                continue;
            }

            for (GoAnnotations ga : goTermLookup.get(dto.getMarkerSymbol())) {
                dto.getGoTermIds().add(ga.goTermId);
                dto.getGoTermNames().add(ga.goTermName);
                //dto.getGoTermDefs().add(ga.goTermDef);
                dto.getGoTermEvids().add(ga.goTermEvid);
                dto.getGoTermDomains().add(ga.goTermDomain);
            }
        }
    }

    private void indexAlleles(Map<String, AlleleDTO> alleles) throws SolrServerException, IOException {

        alleleCore.addBeans(alleles.values(), 60000);
    }

    public static void main(String[] args) throws IndexerException {

        AlleleIndexer main = new AlleleIndexer();
        main.initialise(args);
        main.run();
        main.validateBuild();

        logger.info("Process finished.  Exiting.");
    }

}
