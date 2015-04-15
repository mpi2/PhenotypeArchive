package uk.ac.ebi.phenotype.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;


public class StatisticalResultDTO {



    public final static String DOCUMENT_ID = "doc_id";
    public final static String DB_ID = "db_id";
    public final static String DATA_TYPE = "data_type";

    public final static String MP_TERM_ID = "mp_term_id"; 
    public final static String MP_TERM_NAME = "mp_term_name"; 
    public final static String TOP_LEVEL_MP_TERM_ID = "top_level_mp_term_id";
    public final static String TOP_LEVEL_MP_TERM_NAME = "top_level_mp_term_name";
    public final static String INTERMEDIATE_MP_TERM_ID = "intermediate_mp_term_id";
    public final static String INTERMEDIATE_MP_TERM_NAME = "intermediate_mp_term_name";

    public final static String MALE_MP_TERM_ID = "male_mp_term_id";
    public final static String MALE_MP_TERM_NAME = "male_mp_term_name";
    public final static String MALE_TOP_LEVEL_MP_TERM_ID = "male_top_level_mp_term_id";
    public final static String MALE_TOP_LEVEL_MP_TERM_NAME = "male_top_level_mp_term_name";
    public final static String MALE_INTERMEDIATE_MP_TERM_ID = "male_intermediate_mp_term_id";
    public final static String MALE_INTERMEDIATE_MP_TERM_NAME = "male_intermediate_mp_term_name";

    public final static String FEMALE_MP_TERM_ID = "female_mp_term_id";
    public final static String FEMALE_MP_TERM_NAME = "female_mp_term_name";
    public final static String FEMALE_TOP_LEVEL_MP_TERM_ID = "female_top_level_mp_term_id";
    public final static String FEMALE_TOP_LEVEL_MP_TERM_NAME = "female_top_level_mp_term_name";
    public final static String FEMALE_INTERMEDIATE_MP_TERM_ID = "female_intermediate_mp_term_id";
    public final static String FEMALE_INTERMEDIATE_MP_TERM_NAME = "female_intermediate_mp_term_name";

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
    public static final String GENETIC_BACKGROUND = "genetic_background";
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

    public final static String MALE_CONTROL_MEAN = "male_control_mean";
    public final static String MALE_MUTANT_MEAN = "male_mutant_mean";
    public final static String FEMALE_CONTROL_MEAN = "female_control_mean";
    public final static String FEMALE_MUTANT_MEAN = "female_mutant_mean";

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
    
    public final static String FEMALE_PERCENTAGE_CHANGE = "female_percentage_change";
    public final static String MALE_PERCENTAGE_CHANGE = "male_percentage_change";
    
    public final static String SEX_EFFECT_P_VALUE = "sex_effect_p_value";
    public final static String SEX_EFFECT_STDERR_ESTIMATE = "sex_effect_stderr_estimate";
    public final static String SEX_EFFECT_PARAMETER_ESTIMATE = "sex_effect_parameter_estimate";
    public final static String WEIGHT_EFFECT_P_VALUE = "weight_effect_p_value";
    public final static String WEIGHT_EFFECT_STDERR_ESTIMATE = "weight_effect_stderr_estimate";
    public final static String WEIGHT_EFFECT_PARAMETER_ESTIMATE = "weight_effect_parameter_estimate";

    public final static String GROUP_1_GENOTYPE = "group_1_genotype";
    public final static String GROUP_1_RESIDUALS_NORMALITY_TEST = "group_1_residuals_normality_test";
    public final static String GROUP_2_GENOTYPE = "group_2_genotype";
    public final static String GROUP_2_RESIDUALS_NORMALITY_TEST = "group_2_residuals_normality_test";
    public final static String BLUPS_TEST = "blups_test";
    public final static String ROTATED_RESIDUALS_TEST = "rotated_residuals_test";

    public final static String INTERCEPT_ESTIMATE = "intercept_estimate";
    public final static String INTERCEPT_ESTIMATE_STDERR_ESTIMATE = "intercept_estimate_stderr_estimate";
    public final static String INTERACTION_SIGNIFICANT = "interaction_significant";
    public final static String INTERACTION_EFFECT_P_VALUE = "interaction_effect_p_value";
    public final static String FEMALE_KO_EFFECT_P_VALUE = "female_ko_effect_p_value";
    public final static String FEMALE_KO_EFFECT_STDERR_ESTIMATE = "female_ko_effect_stderr_estimate";
    public final static String FEMALE_KO_PARAMETER_ESTIMATE = "female_ko_parameter_estimate";
    public final static String MALE_KO_EFFECT_P_VALUE = "male_ko_effect_p_value";
    public final static String MALE_KO_EFFECT_STDERR_ESTIMATE = "male_ko_effect_stderr_estimate";
    public final static String MALE_KO_PARAMETER_ESTIMATE = "male_ko_parameter_estimate";
    public final static String CLASSIFICATION_TAG = "classification_tag";

    public final static String EXTERNAL_DB_ID = "external_db_id";
    public final static String ORGANISATION_ID = "organisation_id";
    public final static String PHENOTYPING_CENTER_ID = "phenotyping_center_id";
    public final static String PROJECT_ID = "project_id";

    @Field(DOCUMENT_ID)
    private String docId;

    @Field(DB_ID)
    private Integer dbId;

    @Field(DATA_TYPE)
    private String dataType;

    @Field(MP_TERM_ID)
    private String mpTermId;

    @Field(MP_TERM_NAME)
    private String mpTermName;

    @Field(TOP_LEVEL_MP_TERM_ID)
    private List<String> topLevelMpTermId;

    @Field(TOP_LEVEL_MP_TERM_NAME)
    private List<String> topLevelMpTermName;

    @Field(INTERMEDIATE_MP_TERM_ID)
    private List<String> intermediateMpTermId;

    @Field(INTERMEDIATE_MP_TERM_NAME)
    private List<String> intermediateMpTermName;


    @Field(MALE_MP_TERM_ID)
    private String maleMpTermId;

    @Field(MALE_MP_TERM_NAME)
    private String maleMpTermName;

    @Field(MALE_TOP_LEVEL_MP_TERM_ID)
    private List<String> maleTopLevelMpTermId;

    @Field(MALE_TOP_LEVEL_MP_TERM_NAME)
    private List<String> maleTopLevelMpTermName;

    @Field(MALE_INTERMEDIATE_MP_TERM_ID)
    private List<String> maleIntermediateMpTermId;

    @Field(MALE_INTERMEDIATE_MP_TERM_NAME)
    private List<String> maleIntermediateMpTermName;


    @Field(FEMALE_MP_TERM_ID)
    private String femaleMpTermId;

    @Field(FEMALE_MP_TERM_NAME)
    private String femaleMpTermName;

    @Field(FEMALE_TOP_LEVEL_MP_TERM_ID)
    private List<String> femaleTopLevelMpTermId;

    @Field(FEMALE_TOP_LEVEL_MP_TERM_NAME)
    private List<String> femaleTopLevelMpTermName;

    @Field(FEMALE_INTERMEDIATE_MP_TERM_ID)
    private List<String> femaleIntermediateMpTermId;

    @Field(FEMALE_INTERMEDIATE_MP_TERM_NAME)
    private List<String> femaleIntermediateMpTermName;



    @Field(RESOURCE_NAME)
    private String resourceName;

    @Field(RESOURCE_FULLNAME)
    private String resourceFullname;

    @Field(RESOURCE_ID)
    private Integer resourceId;

    @Field(PROJECT_NAME)
    private String projectName;

    @Field(PHENOTYPING_CENTER)
    private String phenotypingCenter;

    @Field(PIPELINE_STABLE_ID)
    private String pipelineStableId;

    @Field(PIPELINE_STABLE_KEY)
    private String pipelineStableKey;

