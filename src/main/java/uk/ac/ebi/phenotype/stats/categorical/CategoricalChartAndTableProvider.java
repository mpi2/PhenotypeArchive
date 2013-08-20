package uk.ac.ebi.phenotype.stats.categorical;

import java.sql.SQLException;
import java.util.ArrayList;
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

import uk.ac.ebi.phenotype.dao.CategoricalStatisticsDAO;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;

import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.TableObject;

@Service
public class CategoricalChartAndTableProvider {
	private static final Logger logger = Logger.getLogger(CategoricalChartAndTableProvider.class);

	@Autowired
	private CategoricalStatisticsDAO categoricalStatsDao;
	
	public CategoricalResultAndCharts doCategoricalData(List<BiologicalModel> categoricalMutantBiologicalModels, Parameter parameter, String acc, Model model,
			List<String> genderList, List<String> zyList,
			List<String> biologicalModelsParams, List<JSONObject> charts, List<TableObject> categoricalTables, String parameterId) throws SQLException {
		// if one or more parameterIds
													// specified in the url do
													// this
		
		//MEKK1
		//http://localhost:8080/phenotype-archive/stats/genes/MGI:1346872?parameterId=ESLIM_001_001_007

		logger.debug("running categorical data");
			List<String> categories = categoricalStatsDao.getCategories(parameter);
			List<BiologicalModel> biologicalModels = categoricalStatsDao.getMutantBiologicalModelsByParameterAndGene(parameter, acc);
			System.out.println("biological models returned from getMutantBiologicalModelsByParameterAndGene= "+biologicalModels.size());
			//List<BiologicalModel> biologicalModels = categoricalStatsDao.getBiologicalModelsByParameter(parameter);

			model.addAttribute("parameterId", parameter.getId().toString());
			model.addAttribute("parameterDescription", parameter.getDescription());

			logger.debug("biological models list size=" + biologicalModels.size());
			CategoricalResultAndCharts categoricalResultAndCharts=new CategoricalResultAndCharts();
			List<CategoricalResult> categoricalResults=new ArrayList<CategoricalResult>();
			for (BiologicalModel mutantBiologicalModel : biologicalModels) {
				if(biologicalModelsParams.isEmpty()||biologicalModelsParams.contains(mutantBiologicalModel.getId().toString())){

					logger.debug("biologicalModel="+mutantBiologicalModel);
					List<Integer> popIds = categoricalStatsDao.getPopulationIdsByParameterAndMutantBiologicalModel(parameter, mutantBiologicalModel);

					logger.debug("Population IDs: "+popIds);
					for(Integer popId:popIds){
//						BiologicalModel mutantBiologicalModel = categoricalStatsDao.getMutantBiologicalModelByPopulation(popId);
//						logger.debug("popId="+popId+"  mutantBmodel="+mutantBiologicalModel);
						categoricalResultAndCharts.addBiologicalModel(mutantBiologicalModel);
						SexType sexType = categoricalStatsDao.getSexByPopulation(new Integer(popId.intValue()));//(new Integer(5959));
						//should get one for each sex here if there is a result for each experimental sex
					
					
						//logger.debug(popId+" sextype="+sexType);
						List<ZygosityType> zygosities = categoricalStatsDao.getZygositiesByPopulation(popId);
						logger.debug(zygosities);
						

						if(genderList.isEmpty()||genderList.contains(sexType.name())){
							
							List<CategoricalResult> statsResults=categoricalStatsDao.getCategoricalResultByParameter(parameter, mutantBiologicalModel.getId(), sexType);
							
							categoricalResults.addAll(statsResults);
							//categoricalResultAndCharts.setStatsResults(statsResults);
							CategoricalChartDataObject chartData=new CategoricalChartDataObject();//make a new chart object for each sex
							chartData.setSexType(sexType);
							List<String> xAxisCategories = this.getXAxisCategories(zygosities, zyList);
							List<List<Long>> seriesDataForCategoricalType = new ArrayList<List<Long>>();
				
							//do control first as requires no zygocity
							CategoricalSet controlSet=new CategoricalSet();
							controlSet.setName("Control");

					 for (String category : categories) {
								if(category.equals("imageOnly"))continue;//ignore image categories as no numbers!
							CategoricalDataObject controlCatData=new CategoricalDataObject();
							controlCatData.setName("control");
							controlCatData.setCategory(category);
							Long controlCount = categoricalStatsDao.countControl(sexType, parameter, category, popId);
							controlCatData.setCount(controlCount);
							logger.debug("control=" + sexType.name() + " count=" + controlCount + " category=" + category);
							controlSet.add(controlCatData);
					}
							chartData.add(controlSet);

						// now do experimental i.e. zygocities
						for (ZygosityType zType :zygosities) {
							if(zyList.isEmpty()||zyList.contains(zType.name())){
								CategoricalSet zTypeSet=new CategoricalSet();//hold the data for each bar on graph hom, normal, abnormal
								zTypeSet.setName(zType.name());
									for (String category : categories) {
													if(category.equals("imageOnly"))continue;
													logger.debug(zyList);
								
										CategoricalDataObject expCatData=new CategoricalDataObject();
										Long mutantCount = categoricalStatsDao.countMutant(sexType, zType, parameter, category, popId);
										expCatData.setName(zType.name());
										expCatData.setCategory(category);
										expCatData.setCount(mutantCount);
										logger.warn("getting pvalue for sex="+sexType+"  zyg="+ zType+" param="+ parameter+" category="+ category+"popId="+ popId);
										Double pValue = categoricalStatsDao.getpValueByParameterAndMutantBiologicalModelAndSexAndZygosity(parameter, mutantBiologicalModel, sexType, zType);
										Double maxEffect=categoricalStatsDao.getMaxEffectSizeByParameterAndMutantBiologicalModelAndSexAndZygosity(parameter, mutantBiologicalModel,  sexType, zType);
										System.out.println("pValue="+pValue);
										System.out.println("maxEffect");
										if(pValue!=null && maxEffect!=null){
										expCatData.setpValue(pValue);
										expCatData.setMaxEffect(maxEffect);
										logger.warn("pValue="+pValue+" maxEffect="+maxEffect);
										}
										zTypeSet.add(expCatData);
									
								
			
							}
									chartData.add(zTypeSet);
						}
						}
							
//							removeColumnsWithZeroData(xAxisCategories,
//									seriesDataForCategoricalType);
							
//							String chart = this.createCategoricalHighChart( categoricalBarCharts, sexType,
//									parameter.getName() ,
//								xAxisCategories, categories,
//								seriesDataForCategoricalType);
						if(xAxisCategories.size()>1){//if size is greater than one i.e. we have more than the control data then draw charts and tables
							String chartNew = this.createCategoricalHighChartUsingObjects( chartData, parameter.getName(), mutantBiologicalModel);
							chartData.setChart(chartNew);
							categoricalResultAndCharts.add(chartData);
							categoricalResultAndCharts.setStatsResults(categoricalResults);
//							TableObject table = this.creatCategoricalDataTableFromObjects(chartData,  sexType, "",
//								xAxisCategories, categories,
//								seriesDataForCategoricalType);
							//tables.add(table);
						}
						}//end of gender
					}

				}//end of biological model param

			}
		return categoricalResultAndCharts;
	}


