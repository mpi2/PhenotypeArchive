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
package uk.ac.ebi.phenotype.data.europhenome;

import uk.ac.ebi.phenotype.pojo.ZygosityType;

/**
 * 
 * Utilities to handle EuroPhenome data
 * @author Gautier Koscielny
 * @see uk.ac.ebi.phenotype.web.controller.GenesController
 */

public class Utilities {

	public static String getZygosity(ZygosityType zygosity) {
		
	  switch (zygosity) {
	  	case homozygote: return "Hom";
	  	case heterozygote: return "Het";
	  	case hemizygote: return "Hemi";
	  }
	  return "All";
	}
}
