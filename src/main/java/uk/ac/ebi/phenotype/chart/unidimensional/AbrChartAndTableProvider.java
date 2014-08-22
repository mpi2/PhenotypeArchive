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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.chart.utils.ChartUtils;
import uk.ac.ebi.phenotype.chart.utils.Constants;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.error.SpecificExperimentException;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.ExperimentService;
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
	
	public String getChart(Integer pipelineId, String acc, List<String> genderList, List<String> zyList, Integer phenotypingCenterId, String strain, String metadataGroup, String alleleAccession){
		// get data 
    	HashMap<String, ArrayList<UnidimensionalStatsObject>> data = new HashMap(); // <control/experim, ArrayList<dataToPlot>>
    	data.put("control", new ArrayList<UnidimensionalStatsObject>() );
    	data.put("hom", new ArrayList<UnidimensionalStatsObject>() );
    	for (String parameterStableId : Constants.ABR_PARAMETERS){
    		Integer paramId = pipelineDAO.getParameterByStableId(parameterStableId).getId();
    		System.out.println("Getting experiment for " + parameterStableId);
    		try {
    			ExperimentDTO experiment = es.getSpecificExperimentDTO(paramId, pipelineId, acc, genderList, zyList, phenotypingCenterId, strain, metadataGroup, alleleAccession);
				if (experiment != null){
					data.get("control").add(getMeans("control", experiment));
					data.get("hom").add(getMeans("hom", experiment));
				}
    		} catch (SolrServerException | IOException | URISyntaxException | SpecificExperimentException e) {
				e.printStackTrace();
			}
    	}
    	
    	// We've got everything, get the chart now.
    	    	
		return getCustomChart(data);
	}
	
	public String getCustomChart(HashMap<String, ArrayList<UnidimensionalStatsObject>> data){
		// area range and line for control
		// line for mutants
		// dot for click
		
		String categories = "[\"" + StringUtils.join(Constants.ABR_PARAMETERS, "\", \"") + "\"]";
		String ranges = "[";
		String averages = "[";
		String homs = "[";
		
		for (UnidimensionalStatsObject control : data.get("control")){
			ranges += "[\'" + control.getLabel() + "\', " + (control.getMean() - control.getSd()) + ", " + (control.getMean() + control.getSd()) + "]";
			averages += "[\'" + control.getLabel() + "\', " + control.getMean() + "]";
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
			   	"title: { text: 'July temperatures' },"+			
			     " xAxis: {   categories: "  + categories + "},"+			
			     " yAxis: {   title: {    text: null  }  },"+			
			     " tooltip: {  crosshairs: true, shared: true, valueSuffix: '°C' },"+			
			     " legend: { },"+
			     " series: [{"+
				     " name: 'Temperature',"+
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
				     " name: 'Range',"+
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
		System.out.println("+++"+ chart);
		return chart;
		}
	
	public UnidimensionalStatsObject getMeans(String typeOfData, ExperimentDTO exp){
		DescriptiveStatistics stats = new DescriptiveStatistics();
		UnidimensionalStatsObject res = new UnidimensionalStatsObject();
		res.setLabel(exp.getParameterStableId());
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
