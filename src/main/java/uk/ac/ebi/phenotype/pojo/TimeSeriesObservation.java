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
 * Time series.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * 
 */
import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@PrimaryKeyJoinColumn(name="id")
@Table(name = "time_series_observation")
public class TimeSeriesObservation extends Observation {

	
	@Column(name = "data_point")
	private float dataPoint;
	
	@Column(name = "time_point")
	private Date timePoint;

	@Column(name = "discrete_point")
	private float DiscretePoint;
	
	/**
	 * @return the dataPoint
	 */
	public float getDataPoint() {
		return dataPoint;
	}

	/**
	 * @param dataPoint the dataPoint to set
	 */
	public void setDataPoint(float dataPoint) {
		this.dataPoint = dataPoint;
	}

	/**
	 * @return the timePoint
	 */
	public Date getTimePoint() {
		return timePoint;
	}

	/**
	 * @param timePoint the timePoint to set
	 */
	public void setTimePoint(Date timePoint) {
		this.timePoint = timePoint;
	}

	/**
	 * @return the discretePoint
	 */
	public float getDiscretePoint() {
		return DiscretePoint;
	}

	/**
	 * @param discretePoint the discretePoint to set
	 */
	public void setDiscretePoint(float discretePoint) {
		DiscretePoint = discretePoint;
	}
	
	/** 
	 * Set a time point according to a date and a unit
	 * Need some revision
	 * @param timePoint
	 * @param experimentDate
	 * @param unit
	 * @since January 2013
	 */
	public void setTimePoint(String timePoint, Date experimentDate, String unit) {
		
		Date date = null;
		DateFormat inputDateFormatter = null;
//		System.out.println("setTimePoint: " + timePoint + "   "  + unit);
		
		try {

			if (unit.equals("minutes") || unit.equals("number") || unit.equals("Time in hours relative to lights out")) {
				float value = Float.parseFloat(timePoint);
				this.setDiscretePoint(value);
				//inputDateFormatter = new SimpleDateFormat("m");
				//date = inputDateFormatter.parse(timePoint);

			} else { //if (unit.equals("Time in hours relative to lights out")) {
				// experimental code!!!
				// check if it's relative to hours or absolute
				try {
					float value = Float.parseFloat(timePoint);
					// right so convert to an hour
					String rawHours = timePoint.substring(0, timePoint.indexOf("."));
					String rawMinutes = timePoint.substring(timePoint.indexOf(".")+1);
//					System.out.println(rawHours + "::::" + rawMinutes);
					int hours = Integer.parseInt(rawHours);
					float minutes = Float.parseFloat(rawMinutes) * 0.6f;
//					System.out.println(hours + "::::" + minutes);
					
				} catch (NumberFormatException ex) {

				}					
			}

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
}
