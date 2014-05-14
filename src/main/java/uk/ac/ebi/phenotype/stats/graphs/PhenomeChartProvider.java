package uk.ac.ebi.phenotype.stats.graphs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
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


	public String createChart(String alleleAccession, JSONArray series, JSONArray categories) {

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
			    +"         title: { "
			    +"             text: '"+"-Log10(p-value)"+"' "
			    +"           }, "
			    + "plotLines : [{"
				+ "value : " + -Math.log10( 0.0001 ) + ","
				+ "color : 'green', "
				+ "dashStyle : 'shortdash',"
				+ "width : 2,"
				+ "label : { useHTML: true, text : 'Significance threshold 1.00E-4' }"
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
			String alleleAccession, Map<String, StatisticalResultBean> statisticalResults,
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
				tooltip.put("headerFormat", "<b>{series.name}</b><br>");
				tooltip.put("pointFormat", "{point.name}<br/>p-value: {point.pValue}");
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

					if (statisticalResults.containsKey(parameter.getStableId()) && statisticalResults.get(parameter.getStableId()).getIsSuccessful() ) {

						categories.put(parameter.getStableId());
						
						JSONObject dataPoint=new JSONObject();
						dataPoint.put("name", parameter.getName());
						dataPoint.put("stableId", parameter.getStableId());
						dataPoint.put("x", index);
						dataPoint.put("y", statisticalResults.get(parameter.getStableId()).getLogValue());
						dataPoint.put("pValue", statisticalResults.get(parameter.getStableId()).getpValue());
						dataArray.put(dataPoint);
						index++;
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


		String chartString=createChart(alleleAccession, series, categories);

		return chartString;
	}
}
