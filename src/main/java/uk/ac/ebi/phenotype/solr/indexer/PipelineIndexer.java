package uk.ac.ebi.phenotype.solr.indexer;

import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;
import uk.ac.ebi.phenotype.solr.indexer.utils.SangerProcedureMapper;
import uk.ac.ebi.phenotype.solr.indexer.utils.SolrUtils;

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
import uk.ac.ebi.phenotype.service.dto.MpDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.service.dto.PipelineDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;

import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;

/**
 * Populate the MA core
 */
public class PipelineIndexer extends AbstractIndexer {

	private static final Logger logger = LoggerFactory.getLogger(PipelineIndexer.class);
	private Connection komp2DbConnection;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Autowired
	@Qualifier("alleleIndexing")
	SolrServer alleleCore;

	// @Autowired
	// @Qualifier("geneIndexing")
	// SolrServer geneCore;
	//
	@Autowired
	@Qualifier("mpIndexing")
	SolrServer mpCore;

	@Autowired
	@Qualifier("pipelineIndexing")
	SolrServer pipelineCore;

	// @Autowired
	// @Qualifier("sangerImagesIndexing")
	// SolrServer imagesCore;

	// <dataSource name="allele_core" type="HttpDataSource"
	// baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/allele/select?"
	// encoding="UTF-8" connectionTimeout="10000" readTimeout="10000"/>
	// <dataSource name="mp_core" type="HttpDataSource"
	// baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/mp/select?"
	// encoding="UTF-8" connectionTimeout="10000" readTimeout="10000"/>
	// <dataSource name="pipeline_core" type="HttpDataSource"
	// baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/pipeline/select?"
	// encoding="UTF-8" connectionTimeout="10000" readTimeout="10000"/>
	// <dataSource name="images_core" type="HttpDataSource"
	// baseUrl="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/images/select?"
	// encoding="UTF-8" connectionTimeout="10000" readTimeout="10000"/>

	private Map<Integer, Map<String, String>> paramDbIdToParameter = null;
	private Map<Integer, Set<Integer>> paramIdToProcedureList = null;
	private Map<Integer, ProcedureBean> procedureIdToProcedure = null;
	private Map<Integer, PipelineBean> procedureIdToPipeline;
	private Map<String, List<GfMpBean>> pppidsToGfMpBeans;
	private Map<String, List<AlleleDTO>> mgiToAlleleMap;
	private Map<String, MpDTO> mpIdToMp;

	private static final int BATCH_SIZE = 50;


	public PipelineIndexer() {

		try {
			komp2DbConnection = komp2DataSource.getConnection();
		} catch (Exception e) {
			logger.error("Unable to get komp2DataSource: " + e.getLocalizedMessage());
		}
	}


	@Override
	public void initialise(String[] args)
	throws IndexerException {

		super.initialise(args);
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		try {
			DataSource komp2DS = ((DataSource) applicationContext.getBean("komp2DataSource"));
			this.komp2DbConnection = komp2DS.getConnection();
		} catch (SQLException sqle) {
			logger.error("Caught SQL Exception initialising database connections: {}", sqle.getMessage());
			throw new IndexerException(sqle);
		}
	}


