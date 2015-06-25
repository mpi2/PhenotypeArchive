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
package uk.ac.ebi.phenotype.service;

import org.apache.solr.client.solrj.response.FacetField.Count;

public class ProcedureBean {

	String stableId;

	String name;
	
	public ProcedureBean(String name, String stableId) {
		setName(name);
		setStableId(stableId);
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
	
	@Override
	public String toString() {

		return "ProcedureBean [stableId=" + stableId + ", name=" + name + "]";
	}
	
	
}
