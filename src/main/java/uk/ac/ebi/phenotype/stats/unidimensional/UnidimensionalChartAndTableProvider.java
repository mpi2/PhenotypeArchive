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

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.hibernate.cfg.annotations.Nullability;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

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
import uk.ac.ebi.phenotype.stats.StackedBarsData;
import uk.ac.ebi.phenotype.stats.graphs.ChartColors;
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
			Parameter parameter,
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

	
				
					// category e.g normal, abnormal
					Map<SexType, List<List<Float>>> genderAndRawDataMap=new HashMap<SexType, List<List<Float>>>();
					List<ChartsSeriesElement>chartsSeriesElementsList=new ArrayList<ChartsSeriesElement>();
					for (SexType sexType : experiment.getSexes()) { 
						List<List<Float>> rawData = new ArrayList<List<Float>>();
					List<Float> controlCounts = new ArrayList<Float>();
					List<MouseDataPoint> controlMouseDataPoints = new ArrayList<>();
					// unidimensionalStatisticsDAO
					// .getControlDataPointsWithMouseName(popId);

					// loop over the control points and add them
List<Float>dataFloats=new ArrayList<>();
					for (ObservationDTO control : experiment.getControls(sexType)) {
						
						Float dataPoint = control.getDataPoint();
						dataFloats.add(dataPoint);
						
						// logger.debug("adding control point="+dataPoint);

					}
					
					//set up for WT for this sex
					ChartsSeriesElement tempElement=	new ChartsSeriesElement();
					tempElement.setSexType(sexType);
					tempElement.setZygosityType(null);
					tempElement.setOriginalData(dataFloats);
					//tempElement.setColumn(columnIndex);
					chartsSeriesElementsList.add( tempElement);
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
								Float dataPoint = expDto.getDataPoint();
								mutantCounts.add(dataPoint);
							}
							ChartsSeriesElement tempElementExp=	new ChartsSeriesElement();
							tempElementExp.setSexType(sexType);
							tempElementExp.setZygosityType(zType);
							tempElementExp.setOriginalData(mutantCounts);
							chartsSeriesElementsList.add( tempElementExp);
							rawData.add(mutantCounts);
					}
					
					genderAndRawDataMap.put(sexType, rawData);				
					}//end of sextype loop
					Map<String,String>usefulStrings=GraphUtils.getUsefulStrings(expBiologicalModel);
					List<UnidimensionalStatsObject> unidimensionalStatsObject = produceUnidimensionalStatsData(
							parameter,genderAndRawDataMap,
							experiment, usefulStrings.get("allelicComposition"), usefulStrings.get("symbol"), usefulStrings.get("geneticBackground"));
					
					unidimensionalStatsObjects
					.addAll(unidimensionalStatsObject);
					chartAndTable = processChartData(chartId, parameter, 
							experiment, yAxisTitle, chartsSeriesElementsList);
			
			unidimensionalDataSet
					.setChartData(chartAndTable);
			unidimensionalDataSet
					.setAllUnidimensionalResults(allUnidimensionalResults);
			unidimensionalDataSet.setStatsObjects(unidimensionalStatsObjects);
			unidimensionalDataSets.add(unidimensionalDataSet);
		return unidimensionalDataSet;

	}

	

	/**
	 * 
	 * @param chartsSeriesElementsList 
	 * @param parameterUnit
	 * @param xAxisCategoriesList
	 *            - bare categories from database e.g. WT, HOM
	 * @param continuousBarCharts
	 */
	private ChartData processChartData(String chartId, Parameter parameter,
			ExperimentDTO experiment, String yAxisTitle, List<ChartsSeriesElement> chartsSeriesElementsList) {
	
		String chartString = createContinuousBoxPlotChartsString(chartId,
				 parameter,  yAxisTitle,
				 chartsSeriesElementsList, experiment);
		ChartData cNTable = new ChartData();
		cNTable.setChart(chartString);
		return cNTable;
	}

	
	/**
	 * 
	 * @param parameter.getStableId()
	 *            main title of the graph
	 * @param yAxisTitle
	 *            - unit of measurement - how to get this from the db?
	 * @param sexAndScatterMap
	 * @param chartsSeriesElementsList 
	 * @param xAisxCcategoriesList
	 *            e.g. WT, WT, HOM, HOM for each column to be displayed
	 * @return
	 */
	private String createContinuousBoxPlotChartsString(String experimentNumber,
			Parameter parameter, String yAxisTitle, List<ChartsSeriesElement> chartsSeriesElementsList, ExperimentDTO experiment) {
		
		//System.out.println("chartSeriesElements="+chartsSeriesElements);
		//JSONArray categoriesArray = new JSONArray(xAxisCategoriesList);
		JSONArray categories =new JSONArray();// "['WT', 'WT', 'HOM', 'HOM']";
		String femaleBoxPlotObject="";
		String femaleScatterObjectString="";
		
		String seriesData="";
		int decimalPlaces = ChartUtils.getDecimalPlaces(experiment);	
		int column=0;
		Float min=1000000000f;
		Float max=0f;
		
		//loop over the chartSeries data and create boxplots for each
		for(ChartsSeriesElement chartsSeriesElement: chartsSeriesElementsList){
			//fist get the raw data for each column (only one column per data set at the moment as we will create both the scatter and boxplots here
//			// Get a DescriptiveStatistics instance
			String categoryString=chartsSeriesElement.getSexType().toString()+" "+chartsSeriesElement.getControlOrZygosityString();
			categories.put(categoryString);
			List<Float> listOfFloats= chartsSeriesElement.getOriginalData();
			DescriptiveStatistics stats = new DescriptiveStatistics();
			//load up the stats object
			for (Float point : listOfFloats) {
				stats.addValue(point);
				if(point > max)max=point;
				if(point < min)min=point;
			}
			
			//get boxplot data here
			// use the stats object to get the mean upper quartile etc
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
					Float q1=new Float(Q1);
					wt1.add(q1);// lower quartile
					if(minIQR < min)min=minIQR;
	
					Float decFloat = ChartUtils.getDecimalAdjustedFloat(new Float(
							stats.getMean()), decimalPlaces);
					wt1.add(decFloat);// median
					Float q3=new Float(Q3);
					wt1.add(q3);// upper quartile
					Float maxIQR = ChartUtils.getDecimalAdjustedFloat(new Float(Q3
							+ (1.5 * IQR)), decimalPlaces);
					wt1.add(maxIQR);// maximumbs.
					if(maxIQR > max)max=maxIQR;
					chartsSeriesElement.setBoxPlotArray(new JSONArray(wt1));
				}
				
				JSONArray boxPlot2DData = chartsSeriesElement.getBoxPlotArray();
                                if(boxPlot2DData==null){
                                    System.err.println("error no boxplot data for this chartSeriesElemen="+chartsSeriesElement.getName());
                                    boxPlot2DData=new JSONArray();
                                }
				
				String columnPadding="";
				for(int i=0;i<column;i++) {
					//add an empty column for each column
				columnPadding+="[], ";	
				}
				String observationsString = "["+columnPadding+boxPlot2DData.toString()+"]";// " [ [733, 853, 939, 980, 1080], [], [724, 802, 806, 871, 950], [] ]";//array
																		// for each
																		// column/category
																		// WT HOM etc
				//get the color based on if mutant or WT based on terrys ticket MPII-504
				String color=ChartColors.getMutantColor(ChartColors.alphaBox);
				if(chartsSeriesElement.getControlOrZygosityString().equals("WT")) {
					color=ChartColors.getWTColor(ChartColors.alphaScatter);
				}
				
			femaleBoxPlotObject="{"
					+" color: '"+color+"' ,"
					+" name: 'Observations', data:"
					+ observationsString
					+ ",       tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }                    }";
			
			seriesData+=femaleBoxPlotObject+",";
			column++;
				
		}//end of boxplot loop
			
			
				
		//loop over the chartSeries data and create scatters for each
		for(ChartsSeriesElement chartsSeriesElement: chartsSeriesElementsList){
					String categoryString=chartsSeriesElement.getSexType().toString()+" "+chartsSeriesElement.getControlOrZygosityString();
				
				//for the scatter loop over the original data and assign a column as the first element for each array
			
			List<Float> originalDataFloats=chartsSeriesElement.getOriginalData();
			JSONArray scatterJArray = new JSONArray();
			categories.put(categoryString);//add another category string for this scatter as well as the one for boxplot already added
			//int column=1;//chartsSeriesElement.getColumn();
			for(Float data: originalDataFloats) {
				JSONArray array=new JSONArray();
				array.put(column);
				array.put(data);
				scatterJArray.put(array);
			}
			
//			if(chartsSeriesElement.getControlOrZygosity().equalsIgnoreCase("WT")) {
//				color=ChartColors.getWTColor(ChartColors.alphaScatter);
//				fillColor="white";
//				lineColor=color;
//			}
//			
//			if(chartsSeriesElement.getSexType().equals(SexType.male) ) {
//				symbol="triangle";
//			}
			
			String marker = ChartColors.getMarkerString(chartsSeriesElement.getSexType(), chartsSeriesElement.getZygosityType() );

				String scatterString = scatterJArray.toString();// "[ [1, 644], [3, 718], [3, 951], [3, 969] ]";//fist
																// number of pair
																// indicates
																// category/column so 0
																// is first column 3 is
																// second
				femaleScatterObjectString="{ "
					+" 	name: 'Observation', type: 'scatter', data: "
				+ scatterString
				+ ", " 
				+marker+
				//"marker: { lineWidth: 1}" +
				", tooltip: { pointFormat: '{point.y:..4f}' }" +
				"          }";
		seriesData+=femaleScatterObjectString+",";//+","+maleBoxPlotObject+", "+maleScatterObjectString;
				column++;
			
		
	}//end of scatter loop
		List<String> colors=ChartColors.getFemaleMaleColorsRgba(ChartColors.alphaBox);
		JSONArray colorArray = new JSONArray(colors);
		String chartString = " chart = new Highcharts.Chart({ " 
				+" colors:"+colorArray
				+", chart: { type: 'boxplot', renderTo: 'chart"
				+ experimentNumber
				+ "'},  tooltip: { formatter: function () { if(typeof this.point.high === 'undefined'){ return '<b>Observation</b><br/>' + this.point.y; } else { return '<b>Genotype: ' + this.key + '</b><br/>LQ - 1.5 * IQR: ' + this.point.low + '<br/>Lower Quartile: ' + this.point.options.q1 + '<br/>Median: ' + this.point.options.median + '<br/>Upper Quartile: ' + this.point.options.q3 + '<br/>UQ + 1.5 * IQR: ' + this.point.options.high + '</b>'; } } }    , title: { text: '"
				+ parameter.getName()
				+ "' } , credits: { enabled: false },  subtitle: { text: '"
				+ parameter.getStableId()
				+ "', x: -20 }, legend: { enabled: false }, xAxis: {labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }}, categories:  "
				+ categories
				+ " }, \n"
				+
				" plotOptions: {"
					+"series:"
					+"{ groupPadding: 0.45, pointPadding: -1.5 }"
					+"},"
				+ "yAxis: { " +
				"max: "+max+",  min: "+min+","
				+"labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }},title: { text: '"
				+ yAxisTitle
				+ "' } }, "
				+ "\n series: ["+seriesData+"] }); });";
		
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
	
	public ChartData getStackedHistogram(StackedBarsData map, Parameter parameter){
	//	http://jsfiddle.net/gh/get/jquery/1.9.1/highslide-software/highcharts.com/tree/master/samples/highcharts/demo/column-stacked/
		if (map == null){
			return new ChartData();
		}
		String title = parameter.getName();
		String subtitle = parameter.getStableId();
		String xLabel = parameter.getUnit();
		ArrayList<Double> control  = map.getControlMutatns();
		ArrayList<Double> mutant  = map.getPhenMutants();					
		ArrayList<String> labels = new ArrayList<String> (); 				
		ArrayList<String> controlGenes = map.getControlGenes(); 				
		ArrayList<String> mutantGenes = map.getMutantGenes(); 			
		ArrayList<String> controlGenesUrl = map.getControlGeneAccesionIds(); 				
		ArrayList<String> mutantGenesUrl = map.getMutantGeneAccesionIds(); 
		DecimalFormat df = new DecimalFormat("#.##");
		ArrayList<Double> upperBounds = map.getUpperBounds();
		for (int i = 0; i < upperBounds.size(); i ++){
			String c = controlGenes.get(i);
			String controlG = "";
			if (c.length() > 50){
				int len = 0;
				for (String gene : c.split(" " )){
					controlG += gene + " " ;
					len += gene.length();
					if (len > 50){
						controlG += "<br/>";
						len = 0;
					}
				}
				controlG = controlG;
			}
			else controlG = c ;
			labels.add("'" + df.format(upperBounds.get(i)) + "###" + controlG + "###" + mutantGenes.get(i) + "###" + controlGenesUrl.get(i) + "###" + mutantGenesUrl.get(i) + "'");
		}
		double min = 0; 
		for (double val : mutant)
			if (val< min)
				min = val;
		for (double val : control)
			if (val< min)
				min = val;
		// http://www.highcharts.com/demo/line-ajax
		// http://jsfiddle.net/gh/get/jquery/1.7.2/highslide-software/highcharts.com/tree/master/samples/highcharts/plotoptions/series-point-events-click-column/
		// http://jsfiddle.net/gh/get/jquery/1.7.2/highslide-software/highcharts.com/tree/master/samples/highcharts/xaxis/labels-formatter-linked/
		ArrayList <String>  controlUrls = new ArrayList<>();
		
		String chartId = parameter.getStableId();
		String yTitle = "Number of strains";
		String javascript = "$(document).ready(function() {" 
				+ "chart = new Highcharts.Chart({ colors:['rgba(239, 123, 11,0.7)','rgba(9, 120, 161,0.7)'], chart: {  type: 'column' , renderTo: 'single-chart-div'},"+
           " title: { text: '" + title + "' },"+
           " subtitle: { text: '" + subtitle +"'}," +
           " credits: { enabled: false },"+
    		" xAxis: { categories: " + labels + ", "
    				+ "labels: {formatter:function(){ return this.value.split('###')[0]; }, rotation: -45} , title: { text: '" + xLabel + "'} },"+
           " yAxis: { min: "+ min + ",  title: {  text: '"+yTitle+"'  }, stackLabels: { enabled: false}  }," +
           " tooltip: { "
           		+ "formatter: function() { "
           		+ "if ('Mutant strains with no calls for this phenotype' === this.series.name )"
           		+ "return ''+  this.series.name +': '+ this.y + ' out of '+ this.point.stackTotal + '<br/>Genes: ' +  this.x.split('###')[1];  "
           		+ "else return ''+  this.series.name +': '+ this.y + ' out of '+ this.point.stackTotal + '<br/>Genes: ' +  this.x.split('###')[2];}  }, " +
           " plotOptions: { column: {  stacking: 'normal',  dataLabels: { enabled: false} }, "
           		+ "series: { cursor: 'pointer', point: { events: { click: function() { "
           		+ "var url = document.URL.split('/phenotypes/')[0];"
           			+ "if ('Mutant strains with no calls for this phenotype' === this.series.name) {"
           				+ "url += '/charts?' + this.category.split('###')[3];"
           			+ "} else {" 
           				+ "url += '/charts?' + this.category.split('###')[4];"
           			+ "} "
           		+ "url += '&parameter_stable_id=" + parameter.getStableId() + "';"
           		+ "window.open(url); "
           		+ "console.log(url);"
           		+ "} } } }" 
           		+ "} ," +
           " series: [{ name: 'Mutant strains with this phenotype called',  data: " +  mutant + "  }, {name: 'Mutant strains with no calls for this phenotype', data: " + control + "}]" +  " });  }); ";
		ChartData chartAndTable = new ChartData();
		chartAndTable.setChart(javascript);
		chartAndTable.setId(chartId);
		System.out.println(javascript);
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
	private List<UnidimensionalStatsObject> produceUnidimensionalStatsData(
			Parameter parameter, Map<SexType, List<List<Float>>> genderAndRawDataMap,
			 ExperimentDTO experiment, String allelicCompositionString, String symbol, String geneticBackground) {
		// http://localhost:8080/phenotype-archive/stats/genes/MGI:1929878?parameterId=ESLIM_015_001_018
		// logger.debug("experiment="+experiment);
		List<? extends StatisticalResult> results = experiment.getResults();
		logger.debug("result="+results);
		List<UnidimensionalStatsObject> statsObjects = new ArrayList<UnidimensionalStatsObject>();
	
		for(SexType sexType: genderAndRawDataMap.keySet()){
			
			// Set up the controls data
			UnidimensionalStatsObject wtStatsObject = new UnidimensionalStatsObject();
			Set<ObservationDTO> controls = experiment.getControls(sexType);

			wtStatsObject=genrateStats(experiment, wtStatsObject, controls, null, sexType);
			statsObjects.add(wtStatsObject);
			
			//set up the mutant stats data
			
		for (ZygosityType zType : experiment.getZygosities()) {
				UnidimensionalStatsObject tempStatsObject = new UnidimensionalStatsObject();
				
				Set<ObservationDTO> mutants = experiment.getMutants(sexType, zType);
				tempStatsObject=genrateStats(experiment, tempStatsObject, mutants, zType,sexType);
					
			for (StatisticalResult result : results) {
				UnidimensionalResult unidimensionalResult=(UnidimensionalResult) result;
				System.out.println("sex is "+sexType+" | result sex type="+result.getSexType()+"pValue="+unidimensionalResult.getpValue()+"result zyg="+unidimensionalResult.getZygosityType()+"  ztype="+zType);
					if (result.getZygosityType().equals(zType)) {
						tempStatsObject.setResult((UnidimensionalResult) result);
					}
				}
				
				tempStatsObject.setLine(allelicCompositionString);
				tempStatsObject.setAllele(symbol);
				tempStatsObject.setGeneticBackground(geneticBackground);
				statsObjects.add(tempStatsObject);

				
				// sample size for unidimensional controls is both male and female
				// so ok under unidimensional but scatter shows time_series as well
				// so in the scatter we should show number of male or female
				// if use ilincas new code for experiments this wont' be an issue.
				
			
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

	private UnidimensionalStatsObject genrateStats(ExperimentDTO experiment,
			UnidimensionalStatsObject tempStatsObject,
			Set<ObservationDTO> mutants, ZygosityType zygosity, SexType sexType) {
		
		tempStatsObject.setSampleSize(mutants.size());		
		//do the stats to get mean and SD
		// Get a DescriptiveStatistics instance
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// Add the data
		for (ObservationDTO mutantObservationDTO : mutants) {
			stats.addValue(mutantObservationDTO.getDataPoint());
		}
		if (mutants.size() > 0) {
			int decimalPlaces = ChartUtils.getDecimalPlaces(experiment);
			Float mean = ChartUtils.getDecimalAdjustedFloat(
					new Float(stats.getMean()), decimalPlaces);
			//System.out.println("mean=" + mean);
			Float sd = ChartUtils.getDecimalAdjustedFloat(
					new Float(stats.getStandardDeviation()), decimalPlaces);
			tempStatsObject.setMean(mean);
			tempStatsObject.setSd(sd);
			if(zygosity!=null) {
			tempStatsObject.setZygosity(zygosity);
			}
			if(sexType!=null) {
				tempStatsObject.setSexType(sexType);
			}
		}
		//end of stats creation for table
		return tempStatsObject;
	}
}
