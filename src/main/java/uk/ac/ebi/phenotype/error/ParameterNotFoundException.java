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
package uk.ac.ebi.phenotype.error;

/**
 * 
 * GenomicFeatureNotFoundException is thrown when a genomic feature can't be
 * found 
 * @see GenesController
 */

public class ParameterNotFoundException extends Exception {

	String parameter = null;
	
	public ParameterNotFoundException(String msg, String parameter) {
		super(msg);
		this.parameter = parameter;
	}

	/**
	 * @return the acc
	 */
	public String getAcc() {
		return parameter;
	}

	/**
	 * @param acc the acc to set
	 */
	public void setAcc(String parameter) {
		this.parameter = parameter;
	}
	
	
}
