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
public class PhenotypeCallSummaryBean {
	
	private String gfAcc;
	
	private String mpAcc;
	
	private String mpMgi;
	
	private String parameterId;

	private String procedureId;
	
	private String pipelineId;
	
	private String alleleAcc;
	
	private String strainAcc;
	
	private String paramProcPipelineId;

	/**
	 * @return the gfAcc
	 */
	public String getGfAcc() {
		return gfAcc;
	}

	/**
	 * @param gfAcc the gfAcc to set
	 */
	public void setGfAcc(String gfAcc) {
		this.gfAcc = gfAcc;
	}

	/**
	 * @return the mpAcc
	 */
	public String getMpAcc() {
		return mpAcc;
	}

	/**
	 * @param mpAcc the mpAcc to set
	 */
	public void setMpAcc(String mpAcc) {
		this.mpAcc = mpAcc;
	}

	/**
	 * @return the mpMgi
	 */
	public String getMpMgi() {
		return mpMgi;
	}

	/**
	 * @param mpMgi the mpMgi to set
	 */
	public void setMpMgi(String mpMgi) {
		this.mpMgi = mpMgi;
	}

	/**
	 * @return the parameterId
	 */
	public String getParameterId() {
		return parameterId;
	}

	/**
	 * @param parameterId the parameterId to set
	 */
	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	/**
	 * @return the procedureId
	 */
	public String getProcedureId() {
		return procedureId;
	}

	/**
	 * @param procedureId the procedureId to set
	 */
	public void setProcedureId(String procedureId) {
		this.procedureId = procedureId;
	}

	/**
	 * @return the pipelineId
	 */
	public String getPipelineId() {
		return pipelineId;
	}

	/**
	 * @param pipelineId the pipelineId to set
	 */
	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	/**
	 * @return the alleleAcc
	 */
	public String getAlleleAcc() {
		return alleleAcc;
	}

	/**
	 * @param alleleAcc the alleleAcc to set
	 */
	public void setAlleleAcc(String alleleAcc) {
		this.alleleAcc = alleleAcc;
	}

	/**
	 * @return the strainAcc
	 */
	public String getStrainAcc() {
		return strainAcc;
	}

	/**
	 * @param strainAcc the strainAcc to set
	 */
	public void setStrainAcc(String strainAcc) {
		this.strainAcc = strainAcc;
	}

	/**
	 * @return the paramProcPipelineId
	 */
	public String getParamProcPipelineId() {
		return paramProcPipelineId;
	}

	/**
	 * @param paramProcPipelineId the paramProcPipelineId to set
	 */
	public void setParamProcPipelineId(String paramProcPipelineId) {
		this.paramProcPipelineId = paramProcPipelineId;
	}
	
}
