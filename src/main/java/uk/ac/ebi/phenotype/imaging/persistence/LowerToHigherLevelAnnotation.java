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
package uk.ac.ebi.phenotype.imaging.persistence;

import java.io.Serializable;

import javax.annotation.PropertyKey;
import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.Set;


/**
 * The persistent class for the lower_to_higher_level_annotation database table.
 * 
 */
@Entity
@Table(name="lower_to_higher_level_annotation")
public class LowerToHigherLevelAnnotation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TERM_ID")
	@JsonIgnore
	private String termId;
	@JsonIgnore
	@Column(name="TERM_NAME")
	private String termName;

	

	//bi-directional many-to-one association to HigherLevelAnnotation
	@NotFound(action=NotFoundAction.IGNORE)
    @ManyToOne( fetch=FetchType.EAGER)
	@JoinColumn(name="HIGHER_TERM_ID")
    //@OneToMany( fetch=FetchType.EAGER)
	private HigherLevelAnnotation higherLevelAnnotation;

    public LowerToHigherLevelAnnotation() {
    }

	public String getTermId() {
		return this.termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getTermName() {
		return this.termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}
	
	public HigherLevelAnnotation getHigherLevelAnnotation() {
		return this.higherLevelAnnotation;
	}

	public void setHigherLevelAnnotation(HigherLevelAnnotation higherLevelAnnotation) {
		this.higherLevelAnnotation = higherLevelAnnotation;
	}
	
}