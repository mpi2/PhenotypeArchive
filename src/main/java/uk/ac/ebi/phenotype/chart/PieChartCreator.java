package uk.ac.ebi.phenotype.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;


public class PieChartCreator {
String pieChart="";




	public static String getPieChart(Map<String, Integer> labelToNumber, String chartId, String title){
		
		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaBox);
				
		String chart = "$(function () { $('#"+chartId+"').highcharts({ "
				 + " chart: { plotBackgroundColor: null, plotShadow: false}, "	
				 + " colors:"+colors+", "
				 + " title: {  text: '"+title+"' }, "
				 + " credits: { enabled: false }, "
				 + " tooltip: {  pointFormat: '{point.y}: <b>{point.percentage:.1f}%</b>'},"
				 + " plotOptions: { "
				 	+ "pie: { "
				 		+ "size: 200, "
				 		+ "allowPointSelect: true, "
				 		+ "cursor: 'pointer', "
				 		+ "dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', "
				 		+ "style: { color: '#666', width:'60px' }  }  },"
				 	+ "series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} }"
				 + " },"
			+ " series: [{  type: 'pie',   name: '',  "
				+ "data: [";
		for (Map.Entry<String, Integer> entry : labelToNumber.entrySet()){
					chart+="['"+entry.getKey()+"', " +entry.getValue()+ " ],";
			}
			chart+=	"]}]"
		+" }); });";
		
		return chart;
	}
	

}
