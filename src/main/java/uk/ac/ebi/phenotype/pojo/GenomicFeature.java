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
 * Representation of a genomic feature in the database.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@XmlRootElement(name="genomicFeature")
@Entity
@Table(name = "genomic_feature")
public class GenomicFeature {

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name="accession", 
				column=@Column(name="acc")),
				@AttributeOverride(name="databaseId", 
				column=@Column(name="db_id"))
	})
	DatasourceEntityId id;

	@Column(name = "symbol")
	private String symbol;

	@Column(name = "name")
	private String name;

	// element collections are merged/removed with their parents
	@ElementCollection(fetch=FetchType.EAGER)//JW made eager for the indexing of images and rest service - will this cause problems elsewhere?
	@Fetch(value = FetchMode.SELECT)   
	@CollectionTable(name="synonym", 
	joinColumns= {@JoinColumn(name="acc"),@JoinColumn(name="db_id")}
			)
	private List<Synonym> synonyms;

	/*@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinTable(name="synonym",
			   joinColumns= {@JoinColumn(name="acc"),@JoinColumn(name="db_id"),},
	           inverseJoinColumns = @JoinColumn( name="id")
	)
	private Set<Synonym> synonyms;*/

	@ElementCollection(fetch=FetchType.EAGER)//JW made eager for the indexing of images and rest service - will this cause problems elsewhere?
	@Fetch(value = FetchMode.SELECT)   
	@CollectionTable(name="xref", 
	joinColumns= {@JoinColumn(name="acc"),@JoinColumn(name="db_id")})
	@AttributeOverrides({
		@AttributeOverride(name="xrefAccession", column=@Column(name="xref_acc")),
		@AttributeOverride(name="xrefDatabaseId", column=@Column(name="xref_db_id"))})
	private List<Xref> xrefs;

	@OneToOne
	@JoinColumns({
		@JoinColumn(name = "biotype_acc"),
		@JoinColumn(name = "biotype_db_id"),
	})
	private OntologyTerm biotype;

	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumns({
		@JoinColumn(name = "subtype_acc"),
		@JoinColumn(name = "subtype_db_id"),
	})
	private OntologyTerm subtype;

	@JsonIgnore
	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "seq_region_id")
	private SequenceRegion sequenceRegion;

	@Column(name = "seq_region_start")
	private int start;

	@Column(name = "seq_region_end")
	private int end;

	@Column(name = "seq_region_strand")
	private int strand;

	@Column(name = "cm_position")
	private String cMposition;

	public GenomicFeature() {
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
		if (synonyms == null) {
			synonyms = new LinkedList<Synonym>();
		}
		this.synonyms.add(synonym);
	}
	
	/**
	 * @return the xrefs
	 */
	public List<Xref> getXrefs() {
		return xrefs;
	}


	/**
	 * @param xrefs the xrefs to set
	 */
	public void setXrefs(List<Xref> xrefs) {
		this.xrefs = xrefs;
	}


	public void addXref(Xref xref) {
		if (xrefs == null) {
			xrefs = new LinkedList<Xref>();
		}
		this.xrefs.add(xref);
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
	 * @return the subtype
	 */
	public OntologyTerm getSubtype() {
		return subtype;
	}


	/**
	 * @param subtype the subtype to set
	 */
	public void setSubtype(OntologyTerm subtype) {
		this.subtype = subtype;
	}


	/**
	 * @return the sequenceRegion
	 */
	public SequenceRegion getSequenceRegion() {
		return sequenceRegion;
	}


	/**
	 * @param sequenceRegion the sequenceRegion to set
	 */
	public void setSequenceRegion(SequenceRegion sequenceRegion) {
		this.sequenceRegion = sequenceRegion;
	}


	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}


	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}


	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}


	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}


	/**
	 * @return the strand
	 */
	public int getStrand() {
		return strand;
	}


	/**
	 * @param strand the strand to set
	 */
	public void setStrand(int strand) {
		this.strand = strand;
	}


	/**
	 * @return the cMposition
	 */
	public String getcMposition() {
		return cMposition;
	}


	/**
	 * @param cMposition the cMposition to set
	 */
	public void setcMposition(String cMposition) {
		this.cMposition = cMposition;
	}


	@Override
	public String toString() {
		return "GenomicFeature [id=" + id + ", symbol=" + symbol + ", name="
				+ name + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((biotype == null) ? 0 : biotype.hashCode());
		result = prime * result
				+ ((cMposition == null) ? 0 : cMposition.hashCode());
		result = prime * result + end;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((sequenceRegion == null) ? 0 : sequenceRegion.hashCode());
		result = prime * result + start;
		result = prime * result + strand;
		result = prime * result + ((subtype == null) ? 0 : subtype.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result
				+ ((synonyms == null) ? 0 : synonyms.hashCode());
		result = prime * result + ((xrefs == null) ? 0 : xrefs.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenomicFeature other = (GenomicFeature) obj;
		if (biotype == null) {
			if (other.biotype != null)
				return false;
		} else if (!biotype.equals(other.biotype))
			return false;
		if (cMposition == null) {
			if (other.cMposition != null)
				return false;
		} else if (!cMposition.equals(other.cMposition))
			return false;
		if (end != other.end)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sequenceRegion == null) {
			if (other.sequenceRegion != null)
				return false;
		} else if (!sequenceRegion.equals(other.sequenceRegion))
			return false;
		if (start != other.start)
			return false;
		if (strand != other.strand)
			return false;
		if (subtype == null) {
			if (other.subtype != null)
				return false;
		} else if (!subtype.equals(other.subtype))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (synonyms == null) {
			if (other.synonyms != null)
				return false;
		} else if (!synonyms.equals(other.synonyms))
			return false;
		if (xrefs == null) {
			if (other.xrefs != null)
				return false;
		} else if (!xrefs.equals(other.xrefs))
			return false;
		return true;
	}


	


	
}
