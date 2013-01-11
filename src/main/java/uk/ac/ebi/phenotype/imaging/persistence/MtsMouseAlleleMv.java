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

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "mts_mouse_allele_mv")
public class MtsMouseAlleleMv {

	@Id
	@Column(name = "MOUSE_ID")
	private int mouseId;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne
	@JoinColumn(name = "allele", referencedColumnName = "symbol", insertable = false, updatable = false)
	private AlleleMpi alleleMpi;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	@JoinColumn(name = "MOUSE_ID", referencedColumnName = "MOUSE_ID", insertable = false, updatable = false)
	private Set<MtsMouseAllele> mtsMouseAlleles;

	public AlleleMpi getAlleleMpi() {
		return alleleMpi;
	}

	public void setAlleleMpi(AlleleMpi alleleMpi) {
		this.alleleMpi = alleleMpi;
	}

	public int getMouseId() {
		return mouseId;
	}

	public void setMouseId(int id) {
		this.mouseId = id;
	}

	public Set<MtsMouseAllele> getMtsMouseAlleles() {
		return mtsMouseAlleles;
	}

	public void setMtsMouseAlleles(Set<MtsMouseAllele> mtsMouseAlleles) {
		this.mtsMouseAlleles = mtsMouseAlleles;
	}
}
