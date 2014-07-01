package uk.ac.ebi.phenotype.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Repository;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.ObservationService;

@Repository
public class ParameterToGeneMap {
/**
 * Links each parameter to the genes that have it measured (in at least one allele). Can query those by sex.
 * HashMap <parameter_stable_id, ArrayList<marker_accession_id>
 */

	HashMap<String , ArrayList<String>> maleParamToGene = null;
	HashMap<String , ArrayList<String>> femaleParamToGene = null;


	public ParameterToGeneMap(){
	}

	public void fillMaps(ObservationService observationService){
		System.out.println("initializing...");
		// for all parameters
		try {
			maleParamToGene = observationService.getParameterToGeneMap(SexType.male);
			femaleParamToGene = observationService.getParameterToGeneMap(SexType.female);
		} catch (SolrServerException e) {
			e.printStackTrace();
			System.out.println("Error");
		}
	}
	
	public HashMap<String , ArrayList<String>> getMaleMap(ObservationService observationService){
		if (maleParamToGene == null){
			fillMaps(observationService);
		}
			
		return maleParamToGene;
	}

	
	public HashMap<String , ArrayList<String>> getFemaleMap(ObservationService observationService){
		if (femaleParamToGene == null){
			fillMaps(observationService);
		}
		return femaleParamToGene;
	}
	
	public Set<String> getTestedGenesByParameterSex( List<String> parameters, SexType sex, ObservationService observationService){
		HashSet<String> res = new HashSet<>();
		if (maleParamToGene == null || femaleParamToGene == null){
			fillMaps(observationService);
		}
		if (sex == null || sex.equals(SexType.female) ){
			for (String p : parameters){
				if (femaleParamToGene.containsKey(p)){
					res.addAll(femaleParamToGene.get(p));
				}
			}
		}
		if (sex == null || sex.equals(SexType.male) ) {
			for (String p : parameters){
				if (maleParamToGene.containsKey(p)){
					res.addAll(maleParamToGene.get(p));
				}
			}
		}
		return res; 
	}
}
