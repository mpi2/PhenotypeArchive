/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
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
 * Representation of an allele in the database.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "allele")
public class Allele {

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
	@JoinColumn(name = "gf_acc"),
	@JoinColumn(name = "gf_db_id"),
	})
	private GenomicFeature gene;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "symbol")
	private String symbol;
	
	public Allele() {
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
	 * @return the gene
	 */
	public GenomicFeature getGene() {
		return gene;
	}

	/**
	 * @param gene the gene to set
	 */
	public void setGene(GenomicFeature gene) {
		this.gene = gene;
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
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * @param symbol the symbol to set
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	
}
