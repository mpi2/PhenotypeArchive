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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ebi.phenotype.bean.StatisticalResultBean;
import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;

public class PhenomeChartProvider {

	private static final Logger logger = Logger
	.getLogger(PhenomeChartProvider.class);


	public String createPvaluesOverviewChart(String alleleAccession, double minimalPValue, String pointFormat, JSONArray series, JSONArray categories)
	throws JSONException {

		
		String chartString = "	$(function () { \n"
		+ "  pvaluesOverviewChart = new Highcharts.Chart({ \n"
		+ "     chart: {\n"
		+ "renderTo: 'chart" + alleleAccession + "',\n"
		+ "         type: 'scatter',\n"
		+ "         zoomType: 'xy',\n"
		+ "         height: 800\n"
		+ "     },\n"
		+ "   title: {\n"
		+ "        text: ' " + "P-values Overview' \n"
		+ "    },\n"
		+ "     subtitle: {\n"
		+ "        text: 'Parameter by parameter' \n"
		+ "    },\n"
		+ "     yAxis: {\n"
		+ "     categories: " + categories.toString() + ",\n"
		+ "        title: {\n"
		+ "           enabled: true,\n"
		+ "           text: 'Parameters' \n"
		+ "        }, \n"
		+ "       labels: { \n"
	//	+ "           rotation: -90, \n"
	//	+ "           align: 'right', \n"
		+ "           style: { \n"
		+ "              fontSize: '11px', \n"
		+ "              fontFamily: 'Verdana, sans-serif' \n"
		+ "         } \n"
		+ "     }, \n"
		+ "      showLastLabel: true \n"
		+ "  }, \n"
		+ "    xAxis: { \n"
		// +"         min:"+ -Math.log(minimalPValue) + ","
		+ "         title: { \n"
		+ "             text: '" + Constants.MINUS_LOG10_HTML + "(p-value)" + "' \n"
		+ "           }, \n"
		+ "plotLines : [{\n"
		+ "value : " + -Math.log10(minimalPValue) + ",\n"
		+ "color : 'green', \n"
		+ "dashStyle : 'shortdash',\n"
		+ "width : 2,\n"
		+ "label : { text : 'Significance threshold " + minimalPValue + "' }\n"
		+ "}]\n"
		+ "       }, \n"
		+ "      credits: { \n"
		+ "         enabled: false \n"
		+ "      }, \n"
		+ "     tooltip: {\n"
		+ "        headerFormat: '<span style=\"font-size:10px\">{point.name}</span><table>',\n"
		+ "        pointFormat: '" + pointFormat + "',\n"
		+ "        footerFormat: '</table>',\n"
		+ "        shared: 'true',\n"
		+ "        useHTML: 'true',\n"
		+ "     }, \n"
		+ "      plotOptions: { \n"
		+ "        scatter: { \n"
		+ "            marker: { \n"
		+ "                radius: 5, \n"
		+ "                states: { \n"
		+ "                   hover: { \n"
		+ "                      enabled: true, \n"
		+ "                      lineColor: 'rgb(100,100,100)' \n"
		+ "                   } \n"
		+ "                } \n"
		+ "            }, \n"
		+ "            states: { \n"
		+ "               hover: { \n"
		+ "                  marker: { \n"
		+ "                     enabled: false \n"
		+ "                  } \n"
		+ "              } \n"
		+ "            }, \n\n"
		+ "            events: { \n"
		+ "               click: function(event) { \n"
		+ "                   //var sexString = (event.point.sex == \"both\") ? '&gender=male&gender=female' : '&gender=' + event.point.sex; \n"
		+ "                   $.fancybox.open([ \n"
		+ "                  {\n"
		+ "                     href : base_url + '/charts?accession=' + event.point.geneAccession + "
		+ "'&parameter_stable_id=' + event.point.parameter_stable_id + '&allele_accession=' + event.point.alleleAccession + "
		+ "'&zygosity=' + event.point.zygosity + '&phenotyping_center=' + event.point.phenotyping_center + "
		+ "'&pipeline_stable_id=' + event.point.pipeline_stable_id + '&bare=true', \n"
		+ "                     title : event.point.geneAccession \n"
		+ "                  } \n"
		+ "                  ], \n"
		+ "                   { \n"
		+ "                     'maxWidth'          : 1000, \n" 															
		+ "                     'maxHeight'         : 1900, \n"
		+ "                     'fitToView'         : false, \n"
		+ "                     'width'             : '100%',  \n"
		+ "                     'height'            : '85%',  \n"
		+ "                     'autoSize'          : false,  \n"
		+ "                     'transitionIn'      : 'none', \n"
		+ "                     'transitionOut'     : 'none', \n"
		+ "                     'type'              : 'iframe', \n"
		+ "                     scrolling           : 'auto' \n"
		+ "                  }); \n"
		+ "               } \n"
		+ "           } \n" // events
		+ "       } \n"
		+ "   }, \n"
		+ "     series: " + series.toString() + "\n"
		+ "    }); \n"
		+ "	}); \n";
		
		
		
		return chartString;
	}


