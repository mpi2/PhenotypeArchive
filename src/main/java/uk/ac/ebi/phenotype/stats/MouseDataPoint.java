package uk.ac.ebi.phenotype.stats;

import java.util.Date;

public class MouseDataPoint {
	private String mouseId;
	private Float dataPoint;
	private Integer column;//store the column index for this point which is mapped to the mouseId and starts at 0 for first column/mouse
	private Date dateOfExperiment;
	public Date getDateOfExperiment() {
		return dateOfExperiment;
	}

	public void setDateOfExperiment(Date dateOfExperiment) {
		this.dateOfExperiment = dateOfExperiment;
	}

	public String getDateOfExperimentString() {
		return dateOfExperimentString;
	}

	public void setDateOfExperimentString(String dateOfExperimentString) {
		this.dateOfExperimentString = dateOfExperimentString;
	}

	private String dateOfExperimentString;

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public MouseDataPoint(String mouseId, Float dataPoint){
		this.mouseId=mouseId;
		this.dataPoint=dataPoint;
	}

	public MouseDataPoint(String externalSampleId, Float dataPoint2,
			int mouseColumn) {
		this(externalSampleId, dataPoint2);
		this.column=mouseColumn;
	}
	
	public MouseDataPoint(String externalSampleId, Float dataPoint2,
			int mouseColumn, Date dateOfExperiment) {
		this(externalSampleId, dataPoint2, mouseColumn);
		this.dateOfExperiment=dateOfExperiment;
	}
	 

	public String toString(){
		String text= "MouseDataPoint is MouseId="+mouseId+" dataPoint= "+dataPoint;
				if(column!=null) {
					text+=" column="+column;
				}
		return text;
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
