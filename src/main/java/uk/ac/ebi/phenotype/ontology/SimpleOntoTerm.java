package uk.ac.ebi.phenotype.ontology;

public class SimpleOntoTerm {

	private String id;
	private String name;
	
	public void setTermName(String name){
		this.name = name;
	}
	
	public String getTermName(){
		return name;
	}
	
	public void setTermId(String id){
		this.id = id;
	}
	
	public String getTermId(){
		return id;
	}
	
}
