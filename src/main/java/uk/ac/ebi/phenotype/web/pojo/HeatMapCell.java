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

	
    
    
    
   
}
