package uk.ac.ebi.phenotype.stats.unidimensional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


import org.apache.commons.lang.WordUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.dao.BiologicalModelDAO;
import uk.ac.ebi.phenotype.dao.UnidimensionalStatisticsDAO;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;

import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.ChartData;
import uk.ac.ebi.phenotype.stats.ChartType;
import uk.ac.ebi.phenotype.stats.ChartUtils;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
import uk.ac.ebi.phenotype.stats.JSONGraphUtils;
import uk.ac.ebi.phenotype.stats.MouseDataPoint;
import uk.ac.ebi.phenotype.stats.ObservationDTO;
import uk.ac.ebi.phenotype.stats.ScatterGraph;
import uk.ac.ebi.phenotype.stats.TableObject;


@Service
public class UnidimensionalChartAndTableProvider {
	
	private ScatterGraph scatterGraph=new ScatterGraph();
	private static final Logger logger = Logger
			.getLogger(UnidimensionalChartAndTableProvider.class);

//	@Autowired
//	private UnidimensionalStatisticsDAO unidimensionalStatisticsDAO;

	private String axisFontSize = "15";

/**
 * return one unidimensional data set per experiment - one experiment should have one or two graphs corresponding to sex and a stats result for one table at the bottom
 * @param experimentList
 * @param bmDAO
 * @param config
 * @param unidimensionalMutantBiologicalModels
 * @param parameter
 * @param acc
 * @param model
 * @param genderList
 * @param zyList
 * @param boxOrScatter
 * @param byMouseId
 * @return
 * @throws SQLException
 * @throws IOException
 * @throws URISyntaxException
 */
	public List<UnidimensionalDataSet> doUnidimensionalData(
			List<ExperimentDTO> experimentList, BiologicalModelDAO bmDAO, Map<String, String> config, List<BiologicalModel> unidimensionalMutantBiologicalModels,
			Parameter parameter,
			String acc, Model model, List<String> genderList,
			List<String> zyList, ChartType boxOrScatter, Boolean byMouseId)
			throws SQLException, IOException, URISyntaxException {

		// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1920000?parameterId=ESLIM_015_001_018
		// String parameterId="ESLIM_015_001_018";// ESLIM_015_001_018

		// Map<String, Float> allMinMax =
		// unidimensionalStatisticsDAO.getMinAndMaxForParameter(parameter.getStableId());
		List<UnidimensionalDataSet> unidimensionalDataSets=new ArrayList<UnidimensionalDataSet>();
		
		
		// List<BiologicalModel> biologicalModels = unidimensionalStatisticsDAO
		// .getBiologicalModelsByParameterAndGene(parameter, acc);
		// logger.debug("biologicalmodels size=" + biologicalModels.size());
		

		// get control data
		for (ExperimentDTO experiment : experimentList) {
			Float max = new Float(0);
			Float min = new Float(1000000000);
			List<UnidimensionalResult> allUnidimensionalResults = new ArrayList<UnidimensionalResult>();
			UnidimensionalDataSet unidimensionalDataSet = new UnidimensionalDataSet();
			unidimensionalDataSet.setExperimentId(experiment.getExperimentId());
			List<ChartData> chartsAndTablesForParameter = new ArrayList<ChartData>();
			List<UnidimensionalStatsObject> unidimensionalStatsObjects = new ArrayList<>();
			List<ChartData> yAxisAdjustedBoxChartsNTables = new ArrayList<ChartData>();
			
			System.out.println("biolgocialModelId="+experiment.getExperimentalBiologicalModelId());
			Map<String,Integer> mouseIdsToColumnsMap=new TreeMap<>();
			BiologicalModel expBiologicalModel=bmDAO.getBiologicalModelById(experiment.getExperimentalBiologicalModelId());
					for (SexType sexType : experiment.getSexes()) { // one graph for each sex if
													// unspecified in params to
													// page or in list of sex
													// param specified
						
						if (genderList.isEmpty()
								|| genderList.contains(sexType.name())) {

					
							 //mouse data points are needed for the scatter plots so we have mouse ids with them
							 //currently not getting this from solr experiments - can we add it?
							 List<List<MouseDataPoint>> mouseDataPointsSet=new
							 ArrayList<List<MouseDataPoint>>();
							 // category e.g normal, abnormal
							
							 List<List<Float>> observations2DList = new
							 ArrayList<List<Float>>();
//							 List<Float> controlCounts =
//							 unidimensionalStatisticsDAO
//							  .getControlDataPointsForPopulation(popId);
							
							 List<Float> controlCounts=new ArrayList<Float>();
							 List<MouseDataPoint> controlMouseDataPoints =new ArrayList<>();
//							 unidimensionalStatisticsDAO
//							 .getControlDataPointsWithMouseName(popId);
							 
							 //loop over the control points and add them
							
							 for(ObservationDTO control:experiment.getControls()) {
								 //get the attributes of this data point
								 //We don't want to split controls by gender on Unidimensional data
								// SexType docSexType=SexType.valueOf(ctrlDoc.getString("gender"));
								// ZygosityType zygosityType=ZygosityType.valueOf(ctrlDoc.getString("zygosity"));
								
								 Float dataPoint=control.getDataPoint();
								controlCounts.add(new Float(dataPoint));
//								Integer mouseColumn=null;
//								if(mouseIdsToColumnsMap.containsKey(control.getExternalSampleId())){
//									mouseColumn=mouseIdsToColumnsMap.get(control.getExternalSampleId());
//								}else {
//									 mouseColumn=mouseIdsToColumnsMap.size();
//									mouseIdsToColumnsMap.put(control.getExternalSampleId(),mouseColumn);
//								}
//								MouseDataPoint mDataPoint=new MouseDataPoint(control.getExternalSampleId(), dataPoint,  mouseColumn);
//					 			logger.warn("controlMouseDataPoint="+mDataPoint);
								//controlMouseDataPoints.add(mDataPoint);
								controlMouseDataPoints=addMouseDataPoint(mouseIdsToColumnsMap, controlCounts, controlMouseDataPoints, control, dataPoint);
								
								//logger.debug("adding control point="+dataPoint);
								 
							 }
							 mouseDataPointsSet.add(controlMouseDataPoints);
							observations2DList.add(controlCounts);
							
						
							for (ZygosityType zType : experiment.getZygosities()) {
								if (zyList.isEmpty()
										|| zyList.contains(zType.name())) {
									
									 //loop over all the experimental docs and get all that apply to current loop parameters
									 List<Float> mutantCounts=new
											 ArrayList<Float>();
									 
									 List<MouseDataPoint> mutantMouseDataPoints=new ArrayList<>();
									 
										Set<ObservationDTO> expObservationsSet = Collections.emptySet();
										if (zType.equals(ZygosityType.heterozygote)
												|| zType.equals(ZygosityType.hemizygote)) {
											expObservationsSet = experiment
													.getHeterozygoteMutants();
										}
										if (zType.equals(ZygosityType.homozygote)) {
											expObservationsSet = experiment
													.getHomozygoteMutants();
										}

										for (ObservationDTO expDto : expObservationsSet) {
									
									 //get the attributes of this data point
											SexType docSexType = SexType.valueOf(expDto
													.getSex());
									
									
									 Float dataPoint=expDto.getDataPoint();
									 		if( docSexType.equals(sexType)){
									 			mutantMouseDataPoints=addMouseDataPoint(
														mouseIdsToColumnsMap,
														mutantCounts,
														mutantMouseDataPoints,
														expDto, dataPoint);
									 		}
									 		mouseDataPointsSet.add(mutantMouseDataPoints);
									 }
									 observations2DList.add(mutantCounts);
									
								}
								
							}
							if (observations2DList.size() > 1) {// only
							// create the table
							// // and graph if there is
							// // more than just WT
							// // data requested
							String title = parameter.getName();
							ChartData chartAndTable = null;
							if(boxOrScatter.equals(ChartType.UnidimensionalScatter))
							 {// produce a scatter chart string here
							 // rather than an box and scatter plot
							 // like below
							 chartAndTable = scatterGraph.processScatterChartData(title,
							 sexType, parameter, experiment.getZygosities(), zyList,
							 mouseDataPointsSet, expBiologicalModel, byMouseId);
							
							 } else {
							 chartAndTable = processChartData(title, sexType,
							 parameter, experiment.getZygosities(), zyList,
							 observations2DList, experiment);
							
							 }
							
							// // return an new class to represent a
							// unidimensional
							// // data set chart and table object to include
							// other
							// // tables say one for overview and one for detail
							// // (annova?)
							 List<UnidimensionalStatsObject>
							 unidimensionalStatsObject =
							 produceUnidimensionalStatsData(
							 title, sexType, parameter, experiment.getZygosities(), zyList,
							 observations2DList, expBiologicalModel, experiment);
							 logger.debug("unidimensionalStatsObject="+unidimensionalStatsObject);
							 unidimensionalStatsObjects
							 .addAll(unidimensionalStatsObject);
							 chartsAndTablesForParameter.add(chartAndTable);
							 Float tempMin = chartAndTable.getMin();
							 Float tempMax = chartAndTable.getMax();
							 if (tempMin < min)
							 min = tempMin;
							 if (tempMax > max)
							 max = tempMax;

						}
					}// end of gender

					// i++;
					}
					
//					min = allMinMax.get("min");
//					max = allMinMax.get("max");
					logger.debug("min=" + min + "  max=" + max);
					// List<String> yAxisAdjustedBoxCharts
					// =ChartUtils.alterMinAndMaxYAxisOfCharts(continuousCharts, min, max);
					yAxisAdjustedBoxChartsNTables = ChartUtils.alterMinAndMaxYAxisOfCharts(
							chartsAndTablesForParameter, min, max);
					unidimensionalDataSet
							.setSexChartAndTables(yAxisAdjustedBoxChartsNTables);
					unidimensionalDataSet
							.setAllUnidimensionalResults(allUnidimensionalResults);
					unidimensionalDataSet.setStatsObjects(unidimensionalStatsObjects);
					unidimensionalDataSets.add(unidimensionalDataSet);
				} // end of experiment loop

			


		// return yAxisAdjustedBoxChartsNTables;
		return unidimensionalDataSets;

	}

