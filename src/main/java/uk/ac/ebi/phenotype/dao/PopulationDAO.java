package uk.ac.ebi.phenotype.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.phenotype.pojo.Population;

public interface PopulationDAO extends HibernateDAO {

	public void deleteAllPopulations();
	
	public List<Population> getAllPopulations();

	public void savePopulation(Population population);
	
	public Map<String, String> getDateOfExperimentAndTypeByObservation(Integer oid) throws SQLException;
	
	
	
}
