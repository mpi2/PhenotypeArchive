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
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.Date;


/**
 * Created by jmason on 06/05/2015.
 */
@Entity
@PrimaryKeyJoinColumn(name="id")
@Table(name = "datetime_observation")
public class DatetimeObservation extends Observation {


	@Column(name = "datetime_point")
	private Date datetimePoint;


	public Date getDatetimePoint() {

		return datetimePoint;
	}


	public void setDatetimePoint(Date datetimePoint) {

		this.datetimePoint = datetimePoint;
	}


	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (!(o instanceof DatetimeObservation)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		DatetimeObservation that = (DatetimeObservation) o;

		return !(datetimePoint != null ? !datetimePoint.equals(that.datetimePoint) : that.datetimePoint != null);

	}


	@Override
	public int hashCode() {

		int result = super.hashCode();
		result = 31 * result + (datetimePoint != null ? datetimePoint.hashCode() : 0);
		return result;
	}

}