	private List<MouseDataPoint> addMouseDataPoint(Map<String, Integer> mouseIdsToColumnsMap,
			List<Float> countsForSet,
			List<MouseDataPoint> mouseDataPointsList, ObservationDTO observationDTO,
			Float dataPoint) {
		
	
		countsForSet.add(new Float(dataPoint));
		//logger.debug("adding mutant point="+dataPoint);
		MouseDataPoint mDataPoint=new MouseDataPoint(observationDTO.getExternalSampleId(), dataPoint,  0, observationDTO.getDateOfExperiment());
		//logger.warn("mouseDataPoint="+mDataPoint);
		mouseDataPointsList.add(mDataPoint);
		return mouseDataPointsList;
	}

	/**
	 * 
	 * @param sexType
	 * @param zyList
	 * @param rawData
	 *            - list of floats for WT then hom or het
	 * @param biologicalModel
	 * @param parameterUnit
	 * @param xAxisCategoriesList
	 *            - bare categories from database e.g. WT, HOM
	 * @param continuousBarCharts
	 * @param max
	 * @return map containing min and max values
	 */
	private ChartData processChartData(String title, SexType sexType,
			Parameter parameter, Set<ZygosityType> set,
			List<String> zyList, List<List<Float>> rawData,
			ExperimentDTO experiment) {
		// http://localhost:8080/phenotype-archive/stats/genes/MGI:1929878?parameterId=ESLIM_015_001_018
		
		int decimalPlaces=ChartUtils.getDecimalPlaces(experiment);
		List<ChartData> chartsAndTables = new ArrayList<ChartData>();
		Float max = new Float(0);
		Float min = new Float(100000000);
		Map<String, Float> minMax = new HashMap<String, Float>();
		String parameterUnit = parameter.checkParameterUnit(1);
		List<String> categoriesListBoxChart = new ArrayList<String>();
		List<String> categoriesListBarChart = new ArrayList<String>();

		// always add the control columns - one for boxmodel and one for
		// observations
		categoriesListBoxChart.add("WT");
		categoriesListBoxChart.add("WT");
		categoriesListBarChart.add("WT");// only need one for each category for
											// our bar chart

		// add two columns for each zyg
		for (ZygosityType zType : set) {
			if (zyList.isEmpty() || zyList.contains(zType.name())) {
				categoriesListBoxChart.add(zType.name().substring(0, 3)
						.toUpperCase());
				categoriesListBoxChart.add(zType.name().substring(0, 3)
						.toUpperCase());
				categoriesListBarChart.add(zType.name().substring(0, 3)
						.toUpperCase());// only need one for each category for
										// our bar chart
				List<String> tableRow = new ArrayList<String>(6);
				String alleleComposition = "dummyAllelichere";//biologicalModelId.getAllelicComposition();
				if (zType.equals(ZygosityType.homozygote)) {// if homozygote
															// don't need the
															// second part of
															// the string after
															// the forward slash
					//alleleComposition = alleleComposition.substring(0,
							//alleleComposition.indexOf("/"));
				}
			}
		}
		logger.debug("raw data=" + rawData);
		// first list is control/wt then mutant for hom or het or both
		List<List<Float>> boxPlotData = new ArrayList<List<Float>>();
		List<Float> listOfMeansForBarChart = new ArrayList<Float>();// for
																	// barchart
																	// only
		List<List<Float>> sdsList = new ArrayList<List<Float>>();// barchart
																	// only
		int row = 0;

		for (List<Float> listOfFloats : rawData) {
			// Get a DescriptiveStatistics instance
			DescriptiveStatistics stats = new DescriptiveStatistics();

			// Add the data from the array
			for (Float point : listOfFloats) {
				stats.addValue(point);
			}
			
			List<Float> wt1 = new ArrayList<Float>();
			if(listOfFloats.size()>0) {
			double Q1 = ChartUtils.getDecimalAdjustedFloat(new Float(stats.getPercentile(25)),decimalPlaces);
			double Q3 = ChartUtils.getDecimalAdjustedFloat(new Float(stats.getPercentile(75)), decimalPlaces);
			double IQR = Q3 - Q1;

			Float minIQR = ChartUtils.getDecimalAdjustedFloat(new Float(Q1 - (1.5 * IQR)), decimalPlaces);
			wt1.add(minIQR);// minimum
			wt1.add(new Float(Q1));// lower quartile
			
			Float decFloat=ChartUtils.getDecimalAdjustedFloat(new Float(stats.getMean()), 1);
			wt1.add(decFloat);// median
			wt1.add(new Float(Q3));// upper quartile

			Float maxTemp = new Float(stats.getMax());
			Float maxIQR =  ChartUtils.getDecimalAdjustedFloat(new Float(Q3 + (1.5 * IQR)), decimalPlaces);
			wt1.add(maxIQR);// maximumbs.
			if (maxTemp > max)
				max = maxTemp;// count
			if (maxIQR > max)
				max = maxIQR;

			Float minTemp = new Float(stats.getMin());
			if (minTemp < min)
				min = minTemp;// count
			if (minIQR < min)
				min = minIQR; // if the lowest IQR bar is lower than the min
								// value set the min to this
			minMax.put("min", min);
			minMax.put("max", max);
			}
			boxPlotData.add(wt1);
			List<Float> wt2 = new ArrayList<Float>();// add empty llist as the
														// next column is
														// scatter not box plot!
			boxPlotData.add(wt2);

			// for Barchart
//			Float mean = new Float(stats.getMean());
//			Float sd = new Float(stats.getStandardDeviation());
//			listOfMeansForBarChart.add(mean);
//			List<Float> sds = new ArrayList<Float>();
//			Float plusSd = mean + sd;
//			Float minusSd = mean - sd;
//			sds.add(minusSd);
//			sds.add(plusSd);
//			sdsList.add(sds);
			row++;
		}
		String yAxisTitle = parameterUnit;

		List<List<Float>> scatterColumns = new ArrayList<List<Float>>();// for
																		// example
																		// there
																		// are
																		// four
																		// columns
																		// so
																		// four
																		// lists
																		// in
																		// the
																		// list
		int columnIndex = 1;// we want to add observation/scatter column every
							// other column
		for (List<Float> listOfFloats : rawData) {

			for (Float dataPoint : listOfFloats) {
				List<Float> column1 = new ArrayList<Float>();
				column1.add(new Float(columnIndex));
				column1.add(dataPoint);
				scatterColumns.add(column1);
			}

			columnIndex += 2;
		}

		
		String chartString = createContinuousBoxPlotChartsString(
				categoriesListBoxChart, title, sexType, yAxisTitle,
				boxPlotData, scatterColumns);
		// continuousCharts.add(chartString);
		ChartData cNTable = new ChartData();
		// cNTable.setTable(table);
		cNTable.setChart(chartString);
		cNTable.setMin(min);
		cNTable.setMax(max);
		return cNTable;
	}

	

