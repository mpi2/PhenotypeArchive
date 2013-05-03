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
package uk.ac.ebi.phenotype.pojo;

/**
 * 
 * Representation of coordinate system in the database.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @version $Revision: 1291 $
 *  @since February 2012
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "coord_system")
public class CoordinateSystem {

	@Id
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "strain_acc"),
	@JoinColumn(name = "strain_db_id"),
	})
	private Strain strain;
	
	@OneToOne
	@JoinColumn(name = "db_id")
	private Datasource datasource;
	
	protected CoordinateSystem() {
		super();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
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
	 * @return the strain
	 */
	public Strain getStrain() {
		return strain;
	}

	/**
	 * @param strain the strain to set
	 */
	public void setStrain(Strain strain) {
		this.strain = strain;
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
	
}
