package uk.ac.ebi.phenotype.chart.unidimensional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.chart.utils.ChartUtils;
import uk.ac.ebi.phenotype.chart.utils.Constants;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.error.SpecificExperimentException;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.ExperimentService;
import uk.ac.ebi.phenotype.service.ImpressService;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

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
    
	@Autowired
	ImpressService impressService;

	
	public String getChart(Integer pipelineId, String acc, List<String> genderList, List<String> zyList, Integer phenotypingCenterId, String strain, String metadataGroup, String alleleAccession){
		
		// get data 
    	HashMap<String, ArrayList<UnidimensionalStatsObject>> data = new HashMap(); // <control/experim, ArrayList<dataToPlot>>
    	data.put("control", new ArrayList<UnidimensionalStatsObject>() );
    	data.put("hom", new ArrayList<UnidimensionalStatsObject>() );
		UnidimensionalStatsObject emptyObj = new UnidimensionalStatsObject();
    	String procedureUrl = null;
    	
    	emptyObj.setMean(null);
    	emptyObj.setSd(null);
    	
    	for (String parameterStableId : Constants.ABR_PARAMETERS){
    		Integer paramId = pipelineDAO.getParameterByStableId(parameterStableId).getId();
    		System.out.println("Getting experiment for " + parameterStableId);
    		try {
    			ExperimentDTO experiment = es.getSpecificExperimentDTO(paramId, pipelineId, acc, genderList, zyList, phenotypingCenterId, strain, metadataGroup, alleleAccession);
				if (experiment != null){
					if (procedureUrl == null){
						procedureUrl = impressService.getAnchorForProcedure(experiment.getProcedureName(), experiment.getProcedureStableId());
					}
					data.get("control").add(getMeans("control", experiment));
					data.get("hom").add(getMeans("hom", experiment));
				}
				else {
					emptyObj.setLabel(pipelineDAO.getParameterByStableId(parameterStableId).getName());
					data.get("control").add(emptyObj);
					data.get("hom").add(emptyObj);
				}
    		} catch (SolrServerException | IOException | URISyntaxException | SpecificExperimentException e) {
				e.printStackTrace();
			}
    	}
    	
    	// We've got everything, get the chart now.
    	    	
		return getCustomChart(data, procedureUrl);
	}
	
	public String getCustomChart(HashMap<String, ArrayList<UnidimensionalStatsObject>> data, String procedureLink){
		// area range and line for control
		// line for mutants
		// dot for click
		
		JSONArray categories = new JSONArray();
		String title = "Evoked ABR Threshold (6-12-18-24-30)";
		String ranges = "[";
		String averages = "[";
		String homs = "[";
		
		for (String abrId: Constants.ABR_PARAMETERS){
			categories.put(pipelineDAO.getParameterByStableId(abrId).getName());
		}
		
		for (UnidimensionalStatsObject control : data.get("control")){
			ranges += "[\'" + control.getLabel();
			averages += "[\'"  + control.getLabel() ;
			if (control.getMean() != null){
				ranges += "\', " + (control.getMean() - control.getSd()) + ", " + (control.getMean() + control.getSd()) + "]";
				averages +=  "\', " + control.getMean() + "]";
			}else {
				ranges += "\', null, null]";
				averages +=  "\', null]";
			}
			if (!data.get("control").get(data.get("control").size()-1).equals(control)){
				ranges += ",";
				averages += ",";
			}
		}
		for (UnidimensionalStatsObject hom : data.get("hom")){
			homs += "[\'" + hom.getLabel() + "\', " + hom.getMean() + "]";
			if (!data.get("hom").get(data.get("hom").size()-1).equals(hom)){
				homs += ",";
			}
		}
		ranges += "]";
		averages += "]";
		homs += "]";
		
		String chart = 
		"$(function () {"+
			"$('#chartABR').highcharts({"+
			   	 "title: { text: '" + title + "' },"+		
				 "subtitle: {  useHTML: true,  text: '" + procedureLink + "'}, " +	
			     " xAxis: {   categories: "  + categories + "},"+			
			     " yAxis: {   title: {    text: 'dBSPL'  }  },"+			
			     " tooltip: {  crosshairs: true, shared: true, valueSuffix: ' dBSPL' },"+			
			     " legend: { },"+
			     " credits: { enabled: false },  " +
			     " series: [{"+
				     " name: 'Control',"+
				     " data: " + averages + "," + 
				     " zIndex: 1,"+
				     " marker: {"+
				     	" fillColor: 'white',"+
				     	" lineWidth: 2,"+
				     	" lineColor: Highcharts.getOptions().colors[0]"+
				     " }"+
			     " }, {"+
						" name: 'Homozygotes',"+
						" data: " + homs + "," + 
						" zIndex: 1,"+
						" marker: {"+
						   	" lineWidth: 2,"+
						   	" lineColor: Highcharts.getOptions().colors[2]"+
						" }"+
				 " }, {"+
				     " name: 'SD',"+
				     " data: " + ranges + "," + 
				     " type: 'arearange',"+
				     " lineWidth: 0,"+
				     " linkedTo: ':previous',"+
				     " color: Highcharts.getOptions().colors[0],"+
				     " fillOpacity: 0.3,"+
				     " zIndex: 0"+
			     "   }]"+
			    "});"+
			"});" ;
		return chart;
		}
	
	public UnidimensionalStatsObject getMeans(String typeOfData, ExperimentDTO exp){
		
		DescriptiveStatistics stats = new DescriptiveStatistics();
		UnidimensionalStatsObject res = new UnidimensionalStatsObject();
		res.setLabel(pipelineDAO.getParameterByStableId(exp.getParameterStableId()).getName());
		Set<ObservationDTO> dataPoints = null;
		
		if (typeOfData.equals("control")){
			dataPoints = exp.getControls();
			
		}else if (typeOfData.equals("hom")) {
			dataPoints = exp.getHomozygoteMutants();	
			res.setAllele(exp.getAlleleAccession());
			res.setLine("Not control");
			res.setGeneticBackground(exp.getStrain());
		}
		if (dataPoints != null){
			for (ObservationDTO obs : dataPoints){
				stats.addValue(obs.getDataPoint());;
			}
			int decimalPlaces = ChartUtils.getDecimalPlaces(exp);
			res.setMean(ChartUtils.getDecimalAdjustedFloat(new Float(stats.getMean()), decimalPlaces));
			res.setSampleSize(dataPoints.size());
			res.setSd(ChartUtils.getDecimalAdjustedFloat(new Float(stats.getStandardDeviation()), decimalPlaces));
		}
		System.out.println(res);
		return res;
	}
}