	/**
	 * 
	 * @param sexType
	 * @param zyList
	 * @param rawData
	 *            - list of floats for WT then hom or het
	 * @param experiment TODO
	 * @param biologicalModel
	 * @param parameterUnit
	 * @param xAxisCategoriesList
	 *            - bare categories from database e.g. WT, HOM
	 * @param continuousBarCharts
	 * @param max
	 * @return map containing min and max values
	 */
	private List<UnidimensionalStatsObject> produceUnidimensionalStatsData(
			String title, SexType sexType, Parameter parameter,
			Set<ZygosityType> set, List<String> zyList,
			List<List<Float>> rawData, BiologicalModel expBiologicalModel, ExperimentDTO experiment) {
		// http://localhost:8080/phenotype-archive/stats/genes/MGI:1929878?parameterId=ESLIM_015_001_018
		//logger.debug("experiment="+experiment);
		StatisticalResult result = experiment.getResult();
		//logger.debug("result="+result);
		List<UnidimensionalStatsObject> statsObjects = new ArrayList<UnidimensionalStatsObject>();
		// String parameterUnit = parameter.checkParameterUnit(1);
		UnidimensionalStatsObject wtStatsObject = new UnidimensionalStatsObject();
		statsObjects.add(wtStatsObject);
		for (ZygosityType zType : set) {
			if (zyList.isEmpty() || zyList.contains(zType.name())) {
				UnidimensionalStatsObject tempObje = new UnidimensionalStatsObject();
				String alleleComposition =expBiologicalModel.getAllelicComposition();
				if (zType.equals(ZygosityType.homozygote)) {// if homozygote
															// don't need the
															// second part of
															// the string after
															// the forward slash
					alleleComposition = alleleComposition.substring(0,
							alleleComposition.indexOf("/"));
				}
				tempObje.setZygosity(zType);
				tempObje.setLine(alleleComposition);
				if(expBiologicalModel.getAlleles().size()>1) {
					System.err.println("error allele size=0");
				tempObje.setAllele(expBiologicalModel.getAlleles().get(0).getSymbol());
				}
				tempObje.setGeneticBackground(expBiologicalModel.getGeneticBackground());
				statsObjects.add(tempObje);
			}else {
				System.err.println("error allele size=0");
			}
		}
		
		int decimalPlaces=ChartUtils.getDecimalPlaces(experiment);
		int row = 0;
		for (List<Float> listOfFloats : rawData) {
			// Get a DescriptiveStatistics instance
			DescriptiveStatistics stats = new DescriptiveStatistics();
			UnidimensionalStatsObject statsObject = statsObjects.get(row);
			// Add the data from the array
			for (Float point : listOfFloats) {
				stats.addValue(point);
			}
			if(listOfFloats.size()>0) {
			Float mean = ChartUtils.getDecimalAdjustedFloat(new Float(stats.getMean()), decimalPlaces);
			System.out.println("mean="+mean);
			Float sd = ChartUtils.getDecimalAdjustedFloat(new Float(stats.getStandardDeviation()), decimalPlaces);
			statsObject.setMean(mean);
			statsObject.setSd(sd);
			}
			statsObject.setSampleSize(listOfFloats.size());
			statsObject.setSexType(sexType);
			
			row++;
		}
		return statsObjects;
	}

