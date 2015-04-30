package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.MpDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.service.dto.PipelineDTO;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.ValidationException;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;
import uk.ac.ebi.phenotype.solr.indexer.utils.SangerProcedureMapper;
import uk.ac.ebi.phenotype.solr.indexer.utils.SolrUtils;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Populate the MA core
 */
public class PipelineIndexer extends AbstractIndexer {

	private static final Logger logger = LoggerFactory
			.getLogger(PipelineIndexer.class);
	private Connection komp2DbConnection;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Autowired
	@Qualifier("alleleIndexing")
	SolrServer alleleCore;

	@Autowired
	@Qualifier("mpIndexing")
	SolrServer mpCore;

	@Autowired
	@Qualifier("pipelineIndexing")
	SolrServer pipelineCore;

	private Map<Integer, Map<String, String>> paramDbIdToParameter = null;
	private Map<Integer, Set<Integer>> procedureIdToParams = null;
	private Map<Integer, ProcedureBean> procedureIdToProcedure = null;
	private List<PipelineBean> pipelines;
	private Map<String, List<GfMpBean>> pppidsToGfMpBeans;
	private Map<String, List<AlleleDTO>> mgiToAlleleMap;
	private Map<String, MpDTO> mpIdToMp;
	private Map<String, String> parameterStableIdToMaTermIdMap;
	protected static final int MINIMUM_DOCUMENT_COUNT = 10;

	public PipelineIndexer() {

	}

	@Override
	public void validateBuild() throws IndexerException {
		Long numFound = getDocumentCount(pipelineCore);

		if (numFound <= MINIMUM_DOCUMENT_COUNT)
			throw new IndexerException(new ValidationException(
					"Actual pipeline document count is " + numFound + "."));

		if (numFound != documentCount)
			logger.warn("WARNING: Added " + documentCount
					+ " pipeline documents but SOLR reports " + numFound
					+ " documents.");
		else
			logger.info("validateBuild(): Indexed " + documentCount
					+ " pipeline documents.");
	}

	@Override
	public void initialise(String[] args) throws IndexerException {

		super.initialise(args);

		try {
			this.komp2DbConnection = komp2DataSource.getConnection();
		} catch (SQLException sqle) {
			logger.error(
					"Caught SQL Exception initialising database connections: {}",
					sqle.getMessage());
			throw new IndexerException(sqle);
		}
	}

	private void initialiseSupportingBeans() throws IndexerException {

		paramDbIdToParameter = populateParamDbIdToParametersMap();
		procedureIdToParams = populateParamIdToProcedureIdListMap();
		procedureIdToProcedure = populateProcedureIdToProcedureMap();
		pipelines = populateProcedureIdToPipelineMap();
		System.out.println(pipelines);
		pppidsToGfMpBeans = populateGfAccAndMp();
		mgiToAlleleMap = IndexerMap.getGeneToAlleles(alleleCore);
		mpIdToMp = populateMpIdToMp();

	}

