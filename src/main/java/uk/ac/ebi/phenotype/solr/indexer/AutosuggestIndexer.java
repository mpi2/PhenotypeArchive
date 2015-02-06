
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
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.phenotype.service.dto.*;
import uk.ac.ebi.phenotype.solr.indexer.beans.AutosuggestBean;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;


public class AutosuggestIndexer extends AbstractIndexer {

    private static final Logger logger = LoggerFactory.getLogger(AutosuggestIndexer.class);

    @Autowired
    @Qualifier("autosuggestIndexing")
    private SolrServer autosuggestCore;

    @Autowired
    @Qualifier("geneIndexing")
    private SolrServer geneCore;

    @Autowired
    @Qualifier("mpIndexing")
    private SolrServer mpCore;

    @Autowired
    @Qualifier("diseaseIndexing")
    private SolrServer diseaseCore;

    @Autowired
    @Qualifier("maIndexing")
    private SolrServer maCore;

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    private SolrServer phenodigmCore;

    public static final long MIN_EXPECTED_ROWS = 218000;
    public static final int PHENODIGM_CORE_MAX_RESULTS = 350000;
    
    // Sets used to insure uniqueness when loading core components.
    
    // gene
    Set<String> mgiAccessionIdSet = new HashSet();
    Set<String> markerSymbolSet = new HashSet();
    Set<String> markerNameSet = new HashSet();
    Set<String> markerSynonymSet = new HashSet();
    Set<String> humanGeneSymbolSet = new HashSet();
    
    // mp
    Set<String> mpIdSet = new HashSet();
    Set<String> mpTermSet = new HashSet();
    Set<String> mpTermSynonymSet = new HashSet();
    
    // disease
    Set<String> diseaseIdSet = new HashSet();
    Set<String> diseaseTermSet = new HashSet();
    Set<String> diseaseAltsSet = new HashSet();
    
    // ma
    Set<String> maIdSet = new HashSet();
    Set<String> maTermSet = new HashSet();
    Set<String> maTermSynonymSet = new HashSet();
    
    // hp
    Set<String> hpIdSet = new HashSet();
    Set<String> hpTermSet = new HashSet();
    Set<String> hpSynonymSet = new HashSet();
        
    String mapKey;

    @Override
    public void validateBuild() throws IndexerException {
        Long numFound = getDocumentCount(autosuggestCore);
        
        if (numFound <= MINIMUM_DOCUMENT_COUNT)
            throw new IndexerException(new ValidationException("Actual autosuggest document count is " + numFound + "."));
        
        if (numFound != documentCount)
            logger.warn("WARNING: Added " + documentCount + " autosuggest documents but SOLR reports " + numFound + " documents.");
        else
            logger.info("validateBuild(): Indexed " + documentCount + " autosuggest documents.");
    }

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

    @Override
    public void run() throws IndexerException {
        try {
            initializeSolrCores();

            autosuggestCore.deleteByQuery("*:*");

            populateGeneAutosuggestTerms();
            populateMpAutosuggestTerms();
            populateDiseaseAutosuggestTerms();
            populateMaAutosuggestTerms();
            populateHpAutosuggestTerms();


            // Final commit
            autosuggestCore.commit();

            // FIXME
//            logger.info("Added {} beans", results.size());

        } catch (SolrServerException | IOException e) {
            throw new IndexerException(e);
        }
    }


    private void populateGeneAutosuggestTerms() throws SolrServerException, IOException {

        List<String> geneFields = Arrays.asList(GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_SYMBOL, GeneDTO.MARKER_NAME, GeneDTO.MARKER_SYNONYM, GeneDTO.HUMAN_GENE_SYMBOL);

        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(geneFields, ","))
            .setRows(Integer.MAX_VALUE);

