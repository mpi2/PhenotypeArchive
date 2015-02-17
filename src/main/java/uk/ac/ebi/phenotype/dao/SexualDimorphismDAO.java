package uk.ac.ebi.phenotype.dao;

import java.sql.ResultSet;
import java.util.List;

public interface SexualDimorphismDAO {
	
	public List<String[]>  sexualDimorphismReportNoBodyWeight(String baseUrl) ;
	
	public List<String[]> sexualDimorphismReportWithBodyWeight(String baseUrl);
	
}
