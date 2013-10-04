package uk.ac.ebi.phenotype.stats.timeseries;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.dao.BiologicalModelDAO;
import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.dao.TimeSeriesStatisticsDAO;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.ChartData;
import uk.ac.ebi.phenotype.stats.ChartUtils;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
import uk.ac.ebi.phenotype.stats.JSONGraphUtils;
import uk.ac.ebi.phenotype.stats.MouseDataPoint;
import uk.ac.ebi.phenotype.stats.ObservationDTO;
import uk.ac.ebi.phenotype.stats.TableObject;

@Service
public class TimeSeriesChartAndTableProvider {
	private static final Logger logger = Logger.getLogger(TimeSeriesChartAndTableProvider.class);

	@Autowired
	private TimeSeriesStatisticsDAO timeSeriesStatisticsDAO;
	
	 SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");

	public List<ChartData> doTimeSeriesData(BiologicalModelDAO bmDAO, List<ExperimentDTO> experiments, Parameter parameter, Model model, List<String> genderList, List<String> zyList, int listIndex,
			List<String> biologicalModelsParams) throws IOException, URISyntaxException {
		// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1920000?parameterId=ESLIM_004_001_002
		Float max=new Float(0);
		Float min=new Float(1000000000);
		List<ChartData> chartsNTablesForParameter=new ArrayList<ChartData>();

		//maybe need to put these into method that can be called as repeating this - so needs refactoring though there are minor differences?
		
		
		
		
			for (ExperimentDTO experiment : experiments) {
				BiologicalModel expBiologicalModel=bmDAO.getBiologicalModelById(experiment.getExperimentalBiologicalModelId());
				//timeSeriesMutantBiologicalModels.add(expBiologicalModel);
		Map<String, List<DiscreteTimePoint>> lines = new HashMap<String, List<DiscreteTimePoint>>();

					
					for (SexType sex : experiment.getSexes()) { // one graph for each sex if
						// unspecified in params to
						// page or in list of sex
						// param specified
						
					if (genderList.isEmpty()
							|| genderList.contains(sex.name())) {
						List<List<Float>> errorBarsPairs = null;

						
						List<DiscreteTimePoint> controlDataPoints=new ArrayList<>();
						 //loop over the control points and add them
						
						 for(ObservationDTO control: experiment.getControls() ) {
							
							 //get the attributes of this data point
							 //We don't want to split controls by gender on Unidimensional data
							// SexType docSexType=SexType.valueOf(ctrlDoc.getString("gender"));
							// ZygosityType zygosityType=ZygosityType.valueOf(ctrlDoc.getString("zygosity"));
							 String docGender=control.getSex();
							 if(SexType.valueOf(docGender).equals(sex)){
							 Float dataPoint=control.getDataPoint();
							 String timePointString=control.getTimePoint();
							 //System.out.println("timePointString="+timePointString);
							 Float discreteTimePoint=control.getDiscretePoint();//TimePoint();
								 //long timeInMillisSinceEpoch = getEpocTime(timePoint);
								 controlDataPoints.add(new DiscreteTimePoint(discreteTimePoint ,dataPoint));//new Float(dataPoint));
							//controlMouseDataPoints.add(new MouseDataPoint("Need MouseIds from Solr",new Float(dataPoint)));
							//System.out.println("adding control point time="+timePoint+" epoc="+timeInMillisSinceEpoch+" datapoint="+dataPoint);
							//controlMouseDataPoints.add(new MouseDataPoint("uknown", new Float(dataPoint)));
							 }
						 }
						logger.debug("finished putting control to data points");
						 TimeSeriesStats stats=new TimeSeriesStats();
						 List<DiscreteTimePoint> controlMeans= stats.getMeanDataPoints(controlDataPoints);
					//List<DiscreteTimePoint> controlData = timeSeriesStatisticsDAO
					//			.getControlStats(sexType, parameter, popId);
						
//				TimeSeriesStats controlStats = new TimeSeriesStats(
//						controlData);
//						List<DiscreteTimePoint> controlMeans = controlStats
//								.getMeans();
						//logger.debug("control means=" + controlMeans);
						lines.put("Control", controlMeans);
						for (ZygosityType zType : experiment.getZygosities()) {
							if (zyList.isEmpty()
									|| zyList.contains(zType.name())) {
							List<DiscreteTimePoint> mutantData=new ArrayList<>();// = timeSeriesStatisticsDAO
//										.getMutantStats(sexType, zType, parameter,
//												popId);
							//	logger.debug("mutantCounts=" + mutantData);
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
								 //We don't want to split controls by gender on Unidimensional data
								// SexType docSexType=SexType.valueOf(ctrlDoc.getString("gender"));
								// ZygosityType zygosityType=ZygosityType.valueOf(ctrlDoc.getString("zygosity"));
								 String docGender=expDto.getSex();
								 if(SexType.valueOf(docGender).equals(sex)){
								 Float dataPoint=expDto.getDataPoint();
								 Float discreateTimePoint=expDto.getDiscretePoint();//getTimePoint();
									// long timeInMillisSinceEpoch = getEpocTime(timePoint);
								//Float discreteTime=;
									 mutantData.add(new DiscreteTimePoint(discreateTimePoint ,new Float(dataPoint)));//new Float(dataPoint));
								//controlMouseDataPoints.add(new MouseDataPoint("Need MouseIds from Solr",new Float(dataPoint)));
								//System.out.println("adding control point time="+timePoint+" epoc="+timeInMillisSinceEpoch+" datapoint="+dataPoint);
								//controlMouseDataPoints.add(new MouseDataPoint("uknown", new Float(dataPoint)));
								 }
							 }	 
							 
							 logger.debug("doing mutant data");
							 List<DiscreteTimePoint> mutantMeans= stats.getMeanDataPoints(mutantData);	
								lines.put(WordUtils.capitalize(zType.name()),
										mutantMeans);

							}
						}

						String title = "Mean " + parameter.getName();
						if(lines.size()>1){//if lines are greater than one i.e. more than just control create charts and tables
						ChartData chartNTableForParameter=creatDiscretePointTimeSeriesChart(listIndex,
								title, lines, parameter.checkParameterUnit(1),
								parameter.checkParameterUnit(2), sex);
						Float tempMin=chartNTableForParameter.getMin();
						Float tempMax=chartNTableForParameter.getMax();
						chartNTableForParameter.setExpBiologicalModel(expBiologicalModel);
						if(tempMin<min)min=tempMin;
						if(tempMax>max)max=tempMax;
						chartsNTablesForParameter.add(chartNTableForParameter);
						listIndex++;
						}
					}// end of gender
				//}

			}// end of biological model param
		}//end of sex loop
		
			
		
