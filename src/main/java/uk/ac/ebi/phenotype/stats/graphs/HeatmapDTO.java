package uk.ac.ebi.phenotype.stats.graphs;

public class HeatmapDTO {

	private String heatmapTitle;
	private String[] yLabels;
	private String[] xLabels;
	private float[][][] data;
	
	public String getHeatmapTitle() {
		return heatmapTitle;
	}
	public void setHeatmapTitle(String heatmapTitle) {
		this.heatmapTitle = heatmapTitle;
	}
	public String[] getyLabels() {
		return yLabels;
	}
	public void setyLabels(String[] yLabels) {
		this.yLabels = yLabels;
	}
	public String[] getxLabels() {
		return xLabels;
	}
	public void setxLabels(String[] xLabels) {
		this.xLabels = xLabels;
	}
	public float[][][] getData() {
		return data;
	}
	public void setData(float[][][] data) {
		if (xLabels != null && yLabels != null){
			if (data.length != xLabels.length * yLabels.length){
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
