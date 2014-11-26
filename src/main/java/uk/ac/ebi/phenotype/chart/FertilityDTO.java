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