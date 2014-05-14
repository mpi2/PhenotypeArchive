package uk.ac.ebi.phenotype.stats.graphs;

import java.util.List;

import uk.ac.ebi.phenotype.stats.unidimensional.UnidimensionalStatsObject;

public class ScatterChartAndData {
	
	String chart="";
	private List<UnidimensionalStatsObject> unidimensionalStatsObjects;

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
