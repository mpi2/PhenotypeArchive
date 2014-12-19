package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.phenotype.service.dto.StatisticalResultDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.ImpressBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * Load documents into the statistical-results SOLR core
 */
public class StatisticalResultIndexer extends AbstractIndexer {

	private static final Logger logger = LoggerFactory.getLogger(StatisticalResultIndexer.class);
	private static Connection connection;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Autowired
	@Qualifier("ontodbDataSource")
	DataSource ontodbDataSource;

	@Autowired
	@Qualifier("genotypePhenotypeIndexing")
	SolrServer gpSolrServer;

	Map<Integer, ImpressBean> pipelineMap = new HashMap<>();
	Map<Integer, ImpressBean> procedureMap = new HashMap<>();
	Map<Integer, ImpressBean> parameterMap = new HashMap<>();
	Map<String, List<OntologyTermBean>> mpTopTerms = new HashMap<>();
	Map<String, List<OntologyTermBean>> mpIntTerms = new HashMap<>();

	public StatisticalResultIndexer() {
	}

	public void initialise(String[] args) throws IndexerException {

		super.initialise(args);

		try {

			connection = komp2DataSource.getConnection();

			mpTopTerms = IndexerMap.getMpTopLevelTerms(ontodbDataSource.getConnection());
			mpIntTerms = IndexerMap.getMpIntermediateLevelTerms(ontodbDataSource.getConnection());

			logger.info("Populating impress maps");
			pipelineMap = IndexerMap.getImpressPipelines(connection);
			procedureMap = IndexerMap.getImpressProcedures(connection);
			parameterMap = IndexerMap.getImpressParameters(connection);

		} catch (SQLException e) {
			throw new IndexerException(e);
		}

		printConfiguration();

	}


	public static void main(String[] args) throws IndexerException {
		StatisticalResultIndexer main = new StatisticalResultIndexer();
		main.initialise(args);
		main.run();

		logger.info("Process finished.  Exiting.");
	}


	@Override
	protected Logger getLogger() {
		return logger;
	}


	public void run() throws IndexerException {

		Long start = System.currentTimeMillis();
		try {

			logger.info("Populating statistical-results solr core");
			populateStatisticalResultsSolrCore();

		} catch (SQLException | IOException | SolrServerException e) {
			throw new IndexerException(e);
		}

		logger.info("Populating statistical-results solr core - done [took: {}s]", (System.currentTimeMillis() - start) / 1000.0);
	}


