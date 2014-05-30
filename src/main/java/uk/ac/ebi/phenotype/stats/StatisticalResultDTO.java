package uk.ac.ebi.phenotype.stats;

import org.apache.solr.client.solrj.beans.Field;


public class StatisticalResultDTO {

    @Field("doc_id")
    private String docId;

    @Field("db_id")
    private String dbId;

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
    private String resourceId;

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
    private String pipelineId;

    @Field("procedure_stable_id")
    private String procedureStableId;

    @Field("procedure_stable_key")
    private String procedureStableKey;

    @Field("procedure_name")
    private String procedureName;

    @Field("procedure_id")
    private String procedureId;

    @Field("parameter_stable_id")
    private String parameterStableId;

    @Field("parameter_stable_key")
    private String parameterStableKey;

    @Field("parameter_name")
    private String parameterName;

    @Field("parameter_id")
    private String parameterId;

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
    private String controlBiologicalModelId;

    @Field("mutant_biological_model_id")
    private String mutantBiologicalModelId;

    @Field("male_control_count")
    private String maleControlCount;

    @Field("male_mutant_count")
    private String maleMutantCount;

    @Field("female_control_count")
    private String femaleControlCount;

    @Field("female_mutant_count")
    private String femaleMutantCount;

    @Field("statistical_method")
    private String statisticalMethod;

    @Field("status")
    private String status;

    @Field("additional_information")
    private String additionalInformation;

    @Field("raw_output")
    private String rawOutput;

    @Field("p_value")
    private String pValue;

    @Field("effect_size")
    private String effectSize;

    @Field("categories")
    private String categories;

    @Field("categorical_p_value")
    private String categoricalPValue;

    @Field("categorical_effect_size")
    private String categoricalEffectSize;

    @Field("batch_significant")
    private String batchSignificant;

    @Field("variance_significant")
    private String varianceSignificant;

    @Field("null_test_p_value")
    private String nullTestPValue;

    @Field("genotype_effect_p_value")
    private String genotypeEffectPValue;

    @Field("genotype_effect_stderr_estimate")
    private String genotypeEffectStderrEstimate;

    @Field("genotype_effect_parameter_estimate")
    private String genotypeEffectParameterEstimate;

    @Field("sex_effect_p_value")
    private String sexEffectPValue;

    @Field("sex_effect_stderr_estimate")
    private String sexEffectStderrEstimate;

    @Field("sex_effect_parameter_estimate")
    private String sexEffectParameterEstimate;

    @Field("weight_effect_p_value")
    private String weightEffectPValue;

    @Field("weight_effect_stderr_estimate")
    private String weightEffectStderrEstimate;

    @Field("weight_effect_parameter_estimate")
    private String weightEffectParameterEstimate;

    @Field("group_1_genotype")
    private String group1Genotype;

    @Field("group_1_residuals_normality_test")
    private String group1ResidualsNormalityTest;

    @Field("group_2_genotype")
    private String group2Genotype;

    @Field("group_2_residuals_normality_test")
    private String group2ResidualsNormalityTest;

    @Field("blups_test")
    private String blupsTest;

    @Field("rotated_residuals_test")
    private String rotatedResidualsTest;

    @Field("intercept_estimate")
    private String interceptEstimate;

    @Field("intercept_estimate_stderr_estimate")
    private String interceptEstimateStderrEstimate;

    @Field("interaction_significant")
    private String interactionSignificant;

    @Field("interaction_effect_p_value")
    private String interactionEffectPValue;

    @Field("female_ko_effect_p_value")
    private String femaleKoEffectPValue;

    @Field("female_ko_effect_stderr_estimate")
    private String femaleKoEffectStderrEstimate;

    @Field("female_ko_parameter_estimate")
    private String femaleKoParameterEstimate;

    @Field("male_ko_effect_p_value")
    private String maleKoEffectPValue;

    @Field("male_ko_effect_stderr_estimate")
    private String maleKoEffectStderrEstimate;

    @Field("male_ko_parameter_estimate")
    private String maleKoParameterEstimate;

