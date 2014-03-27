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
 * Unidimensional observation
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * 
 */
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@PrimaryKeyJoinColumn(name="id")
@Table(name = "unidimensional_observation")
public class UnidimensionalObservation extends Observation {

	@Column(name = "data_point")
	private float dataPoint;

	/**
	 * @return the dataPoint
	 */
	public float getDataPoint() {
		return dataPoint;
	}

	@Override
	public String toString() {
		return "UnidimensionalObservation [dataPoint=" + dataPoint + "]";
	}

	/**
	 * @param dataPoint the dataPoint to set
	 */
	public void setDataPoint(float dataPoint) {
		this.dataPoint = dataPoint;
	}
	
}
