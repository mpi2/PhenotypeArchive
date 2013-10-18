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

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.dao.BiologicalModelDAO;
import uk.ac.ebi.phenotype.dao.CategoricalStatisticsDAO;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;

import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
import uk.ac.ebi.phenotype.stats.JSONGraphUtils;
import uk.ac.ebi.phenotype.stats.MouseDataPoint;
import uk.ac.ebi.phenotype.stats.ObservationDTO;
import uk.ac.ebi.phenotype.stats.TableObject;

@Service
public class CategoricalChartAndTableProvider {
	private static final Logger logger = Logger
			.getLogger(CategoricalChartAndTableProvider.class);

	@Autowired
	private CategoricalStatisticsDAO categoricalStatsDao;

	public CategoricalResultAndCharts doCategoricalData(
			List<ExperimentDTO> experimentList, BiologicalModelDAO bmDAO,
			Map<String, String> config,
			Parameter parameter,
			String acc, Model model, List<String> genderList,
			List<String> zyList, List<String> biologicalModelsParams,
			List<JSONObject> charts, List<TableObject> categoricalTables,
			String parameterId)
			throws SQLException, IOException, URISyntaxException {
		// if one or more parameterIds
		// specified in the url do
		// this

		// MEKK1
		// http://localhost:8080/phenotype-archive/stats/genes/MGI:1346872?parameterId=ESLIM_001_001_007

		// net.sf.json.JSONObject facetCounts = expResult
		// .getJSONObject("facet_counts");
		// net.sf.json.JSONObject facetFields = facetCounts
		// .getJSONObject("facet_fields");
		// System.out.println("facetFields=" + facetFields);
		// net.sf.json.JSONArray facets =
		// facetFields.getJSONArray("organisation");
		//
		//
		// net.sf.json.JSONArray facets3 = facetFields
		// .getJSONArray("biologicalModelId");
		// // get the strains from the facets
		// ArrayList<Integer> biologicalModelIds = new ArrayList<Integer>();
		// for (int i = 0; i < facets3.size(); i += 2) {
		// int facet = facets3.getInt(i);
		// int count = facets3.getInt(i + 1);
		// if (count > 0) {
		// biologicalModelIds.add(facet);
		// }
		// }
		// System.out.println("biologicalModelIds=" + biologicalModelIds);
		// net.sf.json.JSONArray facets4 = facetFields.getJSONArray("gender");
		// // get the strains from the facets
		// ArrayList<String> genders = new ArrayList<>();
		// for (int i = 0; i < facets4.size(); i += 2) {
		// String facet = facets4.getString(i);
		// int count = facets4.getInt(i + 1);
		// if (count > 0) {
		// genders.add(facet);
		// }
		// }
		// System.out.println("genders=" + genders);
		//
		// net.sf.json.JSONArray facets5 = facetFields.getJSONArray("zygosity");
		// // get the strains from the facets
		// ArrayList<ZygosityType> zygosities = new ArrayList<ZygosityType>();
		// for (int i = 0; i < facets5.size(); i += 2) {
		// String facet = facets5.getString(i);
		// int count = facets5.getInt(i + 1);
		// if (count > 0) {
		// ZygosityType zygosityType = ZygosityType.valueOf(facet);
		// zygosities.add(zygosityType);
		// }
		// }
		// System.out.println("zygosities=" + zygosities);
		//
		// net.sf.json.JSONArray facets6 = facetFields.getJSONArray("category");
		// // get the strains from the facets

		// for (int i = 0; i < facets6.size(); i += 2) {
		// String facet = facets6.getString(i);
		// int count = facets6.getInt(i + 1);
		// if (count > 0) {
		// categories.add(facet);
		// }
		// }

		logger.debug("running categorical data");

		model.addAttribute("parameterId", parameter.getId().toString());
		model.addAttribute("parameterDescription", parameter.getDescription());

		CategoricalResultAndCharts categoricalResultAndCharts = new CategoricalResultAndCharts();
		List<CategoricalResult> categoricalResults = new ArrayList<CategoricalResult>();

		for (ExperimentDTO experiment : experimentList) {

			// should get one for each sex here if there is a result for each
			// experimental sex
			Integer expBiologicalModelId = experiment.getExperimentalBiologicalModelId();
			BiologicalModel expBiologicalModel = bmDAO.getBiologicalModelById(expBiologicalModelId);
			for (SexType sexType : experiment.getSexes()) { // one graph for
															// each sex if
				if (genderList.isEmpty() || genderList.contains(sexType.name())) {
					List<? extends StatisticalResult> results = (List<? extends StatisticalResult>) experiment
							.getResults();// getCategoricalResultByParameter(parameter,
											// expBiologicalModel.getId(),
											// sexType);
					// System.out.println("statsResults size="+statsResults.size()+
					// "statsResults="+statsResults);
					// categoricalResults.addAll(statsResults);
					// categoricalResultAndCharts.setStatsResults(statsResults);
					CategoricalChartDataObject chartData = new CategoricalChartDataObject();// make
																							// a
																							// new
																							// chart
																							// object
																							// for
																							// each
																							// sex
					chartData.setSexType(sexType);
					List<String> xAxisCategories = this.getXAxisCategories(
							experiment.getZygosities(), zyList);
					// do control first as requires no zygocity
					CategoricalSet controlSet = new CategoricalSet();
					controlSet.setName("Control");

					for (String category : experiment.getCatagories()) {
						if (category.equals("imageOnly"))
							continue;// ignore image categories as no numbers!
						CategoricalDataObject controlCatData = new CategoricalDataObject();
						controlCatData.setName("control");
						controlCatData.setCategory(category);

						long controlCount = 0;
						for (ObservationDTO control : experiment.getControls()) {
							// get the attributes of this data point
							SexType docSexType = SexType.valueOf(control
									.getSex());
							String categoString = control.getCategory();
							if (categoString.equals(category) && docSexType.equals(sexType)) {
								controlCount++;

							}
						}

						controlCatData.setCount(controlCount);
						logger.debug("control=" + sexType.name() + " count="
								+ controlCount + " category=" + category);
						controlSet.add(controlCatData);
					}
					chartData.add(controlSet);

					// now do experimental i.e. zygocities
					for (ZygosityType zType : experiment.getZygosities()) {
						if (zyList.isEmpty() || zyList.contains(zType.name())) {
							CategoricalSet zTypeSet = new CategoricalSet();// hold
																			// the
																			// data
																			// for
																			// each
																			// bar
																			// on
																			// graph
																			// hom,
																			// normal,
																			// abnormal
							zTypeSet.setName(zType.name());
							for (String category : experiment.getCatagories()) {
								if (category.equals("imageOnly"))
									continue;
								logger.debug(zyList);
								Long mutantCount = new Long(0);// .countMutant(sexType,
																// zType,
																// parameter,
																// category,
																// popId);
								// loop over all the experimental docs and get
								// all that apply to current loop parameters
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
								expCatData.setCategory(category);
								expCatData.setCount(mutantCount);
								for(StatisticalResult result: results) {
									if(result.getZygosityType().equals(zType) && result.getSexType().equals(sexType)) {
										expCatData.setResult((CategoricalResult)result);
									}
								}
								List<CategoricalResult> categoricalR = categoricalStatsDao.getCategoricalResultByParameter(parameter, expBiologicalModelId, sexType);
								
								// logger.warn("getting pvalue for sex="+sexType+"  zyg="+
								// zType+" param="+ parameter+" category="+
								// category+"popId="+ popId);
								// List<Double> pValue =
								// categoricalStatsDao.getpValueByParameterAndMutantBiologicalModelAndSexAndZygosity(parameter,
								// expBiologicalModel, sexType, zType);
								// List<Double>
								// maxEffect=categoricalStatsDao.getMaxEffectSizeByParameterAndMutantBiologicalModelAndSexAndZygosity(parameter,
								// expBiologicalModel, sexType, zType);
								// System.out.println("pValue="+pValue);
								// System.out.println("maxEffect");
								// if(pValue.size()>0 && maxEffect.size()>0){
								// //TODO get multiple p values when necessary
								// System.err.println("ERROR WE NEED to change the code to handle multiple p values and max effect!!!!!!!!");
								// expCatData.setpValue(pValue.get(0));
								// expCatData.setMaxEffect(maxEffect.get(0));
								// logger.warn("pValue="+pValue+" maxEffect="+maxEffect);
								// }
								zTypeSet.add(expCatData);

							}
							chartData.add(zTypeSet);
						}
					}

					// removeColumnsWithZeroData(xAxisCategories,
					// seriesDataForCategoricalType);

					// String chart = this.createCategoricalHighChart(
					// categoricalBarCharts, sexType,
					// parameter.getName() ,
					// xAxisCategories, categories,
					// seriesDataForCategoricalType);
					if (xAxisCategories.size() > 1) {// if size is greater than
														// one i.e. we have more
														// than the control data
														// then draw charts and
														// tables
						
						String chartNew = this
								.createCategoricalHighChartUsingObjects(
										chartData,
										parameter.getName(),
										expBiologicalModel);
						chartData.setChart(chartNew);
						categoricalResultAndCharts.add(chartData);
						
						System.out.println("experimental result="+experiment.getResults());
						categoricalResultAndCharts
								.setStatsResults(experiment.getResults());
						// TableObject table =
						// this.creatCategoricalDataTableFromObjects(chartData,
						// sexType, "",
						// xAxisCategories, categories,
						// seriesDataForCategoricalType);
						// tables.add(table);
					}
				}
			}// end of gender

		}// end of experiment loop

		return categoricalResultAndCharts;
	}

