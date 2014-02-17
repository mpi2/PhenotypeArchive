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
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the IMA_QC_DICT database table.
 * 
 */
@Entity
@Table(name="IMA_QC_DICT")
public class ImaQcDict implements Serializable {
	private static final long serialVersionUID = 1L;
	private String description;
	private int id;
	private String name;
	private int orderBy;
	//private Set<ImaImageRecord> imaImageRecords;

    public ImaQcDict() {
    }


	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Id
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Column(name="ORDER_BY")
	public int getOrderBy() {
		return this.orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}


	//bi-directional many-to-one association to ImaImageRecord
//	@OneToMany(mappedBy="imaQcDict", fetch=FetchType.EAGER)
//	public Set<ImaImageRecord> getImaImageRecords() {
//		return this.imaImageRecords;
//	}
//
//	public void setImaImageRecords(Set<ImaImageRecord> imaImageRecords) {
//		this.imaImageRecords = imaImageRecords;
//	}
	
}
