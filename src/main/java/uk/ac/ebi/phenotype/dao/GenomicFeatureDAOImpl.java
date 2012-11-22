/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
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
 * External data source access manager implementation.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.bean.GenomicFeatureBean;
import uk.ac.ebi.phenotype.pojo.DatasourceEntityId;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.Synonym;



public class GenomicFeatureDAOImpl extends HibernateDAOImpl implements
		GenomicFeatureDAO {

	/**
	 * Creates a new Hibernate GenomicFeature data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public GenomicFeatureDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<GenomicFeature> getAllGenomicFeatures() {
		return getCurrentSession().createQuery("from GenomicFeature").list();
	}

	@Transactional(readOnly = true)
	public GenomicFeature getGenomicFeatureByName(String name) {
		return (GenomicFeature) getCurrentSession().createQuery("from GenomicFeature as g where g.name= ?").setString(0, name).uniqueResult();
	}

	@Transactional(readOnly = true)
	public GenomicFeature getGenomicFeatureByBiotype(String biotype) {
		return (GenomicFeature) getCurrentSession().createQuery("from GenomicFeature as g where g.biotype.name= ?").setString(0, biotype).uniqueResult();
	}

	@Transactional(readOnly = true)
	public Map<String, GenomicFeature> getGenomicFeaturesByBiotypeAndNoSubtype(String biotype) {
		HashMap<String, GenomicFeature> map = new HashMap<String, GenomicFeature>();
		List<GenomicFeature> result = getCurrentSession().createQuery("from GenomicFeature as g where g.biotype.name= ? and g.subtype = null").setString(0, biotype).list();
		for (GenomicFeature t: result) {
			map.put(t.getId().getAccession(), t);
		}
		return map;
	}
	
	@Transactional(readOnly = true)
	public GenomicFeature getGenomicFeatureByAccession(String accession) {
		return (GenomicFeature) getCurrentSession().createQuery("from GenomicFeature as g where g.id.accession= ?").setString(0, accession).uniqueResult();
	}

	@Transactional(readOnly = true)
	public GenomicFeature getGenomicFeatureByAccessionAndDbId(String accession,
			int dbId) {
		return (GenomicFeature) getCurrentSession().createQuery("from GenomicFeature as g where g.id.accession= ? and g.id.databaseId= ?").setString(0, accession).setInteger(1, dbId).uniqueResult();
	}

	@Transactional(readOnly = true)
	public GenomicFeature getGenomicFeatureBySymbol(String symbol) {
		return (GenomicFeature) getCurrentSession().createQuery("from GenomicFeature as g where g.symbol= ?").setString(0, symbol).uniqueResult();
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<GenomicFeatureBean> getAllGenomicFeatureBeans() {
		
		Criteria crit = getCurrentSession().createCriteria(GenomicFeature.class);
		  ProjectionList proList = Projections.projectionList();
		  proList.add(Projections.property("id"));
		  proList.add(Projections.property("symbol"));
		  proList.add(Projections.property("name"));
		  crit.setProjection(proList);
		  crit.setMaxResults(10);
		  ScrollableResults r = crit.scroll();
		  List<GenomicFeatureBean> l = new LinkedList<GenomicFeatureBean>();
		  while (r.next()) {
			  Object[] a = r.get();
			  GenomicFeatureBean b = new GenomicFeatureBean();
			  b.setAccession(((DatasourceEntityId)a[0]).getAccession());
			  b.setSymbol((String)a[1]);
			  b.setName((String)a[2]);
			  l.add(b);
		  }
		return l;
		
	}

	@Transactional(readOnly = false)
	public void saveGenomicFeature(GenomicFeature feature) {
		getCurrentSession().saveOrUpdate(feature);
		
	}

	@Transactional(readOnly = false)
	public int deleteAllGenomicFeatures() {
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		// Embeddable collections are not deleted with parent when
		// doing a batch delete.  Must remove them manually.
		// Since Synonyms are not actual hibernate entities, drop to SQL
		// to empty the table.
		String hqlSynonymDelete = "TRUNCATE synonym";
		session.createSQLQuery( hqlSynonymDelete ).executeUpdate();

		String hqlDelete = "delete GenomicFeature";
		int deletedEntities = session.createQuery( hqlDelete ).executeUpdate();
		tx.commit();
		session.close();
		return deletedEntities;
	}
	
	@Transactional(readOnly = false)
	public int batchInsertion(Collection<GenomicFeature> genomicFeatures) {
		
		int c = 0;
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		for (GenomicFeature feature: genomicFeatures) {
			if (feature.getBiotype() == null) {
				System.out.println("No biotype for " + feature.getId().getAccession());
			}
		    session.save(feature);
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
