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
}