	@Override
	public void run()
	throws IndexerException {

		long startTime = System.currentTimeMillis();
		try {
			logger.info("Starting Pipeline Indexer...");
			initialiseSupportingBeans();
			int count = 0;
			pipelineCore.deleteByQuery("*:*");

			for (Integer paramDbId : paramDbIdToParameter.keySet()) {
				// System.out.println("allele="+allele.getMarkerSymbol());
				Map<String, String> row = paramDbIdToParameter.get(paramDbId);
				PipelineDTO pipe = new PipelineDTO();
				pipe.setParameterId(paramDbId);
				pipe.setParameterName(row.get(ObservationDTO.PARAMETER_NAME));
				String paramStableId = row.get(ObservationDTO.PARAMETER_STABLE_ID);
				String paramStableName = row.get(ObservationDTO.PARAMETER_NAME);
				pipe.setParameterStableId(paramStableId);
				pipe.setParameterStableKey(row.get("stable_key"));

				Set<Integer> procedureIds = paramIdToProcedureList.get(paramDbId);
				// if(procedureIds.size()>1){System.out.println("more than one procedure for this parameterDbId"+paramDbId);
				for (int procId : procedureIds) {
					// where="pproc_id=phenotype_procedure_parameter.procedure_id">
					// need to change pipelineDTOs to have multiple procedures
					if (procedureIdToProcedure.containsKey(procId)) {
						ProcedureBean procBean = procedureIdToProcedure.get(procId);
						// System.out.println(procBean.procedureStableId);
						pipe.addProcedureId(procId);
						pipe.addProcedureName(procBean.procedureName);
						// System.out.println(procBean.procedureName+" "+procBean.procedureStableId+pipe.getParameterName());
						pipe.addProcedureStableId(procBean.procedureStableId);
						pipe.addProcedureStableKey(procBean.procedureStableKey);
						pipe.addProcedureNameId(procBean.procNameId);
						pipe.addMappedProcedureName(SangerProcedureMapper.getImpcProcedureFromSanger(procBean.procedureName));

						// <field column="proc_param_stable_id"
						// name="proc_param_stable_id" />
						// <field column="proc_param_name"
						// name="proc_param_name" />
						String procParamStableId = procBean.procedureStableId + "___" + paramStableId;
						String procParamName = procBean.procedureName + "___" + paramStableName;
						pipe.addProcParamStableId(procParamStableId);
						pipe.addProcParamName(procParamName);
						// add the pipeline info here
						if (procedureIdToPipeline.containsKey(procId)) {
							PipelineBean pipeline = procedureIdToPipeline.get(procId);
							// System.out.println("pipeline name="+pipeline.pipelineName+" "+pipeline.pipelineStableId);
							// <field column="pipe_id" name="pipeline_id" />
							// <field column="pipe_stable_id"
							// name="pipeline_stable_id" />
							// <field column="pipe_name" name="pipeline_name" />
							// <field column="pipe_stable_key"
							// name="pipeline_stable_key" />
							// <field column="pipe_proc_sid"
							// name="pipe_proc_sid" />
							pipe.setPipelineId(pipeline.pipelineId);
							pipe.setPipelineName(pipeline.pipelineName);
							pipe.setPipelineStableId(pipeline.pipelineStableId);
							pipe.setPipelineStableKey(pipeline.pipelineStableKey);
							pipe.setPipeProcId(pipeline.pipeProcSid);

							// select
							// concat(${phenotype_parameter.id},'_',${phenotype_pipeline_procedure.pproc_id},'_',${phenotype_pipeline_procedure.pipe_id})
							// as ididid">
							// <field column="ididid" name="ididid" />
							String ididid = paramDbId + "_" + procId + "_" + pipe.getPipelineId();
							pipe.setIdIdId(ididid);
							if (pppidsToGfMpBeans.containsKey(ididid)) {
								List<GfMpBean> gfMpBeanList = pppidsToGfMpBeans.get(ididid);
								for (GfMpBean gfMpBean : gfMpBeanList) {
									String mgiAccession = gfMpBean.gfAcc;
									System.out.println("adding " + mgiAccession);
									pipe.addMgiAccession(mgiAccession);
									if (mgiToAlleleMap.containsKey(mgiAccession)) {
										List<AlleleDTO> alleles = mgiToAlleleMap.get(mgiAccession);
										for (AlleleDTO allele : alleles) {
											System.out.println("allele mouse status=" + allele.getGeneLatestMouseStatus());
											if (allele.getMarkerSymbol() != null) {
												pipe.addMarkerType(allele.getMarkerType());
												pipe.addMarkerSymbol(allele.getMarkerSymbol());
												if (allele.getMarkerSynonym() != null) {
													pipe.addMarkerSynonym(allele.getMarkerSynonym());
												}
											}

											pipe.addMarkerName(allele.getMarkerName());
											if(allele.getHumanGeneSymbol()!=null){
											pipe.addHumanGeneSymbol(allele.getHumanGeneSymbol());
											}
											// /> <!-- status name from Bill
											// Skarnes and used at EBI -->
											pipe.addStatus(allele.getStatus());
											pipe.addImitsPhenotypeStarted(allele.getImitsPhenotypeStarted());
											pipe.addImitsPhenotypeComplete(allele.getImitsPhenotypeComplete());
											pipe.addImitsPhenotypeStatus(allele.getImitsPhenotypeStatus());
											if(allele.getLatestProductionCentre()!=null){
											pipe.addLatestProductionCentre(allele.getLatestProductionCentre());
											}
											if(allele.getLatestPhenotypingCentre()!=null){
											pipe.addLatestPhenotypingCentre(allele.getLatestPhenotypingCentre());
											}
											pipe.addLatestPhenotypingCentre(allele.getLatestPhenotypeStatus());
											pipe.addLegacyPhenotypingStatus(allele.getLatestPhenotypeStatus());
											pipe.addAlleleName(allele.getAlleleName());
										}											
									}
									//mps for parameter
									String mpTermId=gfMpBean.mpAcc;
									System.out.println("MPTerm="+mpTermId);
									MpDTO mp=mpIdToMp.get(mpTermId);
									System.out.println("mp anno higher level="+mp.getAnnotatedHigherLevelMpTermName());
									pipe.addMpId(mpTermId);
//									<field column="mp_id" xpath="/response/result/doc/str[@name='mp_id']" />	
									pipe.addMpTerm(mp.getMpTerm());
//									<field column="mp_term" xpath="/response/result/doc/str[@name='mp_term']" />
									pipe.addMpDefinition(mp.getMpDefinition());
//									<field column="mp_definition" xpath="/response/result/doc/str[@name='mp_definition']" />
									if(mp.getMpTermSynonym()!=null){
									pipe.addMpTermSynonym(mp.getMpTermSynonym());
									}
//									<field column="mp_term_synonym" xpath="/response/result/doc/arr[@name='mp_term_synonym']/str" />
									if(mp.getOntologySubset()!=null){
									pipe.addOntologySubset(mp.getOntologySubset());
									}
//									<field column="ontology_subset" xpath="/response/result/doc/arr[@name='ontology_subset']/str" />
									if(mp.getTopLevelMpTermId()!=null){
									pipe.addTopLevelMpId(mp.getTopLevelMpTermId());
									}
//									
//									<field column="top_level_mp_id" xpath="/response/result/doc/arr[@name='top_level_mp_id']/str" />
									pipe.addTopLevelMpTerm(mp.getTopLevelMpTerm());
//									<field column="top_level_mp_term" xpath="/response/result/doc/arr[@name='top_level_mp_term']/str" />
									if(mp.getTopLevelMpTermSynonym()!=null){
									pipe.addTopLevelMpTermSynonym(mp.getTopLevelMpTermSynonym());
									}
//									<field column="top_level_mp_term_synonym" xpath="/response/result/doc/arr[@name='top_level_mp_term_synonym']/str" />					
									pipe.addIntermediateMpId(mp.getIntermediateMpId());
									
//									<field column="intermediate_mp_id" xpath="/response/result/doc/arr[@name='intermediate_mp_id']/str" />
									pipe.addIntermediateMpTerm(mp.getIntermediateMpTerm());
									//<field column="intermediate_mp_term" xpath="/response/result/doc/arr[@name='intermediate_mp_term']/str" />							
									pipe.addIntermediateMpTermSynonym(mp.getIntermediateMpTermSynonym());
									//<field column="intermediate_mp_term_synonym" xpath="/response/result/doc/arr[@name='intermediate_mp_term_synonym']/str" />					
									pipe.addChildMpId(mp.getChildMpId());
//									<field column="child_mp_id" xpath="/response/result/doc/arr[@name='child_mp_id']/str" />
									pipe.addChildMpTerm(mp.getChildMpTerm());
//									<field column="child_mp_term" xpath="/response/result/doc/arr[@name='child_mp_term']/str" />
									if(mp.getChildMaTermSynonym()!=null){
									pipe.addChildMpTermSynonym(mp.getChildMaTermSynonym());
									}
//									<field column="child_mp_term_synonym" xpath="/response/result/doc/arr[@name='child_mp_term_synonym']/str" />					
									pipe.addHpId(mp.getHpId());
//									<field column="hp_id" xpath="/response/result/doc/arr[@name='hp_id']/str" />
									pipe.addHpTerm(mp.getHpTerm());
//									<field column="hp_term" xpath="/response/result/doc/arr[@name='hp_term']/str" />
									if(mp.getInferredMaId()!=null){
										pipe.addInferredMaId(mp.getInferredMaId());
//										<!-- MA: inferred from MP -->
//										<field column="inferred_ma_id" xpath="/response/result/doc/arr[@name='inferred_ma_id']/str" />
										pipe.addInferredMaTerm(mp.getInferredMaTerm());
//										<field column="inferred_ma_term" xpath="/response/result/doc/arr[@name='inferred_ma_term']/str" />
										pipe.addInferredMaTermSynonym(mp.getInferredMaTermSynonym());
									}
//									<field column="inferred_ma_term_synonym" xpath="/response/result/doc/arr[@name='inferred_ma_term_synonym']/str" />
									if(mp.getInferredSelectedTopLevelMaId()!=null){
										pipe.addInferredSelectedTopLevelMaId(mp.getInferredSelectedTopLevelMaId());
//										<field column="inferred_selected_top_level_ma_id" xpath="/response/result/doc/arr[@name='inferred_selected_top_level_ma_id']/str" />
										pipe.addInferredSelectedTopLevelMaTerm(mp.getInferredSelectedTopLevelMaTerm());
//										<field column="inferred_selected_top_level_ma_term" xpath="/response/result/doc/arr[@name='inferred_selected_top_level_ma_term']/str" />				
									pipe.addInferredSelectedToLevelMaTermSynonym(mp.getInferredSelectedTopLevelMaTermSynonym());
//									<field column="inferred_selected_top_level_ma_term_synonym" xpath="/response/result/doc/arr[@name='inferred_selected_top_level_ma_term_synonym']/str" />				

									}
									if(mp.getInferredChildMaId()!=null){
										pipe.addInferredChildMaId(mp.getInferredChildMaId());
//										<field column="inferred_child_ma_id" xpath="/response/result/doc/arr[@name='inferred_child_ma_id']/str" />
										pipe.addInferredChildMaTerm(mp.getInferredChildMaTerm());
//										<field column="inferred_child_ma_term" xpath="/response/result/doc/arr[@name='inferred_child_ma_term']/str" />
										pipe.addInferredChildMaTermSynonyms(mp.getInferredChildMaTermSynonym());
//										<field column="inferred_child_ma_term_synonym" xpath="/response/result/doc/arr[@name='inferred_child_ma_term_synonym']/str" />				
									}
								}

							}

						}
					}
				}
				// }
				// pipe.setPipelineName(pipelineName);
				// pipe.setPipelineStableId(pipelineStableId);
				// pipe.setPipelineId(pipelineId);

				pipelineCore.addBean(pipe, 60000);
				count++;

				if (count % 10 == 0) {
					System.out.println(" added " + count + " beans");
				}
				// if(count>100)break;
			}

			System.out.println("commiting to Pipeline core for last time!");
			pipelineCore.commit();

		} catch (IOException | SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IndexerException(e);
		}

		long endTime = System.currentTimeMillis();
		System.out.println("time was " + (endTime - startTime) / 1000);

		logger.info("Pipeline Indexer complete!");
		System.exit(0);
	}


