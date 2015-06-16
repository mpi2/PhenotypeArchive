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
	private Collection<String> mpIds=new ArrayList<String>();
	private Collection<String> mpTerms=new ArrayList<String>();
	private Collection<String> synonyms = new ArrayList<String>();	
	
	
	public Collection<String> getSynonyms() {
		return synonyms;
	}
	
	public void setSynonyms(Collection<String> synonyms) {	
		this.synonyms = synonyms;
	}

	public Collection<String> getMpTerms() {
		return mpTerms;
	}

	public void setMpTerms(Collection<String> mpTerms) {
		this.mpTerms = mpTerms;
	}


	public Collection<String> getMpIds() {
		return mpIds;
	}

	public void setMpIds(Collection<String> mpIds) {
		this.mpIds = mpIds;
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
