package uk.ac.ebi.phenotype.stats.unidimensional;

import java.util.List;

import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.stats.ChartData;
import uk.ac.ebi.phenotype.stats.TableObject;

/**
 * UnidimensionalDataSet should represent one experimentDTO i.e. both sexes with one table or one sex and one table
 * @author jwarren
 *
 */
public class UnidimensionalDataSet {
	 private String experimentId=""; //experimentId should be distinct per UnidimensionalSet
	 private String organisation="";
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