	/**
	 * Creates a highCharts Phenome summary view plotting p-values for every
	 * significant call for every strain from every IMPReSS parameter for a
	 * specific phenotyping center
	 * 
	 * @param phenotypingCenter
	 *            the specific phenotyping center
	 * @param minimalPValue
	 *            set the minimal threshold
	 * @param series
	 *            series of categories to plot
	 * @param categories
	 *            list of categories (one for every MP term)
	 * @return the chart to be displayed
	 * @throws JSONException
	 */
	public String createPhenomeChart(String phenotypingCenter, double minimalPValue, String pointFormat, JSONArray series, JSONArray categories)
	throws JSONException {

		String chartString = "	$(function () { \n"
		+ "  phenomeChart = new Highcharts.Chart({ \n"
		+ "     chart: {\n"
		+ "renderTo: 'phenomeChart',\n"
		+ "         type: 'scatter',\n"
		+ "         zoomType: 'xy',\n"
		+ "         height: 800\n"
		+ "     },\n"
		+ "   title: {\n"
		+ "       text: 'Significant MP calls'\n"
		+ "    },\n"
		+ "     subtitle: {\n"
		+ "        text: 'by Top Level MP Categories'\n"
		+ "    },\n"
		+ "     xAxis: {\n"
		+ "     categories: " + categories.toString() + ",\n"
		+ "        title: {\n"
		+ "           enabled: true,\n"
		+ "           text: 'Top Level Mammalian Phenotype Ontology Terms' \n"
		+ "        }, \n"
		+ "       labels: { \n"
		+ "           rotation: -90, \n"
		+ "           align: 'right', \n"
		+ "           style: { \n"
		+ "              fontSize: '10px', \n"
		+ "              fontFamily: 'Verdana, sans-serif' \n"
		+ "         } \n"
		+ "     }, \n"
		+ "      showLastLabel: true \n"
		+ "  }, \n"
		+ "    yAxis: { \n"
		+ "min: 0,\n"
		+ "max: " + -Math.log10(1E-21) + ",\n"
		+ "         title: { \n"
		+ "             text: '" + Constants.MINUS_LOG10_HTML + "(p-value)" + "' \n"
		+ "           }, \n"
		+ "       }, \n"
		+ "      credits: { \n"
		+ "         enabled: false \n"
		+ "      }, \n"
		+ "     tooltip: {\n"
		+ "        headerFormat: '<span style=\"font-size:10px\">{point.name}</span><table>',\n"
		+ "        pointFormat: '" + pointFormat + "',\n"
		+ "        footerFormat: '</table>',\n"
		+ "        shared: 'true',\n"
		+ "        useHTML: 'true',\n"
		+ "     }, \n"
		+ "      plotOptions: { \n"
		+ "        scatter: { \n"
		+ "            marker: { \n"
		+ "                radius: 5, \n"
		+ "                states: { \n"
		+ "                   hover: { \n"
		+ "                      enabled: true, \n"
		+ "                      lineColor: 'rgb(100,100,100)' \n"
		+ "                   } \n"
		+ "                } \n"
		+ "            }, \n"
		+ "            states: { \n"
		+ "               hover: { \n"
		+ "                  marker: { \n"
		+ "                     enabled: false \n"
		+ "                  } \n"
		+ "              } \n"
		+ "            }, \n"
		+ "            events: { \n"
		+ "               click: function(event) { \n"
		+ "                   //var sexString = (event.point.sex == \"both\") ? '&gender=male&gender=female' : '&gender=' + event.point.sex; \n"
		+ "                   $.fancybox.open([ \n"
		+ "                  {\n"
		+ "                     href : base_url + '/charts?accession=' + event.point.geneAccession +"
		+ "'&parameter_stable_id=' + event.point.parameter_stable_id + '&allele_accession=' + event.point.alleleAccession + "
		+ "'&zygosity=' + event.point.zygosity + '&phenotyping_center=' + event.point.phenotyping_center + "
		+ "'&pipeline_stable_id=' + event.point.pipeline_stable_id + '&bare=true', \n"
		+ "                     title : event.point.geneAccession \n"
		+ "                  } \n"
		+ "                  ], \n"
		+ "                   { \n"
		+ "                     'maxWidth'          : 1000, \n" // 980 too
																// narrow
		+ "                     'maxHeight'         : 900, \n"
		+ "                     'fitToView'         : false, \n"
		+ "                     'width'             : '100%',  \n"
		+ "                     'height'            : '85%',  \n"
		+ "                     'autoSize'          : false,  \n"
		+ "                     'transitionIn'      : 'none', \n"
		+ "                     'transitionOut'     : 'none', \n"
		+ "                     'type'              : 'iframe', \n"
		+ "                     scrolling           : 'auto' \n"
		+ "                  }); \n"
		+ "               } \n"
		+ "           } \n" // events
		+ "       } \n"
		+ "   }, \n"
		+ "     series: " + series.toString() + "\n"
		+ "    }); \n"
		+ "	}); \n";
		return chartString;
	}


