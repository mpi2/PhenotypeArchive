/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
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
 * Phenotype call manager implementation
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;

public class PhenotypeCallSummaryDAOImpl extends HibernateDAOImpl implements PhenotypeCallSummaryDAO {

	/**
	 * Creates a new Hibernate pipeline data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public PhenotypeCallSummaryDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<PhenotypeCallSummary> getAllPhenotypeCallSummaries() {
		List<PhenotypeCallSummary> summaries = getCurrentSession().createQuery("from PhenotypeCallSummary").list();
		return summaries;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<PhenotypeCallSummary> getPhenotypeCallByAccession(String accId, int dbId) {
		List<PhenotypeCallSummary> results = getCurrentSession().createQuery("from PhenotypeCallSummary as pheno where pheno.gene.id.accession = ? and pheno.gene.id.databaseId = ?")
				.setString(0, accId)
				.setInteger(1, dbId)
				.list();
		
		// Filter out those results that don't have MP terms associated
		List<PhenotypeCallSummary> summaries = new ArrayList<PhenotypeCallSummary>();
		for(PhenotypeCallSummary p : results) {
			if (p.getPhenotypeTerm() != null) {				
				summaries.add(p);
			}
		}
		return summaries;
	}	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<PhenotypeCallSummary> getPhenotypeCallByMPAccession(String accId, int dbId) {
		List<PhenotypeCallSummary> results = getCurrentSession().createQuery("from PhenotypeCallSummary as pheno where pheno.phenotypeTerm.id.accession = ? and pheno.phenotypeTerm.id.databaseId = ? ")
				.setString(0, accId)
				.setInteger(1, dbId)
				.list();
		
		// Filter out those results that don't have MP terms associated
		List<PhenotypeCallSummary> summaries = new ArrayList<PhenotypeCallSummary>();
		for(PhenotypeCallSummary p : results) {
			if (p.getPhenotypeTerm() != null) {				
				summaries.add(p);
			}
		}
		return summaries;
	}	
}
