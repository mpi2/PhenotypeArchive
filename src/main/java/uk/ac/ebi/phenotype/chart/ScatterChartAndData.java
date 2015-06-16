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

import java.util.List;

public class ScatterChartAndData {
	
	String chart="";
	Float min;
	Float max;
	
	private List<UnidimensionalStatsObject> unidimensionalStatsObjects;
	
	public Float getMin() {
	
		return min;
	}
	
	public void setMin(Float min) {
	
		this.min = min;
	}
	
	public Float getMax() {
	
		return max;
	}
	
	public void setMax(Float max) {
	
		this.max = max;
	}

	public List<UnidimensionalStatsObject> getUnidimensionalStatsObjects() {

		return unidimensionalStatsObjects;
	}

	public String getChart() {
		
		return chart;
	}

	public void setChart(String chart) {
		
		this.chart = chart;
	}

	//this needs to be set to show tables under unidimensional data sets with the scatter plot
	public void setUnidimensionalStatsObjects(
			List<UnidimensionalStatsObject> unidimensionalStatsObjects) {
		this.unidimensionalStatsObjects=unidimensionalStatsObjects;
		
	}
	

}