	// PROTECTED METHODS

	@Override
	protected Logger getLogger() {

		return logger;
	}


	private void initialiseSupportingBeans()
	throws IndexerException {

		paramDbIdToParameter = populateParamDbIdToParametersMap();
		paramIdToProcedureList = populateParamIdToProcedureIdListMap();
		procedureIdToProcedure = populateProcedureIdToProcedureMap();
		procedureIdToPipeline = populateProcedureIdToPipelineMap();
		pppidsToGfMpBeans = populateGfAccAndMp();
		mgiToAlleleMap = IndexerMap.getGeneToAlleles(alleleCore);
		mpIdToMp=populateMpIdToMp();

	}


	private Map<Integer, Map<String, String>> populateParamDbIdToParametersMap() {

		System.out.println("populating PCS pipeline info");
		Map<Integer, Map<String, String>> localParamDbIdToParameter = new HashMap<>();
		String queryString = "select 'pipeline' as dataType, id, stable_id, name, stable_key from phenotype_parameter";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				Map<String, String> rowMap = new HashMap<>();// store the row in
																// a map of
																// column names
																// to values
				// <field column="dataType" name="dataType" />
				// <field column="id" name="parameter_id" />
				// <field column="stable_id" name="parameter_stable_id" />
				// <field column="name" name="parameter_name" />
				// <field column="stable_key" name="parameter_stable_key" />
				int id = resultSet.getInt("id");
				rowMap.put(ObservationDTO.PARAMETER_NAME, resultSet.getString("name"));
				rowMap.put(ObservationDTO.PARAMETER_STABLE_ID, resultSet.getString("stable_id"));
				rowMap.put("dataType", resultSet.getString("dataType"));
				rowMap.put("stable_key", resultSet.getString("stable_key"));
				// rowMap.put(ObservationDTO.PIPELINE_STABLE_ID,
				// resultSet.getString("pipe.stable_id"));
				// rowMap.put(ObservationDTO.PIPELINE_NAME,
				// resultSet.getString("pipe.name"));
				// rowMap.put("proc_param_name",
				// resultSet.getString("proc.name")+"___"+resultSet.getString("param.name"));
				// rowMap.put("proc_param_stable_id",
				// resultSet.getString("proc.stable_id")+"___"+resultSet.getString("param.stable_id"));
				// List<Map<String,String>> rows=null;

				localParamDbIdToParameter.put(id, rowMap);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return localParamDbIdToParameter;

	}


