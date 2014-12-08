package uk.ac.ebi.phenotype.solr.indexer;

import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.phenotype.service.dto.MaDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;
import uk.ac.ebi.phenotype.solr.indexer.utils.SolrUtils;

/**
 * Populate the MA core
 */
public class MAIndexer extends AbstractIndexer {

    private static final Logger logger = LoggerFactory.getLogger(MAIndexer.class);
    private Connection ontoDbConnection;

    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;
    
    @Autowired
    @Qualifier("sangerImagesIndexing")
    SolrServer imagesCore;

    @Autowired
    @Qualifier("maIndexing")
    SolrServer maCore;
    
    private Map<String, List<String>> ontologySubsetMap = new HashMap();        // key = term_id.
    private Map<String, List<OntologyTermBean>> maChildMap = new HashMap();     // key = parent term_id.
    private Map<String, List<OntologyTermBean>> maParentMap = new HashMap();    // key = child term_id.
    private Map<String, List<SangerImageDTO>> maImagesMap = new HashMap();      // key = term_id.
    private Map<String, List<OntologyTermBean>> maSelectedTopLevelTermsMap = new HashMap(); // key = ma_term_infos.term_id.
    
    private static final int BATCH_SIZE = 50;
        
    
    public MAIndexer() {
        
    }
    
    @Override
    public void initialise(String[] args) throws IndexerException {
        super.initialise(args);
        try {
            this.ontoDbConnection = ontodbDataSource.getConnection();
        } catch (SQLException sqle) {
            logger.error("Caught SQL Exception initialising database connections: {}", sqle.getMessage());
            throw new IndexerException(sqle);
        }
    }

    @Override
    public void run() throws IndexerException {
        try {
            logger.info("Starting MP Indexer...");
            initialiseSupportingBeans();

            List<MaDTO> maBatch = new ArrayList(BATCH_SIZE);
            int count = 0;

            logger.info("Starting indexing loop");

            // Loop through the mp_term_infos
            String query =
                    "SELECT\n" +
                    "  'ma' AS dataType\n" +
                    ", term_id\n" +
                    ", name\n" +
                    "FROM ma_term_infos\n" +
                    "WHERE term_id != 'MA:0000001'\n" +
                    "ORDER BY term_id, name";
            PreparedStatement ps = ontoDbConnection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String termId = rs.getString("term_id");

                MaDTO ma = new MaDTO();
                ma.setDataType(rs.getString("dataType"));
                ma.setMaId(termId);
                ma.setMaTerm(rs.getString("name"));
                ma.setOntologySubset(ontologySubsetMap.get(termId));
                ma.setMaTermSynonym(IndexerMap.getMaSynonyms(ontoDbConnection, termId));

                // Children
                List<OntologyTermBean> maChildTerms = maChildMap.get(termId);
    //   System.out.println("maChildTerms = " + maChildTerms + ". termId = " + termId);
                if (maChildTerms != null) {
                    List<String> childMaIdList = new ArrayList();
                    List<String> childMaTermList = new ArrayList();
                    List<String> childTermId_termNameList = new ArrayList();
                    for (OntologyTermBean childBean : maChildTerms) {
                        childMaIdList.add(childBean.getTermId());
                        childMaTermList.add(childBean.getName());
                        childTermId_termNameList.add(childBean.getTermIdTermName());
                        ma.setChildMaId(childMaIdList);
                        ma.setChildMaTerm(childMaTermList);
                        ma.setChildMaIdTerm(childTermId_termNameList);
                        List<String> newSynonyms = childBean.getSynonyms();
                        List<String> oldSynonyms = ma.getChildMaTermSynonym();
                        if (newSynonyms != null) {
                            if (oldSynonyms == null) {
                                ma.setChildMaTermSynonym(new ArrayList<String>());
                                oldSynonyms = ma.getChildMaTermSynonym();
                            }
                            oldSynonyms.addAll(newSynonyms);
                        }
                    }
                }

                // Selected Top-Level Terms
                List<OntologyTermBean> maSelectedTopLevelTerms = maSelectedTopLevelTermsMap.get(termId);
                if (maSelectedTopLevelTerms != null) {
                    List<String> selectedTopLevelIdList = new ArrayList();
                    List<String> selectedTopLevelNameList = new ArrayList();
                    List<String> selectedTopLevelSynonymList = new ArrayList();
                    for (OntologyTermBean selectedBean : maSelectedTopLevelTerms) {
                        selectedTopLevelIdList.add(selectedBean.getTermId());
                        selectedTopLevelNameList.add(selectedBean.getName());
                        List<String> synonyms = selectedBean.getSynonyms();
                        for (String synonym : synonyms) {
                            if ( ! selectedTopLevelSynonymList.contains(synonym)) {
                                selectedTopLevelSynonymList.add(synonym);
                            }
                        }
                        ma.setSelectedTopLevelMaId(selectedTopLevelIdList);;
                        ma.setSelectedTopLevelMaTerm(selectedTopLevelNameList);
                        ma.setSelectedTopLevelMaTermSynonym(selectedTopLevelSynonymList);
                    }
                }
                
                // Parents
                List<OntologyTermBean> maParentTerms = maParentMap.get(termId);
                if (maParentTerms != null) {
                    List<String> parentMaIdList = new ArrayList();
                    List<String> parentMaTermList = new ArrayList();
                    for (OntologyTermBean parentBean : maParentTerms) {
                        parentMaIdList.add(parentBean.getTermId());
                        parentMaTermList.add(parentBean.getName());
                        ma.setTopLevelMaId(parentMaIdList);
                        ma.setTopLevelMaTerm(parentMaTermList);
                    }
                }
                
                // Image association fields
                List<SangerImageDTO> sangerImages = maImagesMap.get(termId);
                if (sangerImages != null) {
                    for (SangerImageDTO sangerImage : sangerImages) {
                        ma.setProcedureName(sangerImage.getProcedureName());
                        ma.setExpName(sangerImage.getExpName());
                        ma.setExpNameExp(sangerImage.getExpNameExp());
                        ma.setSymbolGene(sangerImage.getSymbolGene());
                        
                        ma.setMgiAccessionId(sangerImage.getMgiAccessionId());
                        ma.setMarkerSymbol(sangerImage.getMarkerSymbol());
                        ma.setMarkerName(sangerImage.getMarkerName());
                        ma.setMarkerSynonym(sangerImage.getMarkerSynonym());
                        ma.setMarkerType(sangerImage.getMarkerType());
                        ma.setHumanGeneSymbol(sangerImage.getHumanGeneSymbol());
                        
                        ma.setStatus(sangerImage.getStatus());
                        
                        ma.setImitsPhenotypeStarted(sangerImage.getImitsPhenotypeStarted());
                        ma.setImitsPhenotypeComplete(sangerImage.getImitsPhenotypeComplete());
                        ma.setImitsPhenotypeStatus(sangerImage.getImitsPhenotypeStatus());
                        
                        ma.setLatestPhenotypeStatus(sangerImage.getLatestPhenotypeStatus());
                        ma.setLatestPhenotypingCentre(sangerImage.getLatestPhenotypingCentre());
                        
                        ma.setLatestProductionCentre(sangerImage.getLatestProductionCentre());
                        ma.setLatestPhenotypingCentre(sangerImage.getLatestPhenotypingCentre());
                        
                        ma.setAlleleName(sangerImage.getAlleleName());
                    }
                }
                
    //            logger.debug("{}: Built MP DTO {}", count, termId);
                count ++;
                maBatch.add(ma);
                if (maBatch.size() == BATCH_SIZE) {
                    // Update the batch, clear the list
                    maCore.addBeans(maBatch, 60000);
                    maBatch.clear();
    //                logger.info("Indexed {} beans", count);
                }
            }

            // Make sure the last batch is indexed
            if (maBatch.size() > 0) {
                maCore.addBeans(maBatch, 60000);
                count += maBatch.size();
            }
            
            // Send a final commit
            maCore.commit();
            logger.info("Indexed {} beans in total", count);
        } catch (SQLException | SolrServerException| IOException e) {
            throw new IndexerException(e);
        }
        

