package uk.ac.ebi.phenotype.chart;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ebi.phenotype.analytics.bean.AggregateCountXYBean;
import uk.ac.ebi.phenotype.bean.StatisticalResultBean;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;



public class AnalyticsChartProvider {

	static Map<String, String> trendsSeriesTypes = null;
	static Map<String, String> trendsSeriesNames = null;
	static Map<String, String> trendsSeriesUnits = null;
	
	static {
		
		trendsSeriesTypes = new HashMap<String, String>();
		trendsSeriesTypes.put("phenotyped_genes", "column");
		trendsSeriesTypes.put("phenotyped_lines", "column");
		trendsSeriesTypes.put("statistically_significant_calls", "spline");

		trendsSeriesTypes.put("unidimensional_datapoints_QC_passed", "spline");
		trendsSeriesTypes.put("unidimensional_datapoints_QC_failed", "spline");
		trendsSeriesTypes.put("unidimensional_datapoints_issues", "spline");
		trendsSeriesTypes.put("time_series_datapoints_QC_passed", "spline");
		trendsSeriesTypes.put("time_series_datapoints_QC_failed", "spline");
		trendsSeriesTypes.put("time_series_datapoints_issues", "spline");
		trendsSeriesTypes.put("text_datapoints_QC_passed", "spline");
		trendsSeriesTypes.put("text_datapoints_QC_failed", "spline");
		trendsSeriesTypes.put("text_datapoints_issues", "spline");
		trendsSeriesTypes.put("categorical_datapoints_QC_passed", "spline");
		trendsSeriesTypes.put("categorical_datapoints_QC_failed", "spline");
		trendsSeriesTypes.put("categorical_datapoints_issues", "spline");
		trendsSeriesTypes.put("image_record_datapoints_QC_passed", "spline");
		trendsSeriesTypes.put("image_record_datapoints_QC_failed", "spline");
		trendsSeriesTypes.put("image_record_datapoints_issues", "spline");
		
		trendsSeriesNames = new HashMap<String, String>();
		trendsSeriesNames.put("phenotyped_genes", "Phenotyped genes");
		trendsSeriesNames.put("phenotyped_lines", "Phenotyped lines");
		trendsSeriesNames.put("statistically_significant_calls", "MP calls");
		
		trendsSeriesNames.put("unidimensional_datapoints_QC_passed", "Unidimensional (QC passed)");
		trendsSeriesNames.put("unidimensional_datapoints_QC_failed", "Unidimensional (QC failed)");
		trendsSeriesNames.put("unidimensional_datapoints_issues", "Unidimensional (issues)");
		trendsSeriesNames.put("time_series_datapoints_QC_passed", "Time series (QC passed)");
		trendsSeriesNames.put("time_series_datapoints_QC_failed", "Time series (QC failed)");
		trendsSeriesNames.put("time_series_datapoints_issues", "Time series (issues)");
		trendsSeriesNames.put("text_datapoints_QC_passed", "Text (QC passed)");
		trendsSeriesNames.put("text_datapoints_QC_failed", "Text (QC failed)");
		trendsSeriesNames.put("text_datapoints_issues", "Text (issues)");
		trendsSeriesNames.put("categorical_datapoints_QC_passed", "Categorical (QC passed)");
		trendsSeriesNames.put("categorical_datapoints_QC_failed", "Categorical (QC failed)");
		trendsSeriesNames.put("categorical_datapoints_issues", "Categorical (issues)");
		trendsSeriesNames.put("image_record_datapoints_QC_passed", "Image record (QC passed)");
		trendsSeriesNames.put("image_record_datapoints_QC_failed", "Image record (QC failed)");
		trendsSeriesNames.put("image_record_datapoints_issues", "Image record (issues)");
	
		trendsSeriesUnits = new HashMap<String, String>();
		trendsSeriesUnits.put("phenotyped_genes", "genes");
		trendsSeriesUnits.put("phenotyped_lines", "lines");
		trendsSeriesUnits.put("statistically_significant_calls", "calls");
	};
	
