package uk.ac.ebi.phenotype.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class ProductionStatusComparator  implements Comparator<String> {
	Map<String, Integer> order = new HashMap<>(); //<string, desiredPosition>
	
	@Override
	public int compare(String o1, String o2) {
		order.put("Micro-injection in progress", 1);
		order.put("Chimeras obtained", 2);
		order.put("Genotype confirmed", 3);
		order.put("Cre Excision Started", 4);
		order.put("Cre Excision Complete", 5);

		if (order.containsKey(o1) && order.containsKey(o2)){
			return order.get(o1).compareTo(order.get(o2));
		}
		
		return 0;
	}
}
