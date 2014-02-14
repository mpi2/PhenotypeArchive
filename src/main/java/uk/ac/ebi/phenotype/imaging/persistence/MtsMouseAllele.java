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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name="MTS_MOUSE_ALLELE")
public class MtsMouseAllele {
	private int id;
	private int mouseId;
	//private int alleleId;
	
	private MtsGenotypeDict mtsGenotypeDict;

	@ManyToOne
	@JoinColumn(name="GENOTYPE_DICT_ID", referencedColumnName="ID")
	public MtsGenotypeDict getMtsGenotypeDict() {
		return mtsGenotypeDict;
	}

	public void setMtsGenotypeDict(MtsGenotypeDict mtsGenotypeDict) {
		this.mtsGenotypeDict = mtsGenotypeDict;
	}

	@Id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	@Column(name="MOUSE_ID")
	public int getMouseId() {
		return mouseId;
	}

	public void setMouseId(int mouseId) {
		this.mouseId = mouseId;
	}
	
}
