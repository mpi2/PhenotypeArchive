/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.MpOntologyService;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.ImpressBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBeanList;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.ValidationException;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Populate the Genotype-Phenotype core
 */
public class GenotypePhenotypeIndexer extends AbstractIndexer {

    public final static Set<String> source3iProcedurePrefixes = new HashSet(Arrays.asList(
        "MGP_BCI", "MGP_PBI", "MGP_ANA", "MGP_CTL", "MGP_EEI", "MGP_BMI"
    ));

    private static final Logger logger = LoggerFactory.getLogger(GenotypePhenotypeIndexer.class);
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
    
    @Autowired
    MpOntologyService mpOntologyService;

    Map<Integer, ImpressBean> pipelineMap = new HashMap<>();
    Map<Integer, ImpressBean> procedureMap = new HashMap<>();
    Map<Integer, ImpressBean> parameterMap = new HashMap<>();

    public GenotypePhenotypeIndexer() {
    }

    @Override
    public void validateBuild() throws IndexerException {
        Long numFound = getDocumentCount(gpSolrServer);
        
        if (numFound <= MINIMUM_DOCUMENT_COUNT)
            throw new IndexerException(new ValidationException("Actual genotype-phenotype document count is " + numFound + "."));
        
        if (numFound != documentCount)
            logger.warn("WARNING: Added " + documentCount + " genotype-phenotype documents but SOLR reports " + numFound + " documents.");
        else
            logger.info("validateBuild(): Indexed " + documentCount + " genotype-phenotype documents.");
    }

    @Override
    public void initialise(String[] args) throws IndexerException {

        super.initialise(args);

        try {

            connection = komp2DataSource.getConnection();

            logger.info("Populating impress maps");
            pipelineMap = IndexerMap.getImpressPipelines(connection);
            procedureMap = IndexerMap.getImpressProcedures(connection);
            parameterMap = IndexerMap.getImpressParameters(connection);
            logger.info("Done Populating impress maps");

        } catch (SQLException e) {
            throw new IndexerException(e);
        }

        printConfiguration();
    }

    public static void main(String[] args) throws IndexerException {
        GenotypePhenotypeIndexer main = new GenotypePhenotypeIndexer();
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
        try {

            logger.info("Populating genotype-phenotype solr core");
            populateGenotypePhenotypeSolrCore();

        } catch (SQLException | IOException | SolrServerException e) {
            throw new IndexerException(e);
        }

        logger.info("Populating genotype-phenotype solr core - done [took: {}s]", (System.currentTimeMillis() - start) / 1000.0);
    }

