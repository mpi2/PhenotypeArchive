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
package uk.ac.ebi.phenotype.data.impress;

import org.apache.log4j.Logger;

import uk.ac.ebi.phenotype.data.impress.ParameterDataType;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;

/**
 * 
 * Utilities provide a set of utility methods for IMPRESS client
 * @author Gautier Koscielny
 * @see Parameter
 */

public class Utilities {

	protected static Logger logger = Logger.getLogger(Utilities.class);
	
	public static ObservationType checkType(Parameter p) {
		return checkType(p, null);
	}
	
	public static ObservationType checkType(Parameter p, String value) {
		ObservationType observationType = null;

		Float valueToInsert = 0.0f;

		String datatype = p.getDatatype();
		if (ParameterDataType.MAPPING.containsKey(p.getStableId())) {
			datatype = ParameterDataType.MAPPING.get(p.getStableId());
		}

		if (p.isMetaDataFlag()) {

			observationType = ObservationType.metadata;

		} else {
						
			if (p.isOptionsFlag()) {

				observationType = ObservationType.categorical;

			} else {

				if (datatype.equals("TEXT")) {

					observationType = ObservationType.text;

				} else if (datatype.equals("BOOL")) {

					observationType = ObservationType.categorical;

				} else if (datatype.equals("FLOAT") || datatype.equals("INT")) {

					if (p.isIncrementFlag()) {

						observationType = ObservationType.time_series;

					} else {

						observationType = ObservationType.unidimensional;

					}

					try {
						if (value != null)
							valueToInsert = Float.valueOf(value);
					} catch (NumberFormatException ex) {
						logger.info("Invalid float value: " + value);
						//TODO need to throw an exception!
					}

				} else if (datatype.equals("IMAGE") || (datatype.equals("") && p.getName().contains("images"))) {

					observationType = ObservationType.image_record;

				} else if (datatype.equals("") && !p.isOptionsFlag() && !p.getName().contains("images")) {



					// is that a number or a category?
					try {
						// check whether it's null
						if (value != null && !value.equals("null")) {

							valueToInsert = Float.valueOf(value);
						}
						if (p.isIncrementFlag()) {
							observationType = ObservationType.time_series;
						} else {
							observationType = ObservationType.unidimensional;
						}

					} catch (NumberFormatException ex) {
						observationType = ObservationType.categorical;
					}
				}
			}
		}

		return observationType;
	}
}
