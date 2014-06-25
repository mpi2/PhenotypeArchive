package uk.ac.ebi.phenotype.stats.graphs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ebi.phenotype.analytics.bean.AggregateCountXYBean;
import uk.ac.ebi.phenotype.bean.StatisticalResultBean;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;



public class AnalyticsChartProvider {

	public String createLineProceduresOverviewChart(JSONArray series, JSONArray categories, String title, String subTitle, String yAxisLegend, String yAxisUnit, String containerId) {

		String chartString=
			
			"$(function () {\n"+
		    "    $('#"+ containerId +"').highcharts({\n"+
		    "        chart: {\n"+
		    "            type: 'column',\n"+
		    "            height: 800\n"+
		    "        },\n"+
		    "        title: {\n"+
		    "            text: '"+title+"'\n"+
		    "       },\n"+
		    "        subtitle: {\n"+
		    "            text: \""+subTitle+"\"\n"+
		    "       },\n"+
		    "        xAxis: {\n"+
		    "            categories: "+ categories.toString() +",\n"+
			"            labels: { \n"+
			"               rotation: -90, \n"+
			"               align: 'right', \n"+
			"               style: { \n"+
			"                  fontSize: '11px', \n"+
			"                  fontFamily: 'Verdana, sans-serif' \n"+
			"               } \n"+
			"            }, \n"+
			"            showLastLabel: true \n"+ 
			"        },\n"+
		    "        yAxis: {\n"+
		    "            min: 0,\n"+
		    "            title: {\n"+
		    "                text: '"+ yAxisLegend +"'\n"+
		    "            }\n"+
		    "        },\n"+
		    "      credits: {\n"+
		    "         enabled: false\n"+
		    "      },\n"+		    
		    "        tooltip: {\n"+
		    "            headerFormat: '<span style=\"font-size:10px\">{point.key}</span><table>',\n"+
		    "            pointFormat: '<tr><td style=\"color:{series.color};padding:0\">{series.name}: </td>' +\n"+
		    "                '<td style=\"padding:0\"><b>{point.y:.1f} "+yAxisUnit+"</b></td></tr>',\n"+
		    "            footerFormat: '</table>',\n"+
		    "            shared: true,\n"+
		    "            useHTML: true\n"+
		    "        },\n"+
		    "        plotOptions: {\n"+
		    "            column: {\n"+
		    "                pointPadding: 0.2,\n"+
		    "                borderWidth: 0\n"+
		    "            }\n"+
		    "        },\n"+
		    "        series:" + series.toString() +"\n"+
		    "    });\n"+
		    "});\n";
		
		return chartString;
		
	}
		    
	public String generateAggregateCountByProcedureChart(
			String dataReleaseversion,
			List<AggregateCountXYBean> data,
			String title,
			String subTitle,
			String yAxisLegend,
			String yAxisUnit,
			String containerId
			) throws IOException,
			URISyntaxException {

		JSONArray series=new JSONArray();

		JSONArray categories = new JSONArray();

		List<String> categoriesList = new ArrayList<String>();
		Map<String, List<AggregateCountXYBean>> centerMap = new HashMap<String, List<AggregateCountXYBean>>();

		try {

			// List categories first
			// List centers
			for (AggregateCountXYBean bean: data) {
				if (!categoriesList.contains(bean.getxValue())) {
					categoriesList.add(bean.getxValue());
					categories.put(bean.getxValue());
				}
				List<AggregateCountXYBean> beans = null;
				if (!centerMap.containsKey(bean.getyValue())) {
					beans = new ArrayList<AggregateCountXYBean>();
					centerMap.put(bean.getyValue(), beans);
				} else {
					beans = centerMap.get(bean.getyValue());
				}
				beans.add(bean);

			}

			// build by center specific list
			for (String center: centerMap.keySet()) {

				List<AggregateCountXYBean> beans = centerMap.get(center);

				JSONObject containerJsonObject=new JSONObject();
				JSONArray dataArray=new JSONArray();

				// so always the same order for categories
				for (String procedure: categoriesList) {

					int countLines = 0;
					// Retrieve procedure (not the fastest way)
					for (AggregateCountXYBean bean: beans) {
						if (bean.getxValue().equals(procedure)) {
							countLines = bean.getAggregateCount();
						}
					}
					dataArray.put(countLines);

				}

				containerJsonObject.put("data", dataArray);
				containerJsonObject.put("name", center);
				series.put(containerJsonObject);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String chartString= this.createLineProceduresOverviewChart(series, categories, title, subTitle, yAxisLegend, yAxisUnit, containerId);

		return chartString;
	}    
}
