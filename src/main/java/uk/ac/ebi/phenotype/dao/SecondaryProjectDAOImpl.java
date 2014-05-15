/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;

/**
 *
 * @author jwarren
 */
class SecondaryProjectDAOImpl extends HibernateDAOImpl implements SecondaryProjectDAO {
    
    /**
	 * Creates a new Hibernate project data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public SecondaryProjectDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
        
        
    @Override
    public List<String> getAccessionsBySecondaryProjectId(int projectId) throws SQLException{
        ArrayList<String> accessions=new ArrayList<>();
              
		String query = "select * from genes_secondary_project where secondary_project_id="+projectId;

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String result=resultSet.getString(1);
                                accessions.add(result);
			}
		}
		return accessions;
    }
}
