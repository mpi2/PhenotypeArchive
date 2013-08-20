package uk.ac.ebi.phenotype.stats.unidimensional;

import java.util.List;

import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.stats.ChartData;
import uk.ac.ebi.phenotype.stats.TableObject;

public class UnidimensionalDataSet {

	private List<ChartData> sexChartAndTables;
	public List<ChartData> getSexChartAndTables() {
		return sexChartAndTables;
	}
	public void setSexChartAndTables(List<ChartData> sexChartAndTables) {
		this.sexChartAndTables = sexChartAndTables;
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
	
}
