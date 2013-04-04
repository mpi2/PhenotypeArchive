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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "biological_model")
public class BiologicalModel extends SourcedEntry {
	
	@Column(name = "id", insertable=false, updatable=false)
	Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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
	@Fetch(FetchMode.SELECT)
	@JoinTable(
			name="biological_model_genomic_feature",
		    joinColumns = @JoinColumn( name="biological_model_id"),
            inverseJoinColumns = {@JoinColumn(name = "gf_acc"), @JoinColumn(name = "gf_db_id")}
    )
	private List<GenomicFeature> genomicFeatures;	
	
	/**
	 * Unidirectional with join table
	 * Transitive persistence with cascading
	 * We detach the association but we keep 
	 * the allele otherwise
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinTable(
			name="biological_model_allele",
		    joinColumns = @JoinColumn( name="biological_model_id"),
            inverseJoinColumns = {@JoinColumn(name = "allele_acc"), @JoinColumn(name = "allele_db_id")}
    )
	private List<Allele> alleles;	
	
	/**
	 * Unidirectional with join table
	 * Transitive persistence with cascading
	 * We detach the association but we keep 
	 * the strain otherwise
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JoinTable(
			name="biological_model_strain",
		    joinColumns = @JoinColumn( name="biological_model_id"),
            inverseJoinColumns = {@JoinColumn(name = "strain_acc"), @JoinColumn(name = "strain_db_id")}
    )
	private List<Strain> strains;	
	
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

	public void addBiologicalSample(BiologicalSample biologicalSample) {
		if (biologicalSamples == null) {
			biologicalSamples = new LinkedList<BiologicalSample>();
		}
		biologicalSamples.add(biologicalSample);
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
	
	/**
	 * @return the alleles
	 */
	public List<Allele> getAlleles() {
		return alleles;
	}

	/**
	 * @param alleles the alleles to set
	 */
	public void setAlleles(List<Allele> alleles) {
		this.alleles = alleles;
	}
	
	/**
	 * @param allele the allele to add to the collection
	 */
	public void addAllele(Allele allele) {
		if (alleles == null) {
			this.alleles = new LinkedList<Allele>();
		}
		this.alleles.add(allele);
	}	

	/**
	 * @return the strains
	 */
	public List<Strain> getStrains() {
		return strains;
	}

	/**
	 * @param strains the strains to set
	 */
	public void setStrains(List<Strain> strains) {
		this.strains = strains;
	}

	/**
	 * @param strain the strain to add to the collection
	 */
	public void addStrain(Strain strain) {
		if (strains == null) {
			this.strains = new LinkedList<Strain>();
		}
		this.strains.add(strain);
	}		
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BiologicalModel [id="+id+ "allelicComposition=" + allelicComposition
				+ ", geneticBackground=" + geneticBackground
				+ ", biologicalSamples=" + biologicalSamples
				+ ", genomicFeatures=" + genomicFeatures + "]";
	}
	/**
	 * get an html representation (with superscript elements)  of the first allele in this biological model - used in stats graphs in stats.jsp - may cause issues if more than one allele for biological model
	 * @return
	 */
	public String getHtmlSymbol(){
		
		String allele=this.getAlleles().get(0).getSymbol();
		if(allele.contains("<") && allele.contains(">")){
		String array[]=allele.split("<");
		String beforeSup=array[0];
		String sup=array[1].replace(">", "");
		return beforeSup+"<sup>"+sup+"</sup>";
		}else{
			return allele;
		}
	}
}
