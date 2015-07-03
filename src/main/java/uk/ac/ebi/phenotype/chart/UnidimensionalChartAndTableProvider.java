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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.hibernate.cfg.annotations.Nullability;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.imits.StatusConstants;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.ImpressService;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

@Service
public class UnidimensionalChartAndTableProvider {

	private static final Logger logger = Logger.getLogger(UnidimensionalChartAndTableProvider.class);

	private String axisFontSize = "15";
	@Autowired
	PhenotypePipelineDAO ppDAO;
	
	@Autowired
	ImpressService impressService;

	/**
	 * return one unidimensional data set per experiment - one experiment should
	 * have one or two graphs corresponding to sex and a stats result for one
	 * table at the bottom
	 * 
	 * @param chartId
	 * @param zyList
	 * @param boxOrScatter
	 * @param byMouseId
	 * @param symbol
	 * @param allelicCompositionString
	 * @param geneticBackgroundString
	 * @param parameter
	 * @param acc
	 * @param model
	 * @param experimentList
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public UnidimensionalDataSet doUnidimensionalData(ExperimentDTO experiment, String chartId, Parameter parameter, ChartType boxOrScatter, Boolean byMouseId, String yAxisTitle, BiologicalModel expBiologicalModel)
	throws SQLException, IOException, URISyntaxException {

		ChartData chartAndTable = null;
		List<UnidimensionalDataSet> unidimensionalDataSets = new ArrayList<UnidimensionalDataSet>();

		// get control data
		List<UnidimensionalResult> allUnidimensionalResults = new ArrayList<UnidimensionalResult>();
		UnidimensionalDataSet unidimensionalDataSet = new UnidimensionalDataSet();
		unidimensionalDataSet.setExperiment(experiment);
		unidimensionalDataSet.setOrganisation(experiment.getOrganisation());
		unidimensionalDataSet.setExperimentId(experiment.getExperimentId());
		List<UnidimensionalStatsObject> unidimensionalStatsObjects = new ArrayList<>();

		// category e.g normal, abnormal
		Map<SexType, List<List<Float>>> genderAndRawDataMap = new HashMap<SexType, List<List<Float>>>();
		List<ChartsSeriesElement> chartsSeriesElementsList = new ArrayList<ChartsSeriesElement>();
		for (SexType sexType : experiment.getSexes()) {
			List<List<Float>> rawData = new ArrayList<List<Float>>();
			List<Float> dataFloats = new ArrayList<>();
			for (ObservationDTO control : experiment.getControls(sexType)) {

				Float dataPoint = control.getDataPoint();
				dataFloats.add(dataPoint);
			}

			ChartsSeriesElement tempElement = new ChartsSeriesElement();
			tempElement.setSexType(sexType);
			tempElement.setZygosityType(null);
			tempElement.setOriginalData(dataFloats);
			chartsSeriesElementsList.add(tempElement);

			for (ZygosityType zType : experiment.getZygosities()) {

				List<Float> mutantCounts = new ArrayList<Float>();
				Set<ObservationDTO> expObservationsSet = Collections.emptySet();
				expObservationsSet = experiment.getMutants(sexType, zType);
				
				for (ObservationDTO expDto : expObservationsSet) {
					Float dataPoint = expDto.getDataPoint();
					mutantCounts.add(dataPoint);
				}
				ChartsSeriesElement tempElementExp = new ChartsSeriesElement();
				tempElementExp.setSexType(sexType);
				tempElementExp.setZygosityType(zType);
				tempElementExp.setOriginalData(mutantCounts);
				chartsSeriesElementsList.add(tempElementExp);
			}

			genderAndRawDataMap.put(sexType, rawData);
		}
		
		List<UnidimensionalStatsObject> unidimensionalStatsObject = createUnidimensionalStatsObjects(experiment, parameter, expBiologicalModel);

		unidimensionalStatsObjects.addAll(unidimensionalStatsObject);
		Map <String, Float> boxMinMax = ChartUtils.getMinMaxXAxis(chartsSeriesElementsList, experiment);
		chartAndTable = processChartData(chartId, boxMinMax.get("min"), boxMinMax.get("max"), parameter, experiment, yAxisTitle, chartsSeriesElementsList);
		String title = "<span data-parameterStableId=\"" + parameter.getStableId() + "\">" + parameter.getName() + "</span>";
		Procedure proc = ppDAO.getProcedureByStableId(experiment.getProcedureStableId()) ;
		String procedureDescription = "";
		if (proc != null) {
			procedureDescription = String.format("<a href=\"%s\">%s</a>", impressService.getProcedureUrlByKey(((Integer)proc.getStableKey()).toString()), proc.getName());
		}
		
		unidimensionalDataSet.setChartData(chartAndTable);
		unidimensionalDataSet.setAllUnidimensionalResults(allUnidimensionalResults);
		unidimensionalDataSet.setStatsObjects(unidimensionalStatsObjects);
		unidimensionalDataSets.add(unidimensionalDataSet);
		unidimensionalDataSet.setMin(boxMinMax.get("min"));
		unidimensionalDataSet.setMax(boxMinMax.get("max"));
		unidimensionalDataSet.setTitle(title);
		unidimensionalDataSet.setSubtitle(procedureDescription);
		return unidimensionalDataSet;

	}


	public static List<UnidimensionalStatsObject> createUnidimensionalStatsObjects(ExperimentDTO experiment, Parameter parameter, BiologicalModel expBiologicalModel) {

		Map<String, String> usefulStrings = GraphUtils.getUsefulStrings(expBiologicalModel);
		List<UnidimensionalStatsObject> unidimensionalStatsObject = produceUnidimensionalStatsData(parameter, experiment, usefulStrings.get("allelicComposition"), usefulStrings.get("symbol"), usefulStrings.get("geneticBackground"));
		return unidimensionalStatsObject;
	}


	/**
	 * 
	 * @param chartsSeriesElementsList
	 * @param parameterUnit
	 * @param xAxisCategoriesList
	 *            - bare categories from database e.g. WT, HOM
	 * @param continuousBarCharts
	 */
	private ChartData processChartData(String chartId, Float yMin, Float yMax,Parameter parameter, ExperimentDTO experiment, String yAxisTitle, List<ChartsSeriesElement> chartsSeriesElementsList) {

		String chartString = createContinuousBoxPlotChartsString(chartId, yMin, yMax, parameter, yAxisTitle, chartsSeriesElementsList, experiment);
		ChartData cNTable = new ChartData();
		cNTable.setChart(chartString);
		return cNTable;
	}


