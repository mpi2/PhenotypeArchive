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
 * Type of annotation represented in the database.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since January 2013
 * @see PhenotypeAnnotation
 */

public enum PhenotypeAnnotationType {
	
	abnormal, abnormal_specific, increased, decreased, inferred, trait;

	public static final PhenotypeAnnotationType find(String type) {
		String lcType = type.toLowerCase();
		if (lcType.equals("abnormal")) {
			return PhenotypeAnnotationType.abnormal;
		} else if (lcType.equals("abnormal_specific")) {
			return PhenotypeAnnotationType.abnormal_specific;
		} else if (lcType.equals("increased")) {
			return PhenotypeAnnotationType.increased;
		} else if (lcType.equals("decreased")) {
			return PhenotypeAnnotationType.decreased;
		} else if (lcType.equals("inferred")) {
			return PhenotypeAnnotationType.inferred;
		} else if (lcType.equals("trait")) {
			return PhenotypeAnnotationType.trait;
		}
		return PhenotypeAnnotationType.abnormal;
	}
}
