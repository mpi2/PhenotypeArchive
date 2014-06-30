package uk.ac.ebi.phenotype.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Repository;

import java.lang.NullPointerException;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.ObservationService;

@Repository
public class ParameterToGeneMap {
/**
 * Links each parameter to the genes that have it measured (in at least one allele). Can query those by sex.
 * HashMap <parameter_stable_id, ArrayList<marker_accession_id>
 */

	HashMap<String , ArrayList<String>> maleParamToGene = new HashMap<>();
	HashMap<String , ArrayList<String>> femaleParamToGene = new HashMap<>();

	@Autowired
	private ObservationService observationService;
	
	public ParameterToGeneMap(){
		System.out.println("initializing...");
		// for all parameters
		try {
			System.out.println("Getting each sex. Observation service != null  " + (observationService != null));

			System.out.println(" NOTE NOTE NOTE: Caught null pointer exception -jm");

			maleParamToGene = observationService.getParameterToGeneMap(SexType.male);
			femaleParamToGene = observationService.getParameterToGeneMap(SexType.female);
		} catch (SolrServerException | NullPointerException e) {
			e.printStackTrace();
			System.out.println("Error");
		}
	}
	
	public HashMap<String , ArrayList<String>> getMaleMap(){
		return maleParamToGene;
	}

	public ObservationService getObservationService() {
		return observationService;
	}

	public void setObservationService(ObservationService observationService) {
		this.observationService = observationService;
	}
	
	
	
}
