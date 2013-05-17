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

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.BiologicalSample;
import uk.ac.ebi.phenotype.pojo.CategoricalObservation;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.Experiment;
import uk.ac.ebi.phenotype.pojo.MetaDataObservation;
import uk.ac.ebi.phenotype.pojo.Observation;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.TimeSeriesObservation;
import uk.ac.ebi.phenotype.pojo.UnidimensionalObservation;

@Service
public class ObservationDAOImpl extends HibernateDAOImpl implements ObservationDAO {
	
	@Autowired
	SessionFactory sessionFactory;
	
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
	public int deleteAllMetadataObservationsByOrganisationAndDatasource(Organisation organisation, Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllMetadataObservationsByOrganisationAndDatasource")
				.setInteger("organisationID", organisation.getId())
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllExperimentsByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteExperimentByDatasource")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}

	@Transactional(readOnly = false)
	public int deleteAllExperimentsWithoutObservationByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllExperimentWithoutObservationByDatasource")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	
	@Transactional(readOnly = false)
	public int deleteAllTimeSeriesObservationsByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllTimeSeriesObservationsByDatasource")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllCategoricalObservationsByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllCategoricalObservationsByDatasource")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllUnidimensionalObservationsByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllUnidimensionalObservationsByDatasource")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllMetadataObservationsByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllMetadataObservationsByDatasource")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllMetadataObservationsWithoutExperimentByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllMetadataObservationsWithoutExperimentByDatasource")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllTimeSeriesObservationsWithoutExperimentByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllTimeSeriesObservationsWithoutExperimentByDatasource")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllCategoricalObservationsWithoutExperimentByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllCategoricalObservationsWithoutExperimentByDatasource")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
	
	@Transactional(readOnly = false)
	public int deleteAllUnidimensionalObservationsWithoutExperimentByDatasource(Datasource datasource) {
		Query query = getCurrentSession().getNamedQuery("deleteAllUnidimensionalObservationsWithoutExperimentByDatasource")
				.setInteger("dbID", datasource.getId());
		return query.executeUpdate();
	}
		
	@Transactional(readOnly = false)
	public void saveObservation(Observation observation) {
		getCurrentSession().saveOrUpdate(observation);
		
	}
	
	@Transactional(readOnly = false)
	public Observation createSimpleObservation(
			ObservationType observationType, 
			String simpleValue, 
			Parameter parameter, 
			BiologicalSample sample, 
			int populationId,
			Datasource datasource,
			Experiment experiment) {
		return createObservation(observationType, simpleValue, null, null, parameter, sample, populationId, datasource, experiment);
	}
	
	@Transactional(readOnly = false)
	public Observation createObservation(
			ObservationType observationType, 
			String firstDimensionValue, 
			String secondDimensionValue,
			String secondDimensionUnit,
			Parameter parameter, 
			BiologicalSample sample, 
			int populationId,
			Datasource datasource,
			Experiment experiment) {
		
		Observation obs = null;
		
		if (observationType == ObservationType.time_series) {

			logger.debug("Series :" + secondDimensionValue + "\t" + firstDimensionValue);
			
			TimeSeriesObservation seriesObservation = new TimeSeriesObservation();
			
			if (firstDimensionValue == null || firstDimensionValue.equals("null") || firstDimensionValue.equals("")) {
				seriesObservation.setMissingFlag(true);
			} else {
				seriesObservation.setDataPoint(Float.parseFloat(firstDimensionValue));
			}
			
			Date dateOfExperiment = (experiment != null) ? experiment.getDateOfExperiment() : null;
			seriesObservation.setTimePoint(secondDimensionValue, dateOfExperiment, secondDimensionUnit);
			seriesObservation.setDatasource(datasource);
			seriesObservation.setExperiment(experiment);
			seriesObservation.setParameter(parameter);
			seriesObservation.setSample(sample);
			seriesObservation.setType(observationType);

			obs = seriesObservation;
			
		} else if (observationType == ObservationType.metadata) {
			
			logger.debug("Metadata: " + firstDimensionValue);
			MetaDataObservation metaDataObservation = new MetaDataObservation();
			metaDataObservation.setKey(secondDimensionValue);
			metaDataObservation.setValue(firstDimensionValue);
			metaDataObservation.setDatasource(datasource);
			metaDataObservation.setExperiment(experiment);
			metaDataObservation.setParameter(parameter);
			metaDataObservation.setSample(sample);
			metaDataObservation.setType(observationType);
			// we do our best for missing flag
			if (firstDimensionValue.equals("null")) {
				metaDataObservation.setMissingFlag(true);
			}
			
			obs = metaDataObservation;	
			
		} else if (observationType == ObservationType.categorical) {
	
			 /* Categorical information */
			 
			logger.debug("Categorical: " + firstDimensionValue);
			CategoricalObservation categoricalObservation = new CategoricalObservation();
			categoricalObservation.setCategory(firstDimensionValue);
			categoricalObservation.setDatasource(datasource);
			categoricalObservation.setExperiment(experiment);
			categoricalObservation.setParameter(parameter);
			categoricalObservation.setSample(sample);
			categoricalObservation.setType(observationType);
			// we do our best for missing flag
			if (firstDimensionValue.equals("null")) {
				categoricalObservation.setMissingFlag(true);
			}
			
			obs = categoricalObservation;

		} else if (observationType == ObservationType.unidimensional) {

			 /* Unidimensional information */								 

			logger.debug("Unidimensional :" + firstDimensionValue);
			UnidimensionalObservation unidimensionalObservation = new UnidimensionalObservation();
			
			// parse the floating point value
			try {
				unidimensionalObservation.setDataPoint(Float.parseFloat(firstDimensionValue));
			} catch (NumberFormatException ex) {
				// can be "null" can be "/" and many others!
				logger.info(ex.getMessage());
				unidimensionalObservation.setMissingFlag(true);
			}

			unidimensionalObservation.setDatasource(datasource);
			unidimensionalObservation.setExperiment(experiment);
			unidimensionalObservation.setParameter(parameter);
			unidimensionalObservation.setSample(sample);
			unidimensionalObservation.setType(observationType);

			obs = unidimensionalObservation;
		}
		
		obs.setPopulationId(populationId);
		
		return obs;
	}

}
