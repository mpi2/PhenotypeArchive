package uk.ac.ebi.phenotype.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;


public class ViabilityDTO {

	public String getPieChart(){
Map<String, Integer> labelToNumber=new HashMap<String, Integer>();
		
		int femaleOnly=10;
		int maleOnly=20;
		int both=40;
		int total=100;
		labelToNumber.put("Female only",10);
		labelToNumber.put("Male only", maleOnly);
		labelToNumber.put("Both sexes", both);
		String chart=PieChartCreator.getPieChart(labelToNumber);
		return chart;
	}
}
