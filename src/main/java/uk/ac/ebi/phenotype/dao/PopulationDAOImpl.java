package uk.ac.ebi.phenotype.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Population;

public class PopulationDAOImpl extends HibernateDAOImpl implements PopulationDAO{

	/**
	 * Creates a new Hibernate project data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public PopulationDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional(readOnly = false)
	public void deleteAllPopulations() {
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		// Embeddable collections are not deleted with parent when
		// doing a batch delete.  Must remove them manually.
		// Since Synonyms are not actual hibernate entities, drop to SQL
		// to empty the table.
		String query = "TRUNCATE population";
		session.createSQLQuery( query ).executeUpdate();

		query = "TRUNCATE observation_population";
		session.createSQLQuery( query ).executeUpdate();

		tx.commit();
		session.close();
		
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Population> getAllPopulations() {
		return getCurrentSession().createQuery("from Population").list();
	}
	
	@Transactional(readOnly = false)
	public void savePopulation(Population population) {
		getCurrentSession().saveOrUpdate(population);
	}

	@Override
	public Map<String, String> getDateOfExperimentAndTypeByObservation(Integer oid) throws SQLException {
		Map<String,String> date= new HashMap<String,String>();

		String query = "SELECT e.date_of_experiment, bs.sample_group"
				+ " FROM experiment e"
				+ " INNER JOIN experiment_observation eo ON e.id=eo.experiment_id"
				+ " INNER JOIN observation o ON eo.observation_id=o.id"
				+ " INNER JOIN biological_sample bs ON o.biological_sample_id=bs.id"
				+ " WHERE eo.observation_id=?";

		try (PreparedStatement statement = getConnection().prepareStatement(query)){
	        statement.setInt(1, oid);
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				date.put(resultSet.getString("date_of_experiment"), resultSet.getString("sample_group"));
			}
		}

		return date;
	}



	
}
