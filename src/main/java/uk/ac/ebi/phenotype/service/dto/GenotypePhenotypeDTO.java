package uk.ac.ebi.phenotype.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

public class GenotypePhenotypeDTO {

	public static final String ID = "doc_id";
	public static final String GID = "preqc_gid"; // preqc only
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
	public static final String STATISTICAL_METHOD = "statistical_method";
	public static final String PERCENTAGE_CHANGE = "percentage_change";
	public static final String P_VALUE = "p_value";
	public static final String EFFECT_SIZE = "effect_size";
	public static final String EXTERNAL_ID = "external_id";

	@Field(ID)
	Integer id;

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

	@Field(STATISTICAL_METHOD)
	String statisticalMethod;

	@Field(PERCENTAGE_CHANGE)
	String percentageChange;

	@Field(P_VALUE)
	Double p_value;

	@Field(EFFECT_SIZE)
	Double effect_size;

	@Field(EXTERNAL_ID)
	String externalId;

	
	public Integer getId() {
	
		return id;
	}

	
	public void setId(Integer id) {
	
		this.id = id;
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


	public String getStatisticalMethod() {

		return statisticalMethod;
	}


	public void setStatisticalMethod(String statisticalMethod) {

		this.statisticalMethod = statisticalMethod;
	}


	public String getPercentageChange() {

		return percentageChange;
	}


	public void setPercentageChange(String percentageChange) {

		this.percentageChange = percentageChange;
	}


	public Double getP_value() {
	
		return p_value;
	}

	
	public void setP_value(Double p_value) {
	
		this.p_value = p_value;
	}

	
	public Double getEffect_size() {
	
		return effect_size;
	}

	
	public void setEffect_size(Double effect_size) {
	
		this.effect_size = effect_size;
	}

	
	public String getExternalId() {
	
		return externalId;
	}

	
	public void setExternalId(String externalId) {
	
		this.externalId = externalId;
	}


	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (!(o instanceof GenotypePhenotypeDTO)) return false;

		GenotypePhenotypeDTO that = (GenotypePhenotypeDTO) o;

		if (alleleAccessionId != null ? !alleleAccessionId.equals(that.alleleAccessionId) : that.alleleAccessionId != null)
			return false;
		if (alleleName != null ? !alleleName.equals(that.alleleName) : that.alleleName != null) return false;
		if (alleleSymbol != null ? !alleleSymbol.equals(that.alleleSymbol) : that.alleleSymbol != null) return false;
		if (colonyId != null ? !colonyId.equals(that.colonyId) : that.colonyId != null) return false;
		if (effect_size != null ? !effect_size.equals(that.effect_size) : that.effect_size != null) return false;
		if (externalId != null ? !externalId.equals(that.externalId) : that.externalId != null) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (intermediateMpTermDefinition != null ? !intermediateMpTermDefinition.equals(that.intermediateMpTermDefinition) : that.intermediateMpTermDefinition != null)
			return false;
		if (intermediateMpTermId != null ? !intermediateMpTermId.equals(that.intermediateMpTermId) : that.intermediateMpTermId != null)
			return false;
		if (intermediateMpTermName != null ? !intermediateMpTermName.equals(that.intermediateMpTermName) : that.intermediateMpTermName != null)
			return false;
		if (intermediateMpTermSynonym != null ? !intermediateMpTermSynonym.equals(that.intermediateMpTermSynonym) : that.intermediateMpTermSynonym != null)
			return false;
		if (markerAccessionId != null ? !markerAccessionId.equals(that.markerAccessionId) : that.markerAccessionId != null)
			return false;
		if (markerSymbol != null ? !markerSymbol.equals(that.markerSymbol) : that.markerSymbol != null) return false;
		if (mpTermId != null ? !mpTermId.equals(that.mpTermId) : that.mpTermId != null) return false;
		if (mpTermName != null ? !mpTermName.equals(that.mpTermName) : that.mpTermName != null) return false;
		if (p_value != null ? !p_value.equals(that.p_value) : that.p_value != null) return false;
		if (parameterName != null ? !parameterName.equals(that.parameterName) : that.parameterName != null)
			return false;
		if (parameterStableId != null ? !parameterStableId.equals(that.parameterStableId) : that.parameterStableId != null)
			return false;
		if (parameterStableKey != null ? !parameterStableKey.equals(that.parameterStableKey) : that.parameterStableKey != null)
			return false;
		if (percentageChange != null ? !percentageChange.equals(that.percentageChange) : that.percentageChange != null)
			return false;
		if (phenotypingCenter != null ? !phenotypingCenter.equals(that.phenotypingCenter) : that.phenotypingCenter != null)
			return false;
		if (pipelineName != null ? !pipelineName.equals(that.pipelineName) : that.pipelineName != null) return false;
		if (pipelineStableId != null ? !pipelineStableId.equals(that.pipelineStableId) : that.pipelineStableId != null)
			return false;
		if (pipelineStableKey != null ? !pipelineStableKey.equals(that.pipelineStableKey) : that.pipelineStableKey != null)
			return false;
		if (procedureName != null ? !procedureName.equals(that.procedureName) : that.procedureName != null)
			return false;
		if (procedureStableId != null ? !procedureStableId.equals(that.procedureStableId) : that.procedureStableId != null)
			return false;
		if (procedureStableKey != null ? !procedureStableKey.equals(that.procedureStableKey) : that.procedureStableKey != null)
			return false;
		if (projectExternalId != null ? !projectExternalId.equals(that.projectExternalId) : that.projectExternalId != null)
			return false;
		if (projectFullname != null ? !projectFullname.equals(that.projectFullname) : that.projectFullname != null)
			return false;
		if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) return false;
		if (resourceFullname != null ? !resourceFullname.equals(that.resourceFullname) : that.resourceFullname != null)
			return false;
		if (resourceName != null ? !resourceName.equals(that.resourceName) : that.resourceName != null) return false;
		if (sex != null ? !sex.equals(that.sex) : that.sex != null) return false;
		if (statisticalMethod != null ? !statisticalMethod.equals(that.statisticalMethod) : that.statisticalMethod != null)
			return false;
		if (strainAccessionId != null ? !strainAccessionId.equals(that.strainAccessionId) : that.strainAccessionId != null)
			return false;
		if (strainName != null ? !strainName.equals(that.strainName) : that.strainName != null) return false;
		if (topLevelMpTermDefinition != null ? !topLevelMpTermDefinition.equals(that.topLevelMpTermDefinition) : that.topLevelMpTermDefinition != null)
			return false;
		if (topLevelMpTermId != null ? !topLevelMpTermId.equals(that.topLevelMpTermId) : that.topLevelMpTermId != null)
			return false;
		if (topLevelMpTermName != null ? !topLevelMpTermName.equals(that.topLevelMpTermName) : that.topLevelMpTermName != null)
			return false;
		if (topLevelMpTermSynonym != null ? !topLevelMpTermSynonym.equals(that.topLevelMpTermSynonym) : that.topLevelMpTermSynonym != null)
			return false;
		if (zygosity != null ? !zygosity.equals(that.zygosity) : that.zygosity != null) return false;

