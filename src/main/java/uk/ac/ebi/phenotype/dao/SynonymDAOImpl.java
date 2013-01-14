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
 * Synonym data access manager implementation.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Synonym;



public class SynonymDAOImpl extends HibernateDAOImpl implements SynonymDAO {

	/**
	 * Creates a new Hibernate coordinate system data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public SynonymDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Synonym> getAllSynonyms() {
		return getCurrentSession().createQuery("from Synonym").list();
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Synonym> getAllSynonymsByAccessionAndDatabaseId(String accession,
			int databaseId) {
		return getCurrentSession().createQuery("from Synonym as s where s.fk.databaseId = ? and s.fk.accession = ? order by s.symbol").setInteger(0, databaseId).setString(1, accession).list();
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Synonym> getAllSynonymsByDatabaseId(int databaseId) {
		return getCurrentSession().createQuery("from Synonym as s where s.fk.databaseId = ?").setInteger(0, databaseId).list();
	}

}
