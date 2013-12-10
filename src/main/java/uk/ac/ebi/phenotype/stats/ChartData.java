package uk.ac.ebi.phenotype.stats;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;

public class ChartData {
	BiologicalModel expBiologicalModel;
	private String chart;
	String organisation = "";
	private Float min = new Float(0);
	private Float max = new Float(1000000000);
	private String id;
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public BiologicalModel getExpBiologicalModel() {
		return expBiologicalModel;
	}

	public void setExpBiologicalModel(BiologicalModel expBiologicalModel) {
		this.expBiologicalModel = expBiologicalModel;
	}

	public Float getMin() {
		return min;
	}

	public void setMin(Float min) {
		this.min = min;
	}
	
	public void alterMinMax(double d, double e){
		String chartString = getChart();
		String newChartString = chartString.replace("min: 0", "min: "+d);
		newChartString = newChartString.replace("max: 2", "max: "+e);
		setChart(newChartString);
	}

	public Float getMax() {
		return max;
	}

	public void setMax(Float max) {
		this.max = max;
	}

	public String getChart() {
		return chart;
	}

	public void setChart(String chart) {
		this.chart = chart;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

}