	private Map<Integer, Set<Integer>> populateParamIdToProcedureIdListMap() {

		System.out.println("populating PCS pipeline info");
		Map<Integer, Set<Integer>> paramToProcedureMap = new HashMap<>();
		String queryString = "select procedure_id, parameter_id from phenotype_procedure_parameter";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				Set<Integer> procedureIds = new HashSet<>();// store the row in
															// a map of column
															// names to values
				int paramId = resultSet.getInt("parameter_id");
				int procId = resultSet.getInt("procedure_id");
				if (paramToProcedureMap.containsKey(paramId)) {
					procedureIds = paramToProcedureMap.get(paramId);
				} else {
					procedureIds = new HashSet<>();// store the row in a map of
													// column names to values
				}
				procedureIds.add(procId);
				paramToProcedureMap.put(paramId, procedureIds);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramToProcedureMap;
	}


	private Map<Integer, ProcedureBean> populateProcedureIdToProcedureMap() {

		System.out.println("populating PCS pipeline info");
		Map<Integer, ProcedureBean> procedureIdToProcedureMap = new HashMap<>();
		String queryString = "select id as pproc_id, stable_id, name, stable_key, concat(name, '___', stable_id) as proc_name_id from phenotype_procedure";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ProcedureBean proc = new ProcedureBean();

				int procId = resultSet.getInt("pproc_id");
				String procStableId = resultSet.getString("stable_id");
				String procName = resultSet.getString("name");
				int stableKey = resultSet.getInt("stable_key");
				String procNameId = resultSet.getString("proc_name_id");
				proc.procedureStableId = procStableId;
				proc.procedureName = procName;
				proc.procedureStableKey = stableKey;
				proc.procNameId = procNameId;
				procedureIdToProcedureMap.put(procId, proc);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return procedureIdToProcedureMap;
	}

