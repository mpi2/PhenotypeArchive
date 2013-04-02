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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonManagedReference;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import uk.ac.ebi.phenotype.pojo.Organisation;



import java.util.Date;
import java.util.List;


/**
 * The persistent class for the IMA_IMAGE_RECORD database table.
 * 
 */
@Entity
@Table(name="IMA_IMAGE_RECORD")
public class ImaImageRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private int checkNumber;
	private Date createdDate;
	private int creatorId;
	private String downloadFilePath;
	private Date editDate;
	private String editedBy;
	//private int foreignKeyId;
	private ImaMouseImageVw imaMouseImageVw;
	private String foreignTableName;
	private String fullResolutionFilePath;
	private String largeThumbnailFilePath;
	private String originalFileName;
	//private int publishedStatusId;
	private ImaPublishedDict imaPublishedDict;
	private ImaQcDict imaQcDict;
	private String smallThumbnailFilePath;
	//private int subcontextId;
	private ImaSubcontext imaSubcontext;
	private Organisation organisation;
	
	@ManyToOne(optional=false)
    @JoinColumn(name="organisation",referencedColumnName="ID")
	public Organisation getOrganisation() {
		return organisation;
	}


	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	@JsonManagedReference private List<ImaImageTag> imaImageTags;

    public ImaImageRecord() {
    }


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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


	@Column(name="DOWNLOAD_FILE_PATH")
	public String getDownloadFilePath() {
		return this.downloadFilePath;
	}

	public void setDownloadFilePath(String downloadFilePath) {
		this.downloadFilePath = downloadFilePath;
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


//	@Column(name="FOREIGN_KEY_ID")
//	public int getForeignKeyId() {
//		return this.foreignKeyId;
//	}
//
//	public void setForeignKeyId(int foreignKeyId) {
//		this.foreignKeyId = foreignKeyId;
//	}
	
	//not found added to get around this error:
//	INFO : org.hibernate.event.internal.DefaultLoadEventListener - HHH000327: Error performing load command : org.hibernate.ObjectNotFoundException: No row with the given identifier exists: [uk.ac.ebi.mpi2.imaging.persistence.ImaMouseImageVw#1491]
//			Exception in thread "main" org.hibernate.ObjectNotFoundException: No row with the given identifier exists: [uk.ac.ebi.mpi2.imaging.persistence.ImaMouseImageVw#1491]
//				at org.hibernate.internal.SessionFactoryImpl$1$1.handleEntityNotFound(SessionFactoryImpl.java:247)
//				at org.hibernate.event.internal.DefaultLoadEventListener.load(DefaultLoadEventListener.java:210)
//				at org.hibernate.event.internal.DefaultLoadEventListener.proxyOrLoad(DefaultLoadEventListener.java:251)
	 @NotFound(action=NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name="FOREIGN_KEY_ID", referencedColumnName="ID" )
	public ImaMouseImageVw getImaMouseImageVw() {
		return this.imaMouseImageVw;
	}

	public void setImaMouseImageVw(ImaMouseImageVw imaMouseImageVw) {
		this.imaMouseImageVw = imaMouseImageVw;
	}


	@Column(name="FOREIGN_TABLE_NAME")
	public String getForeignTableName() {
		return this.foreignTableName;
	}

	public void setForeignTableName(String foreignTableName) {
		this.foreignTableName = foreignTableName;
	}

	@NotNull
	@Column(name="FULL_RESOLUTION_FILE_PATH")
	public String getFullResolutionFilePath() {
		return this.fullResolutionFilePath;
	}

	public void setFullResolutionFilePath(String fullResolutionFilePath) {
		this.fullResolutionFilePath = fullResolutionFilePath;
	}


	@Column(name="LARGE_THUMBNAIL_FILE_PATH")
	public String getLargeThumbnailFilePath() {
		return this.largeThumbnailFilePath;
	}

	public void setLargeThumbnailFilePath(String largeThumbnailFilePath) {
		this.largeThumbnailFilePath = largeThumbnailFilePath;
	}


	@Column(name="ORIGINAL_FILE_NAME")
	public String getOriginalFileName() {
		return this.originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	@ManyToOne(optional=false)
    @JoinColumn(name="PUBLISHED_STATUS_ID",referencedColumnName="ID")
	//@Column(name="PUBLISHED_STATUS_ID")
	public ImaPublishedDict getImaPublishedDict() {
		return this.imaPublishedDict;
	}

	public void setImaPublishedDict(ImaPublishedDict imaPublishedDict) {
		this.imaPublishedDict = imaPublishedDict;
	}


	@ManyToOne(optional=false)
    @JoinColumn(name="QC_STATUS_ID",referencedColumnName="ID")
	//@Column(name="QC_STATUS_ID")
	public ImaQcDict getImaQcDict() {
		return this.imaQcDict;
	}

	public void setImaQcDict(ImaQcDict imaQcDict) {
		this.imaQcDict = imaQcDict;
	}


	@Column(name="SMALL_THUMBNAIL_FILE_PATH")
	public String getSmallThumbnailFilePath() {
		return this.smallThumbnailFilePath;
	}

	public void setSmallThumbnailFilePath(String smallThumbnailFilePath) {
		this.smallThumbnailFilePath = smallThumbnailFilePath;
	}


//	@Column(name="SUBCONTEXT_ID")
//	public int getSubcontextId() {
//		return this.subcontextId;
//	}

//	public void setSubcontextId(int subcontextId) {
//		this.subcontextId = subcontextId;
//	}

	
	@OneToMany(fetch=FetchType.EAGER, mappedBy="imaImageRecord")
	public List<ImaImageTag> getImaImageTags() {
		return this.imaImageTags;
	}

	public void setImaImageTags(List<ImaImageTag> imaImageTags) {
		this.imaImageTags = imaImageTags;
	}
	
	//uni-directional many-to-one association to subcontext
	//@ManyToOne(fetch=FetchType.EAGER)
	//@JoinColumn(name="SUBCONTEXT_ID")
	@ManyToOne(optional=false)
    @JoinColumn(name="SUBCONTEXT_ID",referencedColumnName="ID")
	public ImaSubcontext getImaSubcontext() {
		return this.imaSubcontext;
	}

	public void setImaSubcontext(ImaSubcontext imaSubcontext) {
		this.imaSubcontext = imaSubcontext;
	}
}