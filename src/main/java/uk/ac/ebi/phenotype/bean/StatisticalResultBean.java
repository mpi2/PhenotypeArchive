/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.bean;

/**
 * Lightweight representation of a statistical object containing 3 attributes, 
 * namely the pvalue, and effect size
 */

public class StatisticalResultBean {

	private double pValue;
	private Double effectSize;
	private String status;
	private String method;
	private String controlSex; // if relevant
	int maleControls; 
	int maleMutants;
	int femaleControls;
	int femaleMutants;
	
	private double colorIndex;
	
	/**
	 * @param pValue
	 * @param effectSize
	 * @param status
	 * @param method
	 */
	public StatisticalResultBean(
			double pValue, Double effectSize,
			String status, String method,
			String controlSex, 
			int maleControls, int maleMutants,
			int femaleControls, int femaleMutants) {
		
		super();
		this.pValue = pValue;
		this.effectSize = effectSize;
		this.status = status;
		this.method = method;
		this.controlSex = controlSex;
		this.maleControls = maleControls;
		this.maleMutants = maleMutants;
		this.femaleControls = femaleControls;
		this.femaleMutants = femaleMutants;
		
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
	public Double getEffectSize() {
		return effectSize;
	}
	/**
	 * @param effectSize the effectSize to set
	 */
	public void setEffectSize(Double effectSize) {
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
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
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
	 * @return the maleControls
	 */
	public int getMaleControls() {
		return maleControls;
	}

	/**
	 * @param maleControls the maleControls to set
	 */
	public void setMaleControls(int maleControls) {
		this.maleControls = maleControls;
	}

	/**
	 * @return the maleMutants
	 */
	public int getMaleMutants() {
		return maleMutants;
	}

	/**
	 * @param maleMutants the maleMutants to set
	 */
	public void setMaleMutants(int maleMutants) {
		this.maleMutants = maleMutants;
	}

	/**
	 * @return the femaleControls
	 */
	public int getFemaleControls() {
		return femaleControls;
	}

	/**
	 * @param femaleControls the femaleControls to set
	 */
	public void setFemaleControls(int femaleControls) {
		this.femaleControls = femaleControls;
	}

	/**
	 * @return the femaleMutants
	 */
	public int getFemaleMutants() {
		return femaleMutants;
	}

	/**
	 * @param femaleMutants the femaleMutants to set
	 */
	public void setFemaleMutants(int femaleMutants) {
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
