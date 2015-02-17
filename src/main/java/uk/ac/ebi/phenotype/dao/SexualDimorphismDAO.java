package uk.ac.ebi.phenotype.dao;

import java.sql.ResultSet;
import java.util.List;

public interface SexualDimorphismDAO {
	
	public List<String[]>  sexualDimorphismReportNoBodyWeight() ;
	
	public List<String[]> sexualDimorphismReportWithBodyWeight();
	
}
