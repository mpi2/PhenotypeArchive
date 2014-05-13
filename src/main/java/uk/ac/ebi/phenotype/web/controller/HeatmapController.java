package uk.ac.ebi.phenotype.web.controller;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.stats.graphs.HeatmapDTO;



@Controller
public class HeatmapController {
	
	@RequestMapping("/heatmap")
	public String getHeatmapJS(Model model,
			HttpServletRequest request,
			RedirectAttributes attributes){
		System.out.println("getHeatmapJS");
			
		HeatmapDTO hdto = new HeatmapDTO();
		hdto.setxLabels(new ArrayList<String>(Arrays.asList("Alexander", "Marie", "Maximilian", "Sophia")));
		hdto.setyLabels(new ArrayList<String>(Arrays.asList("Monday", "Tuesday")));
		ArrayList<ArrayList<Integer>> data = new ArrayList<>();
		data.add(new ArrayList<Integer>(Arrays.asList(10, 92,35,72)));
		data.add(new ArrayList<Integer>(Arrays.asList(19,58,15,132)));
		hdto.setData(data);
		
		model.addAttribute("heatmapCode", fillHeatmap(hdto));
		return "pageWithHeatmap";
	}

	private String fillHeatmap(HeatmapDTO hDto){
		
			
		String code = "$(function () { "+
	 "   $('#heatmapContainer1').highcharts({"+
	 "       "+
	 "       chart: {"+
	 "           type: 'heatmap',"+
	 "           marginTop: 40,"+
	 "           marginBottom: 40"+
	 "       },"+
	 "       title: {"+
	 "           text: 'Sales per employee per weekday'"+
	 "       },"+
	 "       xAxis: {"+
	 "           categories: " + hDto.getxLabelsToString() +
	 "       },"+
	 "       yAxis: {"+
	 "           categories: " + hDto.getyLabelsToString() + ","+
	 "         	 title: null"+
	 "       },"+
	 "       colorAxis: {"+
	 "           min: 0,"+
	 "           minColor: '#FFFFFF',"+
	 "           maxColor: Highcharts.getOptions().colors[0]"+
	 "       },"+
	 "       legend: {"+
	 "           align: 'right',"+
	 "           layout: 'vertical',"+
	 "           margin: 0,"+
	 "           verticalAlign: 'top',"+
	 "           y: 25,"+
	 "           symbolHeight: 320"+
	 "       },"+
	 "       tooltip: {"+
	 "           formatter: function () {"+
	 "               return '<b>' + this.series.xAxis.categories[this.point.x] + '</b> sold <br><b>' +"+
	 "                   this.point.value + '</b> items on <br><b>' + this.series.yAxis.categories[this.point.y] + '</b>';"+
	 "           }"+
	 "       },"+
	 "       series: [{"+
	 "           name: 'Sales per employee',"+
	 "           borderWidth: 1,"+
	 "           data: " + hDto.getData().toString() + "' " + // [[0,0,10],[0,1,19],[0,2,8],[0,3,24],[0,4,67],[1,0,92],[1,1,58],[1,2,78],[1,3,117],[1,4,48],[2,0,35],[2,1,15],[2,2,123],[2,3,64],[2,4,52],[3,0,72],[3,1,132],[3,2,114],[3,3,19],[3,4,16],[4,0,38],[4,1,5],[4,2,8],[4,3,117],[4,4,115],[5,0,88],[5,1,32],[5,2,12],[5,3,6],[5,4,120],[6,0,13],[6,1,44],[6,2,88],[6,3,98],[6,4,96],[7,0,31],[7,1,1],[7,2,82],[7,3,32],[7,4,30],[8,0,85],[8,1,97],[8,2,123],[8,3,64],[8,4,84],[9,0,47],[9,1,114],[9,2,31],[9,3,48],[9,4,91]],"+
	 "           dataLabels: {"+
	 "               enabled: true,"+
	 "               color: 'black',"+
	 "               style: {"+
	 "                   textShadow: 'none'"+
	 "               }"+
	 "           }"+
	 "       }]"+
	 "   });"+
	 "});";
			return code;
	}
}
