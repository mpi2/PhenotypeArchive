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
package uk.ac.ebi.phenotype.pojo;

/**
 * 
 * Representation of a biological model of interest.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * @see BiologicalSample
 * @see GenomicFeature
 */

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name = "biological_model")
public class BiologicalModel extends SourcedEntry {
	
	@Column(name = "allelic_composition")
	String allelicComposition;
	
	@Column(name = "genetic_background")
	String geneticBackground;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name="biological_model_sample",
            joinColumns = @JoinColumn( name="biological_model_id"),
            inverseJoinColumns = @JoinColumn( name="biological_sample_id")
    )
	private List<BiologicalSample> biologicalSamples;	
		
	/**
	 * Unidirectional with join table
	 * Transitive persistence with cascading
	 * We detach the association but we keep 
	 * the genomic feature otherwise
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch= FetchType.EAGER )
	@JoinTable(
			name="biological_model_genomic_feature",
		    joinColumns = @JoinColumn( name="biological_model_id"),
            inverseJoinColumns = {@JoinColumn(name = "gf_acc"), @JoinColumn(name = "gf_db_id")}
    )
	private List<GenomicFeature> genomicFeatures;	
	
	/**
	 * @return the biologicalSamples
	 */
	public List<BiologicalSample> getBiologicalSamples() {
		return biologicalSamples;
	}

	/**
	 * @param biologicalSamples the biologicalSamples to set
	 */
	public void setBiologicalSamples(List<BiologicalSample> biologicalSamples) {
		this.biologicalSamples = biologicalSamples;
	}

	/**
	 * @return the allelicComposition
	 */
	public String getAllelicComposition() {
		return allelicComposition;
	}

	/**
	 * @param allelicComposition the allelicComposition to set
	 */
	public void setAllelicComposition(String allelicComposition) {
		this.allelicComposition = allelicComposition;
	}

	/**
	 * @return the geneticBackground
	 */
	public String getGeneticBackground() {
		return geneticBackground;
	}

	/**
	 * @param geneticBackground the geneticBackground to set
	 */
	public void setGeneticBackground(String geneticBackground) {
		this.geneticBackground = geneticBackground;
	}
		
	/**
	 * @return the genomicFeatures
	 */
	public List<GenomicFeature> getGenomicFeatures() {
		return genomicFeatures;
	}

	/**
	 * @param genomicFeatures the genomicFeatures to set
	 */
	public void setGenomicFeatures(List<GenomicFeature> genomicFeatures) {
		this.genomicFeatures = genomicFeatures;
	}

	/**
	 * @param genomicFeature the genomicFeature to add to the collection
	 */
	public void addGenomicFeature(GenomicFeature genomicFeature) {
		if (genomicFeatures == null) {
			this.genomicFeatures = new LinkedList<GenomicFeature>();
		}
		this.genomicFeatures.add(genomicFeature);
	}
		
}
