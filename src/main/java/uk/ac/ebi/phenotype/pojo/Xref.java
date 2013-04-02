/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
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
 * Representation of a genomic feature cross-reference in the database
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2013
 * 
 */

import javax.persistence.Embeddable;

@Embeddable
public class Xref extends DatasourceEntityId {

	int id;
	private String xrefAccession;
	private int xrefDatabaseId;
	
	public Xref() {
		super();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the xrefAccession
	 */
	public String getXrefAccession() {
		return xrefAccession;
	}

	/**
	 * @param xrefAccession the xrefAccession to set
	 */
	public void setXrefAccession(String xrefAccession) {
		this.xrefAccession = xrefAccession;
	}

	/**
	 * @return the xrefDatabaseId
	 */
	public int getXrefDatabaseId() {
		return xrefDatabaseId;
	}

	/**
	 * @param xrefDatabaseId the xrefDatabaseId to set
	 */
	public void setXrefDatabaseId(int xrefDatabaseId) {
		this.xrefDatabaseId = xrefDatabaseId;
	}	
	
}