	/**
	 * 
	 * @param parameter
	 *            .getStableId() main title of the graph
	 * @param yAxisTitle
	 *            - unit of measurement - how to get this from the db?
	 * @param sexAndScatterMap
	 * @param chartsSeriesElementsList
	 * @param xAisxCcategoriesList
	 *            e.g. WT, WT, HOM, HOM for each column to be displayed
	 * @return
	 */
	private String createContinuousBoxPlotChartsString(String experimentNumber, Float yMin, Float yMax,Parameter parameter, String yAxisTitle, 
		List<ChartsSeriesElement> chartsSeriesElementsList, ExperimentDTO experiment) {

		JSONArray categories = new JSONArray();
		String boxPlotObject = "";

		String seriesData = "";
		int decimalPlaces = ChartUtils.getDecimalPlaces(experiment);
		int column = 0;

		
		Procedure proc = ppDAO.getProcedureByStableId(experiment.getProcedureStableId()) ;
		String procedureDescription = "";
		if (proc != null) {
			procedureDescription = String.format("<a href=\"%s\">%s</a>", impressService.getProcedureUrlByKey(((Integer)proc.getStableKey()).toString()), proc.getName());
		}
		
		for (ChartsSeriesElement chartsSeriesElement : chartsSeriesElementsList) {
			// fist get the raw data for each column (only one column per data
			// set at the moment as we will create both the scatter and boxplots
			// here
			String categoryString = WordUtils.capitalize(chartsSeriesElement.getSexType().toString()) + " " + WordUtils.capitalize(chartsSeriesElement.getControlOrZygosityString());
			categories.put(categoryString);
			List<Float> listOfFloats = chartsSeriesElement.getOriginalData();
			
			PercentileComputation pc = new PercentileComputation(listOfFloats);

			List<Float> wt1 = new ArrayList<Float>();
			if (listOfFloats.size() > 0) {
				double Q1 = ChartUtils.getDecimalAdjustedFloat(new Float(pc.getLowerQuartile()), decimalPlaces);
				double Q3 = ChartUtils.getDecimalAdjustedFloat(new Float(pc.getUpperQuartile()), decimalPlaces);
				double IQR = Q3 - Q1;

				Float minIQR = ChartUtils.getDecimalAdjustedFloat(new Float(Q1 - (1.5 * IQR)), decimalPlaces);
				wt1.add(minIQR);// minimum
				Float q1 = new Float(Q1);
				wt1.add(q1);// lower quartile

				Float decFloat = ChartUtils.getDecimalAdjustedFloat(new Float(pc.getMedian()), decimalPlaces);
				wt1.add(decFloat);// median
				Float q3 = new Float(Q3);
				wt1.add(q3);// upper quartile
				Float maxIQR = ChartUtils.getDecimalAdjustedFloat(new Float(Q3 + (1.5 * IQR)), decimalPlaces);
				wt1.add(maxIQR);// maximumbs.
				chartsSeriesElement.setBoxPlotArray(new JSONArray(wt1));
			}

			JSONArray boxPlot2DData = chartsSeriesElement.getBoxPlotArray();
			if (boxPlot2DData == null) {
				System.err.println("error no boxplot data for this chartSeriesElemen=" + chartsSeriesElement.getName());
				boxPlot2DData = new JSONArray();
			}

			String columnPadding = "";
			for (int i = 0; i < column; i++) {
				columnPadding += "[], ";
			}
			
			String observationsString = "[" + columnPadding + boxPlot2DData.toString() + "]";
			String color = ChartColors.getMutantColor(ChartColors.alphaOpaque);
			if (chartsSeriesElement.getControlOrZygosityString().equals("WT")) {
				color = ChartColors.getWTColor(ChartColors.alphaTranslucid70);
			}

			boxPlotObject = "{" + " color: " + color + " ," + ""
				+ " name: 'Observations', data:" + observationsString + ", "
				+ " tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  }";

			seriesData += boxPlotObject + ",";
			column++;
		}

		// loop over the chartSeries data and create scatters for each
		for (ChartsSeriesElement chartsSeriesElement : chartsSeriesElementsList) {
			String categoryString = WordUtils.capitalize(chartsSeriesElement.getSexType().toString()) + " " + WordUtils.capitalize(chartsSeriesElement.getControlOrZygosityString());

			// for the scatter loop over the original data and assign a column
			// as the first element for each array

			List<Float> originalDataFloats = chartsSeriesElement.getOriginalData();
			JSONArray scatterJArray = new JSONArray();
			categories.put(categoryString);
			for (Float data : originalDataFloats) {
				JSONArray array = new JSONArray();
				array.put(column);
				array.put(data);
				scatterJArray.put(array);
			}			
			column++;
		}
		
		List<String> colors = ChartColors.getFemaleMaleColorsRgba(ChartColors.alphaOpaque);
		String chartString = " chart = new Highcharts.Chart({ " + " colors:" + colors
			+ ", chart: { type: 'boxplot', renderTo: 'chart" + experimentNumber + "'},  "
			+ " tooltip: { formatter: function () { if(typeof this.point.high === 'undefined')"
			+ "{ return '<b>Observation</b><br/>' + this.point.y; } "
			+ "else { return '<b>Genotype: ' + this.key + '</b>"
			+ "<br/>UQ + 1.5 * IQR: ' + this.point.options.high + '"
			+ "<br/>Upper Quartile: ' + this.point.options.q3 + '"
			+ "<br/>Median: ' + this.point.options.median + '"
			+ "<br/>Lower Quartile: ' + this.point.options.q1 +'"
			+ "<br/>LQ - 1.5 * IQR: ' + this.point.low"
			+ "; } } }    ,"
			+ " title: {  text: 'Boxplot of the data', useHTML:true } , "
			+ " credits: { enabled: false },  "
			+ " legend: { enabled: false }, "
			+ " xAxis: { categories:  " + categories + ","
			+ " labels: { "
			+ "           rotation: -45, "
			+ "           align: 'right', "
			+ "           style: { "
			+ "              fontSize: '15px',"
			+ "              fontFamily: 'Verdana, sans-serif'"
			+ "         } "
			+ "     }, "
			+ " }, \n" 
			+ " plotOptions: {" + "series:" + "{ groupPadding: 0.25, pointPadding: -0.5 }" + "}," 
			+ " yAxis: { " + "max: " + yMax + ",  min: " + yMin + "," + "labels: { },title: { text: '" + yAxisTitle + "' }, tickAmount: 5 }, " 
			+ "\n series: [" + seriesData + "] }); });";

		return chartString;
	}


