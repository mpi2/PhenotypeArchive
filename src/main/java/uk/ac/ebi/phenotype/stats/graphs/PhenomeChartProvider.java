package uk.ac.ebi.phenotype.stats.graphs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ebi.phenotype.bean.StatisticalResultBean;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.stats.Constants;



public class PhenomeChartProvider {

	private static final Logger logger = Logger
			.getLogger(PhenomeChartProvider.class);


	public String createPvaluesOverviewChart(String alleleAccession, double minimalPValue, JSONArray series, JSONArray categories) {

		String chartString="	$(function () { "
				+"  pvaluesOverviewChart = new Highcharts.Chart({ "
				+"     chart: {"
				+"renderTo: 'chart"
				+ alleleAccession+"',"
				+"         type: 'scatter',"
				+"         zoomType: 'xy',"
				+"         height: 800"
			    +"     },"
			    +"   title: {"
			    +"       text: ' "+"P-values Overview" 
			    +"'    },"
			    +"     subtitle: {"
			    +"        text: ' "+"Parameter by parameter"+" ' "
			    +"    },"
			    +"     xAxis: {"
			    +"     categories: "+ categories.toString() + ","
			    +"        title: {"
			    +"           enabled: true,"
			    +"           text: 'Parameters' "
			    +"        }, "
			    +"       labels: { "
			    +"           rotation: -90, "
			    +"           align: 'right', "
			    +"           style: { "
			    +"              fontSize: '11px', "
			    +"              fontFamily: 'Verdana, sans-serif' "
			    +"         } "
			    +"     }, "
			    +"      showLastLabel: true "
			    +"  }, "
			    +"    yAxis: { "
			    //+"         min:"+ -Math.log(minimalPValue) + ","
			    +"         title: { "
			    +"             text: '"+ Constants.MINUS_LOG10_HTML + "(p-value)"+"' "
			    +"           }, "
			    + "plotLines : [{"
			    + "value : " + -Math.log10(minimalPValue) + ","
			    + "color : 'green', "
			    + "dashStyle : 'shortdash',"
			    + "width : 2,"
			    + "label : { text : 'Significance threshold " + minimalPValue + "' }"
			    + "}]"
			    +"       }, "
			    +"      credits: { "
			    +"         enabled: false "
			    +"      }, "
			    +"      plotOptions: { "
			    +"        scatter: { "
			    +"            marker: { "
			    +"                radius: 5, "
			    +"                states: { "
			    +"                   hover: { "
			    +"                      enabled: true, "
			    +"                      lineColor: 'rgb(100,100,100)' "
			    +"                   } "
			    +"                } "			    
			    +"            }, "
			    +"            states: { "
			    +"               hover: { "
			    +"                  marker: { "
			    +"                     enabled: false "
			    +"                  } "
			    +"              } "
			    +"            }, \n"	    
                +"            events: { "
                +"               click: function(event) { "
                +"                   var sexString = (event.point.sex == \"both\") ? '&gender=male&gender=female' : '&gender=' + event.point.sex; "
                +"                   $.fancybox.open([ "
                + "                  {"
                + "                     href : base_url + '/charts?accession=' + event.point.geneAccession +"
                + "'&parameter_stable_id=' + event.point.parameter_stable_id + '&allele_accession=' + event.point.alleleAccession + "
                + "'&zygosity=' + event.point.zygosity + sexString + '&phenotyping_center=' + event.point.phenotyping_center + "
                + "'&pipeline_stable_id=' + event.point.pipeline_stable_id + '&bare=true', "
                + "                     title : event.point.geneAccession "
                + "                  } "
                + "                  ], "
                +"                   { "
                +"                     'maxWidth'          : 1000, " // 980 too narrow
        		+"                     'maxHeight'         : 900, "
        		+"                     'fitToView'         : false, "                
                +"                     'width'             : '100%',  "
                +"                     'height'            : '85%',  "
                +"                     'autoSize'          : false,  "
                +"                     'transitionIn'      : 'none', "
                +"                     'transitionOut'     : 'none', "
                +"                     'type'              : 'iframe', "
                +"                     scrolling           : 'auto' "
                +"                  }); "             
                +"               } "
			    +"           } \n" // events
			    +"       } "                
			    +"   }, "
			    +"     series: "+ series.toString()
			    +"    }); "
			    +"	}); ";
		return chartString;
	}

