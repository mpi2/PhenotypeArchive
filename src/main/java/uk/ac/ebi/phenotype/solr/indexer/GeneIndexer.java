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
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
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
    
    @Autowired
    @Qualifier("mpIndexing")
    SolrServer mpCore;
    
    @Autowired
    @Qualifier("pipelineIndexing")
    SolrServer pipelineCore;
    
    @Autowired
    @Qualifier("sangerImagesIndexing")
    SolrServer imagesCore;

    
//    <dataSource name="allele_core" type="HttpDataSource" baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/allele/select?" encoding="UTF-8"  connectionTimeout="10000" readTimeout="10000"/>
// 	<dataSource name="mp_core" type="HttpDataSource" baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/mp/select?" encoding="UTF-8"  connectionTimeout="10000" readTimeout="10000"/>	
//	<dataSource name="pipeline_core" type="HttpDataSource" baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/pipeline/select?" encoding="UTF-8"  connectionTimeout="10000" readTimeout="10000"/>    
//	<dataSource name="images_core" type="HttpDataSource" baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/images/select?" encoding="UTF-8"  connectionTimeout="10000" readTimeout="10000"/>	

    Map<String,List< Map<String,String>>> phenotypeSummaryGeneAccessionsToPipelineInfo=new HashMap<>();
    Map<String, List<SangerImageDTO>> sangerImages=new HashMap<>();
    
