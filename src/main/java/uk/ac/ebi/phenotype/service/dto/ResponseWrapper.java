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
package uk.ac.ebi.phenotype.service.dto;

import java.util.List;


public class ResponseWrapper<E> {
	List<E> list;
	Long totalNumberFound;
		
	public ResponseWrapper(List<E> list){
		this.list=list;
	}
	public Long getTotalNumberFound() {
	
		return totalNumberFound;
	}
	
	public void setTotalNumberFound(Long totalNumberFound) {
	
		this.totalNumberFound = totalNumberFound;
	}
	
	public List<E> getList() {
	
		return list;
	}
	public void setList(List<E> list) {
		this.list = list;
	}
	
	
}