		//min=allMinMax.get("min"); 
		//max=allMinMax.get("max");
			//for time series we always want min to be zero?? maybe some are negative?
			if(min>0)min=new Float(0);
		logger.debug("min="+min+" max="+max);
		List<ChartData> yAxisAdjustedTimeSeriesCharts=ChartUtils.alterMinAndMaxYAxisOfCharts(chartsNTablesForParameter, min, max);
		return yAxisAdjustedTimeSeriesCharts;
	}



	private long getEpocTime(String timeString) {
		
		java.util.Date date = null;
		try {
			date = sdf.parse(timeString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 long timeInMillisSinceEpoch = date.getTime(); 
		 long timeInMinutesSinceEpoch = timeInMillisSinceEpoch / (60 * 1000);
		return timeInMinutesSinceEpoch;
	}



	/**
	 * Creates a single chart and adds it to the chart array that is then added
	 * to the model by the main method
	 * 
	 * @param model
	 * @param timeSeriesCharts
	 * @param title
	 * @param lines
	 * @param xUnitsLabel
	 * @param yUnitsLabel
	 * @param sex
	 * @return
	 */
	private ChartData creatDiscretePointTimeSeriesChart(
			int listIndex, String title,
			Map<String, List<DiscreteTimePoint>> lines, String xUnitsLabel,
			String yUnitsLabel, SexType sex) {
		int size = listIndex;// to know which div to render to
												// not 0 index as using loop
												// count in jsp
		
		JSONArray series = new JSONArray();
		String seriesString="";
		Set<Float> categoriesSet = new HashSet<Float>();
		Float maxForChart=new Float(0);
		Float minForChart=new Float(1000000000);
		Map<String, Float> minMax=new HashMap<String,Float>();

		// { name: 'Confidence', type: 'errorbar', color: 'black', data: [ [7.5,
		// 8.5], [2.8, 4], [1.5, 2.5], [3, 4.1], [6.5, 7.5], [3.3, 4.1], [4.8,
		// 5.1], [2.2, 3.0], [5.1, 8] ] }

		try {
			int i = 0;
			for (String key : lines.keySet()) {// key is control hom or het
				JSONObject object = new JSONObject();
				JSONArray data = new JSONArray();
				object.put("name", key);

				JSONObject errorBarsObject = null;
				try {
					errorBarsObject = new JSONObject();// "{ name: 'Confidence', type: 'errorbar', color: 'black', data: [ [7.5, 8.5], [2.8, 4], [1.5, 2.5], [3, 4.1], [6.5, 7.5], [3.3, 4.1], [4.8, 5.1], [2.2, 3.0], [5.1, 8] ] } ");
					errorBarsObject.put("name", "Standard Deviation");
					errorBarsObject.put("type", "errorbar");
					//errorBarsObject.put("tooltip", "pointFormat: '(error range: {point.low}-{point.high}°C)<br/>' ");
					//errorBarsObject.append("tooltip", "pointFormat: '(error range: {point.low}-{point.high}°C)<br/>' ");
					
					String color = "blue";
					if (i % 2 == 0) {
						color = "black";
					}
					errorBarsObject.put("color", color);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				JSONArray errorsDataJson = new JSONArray();
				for (DiscreteTimePoint pt : lines.get(key)) {
					JSONArray pair = new JSONArray();
					pair.put(pt.getDiscreteTime());
					pair.put(pt.getData());
					categoriesSet.add(pt.getDiscreteTime());
					data.put(pair);
					// set the error bars
					JSONArray errorBarsJ = new JSONArray();
					errorBarsJ.put(pt.getDiscreteTime());
					errorBarsJ.put(pt.getErrorPair().get(0));
					errorBarsJ.put(pt.getErrorPair().get(1));
					if(pt.getErrorPair().get(0) > maxForChart)maxForChart=pt.getErrorPair().get(0);
					if(pt.getErrorPair().get(1) > maxForChart)maxForChart=pt.getErrorPair().get(1);
					if(pt.getErrorPair().get(0) < minForChart)minForChart=pt.getErrorPair().get(0);
					if(pt.getErrorPair().get(1) < minForChart)minForChart=pt.getErrorPair().get(1);
					minMax.put("max", maxForChart);
					logger.debug("minForChart timeseries="+minForChart);
					minMax.put("min",minForChart);
					errorsDataJson.put(errorBarsJ);

					errorBarsObject.put("data", errorsDataJson);

				}
				object.put("data", data);
				series.put(object);
				//we now want to add different tooltips for thses data sets which we can't do for java json objects so we need to deal with strings sooner
				
				series.put(errorBarsObject);
//				System.out.println("object for point data="+object);
//				System.out.println("errorbars="+errorBarsObject);
//				if(i==0) {
//					seriesString+=",";
//				}
//				seriesString+=","+object.toString()+","+errorBarsObject.toString();
//				System.out.println("seriesString="+seriesString);
				// categoriesMap.put(key);
				i++;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// need to add error bars to series data as well!
		// sort the categories by time as means are all sorted already

		List<Float> cats = new ArrayList<Float>();
		for (Float cat : categoriesSet) {
			cats.add(cat);
		}
		Collections.sort(cats);
		String noDecimalsString="";
		if(xUnitsLabel.equals("number")) {
			//set the xAxis to be numbers with no decimals
			noDecimalsString="allowDecimals:false,";
		}
		
		logger.warn("series="+series);
		String errorBarsToolTip="tooltip: { pointFormat: '(error range: {point.low}-{point.high}"+yUnitsLabel+")<br/>' }";
		int index=series.toString().indexOf("\"errorbar");
		logger.warn("index="+index);
		String escapedErrorString="\"errorbar\"";
		seriesString=series.toString().replace(escapedErrorString, escapedErrorString+","+errorBarsToolTip);
		logger.warn("seriesString="+seriesString);
		String axisFontSize = "15";
		String javascript = "$(function () { var chart; $(document).ready(function() { chart = new Highcharts.Chart({ chart: {  zoomType: 'x', renderTo: 'timeChart"
				+ size
				+ "', type: 'line', marginRight: 130, marginBottom: 50 }, title: { text: '"
				+ WordUtils.capitalize(title)
				+ "', x: -20  }, credits: { enabled: false },  subtitle: { text: '"
				+ WordUtils.capitalize(sex.name())
				+ "', x: -20 }, xAxis: { "+noDecimalsString+" labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }},   title: {   text: '"+xUnitsLabel+"'   }  }, yAxis: {max: 2, min: 0, labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }}, title: { text: ' "
				+ yUnitsLabel
				+ "' }, plotLines: [{ value: 0, width: 1, color: '#808080' }] }, tooltip: { formatter: function() { return '<b>'+ this.series.name +'</b><br/>'+ this.x +': '+ this.y +'"
				+ yUnitsLabel
				+ "'; } }, legend: { layout: 'vertical', align: 'right', verticalAlign: 'top', x: -10, y: 100, borderWidth: 0 }, " +
				"tooltip: {shared: true},"+
				"series: "
				+
				seriesString 
				
				
				+ " }); }); }); "; 
		ChartData chartAndTable=new ChartData();
		chartAndTable.setChart(javascript);
		chartAndTable.setMin(minForChart);
		chartAndTable.setMax(maxForChart);
		return chartAndTable;
	}
}