	/**
	 * Creates a highCharts Phenome summary view plotting p-values for every 
	 * significant call for every strain from every IMPReSS parameter for a 
	 * specific phenotyping center
	 * @param phenotypingCenter the specific phenotyping center
	 * @param minimalPValue set the minimal threshold
	 * @param series series of categories to plot
	 * @param categories list of categories (one for every MP term)
	 * @return the chart to be displayed
	 */
	public String createPhenomeChart(String phenotypingCenter, double minimalPValue, JSONArray series, JSONArray categories) {

		String chartString="	$(function () { "
				+"  phenomeChart = new Highcharts.Chart({ "
				+"     chart: {"
				+"renderTo: 'chart"
				+ phenotypingCenter+"',"
				+"         type: 'scatter',"
				+"         zoomType: 'xy',"
				+"         height: 800"
			    +"     },"
			    +"   title: {"
			    +"       text: ' "+"Significant MP calls" 
			    +"'    },"
			    +"     subtitle: {"
			    +"        text: ' "+"by Top Level MP Categories"+" ' "
			    +"    },"
			    +"     xAxis: {"
			    +"     categories: "+ categories.toString() + ","
			    +"        title: {"
			    +"           enabled: true,"
			    +"           text: 'Top Level Mammalian Phenotype Ontology Terms' "
			    +"        }, "
			    +"       labels: { "
			    +"           rotation: -90, "
			    +"           align: 'right', "
			    +"           style: { "
			    +"              fontSize: '10px', "
			    +"              fontFamily: 'Verdana, sans-serif' "
			    +"         } "
			    +"     }, "
			    +"      showLastLabel: true "
			    +"  }, "
			    +"    yAxis: { "
			    +            "min: 0,"
			    +            "max: "+ -Math.log10(1E-21) + ","
			    +"         title: { "
			    +"             text: '" + Constants.MINUS_LOG10_HTML + "(p-value)"+"' "
			    +"           }, "
			    +"       }, "
			    +"      credits: { "
			    +"         enabled: false "
			    +"      }, "
			    +"      plotOptions: { "
			    +"        scatter: { "
			    +"            marker: { "
			    +"                radius: 5, "
			    +"                states: { "
			    +"                   hover: { "
			    +"                      enabled: true, "
			    +"                      lineColor: 'rgb(100,100,100)' "
			    +"                   } "
			    +"                } "			    
			    +"            }, "
			    +"            states: { "
			    +"               hover: { "
			    +"                  marker: { "
			    +"                     enabled: false "
			    +"                  } "
			    +"              } "
			    +"            }, \n"	    
                +"            events: { "
                +"               click: function(event) { "
                +"                   var sexString = (event.point.sex == \"both\") ? '&gender=male&gender=female' : '&gender=' + event.point.sex; "
                +"                   $.fancybox.open([ "
                + "                  {"
                + "                     href : base_url + '/charts?accession=' + event.point.geneAccession +"
                + "'&parameter_stable_id=' + event.point.parameter_stable_id + '&allele_accession=' + event.point.alleleAccession + "
                + "'&zygosity=' + event.point.zygosity + sexString + '&phenotyping_center=' + event.point.phenotyping_center + "
                + "'&pipeline_stable_id=' + event.point.pipeline_stable_id + '&bare=true', "
                + "                     title : event.point.geneAccession "
                + "                  } "
                + "                  ], "
                +"                   { "
                +"                     'maxWidth'          : 1000, " // 980 too narrow
        		+"                     'maxHeight'         : 900, "
        		+"                     'fitToView'         : false, "                
                +"                     'width'             : '100%',  "
                +"                     'height'            : '85%',  "
                +"                     'autoSize'          : false,  "
                +"                     'transitionIn'      : 'none', "
                +"                     'transitionOut'     : 'none', "
                +"                     'type'              : 'iframe', "
                +"                     scrolling           : 'auto' "
                +"                  }); "             
                +"               } "
			    +"           } \n" // events
			    +"       } "                
			    +"   }, "
			    +"     series: "+ series.toString()
			    +"    }); "
			    +"	}); ";
		return chartString;
	}

