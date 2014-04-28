/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
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
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.TextObservation;
import uk.ac.ebi.phenotype.pojo.TimeSeriesObservation;
import uk.ac.ebi.phenotype.pojo.UnidimensionalObservation;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public class ObservationDAOImpl extends HibernateDAOImpl implements ObservationDAO {
	
	/**
	 * Creates a new Hibernate project data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public ObservationDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	public List<Integer> getAllOrganisationIdsWithObservations() throws SQLException {
	    List<Integer> ids = new ArrayList<Integer>();

		String query = "SELECT DISTINCT o.id"
				+ " FROM organisation o"
				+ " INNER JOIN biological_sample bs ON bs.organisation_id=o.id"
				+ " INNER JOIN observation ob ON ob.biological_sample_id=bs.id";

		try (PreparedStatement statement = getConnection().prepareStatement(query)){
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				
				ids.add(resultSet.getInt("id"));
			}
		}

		return ids; 			
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Organisation> getAllOrganisationsWithObservations() {
		return getCurrentSession().createQuery("select distinct org from Observation as o inner join o.sample as s inner join s.organisation org").list();
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Parameter> getAllParametersWithObservations() {
		return getCurrentSession().createQuery("select distinct param from Observation as o inner join o.parameter as param").list();
	}
	
	@Transactional(readOnly = true)
	public List<Integer> getAllParameterIdsWithObservationsByOrganisation(Organisation organisation) throws SQLException {
	    List<Integer> parameterIds = new ArrayList<Integer>();

		String query = "SELECT DISTINCT p.id"
		+ " FROM phenotype_parameter p"
		+ " INNER JOIN observation o ON o.parameter_id=p.id"
		+ " INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id"
		+ " WHERE bs.organisation_id=?"
		;

		try (PreparedStatement statement = getConnection().prepareStatement(query)){
	        statement.setInt(1, organisation.getId());
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				parameterIds.add(resultSet.getInt("id"));
			}
		}

		return parameterIds; 	
	}

	@Transactional(readOnly = true)
	public List<Integer> getAllCategoricalParameterIdsWithObservationsByOrganisation(Organisation organisation) throws SQLException {
	    List<Integer> parameterIds = new ArrayList<Integer>();

		String query = "SELECT DISTINCT p.id"
		+ " FROM phenotype_parameter p"
		+ " INNER JOIN observation o ON o.parameter_id=p.id"
		+ " INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id"
		+ " WHERE bs.organisation_id=?"
		+ " AND o.observation_type='categorical'"
		;

		try (PreparedStatement statement = getConnection().prepareStatement(query)){
	        statement.setInt(1, organisation.getId());
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				parameterIds.add(resultSet.getInt("id"));
			}
		}

		return parameterIds; 	
	}

	
	@Transactional(readOnly = true)
	public Observation getObservationById(Integer obsId) {
		return (Observation) getCurrentSession().createQuery("select distinct o from Observation as o where o.id=?")
			.setInteger(0, obsId)
			.uniqueResult();
	}


	public List<String> getAllStrainsByParameterIdOrganistion(Integer parameterId, Organisation organisation) throws SQLException {
		List<String> strains = new ArrayList<String>();

		String query = "SELECT DISTINCT strain_acc" 
		+ " FROM observation obs"
		+ " INNER JOIN biological_sample bs ON obs.biological_sample_id=bs.id"
		+ " INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id"
		+ " INNER JOIN biological_model bm ON bms.biological_model_id=bm.id"
		+ " INNER JOIN biological_model_strain strain ON strain.biological_model_id=bm.id"
		+ " WHERE parameter_id=?"
		+ " AND bs.organisation_id=?";

		try (PreparedStatement statement = getConnection().prepareStatement(query)){
	        statement.setInt(1, parameterId);
	        statement.setInt(2, organisation.getId());
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				strains.add(resultSet.getString("strain_acc"));
			}
		}

		return strains;
	}

	public List<String> getAllStrainsByParameterOrganistion(Parameter parameter, Organisation organisation) throws SQLException {
		List<String> strains = new ArrayList<String>();

		String query = "SELECT DISTINCT strain_acc" 
		+ " FROM observation obs"
		+ " INNER JOIN biological_sample bs ON obs.biological_sample_id=bs.id"
		+ " INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id"
		+ " INNER JOIN biological_model bm ON bms.biological_model_id=bm.id"
		+ " INNER JOIN biological_model_strain strain ON strain.biological_model_id=bm.id"
		+ " WHERE parameter_id=?"
		+ " AND bs.organisation_id=?";

		try (PreparedStatement statement = getConnection().prepareStatement(query)){
	        statement.setInt(1, parameter.getId());
	        statement.setInt(2, organisation.getId());
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				strains.add(resultSet.getString("strain_acc"));
			}
		}

		return strains;
	}

	public List<Integer> getAllObservationIdsByParameterGeneAccZygosityOrganisationStrainSex(Parameter parameter, String geneAcc, ZygosityType zygosity, Organisation organisation, String strain, SexType sex) throws SQLException {
	    List<Integer> ids = new ArrayList<Integer>();

		// Get all the experimental observation IDs
		String query = "SELECT DISTINCT o.id"
		+ " FROM observation o"
		+ " INNER JOIN biological_sample bs ON o.biological_sample_id=bs.id"
		+ " INNER JOIN live_sample ls ON ls.id=bs.id"
		+ " INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id"
		+ " INNER JOIN biological_model bm ON bms.biological_model_id=bm.id"
		+ " INNER JOIN biological_model_genomic_feature bmgf ON bm.id=bmgf.biological_model_id"
		+ " INNER JOIN biological_model_strain strain on strain.biological_model_id=bm.id"
		+ " WHERE o.parameter_id=?"
		+ " AND o.missing!=1"
		+ " AND bmgf.gf_acc=?"
		+ " AND bs.organisation_id=?"
		+ " AND ls.zygosity=?"
		+ " AND ls.sex=?"
		+ " AND strain.strain_acc=?"
		+ " AND bs.sample_group='experimental'"
		;

		try (PreparedStatement statement = getConnection().prepareStatement(query)){
	        statement.setInt(1, parameter.getId());
	        statement.setString(2, geneAcc);
	        statement.setInt(3, organisation.getId());
	        statement.setString(4, zygosity.name());
	        statement.setString(5, sex.name());
	        statement.setString(6, strain);
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				ids.add(resultSet.getInt("id"));
			}
		}

		// Add in the control observation IDs (do not take into account the zygosity of the control samples) 
		query = "SELECT DISTINCT o.id"
		+ " FROM observation o"
		+ " INNER JOIN biological_sample bs ON o.biological_sample_id=bs.id"
		+ " INNER JOIN live_sample ls ON ls.id=bs.id"
		+ " INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id"
		+ " INNER JOIN biological_model_strain strain on strain.biological_model_id=bms.biological_model_id"
		+ " INNER JOIN experiment_observation eo ON eo.observation_id=o.id"
		+ " INNER JOIN experiment e ON e.id=eo.experiment_id"
		+ " WHERE o.parameter_id=?"
		+ " AND o.missing!=1"
		+ " AND e.organisation_id=?"
		+ " AND ls.sex=?"
		+ " AND strain.strain_acc=?"
		+ " AND bs.sample_group='control'"
		;
		
		try (PreparedStatement statement = getConnection().prepareStatement(query)){
	        statement.setInt(1, parameter.getId());
	        statement.setInt(2, organisation.getId());
	        statement.setString(3, sex.name());
	        statement.setString(4, strain);
		    ResultSet resultSet = statement.executeQuery();
		    int count = 0;
			while (resultSet.next()) {
				ids.add(resultSet.getInt("id"));
				count++;
			}
			// If there is no control data, don't save this as a population
			if(count==0) ids.clear();
		}

		return ids; 	
	}
	
	@Transactional(readOnly = true)
	public List<String> getAllGeneAccessionIdsByParameterOrganisationStrainZygositySex(Parameter parameter, Organisation organisation, String strain, ZygosityType zygosity, SexType sex) throws SQLException {
	    List<String> genes = new ArrayList<String>();

		String query = "SELECT DISTINCT bmgf.gf_acc"
		+ " FROM observation o"
		+ " INNER JOIN biological_sample bs ON o.biological_sample_id=bs.id"
		+ " INNER JOIN live_sample ls ON ls.id=bs.id"
		+ " INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id"
		+ " INNER JOIN biological_model bm ON bms.biological_model_id=bm.id"
		+ " INNER JOIN biological_model_genomic_feature bmgf ON bm.id=bmgf.biological_model_id"
		+ " INNER JOIN biological_model_strain strain on strain.biological_model_id=bm.id"
		+ " WHERE o.parameter_id=?"
		+ " AND bs.organisation_id=?"
		+ " AND strain.strain_acc=?"
		+ " AND ls.zygosity=?"
		+ " AND ls.sex=?"
		;

		try (PreparedStatement statement = getConnection().prepareStatement(query)){
	        statement.setInt(1, parameter.getId());
	        statement.setInt(2, organisation.getId());
	        statement.setString(3, strain);
	        statement.setString(4, zygosity.name());
	        statement.setString(5, sex.name());
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				genes.add(resultSet.getString("gf_acc"));
			}
		}

		return genes; 	
	}


	public List<String> getAllGeneAccessionIdsByParameterIdOrganisationStrainZygositySex(Integer parameterId, Organisation organisation, String strain, ZygosityType zygosity, SexType sex) throws SQLException {
	    List<String> genes = new ArrayList<String>();

		String query = "SELECT DISTINCT bmgf.gf_acc"
		+ " FROM observation o"
		+ " INNER JOIN biological_sample bs ON o.biological_sample_id=bs.id"
		+ " INNER JOIN live_sample ls ON ls.id=bs.id"
		+ " INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id"
		+ " INNER JOIN biological_model bm ON bms.biological_model_id=bm.id"
		+ " INNER JOIN biological_model_genomic_feature bmgf ON bm.id=bmgf.biological_model_id"
		+ " INNER JOIN biological_model_strain strain on strain.biological_model_id=bm.id"
		+ " WHERE o.parameter_id=?"
		+ " AND bs.organisation_id=?"
		+ " AND strain.strain_acc=?"
		+ " AND ls.zygosity=?"
		+ " AND ls.sex=?"
		;

		try (PreparedStatement statement = getConnection().prepareStatement(query)){
	        statement.setInt(1, parameterId);
	        statement.setInt(2, organisation.getId());
	        statement.setString(3, strain);
	        statement.setString(4, zygosity.name());
	        statement.setString(5, sex.name());

	        ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				genes.add(resultSet.getString("gf_acc"));
			}
		}

		return genes;		
	}

	
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Observation> getAllObservationsByParameter(Parameter parameter) {
		return getCurrentSession().createQuery("select distinct o from Observation as o where o.parameterId=?")
			.setInteger(1, parameter.getId())
			.list();
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
			Datasource datasource,
			Experiment experiment, String parameterStatus) {
		return createObservation(observationType, simpleValue, null, null, parameter, sample, datasource, experiment, parameterStatus);
	}
	
	@Transactional(readOnly = false)
	public Observation createObservation(
			ObservationType observationType, 
			String firstDimensionValue, 
			String secondDimensionValue,
			String secondDimensionUnit,
			Parameter parameter, 
			BiologicalSample sample, 
			Datasource datasource,
			Experiment experiment, String parameterStatus) {
		
		Observation obs = null;
//		if (observationType == ObservationType.image_record) {
//
////			logger.debug("Series :" + secondDimensionValue + "\t" + firstDimensionValue);
//			
//			MediaObservation imgObservation = new MediaObservation();
//			
////			if (firstDimensionValue == null || firstDimensionValue.equals("null") || firstDimensionValue.equals("")) {
////				imgObservation.setMissingFlag(true);
////			} else {
////				imgObservation.setDataPoint(Float.parseFloat(firstDimensionValue));
////			}
//			
//			Date dateOfExperiment = (experiment != null) ? experiment.getDateOfExperiment() : null;
//			//imgObservation.setTimePoint(secondDimensionValue, dateOfExperiment, secondDimensionUnit);
//			imgObservation.setDatasource(datasource);
//			imgObservation.setExperiment(experiment);
//			imgObservation.setParameter(parameter);
//			imgObservation.setSample(sample);
//			imgObservation.setType(observationType);
//
//			obs = imgObservation;
//			
//		} 
		if (observationType == ObservationType.time_series) {

//			logger.debug("Series :" + secondDimensionValue + "\t" + firstDimensionValue);
			
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
		} else if (observationType == ObservationType.text) {

			 /* Unidimensional information */								 

			logger.debug("Text :" + firstDimensionValue);
			TextObservation textObservation = new TextObservation();
		
			textObservation.setText(firstDimensionValue);
			textObservation.setDatasource(datasource);
			textObservation.setExperiment(experiment);
			textObservation.setParameter(parameter);
			textObservation.setSample(sample);
			textObservation.setType(observationType);

			obs = textObservation;
		}
		
		obs.setParameterStableId(parameter.getStableId());
		//TODO set missing -> ignore flag
		obs.setParameterStatus(parameterStatus);
		if(parameterStatus!=null) {
		obs.setMissingFlag(true);
		}
		return obs;
	}

	@Transactional(readOnly = false)
	public Observation createTimeSeriesObservationWithOriginalDate(
			ObservationType observationType, 
			String firstDimensionValue, 
			String secondDimensionValue,
			String actualTimepoint,
			String secondDimensionUnit,
			Parameter parameter, 
			BiologicalSample sample, 
			Datasource datasource,
			Experiment experiment, String parameterStatus) {
		
		//logger.debug("Series :" + secondDimensionValue + "\t" + firstDimensionValue);
		
		TimeSeriesObservation obs = new TimeSeriesObservation();
		
		if (firstDimensionValue == null || firstDimensionValue.equals("null") || firstDimensionValue.equals("")) {
			obs.setMissingFlag(true);
		} else {
			obs.setDataPoint(Float.parseFloat(firstDimensionValue));
		}
		
		Date actualTimePoint = (experiment != null) ? experiment.getDateOfExperiment() : null;
		
		// If the center supplied an actual date time, 
		// use that as the time_point
		if (actualTimepoint.contains("-")) {
			DateFormat inputDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				actualTimePoint = inputDateFormatter.parse(actualTimepoint);
			} catch (ParseException e) {
				actualTimePoint = (experiment != null) ? experiment.getDateOfExperiment() : null;
			}
		}

		obs.setTimePoint(secondDimensionValue, actualTimePoint, secondDimensionUnit);
		obs.setDatasource(datasource);
		obs.setExperiment(experiment);
		obs.setParameter(parameter);
		obs.setSample(sample);
		obs.setType(observationType);
		obs.setParameterStableId(parameter.getStableId());
		obs.setParameterStatus(parameterStatus);
		
		return obs;
	}

}
