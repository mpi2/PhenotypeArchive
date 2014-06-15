package uk.ac.ebi.phenotype.web.pojo;

/**
 * class to store basic generic info about a parameter or ontology term etc like id, name, description
 * for use initially by the GeneHeatmap
 * @author jwarren
 *
 */
public class BasicBean {

	String id;
	String name;
	String description;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
