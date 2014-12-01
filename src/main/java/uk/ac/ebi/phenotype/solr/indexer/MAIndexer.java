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
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import uk.ac.ebi.phenotype.service.dto.MaDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;

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
    private Map<String, List<String>> maTermSynonymMap = new HashMap();         // key = term_id.
    private Map<String, List<OntologyTermBean>> maChildMap = new HashMap();     // key = parent term_id.
    private Map<String, List<OntologyTermBean>> maParentMap = new HashMap();    // key = child term_id.
    private Map<String, List<SangerImageDTO>> maImagesMap = new HashMap();      // key = term_id.
    
    private static final int BATCH_SIZE = 50;
        
    
    public MAIndexer() {
        try {
           ontoDbConnection = ontodbDataSource.getConnection();
        } catch (Exception e) {
            logger.error("Unable to get ontodbDataSource: " + e.getLocalizedMessage());
        }
    }
    
    @Override
    public void initialise(String[] args) throws IndexerException {
        args = new String[] { "--context=index-app-config.xml" };
        super.initialise(args);
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        try {
            DataSource ontoDS = ((DataSource) applicationContext.getBean("ontodbDataSource"));
            this.ontoDbConnection = ontoDS.getConnection();
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
                ma.setMaTermSynonym(maTermSynonymMap.get(termId));

                // Children
                List<OntologyTermBean> maChildTerms = maChildMap.get(termId);
    //   System.out.println("maChildTerms = " + maChildTerms + ". termId = " + termId);

                if (maChildTerms != null) {
                    List<String> childMaIdList = new ArrayList();
                    List<String> childMaTermList = new ArrayList();
                    List<String> childTermId_termNameList = new ArrayList();
                    for (OntologyTermBean childBean : maChildTerms) {
                        childMaIdList.add(childBean.getId());
                        childMaTermList.add(childBean.getTerm());
                        childTermId_termNameList.add(childBean.getIdTerm());
                        ma.setChildMaId(childMaIdList);
                        ma.setChildMaTerm(childMaTermList);
                        ma.setChildMaIdTerm(childTermId_termNameList);
                        ma.setChildMaTermSynonym(childBean.getSynonyms());
                    }
                }

                // Parents
                List<OntologyTermBean> maParentTerms = maParentMap.get(termId);
                if (maParentTerms != null) {
                    List<String> parentMaIdList = new ArrayList();
                    List<String> parentMaTermList = new ArrayList();
                    for (OntologyTermBean parentBean : maParentTerms) {
                        parentMaIdList.add(parentBean.getId());
                        parentMaTermList.add(parentBean.getTerm());
                        ma.setTopLevelMaId(parentMaIdList);
                        ma.setTopLevelMaTerm(parentMaTermList);
    //                    ma.setTopLevelMaTermSynonym(parentBean.getSynonyms());
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
    
    
    // PRIVATE METHODS
    
    
    private final Integer MAX_ITERATIONS = 5;                                   // Set to non-null value > 0 to limit max_iterations.
    
    private void initialiseSupportingBeans() throws IndexerException {
        try {
            // Grab all the supporting database content
            ontologySubsetMap = IndexerMap.getMaTermSubsets(ontoDbConnection);
            maTermSynonymMap = IndexerMap.getMaTermSynonyms(ontoDbConnection);

            maChildMap = IndexerMap.getMaTermChildTerms(ontoDbConnection);
            if (logger.isDebugEnabled()) {
                IndexerMap.dumpOntologyMaTermMap(maChildMap, "Child map:");
            }
            maParentMap = IndexerMap.getMaTermParentTerms(ontoDbConnection);
            if (logger.isDebugEnabled()) {
                IndexerMap.dumpOntologyMaTermMap(maParentMap, "Parent map:");
            }

            maImagesMap = IndexerMap.getSangerImages(imagesCore);
            if (logger.isDebugEnabled()) {
                IndexerMap.dumpSangerImagesMap(maImagesMap, "Images map:", MAX_ITERATIONS);
            }
        } catch (SQLException e) {
            throw new IndexerException(e);
        }
    }

    public static void main(String[] args) throws SQLException, IOException, SolrServerException, IndexerException {
        MAIndexer indexer = new MAIndexer();
        indexer.initialise(args);
        indexer.run();

        logger.info("Process finished.  Exiting.");
    }
}