	public ChartData getHistogram(List<String> labels, List<Double> values, String title) {

		double min = 0;
		for (double val : values)
			if (val < min) min = val;
		String chartId = "histogram" + values.hashCode();
		String yTitle = "Number of lines";
		String javascript = "$(function () {    var chart; $(document).ready(function() {chart = new Highcharts.Chart({ chart: {  type: 'column' , renderTo: '" + chartId + "'}," + " title: { text: '" + title + "' },  subtitle: {   text: '' }, " + " xAxis: { categories: " + labels + " }," + " yAxis: { min: " + min + ",  title: {  text: '" + yTitle + "'  }   }," + " tooltip: {" + "   headerFormat: '<span style=\"font-size:10px\">{point.key}</span><table>'," + "  pointFormat: '<tr><td style=\"color:{series.color};padding:0\">{series.name}: </td>' +" + "     '<td style=\"padding:0\"><b>{point.y:.1f} mm</b></td></tr>'," + " footerFormat: '</table>', shared: true,  useHTML: true  }, " + "  plotOptions: {   column: {  pointPadding: 0.2,  borderWidth: 0  }  }," + "   series: [{ name: 'Mutants',  data: " + values + "  }]" + " });  }); });";
		ChartData chartAndTable = new ChartData();
		chartAndTable.setChart(javascript);
		chartAndTable.setId(chartId);
		return chartAndTable;
	}

