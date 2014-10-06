package uk.ac.ebi.phenotype.chart.utils;

public enum ChartType {
	
	UNIDIMENSIONAL_SCATTER_PLOT, UNIDIMENSIONAL_BOX_PLOT, UNIDIMENSIONAL_ABR_PLOT, CATEGORICAL_STACKED_COLUMN, TIME_SERIES_LINE, PIE;
	
	public String getName(){
		return this.toString();
	}
}
