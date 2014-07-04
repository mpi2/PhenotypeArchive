package uk.ac.ebi.phenotype.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Repository;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.ObservationService;

public class ParameterToGeneMap {
/**
 * Links each parameter to the genes that have it measured (in at least one allele). Can query those by sex.
 * HashMap <parameter_stable_id, ArrayList<marker_accession_id>
 */

	Map<String , ArrayList<String>> maleParamToGene = null;
	Map<String , ArrayList<String>> femaleParamToGene = null;


	public ParameterToGeneMap(ObservationService os){
		System.out.println("\n\n passed the observation map\n");
		fillMaps(os);
	}
	
	public ParameterToGeneMap(){
		System.out.println("\n\nNo OS\n");
	}
	
	private void fillMaps(ObservationService observationService){
		System.out.println("Initializing ParameterToGeneMap. This will take a while...");
		// for all parameters
		try {
			maleParamToGene = observationService.getParameterToGeneMap(SexType.male);
			femaleParamToGene = observationService.getParameterToGeneMap(SexType.female);
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String , ArrayList<String>> getMaleMap(ObservationService observationService){
		if (maleParamToGene == null){
			fillMaps(observationService);
		}
		return maleParamToGene;
	}

	
	public Map<String , ArrayList<String>> getFemaleMap(ObservationService observationService){
		if (femaleParamToGene == null){
			fillMaps(observationService);
		}
		return femaleParamToGene;
	}
	
	public Set<String> getTestedGenes( List<String> parameters, SexType sex, ObservationService os){
		HashSet<String> res = new HashSet<>();
		if (femaleParamToGene == null || maleParamToGene == null){
			fillMaps(os);
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