//    private Map<String, List<String>> ontologySubsetMap = new HashMap();        // key = term_id.
//    private Map<String, List<String>> maTermSynonymMap = new HashMap();         // key = term_id.
//    private Map<String, List<OntologyTermBean>> maChildMap = new HashMap();     // key = parent term_id.
//    private Map<String, List<OntologyTermBean>> maParentMap = new HashMap();    // key = child term_id.
//    private Map<String, List<SangerImageDTO>> maImagesMap = new HashMap();      // key = term_id.
//    
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
    	long startTime = System.currentTimeMillis();
    	try {
            logger.info("Starting Gene Indexer...");
            
            initialiseSupportingBeans();
            
            int count=0;
            List<AlleleDTO> alleles = IndexerMap.getAlleles(alleleCore);
            System.out.println("alleles size="+alleles.size());
            
            geneCore.deleteByQuery("*:*");
          
            for(AlleleDTO allele:alleles){
            	//System.out.println("allele="+allele.getMarkerSymbol());
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
            	gene.setDiseaseHumanPhenotypes(allele.getDiseaseHumanPhenotypes());
            	
            	//gene.setMpId(allele.getM)
            	
            	//populate pipeline and procedure info if we have a phenotypeCallSummary entry for this allele/gene
            	if(phenotypeSummaryGeneAccessionsToPipelineInfo.containsKey(allele.getMgiAccessionId())){
            		List<Map<String, String>> rows = phenotypeSummaryGeneAccessionsToPipelineInfo.get(allele.getMgiAccessionId());
            		List<String> pipelineNames=new ArrayList<>();
            		List<String> pipelineStableIds=new ArrayList<>();
            		List<String> procedureNames=new ArrayList<>();
            		List<String> procedureStableIds=new ArrayList<>();
            		List<String> parameterNames=new ArrayList<>();
            		List<String> parameterStableIds=new ArrayList<>();
            		for(Map<String,String> row: rows){
            			pipelineNames.add(row.get(ObservationDTO.PIPELINE_NAME));
            			pipelineStableIds.add(row.get(ObservationDTO.PIPELINE_STABLE_ID));
            			procedureNames.add(row.get(ObservationDTO.PROCEDURE_NAME));
            			procedureStableIds.add(row.get(ObservationDTO.PROCEDURE_STABLE_ID));
            			parameterNames.add(row.get(ObservationDTO.PARAMETER_NAME));
            			parameterStableIds.add(row.get(ObservationDTO.PARAMETER_STABLE_ID));
            			
            		}
            		gene.setPipelineName(pipelineNames);
            		gene.setPipelineStableId(pipelineStableIds);
            		gene.setProcedureName(procedureNames);
            		gene.setProcedureStableId(procedureStableIds);
            		gene.setParameterName(parameterNames);
            		gene.setParameterStableId(parameterStableIds);
            		//gene.setPipelineName(row.get(ObservationDTO.PIPELINE_NAME));
            		//gene.
            	}
            	
            	//do images core data
            	
            	if(sangerImages.containsKey(gene.getMgiAccessionId())){
            		List<String>mpIds=new ArrayList<>();
            		List<String> mpTerms=new ArrayList<>();
            		List<String> mpSyns=new ArrayList<>();
            		List<SangerImageDTO> list = sangerImages.get(gene.getMgiAccessionId());
            		for(SangerImageDTO image: list){
            			
//            			<field column="mp_id" xpath="/response/result/doc/arr[@name='mp_id']/str" />
//    					<field column="mp_term" xpath="/response/result/doc/arr[@name='mp_term']/str" />
//    					<field column="mp_term_synonym" xpath="/response/result/doc/arr[@name='mp_term_synonym']/str" />
            			if(image.getMp_id()!=null){
            			mpIds.addAll(image.getMp_id());
						mpTerms.addAll(image.getMpTerm());
						if(image.getMpSyns()!=null){
							mpSyns.addAll(image.getMpSyns());
						}
//    					<field column="intermediate_mp_id" xpath="/response/result/doc/arr[@name='intermediate_mp_id']/str" />
//    					<field column="intermediate_mp_term" xpath="/response/result/doc/arr[@name='intermediate_mp_term']/str" />							
//    					<field column="intermediate_mp_term_synonym" xpath="/response/result/doc/arr[@name='intermediate_mp_term_synonym']/str" />					
//    					<field column="annotatedHigherLevelMpTermName" xpath="/response/result/doc/arr[@name='annotatedHigherLevelMpTermName']/str" />
//    					<field column="top_level_mp_term_synonym" xpath="/response/result/doc/arr[@name='annotatedHigherLevelMpTermName']/str" />
						if(image.getIntermediateMpId()!=null){
						gene.setIntermediateMpId(image.getIntermediateMpId());
						gene.setIntermediateMpTerm(image.getIntermediateMpTerm());
						gene.setIntermediateMpSynonym(image.getIntermediateMpTermSyn());
						gene.setAnnotatedHigherLevelMaTermName(image.getAnnotatedHigherLevelMpTermName());
						//gene.setTopLevelMpSynonym(image.getTopLevelMpTermSynonym());
						}
						
						
            			}
            			
            			if(image.getMaTermId()!=null){            			
//    					<field column="ma_id" xpath="/response/result/doc/arr[@name='ma_id']/str" />
//    					<field column="ma_term" xpath="/response/result/doc/arr[@name='ma_term']/str" />
//    					<field column="ma_term_synonym" xpath="/response/result/doc/arr[@name='ma_term_synonym']/str" />
            			//gene.setMaTermId(image.getMaTermId());
            			//doesn't look like the ma_id and ma_term are used in the gene index so not adding these - selected top level mas used only
            			
            				
//    					<field column="selected_top_level_ma_id" xpath="/response/result/doc/arr[@name='selected_top_level_ma_id']/str" />
//    					<field column="selected_top_level_ma_term" xpath="/response/result/doc/arr[@name='selected_top_level_ma_term']/str" />				
//    					<field column="selected_top_level_ma_term_synonym" xpath="/response/result/doc/arr[@name='selected_top_level_ma_term_synonym']/str" />				
//    					
//    					<field column="annotatedHigherLevelMaTermName" xpath="/response/result/doc/arr[@name='annotatedHigherLevelMaTermName']/str" />
            				//gene.setSelectedTopLevelMaTermId(image.getSelectedTopLevelMaTermId());
            				if(image.getSelectedTopLevelMaTerm()!=null){
            					gene.setSelectedTopLevelMaTerm(image.getSelectedTopLevelMaTerm());
            					gene.setSelectedTopLevelMaTermId(image.getSelectedTopLevelMaTermId());
            					gene.setSelectedTopLevelMaTermSynonym(image.getSelectedTopLevelMaTermSynonym());
            					gene.setAnnotatedHigherLevelMaTermName(image.getAnnotatedHigherLevelMpTermName());
            				}
            			}
            		}
            		
            		gene.setMpId(mpIds);
            	}
            	geneCore.addBean(gene, 60000);
            	count++;

				if (count % 10000 == 0) {
					System.out.println(" added " + count + " beans");
				}
				//if(count>100)break;
            }
           
            System.out.println("commiting to gene core for last time!");
            geneCore.commit();
            
            } catch (IOException | SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new IndexerException(e);
			}

    	long endTime = System.currentTimeMillis();
		System.out.println("time was " + (endTime - startTime) / 1000);

        logger.info("Gene Indexer complete!");
        System.exit(0);
    }
    
    
    // PROTECTED METHODS
    
    
    @Override
    protected Logger getLogger() {
        return logger;
    }
    
    
    // PRIVATE METHODS
    
    
    private final Integer MAX_ITERATIONS = 5;                                   // Set to non-null value > 0 to limit max_iterations.
    
    private void initialiseSupportingBeans() throws IndexerException {
    	
    	populatePhenotypeCallSummaryGeneAccessions();
    	sangerImages = IndexerMap.getSangerImagesByMgiAccession(imagesCore);
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
    
    private void populatePhenotypeCallSummaryGeneAccessions(){
    	System.out.println("populating PCS pipeline info");
    	String queryString="select pcs.*, param.name, param.stable_id, proc.stable_id, proc.name, pipe.stable_id, pipe.name"+
    	" from phenotype_call_summary pcs"+
    	" inner join ontology_term term on term.acc=mp_acc"+
    	" inner join genomic_feature gf on gf.acc=pcs.gf_acc"+
    	" inner join phenotype_parameter param on param.id=pcs.parameter_id"+
    	" inner join phenotype_procedure proc on proc.id=pcs.procedure_id"+
    	" inner join phenotype_pipeline pipe on pipe.id=pcs.pipeline_id";
 
    	try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				String gf_acc = resultSet.getString("gf_acc");
				Map<String,String> rowMap=new HashMap<>();//store the row in a map of column names to values
//				<field column="pipeline_name" xpath="/response/result/doc/str[@name='pipeline_name']" />	
//				<field column="procedure_name" xpath="/response/result/doc/str[@name='procedure_name']" />	
//				<field column="parameter_name" xpath="/response/result/doc/str[@name='parameter_name']" />	
//				<field column="pipeline_stable_id" xpath="/response/result/doc/str[@name='pipeline_stable_id']" />	
//				<field column="procedure_stable_id" xpath="/response/result/doc/str[@name='procedure_stable_id']" />	
//				<field column="parameter_stable_id" xpath="/response/result/doc/str[@name='parameter_stable_id']" />
//				<field column="proc_param_name" xpath="/response/result/doc/str[@name='proc_param_name']" />
//				<field column="proc_param_stable_id" xpath="/response/result/doc/str[@name='proc_param_stable_id']" />
				
				rowMap.put(ObservationDTO.PARAMETER_NAME, resultSet.getString("param.name"));
				rowMap.put(ObservationDTO.PARAMETER_STABLE_ID, resultSet.getString("param.stable_id"));
				rowMap.put(ObservationDTO.PROCEDURE_STABLE_ID, resultSet.getString("proc.stable_id"));
				rowMap.put(ObservationDTO.PROCEDURE_NAME, resultSet.getString("proc.name"));
				rowMap.put(ObservationDTO.PIPELINE_STABLE_ID, resultSet.getString("pipe.stable_id"));
				rowMap.put(ObservationDTO.PIPELINE_NAME, resultSet.getString("pipe.name"));
				rowMap.put("proc_param_name", resultSet.getString("proc.name")+"___"+resultSet.getString("param.name"));
				rowMap.put("proc_param_stable_id", resultSet.getString("proc.stable_id")+"___"+resultSet.getString("param.stable_id"));
				List<Map<String,String>> rows=null;
				if(phenotypeSummaryGeneAccessionsToPipelineInfo.containsKey(gf_acc)){
					rows=phenotypeSummaryGeneAccessionsToPipelineInfo.get(gf_acc);
				}else{
					rows=new ArrayList<Map<String,String>>();
				}
				rows.add(rowMap);
				
				phenotypeSummaryGeneAccessionsToPipelineInfo.put(gf_acc, rows);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    
    }

    public static void main(String[] args) throws IndexerException {
        GeneIndexer indexer = new GeneIndexer();
        indexer.initialise(args);
        indexer.run();

        logger.info("Process finished.  Exiting.");
    }
}