        logger.info("MP Indexer complete!");
    }
    
    
    // PROTECTED METHODS
    
    
    @Override
    protected Logger getLogger() {
        return logger;
    }
    
    @Override
    protected void printConfiguration() {
        if (logger.isDebugEnabled()) {
            logger.debug("WRITING ma     CORE TO: " + SolrUtils.getBaseURL(maCore));
            logger.debug("USING   images CORE AT: " + SolrUtils.getBaseURL(imagesCore));
        }
    }
    
    
    // PRIVATE METHODS
    
    
    private final Integer MAX_ITERATIONS = 2;                                // Set to non-null value > 0 to limit max_iterations.
    
    private void initialiseSupportingBeans() throws IndexerException {
        try {
            // Grab all the supporting database content
            ontologySubsetMap = IndexerMap.getMaTermSubsets(ontoDbConnection);

            maChildMap = IndexerMap.getMaTermChildTerms(ontoDbConnection);
            if (logger.isDebugEnabled()) {
                IndexerMap.dumpOntologyMaTermMap(maChildMap, "Child map:");
            }
            maParentMap = IndexerMap.getMaTermParentTerms(ontoDbConnection);
            if (logger.isDebugEnabled()) {
                IndexerMap.dumpOntologyMaTermMap(maParentMap, "Parent map:");
            }
            
            maSelectedTopLevelTermsMap = IndexerMap.getMaSelectedTopLevelTerms(ontoDbConnection);
            if (logger.isDebugEnabled()) {
                IndexerMap.dumpOntologyMaTermMap(maSelectedTopLevelTermsMap, "Selected Top Level map:");
            }

            maImagesMap = IndexerMap.getSangerImagesByMA(imagesCore);
            if (logger.isDebugEnabled()) {
                IndexerMap.dumpSangerImagesMap(maImagesMap, "Images map:", MAX_ITERATIONS);
            }
        } catch (SQLException e) {
            throw new IndexerException(e);
        }
    }

    public static void main(String[] args) throws IndexerException {
        MAIndexer indexer = new MAIndexer();
        indexer.initialise(args);
        indexer.run();

        logger.info("Process finished.  Exiting.");
    }
}