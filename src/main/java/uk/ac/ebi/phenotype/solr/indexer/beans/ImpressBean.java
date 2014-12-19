package uk.ac.ebi.phenotype.solr.indexer.beans;

/**
 * Class to act as Map value DTO for impress data
 */
public class ImpressBean {
	public Integer id;
	public String stableKey;
	public String stableId;
	public String name;


	public Integer getId() {

		return id;
	}


	public void setId(Integer id) {

		this.id = id;
	}


	public String getStableKey() {

		return stableKey;
	}


	public void setStableKey(String stableKey) {

		this.stableKey = stableKey;
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
}
