/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
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
 * Representation of a strain.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * 
 */

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
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "strain")
public class Strain {

	@EmbeddedId
	@AttributeOverrides({
	@AttributeOverride(name="accession", 
					   column=@Column(name="acc")),
	@AttributeOverride(name="databaseId", 
	   column=@Column(name="db_id"))
	})
	DatasourceEntityId id;
	
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "biotype_acc"),
	@JoinColumn(name = "biotype_db_id"),
	})
	private OntologyTerm biotype;
	
	@Column(name = "name")
	private String name;

	// element collections are merged/removed with their parents
	@ElementCollection
	   @CollectionTable(name="synonym", 
	   					joinColumns= {@JoinColumn(name="acc"),@JoinColumn(name="db_id"),}
	   )
	private List<Synonym> synonyms;
	
	public Strain() {
		super();
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
		if (synonyms == null) {
			synonyms = new LinkedList<Synonym>();
		}
		this.synonyms.add(synonym);
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
	 * @return the biotype
	 */
	public OntologyTerm getBiotype() {
		return biotype;
	}



	/**
	 * @param biotype the biotype to set
	 */
	public void setBiotype(OntologyTerm biotype) {
		this.biotype = biotype;
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



	public String toString() {
		return "Id: " + id + "; Name: " + name + ";";}
}
