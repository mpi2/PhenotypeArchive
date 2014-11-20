package uk.ac.ebi.phenotype.chart;

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
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.ExperimentService;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

public class GraphUtils {

	private static final Logger log = Logger.getLogger(GraphUtils.class);
	ExperimentService experimentService;


    @Autowired
    private static PhenotypePipelineDAO pipelineDAO;
    
	public GraphUtils(ExperimentService experimentService) {

		this.experimentService = experimentService;
	}


	public Set<String> getGraphUrls(String acc,
	Parameter parameter, List<String> pipelineStableIds, List<String> genderList, List<String> zyList, List<String> phenotypingCentersList,
	List<String> strainsParams, List<String> metaDataGroup, ChartType chartType, List<String> alleleAccession)
	throws SolrServerException {

		// each url should be unique and so we use a set
		Set<String> urls = new LinkedHashSet<String>();
		Map<String, List<String>> keyList = experimentService.getExperimentKeys(acc, parameter.getStableId(), pipelineStableIds, phenotypingCentersList, strainsParams, metaDataGroup, alleleAccession);
		List<String> centersList = keyList.get(ObservationDTO.PHENOTYPING_CENTER);
		List<String> strains = keyList.get(ObservationDTO.STRAIN_ACCESSION_ID);
		List<String> metaDataGroupStrings = keyList.get(ObservationDTO.METADATA_GROUP);
		List<String> alleleAccessionStrings = keyList.get(ObservationDTO.ALLELE_ACCESSION_ID);
		List<String> pipelineStableIdStrings = keyList.get(ObservationDTO.PIPELINE_STABLE_ID);
		// if(metaDataGroupStrings==null){
		// metaDataGroupStrings=new ArrayList<String>();
		// metaDataGroupStrings.add("");
		// }
		// for each parameter we want the unique set of urls to make ajax
		// requests for experiments
		String seperator = "&";
		String accessionAndParam = "accession=" + acc + seperator + "parameter_stable_id=" + parameter.getStableId();

		String phenoCenterString = "";
		// for(String phenoCString: phenotypingCentersList) {
		// phenoCenterString+=
		// seperator+ObservationService.PHENOTYPING_CENTER+"="+phenoCString;
		// }
		// if(phenotypingCentersList.size()>0) {
		// //if phenotype centers specified in url then just set the centerlist
		// to this and the phenoCenterString should be set correctly above???
		// centersList=phenotypingCentersList;
		// }

		String genderString = "";
		for (String sex : genderList) {
			genderString += seperator + "gender=" + sex;
		}
		if (chartType != null) {
			accessionAndParam += seperator + "chart_type=" + chartType;
			if(chartType==ChartType.PIE){
				urls.add("chart_type=PIE&parameter_stable_id=IMPC_VIA_001_001");
				return urls;
			}
		}else {
			// find out the default chart type
			accessionAndParam += seperator + "chart_type=" + getDefaultChartType(parameter);			
		}
		// if not a phenotyping center returned in the keys for this gene and
		// param then don't return a url
		if (centersList == null || centersList.isEmpty()) {
			log.debug("no centers specified returning empty list");
			return urls;
		}

		for (String zyg : zyList) {
			for (String pipeStableId : pipelineStableIdStrings) {
				for (String center : centersList) {
					try {
						// encode the phenotype center to get around harwell
						// spaces
						center = URLEncoder.encode(center, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for (String strain : strains) {
						// one allele accession for each graph url created as
						// part of unique key- pielineStableId as well?????
						for (String alleleAcc : alleleAccessionStrings) {
							String alleleAccessionString = "&" + ObservationDTO.ALLELE_ACCESSION_ID + "=" + alleleAcc;
							if (metaDataGroupStrings != null) {
								for (String metaGroup : metaDataGroupStrings) {
									urls.add(accessionAndParam + alleleAccessionString + "&zygosity=" + zyg + genderString + seperator + ObservationDTO.PHENOTYPING_CENTER + "=" + center + "" + seperator + ObservationDTO.STRAIN_ACCESSION_ID + "=" + strain + seperator + ObservationDTO.PIPELINE_STABLE_ID + "=" + pipeStableId + seperator + ObservationDTO.METADATA_GROUP + "=" + metaGroup);

								}
							}
							else {
								// if metadataGroup is null then don't add it to
								// the request
								urls.add(accessionAndParam + alleleAccessionString + "&zygosity=" + zyg + genderString + seperator + ObservationDTO.PHENOTYPING_CENTER + "=" + center + seperator + ObservationDTO.STRAIN_ACCESSION_ID + "=" + strain + seperator + ObservationDTO.PIPELINE_STABLE_ID + "=" + pipeStableId);

							}
						}
					}
				}
			}
		}
		// for(String url:urls) {
		// System.out.println("graph url!!!="+url);
		// }

		return urls;
	}

	
	public static ChartType getDefaultChartType(Parameter parameter){
		
		if (Constants.ABR_PARAMETERS.contains(parameter.getStableId())){
			
			return ChartType.UNIDIMENSIONAL_ABR_PLOT;
			
		}else if(parameter.getStableId().equals("IMPC_VIA_001_001")){
			return ChartType.PIE;
			
		}else{

	        ObservationType observationTypeForParam = Utilities.checkType(parameter);
	        
	        switch (observationTypeForParam) {

                case unidimensional:
                   return ChartType.UNIDIMENSIONAL_BOX_PLOT;

                case categorical:
                	return ChartType.CATEGORICAL_STACKED_COLUMN;

                case time_series:
                	return ChartType.TIME_SERIES_LINE;
	        }
		}
		return null;
	}

	public static Map<String, String> getUsefulStrings(BiologicalModel expBiologicalModel) {

		Map<String, String> usefulStrings = new HashMap<String, String>();
		if (expBiologicalModel == null) {
			usefulStrings.put("allelicComposition", "unknown");
			usefulStrings.put("geneticBackground", "unknown");
			usefulStrings.put("symbol", "unknown");

		} else {
			String allelicCompositionString = expBiologicalModel.getAllelicComposition();
			String symbol = expBiologicalModel.getAlleles().get(0).getSymbol();
			String geneticBackgroundString = expBiologicalModel.getGeneticBackground();

			usefulStrings.put("allelicComposition", allelicCompositionString);
			usefulStrings.put("geneticBackground", geneticBackgroundString);
			usefulStrings.put("symbol", symbol);
		}
		return usefulStrings;
	}
}
