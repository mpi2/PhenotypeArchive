package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
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
import org.apache.solr.client.solrj.SolrQuery;

/**
 * Populate the Genotype-Phenotype core
 */
public class GenotypePhenotypeIndexer extends AbstractIndexer {

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

    Map<Integer, ImpressBean> pipelineMap = new HashMap<>();
    Map<Integer, ImpressBean> procedureMap = new HashMap<>();
    Map<Integer, ImpressBean> parameterMap = new HashMap<>();
    Map<String, List<OntologyTermBean>> mpTopTerms = new HashMap<>();
    Map<String, List<OntologyTermBean>> mpIntTerms = new HashMap<>();

    public GenotypePhenotypeIndexer() {
    }

    public static final long MIN_EXPECTED_ROWS = 7600;

    @Override
    public void validateBuild() throws IndexerException {
        SolrQuery query = new SolrQuery().setQuery("*:*").setRows(0);
        try {
            Long numFound = gpSolrServer.query(query).getResults().getNumFound();
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

            mpTopTerms = IndexerMap.getMpTopLevelTerms(ontodbDataSource.getConnection());
            mpIntTerms = IndexerMap.getMpIntermediateLevelTerms(ontodbDataSource.getConnection());

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

        String query = "SELECT s.id as id, o.name as phenotyping_center, s.external_id, s.parameter_id as parameter_id, "
                + "s.procedure_id as procedure_id, s.pipeline_id as pipeline_id, s.gf_acc as marker_accession_id, gf.symbol as marker_symbol, "
                + "s.allele_acc as allele_accession_id, al.name as allele_name, al.symbol as allele_symbol, s.strain_acc as strain_accession_id, "
                + "st.name as strain_name, s.sex as sex, s.zygosity as zygosity, p.name as project_name, p.fullname as project_fullname, "
                + "s.mp_acc as mp_term_id, ot.name as mp_term_name, s.p_value as p_value, s.effect_size as effect_size, s.colony_id, "
                + "db.name as resource_fullname, db.short_name as resource_name "
                + "FROM phenotype_call_summary s "
                + "INNER JOIN organisation o ON s.organisation_id = o.id "
                + "INNER JOIN project p ON s.project_id = p.id "
                + "INNER JOIN ontology_term ot ON ot.acc = s.mp_acc "
                + "INNER JOIN genomic_feature gf ON s.gf_acc = gf.acc "
                + "LEFT OUTER JOIN strain st ON s.strain_acc = st.acc "
                + "LEFT OUTER JOIN allele al ON s.allele_acc = al.acc "
                + "INNER JOIN external_db db ON s.external_db_id = db.id "
                + "WHERE 0.0001 >= s.p_value";

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
                doc.setResourceFullname(r.getString("resource_fullname"));
                doc.setResourceName(r.getString("resource_name"));
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

                List<String> termIds = new ArrayList<>();
                List<String> termNames = new ArrayList<>();
                Set<String> termSynonyms = new HashSet<>();
                List<String> termDefinitions = new ArrayList<>();
                if (mpTopTerms.get(r.getString("mp_term_id")) != null) {
                    for (OntologyTermBean term : new HashSet<>(mpTopTerms.get(r.getString("mp_term_id")))) {
                        termIds.add(term.getTermId());
                        termNames.add(term.getName());
                        termSynonyms.addAll(term.getSynonyms());
                        termDefinitions.add(term.getDefinition());
                    }
                    doc.setTopLevelMpTermId(termIds);
                    doc.setTopLevelMpTermName(termNames);
                    doc.setTopLevelMpTermSynonym(new ArrayList(termSynonyms));
                    doc.setTopLevelMpTermDefinition(termDefinitions);
                }

                termIds = new ArrayList<>();
                termNames = new ArrayList<>();
                termSynonyms = new HashSet<>();
                termDefinitions = new ArrayList<>();
                if (mpIntTerms.get(r.getString("mp_term_id")) != null) {
                    for (OntologyTermBean term : new HashSet<>(mpIntTerms.get(r.getString("mp_term_id")))) {
                        termIds.add(term.getTermId());
                        termNames.add(term.getName());
                        termSynonyms.addAll(term.getSynonyms());
                        termDefinitions.add(term.getDefinition());
                    }
                    doc.setIntermediateMpTermId(termIds);
                    doc.setIntermediateMpTermName(termNames);
                    doc.setIntermediateMpTermSynonym(new ArrayList(termSynonyms));
                    doc.setIntermediateMpTermDefinition(termDefinitions);
                }

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
