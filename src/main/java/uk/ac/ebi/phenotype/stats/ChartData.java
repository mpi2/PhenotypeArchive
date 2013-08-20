package uk.ac.ebi.phenotype.stats;

public class ChartData {
	
	private Float min=new Float(0);
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
	private Float max=new Float(1000000000);

 private String chart;
 public String getChart() {
	return chart;
}
public void setChart(String chart) {
	this.chart = chart;
}
 
}
