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
 * Ontology term and controlled vocabulary data access manager implementation.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;

public class OntologyTermDAOImpl extends HibernateDAOImpl implements OntologyTermDAO {

	/**
	 * Creates a new Hibernate ontology term data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public OntologyTermDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<OntologyTerm> getAllOntologyTerms() {
		return getCurrentSession().createQuery("from OntologyTerm").list();
	}

	@Transactional(readOnly = true)
	public OntologyTerm getOntologyTermByName(String name) {
		return (OntologyTerm) getCurrentSession().createQuery("from OntologyTerm as o where o.name= ?").setString(0, name).uniqueResult();
	}

	@Transactional(readOnly = true)
	public OntologyTerm getOntologyTermBySynonym(String name) {
		return (OntologyTerm) getCurrentSession().createQuery("from OntologyTerm as o inner join o.synonyms s where s.symbol = ?").setString(0, name).uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public OntologyTerm getOntologyTermByNameAndDatabaseId(String name, int databaseId) {
		return (OntologyTerm) getCurrentSession().createQuery("from OntologyTerm as o where o.name= ? and o.id.databaseId = ?").setString(0, name).setInteger(1, databaseId).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public HashMap<String, OntologyTerm> getAllTerms(int databaseId) {
		HashMap<String, OntologyTerm> map = new HashMap<String, OntologyTerm>();
		List<OntologyTerm> result = getCurrentSession().createQuery("from OntologyTerm as o where o.id.databaseId = ?").setInteger(0, databaseId).list();
		for (OntologyTerm t: result) {
			map.put(t.getName(), t);
		}
		return map;
	}
	
	@Override
	public OntologyTerm getOntologyTermByAccession(String accession) {
		return (OntologyTerm) getCurrentSession().createQuery("from OntologyTerm as ot where ot.id.accession = ?").setString(0, accession).uniqueResult();
	}
	
	@Override
	public OntologyTerm getOntologyTermByAccessionAndDatabaseId(
			String accession, int databaseId) {
		return (OntologyTerm) getCurrentSession().createQuery("from OntologyTerm as ot where ot.id.accession = ? and ot.id.databaseId = ?").setString(0, accession).setInteger(1, databaseId).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OntologyTerm> getAllOntologyTermsByDatabaseId(int databaseId) {
		return getCurrentSession().createQuery("from OntologyTerm as ot where ot.id.databaseId = ?").setInteger(0, databaseId).list();
	}

	@Transactional(readOnly = false)
	public int deleteAllTerms(String shortName) {
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		// get the database id
		Datasource d = (Datasource) session.createQuery("from Datasource as d where d.shortName= ?").setString(0, shortName).uniqueResult();
		
		// execute the delete query
		String hqlDelete = "delete OntologyTerm as ot where ot.id.databaseId = ?";
		int deletedEntities = session.createQuery( hqlDelete ).setInteger(0, d.getId()).executeUpdate();
		tx.commit();
		session.close();
		return deletedEntities;
	}

	@Transactional(readOnly = false)
	public int batchInsertion(Collection<OntologyTerm> ontologyTerms) {
		int c = 0;
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		for (OntologyTerm term: ontologyTerms) {
			
		  /*  for (Synonym synonym: term.getSynonyms()) {
		    	session.saveOrUpdate(synonym);
		    }
		    */
		    session.save(term);
		    
		    if ( c % 20 == 0 ) { //20, same as the JDBC batch size
		        //flush a batch of inserts and release memory:
		        session.flush();
		        session.clear();
		    }
		    c++;
		}
		
		tx.commit();
		session.close();
		return c;
	}

}
