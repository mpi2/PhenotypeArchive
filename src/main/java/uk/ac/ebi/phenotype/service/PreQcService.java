package uk.ac.ebi.phenotype.service;

import org.springframework.stereotype.Service;


public class PreQcService extends GenotypePhenotypeService {

	public PreQcService(String solrUrl) {
		super(solrUrl);
	}

}
