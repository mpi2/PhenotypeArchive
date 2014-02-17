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

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;



/**
 * 
 * A representation of a mutant mouse for a unidimensional value.
 * 
 */
@Entity
@Table(name = "stats_mv_experimental_unidimensional_values")
public class UnidimensionalMutantView extends UnidimensionalView implements Serializable {

	private static final long serialVersionUID = 4505038269190999331L;
	
	@Override
	public String whatAmI() {
		return "mutant";
	}

	@Override
	public String toString() {
		return "UnidimensionalMutantView [biologicalModel=" + biologicalModel
				+ ", biologicalSample=" + biologicalSample + ", organisation="
				+ organisation + ", parameter=" + parameter + ", sex=" + sex
				+ ", zygosity=" + zygosity + ", colony=" + colony
				+ ", populationId=" + populationId + ", dataPoint=" + dataPoint
				+ "]";
	}

}

