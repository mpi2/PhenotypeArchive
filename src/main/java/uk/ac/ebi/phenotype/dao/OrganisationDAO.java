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

/**
 *
 * Organisation (institute, company) data access manager interface.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.List;

import uk.ac.ebi.phenotype.pojo.Organisation;

public interface OrganisationDAO extends HibernateDAO {

	/**
	 * Get all organisations
	 * @return all organisations
	 */
	public List<Organisation> getAllOrganisations();

	/**
	 * Find an organisation by its name.
	 * @param name the organisation name
	 * @return the organisation
	 */
	public Organisation getOrganisationByName(String name);

	/**
	 * Find an organisation by its id.
	 * @param id the organisation internal id
	 * @return the organisation
	 */
	public Organisation getOrganisationById(Integer id);
}
