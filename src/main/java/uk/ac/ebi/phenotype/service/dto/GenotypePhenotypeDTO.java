package uk.ac.ebi.phenotype.service.dto;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class GenotypePhenotypeDTO {

	public static final String DOC_ID = "doc_id";
	public static final String MP_TERM_ID = "mp_term_id";
	public static final String MP_TERM_NAME = "mp_term_name";
	public static final String TOP_LEVEL_MP_TERM_ID = "top_level_mp_term_id";
	public static final String TOP_LEVEL_MP_TERM_NAME = "top_level_mp_term_name";
	public static final String TOP_LEVEL_MP_TERM_DEFINITION = "top_level_mp_term_definition";
	public static final String TOP_LEVEL_MP_TERM_SYNONYM = "top_level_mp_term_synonym";
	public static final String INTERMEDIATE_MP_TERM_ID = "intermediate_mp_term_id";
	public static final String INTERMEDIATE_MP_TERM_NAME = "intermediate_mp_term_name";
	public static final String INTERMEDIATE_MP_TERM_DEFINITION = "intermediate_mp_term_definition";
	public static final String INTERMEDIATE_MP_TERM_SYNONYM = "intermediate_mp_term_synonym";
	public static final String MARKER_SYMBOL = "marker_symbol";
	public static final String MARKER_ACCESSION_ID = "marker_accession_id";
	public static final String COLONY_ID = "colony_id";
	public static final String ALLELE_NAME = "allele_name";
	public static final String ALLELE_SYMBOL = "allele_symbol";
	public static final String ALLELE_ACCESSION_ID = "allele_accession_id";
	public static final String STRAIN_NAME = "strain_name";
	public static final String STRAIN_ACCESSION_ID = "strain_accession_id";
	public static final String PHENOTYPING_CENTER = "phenotyping_center";
	public static final String PROJECT_EXTERNAL_ID = "project_external_id";
	public static final String PROJECT_NAME = "project_name";
	public static final String PROJECT_FULLNAME = "project_fullname";
	public static final String RESOURCE_NAME = "resource_name";
	public static final String RESOURCE_FULLNAME = "resource_fullname";
	public static final String SEX = "sex";
	public static final String ZYGOSITY = "zygosity";
	public static final String PIPELINE_NAME = "pipeline_name";
	public static final String PIPELINE_STABLE_ID = "pipeline_stable_id";
	public static final String PIPELINE_STABLE_KEY = "pipeline_stable_key";
	public static final String PROCEDURE_NAME = "procedure_name";
	public static final String PROCEDURE_STABLE_ID = "procedure_stable_id";
	public static final String PROCEDURE_STABLE_KEY = "procedure_stable_key";
	public static final String PARAMETER_NAME = "parameter_name";
	public static final String PARAMETER_STABLE_ID = "parameter_stable_id";
	public static final String PARAMETER_STABLE_KEY = "parameter_stable_key";
	public static final String P_VALUE = "p_value";
	public static final String EFFECT_SIZE = "effect_size";
	public static final String EXTERNAL_ID = "external_id";

	@Field(DOC_ID)
	Integer doc_id;

	@Field(MP_TERM_ID)
	String mpTermId;

	@Field(MP_TERM_NAME)
	String mpTermName;

	@Field(TOP_LEVEL_MP_TERM_ID)
	List<String> topLevelMpTermId;

	@Field(TOP_LEVEL_MP_TERM_NAME)
	List<String> topLevelMpTermName;

	@Field(TOP_LEVEL_MP_TERM_DEFINITION)
	List<String> topLevelMpTermDefinition;

	@Field(TOP_LEVEL_MP_TERM_SYNONYM)
	List<String> topLevelMpTermSynonym;

	@Field(INTERMEDIATE_MP_TERM_ID)
	List<String> intermediateMpTermId;

	@Field(INTERMEDIATE_MP_TERM_NAME)
	List<String> intermediateMpTermName;

	@Field(INTERMEDIATE_MP_TERM_DEFINITION)
	List<String> intermediateMpTermDefinition;

	@Field(INTERMEDIATE_MP_TERM_SYNONYM)
	List<String> intermediateMpTermSynonym;

	@Field(MARKER_SYMBOL)
	String markerSymbol;

	@Field(MARKER_ACCESSION_ID)
	String markerAccessionId;

	@Field(COLONY_ID)
	String colonyId;

	@Field(ALLELE_NAME)
	String alleleName;

	@Field(ALLELE_SYMBOL)
	String alleleSymbol;

	@Field(ALLELE_ACCESSION_ID)
	String alleleAccessionId;

	@Field(STRAIN_NAME)
	String strainName;

	@Field(STRAIN_ACCESSION_ID)
	String strainAccessionId;

	@Field(PHENOTYPING_CENTER)
	String phenotypingCenter;

	@Field(PROJECT_EXTERNAL_ID)
	String projectExternalId;

	@Field(PROJECT_NAME)
	String projectName;

	@Field(PROJECT_FULLNAME)
	String projectFullname;

	@Field(RESOURCE_NAME)
	String resourceName;

	@Field(RESOURCE_FULLNAME)
	String resourceFullname;

	@Field(SEX)
	String sex;

	@Field(ZYGOSITY)
	String zygosity;

	@Field(PIPELINE_NAME)
	String pipelineName;

	@Field(PIPELINE_STABLE_ID)
	String pipelineStableId;

	@Field(PIPELINE_STABLE_KEY)
	String pipelineStableKey;

	@Field(PROCEDURE_NAME)
	String procedureName;

	@Field(PROCEDURE_STABLE_ID)
	String procedureStableId;

	@Field(PROCEDURE_STABLE_KEY)
	String procedureStableKey;

	@Field(PARAMETER_NAME)
	String parameterName;

	@Field(PARAMETER_STABLE_ID)
	String parameterStableId;

	@Field(PARAMETER_STABLE_KEY)
	String parameterStableKey;

	@Field(P_VALUE)
	Float p_value;

	@Field(EFFECT_SIZE)
	Float effect_size;

	@Field(EXTERNAL_ID)
	String externalId;

	
	public Integer getDoc_id() {
	
		return doc_id;
	}

	
	public void setDoc_id(Integer doc_id) {
	
		this.doc_id = doc_id;
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

	
	public List<String> getTopLevelMpTermDefinition() {
	
		return topLevelMpTermDefinition;
	}

	
	public void setTopLevelMpTermDefinition(List<String> topLevelMpTermDefinition) {
	
		this.topLevelMpTermDefinition = topLevelMpTermDefinition;
	}

	
	public List<String> getTopLevelMpTermSynonym() {
	
		return topLevelMpTermSynonym;
	}

	
	public void setTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {
	
		this.topLevelMpTermSynonym = topLevelMpTermSynonym;
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

	
	public List<String> getIntermediateMpTermDefinition() {
	
		return intermediateMpTermDefinition;
	}

	
	public void setIntermediateMpTermDefinition(List<String> intermediateMpTermDefinition) {
	
		this.intermediateMpTermDefinition = intermediateMpTermDefinition;
	}

	
	public List<String> getIntermediateMpTermSynonym() {
	
		return intermediateMpTermSynonym;
	}

	
	public void setIntermediateMpTermSynonym(List<String> intermediateMpTermSynonym) {
	
		this.intermediateMpTermSynonym = intermediateMpTermSynonym;
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

	
	public String getColonyId() {
	
		return colonyId;
	}

	
	public void setColonyId(String colonyId) {
	
		this.colonyId = colonyId;
	}

	
	public String getAlleleName() {
	
		return alleleName;
	}

	
	public void setAlleleName(String alleleName) {
	
		this.alleleName = alleleName;
	}

	
	public String getAlleleSymbol() {
	
		return alleleSymbol;
	}

	
	public void setAlleleSymbol(String alleleSymbol) {
	
		this.alleleSymbol = alleleSymbol;
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

	
	public String getPhenotypingCenter() {
	
		return phenotypingCenter;
	}

	
	public void setPhenotypingCenter(String phenotypingCenter) {
	
		this.phenotypingCenter = phenotypingCenter;
	}

	
	public String getProjectExternalId() {
	
		return projectExternalId;
	}

	
	public void setProjectExternalId(String projectExternalId) {
	
		this.projectExternalId = projectExternalId;
	}

	
	public String getProjectName() {
	
		return projectName;
	}

	
	public void setProjectName(String projectName) {
	
		this.projectName = projectName;
	}

	
	public String getProjectFullname() {
	
		return projectFullname;
	}

	
	public void setProjectFullname(String projectFullname) {
	
		this.projectFullname = projectFullname;
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

	
	public String getPipelineName() {
	
		return pipelineName;
	}

	
	public void setPipelineName(String pipelineName) {
	
		this.pipelineName = pipelineName;
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

	
	public String getProcedureName() {
	
		return procedureName;
	}

	
	public void setProcedureName(String procedureName) {
	
		this.procedureName = procedureName;
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

	
	public String getParameterName() {
	
		return parameterName;
	}

	
	public void setParameterName(String parameterName) {
	
		this.parameterName = parameterName;
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

	
	public Float getP_value() {
	
		return p_value;
	}

	
	public void setP_value(Float p_value) {
	
		this.p_value = p_value;
	}

	
	public Float getEffect_size() {
	
		return effect_size;
	}

	
	public void setEffect_size(Float effect_size) {
	
		this.effect_size = effect_size;
	}

	
	public String getExternalId() {
	
		return externalId;
	}

	
	public void setExternalId(String externalId) {
	
		this.externalId = externalId;
	}


	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((alleleAccessionId == null) ? 0 : alleleAccessionId.hashCode());
		result = prime * result + ((alleleName == null) ? 0 : alleleName.hashCode());
		result = prime * result + ((alleleSymbol == null) ? 0 : alleleSymbol.hashCode());
		result = prime * result + ((colonyId == null) ? 0 : colonyId.hashCode());
		result = prime * result + ((doc_id == null) ? 0 : doc_id.hashCode());
		result = prime * result + ((effect_size == null) ? 0 : effect_size.hashCode());
		result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
		result = prime * result + ((intermediateMpTermDefinition == null) ? 0 : intermediateMpTermDefinition.hashCode());
		result = prime * result + ((intermediateMpTermId == null) ? 0 : intermediateMpTermId.hashCode());
		result = prime * result + ((intermediateMpTermName == null) ? 0 : intermediateMpTermName.hashCode());
		result = prime * result + ((intermediateMpTermSynonym == null) ? 0 : intermediateMpTermSynonym.hashCode());
		result = prime * result + ((markerAccessionId == null) ? 0 : markerAccessionId.hashCode());
		result = prime * result + ((markerSymbol == null) ? 0 : markerSymbol.hashCode());
		result = prime * result + ((mpTermId == null) ? 0 : mpTermId.hashCode());
		result = prime * result + ((mpTermName == null) ? 0 : mpTermName.hashCode());
		result = prime * result + ((p_value == null) ? 0 : p_value.hashCode());
		result = prime * result + ((parameterName == null) ? 0 : parameterName.hashCode());
		result = prime * result + ((parameterStableId == null) ? 0 : parameterStableId.hashCode());
		result = prime * result + ((parameterStableKey == null) ? 0 : parameterStableKey.hashCode());
		result = prime * result + ((phenotypingCenter == null) ? 0 : phenotypingCenter.hashCode());
		result = prime * result + ((pipelineName == null) ? 0 : pipelineName.hashCode());
		result = prime * result + ((pipelineStableId == null) ? 0 : pipelineStableId.hashCode());
		result = prime * result + ((pipelineStableKey == null) ? 0 : pipelineStableKey.hashCode());
		result = prime * result + ((procedureName == null) ? 0 : procedureName.hashCode());
		result = prime * result + ((procedureStableId == null) ? 0 : procedureStableId.hashCode());
		result = prime * result + ((procedureStableKey == null) ? 0 : procedureStableKey.hashCode());
		result = prime * result + ((projectExternalId == null) ? 0 : projectExternalId.hashCode());
		result = prime * result + ((projectFullname == null) ? 0 : projectFullname.hashCode());
		result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
		result = prime * result + ((resourceFullname == null) ? 0 : resourceFullname.hashCode());
		result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
		result = prime * result + ((sex == null) ? 0 : sex.hashCode());
		result = prime * result + ((strainAccessionId == null) ? 0 : strainAccessionId.hashCode());
		result = prime * result + ((strainName == null) ? 0 : strainName.hashCode());
		result = prime * result + ((topLevelMpTermDefinition == null) ? 0 : topLevelMpTermDefinition.hashCode());
		result = prime * result + ((topLevelMpTermId == null) ? 0 : topLevelMpTermId.hashCode());
		result = prime * result + ((topLevelMpTermName == null) ? 0 : topLevelMpTermName.hashCode());
		result = prime * result + ((topLevelMpTermSynonym == null) ? 0 : topLevelMpTermSynonym.hashCode());
		result = prime * result + ((zygosity == null) ? 0 : zygosity.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {

		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		GenotypePhenotypeDTO other = (GenotypePhenotypeDTO) obj;
		if (alleleAccessionId == null) {
			if (other.alleleAccessionId != null) { return false; }
		} else if (!alleleAccessionId.equals(other.alleleAccessionId)) { return false; }
		if (alleleName == null) {
			if (other.alleleName != null) { return false; }
		} else if (!alleleName.equals(other.alleleName)) { return false; }
		if (alleleSymbol == null) {
			if (other.alleleSymbol != null) { return false; }
		} else if (!alleleSymbol.equals(other.alleleSymbol)) { return false; }
		if (colonyId == null) {
			if (other.colonyId != null) { return false; }
		} else if (!colonyId.equals(other.colonyId)) { return false; }
		if (doc_id == null) {
			if (other.doc_id != null) { return false; }
		} else if (!doc_id.equals(other.doc_id)) { return false; }
		if (effect_size == null) {
			if (other.effect_size != null) { return false; }
		} else if (!effect_size.equals(other.effect_size)) { return false; }
		if (externalId == null) {
			if (other.externalId != null) { return false; }
		} else if (!externalId.equals(other.externalId)) { return false; }
		if (intermediateMpTermDefinition == null) {
			if (other.intermediateMpTermDefinition != null) { return false; }
		} else if (!intermediateMpTermDefinition.equals(other.intermediateMpTermDefinition)) { return false; }
		if (intermediateMpTermId == null) {
			if (other.intermediateMpTermId != null) { return false; }
		} else if (!intermediateMpTermId.equals(other.intermediateMpTermId)) { return false; }
		if (intermediateMpTermName == null) {
			if (other.intermediateMpTermName != null) { return false; }
		} else if (!intermediateMpTermName.equals(other.intermediateMpTermName)) { return false; }
		if (intermediateMpTermSynonym == null) {
			if (other.intermediateMpTermSynonym != null) { return false; }
		} else if (!intermediateMpTermSynonym.equals(other.intermediateMpTermSynonym)) { return false; }
		if (markerAccessionId == null) {
			if (other.markerAccessionId != null) { return false; }
		} else if (!markerAccessionId.equals(other.markerAccessionId)) { return false; }
		if (markerSymbol == null) {
			if (other.markerSymbol != null) { return false; }
		} else if (!markerSymbol.equals(other.markerSymbol)) { return false; }
		if (mpTermId == null) {
			if (other.mpTermId != null) { return false; }
		} else if (!mpTermId.equals(other.mpTermId)) { return false; }
		if (mpTermName == null) {
			if (other.mpTermName != null) { return false; }
		} else if (!mpTermName.equals(other.mpTermName)) { return false; }
		if (p_value == null) {
			if (other.p_value != null) { return false; }
		} else if (!p_value.equals(other.p_value)) { return false; }
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
		if (pipelineName == null) {
			if (other.pipelineName != null) { return false; }
		} else if (!pipelineName.equals(other.pipelineName)) { return false; }
		if (pipelineStableId == null) {
			if (other.pipelineStableId != null) { return false; }
		} else if (!pipelineStableId.equals(other.pipelineStableId)) { return false; }
		if (pipelineStableKey == null) {
			if (other.pipelineStableKey != null) { return false; }
		} else if (!pipelineStableKey.equals(other.pipelineStableKey)) { return false; }
		if (procedureName == null) {
			if (other.procedureName != null) { return false; }
		} else if (!procedureName.equals(other.procedureName)) { return false; }
		if (procedureStableId == null) {
			if (other.procedureStableId != null) { return false; }
		} else if (!procedureStableId.equals(other.procedureStableId)) { return false; }
		if (procedureStableKey == null) {
			if (other.procedureStableKey != null) { return false; }
		} else if (!procedureStableKey.equals(other.procedureStableKey)) { return false; }
		if (projectExternalId == null) {
			if (other.projectExternalId != null) { return false; }
		} else if (!projectExternalId.equals(other.projectExternalId)) { return false; }
		if (projectFullname == null) {
			if (other.projectFullname != null) { return false; }
		} else if (!projectFullname.equals(other.projectFullname)) { return false; }
		if (projectName == null) {
			if (other.projectName != null) { return false; }
		} else if (!projectName.equals(other.projectName)) { return false; }
		if (resourceFullname == null) {
			if (other.resourceFullname != null) { return false; }
		} else if (!resourceFullname.equals(other.resourceFullname)) { return false; }
		if (resourceName == null) {
			if (other.resourceName != null) { return false; }
		} else if (!resourceName.equals(other.resourceName)) { return false; }
		if (sex == null) {
			if (other.sex != null) { return false; }
		} else if (!sex.equals(other.sex)) { return false; }
		if (strainAccessionId == null) {
			if (other.strainAccessionId != null) { return false; }
		} else if (!strainAccessionId.equals(other.strainAccessionId)) { return false; }
		if (strainName == null) {
			if (other.strainName != null) { return false; }
		} else if (!strainName.equals(other.strainName)) { return false; }
		if (topLevelMpTermDefinition == null) {
			if (other.topLevelMpTermDefinition != null) { return false; }
		} else if (!topLevelMpTermDefinition.equals(other.topLevelMpTermDefinition)) { return false; }
		if (topLevelMpTermId == null) {
			if (other.topLevelMpTermId != null) { return false; }
		} else if (!topLevelMpTermId.equals(other.topLevelMpTermId)) { return false; }
		if (topLevelMpTermName == null) {
			if (other.topLevelMpTermName != null) { return false; }
		} else if (!topLevelMpTermName.equals(other.topLevelMpTermName)) { return false; }
		if (topLevelMpTermSynonym == null) {
			if (other.topLevelMpTermSynonym != null) { return false; }
		} else if (!topLevelMpTermSynonym.equals(other.topLevelMpTermSynonym)) { return false; }
		if (zygosity == null) {
			if (other.zygosity != null) { return false; }
		} else if (!zygosity.equals(other.zygosity)) { return false; }
		return true;
	}


	@Override
	public String toString() {

		return "GenotypePhenotypeDTO [doc_id=" + doc_id + ", mpTermId=" + mpTermId + ", mpTermName=" + mpTermName + ", topLevelMpTermId=" + topLevelMpTermId + ", topLevelMpTermName=" + topLevelMpTermName + ", topLevelMpTermDefinition=" + topLevelMpTermDefinition + ", topLevelMpTermSynonym=" + topLevelMpTermSynonym + ", intermediateMpTermId=" + intermediateMpTermId + ", intermediateMpTermName=" + intermediateMpTermName + ", intermediateMpTermDefinition=" + intermediateMpTermDefinition + ", intermediateMpTermSynonym=" + intermediateMpTermSynonym + ", markerSymbol=" + markerSymbol + ", markerAccessionId=" + markerAccessionId + ", colonyId=" + colonyId + ", alleleName=" + alleleName + ", alleleSymbol=" + alleleSymbol + ", alleleAccessionId=" + alleleAccessionId + ", strainName=" + strainName + ", strainAccessionId=" + strainAccessionId + ", phenotypingCenter=" + phenotypingCenter + ", projectExternalId=" + projectExternalId + ", projectName=" + projectName + ", projectFullname=" + projectFullname + ", resourceName=" + resourceName + ", resourceFullname=" + resourceFullname + ", sex=" + sex + ", zygosity=" + zygosity + ", pipelineName=" + pipelineName + ", pipelineStableId=" + pipelineStableId + ", pipelineStableKey=" + pipelineStableKey + ", procedureName=" + procedureName + ", procedureStableId=" + procedureStableId + ", procedureStableKey=" + procedureStableKey + ", parameterName=" + parameterName + ", parameterStableId=" + parameterStableId + ", parameterStableKey=" + parameterStableKey + ", p_value=" + p_value + ", effect_size=" + effect_size + ", externalId=" + externalId + "]";
	}

	
}
