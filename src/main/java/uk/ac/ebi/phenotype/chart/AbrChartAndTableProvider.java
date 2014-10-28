package uk.ac.ebi.phenotype.chart;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.error.SpecificExperimentException;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.ExperimentService;
import uk.ac.ebi.phenotype.service.ImpressService;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

		Set<ZygosityType> zygosities = null;
		// get data 
    	HashMap<String, ArrayList<UnidimensionalStatsObject>> data = new HashMap(); // <control/experim, ArrayList<dataToPlot>>
    	data.put("control", new ArrayList<UnidimensionalStatsObject>() );
    	data.put("hom", new ArrayList<UnidimensionalStatsObject>() );

		UnidimensionalStatsObject emptyObj = new UnidimensionalStatsObject();
		emptyObj.setMean(null);
		emptyObj.setSd(null);

    	String procedureUrl = null;
    	String unit = pipelineDAO.getParameterByStableId(Constants.ABR_PARAMETERS.get(1)).getUnit();

    	for (String parameterStableId : Constants.ABR_PARAMETERS){
    		Integer paramId = pipelineDAO.getParameterByStableId(parameterStableId).getId();
    		System.out.println("Getting experiment for " + parameterStableId);
    		try {
    			ExperimentDTO experiment = es.getSpecificExperimentDTO(paramId, pipelineId, acc, genderList, zyList, phenotypingCenterId, strain, metadataGroup, alleleAccession);
			    zygosities = experiment.getZygosities();

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
    	    	
		return getCustomChart(data, procedureUrl, unit, zygosities);
	}
	
	public String getCustomChart(HashMap<String, ArrayList<UnidimensionalStatsObject>> data, String procedureLink, String unit, Set<ZygosityType> zygosities){
		// area range and line for control
		// line for mutants
		// dot for click
		
		JSONArray categories = new JSONArray();
		String title = "Evoked ABR Threshold (6, 12, 18, 24, 30 kHz)";

		JSONArray controlSD = new JSONArray(); // whiskers +/- sd
		JSONArray homSD = new JSONArray(); // whiskers +/- sd
		JSONArray control = new JSONArray();
		JSONArray homs = new JSONArray();

		Integer decimalNumber = 2;
		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaBox);
		String empty = null;
		JSONArray emptyObj = new JSONArray();
		emptyObj.put("");
		emptyObj.put(empty);
		emptyObj.put(empty);
		
		for (String abrId: Constants.ABR_PARAMETERS){
			categories.put(pipelineDAO.getParameterByStableId(abrId).getName());
			try {
				categories.put(1, ""); // empty category with null data so that the points won't be connected
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			boolean first = true;
			
			for (UnidimensionalStatsObject c : data.get("control")){

				JSONArray obj = new JSONArray();
				obj.put(c.getLabel());
				obj.put(c.getMean());

				if(first) {
					first = false;
					control.put(obj);
					control.put(emptyObj);
					controlSD.put(obj);
					controlSD.put(emptyObj);
				} else {
					control.put(obj);

					obj = new JSONArray();
					obj.put(c.getLabel());
					if (c.getMean() != null){
						obj.put(c.getMean() - c.getSd());
						obj.put(c.getMean() + c.getSd());
					}else {
						obj.put(empty);
						obj.put(empty);
					}
					controlSD.put(obj);
				}
			}

			first = true;
			for (UnidimensionalStatsObject hom : data.get("hom")){
				JSONArray obj = new JSONArray();
				obj.put(hom.getLabel());
				obj.put(hom.getMean());

				if(first) {
					first = false;
					homs.put(obj);
					homs.put(emptyObj);

					obj = new JSONArray();
					obj.put(hom.getLabel());
					// in case data is missing for one parameter
					if (hom.getMean() != null){
						obj.put(hom.getMean() - hom.getSd());
						obj.put(hom.getMean() + hom.getSd());
					}else{
						obj.put(empty);
						obj.put(empty);
					}
					homSD.put(obj);
					homSD.put(emptyObj);

				} else {
					homs.put(obj);

					obj = new JSONArray();
					obj.put(hom.getLabel());
					// in case data is missing for one parameter
					if (hom.getMean() != null){
						obj.put(hom.getMean() - hom.getSd());
						obj.put(hom.getMean() + hom.getSd());
					}else{
						obj.put(empty);
						obj.put(empty);
					}
					homSD.put(obj);
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String chart =
			"$(function () {"+
				"$('#chartABR').highcharts({"+
				"  title: { text: '" + title + "' },"+
				"  subtitle: {  useHTML: true,  text: '" + procedureLink + "'}, " +
				"  xAxis: {   categories: "  + categories + "},"+
				"  yAxis: {   title: {    text: '" + unit + "'  }  },"+
				"  tooltip: {valueSuffix: ' " + unit + "', shared:true },"+
				"  legend: { },"+
				"  credits: { enabled: false },  " +
				"  series: [ {"+
				"    name: '"+ StringUtils.capitalize(StringUtils.join(zygosities, ", "))+"',"+
				"    data: " + homs.toString() + "," +
				"    zIndex: 1,"+
				"    color: "+ colors.get(0) +","+
				"    tooltip: { pointFormat: '<span style=\"font-weight: bold; color: {series.color}\">{series.name}</span>: <b>{point.y:."+decimalNumber+"f}</b>' }," +
				"  }, {"+
				"    name: '"+ StringUtils.capitalize(StringUtils.join(zygosities, ", "))+" SD',"+
				"    data: " + homSD.toString() + "," +
				"    type: 'errorbar',"+
				"    linkedTo: ':previous',"+
				"    color: "+ colors.get(0) +","+
				"    tooltip: { pointFormat: ' (SD: {point.low:."+decimalNumber+"f} - {point.high:."+decimalNumber+"f} )<br/>', shared:true }" +
				"  },{"+
				"    name: 'Control',"+
				"    data: " + control.toString() + "," +
				"    zIndex: 1,"+
				"    color: "+ colors.get(1) +", " +
				"    tooltip: { pointFormat: '<span style=\"font-weight: bold; color: {series.color}\">{series.name}</span>: <b>{point.y:."+decimalNumber+"f}</b>' }" +
				"  }, {"+
				"    name: 'Control SD',"+
				"    data: " + controlSD.toString() + "," +
				"    type: 'errorbar',"+
				"    linkedTo: ':previous',"+
				"    color: "+ colors.get(1) +","+
				"    tooltip: { pointFormat: ' (SD: {point.low:."+decimalNumber+"f} - {point.high:."+decimalNumber+"f}) <br/>' }" +
				"  } ]"+
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
