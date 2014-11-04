/**
 * Copyright (c) 2014 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.solr.indexer.beans;

/**
 * @author Matt Pearce
 *
 */
public class ParamProcedurePipelineBean {

	private String parameterName;
	
	private String parameterStableId;
	
	private String parameterStableKey;
	
	private String procedureName;
	
	private String procedureStableId;
	
	private String procedureStableKey;
	
	private String pipelineName;
	
	private String pipelineStableId;

	private String pipelineStableKey;

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
	 * @return the parameterStableId
	 */
	public String getParameterStableId() {
		return parameterStableId;
	}

	/**
	 * @param parameterStableId the parameterStableId to set
	 */
	public void setParameterStableId(String parameterStableId) {
		this.parameterStableId = parameterStableId;
	}

	/**
	 * @return the parameterStableKey
	 */
	public String getParameterStableKey() {
		return parameterStableKey;
	}

	/**
	 * @param parameterStableKey the parameterStableKey to set
	 */
	public void setParameterStableKey(String parameterStableKey) {
		this.parameterStableKey = parameterStableKey;
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
	public String getProcedureStableKey() {
		return procedureStableKey;
	}

	/**
	 * @param procedureStableKey the procedureStableKey to set
	 */
	public void setProcedureStableKey(String procedureStableKey) {
		this.procedureStableKey = procedureStableKey;
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

	/**
	 * @return the pipelineStableKey
	 */
	public String getPipelineStableKey() {
		return pipelineStableKey;
	}

	/**
	 * @param pipelineStableKey the pipelineStableKey to set
	 */
	public void setPipelineStableKey(String pipelineStableKey) {
		this.pipelineStableKey = pipelineStableKey;
	}

}
