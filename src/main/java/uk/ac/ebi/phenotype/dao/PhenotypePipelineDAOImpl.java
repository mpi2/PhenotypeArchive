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
 * 
 * Phenotype pipeline data access manager implementation.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.ParameterIncrement;
import uk.ac.ebi.phenotype.pojo.ParameterOntologyAnnotation;
import uk.ac.ebi.phenotype.pojo.ParameterOption;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.stats.MouseDataPoint;



public class PhenotypePipelineDAOImpl extends HibernateDAOImpl implements PhenotypePipelineDAO {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Creates a new Hibernate pipeline data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public PhenotypePipelineDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
		
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Pipeline> getAllPhenotypePipelines() {
		List<Pipeline> pipelines = getCurrentSession().createQuery("from Pipeline").list();
		return pipelines;
	}

	@Transactional(readOnly = true)
	public Pipeline getPhenotypePipelineByStableId(String stableId) {
		return (Pipeline) getCurrentSession().createQuery("from Pipeline as p where p.stableId = ?").setString(0, stableId).uniqueResult();
	}

	
	@Transactional(readOnly = true)
	public Pipeline getPhenotypePipelineById(Integer id) {
		return (Pipeline) getCurrentSession().get(Pipeline.class, id);
	}

	@Transactional(readOnly = true)
	public Pipeline getPhenotypePipelineByStableIdAndVersion(String stableId, int majorVersion, int minorVersion) {
		Object o = getCurrentSession().createQuery("from Pipeline as p where p.stableId = ? and p.majorVersion = ? and p.minorVersion = ?")
				.setString(0, stableId)
				.setInteger(1, majorVersion)
				.setInteger(2, minorVersion)
				.uniqueResult();
		return (o == null) ? null : (Pipeline) o;
	}
	
	
	@Transactional(readOnly = true)
	public Procedure getProcedureByStableIdAndVersion(String stableId, int majorVersion, int minorVersion) {
		return (Procedure) getCurrentSession().createQuery("from Procedure as p where p.stableId = ? and p.majorVersion = ? and p.minorVersion = ?")
				.setString(0, stableId)
				.setInteger(1, majorVersion)
				.setInteger(2, minorVersion)
				.uniqueResult();
	}
	
