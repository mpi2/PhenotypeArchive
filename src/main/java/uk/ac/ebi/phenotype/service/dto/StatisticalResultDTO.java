package uk.ac.ebi.phenotype.service.dto;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;


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
    private String topLevelMpTermId;

    @Field(TOP_LEVEL_MP_TERM_NAME)
    private String topLevelMpTermName;

    @Field(INTERMEDIATE_MP_TERM_ID)
    private String intermediateMpTermId;

    @Field(INTERMEDIATE_MP_TERM_NAME)
    private String intermediateMpTermName;

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
    private Float pValue;

    @Field(EFFECT_SIZE)
    private Float effectSize;

    @Field(CATEGORIES)
    private List<String> categories;

    @Field(CATEGORICAL_P_VALUE)
    private Float categoricalPValue;

    @Field(CATEGORICAL_EFFECT_SIZE)
    private Float categoricalEffectSize;

    @Field(BATCH_SIGNIFICANT)
    private Boolean batchSignificant;

    @Field(VARIANCE_SIGNIFICANT)
    private Boolean varianceSignificant;

    @Field(NULL_TEST_P_VALUE)
    private Float nullTestPValue;

    @Field(GENOTYPE_EFFECT_P_VALUE)
    private Float genotypeEffectPValue;

    @Field(GENOTYPE_EFFECT_STDERR_ESTIMATE)
    private Float genotypeEffectStderrEstimate;

    @Field(GENOTYPE_EFFECT_PARAMETER_ESTIMATE)
    private Float genotypeEffectParameterEstimate;

    @Field(SEX_EFFECT_P_VALUE)
    private Float sexEffectPValue;

    @Field(SEX_EFFECT_STDERR_ESTIMATE)
    private Float sexEffectStderrEstimate;

    @Field(SEX_EFFECT_PARAMETER_ESTIMATE)
    private Float sexEffectParameterEstimate;

    @Field(WEIGHT_EFFECT_P_VALUE)
    private Float weightEffectPValue;

    @Field(WEIGHT_EFFECT_STDERR_ESTIMATE)
    private Float weightEffectStderrEstimate;

    @Field(WEIGHT_EFFECT_PARAMETER_ESTIMATE)
    private Float weightEffectParameterEstimate;

    @Field(GROUP_1_GENOTYPE)
    private String group1Genotype;

    @Field(GROUP_1_RESIDUALS_NORMALITY_TEST)
    private Float group1ResidualsNormalityTest;

    @Field(GROUP_2_GENOTYPE)
    private String group2Genotype;

    @Field(GROUP_2_RESIDUALS_NORMALITY_TEST)
    private Float group2ResidualsNormalityTest;

    @Field(BLUPS_TEST)
    private Float blupsTest;

    @Field(ROTATED_RESIDUALS_TEST)
    private Float rotatedResidualsTest;

    @Field(INTERCEPT_ESTIMATE)
    private Float interceptEstimate;

    @Field(INTERCEPT_ESTIMATE_STDERR_ESTIMATE)
    private Float interceptEstimateStderrEstimate;

    @Field(INTERACTION_SIGNIFICANT)
    private Boolean interactionSignificant;

    @Field(INTERACTION_EFFECT_P_VALUE)
    private Float interactionEffectPValue;

    @Field(FEMALE_KO_EFFECT_P_VALUE)
    private Float femaleKoEffectPValue;

    @Field(FEMALE_KO_EFFECT_STDERR_ESTIMATE)
    private Float femaleKoEffectStderrEstimate;

    @Field(FEMALE_KO_PARAMETER_ESTIMATE)
    private Float femaleKoParameterEstimate;

    @Field(MALE_KO_EFFECT_P_VALUE)
    private Float maleKoEffectPValue;

    @Field(MALE_KO_EFFECT_STDERR_ESTIMATE)
    private Float maleKoEffectStderrEstimate;

    @Field(MALE_KO_PARAMETER_ESTIMATE)
    private Float maleKoParameterEstimate;

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

    
    public String getTopLevelMpTermId() {
        return topLevelMpTermId;
    }

    
    public void setTopLevelMpTermId(String topLevelMpTermId) {
        this.topLevelMpTermId = topLevelMpTermId;
    }

    
    public String getTopLevelMpTermName() {
        return topLevelMpTermName;
    }

    
    public void setTopLevelMpTermName(String topLevelMpTermName) {
        this.topLevelMpTermName = topLevelMpTermName;
    }

    
    public String getIntermediateMpTermId() {
        return intermediateMpTermId;
    }

    
    public void setIntermediateMpTermId(String intermediateMpTermId) {
        this.intermediateMpTermId = intermediateMpTermId;
    }

    
    public String getIntermediateMpTermName() {
        return intermediateMpTermName;
    }

    
    public void setIntermediateMpTermName(String intermediateMpTermName) {
        this.intermediateMpTermName = intermediateMpTermName;
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

    
    public Float getpValue() {
        return pValue;
    }

    
    public void setpValue(Float pValue) {
        this.pValue = pValue;
    }

    
    public Float getEffectSize() {
        return effectSize;
    }

    
    public void setEffectSize(Float effectSize) {
        this.effectSize = effectSize;
    }

    
    public List<String> getCategories() {
        return categories;
    }

    
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    
    public Float getCategoricalPValue() {
        return categoricalPValue;
    }

    
    public void setCategoricalPValue(Float categoricalPValue) {
        this.categoricalPValue = categoricalPValue;
    }

    
    public Float getCategoricalEffectSize() {
        return categoricalEffectSize;
    }

    
    public void setCategoricalEffectSize(Float categoricalEffectSize) {
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

    
    public Float getNullTestPValue() {
        return nullTestPValue;
    }

    
    public void setNullTestPValue(Float nullTestPValue) {
        this.nullTestPValue = nullTestPValue;
    }

    
    public Float getGenotypeEffectPValue() {
        return genotypeEffectPValue;
    }

    
    public void setGenotypeEffectPValue(Float genotypeEffectPValue) {
        this.genotypeEffectPValue = genotypeEffectPValue;
    }

    
    public Float getGenotypeEffectStderrEstimate() {
        return genotypeEffectStderrEstimate;
    }

    
    public void setGenotypeEffectStderrEstimate(Float genotypeEffectStderrEstimate) {
        this.genotypeEffectStderrEstimate = genotypeEffectStderrEstimate;
    }

    
    public Float getGenotypeEffectParameterEstimate() {
        return genotypeEffectParameterEstimate;
    }

    
    public void setGenotypeEffectParameterEstimate(Float genotypeEffectParameterEstimate) {
        this.genotypeEffectParameterEstimate = genotypeEffectParameterEstimate;
    }

    
    public Float getSexEffectPValue() {
        return sexEffectPValue;
    }

    
    public void setSexEffectPValue(Float sexEffectPValue) {
        this.sexEffectPValue = sexEffectPValue;
    }

    
    public Float getSexEffectStderrEstimate() {
        return sexEffectStderrEstimate;
    }

    
    public void setSexEffectStderrEstimate(Float sexEffectStderrEstimate) {
        this.sexEffectStderrEstimate = sexEffectStderrEstimate;
    }

    
    public Float getSexEffectParameterEstimate() {
        return sexEffectParameterEstimate;
    }

    
    public void setSexEffectParameterEstimate(Float sexEffectParameterEstimate) {
        this.sexEffectParameterEstimate = sexEffectParameterEstimate;
    }

    
    public Float getWeightEffectPValue() {
        return weightEffectPValue;
    }

    
    public void setWeightEffectPValue(Float weightEffectPValue) {
        this.weightEffectPValue = weightEffectPValue;
    }

    
    public Float getWeightEffectStderrEstimate() {
        return weightEffectStderrEstimate;
    }

    
    public void setWeightEffectStderrEstimate(Float weightEffectStderrEstimate) {
        this.weightEffectStderrEstimate = weightEffectStderrEstimate;
    }

    
    public Float getWeightEffectParameterEstimate() {
        return weightEffectParameterEstimate;
    }

    
    public void setWeightEffectParameterEstimate(Float weightEffectParameterEstimate) {
        this.weightEffectParameterEstimate = weightEffectParameterEstimate;
    }

    
    public String getGroup1Genotype() {
        return group1Genotype;
    }

    
    public void setGroup1Genotype(String group1Genotype) {
        this.group1Genotype = group1Genotype;
    }

    
    public Float getGroup1ResidualsNormalityTest() {
        return group1ResidualsNormalityTest;
    }

    
    public void setGroup1ResidualsNormalityTest(Float group1ResidualsNormalityTest) {
        this.group1ResidualsNormalityTest = group1ResidualsNormalityTest;
    }

    
    public String getGroup2Genotype() {
        return group2Genotype;
    }

    
    public void setGroup2Genotype(String group2Genotype) {
        this.group2Genotype = group2Genotype;
    }

    
    public Float getGroup2ResidualsNormalityTest() {
        return group2ResidualsNormalityTest;
    }

    
    public void setGroup2ResidualsNormalityTest(Float group2ResidualsNormalityTest) {
        this.group2ResidualsNormalityTest = group2ResidualsNormalityTest;
    }

    
    public Float getBlupsTest() {
        return blupsTest;
    }

    
    public void setBlupsTest(Float blupsTest) {
        this.blupsTest = blupsTest;
    }

    
    public Float getRotatedResidualsTest() {
        return rotatedResidualsTest;
    }

    
    public void setRotatedResidualsTest(Float rotatedResidualsTest) {
        this.rotatedResidualsTest = rotatedResidualsTest;
    }

    
    public Float getInterceptEstimate() {
        return interceptEstimate;
    }

    
    public void setInterceptEstimate(Float interceptEstimate) {
        this.interceptEstimate = interceptEstimate;
    }

    
    public Float getInterceptEstimateStderrEstimate() {
        return interceptEstimateStderrEstimate;
    }

    
    public void setInterceptEstimateStderrEstimate(Float interceptEstimateStderrEstimate) {
        this.interceptEstimateStderrEstimate = interceptEstimateStderrEstimate;
    }

    
    public Boolean getInteractionSignificant() {
        return interactionSignificant;
    }

    
    public void setInteractionSignificant(Boolean interactionSignificant) {
        this.interactionSignificant = interactionSignificant;
    }

    
    public Float getInteractionEffectPValue() {
        return interactionEffectPValue;
    }

    
    public void setInteractionEffectPValue(Float interactionEffectPValue) {
        this.interactionEffectPValue = interactionEffectPValue;
    }

    
    public Float getFemaleKoEffectPValue() {
        return femaleKoEffectPValue;
    }

    
    public void setFemaleKoEffectPValue(Float femaleKoEffectPValue) {
        this.femaleKoEffectPValue = femaleKoEffectPValue;
    }

    
    public Float getFemaleKoEffectStderrEstimate() {
        return femaleKoEffectStderrEstimate;
    }

    
    public void setFemaleKoEffectStderrEstimate(Float femaleKoEffectStderrEstimate) {
        this.femaleKoEffectStderrEstimate = femaleKoEffectStderrEstimate;
    }

    
    public Float getFemaleKoParameterEstimate() {
        return femaleKoParameterEstimate;
    }

    
    public void setFemaleKoParameterEstimate(Float femaleKoParameterEstimate) {
        this.femaleKoParameterEstimate = femaleKoParameterEstimate;
    }

    
    public Float getMaleKoEffectPValue() {
        return maleKoEffectPValue;
    }

    
    public void setMaleKoEffectPValue(Float maleKoEffectPValue) {
        this.maleKoEffectPValue = maleKoEffectPValue;
    }

    
    public Float getMaleKoEffectStderrEstimate() {
        return maleKoEffectStderrEstimate;
    }

    
    public void setMaleKoEffectStderrEstimate(Float maleKoEffectStderrEstimate) {
        this.maleKoEffectStderrEstimate = maleKoEffectStderrEstimate;
    }

    
    public Float getMaleKoParameterEstimate() {
        return maleKoParameterEstimate;
    }

    
    public void setMaleKoParameterEstimate(Float maleKoParameterEstimate) {
        this.maleKoParameterEstimate = maleKoParameterEstimate;
    }

    
    public String getClassificationTag() {
        return classificationTag;
    }

    
    public void setClassificationTag(String classificationTag) {
        this.classificationTag = classificationTag;
    }



}
