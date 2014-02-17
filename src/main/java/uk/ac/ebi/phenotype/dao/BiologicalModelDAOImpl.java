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
 * Biological model manager implementation.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.BiologicalSample;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.LiveSample;
import uk.ac.ebi.phenotype.pojo.Organisation;

public class BiologicalModelDAOImpl extends HibernateDAOImpl implements BiologicalModelDAO {

	/**
	 * Creates a new Hibernate project data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public BiologicalModelDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional(readOnly = false)
	public void saveBiologicalSample(BiologicalSample sample) {
		getCurrentSession().saveOrUpdate(sample);
	}

	@Transactional(readOnly = false)
	public void saveLiveSample(LiveSample sample) {
		getCurrentSession().saveOrUpdate(sample);
	}
	
	@Transactional(readOnly = false)
	public int  deleteAllLiveSamplesByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteLiveSamples")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int  deleteAllLiveSamplesWithoutModelsByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteLiveSamplesWithoutModels")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllBiologicalSamplesByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteBiologicalSamples")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = true)
	public List<BiologicalModel> getAllBiologicalModelsByDatasourceId(int databaseId) {

		return (List<BiologicalModel>) getCurrentSession().createQuery("select distinct m from BiologicalModel as m inner join m.datasource as d where d.id = ?").setInteger(0, databaseId).list();
		
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<BiologicalModel> getAllBiologicalModelsByAccession(String accession) {
		return getCurrentSession()
			.createQuery("from BiologicalModel as m join m.genomicFeatures as gf where gf.id.accession = ?")
			.setString(0, accession)
			.list();
	}


	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")	
	public List<LiveSample> getAllLiveSamplesByDatasourceId(int databaseId) {
		return getCurrentSession().createQuery("from LiveSample as l inner join l.datasource as d where d.id = ?").setInteger(0, databaseId).list();
	}
	

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<LiveSample> getAllLiveSamples() {
		List<LiveSample> liveSamples = new LinkedList<LiveSample>();
		List<Object> l = getCurrentSession().createQuery("SELECT live FROM LiveSample AS live").list();
		for (Object o: l) {
			liveSamples.add((LiveSample) o);
		}
		return liveSamples;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<LiveSample> getAllLiveSampleByOrganisationAndDatasource(Organisation organisation, Datasource datasource) {
		List<LiveSample> liveSamples = new LinkedList<LiveSample>();
		List<Object> l = getCurrentSession().createQuery("SELECT live FROM LiveSample AS live INNER JOIN live.datasource AS d INNER JOIN live.organisation AS o WHERE d.id = ? AND o.id = ?")
				.setInteger(0, datasource.getId())
				.setInteger(1, organisation.getId())
				.list();
		for (Object o: l) {
			liveSamples.add((LiveSample) o);
		}
		return liveSamples;
	}

	@Transactional(readOnly = true)
	public LiveSample getAllLiveSampleBySampleId(String sampleId){
		return (LiveSample)getCurrentSession().createQuery("FROM LiveSample WHERE stableId = ?")
				.setString(0, sampleId)
				.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public BiologicalModel getBiologicalModelById(int modelId) {
		return (BiologicalModel) getCurrentSession().createQuery("from BiologicalModel as m where m.id = ?").setInteger(0, modelId).uniqueResult();
	}

	@Transactional(readOnly = true)
	public BiologicalSample getBiologicalSampleById(int sampleId) {
		return (BiologicalSample) getCurrentSession().createQuery("from BiologicalSample as s where s.id = ?").setInteger(0, sampleId).uniqueResult();
	}

	@Transactional(readOnly = false)
	public void saveBiologicalModel(BiologicalModel model) {
		getCurrentSession().saveOrUpdate(model);
		getCurrentSession().flush();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllBiologicalModelsByDatasource(Datasource datasource) {
		// the following code is not efficient
		//String hql = "delete BiologicalModel as ls where ls.datasource.id = :id";
		//getCurrentSession().createQuery(hql).setInteger("id", datasource.getId()).executeUpdate();
		
		// delete associated gene first
		Query query = getCurrentSession().getNamedQuery("deleteBiologicalModelGenomicFeatures")
				.setInteger("dbID", datasource.getId());
		query.executeUpdate();
		
		// needs to be replaced by native SQL queries
		query = getCurrentSession().getNamedQuery("deleteBiologicalModels")
				.setInteger("dbID", datasource.getId());
		int count = query.executeUpdate();
		return count;
	}

	@Transactional(readOnly = false)
	public void deleteAllBiologicalModelsAndRelatedDataByDatasourceOrganisation(Datasource ds, Organisation o) {
		Query query = getCurrentSession().getNamedQuery("deleteBiologicalModelAndRelatedData")
				.setInteger("dbID", ds.getId())
				.setInteger("orgID", o.getId());
		query.executeUpdate();
		getCurrentSession().flush();
	}

}
