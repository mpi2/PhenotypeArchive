package uk.ac.ebi.phenotype.stats.unidimensional;

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
import java.util.TreeMap;
import javax.annotation.Resource;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

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
import uk.ac.ebi.phenotype.stats.MouseDataPoint;
import uk.ac.ebi.phenotype.stats.ObservationDTO;
import uk.ac.ebi.phenotype.stats.graphs.GraphUtils;

@Service
public class UnidimensionalChartAndTableProvider {

	
	private static final Logger logger = Logger.getLogger(UnidimensionalChartAndTableProvider.class);

	private String axisFontSize = "15";

	/**
	 * return one unidimensional data set per experiment - one experiment should
	 * have one or two graphs corresponding to sex and a stats result for one
	 * table at the bottom
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
	public UnidimensionalDataSet doUnidimensionalData(
			ExperimentDTO experiment, String chartId,
			String title,
			ChartType boxOrScatter, Boolean byMouseId,
			String yAxisTitle, BiologicalModel expBiologicalModel) throws SQLException,
			IOException, URISyntaxException {
		ChartData chartAndTable = null;
		// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1920000?parameterId=ESLIM_015_001_018
		// String parameterId="ESLIM_015_001_018";// ESLIM_015_001_018

		List<UnidimensionalDataSet> unidimensionalDataSets = new ArrayList<UnidimensionalDataSet>();

		
		// get control data
	
			List<UnidimensionalResult> allUnidimensionalResults = new ArrayList<UnidimensionalResult>();
			UnidimensionalDataSet unidimensionalDataSet = new UnidimensionalDataSet();
			unidimensionalDataSet.setExperiment(experiment);
			unidimensionalDataSet.setOrganisation(experiment.getOrganisation());
			unidimensionalDataSet.setExperimentId(experiment.getExperimentId());
			List<UnidimensionalStatsObject> unidimensionalStatsObjects = new ArrayList<>();

			//System.out.println("biolgocialModelId="
					//+ experiment.getExperimentalBiologicalModelId());
			Map<String, Integer> mouseIdsToColumnsMap = new TreeMap<>();
		
			

					// mouse data points are needed for the scatter plots so we
					// have mouse ids with them
					// currently not getting this from solr experiments - can we
					// add it?
					List<List<MouseDataPoint>> mouseDataPointsSet = new ArrayList<List<MouseDataPoint>>();
					// category e.g normal, abnormal
					Map<SexType, List<List<Float>>> genderAndRawDataMap=new HashMap<SexType, List<List<Float>>>();
				
					for (SexType sexType : experiment.getSexes()) { 
						List<List<Float>> rawData = new ArrayList<List<Float>>();
					List<Float> controlCounts = new ArrayList<Float>();
					List<MouseDataPoint> controlMouseDataPoints = new ArrayList<>();
					// unidimensionalStatisticsDAO
					// .getControlDataPointsWithMouseName(popId);

					// loop over the control points and add them

					for (ObservationDTO control : experiment.getControls()) {
						 SexType
						 docSexType=SexType.valueOf(control.getSex());
						Float dataPoint = control.getDataPoint();
						
						if (docSexType.equals(sexType)) {
						controlMouseDataPoints = addMouseDataPoint(
								mouseIdsToColumnsMap, controlCounts,
								controlMouseDataPoints, control, dataPoint);
						}
						// logger.debug("adding control point="+dataPoint);

					}
					mouseDataPointsSet.add(controlMouseDataPoints);
					rawData.add(controlCounts);

					for (ZygosityType zType : experiment.getZygosities()) {
						

							// loop over all the experimental docs and get all
							// that apply to current loop parameters
							List<Float> mutantCounts = new ArrayList<Float>();

							List<MouseDataPoint> mutantMouseDataPoints = new ArrayList<>();

							Set<ObservationDTO> expObservationsSet = Collections
									.emptySet();
							//JW added hemizygote capabillity to experimentService so just use this one method
							expObservationsSet=experiment.getMutants(sexType, zType);
							

							for (ObservationDTO expDto : expObservationsSet) {

								// get the attributes of this data point
								SexType docSexType = SexType.valueOf(expDto
										.getSex());

								Float dataPoint = expDto.getDataPoint();
								if (docSexType.equals(sexType)) {
									mutantMouseDataPoints = addMouseDataPoint(
											mouseIdsToColumnsMap, mutantCounts,
											mutantMouseDataPoints, expDto,
											dataPoint);
								}

							}
							mouseDataPointsSet.add(mutantMouseDataPoints);
							rawData.add(mutantCounts);
					}
					
					genderAndRawDataMap.put(sexType, rawData);				
					}//end of sextype loop
					Map<String,String>usefulStrings=GraphUtils.getUsefulStrings(expBiologicalModel);
					List<UnidimensionalStatsObject> unidimensionalStatsObject = produceUnidimensionalStatsData(
							title,genderAndRawDataMap,
							experiment, usefulStrings.get("allelicComposition"), usefulStrings.get("symbol"), usefulStrings.get("geneticBackground"));
					unidimensionalStatsObjects
					.addAll(unidimensionalStatsObject);
					chartAndTable = processChartData(chartId, title, 
							experiment.getZygosities(), genderAndRawDataMap, experiment, yAxisTitle, usefulStrings.get("allelicComposition"), usefulStrings.get("symbol"));
			
			unidimensionalDataSet
					.setChartData(chartAndTable);
			unidimensionalDataSet
					.setAllUnidimensionalResults(allUnidimensionalResults);
			unidimensionalDataSet.setStatsObjects(unidimensionalStatsObjects);
			unidimensionalDataSets.add(unidimensionalDataSet);
		return unidimensionalDataSet;

	}

	private List<MouseDataPoint> addMouseDataPoint(
			Map<String, Integer> mouseIdsToColumnsMap,
			List<Float> countsForSet, List<MouseDataPoint> mouseDataPointsList,
			ObservationDTO observationDTO, Float dataPoint) {

		countsForSet.add(new Float(dataPoint));
		// logger.debug("adding mutant point="+dataPoint);
		MouseDataPoint mDataPoint = new MouseDataPoint(
				observationDTO.getExternalSampleId(), dataPoint, 0,
				observationDTO.getDateOfExperiment(), SexType.valueOf(observationDTO.getSex()));
		// logger.warn("mouseDataPoint="+mDataPoint);
		mouseDataPointsList.add(mDataPoint);
		return mouseDataPointsList;
	}

	/**
	 * 
	 * @param sexOrder 
	 * @param genderList
	 * @param genderAndRawDataMap
	 *            - list of floats for WT then hom or het
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
	private ChartData processChartData(String chartId, String title,
			Set<ZygosityType> set, Map<SexType, List<List<Float>>> genderAndRawDataMap, ExperimentDTO experiment, String yAxisTitle, String allelicCompositionString, String symbol) {
		// http://localhost:8080/phenotype-archive/stats/genes/MGI:1929878?parameterId=ESLIM_015_001_018

		
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
		List<String> categoriesListBoxChart = new ArrayList<String>();
		int decimalPlaces = ChartUtils.getDecimalPlaces(experiment);	
		int columnIndex = 0;// we want to add observation/scatter column every
		// other column
		// always add the control columns - one for boxmodel and one for
		// observations
		//for(String gender: genderList) {
		Map<SexType,List<List<Float>>> sexAndBoxPlotMap=new HashMap<SexType,List<List<Float>>>(); 
		Map<SexType,List<List<Float>>> sexAndScatterMap=new HashMap<SexType,List<List<Float>>>(); 
		for(SexType sexKey:genderAndRawDataMap.keySet()) {
			
			List<List<Float>> boxPlotData = new ArrayList<List<Float>>();
			for(int i=0; i<columnIndex;i++) {
				List<Float> empty = new ArrayList<Float>();// add 2 empty arrays as the next two columns are for scatter charts
				 boxPlotData.add(empty);
				}
			List<List<Float>> scatterColumns = new ArrayList<List<Float>>();// for
			
		for(int i=0; i<2;i++) {//need two sets of these labels for columns one set for box and one set for scatter
		categoriesListBoxChart.add(WordUtils.capitalize(sexKey.name())+" WT");
		// add two columns for each zyg
		for (ZygosityType zType : set) {
		
				categoriesListBoxChart.add(WordUtils.capitalize(sexKey.name())+" "+zType.name().substring(0, 3)
						.toUpperCase());	
			
		}
		}
		
		logger.debug("raw data=" + genderAndRawDataMap);
		// first list is control/wt then mutant for hom or het or both
		
		
		
		for (List<Float> listOfFloats : genderAndRawDataMap.get(sexKey)) {
			
			// Get a DescriptiveStatistics instance
			DescriptiveStatistics stats = new DescriptiveStatistics();
			for (Float point : listOfFloats) {
				stats.addValue(point);
			}

			List<Float> wt1 = new ArrayList<Float>();
			if (listOfFloats.size() > 0) {
				// double lower = stats.getPercentile(25);
				// double higher=stats.getPercentile(75);
				double Q1 = ChartUtils.getDecimalAdjustedFloat(
						new Float(stats.getPercentile(25)), decimalPlaces);
				double Q3 = ChartUtils.getDecimalAdjustedFloat(
						new Float(stats.getPercentile(75)), decimalPlaces);
				double IQR = Q3 - Q1;

				Float minIQR = ChartUtils.getDecimalAdjustedFloat(new Float(Q1
						- (1.5 * IQR)), decimalPlaces);
				wt1.add(minIQR);// minimum
				wt1.add(new Float(Q1));// lower quartile

				Float decFloat = ChartUtils.getDecimalAdjustedFloat(new Float(
						stats.getMean()), decimalPlaces);
				wt1.add(decFloat);// median
				wt1.add(new Float(Q3));// upper quartile

				Float maxIQR = ChartUtils.getDecimalAdjustedFloat(new Float(Q3
						+ (1.5 * IQR)), decimalPlaces);
				wt1.add(maxIQR);// maximumbs.
				boxPlotData.add(wt1);
			}
			
			
			
			System.out.println("column="+columnIndex);
			columnIndex++;
		}
		
		
		
		for (List<Float> listOfFloats : genderAndRawDataMap.get(sexKey)) {

			for (Float dataPoint : listOfFloats) {
				List<Float> column1 = new ArrayList<Float>();
				column1.add(new Float(columnIndex));
				column1.add(dataPoint);
				scatterColumns.add(column1);
			}
			System.out.println("columnindex in scatter="+columnIndex);
			columnIndex ++;
		}
		
		sexAndBoxPlotMap.put(sexKey, boxPlotData);
		sexAndScatterMap.put(sexKey, scatterColumns);
		}//end of gender loop
		
		

		String chartString = createContinuousBoxPlotChartsString(chartId,
				categoriesListBoxChart, title,  yAxisTitle,
				sexAndBoxPlotMap, sexAndScatterMap);
		System.out.println("unichart="+chartString);
		// continuousCharts.add(chartString);
		ChartData cNTable = new ChartData();
		// cNTable.setTable(table);
		cNTable.setChart(chartString);
		return cNTable;
	}

	
	/**
	 * 
	 * @param title
	 *            main title of the graph
	 * @param yAxisTitle
	 *            - unit of measurement - how to get this from the db?
	 * @param sexAndBoxPlotMap
	 * @param sexAndScatterMap
	 * @param xAisxCcategoriesList
	 *            e.g. WT, WT, HOM, HOM for each column to be displayed
	 * @return
	 */
	private String createContinuousBoxPlotChartsString(String experimentNumber,
			List<String> xAxisCategoriesList, String title, String yAxisTitle, Map<SexType, List<List<Float>>> sexAndBoxPlotMap,
			Map<SexType, List<List<Float>>> sexAndScatterMap) {
		JSONArray categoriesArray = new JSONArray(xAxisCategoriesList);
		String categories = categoriesArray.toString();// "['WT', 'WT', 'HOM', 'HOM']";
		String femaleBoxPlotObject="";
		String femaleScatterObjectString="";
		String maleBoxPlotObject="";
		String maleScatterObjectString="";
		for(SexType sexKey: sexAndBoxPlotMap.keySet()){
		
		JSONArray boxPlot2DData = new JSONArray(sexAndBoxPlotMap.get(sexKey));
		String observationsString = boxPlot2DData.toString();// " [ [733, 853, 939, 980, 1080], [], [724, 802, 806, 871, 950], [] ]";//array
																// for each
																// column/category
																// WT HOM etc
		JSONArray scatterJArray = new JSONArray(sexAndScatterMap.get(sexKey));

		String scatterString = scatterJArray.toString();// "[ [1, 644], [3, 718], [3, 951], [3, 969] ]";//fist
														// number of pair
														// indicates
														// category/column so 0
														// is first column 3 is
														// second
		if(sexKey.equals(SexType.female)) {
		femaleBoxPlotObject="{ name: 'Observations',color: 'red', data:"
				+ observationsString
				+ ",       tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }                    }";
		
		femaleScatterObjectString="{ name: 'Observation', type: 'scatter', data: "
				+ scatterString
				+ ", marker: { fillColor: 'white', lineWidth: 1, lineColor: 'red' }, tooltip: { pointFormat: '{point.y:..4f}' }          }";
		}
		if(sexKey.equals(SexType.male)) {
		maleBoxPlotObject="{ name: 'Observations',color: 'blue', data:"
				+ observationsString
				+ ",       tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }                    }";
		
		maleScatterObjectString="{ name: 'Observation', type: 'scatter', data: "
				+ scatterString
				+ ", marker: { fillColor: 'white', lineWidth: 1, lineColor: 'blue' }, tooltip: { pointFormat: '{point.y:..4f}' }          }";
		}
		}//end of gender loop
		
		String dataStrings="";
		if(!maleScatterObjectString.equals("")&& !femaleScatterObjectString.equals("")) {//if male is defined add the objects together otherwise just use the female
			dataStrings=femaleBoxPlotObject+", "+femaleScatterObjectString+","+maleBoxPlotObject+", "+maleScatterObjectString;
		}else {
		if(!femaleScatterObjectString.equals("")) {//check we definitely have female data if so make this the data string
				dataStrings=femaleBoxPlotObject+", "+femaleScatterObjectString;
		}
		if(!maleScatterObjectString.equals("")) {
			dataStrings=maleBoxPlotObject+", "+maleScatterObjectString;
		}
		}
		
		
		String chartString = " chart = new Highcharts.Chart({ chart: { type: 'boxplot', renderTo: 'chart"
				+ experimentNumber
				+ "'},  tooltip: { formatter: function () { if(typeof this.point.high === 'undefined'){ return '<b>Observation</b><br/>' + this.point.y; } else { return '<b>Genotype: ' + this.key + '</b><br/>LQ - 1.5 * IQR: ' + this.point.low + '<br/>Lower Quartile: ' + this.point.options.q1 + '<br/>Median: ' + this.point.options.median + '<br/>Upper Quartile: ' + this.point.options.q3 + '<br/>UQ + 1.5 * IQR: ' + this.point.options.high + '</b>'; } } }    , title: { text: '"
				+ title
				+ "' } , credits: { enabled: false },  subtitle: { text: '"
				+ WordUtils.capitalize("title here")
				+ "', x: -20 }, legend: { enabled: false }, xAxis: {labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }}, categories:  "
				+ categories
				+ " }, \n"
				+ "yAxis: { labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }},title: { text: '"
				+ yAxisTitle
				+ "' } }, "
				+ "\n series: ["+dataStrings+"] }); });";
		
		return chartString;
	}

