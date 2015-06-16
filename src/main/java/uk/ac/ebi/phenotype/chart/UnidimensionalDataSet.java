/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
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

	private String experimentId = ""; 
	private String title;
	private String subtitle;
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

	public String getTitle() {
	
		return title;
	}
	
	public void setTitle(String title) {
	
		this.title = title;
	}
	
	public String getSubtitle() {
	
		return subtitle;
	}
	
	public void setSubtitle(String subtitle) {
	
		this.subtitle = subtitle;
	}
	
}
