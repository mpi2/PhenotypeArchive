package uk.ac.ebi.phenotype.stats.categorical;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;

/**
 * Class to hold all data, charts and tables pertaining to a Categorical Result Stats set so for male and female data combined
 * @author jwarren
 *
 */
public class CategoricalResultAndCharts {
	
	List<CategoricalChartDataObject> maleAndFemale=new ArrayList<CategoricalChartDataObject>();
	ExperimentDTO experiment;
	List<BiologicalModel> biologicalModels=new ArrayList<BiologicalModel>();
	private List<CategoricalResult> statsResults; 
	private String organisation="placeHolder";
	
	public List<CategoricalResult> getStatsResults() {
		return statsResults;
	}

	public void setStatsResults(List<? extends StatisticalResult> list) {
		this.statsResults = (List<CategoricalResult>) list;
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

	public String getOrganisation() {
		 return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation=organisation;
		
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