	public String createPhenomeChartByGene(String phenotypingCenter, double minimalPValue, String pointFormat, JSONArray series, JSONArray categories)
	throws JSONException {

		String chartString = "	$(function () {"+
"		$('#container').highcharts({"+
"       title: {"+
"            text:  'Significant MP calls'"+
"      },"+
"       subtitle: {"+
 "           text: 'by Top Level MP Categories & Genes',"+
 "           x: -20"+
 "       },"+
 "       xAxis: {"+
 "           categories:  " + categories.toString() + ","+
 "           title: {"+
 "               enabled: true,"+
 "               text: 'Top Level Mammalian Phenotype Ontology Terms'"+
 "           },"+
 "           labels: {"+
 "               rotation: -90,"+
 "               align: 'right',"+
 "               style: {"+
 "                   fontSize: '10px',"+
 "                   fontFamily: 'Verdana, sans-serif'"+
 "               }"+
 "           },"+
 "           showLastLabel: true"+
 "       },"+
 "       yAxis: {"+
 "            min: 0,"+
 "           max: 21.0,"+
 "           title: {"+
 "               text: '-Log<sub>10</sub>(p-value)'"+
 "           },"+
 "       },"+
 "      credits: {"+
 "           enabled: false"+
 "       },"+
 "       tooltip: {"+
 "           headerFormat: '<span style=\"font-size:10px\">{point.name}</span><table>',"+
 "           pointFormat: '<tr><td style=\"color:{series.color};padding:0\">Top Level MP: {series.name}</td></tr><tr><td style=\"padding:0\">MP Term: {point.mp_term}</td></tr><tr><td style=\"padding:0\">Gene: {point.geneSymbol}</td></tr><tr><td style=\"padding:0\">Zygosity: {point.zygosity}</td></tr><tr><td style=\"padding:0\">P-value: {point.pValue}</td></tr><tr><td style=\"padding:0\">Effect size: {point.effectSize}</td></tr>',"+
 "           footerFormat: '</table>',"+
 "           shared: 'true',"+
 "           useHTML: 'true',"+
 "       },"+
 "       legend: {"+
 "           layout: 'vertical',"+
 "           align: 'right',"+
 "           verticalAlign: 'middle',"+
 "           borderWidth: 0"+
 "       },"+
 "       series: " + series.toString() + 
 "   });"+
 "});";
		return chartString;
	}
	
