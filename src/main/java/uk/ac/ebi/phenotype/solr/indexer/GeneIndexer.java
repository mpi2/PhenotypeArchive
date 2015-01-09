package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import uk.ac.ebi.phenotype.service.dto.*;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;
import uk.ac.ebi.phenotype.solr.indexer.utils.SolrUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
	@Qualifier("sangerImagesIndexing")
	SolrServer imagesCore;

	private Map<String, List<Map<String, String>>> phenotypeSummaryGeneAccessionsToPipelineInfo = new HashMap<>();
	private Map<String, List<SangerImageDTO>> sangerImages = new HashMap<>();
	private Map<String, List<MpDTO>> mgiAccessionToMP = new HashMap<>();


	public GeneIndexer() {

	}


	@Override
	public void initialise(String[] args) throws IndexerException {

		super.initialise(args);
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		try {

			komp2DbConnection = komp2DataSource.getConnection();

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

			int count = 0;
			List<AlleleDTO> alleles = IndexerMap.getAlleles(alleleCore);
			System.out.println("alleles size=" + alleles.size());

			geneCore.deleteByQuery("*:*");

			for (AlleleDTO allele : alleles) {
				//System.out.println("allele="+allele.getMarkerSymbol());
				GeneDTO gene = new GeneDTO();
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
				gene.getGoTermIds().addAll(allele.getGoTermIds());
				gene.getGoTermNames().addAll(allele.getGoTermNames());
				gene.getGoTermDefs().addAll(allele.getGoTermDefs());
				gene.getGoTermEvids().addAll(allele.getGoTermEvids());
				gene.getGoTermDomains().addAll(allele.getGoTermDomains());

				//gene.setMpId(allele.getM)

				// Populate pipeline and procedure info if we have a phenotypeCallSummary entry for this allele/gene
				if (phenotypeSummaryGeneAccessionsToPipelineInfo.containsKey(allele.getMgiAccessionId())) {
					List<Map<String, String>> rows = phenotypeSummaryGeneAccessionsToPipelineInfo.get(allele.getMgiAccessionId());
					List<String> pipelineNames = new ArrayList<>();
					List<String> pipelineStableIds = new ArrayList<>();
					List<String> procedureNames = new ArrayList<>();
					List<String> procedureStableIds = new ArrayList<>();
					List<String> parameterNames = new ArrayList<>();
					List<String> parameterStableIds = new ArrayList<>();
					for (Map<String, String> row : rows) {
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
				}

				//do images core data

				// Initialize all the ontology term lists
				gene.setMpId(new ArrayList<String>());
				gene.setMpTerm(new ArrayList<String>());
				gene.setMpTermSynonym(new ArrayList<String>());
				gene.setMpTermDefinition(new ArrayList<String>());
				gene.setOntologySubset(new ArrayList<String>());

				gene.setMaId(new ArrayList<String>());
				gene.setMaTerm(new ArrayList<String>());
				gene.setMaTermSynonym(new ArrayList<String>());
				gene.setMaTermDefinition(new ArrayList<String>());

				gene.setHpId(new ArrayList<String>());
				gene.setHpTerm(new ArrayList<String>());

				gene.setTopLevelMpId(new ArrayList<String>());
				gene.setTopLevelMpTerm(new ArrayList<String>());
				gene.setTopLevelMpTermSynonym(new ArrayList<String>());

				gene.setIntermediateMpId(new ArrayList<String>());
				gene.setIntermediateMpTerm(new ArrayList<String>());
				gene.setIntermediateMpTermSynonym(new ArrayList<String>());

				gene.setChildMpId(new ArrayList<String>());
				gene.setChildMpTerm(new ArrayList<String>());
				gene.setChildMpTermSynonym(new ArrayList<String>());

				gene.setChildMpId(new ArrayList<String>());
				gene.setChildMpTerm(new ArrayList<String>());
				gene.setChildMpTermSynonym(new ArrayList<String>());


				gene.setInferredMaId(new ArrayList<String>());
				gene.setInferredMaTerm(new ArrayList<String>());
				gene.setInferredMaTermSynonym(new ArrayList<String>());

				gene.setSelectedTopLevelMaTermId(new ArrayList<String>());
				gene.setSelectedTopLevelMaTerm(new ArrayList<String>());
				gene.setSelectedTopLevelMaTermSynonym(new ArrayList<String>());

				gene.setInferredChildMaId(new ArrayList<String>());
				gene.setInferredChildMaTerm(new ArrayList<String>());
				gene.setInferredChildMaTermSynonym(new ArrayList<String>());


				// Add all ontology information from images associated to this gene
				if (sangerImages.containsKey(allele.getMgiAccessionId())) {

					List<SangerImageDTO> list = sangerImages.get(allele.getMgiAccessionId());
					for (SangerImageDTO image : list) {

						if (image.getMp_id() != null) {

							gene.getMpId().addAll(image.getMp_id());
							gene.getMpTerm().addAll(image.getMpTerm());
							if (image.getMpSyns() != null)  gene.getMpTermSynonym().addAll(image.getMpSyns());

							if (image.getAnnotatedHigherLevelMpTermId() != null)  gene.getTopLevelMpId().addAll(image.getAnnotatedHigherLevelMpTermId());
							if (image.getAnnotatedHigherLevelMpTermName() != null)  gene.getTopLevelMpTerm().addAll(image.getAnnotatedHigherLevelMpTermName());
							if (image.getTopLevelMpTermSynonym() != null)  gene.getTopLevelMpTermSynonym().addAll(image.getTopLevelMpTermSynonym());

							if (image.getIntermediateMpId() != null) gene.getIntermediateMpId().addAll(image.getIntermediateMpId());
							if (image.getIntermediateMpTerm() != null) gene.getIntermediateMpTerm().addAll(image.getIntermediateMpTerm());
							if (image.getIntermediateMpTermSyn() != null) gene.getIntermediateMpTermSynonym().addAll(image.getIntermediateMpTermSyn());

						}

						if (image.getMaTermId() != null) {

							gene.getMaId().addAll(image.getMaTermId());
							gene.getMaTerm().addAll(image.getMaTermName());
							if (image.getMaTermSynonym() != null) gene.getMaTermSynonym().addAll(image.getMaTermSynonym());

							if (image.getSelectedTopLevelMaTermId() != null) gene.setSelectedTopLevelMaTermId(image.getSelectedTopLevelMaTermId());
							if (image.getSelectedTopLevelMaTerm() != null) gene.setSelectedTopLevelMaTerm(image.getSelectedTopLevelMaTerm());
							if (image.getSelectedTopLevelMaTermSynonym() != null) gene.setSelectedTopLevelMaTermSynonym(image.getSelectedTopLevelMaTermSynonym());

						}
					}
				}


				// Add all ontology information directly associated from MP to this gene
				if (StringUtils.isNotEmpty(allele.getMgiAccessionId())) {

					if (mgiAccessionToMP.containsKey(allele.getMgiAccessionId())) {

						List<MpDTO> mps = mgiAccessionToMP.get(allele.getMgiAccessionId());
						for (MpDTO mp : mps) {

							gene.getMpId().add(mp.getMpId());
							gene.getMpTerm().add(mp.getMpTerm());
							if (mp.getMpTermSynonym() != null) gene.getMpTermSynonym().addAll(mp.getMpTermSynonym());

							if (mp.getOntologySubset() != null) gene.getOntologySubset().addAll(mp.getOntologySubset());

							if (mp.getHpId() != null) {
								gene.getHpId().addAll(mp.getHpId());
								gene.getHpTerm().addAll(mp.getHpTerm());
							}

							if (mp.getTopLevelMpId() != null) {
								gene.getTopLevelMpId().addAll(mp.getTopLevelMpId());
								gene.getTopLevelMpTerm().addAll(mp.getTopLevelMpTerm());
							}
							if (mp.getTopLevelMpTermSynonym() != null) gene.getTopLevelMpTermSynonym().addAll(mp.getTopLevelMpTermSynonym());

							if (mp.getIntermediateMpId() != null) {
								gene.getIntermediateMpId().addAll(mp.getIntermediateMpId());
								gene.getIntermediateMpTerm().addAll(mp.getIntermediateMpTerm());
							}
							if (mp.getIntermediateMpTermSynonym() != null) gene.getIntermediateMpTermSynonym().addAll(mp.getIntermediateMpTermSynonym());

							if (mp.getChildMpId() != null) {
								gene.getChildMpId().addAll(mp.getChildMpId());
								gene.getChildMpTerm().addAll(mp.getChildMpTerm());
							}
							if (mp.getChildMpTermSynonym() != null) gene.getChildMpTermSynonym().addAll(mp.getChildMpTermSynonym());


							if (mp.getInferredMaId() != null) {
								gene.getInferredMaId().addAll(mp.getInferredMaId());
								gene.getInferredMaTerm().addAll(mp.getInferredMaTerm());
							}
							if (mp.getInferredMaTermSynonym() != null) gene.getInferredMaTermSynonym().addAll(mp.getInferredMaTermSynonym());

							if (mp.getInferredSelectedTopLevelMaId() != null) {
								gene.getInferredSelectedTopLevelMaId().addAll(mp.getInferredSelectedTopLevelMaId());
								gene.getInferredSelectedTopLevelMaTerm().addAll(mp.getInferredSelectedTopLevelMaTerm());
							}
							if (mp.getInferredSelectedTopLevelMaTermSynonym() != null) gene.getInferredSelectedTopLevelMaTermSynonym().addAll(mp.getInferredSelectedTopLevelMaTermSynonym());

							if (mp.getInferredChildMaId() != null) {
								gene.getInferredChildMaId().addAll(mp.getInferredChildMaId());
								gene.getInferredChildMaTerm().addAll(mp.getInferredChildMaTerm());
							}
							if (mp.getInferredChildMaTermSynonym() != null) gene.getInferredChildMaTermSynonym().addAll(mp.getInferredChildMaTermSynonym());

						}
					}


				}

				geneCore.addBean(gene, 60000);
				count++;

				if (count % 10000 == 0) {
					System.out.println(" added " + count + " beans");
				}
			}

			logger.info("Committing to gene core for last time");
			geneCore.commit();

		} catch (IOException | SolrServerException e) {
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

	private void initialiseSupportingBeans() throws IndexerException {

		phenotypeSummaryGeneAccessionsToPipelineInfo = populatePhenotypeCallSummaryGeneAccessions();
		sangerImages = IndexerMap.getSangerImagesByMgiAccession(imagesCore);
		mgiAccessionToMP = populateMgiAccessionToMp();
		System.out.println("mgiAccessionToMP size=" + mgiAccessionToMP.size());
	}


	private Map<String, List<MpDTO>> populateMgiAccessionToMp() throws IndexerException {

		return SolrUtils.populateMgiAccessionToMp(mpCore);
	}


	private Map<String, List<Map<String, String>>> populatePhenotypeCallSummaryGeneAccessions() {

		System.out.println("populating PCS pipeline info");
		String queryString = "select pcs.*, param.name, param.stable_id, proc.stable_id, proc.name, pipe.stable_id, pipe.name" +
			" from phenotype_call_summary pcs" +
			" inner join ontology_term term on term.acc=mp_acc" +
			" inner join genomic_feature gf on gf.acc=pcs.gf_acc" +
			" inner join phenotype_parameter param on param.id=pcs.parameter_id" +
			" inner join phenotype_procedure proc on proc.id=pcs.procedure_id" +
			" inner join phenotype_pipeline pipe on pipe.id=pcs.pipeline_id";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				String gf_acc = resultSet.getString("gf_acc");

				Map<String, String> rowMap = new HashMap<>();
				rowMap.put(ObservationDTO.PARAMETER_NAME, resultSet.getString("param.name"));
				rowMap.put(ObservationDTO.PARAMETER_STABLE_ID, resultSet.getString("param.stable_id"));
				rowMap.put(ObservationDTO.PROCEDURE_STABLE_ID, resultSet.getString("proc.stable_id"));
				rowMap.put(ObservationDTO.PROCEDURE_NAME, resultSet.getString("proc.name"));
				rowMap.put(ObservationDTO.PIPELINE_STABLE_ID, resultSet.getString("pipe.stable_id"));
				rowMap.put(ObservationDTO.PIPELINE_NAME, resultSet.getString("pipe.name"));
				rowMap.put("proc_param_name", resultSet.getString("proc.name") + "___" + resultSet.getString("param.name"));
				rowMap.put("proc_param_stable_id", resultSet.getString("proc.stable_id") + "___" + resultSet.getString("param.stable_id"));
				List<Map<String, String>> rows = null;

				if (phenotypeSummaryGeneAccessionsToPipelineInfo.containsKey(gf_acc)) {
					rows = phenotypeSummaryGeneAccessionsToPipelineInfo.get(gf_acc);
				} else {
					rows = new ArrayList<>();
				}
				rows.add(rowMap);

				phenotypeSummaryGeneAccessionsToPipelineInfo.put(gf_acc, rows);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return phenotypeSummaryGeneAccessionsToPipelineInfo;

	}


	public static void main(String[] args) throws IndexerException {

		GeneIndexer indexer = new GeneIndexer();
		indexer.initialise(args);
		indexer.run();

		logger.info("Process finished.  Exiting.");
	}
}
