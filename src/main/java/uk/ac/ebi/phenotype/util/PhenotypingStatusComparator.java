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