	public ChartData getHistogram(List<String> labels, List<Double> values, String title){
		double min = 0; 
		for (double val : values)
			if (val< min)
				min = val;
		String chartId = "histogram" + values.hashCode();
		String yTitle = "Number of strains";
		String javascript = "$(function () {    var chart; $(document).ready(function() {chart = new Highcharts.Chart({ chart: {  type: 'column' , renderTo: '"
				+ chartId
				+ "'},"+
           " title: { text: '" + title + "' },  subtitle: {   text: '' }, "+
           " xAxis: { categories: " + labels + " },"+
           " yAxis: { min: "+ min + ",  title: {  text: '"+yTitle+"'  }   },"+
           " tooltip: {"+
             "   headerFormat: '<span style=\"font-size:10px\">{point.key}</span><table>',"+
              "  pointFormat: '<tr><td style=\"color:{series.color};padding:0\">{series.name}: </td>' +"+
               "     '<td style=\"padding:0\"><b>{point.y:.1f} mm</b></td></tr>',"+
               " footerFormat: '</table>', shared: true,  useHTML: true  }, "+
           "  plotOptions: {   column: {  pointPadding: 0.2,  borderWidth: 0  }  }," +
               "   series: [{ name: 'Mutants',  data: "+
           values + "  }]"+
               " });  }); });";
		ChartData chartAndTable=new ChartData();
		chartAndTable.setChart(javascript);
		chartAndTable.setId(chartId);
//		System.out.println("... histogram with id " + chartId);
		return chartAndTable;
	}
	
