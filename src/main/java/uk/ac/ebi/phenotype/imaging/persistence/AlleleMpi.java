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
package uk.ac.ebi.phenotype.imaging.persistence;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import uk.ac.ebi.phenotype.pojo.DatasourceEntityId;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;


@Entity
@Table(name="allele")
public class AlleleMpi implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	private String name;
	
	@Column(name = "symbol")
	private String symbol;
	
	DatasourceEntityId id;
	
	String gfAcc;
	
//	@Column(name="gf_acc")
//	public String getGfAcc() {
//		return gfAcc;
//	}
//	public void setGfAcc(String gfAcc) {
//		this.gfAcc = gfAcc;
//	}
	public AlleleMpi() {
		super();
	}
	


	private GenomicFeature genomicFeature;
	
	@ManyToOne
	@JoinColumns({
	@JoinColumn(name = "gf_acc"),
	@JoinColumn(name = "gf_db_id"),
	})
	public GenomicFeature getGenomicFeature() {
		return genomicFeature;
	}
	public void setGenomicFeature(GenomicFeature genomicFeature) {
		this.genomicFeature = genomicFeature;
	}


	
	
	//@JsonIgnore
	@Id
	@EmbeddedId
	@AttributeOverrides({
	@AttributeOverride(name="accession", 
					   column=@Column(name="acc")),
	@AttributeOverride(name="databaseId", 
	   column=@Column(name="db_id"))
	})
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