	public String generatePhenomeChartByPhenotype(
	List<PhenotypeCallSummary> calls,
	String phenotypingCenter,
	double minimalPvalue)
	throws IOException,
	URISyntaxException {

		String chartString = null;

		JSONArray series = new JSONArray();

		JSONArray categories = new JSONArray();
		List<String> categoryGroupList = new ArrayList<String>();
		Map<String, List<String>> specificTermMatrix = new HashMap<String, List<String>>();

		Map<String, JSONObject> seriesMap = new HashMap<String, JSONObject>();

		try {

			// build tooltip
			StringBuilder pointFormat = new StringBuilder();

			pointFormat.append("<tr><td style=\"color:{series.color};padding:0\">Top Level MP: {series.name}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">MP Term: {point.mp_term}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">Gene: {point.geneSymbol}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">Zygosity: {point.zygosity}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">P-value: {point.pValue}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">Effect size: {point.effectSize}</td></tr>");

			// first grab all categories and associated terms

			for (PhenotypeCallSummary call : calls) {

					for (OntologyTerm topLevel : call.getTopLevelPhenotypeTerms()) {

						String topLevelName = topLevel.getName();
						if (!categoryGroupList.contains(topLevelName)) {

							specificTermMatrix.put(topLevelName, new ArrayList<String>());
							categoryGroupList.add(topLevelName);

							JSONObject scatterJsonObject = new JSONObject();
							seriesMap.put(topLevelName, scatterJsonObject);

							scatterJsonObject.put("type", "scatter");
							scatterJsonObject.put("name", topLevelName);

							JSONArray dataArray = new JSONArray();

							scatterJsonObject.put("data", dataArray);

							series.put(scatterJsonObject);

						}

						if (!specificTermMatrix.get(topLevelName).contains(call.getPhenotypeTerm().getName())) {
							specificTermMatrix.get(topLevelName).add(call.getPhenotypeTerm().getName());
						}
					}
			}

			// Then generate categories for all of them
			int categoriesDim = 0;
			int total = 0;
			for (String categoryName : categoryGroupList) {
				for (String specificTerm : specificTermMatrix.get(categoryName)) {
					categories.put((categoriesDim + 1) + ". " + specificTerm);
					total++;
				}
				categoriesDim++;
			}
			// finally extract the data points and generate a point for every
			// top level categories associated
				for (PhenotypeCallSummary call : calls) {
					for (OntologyTerm topLevel : call.getTopLevelPhenotypeTerms()) {

						String topLevelName = topLevel.getName();
						int firstDim = categoryGroupList.indexOf(topLevelName);

						// convert to position on x axis
						int index = 0;
						for (int i = 0; i <= firstDim; i++) {
							index += (i != firstDim) ?
							specificTermMatrix.get(categoryGroupList.get(i)).size() :
							specificTermMatrix.get(topLevelName).indexOf(call.getPhenotypeTerm().getName());
						}

						JSONObject dataPoint = new JSONObject();
						dataPoint.put("name", (firstDim + 1) + ". " + call.getPhenotypeTerm().getName());
						dataPoint.put("mp_term", call.getPhenotypeTerm().getName());
						dataPoint.put("geneSymbol", call.getGene().getSymbol());
						dataPoint.put("geneAccession", call.getGene().getId().getAccession());
						dataPoint.put("alleleAccession", call.getAllele().getId().getAccession());
						dataPoint.put("parameter_stable_id", call.getParameter().getStableId());
						dataPoint.put("pipeline_stable_id", call.getPipeline().getStableId());
						dataPoint.put("phenotyping_center", call.getPhenotypingCenter());
						dataPoint.put("x", index);
						dataPoint.put("y", call.getLogValue() + addJitter(call.getEffectSize()));
						dataPoint.put("pValue", call.getpValue());
						dataPoint.put("effectSize", call.getEffectSize());
						dataPoint.put("sex", call.getSex());
						dataPoint.put("zygosity", call.getZygosity());

						((JSONArray) seriesMap.get(topLevelName).get("data")).put(dataPoint);

					}
				}

				// finally sort by index
				for (String topLevelName : categoryGroupList) {

					JSONArray array = ((JSONArray) seriesMap.get(topLevelName).get("data"));
					seriesMap.get(topLevelName).put("data", this.getSortedList(array));
				}

			chartString = createPhenomeChart(phenotypingCenter, minimalPvalue, pointFormat.toString(), series, categories);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return chartString;
	}
	
	public String generatePhenomeChartByGenes(
	List<PhenotypeCallSummary> calls,
	String phenotypingCenter,
	double minimalPvalue)
	throws IOException,
	URISyntaxException {

		String chartString = null;

		JSONArray series = new JSONArray();

		JSONArray categories = new JSONArray();
		List<String> categoryGroupList = new ArrayList<String>();
		List<String> specificTerms = new ArrayList<String>();

		Map<String, JSONObject> seriesMap = new HashMap<String, JSONObject>();

		try {

			// build tooltip
			StringBuilder pointFormat = new StringBuilder();

			pointFormat.append("<tr><td style=\"color:{series.color};padding:0\">Top Level MP: {series.name}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">MP Term: {point.mp_term}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">Gene: {point.geneSymbol}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">Zygosity: {point.zygosity}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">P-value: {point.pValue}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">Effect size: {point.effectSize}</td></tr>");

			// first grab all categories and associated terms
			
			Map<String, List<String>> phenotypeGroups = new HashMap<String, List<String>>();
			
			
			// Set phenotype order to show (x-axis)
			for (PhenotypeCallSummary call : calls) {

				String topLevelName = call.getTopLevelPhenotypeTerms().get(0).getName();
				if (!phenotypeGroups.containsKey(topLevelName)) {
					phenotypeGroups.put(topLevelName, new ArrayList<String>());

				}

				if (!phenotypeGroups.get(topLevelName).contains(call.getPhenotypeTerm().getName())) {
					phenotypeGroups.get(topLevelName).add(call.getPhenotypeTerm().getName());
				}
			}

			for (String topLevelMP:phenotypeGroups.keySet()){
				for (String mp : phenotypeGroups.get(topLevelMP)){
					categoryGroupList.add(mp);
					categories.put(mp);
				}
			}
						
			// get genes 
			for (PhenotypeCallSummary call : calls) {

					String gene = call.getGene().getSymbol();
					if (!specificTerms.contains(gene)) {
						specificTerms.add(gene);
						JSONObject scatterJsonObject = new JSONObject();
						seriesMap.put(gene, scatterJsonObject);
						scatterJsonObject.put("name", gene);
						JSONArray dataArray = new JSONArray();
						scatterJsonObject.put("data", dataArray);
						series.put(scatterJsonObject);
					}
			}


			// finally extract the data points and generate a point for every
			// top level categories associated.
				for (PhenotypeCallSummary call : calls) {

					String gene = call.getGene().getSymbol();
					int firstDim = categoryGroupList.indexOf(gene);

					// convert to position on x axis
					
					JSONObject dataPoint = new JSONObject();
					dataPoint.put("name", (firstDim + 1) + ". " + call.getPhenotypeTerm().getName());
					dataPoint.put("mp_term", call.getPhenotypeTerm().getName());
					dataPoint.put("geneSymbol", call.getGene().getSymbol());
					dataPoint.put("geneAccession", call.getGene().getId().getAccession());
					dataPoint.put("alleleAccession", call.getAllele().getId().getAccession());
					dataPoint.put("parameter_stable_id", call.getParameter().getStableId());
					dataPoint.put("pipeline_stable_id", call.getPipeline().getStableId());
					dataPoint.put("phenotyping_center", call.getPhenotypingCenter());
					dataPoint.put("x", categoryGroupList.indexOf(call.getPhenotypeTerm().getName()));
					dataPoint.put("y", call.getLogValue() + addJitter(call.getEffectSize()));
					dataPoint.put("pValue", call.getpValue());
					dataPoint.put("effectSize", call.getEffectSize());
					dataPoint.put("sex", call.getSex());
					dataPoint.put("zygosity", call.getZygosity());
					((JSONArray) seriesMap.get(gene).get("data")).put(dataPoint);
				}

				// finally sort by index
				for (String geneSymbol : seriesMap.keySet()) {

					JSONArray array = ((JSONArray) seriesMap.get(geneSymbol).get("data"));
					seriesMap.get(geneSymbol).put("data", this.getSortedList(array));
				}

			chartString = createPhenomeChart(phenotypingCenter, minimalPvalue, pointFormat.toString(), series, categories);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return chartString;
	}


	/**
	 * 
	 * @param alleleAccession
	 * @param statisticalResults
	 * @param minimalPvalue
	 * @param pipeline
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String generatePvaluesOverviewChart(	Allele allele,	Map<String, List<StatisticalResultBean>> statisticalResults, double minimalPvalue, Map<String, List<String>> parametersByProcedure, String phenotypingCenter, String pipelineStableId)
	throws IOException,	URISyntaxException {

		String chartString = null;
		JSONArray series = new JSONArray();
		ArrayList<String> categories = new ArrayList();

		try {

			int index = 0;
			StringBuilder pointFormat = new StringBuilder();

			pointFormat.append("<tr><td style=\"color:{series.color};padding:0\">parameter: {point.name}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">procedure: {series.name}</td></tr>");
		//	pointFormat.append("<tr><td style=\"padding:0\">sex: {point.controlSex}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">zygosity: {point.zygosity}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">mutants: {point.femaleMutants}f:{point.maleMutants}m</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">metadata_group: {point.metadataGroup}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">p-value: {point.pValue}</td></tr>");
			pointFormat.append("<tr><td style=\"padding:0\">Effect size: {point.effectSize}</td></tr>");

			// Create a statistical series for every procedure in the pipeline
			// Start from the pipeline so that there is no need to keep this
			// information from the caller side
			// get All procedures and generate a Map Parameter => Procedure
						
			for (String procedure : parametersByProcedure.keySet()) {

				JSONObject scatterJsonObject = new JSONObject();
				JSONArray dataArray = new JSONArray();

				scatterJsonObject.put("type", "scatter");
				scatterJsonObject.put("name", procedure);
				// create a series here
								
				for (String parameterStableId : parametersByProcedure.get(procedure)) {
											
					if (statisticalResults.containsKey(parameterStableId)) {
									
						int resultIndex = 0;
						long tempTime = System.currentTimeMillis();						
						StatisticalResultBean statsResult = statisticalResults.get(parameterStableId).get(0);							
													
						// smallest p-value sis the first (solr docs are sorted)
						if (statsResult.getIsSuccessful() && resultIndex == 0) { 

							// create the point first
							JSONObject dataPoint = new JSONObject();
							dataPoint.put("name", statsResult.getParameterName());
							dataPoint.put("parameter_stable_id", parameterStableId);
							dataPoint.put("parameter_name", statsResult.getParameterName());
							dataPoint.put("pipeline_stable_id", pipelineStableId);
							dataPoint.put("geneAccession", allele.getGene().getId().getAccession());
							dataPoint.put("alleleAccession", allele.getId().getAccession());
							dataPoint.put("phenotyping_center", phenotypingCenter);
							dataPoint.put("y", index);
							dataPoint.put("x", statsResult.getLogValue());
							dataPoint.put("pValue", statsResult.getpValue());
							dataPoint.put("effectSize", statsResult.getEffectSize());
							dataPoint.put("sex", statsResult.getControlSex());
							dataPoint.put("zygosity", statsResult.getZygosity());
							// maybe change for the complete object here
							dataPoint.put("femaleMutants", statsResult.getFemaleMutants());
							dataPoint.put("maleMutants", statsResult.getMaleMutants());
							dataPoint.put("metadataGroup", statsResult.getMetadataGroup());								
							
							if (!categories.contains(statsResult.getParameterName())) {
								categories.add(statsResult.getParameterName());
								dataArray.put(dataPoint);
								resultIndex++;
								index++;
							}
						}
					}					
				}

				if (dataArray.length() > 0) {
					scatterJsonObject.put("data", dataArray);
					series.put(scatterJsonObject);
				}
			}
			chartString = createPvaluesOverviewChart( allele.getId().getAccession(), minimalPvalue, pointFormat.toString(),	series,	new JSONArray(categories));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return chartString;
	}


	/**
	 * Add jitter to a data point by capping the effect size between 0 and 0.8
	 * 
	 * @param effectSize
	 *            the effect size associated to a MP call
	 * @return a jittered value based on the effect size
	 */
	double addJitter(double effectSize) {

		// cap to 0.8 max otherwise this means nothing
		// 2 decimals
		double scale = 8E-2;
		boolean neg = (effectSize < 0);
		return (((Math.abs(effectSize) >= 10) ? ((neg) ? -10 : 10) : effectSize) * scale);
	}


	private JSONArray getSortedList(JSONArray array)
	throws JSONException {

		List<JSONObject> list = new ArrayList<JSONObject>();
		for (int i = 0; i < array.length(); i++) {
			list.add(array.getJSONObject(i));
		}
		Collections.sort(list, new JSONSortBasedonXAxisIndexComparator());

		JSONArray resultArray = new JSONArray(list);

		return resultArray;

	}

	protected class JSONSortBasedonXAxisIndexComparator implements Comparator<JSONObject>
	{

		public int compare(JSONObject a, JSONObject b)
		{

			try {
				int valA = a.getInt("x");
				int valB = b.getInt("x");

				if (valA > valB)
					return 1;
				if (valA < valB)
					return -1;

				return 0;

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 0;
			}
		}
	}

}