	public ChartData getStackedHistogram(Map<String, List<Double>> map, String title, String xLabel){
	//	http://jsfiddle.net/gh/get/jquery/1.9.1/highslide-software/highcharts.com/tree/master/samples/highcharts/demo/column-stacked/
		if (map == null){
			return new ChartData();
		}
		List<Double> control  = map.get("control");
		List<Double> mutant  = map.get("mutant");					
		List<String> labels = new ArrayList<String> (); 
		DecimalFormat df = new DecimalFormat("#.##");
		for (double label : map.get("labels")){
			labels.add("'" + df.format(label) + "'");
		}
		double min = 0; 
		for (double val : mutant)
			if (val< min)
				min = val;
		for (double val : control)
			if (val< min)
				min = val;
		String chartId = "column-stacked" + mutant.hashCode();
		String yTitle = "Number of strains";
		String javascript = "$(document).ready(function() {chart = new Highcharts.Chart({ chart: {  type: 'column' , renderTo: '"
				+ chartId
				+ "'},"+
           " title: { text: '" + title + "' },"+
           " credits: { enabled: false },"+
           " xAxis: { categories: " + labels + ", labels: {rotation: -45} , title: { text: '" + xLabel + "'} },"+
           " yAxis: { min: "+ min + ",  title: {  text: '"+yTitle+"'  }, stackLabels: { enabled: false}  }," +
           " tooltip: { formatter: function() { return ''+  this.series.name +': '+ this.y +'<br/>'+ 'Total: '+ this.point.stackTotal;  }  }, " +
           " plotOptions: { column: {  stacking: 'normal',  dataLabels: { enabled: false} } }," +
           " series: [{ name: 'Mutant strains with this phenotype called',  data: " +  mutant + "  }, {name: 'Mutant strains with no calls for this phenotype', data: " + control + "}]" +  " });  }); ";
		ChartData chartAndTable = new ChartData();
		chartAndTable.setChart(javascript);
		chartAndTable.setId(chartId);
//		System.out.println("... column-stacked with id " + chartId);
//		System.out.println("and the mutants were : " + mutant);
		return chartAndTable;
		
		/*
		 * $(function () {
        $('#container').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: 'Stacked column chart'
            },
            xAxis: {
                categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Total fruit consumption'
                },
                stackLabels: {
                    enabled: true,
                    style: {
                        fontWeight: 'bold',
                        color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                    }
                }
            },
            legend: {
                align: 'right',
                x: -70,
                verticalAlign: 'top',
                y: 20,
                floating: true,
                backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColorSolid) || 'white',
                borderColor: '#CCC',
                borderWidth: 1,
                shadow: false
            },
            tooltip: {
                formatter: function() {
                    return '<b>'+ this.x +'</b><br/>'+
                        this.series.name +': '+ this.y +'<br/>'+
                        'Total: '+ this.point.stackTotal;
                }
            },
            plotOptions: {
                column: {
                    stacking: 'normal',
                    dataLabels: {
                        enabled: true,
                        color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
                    }
                }
            },
            series: [{
                name: 'John',
                data: [5, 3, 4, 7, 2]
            }, {
                name: 'Jane',
                data: [2, 2, 3, 2, 1]
            }, {
                name: 'Joe',
                data: [3, 4, 4, 2, 5]
            }]
        });
    });
		 * 
		 * */		
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
	private List<UnidimensionalStatsObject> produceUnidimensionalStatsData(
			String title, Map<SexType, List<List<Float>>> genderAndRawDataMap,
			 ExperimentDTO experiment, String allelicCompositionString, String symbol, String geneticBackground) {
		// http://localhost:8080/phenotype-archive/stats/genes/MGI:1929878?parameterId=ESLIM_015_001_018
		// logger.debug("experiment="+experiment);
		List<? extends StatisticalResult> results = experiment.getResults();
		// logger.debug("result="+result);
		List<UnidimensionalStatsObject> statsObjects = new ArrayList<UnidimensionalStatsObject>();
	
		for(SexType sexType: genderAndRawDataMap.keySet()){
			
			// Set up the controls data
			UnidimensionalStatsObject wtStatsObject = new UnidimensionalStatsObject();
			wtStatsObject.setSampleSizeFemale(experiment.getControlSampleSizeFemale());
			wtStatsObject.setSampleSizeMale(experiment.getControlSampleSizeMale());
			wtStatsObject.setSexType(sexType);
			statsObjects.add(wtStatsObject);
			
			//set up the mutant stats data
			
		for (ZygosityType zType : experiment.getZygosities()) {
				UnidimensionalStatsObject tempStatsObject = new UnidimensionalStatsObject();
				
				if (zType.equals(ZygosityType.homozygote)) {// if homozygote
															// don't need the
															// second part of
															// the string after
															// the forward slash
				//	alleleComposition = allelicCompositionString.substring(0,
						//	alleleComposition.indexOf("/"));
				}
				if(sexType.equals(SexType.female)) {
					tempStatsObject.setSampleSizeFemale(experiment.getMutants(sexType, zType).size());
					}else {
						tempStatsObject.setSampleSizeMale(experiment.getMutants(sexType, zType).size());
					}
				
			
				for (StatisticalResult result : results) {
					if (result.getZygosityType().equals(zType)
							&& result.getSexType().equals(sexType)) {
						tempStatsObject.setResult((UnidimensionalResult) result);
					}
				}
				
				tempStatsObject.setZygosity(zType);
				tempStatsObject.setLine(allelicCompositionString);
				
				tempStatsObject.setAllele(symbol);
				
				tempStatsObject.setGeneticBackground(geneticBackground);
				tempStatsObject.setSexType(sexType);
				statsObjects.add(tempStatsObject);
				
			
		}
		

		//set the mean and standard dev for the stats objects just set up above using the row in the table row
//		int decimalPlaces = ChartUtils.getDecimalPlaces(experiment);
//		int row = 0;
//		for (List<Float> listOfFloats : genderAndRawDataMap.get(sexType)) {
//			// Get a DescriptiveStatistics instance
//			DescriptiveStatistics stats = new DescriptiveStatistics();
//			UnidimensionalStatsObject statsObject = statsObjects.get(row);
//			// Add the data from the array
//			for (Float point : listOfFloats) {
//				stats.addValue(point);
//			}
//			if (listOfFloats.size() > 0) {
//				Float mean = ChartUtils.getDecimalAdjustedFloat(
//						new Float(stats.getMean()), decimalPlaces);
//				//System.out.println("mean=" + mean);
//				Float sd = ChartUtils.getDecimalAdjustedFloat(
//						new Float(stats.getStandardDeviation()), decimalPlaces);
//				statsObject.setMean(mean);
//				statsObject.setSd(sd);
//			}
//			// sample size for unidimensional controls is both male and female
//			// so ok under unidimensional but scatter shows time_series as well
//			// so in the scatter we should show number of male or female
//			// if use ilincas new code for experiments this wont' be an issue.
//			row++;
//		}
		}//end of sexType
		return statsObjects;
	}
}
