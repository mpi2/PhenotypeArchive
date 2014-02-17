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
 * Representation of a sequence region.
 * Sequence are not stored (currently).
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * @see CoordinateSystem
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "seq_region")
public class SequenceRegion {

	@Id
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@OneToOne
	@JoinColumn(name = "coord_system_id")
	private CoordinateSystem coordinateSystem;
	
	@Column(name = "length")
	private int length;	
	
	public SequenceRegion() {
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
	 * @return the coordinateSystem
	 */
	public CoordinateSystem getCoordinateSystem() {
		return coordinateSystem;
	}

	/**
	 * @param coordinateSystem the coordinateSystem to set
	 */
	public void setCoordinateSystem(CoordinateSystem coordinateSystem) {
		this.coordinateSystem = coordinateSystem;
	}
	
	
}
