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
 * experimental observation manager implementation
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.Experiment;
import uk.ac.ebi.phenotype.pojo.Observation;
import uk.ac.ebi.phenotype.pojo.Organisation;

public class ObservationDAOImpl extends HibernateDAOImpl implements ObservationDAO {

	/**
	 * Creates a new Hibernate project data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public ObservationDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Observation> getObservationsBySampleIdAndParameterStableId(
			int sampleId, String parameterStableId) {
		return getCurrentSession().createQuery("from Observation as o inner join o.parameter as param inner join o.sample as s where param.stableId = ? and s.id = ?")
				.setString(1, parameterStableId)
				.setInteger(2, sampleId).list();
	}

	@Transactional(readOnly = false)
	public void saveExperiment(Experiment experiment) {
		getCurrentSession().saveOrUpdate(experiment);
		
	}
	
	@Transactional(readOnly = false)
	public int deleteAllExperimentsByOrganisationAndDatasource(Organisation organisation, Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteExperimentByOrganisationAndDatasource")
				.setInteger("organisationID", organisation.getId())
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}

	@Transactional(readOnly = false)
	public int deleteAllTimeSeriesObservationsByOrganisationAndDatasource(Organisation organisation, Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllTimeSeriesObservationsByOrganisationAndDatasource")
				.setInteger("organisationID", organisation.getId())
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllCategoricalObservationsByOrganisationAndDatasource(Organisation organisation, Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllCategoricalObservationsByOrganisationAndDatasource")
				.setInteger("organisationID", organisation.getId())
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllUnidimensionalObservationsByOrganisationAndDatasource(Organisation organisation, Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllUnidimensionalObservationsByOrganisationAndDatasource")
				.setInteger("organisationID", organisation.getId())
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public void saveObservation(Observation observation) {
		getCurrentSession().saveOrUpdate(observation);
		
	}

}
