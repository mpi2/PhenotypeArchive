package uk.ac.ebi.phenotype.stats.timeseries;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.ChartData;
import uk.ac.ebi.phenotype.stats.ChartUtils;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
import uk.ac.ebi.phenotype.stats.ObservationDTO;
import uk.ac.ebi.phenotype.stats.graphs.ChartColors;

@Service
public class TimeSeriesChartAndTableProvider {
	private static final Logger logger = Logger
			.getLogger(TimeSeriesChartAndTableProvider.class);

	public ChartData doTimeSeriesOverviewData(
			Map<String, List<DiscreteTimePoint>> lines, Parameter p) {

		if (lines == null) {
			return new ChartData();
		}

		String title = p.getName();
		// create CharData
		ChartData chartsNTablesForParameter = creatDiscretePointTimeSeriesChartOverview(
				"1", title, lines, p.checkParameterUnit(1),
				p.checkParameterUnit(2), 1, "org", p);
		return chartsNTablesForParameter;
	}

	public ChartData doTimeSeriesData(ExperimentDTO experiment,
			Parameter parameter, String experimentNumber, BiologicalModel expBiologicalModel) throws IOException,
			URISyntaxException {
		ChartData chartNTableForParameter = null;

		// maybe need to put these into method that can be called as repeating
		// this - so needs refactoring though there are minor differences?
		Map<String, List<DiscreteTimePoint>> lines = new HashMap<String, List<DiscreteTimePoint>>();

		for (SexType sex : experiment.getSexes()) {
			List<DiscreteTimePoint> controlDataPoints = new ArrayList<>();
			// loop over the control points and add them

			for (ObservationDTO control : experiment.getControls()) {
				String docGender = control.getSex();
				if (SexType.valueOf(docGender).equals(sex)) {
					Float dataPoint = control.getDataPoint();
					logger.debug("data value=" + dataPoint);
					Float discreteTimePoint = control.getDiscretePoint();

					controlDataPoints.add(new DiscreteTimePoint(
							discreteTimePoint, dataPoint));;

				}
			}
			logger.debug("finished putting control to data points");
			TimeSeriesStats stats = new TimeSeriesStats();
			List<DiscreteTimePoint> controlMeans = stats
					.getMeanDataPoints(controlDataPoints);

			lines.put(WordUtils.capitalize(sex.name())+" Control", controlMeans);
			for (ZygosityType zType : experiment.getZygosities()) {
				List<DiscreteTimePoint> mutantData = new ArrayList<>();// =
																		// timeSeriesStatisticsDAO
				Set<ObservationDTO> expObservationsSet = Collections.emptySet();
				if (zType.equals(ZygosityType.heterozygote)
						|| zType.equals(ZygosityType.hemizygote)) {
					expObservationsSet = experiment.getHeterozygoteMutants();
				}
				if (zType.equals(ZygosityType.homozygote)) {
					expObservationsSet = experiment.getHomozygoteMutants();
				}

				for (ObservationDTO expDto : expObservationsSet) {
					// get the attributes of this data point
					String docGender = expDto.getSex();
					if (SexType.valueOf(docGender).equals(sex)) {
						Float dataPoint = expDto.getDataPoint();
						logger.debug("data value=" + dataPoint);
						Float discreateTimePoint = expDto.getDiscretePoint();// getTimePoint();
						mutantData.add(new DiscreteTimePoint(
								discreateTimePoint, new Float(dataPoint)));// new
																			// Float(dataPoint));

					}
				}

				logger.debug("doing mutant data");
				List<DiscreteTimePoint> mutantMeans = stats
						.getMeanDataPoints(mutantData);
				lines.put(WordUtils.capitalize(sex.name())+" "+WordUtils.capitalize(zType.name()), mutantMeans);

			}
		}// end of gender

			String title = "Mean " + parameter.getName();
			if (lines.size() > 1) {// if lines are greater than one i.e. more
									// than just control create charts and
									// tables
				int deimalPlaces = ChartUtils.getDecimalPlaces(experiment);
				chartNTableForParameter = creatDiscretePointTimeSeriesChart(
						experimentNumber, title, lines, parameter.checkParameterUnit(1),
						parameter.checkParameterUnit(2), deimalPlaces,
						experiment.getOrganisation(), parameter);
				chartNTableForParameter.setExperiment(experiment);

			}
		chartNTableForParameter.setLines(lines);
		return chartNTableForParameter;
	}

