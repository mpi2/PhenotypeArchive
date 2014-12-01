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

package uk.ac.ebi.phenotype.solr.indexer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.phenotype.service.dto.MaDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;

/**
 * This class encapsulates the code and data necessary to represent an index
 * manager that manages the creation of all of the indexes required for phenotype
 * archive; a job previously delegated to Jenkins that was susceptible to frequent
 * failure.
 * 
 * @author mrelac
 */
public class IndexManager {
    private static final Logger logger = LoggerFactory.getLogger(IndexManager.class);
    private Connection komp2DbConnection;
    private Connection ontoDbConnection;
    
//    private static final String IMAGES_URL="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/images";
//    private static final String IMAGES_URL="http://ves-ebi-d1.ebi.ac.uk:8090/mi/impc/solr/images";
    private static final String MA_URL="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/ma";
	private static final String ALLELE_URL="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/allele";	  
	private static final String IMAGES_URL="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/images";       
	private static final String PREQC_URL="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/preqc";       
	private static final String PHENODIGM_URL="http://solrcloudlive.sanger.ac.uk/solr/phenodigm";
    
    private final SolrServer imagesCore;
    private final SolrServer maCore;
    
    // maps shared amongst all cores.
    private Map<String, List<String>> ontologySubsetMap = new HashMap();        // key = term_id.
    private Map<String, List<String>> maTermSynonymMap = new HashMap();         // key = term_id.
    private Map<String, List<OntologyTermBean>> maChildMap = new HashMap();             // key = parent term_id.
    private Map<String, List<OntologyTermBean>> maParentMap = new HashMap();            // key = child term_id.
    private Map<String, List<SangerImageDTO>> maImagesMap = new HashMap();                    // key = term_id.
    
    private static final int BATCH_SIZE = 50;
    
    public IndexManager() {
        this.imagesCore = new HttpSolrServer(IMAGES_URL);
        this.maCore = new HttpSolrServer(MA_URL);
    }
    
//    @Override
//    public void initialise(String[] args) throws IndexerException {
//        args = new String[] { "--context=index-app-config.xml" };
//        super.initialise(args);
//        try {
//            DataSource komp2DS = ((DataSource) applicationContext.getBean("komp2DataSource"));
//            this.komp2DbConnection = komp2DS.getConnection();
//            DataSource ontoDS = ((DataSource) applicationContext.getBean("ontodbDataSource"));
//            this.ontoDbConnection = ontoDS.getConnection();
//        } catch (SQLException sqle) {
//            logger.error("Caught SQL Exception initialising database connections: {}", sqle.getMessage());
//            throw new IndexerException(sqle);
//        }
//    }

//    @Override
//    public void run() throws SolrServerException, SQLException, IOException {
//        logger.info("Starting MP Indexer...");
//        initialiseSupportingBeans();
//
//        List<MaDTO> maBatch = new ArrayList(BATCH_SIZE);
//        int count = 0;
//
//        logger.info("Starting indexing loop");
//
//        // Loop through the mp_term_infos
//        String query =
//                "SELECT\n" +
//                "  'ma' AS dataType\n" +
//                ", term_id\n" +
//                ", name\n" +
//                "FROM ma_term_infos\n" +
//                "WHERE term_id != 'MA:0000001'\n" +
//                "ORDER BY term_id, name";
//        PreparedStatement ps = ontoDbConnection.prepareStatement(query);
//        ResultSet rs = ps.executeQuery();
//        while (rs.next()) {
//            String termId = rs.getString("term_id");
//
//            MaDTO ma = new MaDTO();
//            ma.setDataType(rs.getString("dataType"));
//            ma.setMaId(termId);
//            ma.setMaTerm(rs.getString("name"));
//            ma.setOntologySubset(ontologySubsetMap.get(termId));
//            ma.setMaTermSynonym(maTermSynonymMap.get(termId));
//            
//            // Children
//            List<OntologyTermBean> maChildTerms = maChildMap.get(termId);
////   System.out.println("maChildTerms = " + maChildTerms + ". termId = " + termId);
//            
//            if (maChildTerms != null) {
//                List<String> childMaIdList = new ArrayList();
//                List<String> childMaTermList = new ArrayList();
//                List<String> childTermId_termNameList = new ArrayList();
//                for (OntologyTermBean childBean : maChildTerms) {
//                    childMaIdList.add(childBean.getId());
//                    childMaTermList.add(childBean.getTerm());
//                    childTermId_termNameList.add(childBean.getIdTerm());
//                    ma.setChildMaId(childMaIdList);
//                    ma.setChildMaTerm(childMaTermList);
//                    ma.setChildMaIdTerm(childTermId_termNameList);
//                    ma.setChildMaTermSynonym(childBean.getSynonyms());
//                }
//            }
//            
//            // Parents
//            List<OntologyTermBean> maParentTerms = maParentMap.get(termId);
//            if (maParentTerms != null) {
//                List<String> parentMaIdList = new ArrayList();
//                List<String> parentMaTermList = new ArrayList();
//                for (OntologyTermBean parentBean : maParentTerms) {
//                    parentMaIdList.add(parentBean.getId());
//                    parentMaTermList.add(parentBean.getTerm());
//                    ma.setTopLevelMaId(parentMaIdList);
//                    ma.setTopLevelMaTerm(parentMaTermList);
////                    ma.setTopLevelMaTermSynonym(parentBean.getSynonyms());
//                }
//            }
//            
////            logger.debug("{}: Built MP DTO {}", count, termId);
//            count ++;
//            maBatch.add(ma);
//            if (maBatch.size() == BATCH_SIZE) {
//                // Update the batch, clear the list
//                maCore.addBeans(maBatch, 60000);
//                maBatch.clear();
////                logger.info("Indexed {} beans", count);
//            }
//        }
//
//        // Make sure the last batch is indexed
//        if (maBatch.size() > 0) {
//            maCore.addBeans(maBatch, 60000);
//            count += maBatch.size();
//        }
//        
//        // Send a final commit
//        maCore.commit();
//        logger.info("Indexed {} beans in total", count);
//
//        logger.info("MP Indexer complete!");
//    }
//    
//    
//    // PROTECTED METHODS
//    
//    
//    @Override
//    protected Logger getLogger() {
//        return logger;
//    }
//    
//    public static void main(String[] args) throws SQLException, IOException, SolrServerException, IndexerException {
//            IndexManager manager = new IndexManager();
//            manager.initialise(args);
//            manager.run();
//
//            logger.info("IndexManager process finished.  Exiting.");
//    }
}
