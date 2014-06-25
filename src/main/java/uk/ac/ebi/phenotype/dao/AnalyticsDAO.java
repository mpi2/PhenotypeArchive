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

import java.util.List;
import java.util.Map;

import uk.ac.ebi.phenotype.analytics.bean.*;


public interface AnalyticsDAO {

	/**
	 * Get all meta information for this release
	 * @return all meta information in a Map
	 */
	Map<String, String> getMetaData();
	
	/**
	 * Retrieves number of lines per procedure for every phenotyping center.
	 * @return a list of objects containing the number of lines per procedure
	 * per center
	 */
	List<AggregateCountXYBean> getAllProcedureLines();
	
	/**
	 * Retrieves the aggregate count of significant call per procedure per center
	 * @return list of aggregate count
	 */
	List<AggregateCountXYBean> getAllProcedurePhenotypeCalls();
	
	/**
	 * Retrieves the statistical methods used for the analysis
	 */
	Map<String, List<String>> getAllStatisticalMethods();
	
	/**
	 * Return the p-value distribution from the statistical analysis using 
	 * statistical method statisticalMethod on data of type dataType
	 * @param dataType type of data
	 * @param statisticalMethod statistical method used.
	 * @return
	 */
	List<AggregateCountXYBean> getPValueDistribution(String dataType, String statisticalMethod);
	
	/**
	 * Get historical data from release to release
	 * @param propertyKey which variable to look at 
	 * @return a list of points for this variable ordered by release date
	 */
	List<AggregateCountXYBean> getHistoricalData(String propertyKey);
	
	/**
	 * List all releases
	 * @return a list of releases
	 */
	List<String> getReleases(String excludeRelease);
}
