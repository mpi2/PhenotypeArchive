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
	sangerProcedureToImpcMapping.put("Embryo Dysmorphology", "Combined SHIRPA and Dysmorphology");
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
