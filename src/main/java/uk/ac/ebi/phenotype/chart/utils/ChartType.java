package uk.ac.ebi.phenotype.chart.utils;

public enum ChartType {
	
	UNIDIMENSIONAL_SCATTER_PLOT, UNIDIMENSIONAL_BOX_PLOT, UNIDIMENSIONAL_ABR;
	
	public String getName(){
		return this.toString();
	}
}
