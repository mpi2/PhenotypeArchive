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
package uk.ac.ebi.phenotype.chart;

import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

/**
 * class to hold the categorical data with associated meta data and stats so we
 * keep things neat and tidy and don't have long list of parameters and or
 * arrays that can get mixed up
 * 
 * @author jwarren
 * 
 */
public class CategoricalDataObject {
	private String name = "";
	
	private CategoricalResult result;
	

	public CategoricalResult getResult() {
		return result;
	}

	public void setResult(CategoricalResult result) {
		this.result = result;
	}

	private Long count=new Long(0);
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getpValue() {
		return this.pValue;
	}

	public void setpValue(double pValue) {
		this.pValue = pValue;
	}

	public Double getMaxEffect() {
		return maxEffect;
	}

	public void setMaxEffect(double maxEffect) {
		this.maxEffect = maxEffect;
	}

	private Double pValue;
	private Double maxEffect;
	private String category="";

	public String getCategory() {
		return category;
	}

	public CategoricalDataObject(String name, double pValue, double maxEffect) {
		this.name = name;
		this.pValue = pValue;
		this.maxEffect = maxEffect;
	}

	public CategoricalDataObject(){
		
	}
	
	public String toString(){
		String name=this.getName();
		String count="";
				if(this.count!=null){
					count=Long.toString(this.count);
				}
		String pValue="";
		if(this.pValue!=null){
			pValue=Double.toString(this.pValue);
		}
		String maxEffect="";
		if(this.maxEffect!=null){
			maxEffect=Double.toString(this.maxEffect);
		}
	
		String string="name="+name+" category="+category+" count="+count+" pValue="+pValue+" maxEffect="+maxEffect;
		return string;
	}

	public void setCategory(String category) {
		this.category=category;
		
	}
	
}