		return true;
	}


	@Override
	public int hashCode() {

		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (mpTermId != null ? mpTermId.hashCode() : 0);
		result = 31 * result + (mpTermName != null ? mpTermName.hashCode() : 0);
		result = 31 * result + (topLevelMpTermId != null ? topLevelMpTermId.hashCode() : 0);
		result = 31 * result + (topLevelMpTermName != null ? topLevelMpTermName.hashCode() : 0);
		result = 31 * result + (topLevelMpTermDefinition != null ? topLevelMpTermDefinition.hashCode() : 0);
		result = 31 * result + (topLevelMpTermSynonym != null ? topLevelMpTermSynonym.hashCode() : 0);
		result = 31 * result + (intermediateMpTermId != null ? intermediateMpTermId.hashCode() : 0);
		result = 31 * result + (intermediateMpTermName != null ? intermediateMpTermName.hashCode() : 0);
		result = 31 * result + (intermediateMpTermDefinition != null ? intermediateMpTermDefinition.hashCode() : 0);
		result = 31 * result + (intermediateMpTermSynonym != null ? intermediateMpTermSynonym.hashCode() : 0);
		result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
		result = 31 * result + (markerAccessionId != null ? markerAccessionId.hashCode() : 0);
		result = 31 * result + (colonyId != null ? colonyId.hashCode() : 0);
		result = 31 * result + (alleleName != null ? alleleName.hashCode() : 0);
		result = 31 * result + (alleleSymbol != null ? alleleSymbol.hashCode() : 0);
		result = 31 * result + (alleleAccessionId != null ? alleleAccessionId.hashCode() : 0);
		result = 31 * result + (strainName != null ? strainName.hashCode() : 0);
		result = 31 * result + (strainAccessionId != null ? strainAccessionId.hashCode() : 0);
		result = 31 * result + (phenotypingCenter != null ? phenotypingCenter.hashCode() : 0);
		result = 31 * result + (projectExternalId != null ? projectExternalId.hashCode() : 0);
		result = 31 * result + (projectName != null ? projectName.hashCode() : 0);
		result = 31 * result + (projectFullname != null ? projectFullname.hashCode() : 0);
		result = 31 * result + (resourceName != null ? resourceName.hashCode() : 0);
		result = 31 * result + (resourceFullname != null ? resourceFullname.hashCode() : 0);
		result = 31 * result + (sex != null ? sex.hashCode() : 0);
		result = 31 * result + (zygosity != null ? zygosity.hashCode() : 0);
		result = 31 * result + (pipelineName != null ? pipelineName.hashCode() : 0);
		result = 31 * result + (pipelineStableId != null ? pipelineStableId.hashCode() : 0);
		result = 31 * result + (pipelineStableKey != null ? pipelineStableKey.hashCode() : 0);
		result = 31 * result + (procedureName != null ? procedureName.hashCode() : 0);
		result = 31 * result + (procedureStableId != null ? procedureStableId.hashCode() : 0);
		result = 31 * result + (procedureStableKey != null ? procedureStableKey.hashCode() : 0);
		result = 31 * result + (parameterName != null ? parameterName.hashCode() : 0);
		result = 31 * result + (parameterStableId != null ? parameterStableId.hashCode() : 0);
		result = 31 * result + (parameterStableKey != null ? parameterStableKey.hashCode() : 0);
		result = 31 * result + (statisticalMethod != null ? statisticalMethod.hashCode() : 0);
		result = 31 * result + (percentageChange != null ? percentageChange.hashCode() : 0);
		result = 31 * result + (p_value != null ? p_value.hashCode() : 0);
		result = 31 * result + (effect_size != null ? effect_size.hashCode() : 0);
		result = 31 * result + (externalId != null ? externalId.hashCode() : 0);
		return result;
	}


	@Override
	public String toString() {

		return "GenotypePhenotypeDTO [id=" + id + ", mpTermId=" + mpTermId + ", mpTermName=" + mpTermName + ", topLevelMpTermId=" + topLevelMpTermId + ", topLevelMpTermName=" + topLevelMpTermName + ", topLevelMpTermDefinition=" + topLevelMpTermDefinition + ", topLevelMpTermSynonym=" + topLevelMpTermSynonym + ", intermediateMpTermId=" + intermediateMpTermId + ", intermediateMpTermName=" + intermediateMpTermName + ", intermediateMpTermDefinition=" + intermediateMpTermDefinition + ", intermediateMpTermSynonym=" + intermediateMpTermSynonym + ", markerSymbol=" + markerSymbol + ", markerAccessionId=" + markerAccessionId + ", colonyId=" + colonyId + ", alleleName=" + alleleName + ", alleleSymbol=" + alleleSymbol + ", alleleAccessionId=" + alleleAccessionId + ", strainName=" + strainName + ", strainAccessionId=" + strainAccessionId + ", phenotypingCenter=" + phenotypingCenter + ", projectExternalId=" + projectExternalId + ", projectName=" + projectName + ", projectFullname=" + projectFullname + ", resourceName=" + resourceName + ", resourceFullname=" + resourceFullname + ", sex=" + sex + ", zygosity=" + zygosity + ", pipelineName=" + pipelineName + ", pipelineStableId=" + pipelineStableId + ", pipelineStableKey=" + pipelineStableKey + ", procedureName=" + procedureName + ", procedureStableId=" + procedureStableId + ", procedureStableKey=" + procedureStableKey + ", parameterName=" + parameterName + ", parameterStableId=" + parameterStableId + ", parameterStableKey=" + parameterStableKey + ", p_value=" + p_value + ", effect_size=" + effect_size + ", externalId=" + externalId + "]";
	}

	
}
