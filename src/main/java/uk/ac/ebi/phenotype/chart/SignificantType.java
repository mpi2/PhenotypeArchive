/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.chart;

public enum SignificantType {
	none("No significant change"),
	cannot_classify("Can not differentiate genders"),
	both_equally("Both genders equally"),
	female_only("Female only"),
	male_only("Male only"),
	female_greater("Different effect size, females greater"),
	male_greater("Different effect size, males greater"),
	different_directions("Female and male different directions"),
	one_genotype_tested("If phenotype is significant it is for the one genotype tested")
	;

	private final String text;
	SignificantType(String text) {
		this.text = text;
	}

	public String toString() {
		return text;
	}
	
	/**
	 * convenience method for bean access from jsp
	 * @return
	 */
	public String getText(){
		return this.text;
	}

	public static SignificantType fromString(String text) {
		if (text != null) {
			for (SignificantType b : SignificantType.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}

	public static SignificantType getValue(String databaseSignificanceType){
		switch(databaseSignificanceType){
		case "If phenotype is significant - both sexes equally":
			return both_equally;
		case "If phenotype is significant - different direction for the sexes":
			return different_directions;
		case "If phenotype is significant - different size as females greater":
			return female_greater;
		case "If phenotype is significant - different size as males greater":
			return male_greater;
		case "If phenotype is significant - females only":
			return female_only;
		case "If phenotype is significant - males only":
			return male_only;
		case "If phenotype is significant it is for the one genotype tested":
			return one_genotype_tested;
		}

		return null;
	}
	
}
