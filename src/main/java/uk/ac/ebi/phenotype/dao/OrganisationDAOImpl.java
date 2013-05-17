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
 * 
 * Organisation (institute, company) data access manager implementation.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Organisation;

@Service
public class OrganisationDAOImpl extends HibernateDAOImpl implements OrganisationDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Organisation> getAllOrganisations() {
		return getCurrentSession().createQuery("from Organisation").list();
	}

	@Transactional(readOnly = true)
	public Organisation getOrganisationByName(String name) {
		return (Organisation) getCurrentSession().createQuery("from Organisation as o where o.name= ?").setString(0, name).uniqueResult();
	}

	@Transactional(readOnly = true)
	public Organisation getOrganisationById(Integer id) {
		return (Organisation) getCurrentSession().createQuery("from Organisation as o where o.id= ?").setInteger(0, id).uniqueResult();
	}

}
