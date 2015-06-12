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
package uk.ac.ebi.phenotype.solr.indexer.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class SangerProcedureMapper {
	private static  final Map<String, String> sangerProcedureToImpcMapping = populateMap();
	
	private static Map<String, String> populateMap(){
	Map<String, String> sangerProcedureToImpcMapping=new HashMap<>();
	sangerProcedureToImpcMapping.put("Wholemount Expression", "Adult LacZ");
	sangerProcedureToImpcMapping.put("Xray", "X-ray");
	sangerProcedureToImpcMapping.put("X-ray Imaging","Xray");
	sangerProcedureToImpcMapping.put("X-ray","Xray");
//	  'Adult LacZ' : 'Wholemount Expression',
//      'FACS Analysis' : 'Flow Cytometry',
//      'Histopathology' : 'Histology Slide',
//      'X-ray' : 'Xray',
//      'X-ray Imaging' : 'Xray',
//      'Combined SHIRPA and Dysmorphology' : 'Embryo Dysmorphology'
	// 'Xray' : 'X-ray Imaging',
	sangerProcedureToImpcMapping.put("Flow Cytometry", "FACS Analysis");
	sangerProcedureToImpcMapping.put("Histology Slide", "Histopathology");
	sangerProcedureToImpcMapping.put("Embryo Dysmorphology", "Gross Morphology Embryo");
	return Collections.unmodifiableMap(sangerProcedureToImpcMapping);
	}
	
	public static String getImpcProcedureFromSanger(String sangerProcedure) {
		if (sangerProcedureToImpcMapping.containsKey(sangerProcedure)) {
			return sangerProcedureToImpcMapping.get(sangerProcedure);
		} else {
			return sangerProcedure;
		}

	}
}