	public void populateStatisticalResultsSolrCore() throws SQLException, IOException, SolrServerException {

		int count=0;

		gpSolrServer.deleteByQuery("*:*");

		String query = "(SELECT\n" +
			"                        CONCAT(dependent_variable, '_', id) as doc_id,\n" +
			"                        'unidimensional' AS data_type, id, control_id, \n" +
			"                        experimental_id, NULL AS sex, experimental_zygosity,\n" +
			"                        external_db_id, project_id, organisation_id,\n" +
			"                        pipeline_id, parameter_id, colony_id,\n" +
			"                        dependent_variable, control_selection_strategy, male_controls,\n" +
			"                        male_mutants, female_controls, female_mutants,\n" +
			"                        metadata_group, statistical_method, status,\n" +
			"                        NULL AS category_a, NULL AS category_b, NULL AS categorical_p_value,\n" +
			"                        NULL AS categorical_effect_size, 'Suppressed' AS raw_output, batch_significance,\n" +
			"                        variance_significance, null_test_significance, genotype_parameter_estimate,\n" +
			"                        genotype_stderr_estimate, genotype_effect_pvalue, gender_parameter_estimate,\n" +
			"                        gender_stderr_estimate, gender_effect_pvalue, weight_parameter_estimate,\n" +
			"                        weight_stderr_estimate, weight_effect_pvalue, gp1_genotype,\n" +
			"                        gp1_residuals_normality_test, gp2_genotype, gp2_residuals_normality_test,\n" +
			"                        blups_test, rotated_residuals_normality_test, intercept_estimate,\n" +
			"                        intercept_stderr_estimate, interaction_significance, interaction_effect_pvalue,\n" +
			"                        gender_female_ko_estimate, gender_female_ko_stderr_estimate, gender_female_ko_pvalue,\n" +
			"                        gender_male_ko_estimate, gender_male_ko_stderr_estimate, gender_male_ko_pvalue,\n" +
			"                        classification_tag, additional_information\n" +
			"                    FROM stats_unidimensional_results WHERE dependent_variable NOT LIKE '%FER%' AND dependent_variable NOT LIKE '%VIA%')\n" +
			"                    UNION ALL\n" +
			"                        (SELECT\n" +
			"                        CONCAT(dependent_variable, '_', id) as doc_id,\n" +
			"                        'categorical' AS data_type, id, control_id,\n" +
			"                        experimental_id, experimental_sex as sex, experimental_zygosity,\n" +
			"                        external_db_id, project_id, organisation_id,\n" +
			"                        pipeline_id, parameter_id, colony_id,\n" +
			"                        dependent_variable, control_selection_strategy, male_controls,\n" +
			"                        male_mutants, female_controls, female_mutants,\n" +
			"                        metadata_group, statistical_method, status,\n" +
			"                        category_a, category_b, p_value as categorical_p_value,\n" +
			"                        effect_size AS categorical_effect_size, 'Suppressed' AS raw_output, NULL AS batch_significance,\n" +
			"                        NULL AS variance_significance, NULL AS null_test_significance, NULL AS genotype_parameter_estimate,\n" +
			"                        NULL AS genotype_stderr_estimate, NULL AS genotype_effect_pvalue, NULL AS gender_parameter_estimate,\n" +
			"                        NULL AS gender_stderr_estimate, NULL AS gender_effect_pvalue, NULL AS weight_parameter_estimate,\n" +
			"                        NULL AS weight_stderr_estimate, NULL AS weight_effect_pvalue, NULL AS gp1_genotype,\n" +
			"                        NULL AS gp1_residuals_normality_test, NULL AS gp2_genotype, NULL AS gp2_residuals_normality_test,\n" +
			"                        NULL AS blups_test, NULL AS rotated_residuals_normality_test, NULL AS intercept_estimate,\n" +
			"                        NULL AS intercept_stderr_estimate, NULL AS interaction_significance, NULL AS interaction_effect_pvalue,\n" +
			"                        NULL AS gender_female_ko_estimate, NULL AS gender_female_ko_stderr_estimate, NULL AS gender_female_ko_pvalue,\n" +
			"                        NULL AS gender_male_ko_estimate, NULL AS gender_male_ko_stderr_estimate, NULL AS gender_male_ko_pvalue,\n" +
			"                        NULL AS classification_tag, NULL AS additional_information\n" +
			"                    FROM stats_categorical_results WHERE dependent_variable NOT LIKE '%FER%' AND dependent_variable NOT LIKE '%VIA%')";

		try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {

				StatisticalResultDTO doc = new StatisticalResultDTO();

				doc.setDocId(r.getString("doc_id"));
				doc.setSex(r.getString("sex"));
				doc.setZygosity(r.getString("zygosity"));
				doc.setPhenotypingCenter(r.getString("phenotyping_center"));
				doc.setProjectName(r.getString("project_name"));
				doc.setMpTermId(r.getString("mp_term_id"));
				doc.setMpTermName(r.getString("mp_term_name"));
				doc.setpValue(r.getDouble("p_value"));
				doc.setEffectSize(r.getDouble("effect_size"));
				doc.setMarkerAccessionId(r.getString("marker_accession_id"));
				doc.setMarkerSymbol(r.getString("marker_symbol"));
				doc.setColonyId(r.getString("colony_id"));
				doc.setAlleleAccessionId(r.getString("allele_accession_id"));
				doc.setAlleleName(r.getString("allele_name"));
				doc.setAlleleSymbol(r.getString("allele_symbol"));
				doc.setStrainAccessionId(r.getString("strain_accession_id"));
				doc.setStrainName(r.getString("strain_name"));
				doc.setResourceFullname(r.getString("resource_fullname"));
				doc.setResourceName(r.getString("resource_name"));

				doc.setPipelineStableKey(pipelineMap.get(r.getInt("pipeline_id")).stableKey);
				doc.setPipelineName(pipelineMap.get(r.getInt("pipeline_id")).name);
				doc.setPipelineStableId(pipelineMap.get(r.getInt("pipeline_id")).stableId);

				doc.setProcedureStableKey(procedureMap.get(r.getInt("procedure_id")).stableKey);
				doc.setProcedureName(procedureMap.get(r.getInt("procedure_id")).name);
				doc.setProcedureStableId(procedureMap.get(r.getInt("procedure_id")).stableId);

				doc.setParameterStableKey(parameterMap.get(r.getInt("parameter_id")).stableKey);
				doc.setParameterName(parameterMap.get(r.getInt("parameter_id")).name);
				doc.setParameterStableId(parameterMap.get(r.getInt("parameter_id")).stableId);

				/*
				TODO: The sexes can have different MP terms!!!  Need to handle this case
				 */
				List<String> termIds = new ArrayList<>();
				List<String> termNames = new ArrayList<>();
				Set<String> termSynonyms = new HashSet<>();
				List<String> termDefinitions = new ArrayList<>();
				if(mpTopTerms.get(r.getString("mp_term_id"))!=null) {
					for (OntologyTermBean term : new HashSet<>(mpTopTerms.get(r.getString("mp_term_id")))) {
						termIds.add(term.getTermId());
						termNames.add(term.getName());
						termSynonyms.addAll(term.getSynonyms());
						termDefinitions.add(term.getDefinition());
					}
					doc.setTopLevelMpTermId(termIds);
					doc.setTopLevelMpTermName(termNames);
				}

				termIds = new ArrayList<>();
				termNames = new ArrayList<>();
				termSynonyms = new HashSet<>();
				termDefinitions = new ArrayList<>();
				if(mpIntTerms.get(r.getString("mp_term_id"))!=null) {
					for (OntologyTermBean term : new HashSet<>(mpIntTerms.get(r.getString("mp_term_id")))) {
						termIds.add(term.getTermId());
						termNames.add(term.getName());
						termSynonyms.addAll(term.getSynonyms());
						termDefinitions.add(term.getDefinition());
					}
					doc.setIntermediateMpTermId(termIds);
					doc.setIntermediateMpTermName(termNames);
				}

				gpSolrServer.addBean(doc, 30000);

				count++;

				if (count%1000 == 0) {
					logger.info(" added {} beans", count);
				}

			}

			// Final commit to save the rest of the docs
			logger.info(" added {} beans", count);
			gpSolrServer.commit();

		} catch (Exception e) {
			logger.error("Big error {}", e.getMessage(), e);
		}

	}


}
