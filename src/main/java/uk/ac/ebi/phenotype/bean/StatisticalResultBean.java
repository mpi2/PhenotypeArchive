/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.bean;

import uk.ac.ebi.phenotype.pojo.StatisticalSignificance;
import uk.ac.ebi.phenotype.service.dto.StatisticalResultDTO;


/**
 * Lightweight representation of a statistical object containing 3 attributes, 
 * namely the pvalue, and effect size
 */

public class StatisticalResultBean implements StatisticalSignificance {

	private double pValue;
	private double effectSize;
	private String status;
	private String statisticalMethod;
	private String controlSex; // if relevant
	private String zygosity;
	private String parameterStableId;
	private String parameterName;
	private String procedureStableId;
	private String procedureName;
	Integer maleControls;
	Integer maleMutants;
	Integer femaleControls;
	Integer femaleMutants;
	String metadataGroup;
	
	private double colorIndex;
	
	/**
	 * @param pValue
	 * @param effectSize
	 * @param status
	 * @param statisticalMethod
	 */
	public StatisticalResultBean(
			double pValue, double effectSize,
			String status, String method,
			String controlSex, 
			String zygosity,
			int maleControls, int maleMutants,
			int femaleControls, int femaleMutants,
			String metadataGroup) {
		
		super();
		this.pValue = pValue;
		this.effectSize = effectSize;
		this.status = status;
		this.statisticalMethod = method;
		this.controlSex = controlSex;
		this.zygosity = zygosity;
		this.maleControls = maleControls;
		this.maleMutants = maleMutants;
		this.femaleControls = femaleControls;
		this.femaleMutants = femaleMutants;
		this.metadataGroup = metadataGroup;
		
	}

	public StatisticalResultBean(StatisticalResultDTO dto) {

		this.pValue = dto.getpValue();
		this.effectSize = dto.getEffectSize()!=null ? dto.getEffectSize() : 1;
		this.status = dto.getStatus()!=null ? dto.getStatus() : "no status found";
		this.statisticalMethod = dto.getStatisticalMethod();
		this.controlSex = dto.getSex();
		this.zygosity = dto.getZygosity();
		this.maleControls = dto.getMaleControlCount();
		this.maleMutants = dto.getMaleMutantCount();
		this.femaleControls = dto.getFemaleControlCount();
		this.femaleMutants = dto.getFemaleMutantCount();
		this.metadataGroup = dto.getMetadataGroup();
		this.parameterStableId = dto.getParameterStableId();
		this.parameterName = dto.getParameterName();
		this.procedureStableId = dto.getProcedureStableId();
		this.procedureName = dto.getProcedureName();
	}

	
	@Override
	public String toString() {

		return "StatisticalResultBean [pValue=" + pValue + ", effectSize=" + effectSize + ", status=" + status + ", statisticalMethod=" + statisticalMethod + ", controlSex=" + controlSex + ", zygosity=" + zygosity + ", parameterStableId=" + parameterStableId + ", parameterName=" + parameterName + ", procedureStableId=" + procedureStableId + ", procedureName=" + procedureName + ", maleControls=" + maleControls + ", maleMutants=" + maleMutants + ", femaleControls=" + femaleControls + ", femaleMutants=" + femaleMutants + ", metadataGroup=" + metadataGroup + ", colorIndex=" + colorIndex + "]";
	}

	public String getProcedureStableId() {
	
		return procedureStableId;
	}

	
	public void setProcedureStableId(String procedureStableId) {
	
		this.procedureStableId = procedureStableId;
	}

	
	public String getProcedureName() {
	
		return procedureName;
	}

	
	public void setProcedureName(String procedureName) {
	
		this.procedureName = procedureName;
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

	/**
	 * @return the pValue
	 */
	public double getpValue() {
		return pValue;
	}
	/**
	 * @param pValue the pValue to set
	 */
	public void setpValue(double pValue) {
		this.pValue = pValue;
	}
	/**
	 * @return the effectSize
	 */
	public double getEffectSize() {
		return effectSize;
	}
	/**
	 * @param effectSize the effectSize to set
	 */
	public void setEffectSize(double effectSize) {
		this.effectSize = effectSize;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the statisticalMethod
	 */
	public String getStatisticalMethod() {
		return statisticalMethod;
	}
	/**
	 * @param statisticalMethod the statisticalMethod to set
	 */
	public void setStatisticalMethod(String method) {
		this.statisticalMethod = method;
	}	
	
	/**
	 * @return the controlSex
	 */
	public String getControlSex() {
		return controlSex;
	}

	/**
	 * @param controlSex the controlSex to set
	 */
	public void setControlSex(String controlSex) {
		this.controlSex = controlSex;
	}	
	
	/**
	 * @return the zygosity
	 */
	public String getZygosity() {
		return zygosity;
	}

	/**
	 * @param zygosity the zygosity to set
	 */
	public void setZygosity(String zygosity) {
		this.zygosity = zygosity;
	}


	public Integer getMaleControls() {
		return maleControls;
	}


	public void setMaleControls(Integer maleControls) {
		this.maleControls = maleControls;
	}


	public Integer getMaleMutants() {
		return maleMutants;
	}


	public void setMaleMutants(Integer maleMutants) {
		this.maleMutants = maleMutants;
	}


	public Integer getFemaleControls() {
		return femaleControls;
	}


	public void setFemaleControls(Integer femaleControls) {
		this.femaleControls = femaleControls;
	}


	public Integer getFemaleMutants() {
		return femaleMutants;
	}


	public void setFemaleMutants(Integer femaleMutants) {
		this.femaleMutants = femaleMutants;
	}


	/**
	 * @return the colorIndex
	 */
	public double getColorIndex() {
		return colorIndex;
	}

	/**
	 * @param colorIndex the colorIndex to set
	 */
	public void setColorIndex(double colorIndex) {
		this.colorIndex = colorIndex;
	}
	
	/**
	 * @return the metadataGroup
	 */
	public String getMetadataGroup() {
		return metadataGroup;
	}

	/**
	 * @param metadataGroup the metadataGroup to set
	 */
	public void setMetadataGroup(String metadataGroup) {
		this.metadataGroup = metadataGroup;
	}

	/**
	 * Return a -Log10 value to generate a scale
	 * @return -Math.log10(pValue)
	 */
	public double getLogValue() {
		if (pValue < 1E-20) {
			return -Math.log10(1E-20);
		}
		return -Math.log10(pValue);
	}
	
	/**
	 * Check whether the statistical call worked or failed.
	 * @return a boolean indicated whether the status is equal to 'Success'
	 */
	public boolean getIsSuccessful() {
		return status!=null && status.equals("Success");
	}
	
	/**
	 * Effect size
	 * if both sex are affected equally, use genotype_parameter_estimate for effect size
	 * if male or female only, use gender_female_ko_estimate or gender_male_ko_estimate
	 * P-value:
	 * null_test_significance
	 */
}
