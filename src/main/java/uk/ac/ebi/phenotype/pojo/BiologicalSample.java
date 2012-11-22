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
 * Instance of a biological model
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * @see BiologicalModel
 */

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;


@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name = "biological_sample")
public class BiologicalSample implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * MySQL auto increment
	 * GenerationType.AUTO won't work
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "external_id")
	String stableId;
	
	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "db_id")
	private Datasource datasource;

	@Column(name = "sample_group")
	String group;
	
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "sample_type_acc"),
	@JoinColumn(name = "sample_type_db_id"),
	})
	private OntologyTerm type;
		
	@OneToOne
	@JoinColumn(name = "organisation_id")
	private Organisation organisation;

	//a association table is used to store the link between the 2 entities
	// will implement this later!
	//@OneToOne
	//@JoinColumn(name = "biological_model_id")
	//private BiologicalModel biologicalModel;
	/**
	 * bi-directional
	 */
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
    @JoinTable(name="biological_model_sample",
        joinColumns = @JoinColumn(name="biological_sample_id"),
        inverseJoinColumns = @JoinColumn(name="biological_model_id")
    )
	private BiologicalModel biologicalModel;
		
	/**
	 * @return the biologicalModel
	 */
	public BiologicalModel getBiologicalModel() {
		return biologicalModel;
	}

	/**
	 * @param biologicalModel the biologicalModel to set
	 */
	public void setBiologicalModel(BiologicalModel biologicalModel) {
		this.biologicalModel = biologicalModel;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the stableId
	 */
	public String getStableId() {
		return stableId;
	}

	/**
	 * @param stableId the stableId to set
	 */
	public void setStableId(String stableId) {
		this.stableId = stableId;
	}

	/**
	 * @return the datasource
	 */
	public Datasource getDatasource() {
		return datasource;
	}

	/**
	 * @param datasource the datasource to set
	 */
	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the type
	 */
	public OntologyTerm getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(OntologyTerm type) {
		this.type = type;
	}


	/**
	 * @return the organisation
	 */
	public Organisation getOrganisation() {
		return organisation;
	}

	/**
	 * @param organisation the organisation to set
	 */
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

}
