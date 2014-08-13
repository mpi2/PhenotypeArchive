package uk.ac.ebi.phenotype.stats.graphs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.eclipse.jetty.util.log.Log;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.service.ExperimentService;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

public class GraphUtils {
 
private static final Logger log = Logger.getLogger(GraphUtils.class);
	ExperimentService experimentService;
	public GraphUtils(ExperimentService experimentService) {
		this.experimentService=experimentService;
	}
	public Set<String> getGraphUrls(String acc,
			String parameterStableId, List<String> pipelineStableIds, List<String> genderList, List<String> zyList, List<String> phenotypingCentersList, List<String> strainsParams, List<String> metaDataGroup, boolean scatter, List<String>alleleAccession) throws SolrServerException {
		
			Set<String>urls=new LinkedHashSet<String>(); //each url should be unique and so we use a set
			Map<String, List<String>> keyList = experimentService.getExperimentKeys(acc, parameterStableId, pipelineStableIds, phenotypingCentersList, strainsParams, metaDataGroup, alleleAccession);
            List <String>centersList=keyList.get(ObservationDTO.PHENOTYPING_CENTER);
            List <String>strains=keyList.get(ObservationDTO.STRAIN_ACCESSION_ID);
            List<String> metaDataGroupStrings=keyList.get(ObservationDTO.METADATA_GROUP); 
            List<String> alleleAccessionStrings=keyList.get(ObservationDTO.ALLELE_ACCESSION_ID); 
            List<String> pipelineStableIdStrings=keyList.get(ObservationDTO.PIPELINE_STABLE_ID); 
            //System.out.println("metaDataGroupStrings"+metaDataGroupStrings);
//            if(metaDataGroupStrings==null){
//                metaDataGroupStrings=new ArrayList<String>();
//                metaDataGroupStrings.add("");
//            }
                //for each parameter we want the unique set of urls to make ajax requests for experiments
                String seperator="&";
                String accessionAndParam="accession="+acc+seperator+"parameter_stable_id="+parameterStableId;
                
                String phenoCenterString="";
//    	       for(String phenoCString: phenotypingCentersList) {
//    	    	  phenoCenterString+= seperator+ObservationService.PHENOTYPING_CENTER+"="+phenoCString;
//    	       }
//    	       if(phenotypingCentersList.size()>0) {
//    	    	   //if phenotype centers specified in url then just set the centerlist to this and the phenoCenterString should be set correctly above???
//    	    	   centersList=phenotypingCentersList;
//    	       }
    	    	                   
            String genderString="";
            for(String sex:genderList) {
            	genderString+=seperator+"gender="+sex;
            }
            if(scatter) {
            	accessionAndParam+=seperator+"scatter="+scatter;
            }
            //if not a phenotyping center returned in the keys for this gene and param then don't return a url
            if(centersList==null||centersList.isEmpty()) {
                log.debug("no centers specified returning empty list");
            	return urls;
            }
            
            for(String zyg: zyList) {    
		         for(String pipeStableId: pipelineStableIdStrings) {
		            for(String center:centersList) {
		            	try {
							center=URLEncoder.encode(center, "UTF-8");//encode the phenotype center to get around harwell spaces
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	for(String strain:strains) {
		                    for(String alleleAcc: alleleAccessionStrings){//one allele accession for each graph url created as part of unique key- pielineStableId as well?????
		                        String alleleAccessionString="&"+ObservationDTO.ALLELE_ACCESSION_ID+"="+alleleAcc;
		            		if(metaDataGroupStrings!=null){
		                            for(String metaGroup: metaDataGroupStrings) {
		            			
		            			urls.add(accessionAndParam+alleleAccessionString+"&zygosity="+zyg+genderString+seperator+ObservationDTO.PHENOTYPING_CENTER+"="+center+""+seperator+ObservationDTO.STRAIN_ACCESSION_ID+"="+strain+seperator+ObservationDTO.PIPELINE_STABLE_ID+":"+pipeStableId+seperator+ObservationDTO.METADATA_GROUP+"="+metaGroup);
		            			
		            		}
		                        }
		                        else{
		                            //if metadataGroup is null then don't add it to the request
		                            urls.add(accessionAndParam+alleleAccessionString+"&zygosity="+zyg+genderString+seperator+ObservationDTO.PHENOTYPING_CENTER+"="+center+seperator+ObservationDTO.STRAIN_ACCESSION_ID+"="+strain+seperator+ObservationDTO.PIPELINE_STABLE_ID+":"+pipeStableId);
		            			
		                        }
		                    }
		            	}
		            }
		         }
            }
//            for(String url:urls) {
//            	System.out.println("graph url!!!="+url);
//            }
            
            return urls;
	}
	
	public static Map<String,String>getUsefulStrings(BiologicalModel expBiologicalModel) {
		Map<String,String> usefulStrings=new HashMap<String, String>();
		if(expBiologicalModel==null) {
			usefulStrings.put("allelicComposition", "unknown");
			usefulStrings.put("geneticBackground", "unknown");
			usefulStrings.put("symbol", "unknown");
		
		}else {
		String allelicCompositionString=expBiologicalModel.getAllelicComposition();
		String symbol=expBiologicalModel.getAlleles().get(0).getSymbol();
		String geneticBackgroundString=expBiologicalModel.getGeneticBackground();

		usefulStrings.put("allelicComposition", allelicCompositionString);
		usefulStrings.put("geneticBackground", geneticBackgroundString);
		usefulStrings.put("symbol", symbol);
		}
		return usefulStrings;
	}
}
