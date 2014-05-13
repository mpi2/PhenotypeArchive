package uk.ac.ebi.phenotype.stats.graphs;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

public class HeatmapDTO {

	private String heatmapTitle;
	private ArrayList<String> yLabels;
	private ArrayList<String> xLabels;
	private ArrayList<ArrayList<Integer>> data;
	
	public String getHeatmapTitle() {
		return heatmapTitle;
	}
	public void setHeatmapTitle(String heatmapTitle) {
		this.heatmapTitle = heatmapTitle;
	}
	public ArrayList<String> getyLabels() {
		return yLabels;
	}
	public void setyLabels(ArrayList<String> yLabels) {
		this.yLabels = yLabels;
	}
	public ArrayList<String> getxLabels() {
		return xLabels;
	}
	public String getxLabelsToString(){
		return "['" + StringUtils.join(xLabels, "', '") + "']";
	}
	public String getyLabelsToString(){
		return "['" + StringUtils.join(yLabels, "', '") + "']";
	}
	public void setxLabels(ArrayList<String> xLabels) {
		this.xLabels = xLabels;
	}
	public ArrayList<ArrayList<Integer>> getData() {
		return data;
	}
	
	public void setData(ArrayList<ArrayList<Integer>> dataRows) {
		if (xLabels != null && yLabels != null){
			int size = 0;
			for (ArrayList<Integer> row : dataRows){
				size += row.size();
			}
			if (size != xLabels.size() * yLabels.size()){
				throw new Error("Size of data array does not mach");
			}
			else{
				data = new ArrayList<>();
				int y = 0;
				for (ArrayList<Integer> row : dataRows){
					int x = 0;
					for (Integer val : row){
						ArrayList<Integer> entry = new ArrayList<Integer>();
						entry.add(x);
						entry.add(y);
						entry.add(val);
						data.add(entry);
						x++;
					}
					y++;
				}
			}
		}
		else {
			throw new Error("You must set the labels arrays before the data.");
		}
	}
	
	
	
}
