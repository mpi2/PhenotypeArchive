package uk.ac.ebi.phenotype.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

public class ViabilityDTO {
	//
	
	Map<String, ObservationDTO> paramNameToDataPoint = new HashMap<>();

	private String totalChart = "";
	private String maleChart = "";
	private String femaleChart = "";
	String category = "";// should get set to e.g. Homozygous - Viable


	public Map<String, ObservationDTO> getParamNameToDataPoint() {

		return paramNameToDataPoint;
	}


	public void setParamNameToDataPoint(Map<String, ObservationDTO> paramNameToDataPoint) {

		this.paramNameToDataPoint = paramNameToDataPoint;
	}


	public String getCategory() {

		return category;
	}


	public void setCategory(String category) {

		this.category = category;
	}


	public String getTotalChart() {

		return totalChart;
	}


	public String getMaleChart() {

		return maleChart;
	}


	public String getFemaleChart() {

		return femaleChart;
	}


	public void setTotalChart(String totalChart) {

		this.totalChart = totalChart;
	}


	public void setMaleChart(String maleChart) {

		this.maleChart = maleChart;
	}


	public void setFemaleChart(String femaleChart) {

		this.femaleChart = femaleChart;
	}

}
