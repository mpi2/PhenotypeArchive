/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.phenotype.analytics.bean.AggregateCountXYBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class AnalyticsDAOImpl extends HibernateDAOImpl implements AnalyticsDAO {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Creates a new Hibernate project data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public AnalyticsDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")	public Map<String, String> getMetaData() {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Map<String, String> metaInfo = new HashMap<String, String>();
		
		try (Connection connection = getConnection()) {
			
			statement = connection.prepareStatement("SELECT * from meta_info");
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				
				String pKey = resultSet.getString(2);
				String pValue = resultSet.getString(3);
				metaInfo.put(pKey,  pValue);

			}
			statement.close();
			
		}catch (SQLException e) {
			e.printStackTrace();
			
		}
		
		return metaInfo;
	}

	@Override
	public List<AggregateCountXYBean> getAllProcedureLines() {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<AggregateCountXYBean> results = new ArrayList<AggregateCountXYBean>();
		
		try (Connection connection = getConnection()) {
			
			statement = connection.prepareStatement("SELECT * FROM analytics_lines_procedures");
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				
				results.add(new AggregateCountXYBean(
						resultSet.getInt(1), 
						resultSet.getString(4),
						"procedure",
						resultSet.getString(3),
						resultSet.getString(2), 
						"nb of lines",
						null
						));
			}
			statement.close();
			
		}catch (SQLException e) {
			e.printStackTrace();
			
		}
		
		return results;
	}

	@Override
	public List<AggregateCountXYBean> getAllProcedurePhenotypeCalls() {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<AggregateCountXYBean> results = new ArrayList<AggregateCountXYBean>();
		
		try (Connection connection = getConnection()) {
			
			statement = connection.prepareStatement("SELECT * FROM analytics_significant_calls_procedures");
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				
				results.add(new AggregateCountXYBean(
						resultSet.getInt(1), 
						resultSet.getString(4),
						"procedure",
						resultSet.getString(3),
						resultSet.getString(2), 
						"nb of calls",
						null
						));
			}
			statement.close();
			
		}catch (SQLException e) {
			e.printStackTrace();
			
		}
		
		return results;
	}

	@Override
	public Map<String, List<String>> getAllStatisticalMethods() {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Map<String, List<String>> results = new HashMap<String, List<String>>();
		
		try (Connection connection = getConnection()) {
			
			statement = connection.prepareStatement("SELECT DISTINCT datatype, statistical_method FROM analytics_pvalue_distribution;");
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				
				String datatype = resultSet.getString(1);
				String method = resultSet.getString(2);
				
				List<String> methods = null;
				if (!results.containsKey(datatype)) {
					methods = new ArrayList<String>();
					results.put(datatype, methods);
				} else {
					methods = results.get(datatype);
				}
				methods.add(method);
				
			}
			statement.close();
			
		}catch (SQLException e) {
			e.printStackTrace();
			
		}
		
		return results;
	}

	@Override
	public List<AggregateCountXYBean> getPValueDistribution(String dataType,
			String statisticalMethod) {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<AggregateCountXYBean> results = new ArrayList<AggregateCountXYBean>();
		
		try (Connection connection = getConnection()) {
			
			statement = connection.prepareStatement("select pvalue_count, pvalue_bin from analytics_pvalue_distribution where datatype = ? and statistical_method = ? order by pvalue_bin asc;");
			statement.setString(1, dataType);
			statement.setString(2, statisticalMethod);
			
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				
				results.add(new AggregateCountXYBean(
						resultSet.getInt(1), 
						resultSet.getString(2),
						"p-value",
						null,
						statisticalMethod,
						statisticalMethod, 
						null
						));
			}
			statement.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
		
		return results;
	}

	@Override
	public List<AggregateCountXYBean> getHistoricalData(String propertyKey) {
			
			PreparedStatement statement = null;
			ResultSet resultSet = null;
			List<AggregateCountXYBean> results = new ArrayList<AggregateCountXYBean>();
			try (Connection connection = getConnection()) {
				
				statement = connection.prepareStatement("SELECT property_value, property_key, data_release_version FROM meta_history WHERE property_key = ? ORDER BY data_release_version ASC");
				statement.setString(1, propertyKey);
				resultSet = statement.executeQuery();
				
				while (resultSet.next()) {
					results.add(new AggregateCountXYBean(
							resultSet.getInt(1), 
							resultSet.getString(2),
							resultSet.getString(2),
							null,
							resultSet.getString(3),
							resultSet.getString(3), 
							null
							));
				}
				statement.close();
				
			}catch (SQLException e) {
				e.printStackTrace();
				
			}
			
			return results;
		}

	@Override
	public List<String> getReleases(String excludeRelease) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<String> results = new ArrayList<String>();
		
		try (Connection connection = getConnection()) {
			
			if (excludeRelease != null) {
				statement = connection.prepareStatement("SELECT DISTINCT data_release_version FROM meta_history WHERE data_release_version <> ? ORDER BY data_release_version ASC");
				statement.setString(1, excludeRelease);
			} else {
				statement = connection.prepareStatement("SELECT DISTINCT data_release_version FROM meta_history ORDER BY data_release_version ASC");
			}
				resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				
				results.add(resultSet.getString(1));

			}
			statement.close();
			
		}catch (SQLException e) {
			e.printStackTrace();
			
		}
		
		return results;
	}

	public String getCurrentRelease() {

		String query = "SELECT property_value as release_version FROM meta_info WHERE property_key='data_release_version'";
		String releaseVersion = "";

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				releaseVersion = rs.getString("release_version");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return releaseVersion;
	}
	
}
