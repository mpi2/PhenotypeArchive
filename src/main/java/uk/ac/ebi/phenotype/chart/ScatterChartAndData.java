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
