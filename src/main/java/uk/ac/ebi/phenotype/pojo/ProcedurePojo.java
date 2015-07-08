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
/**
 * @author tudose
 */
package uk.ac.ebi.phenotype.pojo;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.phenotype.solr.indexer.beans.ImpressBean;


/**
 * @author tudose
 *
 */
public class ProcedurePojo {

	String procedureId;
	String procedureName;
	String procedureStableId;
	String procedureKey;
	List<ImpressBean> parameters;
	
	
	
	public ProcedurePojo(String procedureId,	String procedureName, String procedureStableId, String procedureKey){
		
		this.procedureId = procedureId;
		this.procedureName = procedureName;
		this.procedureKey = procedureKey;
		this.procedureStableId = procedureStableId;
		this.parameters = new ArrayList<>();
	}
	
	
	
	public void addParameter(ImpressBean parameter){
		parameters.add(parameter);
	}


	
	public String getProcedureId() {
	
		return procedureId;
	}


	
	public void setProcedureId(String procedureId) {
	
		this.procedureId = procedureId;
	}


	
	public String getProcedureName() {
	
		return procedureName;
	}


	
	public void setProcedureName(String procedureName) {
	
		this.procedureName = procedureName;
	}


	
	public String getProcedureStableId() {
	
		return procedureStableId;
	}


	
	public void setProcedureStableId(String procedureStableId) {
	
		this.procedureStableId = procedureStableId;
	}


	
	public String getProcedureKey() {
	
		return procedureKey;
	}


	
	public void setProcedureKey(String procedureKey) {
	
		this.procedureKey = procedureKey;
	}


	
	public List<ImpressBean> getParameters() {
	
		return parameters;
	}


	
	public void setParameters(List<ImpressBean> parameters) {
	
		this.parameters = parameters;
	}
	
		
	
}
