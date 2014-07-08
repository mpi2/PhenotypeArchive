package uk.ac.ebi.phenotype.util;

import java.util.Comparator;

import uk.ac.ebi.phenotype.pojo.Procedure;

public class ProcedureComparator implements Comparator<Procedure> {

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