	public class ProcedureBean {

		public String procedureName;
		int procedureId;
		String procedureStableId;
		int procedureStableKey;
		String procNameId;

	}


	// select pproc.id as pproc_id, ppipe.name as pipe_name, ppipe.id as
	// pipe_id, ppipe.stable_id as pipe_stable_id, ppipe.stable_key as
	// pipe_stable_key, concat(ppipe.name, '___', pproc.name, '___',
	// pproc.stable_id) as pipe_proc_sid from phenotype_procedure pproc inner
	// join phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id
	// inner join phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id where
	// ppipe.db_id=6
	private Map<Integer, PipelineBean> populateProcedureIdToPipelineMap() {

		System.out.println("populating PCS pipeline info");
		Map<Integer, PipelineBean> procIdToPipelineMap = new HashMap<>();
		String queryString = "select pproc.id as pproc_id, ppipe.name as pipe_name, ppipe.id as pipe_id, ppipe.stable_id as pipe_stable_id, ppipe.stable_key as pipe_stable_key, concat(ppipe.name, '___', pproc.name, '___', pproc.stable_id) as pipe_proc_sid from phenotype_procedure pproc inner join phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id inner join phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id where ppipe.db_id=6";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				PipelineBean pipe = new PipelineBean();

				int procedureId = resultSet.getInt("pproc_id");
				String pipeName = resultSet.getString("pipe_name");
				int pipeId = resultSet.getInt("pipe_id");
				String pipeStableId = resultSet.getString("pipe_stable_id");
				int pipeStableKey = resultSet.getInt("pipe_stable_key");
				String pipeProcSid = resultSet.getString("pipe_proc_sid");
				pipe.pipelineId = pipeId;
				pipe.pipelineName = pipeName;
				pipe.pipelineStableKey = pipeStableKey;
				pipe.pipelineStableId = pipeStableId;
				pipe.pipeProcSid = pipeProcSid;
				procIdToPipelineMap.put(procedureId, pipe);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return procIdToPipelineMap;
	}