    @Field(PIPELINE_NAME)
    private String pipelineName;

    @Field(PIPELINE_ID)
    private Integer pipelineId;

    @Field(PROCEDURE_STABLE_ID)
    private String procedureStableId;

    @Field(PROCEDURE_STABLE_KEY)
    private String procedureStableKey;

    @Field(PROCEDURE_NAME)
    private String procedureName;

    @Field(PROCEDURE_ID)
    private Integer procedureId;

    @Field(PARAMETER_STABLE_ID)
    private String parameterStableId;

    @Field(PARAMETER_STABLE_KEY)
    private String parameterStableKey;

    @Field(PARAMETER_NAME)
    private String parameterName;

    @Field(PARAMETER_ID)
    private Integer parameterId;

    @Field(COLONY_ID)
    private String colonyId;

    @Field(MARKER_SYMBOL)
    private String markerSymbol;

    @Field(MARKER_ACCESSION_ID)
    private String markerAccessionId;

    @Field(ALLELE_SYMBOL)
    private String alleleSymbol;

    @Field(ALLELE_NAME)
    private String alleleName;

    @Field(ALLELE_ACCESSION_ID)
    private String alleleAccessionId;

    @Field(STRAIN_NAME)
    private String strainName;

    @Field(STRAIN_ACCESSION_ID)
    private String strainAccessionId;

    @Field(GENETIC_BACKGROUND)
    String geneticBackground;
    
    @Field(SEX)
    private String sex;

    @Field(ZYGOSITY)
    private String zygosity;

    @Field(CONTROL_SELECTION_METHOD)
    private String controlSelectionMethod;

    @Field(DEPENDENT_VARIABLE)
    private String dependentVariable;

    @Field(METADATA_GROUP)
    private String metadataGroup;

    @Field(CONTROL_BIOLOGICAL_MODEL_ID)
    private Integer controlBiologicalModelId;

    @Field(MUTANT_BIOLOGICAL_MODEL_ID)
    private Integer mutantBiologicalModelId;

    @Field(MALE_CONTROL_COUNT)
    private Integer maleControlCount;

    @Field(MALE_MUTANT_COUNT)
    private Integer maleMutantCount;

    @Field(FEMALE_CONTROL_COUNT)
    private Integer femaleControlCount;

    @Field(FEMALE_MUTANT_COUNT)
    private Integer femaleMutantCount;

    @Field(FEMALE_MUTANT_MEAN)
    private Double femaleMutantMean;

    @Field(FEMALE_CONTROL_MEAN)
    private Double femaleControlMean;

    @Field(MALE_MUTANT_MEAN)
    private Double maleMutantMean;

    @Field(MALE_CONTROL_MEAN)
    private Double maleControlMean;

    @Field(STATISTICAL_METHOD)
    private String statisticalMethod;

    @Field(STATUS)
    private String status;

    @Field(ADDITIONAL_INFORMATION)
    private String additionalInformation;

    @Field(RAW_OUTPUT)
    private String rawOutput;

    @Field(P_VALUE)
    private Double pValue;

    @Field(EFFECT_SIZE)
    private Double effectSize;

    @Field(CATEGORIES)
    private List<String> categories;

    @Field(CATEGORICAL_P_VALUE)
    private Double categoricalPValue;

    @Field(CATEGORICAL_EFFECT_SIZE)
    private Double categoricalEffectSize;

    @Field(BATCH_SIGNIFICANT)
    private Boolean batchSignificant;

    @Field(VARIANCE_SIGNIFICANT)
    private Boolean varianceSignificant;

    @Field(NULL_TEST_P_VALUE)
    private Double nullTestPValue;

    @Field(GENOTYPE_EFFECT_P_VALUE)
    private Double genotypeEffectPValue;

    @Field(GENOTYPE_EFFECT_STDERR_ESTIMATE)
    private Double genotypeEffectStderrEstimate;

    @Field(GENOTYPE_EFFECT_PARAMETER_ESTIMATE)
    private Double genotypeEffectParameterEstimate;

    @Field(FEMALE_PERCENTAGE_CHANGE)
    private String femalePercentageChange;

    @Field(MALE_PERCENTAGE_CHANGE)
    private String malePercentageChange;
    
    @Field(SEX_EFFECT_P_VALUE)
    private Double sexEffectPValue;

    @Field(SEX_EFFECT_STDERR_ESTIMATE)
    private Double sexEffectStderrEstimate;

    @Field(SEX_EFFECT_PARAMETER_ESTIMATE)
    private Double sexEffectParameterEstimate;

    @Field(WEIGHT_EFFECT_P_VALUE)
    private Double weightEffectPValue;

    @Field(WEIGHT_EFFECT_STDERR_ESTIMATE)
    private Double weightEffectStderrEstimate;

    @Field(WEIGHT_EFFECT_PARAMETER_ESTIMATE)
    private Double weightEffectParameterEstimate;

    @Field(GROUP_1_GENOTYPE)
    private String group1Genotype;

    @Field(GROUP_1_RESIDUALS_NORMALITY_TEST)
    private Double group1ResidualsNormalityTest;

    @Field(GROUP_2_GENOTYPE)
    private String group2Genotype;

    @Field(GROUP_2_RESIDUALS_NORMALITY_TEST)
    private Double group2ResidualsNormalityTest;

    @Field(BLUPS_TEST)
    private Double blupsTest;

    @Field(ROTATED_RESIDUALS_TEST)
    private Double rotatedResidualsTest;

    @Field(INTERCEPT_ESTIMATE)
    private Double interceptEstimate;

    @Field(INTERCEPT_ESTIMATE_STDERR_ESTIMATE)
    private Double interceptEstimateStderrEstimate;

    @Field(INTERACTION_SIGNIFICANT)
    private Boolean interactionSignificant;

    @Field(INTERACTION_EFFECT_P_VALUE)
    private Double interactionEffectPValue;

    @Field(FEMALE_KO_EFFECT_P_VALUE)
    private Double femaleKoEffectPValue;

    @Field(FEMALE_KO_EFFECT_STDERR_ESTIMATE)
    private Double femaleKoEffectStderrEstimate;

    @Field(FEMALE_KO_PARAMETER_ESTIMATE)
    private Double femaleKoParameterEstimate;

    @Field(MALE_KO_EFFECT_P_VALUE)
    private Double maleKoEffectPValue;

    @Field(MALE_KO_EFFECT_STDERR_ESTIMATE)
    private Double maleKoEffectStderrEstimate;

    @Field(MALE_KO_PARAMETER_ESTIMATE)
    private Double maleKoParameterEstimate;

    @Field(CLASSIFICATION_TAG)
    private String classificationTag;
    
    @Field(EXTERNAL_DB_ID)
    private Integer externalDbId;
    
    @Field(ORGANISATION_ID)
    private Integer organisationId;
    
    @Field(PHENOTYPING_CENTER_ID)
    private Integer phenotypingCenterId;
    
    @Field(PROJECT_ID)
    private Integer projectId;

    public String getDocId() {

        return docId;
    }


    public void setDocId(String docId) {

        this.docId = docId;
    }


    public Integer getDbId() {

        return dbId;
    }


    public void setDbId(Integer dbId) {

        this.dbId = dbId;
    }


    public String getDataType() {

        return dataType;
    }


    public void setDataType(String dataType) {

        this.dataType = dataType;
    }


    public String getMpTermId() {

        return mpTermId;
    }


    public void setMpTermId(String mpTermId) {

        this.mpTermId = mpTermId;
    }


    public String getMpTermName() {

        return mpTermName;
    }


    public void setMpTermName(String mpTermName) {

        this.mpTermName = mpTermName;
    }


    public List<String> getTopLevelMpTermId() {

        return topLevelMpTermId;
    }


