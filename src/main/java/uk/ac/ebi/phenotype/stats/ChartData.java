package uk.ac.ebi.phenotype.stats;

public class ChartData {
	private ExperimentDTO experiment;
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

	/**
	 * @return the experiment
	 */
	public ExperimentDTO getExperiment() {
		return experiment;
	}

	/**
	 * @param experiment the experiment to set
	 */
	public void setExperiment(ExperimentDTO experiment) {
		this.experiment = experiment;
	}

}
