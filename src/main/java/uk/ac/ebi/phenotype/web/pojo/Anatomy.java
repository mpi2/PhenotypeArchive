package uk.ac.ebi.phenotype.web.pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class to hold info on an anatomy term
 * 
 * @author jwarren
 * 
 */
public class Anatomy {
	/**
	 * e.g. MA:0002950
	 */
	private String accession="";
	/**
	 * e.g. right eye
	 */
	private String term="";
	
	private String description="Description here when we get it!";
	
	private Collection<String> topLevelTerms=new ArrayList<String>();
	private Collection<String> topLevelIds=new ArrayList<String>();
	private Collection<String> childTerms=new ArrayList<String>();
	private Collection<String> mpTerms=new ArrayList<String>();
	public Collection<String> getMpTerms() {
		return mpTerms;
	}

	public void setMpTerms(Collection<String> mpTerms) {
		this.mpTerms = mpTerms;
	}

	public Collection<String> getChildTerms() {
		return childTerms;
	}

	public void setChildTerms(Collection<String> childTerms) {
		this.childTerms = childTerms;
	}

	public Collection<String> getChildIds() {
		return childIds;
	}

	public void setChildIds(Collection<String> childIds) {
		this.childIds = childIds;
	}

	private Collection<String> childIds=new ArrayList<String>();
	
	public Collection<String> getTopLevelTerms() {
		return topLevelTerms;
	}

	public void setTopLevelTerms(Collection<String> topLevelTerms) {
		this.topLevelTerms = topLevelTerms;
	}

	public Collection<String> getTopLevelIds() {
		return topLevelIds;
	}

	public void setTopLevelIds(Collection<String> collection) {
		this.topLevelIds = collection;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * link to adult anatomy browser e.g. http://www.informatics.jax.org/searches/AMA.cgi?id=MA:0002950
	 */
	public String getMgiLinkString() {
		return "http://www.informatics.jax.org/searches/AMA.cgi?id="+accession;
	}

	public Anatomy(String accession, String term) {
		this.accession=accession;
		this.term=term;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

}
