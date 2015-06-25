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

import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.phenotype.service.dto.ObservationDTO;


public class FertilityDTO {

	private String category;
	private String totalChart;

	
	public String getCategory() {
	
		return category;
	}



	
	public String getTotalChart() {
	
		return totalChart;
	}



	public void setCategory(String category) {

		this.category=category;
		
	}

	Map<String, ObservationDTO> paramStableIdToObservation= new HashMap<>();

	
	public Map<String, ObservationDTO> getParamStableIdToObservation() {
	
		return paramStableIdToObservation;
	}


	
	public void setParamStableIdToObservation(Map<String, ObservationDTO> paramStableIdToObservation) {
	
		this.paramStableIdToObservation = paramStableIdToObservation;
	}



	public void setTotalChart(String totalChart) {

		this.totalChart=totalChart;
		
	}


}