	/**
	 * Creates a single chart and adds it to the chart array that is then added
	 * to the model by the main method
	 * 
	 * @param title
	 * @param lines
	 * @param xUnitsLabel
	 * @param yUnitsLabel
	 * @param organisation
	 *            TODO
	 * @param parameter 
	 * @param model
	 * @param timeSeriesCharts
	 * 
	 * @return
	 */
	private ChartData creatDiscretePointTimeSeriesChart(String expNumber,
			String title, Map<String, List<DiscreteTimePoint>> lines,
			String xUnitsLabel, String yUnitsLabel, int decimalPlaces, String organisation, Parameter parameter) {

		JSONArray series = new JSONArray();
		String seriesString = "";
		Set<Float> categoriesSet = new HashSet<Float>();
		Float maxForChart = new Float(0);
		Float minForChart = new Float(1000000000);
		// { name: 'Confidence', type: 'errorbar', color: 'black', data: [ [7.5,
		// 8.5], [2.8, 4], [1.5, 2.5], [3, 4.1], [6.5, 7.5], [3.3, 4.1], [4.8,
		// 5.1], [2.2, 3.0], [5.1, 8] ] }

		try {
			int i = 0;
			for (String key : lines.keySet()) {// key is control hom or het
				JSONObject object = new JSONObject();
				JSONArray data = new JSONArray();
				object.put("name", key);
				SexType sexType=SexType.male;
				if(key.contains("Female")) {
					sexType=SexType.female;
				}
				String colorString=ChartColors.getRgbaString(sexType, i, ChartColors.alphaScatter);
				object.put("color", colorString);

				JSONObject errorBarsObject = null;
				try {
					errorBarsObject = new JSONObject();// "{ name: 'Confidence', type: 'errorbar', color: 'black', data: [ [7.5, 8.5], [2.8, 4], [1.5, 2.5], [3, 4.1], [6.5, 7.5], [3.3, 4.1], [4.8, 5.1], [2.2, 3.0], [5.1, 8] ] } ");
					errorBarsObject.put("name", "Standard Deviation");
					errorBarsObject.put("type", "errorbar");
					errorBarsObject.put("color", colorString);

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
					errorsDataJson.put(errorBarsJ);

					errorBarsObject.put("data", errorsDataJson);

				}
				object.put("data", data);
				// add a placholder string so we can add a tooltip method
				// specifically for data that is not error bars later on in this
				// code
				String placeholderString = "placeholder";
				object.put(placeholderString, placeholderString);
				series.put(object);
				// we now want to add different tooltips for thses data sets
				// which we can't do for java json objects so we need to deal
				// with strings sooner

				series.put(errorBarsObject);
				// System.out.println("object for point data="+object);
				// System.out.println("errorbars="+errorBarsObject);
				// if(i==0) {
				// seriesString+=",";
				// }
				// seriesString+=","+object.toString()+","+errorBarsObject.toString();
				// System.out.println("seriesString="+seriesString);
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
		String noDecimalsString = "";
		if (xUnitsLabel.equals("number")) {
			// set the xAxis to be numbers with no decimals
			noDecimalsString = "allowDecimals:false,";
		}

		String decimalFormatString = ":." + decimalPlaces + "f";
		String headerFormatString = "headerFormat: '<span style=\"font-size: 12px\">"
				+ WordUtils.capitalize(xUnitsLabel)
				+ " {point.key}</span><br/>',";
		String pointToolTip = "tooltip: { "
				+ headerFormatString
				+ "pointFormat: '<span style=\"font-weight: bold; color: {series.color}\">{series.name}</span>:<b>{point.y"
				+ decimalFormatString + "}" + yUnitsLabel + "</b> '}";
		String escapedPlaceholder = "\"placeholder\":\"placeholder\"";
		seriesString = series.toString().replace(escapedPlaceholder,
				pointToolTip);

		String errorBarsToolTip = "tooltip: { pointFormat: 'SD: {point.low"
				+ decimalFormatString + "}-{point.high" + decimalFormatString
				+ "}<br/>' }";
		int index = series.toString().indexOf("\"errorbar");
		String escapedErrorString = "\"errorbar\"";
		seriesString = seriesString.replace(escapedErrorString,
				escapedErrorString + "," + errorBarsToolTip);
		String axisFontSize = "15";
		
		List<String> colors=ChartColors.getFemaleMaleColorsRgba(ChartColors.alphaScatter);
//		JSONArray colorArray = new JSONArray(colors);
		
		String javascript = "$(document).ready(function() { chart = new Highcharts.Chart({ " 
//				+" colors:"+colorArray
				+" chart: {  zoomType: 'x', renderTo: 'timechart"
				+ expNumber
				+ "', type: 'line', marginRight: 130, marginBottom: 50 }, title: { text: '"
				+ WordUtils.capitalize(title)
				+ "', x: -20  }, credits: { enabled: false },  subtitle: { text: '"
				+ parameter.getStableId()
				+ "', x: -20 }, xAxis: { "
				+ noDecimalsString
				+ " labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }},   title: {   text: '"
				+ xUnitsLabel
				+ "'   }  }, yAxis: { labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }}, title: { text: ' "
				+ yUnitsLabel
				+ "' }, plotLines: [{ value: 0, width: 1, color: '#808080' }] },  legend: { layout: 'vertical', align: 'right', verticalAlign: 'top', x: -10, y: 100, borderWidth: 0 }, "
				+ "tooltip: {shared: true},"
				+ "series: "
				+ seriesString
				+ " }); });  ";
		ChartData chartAndTable = new ChartData();
		chartAndTable.setChart(javascript);
		chartAndTable.setOrganisation(organisation);
		chartAndTable.setId(expNumber);
		return chartAndTable;
	}

	private ChartData creatDiscretePointTimeSeriesChartOverview(String expNumber,
			String title, Map<String, List<DiscreteTimePoint>> lines,
			String xUnitsLabel, String yUnitsLabel, int decimalPlaces, String organisation, Parameter parameter) {

		JSONArray series = new JSONArray();
		String seriesString = "";
		Set<Float> categoriesSet = new HashSet<Float>();
		Float maxForChart = new Float(0);
		Float minForChart = new Float(1000000000);

		try {
			int i = 0;
			for (String key : lines.keySet()) {// key is line name or "Control"
				
				JSONObject object = new JSONObject();
				JSONArray data = new JSONArray();
				object.put("name", key);
				
				String colorString;
				if (key.equalsIgnoreCase("Control")){
					colorString = ChartColors.getDefaultControlColor(ChartColors.alphaScatter);
				}
				else {
					colorString = ChartColors.getRgbaString(SexType.male, i, ChartColors.alphaScatter);
				}
				object.put("color", colorString);
				
				JSONObject errorBarsObject = null;
				try {
					errorBarsObject = new JSONObject();
					errorBarsObject.put("name", "Standard Deviation");
					errorBarsObject.put("type", "errorbar");
					errorBarsObject.put("color", colorString);

				} catch (JSONException e) {
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
					errorsDataJson.put(errorBarsJ);

					errorBarsObject.put("data", errorsDataJson);

				}
				object.put("data", data);
				String placeholderString = "placeholder";
				object.put(placeholderString, placeholderString);
				series.put(object);
				series.put(errorBarsObject);
				i++;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// need to add error bars to series data as well!
		// sort the categories by time as means are all sorted already

		List<Float> cats = new ArrayList<Float>();
		for (Float cat : categoriesSet) {
			cats.add(cat);
		}
		Collections.sort(cats);
		String noDecimalsString = "";
		if (xUnitsLabel.equals("number")) {
			// set the xAxis to be numbers with no decimals
			noDecimalsString = "allowDecimals:false,";
		}

		String decimalFormatString = ":." + decimalPlaces + "f";
		String headerFormatString = "headerFormat: '<span style=\"font-size: 12px\">"
				+ WordUtils.capitalize(xUnitsLabel)
				+ " {point.key}</span><br/>',";
		String pointToolTip = "tooltip: { "
				+ headerFormatString
				+ "pointFormat: '<span style=\"font-weight: bold; color: {series.color}\">{series.name}</span>:<b>{point.y"
				+ decimalFormatString + "}" + yUnitsLabel + "</b> '}";
		String escapedPlaceholder = "\"placeholder\":\"placeholder\"";
		seriesString = series.toString().replace(escapedPlaceholder,
				pointToolTip);

		String errorBarsToolTip = "tooltip: { pointFormat: 'SD: {point.low"
				+ decimalFormatString + "}-{point.high" + decimalFormatString
				+ "}<br/>' }";
		int index = series.toString().indexOf("\"errorbar");
		String escapedErrorString = "\"errorbar\"";
		seriesString = seriesString.replace(escapedErrorString,
				escapedErrorString + "," + errorBarsToolTip);
		String axisFontSize = "15";
		
		List<String> colors=ChartColors.getFemaleMaleColorsRgba(ChartColors.alphaScatter);
//		JSONArray colorArray = new JSONArray(colors);
		
		String javascript = "$(document).ready(function() { chart = new Highcharts.Chart({ " 
//				+" colors:"+colorArray
				+" chart: {  zoomType: 'x', renderTo: 'single-chart-div', type: 'line', marginRight: 130, marginBottom: 50 }, title: { text: '"
				+ WordUtils.capitalize(title)
				+ "', x: -20  }, credits: { enabled: false },  subtitle: { text: '"
				+ parameter.getStableId()
				+ "', x: -20 }, xAxis: { "
				+ noDecimalsString
				+ " labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }},   title: {   text: '"
				+ xUnitsLabel
				+ "'   }  }, yAxis: { labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }}, title: { text: ' "
				+ yUnitsLabel
				+ "' }, plotLines: [{ value: 0, width: 1, color: '#808080' }] },  legend: { layout: 'vertical', align: 'right', verticalAlign: 'top', x: -10, y: 100, borderWidth: 0 }, "
				+ "tooltip: {shared: true},"
				+ "series: "
				+ seriesString
				+ " }); });  ";
		ChartData chartAndTable = new ChartData();
		chartAndTable.setChart(javascript);
		chartAndTable.setOrganisation(organisation);
		chartAndTable.setId(parameter.getStableId());
		return chartAndTable;
	}

}
