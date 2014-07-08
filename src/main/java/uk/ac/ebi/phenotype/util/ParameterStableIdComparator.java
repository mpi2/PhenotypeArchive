package uk.ac.ebi.phenotype.util;

import java.util.Comparator;

public class ParameterStableIdComparator implements Comparator<String> {
	/**
	 *  We need this to show IMPC parameters always on top (when ordering ascending).
	 * @param param1
	 * @param param2
	 * @return
	 */
	@Override
	public int compare(String param1, String param2) {
		if (isImpc(param1) && !isImpc(param2)){
			return -1;
		}
		if (isImpc(param2) && !isImpc(param1)){
			return 1;
		}
		return param1.compareTo(param2);
	}
	
	private boolean isImpc(String param){
		return param.startsWith("IMPC");
	}

}
