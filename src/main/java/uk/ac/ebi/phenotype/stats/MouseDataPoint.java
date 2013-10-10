package uk.ac.ebi.phenotype.stats;

public class MouseDataPoint {
	private String mouseId;
	private Float dataPoint;
	private Float column;//store the column index for this point which is mapped to the mouseId and starts at 0 for first column/mouse

	public Float getColumn() {
		return column;
	}

	public void setColumn(Float column) {
		this.column = column;
	}

	public MouseDataPoint(String mouseId, Float dataPoint){
		this.mouseId=mouseId;
		this.dataPoint=dataPoint;
	}

	public MouseDataPoint(String externalSampleId, Float dataPoint2,
			Float mouseColumn) {
		this(externalSampleId, dataPoint2);
		this.column=mouseColumn;
	}

	public String toString(){
		String text= "MouseDataPoint is MouseId="+mouseId+" dataPoint= "+dataPoint;
				if(column!=null) {
					text+=" column="+column;
				}
		return "MouseDataPoint is MouseId="+mouseId+" dataPoint= "+dataPoint+" column="+column;
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
