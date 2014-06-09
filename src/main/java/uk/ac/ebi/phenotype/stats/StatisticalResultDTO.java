package uk.ac.ebi.phenotype.stats;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;


public class StatisticalResultDTO {

    @Field("doc_id")
    private String docId;

    @Field("db_id")
    private Integer dbId;

    @Field("data_type")
    private String dataType;

    @Field("mp_term_id")
    private String mpTermId;

    @Field("mp_term_name")
    private String mpTermName;

    @Field("top_level_mp_term_id")
    private String topLevelMpTermId;

    @Field("top_level_mp_term_name")
    private String topLevelMpTermName;

    @Field("intermediate_mp_term_id")
    private String intermediateMpTermId;

    @Field("intermediate_mp_term_name")
    private String intermediateMpTermName;

    @Field("resource_name")
    private String resourceName;

    @Field("resource_fullname")
    private String resourceFullname;

    @Field("resource_id")
    private Integer resourceId;

    @Field("project_name")
    private String projectName;

    @Field("phenotyping_center")
    private String phenotypingCenter;

    @Field("pipeline_stable_id")
    private String pipelineStableId;

    @Field("pipeline_stable_key")
    private String pipelineStableKey;

    @Field("pipeline_name")
    private String pipelineName;

    @Field("pipeline_id")
    private Integer pipelineId;

    @Field("procedure_stable_id")
    private String procedureStableId;

    @Field("procedure_stable_key")
    private String procedureStableKey;

    @Field("procedure_name")
    private String procedureName;

    @Field("procedure_id")
    private Integer procedureId;

    @Field("parameter_stable_id")
    private String parameterStableId;

    @Field("parameter_stable_key")
    private String parameterStableKey;

    @Field("parameter_name")
    private String parameterName;

    @Field("parameter_id")
    private Integer parameterId;

    @Field("colony_id")
    private String colonyId;

    @Field("marker_symbol")
    private String markerSymbol;

    @Field("marker_accession_id")
    private String markerAccessionId;

    @Field("allele_symbol")
    private String alleleSymbol;

    @Field("allele_name")
    private String alleleName;

    @Field("allele_accession_id")
    private String alleleAccessionId;

    @Field("strain_name")
    private String strainName;

    @Field("strain_accession_id")
    private String strainAccessionId;

    @Field("sex")
    private String sex;

    @Field("zygosity")
    private String zygosity;

    @Field("control_selection_method")
    private String controlSelectionMethod;

    @Field("dependent_variable")
    private String dependentVariable;

    @Field("metadata_group")
    private String metadataGroup;

    @Field("control_biological_model_id")
    private Integer controlBiologicalModelId;

    @Field("mutant_biological_model_id")
    private Integer mutantBiologicalModelId;

    @Field("male_control_count")
    private Integer maleControlCount;

    @Field("male_mutant_count")
    private Integer maleMutantCount;

    @Field("female_control_count")
    private Integer femaleControlCount;

    @Field("female_mutant_count")
    private Integer femaleMutantCount;

    @Field("statistical_method")
    private String statisticalMethod;

    @Field("status")
    private String status;

    @Field("additional_information")
    private String additionalInformation;

    @Field("raw_output")
    private String rawOutput;

    @Field("p_value")
    private Float pValue;

    @Field("effect_size")
    private Float effectSize;

    @Field("categories")
    private List<String> categories;

    @Field("categorical_p_value")
    private Float categoricalPValue;

    @Field("categorical_effect_size")
    private Float categoricalEffectSize;

    @Field("batch_significant")
    private Boolean batchSignificant;

    @Field("variance_significant")
    private Boolean varianceSignificant;

    @Field("null_test_p_value")
    private Float nullTestPValue;

    @Field("genotype_effect_p_value")
    private Float genotypeEffectPValue;

    @Field("genotype_effect_stderr_estimate")
    private Float genotypeEffectStderrEstimate;

    @Field("genotype_effect_parameter_estimate")
    private Float genotypeEffectParameterEstimate;

    @Field("sex_effect_p_value")
    private Float sexEffectPValue;

    @Field("sex_effect_stderr_estimate")
    private Float sexEffectStderrEstimate;

    @Field("sex_effect_parameter_estimate")
    private Float sexEffectParameterEstimate;

    @Field("weight_effect_p_value")
    private Float weightEffectPValue;

    @Field("weight_effect_stderr_estimate")
    private Float weightEffectStderrEstimate;

    @Field("weight_effect_parameter_estimate")
    private Float weightEffectParameterEstimate;

    @Field("group_1_genotype")
    private String group1Genotype;

    @Field("group_1_residuals_normality_test")
    private Float group1ResidualsNormalityTest;

    @Field("group_2_genotype")
    private String group2Genotype;

    @Field("group_2_residuals_normality_test")
    private Float group2ResidualsNormalityTest;

    @Field("blups_test")
    private Float blupsTest;

    @Field("rotated_residuals_test")
    private Float rotatedResidualsTest;

    @Field("intercept_estimate")
    private Float interceptEstimate;

    @Field("intercept_estimate_stderr_estimate")
    private Float interceptEstimateStderrEstimate;

    @Field("interaction_significant")
    private Boolean interactionSignificant;

    @Field("interaction_effect_p_value")
    private Float interactionEffectPValue;

    @Field("female_ko_effect_p_value")
    private Float femaleKoEffectPValue;

    @Field("female_ko_effect_stderr_estimate")
    private Float femaleKoEffectStderrEstimate;

    @Field("female_ko_parameter_estimate")
    private Float femaleKoParameterEstimate;

    @Field("male_ko_effect_p_value")
    private Float maleKoEffectPValue;

    @Field("male_ko_effect_stderr_estimate")
    private Float maleKoEffectStderrEstimate;

    @Field("male_ko_parameter_estimate")
    private Float maleKoParameterEstimate;

    @Field("classification_tag")
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