	/**
	 * 
	 * @param title
	 *            main title of the graph
	 * @param yAxisTitle
	 *            - unit of measurement - how to get this from the db?
	 * @param observations2dList
	 * @param scatterColumns
	 * @param xAisxCcategoriesList
	 *            e.g. WT, WT, HOM, HOM for each column to be displayed
	 * @return
	 */
	private String createContinuousBoxPlotChartsString(
			List<String> xAxisCategoriesList, String title, SexType sex,
			String yAxisTitle, List<List<Float>> observations2dList,
			List<List<Float>> scatterColumns) {
		JSONArray categoriesArray = new JSONArray(xAxisCategoriesList);
		String categories = categoriesArray.toString();// "['WT', 'WT', 'HOM', 'HOM']";
		JSONArray boxPlot2DData = new JSONArray(observations2dList);
		String observationsString = boxPlot2DData.toString();// " [ [733, 853, 939, 980, 1080], [], [724, 802, 806, 871, 950], [] ]";//array
																// for each
																// column/category
																// WT HOM etc
		JSONArray scatterJArray = new JSONArray(scatterColumns);

		String scatterString = scatterJArray.toString();// "[ [1, 644], [3, 718], [3, 951], [3, 969] ]";//fist
														// number of pair
														// indicates
														// category/column so 0
														// is first column 3 is
														// second
		
		
		String chartString = "{ chart: { type: 'boxplot' },  tooltip: { formatter: function () { if(typeof this.point.high === 'undefined'){ return '<b>Observation</b><br/>' + this.point.y; } else { return '<b>Genotype: ' + this.key + '</b><br/>LQ - 1.5 * IQR: ' + this.point.low + '<br/>Lower Quartile: ' + this.point.options.q1 + '<br/>Median: ' + this.point.options.median + '<br/>Upper Quartile: ' + this.point.options.q3 + '<br/>UQ + 1.5 * IQR: ' + this.point.options.high + '</b>'; } } }    , title: { text: '"
				+ title
				+ "' } , credits: { enabled: false },  subtitle: { text: '"
				+ WordUtils.capitalize(sex.name())
				+ "', x: -20 }, legend: { enabled: false }, xAxis: {labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }}, categories:  "
				+ categories
				+ " }, \n"
				+ "yAxis: {max: 2,  min: 0, labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }},title: { text: '"
				+ yAxisTitle
				+ "' } }, "
				+ "\n series: [{ name: 'Observations', data:"
				+ observationsString
				+ ",       tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }                    }, { name: 'Observation', color: Highcharts.getOptions().colors[0], type: 'scatter', data: "
				+ scatterString
				+ ", marker: { fillColor: 'white', lineWidth: 1, lineColor: Highcharts.getOptions().colors[0] }, tooltip: { pointFormat: '{point.y:..4f}' }          }] }); }";
		return chartString;
	}


}
