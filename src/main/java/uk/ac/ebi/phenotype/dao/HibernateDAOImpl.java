/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.dao;

/**
 * 
 * Parent data access manager implementation.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.annotation.Transactional;

/*
* Implementation of the HibernateDAO interface
*/

@Transactional
public class HibernateDAOImpl implements HibernateDAO {
	
	protected Logger logger = Logger.getLogger(HibernateDAOImpl.class);
	
	/**
	 * The session factory used to query the database
	 */
	protected SessionFactory sessionFactory;
	
	/**
	 * Method to get a connection from the session factory
	 * This is deprecated and should be replaced!
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Connection getConnection() {
		Session session = SessionFactoryUtils.doGetSession(sessionFactory, false);
		Connection connection = session.connection(); 
		return connection;
	}
	
	/**
	 * Method to get a session from the session factory
	 * @return
	 */
	public Session getSession() {
		Session session = SessionFactoryUtils.doGetSession(sessionFactory, false);
		return session;
	}
	
	/**
	 * @return Returns the sessionFactory.
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("rawtypes")
	public Collection executeNativeQuery(String sql) throws SQLException {
		
		LinkedList<Object> results = new LinkedList<Object>();
		
		Connection connection = getConnection();
		Statement stmt = connection.createStatement();
		ResultSet rs = null;        
		rs = stmt.executeQuery(sql);
		while (rs.next()) {
		  results.add(rs.getString(1));
		}
		
		rs.close();
		
		return results;
	}
	
	/**
	 * Returns the session associated with the ongoing reward transaction.
	 * @return the transactional session
	 */
	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}	

	public void flushAndClearSession() {
		getCurrentSession().flush();
		getCurrentSession().clear();
	}
	
	protected void finalize() {
		
		getCurrentSession().flush();
		getCurrentSession().clear();
		getCurrentSession().close();
		
	}
	
	
	
}
