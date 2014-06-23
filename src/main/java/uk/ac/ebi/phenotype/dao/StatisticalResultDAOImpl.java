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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.bean.StatisticalResultBean;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.MouseDataPoint;

/**
 * 
 * The statistical result data access object is a wrapper to get access to
 * the results stored in the databased once the statistical pipeline has been
 * run on the categorical, unidimensional and derived parameters.
 * 
 * @author Jonathan Warren <jwarren@ebi.ac.uk>
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2014
 */

public class StatisticalResultDAOImpl extends HibernateDAOImpl implements StatisticalResultDAO {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Creates a new Hibernate sequence region data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public StatisticalResultDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public Map<String, List<StatisticalResultBean>> getPvaluesByAlleleAndPhenotypingCenterAndPipeline(
			String alleleAccession, String phenotypingCenter,
			String pipelineStableId) {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Map<String, List<StatisticalResultBean>> results = new HashMap<String, List<StatisticalResultBean>>();
		
		/* 
		 * get all categorical parameter:
		 *   - ordered by parameter
		 *   - one for each sex
		 *   - with the most animals
		 *   - with success first
		 */
		
		String query = 
				"SELECT param.stable_id AS parameter_stable_id, "
				+ "c.p_value AS p_value, c.effect_size AS effect_size, "
				+ "c.status AS status, c.statistical_method AS statistical_method, " 
				+ "c.control_sex AS control_sex, c.experimental_zygosity, "
				+ "c.male_controls, c.male_mutants, c.female_controls, c.female_mutants, "
				+ "c.metadata_group "
				+ "FROM stats_categorical_results c JOIN phenotype_parameter param "
				+ "ON param.id = c.parameter_id JOIN phenotype_pipeline pip "
				+ "ON pip.id = c.pipeline_id JOIN biological_model_allele bma "
				+ "ON bma.biological_model_id = c.experimental_id JOIN organisation o "
				+ "ON o.id = c.organisation_id "
				+ "WHERE pip.stable_id = ? AND o.name = ? AND bma.allele_acc = ? "
				+ "ORDER by param.stable_id asc, c.status asc, c.p_value desc, "
				+ "CASE c.control_sex "
				+ " WHEN 'male' THEN c.male_mutants "
				+ " WHEN 'female' THEN c.female_mutants END ASC";

		try (Connection connection = getConnection()) {
			statement = connection.prepareStatement(query);
			statement.setString(1, pipelineStableId);
			statement.setString(2, phenotypingCenter);
			statement.setString(3, alleleAccession);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				
				String parameterStableId = resultSet.getString("parameter_stable_id");
				List<StatisticalResultBean> lb = null;
				
				if (results.containsKey(parameterStableId)) {
					lb = results.get(parameterStableId);
				} else {
					lb = new ArrayList<StatisticalResultBean>();
					results.put(parameterStableId, lb);
				} 
				
				lb.add(
							new StatisticalResultBean(
									resultSet.getDouble("p_value"), 
									resultSet.getDouble("effect_size"),
									resultSet.getString("status"),
									resultSet.getString("statistical_method"),
									resultSet.getString("control_sex"),
									resultSet.getString("experimental_zygosity"),
									resultSet.getInt("male_controls"),
									resultSet.getInt("male_mutants"),
									resultSet.getInt("female_controls"),
									resultSet.getInt("female_mutants"),
									resultSet.getString("metadata_group"))
					 );
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		/**
		 * Get all continuous parameters:
		 * - MM 
		 * - Rank Sum test
		 * To be able to achieve that, we need to separate the queries
		 */
		
		String rankSumTest = "Wilcoxon rank sum test with continuity correction";
		
		/**
		 * Mixed model
		 */
		
		query = 
				"SELECT param.stable_id AS parameter_stable_id,"
				+ "c.null_test_significance AS p_value, 0 AS effect_size, "
				+ "c.status AS status, c.statistical_method AS statistical_method, "
				// note that control_sex has no meaning in this case
				+ "'male' as control_sex, c.experimental_zygosity, "
				+ "c.male_controls, c.male_mutants, c.female_controls, c.female_mutants, "
				+ "c.metadata_group "
				+ "FROM stats_unidimensional_results c JOIN phenotype_parameter param "
				+ "ON param.id = c.parameter_id JOIN phenotype_pipeline pip "
				+ "ON pip.id = c.pipeline_id JOIN biological_model_allele bma "
				+ "ON bma.biological_model_id = c.experimental_id JOIN organisation o "
				+ "ON o.id = c.organisation_id "
				+ "WHERE c.statistical_method <> ?"
				+ " AND pip.stable_id = ? AND o.name = ? AND bma.allele_acc = ? "
				//+ "ORDER by param.stable_id asc, c.null_test_significance desc, c.male_mutants desc, c.female_mutants desc";
				+ "ORDER by param.stable_id asc, c.status asc, c.null_test_significance desc, c.male_mutants asc, c.female_mutants asc";
		
		System.out.println(query);
		
		try (Connection connection = getConnection()) {
			
			statement = connection.prepareStatement(query);
			statement.setString(1, rankSumTest);
			statement.setString(2, pipelineStableId);
			statement.setString(3, phenotypingCenter);
			statement.setString(4, alleleAccession);
			
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				
				
				String parameterStableId = resultSet.getString("parameter_stable_id");
				List<StatisticalResultBean> lb = null;
				
				if (results.containsKey(parameterStableId)) {
					lb = results.get(parameterStableId);
				} else {
					lb = new ArrayList<StatisticalResultBean>();
					results.put(parameterStableId, lb);
				} 
				
				lb.add(
							new StatisticalResultBean(
									resultSet.getDouble("p_value"), 
									resultSet.getDouble("effect_size"),
									resultSet.getString("status"),
									resultSet.getString("statistical_method"),
									"both",
									resultSet.getString("experimental_zygosity"),
									resultSet.getInt("male_controls"),
									resultSet.getInt("male_mutants"),
									resultSet.getInt("female_controls"),
									resultSet.getInt("female_mutants"),
									resultSet.getString("metadata_group"))
					 );

			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
			/*
			 * Wilcoxon Rank Sum test 
			 */
			
			query = 
					"SELECT param.stable_id AS parameter_stable_id,"
					+ "LEAST(c.gender_female_ko_pvalue, c.gender_male_ko_pvalue) AS p_value, "
					+ "0 AS effect_size, "
					+ "c.status AS status, c.statistical_method AS statistical_method, "
					// note that control_sex has no meaning in this case
					+ "'male' as control_sex, c.experimental_zygosity, "
					+ "c.male_controls, c.male_mutants, c.female_controls, c.female_mutants, "
					+ "c.metadata_group "
					+ "FROM stats_unidimensional_results c JOIN phenotype_parameter param "
					+ "ON param.id = c.parameter_id JOIN phenotype_pipeline pip "
					+ "ON pip.id = c.pipeline_id JOIN biological_model_allele bma "
					+ "ON bma.biological_model_id = c.experimental_id JOIN organisation o "
					+ "ON o.id = c.organisation_id "
					+ "WHERE c.statistical_method = ?"
					+ " AND pip.stable_id = ? AND o.name = ? AND bma.allele_acc = ? "
					//+ "ORDER by param.stable_id asc, c.null_test_significance desc, c.male_mutants desc, c.female_mutants desc";
					+ "ORDER by param.stable_id asc, c.status asc, c.null_test_significance desc, c.male_mutants asc, c.female_mutants asc";
			
			System.out.println(query);
			
			try (Connection connection = getConnection()) {
				
				statement = connection.prepareStatement(query);
				statement.setString(1, rankSumTest);
				statement.setString(2, pipelineStableId);
				statement.setString(3, phenotypingCenter);
				statement.setString(4, alleleAccession);
				
				resultSet = statement.executeQuery();
				
				while (resultSet.next()) {
					
					
					String parameterStableId = resultSet.getString("parameter_stable_id");
					List<StatisticalResultBean> lb = null;
					
					if (results.containsKey(parameterStableId)) {
						lb = results.get(parameterStableId);
					} else {
						lb = new ArrayList<StatisticalResultBean>();
						results.put(parameterStableId, lb);
					} 
					
					lb.add(
								new StatisticalResultBean(
										resultSet.getDouble("p_value"), 
										resultSet.getDouble("effect_size"),
										resultSet.getString("status"),
										resultSet.getString("statistical_method"),
										"both",
										resultSet.getString("experimental_zygosity"),
										resultSet.getInt("male_controls"),
										resultSet.getInt("male_mutants"),
										resultSet.getInt("female_controls"),
										resultSet.getInt("female_mutants"),
										resultSet.getString("metadata_group"))
						 );

				}			
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("nb of results : " + results.size());
		log.info("nb of results : " + results.size());
		return results;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public List<UnidimensionalResult> 
	getUnidimensionalResultByParameterAndBiologicalModel(Parameter parameter, 
			BiologicalModel controlBiologicalModel, 
			BiologicalModel mutantBiologicalModel) {
		return (List<UnidimensionalResult>) getCurrentSession().createQuery("from UnidimensionalResult u  WHERE parameter=? and control_id=? and experimental_id=?")
				.setInteger(0, parameter.getId())
				.setInteger(1, controlBiologicalModel.getId())
				.setInteger(2, mutantBiologicalModel.getId())
				.list();

		//select p from AnalysisPolicy p where exists elements(p.nodeIds)
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<UnidimensionalResult> 
	getUnidimensionalResultByParameterIdAndBiologicalModelIds(
			Integer parameterId, Integer controlBiologicalId,
			Integer biologicalId) {
		return (List<UnidimensionalResult>) getCurrentSession().createQuery("from UnidimensionalResult u  WHERE parameter=? and control_id=? and experimental_id=?")
				.setInteger(0, parameterId)
				.setInteger(1, controlBiologicalId)
				.setInteger(2, biologicalId)
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<MouseDataPoint> 
	getMutantDataPointsWithMouseName(
			SexType sex, 
			ZygosityType zygosity, 
			Parameter parameter,  
			Integer populationId){

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<MouseDataPoint> mouseDataPoints = new ArrayList<MouseDataPoint>();

		String query = "SELECT * FROM komp2.stats_mv_experimental_unidimensional_values vw, biological_sample bs where vw.sex=? AND vw.zygosity=? AND vw.parameter_id=?  AND vw.population_id=? and vw.biological_sample_id=bs.id";

		try (Connection connection = getConnection()) {
			statement = connection.prepareStatement(query);
			statement.setString(1, sex.name());
			statement.setString(2, zygosity.getName());
			statement.setInt(3, parameter.getId());
			statement.setInt(4, populationId);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				mouseDataPoints.add(new MouseDataPoint(resultSet.getString("external_id"), resultSet.getFloat("data_point")));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		//SELECT * FROM komp2.stats_mv_experimental_unidimensional_values vw, biological_sample bs where vw.sex='male' AND vw.zygosity='heterozygote' AND vw.parameter_id=1268  AND vw.population_id=301919 and vw.biological_sample_id=bs.id;
		return mouseDataPoints;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<MouseDataPoint> getControlDataPointsWithMouseName(Integer populationId){

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<MouseDataPoint> mouseDataPoints = new ArrayList<MouseDataPoint>();

		String query = "SELECT * FROM komp2.stats_mv_control_unidimensional_values vw, biological_sample bs where  vw.population_id=? and vw.biological_sample_id=bs.id";

		try (Connection connection = getConnection()) {
			statement = connection.prepareStatement(query);
			statement.setInt(1, populationId);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				mouseDataPoints.add(new MouseDataPoint(resultSet.getString("external_id"), resultSet.getFloat("data_point")));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		//SELECT * FROM komp2.stats_mv_experimental_unidimensional_values vw, biological_sample bs where vw.sex='male' AND vw.zygosity='heterozygote' AND vw.parameter_id=1268  AND vw.population_id=301919 and vw.biological_sample_id=bs.id;
		return mouseDataPoints;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public UnidimensionalResult getUnidimensionalStatsForPhenotypeCallSummaryId(int phenotypeCallSummaryId) throws SQLException{
		//get the id we need from the join table
		int resultId=this.getUnidimensionalResultIdFromStatsResultPhenotypeCallSummary(phenotypeCallSummaryId);
		//use the join table id to get the actual result
		return (UnidimensionalResult) getCurrentSession().get(UnidimensionalResult.class,  resultId);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public CategoricalResult getCategoricalStatsForPhenotypeCallSummaryId(int phenotypeCallSummaryId) throws SQLException{
		//get the id we need from the join table
		int resultId=this.getCategoricalResultIdFromStatsResultPhenotypeCallSummary(phenotypeCallSummaryId);
		//use the join table id to get the actual result
		return (CategoricalResult) getCurrentSession().get(CategoricalResult.class,  resultId);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private int getUnidimensionalResultIdFromStatsResultPhenotypeCallSummary(int id) throws SQLException {

		int result=-1;
		String query = "SELECT unidimensional_result_id FROM stat_result_phenotype_call_summary where phenotype_call_summary_id= '"
				+ id + "'";

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result=resultSet.getInt(1);
			}
		}
		return result;
	}	

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private int getCategoricalResultIdFromStatsResultPhenotypeCallSummary(int id)throws SQLException{
		//get the id we need from the join table
		int result=-1;
		String query = "SELECT categorical_result_id FROM stat_result_phenotype_call_summary where phenotype_call_summary_id= '"
				+ id + "'";

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result=resultSet.getInt(1);
			}
		}
		return result;
	}

}
