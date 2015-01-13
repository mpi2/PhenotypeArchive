package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.phenotype.service.dto.StatisticalResultDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.ImpressBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.OrganisationBean;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import org.apache.solr.client.solrj.SolrQuery;

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
    @Qualifier("statisticalResultsIndexing")
    SolrServer statResultCore;

    Map<String, List<OntologyTermBean>> mpTopTerms = new HashMap<>();
    Map<String, List<OntologyTermBean>> mpIntTerms = new HashMap<>();
    Map<String, OntologyTermBean> mpTerms = new HashMap<>();

    Map<Integer, ImpressBean> pipelineMap = new HashMap<>();
    Map<Integer, ImpressBean> procedureMap = new HashMap<>();
    Map<Integer, ImpressBean> parameterMap = new HashMap<>();
    Map<Integer, OrganisationBean> organisationMap = new HashMap<>();

    Map<Integer, BiologicalDataBean> biologicalDataMap = new HashMap<>();

    public StatisticalResultIndexer() {
        
    }
    
    public static final long MIN_EXPECTED_ROWS = 528000;
    
    @Override
    public void validateBuild() throws IndexerException {
        SolrQuery query = new SolrQuery().setQuery("*:*").setRows(0);
        try {
            Long numFound = statResultCore.query(query).getResults().getNumFound();
            if (numFound < MIN_EXPECTED_ROWS) {
                throw new IndexerException("validateBuild(): Expected " + MIN_EXPECTED_ROWS + " rows but found " + numFound + " rows.");
            }
            logger.info("MIN_EXPECTED_ROWS: " + MIN_EXPECTED_ROWS + ". Actual rows: " + numFound);
        } catch (SolrServerException sse) {
            throw new IndexerException(sse);
        }
    }

    @Override
    public void initialise(String[] args) throws IndexerException {

        super.initialise(args);

        try {

            connection = komp2DataSource.getConnection();

            logger.info("Populating mp maps");
            mpTopTerms = IndexerMap.getMpTopLevelTerms(ontodbDataSource.getConnection());
            mpIntTerms = IndexerMap.getMpIntermediateLevelTerms(ontodbDataSource.getConnection());
            mpTerms = IndexerMap.getMpTerms(ontodbDataSource.getConnection());
            logger.info("MP terms has {} entries", mpTerms.size());

            logger.info("Populating impress maps");
            pipelineMap = IndexerMap.getImpressPipelines(connection);
            procedureMap = IndexerMap.getImpressProcedures(connection);
            parameterMap = IndexerMap.getImpressParameters(connection);
            organisationMap = IndexerMap.getOrganisationMap(connection);

            logger.info("Populating biological data map");
            populateBiologicalDataMap();

        } catch (SQLException e) {
            throw new IndexerException(e);
        }

        printConfiguration();
    }

    public static void main(String[] args) throws IndexerException {
        StatisticalResultIndexer main = new StatisticalResultIndexer();
        main.initialise(args);
        main.run();
        main.validateBuild();

        logger.info("Process finished.  Exiting.");
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public void run() throws IndexerException {

        Long start = System.currentTimeMillis();

        logger.info("Populating statistical-results solr core");
        populateStatisticalResultsSolrCore();

        logger.info("Populating statistical-results solr core - done [took: {}s]", (System.currentTimeMillis() - start) / 1000.0);
    }

    private void populateStatisticalResultsSolrCore() throws IndexerException {

        try {
            int count = 0;

            statResultCore.deleteByQuery("*:*");

            // Populate unidimensional statistic results
            String query = "SELECT CONCAT(dependent_variable, '_', sr.id) as doc_id, "
                    + "  'unidimensional' AS data_type, "
                    + "  sr.id AS db_id, control_id, experimental_id, experimental_zygosity, "
                    + "  external_db_id, organisation_id, "
                    + "  pipeline_id, procedure_id, parameter_id, colony_id, "
                    + "  dependent_variable, control_selection_strategy, male_controls, "
                    + "  male_mutants, female_controls, female_mutants, "
                    + "  metadata_group, statistical_method, status, "
                    + "  batch_significance, "
                    + "  variance_significance, null_test_significance, genotype_parameter_estimate, "
                    + "  genotype_percentage_change, "
                    + "  genotype_stderr_estimate, genotype_effect_pvalue, gender_parameter_estimate, "
                    + "  gender_stderr_estimate, gender_effect_pvalue, weight_parameter_estimate, "
                    + "  weight_stderr_estimate, weight_effect_pvalue, gp1_genotype, "
                    + "  gp1_residuals_normality_test, gp2_genotype, gp2_residuals_normality_test, "
                    + "  blups_test, rotated_residuals_normality_test, intercept_estimate, "
                    + "  intercept_stderr_estimate, interaction_significance, interaction_effect_pvalue, "
                    + "  gender_female_ko_estimate, gender_female_ko_stderr_estimate, gender_female_ko_pvalue, "
                    + "  gender_male_ko_estimate, gender_male_ko_stderr_estimate, gender_male_ko_pvalue, "
                    + "  classification_tag, additional_information, "
                    + "  mp_acc, "
                    + "  db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, "
                    + "  proj.name as project_name, proj.id as project_id, "
                    + "  org.name as phenotyping_center, org.id as phenotyping_center_id "
                    + "FROM stats_unidimensional_results sr "
                    + "INNER JOIN external_db db on db.id=sr.external_db_id "
                    + "INNER JOIN project proj on proj.id=sr.project_id "
                    + "INNER JOIN organisation org on org.id=sr.organisation_id "
                    + "WHERE dependent_variable NOT LIKE '%FER%' AND dependent_variable NOT LIKE '%VIA%'";

            try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                p.setFetchSize(Integer.MIN_VALUE);
                ResultSet r = p.executeQuery();
                while (r.next()) {

                    StatisticalResultDTO doc = parseUnidimensionalResult(r);
                    statResultCore.addBean(doc, 30000);
                    count ++;

                    if (count % 10000 == 0) {
                        logger.info(" added {} unidimensional beans", count);
                    }
                }
            }

            // Populate categorical statistic results
            query = "SELECT CONCAT(dependent_variable, '_', sr.id) as doc_id, "
                    + "  'categorical' AS data_type, sr.id AS db_id, control_id, "
                    + "  experimental_id, experimental_sex as sex, experimental_zygosity, "
                    + "  external_db_id, organisation_id, "
                    + "  pipeline_id, procedure_id, parameter_id, colony_id, "
                    + "  dependent_variable, control_selection_strategy, male_controls, "
                    + "  male_mutants, female_controls, female_mutants, "
                    + "  metadata_group, statistical_method, status, "
                    + "  category_a, category_b, "
                    + "  p_value as categorical_p_value, effect_size AS categorical_effect_size, "
                    + "  mp_acc, "
                    + "  db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, "
                    + "  proj.name as project_name, proj.id as project_id, "
                    + "  org.name as phenotyping_center, org.id as phenotyping_center_id "
                    + "FROM stats_categorical_results sr "
                    + "INNER JOIN external_db db on db.id=sr.external_db_id "
                    + "INNER JOIN project proj on proj.id=sr.project_id "
                    + "INNER JOIN organisation org on org.id=sr.organisation_id "
                    + "WHERE dependent_variable NOT LIKE '%FER%' AND dependent_variable NOT LIKE '%VIA%'";

            try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                p.setFetchSize(Integer.MIN_VALUE);
                ResultSet r = p.executeQuery();
                while (r.next()) {

                    StatisticalResultDTO doc = parseCategoricalResult(r);
                    statResultCore.addBean(doc, 30000);
                    count ++;

                    if (count % 10000 == 0) {
                        logger.info(" added {} categorical beans", count);
                    }
                }
            }

            // Final commit to save the rest of the docs
            logger.info(" added {} totalbeans", count);
            statResultCore.commit(true, true);              // waitflush & waitserver.

        } catch (SQLException | IOException | SolrServerException e) {
            logger.error("Big error {}", e.getMessage(), e);
        }

    }

    private StatisticalResultDTO parseUnidimensionalResult(ResultSet r) throws SQLException {

        StatisticalResultDTO doc = parseResultCommonFields(r);
        doc.setNullTestPValue(r.getDouble("null_test_significance"));
        doc.setpValue(r.getDouble("null_test_significance"));

        doc.setGroup1Genotype(r.getString("gp1_genotype"));
        doc.setGroup1ResidualsNormalityTest(r.getDouble("gp1_residuals_normality_test"));
        doc.setGroup2Genotype(r.getString("gp2_genotype"));
        doc.setGroup2ResidualsNormalityTest(r.getDouble("gp2_residuals_normality_test"));

        doc.setBatchSignificant(r.getBoolean("batch_significance"));
        doc.setVarianceSignificant(r.getBoolean("variance_significance"));
        doc.setInteractionSignificant(r.getBoolean("interaction_significance"));

        doc.setGenotypeEffectParameterEstimate(r.getDouble("genotype_parameter_estimate"));
        
        String percentageChange = r.getString("genotype_percentage_change");
        if ( ! r.wasNull()) {
            Double femalePercentageChange = getFemalePercentageChange(percentageChange);
            if (femalePercentageChange != null) {
                doc.setFemalePercentageChange(femalePercentageChange.toString() + "%");
            }
            
            Double malePercentageChange = getMalePercentageChange(percentageChange);
            if (malePercentageChange != null) {
                doc.setMalePercentageChange(malePercentageChange.toString() + "%");
            }
        }
        
        doc.setGenotypeEffectStderrEstimate(r.getDouble("genotype_stderr_estimate"));
        doc.setGenotypeEffectPValue(r.getDouble("genotype_effect_pvalue"));

        doc.setSexEffectParameterEstimate(r.getDouble("gender_parameter_estimate"));
        doc.setSexEffectStderrEstimate(r.getDouble("gender_stderr_estimate"));
        doc.setSexEffectPValue(r.getDouble("gender_effect_pvalue"));

        doc.setWeightEffectParameterEstimate(r.getDouble("weight_parameter_estimate"));
        doc.setWeightEffectStderrEstimate(r.getDouble("weight_stderr_estimate"));
        doc.setWeightEffectPValue(r.getDouble("weight_effect_pvalue"));

        doc.setInterceptEstimate(r.getDouble("intercept_estimate"));
        doc.setInterceptEstimateStderrEstimate(r.getDouble("intercept_stderr_estimate"));
        doc.setInteractionEffectPValue(r.getDouble("interaction_effect_pvalue"));

        doc.setFemaleKoParameterEstimate(r.getDouble("gender_female_ko_estimate"));
        doc.setFemaleKoEffectStderrEstimate(r.getDouble("gender_female_ko_stderr_estimate"));
        doc.setFemaleKoEffectPValue(r.getDouble("gender_female_ko_pvalue"));

        doc.setMaleKoParameterEstimate(r.getDouble("gender_male_ko_estimate"));
        doc.setMaleKoEffectStderrEstimate(r.getDouble("gender_male_ko_stderr_estimate"));
        doc.setMaleKoEffectPValue(r.getDouble("gender_male_ko_pvalue"));

        doc.setBlupsTest(r.getDouble("blups_test"));
        doc.setRotatedResidualsTest(r.getDouble("rotated_residuals_normality_test"));
        doc.setClassificationTag(r.getString("classification_tag"));
        doc.setAdditionalInformation(r.getString("additional_information"));
        return doc;

    }
        
    private Double getFemalePercentageChange(String token) {
        Double retVal = null;
        
        List<String> sexes = Arrays.asList(token.split(","));
        for (String sex : sexes) {
            if (sex.contains("Female")) {
                try {
                    String[] pieces = sex.split(":");
                    retVal = Double.parseDouble(pieces[1].replaceAll("%", ""));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    // null statement;
                }
                break;
            }
        }
        
        return retVal;
    }
        
    private Double getMalePercentageChange(String token) {
        Double retVal = null;
        
        List<String> sexes = Arrays.asList(token.split(","));
        for (String sex : sexes) {
            if (sex.contains("Male")) {
                try {
                    String[] pieces = sex.split(":");
                    retVal = Double.parseDouble(pieces[1].replaceAll("%", ""));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    // null statement;
                }
                break;
            }
        }
        
        return retVal;
    }

    private StatisticalResultDTO parseCategoricalResult(ResultSet r) throws SQLException {

        StatisticalResultDTO doc = parseResultCommonFields(r);
        doc.setSex(r.getString("sex"));
        doc.setpValue(r.getDouble("categorical_p_value"));
        doc.setEffectSize(r.getDouble("categorical_effect_size"));

        Set<String> categories = new HashSet<>();
        if (StringUtils.isNotEmpty(r.getString("category_a"))) {
            for (String category : r.getString("category_a").split("|")) {
                categories.add(category);
            }
        }
        if (StringUtils.isNotEmpty(r.getString("category_b"))) {
            for (String category : r.getString("category_b").split("|")) {
                categories.add(category);
            }
        }

        doc.setCategories(new ArrayList<>(categories));

        return doc;

    }

    private StatisticalResultDTO parseResultCommonFields(ResultSet r) throws SQLException {

        StatisticalResultDTO doc = new StatisticalResultDTO();

        doc.setDocId(r.getString("doc_id"));
        doc.setDataType(r.getString("data_type"));

        // Experiment details
        doc.setResourceId(r.getInt("resource_id"));
        doc.setResourceName(r.getString("resource_name"));
        doc.setResourceFullname(r.getString("resource_fullname"));
        doc.setProjectName(r.getString("project_name"));
        doc.setPhenotypingCenter(r.getString("phenotyping_center"));
        doc.setPipelineStableKey(pipelineMap.get(r.getInt("pipeline_id")).stableKey);
        doc.setPipelineName(pipelineMap.get(r.getInt("pipeline_id")).name);
        doc.setPipelineStableId(pipelineMap.get(r.getInt("pipeline_id")).stableId);
        doc.setProcedureStableKey(procedureMap.get(r.getInt("procedure_id")).stableKey);
        doc.setProcedureName(procedureMap.get(r.getInt("procedure_id")).name);
        doc.setProcedureStableId(procedureMap.get(r.getInt("procedure_id")).stableId);
        doc.setParameterStableKey(parameterMap.get(r.getInt("parameter_id")).stableKey);
        doc.setParameterName(parameterMap.get(r.getInt("parameter_id")).name);
        doc.setParameterStableId(parameterMap.get(r.getInt("parameter_id")).stableId);
        doc.setControlBiologicalModelId(r.getInt("control_id"));
        doc.setMutantBiologicalModelId(r.getInt("experimental_id"));
        doc.setZygosity(r.getString("experimental_zygosity"));
        doc.setDependentVariable(r.getString("dependent_variable"));
        doc.setExternalDbId(r.getInt("external_db_id"));
        doc.setDbId(r.getInt("db_id"));
        doc.setOrganisationId(r.getInt("organisation_id"));
        doc.setPhenotypingCenterId(r.getInt("phenotyping_center_id"));
        doc.setProjectId(r.getInt("project_id"));

        // Biological details
        BiologicalDataBean b = biologicalDataMap.get(r.getInt("experimental_id"));

        doc.setMarkerAccessionId(b.geneAcc);
        doc.setMarkerSymbol(b.geneSymbol);
        doc.setAlleleAccessionId(b.alleleAccession);
        doc.setAlleleName(b.alleleName);
        doc.setAlleleSymbol(b.alleleSymbol);
        doc.setStrainAccessionId(b.strainAcc);
        doc.setStrainName(b.strainName);

        // Data details
        if (StringUtils.isNotEmpty(r.getString("metadata_group"))) {
            doc.setMetadataGroup(r.getString("metadata_group"));
        }
        doc.setControlSelectionMethod(r.getString("control_selection_strategy"));
        doc.setStatisticalMethod(r.getString("statistical_method"));
        doc.setMaleControlCount(r.getInt("male_controls"));
        doc.setFemaleControlCount(r.getInt("female_controls"));
        doc.setMaleMutantCount(r.getInt("male_mutants"));
        doc.setFemaleMutantCount(r.getInt("female_mutants"));
        doc.setColonyId(r.getString("colony_id"));
        doc.setStatus(r.getString("status"));

        // MP Terms
		/*
         TODO: The sexes can have different MP terms!!!  Need to handle this case
         */
        OntologyTermBean otBean = mpTerms.get(r.getString("mp_acc"));
        if (otBean != null) {
            doc.setMpTermId(otBean.getTermId());
            doc.setMpTermName(otBean.getName());
        }

        List<String> termIds = new ArrayList<>();
        List<String> termNames = new ArrayList<>();
        Set<String> termSynonyms = new HashSet<>();
        List<String> termDefinitions = new ArrayList<>();
        if (doc.getMpTermId() != null && mpTopTerms.containsKey(doc.getMpTermId())) {
            for (OntologyTermBean term : new HashSet<>(mpTopTerms.get(doc.getMpTermId()))) {
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
        if (doc.getMpTermId() != null && mpIntTerms.containsKey(doc.getMpTermId())) {
            for (OntologyTermBean term : new HashSet<>(mpIntTerms.get(doc.getMpTermId()))) {
                termIds.add(term.getTermId());
                termNames.add(term.getName());
                termSynonyms.addAll(term.getSynonyms());
                termDefinitions.add(term.getDefinition());
            }
            doc.setIntermediateMpTermId(termIds);
            doc.setIntermediateMpTermName(termNames);
        }

        return doc;
    }

    /**
     * Add all the relevant data required quickly looking up biological data
     * associated to a biological sample
     *
     * @throws SQLException when a database exception occurs
     */
    private void populateBiologicalDataMap() throws SQLException {

        String query = "SELECT bm.id, "
                + "strain.acc AS strain_acc, strain.name AS strain_name, "
                + "(SELECT DISTINCT allele_acc FROM biological_model_allele bma WHERE bma.biological_model_id=bm.id) AS allele_accession, "
                + "(SELECT DISTINCT a.symbol FROM biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bm.id) AS allele_symbol, "
                + "(SELECT DISTINCT a.name FROM biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bm.id) AS allele_name, "
                + "(SELECT DISTINCT gf_acc FROM biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=bm.id) AS acc, "
                + "(SELECT DISTINCT gf.symbol FROM biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf ON gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bm.id) AS symbol "
                + "FROM biological_model bm "
                + "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bm.id "
                + "INNER JOIN strain strain ON strain.acc=bmstrain.strain_acc "
                + "WHERE exists(SELECT DISTINCT gf.symbol FROM biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf ON gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bm.id)";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                BiologicalDataBean b = new BiologicalDataBean();

                b.alleleAccession = resultSet.getString("allele_accession");
                b.alleleSymbol = resultSet.getString("allele_symbol");
                b.alleleName = resultSet.getString("allele_name");
                b.geneAcc = resultSet.getString("acc");
                b.geneSymbol = resultSet.getString("symbol");
                b.strainAcc = resultSet.getString("strain_acc");
                b.strainName = resultSet.getString("strain_name");

                biologicalDataMap.put(resultSet.getInt("id"), b);
            }
        }
        logger.info("Populated biological data map with {} entries", biologicalDataMap.size());
    }

    /**
     * Internal class to act as Map value DTO for biological data
     */
    protected class BiologicalDataBean {
        public String alleleAccession;
        public String alleleSymbol;
        public String alleleName;
        public String colonyId;
        public String externalSampleId;
        public String geneAcc;
        public String geneSymbol;
        public String sex;
        public String strainAcc;
        public String strainName;
        public String zygosity;
    }

}