	public ChartData getStatusColumnChart(HashMap<String , Long> values, String title, String divId){
		
		String data = "[";
		// custom order & selection from Terry
		if (divId.equalsIgnoreCase("genotypeStatusChart")){
			// custom statuses to show + custom order
			data += "['" + StatusConstants.IMITS_MOUSE_STATUS_MICRO_INJECTION_IN_PROGRESS + "', " +  values.get(StatusConstants.IMITS_MOUSE_STATUS_MICRO_INJECTION_IN_PROGRESS) + "], ";
			data += "['" + StatusConstants.IMITS_MOUSE_STATUS_CHIMERA_OBTAINED + "', " +  values.get(StatusConstants.IMITS_MOUSE_STATUS_CHIMERA_OBTAINED) + "], ";
			data += "['" + StatusConstants.IMITS_MOUSE_STATUS_GENOTYPE_CONFIRMED + "', " +  values.get(StatusConstants.IMITS_MOUSE_STATUS_GENOTYPE_CONFIRMED) + "], ";
			data += "['" + StatusConstants.IMITS_MOUSE_STATUS_CRE_EXCISION_STARTED + "', " +  values.get(StatusConstants.IMITS_MOUSE_STATUS_CRE_EXCISION_STARTED) + "], ";
			data += "['" + StatusConstants.IMITS_MOUSE_STATUS_CRE_EXCISION_COMPLETE + "', " +  values.get(StatusConstants.IMITS_MOUSE_STATUS_CRE_EXCISION_COMPLETE) + "], ";
		}
		else if (divId.equalsIgnoreCase("phenotypeStatusChart")){
			// custom statuses to show + custom order
			data += "['" + StatusConstants.IMITS_MOUSE_PHENOTYPING_ATTEMPT_REGISTERED + "', " +  values.get(StatusConstants.IMITS_MOUSE_PHENOTYPING_ATTEMPT_REGISTERED) + "], ";
			data += "['" + StatusConstants.IMITS_MOUSE_PHENOTYPING_STARTED + "', " +  values.get(StatusConstants.IMITS_MOUSE_PHENOTYPING_STARTED) + "], ";
			data += "['" + StatusConstants.IMITS_MOUSE_PHENOTYPING_COMPLETE + "', " +  values.get(StatusConstants.IMITS_MOUSE_PHENOTYPING_COMPLETE) + "], ";	
		}
		else {
			for (String key: values.keySet()){
				data += "['" + key + "', " + values.get(key) + "], ";
			}
		}
		data += "]";
		
		String javascript = "$(function () { $('#" + divId + "').highcharts({" +
        	" chart: {type: 'column' }," + 
        	" title: {text: '" + title + "'}," +	
        	" credits: { enabled: false },  " +
        	" xAxis: { type: 'category', labels: { rotation: -90, style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'} } }," +
        	" yAxis: { min: 0, title: { text: 'Number of genes' } }," + 
        	" legend: { enabled: false }," +
        	" tooltip: { pointFormat: '<b>{point.y}</b>' }," +
        	" series: [{ name: 'Population',  data: " + data + "," +
            " dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif' } } }]" +
			" }); });";
		ChartData chartAndTable = new ChartData();
		chartAndTable.setChart(javascript);
		chartAndTable.setId("statusChart");
		return chartAndTable;
	}
	
	
	public ChartData getStackedHistogram(StackedBarsData map, Parameter parameter, String procedureName) {

		if (map == null) { return new ChartData(); }
		String title = parameter.getName();
		String subtitle = procedureName;
		String xLabel = "Ratio (mutantMean / controlMean)";//parameter.getUnit();
		ArrayList<Double> control = map.getControlMutatns();
		ArrayList<Double> mutant = map.getPhenMutants();
		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<String> controlGenes = map.getControlGenes();
		ArrayList<String> mutantGenes = map.getMutantGenes();
		ArrayList<String> controlGenesUrl = map.getControlGeneAccesionIds();
		ArrayList<String> mutantGenesUrl = map.getMutantGeneAccesionIds();
		DecimalFormat df;		
		ArrayList<Double> upperBounds = map.getUpperBounds();
		// We need to set the number of decimals according to the difference between the lowest and highest, so that the bin labels will be distinct
		// Here's an example where 2 decimals are not enogh https://www.mousephenotype.org/data/phenotypes/MP:0000063
		if (upperBounds.get(upperBounds.size() - 1) - upperBounds.get(0) > 0.1){
			df = new DecimalFormat("#.##");
		}
		else if (upperBounds.get(upperBounds.size() - 1) - upperBounds.get(0) > 0.01){
			df = new DecimalFormat("#.####");			
		}
		else if (upperBounds.get(upperBounds.size() - 1) - upperBounds.get(0) > 0.001){
			df = new DecimalFormat("#.#####");			
		}
		else{
			df = new DecimalFormat("#.########ß");			
		}
		for (int i = 0; i < upperBounds.size(); i++) {
			String c = controlGenes.get(i);
			String controlG = "";
			if (c.length() > 50) {
				int len = 0;
				for (String gene : c.split(" ")) {
					controlG += gene + " ";
					len += gene.length();
					if (len > 50) {
						controlG += "<br/>";
						len = 0;
					}
				}
			} else controlG = c;
			labels.add("'" + df.format(upperBounds.get(i)) + "###" + controlG + "###" + mutantGenes.get(i) + "###" + controlGenesUrl.get(i) + "###" + mutantGenesUrl.get(i) + "'");
		}
		double min = 0;
		for (double val : mutant)
			if (val < min) min = val;
		for (double val : control)
			if (val < min) min = val;
	
		String chartId = parameter.getStableId();
		String yTitle = "Number of lines";
		String javascript = "$(document).ready(function() {" + "chart = new Highcharts.Chart({ "
		+ "	colors:['rgba(239, 123, 11,0.7)','rgba(9, 120, 161,0.7)'],"
		+ " chart: {  type: 'column' , renderTo: 'single-chart-div'}," + 
		" title: {  text: '<span data-parameterStableId=\"" + parameter.getStableId() + "\">" + title + "</span>', useHTML:true  }," + 
		" subtitle: { text: '" + subtitle + "'}," + 
		" credits: { enabled: false }," + 
		" xAxis: { categories: " + labels + ", " + 
			"labels: {formatter:function(){ return this.value.split('###')[0]; }, rotation: -45} , "
			+ "title: { text: '" + xLabel + "'} }," + 
		" yAxis: { min: " + min + ",  "
			+ "	title: {  text: '" + yTitle + "'  }, "
			+ "stackLabels: { enabled: false}  }," + " "
			+ "tooltip: { " + "formatter: function() { " + "if ('Mutant strains with no calls for this phenotype' === this.series.name )" + "return ''+  this.series.name +': '+ this.y + ' out of '+ this.point.stackTotal + '<br/>Genes: ' +  this.x.split('###')[1];  " + "else return ''+  this.series.name +': '+ this.y + ' out of '+ this.point.stackTotal + '<br/>Genes: ' +  this.x.split('###')[2];}  }, " + " "
		+ "plotOptions: { column: {  stacking: 'normal',  dataLabels: { enabled: false} }, " + "series: { cursor: 'pointer', point: { events: { click: function() { " + "var url = document.URL.split('/phenotypes/')[0];" + "if ('Mutant strains with no calls for this phenotype' === this.series.name) {" + "url += '/charts?' + this.category.split('###')[3];" + "} else {" + "url += '/charts?' + this.category.split('###')[4];" + "} " + "url += '&parameter_stable_id=" + parameter.getStableId() + "';" + "window.open(url); " + "console.log(url);" + "} } } }" + "} ," + " series: [{ name: 'Mutant strains with this phenotype called',  data: " + mutant + "  }, {name: 'Mutant strains with no calls for this phenotype', data: " + control + "}]" + " });  }); ";
		ChartData chartAndTable = new ChartData();
		chartAndTable.setChart(javascript);
		chartAndTable.setId(chartId);
	
		return chartAndTable;
	}


