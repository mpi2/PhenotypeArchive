package uk.ac.ebi.phenotype.stats.categorical;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;

/**
 * Class to hold all data, charts and tables pertaining to a Categorical Result Stats set so for male and female data combined
 * @author jwarren
 *
 */
public class CategoricalResultAndCharts {
	
	List<CategoricalChartDataObject> maleAndFemale=new ArrayList<CategoricalChartDataObject>();
	List<BiologicalModel> biologicalModels=new ArrayList<BiologicalModel>();
	private List<CategoricalResult> statsResults; 
	
	public List<CategoricalResult> getStatsResults() {
		return statsResults;
	}

	public void setStatsResults(List<CategoricalResult> statsResults) {
		this.statsResults = statsResults;
	}

	public List<CategoricalChartDataObject> getMaleAndFemale() {
		return maleAndFemale;
	}

	public void setMaleAndFemale(List<CategoricalChartDataObject> maleAndFemale) {
		this.maleAndFemale = maleAndFemale;
	}


	public List<BiologicalModel> getBiologicalModels() {
		return biologicalModels;
	}

	public void setBiologicalModels(List<BiologicalModel> biologicalModels) {
		this.biologicalModels = biologicalModels;
	}

	public void addBiologicalModel(BiologicalModel biologicalModel) {
		this.biologicalModels.add(biologicalModel);
	}

	public void add(CategoricalChartDataObject chartData) {
		this.maleAndFemale.add(chartData);
		
	}


}
