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
 * Inherits from a biological sample. Usually an animal.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * @see BiologicalSample
 * @see OntologyTerm
 */

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@PrimaryKeyJoinColumn(name="id")
@Table(name = "live_sample")
public class LiveSample extends BiologicalSample {

	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "developmental_stage_acc"),
	@JoinColumn(name = "developmental_stage_db_id"),
	})	
	private OntologyTerm developmentalStage;
	
	@Column(name = "sex")
	private String sex;
	
	@Column(name = "zygosity")
	private String zygosity;
	
	@Column(name = "colony_id")
	private String colonyID;
	
	@Column(name = "date_of_birth")
	private Date dateOfBirth;

	/**
	 * @return the developmentalStage
	 */
	public OntologyTerm getDevelopmentalStage() {
		return developmentalStage;
	}

	/**
	 * @param developmentalStage the developmentalStage to set
	 */
	public void setDevelopmentalStage(OntologyTerm developmentalStage) {
		this.developmentalStage = developmentalStage;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @return the zygosity
	 */
	public String getZygosity() {
		return zygosity;
	}

	/**
	 * @param zygosity the zygosity to set
	 */
	public void setZygosity(String zygosity) {
		this.zygosity = zygosity;
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the colonyID
	 */
	public String getColonyID() {
		return colonyID;
	}

	/**
	 * @param colonyID the colonyID to set
	 */
	public void setColonyID(String colonyID) {
		this.colonyID = colonyID;
	}

}
