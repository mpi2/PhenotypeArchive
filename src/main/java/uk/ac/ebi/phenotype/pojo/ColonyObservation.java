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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "colony_observation")
public class ColonyObservation extends Observation {

	@OneToOne
	@JoinColumn(name = "biological_model_id")
	private BiologicalModel model;

	@Column(name = "colony_id")
	private String colony;


	public String getColony() {

		return colony;
	}


	public void setColony(String colony) {

		this.colony = colony;
	}


	public BiologicalModel getModel() {

		return model;
	}


	public void setModel(BiologicalModel model) {

		this.model = model;
	}
}