    @Field("classification_tag")
    private String classificationTag;

    
    public String getDocId() {
        return docId;
    }

    
    public void setDocId(String docId) {
        this.docId = docId;
    }

    
    public String getDbId() {
        return dbId;
    }

    
    public void setDbId(String dbId) {
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

    
    public String getResourceId() {
        return resourceId;
    }

    
    public void setResourceId(String resourceId) {
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

    
    public String getPipelineId() {
        return pipelineId;
    }

    
    public void setPipelineId(String pipelineId) {
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

    
    public String getProcedureId() {
        return procedureId;
    }

    
    public void setProcedureId(String procedureId) {
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

    
    public String getParameterId() {
        return parameterId;
    }

    
    public void setParameterId(String parameterId) {
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

    
    public String getControlBiologicalModelId() {
        return controlBiologicalModelId;
    }

    
    public void setControlBiologicalModelId(String controlBiologicalModelId) {
        this.controlBiologicalModelId = controlBiologicalModelId;
    }

    
    public String getMutantBiologicalModelId() {
        return mutantBiologicalModelId;
    }

    
    public void setMutantBiologicalModelId(String mutantBiologicalModelId) {
        this.mutantBiologicalModelId = mutantBiologicalModelId;
    }

    
    public String getMaleControlCount() {
        return maleControlCount;
    }

    
    public void setMaleControlCount(String maleControlCount) {
        this.maleControlCount = maleControlCount;
    }

    
    public String getMaleMutantCount() {
        return maleMutantCount;
    }

    
    public void setMaleMutantCount(String maleMutantCount) {
        this.maleMutantCount = maleMutantCount;
    }

    
    public String getFemaleControlCount() {
        return femaleControlCount;
    }

    
    public void setFemaleControlCount(String femaleControlCount) {
        this.femaleControlCount = femaleControlCount;
    }

    
    public String getFemaleMutantCount() {
        return femaleMutantCount;
    }

    
    public void setFemaleMutantCount(String femaleMutantCount) {
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

    
    public String getpValue() {
        return pValue;
    }

    
    public void setpValue(String pValue) {
        this.pValue = pValue;
    }

    
    public String getEffectSize() {
        return effectSize;
    }

    
    public void setEffectSize(String effectSize) {
        this.effectSize = effectSize;
    }

    
    public String getCategories() {
        return categories;
    }

    
    public void setCategories(String categories) {
        this.categories = categories;
    }

    
    public String getCategoricalPValue() {
        return categoricalPValue;
    }

    
    public void setCategoricalPValue(String categoricalPValue) {
        this.categoricalPValue = categoricalPValue;
    }

    
    public String getCategoricalEffectSize() {
        return categoricalEffectSize;
    }

    
    public void setCategoricalEffectSize(String categoricalEffectSize) {
        this.categoricalEffectSize = categoricalEffectSize;
    }

    
    public String getBatchSignificant() {
        return batchSignificant;
    }

    
    public void setBatchSignificant(String batchSignificant) {
        this.batchSignificant = batchSignificant;
    }

    
    public String getVarianceSignificant() {
        return varianceSignificant;
    }

    
    public void setVarianceSignificant(String varianceSignificant) {
        this.varianceSignificant = varianceSignificant;
    }

    
    public String getNullTestPValue() {
        return nullTestPValue;
    }

    
    public void setNullTestPValue(String nullTestPValue) {
        this.nullTestPValue = nullTestPValue;
    }

    
    public String getGenotypeEffectPValue() {
        return genotypeEffectPValue;
    }

    
    public void setGenotypeEffectPValue(String genotypeEffectPValue) {
        this.genotypeEffectPValue = genotypeEffectPValue;
    }

    
    public String getGenotypeEffectStderrEstimate() {
        return genotypeEffectStderrEstimate;
    }

    
    public void setGenotypeEffectStderrEstimate(String genotypeEffectStderrEstimate) {
        this.genotypeEffectStderrEstimate = genotypeEffectStderrEstimate;
    }

    
    public String getGenotypeEffectParameterEstimate() {
        return genotypeEffectParameterEstimate;
    }

    
    public void setGenotypeEffectParameterEstimate(String genotypeEffectParameterEstimate) {
        this.genotypeEffectParameterEstimate = genotypeEffectParameterEstimate;
    }

    
    public String getSexEffectPValue() {
        return sexEffectPValue;
    }

    
    public void setSexEffectPValue(String sexEffectPValue) {
        this.sexEffectPValue = sexEffectPValue;
    }

    
    public String getSexEffectStderrEstimate() {
        return sexEffectStderrEstimate;
    }

    
    public void setSexEffectStderrEstimate(String sexEffectStderrEstimate) {
        this.sexEffectStderrEstimate = sexEffectStderrEstimate;
    }

    
    public String getSexEffectParameterEstimate() {
        return sexEffectParameterEstimate;
    }

    
    public void setSexEffectParameterEstimate(String sexEffectParameterEstimate) {
        this.sexEffectParameterEstimate = sexEffectParameterEstimate;
    }

    
    public String getWeightEffectPValue() {
        return weightEffectPValue;
    }

    
    public void setWeightEffectPValue(String weightEffectPValue) {
        this.weightEffectPValue = weightEffectPValue;
    }

    
    public String getWeightEffectStderrEstimate() {
        return weightEffectStderrEstimate;
    }

    
    public void setWeightEffectStderrEstimate(String weightEffectStderrEstimate) {
        this.weightEffectStderrEstimate = weightEffectStderrEstimate;
    }

    
    public String getWeightEffectParameterEstimate() {
        return weightEffectParameterEstimate;
    }

    
    public void setWeightEffectParameterEstimate(String weightEffectParameterEstimate) {
        this.weightEffectParameterEstimate = weightEffectParameterEstimate;
    }

    
    public String getGroup1Genotype() {
        return group1Genotype;
    }

    
    public void setGroup1Genotype(String group1Genotype) {
        this.group1Genotype = group1Genotype;
    }

    
    public String getGroup1ResidualsNormalityTest() {
        return group1ResidualsNormalityTest;
    }

    
    public void setGroup1ResidualsNormalityTest(String group1ResidualsNormalityTest) {
        this.group1ResidualsNormalityTest = group1ResidualsNormalityTest;
    }

    
    public String getGroup2Genotype() {
        return group2Genotype;
    }

    
    public void setGroup2Genotype(String group2Genotype) {
        this.group2Genotype = group2Genotype;
    }

    
    public String getGroup2ResidualsNormalityTest() {
        return group2ResidualsNormalityTest;
    }

    
    public void setGroup2ResidualsNormalityTest(String group2ResidualsNormalityTest) {
        this.group2ResidualsNormalityTest = group2ResidualsNormalityTest;
    }

    
    public String getBlupsTest() {
        return blupsTest;
    }

    
    public void setBlupsTest(String blupsTest) {
        this.blupsTest = blupsTest;
    }

    
    public String getRotatedResidualsTest() {
        return rotatedResidualsTest;
    }

    
    public void setRotatedResidualsTest(String rotatedResidualsTest) {
        this.rotatedResidualsTest = rotatedResidualsTest;
    }

    
    public String getInterceptEstimate() {
        return interceptEstimate;
    }

    
    public void setInterceptEstimate(String interceptEstimate) {
        this.interceptEstimate = interceptEstimate;
    }

    
    public String getInterceptEstimateStderrEstimate() {
        return interceptEstimateStderrEstimate;
    }

    
    public void setInterceptEstimateStderrEstimate(String interceptEstimateStderrEstimate) {
        this.interceptEstimateStderrEstimate = interceptEstimateStderrEstimate;
    }

    
    public String getInteractionSignificant() {
        return interactionSignificant;
    }

    
    public void setInteractionSignificant(String interactionSignificant) {
        this.interactionSignificant = interactionSignificant;
    }

    
    public String getInteractionEffectPValue() {
        return interactionEffectPValue;
    }

    
    public void setInteractionEffectPValue(String interactionEffectPValue) {
        this.interactionEffectPValue = interactionEffectPValue;
    }

    
    public String getFemaleKoEffectPValue() {
        return femaleKoEffectPValue;
    }

    
    public void setFemaleKoEffectPValue(String femaleKoEffectPValue) {
        this.femaleKoEffectPValue = femaleKoEffectPValue;
    }

    
    public String getFemaleKoEffectStderrEstimate() {
        return femaleKoEffectStderrEstimate;
    }

    
    public void setFemaleKoEffectStderrEstimate(String femaleKoEffectStderrEstimate) {
        this.femaleKoEffectStderrEstimate = femaleKoEffectStderrEstimate;
    }

    
    public String getFemaleKoParameterEstimate() {
        return femaleKoParameterEstimate;
    }

    
    public void setFemaleKoParameterEstimate(String femaleKoParameterEstimate) {
        this.femaleKoParameterEstimate = femaleKoParameterEstimate;
    }

    
    public String getMaleKoEffectPValue() {
        return maleKoEffectPValue;
    }

    
    public void setMaleKoEffectPValue(String maleKoEffectPValue) {
        this.maleKoEffectPValue = maleKoEffectPValue;
    }

    
    public String getMaleKoEffectStderrEstimate() {
        return maleKoEffectStderrEstimate;
    }

    
    public void setMaleKoEffectStderrEstimate(String maleKoEffectStderrEstimate) {
        this.maleKoEffectStderrEstimate = maleKoEffectStderrEstimate;
    }

    
    public String getMaleKoParameterEstimate() {
        return maleKoParameterEstimate;
    }

    
    public void setMaleKoParameterEstimate(String maleKoParameterEstimate) {
        this.maleKoParameterEstimate = maleKoParameterEstimate;
    }

    
    public String getClassificationTag() {
        return classificationTag;
    }

    
    public void setClassificationTag(String classificationTag) {
        this.classificationTag = classificationTag;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((additionalInformation == null) ? 0 : additionalInformation.hashCode());
        result = prime * result + ((alleleAccessionId == null) ? 0 : alleleAccessionId.hashCode());
        result = prime * result + ((alleleName == null) ? 0 : alleleName.hashCode());
        result = prime * result + ((alleleSymbol == null) ? 0 : alleleSymbol.hashCode());
        result = prime * result + ((batchSignificant == null) ? 0 : batchSignificant.hashCode());
        result = prime * result + ((blupsTest == null) ? 0 : blupsTest.hashCode());
        result = prime * result + ((categoricalEffectSize == null) ? 0 : categoricalEffectSize.hashCode());
        result = prime * result + ((categoricalPValue == null) ? 0 : categoricalPValue.hashCode());
        result = prime * result + ((categories == null) ? 0 : categories.hashCode());
        result = prime * result + ((classificationTag == null) ? 0 : classificationTag.hashCode());
        result = prime * result + ((colonyId == null) ? 0 : colonyId.hashCode());
        result = prime * result + ((controlBiologicalModelId == null) ? 0 : controlBiologicalModelId.hashCode());
        result = prime * result + ((controlSelectionMethod == null) ? 0 : controlSelectionMethod.hashCode());
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
        result = prime * result + ((dependentVariable == null) ? 0 : dependentVariable.hashCode());
        result = prime * result + ((docId == null) ? 0 : docId.hashCode());
        result = prime * result + ((effectSize == null) ? 0 : effectSize.hashCode());
        result = prime * result + ((femaleControlCount == null) ? 0 : femaleControlCount.hashCode());
        result = prime * result + ((femaleKoEffectPValue == null) ? 0 : femaleKoEffectPValue.hashCode());
        result = prime * result + ((femaleKoEffectStderrEstimate == null) ? 0 : femaleKoEffectStderrEstimate.hashCode());
        result = prime * result + ((femaleKoParameterEstimate == null) ? 0 : femaleKoParameterEstimate.hashCode());
        result = prime * result + ((femaleMutantCount == null) ? 0 : femaleMutantCount.hashCode());
        result = prime * result + ((genotypeEffectPValue == null) ? 0 : genotypeEffectPValue.hashCode());
        result = prime * result + ((genotypeEffectParameterEstimate == null) ? 0 : genotypeEffectParameterEstimate.hashCode());
        result = prime * result + ((genotypeEffectStderrEstimate == null) ? 0 : genotypeEffectStderrEstimate.hashCode());
        result = prime * result + ((group1Genotype == null) ? 0 : group1Genotype.hashCode());
        result = prime * result + ((group1ResidualsNormalityTest == null) ? 0 : group1ResidualsNormalityTest.hashCode());
        result = prime * result + ((group2Genotype == null) ? 0 : group2Genotype.hashCode());
        result = prime * result + ((group2ResidualsNormalityTest == null) ? 0 : group2ResidualsNormalityTest.hashCode());
        result = prime * result + ((interactionEffectPValue == null) ? 0 : interactionEffectPValue.hashCode());
        result = prime * result + ((interactionSignificant == null) ? 0 : interactionSignificant.hashCode());
        result = prime * result + ((interceptEstimate == null) ? 0 : interceptEstimate.hashCode());
        result = prime * result + ((interceptEstimateStderrEstimate == null) ? 0 : interceptEstimateStderrEstimate.hashCode());
        result = prime * result + ((intermediateMpTermId == null) ? 0 : intermediateMpTermId.hashCode());
        result = prime * result + ((intermediateMpTermName == null) ? 0 : intermediateMpTermName.hashCode());
        result = prime * result + ((maleControlCount == null) ? 0 : maleControlCount.hashCode());
        result = prime * result + ((maleKoEffectPValue == null) ? 0 : maleKoEffectPValue.hashCode());
        result = prime * result + ((maleKoEffectStderrEstimate == null) ? 0 : maleKoEffectStderrEstimate.hashCode());
        result = prime * result + ((maleKoParameterEstimate == null) ? 0 : maleKoParameterEstimate.hashCode());
        result = prime * result + ((maleMutantCount == null) ? 0 : maleMutantCount.hashCode());
        result = prime * result + ((markerAccessionId == null) ? 0 : markerAccessionId.hashCode());
        result = prime * result + ((markerSymbol == null) ? 0 : markerSymbol.hashCode());
        result = prime * result + ((metadataGroup == null) ? 0 : metadataGroup.hashCode());
        result = prime * result + ((mpTermId == null) ? 0 : mpTermId.hashCode());
        result = prime * result + ((mpTermName == null) ? 0 : mpTermName.hashCode());
        result = prime * result + ((mutantBiologicalModelId == null) ? 0 : mutantBiologicalModelId.hashCode());
        result = prime * result + ((nullTestPValue == null) ? 0 : nullTestPValue.hashCode());
        result = prime * result + ((pValue == null) ? 0 : pValue.hashCode());
        result = prime * result + ((parameterId == null) ? 0 : parameterId.hashCode());
        result = prime * result + ((parameterName == null) ? 0 : parameterName.hashCode());
        result = prime * result + ((parameterStableId == null) ? 0 : parameterStableId.hashCode());
        result = prime * result + ((parameterStableKey == null) ? 0 : parameterStableKey.hashCode());
        result = prime * result + ((phenotypingCenter == null) ? 0 : phenotypingCenter.hashCode());
        result = prime * result + ((pipelineId == null) ? 0 : pipelineId.hashCode());
        result = prime * result + ((pipelineName == null) ? 0 : pipelineName.hashCode());
        result = prime * result + ((pipelineStableId == null) ? 0 : pipelineStableId.hashCode());
        result = prime * result + ((pipelineStableKey == null) ? 0 : pipelineStableKey.hashCode());
        result = prime * result + ((procedureId == null) ? 0 : procedureId.hashCode());
        result = prime * result + ((procedureName == null) ? 0 : procedureName.hashCode());
        result = prime * result + ((procedureStableId == null) ? 0 : procedureStableId.hashCode());
        result = prime * result + ((procedureStableKey == null) ? 0 : procedureStableKey.hashCode());
        result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
        result = prime * result + ((rawOutput == null) ? 0 : rawOutput.hashCode());
        result = prime * result + ((resourceFullname == null) ? 0 : resourceFullname.hashCode());
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
        result = prime * result + ((rotatedResidualsTest == null) ? 0 : rotatedResidualsTest.hashCode());
        result = prime * result + ((sex == null) ? 0 : sex.hashCode());
        result = prime * result + ((sexEffectPValue == null) ? 0 : sexEffectPValue.hashCode());
        result = prime * result + ((sexEffectParameterEstimate == null) ? 0 : sexEffectParameterEstimate.hashCode());
        result = prime * result + ((sexEffectStderrEstimate == null) ? 0 : sexEffectStderrEstimate.hashCode());
        result = prime * result + ((statisticalMethod == null) ? 0 : statisticalMethod.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((strainAccessionId == null) ? 0 : strainAccessionId.hashCode());
        result = prime * result + ((strainName == null) ? 0 : strainName.hashCode());
        result = prime * result + ((topLevelMpTermId == null) ? 0 : topLevelMpTermId.hashCode());
        result = prime * result + ((topLevelMpTermName == null) ? 0 : topLevelMpTermName.hashCode());
        result = prime * result + ((varianceSignificant == null) ? 0 : varianceSignificant.hashCode());
        result = prime * result + ((weightEffectPValue == null) ? 0 : weightEffectPValue.hashCode());
        result = prime * result + ((weightEffectParameterEstimate == null) ? 0 : weightEffectParameterEstimate.hashCode());
        result = prime * result + ((weightEffectStderrEstimate == null) ? 0 : weightEffectStderrEstimate.hashCode());
        result = prime * result + ((zygosity == null) ? 0 : zygosity.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        StatisticalResultDTO other = (StatisticalResultDTO) obj;
        if (additionalInformation == null) {
            if (other.additionalInformation != null) { return false; }
        } else if (!additionalInformation.equals(other.additionalInformation)) { return false; }
        if (alleleAccessionId == null) {
            if (other.alleleAccessionId != null) { return false; }
        } else if (!alleleAccessionId.equals(other.alleleAccessionId)) { return false; }
        if (alleleName == null) {
            if (other.alleleName != null) { return false; }
        } else if (!alleleName.equals(other.alleleName)) { return false; }
        if (alleleSymbol == null) {
            if (other.alleleSymbol != null) { return false; }
        } else if (!alleleSymbol.equals(other.alleleSymbol)) { return false; }
        if (batchSignificant == null) {
            if (other.batchSignificant != null) { return false; }
        } else if (!batchSignificant.equals(other.batchSignificant)) { return false; }
        if (blupsTest == null) {
            if (other.blupsTest != null) { return false; }
        } else if (!blupsTest.equals(other.blupsTest)) { return false; }
        if (categoricalEffectSize == null) {
            if (other.categoricalEffectSize != null) { return false; }
        } else if (!categoricalEffectSize.equals(other.categoricalEffectSize)) { return false; }
        if (categoricalPValue == null) {
            if (other.categoricalPValue != null) { return false; }
        } else if (!categoricalPValue.equals(other.categoricalPValue)) { return false; }
        if (categories == null) {
            if (other.categories != null) { return false; }
        } else if (!categories.equals(other.categories)) { return false; }
        if (classificationTag == null) {
            if (other.classificationTag != null) { return false; }
        } else if (!classificationTag.equals(other.classificationTag)) { return false; }
        if (colonyId == null) {
            if (other.colonyId != null) { return false; }
        } else if (!colonyId.equals(other.colonyId)) { return false; }
        if (controlBiologicalModelId == null) {
            if (other.controlBiologicalModelId != null) { return false; }
        } else if (!controlBiologicalModelId.equals(other.controlBiologicalModelId)) { return false; }
        if (controlSelectionMethod == null) {
            if (other.controlSelectionMethod != null) { return false; }
        } else if (!controlSelectionMethod.equals(other.controlSelectionMethod)) { return false; }
        if (dataType == null) {
            if (other.dataType != null) { return false; }
        } else if (!dataType.equals(other.dataType)) { return false; }
        if (dbId == null) {
            if (other.dbId != null) { return false; }
        } else if (!dbId.equals(other.dbId)) { return false; }
        if (dependentVariable == null) {
            if (other.dependentVariable != null) { return false; }
        } else if (!dependentVariable.equals(other.dependentVariable)) { return false; }
        if (docId == null) {
            if (other.docId != null) { return false; }
        } else if (!docId.equals(other.docId)) { return false; }
        if (effectSize == null) {
            if (other.effectSize != null) { return false; }
        } else if (!effectSize.equals(other.effectSize)) { return false; }
        if (femaleControlCount == null) {
            if (other.femaleControlCount != null) { return false; }
        } else if (!femaleControlCount.equals(other.femaleControlCount)) { return false; }
        if (femaleKoEffectPValue == null) {
            if (other.femaleKoEffectPValue != null) { return false; }
        } else if (!femaleKoEffectPValue.equals(other.femaleKoEffectPValue)) { return false; }
        if (femaleKoEffectStderrEstimate == null) {
            if (other.femaleKoEffectStderrEstimate != null) { return false; }
        } else if (!femaleKoEffectStderrEstimate.equals(other.femaleKoEffectStderrEstimate)) { return false; }
        if (femaleKoParameterEstimate == null) {
            if (other.femaleKoParameterEstimate != null) { return false; }
        } else if (!femaleKoParameterEstimate.equals(other.femaleKoParameterEstimate)) { return false; }
        if (femaleMutantCount == null) {
            if (other.femaleMutantCount != null) { return false; }
        } else if (!femaleMutantCount.equals(other.femaleMutantCount)) { return false; }
        if (genotypeEffectPValue == null) {
            if (other.genotypeEffectPValue != null) { return false; }
        } else if (!genotypeEffectPValue.equals(other.genotypeEffectPValue)) { return false; }
        if (genotypeEffectParameterEstimate == null) {
            if (other.genotypeEffectParameterEstimate != null) { return false; }
        } else if (!genotypeEffectParameterEstimate.equals(other.genotypeEffectParameterEstimate)) { return false; }
        if (genotypeEffectStderrEstimate == null) {
            if (other.genotypeEffectStderrEstimate != null) { return false; }
        } else if (!genotypeEffectStderrEstimate.equals(other.genotypeEffectStderrEstimate)) { return false; }
        if (group1Genotype == null) {
            if (other.group1Genotype != null) { return false; }
        } else if (!group1Genotype.equals(other.group1Genotype)) { return false; }
        if (group1ResidualsNormalityTest == null) {
            if (other.group1ResidualsNormalityTest != null) { return false; }
        } else if (!group1ResidualsNormalityTest.equals(other.group1ResidualsNormalityTest)) { return false; }
        if (group2Genotype == null) {
            if (other.group2Genotype != null) { return false; }
        } else if (!group2Genotype.equals(other.group2Genotype)) { return false; }
        if (group2ResidualsNormalityTest == null) {
            if (other.group2ResidualsNormalityTest != null) { return false; }
        } else if (!group2ResidualsNormalityTest.equals(other.group2ResidualsNormalityTest)) { return false; }
        if (interactionEffectPValue == null) {
            if (other.interactionEffectPValue != null) { return false; }
        } else if (!interactionEffectPValue.equals(other.interactionEffectPValue)) { return false; }
        if (interactionSignificant == null) {
            if (other.interactionSignificant != null) { return false; }
        } else if (!interactionSignificant.equals(other.interactionSignificant)) { return false; }
        if (interceptEstimate == null) {
            if (other.interceptEstimate != null) { return false; }
        } else if (!interceptEstimate.equals(other.interceptEstimate)) { return false; }
        if (interceptEstimateStderrEstimate == null) {
            if (other.interceptEstimateStderrEstimate != null) { return false; }
        } else if (!interceptEstimateStderrEstimate.equals(other.interceptEstimateStderrEstimate)) { return false; }
        if (intermediateMpTermId == null) {
            if (other.intermediateMpTermId != null) { return false; }
        } else if (!intermediateMpTermId.equals(other.intermediateMpTermId)) { return false; }
        if (intermediateMpTermName == null) {
            if (other.intermediateMpTermName != null) { return false; }
        } else if (!intermediateMpTermName.equals(other.intermediateMpTermName)) { return false; }
        if (maleControlCount == null) {
            if (other.maleControlCount != null) { return false; }
        } else if (!maleControlCount.equals(other.maleControlCount)) { return false; }
        if (maleKoEffectPValue == null) {
            if (other.maleKoEffectPValue != null) { return false; }
        } else if (!maleKoEffectPValue.equals(other.maleKoEffectPValue)) { return false; }
        if (maleKoEffectStderrEstimate == null) {
            if (other.maleKoEffectStderrEstimate != null) { return false; }
        } else if (!maleKoEffectStderrEstimate.equals(other.maleKoEffectStderrEstimate)) { return false; }
        if (maleKoParameterEstimate == null) {
            if (other.maleKoParameterEstimate != null) { return false; }
        } else if (!maleKoParameterEstimate.equals(other.maleKoParameterEstimate)) { return false; }
        if (maleMutantCount == null) {
            if (other.maleMutantCount != null) { return false; }
        } else if (!maleMutantCount.equals(other.maleMutantCount)) { return false; }
        if (markerAccessionId == null) {
            if (other.markerAccessionId != null) { return false; }
        } else if (!markerAccessionId.equals(other.markerAccessionId)) { return false; }
        if (markerSymbol == null) {
            if (other.markerSymbol != null) { return false; }
        } else if (!markerSymbol.equals(other.markerSymbol)) { return false; }
        if (metadataGroup == null) {
            if (other.metadataGroup != null) { return false; }
        } else if (!metadataGroup.equals(other.metadataGroup)) { return false; }
        if (mpTermId == null) {
            if (other.mpTermId != null) { return false; }
        } else if (!mpTermId.equals(other.mpTermId)) { return false; }
        if (mpTermName == null) {
            if (other.mpTermName != null) { return false; }
        } else if (!mpTermName.equals(other.mpTermName)) { return false; }
        if (mutantBiologicalModelId == null) {
            if (other.mutantBiologicalModelId != null) { return false; }
        } else if (!mutantBiologicalModelId.equals(other.mutantBiologicalModelId)) { return false; }
        if (nullTestPValue == null) {
            if (other.nullTestPValue != null) { return false; }
        } else if (!nullTestPValue.equals(other.nullTestPValue)) { return false; }
        if (pValue == null) {
            if (other.pValue != null) { return false; }
        } else if (!pValue.equals(other.pValue)) { return false; }
        if (parameterId == null) {
            if (other.parameterId != null) { return false; }
        } else if (!parameterId.equals(other.parameterId)) { return false; }
        if (parameterName == null) {
            if (other.parameterName != null) { return false; }
        } else if (!parameterName.equals(other.parameterName)) { return false; }
        if (parameterStableId == null) {
            if (other.parameterStableId != null) { return false; }
        } else if (!parameterStableId.equals(other.parameterStableId)) { return false; }
        if (parameterStableKey == null) {
            if (other.parameterStableKey != null) { return false; }
        } else if (!parameterStableKey.equals(other.parameterStableKey)) { return false; }
        if (phenotypingCenter == null) {
            if (other.phenotypingCenter != null) { return false; }
        } else if (!phenotypingCenter.equals(other.phenotypingCenter)) { return false; }
        if (pipelineId == null) {
            if (other.pipelineId != null) { return false; }
        } else if (!pipelineId.equals(other.pipelineId)) { return false; }
        if (pipelineName == null) {
            if (other.pipelineName != null) { return false; }
        } else if (!pipelineName.equals(other.pipelineName)) { return false; }
        if (pipelineStableId == null) {
            if (other.pipelineStableId != null) { return false; }
        } else if (!pipelineStableId.equals(other.pipelineStableId)) { return false; }
        if (pipelineStableKey == null) {
            if (other.pipelineStableKey != null) { return false; }
        } else if (!pipelineStableKey.equals(other.pipelineStableKey)) { return false; }
        if (procedureId == null) {
            if (other.procedureId != null) { return false; }
        } else if (!procedureId.equals(other.procedureId)) { return false; }
        if (procedureName == null) {
            if (other.procedureName != null) { return false; }
        } else if (!procedureName.equals(other.procedureName)) { return false; }
        if (procedureStableId == null) {
            if (other.procedureStableId != null) { return false; }
        } else if (!procedureStableId.equals(other.procedureStableId)) { return false; }
        if (procedureStableKey == null) {
            if (other.procedureStableKey != null) { return false; }
        } else if (!procedureStableKey.equals(other.procedureStableKey)) { return false; }
        if (projectName == null) {
            if (other.projectName != null) { return false; }
        } else if (!projectName.equals(other.projectName)) { return false; }
        if (rawOutput == null) {
            if (other.rawOutput != null) { return false; }
        } else if (!rawOutput.equals(other.rawOutput)) { return false; }
        if (resourceFullname == null) {
            if (other.resourceFullname != null) { return false; }
        } else if (!resourceFullname.equals(other.resourceFullname)) { return false; }
        if (resourceId == null) {
            if (other.resourceId != null) { return false; }
        } else if (!resourceId.equals(other.resourceId)) { return false; }
        if (resourceName == null) {
            if (other.resourceName != null) { return false; }
        } else if (!resourceName.equals(other.resourceName)) { return false; }
        if (rotatedResidualsTest == null) {
            if (other.rotatedResidualsTest != null) { return false; }
        } else if (!rotatedResidualsTest.equals(other.rotatedResidualsTest)) { return false; }
        if (sex == null) {
            if (other.sex != null) { return false; }
        } else if (!sex.equals(other.sex)) { return false; }
        if (sexEffectPValue == null) {
            if (other.sexEffectPValue != null) { return false; }
        } else if (!sexEffectPValue.equals(other.sexEffectPValue)) { return false; }
        if (sexEffectParameterEstimate == null) {
            if (other.sexEffectParameterEstimate != null) { return false; }
        } else if (!sexEffectParameterEstimate.equals(other.sexEffectParameterEstimate)) { return false; }
        if (sexEffectStderrEstimate == null) {
            if (other.sexEffectStderrEstimate != null) { return false; }
        } else if (!sexEffectStderrEstimate.equals(other.sexEffectStderrEstimate)) { return false; }
        if (statisticalMethod == null) {
            if (other.statisticalMethod != null) { return false; }
        } else if (!statisticalMethod.equals(other.statisticalMethod)) { return false; }
        if (status == null) {
            if (other.status != null) { return false; }
        } else if (!status.equals(other.status)) { return false; }
        if (strainAccessionId == null) {
            if (other.strainAccessionId != null) { return false; }
        } else if (!strainAccessionId.equals(other.strainAccessionId)) { return false; }
        if (strainName == null) {
            if (other.strainName != null) { return false; }
        } else if (!strainName.equals(other.strainName)) { return false; }
        if (topLevelMpTermId == null) {
            if (other.topLevelMpTermId != null) { return false; }
        } else if (!topLevelMpTermId.equals(other.topLevelMpTermId)) { return false; }
        if (topLevelMpTermName == null) {
            if (other.topLevelMpTermName != null) { return false; }
        } else if (!topLevelMpTermName.equals(other.topLevelMpTermName)) { return false; }
        if (varianceSignificant == null) {
            if (other.varianceSignificant != null) { return false; }
        } else if (!varianceSignificant.equals(other.varianceSignificant)) { return false; }
        if (weightEffectPValue == null) {
            if (other.weightEffectPValue != null) { return false; }
        } else if (!weightEffectPValue.equals(other.weightEffectPValue)) { return false; }
        if (weightEffectParameterEstimate == null) {
            if (other.weightEffectParameterEstimate != null) { return false; }
        } else if (!weightEffectParameterEstimate.equals(other.weightEffectParameterEstimate)) { return false; }
        if (weightEffectStderrEstimate == null) {
            if (other.weightEffectStderrEstimate != null) { return false; }
        } else if (!weightEffectStderrEstimate.equals(other.weightEffectStderrEstimate)) { return false; }
        if (zygosity == null) {
            if (other.zygosity != null) { return false; }
        } else if (!zygosity.equals(other.zygosity)) { return false; }
        return true;
    }


    @Override
    public String toString() {
        return "StatisticalResultDTO [docId=" + docId + ", dbId=" + dbId + ", dataType=" + dataType + ", mpTermId=" + mpTermId + ", mpTermName=" + mpTermName + ", topLevelMpTermId=" + topLevelMpTermId + ", topLevelMpTermName=" + topLevelMpTermName + ", intermediateMpTermId=" + intermediateMpTermId + ", intermediateMpTermName=" + intermediateMpTermName + ", resourceName=" + resourceName + ", resourceFullname=" + resourceFullname + ", resourceId=" + resourceId + ", projectName=" + projectName + ", phenotypingCenter=" + phenotypingCenter + ", pipelineStableId=" + pipelineStableId + ", pipelineStableKey=" + pipelineStableKey + ", pipelineName=" + pipelineName + ", pipelineId=" + pipelineId + ", procedureStableId=" + procedureStableId + ", procedureStableKey=" + procedureStableKey + ", procedureName=" + procedureName + ", procedureId=" + procedureId + ", parameterStableId=" + parameterStableId + ", parameterStableKey=" + parameterStableKey + ", parameterName=" + parameterName + ", parameterId=" + parameterId + ", colonyId=" + colonyId + ", markerSymbol=" + markerSymbol + ", markerAccessionId=" + markerAccessionId + ", alleleSymbol=" + alleleSymbol + ", alleleName=" + alleleName + ", alleleAccessionId=" + alleleAccessionId + ", strainName=" + strainName + ", strainAccessionId=" + strainAccessionId + ", sex=" + sex + ", zygosity=" + zygosity + ", controlSelectionMethod=" + controlSelectionMethod + ", dependentVariable=" + dependentVariable + ", metadataGroup=" + metadataGroup + ", controlBiologicalModelId=" + controlBiologicalModelId + ", mutantBiologicalModelId=" + mutantBiologicalModelId + ", maleControlCount=" + maleControlCount + ", maleMutantCount=" + maleMutantCount + ", femaleControlCount=" + femaleControlCount + ", femaleMutantCount=" + femaleMutantCount + ", statisticalMethod=" + statisticalMethod + ", status=" + status + ", additionalInformation=" + additionalInformation + ", rawOutput=" + rawOutput + ", pValue=" + pValue + ", effectSize=" + effectSize + ", categories=" + categories + ", categoricalPValue=" + categoricalPValue + ", categoricalEffectSize=" + categoricalEffectSize + ", batchSignificant=" + batchSignificant + ", varianceSignificant=" + varianceSignificant + ", nullTestPValue=" + nullTestPValue + ", genotypeEffectPValue=" + genotypeEffectPValue + ", genotypeEffectStderrEstimate=" + genotypeEffectStderrEstimate + ", genotypeEffectParameterEstimate=" + genotypeEffectParameterEstimate + ", sexEffectPValue=" + sexEffectPValue + ", sexEffectStderrEstimate=" + sexEffectStderrEstimate + ", sexEffectParameterEstimate=" + sexEffectParameterEstimate + ", weightEffectPValue=" + weightEffectPValue + ", weightEffectStderrEstimate=" + weightEffectStderrEstimate + ", weightEffectParameterEstimate=" + weightEffectParameterEstimate + ", group1Genotype=" + group1Genotype + ", group1ResidualsNormalityTest=" + group1ResidualsNormalityTest + ", group2Genotype=" + group2Genotype + ", group2ResidualsNormalityTest=" + group2ResidualsNormalityTest + ", blupsTest=" + blupsTest + ", rotatedResidualsTest=" + rotatedResidualsTest + ", interceptEstimate=" + interceptEstimate + ", interceptEstimateStderrEstimate=" + interceptEstimateStderrEstimate + ", interactionSignificant=" + interactionSignificant + ", interactionEffectPValue=" + interactionEffectPValue + ", femaleKoEffectPValue=" + femaleKoEffectPValue + ", femaleKoEffectStderrEstimate=" + femaleKoEffectStderrEstimate + ", femaleKoParameterEstimate=" + femaleKoParameterEstimate + ", maleKoEffectPValue=" + maleKoEffectPValue + ", maleKoEffectStderrEstimate=" + maleKoEffectStderrEstimate + ", maleKoParameterEstimate=" + maleKoParameterEstimate + ", classificationTag=" + classificationTag + "]";
    }




}
