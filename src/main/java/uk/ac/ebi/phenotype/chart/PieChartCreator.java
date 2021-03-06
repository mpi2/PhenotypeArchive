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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.ebi.phenotype.pojo.ZygosityType;


public class PieChartCreator {
String pieChart="";




	public static String getPieChart(Map<String, Integer> labelToNumber, String chartId, String title, Map<String, String> map){
		List<String> colors=new ArrayList<>();;
		if(map==null){//if no colormap then use highdifference colors as default
		colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaOpaque);
		}else{
			for(Entry<String, Integer> entry: labelToNumber.entrySet()){
				if(entry.getKey().contains("WT")){
					colors.add(map.get("WT"));
				}
				if(entry.getKey().contains("Homozygous")){
					colors.add(map.get(ZygosityType.homozygote.name()));
				}
				if(entry.getKey().contains("Heterozygous")){
					colors.add(map.get(ZygosityType.heterozygote.name()));
				}
				if(entry.getKey().contains("Hemizygous")){
					colors.add(map.get(ZygosityType.hemizygote.name()));
				}
			}
		}
		
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
				 		+ "dataLabels: { distance: 1, enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', "
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
