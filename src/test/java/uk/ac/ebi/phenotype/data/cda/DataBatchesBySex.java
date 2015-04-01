package uk.ac.ebi.phenotype.data.cda;

import uk.ac.ebi.phenotype.enumeration.BatchClassification;

import java.util.HashSet;
import java.util.Set;
import com.google.common.collect.Sets;

/**
 * Created by jmason on 30/03/2015.
 */
public class DataBatchesBySex {
	private Set<String> maleBatches = new HashSet<>();
	private Set<String> femaleBatches = new HashSet<>();

	public void addMaleBatch(String batch) { maleBatches.add(batch); }
	public void addFemaleBatch(String batch) { femaleBatches.add(batch); }

	public Integer getMaleBatchCount() { return maleBatches.size(); }
	public Integer getFemaleBatchCount() { return maleBatches.size(); }

	/*
	    if male_batches == 0 or female_batches == 0:
	        batch = "One sex only"
	    elif both_batches == 1:
	        batch = "One batch"
	    elif both_batches <= 3:
	        batch = "Low batch"
	    elif male_batches >= 3 and female_batches >= 2:
	        batch = "Multi batch"
	    elif female_batches >= 3 and male_batches >= 2:
	        batch = "Multi batch"
	    else:
	        batch = "Low batch"
	 */
	public BatchClassification getBatchClassification() {


		if (maleBatches.size()==0 && femaleBatches.size()>0 || false)
		if (maleBatches.size()==1 && femaleBatches.size()==1 &&
			Sets.intersection(maleBatches, femaleBatches).size()==1) {
			return BatchClassification.one_batch;
		}

	}
}
