package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.GenotypePhenotypeService.GenotypePhenotypeField;
import uk.ac.ebi.phenotype.service.ObservationService.ExperimentField;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;
import uk.ac.ebi.phenotype.web.pojo.HeatMapCell;

@Service
public class StatisticalResultService {

    private HttpSolrServer solr;

    private static final Logger LOG = LoggerFactory.getLogger(StatisticalResultService.class);

    // Definition of the solr fields
    public static final class StatisticalResultField {

        public final static String DOCUMENT_ID = "doc_id";
        public final static String DB_ID = "db_id";
        public final static String DATA_TYPE = "data_type";

        public final static String MP_TERM_ID = "mp_term_id"; 
        public final static String MP_TERM_NAME = "mp_term_name"; 
        public final static String TOP_LEVEL_MP_TERM_ID = "top_level_mp_term_id";
        public final static String TOP_LEVEL_MP_TERM_NAME = "top_level_mp_term_name";
        public final static String INTERMEDIATE_MP_TERM_ID = "intermediate_mp_term_id";
        public final static String INTERMEDIATE_MP_TERM_NAME = "intermediate_mp_term_name";

        public final static String RESOURCE_NAME = "resource_name"; 
        public final static String RESOURCE_FULLNAME = "resource_fullname";
        public final static String RESOURCE_ID = "resource_id"; 
        public final static String PROJECT_NAME = "project_name";
        public final static String PHENOTYPING_CENTER = "phenotyping_center"; 

        public final static String PIPELINE_STABLE_ID = "pipeline_stable_id"; 
        public final static String PIPELINE_STABLE_KEY = "pipeline_stable_key"; 
        public final static String PIPELINE_NAME = "pipeline_name"; 
        public final static String PIPELINE_ID = "pipeline_id"; 

        public final static String PROCEDURE_STABLE_ID = "procedure_stable_id";
        public final static String PROCEDURE_STABLE_KEY = "procedure_stable_key"; 
        public final static String PROCEDURE_NAME = "procedure_name"; 
        public final static String PROCEDURE_ID = "procedure_id"; 

        public final static String PARAMETER_STABLE_ID = "parameter_stable_id";
        public final static String PARAMETER_STABLE_KEY = "parameter_stable_key";
        public final static String PARAMETER_NAME = "parameter_name";
        public final static String PARAMETER_ID = "parameter_id";

        public final static String COLONY_ID = "colony_id"; 
        public final static String MARKER_SYMBOL = "marker_symbol";
        public final static String MARKER_ACCESSION_ID = "marker_accession_id";
        public final static String ALLELE_SYMBOL = "allele_symbol";
        public final static String ALLELE_NAME = "allele_name";
        public final static String ALLELE_ACCESSION_ID = "allele_accession_id";
        public final static String STRAIN_NAME = "strain_name"; 
        public final static String STRAIN_ACCESSION_ID = "strain_accession_id"; 
        public final static String SEX = "sex"; 
        public final static String ZYGOSITY = "zygosity"; 

        public final static String CONTROL_SELECTION_METHOD = "control_selection_method";
        public final static String DEPENDENT_VARIABLE = "dependent_variable";
        public final static String METADATA_GROUP = "metadata_group";

        public final static String CONTROL_BIOLOGICAL_MODEL_ID = "control_biological_model_id";
        public final static String MUTANT_BIOLOGICAL_MODEL_ID = "mutant_biological_model_id";
        public final static String MALE_CONTROL_COUNT = "male_control_count";
        public final static String MALE_MUTANT_COUNT = "male_mutant_count";
        public final static String FEMALE_CONTROL_COUNT = "female_control_count";
        public final static String FEMALE_MUTANT_COUNT = "female_mutant_count";

        public final static String STATISTICAL_METHOD = "statistical_method";
        public final static String STATUS = "status";
        public final static String ADDITIONAL_INFORMATION = "additional_information";
        public final static String RAW_OUTPUT = "raw_output";
        public final static String P_VALUE = "p_value";
        public final static String EFFECT_SIZE = "effect_size";

        public final static String CATEGORIES = "categories";
        public final static String CATEGORICAL_P_VALUE = "categorical_p_value";
        public final static String CATEGORICAL_EFFECT_SIZE = "categorical_effect_size";
        
        public final static String BATCH_SIGNIFICANT = "batch_significant";
        public final static String VARIANCE_SIGNIFICANT = "variance_significant";
        public final static String NULL_TEST_P_VALUE = "null_test_p_value";
        public final static String GENOTYPE_EFFECT_P_VALUE = "genotype_effect_p_value";
        public final static String GENOTYPE_EFFECT_STDERR_ESTIMATE = "genotype_effect_stderr_estimate";
        public final static String GENOTYPE_EFFECT_PARAMETER_ESTIMATE = "genotype_effect_parameter_estimate";

        public final static String SEX_EFFECT_P_VALUE = "sex_effect_p_value";
        public final static String SEX_EFFECT_STDERR_ESTIMATE = "sex_effect_stderr_estimate";
        public final static String SEX_EFFECT_PARAMETER_ESTIMATE = "sex_effect_parameter_estimate";
        public final static String WEIGHT_EFFECT_P_VALUE = "weight_effect_p_value";
        public final static String WEIGHT_EFFECT_STDERR_ESTIMATE = "weight_effect_stderr_estimate";
        public final static String WEIGHT_EFFECT_PARAMETER_ESTIMATE = "weight_effect_parameter_estimate";

        public final static String group_1_genotype = "group_1_genotype";
        public final static String group_1_residuals_normality_test = "group_1_residuals_normality_test";
        public final static String group_2_genotype = "group_2_genotype";
        public final static String group_2_residuals_normality_test = "group_2_residuals_normality_test";
        public final static String blups_test = "blups_test";
        public final static String rotated_residuals_test = "rotated_residuals_test";

