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

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;



/**
 * 
 * A representation of a control mouse for a unidimensional value.
 * 
 */
@Entity
@Table(name = "stats_mv_control_unidimensional_values")
public class UnidimensionalControlView extends UnidimensionalView implements Serializable {

	private static final long serialVersionUID = -2302911113942593680L;

	@Override
	public String whatAmI() {
		return "control";
	}


}

