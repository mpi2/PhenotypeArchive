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

import uk.ac.ebi.phenotype.pojo.Procedure;

public class ProcedureComparator implements Comparator<Procedure> {

	/**
	 * We need to sort the procedures on the phenotype page so that the IMPC ones are always on top. Terry's requirement.
	 * @author tudose
	 */
	@Override
	public int compare(Procedure p1, Procedure p2) {

		if (isImpc(p1.getStableId()) && !isImpc(p2.getStableId())){
			return -1;
		}
		if (isImpc(p2.getStableId()) && !isImpc(p1.getStableId())){
			return 1;
		}
		return p1.getStableId().compareTo(p2.getStableId());
	}
	
	private boolean isImpc(String param){
		return param.startsWith("IMPC");
	}


}