    public void setTopLevelMpTermId(List<String> topLevelMpTermId) {

        this.topLevelMpTermId = topLevelMpTermId;
    }


    public List<String> getTopLevelMpTermName() {

        return topLevelMpTermName;
    }


    public void setTopLevelMpTermName(List<String> topLevelMpTermName) {

        this.topLevelMpTermName = topLevelMpTermName;
    }


    public List<String> getIntermediateMpTermId() {

        return intermediateMpTermId;
    }


    public void setIntermediateMpTermId(List<String> intermediateMpTermId) {

        this.intermediateMpTermId = intermediateMpTermId;
    }


    public List<String> getIntermediateMpTermName() {

        return intermediateMpTermName;
    }


    public void setIntermediateMpTermName(List<String> intermediateMpTermName) {

        this.intermediateMpTermName = intermediateMpTermName;
    }


    public String getMaleMpTermId() {

        return maleMpTermId;
    }


    public void setMaleMpTermId(String maleMpTermId) {

        this.maleMpTermId = maleMpTermId;
    }


    public String getMaleMpTermName() {

        return maleMpTermName;
    }


    public void setMaleMpTermName(String maleMpTermName) {

        this.maleMpTermName = maleMpTermName;
    }


    public List<String> getMaleTopLevelMpTermId() {

        return maleTopLevelMpTermId;
    }


    public void setMaleTopLevelMpTermId(List<String> maleTopLevelMpTermId) {

        this.maleTopLevelMpTermId = maleTopLevelMpTermId;
    }


    public List<String> getMaleTopLevelMpTermName() {

        return maleTopLevelMpTermName;
    }


    public void setMaleTopLevelMpTermName(List<String> maleTopLevelMpTermName) {

        this.maleTopLevelMpTermName = maleTopLevelMpTermName;
    }


    public List<String> getMaleIntermediateMpTermId() {

        return maleIntermediateMpTermId;
    }


    public void setMaleIntermediateMpTermId(List<String> maleIntermediateMpTermId) {

        this.maleIntermediateMpTermId = maleIntermediateMpTermId;
    }


    public List<String> getMaleIntermediateMpTermName() {

        return maleIntermediateMpTermName;
    }


    public void setMaleIntermediateMpTermName(List<String> maleIntermediateMpTermName) {

        this.maleIntermediateMpTermName = maleIntermediateMpTermName;
    }


    public String getFemaleMpTermId() {

        return femaleMpTermId;
    }


    public void setFemaleMpTermId(String femaleMpTermId) {

        this.femaleMpTermId = femaleMpTermId;
    }


    public String getFemaleMpTermName() {

        return femaleMpTermName;
    }


    public void setFemaleMpTermName(String femaleMpTermName) {

        this.femaleMpTermName = femaleMpTermName;
    }


    public List<String> getFemaleTopLevelMpTermId() {

        return femaleTopLevelMpTermId;
    }


    public void setFemaleTopLevelMpTermId(List<String> femaleTopLevelMpTermId) {

        this.femaleTopLevelMpTermId = femaleTopLevelMpTermId;
    }


    public List<String> getFemaleTopLevelMpTermName() {

        return femaleTopLevelMpTermName;
    }


    public void setFemaleTopLevelMpTermName(List<String> femaleTopLevelMpTermName) {

        this.femaleTopLevelMpTermName = femaleTopLevelMpTermName;
    }


    public List<String> getFemaleIntermediateMpTermId() {

        return femaleIntermediateMpTermId;
    }


    public void setFemaleIntermediateMpTermId(List<String> femaleIntermediateMpTermId) {

        this.femaleIntermediateMpTermId = femaleIntermediateMpTermId;
    }


    public List<String> getFemaleIntermediateMpTermName() {

        return femaleIntermediateMpTermName;
    }


    public void setFemaleIntermediateMpTermName(List<String> femaleIntermediateMpTermName) {

        this.femaleIntermediateMpTermName = femaleIntermediateMpTermName;
    }


    public String getResourceName() {

        return resourceName;
    }


    public void setResourceName(String resourceName) {

        this.resourceName = resourceName;
    }


    public String getResourceFullname() {

        return resourceFullname;
    }


    public void setResourceFullname(String resourceFullname) {

        this.resourceFullname = resourceFullname;
    }


    public Integer getResourceId() {

        return resourceId;
    }


    public void setResourceId(Integer resourceId) {

        this.resourceId = resourceId;
    }


    public String getProjectName() {

        return projectName;
    }


    public void setProjectName(String projectName) {

        this.projectName = projectName;
    }


    public String getPhenotypingCenter() {

        return phenotypingCenter;
    }


    public void setPhenotypingCenter(String phenotypingCenter) {

        this.phenotypingCenter = phenotypingCenter;
    }


    public String getPipelineStableId() {

        return pipelineStableId;
    }


    public void setPipelineStableId(String pipelineStableId) {

        this.pipelineStableId = pipelineStableId;
    }


    public String getPipelineStableKey() {

        return pipelineStableKey;
    }


    public void setPipelineStableKey(String pipelineStableKey) {

        this.pipelineStableKey = pipelineStableKey;
    }


    public String getPipelineName() {

        return pipelineName;
    }


    public void setPipelineName(String pipelineName) {

        this.pipelineName = pipelineName;
    }


    public Integer getPipelineId() {

        return pipelineId;
    }


    public void setPipelineId(Integer pipelineId) {

        this.pipelineId = pipelineId;
    }


    public String getProcedureStableId() {

        return procedureStableId;
    }


    public void setProcedureStableId(String procedureStableId) {

        this.procedureStableId = procedureStableId;
    }


    public String getProcedureStableKey() {

        return procedureStableKey;
    }


    public void setProcedureStableKey(String procedureStableKey) {

        this.procedureStableKey = procedureStableKey;
    }


    public String getProcedureName() {

        return procedureName;
    }


    public void setProcedureName(String procedureName) {

        this.procedureName = procedureName;
    }


    public Integer getProcedureId() {

        return procedureId;
    }


    public void setProcedureId(Integer procedureId) {

        this.procedureId = procedureId;
    }


    public String getParameterStableId() {

        return parameterStableId;
    }


    public void setParameterStableId(String parameterStableId) {

        this.parameterStableId = parameterStableId;
    }


    public String getParameterStableKey() {

        return parameterStableKey;
    }


    public void setParameterStableKey(String parameterStableKey) {

        this.parameterStableKey = parameterStableKey;
    }


    public String getParameterName() {

        return parameterName;
    }


    public void setParameterName(String parameterName) {

        this.parameterName = parameterName;
    }


    public Integer getParameterId() {

        return parameterId;
    }


    public void setParameterId(Integer parameterId) {

        this.parameterId = parameterId;
    }


    public String getColonyId() {

        return colonyId;
    }


    public void setColonyId(String colonyId) {

        this.colonyId = colonyId;
    }


    public String getMarkerSymbol() {

        return markerSymbol;
    }


    public void setMarkerSymbol(String markerSymbol) {

        this.markerSymbol = markerSymbol;
    }


    public String getMarkerAccessionId() {

        return markerAccessionId;
    }


    public void setMarkerAccessionId(String markerAccessionId) {

        this.markerAccessionId = markerAccessionId;
    }


    public String getAlleleSymbol() {

        return alleleSymbol;
    }


    public void setAlleleSymbol(String alleleSymbol) {

        this.alleleSymbol = alleleSymbol;
    }


    public String getAlleleName() {

        return alleleName;
    }


    public void setAlleleName(String alleleName) {

        this.alleleName = alleleName;
    }


    public String getAlleleAccessionId() {

        return alleleAccessionId;
    }


    public void setAlleleAccessionId(String alleleAccessionId) {

        this.alleleAccessionId = alleleAccessionId;
    }


    public String getStrainName() {

        return strainName;
    }


