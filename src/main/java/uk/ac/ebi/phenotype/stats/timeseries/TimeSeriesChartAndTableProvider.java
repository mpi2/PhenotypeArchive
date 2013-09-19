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
import uk.ac.ebi.phenotype.stats.JSONGraphUtils;
import uk.ac.ebi.phenotype.stats.MouseDataPoint;
import uk.ac.ebi.phenotype.stats.TableObject;

@Service
public class TimeSeriesChartAndTableProvider {
	private static final Logger logger = Logger.getLogger(TimeSeriesChartAndTableProvider.class);

	@Autowired
	private TimeSeriesStatisticsDAO timeSeriesStatisticsDAO;
	
	 SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");

	public List<ChartData> doTimeSeriesData(BiologicalModelDAO bmDAO, Map<String, String> config, net.sf.json.JSONObject expResult, List<BiologicalModel> timeSeriesMutantBiologicalModels, Parameter parameter, String acc, Model model,
			List<String> genderList, List<String> zyList,
			int listIndex, List<String> biologicalModelsParams) throws IOException, URISyntaxException {
		// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1920000?parameterId=ESLIM_004_001_002
		Float max=new Float(0);
		Float min=new Float(1000000000);
		List<ChartData> chartsNTablesForParameter=new ArrayList<ChartData>();

		//maybe need to put these into method that can be called as repeating this - so needs refactoring though there are minor differences?
		
		net.sf.json.JSONObject facetCounts = expResult
				.getJSONObject("facet_counts");
		net.sf.json.JSONObject facetFields = facetCounts
				.getJSONObject("facet_fields");
		System.out.println("facetFields=" + facetFields);
		net.sf.json.JSONArray facets = facetFields.getJSONArray("organisation");
		ArrayList<String> organisationsWithData = new ArrayList<>();
		for (int i = 0; i < facets.size(); i += 2) {
			String facet = facets.getString(i);
			int count = facets.getInt(i + 1);
			if (count > 0) {
				organisationsWithData.add(facet);
			}
		}
		System.out.println("organisations with data=" + organisationsWithData);

		net.sf.json.JSONArray facets2 = facetFields.getJSONArray("strain");
		// get the strains from the facets
		ArrayList<String> strains = new ArrayList<>();
		for (int i = 0; i < facets2.size(); i += 2) {
			String facet = facets2.getString(i);
			int count = facets2.getInt(i + 1);
			if (count > 0) {
				strains.add(facet);
			}
		}
		System.out.println("strains=" + strains);

		net.sf.json.JSONArray facets3 = facetFields
				.getJSONArray("biologicalModelId");
		// get the strains from the facets
		ArrayList<Integer> biologicalModelIds = new ArrayList<Integer>();
		for (int i = 0; i < facets3.size(); i += 2) {
			int facet = facets3.getInt(i);
			int count = facets3.getInt(i + 1);
			if (count > 0) {
				biologicalModelIds.add(facet);
			}
		}
		System.out.println("biologicalModelIds=" + biologicalModelIds);
		net.sf.json.JSONArray facets4 = facetFields.getJSONArray("gender");
		// get the strains from the facets
		ArrayList<String> genders = new ArrayList<>();
		for (int i = 0; i < facets4.size(); i += 2) {
			String facet = facets4.getString(i);
			int count = facets4.getInt(i + 1);
			if (count > 0) {
				genders.add(facet);
			}
		}
		System.out.println("genders=" + genders);
		
		net.sf.json.JSONArray facets5 = facetFields.getJSONArray("zygosity");
		// get the strains from the facets
		ArrayList<ZygosityType> zygosities = new ArrayList<ZygosityType>();
		for (int i = 0; i < facets5.size(); i += 2) {
			String facet = facets5.getString(i);
			int count = facets5.getInt(i + 1);
			if (count > 0) {
				ZygosityType zygosityType = ZygosityType.valueOf(facet);
				zygosities.add(zygosityType);
			}
		}
		System.out.println("zygosities=" + zygosities);
		
		
		for (String organisation : organisationsWithData) {

			for (String strain : strains) {
			Integer 	controlBiologicalModelId=null;
				net.sf.json.JSONObject controlResult = JSONGraphUtils.getControlData( organisation, strain, parameter.getStableId(), config);
				
				BiologicalModel controlBiologicalModel=null;
				net.sf.json.JSONObject controlFacetCounts = controlResult.getJSONObject("facet_counts");
				net.sf.json.JSONObject controlFacetFields = controlFacetCounts
						.getJSONObject("facet_fields");
				System.out.println("facetFields=" + facetFields);
				
				net.sf.json.JSONArray controlFacets = controlFacetFields.getJSONArray("biologicalModelId");
				ArrayList<Integer> controlBiologicalModelIds = new ArrayList<>();
				for (int i = 0; i < facets.size(); i += 2) {
					int facet = controlFacets.getInt(i);
					int count = controlFacets.getInt(i + 1);
					if (count > 0) {
						controlBiologicalModelIds.add(facet);
					}
				}
					if(controlBiologicalModelIds.size()!=1) {
								System.err.println("There should be only one control biological model");
					}else {
										controlBiologicalModelId=controlBiologicalModelIds.get(0);
										controlBiologicalModel = bmDAO.getBiologicalModelById(controlBiologicalModelId);
					}
				System.out.println("Control Biological models=" + controlBiologicalModelIds);		
		
		
		
		
		
		
		Map<String, List<DiscreteTimePoint>> lines = new HashMap<String, List<DiscreteTimePoint>>();
//		List<BiologicalModel> biologicalModels = timeSeriesStatisticsDAO
//				.getBiologicalModelsByParameterAndGene(parameter, acc);
		//logger.warn("biologicalmodels size=" + biologicalModels.size());
		for (int biologicalModelId : biologicalModelIds) {
			BiologicalModel biologicalModel=bmDAO.getBiologicalModelById(biologicalModelId);
			
			if (biologicalModelsParams.isEmpty()
					|| biologicalModelsParams.contains(biologicalModel.getId()
							.toString())) {

				logger.warn("biologicalModel=" + biologicalModel);
//				List<Integer> popIds = timeSeriesStatisticsDAO
//						.getPopulationIdsByParameterAndMutantBiologicalModel(
//								parameter, biologicalModel);
//				logger.warn("Population IDs: " + popIds);
				//for (Integer popId : popIds) {

//					SexType sexType = timeSeriesStatisticsDAO
//							.getSexByPopulation(new Integer(popId.intValue()));// (new
//																				// Integer(5959));
//					logger.debug(popId + " sextype=" + sexType);
//					List<ZygosityType> zygosities = timeSeriesStatisticsDAO
//							.getZygositiesByPopulation(popId);
//					BiologicalModel mutantBiologicalModel = timeSeriesStatisticsDAO.getMutantBiologicalModelByPopulation(popId);
//					logger.info("getting data for mutant popId="+popId);
					timeSeriesMutantBiologicalModels.add(biologicalModel);

					
					for (String sex : genders) { // one graph for each sex if
						// unspecified in params to
						// page or in list of sex
						// param specified
						SexType sexType = SexType.valueOf(sex);
					if (genderList.isEmpty()
							|| genderList.contains(sexType.name())) {
						List<List<Float>> errorBarsPairs = null;

						
						List<DiscreteTimePoint> controlDataPoints=new ArrayList<>();
						 //loop over the control points and add them
						System.out.println("parsing control data to json");
						 net.sf.json.JSONArray controlDocs = JSONRestUtil.getDocArray(controlResult);
						 System.out.println("finished parsing control data to json");
						 for(int i=0; i<controlDocs.size();i++) {
							 net.sf.json.JSONObject ctrlDoc = controlDocs.getJSONObject(i);
							 //get the attributes of this data point
							 //We don't want to split controls by gender on Unidimensional data
							// SexType docSexType=SexType.valueOf(ctrlDoc.getString("gender"));
							// ZygosityType zygosityType=ZygosityType.valueOf(ctrlDoc.getString("zygosity"));
							 String docGender=ctrlDoc.getString("gender");
							String docStrain= ctrlDoc.getString("strain");
							 if(docStrain.equals(strain) && docGender.equals(sex)){
							 Long dataPoint=ctrlDoc.getLong("dataPoint");
							 String timePoint=ctrlDoc.getString("timePoint");
								 long timeInMillisSinceEpoch = getEpocTime(timePoint);
							//Float discreteTime=;
								 controlDataPoints.add(new DiscreteTimePoint(new Float(timeInMillisSinceEpoch) ,new Float(dataPoint)));//new Float(dataPoint));
							//controlMouseDataPoints.add(new MouseDataPoint("Need MouseIds from Solr",new Float(dataPoint)));
							//System.out.println("adding control point time="+timePoint+" epoc="+timeInMillisSinceEpoch+" datapoint="+dataPoint);
							//controlMouseDataPoints.add(new MouseDataPoint("uknown", new Float(dataPoint)));
							 }
						 }
						 System.out.println("finished putting control to data points");
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
						for (ZygosityType zType : zygosities) {
							if (zyList.isEmpty()
									|| zyList.contains(zType.name())) {
							List<DiscreteTimePoint> mutantData=new ArrayList<>();// = timeSeriesStatisticsDAO
//										.getMutantStats(sexType, zType, parameter,
//												popId);
							//	logger.debug("mutantCounts=" + mutantData);
								
							 net.sf.json.JSONArray expDocs = JSONRestUtil.getDocArray(expResult);
							 for(int i=0; i<expDocs.size();i++) {
								 net.sf.json.JSONObject expDoc = expDocs.getJSONObject(i);
								 //get the attributes of this data point
								 //We don't want to split controls by gender on Unidimensional data
								// SexType docSexType=SexType.valueOf(ctrlDoc.getString("gender"));
								// ZygosityType zygosityType=ZygosityType.valueOf(ctrlDoc.getString("zygosity"));
								 String docGender=expDoc.getString("gender");
								String docStrain= expDoc.getString("strain");
								 if(docStrain.equals(strain) && docGender.equals(sex)){
								 Long dataPoint=expDoc.getLong("dataPoint");
								 String timePoint=expDoc.getString("timePoint");
									 long timeInMillisSinceEpoch = getEpocTime(timePoint);
								//Float discreteTime=;
									 mutantData.add(new DiscreteTimePoint(new Float(timeInMillisSinceEpoch) ,new Float(dataPoint)));//new Float(dataPoint));
								//controlMouseDataPoints.add(new MouseDataPoint("Need MouseIds from Solr",new Float(dataPoint)));
								//System.out.println("adding control point time="+timePoint+" epoc="+timeInMillisSinceEpoch+" datapoint="+dataPoint);
								//controlMouseDataPoints.add(new MouseDataPoint("uknown", new Float(dataPoint)));
								 }
							 }	 
							 
							 System.out.println("doing mutant data");
							 List<DiscreteTimePoint> mutantMeans= stats.getMeanDataPoints(mutantData);	
								lines.put(WordUtils.capitalize(zType.name()),
										mutantMeans);

							}
						}

						String title = "Mean " + parameter.getName();
						if(lines.size()>1){//if lines are greater than one i.e. more than just control create charts and tables
						ChartData chartNTableForParameter=creatDiscretePointTimeSeriesChart(listIndex,
								title, lines, parameter.checkParameterUnit(1),
								parameter.checkParameterUnit(2), sexType);
						Float tempMin=chartNTableForParameter.getMin();
						Float tempMax=chartNTableForParameter.getMax();
						if(tempMin<min)min=tempMin;
						if(tempMax>max)max=tempMax;
						chartsNTablesForParameter.add(chartNTableForParameter);
						listIndex++;
						}
					}// end of gender
				//}

			}// end of biological model param
		}//end of sex loop
		}
			}
		}
		//min=allMinMax.get("min"); 
		//max=allMinMax.get("max");
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
				series.put(errorBarsObject);
				// categoriesMap.put(key);
				i++;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.debug("series=" + series);
		// need to add error bars to series data as well!
		// sort the categories by time as means are all sorted already

		List<Float> cats = new ArrayList<Float>();
		for (Float cat : categoriesSet) {
			cats.add(cat);
		}
		Collections.sort(cats);
		String axisFontSize = "15";
		String javascript = "$(function () { var chart; $(document).ready(function() { chart = new Highcharts.Chart({ chart: {  zoomType: 'x', renderTo: 'timeChart"
				+ size
				+ "', type: 'line', marginRight: 130, marginBottom: 50 }, title: { text: '"
				+ WordUtils.capitalize(title)
				+ "', x: -20  }, credits: { enabled: false },  subtitle: { text: '"
				+ WordUtils.capitalize(sex.name())
				+ "', x: -20 }, xAxis: { labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }},   title: {   text: '"+xUnitsLabel+"'   }  }, yAxis: {max: 2, min: 0, labels: { style:{ fontSize:"
				+ axisFontSize
				+ " }}, title: { text: ' "
				+ yUnitsLabel
				+ "' }, plotLines: [{ value: 0, width: 1, color: '#808080' }] }, tooltip: { formatter: function() { return '<b>'+ this.series.name +'</b><br/>'+ this.x +': '+ this.y +'"
				+ yUnitsLabel
				+ "'; } }, legend: { layout: 'vertical', align: 'right', verticalAlign: 'top', x: -10, y: 100, borderWidth: 0 }, series: "
				+ series + " }); }); }); "; 
		ChartData chartAndTable=new ChartData();
		chartAndTable.setChart(javascript);
		chartAndTable.setMin(minForChart);
		chartAndTable.setMax(maxForChart);
		return chartAndTable;
	}
}
