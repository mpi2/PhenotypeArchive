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
package uk.ac.ebi.phenotype.pojo;

/**
 * 
 * Store phenotype calls from diverse statistical pipeline on multiple datasets.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "phenotype_call_summary")
public class PhenotypeCallSummary {

	@Id
	@GeneratedValue
	@Column(name = "id")
	protected Integer id;
	
	@Column(name = "external_id")
	Integer externalId;

	@Enumerated(EnumType.STRING)
	@Column(name = "sex")
	protected SexType sex;

	@Enumerated(EnumType.STRING)
	@Column(name = "zygosity")
	protected ZygosityType zygosity;

	@OneToOne
	@JoinColumn(name = "external_db_id")
	protected Datasource datasource;	
	
	@OneToOne
	@JoinColumn(name = "project_id")
	protected Project project;	
	
	@OneToOne
	@JoinColumn(name = "organisation_id")
	protected Organisation organisation;	
	
	@NotFound(action=NotFoundAction.IGNORE) // phenotype_call_summary.gf_acc maybe null
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "gf_acc"),
	@JoinColumn(name = "gf_db_id"),
	})
	protected GenomicFeature gene;
	
	@NotFound(action=NotFoundAction.IGNORE)
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "mp_acc"),
	@JoinColumn(name = "mp_db_id"),
	})
	protected OntologyTerm phenotypeTerm;
	
	@Column(name = "p_value")
	protected float pValue = 0;

	@Column(name = "effect_size")
	protected float effectSize = 0;
	
	@NotFound(action=NotFoundAction.IGNORE)
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "strain_acc"),
	@JoinColumn(name = "strain_db_id"),
	})
	protected Strain strain;
	
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "allele_acc"),
	@JoinColumn(name = "allele_db_id"),
	})
	protected Allele allele;
	
	@OneToOne
	@JoinColumn(name = "pipeline_id")
	protected Pipeline pipeline;

	@OneToOne(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "procedure_id")
	protected Procedure procedure;
	
	@OneToOne(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "parameter_id")
	protected Parameter parameter;

	@Transient
	protected String phenotypingCenter;

	@Transient
	private double colorIndex;
	
	public PhenotypeCallSummary() {
		
	}

	public String getPhenotypingCenter(){
		return phenotypingCenter;
	}
	
	public void setPhenotypeingCenter(String phenotypingCenter){
		this.phenotypingCenter = phenotypingCenter;
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the externalId
	 */
	public Integer getExternalId() {
		return externalId;
	}

	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(Integer externalId) {
		this.externalId = externalId;
	}

	/**
	 * @return the datasource
	 */
	public Datasource getDatasource() {
		return datasource;
	}

	/**
	 * @param datasource the datasource to set
	 */
	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}	
	
	/**
	 * @return the organisation
	 */
	public Organisation getOrganisation() {
		return organisation;
	}

	/**
	 * @param organisation the organisation to set
	 */
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	/**
	 * @return the gene
	 */
	public GenomicFeature getGene() {
		return gene;
	}

	/**
	 * @param gene the gene to set
	 */
	public void setGene(GenomicFeature gene) {
		this.gene = gene;
	}

	/**
	 * @return the phenotypeTerm
	 */
	public OntologyTerm getPhenotypeTerm() {
		return phenotypeTerm;
	}

	/**
	 * @param phenotypeTerm the phenotypeTerm to set
	 */
	public void setPhenotypeTerm(OntologyTerm phenotypeTerm) {
		this.phenotypeTerm = phenotypeTerm;
	}

	/**
	 * @return the strain
	 */
	public Strain getStrain() {
		return strain;
	}

	/**
	 * @param strain the strain to set
	 */
	public void setStrain(Strain strain) {
		this.strain = strain;
	}

	/**
	 * @return the allele
	 */
	public Allele getAllele() {
		return allele;
	}

	/**
	 * @param allele the allele to set
	 */
	public void setAllele(Allele allele) {
		this.allele = allele;
	}

	/**
	 * @return the pipeline
	 */
	public Pipeline getPipeline() {
		return pipeline;
	}

	/**
	 * @param pipeline the pipeline to set
	 */
	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	/**
	 * @return the procedure
	 */
	public Procedure getProcedure() {
		return procedure;
	}

	/**
	 * @param procedure the procedure to set
	 */
	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	/**
	 * @return the parameter
	 */
	public Parameter getParameter() {
		return parameter;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the sex
	 */
	public SexType getSex() {
		return sex;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setSex(SexType sex) {
		this.sex = sex;
	}

	/**
	 * @return the zygosity
	 */
	public ZygosityType getZygosity() {
		return zygosity;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setZygosity(ZygosityType zygosity) {
		this.zygosity = zygosity;
	}

	/**
	 * @return the pValue
	 */
	public float getpValue() {
		return pValue;
	}

	/**
	 * @param pValue the pValue to set
	 */
	public void setpValue(float pValue) {
		this.pValue = pValue;
	}

	/**
	 * @return the effectSize
	 */
	public float getEffectSize() {
		return effectSize;
	}

	/**
	 * @param effectSize the effectSize to set
	 */
	public void setEffectSize(float effectSize) {
		this.effectSize = effectSize;
	}

	/**
	 * @return the colorIndex
	 */
	public double getColorIndex() {
		return colorIndex;
	}

	/**
	 * @param colorIndex the colorIndex to set
	 */
	public void setColorIndex(double colorIndex) {
		this.colorIndex = colorIndex;
	}
	
	/**
	 * Return a -Log10 value to generate a scale
	 * @return -Math.log10(pValue)
	 */
	public double getLogValue() {
		if (pValue < 1E-20) {
			return -Math.log10(1E-20);
		}
		return -Math.log10(pValue);
	}	

}
