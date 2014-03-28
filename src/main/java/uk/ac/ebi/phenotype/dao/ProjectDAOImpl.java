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
* Phenotyping project data access manager implementation.
* 
* @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
* @since February 2012
*/

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Project;

public class ProjectDAOImpl extends HibernateDAOImpl implements ProjectDAO {

	/**
	 * Creates a new Hibernate project data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public ProjectDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Project> getAllProjects() {
		return getCurrentSession().createQuery("from Project").list();
	}

	@Transactional(readOnly = true)
	public Project getProjectByName(String name) {
		return (Project) getCurrentSession().createQuery("from Project as p where p.name= ?").setString(0, name).uniqueResult();
	}

	@Transactional(readOnly = true)
	public Project getProjectById(Integer projectId) {
		return (Project) getCurrentSession().createQuery("from Project as p where p.id= ?").setInteger(0, projectId).uniqueResult();	
	}

}