        List<GeneDTO> genes = geneCore.query(query).getBeans(GeneDTO.class);
        for (GeneDTO gene : genes) {
            
            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : geneFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType("gene");

                switch (field) {
                    case GeneDTO.MGI_ACCESSION_ID:
                        mapKey = gene.getMgiAccessionId();
                        if (mgiAccessionIdSet.add(mapKey)) {
                            a.setMgiAccessionID(gene.getMgiAccessionId());
                            beans.add(a);
                        }
                        break;
                    case GeneDTO.MARKER_SYMBOL:
                        mapKey = gene.getMarkerSymbol();
                        if (markerSymbolSet.add(mapKey)) {
                            a.setMarkerSymbol(gene.getMgiAccessionId());
                            beans.add(a);
                        }
                        break;
                    case GeneDTO.MARKER_NAME:
                        mapKey = a.getMarkerName();
                        if (markerNameSet.add(mapKey)) {
                            a.setMarkerName(gene.getMgiAccessionId());
                            beans.add(a);
                        }
                        break;
                    case GeneDTO.MARKER_SYNONYM:
                        if (gene.getMarkerSynonym() != null) {
                            for (String s : gene.getMarkerSynonym()) {
                                mapKey = s;
                                if (markerSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMarkerSynonym(s);
                                    asyn.setDocType("gene");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case GeneDTO.HUMAN_GENE_SYMBOL:
                        if (gene.getHumanGeneSymbol() != null) {
                            for (String s : gene.getHumanGeneSymbol()) {
                                mapKey = s;
                                if (humanGeneSymbolSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setHumanGeneSymbol(s);
                                    asyn.setDocType("gene");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }

        }
    }

    private void populateMpAutosuggestTerms() throws SolrServerException, IOException {

        List<String> mpFields = Arrays.asList(
                MpDTO.MP_ID, MpDTO.MP_TERM, MpDTO.MP_TERM_SYNONYM, MpDTO.TOP_LEVEL_MP_ID, MpDTO.TOP_LEVEL_MP_TERM,
                MpDTO.TOP_LEVEL_MP_TERM_SYNONYM, MpDTO.INTERMEDIATE_MP_ID, MpDTO.INTERMEDIATE_MP_TERM,
                MpDTO.INTERMEDIATE_MP_TERM_SYNONYM, MpDTO.CHILD_MP_ID, MpDTO.CHILD_MP_TERM, MpDTO.CHILD_MP_TERM_SYNONYM);
        
        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(mpFields, ","))
            .setRows(Integer.MAX_VALUE);

        List<MpDTO> mps = mpCore.query(query).getBeans(MpDTO.class);
        for (MpDTO mp : mps) {

            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : mpFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType("mp");

                switch (field) {
                    case MpDTO.MP_ID:
                        mapKey = mp.getMpId();
                        if (mpIdSet.add(mapKey)) {
                            a.setMpID(mp.getMpId());
                            beans.add(a);
                        }
                        break;
                    case MpDTO.MP_TERM:
                        mapKey = mp.getMpTerm();
                        if (mpTermSet.add(mapKey)) {
                            a.setMpTerm(mp.getMpTerm());
                            beans.add(a);
                        }
                        break;
                    case MpDTO.MP_TERM_SYNONYM:
                        if (mp.getMpTermSynonym() != null) {
                            for (String s : mp.getMpTermSynonym()) {
                                mapKey = s;
                                if (mpTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpTermSynonym(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.TOP_LEVEL_MP_ID:
                        if (mp.getTopLevelMpId() != null) {
                            for (String s : mp.getTopLevelMpId()) {
                                mapKey = s;
                                if (mpIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setTopLevelMpID(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.TOP_LEVEL_MP_TERM:
                        if (mp.getTopLevelMpTerm() != null) {
                            for (String s : mp.getTopLevelMpTerm()) {
                                mapKey = s;
                                if (mpTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setTopLevelMpTerm(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.TOP_LEVEL_MP_TERM_SYNONYM:
                        if (mp.getTopLevelMpTermSynonym() != null) {
                            for (String s : mp.getTopLevelMpTermSynonym()) {
                                mapKey = s;
                                if (mpTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setTopLevelMpTermSynonym(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.INTERMEDIATE_MP_ID:
                        if (mp.getIntermediateMpId() != null) {
                            for (String s : mp.getIntermediateMpId()) {
                                mapKey = s;
                                if (mpIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setIntermediateMpID(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.INTERMEDIATE_MP_TERM:
                        if (mp.getIntermediateMpTerm() != null) {
                            for (String s : mp.getIntermediateMpTerm()) {
                                mapKey = s;
                                if (mpTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setIntermediateMpTerm(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.INTERMEDIATE_MP_TERM_SYNONYM:
                        if (mp.getIntermediateMpTermSynonym() != null) {
                            for (String s : mp.getIntermediateMpTermSynonym()) {
                                mapKey = s;
                                if (mpTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setIntermediateMpTermSynonym(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.CHILD_MP_ID:
                        if (mp.getChildMpId() != null) {
                            for (String s : mp.getChildMpId()) {
                                mapKey = s;
                                if (mpIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setChildMpID(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.CHILD_MP_TERM:
                        if (mp.getChildMpTerm() != null) {
                            for (String s : mp.getChildMpTerm()) {
                                mapKey = s;
                                if (mpTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setChildMpTerm(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.CHILD_MP_TERM_SYNONYM:
                        if (mp.getChildMpTermSynonym() != null) {
                            for (String s : mp.getChildMpTermSynonym()) {
                                mapKey = s;
                                if (mpTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setChildMpTermSynonym(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }

        }
    }
    
    private void populateDiseaseAutosuggestTerms() throws SolrServerException, IOException {

        List<String> diseaseFields = Arrays.asList(DiseaseDTO.DISEASE_ID, DiseaseDTO.DISEASE_TERM, DiseaseDTO.DISEASE_ALTS);
            
        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(diseaseFields, ","))
            .setRows(Integer.MAX_VALUE);

        List<DiseaseDTO> diseases = diseaseCore.query(query).getBeans(DiseaseDTO.class);
        for (DiseaseDTO disease : diseases) {
            
            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : diseaseFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType("disease");

                switch (field) {
                    case DiseaseDTO.DISEASE_ID:
                        mapKey = disease.getDiseaseId();
                        if (diseaseIdSet.add(mapKey)) {
                            a.setDiseaseID(disease.getDiseaseId());
                            beans.add(a);
                        }
                        break;
                    case DiseaseDTO.DISEASE_TERM:
                        mapKey = disease.getDiseaseTerm();
                        if (diseaseTermSet.add(mapKey)) {
                            a.setMarkerSymbol(disease.getDiseaseTerm());
                            beans.add(a);
                        }
                        break;
                    case DiseaseDTO.DISEASE_ALTS:
                        if (disease.getDiseaseAlts() != null) {
                            for (String s : disease.getDiseaseAlts()) {
                                mapKey = s;
                                if (diseaseAltsSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setDiseaseAlts(s);
                                    asyn.setDocType("disease");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }

        }
    }
    
    private void populateMaAutosuggestTerms() throws SolrServerException, IOException {

        List<String> maFields = Arrays.asList(
                MaDTO.MA_ID, MaDTO.MA_TERM, MaDTO.MA_TERM_SYNONYM, MaDTO.CHILD_MA_ID, MaDTO.CHILD_MA_TERM,
                MaDTO.CHILD_MA_TERM_SYNONYM, MaDTO.SELECTED_TOP_LEVEL_MA_ID,
                MaDTO.SELECTED_TOP_LEVEL_MA_TERM, MaDTO.SELECTED_TOP_LEVEL_MA_TERM_SYNONYM);
            
        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(maFields, ","))
            .setRows(Integer.MAX_VALUE);

        List<MaDTO> mas = maCore.query(query).getBeans(MaDTO.class);
        for (MaDTO ma : mas) {
            
            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : maFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType("ma");

                switch (field) {
                    case MaDTO.MA_ID:
                        mapKey = ma.getMaId();
                        if (maIdSet.add(mapKey)) {
                            a.setMaID(ma.getMaId());
                            beans.add(a);
                        }
                        break;
                    case MaDTO.MA_TERM:
                        mapKey = ma.getMaTerm();
                        if (maTermSet.add(mapKey)) {
                            a.setMaTerm(ma.getMaTerm());
                            beans.add(a);
                        }
                        break;
                    case MaDTO.MA_TERM_SYNONYM:
                        if (ma.getMaTermSynonym() != null) {
                            for (String s : ma.getMaTermSynonym()) {
                                mapKey = s;
                                if (maTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMaTermSynonym(s);
                                    asyn.setDocType("ma");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MaDTO.CHILD_MA_ID:
                        if (ma.getChildMaId() != null) {
                            for (String s : ma.getChildMaId()) {
                                mapKey = s;
                                if (maIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setChildMaID(s);
                                    asyn.setDocType("ma");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MaDTO.CHILD_MA_TERM:
                        if (ma.getChildMaTerm() != null) {
                            for (String s : ma.getChildMaTerm()) {
                                mapKey = s;
                                if (maTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setChildMaTerm(s);
                                    asyn.setDocType("ma");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MaDTO.CHILD_MA_TERM_SYNONYM:
                        if (ma.getChildMaTermSynonym() != null) {
                            for (String s : ma.getChildMaTermSynonym()) {
                                mapKey = s;
                                if (maTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setChildMaTermSynonym(s);
                                    asyn.setDocType("ma");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MaDTO.SELECTED_TOP_LEVEL_MA_ID:
                        if (ma.getSelectedTopLevelMaId() != null) {
                            for (String s : ma.getSelectedTopLevelMaId()) {
                                mapKey = s;
                                if (maIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setSelectedTopLevelMaID(s);
                                    asyn.setDocType("ma");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MaDTO.SELECTED_TOP_LEVEL_MA_TERM:
                        if (ma.getSelectedTopLevelMaTerm() != null) {
                            for (String s : ma.getSelectedTopLevelMaTerm()) {
                                mapKey = s;
                                if (maTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setSelectedTopLevelMaTerm(s);
                                    asyn.setDocType("ma");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MaDTO.SELECTED_TOP_LEVEL_MA_TERM_SYNONYM:
                        if (ma.getSelectedTopLevelMaTermSynonym() != null) {
                            for (String s : ma.getSelectedTopLevelMaTermSynonym()) {
                                mapKey = s;
                                if (maTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setSelectedTopLevelMaTermSynonym(s);
                                    asyn.setDocType("ma");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }
        }
    }
    
    private void populateHpAutosuggestTerms() throws SolrServerException, IOException {

        List<String> hpFields = Arrays.asList(HpDTO.MP_ID, HpDTO.MP_TERM, HpDTO.HP_ID, HpDTO.HP_TERM, HpDTO.HP_SYNONYM);
        
        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(hpFields, ","))
            .addFilterQuery("type:hp_mp")
            .setRows(PHENODIGM_CORE_MAX_RESULTS);

        QueryResponse r = phenodigmCore.query(query);
        List<HpDTO> hps = phenodigmCore.query(query).getBeans(HpDTO.class);
        for (HpDTO hp : hps) {
            
            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : hpFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType("hp");
                
                switch (field) {
                    case HpDTO.HP_ID:
                        mapKey = hp.getHpId();
                        if (hpIdSet.add(mapKey)) {
                            a.setHpID(hp.getHpId());
                            a.setHpmpID(hp.getMpId());
                            a.setHpmpTerm(hp.getMpTerm());
                            beans.add(a);
                        }
                        break;
                    case HpDTO.HP_TERM:
                        mapKey = hp.getHpTerm();
                        if (hpTermSet.add(mapKey)) {
                            a.setHpTerm(hp.getHpTerm());
                            a.setHpmpID(hp.getMpId());
                            a.setHpmpTerm(hp.getMpTerm());
                            beans.add(a);
                        }
                        break;
                    case HpDTO.HP_SYNONYM:
                        if (hp.getHpSynonym() != null) {
                            for (String s : hp.getHpSynonym()) {
                                mapKey = s;
                                if (hpSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setDocType("hp");
                                    asyn.setHpSynonym(s);
                                    asyn.setHpmpID(hp.getMpId());
                                    asyn.setHpmpTerm(hp.getMpTerm());
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }
        }
    }

    public static void main(String[] args) throws IndexerException {

        AutosuggestIndexer main = new AutosuggestIndexer();
        main.initialise(args);
        main.run();

        logger.info("Process finished.  Exiting.");

    }


    @Override
    protected Logger getLogger() {

        return logger;
    }

}

