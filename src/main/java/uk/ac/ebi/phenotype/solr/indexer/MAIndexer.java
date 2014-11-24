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
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.MaDTO;
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
    
        
//    @Autowired
//    @Qualifier("maIndexing")
//    SolrServer maSolrServer;
    

    private Map<String, List<MaTermSubsetsBean>> maTermSubsetsMap = new HashMap();      // key = term_id.
    private Map<String, List<MaTermInfo2SynBean>> maTermInfo2SynMap = new HashMap();    // key = term_id.
    private Map<String, List<OntologyTermBean>> maChildMap = new HashMap();             // key = parent term_id.
    private Map<String, List<OntologyTermBean>> maParentMap = new HashMap();            // key = child term_id.
    private Map<String, List<OntologyTermBean>> maImagesMap = new HashMap();            // key = child term_id.
    
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
                "WHERE term_id != 'MA:0000001'\n";
        PreparedStatement ps = ontoDbConnection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String termId = rs.getString("term_id");

            MaDTO ma = new MaDTO();
            ma.setDataType(rs.getString("dataType"));
            ma.setMaId(termId);
            ma.setMaTerm(rs.getString("name"));
            
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
        maTermSubsetsMap = populateMaTermSubsetsBean();
        maTermInfo2SynMap = populateMaTermInfo2SynBean();
        maChildMap = OntologyUtil.populateChildTerms(ontoDbConnection);
        maParentMap = OntologyUtil.populateParentTerms(ontoDbConnection);
//        maImagesMap = OntologyUtil.populateImageBean(imagesCore);
    }

    /**
     * Add all the relevant data.
     *
     * @throws SQLException when a database exception occurs
     * @return the populated map.
     */
    private Map<String, List<MaTermSubsetsBean>> populateMaTermSubsetsBean() throws SQLException {
        Map<String, List<MaTermSubsetsBean>> map = new HashMap();
        String query = 
                  "SELECT\n"
                + "  term_id\n"
                + ", subset\n"
                + "FROM ma_term_subsets mts\n";
        
        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                MaTermSubsetsBean bean = new MaTermSubsetsBean();
                bean.subset = resultSet.getString("subset");
                String termId = resultSet.getString("term_id");
                if ( ! map.containsKey(termId)) {
                    map.put(termId, new ArrayList<MaTermSubsetsBean>());
                }
                
                map.get(termId).add(bean);   
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
    private Map<String, List<MaTermInfo2SynBean>> populateMaTermInfo2SynBean() throws SQLException {
        Map<String, List<MaTermInfo2SynBean>> map = new HashMap();
        String query = "SELECT\n"
                + "  term_id\n"
                + ", syn_name\n"
                + "FROM ma_synonyms\n";
        
        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                MaTermInfo2SynBean bean = new MaTermInfo2SynBean();
                bean.synName = resultSet.getString("syn_name");
                bean.termId = resultSet.getString("term_id");
                if ( ! map.containsKey(bean.termId)) {
                    map.put(bean.termId, new ArrayList<MaTermInfo2SynBean>());
                }
                
                map.get(bean.termId).add(bean);   
            }
        }
        
        return map;
    }
    
    
    
    
//    private Map<String, List<ImageDTO>> populateImageBean() throws SolrServerException {
//        Map<String, List<ImageDTO>> map = new HashMap();
//
//        int pos = 0;
//        long total = Integer.MAX_VALUE;
////        SolrQuery query = new SolrQuery("*:*");
//        SolrQuery query = new SolrQuery("q=maTermId:*");
//        query.setRows(BATCH_SIZE);
//        while (pos < total) {
//            query.setStart(pos);
//            QueryResponse response = imagesCore.query(query);
//            total = response.getResults().getNumFound();
//            List<ImageDTO> imageList = response.getBeans(ImageDTO.class);
//            for (ImageDTO image : imageList) {
//                map.put(image.   .getMgiAccessionId(), map);
//            }
//            pos += BATCH_SIZE;
//        }
//        logger.debug("Loaded {} alleles", map.size());
//
//        return map;
//    }
        
        
        
        
        
        


    // INTERNAL MAPPING CLASSES
    
    
    protected class MaTermSubsetsBean {
        public String subset;
    }
    
    protected class MaTermInfo2SynBean {
        public String termId;
        public String synName;
    }
  
    protected class ChildMa2SynBean {
        public String childMaTermSynonym;
    }
    
    public static void main(String[] args) throws SQLException, IOException, SolrServerException, IndexerException {
            MAIndexer indexer = new MAIndexer();
            indexer.initialise(args);
            indexer.run();

            logger.info("Process finished.  Exiting.");
    }
}
