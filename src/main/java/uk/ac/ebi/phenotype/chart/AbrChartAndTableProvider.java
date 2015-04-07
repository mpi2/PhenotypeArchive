package uk.ac.ebi.phenotype.chart;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONArray;
import org.json.JSONException;
import org.mousephenotype.www.testing.model.GraphCatTable.Sex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.error.SpecificExperimentException;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.SexType;
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
import java.util.Map;
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

	
	public String getChart(Integer pipelineId, String acc, List<String> genderList, List<String> zyList, 
			Integer phenotypingCenterId, String strain, String metadataGroup, String alleleAccession){

    	HashMap<String, ArrayList<UnidimensionalStatsObject>> data = new HashMap(); // <control/experim, ArrayList<dataToPlot>>
    	data.put(ChartUtils.getLabel(null,  SexType.female), new ArrayList<UnidimensionalStatsObject>() );
    	data.put(ChartUtils.getLabel(null,  SexType.male), new ArrayList<UnidimensionalStatsObject>() );
    	for (String zygosity: zyList){
        	data.put(ChartUtils.getLabel(ZygosityType.valueOf(zygosity), SexType.male), new ArrayList<UnidimensionalStatsObject>() );
        	data.put(ChartUtils.getLabel(ZygosityType.valueOf(zygosity), SexType.female), new ArrayList<UnidimensionalStatsObject>() );
    	}
    	
		UnidimensionalStatsObject emptyObj = new UnidimensionalStatsObject();
		emptyObj.setMean(null);
		emptyObj.setSd(null);

		Set<ZygosityType> zygosities = null;
    	String procedureUrl = null;
    	String unit = pipelineDAO.getParameterByStableId(Constants.ABR_PARAMETERS.get(1)).getUnit();

    	for (String parameterStableId : Constants.ABR_PARAMETERS){
    		
    		Integer paramId = pipelineDAO.getParameterByStableId(parameterStableId).getId();
    		try {
    			ExperimentDTO experiment = es.getSpecificExperimentDTO(paramId, pipelineId, acc, genderList, zyList, phenotypingCenterId, strain, metadataGroup, alleleAccession);
			    
    			if (experiment != null){
			    	zygosities = experiment.getZygosities();
			    	Set<SexType> sexes = experiment.getSexes();
					if (procedureUrl == null){
						Procedure proc = pipelineDAO.getProcedureByStableId(experiment.getProcedureStableId()) ;
						if (proc != null) {
							procedureUrl = String.format("<a href=\"%s\">%s</a>", impressService.getProcedureUrlByKey(((Integer)proc.getStableKey()).toString()), proc.getName());
						}
					}
					for (SexType sex : sexes){
						data.get(ChartUtils.getLabel(null, sex)).add(getMeans( sex, null, experiment));
						for (ZygosityType z : zygosities){
							data.get(ChartUtils.getLabel(z, sex)).add(getMeans(sex, z, experiment));
						}
					}
				}
				else {
					emptyObj.setLabel(pipelineDAO.getParameterByStableId(parameterStableId).getName());
			    	data.get(ChartUtils.getLabel(null,  SexType.female)).add(emptyObj);
			    	data.get(ChartUtils.getLabel(null,  SexType.male)).add(emptyObj);
					for (String z : zyList){
						data.get(ChartUtils.getLabel(ZygosityType.valueOf(z),  SexType.male)).add(emptyObj);
						data.get(ChartUtils.getLabel(ZygosityType.valueOf(z),  SexType.female)).add(emptyObj);
					}
				}
    		} catch (SolrServerException | IOException | URISyntaxException | SpecificExperimentException e) {
				e.printStackTrace();
			}
    	}
    	
    	// We've got everything, get the chart now.
		return getCustomChart(data, procedureUrl, unit, zygosities);
	}
	
	
	public String getCustomChart(HashMap<String, ArrayList<UnidimensionalStatsObject>> data, String procedureLink, String unit, Set<ZygosityType> zygosities){
				
		JSONArray categories = new JSONArray();
		String title = "Evoked ABR Threshold (6, 12, 18, 24, 30 kHz)";

		Map<String, JSONArray> standardDeviation = new LinkedMap();
		Map<String, JSONArray> lines = new LinkedMap();
		
		Integer decimalNumber = 2;
		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaTranslucid50);
		String empty = null;
		JSONArray emptyObj = new JSONArray();
		emptyObj.put("");
		emptyObj.put(empty);
		emptyObj.put(empty);
		
		List<SexType> sexes = new ArrayList<>();
		sexes.add(SexType.male);
		sexes.add(SexType.female);
		
		for (String abrId: Constants.ABR_PARAMETERS){
			categories.put(pipelineDAO.getParameterByStableId(abrId).getName());
			try {
				categories.put(1, ""); // empty category with null data so that the points won't be connected
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
						
			for (SexType sex : sexes){
				
				boolean first = true;
				String label = ChartUtils.getLabel(null, sex);
				
				for (UnidimensionalStatsObject c : data.get(label)){
					
					JSONArray obj = new JSONArray();
					obj.put(c.getLabel());
					obj.put(c.getMean());
					
					JSONArray sdobj = new JSONArray();
					sdobj.put(c.getLabel());
					
					if (c.getMean() != null){
						sdobj.put(c.getMean() - c.getSd());
						sdobj.put(c.getMean() + c.getSd());
					}else {
						sdobj.put(empty);
						sdobj.put(empty);
					}
					
					if(first) {
						standardDeviation.put(label, new JSONArray());
						lines.put(label, new JSONArray());
						first = false;
						lines.get(label).put(obj);
						lines.get(label).put(emptyObj);
						standardDeviation.get(label).put(sdobj);
						standardDeviation.get(label).put(emptyObj);
					} else {
						lines.get(label).put(obj);
						standardDeviation.get(label).put(sdobj);
					}
				}
				
				for (ZygosityType zyg : zygosities){			
					
					first = true;
					label = ChartUtils.getLabel(zyg, sex);
					
					for (UnidimensionalStatsObject mutant : data.get(label)){
						JSONArray obj = new JSONArray();
						obj.put(mutant.getLabel());
						obj.put(mutant.getMean());
						
						JSONArray sdobj = new JSONArray();
						sdobj.put(mutant.getLabel());
						
						if (mutant.getMean() != null){
							sdobj.put(mutant.getMean() - mutant.getSd());
							sdobj.put(mutant.getMean() + mutant.getSd());
						}else {
							sdobj.put(empty);
							sdobj.put(empty);
						}
						if(first) {
							first = false;
							standardDeviation.put(label, new JSONArray());
							lines.put(label, new JSONArray());
							// add empty datapoint too to keep the click separated
							lines.get(label).put(obj);
							lines.get(label).put(emptyObj);
							standardDeviation.get(label).put(sdobj);
							standardDeviation.get(label).put(emptyObj);
						} else {
							lines.get(label).put(obj);
							standardDeviation.get(label).put(sdobj);
						}
		
					}
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
				"  series: [ ";

				for (SexType sex: sexes){
					for (ZygosityType zyg: zygosities){
						String label = ChartUtils.getLabel(zyg, sex);
						chart += "   { name: '"+ label + "'," +
						"    data: " + lines.get(label).toString() + "," +
						"    zIndex: 1," +
						"    color: " + colors.get(1) + "," +
						"    tooltip: { pointFormat: '<span style=\"font-weight: bold; color: {series.color}\">{series.name}</span>: <b>{point.y:." + decimalNumber + "f}</b>' }," +
						"  }, {"+
						"    name: '" + label + " SD'," +
						"    data: " + standardDeviation.get(label).toString() + "," +
						"    type: 'errorbar',"+
						"    linkedTo: ':previous',"+
						"    color: " + colors.get(1) +","+
						"    tooltip: { pointFormat: ' (SD: {point.low:." + decimalNumber + "f} - {point.high:." + decimalNumber + "f} )<br/>', shared:true }" +
						"  },";
					}

					String label = ChartUtils.getLabel(null, sex);
					chart += "{" +
					"    name: '" + label + "',"+
					"    data: " + lines.get(label).toString() + "," +
					"    zIndex: 1,"+
					"    color: "+ colors.get(0) +", " +
					"    tooltip: { pointFormat: '<span style=\"font-weight: bold; color: {series.color}\">{series.name}</span>: <b>{point.y:." + decimalNumber + "f}</b>' }" +
					"  }, {" +
					"    name: '" + label + " SD',"+
					"    data: " + standardDeviation.get(label).toString() + "," +
					"    type: 'errorbar',"+
					"    linkedTo: ':previous',"+
					"    color: "+ colors.get(0) +","+
					"    tooltip: { pointFormat: ' (SD: {point.low:." + decimalNumber + "f} - {point.high:." + decimalNumber + "f}) <br/>' }" +
					"  },";
				}
				chart += " ]" +
				"});" +
			"});" ; 
				
				System.out.println(chart);
		return chart;
		}
	
	public UnidimensionalStatsObject getMeans(SexType sex, ZygosityType zyg, ExperimentDTO exp){
		
		DescriptiveStatistics stats = new DescriptiveStatistics();
		UnidimensionalStatsObject res = new UnidimensionalStatsObject();
		Set<ObservationDTO> dataPoints = null;
				
		if (zyg == null){
			dataPoints = exp.getControls(sex);
		} else{
			dataPoints = exp.getMutants(sex, zyg);
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
		return res;
	}
}
