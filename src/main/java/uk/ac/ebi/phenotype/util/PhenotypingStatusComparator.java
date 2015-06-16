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
import java.util.HashMap;
import java.util.Map;


public class PhenotypingStatusComparator implements Comparator<String> {

	Map<String, Integer> order = new HashMap<>(); //<string, desiredPosition>

	
	@Override
	public int compare(String o1, String o2) {
		order.put("Phenotype Attempt Registered", 1);
		order.put("Phenotyping Started", 2);
		order.put("Phenotyping Complete", 3);

		if (order.containsKey(o1) && order.containsKey(o2)){
			return order.get(o1).compareTo(order.get(o2));
		}
		
		return 0;
	}

}