    public void setStrainName(String strainName) {

        this.strainName = strainName;
    }


    public String getStrainAccessionId() {

        return strainAccessionId;
    }


    public void setStrainAccessionId(String strainAccessionId) {

        this.strainAccessionId = strainAccessionId;
    }


    public String getGeneticBackground() {
        return geneticBackground;
    }

    public void setGeneticBackground(String geneticBackground) {
        this.geneticBackground = geneticBackground;
    }

    public String getSex() {

        return sex;
    }


    public void setSex(String sex) {

        this.sex = sex;
    }


    public String getZygosity() {

        return zygosity;
    }


    public void setZygosity(String zygosity) {

        this.zygosity = zygosity;
    }


    public String getControlSelectionMethod() {

        return controlSelectionMethod;
    }


    public void setControlSelectionMethod(String controlSelectionMethod) {

        this.controlSelectionMethod = controlSelectionMethod;
    }


    public String getDependentVariable() {

        return dependentVariable;
    }


    public void setDependentVariable(String dependentVariable) {

        this.dependentVariable = dependentVariable;
    }


    public String getMetadataGroup() {

        return metadataGroup;
    }


    public void setMetadataGroup(String metadataGroup) {

        this.metadataGroup = metadataGroup;
    }


    public Integer getControlBiologicalModelId() {

        return controlBiologicalModelId;
    }


    public void setControlBiologicalModelId(Integer controlBiologicalModelId) {

        this.controlBiologicalModelId = controlBiologicalModelId;
    }


    public Integer getMutantBiologicalModelId() {

        return mutantBiologicalModelId;
    }


    public void setMutantBiologicalModelId(Integer mutantBiologicalModelId) {

        this.mutantBiologicalModelId = mutantBiologicalModelId;
    }


    public Integer getMaleControlCount() {

        return maleControlCount;
    }


    public void setMaleControlCount(Integer maleControlCount) {

        this.maleControlCount = maleControlCount;
    }


    public Integer getMaleMutantCount() {

        return maleMutantCount;
    }


    public void setMaleMutantCount(Integer maleMutantCount) {

        this.maleMutantCount = maleMutantCount;
    }


    public Integer getFemaleControlCount() {

        return femaleControlCount;
    }


    public void setFemaleControlCount(Integer femaleControlCount) {

        this.femaleControlCount = femaleControlCount;
    }


    public Integer getFemaleMutantCount() {

        return femaleMutantCount;
    }


    public void setFemaleMutantCount(Integer femaleMutantCount) {

        this.femaleMutantCount = femaleMutantCount;
    }


    public Double getFemaleMutantMean() {

        return femaleMutantMean;
    }


    public void setFemaleMutantMean(Double femaleMutantMean) {

        this.femaleMutantMean = femaleMutantMean;
    }


    public Double getFemaleControlMean() {

        return femaleControlMean;
    }


    public void setFemaleControlMean(Double femaleControlMean) {

        this.femaleControlMean = femaleControlMean;
    }


    public Double getMaleMutantMean() {

        return maleMutantMean;
    }


    public void setMaleMutantMean(Double maleMutantMean) {

        this.maleMutantMean = maleMutantMean;
    }


    public Double getMaleControlMean() {

        return maleControlMean;
    }


    public void setMaleControlMean(Double maleControlMean) {

        this.maleControlMean = maleControlMean;
    }



    public String getStatisticalMethod() {

        return statisticalMethod;
    }


    public void setStatisticalMethod(String statisticalMethod) {

        this.statisticalMethod = statisticalMethod;
    }


    public String getStatus() {

        return status;
    }


    public void setStatus(String status) {

        this.status = status;
    }


    public String getAdditionalInformation() {

        return additionalInformation;
    }


    public void setAdditionalInformation(String additionalInformation) {

        this.additionalInformation = additionalInformation;
    }


    public String getRawOutput() {

        return rawOutput;
    }


    public void setRawOutput(String rawOutput) {

        this.rawOutput = rawOutput;
    }


    public Double getpValue() {

        return pValue;
    }


    public void setpValue(Double pValue) {

        this.pValue = pValue;
    }


    public Double getEffectSize() {

        return effectSize;
    }


    public void setEffectSize(Double effectSize) {

        this.effectSize = effectSize;
    }


    public List<String> getCategories() {

        return categories;
    }


    public void setCategories(List<String> categories) {

        this.categories = categories;
    }


    public Double getCategoricalPValue() {

        return categoricalPValue;
    }


    public void setCategoricalPValue(Double categoricalPValue) {

        this.categoricalPValue = categoricalPValue;
    }


    public Double getCategoricalEffectSize() {

        return categoricalEffectSize;
    }


    public void setCategoricalEffectSize(Double categoricalEffectSize) {

        this.categoricalEffectSize = categoricalEffectSize;
    }


    public Boolean getBatchSignificant() {

        return batchSignificant;
    }


    public void setBatchSignificant(Boolean batchSignificant) {

        this.batchSignificant = batchSignificant;
    }


    public Boolean getVarianceSignificant() {

        return varianceSignificant;
    }


    public void setVarianceSignificant(Boolean varianceSignificant) {

        this.varianceSignificant = varianceSignificant;
    }


    public Double getNullTestPValue() {

        return nullTestPValue;
    }


    public void setNullTestPValue(Double nullTestPValue) {

        this.nullTestPValue = nullTestPValue;
    }


    public Double getGenotypeEffectPValue() {

        return genotypeEffectPValue;
    }


    public void setGenotypeEffectPValue(Double genotypeEffectPValue) {

        this.genotypeEffectPValue = genotypeEffectPValue;
    }


    public Double getGenotypeEffectStderrEstimate() {

        return genotypeEffectStderrEstimate;
    }


    public void setGenotypeEffectStderrEstimate(Double genotypeEffectStderrEstimate) {

        this.genotypeEffectStderrEstimate = genotypeEffectStderrEstimate;
    }


    public Double getGenotypeEffectParameterEstimate() {

        return genotypeEffectParameterEstimate;
    }


    public void setGenotypeEffectParameterEstimate(Double genotypeEffectParameterEstimate) {

        this.genotypeEffectParameterEstimate = genotypeEffectParameterEstimate;
    }

    public String getFemalePercentageChange() {
        return femalePercentageChange;
    }

    public void setFemalePercentageChange(String femalePercentageChange) {
        this.femalePercentageChange = femalePercentageChange;
    }

    public String getMalePercentageChange() {
        return malePercentageChange;
    }

    public void setMalePercentageChange(String malePercentageChange) {
        this.malePercentageChange = malePercentageChange;
    }


    public Double getSexEffectPValue() {

        return sexEffectPValue;
    }


    public void setSexEffectPValue(Double sexEffectPValue) {

        this.sexEffectPValue = sexEffectPValue;
    }


    public Double getSexEffectStderrEstimate() {

        return sexEffectStderrEstimate;
    }


    public void setSexEffectStderrEstimate(Double sexEffectStderrEstimate) {

        this.sexEffectStderrEstimate = sexEffectStderrEstimate;
    }


    public Double getSexEffectParameterEstimate() {

        return sexEffectParameterEstimate;
    }


    public void setSexEffectParameterEstimate(Double sexEffectParameterEstimate) {

        this.sexEffectParameterEstimate = sexEffectParameterEstimate;
    }


    public Double getWeightEffectPValue() {

        return weightEffectPValue;
    }


    public void setWeightEffectPValue(Double weightEffectPValue) {

        this.weightEffectPValue = weightEffectPValue;
    }


    public Double getWeightEffectStderrEstimate() {

        return weightEffectStderrEstimate;
    }


    public void setWeightEffectStderrEstimate(Double weightEffectStderrEstimate) {

        this.weightEffectStderrEstimate = weightEffectStderrEstimate;
    }


