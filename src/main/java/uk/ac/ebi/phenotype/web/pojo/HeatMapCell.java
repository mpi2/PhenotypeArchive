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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.web.pojo;

import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author jwarren
 */
public class HeatMapCell {
    

	public static final String THREE_I_COULD_NOT_ANALYSE = "Could not analyse";
	public static final String THREE_I_NO_DATA = "No data";
	public static final String THREE_I_DATA_ANALYSED_NOT_SIGNIFICANT = "Data analysed, no significant call";
	public static final String THREE_I_DEVIANCE_SIGNIFICANT = "Deviance Significant";
		
    private Float floatValue;
    private String xAxisKey;    
    private String label="";//label to display possibly in the cell
    private String mouseOver="";
    private String status="";//use to give a status of a cell e.g. In progress or complete etc
   

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMouseOver() {
		return mouseOver;
	}

	public void setMouseOver(String mouseOver) {
		this.mouseOver = mouseOver;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getxAxisKey() {
		return xAxisKey;
	}

	public void setxAxisKey(String xAxisKey) {
		this.xAxisKey = xAxisKey;
	}

    public Float getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(Float floatValue) {
		this.floatValue = floatValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "HeatMapCell [floatValue=" + floatValue + ", xAxisKey=" + xAxisKey + ", label=" + label + ", mouseOver=" + mouseOver + ", status=" + status + "]";
	}    
   
}
