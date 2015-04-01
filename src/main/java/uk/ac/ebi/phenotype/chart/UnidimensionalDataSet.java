package uk.ac.ebi.phenotype.chart;

import java.util.List;

import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;

/**
 * UnidimensionalDataSet should represent one experimentDTO i.e. both sexes with one table or one sex and one table
 * @author jwarren
 *
 */
public class UnidimensionalDataSet {

	private String experimentId = ""; // experimentId should be distinct per
										// UnidimensionalSet
	private ExperimentDTO experiment;
	private String organisation = "";
	private Float min;
	private Float max;
	

	public String getOrganisation() {

		return organisation;
	}


	public void setOrganisation(String organisation) {

	this.organisation = organisation;
	}
	public String getExperimentId() {
		return experimentId;
	}
	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}

	private ChartData chartData;

	
	public ChartData getChartData() {
		return chartData;
	}
	public void setChartData(ChartData chartData) {
		this.chartData = chartData;
	}
	public List<UnidimensionalResult> getAllUnidimensionalResults() {
		return allUnidimensionalResults;
	}
	public void setAllUnidimensionalResults(
			List<UnidimensionalResult> allUnidimensionalResults) {
		this.allUnidimensionalResults = allUnidimensionalResults;
	}

	private List<UnidimensionalResult> allUnidimensionalResults;
	private List<UnidimensionalStatsObject> statsObjects;
	public List<UnidimensionalStatsObject> getStatsObjects() {
		return statsObjects;
	}
	public void setStatsObjects(List<UnidimensionalStatsObject> statsObjects) {
		this.statsObjects = statsObjects;
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
	
}
