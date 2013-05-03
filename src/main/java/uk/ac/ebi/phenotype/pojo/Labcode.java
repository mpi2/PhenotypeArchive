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
 * 
 * Representation of a laboratory code (based on ILAR) in the database.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @version $Revision: 1291 $
 *  @since February 2012
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ilar")
public class Labcode {

	@Id
	@Column(name = "labcode")
	private String labcode;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "investigator")
	private String investigator;
	
	@Column(name = "organisation")
	private String organisation;
	
	protected Labcode() {
		super();
	}
	
	/**
	 * @return the labcode
	 */
	public String getLabcode() {
		return labcode;
	}
	/**
	 * @param labcode the labcode to set
	 */
	public void setLabcode(String labcode) {
		this.labcode = labcode;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the investigator
	 */
	public String getInvestigator() {
		return investigator;
	}
	/**
	 * @param investigator the investigator to set
	 */
	public void setInvestigator(String investigator) {
		this.investigator = investigator;
	}
	/**
	 * @return the organisation
	 */
	public String getOrganisation() {
		return organisation;
	}
	/**
	 * @param organisation the organisation to set
	 */
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	
	public String toString() {
		return "Labcode = '" + labcode + "', status = " + status + "', investigator = '" + investigator + "', organisation = '" + organisation + "'";
	}
	
}
