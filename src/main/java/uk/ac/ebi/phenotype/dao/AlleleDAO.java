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
 * Allele manager interface.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.List;

import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;

public interface AlleleDAO extends HibernateDAO {

	public List<Allele> getAllAlleles();
	
	public Allele getAlleleByAccession(String accession);
	
	public Allele getAlleleBySymbolAndGene(String symbol, GenomicFeature gene);

	public Allele getAlleleBySymbol(String symbol);
	
	public List<Allele> getAlleleByGeneSymbol(String geneSymbol);
	
	public List<Allele> getAlleleByGeneAccession(String geneAccession);
}
