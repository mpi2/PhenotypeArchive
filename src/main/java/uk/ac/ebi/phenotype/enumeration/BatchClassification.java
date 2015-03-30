package uk.ac.ebi.phenotype.enumeration;

/**
 * Captures the possible states of the batch classification
 *
 * one_sex_only = only one sex tested
 * one_batch = all data for both males and females was gathered on the same day
 * low_batch = data was gathered on 3 or fewer days, or one sex < 3 batches or the other < 2
 * multi_batch = one sex >= 3 batches and the other >= 2 batches
 *
 */
public enum BatchClassification {

	one_sex_only,
	one_batch,
	low_batch,
	multi_batch;

	public String getName(){
		return this.toString();
	}

}