	/**
	 * 
	 * @param sexType
	 * @param rawData
	 *            - list of floats for WT then hom or het
	 * @param experiment
	 *            TODO
	 * @param symbol
	 * @param allelicCompositionString
	 * @param biologicalModel
	 * @param parameterUnit
	 * @param xAxisCategoriesList
	 *            - bare categories from database e.g. WT, HOM
	 * @param continuousBarCharts
	 * @param max
	 * @return map containing min and max values
	 */
	private static List<UnidimensionalStatsObject> produceUnidimensionalStatsData(Parameter parameter, ExperimentDTO experiment, String allelicCompositionString, String symbol, String geneticBackground) {

		List<? extends StatisticalResult> results = experiment.getResults();
		logger.debug("result=" + results);
		List<UnidimensionalStatsObject> statsObjects = new ArrayList<UnidimensionalStatsObject>();

		for (SexType sexType : experiment.getSexes()) {

			// Set up the controls data
			UnidimensionalStatsObject wtStatsObject = new UnidimensionalStatsObject();
			Set<ObservationDTO> controls = experiment.getControls(sexType);

			wtStatsObject = generateStats(experiment, wtStatsObject, controls, null, sexType);
			statsObjects.add(wtStatsObject);

			// set up the mutant stats data
			for (ZygosityType zType : experiment.getZygosities()) {
				UnidimensionalStatsObject tempStatsObject = new UnidimensionalStatsObject();

				Set<ObservationDTO> mutants = experiment.getMutants(sexType, zType);
				tempStatsObject = generateStats(experiment, tempStatsObject, mutants, zType, sexType);

				for (StatisticalResult result : results) {
					UnidimensionalResult unidimensionalResult = (UnidimensionalResult) result;
					if (result.getZygosityType().equals(zType)) {
						tempStatsObject.setResult((UnidimensionalResult) result);
					}
				}

				tempStatsObject.setLine(allelicCompositionString);
				tempStatsObject.setAllele(symbol);
				tempStatsObject.setGeneticBackground(geneticBackground);
				statsObjects.add(tempStatsObject);
			}
		}
		return statsObjects;
	}


	private static UnidimensionalStatsObject generateStats(ExperimentDTO experiment, UnidimensionalStatsObject tempStatsObject, Set<ObservationDTO> mutants, ZygosityType zygosity, SexType sexType) {

		tempStatsObject.setSampleSize(mutants.size());
		// do the stats to get mean and SD
		// Get a DescriptiveStatistics instance
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// Add the data
		for (ObservationDTO mutantObservationDTO : mutants) {
			stats.addValue(mutantObservationDTO.getDataPoint());
		}
		if (mutants.size() > 0) {
			int decimalPlaces = ChartUtils.getDecimalPlaces(experiment);
			Float mean = ChartUtils.getDecimalAdjustedFloat(new Float(stats.getMean()), decimalPlaces);
			// System.out.println("mean=" + mean);
			Float sd = ChartUtils.getDecimalAdjustedFloat(new Float(stats.getStandardDeviation()), decimalPlaces);
			tempStatsObject.setMean(mean);
			tempStatsObject.setSd(sd);
			if (zygosity != null) {
				tempStatsObject.setZygosity(zygosity);
			}
			if (sexType != null) {
				tempStatsObject.setSexType(sexType);
			}
		}
		// end of stats creation for table
		return tempStatsObject;
	}
}
