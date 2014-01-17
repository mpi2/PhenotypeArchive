package uk.ac.ebi.phenotype.stats.graphs;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;

import uk.ac.ebi.phenotype.stats.ExperimentService;
import uk.ac.ebi.phenotype.stats.ObservationService;

public class GraphUtils {

	ExperimentService experimentService;
	public GraphUtils(ExperimentService experimentService) {
		this.experimentService=experimentService;
	}
	public Set<String> getGraphUrls(String acc,
			String parameterStableId, List<String> genderList, List<String> zyList) throws SolrServerException {
		Set<String>urls=new HashSet<String>(); //each url should be unique and so we use a set
		Map<String, List<String>> keyList = experimentService.getExperimentKeys(acc, parameterStableId);
            
            //for each parameter we want the unique set of urls to make ajax requests for experiments
            String seperator="&";
            String accessionAndParam="accession="+acc+seperator+"parameterId="+parameterStableId;
            //add  sex and zyg
            String zygosities="";
	       
            for(String zyg: zyList) {
            	zygosities+="&zygosity="+zyg;
            }
            accessionAndParam+=zygosities;
            List <String>centersList=keyList.get(ObservationService.ExperimentField.PHENOTYPING_CENTER);
            List <String>strains=keyList.get(ObservationService.ExperimentField.STRAIN);
            List<String> metaDataGroupStrings=keyList.get(ObservationService.ExperimentField.METADATA_GROUP);
            
            for(String center:centersList) {
            	for(String strain:strains) {
            		for(String metaGroup: metaDataGroupStrings) {
            			 for(String sex:genderList) {
            			urls.add(accessionAndParam+seperator+"&gender="+sex+seperator+ObservationService.ExperimentField.PHENOTYPING_CENTER+"="+center+seperator+ObservationService.ExperimentField.STRAIN+"="+strain+seperator+ObservationService.ExperimentField.METADATA_GROUP+"="+metaGroup);
            			 }
            		}
            	}
            }
            for(String url:urls) {
            	System.out.println("graph url="+url);
            }
            
            return urls;
	}
}
