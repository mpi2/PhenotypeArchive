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
package uk.ac.ebi.phenotype.bean;

/**
 * 
 * Represents a genomic feature as a bean (not a plain object representation).
 * It's a lightweight representation excluding relationships to other objects.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="genomicfeature")
public class GenomicFeatureBean {

	String accession;
	String symbol;
	String name;
	/**
	 * @return the accession
	 */
	public String getAccession() {
		return accession;
	}
	/**
	 * @param accession the accession to set
	 */
	public void setAccession(String accession) {
		this.accession = accession;
	}
	/**
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}
	/**
	 * @param symbol the symbol to set
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
