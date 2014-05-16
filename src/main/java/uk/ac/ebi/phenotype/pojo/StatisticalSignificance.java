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
 * Interface to statistical significance objects containing 
 * a pValue, an effectSize and a color index. 
 * @since May 2014
 */

public interface StatisticalSignificance {

	/**
	 * @return the pValue
	 */
	public double getpValue();

	/**
	 * @param pValue the pValue to set
	 */
	public void setpValue(double pValue);
	
	/**
	 * @return the effectSize
	 */
	public double getEffectSize();

	/**
	 * @param effectSize the effectSize to set
	 */
	public void setEffectSize(double effectSize);

	/**
	 * @return the colorIndex
	 */
	public double getColorIndex();

	/**
	 * @param colorIndex the colorIndex to set
	 */
	public void setColorIndex(double colorIndex);
	
	/**
	 * Return a -Log10 value to generate a scale
	 * @return -Math.log10(pValue)
	 */
	public double getLogValue();
	
}
