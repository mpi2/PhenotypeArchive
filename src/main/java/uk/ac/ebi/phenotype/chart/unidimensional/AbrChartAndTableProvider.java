package uk.ac.ebi.phenotype.chart.unidimensional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.chart.utils.Constants;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.error.SpecificExperimentException;
import uk.ac.ebi.phenotype.service.ExperimentService;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;

@Service
public class AbrChartAndTableProvider {

	/*
	 * IMPC_ABR_002_001 -- click
	 * IMPC_ABR_004_001 -- 6
	 * IMPC_ABR_006_001 -- 12
	 * IMPC_ABR_008_001	-- 18
	 * IMPC_ABR_010_001 -- 24
	 * IMPC_ABR_012_001 -- 30
	 * 
	 */
	@Autowired 
	ExperimentService es;

    @Autowired
    private PhenotypePipelineDAO pipelineDAO;
	
	public String getChart(Integer pipelineId, String acc, List<String> genderList, List<String> zyList, Integer phenotypingCenterId, String strain, String metadataGroup, String alleleAccession){
		// get data 
    	List<ExperimentDTO> experimentList = new ArrayList<>();
    	for (String parameterStableId : Constants.ABR_PARAMETERS){
    		System.out.println("Is pipelineDAO null? " + (pipelineDAO == null));
    		Integer paramId = pipelineDAO.getParameterByStableId(parameterStableId).getId();
    		System.out.println("Detting experiment for " + parameterStableId);
    		try {
				experimentList.add(es.getSpecificExperimentDTO(paramId, pipelineId, acc, genderList, zyList, phenotypingCenterId, strain, metadataGroup, alleleAccession));
			} catch (SolrServerException | IOException | URISyntaxException | SpecificExperimentException e) {
				e.printStackTrace();
			}
    	}
    	
    	// process data to do means for control and experiment for each parameter
    	
    	
		return "alert(\"This is not what it seems\")";
	}
}