        public final static String intercept_estimate = "intercept_estimate";
        public final static String intercept_estimate_stderr_estimate = "intercept_estimate_stderr_estimate";
        public final static String interaction_significant = "interaction_significant";
        public final static String interaction_effect_p_value = "interaction_effect_p_value";
        public final static String female_ko_effect_p_value = "female_ko_effect_p_value";
        public final static String female_ko_effect_stderr_estimate = "female_ko_effect_stderr_estimate";
        public final static String female_ko_parameter_estimate = "female_ko_parameter_estimate";
        public final static String male_ko_effect_p_value = "male_ko_effect_p_value";
        public final static String male_ko_effect_stderr_estimate = "male_ko_effect_stderr_estimate";
        public final static String male_ko_parameter_estimate = "male_ko_parameter_estimate";
        public final static String classification_tag = "classification_tag";

    }

    public StatisticalResultService(String solrUrl) {
        solr = new HttpSolrServer(solrUrl);
    }

    /**
     * Get the result for a set of 
     *  allele strain phenotypeCenter, pipeline, parameter, metadata, zygosity, sex
     * @param statisticalType 
     * @throws SolrServerException 
     */
    public List<? extends StatisticalResult> getStatisticalResult(
            String alleleAccession, 
            String strain, 
            String phenotypingCenter, 
            String pipelineStableId, 
            String parameterStableId, 
            String metadataGroup, 
            ZygosityType zygosity, 
            SexType sex, 
            ObservationType statisticalType) throws SolrServerException {

        List<? extends StatisticalResult> results = new ArrayList<>();

        QueryResponse response = new QueryResponse();
    
        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .addFilterQuery(StatisticalResultField.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"")
            .addFilterQuery(StatisticalResultField.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"")
            .addFilterQuery(StatisticalResultField.PIPELINE_STABLE_ID + ":" + pipelineStableId)
            .addFilterQuery(StatisticalResultField.PARAMETER_STABLE_ID + ":" + parameterStableId)
            .addFilterQuery(StatisticalResultField.ZYGOSITY + ":" + zygosity.name())
            .setStart(0)
            .setRows(10)
        ;

        if(strain != null) {
            query.addFilterQuery(StatisticalResultField.STRAIN_ACCESSION_ID + ":\"" + strain + "\"");
        }

        if(sex != null) {
            query.addFilterQuery(ExperimentField.SEX + ":" + sex);
        }

        if(metadataGroup==null) {
            // don't add a metadata group filter
        } else if (metadataGroup.isEmpty()) {
            query.addFilterQuery(ExperimentField.METADATA_GROUP + ":\"\"");
        } else {
            query.addFilterQuery(ExperimentField.METADATA_GROUP + ":" + metadataGroup);
        }

        response = solr.query(query);
        if (statisticalType == ObservationType.unidimensional) {
            results = (List<? extends StatisticalResult>) response.getBeans(UnidimensionalResult.class);
        } else if (statisticalType == ObservationType.categorical) {
            results = (List<? extends StatisticalResult>) response.getBeans(CategoricalResult.class);
        }
        
        return results;
    }


    
    
    // ***************************************************************//
    // DO WE NEED THIS ANYMORE?
    // ***************************************************************//
    public GeneRowForHeatMap getResultsForGeneHeatMap(String accession, GenomicFeature gene, List<Parameter> parameters) {
        GeneRowForHeatMap row = new GeneRowForHeatMap(accession);
        if (gene != null) {
            row.setSymbol(gene.getSymbol());
        } else {
            System.err.println("error no symbol for gene " + accession);
        }
        List<HeatMapCell> results = new ArrayList<HeatMapCell>();
        // search by gene and a list of params
        // or search on gene and then loop through params to add the results if
        // available order by ascending p value means we can just pick off the
        // first entry for that param
        // http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype/select/?q=marker_accession_id:%22MGI:104874%22&rows=10000000&version=2.2&start=0&indent=on&wt=json

        Map<String, HeatMapCell> paramMap = new HashMap<>();// map to contain
                                                            // parameters with
                                                            // their associated
                                                            // status or pvalue
                                                            // as a string
        for (Parameter param : parameters) {
            // System.out.println("adding param to paramMap="+paramId);
            paramMap.put(param.getStableId(), null);
        }

        SolrQuery q = new SolrQuery()
                .setQuery(GenotypePhenotypeField.MARKER_ACCESSION_ID + ":\"" + accession + "\"").setSort(GenotypePhenotypeField.P_VALUE, SolrQuery.ORDER.asc)
                .setRows(10000);
        QueryResponse response = null;

        try {
            response = solr.query(q);
            results = response.getBeans(HeatMapCell.class);
            for (HeatMapCell cell : results) {
                // System.out.println(doc.getFieldValues("p_value"));

                String paramStableId = cell.getParameterStableId();
                // System.out.println("comparing"+cell.getParameterStableId()+"|");
                if (paramMap.containsKey(cell.getParameterStableId())) {
                    System.out.println("cell mp Term name=" + cell.getMpTermName());
                    System.out.println("cell p value=" + cell.getpValue());
                    System.out.println(cell.getpValue() + "found");
                    paramMap.put(paramStableId, cell);
                    if (row.getLowestPValue() > cell.getpValue()) {
                        row.setLowestPValue(cell.getpValue());
                    }
                }
            }

            row.setParamToCellMap(paramMap);
        } catch (SolrServerException ex) {
            LOG.error(ex.getMessage());
        }

        return row;
    }
    // ***************************************************************//
    // DO WE NEED THIS ANYMORE?
    // ***************************************************************//

}
