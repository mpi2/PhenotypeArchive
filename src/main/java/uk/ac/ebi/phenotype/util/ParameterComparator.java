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
