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

import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;
import uk.ac.ebi.phenotype.service.dto.MaDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;

/**
 * Populate the MA core
 */
public class GeneIndexer extends AbstractIndexer {

    private static final Logger logger = LoggerFactory.getLogger(GeneIndexer.class);
    private Connection komp2DbConnection;
    
    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;
    
    @Autowired
    @Qualifier("alleleIndexing")
    SolrServer alleleCore;
    
    @Autowired
    @Qualifier("geneIndexing")
    SolrServer geneCore;
    
//    @Autowired
//    @Qualifier("mpIndexing")
//    SolrServer mpCore;
//    
//    @Autowired
//    @Qualifier("pipelineIndexing")
//    SolrServer pipelineCore;
    
    @Autowired
    @Qualifier("sangerImagesIndexing")
    SolrServer imagesCore;

    
//    <dataSource name="allele_core" type="HttpDataSource" baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/allele/select?" encoding="UTF-8"  connectionTimeout="10000" readTimeout="10000"/>
// 	<dataSource name="mp_core" type="HttpDataSource" baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/mp/select?" encoding="UTF-8"  connectionTimeout="10000" readTimeout="10000"/>	
//	<dataSource name="pipeline_core" type="HttpDataSource" baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/pipeline/select?" encoding="UTF-8"  connectionTimeout="10000" readTimeout="10000"/>    
//	<dataSource name="images_core" type="HttpDataSource" baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/images/select?" encoding="UTF-8"  connectionTimeout="10000" readTimeout="10000"/>	

    
    
    private Map<String, List<String>> ontologySubsetMap = new HashMap();        // key = term_id.
    private Map<String, List<String>> maTermSynonymMap = new HashMap();         // key = term_id.
    private Map<String, List<OntologyTermBean>> maChildMap = new HashMap();     // key = parent term_id.
    private Map<String, List<OntologyTermBean>> maParentMap = new HashMap();    // key = child term_id.
    private Map<String, List<SangerImageDTO>> maImagesMap = new HashMap();      // key = term_id.
    
    private static final int BATCH_SIZE = 50;
        
    
    public GeneIndexer() {
        try {
           komp2DbConnection = komp2DataSource.getConnection();
        } catch (Exception e) {
            logger.error("Unable to get komp2DataSource: " + e.getLocalizedMessage());
        }
    }
    
    @Override
    public void initialise(String[] args) throws IndexerException {
        super.initialise(args);
        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        try {
            DataSource ontoDS = ((DataSource) applicationContext.getBean("komp2DataSource"));
            this.komp2DbConnection = ontoDS.getConnection();
        } catch (SQLException sqle) {
            logger.error("Caught SQL Exception initialising database connections: {}", sqle.getMessage());
            throw new IndexerException(sqle);
        }
    }

