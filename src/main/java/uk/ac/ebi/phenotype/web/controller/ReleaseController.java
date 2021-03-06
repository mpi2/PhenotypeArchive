/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.phenotype.analytics.bean.AggregateCountXYBean;
import uk.ac.ebi.phenotype.chart.AnalyticsChartProvider;
import uk.ac.ebi.phenotype.chart.SignificantType;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.dao.AnalyticsDAO;
import uk.ac.ebi.phenotype.dao.StatisticalResultDAO;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.AlleleService;
import uk.ac.ebi.phenotype.service.ObservationService;
import uk.ac.ebi.phenotype.service.PostQcService;
import uk.ac.ebi.phenotype.service.dto.AlleleDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

@Controller
public class ReleaseController {

	@Autowired
	private AnalyticsDAO analyticsDAO;

	@Autowired
	private StatisticalResultDAO statisticalResultDAO;

	@Autowired
	private PostQcService gpService;

	@Autowired
	private AlleleService as;

	@Autowired
	private ObservationService os;
	
	@Autowired
	private UnidimensionalChartAndTableProvider chartProvider;	

	public static Map<String, String> statisticalMethodsShortName = new HashMap<>();
	static {
		statisticalMethodsShortName.put("Fisher's exact test", "Fisher");
		statisticalMethodsShortName.put("Wilcoxon rank sum test with continuity correction", "Wilcoxon");
		statisticalMethodsShortName.put("Mixed Model framework, generalized least squares, equation withoutWeight", "MMgls");
		statisticalMethodsShortName.put("Mixed Model framework, linear mixed-effects model, equation withoutWeight", "MMlme");
	}

