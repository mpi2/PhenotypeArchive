package uk.ac.ebi.phenotype.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.ObservationService;

public class ParameterToGeneMap {

    @Autowired
    ObservationService observationService;

    /**
     * Links each parameter to the genes that have it measured (in at least one
     * allele). Can query those by sex. HashMap <parameter_stable_id,
     * ArrayList<marker_accession_id>
     */

    Map<String, ArrayList<String>> maleParamToGene = null;
    Map<String, ArrayList<String>> femaleParamToGene = null;


	public ParameterToGeneMap(){
	}
    
    
    private void fillMaps() {
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

    public Set<String> getTestedGenes(List<String> parameters, SexType sex) {
        HashSet<String> res = new HashSet<>();
        if (femaleParamToGene == null || maleParamToGene == null) {
        	System.out.println("CALL FILL MAPS FROM getTestedGenes");
            fillMaps();
        }
        if (sex == null || sex.equals(SexType.female)) {
            for (String p : parameters) {
                if (femaleParamToGene.containsKey(p)) {
                    res.addAll(femaleParamToGene.get(p));
                }
            }
        }
        if (sex == null || sex.equals(SexType.male)) {
            for (String p : parameters) {
                if (maleParamToGene.containsKey(p)) {
                    res.addAll(maleParamToGene.get(p));
                }
            }
        }
        return res;
    }
}
