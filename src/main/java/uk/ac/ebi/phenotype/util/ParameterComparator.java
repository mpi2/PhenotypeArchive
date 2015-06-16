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
package uk.ac.ebi.phenotype.util;

import java.util.Comparator;

import uk.ac.ebi.phenotype.pojo.Parameter;

public class ParameterComparator implements Comparator<Parameter> {
	/**
	 *  We need this to show IMPC parameters always on top (when ordering ascending).
	 * @param param1
	 * @param param2
	 * @return
	 */
	@Override
	public int compare(Parameter param1, Parameter param2) {
		if (isImpc(param1.getStableId()) && !isImpc(param2.getStableId())){
			return -1;
		}
		if (isImpc(param2.getStableId()) && !isImpc(param1.getStableId())){
			return 1;
		}
		return param1.getName().compareTo(param2.getName());
	}
	
	private boolean isImpc(String param){
		return param.startsWith("IMPC");
	}

}
