package uk.ac.ebi.phenotype.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViabilityDTO {

	private String totalChart = "";
	private String maleChart="";
	private String femaleChart="";
	String category="";//should get set to e.g. Homozygous - Viable

	
	
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
		this.totalChart=totalChart;
	}


	public void setMaleChart(String maleChart) {
		this.maleChart = maleChart;
	}
	
	public void setFemaleChart(String femaleChart){
		this.femaleChart=femaleChart;
	}

}
