/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.solr.indexer.beans;

/**
 * Class to act as Map value DTO for impress data
 */
public class ImpressBean {
	public Integer id;
	public String stableKey;
	public String stableId;
	public String name;


	public Integer getId() {

		return id;
	}


	public void setId(Integer id) {

		this.id = id;
	}


	public String getStableKey() {

		return stableKey;
	}


	public void setStableKey(String stableKey) {

		this.stableKey = stableKey;
	}


	public String getStableId() {

		return stableId;
	}


	public void setStableId(String stableId) {

		this.stableId = stableId;
	}


	public String getName() {

		return name;
	}


	public void setName(String name) {

		this.name = name;
	}
}
