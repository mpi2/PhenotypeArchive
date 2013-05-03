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
 * Genomic feature data access manager interface.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.phenotype.bean.GenomicFeatureBean;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;

public interface GenomicFeatureDAO extends HibernateDAO {

	/**
	 * Get all genomic feature
	 * @return all coordinate system
	 */
	public List<GenomicFeature> getAllGenomicFeatures();
	
	public List<GenomicFeatureBean> getAllGenomicFeatureBeans();

	public GenomicFeature getGenomicFeatureByAccession(String accession);
	
	public GenomicFeature getGenomicFeatureByAccessionAndDbId(String accession, int dbId);
	
	public GenomicFeature getGenomicFeatureBySymbol(String symbol);
	
	public GenomicFeature getGenomicFeatureBySymbolOrSynonym(String symbol);
	
	public int deleteAllGenomicFeatures();
	public int batchInsertion(Collection<GenomicFeature> genomicFeatures, int batchSize);
	
	/**
	 * Find a coordinate system by its name.
	 * @param name the coordinate system name
	 * @return the coordinate system
	 */
	public GenomicFeature getGenomicFeatureByName(String name);
	
	public GenomicFeature getGenomicFeatureByBiotype(String biotype);
	
	public Map<String, GenomicFeature> getGenomicFeaturesByBiotype(String biotype);
	
	public Map<String, GenomicFeature> getGenomicFeaturesByBiotypeAndNoSubtype(String biotype);
	
	/**
	 * Save a genomic feature representation
	 * @param feature genomic feature to be saved.
	 */
	public void saveGenomicFeature(GenomicFeature feature);
	
	
}