	private String createHistoryTrendChart(
			JSONArray series, 
			JSONArray categories, 
			String title, 
			String subTitle, 
			String yAxis1Legend,
			String yAxis2Legend,
			boolean yAxisCombined, String containerId) {
		
		String chartString=
				
				"$(function () {\n"+
			    "    $('#"+ containerId +"').highcharts({\n"+
			    "        chart: {\n"+
				"                zoomType: 'xy'\n"+
				"            },\n"+
				"            title: {\n"+
				"                text: '"+title+"'\n"+
				"            },\n"+
				"            subtitle: {\n"+
				"                text: '"+subTitle+"'\n"+
				"            },\n"+
				"            xAxis: [{\n"+
				"                categories: "+ categories.toString() +",\n"+
				"                   }],\n"+
            				"            yAxis: [{ // Primary yAxis\n"+
            				"                labels: {\n"+
            				"                    format: '{value}',\n"+
            				"                    style: {\n"+
            				"                        color: Highcharts.getOptions().colors[1]\n"+
            				"                    }\n"+
            				"                },\n"+
            				"                title: {\n"+
            				"                    text: '"+yAxis1Legend+"',\n"+
            				"                    style: {\n"+
            				"                        color: Highcharts.getOptions().colors[1]\n"+
            				"                    }\n"+
            				"                }\n"+
            				"            }, \n"+
            				((!yAxisCombined) ? "" : 
            				"            { // Secondary yAxis\n"+
            				"                title: {\n"+
            				"                    text: '"+yAxis2Legend+"',\n"+
            				"                    style: {\n"+
            				"                        color: Highcharts.getOptions().colors[0]\n"+
            				"                    }\n"+
            				"                },\n"+
            				"                labels: {\n"+
            				"                    format: '{value}',\n"+
            				"                    style: {\n"+
            				"                        color: Highcharts.getOptions().colors[0]\n"+
            				"                    }\n"+
            				"                },\n"+
            				"                opposite: true\n"+
            				"            }\n") +
            				"            ],\n"+
            			    "      credits: { \n"+
            			    "         enabled: false \n"+
            			    "      }, \n"+            				
            				"            tooltip: {\n"+
            				"                shared: true\n"+
            				"            },\n"+
 /*           				"            legend: {\n"+
            				"                layout: 'vertical',\n"+
            				"                align: 'left',\n"+
            				"                x: 120,\n"+
            				"                verticalAlign: 'top',\n"+
            				"                y: 100,\n"+
            				"                floating: true,\n"+
            				"                backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'\n"+
            				"            },\n"+*/
            				"            series:" + series.toString() +"\n"+
            				"        });\n"+
            				"    });\n";			    
		return chartString;
	}
	
	
	public String createLineProceduresOverviewChart(JSONArray series, JSONArray categories, String title, String subTitle, String yAxisLegend, String yAxisUnit, String containerId, Boolean stacked) {

		String chartString= 			
			"$(function () {\n"+
			"	Highcharts.setOptions({"+
			"	    colors: " + ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaBox) + "});" +
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
		    "                '<td style=\"padding:0\"><b>{point.y:.0f} "+yAxisUnit+"</b></td></tr>',\n"+
		    "            footerFormat: '</table>',\n"+
		    "            shared: true,\n"+
		    "            useHTML: true\n"+
		    "        },\n"+
		    "        plotOptions: {\n"+
		    "            column: {\n" + ((stacked) ? "            	 stacking: 'normal',\n" : "")+
		    "                pointPadding: 0.2,\n"+
		    "                borderWidth: 0\n"+
		    "            }\n"+
		    "        },\n"+
		    "        series:" + series.toString() +"\n"+
		    "    });\n"+
		    "});\n";
		
