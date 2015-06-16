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

import java.util.Date;

import uk.ac.ebi.phenotype.pojo.SexType;

public class MouseDataPoint {
	private String mouseId;
	private Float dataPoint;
	private Integer column;//store the column index for this point which is mapped to the mouseId and starts at 0 for first column/mouse
	private Date dateOfExperiment;
	private SexType sexType;
	public SexType getSexType() {
		return sexType;
	}

	public void setSexType(SexType sexType) {
		this.sexType = sexType;
	}

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
			int mouseColumn, Date dateOfExperiment, SexType sexType) {
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