    public Double getWeightEffectParameterEstimate() {

        return weightEffectParameterEstimate;
    }


    public void setWeightEffectParameterEstimate(Double weightEffectParameterEstimate) {

        this.weightEffectParameterEstimate = weightEffectParameterEstimate;
    }


    public String getGroup1Genotype() {

        return group1Genotype;
    }


    public void setGroup1Genotype(String group1Genotype) {

        this.group1Genotype = group1Genotype;
    }


    public Double getGroup1ResidualsNormalityTest() {

        return group1ResidualsNormalityTest;
    }


    public void setGroup1ResidualsNormalityTest(Double group1ResidualsNormalityTest) {

        this.group1ResidualsNormalityTest = group1ResidualsNormalityTest;
    }


    public String getGroup2Genotype() {

        return group2Genotype;
    }


    public void setGroup2Genotype(String group2Genotype) {

        this.group2Genotype = group2Genotype;
    }


    public Double getGroup2ResidualsNormalityTest() {

        return group2ResidualsNormalityTest;
    }


    public void setGroup2ResidualsNormalityTest(Double group2ResidualsNormalityTest) {

        this.group2ResidualsNormalityTest = group2ResidualsNormalityTest;
    }


    public Double getBlupsTest() {

        return blupsTest;
    }


    public void setBlupsTest(Double blupsTest) {

        this.blupsTest = blupsTest;
    }


    public Double getRotatedResidualsTest() {

        return rotatedResidualsTest;
    }


    public void setRotatedResidualsTest(Double rotatedResidualsTest) {

        this.rotatedResidualsTest = rotatedResidualsTest;
    }


    public Double getInterceptEstimate() {

        return interceptEstimate;
    }


    public void setInterceptEstimate(Double interceptEstimate) {

        this.interceptEstimate = interceptEstimate;
    }


    public Double getInterceptEstimateStderrEstimate() {

        return interceptEstimateStderrEstimate;
    }


    public void setInterceptEstimateStderrEstimate(Double interceptEstimateStderrEstimate) {

        this.interceptEstimateStderrEstimate = interceptEstimateStderrEstimate;
    }


    public Boolean getInteractionSignificant() {

        return interactionSignificant;
    }


    public void setInteractionSignificant(Boolean interactionSignificant) {

        this.interactionSignificant = interactionSignificant;
    }


    public Double getInteractionEffectPValue() {

        return interactionEffectPValue;
    }


    public void setInteractionEffectPValue(Double interactionEffectPValue) {

        this.interactionEffectPValue = interactionEffectPValue;
    }


    public Double getFemaleKoEffectPValue() {

        return femaleKoEffectPValue;
    }


    public void setFemaleKoEffectPValue(Double femaleKoEffectPValue) {

        this.femaleKoEffectPValue = femaleKoEffectPValue;
    }


    public Double getFemaleKoEffectStderrEstimate() {

        return femaleKoEffectStderrEstimate;
    }


    public void setFemaleKoEffectStderrEstimate(Double femaleKoEffectStderrEstimate) {

        this.femaleKoEffectStderrEstimate = femaleKoEffectStderrEstimate;
    }


    public Double getFemaleKoParameterEstimate() {

        return femaleKoParameterEstimate;
    }


    public void setFemaleKoParameterEstimate(Double femaleKoParameterEstimate) {

        this.femaleKoParameterEstimate = femaleKoParameterEstimate;
    }


    public Double getMaleKoEffectPValue() {

        return maleKoEffectPValue;
    }


    public void setMaleKoEffectPValue(Double maleKoEffectPValue) {

        this.maleKoEffectPValue = maleKoEffectPValue;
    }


    public Double getMaleKoEffectStderrEstimate() {

        return maleKoEffectStderrEstimate;
    }


    public void setMaleKoEffectStderrEstimate(Double maleKoEffectStderrEstimate) {

        this.maleKoEffectStderrEstimate = maleKoEffectStderrEstimate;
    }


    public Double getMaleKoParameterEstimate() {

        return maleKoParameterEstimate;
    }


    public void setMaleKoParameterEstimate(Double maleKoParameterEstimate) {

        this.maleKoParameterEstimate = maleKoParameterEstimate;
    }


    public String getClassificationTag() {

        return classificationTag;
    }


    public void setClassificationTag(String classificationTag) {

        this.classificationTag = classificationTag;
    }

    public Integer getExternalDbId() {
        return externalDbId;
    }

    public void setExternalDbId(Integer externalDbId) {
        this.externalDbId = externalDbId;
    }