	private String createCategoricalHighChartUsingObjects(
			CategoricalChartDataObject chartData, String parameterName,
			BiologicalModel bm) {
		System.out.println(chartData);

		// int size=categoricalBarCharts.size()+1;//to know which div to render
		// to not 0 index as using loop count in jsp
		JSONArray seriesArray = new JSONArray();
		JSONArray xAxisCategoriesArray = new JSONArray();
		String title = parameterName;
		SexType sex = chartData.getSexType();
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
		HashMap<String, List<Long>> categories = new LinkedHashMap<String, List<Long>>();// keep
																							// the
																							// order
																							// so
																							// we
																							// have
																							// normal
																							// first!
		// for(CategoricalSet catSet: catSets){
		CategoricalSet catSet1 = catSets.get(0);// assume each cat set has the
												// same number of categories
		for (CategoricalDataObject catObject : catSet1.getCatObjects()) {
			String category = catObject.getCategory();
			// if(!category.equals("")){
			System.out.println("adding category=" + category);
			categories.put(category, new ArrayList<Long>());
			// }
		}
		// }

		for (CategoricalSet catSet : catSets) {// loop through control, then
												// hom, then het etc
			xAxisCategoriesArray.put(catSet.getName());
			for (CategoricalDataObject catObject : catSet.getCatObjects()) {// each
																			// cat
																			// object
																			// represents
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
				if (i == 0) {
					color = colorNormal;
				} else {
					color = colorAbnormal;
				}
				// dataset1.put("color", color);
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

		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// logger.debug("model="+model);
		String chartId = bm.getId() + sex.name();
		String toolTipFunction = "	{ formatter: function() {         return \''+  this.series.name +': '+ this.y +' ('+ Math.round(this.percentage) +'%)';   }    }";
		String javascript = "$(function () {  var chart"
				+ chartId
				+ "; $(document).ready(function() { chart"
				+ chartId
				+ " = new Highcharts.Chart({ tooltip : "
				+ toolTipFunction
				+ ", chart: { renderTo: 'categoricalBarChart"
				+ chartId
				+ "', type: 'column' }, title: { text: '"
				+ WordUtils.capitalize(title)
				+ "' }, credits: { enabled: false }, subtitle: { text: '"
				+ WordUtils.capitalize(sex.name())
				+ "', x: -20 }, xAxis: { categories: "
				+ xAxisCategoriesArray
				+ "}, yAxis: { min: 0, title: { text: 'Percent Occurrance' } ,  labels: {       formatter: function() { return this.value +'%';   }  }},  plotOptions: { column: { stacking: 'percent' } }, series: "
				+ seriesArray + " });   });});";
		// logger.debug(javascript);
		// categoricalBarCharts.add(javascript);
		chartData.setChart(javascript);
		chartData.setChartIdentifier(chartId);
		chartData.setBiologicalModel(bm);
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

	private List<String> getXAxisCategories(Set<ZygosityType> set,
			List<String> zygosityParams) {
		List<String> xAxisCat = new ArrayList<String>();
		xAxisCat.add("Control");// we know we have controls and we want to put
								// these first.

		for (ZygosityType type : set) {
			if (zygosityParams.isEmpty()
					|| zygosityParams.contains(type.name())) {
				xAxisCat.add(type.name());
			}
		}
		return xAxisCat;
	}

}
