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
 * Statistics DAO hibernate implementation.
 * 
 * @author Jeremy Mason (EMBL-EBI) <jmason@ebi.ac.uk>
 * @since 2013-04-29
 */

import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;

public class StatisticsDAOImpl extends HibernateDAOImpl implements StatisticsDAO {


	/**
	 * Save a result to the database.
	 */
	@Transactional(readOnly = false)
	public void saveResult(StatisticalResult result) {
		getCurrentSession().saveOrUpdate(result);
		getCurrentSession().flush();
	}

	/**
	 * Save an annotation association to the database.
	 */
	@Transactional(readOnly = false)
	public void saveAnnotationAssociation(PhenotypeCallSummary pcs) {
		getCurrentSession().saveOrUpdate(pcs);
		getCurrentSession().flush();
	}

}