	@RequestMapping(value="/release.json", method=RequestMethod.GET)
	public ResponseEntity<String> getJsonReleaseInformation() {

		Map<String, String> metaInfo = analyticsDAO.getMetaData();
		JSONObject json = new JSONObject(metaInfo);

		return new ResponseEntity<>(json.toString(), createResponseHeaders(), HttpStatus.OK);

	}
	private HttpHeaders createResponseHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}

	@RequestMapping(value="/release", method=RequestMethod.GET)
	public String getReleaseInformation(
		Model model) throws SolrServerException, IOException, URISyntaxException, SQLException{

		Map<String, String> metaInfo = analyticsDAO.getMetaData();
		
		/*
		 * What are the different Phenotyping centers?
		 */
		
		String sCenters = metaInfo.get("phenotyped_lines_centers");
		String[] phenotypingCenters = sCenters.split(",");
		
		/*
		 * Data types
		 */
		
		String sDataTypes = metaInfo.get("datapoint_types");
		String[] dataTypes = sDataTypes.split(",");
		
		/*
		 * QC types
		 */
		String[] qcTypes = new String[]{"QC_passed","QC_failed","issues"};
		
		/*
		 * Targeted allele types
		 */
		
		String sAlleleTypes = metaInfo.get("targeted_allele_types");
		String[] alleleTypes = sAlleleTypes.split(",");
		
		
		/*
		 * Helps to generate graphs
		 */
		AnalyticsChartProvider chartsProvider = new AnalyticsChartProvider();
		
		/*
		 * Analytics data: nb of lines per procedure per center
		 */
		
		List<AggregateCountXYBean> beans = analyticsDAO.getAllProcedureLines();
		String lineProcedureChart = 
				chartsProvider.generateAggregateCountByProcedureChart(
						metaInfo.get("data_release_version"), 
						beans,
						"Lines per procedure",
						"Center by center",
						"Number of lines",
						"lines",
						"lineProcedureChart");
		
		List<AggregateCountXYBean> callBeans = analyticsDAO.getAllProcedurePhenotypeCalls();
		String callProcedureChart = 
				chartsProvider.generateAggregateCountByProcedureChart(
						metaInfo.get("data_release_version"), 
						callBeans,
						"Phenotype calls per procedure",
						"Center by center",
						"Number of phenotype calls",
						"calls",						
						"callProcedureChart");
		
		Map<String, List<String>> statisticalMethods = analyticsDAO.getAllStatisticalMethods();
		
		/**
		 * Generate pValue distribution graph for all methods
		 */
		
		Map<String, String> distributionCharts = new HashMap<String, String>();
		
		for (String dataType: statisticalMethods.keySet()) {
			for (String statisticalMethod: statisticalMethods.get(dataType)) {
				List<AggregateCountXYBean> distribution = analyticsDAO.getPValueDistribution(dataType, statisticalMethod);
				String chart = chartsProvider.generateAggregateCountByProcedureChart(
						metaInfo.get("data_release_version"), 
						distribution,
						"P-value distribution",
						statisticalMethod,
						"Frequency",
						"",
						statisticalMethodsShortName.get(statisticalMethod)+"Chart");
				distributionCharts.put(statisticalMethodsShortName.get(statisticalMethod)+"Chart", chart);
			}
		}
		
		/**
		 * Get Historical trends release by release
		 */
		
		List<String> allReleases = analyticsDAO.getReleases(null);
		
		String[] trendsVariables = new String[] {"statistically_significant_calls", "phenotyped_genes", "phenotyped_lines"};
		Map<String, List<AggregateCountXYBean>> trendsMap = new HashMap<String, List<AggregateCountXYBean>>();
		for (int i=0; i<trendsVariables.length; i++) {
			trendsMap.put(trendsVariables[i], analyticsDAO.getHistoricalData(trendsVariables[i]));
		}
		
		String trendsChart = chartsProvider.generateHistoryTrendsChart(trendsMap, allReleases, "Genes/Mutant Lines/MP Calls", "Release by Release", "Genes/Mutant Lines", "Phenotype Calls", true, "trendsChart");
		
		Map<String, List<AggregateCountXYBean>> datapointsTrendsMap = new HashMap<String, List<AggregateCountXYBean>>();
		String[] status = new String[] {"QC_passed", "QC_failed", "issues"};
		
		for (int i=0; i<dataTypes.length; i++) {
			for (int j=0; j<status.length; j++) {
				String propertyKey = dataTypes[i]+"_datapoints_"+status[j];
				List<AggregateCountXYBean> dataPoints = analyticsDAO.getHistoricalData(propertyKey);
				//if (beans.size() > 0) {
					datapointsTrendsMap.put(propertyKey, dataPoints);
				//}
			}
		}
		
		String datapointsTrendsChart = chartsProvider.generateHistoryTrendsChart(datapointsTrendsMap, allReleases, "Data points", "", "Data points", null, false, "datapointsTrendsChart");
		
		/**
		 * Drill down by top level phenotypes
		 */
		
		String topLevelsMPs = metaInfo.get("top_level_mps");
		String[] topLevelsMPsArray = topLevelsMPs.split(",");
		// List all categories name
		Map<String, String> topLevelsNames = new HashMap<String, String>();
		
		Map<String, List<AggregateCountXYBean>> topLevelMap = new HashMap<String, List<AggregateCountXYBean>>();
		for (int i=0; i<topLevelsMPsArray.length; i++) {
			topLevelsNames.put(topLevelsMPsArray[i], metaInfo.get("top_level_"+topLevelsMPsArray[i]));
			topLevelMap.put(metaInfo.get("top_level_"+topLevelsMPsArray[i]), analyticsDAO.getHistoricalData("top_level_"+topLevelsMPsArray[i]+"_calls"));
		}
		
		String topLevelTrendsChart = chartsProvider.generateHistoryTrendsChart(topLevelMap, allReleases, "Top Level Phenotypes", "", "MP Calls", null, false, "topLevelTrendsChart");
		
		TreeMap<String, TreeMap<String, Long>> annotationDistribution = new TreeMap<>();
		annotationDistribution.put(ZygosityType.heterozygote.getName(), gpService.getDistributionOfAnnotationsByMPTopLevel(ZygosityType.heterozygote, null));
		annotationDistribution.put(ZygosityType.homozygote.getName(), gpService.getDistributionOfAnnotationsByMPTopLevel(ZygosityType.homozygote, null));
		annotationDistribution.put(ZygosityType.hemizygote.getName(), gpService.getDistributionOfAnnotationsByMPTopLevel(ZygosityType.hemizygote, null));
		String annotationDistributionChart = chartsProvider.generateAggregateCountByProcedureChart("1.2", 
			gpService.getAggregateCountXYBean(annotationDistribution), "Distribution of Phenotype Associations in IMPC", "", "Number of Lines", " lines", "distribution");
		
		Set<String> allPhenotypingCenters = as.getFacets(AlleleDTO.PHENOTYPING_CENTRE);
		TreeMap<String, TreeMap<String, Long>> phenotypingDistribution = new TreeMap<>();
		for (String center : allPhenotypingCenters){
			if (!center.equals("")){
				phenotypingDistribution.put(center, as.getStatusCountByPhenotypingCenter(center, AlleleDTO.PHENOTYPE_STATUS));
			}
		}
		String phenotypingDistributionChart = chartsProvider.generateAggregateCountByProcedureChart("1.2", 
		gpService.getAggregateCountXYBean(phenotypingDistribution), "Phenotyping Status by Center", "", "Number of Genes", " genes", "phenotypeStatusByCenterChart");
	
		Set<String> allGenotypingCenters = as.getFacets(AlleleDTO.PRODUCTION_CENTRE);
		TreeMap<String, TreeMap<String, Long>> genotypingDistribution = new TreeMap<>();
		for (String center : allGenotypingCenters){
			if (!center.equals("")){
				genotypingDistribution.put(center, as.getStatusCountByProductionCenter(center, AlleleDTO.GENE_LATEST_MOUSE_STATUS));
			}
		}
		String genotypingDistributionChart = chartsProvider.generateAggregateCountByProcedureChart("1.2", 
		gpService.getAggregateCountXYBean(genotypingDistribution), "Genotyping Status by Center", "", "Number of Genes", " genes", "genotypeStatusByCenterChart");
		
		HashMap<SignificantType, Integer> sexualDimorphismSummary = statisticalResultDAO.getSexualDimorphismSummary();
		String sexualDimorphismChart = chartsProvider.generateSexualDimorphismChart(sexualDimorphismSummary, "Distribution of Phenotype Calls", "sexualDimorphismChart" ); 
		
		HashMap<String, Integer> fertilityDistrib = getFertilityMap();
		HashMap<String, Integer> viabilityMap = getViabilityMap();
		
		/**
		 * Get all former releases: releases but the current one
		 */
		List<String> releases = analyticsDAO.getReleases(metaInfo.get("data_release_version"));
		
		model.addAttribute("metaInfo", metaInfo);
		model.addAttribute("releases", releases);
		model.addAttribute("phenotypingCenters", phenotypingCenters);
		model.addAttribute("dataTypes", dataTypes);
		model.addAttribute("qcTypes", qcTypes);
		model.addAttribute("alleleTypes", alleleTypes);
		model.addAttribute("statisticalMethods", statisticalMethods);
		model.addAttribute("statisticalMethodsShortName", statisticalMethodsShortName);
		model.addAttribute("lineProcedureChart", lineProcedureChart);
		model.addAttribute("callProcedureChart", callProcedureChart);
		model.addAttribute("distributionCharts", distributionCharts);
		model.addAttribute("trendsChart", trendsChart);
		model.addAttribute("datapointsTrendsChart", datapointsTrendsChart);
		model.addAttribute("topLevelTrendsChart", topLevelTrendsChart);
		model.addAttribute("annotationDistributionChart", annotationDistributionChart);
		model.addAttribute("genotypeStatusChart", chartProvider.getStatusColumnChart(as.getStatusCount(null, AlleleDTO.GENE_LATEST_MOUSE_STATUS), "Genotyping Status", "genotypeStatusChart" ));
		model.addAttribute("phenotypeStatusChart", chartProvider.getStatusColumnChart(as.getStatusCount(null, AlleleDTO.LATEST_PHENOTYPE_STATUS), "Phenotyping Status", "phenotypeStatusChart"));
		model.addAttribute("phenotypingDistributionChart", phenotypingDistributionChart);
		model.addAttribute("genotypingDistributionChart", genotypingDistributionChart);
		model.addAttribute("sexualDimorphismChart", sexualDimorphismChart);
		model.addAttribute("sexualDimorphismSummary", sexualDimorphismSummary);
		model.addAttribute("fertilityChart", getFertilityChart(chartsProvider, fertilityDistrib));
		model.addAttribute("fertilityMap", fertilityDistrib);
		model.addAttribute("viabilityMap", viabilityMap);
		model.addAttribute("viabilityChart", getViabilityChart(chartsProvider, viabilityMap));
		
		return null;
	}
	
	
	public HashMap<String, Integer> getFertilityMap(){

		List<String> resource = new ArrayList<>();
		resource.add("IMPC");
		Set<String> fertileColonies = os.getAllColonyIdsByResource(resource, true);
		Set<String> maleInfertileColonies = new HashSet<>();
		Set<String> femaleInfertileColonies = new HashSet<>();
		Set<String> bothSexesInfertileColonies;

		maleInfertileColonies = gpService.getAssociationsDistribution("male infertility", "IMPC").keySet();
		femaleInfertileColonies = gpService.getAssociationsDistribution("female infertility", "IMPC").keySet();
				
		bothSexesInfertileColonies = new HashSet<>(maleInfertileColonies);
		bothSexesInfertileColonies.retainAll(femaleInfertileColonies);
		fertileColonies.removeAll(maleInfertileColonies);
		fertileColonies.removeAll(femaleInfertileColonies);
		maleInfertileColonies.removeAll(bothSexesInfertileColonies);
		femaleInfertileColonies.removeAll(bothSexesInfertileColonies);
		
		HashMap<String, Integer> res = new HashMap<>();
		res.put("female infertile", femaleInfertileColonies.size());
		res.put("male infertile", maleInfertileColonies.size());
		res.put("both sexes infertile", bothSexesInfertileColonies.size());
		res.put("fertile", fertileColonies.size());
		
		return res;
	}
	
	public String getFertilityChart(AnalyticsChartProvider chartProvider, HashMap<String, Integer> fertilityMap){
		
		HashMap<String, Integer> slicedOut = new HashMap<>(fertilityMap);
		slicedOut.remove("fertile");
		HashMap<String, Integer> notSliced = new HashMap<>();
		notSliced.put("fertile" , fertilityMap.get("fertile"));
		return chartProvider.getSlicedPieChart(slicedOut, notSliced, "Fertility Distribution", "fertilityChart");
	}
	
	public String getViabilityChart(AnalyticsChartProvider chartProvider, HashMap<String, Integer> fertilityMap){
		
		HashMap<String, Integer> slicedOut = new HashMap<>(fertilityMap);
		slicedOut.remove("viable");
		HashMap<String, Integer> notSliced = new HashMap<>();
		notSliced.put("viable" , fertilityMap.get("viable"));
		return chartProvider.getSlicedPieChart(slicedOut, notSliced, "Viability Distribution", "viabilityChart");
	}
	
	public HashMap<String , Integer> getViabilityMap(){
		List<String> resource = new ArrayList<>();
		resource.add("IMPC");
		Set<String> partialLethality = gpService.getAssociationsDistribution("partial preweaning lethality", "IMPC").keySet();
		Set<String> completeLethality = gpService.getAssociationsDistribution("complete preweaning lethality", "IMPC").keySet();
		Set<String> all = os.getAllColonyIdsByResource(resource, true);
		all.removeAll(partialLethality);
		all.removeAll(completeLethality);

		HashMap<String, Integer> res = new HashMap<>();
		res.put("partial preweaning lethality", partialLethality.size());
		res.put("complete preweaning lethality", completeLethality.size());
		res.put("viable", all.size());
		
		return res;
	}

}