    @Override
    public void run() throws IndexerException {
    	try {
            logger.info("Starting Gene Indexer...");
            initialiseSupportingBeans();
            
            int count=0;
            List<AlleleDTO> alleles = IndexerMap.getAlleles(alleleCore);
            
            geneCore.deleteByQuery("*:*");
          
            for(AlleleDTO allele:alleles){
            	System.out.println("allele="+allele.getMarkerSymbol());
            	GeneDTO gene=new GeneDTO();
            	gene.setMgiAccessionId(allele.getMgiAccessionId());
            	gene.setDataType(allele.getDataType());
            	gene.setMarkerType(allele.getMarkerType());
            	gene.setMarkerSymbol(allele.getMarkerSymbol());
            	gene.setMarkerSynonym(allele.getMarkerSynonym());
            	gene.setMarkerName(allele.getMarkerName());
            	gene.setHumanGeneSymbol(allele.getHumanGeneSymbol());
            	gene.setLatestEsCellStatus(allele.getLatestEsCellStatus());
            	gene.setImitsPhenotypeStarted(allele.getImitsPhenotypeStarted());
            	gene.setImitsPhenotypeComplete(allele.getImitsPhenotypeComplete());
            	gene.setImitsPhenotypeStatus(allele.getImitsPhenotypeStatus());
            	gene.setLatestMouseStatus(allele.getLatestMouseStatus());
            	gene.setLatestProjectStatus(allele.getLatestProjectStatus());
            	gene.setStatus(allele.getStatus());
            	gene.setLatestPhenotypeStatus(allele.getLatestPhenotypeStatus());
            	gene.setLegacy_phenotype_status(allele.getLegacyPhenotypeStatus());
            	gene.setLatestProductionCentre(allele.getLatestProductionCentre());
            	gene.setLatestPhenotypingCentre(allele.getLatestPhenotypingCentre());
            	gene.setAlleleName(allele.getAlleleName());
            	gene.setEsCellStatus(allele.getEsCellStatus());
            	gene.setMouseStatus(allele.getMouseStatus());
            	gene.setPhenotypeStatus(allele.getPhenotypeStatus());
            	gene.setProductionCentre(allele.getProductionCentre());
            	gene.setPhenotypingCentre(allele.getPhenotypingCentre());
            	gene.setType(allele.getType());
            	gene.setDiseaseSource(allele.getDiseaseSource());
            	gene.setDiseaseId(allele.getDiseaseId());
            	gene.setDiseaseTerm(allele.getDiseaseTerm());
            	gene.setDiseaseAlts(allele.getDiseaseAlts());
            	gene.setDiseaseClasses(allele.getDiseaseClasses());
            	gene.setHumanCurated(allele.getHumanCurated());
            	gene.setMouseCurated(allele.getMouseCurated());
            	gene.setMgiPredicted(allele.getMgiPredicted());
            	gene.setImpcPredicted(allele.getImpcPredicted());
            	gene.setMgiPredicted(allele.getMgiPredicted());
            	gene.setMgiPredictedKnonwGene(allele.getMgiPredictedKnownGene());
            	gene.setImpcNovelPredictedInLocus(allele.getImpcNovelPredictedInLocus());
            	
            	geneCore.addBean(gene, 60000);
            	count++;

				if (count % 10 == 0) {
					System.out.println(" added " + count + " beans");
				}
            }
           
            System.out.println("commiting to gene core for last time!");
            geneCore.commit();
            
            } catch (IOException | SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IndexerException(e);
			}

        

        logger.info("Gene Indexer complete!");
    }
    
    
    // PROTECTED METHODS
    
    
    @Override
    protected Logger getLogger() {
        return logger;
    }
    
    
    // PRIVATE METHODS
    
    
    private final Integer MAX_ITERATIONS = 5;                                   // Set to non-null value > 0 to limit max_iterations.
    
    private void initialiseSupportingBeans() throws IndexerException {
//        try {
//            // Grab all the supporting database content
//            ontologySubsetMap = IndexerMap.getMaTermSubsets(ontoDbConnection);
//            maTermSynonymMap = IndexerMap.getMaTermSynonyms(ontoDbConnection);
//
//            maChildMap = IndexerMap.getMaTermChildTerms(ontoDbConnection);
//            if (logger.isDebugEnabled()) {
//                IndexerMap.dumpOntologyMaTermMap(maChildMap, "Child map:");
//            }
//            maParentMap = IndexerMap.getMaTermParentTerms(ontoDbConnection);
//            if (logger.isDebugEnabled()) {
//                IndexerMap.dumpOntologyMaTermMap(maParentMap, "Parent map:");
//            }
//
//            maImagesMap = IndexerMap.getSangerImages(imagesCore);
//            if (logger.isDebugEnabled()) {
//                IndexerMap.dumpSangerImagesMap(maImagesMap, "Images map:", MAX_ITERATIONS);
//            }
//        } catch (SQLException e) {
//            throw new IndexerException(e);
//        }
    }

    public static void main(String[] args) throws IndexerException {
        GeneIndexer indexer = new GeneIndexer();
        indexer.initialise(args);
        indexer.run();

        logger.info("Process finished.  Exiting.");
    }
}