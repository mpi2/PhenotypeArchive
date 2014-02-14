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
 * Coordinate system data access manager implementation
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.CoordinateSystem;

public class CoordinateSystemDAOImpl extends HibernateDAOImpl implements CoordinateSystemDAO {

	/**
	 * Creates a new Hibernate coordinate system data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public CoordinateSystemDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
		
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<CoordinateSystem> getAllCoordinateSystems() {
		return getCurrentSession().createQuery("from CoordinateSystem").list();
	}

	@Transactional(readOnly = true)
	public CoordinateSystem getCoordinateSystemByName(String name) {
		return (CoordinateSystem) getCurrentSession().createQuery("from CoordinateSystem as cs where cs.name= ?").setString(0, name).uniqueResult();
	}

}