	public String generatePhenomeChart(
			List<PhenotypeCallSummary> calls, 
			String phenotypingCenter,
			double minimalPvalue) throws IOException,
			URISyntaxException {

		JSONArray series = new JSONArray();

		JSONArray categories = new JSONArray();
		List<String> topLevelOntologyTermsList = new ArrayList<String>();
		Map<String, List<String>> specificTermMatrix = new HashMap<String, List<String>>();

		Map<String, JSONObject> seriesMap = new HashMap<String, JSONObject>();

		try {

			// first grab all categories and associated terms
			
			for (PhenotypeCallSummary call: calls) {

				for (OntologyTerm topLevel: call.getTopLevelPhenotypeTerms()) {

					List<PhenotypeCallSummary> toTopLevelCalls = null;

					String topLevelName = topLevel.getName();
					if (!topLevelOntologyTermsList.contains(topLevelName)) {

						specificTermMatrix.put(topLevelName, new ArrayList<String>());
						topLevelOntologyTermsList.add(topLevelName);
						
						JSONObject scatterJsonObject = new JSONObject();
						seriesMap.put(topLevelName, scatterJsonObject);

						JSONObject tooltip=new JSONObject();
						//tooltip.put("headerFormat", "<b>{point.name}</b><br>");
						tooltip.put("pointFormat", "<b>{point.mp_term}</b><br/>Top Level MP: {series.name}<br/>Gene: {point.geneSymbol}<br/>Zygosity: {point.zygosity}<br/>p-value: {point.pValue}<br/>Effect size: {point.effectSize}");
						scatterJsonObject.put("tooltip", tooltip);
						scatterJsonObject.put("type", "scatter");
						scatterJsonObject.put("name", topLevelName);

						JSONArray dataArray=new JSONArray();

						scatterJsonObject.put("data", dataArray);

						series.put(scatterJsonObject);

					}

					if (!specificTermMatrix.get(topLevelName).contains(call.getPhenotypeTerm().getName())) {
						specificTermMatrix.get(topLevelName).add(call.getPhenotypeTerm().getName());
					}
				}
			}

			// Then generate categories for all of them
			int topLevelDim = 0;
			int total = 0;
			for (String topLevelName: topLevelOntologyTermsList) {
				for (String specificTerm: specificTermMatrix.get(topLevelName)) {
					categories.put((topLevelDim+1) + ". " + specificTerm);
					total++;
				}
				topLevelDim++;
			}
			System.out.println("TOTAL CATS=" +total);
			
			// finally extract the data points and generate a point for every
			// top level categories associated.
			for (PhenotypeCallSummary call: calls) {

				for (OntologyTerm topLevel: call.getTopLevelPhenotypeTerms()) {

					String topLevelName = topLevel.getName();
					int firstDim = topLevelOntologyTermsList.indexOf(topLevelName);
					
					// convert to position on x axis
					int index = 0;
					for (int i=0; i<=firstDim; i++) {
						index+= (i != firstDim) ?
								specificTermMatrix.get(topLevelOntologyTermsList.get(i)).size() :
								specificTermMatrix.get(topLevelName).indexOf(call.getPhenotypeTerm().getName());	 
					}
					
					JSONObject dataPoint=new JSONObject();
					dataPoint.put("name", (firstDim+1) +". " + call.getPhenotypeTerm().getName());
					dataPoint.put("mp_term", call.getPhenotypeTerm().getName());
					dataPoint.put("geneSymbol", call.getGene().getSymbol());
					dataPoint.put("geneAccession", call.getGene().getId().getAccession());
					dataPoint.put("alleleAccession", call.getAllele().getId().getAccession());
					dataPoint.put("parameter_stable_id", call.getParameter().getStableId());
					dataPoint.put("pipeline_stable_id", call.getPipeline().getStableId());
					dataPoint.put("phenotyping_center", phenotypingCenter);
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
			for (String topLevelName: topLevelOntologyTermsList) {

				JSONArray array = ((JSONArray) seriesMap.get(topLevelName).get("data"));
				seriesMap.get(topLevelName).put("data", this.getSortedList(array));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		String chartString=createPhenomeChart(phenotypingCenter, minimalPvalue, series, categories);

		return chartString;
	}

	public String generatePvaluesOverviewChart(
			String alleleAccession, Map<String, List<StatisticalResultBean>> statisticalResults,
			double minimalPvalue,
			Pipeline pipeline) throws IOException,
			URISyntaxException {

		JSONArray series=new JSONArray();

		JSONArray categories = new JSONArray();

		try {

			int index = 0;

			// Create a statistical series for every procedure in the pipeline
			// Start from the pipeline so that there is no need to keep this 
			// information from the caller side
			// get All procedures and generate a Map Parameter => Procedure
			Map<Parameter, Procedure> parametersToProcedure = new HashMap<Parameter, Procedure>();
			Map<String, Parameter> parametersMap = new HashMap<String, Parameter>();
			for (Procedure procedure: pipeline.getProcedures()) {

				JSONObject scatterJsonObject=new JSONObject();
				// Tooltip first for correct formatting
				/*	        tooltip: {

						     headerFormat: '<b>{series.name}</b><br>',
						     pointFormat: '{point.name}<br/>value: {point.y}'
						                    },*/

				JSONObject tooltip=new JSONObject();
				//tooltip.put("headerFormat", "<b>{point.name}</b><br>");
				tooltip.put("pointFormat", "<b>{point.name}</b><br/>procedure: {series.name}<br/>sex: {point.controlSex}<br/>zygosity: {point.zygosity}<br/>mutants: {point.femaleMutants}f:{point.maleMutants}m<br/>metadata_group: {point.metadataGroup}<br/>p-value: {point.pValue}<br/>Effect size: {point.effectSize}");
				scatterJsonObject.put("tooltip", tooltip);
				scatterJsonObject.put("type", "scatter");
				scatterJsonObject.put("name", procedure.getName());

				JSONArray dataArray=new JSONArray();

				// create a series here
				for (Parameter parameter: procedure.getParameters()) {

					/*			 data: [{
						        name: 'IMPC_...',
						        x: 1,
						        y: 2
						    }, {
						        name: 'IMPC_...',
						        x: 2,
						        y: 5
						    }]		*/	

					if (statisticalResults.containsKey(parameter.getStableId())) {

						int resultIndex = 0;
						for (StatisticalResultBean statsResult: statisticalResults.get(parameter.getStableId())) {

							if ( statsResult.getIsSuccessful() ) {

								// create the point first
								JSONObject dataPoint=new JSONObject();
								dataPoint.put("name", parameter.getName());
								dataPoint.put("stableId", parameter.getStableId());
								dataPoint.put("x", index);
								dataPoint.put("y", statsResult.getLogValue());
								dataPoint.put("pValue", statsResult.getpValue());
								dataPoint.put("effectSize", statsResult.getEffectSize());
								dataPoint.put("controlSex", statsResult.getControlSex());
								dataPoint.put("zygosity", statsResult.getZygosity());
								// maybe change for the complete object here
								dataPoint.put("femaleMutants", statsResult.getFemaleMutants());
								dataPoint.put("maleMutants", statsResult.getMaleMutants());
								dataPoint.put("metadataGroup", statsResult.getMetadataGroup());
								dataArray.put(dataPoint);


								if (resultIndex == 0) {
									categories.put(parameter.getStableId());
									index++;
								}

								resultIndex++;


							}
						}
					}

				}

				if (dataArray.length() > 0) {
					scatterJsonObject.put("data", dataArray);
					series.put(scatterJsonObject);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		String chartString=createPvaluesOverviewChart(alleleAccession, minimalPvalue, series, categories);

		return chartString;
	}

	/**
	 * Add jitter to a data point by capping the effect size between 0 and 0.8
	 * @param effectSize the effect size associated to a MP call
	 * @return a jittered value based on the effect size
	 */
	double addJitter(double effectSize) {
		// cap to 0.8 max otherwise this means nothing
		// 2 decimals
		double scale = 8E-2;
		boolean neg = (effectSize < 0);
		return (((Math.abs(effectSize) >= 10) ? ((neg) ? -10 : 10) : effectSize) * scale);
	}
	
	private JSONArray getSortedList(JSONArray array) throws JSONException {
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

				if(valA > valB)
					return 1;
				if(valA < valB)
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
