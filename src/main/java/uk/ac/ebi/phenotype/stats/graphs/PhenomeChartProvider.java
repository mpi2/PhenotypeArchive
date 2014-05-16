package uk.ac.ebi.phenotype.stats.graphs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ebi.phenotype.bean.StatisticalResultBean;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;



public class PhenomeChartProvider {

	private static final Logger logger = Logger
			.getLogger(PhenomeChartProvider.class);


	public String createChart(String alleleAccession, double minimalPValue, JSONArray series, JSONArray categories) {

		String chartString="	$(function () { "
				+"  phenomeChart = new Highcharts.Chart({ "
				+"     chart: {"
				+"renderTo: 'chart"
				+ alleleAccession+"',"
				+"         type: 'scatter',"
				+"         zoomType: 'xy'"

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
			    +"             text: '"+"-Log10(p-value)"+"' "
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
			    +"              states: { "
			    +"                hover: { "
			    +"                    enabled: true, "
			    +"                   lineColor: 'rgb(100,100,100)' "
			    +"               } "
			    +"           } "
			    +"       }, "
			    +"       states: { "
			    +"           hover: { "
			    +"               marker: { "
			    +"                   enabled: false "
			    +"               } "
			    +"           } "
			    +"        } "
			    +"     } "
			    +"   }, "
			    +"     series: "+ series.toString()
			    +"    }); "
			    +"	}); ";
		return chartString;
	}

	public String generatePhenomeChart(
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
				tooltip.put("pointFormat", "<b>{point.name}</b><br/>procedure: {series.name}<br/>sex: {point.controlSex}<br/>zygosity: {point.zygosity}<br/>mutants: {point.femaleMutants}f:{point.maleMutants}m<br/>metadata_group: {point.metadataGroup}<br/>p-value: {point.pValue}");
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
								dataPoint.put("controlSex", statsResult.getControlSex());
								dataPoint.put("zygosity", statsResult.getZygosity());
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


		String chartString=createChart(alleleAccession, minimalPvalue, series, categories);

		return chartString;
	}
}
