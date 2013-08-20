package uk.ac.ebi.phenotype.stats;

public enum SignificantType {
	none("No significant change"),
	cannot_classify("Can not differentiate genders"),
	both_equally("Both genders equally"),
	female_only("Female only"),
	male_only("Male only"),
	female_greater("Different size females greater"),
	male_greater("Different size males greater"),
	different_directions("Female and male different directions")
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

}
