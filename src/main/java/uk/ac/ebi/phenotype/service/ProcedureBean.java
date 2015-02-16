package uk.ac.ebi.phenotype.service;

import org.apache.solr.client.solrj.response.FacetField.Count;

public class ProcedureBean {

	String stableId;

	String name;
	
	public ProcedureBean(String name, String stableId) {
		setName(name);
		setStableId(stableId);
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
	
	@Override
	public String toString() {

		return "ProcedureBean [stableId=" + stableId + ", name=" + name + "]";
	}
	
	
}