	public Procedure getProcedureByStableId(String stableId) {
		return (Procedure) getCurrentSession().createQuery("from Procedure as p where p.stableId = ?")
				.setString(0, stableId)
				.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public Parameter getParameterByStableIdAndVersion(String stableId, int majorVersion, int minorVersion) {
		return (Parameter) getCurrentSession().createQuery("from Parameter as p where p.stableId = ? and p.majorVersion = ? and p.minorVersion = ?")
				.setString(0, stableId)
				.setInteger(1, majorVersion)
				.setInteger(2, minorVersion)
				.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public Parameter getParameterByStableId(String stableId) {
		return (Parameter) getCurrentSession().createQuery("from Parameter as p where p.stableId = ?")
				.setString(0, stableId)
				.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Parameter> getProcedureMetaDataParametersByStableIdAndVersion(String stableId, int majorVersion, int minorVersion) {
		List<Parameter> parameters =   getCurrentSession().createQuery("select param from Parameter as param inner join param.procedure as proc where proc.stableId = ? and param.majorVersion = ? and param.minorVersion = ? and param.metaDataFlag = true")
				.setString(0, stableId)
				.setInteger(1, majorVersion)
				.setInteger(2, minorVersion)
				.list();
		return parameters;
	}
	
	@Transactional(readOnly = false)
	public void save(Object object) {
		getCurrentSession().save(object);
		
	}
	
	@Transactional(readOnly = false)
	public void update(Object object) {
		getCurrentSession().saveOrUpdate(object);
	}
	
	@Transactional(readOnly = false)
	public void savePipeline(Pipeline pipeline) {
		getCurrentSession().saveOrUpdate(pipeline);
		
	}
	
	@Transactional(readOnly = false)
	public void saveProcedure(Procedure procedure) {
		getCurrentSession().saveOrUpdate(procedure);
	}
	
	@Transactional(readOnly = false)
	public void saveParameter(Parameter parameter) {
		getCurrentSession().saveOrUpdate(parameter);
	}

	@Transactional(readOnly = false)
	public void saveParameterOption(ParameterOption parameterOption) {
		getCurrentSession().saveOrUpdate(parameterOption);
		
	}

	@Transactional(readOnly = false)
	public void saveParameterIncrement(ParameterIncrement parameterIncrement) {
		getCurrentSession().saveOrUpdate(parameterIncrement);
		
	}

	@Transactional(readOnly = false)
	public void saveParameterOntologyAnnotation(
			ParameterOntologyAnnotation parameterOntologyAnnotation) {
		getCurrentSession().saveOrUpdate(parameterOntologyAnnotation);
		
	}
	
	@Transactional(readOnly = false)
	public void deleteAllPipelinesByDatasource(Datasource datasource) {
		
		Query query = getCurrentSession().getNamedQuery("deleteAllPipelinesByDatasource")
				.setInteger("dbID", datasource.getId());
		query.executeUpdate();
	}
	
	/**
	 * Helper method to fetch the actual parameter pojo when provided a 
	 * database id.
	 */
	@Transactional(readOnly = true)
	public Parameter getParameterById(Integer parameterId) {
		return (Parameter) getCurrentSession()
				.createQuery("SELECT p FROM Parameter p WHERE p.id=?")
				.setInteger(0, parameterId)
				.uniqueResult();
	}

	/**
	 * getProcedureByOntologyTerm returns the sorted set of procedures
	 * associated to a passed in ontology term 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Set<Procedure> getProceduresByOntologyTerm(OntologyTerm term) {
		if (term == null) return null;
		return (Set<Procedure>) new HashSet<Procedure>(getCurrentSession()
				.createQuery("SELECT proc FROM Procedure proc INNER JOIN proc.parameters as param INNER JOIN param.annotations as annotations WHERE annotations.ontologyTerm.id.databaseId=? AND annotations.ontologyTerm.id.accession=?")
				.setInteger(0, term.getId().getDatabaseId())
				.setString(1, term.getId().getAccession())
				.list());
	}

	/**
	 * Return all categorical parameters for which we have data loaded
	 * 
	 * @exception SQLException When a database error occurrs
	 */
	//TODO: REMOVE THIS METHOD AFTER REFACTOR
	@Transactional(readOnly = true)
	public Set<Parameter> getAllCategoricalParametersForProcessing() throws SQLException {
		Set<Parameter> parameters = new HashSet<Parameter>();

		String query = "SELECT DISTINCT o.parameter_id FROM observation o JOIN biological_sample bs ON o.biological_sample_id = bs.id WHERE o.observation_type = 'categorical' AND bs.sample_group = 'experimental'";

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				parameters.add(getParameterById(resultSet.getInt("parameter_id")));
			}
		}

		return parameters;
	}
	//TODO: REMOVE THIS METHOD AFTER REFACTOR


	
	/**
	 * Return all categories for a parameter
	 * 
	 * @exception SQLException When a database error occurrs
	 */
	@Transactional(readOnly = true)
	public List<String> getCategoriesByParameterId(Integer id) throws SQLException {
		List<String> categories = new ArrayList<String>();

		String query = "SELECT DISTINCT ppo.name"
			+ " FROM phenotype_parameter pp"
			+ " INNER JOIN phenotype_parameter_lnk_option pplo ON pp.id=pplo.parameter_id"
			+ " INNER JOIN phenotype_parameter_option ppo ON pplo.option_id=ppo.id"
			+ " WHERE pp.id=?";

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {
			statement.setInt(1, id);
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				categories.add(resultSet.getString("name"));
			}
		}

		return categories;
	}

	
	/**
	 * Return all unidimensional parameters for which we have data loaded
	 * 
	 * @exception SQLException When a database error occurrs
	 */
	@Transactional(readOnly = true)
	public Set<Parameter> getAllUnidimensionalParametersForProcessing() throws SQLException {
		Set<Parameter> parameters = new HashSet<Parameter>();

		String query = "SELECT DISTINCT o.parameter_id FROM observation o JOIN biological_sample bs ON o.biological_sample_id = bs.id WHERE o.observation_type = 'unidimensional' AND bs.sample_group = 'experimental'";

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				parameters.add(getParameterById(resultSet.getInt("parameter_id")));
			}
		}

		return parameters;
	}
	
	
	@Override
	public List<String> getParameterStableIdsByPhenotypeTerm(String mpTermId) {
		
		String query = "SELECT DISTINCT pp.stable_id FROM phenotype_parameter pp INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa " +
		"ON pp.id=pploa.parameter_id INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id=pploa.annotation_id WHERE ppoa.ontology_db_id=5 AND ppoa.ontology_acc=? LIMIT 1000";
		PreparedStatement statement = null;
	    ResultSet resultSet = null;
	    ArrayList<String> parameters = new ArrayList<String>();
		try (Connection connection = getConnection()) {
	        statement = connection.prepareStatement(query);
	        statement.setString(1, mpTermId);
	        resultSet = statement.executeQuery();
			while (resultSet.next()) {
				parameters.add(resultSet.getString("stable_id"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return parameters;		
	}

	
//	@Transactional(readOnly = true)
	@Override
	public String getCategoryDescription(int parameterId, String category) throws SQLException {
		
		if (isNumeric(category)){
			String query = "SELECT * FROM phenotype_parameter_lnk_option pplo INNER JOIN phenotype_parameter_option ppo ON ppo.id = pplo.option_id WHERE ppo.name=? AND pplo.parameter_id=?";
			
			try (PreparedStatement statement = getConnection().prepareStatement(query)){
			       statement.setString(1, category.split(".0")[0]);
			       statement.setInt(2, parameterId);
			    ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					if (resultSet.getString("description") != null && !resultSet.getString("description").equals(""))
						return resultSet.getString("description");
				}
			}
		}
		return category;
	}
	
	public boolean isNumeric(String str)  
	{  
		return str.matches("-?\\d+(\\.\\d+)?");
	}
}
