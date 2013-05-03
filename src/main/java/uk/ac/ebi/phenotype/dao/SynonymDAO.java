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
 * Synonym data access manager interface.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.List;

import uk.ac.ebi.phenotype.pojo.Synonym;



public interface SynonymDAO {

	/**
	 * Get all synonyms in the database
	 * @return all synonyms
	 */
	public List<Synonym> getAllSynonyms();

	/**
	 * Get all synonyms in the database from a specific resources
	 * @param databaseId the database id
	 * @return all synonyms
	 */
	public List<Synonym> getAllSynonymsByDatabaseId(int databaseId);
	
	/**
	 * Find a synonym by it's accession and database id
	 * @param accession the synonym symbol
	 * @return the synonym
	 */
	public List<Synonym> getAllSynonymsByAccessionAndDatabaseId(String accession, int databaseId);
	
}
