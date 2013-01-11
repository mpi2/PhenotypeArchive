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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import uk.ac.ebi.phenotype.pojo.BiologicalSample;


/**
 * The persistent class for the ima_mouse_image_vw database table.
 * 
 */
@Entity
@Table(name="ima_mouse_image_vw")
public class ImaMouseImageVw implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String barcode;
	private String cageBarcode;
	private int cageId;
	private int colonyId;
	private String colonyName;
	private String colonyPrefix;
	private String gender;
	private int genderId;
	private String genotype;
	private int litterId;
	private String litterName;
	private String name;
	private Integer ageInWeeks;
	private MtsMouseAlleleMv  mtsMouseAlleleMv;
	
	
	@NotFound(action=NotFoundAction.IGNORE)
	 @OneToOne( optional=true, fetch = FetchType.EAGER)
	  //@Fetch(value = FetchMode.SELECT)
	  @JoinColumn(name="ID" ,referencedColumnName="MOUSE_ID", insertable = false, updatable = false)
	public MtsMouseAlleleMv getMtsMouseAlleleMv() {
		return mtsMouseAlleleMv;
	}

	public void setMtsMouseAlleleMv(MtsMouseAlleleMv mtsMouseAlleleMv) {
		this.mtsMouseAlleleMv = mtsMouseAlleleMv;
	}

	@Column(name="age_in_weeks")
	public Integer getAgeInWeeks() {
		return ageInWeeks;
	}

	public void setAgeInWeeks(Integer ageInWeeks) {
		this.ageInWeeks = ageInWeeks;
	}

	//we don't care about going back to the record at the moment
	//private Set<ImaImageRecord> imaImageRecords;
	private BiologicalSample biologicalSample;

    public ImaMouseImageVw() {
    }

    @OneToOne(optional=false, fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name="ID",referencedColumnName="external_id")
	  public BiologicalSample getLiveSample() {
			return biologicalSample;
		}


		public void setLiveSample(BiologicalSample biologicalSample) {
			this.biologicalSample = biologicalSample;
		}

	@Id
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getBarcode() {
		return this.barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}


	@Column(name="CAGE_BARCODE")
	public String getCageBarcode() {
		return this.cageBarcode;
	}

	public void setCageBarcode(String cageBarcode) {
		this.cageBarcode = cageBarcode;
	}


	@Column(name="CAGE_ID")
	public int getCageId() {
		return this.cageId;
	}

	public void setCageId(int cageId) {
		this.cageId = cageId;
	}


	@Column(name="COLONY_ID")
	public int getColonyId() {
		return this.colonyId;
	}

	public void setColonyId(int colonyId) {
		this.colonyId = colonyId;
	}


	@Column(name="COLONY_NAME")
	public String getColonyName() {
		return this.colonyName;
	}

	public void setColonyName(String colonyName) {
		this.colonyName = colonyName;
	}


	@Column(name="COLONY_PREFIX")
	public String getColonyPrefix() {
		return this.colonyPrefix;
	}

	public void setColonyPrefix(String colonyPrefix) {
		this.colonyPrefix = colonyPrefix;
	}


	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}


	@Column(name="GENDER_ID")
	public int getGenderId() {
		return this.genderId;
	}

	public void setGenderId(int genderId) {
		this.genderId = genderId;
	}


	public String getGenotype() {
		return this.genotype;
	}

	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}


	@Column(name="LITTER_ID")
	public int getLitterId() {
		return this.litterId;
	}

	public void setLitterId(int litterId) {
		this.litterId = litterId;
	}


	@Column(name="LITTER_NAME")
	public String getLitterName() {
		return this.litterName;
	}

	public void setLitterName(String litterName) {
		this.litterName = litterName;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	//bi-directional many-to-one association to ImaImageRecord
//	@OneToMany(mappedBy="imaMouseImageVw", fetch=FetchType.EAGER)
//	public Set<ImaImageRecord> getImaImageRecords() {
//		return this.imaImageRecords;
//	}
//
//	public void setImaImageRecords(Set<ImaImageRecord> imaImageRecords) {
//		this.imaImageRecords = imaImageRecords;
//	}
	
}