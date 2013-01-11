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
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the higher_level_annotation database table.
 * 
 */
@Entity
@Table(name="higher_level_annotation")
public class HigherLevelAnnotation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TERM_ID")
	private String termId;

	@Column(name="TERM_NAME")
	private String termName;

	//bi-directional many-to-one association to LowerToHigherLevelAnnotation
	
    public HigherLevelAnnotation() {
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

	
	
}