	@Override
	public void run() throws IndexerException {

		long startTime = System.currentTimeMillis();

		try {

			logger.info("Starting Pipeline Indexer...");

			initialiseSupportingBeans();
			pipelineCore.deleteByQuery("*:*");
			pipelineCore.commit();

			for (PipelineBean pipeline : pipelines) {

				Set<Integer> parameterIds = procedureIdToParams
						.get(pipeline.pipelineId);

				for (int paramDbId : parameterIds) {
					PipelineDTO pipe = new PipelineDTO();// new pipe object for
															// each param
					Map<String, String> row = paramDbIdToParameter
							.get(paramDbId);
					pipe.setParameterId(paramDbId);
					pipe.setParameterName(row
							.get(ObservationDTO.PARAMETER_NAME));
					String paramStableId = row
							.get(ObservationDTO.PARAMETER_STABLE_ID);
					String paramStableName = row
							.get(ObservationDTO.PARAMETER_NAME);
					pipe.setParameterStableId(paramStableId);
					System.out.println("parameterStableId="+paramStableId);
					pipe.setParameterStableKey(row.get("stable_key"));
					// where="pproc_id=phenotype_procedure_parameter.procedure_id">
					// need to change pipelineDTOs to have multiple procedures

					ProcedureBean procBean = procedureIdToProcedure
							.get(pipeline.procedureId);
					// System.out.println(procBean.procedureStableId);
					pipe.addProcedureId(pipeline.procedureId);
					pipe.addProcedureName(procBean.procedureName);
					// System.out.println(procBean.procedureName+" "+procBean.procedureStableId+pipe.getParameterName());
					pipe.addProcedureStableId(procBean.procedureStableId);
					pipe.addProcedureStableKey(procBean.procedureStableKey);
					pipe.addProcedureNameId(procBean.procNameId);
					pipe.addMappedProcedureName(SangerProcedureMapper
							.getImpcProcedureFromSanger(procBean.procedureName));

					// <field column="proc_param_stable_id"
					// name="proc_param_stable_id" />
					// <field column="proc_param_name"
					// name="proc_param_name" />
					String procParamStableId = procBean.procedureStableId
							+ "___" + paramStableId;
					String procParamName = procBean.procedureName + "___"
							+ paramStableName;
					// System.out.println(procBean.procedureStableId + "___" +
					// paramStableId);
					pipe.addProcParamStableId(procParamStableId);
					pipe.addProcParamName(procParamName);
					// add the pipeline info here

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
					pipe.addPipelineName(pipeline.pipelineName);
					pipe.addPipelineStableId(pipeline.pipelineStableId);
					pipe.addPipelineStableKey(pipeline.pipelineStableKey);
					pipe.addPipeProcId(pipeline.pipeProcSid);

					//changed the ididid to be pipe proc param stable id combination that should be unique and is unique in solr					
					String ididid = pipeline.pipelineStableId + "_" + procBean.procedureStableId
							+ "_" + paramStableId;
					String idididKey = paramDbId + "_" + pipeline.pipeProcSid
							+ "_" + pipe.getPipelineId();
					pipe.setIdIdId(ididid);
					if (pppidsToGfMpBeans.containsKey(idididKey)) {
						List<GfMpBean> gfMpBeanList = pppidsToGfMpBeans
								.get(idididKey);
						for (GfMpBean gfMpBean : gfMpBeanList) {
							String mgiAccession = gfMpBean.gfAcc;
							pipe.addMgiAccession(mgiAccession);
							if (mgiToAlleleMap.containsKey(mgiAccession)) {
								List<AlleleDTO> alleles = mgiToAlleleMap
										.get(mgiAccession);
								for (AlleleDTO allele : alleles) {
									if (allele.getMarkerSymbol() != null) {
										pipe.addMarkerType(allele
												.getMarkerType());
										pipe.addMarkerSymbol(allele
												.getMarkerSymbol());
										if (allele.getMarkerSynonym() != null) {
											pipe.addMarkerSynonym(allele
													.getMarkerSynonym());
										}
									}

									pipe.addMarkerName(allele.getMarkerName());
									if (allele.getHumanGeneSymbol() != null) {
										pipe.addHumanGeneSymbol(allele
												.getHumanGeneSymbol());
									}
									// /> <!-- status name from Bill
									// Skarnes and used at EBI -->
									pipe.addStatus(allele.getStatus());
									pipe.addImitsPhenotypeStarted(allele
											.getImitsPhenotypeStarted());
									pipe.addImitsPhenotypeComplete(allele
											.getImitsPhenotypeComplete());
									pipe.addImitsPhenotypeStatus(allele
											.getImitsPhenotypeStatus());
									if (allele.getLatestProductionCentre() != null) {
										pipe.addLatestProductionCentre(allele
												.getLatestProductionCentre());
									}
									if (allele.getLatestPhenotypingCentre() != null) {
										pipe.addLatestPhenotypingCentre(allele
												.getLatestPhenotypingCentre());
									}
									pipe.addLatestPhenotypingCentre(allele
											.getLatestPhenotypeStatus());
									pipe.addLegacyPhenotypingStatus(allele
											.getLatestPhenotypeStatus());
									pipe.addAlleleName(allele.getAlleleName());
								}
							}
							// mps for parameter

							String mpTermId = gfMpBean.mpAcc;
							MpDTO mp = mpIdToMp.get(mpTermId);

							// <field column="mp_id"
							// xpath="/response/result/doc/str[@name='mp_id']"
							// />
							pipe.addMpId(mpTermId);

							if (mp != null) {

								// <field column="mp_term"
								// xpath="/response/result/doc/str[@name='mp_term']"
								// />
								pipe.addMpTerm(mp.getMpTerm());

								// <field column="mp_term_synonym"
								// xpath="/response/result/doc/arr[@name='mp_term_synonym']/str"
								// />
								if (mp.getMpTermSynonym() != null) {
									pipe.addMpTermSynonym(mp.getMpTermSynonym());
								}

								// <field column="ontology_subset"
								// xpath="/response/result/doc/arr[@name='ontology_subset']/str"
								// />
								if (mp.getOntologySubset() != null) {
									pipe.addOntologySubset(mp
											.getOntologySubset());
								}
								if (mp.getTopLevelMpTermId() != null) {
									pipe.addTopLevelMpId(mp
											.getTopLevelMpTermId());
								} else {
									logger.warn("topLevelMpTermId for mpTerm "
											+ mpTermId + " is null!");
								}
								// <field column="top_level_mp_id"
								// xpath="/response/result/doc/arr[@name='top_level_mp_id']/str"
								// />
								if (mp.getTopLevelMpTerm() != null) {
									pipe.addTopLevelMpTerm(mp
											.getTopLevelMpTerm());
								} else {
									logger.warn("topLevelMpTerm for mpTerm "
											+ mpTermId + " is null!");
								}
								// <field column="top_level_mp_term"
								// xpath="/response/result/doc/arr[@name='top_level_mp_term']/str"
								// />
								if (mp.getTopLevelMpTermSynonym() != null) {
									pipe.addTopLevelMpTermSynonym(mp
											.getTopLevelMpTermSynonym());
								}
								// <field column="top_level_mp_term_synonym"
								// xpath="/response/result/doc/arr[@name='top_level_mp_term_synonym']/str"
								// />
								if (mp.getIntermediateMpId() != null) {
									pipe.addIntermediateMpId(mp
											.getIntermediateMpId());
								}

								// <field column="intermediate_mp_id"
								// xpath="/response/result/doc/arr[@name='intermediate_mp_id']/str"
								// />
								if (mp.getIntermediateMpTerm() != null) {
									pipe.addIntermediateMpTerm(mp
											.getIntermediateMpTerm());
								}
								// <field column="intermediate_mp_term"
								// xpath="/response/result/doc/arr[@name='intermediate_mp_term']/str"
								// />
								if (mp.getIntermediateMpTermSynonym() != null) {
									pipe.addIntermediateMpTermSynonym(mp
											.getIntermediateMpTermSynonym());
								}
								// <field column="intermediate_mp_term_synonym"
								// xpath="/response/result/doc/arr[@name='intermediate_mp_term_synonym']/str"
								// />

								// <field column="child_mp_id"
								// xpath="/response/result/doc/arr[@name='child_mp_id']/str"
								// />
								// <field column="child_mp_term"
								// xpath="/response/result/doc/arr[@name='child_mp_term']/str"
								// />
								if (mp.getChildMpId() != null) {
									pipe.addChildMpId(mp.getChildMpId());
									pipe.addChildMpTerm(mp.getChildMpTerm());
								}

								// <field column="child_mp_term_synonym"
								// xpath="/response/result/doc/arr[@name='child_mp_term_synonym']/str"
								// />
								if (mp.getChildMpTermSynonym() != null) {
									pipe.addChildMpTermSynonym(mp
											.getChildMpTermSynonym());
								}

								// <field column="hp_id"
								// xpath="/response/result/doc/arr[@name='hp_id']/str"
								// />
								if (mp.getHpId() != null) {
									pipe.addHpId(mp.getHpId());
								}

								// <field column="hp_term"
								// xpath="/response/result/doc/arr[@name='hp_term']/str"
								// />
								if (mp.getHpTerm() != null) {
									pipe.addHpTerm(mp.getHpTerm());
								}

								// <!-- MA: inferred from MP -->
								// <field column="inferred_ma_id"
								// xpath="/response/result/doc/arr[@name='inferred_ma_id']/str"
								// />
								// <field column="inferred_ma_term"
								// xpath="/response/result/doc/arr[@name='inferred_ma_term']/str"
								// />
								if (mp.getInferredMaId() != null) {
									pipe.addInferredMaId(mp.getInferredMaId());
									pipe.addInferredMaTerm(mp
											.getInferredMaTerm());
									// <field column="inferred_ma_term_synonym"
									// xpath="/response/result/doc/arr[@name='inferred_ma_term_synonym']/str"
									// />
									if (mp.getInferredMaTermSynonym() != null) {
										pipe.addInferredMaTermSynonym(mp
												.getInferredMaTermSynonym());
									}
								}
								if (mp.getInferredSelectedTopLevelMaId() != null) {
									pipe.addInferredSelectedTopLevelMaId(mp
											.getInferredSelectedTopLevelMaId());
									// <field
									// column="inferred_selected_top_level_ma_id"
									// xpath="/response/result/doc/arr[@name='inferred_selected_top_level_ma_id']/str"
									// />
									if (mp.getInferredSelectedTopLevelMaTerm() != null) {
										pipe.addInferredSelectedTopLevelMaTerm(mp
												.getInferredSelectedTopLevelMaTerm());
									}
									// <field
									// column="inferred_selected_top_level_ma_term"
									// xpath="/response/result/doc/arr[@name='inferred_selected_top_level_ma_term']/str"
									// />
									if (mp.getInferredSelectedTopLevelMaTermSynonym() != null) {
										pipe.addInferredSelectedToLevelMaTermSynonym(mp
												.getInferredSelectedTopLevelMaTermSynonym());
									}
									// <field
									// column="inferred_selected_top_level_ma_term_synonym"
									// xpath="/response/result/doc/arr[@name='inferred_selected_top_level_ma_term_synonym']/str"
									// />

								}
								if (mp.getInferredChildMaId() != null) {
									pipe.addInferredChildMaId(mp
											.getInferredChildMaId());
									// <field column="inferred_child_ma_id"
									// xpath="/response/result/doc/arr[@name='inferred_child_ma_id']/str"
									// />
									pipe.addInferredChildMaTerm(mp
											.getInferredChildMaTerm());
									// <field column="inferred_child_ma_term"
									// xpath="/response/result/doc/arr[@name='inferred_child_ma_term']/str"
									// />
									if (mp.getInferredChildMaTermSynonym() != null) {
										pipe.addInferredChildMaTermSynonyms(mp
												.getInferredChildMaTermSynonym());
									}
									// <field
									// column="inferred_child_ma_term_synonym"
									// xpath="/response/result/doc/arr[@name='inferred_child_ma_term_synonym']/str"
									// />
								}
							}

						}
					}
					documentCount++;
					System.out.println("documentCount="+documentCount);
					pipelineCore.addBean(pipe);
					if(documentCount % 10000==0){
						pipelineCore.commit();
					}
				}

			}

			// }
			// pipe.setPipelineName(pipelineName);
			// pipe.setPipelineStableId(pipelineStableId);
			// pipe.setPipelineId(pipelineId);

			logger.info("commiting to Pipeline core for last time!");
			logger.info("Pipeline commit started.");
			pipelineCore.commit();
			logger.info("Pipeline commit finished.");

		} catch (IOException | SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IndexerException(e);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		logger.info("time was " + (endTime - startTime) / 1000);

		logger.info("Pipeline Indexer complete!");
	}

	// PROTECTED METHODS
	@Override
	protected Logger getLogger() {

		return logger;
	}

	private Map<Integer, Map<String, String>> populateParamDbIdToParametersMap() {

		logger.info("populating PCS pipeline info");
		Map<Integer, Map<String, String>> localParamDbIdToParameter = new HashMap<>();
		String queryString = "select 'pipeline' as dataType, id, stable_id, name, stable_key from phenotype_parameter";

		try (PreparedStatement p = komp2DbConnection
				.prepareStatement(queryString)) {
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
				rowMap.put(ObservationDTO.PARAMETER_NAME,
						resultSet.getString("name"));
				rowMap.put(ObservationDTO.PARAMETER_STABLE_ID,
						resultSet.getString("stable_id"));
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
			System.out
					.println("phenotype parameter should have 5704+ entries and has "
							+ localParamDbIdToParameter.size() + " entries");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return localParamDbIdToParameter;

	}

	private Map<Integer, Set<Integer>> populateParamIdToProcedureIdListMap() {

		logger.info("populating param To ProcedureId info");
		Map<Integer, Set<Integer>> procIdToParams = new HashMap<>();
		String queryString = "select procedure_id, parameter_id from phenotype_procedure_parameter";

		try (PreparedStatement p = komp2DbConnection
				.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				Set<Integer> parameterIds = new HashSet<>();// store the row in
				// a map of column
				// names to values
				int paramId = resultSet.getInt("parameter_id");
				int procId = resultSet.getInt("procedure_id");
				if (procIdToParams.containsKey(procId)) {
					parameterIds = procIdToParams.get(procId);
				} else {
					parameterIds = new HashSet<>();// store the row in a map of
					// column names to values
				}
				parameterIds.add(paramId);
				procIdToParams.put(procId, parameterIds);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("should be 5704+ entries " + procIdToParams.size());
		return procIdToParams;
	}

	private Map<Integer, ProcedureBean> populateProcedureIdToProcedureMap() {

		logger.info("populating procedureId to Procedure Map info");
		Map<Integer, ProcedureBean> procedureIdToProcedureMap = new HashMap<>();
		String queryString = "select id as pproc_id, stable_id, name, stable_key, concat(name, '___', stable_id) as proc_name_id from phenotype_procedure";

		try (PreparedStatement p = komp2DbConnection
				.prepareStatement(queryString)) {
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
		System.out.println("187+ the procedureIdToProcedureMap size="
				+ procedureIdToProcedureMap.size());
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
	private List<PipelineBean> populateProcedureIdToPipelineMap() {

		logger.info("populating procedureId to  pipeline Map info");
		List<PipelineBean> procIdToPipelineMap = new ArrayList<>();
		String queryString = "select pproc.id as pproc_id, ppipe.name as pipe_name, ppipe.id as pipe_id, ppipe.stable_id as pipe_stable_id, ppipe.stable_key as pipe_stable_key, concat(ppipe.name, '___', pproc.name, '___', pproc.stable_id) as pipe_proc_sid from phenotype_procedure pproc inner join phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id inner join phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id where ppipe.db_id=6";

		try (PreparedStatement p = komp2DbConnection
				.prepareStatement(queryString)) {
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
				pipe.procedureId = procedureId;
				procIdToPipelineMap.add(pipe);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("547+ should be and is in procIdToPipelineMap "
				+ procIdToPipelineMap.size());
		return procIdToPipelineMap;
	}

	public class PipelineBean {

		public int procedureId;
		public String pipelineName;
		int pipelineId;
		String pipelineStableId;
		int pipelineStableKey;
		String pipeProcSid;
	}

	private Map<String, List<GfMpBean>> populateGfAccAndMp() {
		logger.info("populating GfAcc and Mp info - started");
		Map<String, List<GfMpBean>> gfMpBeansMap = new HashMap<>();
		String queryString = "select distinct concat(s.parameter_id,'_',s.procedure_id,'_',s.pipeline_id) as pppIds, s.gf_acc, s.mp_acc, s.parameter_id as pp_parameter_id, s.procedure_id as pproc_procedure_id, s.pipeline_id as ppipe_pipeline_id, s.allele_acc, s.strain_acc from phenotype_parameter pp INNER JOIN phenotype_procedure_parameter ppp on pp.id=ppp.parameter_id INNER JOIN phenotype_procedure pproc on ppp.procedure_id=pproc.id INNER JOIN phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id INNER JOIN phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id inner join phenotype_call_summary s on ppipe.id=s.pipeline_id and pproc.id=s.procedure_id and pp.id=s.parameter_id";

		try (PreparedStatement p = komp2DbConnection
				.prepareStatement(queryString)) {
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

		logger.info("populating GfAcc and Mp info - finished");

		return gfMpBeansMap;
	}

	class GfMpBean {

		String gfAcc;
		String mpAcc;

	}

	private Map<String, MpDTO> populateMpIdToMp() throws IndexerException {
		return SolrUtils.populateMpTermIdToMp(mpCore);
	}

	public static void main(String[] args) throws IndexerException {

		PipelineIndexer indexer = new PipelineIndexer();
		indexer.initialise(args);
		indexer.run();
		indexer.validateBuild();

		logger.info("Process finished.  Exiting.");
	}
}
