/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.ebi.phenotype.data.impress;

import org.apache.log4j.Logger;

import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;

/**
 *
 * Utilities provide a set of utility methods for IMPRESS client
 *
 * @author Gautier Koscielny
 * @see Parameter
 */
public class Utilities {

    protected static Logger logger = Logger.getLogger(Utilities.class);

    /**
     * Returns the observation type based on the parameter, when the parameter
     * has a valid dataType.
     *
     * @param parameter a valid <code>Parameter</code> instance
     * @return The observation type based on the parameter, when the parameter
     * has a valid data type (<code>parameter.getDatatype()</code>).
     *
     * <b>NOTE: if the parameter does not have a valid data type, this function
     * may return incorrect results. When the parameter datatype is unknown,
     * call <code>checktype(Parameter p, String value)</code> with a sample data
     * value to get the correct observation type.</b>
     */
    public static ObservationType checkType(Parameter parameter) {
        return checkType(parameter, null);
    }

    /**
     * Returns the observation type based on the parameter and a sample
     * parameter value.
     *
     * @param parameter a valid <code>Parameter</code> instance
     * @param value a string representing parameter sample data (e.g. a floating
     * point number or anything else).
     * @return The observation type based on the parameter and a sample
     * parameter value. If <code>value</code> is a floating point number and
     * <code>parameter</code> does not have a valid data type,
     * <code>value</code> is used to disambiguate the graph type: the
     * observation type will be either <i>time_series</i> or
     * <i>unidimensional</i>; otherwise, it will be interpreted as
     * <i>categorical</i>.
     */
    public static ObservationType checkType(Parameter parameter, String value) {
        ObservationType observationType = null;

        Float valueToInsert = 0.0f;

        String datatype = parameter.getDatatype();
        if (ParameterDataType.MAPPING.containsKey(parameter.getStableId())) {
            datatype = ParameterDataType.MAPPING.get(parameter.getStableId());
        }

        if (parameter.isMetaDataFlag()) {

            observationType = ObservationType.metadata;

        } else {

            if (parameter.isOptionsFlag()) {

                observationType = ObservationType.categorical;

            } else {

                if (datatype.equals("TEXT")) {

                    observationType = ObservationType.text;

                } else if (datatype.equals("BOOL")) {

                    observationType = ObservationType.categorical;

                } else if (datatype.equals("FLOAT") || datatype.equals("INT")) {

                    if (parameter.isIncrementFlag()) {

                        observationType = ObservationType.time_series;

                    } else {

                        observationType = ObservationType.unidimensional;

                    }

                    try {
                        if (value != null) {
                            valueToInsert = Float.valueOf(value);
                        }
                    } catch (NumberFormatException ex) {
                        logger.info("Invalid float value: " + value);
                        //TODO need to throw an exception!
                    }

                } else if (datatype.equals("IMAGE") || (datatype.equals("") && parameter.getName().contains("images"))) {

                    observationType = ObservationType.image_record;

                } else if (datatype.equals("") &&  ! parameter.isOptionsFlag() &&  ! parameter.getName().contains("images")) {

                    // is that a number or a category?
                    try {
                        // check whether it's null
                        if (value != null &&  ! value.equals("null")) {

                            valueToInsert = Float.valueOf(value);
                        }
                        if (parameter.isIncrementFlag()) {
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
