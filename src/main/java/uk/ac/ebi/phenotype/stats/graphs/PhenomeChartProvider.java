package uk.ac.ebi.phenotype.stats.graphs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ebi.phenotype.bean.StatisticalResultBean;



public class PhenomeChartProvider {

	private static final Logger logger = Logger
			.getLogger(PhenomeChartProvider.class);


	public String createChart(String alleleAccession, JSONArray series) {

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
			    +"        type: 'category',"
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
			    +"           } "
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

	public String generatePhenomeChart(String alleleAccession, Map<String, StatisticalResultBean> statisticalResults) throws IOException,
	URISyntaxException {

		JSONArray series=new JSONArray();

		JSONObject controlJsonObject=new JSONObject();

		try {

			// Tooltip first for correct formatting
/*	        tooltip: {
		     
		     headerFormat: '<b>{series.name}</b><br>',
		     pointFormat: '{point.name}<br/>value: {point.y}'
		                    },*/
			
			JSONObject tooltip=new JSONObject();
			tooltip.put("headerFormat", "<b>{series.name}</b><br>");
			tooltip.put("pointFormat", "{point.name}<br/>value: {point.y}");
			controlJsonObject.put("tooltip", tooltip);
			controlJsonObject.put("name", WordUtils.capitalize("parameters p-values"));

			JSONArray dataArray=new JSONArray();
			int index = 1;
			
/*			 data: [{
			        name: 'IMPC_...',
			        x: 1,
			        y: 2
			    }, {
			        name: 'IMPC_...',
			        x: 2,
			        y: 5
			    }]		*/	
			
			for (String parameterId: statisticalResults.keySet()) {
				//JSONArray parameterAndValue = new JSONArray();
				// [2,0.18801234239446038]
				//parameterAndValue.put(index);
				//parameterAndValue.put(statisticalResults.get(parameterId).getLogValue());
				
				//dataArray.put(parameterAndValue);
				
				JSONObject dataPoint=new JSONObject();
				dataPoint.put("name", parameterId);
				dataPoint.put("x", index);
				dataPoint.put("y", statisticalResults.get(parameterId).getLogValue());
				dataArray.put(dataPoint);
				index++;
			}
			controlJsonObject.put("data", dataArray);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		series.put(controlJsonObject);

		String chartString=createChart(alleleAccession, series);

		return chartString;
	}
}