    public void populateGenotypePhenotypeSolrCore() throws SQLException, IOException, SolrServerException {

        int count = 0;

        gpSolrServer.deleteByQuery("*:*");

        String query = "SELECT s.id AS id, CASE WHEN sur.statistical_method IS NOT NULL THEN sur.statistical_method WHEN scr.statistical_method IS NOT NULL THEN scr.statistical_method ELSE 'Unknown' END AS statistical_method, " +
            "  sur.genotype_percentage_change, o.name AS phenotyping_center, s.external_id, s.parameter_id AS parameter_id, s.procedure_id AS procedure_id, s.pipeline_id AS pipeline_id, s.gf_acc AS marker_accession_id, " +
            "  gf.symbol AS marker_symbol, s.allele_acc AS allele_accession_id, al.name AS allele_name, al.symbol AS allele_symbol, s.strain_acc AS strain_accession_id, st.name AS strain_name, " +
            "  s.sex AS sex, s.zygosity AS zygosity, p.name AS project_name, p.fullname AS project_fullname, s.mp_acc AS mp_term_id, ot.name AS mp_term_name, " +
            "  CASE WHEN s.p_value IS NOT NULL THEN s.p_value WHEN s.sex='female' THEN sur.gender_female_ko_pvalue WHEN s.sex='male' THEN sur.gender_male_ko_pvalue END AS p_value, " +
            "  s.effect_size AS effect_size, " +
            "  s.colony_id, db.name AS resource_fullname, db.short_name AS resource_name " +
            "FROM phenotype_call_summary s " +
            "  LEFT OUTER JOIN stat_result_phenotype_call_summary srpcs ON srpcs.phenotype_call_summary_id = s.id " +
            "  LEFT OUTER JOIN stats_unidimensional_results sur ON sur.id = srpcs.unidimensional_result_id " +
            "  LEFT OUTER JOIN stats_categorical_results scr ON scr.id = srpcs.categorical_result_id " +
            "  INNER JOIN organisation o ON s.organisation_id = o.id " +
            "  INNER JOIN project p ON s.project_id = p.id " +
            "  INNER JOIN ontology_term ot ON ot.acc = s.mp_acc " +
            "  INNER JOIN genomic_feature gf ON s.gf_acc = gf.acc " +
            "  LEFT OUTER JOIN strain st ON s.strain_acc = st.acc " +
            "  LEFT OUTER JOIN allele al ON s.allele_acc = al.acc " +
            "  INNER JOIN external_db db ON s.external_db_id = db.id " +
            "WHERE (0.0001 >= s.p_value " +
            "  OR (s.p_value IS NULL AND s.sex='male' AND sur.gender_male_ko_pvalue<0.0001) " +
            "  OR (s.p_value IS NULL AND s.sex='female' AND sur.gender_female_ko_pvalue<0.0001))" ;

        try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            p.setFetchSize(Integer.MIN_VALUE);

            ResultSet r = p.executeQuery();
            while (r.next()) {

                GenotypePhenotypeDTO doc = new GenotypePhenotypeDTO();

                doc.setId(r.getInt("id"));
                doc.setSex(r.getString("sex"));
                doc.setZygosity(r.getString("zygosity"));
                doc.setPhenotypingCenter(r.getString("phenotyping_center"));
                doc.setProjectName(r.getString("project_name"));
                doc.setProjectFullname(r.getString("project_fullname"));
                doc.setMpTermId(r.getString("mp_term_id"));
                doc.setMpTermName(r.getString("mp_term_name"));

                String percentageChangeDb = r.getString("genotype_percentage_change");
                if ( ! r.wasNull()) {

                    // Default female, override if male
                    Double percentageChange = StatisticalResultIndexer.getFemalePercentageChange(percentageChangeDb);

                    if (doc.getSex().equals(SexType.male.getName())) {
                        percentageChange = StatisticalResultIndexer.getMalePercentageChange(percentageChangeDb);
                    }

                    if (percentageChange != null) {
                        doc.setPercentageChange(percentageChange.toString() + "%");
                    }

                }

                doc.setStatisticalMethod(r.getString("statistical_method"));
                doc.setP_value(r.getDouble("p_value"));
                doc.setEffect_size(r.getDouble("effect_size"));
                doc.setMarkerAccessionId(r.getString("marker_accession_id"));
                doc.setMarkerSymbol(r.getString("marker_symbol"));
                doc.setColonyId(r.getString("colony_id"));
                doc.setAlleleAccessionId(r.getString("allele_accession_id"));
                doc.setAlleleName(r.getString("allele_name"));
                doc.setAlleleSymbol(r.getString("allele_symbol"));
                doc.setStrainAccessionId(r.getString("strain_accession_id"));
                doc.setStrainName(r.getString("strain_name"));

                // Procedure prefix is the first two strings of the parameter after splitting on underscore
                // i.e. IMPC_BWT_001_001 => IMPC_BWT
                String procedurePrefix = StringUtils.join(Arrays.asList(parameterMap.get(r.getInt("parameter_id")).stableId.split("_")).subList(0, 2), "_");
                if (source3iProcedurePrefixes.contains(procedurePrefix)) {
                    doc.setResourceName("3i");
                    doc.setResourceFullname("Infection, Immunity and Immunophenotyping consortium");
                } else {
                    doc.setResourceFullname(r.getString("resource_fullname"));
                    doc.setResourceName(r.getString("resource_name"));
                }

                doc.setExternalId(r.getString("external_id"));

                doc.setPipelineStableKey(pipelineMap.get(r.getInt("pipeline_id")).stableKey);
                doc.setPipelineName(pipelineMap.get(r.getInt("pipeline_id")).name);
                doc.setPipelineStableId(pipelineMap.get(r.getInt("pipeline_id")).stableId);

                doc.setProcedureStableKey(procedureMap.get(r.getInt("procedure_id")).stableKey);
                doc.setProcedureName(procedureMap.get(r.getInt("procedure_id")).name);
                doc.setProcedureStableId(procedureMap.get(r.getInt("procedure_id")).stableId);

                doc.setParameterStableKey(parameterMap.get(r.getInt("parameter_id")).stableKey);
                doc.setParameterName(parameterMap.get(r.getInt("parameter_id")).name);
                doc.setParameterStableId(parameterMap.get(r.getInt("parameter_id")).stableId);

                String mpId = r.getString("mp_term_id");
                OntologyTermBeanList beanlist = new OntologyTermBeanList(mpOntologyService, mpId);
                doc.setTopLevelMpTermId(beanlist.getTopLevels().getIds());
                doc.setTopLevelMpTermName(beanlist.getTopLevels().getNames());
                doc.setTopLevelMpTermSynonym(beanlist.getTopLevels().getSynonyms());
                doc.setTopLevelMpTermDefinition(beanlist.getTopLevels().getDefinitions());
                
                doc.setIntermediateMpTermId(beanlist.getIntermediates().getIds());
                doc.setIntermediateMpTermName(beanlist.getIntermediates().getNames());
                doc.setIntermediateMpTermSynonym(beanlist.getIntermediates().getSynonyms());
                doc.setIntermediateMpTermDefinition(beanlist.getIntermediates().getDefinitions());

                documentCount++;
                gpSolrServer.addBean(doc, 30000);

                count ++;

                if (count % 1000 == 0) {
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
