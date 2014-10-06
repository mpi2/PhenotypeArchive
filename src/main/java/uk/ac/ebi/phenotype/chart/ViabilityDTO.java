package uk.ac.ebi.phenotype.chart;

import java.util.List;

import org.json.JSONArray;


public class ViabilityDTO {

	public String getPieChart(){
		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaBox);
		JSONArray colorArray = new JSONArray(colors);	
		int femaleOnly=10;
		int maleOnly=20;
		int both=40;
		int total=100;
		
		String chart = "$(function () { $('#viabilityChart').highcharts({ "
				 + " chart: { plotBackgroundColor: null, plotShadow: false}, "	
				 + " colors:"+colorArray+", "
				 + " title: {  text: '' }, "
				 + " credits: { enabled: false }, "
				 + " tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'},"
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
				+ "data: [ { name: 'Female only', y: " + femaleOnly + ", sliced: true, selected: true }, "
					+ "{ name: 'Male only', y: " + maleOnly + ", sliced: true, selected: true }, "
					+ "{ name: 'Both sexes', y: " + both + ", sliced: true, selected: true }, "
					+ "['Phenotype not present', " + (total- maleOnly - femaleOnly - both) + " ] ]  }]"
		+" }); });";
		
		return chart;
	}
}
