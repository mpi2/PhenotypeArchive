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
import java.util.List;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

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
