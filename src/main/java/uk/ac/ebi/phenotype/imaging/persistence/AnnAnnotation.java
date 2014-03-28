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
package uk.ac.ebi.phenotype.imaging.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the ANN_ANNOTATION database table.
 * 
 */
@Entity
@Table(name="ANN_ANNOTATION")
public class AnnAnnotation implements Serializable {
	private static final long serialVersionUID = 1L;
	private int checkNumber;
	private Date createdDate;
	private int creatorId;
	private Date editDate;
	private String editedBy;
	private int foreignKeyId;
	private String foreignTableName;
	private int id;
	private AnnOntologyDict annOntologyDict;
	private String termId;
	private String termName;

	public AnnAnnotation() {
    }


	@Column(name="CHECK_NUMBER")
	public int getCheckNumber() {
		return this.checkNumber;
	}

	public void setCheckNumber(int checkNumber) {
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
	public int getCreatorId() {
		return this.creatorId;
	}

	public void setCreatorId(int creatorId) {
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


	@Column(name="FOREIGN_KEY_ID")
	public int getForeignKeyId() {
		return this.foreignKeyId;
	}

	public void setForeignKeyId(int foreignKeyId) {
		this.foreignKeyId = foreignKeyId;
	}


	@Column(name="FOREIGN_TABLE_NAME")
	public String getForeignTableName() {
		return this.foreignTableName;
	}

	public void setForeignTableName(String foreignTableName) {
		this.foreignTableName = foreignTableName;
	}

	@Id
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	//bi-directional many-to-one association to AnnOntologyDict
    @ManyToOne
	@JoinColumn(name="ONTOLOGY_DICT_ID", referencedColumnName="ID")
	public AnnOntologyDict getAnnOntologyDict() {
		return this.annOntologyDict;
	}

	public void setAnnOntologyDict(AnnOntologyDict annOntologyDict) {
		this.annOntologyDict = annOntologyDict;
	}


	@Column(name="TERM_ID")
	public String getTermId() {
		return this.termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}


	@Column(name="TERM_NAME")
	public String getTermName() {
		return this.termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	
	
}
