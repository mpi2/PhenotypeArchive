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
 * Compound key representation for external id in the database.
 * It is composed of an accession and an external database identifier.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @version $Revision: 1291 $
 *  @since February 2012
 */

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class DatasourceEntityId implements Serializable{
	private String accession;
	private int databaseId;
	
	public DatasourceEntityId() {
		super();
	}
	
	public DatasourceEntityId(String accession, int databaseId){
		this.accession = accession;
		this.databaseId = databaseId;
	}

	
	
	/**
	 * @return the accession
	 */
	public String getAccession() {
		return accession;
	}



	/**
	 * @param accession the accession to set
	 */
	public void setAccession(String accession) {
		this.accession = accession;
	}



	/**
	 * @return the databaseId
	 */
	public int getDatabaseId() {
		return databaseId;
	}



	/**
	 * @param databaseId the databaseId to set
	 */
	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}



	@Override
	public boolean equals(Object arg0) {
		if(arg0 == null) return false;
		if(!(arg0 instanceof DatasourceEntityId)) return false;
		DatasourceEntityId arg1 = (DatasourceEntityId) arg0;
		return (this.databaseId == arg1.getDatabaseId()) && (this.accession.equals(arg1.getAccession()));

	}
	@Override
	public int hashCode() {
		int hsCode;
		hsCode = Integer.valueOf(databaseId).hashCode();
		hsCode = 19 * hsCode+ accession.hashCode();
		return hsCode;
	}

}