	public class PipelineBean {

		public String pipelineName;
		int pipelineId;
		String pipelineStableId;
		int pipelineStableKey;
		String pipeProcSid;
	}


	private Map<String, List<GfMpBean>> populateGfAccAndMp() {

		System.out.println("populating PCS pipeline info");
		Map<String, List<GfMpBean>> gfMpBeansMap = new HashMap<>();
		String queryString = "select distinct concat(s.parameter_id,'_',s.procedure_id,'_',s.pipeline_id) as pppIds, s.gf_acc, s.mp_acc, s.parameter_id as pp_parameter_id, s.procedure_id as pproc_procedure_id, s.pipeline_id as ppipe_pipeline_id, s.allele_acc, s.strain_acc from phenotype_parameter pp INNER JOIN phenotype_procedure_parameter ppp on pp.id=ppp.parameter_id INNER JOIN phenotype_procedure pproc on ppp.procedure_id=pproc.id INNER JOIN phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id INNER JOIN phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id inner join phenotype_call_summary s on ppipe.id=s.pipeline_id and pproc.id=s.procedure_id and pp.id=s.parameter_id";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				GfMpBean gfMpBean = new GfMpBean();

				String pppids = resultSet.getString("pppids");
				String gfAcc = resultSet.getString("gf_acc");
				String mpAcc = resultSet.getString("mp_acc");
				// String alleleAcc=resultSet.getString("allele_acc");//doesn't
				// look like these are needed?
				// String strainAcc=resultSet.getString("strain_acc");
				gfMpBean.gfAcc = gfAcc;
				gfMpBean.mpAcc = mpAcc;
				List<GfMpBean> beanList = new ArrayList<>();
				if (gfMpBeansMap.containsKey(pppids)) {
					beanList = gfMpBeansMap.get(pppids);
				}
				beanList.add(gfMpBean);
				gfMpBeansMap.put(pppids, beanList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return gfMpBeansMap;
	}

	class GfMpBean {

		String gfAcc;
		String mpAcc;

	}

	private Map<String, MpDTO> populateMpIdToMp() throws IndexerException{
		return SolrUtils.populateMpTermIdToMp(mpCore);
	}

	public static void main(String[] args)
	throws IndexerException {

		PipelineIndexer indexer = new PipelineIndexer();
		indexer.initialise(args);
		indexer.run();

		logger.info("Process finished.  Exiting.");
	}
}
