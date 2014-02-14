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
 * Allele manager implementation.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;

public class AlleleDAOImpl extends HibernateDAOImpl implements AlleleDAO {

	/**
	 * Creates a new Hibernate project data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public AlleleDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}	

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Allele> getAllAlleles() {
		return getCurrentSession().createQuery("from Allele").list();
	}

	@Transactional(readOnly = true)
	public Allele getAlleleByAccession(String accession) {
		return (Allele) getCurrentSession().createQuery("from Allele as a where a.id.accession = ?").setString(0, accession).uniqueResult();
		
	}


	@Transactional(readOnly = true)
	public Allele getAlleleBySymbolAndGene(String symbol, GenomicFeature gene) {
		return (Allele) getCurrentSession().createQuery("from Allele as a where a.symbol = ? and a.gene.id.accession=?")
				.setString(0, symbol)
				.setString(1, gene.getId().getAccession())
				.uniqueResult();
		
	}

	@Transactional(readOnly = true)
	public Allele getAlleleBySymbol(String symbol) {
		return (Allele) getCurrentSession().createQuery("from Allele as a where a.symbol = ?").setString(0, symbol).uniqueResult();
		
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Allele> getAlleleByGeneSymbol(String geneSymbol) {
		return getCurrentSession().createQuery("from Allele as a where a.gene.symbol = ?").setString(0, geneSymbol).list();
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Allele> getAlleleByGeneAccession(String geneAccession) {
		return getCurrentSession().createQuery("from Allele as a where a.gene.id.accession = ?").setString(0, geneAccession).list();
	}

}
