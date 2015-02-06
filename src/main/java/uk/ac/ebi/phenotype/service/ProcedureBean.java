package uk.ac.ebi.phenotype.service;

import org.apache.solr.client.solrj.response.FacetField.Count;

public class ProcedureBean {

	String stableId;
	
	
	public ProcedureBean(String name, String stableId) {
		// TODO Auto-generated constructor stub
	}
	public String getStableId() {
		return stableId;
	}
	public void setStableId(String stableId) {
		this.stableId = stableId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	String name;
}
