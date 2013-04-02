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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "phenotype_parameter_ontology_annotation")
public class ParameterOntologyAnnotation {

	/**
	 * MySQL auto increment
	 * GenerationType.AUTO won't work
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "event_type")
	private PhenotypeAnnotationType type;
	
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "option_id"),
	})
	private ParameterOption option;
	
	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "ontology_acc"),
	@JoinColumn(name = "ontology_db_id"),
	})
	private OntologyTerm ontologyTerm;

	
	/**
	 * Default constructor
	 */
	public ParameterOntologyAnnotation() {
		super();
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
	 * @return the type
	 */
	public PhenotypeAnnotationType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PhenotypeAnnotationType type) {
		this.type = type;
	}

	/**
	 * @return the option
	 */
	public ParameterOption getOption() {
		return option;
	}

	/**
	 * @param option the option to set
	 */
	public void setOption(ParameterOption option) {
		this.option = option;
	}

	/**
	 * @return the ontologyTerm
	 */
	public OntologyTerm getOntologyTerm() {
		return ontologyTerm;
	}

	/**
	 * @param ontologyTerm the ontologyTerm to set
	 */
	public void setOntologyTerm(OntologyTerm ontologyTerm) {
		this.ontologyTerm = ontologyTerm;
	}
	
	
}
