package uk.ac.ebi.phenotype.stats;

public class MouseDataPoint {
	private String mouseId;
	private Float dataPoint;

	public MouseDataPoint(String mouseId, Float dataPoint){
		this.mouseId=mouseId;
		this.dataPoint=dataPoint;
	}

	public String toString(){
		return "MouseDataPoint is MouseId="+mouseId+" dataPoint= "+dataPoint;
	}

	public String getMouseId() {
		return mouseId;
	}

	public void setMouseId(String mouseId) {
		this.mouseId = mouseId;
	}

	public Float getDataPoint() {
		return dataPoint;
	}

	public void setDataPoint(Float dataPoint) {
		this.dataPoint = dataPoint;
	}
}
