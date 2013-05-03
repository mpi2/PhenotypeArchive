/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.pojo;

/**
 * 
 * Represents an ontology term in the database.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 * @see Synonym
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "ontology_term")
public class OntologyTerm {

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name="accession", column=@Column(name="acc")),
		@AttributeOverride(name="databaseId", column=@Column(name="db_id"))
	})
	DatasourceEntityId id;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "name")
	private String name;	
	
	@ElementCollection
	@CollectionTable(
		name="synonym", 
		joinColumns= {
			@JoinColumn(name="acc"),
			@JoinColumn(name="db_id"),
		}
	)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Fetch(FetchMode.SELECT)
	private List<Synonym> synonyms = new ArrayList<Synonym>();
	
	public OntologyTerm() {
		super();
	}

	/**
	 * @return the id
	 */
	public DatasourceEntityId getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(DatasourceEntityId id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the synonyms
	 */
	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	/**
	 * @param synonyms the synonyms to set
	 */
	public void setSynonyms(List<Synonym> synonyms) {
		this.synonyms = synonyms;
	}
	
	public void addSynonym(Synonym synonym) {
		if (this.synonyms == null) {
			this.synonyms = new LinkedList<Synonym>();
		}
		synonyms.add(synonym);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OntologyTerm [id=" + id + ", name=" + name + "]";
	}
	
}