		return chartString;
		
	}
		
	public String generateSexualDimorphismChart(HashMap<SignificantType, Integer>sexualDimorphismSummary, String title, String containerId) {
		
		Integer totalWithDimorphism = sexualDimorphismSummary.get(SignificantType.different_directions) +
		 	sexualDimorphismSummary.get(SignificantType.female_greater) +
		 	sexualDimorphismSummary.get(SignificantType.male_greater) +
		 	sexualDimorphismSummary.get(SignificantType.female_only) +
		 	sexualDimorphismSummary.get(SignificantType.male_only);
		Integer total = totalWithDimorphism + sexualDimorphismSummary.get(SignificantType.both_equally);
		
		String chart = " $(function () {" +			
			"		    var colors = Highcharts.getOptions().colors," +
			"		        categories = ['Equal accross sexes', 'Sexual Dimorphism'],"+ 
			"		        data = [{\n" +
			"		            y: " + sexualDimorphismSummary.get(SignificantType.both_equally) + ",\n" +
			"		            color: colors[0],\n" +
			"		            drilldown: {\n" +
			"		                name: 'Phenotype is significant for both sexes equally',\n" +
			"		                categories: ['Equal accross sexes'],\n" +
			"		                data: [" + sexualDimorphismSummary.get(SignificantType.both_equally) + "],\n" +
			"		                color: colors[1]\n" +
			"		            }\n" +
			"		        }, {\n" +
			"		            y: " + totalWithDimorphism + ",\n" +
			"		            color: colors[1],\n" +
			"		            drilldown: {\n" +
			"		                name: 'Different',\n" +
			"		                categories: ['"+SignificantType.female_greater + "', '" + SignificantType.male_greater + "', '" + SignificantType.female_only 
													+"', '" + SignificantType.male_only + "', '" + SignificantType.different_directions + "'],\n" +
			"		                data: [" +
										sexualDimorphismSummary.get(SignificantType.female_greater) + ", " + 
										sexualDimorphismSummary.get(SignificantType.male_greater)+ ", " + 
										sexualDimorphismSummary.get(SignificantType.female_only)+ ", " + 
										sexualDimorphismSummary.get(SignificantType.male_only) + ", " +
										sexualDimorphismSummary.get(SignificantType.different_directions) + "],\n" +
			"		                color: colors[1]\n" +
			"		            }\n" +
			"		        }\n" +
			"		        ],\n" +
			"		        browserData = [],\n" +
			"		        versionsData = [],\n" +
			"		        i,\n" +
			"		        j,\n" +
			"		        dataLen = data.length,\n" +
			"		        drillDataLen,\n" +
			"		        brightness;\n" +
			"		    for (i = 0; i < dataLen; i += 1) {\n" +
			"		        browserData.push({\n" +
			"		            name: categories[i],\n" +
			"		            y: data[i].y,\n" +
			"		            color: data[i].color\n" +
			"		        });\n" +
			"		        drillDataLen = data[i].drilldown.data.length;\n" +
			"		        for (j = 0; j < drillDataLen; j += 1) {\n" +
			"		            brightness = 0.2 - (j / drillDataLen) / 5;\n" +
			"		            versionsData.push({\n" +
			"		                name: data[i].drilldown.categories[j],\n" +
			"		                y: data[i].drilldown.data[j],\n" +
			"		                color: Highcharts.Color(data[i].color).brighten(brightness).get()\n" +
			"		            });\n" +
			"		        }\n" +
			"		    }\n" +
			"		    $('#" + containerId + "').highcharts({\n" +
			"				credits: {\n" +
		    "			         enabled: false\n" +
		    "      			},\n"+		    
			"		        chart: {\n" +
			"		            type: 'pie'\n" +
			"		        },\n" +
			"		        title: {\n" +
			"		            text: '" + title + "'\n" +
			"		        },\n" +
			"		        yAxis: {\n" +
			"		            title: {\n" +
			"		                text: 'Total percent market share'\n" +
			"		            }\n" +
			"		        },\n" +
			"		        plotOptions: {\n" +
			"		            pie: {\n" +
			"		                shadow: false,\n" +
			"		                center: ['50%', '50%']\n" +
			"		            }\n" +
			"		        },\n" +
			"		        tooltip: {\n" +
			"		            formatter: function () {\n" +
			"							var div = this.y*100/" + total +";\n" +
			"							var percent = div.toFixed(2);\n	" +	
			"		                    return 'Phenotype Calls<br/> ' + this.point.name + ':<b> ' + percent + '%</b> (' + this.y + ')' ;\n" +
			"		                },\n" +
			"		        },\n" +
			"		        series: [{\n" +
			"		            name: 'Phenotype Calls',\n" +
			"		            data: browserData,\n" +
			"		            size: '60%',\n" +
			"		            dataLabels: {\n" +
			"		                formatter: function () {\n" +
			"							var div = this.y*100/" + total +";\n" +
			"							var percent = div.toFixed(2);\n	" +	
			"		                    return this.point.name + ':<br/> ' + percent + '% (' + this.y + ')' ;\n" +
			"		                },\n" +
			"		                color: 'white',\n" +
			"		                distance: -30\n" +
			"		            }\n" +
			"		        }, {\n" +
			"		            name: 'Versions',\n" +
			"		            data: versionsData,\n" +
			"		            size: '80%',\n" +
			"		            innerSize: '60%',\n" +
			"		            dataLabels: {\n" +
			"		                formatter: function () {\n" +
			"							var div = this.y*100/" + total +";\n" +
			"							var percent = div.toFixed(2);\n	" +	
			"		                    return (this.point.name != 'Equal accross sexes') ? '<b>' + this.point.name + ':</b> ' + percent + '% (' + this.y + ')'  : null;\n" +
			"		                }\n" +
			"		            }\n" +
			"		        }]\n" +
			"		    });\n" +
			"		});";
				
				
		return chart;
	}
	
	public String getSlicedPieChart(Map<String, Integer> slicedOut, Map<String, Integer> notSliced, String title, String containerId){
			
			List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaBox);
			
			JSONArray data = new JSONArray();
			try {
				for ( Entry<String, Integer> entry : slicedOut.entrySet()){
					JSONObject obj = new JSONObject();
					obj.put("name", entry.getKey());
					obj.put("y", entry.getValue());
					obj.put("sliced", true);
					obj.put("selected", true);
					data.put(obj);
				}
				for ( Entry<String, Integer> entry : notSliced.entrySet()){
					JSONObject obj = new JSONObject();
					obj.put("name", entry.getKey());
					obj.put("y", entry.getValue());
					data.put(obj);
				}
				
				String chart = "$(function () { $('#" + containerId + "').highcharts({ "
						 + " chart: { plotBackgroundColor: null, plotShadow: false}, "	
						 + " colors:" + colors + ", "
						 + " title: {  text: '" + title + "' }, "
						 + " credits: { enabled: false }, "
						 + " tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'},"
						 + " plotOptions: { "
						 	+ "pie: { "
						 		+ "size: 200, "
						 		+"showInLegend: true, "
						 		+ "allowPointSelect: true, "
						 		+ "cursor: 'pointer', "
						 		+ "dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', "
						 		+ "style: { color: '#666', width:'60px' }  }  },"
						 	+ "series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} }"
						 + " },"
					+ " series: [{  type: 'pie',   name: '',  "
						+ "data: " + data + "  }]"
				+" }); });";
				
				return chart;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
	}
	
	public String generateAggregateCountByProcedureChart(
			String dataReleaseVersion,
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
			e.printStackTrace();
		}

		String chartString= this.createLineProceduresOverviewChart(series, categories, title, subTitle, yAxisLegend, yAxisUnit, containerId, true);

		return chartString;
	}
	
	/**
	 * Generate a graph with trends information...
	 * @param historyMap
	 * @return
	 */
	public String generateHistoryTrendsChart(
			Map<String, List<AggregateCountXYBean>> trendsMap,
			List<String> releases,
			String title,
			String subtitle,
			String yAxis1Legend,
			String yAxis2Legend,
			boolean yAxisCombined,
			String containerId) {

		JSONArray series = new JSONArray();
		JSONArray categories = new JSONArray();
		try {

			// generate categories (release by release asc)

			for (String release: releases) {
				categories.put(release);
			}

			int count = 0;
			List<String> keys = new ArrayList<String>(trendsMap.keySet());
			Collections.sort(keys);

			// We use all keys provided


			for (String trendProperty : keys) {

				// new series
				List<AggregateCountXYBean> beans = trendsMap.get(trendProperty);

				JSONObject containerJsonObject=new JSONObject();
				JSONArray dataArray=new JSONArray();
				// this is not performant but we need to plot the missing values
				for (String release: releases) {
					boolean found = false;
					for (AggregateCountXYBean bean: beans) {
						if (bean.getyValue().equals(release)) {
							dataArray.put(bean.getAggregateCount());
							found = true;
							break;
						} 
					}
					if (!found){
						dataArray.put(0); // default vaule
					}
				}
				containerJsonObject.put("data", dataArray);
				System.out.println(trendProperty + " " +  trendsSeriesTypes.get(trendProperty));
				containerJsonObject.put("type", trendsSeriesTypes.get(trendProperty));
				String name = (trendsSeriesNames.containsKey(trendProperty)) ? trendsSeriesNames.get(trendProperty) : trendProperty;
				containerJsonObject.put("name", name);
				
				
				if (trendProperty.equals("statistically_significant_calls")) {
					containerJsonObject.put("yAxis", 1);
				}

				JSONObject tooltip = new JSONObject();
				tooltip.put("pointFormat", "<span style=\"color:{series.color}\">\u25CF</span> {series.name}: <b>{point.y}</b><br/>");
				tooltip.put("valueSuffix", " " + ((trendsSeriesUnits.containsKey(trendProperty)) ? trendsSeriesUnits.get(trendProperty) : ""));
				containerJsonObject.put("tooltip", tooltip);

				series.put(containerJsonObject);

				count++;
				// this is hardcoded for the moment
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String chartString= createHistoryTrendChart(series, categories, title, subtitle, yAxis1Legend, yAxis2Legend, yAxisCombined, containerId);
		return chartString;

	}
	
}
