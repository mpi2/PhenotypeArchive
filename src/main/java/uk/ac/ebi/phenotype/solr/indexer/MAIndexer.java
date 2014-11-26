package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.MaDTO;
import static uk.ac.ebi.phenotype.solr.indexer.SolrUtils.populateImageBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;

/**
 * Populate the MA core
 */
public class MAIndexer extends AbstractIndexer {

    private static final Logger logger = LoggerFactory.getLogger(MAIndexer.class);
    private Connection komp2DbConnection;
    private Connection ontoDbConnection;
    
    private static final String IMAGES_URL="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/images";
    private static final String MA_URL="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/ma";
    
    private final SolrServer imagesCore;
    private final SolrServer maCore;
    
    private Map<String, List<String>> ontologySubsetMap = new HashMap();        // key = term_id.
    private Map<String, List<String>> maTermSynonymMap = new HashMap();         // key = term_id.
    private Map<String, List<OntologyTermBean>> maChildMap = new HashMap();             // key = parent term_id.
    private Map<String, List<OntologyTermBean>> maParentMap = new HashMap();            // key = child term_id.
    private Map<String, List<ImageDTO>> maImagesMap = new HashMap();                    // key = term_id.
    
    private static final int BATCH_SIZE = 50;
        
    
    public MAIndexer() {
        this.imagesCore = new HttpSolrServer(IMAGES_URL);
        this.maCore = new HttpSolrServer(MA_URL);
    }
    
    @Override
    public void initialise(String[] args) throws IndexerException {
        args = new String[] { "--context=index-app-config.xml" };
        super.initialise(args);
        try {
            DataSource komp2DS = ((DataSource) applicationContext.getBean("komp2DataSource"));
            this.komp2DbConnection = komp2DS.getConnection();
            DataSource ontoDS = ((DataSource) applicationContext.getBean("ontodbDataSource"));
            this.ontoDbConnection = ontoDS.getConnection();
        } catch (SQLException sqle) {
            logger.error("Caught SQL Exception initialising database connections: {}", sqle.getMessage());
            throw new IndexerException(sqle);
        }
    }

    @Override
    public void run() throws SolrServerException, SQLException, IOException {
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

        logger.info("MP Indexer complete!");
    }
    
    
    // PROTECTED METHODS
    
    
    @Override
    protected Logger getLogger() {
        return logger;
    }
    
    
    // PRIVATE METHODS
    

    private void initialiseSupportingBeans() throws SQLException, SolrServerException {
        // Grab all the supporting database content
        ontologySubsetMap = populateMaTermSubsetsBean();
        maTermSynonymMap = populateMaTermSynonym();
        maChildMap = OntologyUtil.populateChildTerms(ontoDbConnection);
        OntologyUtil.dumpTerms(maChildMap, "Child map:");
        maParentMap = OntologyUtil.populateParentTerms(ontoDbConnection);
        OntologyUtil.dumpTerms(maParentMap, "Parent map:");
        maImagesMap = populateImageBean(imagesCore);
    }

    /**
     * Add all the relevant data.
     *
     * @throws SQLException when a database exception occurs
     * @return the populated map.
     */
    private Map<String, List<String>> populateMaTermSubsetsBean() throws SQLException {
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
     * Add all the relevant data.
     *
     * @throws SQLException when a database exception occurs
     * @return the populated map.
     */
    private Map<String, List<String>> populateMaTermSynonym() throws SQLException {
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
    
    public static void main(String[] args) throws SQLException, IOException, SolrServerException, IndexerException {
            MAIndexer indexer = new MAIndexer();
            indexer.initialise(args);
            indexer.run();

            logger.info("Process finished.  Exiting.");
    }
}