    public Integer getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Integer organisationId) {
        this.organisationId = organisationId;
    }

    public Integer getPhenotypingCenterId() {
        return phenotypingCenterId;
    }

    public void setPhenotypingCenterId(Integer phenotypingCenterId) {
        this.phenotypingCenterId = phenotypingCenterId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof StatisticalResultDTO)) {
            return false;
        }

        StatisticalResultDTO that = (StatisticalResultDTO) o;

        if (docId != null ? !docId.equals(that.docId) : that.docId != null) {
            return false;
        }
        if (dbId != null ? !dbId.equals(that.dbId) : that.dbId != null) {
            return false;
        }
        if (dataType != null ? !dataType.equals(that.dataType) : that.dataType != null) {
            return false;
        }
        if (mpTermId != null ? !mpTermId.equals(that.mpTermId) : that.mpTermId != null) {
            return false;
        }
        if (mpTermName != null ? !mpTermName.equals(that.mpTermName) : that.mpTermName != null) {
            return false;
        }
        if (topLevelMpTermId != null ? !topLevelMpTermId.equals(that.topLevelMpTermId) : that.topLevelMpTermId != null) {
            return false;
        }
        if (topLevelMpTermName != null ? !topLevelMpTermName.equals(that.topLevelMpTermName) : that.topLevelMpTermName != null) {
            return false;
        }
        if (intermediateMpTermId != null ? !intermediateMpTermId.equals(that.intermediateMpTermId) : that.intermediateMpTermId != null) {
            return false;
        }
        if (intermediateMpTermName != null ? !intermediateMpTermName.equals(that.intermediateMpTermName) : that.intermediateMpTermName != null) {
            return false;
        }
        if (maleMpTermId != null ? !maleMpTermId.equals(that.maleMpTermId) : that.maleMpTermId != null) {
            return false;
        }
        if (maleMpTermName != null ? !maleMpTermName.equals(that.maleMpTermName) : that.maleMpTermName != null) {
            return false;
        }
        if (maleTopLevelMpTermId != null ? !maleTopLevelMpTermId.equals(that.maleTopLevelMpTermId) : that.maleTopLevelMpTermId != null) {
            return false;
        }
        if (maleTopLevelMpTermName != null ? !maleTopLevelMpTermName.equals(that.maleTopLevelMpTermName) : that.maleTopLevelMpTermName != null) {
            return false;
        }
        if (maleIntermediateMpTermId != null ? !maleIntermediateMpTermId.equals(that.maleIntermediateMpTermId) : that.maleIntermediateMpTermId != null) {
            return false;
        }
        if (maleIntermediateMpTermName != null ? !maleIntermediateMpTermName.equals(that.maleIntermediateMpTermName) : that.maleIntermediateMpTermName != null) {
            return false;
        }
        if (femaleMpTermId != null ? !femaleMpTermId.equals(that.femaleMpTermId) : that.femaleMpTermId != null) {
            return false;
        }
        if (femaleMpTermName != null ? !femaleMpTermName.equals(that.femaleMpTermName) : that.femaleMpTermName != null) {
            return false;
        }
        if (femaleTopLevelMpTermId != null ? !femaleTopLevelMpTermId.equals(that.femaleTopLevelMpTermId) : that.femaleTopLevelMpTermId != null) {
            return false;
        }
        if (femaleTopLevelMpTermName != null ? !femaleTopLevelMpTermName.equals(that.femaleTopLevelMpTermName) : that.femaleTopLevelMpTermName != null) {
            return false;
        }
        if (femaleIntermediateMpTermId != null ? !femaleIntermediateMpTermId.equals(that.femaleIntermediateMpTermId) : that.femaleIntermediateMpTermId != null) {
            return false;
        }
        if (femaleIntermediateMpTermName != null ? !femaleIntermediateMpTermName.equals(that.femaleIntermediateMpTermName) : that.femaleIntermediateMpTermName != null) {
            return false;
        }
        if (resourceName != null ? !resourceName.equals(that.resourceName) : that.resourceName != null) {
            return false;
        }
        if (resourceFullname != null ? !resourceFullname.equals(that.resourceFullname) : that.resourceFullname != null) {
            return false;
        }
        if (resourceId != null ? !resourceId.equals(that.resourceId) : that.resourceId != null) {
            return false;
        }
        if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) {
            return false;
        }
        if (phenotypingCenter != null ? !phenotypingCenter.equals(that.phenotypingCenter) : that.phenotypingCenter != null) {
            return false;
        }
        if (pipelineStableId != null ? !pipelineStableId.equals(that.pipelineStableId) : that.pipelineStableId != null) {
            return false;
        }
        if (pipelineStableKey != null ? !pipelineStableKey.equals(that.pipelineStableKey) : that.pipelineStableKey != null) {
            return false;
        }
        if (pipelineName != null ? !pipelineName.equals(that.pipelineName) : that.pipelineName != null) {
            return false;
        }
        if (pipelineId != null ? !pipelineId.equals(that.pipelineId) : that.pipelineId != null) {
            return false;
        }
        if (procedureStableId != null ? !procedureStableId.equals(that.procedureStableId) : that.procedureStableId != null) {
            return false;
        }
        if (procedureStableKey != null ? !procedureStableKey.equals(that.procedureStableKey) : that.procedureStableKey != null) {
            return false;
        }
        if (procedureName != null ? !procedureName.equals(that.procedureName) : that.procedureName != null) {
            return false;
        }
        if (procedureId != null ? !procedureId.equals(that.procedureId) : that.procedureId != null) {
            return false;
        }
        if (parameterStableId != null ? !parameterStableId.equals(that.parameterStableId) : that.parameterStableId != null) {
            return false;
        }
        if (parameterStableKey != null ? !parameterStableKey.equals(that.parameterStableKey) : that.parameterStableKey != null) {
            return false;
        }
        if (parameterName != null ? !parameterName.equals(that.parameterName) : that.parameterName != null) {
            return false;
        }
        if (parameterId != null ? !parameterId.equals(that.parameterId) : that.parameterId != null) {
            return false;
        }
        if (colonyId != null ? !colonyId.equals(that.colonyId) : that.colonyId != null) {
            return false;
        }
        if (markerSymbol != null ? !markerSymbol.equals(that.markerSymbol) : that.markerSymbol != null) {
            return false;
        }
        if (markerAccessionId != null ? !markerAccessionId.equals(that.markerAccessionId) : that.markerAccessionId != null) {
            return false;
        }
        if (alleleSymbol != null ? !alleleSymbol.equals(that.alleleSymbol) : that.alleleSymbol != null) {
            return false;
        }
        if (alleleName != null ? !alleleName.equals(that.alleleName) : that.alleleName != null) {
            return false;
        }
        if (alleleAccessionId != null ? !alleleAccessionId.equals(that.alleleAccessionId) : that.alleleAccessionId != null) {
            return false;
        }
        if (strainName != null ? !strainName.equals(that.strainName) : that.strainName != null) {
            return false;
        }
        if (strainAccessionId != null ? !strainAccessionId.equals(that.strainAccessionId) : that.strainAccessionId != null) {
            return false;
        }
        if (geneticBackground != null ? !geneticBackground.equals(that.geneticBackground) : that.geneticBackground != null) {
            return false;
        }
        if (sex != null ? !sex.equals(that.sex) : that.sex != null) {
            return false;
        }
        if (zygosity != null ? !zygosity.equals(that.zygosity) : that.zygosity != null) {
            return false;
        }
        if (controlSelectionMethod != null ? !controlSelectionMethod.equals(that.controlSelectionMethod) : that.controlSelectionMethod != null) {
            return false;
        }
        if (dependentVariable != null ? !dependentVariable.equals(that.dependentVariable) : that.dependentVariable != null) {
            return false;
        }
        if (metadataGroup != null ? !metadataGroup.equals(that.metadataGroup) : that.metadataGroup != null) {
            return false;
        }
        if (controlBiologicalModelId != null ? !controlBiologicalModelId.equals(that.controlBiologicalModelId) : that.controlBiologicalModelId != null) {
            return false;
        }
        if (mutantBiologicalModelId != null ? !mutantBiologicalModelId.equals(that.mutantBiologicalModelId) : that.mutantBiologicalModelId != null) {
            return false;
        }
        if (maleControlCount != null ? !maleControlCount.equals(that.maleControlCount) : that.maleControlCount != null) {
            return false;
        }
        if (maleMutantCount != null ? !maleMutantCount.equals(that.maleMutantCount) : that.maleMutantCount != null) {
            return false;
        }
        if (femaleControlCount != null ? !femaleControlCount.equals(that.femaleControlCount) : that.femaleControlCount != null) {
            return false;
        }
        if (femaleMutantCount != null ? !femaleMutantCount.equals(that.femaleMutantCount) : that.femaleMutantCount != null) {
            return false;
        }
        if (femaleMutantMean != null ? !femaleMutantMean.equals(that.femaleMutantMean) : that.femaleMutantMean != null) {
            return false;
        }
        if (femaleControlMean != null ? !femaleControlMean.equals(that.femaleControlMean) : that.femaleControlMean != null) {
            return false;
        }
        if (maleMutantMean != null ? !maleMutantMean.equals(that.maleMutantMean) : that.maleMutantMean != null) {
            return false;
        }
        if (maleControlMean != null ? !maleControlMean.equals(that.maleControlMean) : that.maleControlMean != null) {
            return false;
        }
        if (statisticalMethod != null ? !statisticalMethod.equals(that.statisticalMethod) : that.statisticalMethod != null) {
            return false;
        }
        if (status != null ? !status.equals(that.status) : that.status != null) {
            return false;
        }
        if (additionalInformation != null ? !additionalInformation.equals(that.additionalInformation) : that.additionalInformation != null) {
            return false;
        }
        if (rawOutput != null ? !rawOutput.equals(that.rawOutput) : that.rawOutput != null) {
            return false;
        }
        if (pValue != null ? !pValue.equals(that.pValue) : that.pValue != null) {
            return false;
        }
        if (effectSize != null ? !effectSize.equals(that.effectSize) : that.effectSize != null) {
            return false;
        }
        if (categories != null ? !categories.equals(that.categories) : that.categories != null) {
            return false;
        }
        if (categoricalPValue != null ? !categoricalPValue.equals(that.categoricalPValue) : that.categoricalPValue != null) {
            return false;
        }
        if (categoricalEffectSize != null ? !categoricalEffectSize.equals(that.categoricalEffectSize) : that.categoricalEffectSize != null) {
            return false;
        }
        if (batchSignificant != null ? !batchSignificant.equals(that.batchSignificant) : that.batchSignificant != null) {
            return false;
        }
        if (varianceSignificant != null ? !varianceSignificant.equals(that.varianceSignificant) : that.varianceSignificant != null) {
            return false;
        }
        if (nullTestPValue != null ? !nullTestPValue.equals(that.nullTestPValue) : that.nullTestPValue != null) {
            return false;
        }
        if (genotypeEffectPValue != null ? !genotypeEffectPValue.equals(that.genotypeEffectPValue) : that.genotypeEffectPValue != null) {
            return false;
        }
        if (genotypeEffectStderrEstimate != null ? !genotypeEffectStderrEstimate.equals(that.genotypeEffectStderrEstimate) : that.genotypeEffectStderrEstimate != null) {
            return false;
        }
        if (genotypeEffectParameterEstimate != null ? !genotypeEffectParameterEstimate.equals(that.genotypeEffectParameterEstimate) : that.genotypeEffectParameterEstimate != null) {
            return false;
        }
        if (femalePercentageChange != null ? !femalePercentageChange.equals(that.femalePercentageChange) : that.femalePercentageChange != null) {
            return false;
        }
        if (malePercentageChange != null ? !malePercentageChange.equals(that.malePercentageChange) : that.malePercentageChange != null) {
            return false;
        }
        if (sexEffectPValue != null ? !sexEffectPValue.equals(that.sexEffectPValue) : that.sexEffectPValue != null) {
            return false;
        }
        if (sexEffectStderrEstimate != null ? !sexEffectStderrEstimate.equals(that.sexEffectStderrEstimate) : that.sexEffectStderrEstimate != null) {
            return false;
        }
        if (sexEffectParameterEstimate != null ? !sexEffectParameterEstimate.equals(that.sexEffectParameterEstimate) : that.sexEffectParameterEstimate != null) {
            return false;
        }
        if (weightEffectPValue != null ? !weightEffectPValue.equals(that.weightEffectPValue) : that.weightEffectPValue != null) {
            return false;
        }
        if (weightEffectStderrEstimate != null ? !weightEffectStderrEstimate.equals(that.weightEffectStderrEstimate) : that.weightEffectStderrEstimate != null) {
            return false;
        }
        if (weightEffectParameterEstimate != null ? !weightEffectParameterEstimate.equals(that.weightEffectParameterEstimate) : that.weightEffectParameterEstimate != null) {
            return false;
        }
        if (group1Genotype != null ? !group1Genotype.equals(that.group1Genotype) : that.group1Genotype != null) {
            return false;
        }
        if (group1ResidualsNormalityTest != null ? !group1ResidualsNormalityTest.equals(that.group1ResidualsNormalityTest) : that.group1ResidualsNormalityTest != null) {
            return false;
        }
        if (group2Genotype != null ? !group2Genotype.equals(that.group2Genotype) : that.group2Genotype != null) {
            return false;
        }
        if (group2ResidualsNormalityTest != null ? !group2ResidualsNormalityTest.equals(that.group2ResidualsNormalityTest) : that.group2ResidualsNormalityTest != null) {
            return false;
        }
        if (blupsTest != null ? !blupsTest.equals(that.blupsTest) : that.blupsTest != null) {
            return false;
        }
        if (rotatedResidualsTest != null ? !rotatedResidualsTest.equals(that.rotatedResidualsTest) : that.rotatedResidualsTest != null) {
            return false;
        }
        if (interceptEstimate != null ? !interceptEstimate.equals(that.interceptEstimate) : that.interceptEstimate != null) {
            return false;
        }
        if (interceptEstimateStderrEstimate != null ? !interceptEstimateStderrEstimate.equals(that.interceptEstimateStderrEstimate) : that.interceptEstimateStderrEstimate != null) {
            return false;
        }
        if (interactionSignificant != null ? !interactionSignificant.equals(that.interactionSignificant) : that.interactionSignificant != null) {
            return false;
        }
        if (interactionEffectPValue != null ? !interactionEffectPValue.equals(that.interactionEffectPValue) : that.interactionEffectPValue != null) {
            return false;
        }
        if (femaleKoEffectPValue != null ? !femaleKoEffectPValue.equals(that.femaleKoEffectPValue) : that.femaleKoEffectPValue != null) {
            return false;
        }
        if (femaleKoEffectStderrEstimate != null ? !femaleKoEffectStderrEstimate.equals(that.femaleKoEffectStderrEstimate) : that.femaleKoEffectStderrEstimate != null) {
            return false;
        }
        if (femaleKoParameterEstimate != null ? !femaleKoParameterEstimate.equals(that.femaleKoParameterEstimate) : that.femaleKoParameterEstimate != null) {
            return false;
        }
        if (maleKoEffectPValue != null ? !maleKoEffectPValue.equals(that.maleKoEffectPValue) : that.maleKoEffectPValue != null) {
            return false;
        }
        if (maleKoEffectStderrEstimate != null ? !maleKoEffectStderrEstimate.equals(that.maleKoEffectStderrEstimate) : that.maleKoEffectStderrEstimate != null) {
            return false;
        }
        if (maleKoParameterEstimate != null ? !maleKoParameterEstimate.equals(that.maleKoParameterEstimate) : that.maleKoParameterEstimate != null) {
            return false;
        }
        if (classificationTag != null ? !classificationTag.equals(that.classificationTag) : that.classificationTag != null) {
            return false;
        }
        if (externalDbId != null ? !externalDbId.equals(that.externalDbId) : that.externalDbId != null) {
            return false;
        }
        if (organisationId != null ? !organisationId.equals(that.organisationId) : that.organisationId != null) {
            return false;
        }
        if (phenotypingCenterId != null ? !phenotypingCenterId.equals(that.phenotypingCenterId) : that.phenotypingCenterId != null) {
            return false;
        }
        return !(projectId != null ? !projectId.equals(that.projectId) : that.projectId != null);

    }


    @Override
    public int hashCode() {

        int result = docId != null ? docId.hashCode() : 0;
        result = 31 * result + (dbId != null ? dbId.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (mpTermId != null ? mpTermId.hashCode() : 0);
        result = 31 * result + (mpTermName != null ? mpTermName.hashCode() : 0);
        result = 31 * result + (topLevelMpTermId != null ? topLevelMpTermId.hashCode() : 0);
        result = 31 * result + (topLevelMpTermName != null ? topLevelMpTermName.hashCode() : 0);
        result = 31 * result + (intermediateMpTermId != null ? intermediateMpTermId.hashCode() : 0);
        result = 31 * result + (intermediateMpTermName != null ? intermediateMpTermName.hashCode() : 0);
        result = 31 * result + (maleMpTermId != null ? maleMpTermId.hashCode() : 0);
        result = 31 * result + (maleMpTermName != null ? maleMpTermName.hashCode() : 0);
        result = 31 * result + (maleTopLevelMpTermId != null ? maleTopLevelMpTermId.hashCode() : 0);
        result = 31 * result + (maleTopLevelMpTermName != null ? maleTopLevelMpTermName.hashCode() : 0);
        result = 31 * result + (maleIntermediateMpTermId != null ? maleIntermediateMpTermId.hashCode() : 0);
        result = 31 * result + (maleIntermediateMpTermName != null ? maleIntermediateMpTermName.hashCode() : 0);
        result = 31 * result + (femaleMpTermId != null ? femaleMpTermId.hashCode() : 0);
        result = 31 * result + (femaleMpTermName != null ? femaleMpTermName.hashCode() : 0);
        result = 31 * result + (femaleTopLevelMpTermId != null ? femaleTopLevelMpTermId.hashCode() : 0);
        result = 31 * result + (femaleTopLevelMpTermName != null ? femaleTopLevelMpTermName.hashCode() : 0);
        result = 31 * result + (femaleIntermediateMpTermId != null ? femaleIntermediateMpTermId.hashCode() : 0);
        result = 31 * result + (femaleIntermediateMpTermName != null ? femaleIntermediateMpTermName.hashCode() : 0);
        result = 31 * result + (resourceName != null ? resourceName.hashCode() : 0);
        result = 31 * result + (resourceFullname != null ? resourceFullname.hashCode() : 0);
        result = 31 * result + (resourceId != null ? resourceId.hashCode() : 0);
        result = 31 * result + (projectName != null ? projectName.hashCode() : 0);
        result = 31 * result + (phenotypingCenter != null ? phenotypingCenter.hashCode() : 0);
        result = 31 * result + (pipelineStableId != null ? pipelineStableId.hashCode() : 0);
        result = 31 * result + (pipelineStableKey != null ? pipelineStableKey.hashCode() : 0);
        result = 31 * result + (pipelineName != null ? pipelineName.hashCode() : 0);
        result = 31 * result + (pipelineId != null ? pipelineId.hashCode() : 0);
        result = 31 * result + (procedureStableId != null ? procedureStableId.hashCode() : 0);
        result = 31 * result + (procedureStableKey != null ? procedureStableKey.hashCode() : 0);
        result = 31 * result + (procedureName != null ? procedureName.hashCode() : 0);
        result = 31 * result + (procedureId != null ? procedureId.hashCode() : 0);
        result = 31 * result + (parameterStableId != null ? parameterStableId.hashCode() : 0);
        result = 31 * result + (parameterStableKey != null ? parameterStableKey.hashCode() : 0);
        result = 31 * result + (parameterName != null ? parameterName.hashCode() : 0);
        result = 31 * result + (parameterId != null ? parameterId.hashCode() : 0);
        result = 31 * result + (colonyId != null ? colonyId.hashCode() : 0);
        result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
        result = 31 * result + (markerAccessionId != null ? markerAccessionId.hashCode() : 0);
        result = 31 * result + (alleleSymbol != null ? alleleSymbol.hashCode() : 0);
        result = 31 * result + (alleleName != null ? alleleName.hashCode() : 0);
        result = 31 * result + (alleleAccessionId != null ? alleleAccessionId.hashCode() : 0);
        result = 31 * result + (strainName != null ? strainName.hashCode() : 0);
        result = 31 * result + (strainAccessionId != null ? strainAccessionId.hashCode() : 0);
        result = 31 * result + (geneticBackground != null ? geneticBackground.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (zygosity != null ? zygosity.hashCode() : 0);
        result = 31 * result + (controlSelectionMethod != null ? controlSelectionMethod.hashCode() : 0);
        result = 31 * result + (dependentVariable != null ? dependentVariable.hashCode() : 0);
        result = 31 * result + (metadataGroup != null ? metadataGroup.hashCode() : 0);
        result = 31 * result + (controlBiologicalModelId != null ? controlBiologicalModelId.hashCode() : 0);
        result = 31 * result + (mutantBiologicalModelId != null ? mutantBiologicalModelId.hashCode() : 0);
        result = 31 * result + (maleControlCount != null ? maleControlCount.hashCode() : 0);
        result = 31 * result + (maleMutantCount != null ? maleMutantCount.hashCode() : 0);
        result = 31 * result + (femaleControlCount != null ? femaleControlCount.hashCode() : 0);
        result = 31 * result + (femaleMutantCount != null ? femaleMutantCount.hashCode() : 0);
        result = 31 * result + (femaleMutantMean != null ? femaleMutantMean.hashCode() : 0);
        result = 31 * result + (femaleControlMean != null ? femaleControlMean.hashCode() : 0);
        result = 31 * result + (maleMutantMean != null ? maleMutantMean.hashCode() : 0);
        result = 31 * result + (maleControlMean != null ? maleControlMean.hashCode() : 0);
        result = 31 * result + (statisticalMethod != null ? statisticalMethod.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (additionalInformation != null ? additionalInformation.hashCode() : 0);
        result = 31 * result + (rawOutput != null ? rawOutput.hashCode() : 0);
        result = 31 * result + (pValue != null ? pValue.hashCode() : 0);
        result = 31 * result + (effectSize != null ? effectSize.hashCode() : 0);
        result = 31 * result + (categories != null ? categories.hashCode() : 0);
        result = 31 * result + (categoricalPValue != null ? categoricalPValue.hashCode() : 0);
        result = 31 * result + (categoricalEffectSize != null ? categoricalEffectSize.hashCode() : 0);
        result = 31 * result + (batchSignificant != null ? batchSignificant.hashCode() : 0);
        result = 31 * result + (varianceSignificant != null ? varianceSignificant.hashCode() : 0);
        result = 31 * result + (nullTestPValue != null ? nullTestPValue.hashCode() : 0);
        result = 31 * result + (genotypeEffectPValue != null ? genotypeEffectPValue.hashCode() : 0);
        result = 31 * result + (genotypeEffectStderrEstimate != null ? genotypeEffectStderrEstimate.hashCode() : 0);
        result = 31 * result + (genotypeEffectParameterEstimate != null ? genotypeEffectParameterEstimate.hashCode() : 0);
        result = 31 * result + (femalePercentageChange != null ? femalePercentageChange.hashCode() : 0);
        result = 31 * result + (malePercentageChange != null ? malePercentageChange.hashCode() : 0);
        result = 31 * result + (sexEffectPValue != null ? sexEffectPValue.hashCode() : 0);
        result = 31 * result + (sexEffectStderrEstimate != null ? sexEffectStderrEstimate.hashCode() : 0);
        result = 31 * result + (sexEffectParameterEstimate != null ? sexEffectParameterEstimate.hashCode() : 0);
        result = 31 * result + (weightEffectPValue != null ? weightEffectPValue.hashCode() : 0);
        result = 31 * result + (weightEffectStderrEstimate != null ? weightEffectStderrEstimate.hashCode() : 0);
        result = 31 * result + (weightEffectParameterEstimate != null ? weightEffectParameterEstimate.hashCode() : 0);
        result = 31 * result + (group1Genotype != null ? group1Genotype.hashCode() : 0);
        result = 31 * result + (group1ResidualsNormalityTest != null ? group1ResidualsNormalityTest.hashCode() : 0);
        result = 31 * result + (group2Genotype != null ? group2Genotype.hashCode() : 0);
        result = 31 * result + (group2ResidualsNormalityTest != null ? group2ResidualsNormalityTest.hashCode() : 0);
        result = 31 * result + (blupsTest != null ? blupsTest.hashCode() : 0);
        result = 31 * result + (rotatedResidualsTest != null ? rotatedResidualsTest.hashCode() : 0);
        result = 31 * result + (interceptEstimate != null ? interceptEstimate.hashCode() : 0);
        result = 31 * result + (interceptEstimateStderrEstimate != null ? interceptEstimateStderrEstimate.hashCode() : 0);
        result = 31 * result + (interactionSignificant != null ? interactionSignificant.hashCode() : 0);
        result = 31 * result + (interactionEffectPValue != null ? interactionEffectPValue.hashCode() : 0);
        result = 31 * result + (femaleKoEffectPValue != null ? femaleKoEffectPValue.hashCode() : 0);
        result = 31 * result + (femaleKoEffectStderrEstimate != null ? femaleKoEffectStderrEstimate.hashCode() : 0);
        result = 31 * result + (femaleKoParameterEstimate != null ? femaleKoParameterEstimate.hashCode() : 0);
        result = 31 * result + (maleKoEffectPValue != null ? maleKoEffectPValue.hashCode() : 0);
        result = 31 * result + (maleKoEffectStderrEstimate != null ? maleKoEffectStderrEstimate.hashCode() : 0);
        result = 31 * result + (maleKoParameterEstimate != null ? maleKoParameterEstimate.hashCode() : 0);
        result = 31 * result + (classificationTag != null ? classificationTag.hashCode() : 0);
        result = 31 * result + (externalDbId != null ? externalDbId.hashCode() : 0);
        result = 31 * result + (organisationId != null ? organisationId.hashCode() : 0);
        result = 31 * result + (phenotypingCenterId != null ? phenotypingCenterId.hashCode() : 0);
        result = 31 * result + (projectId != null ? projectId.hashCode() : 0);
        return result;
    }
}
