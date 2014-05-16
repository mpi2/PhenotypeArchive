package uk.ac.ebi.phenotype.stats.categorical;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.ParameterOption;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.ExperimentService;
import uk.ac.ebi.phenotype.stats.ChartData;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
import uk.ac.ebi.phenotype.stats.ObservationDTO;
import uk.ac.ebi.phenotype.stats.TableObject;
import uk.ac.ebi.phenotype.stats.graphs.ChartColors;

@Service
public class CategoricalChartAndTableProvider {
	private static final Logger logger = Logger.getLogger(CategoricalChartAndTableProvider.class);

        @Autowired
	PhenotypePipelineDAO ppDAO;

	/**
	 * return a list of categorical result and chart objects - one for each ExperimentDTO
	 * @param experiment
	 * @param parameter
	 * @param acc
	 * @param gender
	 * @param parameterId
	 * @param charts
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public CategoricalResultAndCharts doCategoricalData(
			ExperimentDTO experiment, Parameter parameter,
			String acc,
			String numberString, BiologicalModel expBiologicalModel)
			throws SQLException, IOException, URISyntaxException {

		
		List<String> categories = this.getCategories(parameter);//loop through all the parameters no just ones with >0 result so use parameter rather than experiment
		logger.debug("running categorical data");
		//https://www.mousephenotype.org/data/charts?accession=MGI:98373?parameterId=M-G-P_014_001_009&zygosity=homozygote&phenotypingCenter=WTSI
		
			CategoricalResultAndCharts categoricalResultAndCharts = new CategoricalResultAndCharts();
			categoricalResultAndCharts.setExperiment(experiment);
			List<? extends StatisticalResult> statsResults = (List<? extends StatisticalResult>) experiment
					.getResults();
			// should get one for each sex here if there is a result for each
			// experimental sex
			CategoricalChartDataObject chartData = new CategoricalChartDataObject();// make a chart object one for both sexes
			for (SexType sexType : experiment.getSexes()) { 
				
					 categoricalResultAndCharts.setStatsResults(statsResults);
					
					//chartData.setSexType(sexType);
					// do control first as requires no zygocity
					CategoricalSet controlSet = new CategoricalSet();
					controlSet.setName(WordUtils.capitalize(sexType.name())+" Control");
					controlSet.setSexType(sexType);

					for (String category :categories) {
						if (category.equals("imageOnly"))
							continue;// ignore image categories as no numbers!
						CategoricalDataObject controlCatData = new CategoricalDataObject();
						controlCatData.setName(WordUtils.capitalize(sexType.name())+" Control");
						controlCatData.setCategory(ppDAO.getCategoryDescription(parameter.getId(), category));

						long controlCount = 0;
						for (ObservationDTO control : experiment.getControls()) {
							// get the attributes of this data point
							SexType docSexType = SexType.valueOf(control
									.getSex());
							String categoString =control.getCategory();
							//System.out.println("category string="+categoString);
							if (categoString.equals( category) && docSexType.equals(sexType)) {
								controlCount++;
							}
						}

						controlCatData.setCount(controlCount);
						logger.debug("control=" + sexType.name() + " count="
								+ controlCount + " category=" + ppDAO.getCategoryDescription(parameter.getId(), category));
						controlSet.add(controlCatData);
					}
					chartData.add(controlSet);

					// now do experimental i.e. zygocities
					for (ZygosityType zType : experiment.getZygosities()) {
						
							CategoricalSet zTypeSet = new CategoricalSet();// hold the data for each bar on graph hom, normal, abnormal
							zTypeSet.setName(WordUtils.capitalize(sexType.name())+" "+WordUtils.capitalize(zType.name()));
							for (String category : categories) {
								if (category.equals("imageOnly"))
									continue;
								Long mutantCount = new Long(0);// .countMutant(sexType, zType, parameter, category, popId);
								// loop over all the experimental docs and get
								// all that apply to current loop parameters
								Set<ObservationDTO> expObservationsSet = Collections.emptySet();
								expObservationsSet=experiment.getMutants(sexType, zType);
								
								for (ObservationDTO expDto : expObservationsSet) {

									// get the attributes of this data point
									SexType docSexType = SexType.valueOf(expDto
											.getSex());
									String categoString = expDto.getCategory();
									// get docs that match the criteria and add
									// 1 for each that does
									if (categoString.equals(category)
											&& docSexType.equals(sexType)) {
										mutantCount++;
									}
								}

								CategoricalDataObject expCatData = new CategoricalDataObject();
								expCatData.setName(zType.name());
								expCatData.setCategory(ppDAO.getCategoryDescription(parameter.getId(), category));
								expCatData.setCount(mutantCount);
								CategoricalResult tempStatsResult=null;
								for(StatisticalResult result: statsResults) {
                                                                   // System.out.println("result.getZygosityType()!="+result.getZygosityType()+"  && result.getSexType()="+result.getSexType());
                                                                    if(result.getZygosityType()!=null && result.getSexType()!=null) {
									if(result.getZygosityType().equals(zType) && result.getSexType().equals(sexType)) {
										expCatData.setResult((CategoricalResult)result);
										result.setSexType(sexType);
										result.setZygosityType(zType);
										tempStatsResult=(CategoricalResult)result;
										//result.setControlBiologicalModel(controlBiologicalModel);
									}
                                                                    }
								}
								
								// //TODO get multiple p values when necessary
								// System.err.println("ERROR WE NEED to change the code to handle multiple p values and max effect!!!!!!!!");
								if(tempStatsResult!=null) {
								expCatData.setpValue(tempStatsResult.getpValue());
								if(tempStatsResult.getEffectSize()!=null) {
								 expCatData.setMaxEffect(tempStatsResult.getEffectSize());
								}
								}
								// logger.warn("pValue="+pValue+" maxEffect="+maxEffect);
								// }
								zTypeSet.add(expCatData);

							}
							chartData.add(zTypeSet);
						}
					categoricalResultAndCharts.setOrganisation(experiment.getOrganisation());//add it here before check so we can see the organisation even if no graph data
				
			}// end of gender
			
		
				String chartNew = this
						.createCategoricalHighChartUsingObjects(numberString,
								chartData,
								parameter,
								experiment.getOrganisation(),
                                                                        experiment.getMetadataGroup());
				chartData.setChart(chartNew);
				categoricalResultAndCharts.add(chartData);
				categoricalResultAndCharts
						.setStatsResults(experiment.getResults());
		return categoricalResultAndCharts;
	}

	
	public List<ChartData> doCategoricalDataOverview(CategoricalSet controlSet, 
			CategoricalSet mutantSet,
			Model model, 
			Parameter parameter) throws SQLException{		
		// do the charts
		ChartData chartData = new ChartData();
		List<ChartData> categoricalResultAndCharts = new ArrayList<ChartData>();
		if (mutantSet.getCount() > 0 && controlSet.getCount() > 0) {// if size is greater than one i.e. we have more than the control data then draw charts and tables
			String chartNew = this.createCategoricalHighChartUsingObjectsOverview( controlSet, mutantSet, model, parameter, chartData);
			chartData.setChart(chartNew);
			categoricalResultAndCharts.add(chartData);
		}
		return categoricalResultAndCharts;
	}
	
	
	private String createCategoricalHighChartUsingObjectsOverview(CategoricalSet controlSet, 
			CategoricalSet mutantSet,
			Model model, 
			Parameter parameter,
			ChartData chartData) throws SQLException {

		// to not 0 index as using loop count in jsp
		JSONArray seriesArray = new JSONArray();
		JSONArray xAxisCategoriesArray = new JSONArray();
		String title = parameter.getName();
		String subtitle = parameter.getStableId();

		// get a list of unique categories
		HashMap<String, List<Long>> categories = new LinkedHashMap<String, List<Long>>();// keep the order so we have normal first!
		for (CategoricalDataObject catObject : controlSet.getCatObjects()) {
			String category = catObject.getCategory();
			categories.put(category, new ArrayList<Long>());
		}
		for (CategoricalDataObject catObject : mutantSet.getCatObjects()) {
			String category = catObject.getCategory();
			if (!categories.containsKey(category) && !category.equalsIgnoreCase("no data")){
				categories.put(category, new ArrayList<Long>());
			}
		}
		
		for(String categoryLabel : categories.keySet()){

			if (controlSet.getCategoryByLabel(categoryLabel) != null){
				categories.get(categoryLabel).add(controlSet.getCategoryByLabel(categoryLabel).getCount());
			}
			else categories.get(categoryLabel).add((long)0);

			if (mutantSet.getCategoryByLabel(categoryLabel) != null){
				categories.get(categoryLabel).add(mutantSet.getCategoryByLabel(categoryLabel).getCount());
			}
			else categories.get(categoryLabel).add((long)0);
		}
			xAxisCategoriesArray.put(controlSet.getName());
			xAxisCategoriesArray.put(mutantSet.getName());
			
			
		try {
			int i = 0;
			Iterator it = categories.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				List<Long> data = (List<Long>) pairs.getValue();
				JSONObject dataset1 = new JSONObject();// e.g. normal
				dataset1.put("name", pairs.getKey());
				JSONArray dataset = new JSONArray();

				for (Long singleValue : data) {
					dataset.put(singleValue);
				}
				dataset1.put("data", dataset);
				seriesArray.put(dataset1);
				i++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String chartId = "single-chart-div";//replace space in MRC Harwell with underscore so valid javascritp variable
		String toolTipFunction = "	{ formatter: function() {         return \''+  this.series.name +': '+ this.y +' ('+ (this.y*100/this.total).toFixed(1) +'%)';   }    }";
		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaBox);
		JSONArray colorArray = new JSONArray(colors);
		String javascript = "$(document).ready(function() { chart = new Highcharts.Chart({ " 
				+" colors:"+colorArray
				+", chart: { renderTo: '"
				+ chartId
				+ "', type: 'column' }, title: { text: '"
				+ WordUtils.capitalize(title)
				+ "' }, subtitle: { text:'" + subtitle + "'}, credits: { enabled: false }, "
				+ "xAxis: { categories: "
				+ xAxisCategoriesArray
				+ "}, yAxis: { min: 0, title: { text: 'Percent Occurrance' } ,  labels: {       formatter: function() { return this.value +'%';   }  }},  plotOptions: { column: { stacking: 'percent' } }, series: "
				+ seriesArray + " });  });";
		
		chartData.setChart(javascript);
		chartData.setId(chartId);	
		return javascript;
		
	}
		
	private String createCategoricalHighChartUsingObjects(String chartId,
			CategoricalChartDataObject chartData, Parameter parameter,
			 String organisation, String metadataGroup) throws SQLException {
		//System.out.println(chartData);

		// int size=categoricalBarCharts.size()+1;//to know which div to render
		// to not 0 index as using loop count in jsp
		JSONArray seriesArray = new JSONArray();
		JSONArray xAxisCategoriesArray = new JSONArray();
		String title = parameter.getName();
		// try {

		// logger.debug("call to highchart" + " sex=" + sex + " title="
		// + title + " xAxisCategories=" + xAxisCategories
		// + "  categoricalDataTypesTitles="
		// + categoricalDataTypesTitles
		// + " seriesDataForCategoricalType="
		// + seriesDataForCategoricalType);
		// "{ chart: { renderTo: 'female', type: 'column' }, title: { text: '' }, xAxis: { categories: [] }, yAxis: { min: 0, title: { text: '' } },  plotOptions: { column: { stacking: 'percent' } }, series: [ { name: 'Abnormal Femur', color: '#AA4643', data: [2, 2, 3] },{ name: 'Normal', color: '#4572A7', data: [5, 3, 4] }] }"
		String colorNormal = "#4572A7";
		String colorAbnormal = "#AA4643";
		String color = "";

		List<CategoricalSet> catSets = chartData.getCategoricalSets();
		// get a list of unique categories
		HashMap<String, List<Long>> categories = new LinkedHashMap<String, List<Long>>();// keep the order so we have normal first!
		// for(CategoricalSet catSet: catSets){
		CategoricalSet catSet1 = catSets.get(0);// assume each cat set has the same number of categories
		for (CategoricalDataObject catObject : catSet1.getCatObjects()) {
			String category = catObject.getCategory();
			// if(!category.equals("")){
			categories.put(category, new ArrayList<Long>());
			// }
		}
		// }

		for (CategoricalSet catSet : catSets) {// loop through control, then hom, then het etc
			xAxisCategoriesArray.put(catSet.getName());
			for (CategoricalDataObject catObject : catSet.getCatObjects()) {// each cat object represents
				List<Long> catData = categories.get(catObject.getCategory());
				catData.add(catObject.getCount());
			}
		}

		try {
			int i = 0;
			Iterator it = categories.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				List<Long> data = (List<Long>) pairs.getValue();
				JSONObject dataset1 = new JSONObject();// e.g. normal
				dataset1.put("name", pairs.getKey());
				//System.out.println("paris key="+pairs.getKey());
				// dataset1.put("color", color);
				JSONArray dataset = new JSONArray();

				for (Long singleValue : data) {
//					if (i == 0) {
//						sex = SexType.female;
//					} else {
//						sex=SexType.male;
//					}
//					//System.out.println("single value="+singleValue);
					dataset.put(singleValue);
					//dataset1.put("color", ChartColors.getRgbaString(sex, i, ChartColors.alphaScatter));
					i++;
				}
				dataset1.put("data", dataset);
				//System.out.println("XAxisCat="+xAxisCategoriesArray.get(i));
				seriesArray.put(dataset1);
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
                //replace space in MRC Harwell with underscore so valid javascript variable
                //String chartId = bm.getId() + sex.name()+organisation.replace(" ", "_")+"_"+metadataGroup;
		
		List<String> colors=ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaBox);
		JSONArray colorArray = new JSONArray(colors);
		String toolTipFunction = "	{ formatter: function() {         return \''+  this.series.name +': '+ this.y +' ('+ (this.y*100/this.total).toFixed(1) +'%)';   }    }";
		String javascript = "$(function () {  var chart_"
				+ chartId
				+ "; $(document).ready(function() { chart_"
				+ chartId
				+ " = new Highcharts.Chart({ tooltip : "
				+ toolTipFunction
				+", colors:"+colorArray
				+ ", chart: { renderTo: 'chart"
				+ chartId
				+ "', type: 'column' }, title: { text: '"
				+ title				+ "' }, credits: { enabled: false }, subtitle: { text: '"
				+  parameter.getStableId()
				+ "', x: -20 }, xAxis: { categories: "
				+ xAxisCategoriesArray
				+ "}, yAxis: { min: 0, title: { text: 'Percent Occurrance' } ,  labels: {       formatter: function() { return this.value +'%';   }  }},  plotOptions: { column: { stacking: 'percent' } }, series: "
				+ seriesArray + " });   });});";
		// logger.debug(javascript);
		// categoricalBarCharts.add(javascript);
		chartData.setChart(javascript);
		chartData.setChartIdentifier(chartId);

		//System.out.println("\n\n" + javascript);
		return javascript;
	}

	private void removeColumnsWithZeroData(List<String> xAxisCategories,
			List<List<Long>> seriesDataForCategoricalType) {
		Set<Integer> removeColumns = new HashSet<Integer>();
		for (int i = 0; i < seriesDataForCategoricalType.get(0).size(); i++) {
			int count = 0;
			for (int j = 0; j < seriesDataForCategoricalType.size(); j++) {
				// logger.debug("checking cell with data="+seriesDataForCategoricalType.get(j).get(i));
				count += seriesDataForCategoricalType.get(j).get(i);
			}
			if (count == 0) {
				// remove the column as there is no data
				removeColumns.add(i);
			}
			logger.debug("end of checking column");
		}
		if (!removeColumns.isEmpty()) {
			for (Integer column : removeColumns) {
				for (List<Long> ll : seriesDataForCategoricalType) {
					ll.remove(column.intValue());
					// logger.debug("removedcell="+column.intValue());
				}
				String removedCat = xAxisCategories.remove(column.intValue());
				// logger.debug("removedCat="+removedCat);
			}
		}
	}
	
	public List<String> getCategories(Parameter parameter) {
//		List<ParameterOption> options = parameter.getOptions();
//		List<String> categories = new ArrayList<String>();
//
//		for (ParameterOption option : options) {
//			categories.add(option.getName());
//		}
//		//exclude - "no data", "not defined" etc	
//		List<String>okCategoriesList=CategoriesExclude.getInterfaceFreindlyCategories(categories);	
//		return okCategoriesList;
		return parameter.getCategoriesUserInterfaceFreindly();
	}


}
