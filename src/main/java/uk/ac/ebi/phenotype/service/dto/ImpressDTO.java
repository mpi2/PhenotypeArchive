package uk.ac.ebi.phenotype.service.dto;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.beans.Field;


public class ImpressDTO {
	public static final String PARAMETER_ID = "parameter_id" ;
	public static final String DATA_TYPE = "dataType" ;
	public static final String PARAMETER_STABLE_ID = "parameter_stable_id" ;
	public static final String PARAMETER_STABLE_KEY = "parameter_stable_key" ;
	public static final String PARAMETER_NAME = "parameter_name" ;
	public static final String PROCEDURE_ID = "procedure_id" ;
	public static final String PROCEDURE_STABLE_ID = "procedure_stable_id" ;
	public static final String PROCEDURE_STABLE_KEY = "procedure_stable_key" ;
	public static final String PROCEDURE_NAME = "procedure_name" ;
	public static final String PIPELINE_NAME = "pipeline_name" ;
	public static final String PIPELINE_ID = "pipeline_id" ;
	public static final String PIPELINE_STABLE_KEY = "pipeline_stable_key" ;
	public static final String PIPELINE_STABLE_ID = "pipeline_stable_id" ;

	@Field(PARAMETER_ID)
	int parameter_id;
	@Field(DATA_TYPE)
	String dataType;
	@Field(PARAMETER_STABLE_ID)
	String parameter_stable_id;
	@Field(PARAMETER_STABLE_KEY)
	int parameterStableKey;
	@Field(PARAMETER_NAME)
	String parameterName;
	@Field(PROCEDURE_ID)
	int procedureId;
	@Field(PROCEDURE_STABLE_ID)
	String procedureStableId;
	@Field(PROCEDURE_STABLE_KEY)
	int procedureStableKey;
	@Field(PROCEDURE_NAME)
	String procedureName;
	@Field(PIPELINE_NAME)
	String pipelineName;
	@Field(PIPELINE_ID)
	int pipelineId;
	@Field(PIPELINE_STABLE_KEY)
	int pipelineStableKey;
	@Field(PIPELINE_STABLE_ID)
	String pipelineStableId;
	
	String drupalBaseUrl ;
	
	public String getProcedureUrl(String drupalBaseUrl){
		return drupalBaseUrl + "/impress/impress/displaySOP/" + procedureStableKey;
	}
	
	public String getPipelineUrl (String drupalBaseUrl){
		return drupalBaseUrl + "/impress/procedures/" + pipelineStableKey;
	}
	

	public String getProcedureUrl(){
		return drupalBaseUrl + "/impress/impress/displaySOP/" + procedureStableKey;
	}
	
	public String getPipelineUrl (){
		return drupalBaseUrl + "/impress/procedures/" + pipelineStableKey;
	}
	
	public void setDrupalBaseUrl(String url){
		drupalBaseUrl = url;
	}
	
	
	/**
	 * @return the parameter_id
	 */
	public int getParameter_id() {
	
		return parameter_id;
	}
	
	/**
	 * @param parameter_id the parameter_id to set
	 */
	public void setParameter_id(int parameter_id) {
	
		this.parameter_id = parameter_id;
	}
	
	/**
	 * @return the dataType
	 */
	public String getDataType() {
	
		return dataType;
	}
	
	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
	
		this.dataType = dataType;
	}
	
	/**
	 * @return the parameter_stable_id
	 */
	public String getParameter_stable_id() {
	
		return parameter_stable_id;
	}
	
	/**
	 * @param parameter_stable_id the parameter_stable_id to set
	 */
	public void setParameter_stable_id(String parameter_stable_id) {
	
		this.parameter_stable_id = parameter_stable_id;
	}
	
	/**
	 * @return the parameterStableKey
	 */
	public int getParameterStableKey() {
	
		return parameterStableKey;
	}
	
	/**
	 * @param parameterStableKey the parameterStableKey to set
	 */
	public void setParameterStableKey(int parameterStableKey) {
	
		this.parameterStableKey = parameterStableKey;
	}
	
	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
	
		return parameterName;
	}
	
	/**
	 * @param parameterName the parameterName to set
	 */
	public void setParameterName(String parameterName) {
	
		this.parameterName = parameterName;
	}
	
	/**
	 * @return the procedureId
	 */
	public int getProcedureId() {
	
		return procedureId;
	}
	
	/**
	 * @param procedureId the procedureId to set
	 */
	public void setProcedureId(int procedureId) {
	
		this.procedureId = procedureId;
	}
	
	/**
	 * @return the procedureStableId
	 */
	public String getProcedureStableId() {
	
		return procedureStableId;
	}
	
	/**
	 * @param procedureStableId the procedureStableId to set
	 */
	public void setProcedureStableId(String procedureStableId) {
	
		this.procedureStableId = procedureStableId;
	}
	
	/**
	 * @return the procedureStableKey
	 */
	public int getProcedureStableKey() {
	
		return procedureStableKey;
	}
	
	/**
	 * @param procedureStableKey the procedureStableKey to set
	 */
	public void setProcedureStableKey(int procedureStableKey) {
	
		this.procedureStableKey = procedureStableKey;
	}
	
	/**
	 * @return the procedureName
	 */
	public String getProcedureName() {
	
		return procedureName;
	}
	
	/**
	 * @param procedureName the procedureName to set
	 */
	public void setProcedureName(String procedureName) {
	
		this.procedureName = procedureName;
	}
	
	/**
	 * @return the pipelineName
	 */
	public String getPipelineName() {
	
		return pipelineName;
	}
	
	/**
	 * @param pipelineName the pipelineName to set
	 */
	public void setPipelineName(String pipelineName) {
	
		this.pipelineName = pipelineName;
	}
	
	/**
	 * @return the pipelineId
	 */
	public int getPipelineId() {
	
		return pipelineId;
	}
	
	/**
	 * @param pipelineId the pipelineId to set
	 */
	public void setPipelineId(int pipelineId) {
	
		this.pipelineId = pipelineId;
	}
	
	/**
	 * @return the pipelineStableKey
	 */
	public int getPipelineStableKey() {
	
		return pipelineStableKey;
	}
	
	/**
	 * @param pipelineStableKey the pipelineStableKey to set
	 */
	public void setPipelineStableKey(int pipelineStableKey) {
	
		this.pipelineStableKey = pipelineStableKey;
	}
	
	/**
	 * @return the pipelineStableId
	 */
	public String getPipelineStableId() {
	
		return pipelineStableId;
	}
	
	/**
	 * @param pipelineStableId the pipelineStableId to set
	 */
	public void setPipelineStableId(String pipelineStableId) {
	
		this.pipelineStableId = pipelineStableId;
	}
	
	
	
	
}
