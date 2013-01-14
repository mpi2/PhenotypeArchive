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
 * Store phenotype calls from diverse statistical pipeline on multiple datasets.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "phenotype_call_summary")
public class PhenotypeCallSummary {

	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "external_id")
	int externalId;

	@Enumerated(EnumType.STRING)
	@Column(name = "sex")
	private SexType sex;

	@Enumerated(EnumType.STRING)
	@Column(name = "zygosity")
	private ZygosityType zygosity;

	@OneToOne
	@JoinColumn(name = "external_db_id")
	private Datasource datasource;	
	
	@OneToOne
	@JoinColumn(name = "project_id")
	private Project project;	
	
	@NotFound(action=NotFoundAction.IGNORE) // phenotype_call_summary.gf_acc maybe null
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "gf_acc"),
	@JoinColumn(name = "gf_db_id"),
	})
	private GenomicFeature gene;
	
	@NotFound(action=NotFoundAction.IGNORE)
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "mp_acc"),
	@JoinColumn(name = "mp_db_id"),
	})
	private OntologyTerm phenotypeTerm;
	
	@NotFound(action=NotFoundAction.IGNORE)
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "strain_acc"),
	@JoinColumn(name = "strain_db_id"),
	})
	private Strain strain;
	
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "allele_acc"),
	@JoinColumn(name = "allele_db_id"),
	})
	private Allele allele;
	
	@OneToOne
	@JoinColumn(name = "pipeline_id")
	private Pipeline pipeline;

	@OneToOne
	@JoinColumn(name = "procedure_id")
	private Procedure procedure;
	
	@OneToOne
	@JoinColumn(name = "parameter_id")
	private Parameter parameter;

	
	public PhenotypeCallSummary() {
		
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
	public int getExternalId() {
		return externalId;
	}

	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(int externalId) {
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


}