	private String createCategoricalHighChartUsingObjects(
			CategoricalChartDataObject chartData, String parameterName, BiologicalModel bm) {
		System.out.println(chartData);
		

	//	int size=categoricalBarCharts.size()+1;//to know which div to render to not 0 index as using loop count in jsp
		JSONArray seriesArray = new JSONArray();
		JSONArray xAxisCategoriesArray = new JSONArray();
		String title=parameterName;
		SexType sex=chartData.getSexType();
		//try {
			
//			logger.debug("call to highchart" + " sex=" + sex + " title="
//					+ title + " xAxisCategories=" + xAxisCategories
//					+ "  categoricalDataTypesTitles="
//					+ categoricalDataTypesTitles
//					+ " seriesDataForCategoricalType="
//					+ seriesDataForCategoricalType);
			// "{ chart: { renderTo: 'female', type: 'column' }, title: { text: '' }, xAxis: { categories: [] }, yAxis: { min: 0, title: { text: '' } },  plotOptions: { column: { stacking: 'percent' } }, series: [ { name: 'Abnormal Femur', color: '#AA4643', data: [2, 2, 3] },{ name: 'Normal', color: '#4572A7', data: [5, 3, 4] }] }"
			String colorNormal = "#4572A7";
			String colorAbnormal = "#AA4643";
			String color = "";
		
					
			List<CategoricalSet> catSets = chartData.getCategoricalSets();
			//get a list of unique categories
			HashMap<String, List<Long>> categories=new LinkedHashMap<String, List<Long>>();//keep the order so we have normal first!
			//for(CategoricalSet catSet: catSets){
			CategoricalSet catSet1=catSets.get(0);//assume each cat set has the same number of categories
				for(CategoricalDataObject catObject: catSet1.getCatObjects()){
					String category=catObject.getCategory();
					//if(!category.equals("")){
					System.out.println("adding category="+category);
					categories.put(category, new ArrayList<Long>());
					//}
				}
			//}
			
				for(CategoricalSet catSet: catSets){//loop through control, then hom, then het etc
					xAxisCategoriesArray.put(catSet.getName());
					for(CategoricalDataObject catObject: catSet.getCatObjects()){//each cat object represents
						List <Long>catData = categories.get(catObject.getCategory());
						catData.add(catObject.getCount());
					
				}
				}
			
			
			
			
			try {
				int i = 0;
				 Iterator it = categories.entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pairs = (Map.Entry)it.next();
					List<Long> data=(List<Long>) pairs.getValue();
					JSONObject dataset1 = new JSONObject();// e.g. normal
					dataset1.put("name", pairs.getKey());
					if (i == 0) {
						color = colorNormal;
					} else {
						color = colorAbnormal;
					}
					//dataset1.put("color", color);
					JSONArray dataset = new JSONArray();

					for (Long singleValue : data) {
						dataset.put(singleValue);
					}
					dataset1.put("data", dataset);
					seriesArray.put(dataset1);
					i++;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		// logger.debug("model="+model);
			String chartId=bm.getId()+sex.name();
			String allele=bm.getAllelicComposition();
		String toolTipFunction="	{ formatter: function() {         return \''+  this.series.name +': '+ this.y +' ('+ Math.round(this.percentage) +'%)';   }    }";
		String javascript=	"$(function () {  var chart"+chartId+"; $(document).ready(function() { chart"+chartId+" = new Highcharts.Chart({ tooltip : "+toolTipFunction + ", chart: { renderTo: 'categoricalBarChart"+chartId+"', type: 'column' }, title: { text: '"+WordUtils.capitalize(title)+"' }, credits: { enabled: false }, subtitle: { text: '"+WordUtils.capitalize(sex.name())+"', x: -20 }, xAxis: { categories: "+xAxisCategoriesArray+"}, yAxis: { min: 0, title: { text: 'Percent Occurrance' } ,  labels: {       formatter: function() { return this.value +'%';   }  }},  plotOptions: { column: { stacking: 'percent' } }, series: "+seriesArray+" });   });});";
//logger.debug(javascript);
		//categoricalBarCharts.add(javascript);
		chartData.setChart(javascript);
		chartData.setChartIdentifier(chartId);
		chartData.setBiologicalModel(bm);
		return javascript;
	}


	private void removeColumnsWithZeroData(List<String> xAxisCategories,
			List<List<Long>> seriesDataForCategoricalType) {
		Set<Integer> removeColumns = new HashSet<Integer>();
		for (int i=0;i<seriesDataForCategoricalType.get(0).size();i++) {
			int count = 0;
			for (int j=0;j<seriesDataForCategoricalType.size();j++) {
				//logger.debug("checking cell with data="+seriesDataForCategoricalType.get(j).get(i));
				count+=seriesDataForCategoricalType.get(j).get(i);
			}
			if (count == 0) {
				//remove the column as there is no data
				removeColumns.add(i);
			}
			logger.debug("end of checking column");
		}
		if(!removeColumns.isEmpty()) {
			for (Integer column:removeColumns) {
				for (List<Long> ll:seriesDataForCategoricalType) {
					ll.remove(column.intValue());
					//logger.debug("removedcell="+column.intValue());
				}
				String removedCat=xAxisCategories.remove(column.intValue());
				//logger.debug("removedCat="+removedCat);
			}
		}
	}
	
	
	/**
	 * 
	 * @param model
	 *            mvc model from spring
	 * @param sex
	 *            SexType
	 * @param title
	 *            main title for the graph
	 * @param xAxisCategories
	 *            e.g. Control, Homozygote, Heterozygote
	 * @param categoricalDataTypesTitles
	 *            e.g. Abnormal, Normal
	 * @param seriesDataForCategoricalType
	 *            e.g.
	 * @return
	 */
	private String  createCategoricalHighChart(List<String> categoricalBarCharts, SexType sex, String title,
			List<String> xAxisCategories,
			List<String> categoricalDataTypesTitles,
			List<List<Long>> seriesDataForCategoricalType) {
		int size=categoricalBarCharts.size()+1;//to know which div to render to not 0 index as using loop count in jsp
		JSONArray seriesArray = new JSONArray();
		JSONArray xAxisCategoriesArray = new JSONArray();
		try {
			
			logger.debug("call to highchart" + " sex=" + sex + " title="
					+ title + " xAxisCategories=" + xAxisCategories
					+ "  categoricalDataTypesTitles="
					+ categoricalDataTypesTitles
					+ " seriesDataForCategoricalType="
					+ seriesDataForCategoricalType);
			// "{ chart: { renderTo: 'female', type: 'column' }, title: { text: '' }, xAxis: { categories: [] }, yAxis: { min: 0, title: { text: '' } },  plotOptions: { column: { stacking: 'percent' } }, series: [ { name: 'Abnormal Femur', color: '#AA4643', data: [2, 2, 3] },{ name: 'Normal', color: '#4572A7', data: [5, 3, 4] }] }"
			String colorNormal = "#4572A7";
			String colorAbnormal = "#AA4643";
			String color = "";
			
			for (String xAxisCategory : xAxisCategories) {
				xAxisCategoriesArray.put(xAxisCategory);
			}
			
			int i = 0;
			for (List<Long> data : seriesDataForCategoricalType) {
				JSONObject dataset1 = new JSONObject();// e.g. normal
				dataset1.put("name", categoricalDataTypesTitles.get(i));
				if (i == 0) {
					color = colorNormal;
				} else {
					color = colorAbnormal;
				}
				//dataset1.put("color", color);
				JSONArray dataset = new JSONArray();

				for (Long singleValue : data) {
					dataset.put(singleValue);
				}
				dataset1.put("data", dataset);
				seriesArray.put(dataset1);
				i++;
			}

			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// logger.debug("model="+model);
		String toolTipFunction="	{ formatter: function() {         return \''+  this.series.name +': '+ this.y +' ('+ Math.round(this.percentage) +'%)';   }    }";
		String javascript=	"$(function () {  var chart"+size+"; $(document).ready(function() { chart"+size+" = new Highcharts.Chart({ tooltip : "+toolTipFunction + ", chart: { renderTo: 'categoricalBarChart"+size+"', type: 'column' }, title: { text: '"+WordUtils.capitalize(title)+"' }, credits: { enabled: false }, subtitle: { text: '"+WordUtils.capitalize(sex.name())+"', x: -20 }, xAxis: { categories: "+xAxisCategoriesArray+"}, yAxis: { min: 0, title: { text: 'Percent Occurrance' } ,  labels: {       formatter: function() { return this.value +'%';   }  }},  plotOptions: { column: { stacking: 'percent' } }, series: "+seriesArray+" });   });});";
//logger.debug(javascript);
		categoricalBarCharts.add(javascript);
		return javascript;
	}
	
	private TableObject creatCategoricalDataTable(SexType sexType, String title,
			List<String> xAxisCategories, List<String> categories,
			List<List<Long>> seriesDataForCategoricalType) {
		TableObject tableObject=new TableObject();
		tableObject.setSexType(sexType.name());
		tableObject.setTitle(title);
		//logger.debug("xAxisCategories="+xAxisCategories);
		List<String> columnHeaders=new ArrayList<String>(categories);
		columnHeaders.add(0, ""); // add empty header to the new 
		columnHeaders.remove("imageOnly");
		//headers list not to the categories object list
		tableObject.setColumnHeaders(columnHeaders);
	//	logger.debug("categories="+categories);
		tableObject.setRowHeaders(xAxisCategories);
		
		//logger.debug("seriesData="+seriesDataForCategoricalType);
		for(List<Long> colData:seriesDataForCategoricalType){
			//logger.debug("coldata size="+colData.size());
			List<String> col=new ArrayList<String>();
			for(Long dataCell:colData){
				col.add(dataCell.toString());
			}
			tableObject.addColumn(col);
		}
		//tableObject.setCellData(cellData));
		return tableObject;
		
	}
	
//	private TableObject creatCategoricalDataTableFromObjects(CategoricalChartDataObject chartData, SexType sexType, String title,
//			List<String> xAxisCategories, List<String> categories,
//			List<List<Long>> seriesDataForCategoricalType) {
//		TableObject tableObject=new TableObject();
//		tableObject.setSexType(sexType.name());
//		tableObject.setTitle(title);
//		System.out.println("xAxisCategories="+xAxisCategories);
//		List<String> columnHeaders=new ArrayList<String>(categories);
//		columnHeaders.add(0, ""); // add empty header to the new 
//		columnHeaders.remove("imageOnly");
//		columnHeaders.add("p Value");
//		columnHeaders.add("Effect Size");
//		//headers list not to the categories object list
//		tableObject.setColumnHeaders(columnHeaders);
//	//	logger.debug("categories="+categories);
//		tableObject.setRowHeaders(xAxisCategories);
//		 List<List<String>> cellData=new ArrayList<List<String>>();
//		for(CategoricalSet set:chartData.getCategoricalSets()){
//			List <String>listForSet=new ArrayList<String>();//list for control then hom or het
//			List<CategoricalDataObject> catObjects = set.getCatObjects();
//			for(CategoricalDataObject dataObject: catObjects){
//				String name=dataObject.getName();
//				Long count = dataObject.getCount();
//				listForSet.add(count.toString());
//				
//			}
//			CategoricalDataObject lastCatObject = catObjects.get(catObjects.size()-1);//get the last result for the set which should be experimental and contain the pvalue and max effect values for the set if not control set
//			if((lastCatObject.getpValue()!=null) && (lastCatObject.getMaxEffect()!=null)){
//				listForSet.add(lastCatObject.getpValue().toString());
//				listForSet.add(lastCatObject.getMaxEffect().toString());
//			}else{
//				listForSet.add(" ");
//				listForSet.add(" ");
//			}
//			cellData.add(listForSet);
//		}
//		tableObject.setCellData(cellData);
//		//logger.debug("seriesData="+seriesDataForCategoricalType);
////		for(List<Long> colData:seriesDataForCategoricalType){
////			//logger.debug("coldata size="+colData.size());
////			List<String> col=new ArrayList<String>();
////			for(Long dataCell:colData){
////				col.add(dataCell.toString());
////			}
////			tableObject.addColumn(col);
////		}
//		//tableObject.setCellData(cellData));
//		return tableObject;
//		
//	}
	
	private List<String> getXAxisCategories(List<ZygosityType> zygosities, List<String> zygosityParams) {
		List<String> xAxisCat = new ArrayList<String>();
		xAxisCat.add("Control");// we know we have controls and we want to put
								// these first.
		
		for (ZygosityType type : zygosities) {
			if(zygosityParams.isEmpty()||zygosityParams.contains(type.name())){
			xAxisCat.add(type.name());
			}
		}
		return xAxisCat;
	}

}
