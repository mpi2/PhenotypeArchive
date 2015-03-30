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

	public BatchClassification getBatchClassification() {


		if (maleBatches.size()==0 && femaleBatches.size()>0 ||)
		if (maleBatches.size()==1 && femaleBatches.size()==1 &&
			Sets.intersection(maleBatches, femaleBatches).size()==1) {
			return BatchClassification.one_batch;
		}

	}
}
