package uk.ac.ebi.phenotype.stats.graphs;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

public class HeatmapDTO {

	private String heatmapTitle;
	private ArrayList<String> yLabels;
	private ArrayList<String> xLabels;
	private float[][][] data;
	
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
	public float[][][] getData() {
		return data;
	}
	public void setData(float[][][] data) {
		if (xLabels != null && yLabels != null){
			if (data.length != xLabels.size() * yLabels.size()){
				throw new Error("Size of data array does not mach");
			}
			else{
				this.data = data;
			}
		}
		else {
			throw new Error("You must set the labels arrays before the data.");
		}
	}
	
	
	
}
