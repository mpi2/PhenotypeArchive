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
package uk.ac.ebi.phenotype.stats;

/**
 * Constants for stats reused across the project.
 */

public class Constants {

	/**
	 * Prevents from creating Constants objects
	 */
    private Constants() {
    }

    /**
     * The significant threshold is used across the project
     */
    public static final double SIGNIFICANT_P_VALUE = 1.00E-4; // 0.0001
    public static final String SIGNIFICANT_P_VALUE_HTML = "1.00x10<sup>-4</sup>";
    public static final String SIGNIFICANT_P_VALUE_TEXT = "1.00x10-4";
    public static final String MINUS_LOG10_HTML = "-Log<sub>10</sub>";
    
    /**
     * Return the HTML representation of -Log10(value)
     * @param value input value
     * @return a HMTL representation of the -Log10(value)
     */
    public String getMinusLog10Html(double value) {
    	return MINUS_LOG10_HTML + "(" + value + ")";
    }
    
}