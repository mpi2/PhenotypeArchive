package uk.ac.ebi.phenotype.ontology;

import java.util.ArrayList;
import java.util.HashSet;


public class PhenotypeSummaryType {
	private String id; // mp top level term id
	private String name;
	private String sex;
	private long  numberOfEntries;
	private HashSet <String> dataSources;
	
	public PhenotypeSummaryType (String mpId, String mpName, String sex, long numberOfEntries, HashSet <String> dataSources){
		this.id = mpId;
		this.name = mpName;
		this.sex = sex;
		this.numberOfEntries = numberOfEntries;
		this.dataSources = dataSources;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSex() {
		return sex;
	}

	public long getNumberOfEntries() {
		return numberOfEntries;
	}

	public ArrayList<String> getDataSources() {
		return new ArrayList<String>(dataSources);
	}
		
}
