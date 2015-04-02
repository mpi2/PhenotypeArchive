package uk.ac.ebi.phenotype.data.cda;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.phenotype.enumeration.BatchClassification;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jmason on 30/03/2015.
 */
public class DataBatchesBySex {

	private static final Logger logger = LoggerFactory.getLogger(DataBatchesBySex.class);


	private Set<String> maleBatches = new HashSet<>();
	private Set<String> femaleBatches = new HashSet<>();

	public DataBatchesBySex(List<ObservationDTO> observations) {
		for (ObservationDTO obs : observations) {

			if (obs.getSex().equals(SexType.male.toString())) {
				maleBatches.add(obs.getDateOfExperimentString());
			}

			if (obs.getSex().equals(SexType.female.toString())) {
				femaleBatches.add(obs.getDateOfExperimentString());
			}

		}
	}

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

		logger.debug("Male batches by sex: " + StringUtils.join(maleBatches, ", "));
		logger.debug("Femle batches by sex: " + StringUtils.join(femaleBatches, ", "));
		logger.debug("Both batches by sex: " + StringUtils.join(Sets.union(maleBatches, femaleBatches), ", "));

		if ((maleBatches.size()==0 && femaleBatches.size()>0) ||
			(femaleBatches.size()==0 && maleBatches.size()>0) ) {
			return BatchClassification.one_sex_only;

		} else if ( Sets.union(maleBatches, femaleBatches).size() == 1 ) {
			return BatchClassification.one_batch;

		} else if ( Sets.union(maleBatches, femaleBatches).size() <= 3 ) {
			return BatchClassification.low_batch;

		} else if ( maleBatches.size() >=3 && femaleBatches.size() >= 2 ||
			femaleBatches.size() >=3 && maleBatches.size() >= 2 ) {
			return BatchClassification.multi_batch;
		}

		return BatchClassification.low_batch;
	}

}
