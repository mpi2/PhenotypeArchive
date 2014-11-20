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
 * Ontology term and controlled vocabulary data access manager interface.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import uk.ac.ebi.phenotype.pojo.OntologyTerm;


public interface OntologyTermDAO extends HibernateDAO {

	public List<OntologyTerm> getAllOntologyTerms();
	
	public List<OntologyTerm> getAllOntologyTermsByDatabaseId(int databaseId);
	
	public OntologyTerm getOntologyTermByName(String name);
	
	public OntologyTerm getOntologyTermBySynonym(String name);
	
	public OntologyTerm getOntologyTermByNameAndDatabaseId(String name, int databaseId);
	
	public OntologyTerm getOntologyTermByAccession(String accession);
	
	public OntologyTerm getOntologyTermByAccessionAndDatabaseId(String accession, int databaseId);
	
	public HashMap<String, OntologyTerm> getAllTerms(int databaseId);
	
	public int deleteAllTerms(String datasourceName);
	
	public int batchInsertion(Collection<OntologyTerm> ontologyTerms);
	
}
