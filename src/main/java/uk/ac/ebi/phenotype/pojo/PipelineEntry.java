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
 * Share common properties of pipelines and procedures through a technical or a 
 * business superclass without including it as a regular mapped entity 
 * (ie no specific table for this entity). 
 * We map them as @MappedSuperclass.
 */

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class PipelineEntry extends SourcedEntry {

	@Column(name = "stable_id")
	String stableId;

	@Column(name = "stable_key")
	int stableKey;
		
	@Column(name = "name")
	private String name;	
	
	@Column(name = "description")
	private String description;
	

	@Column(name = "major_version")
	private Integer majorVersion;
	

	@Column(name = "minor_version")
	private Integer minorVersion;	
	
	/**
	 * @return the stableKey
	 */
	public int getStableKey() {
		return stableKey;
	}

	/**
	 * @param stableKey the stableKey to set
	 */
	public void setStableKey(int stableKey) {
		this.stableKey = stableKey;
	}

	/**
	 * @return the stableId
	 */
	public String getStableId() {
		return stableId;
	}

	/**
	 * @param stableId the stableId to set
	 */
	public void setStableId(String stableId) {
		this.stableId = stableId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the majorVersion
	 */
	public Integer getMajorVersion() {
		return majorVersion;
	}

	/**
	 * @param majorVersion the majorVersion to set
	 */
	public void setMajorVersion(Integer majorVersion) {
		this.majorVersion = majorVersion;
	}

	/**
	 * @return the minorVersion
	 */
	public Integer getMinorVersion() {
		return minorVersion;
	}

	/**
	 * @param minorVersion the minorVersion to set
	 */
	public void setMinorVersion(Integer minorVersion) {
		this.minorVersion = minorVersion;
	}

	public String toString() {
		return stableId + ": " +  name;
	}
}
