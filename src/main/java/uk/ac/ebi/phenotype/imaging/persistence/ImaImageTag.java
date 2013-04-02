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
import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import uk.ac.ebi.phenotype.imaging.persistence.ImaImageTagType;

import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * The persistent class for the IMA_IMAGE_TAG database table.
 * 
 */
@Entity
@Table(name="IMA_IMAGE_TAG")
public class ImaImageTag implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer checkNumber;
	private Date createdDate;
	private Integer creatorId;
	private Date editDate;
	private String editedBy;
	
	private int id;
	private String tagName;
	private ImaImageTagType imaImageTagType;
	private String tagValue;
	private Float xEnd;
	private Float xStart;
	private Float yEnd;
	private Float yStart;
	@JsonManagedReference private Set<AnnAnnotation> annAnnotations;
	
	//to get this to work without a request for each entry I need to use Set instead of list?????
	//bi-directional many-to-one association to AnnAnnotation
	@OneToMany(fetch=FetchType.EAGER)
	//@LazyCollection(LazyCollectionOption.FALSE)
	@JoinColumn(name="foreign_key_id", referencedColumnName="id")
	//@IndexColumn(name="ID")
	public Set<AnnAnnotation> getAnnAnnotations() {
			return this.annAnnotations;
		}

		public void setAnnAnnotations(Set<AnnAnnotation> annAnnotations) {
			this.annAnnotations = annAnnotations;
		}

@JsonBackReference	private ImaImageRecord imaImageRecord;

    public ImaImageTag() {
    }


	@Column(name="CHECK_NUMBER")
	public Integer getCheckNumber() {
		return this.checkNumber;
	}

	public void setCheckNumber(Integer checkNumber) {
		this.checkNumber = checkNumber;
	}


    @Temporal( TemporalType.DATE)
	@Column(name="CREATED_DATE")
	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


	@Column(name="CREATOR_ID")
	public Integer getCreatorId() {
		return this.creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}


    @Temporal( TemporalType.DATE)
	@Column(name="EDIT_DATE")
	public Date getEditDate() {
		return this.editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}


	@Column(name="EDITED_BY")
	public String getEditedBy() {
		return this.editedBy;
	}

	public void setEditedBy(String editedBy) {
		this.editedBy = editedBy;
	}

	//@JsonIgnore
	@Id
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Column(name="TAG_NAME")
	public String getTagName() {
		return this.tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}


	//uni-directional many-to-one association to ImaImageTagType
		@ManyToOne(fetch=FetchType.EAGER)
		@JoinColumn(name="TAG_TYPE_ID")
		public ImaImageTagType getImaImageTagType() {
			return this.imaImageTagType;
		}

		public void setImaImageTagType(ImaImageTagType imaImageTagType) {
			this.imaImageTagType = imaImageTagType;
		}


	@Column(name="TAG_VALUE")
	public String getTagValue() {
		return this.tagValue;
	}

	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}


	@Column(name="X_END")
	public Float getXEnd() {
		return this.xEnd;
	}

	public void setXEnd(Float xEnd) {
		this.xEnd = xEnd;
	}


	@Column(name="X_START")
	public Float getXStart() {
		return this.xStart;
	}

	public void setXStart(Float xStart) {
		this.xStart = xStart;
	}


	@Column(name="Y_END")
	public Float getYEnd() {
		return this.yEnd;
	}

	public void setYEnd(Float yEnd) {
		this.yEnd = yEnd;
	}


	@Column(name="Y_START")
	public Float getYStart() {
		return this.yStart;
	}

	public void setYStart(Float yStart) {
		this.yStart = yStart;
	}


	//bi-directional many-to-one association to ImaImageRecord
	//@ManyToOne
    //@JoinColumn(name="CUST_ID", nullable=false)
	@ManyToOne
	@JoinColumn(name="IMAGE_RECORD_ID", nullable=false)
	public ImaImageRecord getImaImageRecord() {
		return this.imaImageRecord;
	}

	public void setImaImageRecord(ImaImageRecord imaImageRecord) {
		this.imaImageRecord = imaImageRecord;
	}
	
}