package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.analytics.bean.AggregateCountXYBean;
import uk.ac.ebi.phenotype.chart.AnalyticsChartProvider;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.dao.AnalyticsDAO;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.AlleleService;
import uk.ac.ebi.phenotype.service.AlleleService.AlleleField;
import uk.ac.ebi.phenotype.service.PostQcService;

@Controller
public class ReleaseController {

	@Autowired
	private AnalyticsDAO analyticsDAO;

	@Autowired
	private PostQcService gpService;

	@Autowired
	private UnidimensionalChartAndTableProvider chartProvider;
	
	@Autowired 
	AlleleService as;
	
	@Resource(name="globalConfiguration")
	private Map<String, String> config;
	
	public static Map<String, String> statisticalMethodsShortName = new HashMap<String, String>();
	
	static {

		statisticalMethodsShortName.put("Fisher's exact test", "Fisher");
		statisticalMethodsShortName.put("Wilcoxon rank sum test with continuity correction", "Wilcoxon");
		statisticalMethodsShortName.put("MM framework, generalized least squares, equation withoutWeight", "MMgls");
		statisticalMethodsShortName.put("MM framework, linear mixed-effects model, equation withoutWeight", "MMlme");
		
	}
	
	@RequestMapping(value="/release", method=RequestMethod.GET)
	public String getReleaseInformation(
		Model model,
		HttpServletRequest request,
		RedirectAttributes attributes) throws SolrServerException, IOException, URISyntaxException, SQLException{

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
		annotationDistribution.put(ZygosityType.heterozygote.getName(), gpService.getDistributionOfAnnotationsByMPTopLevel(ZygosityType.heterozygote));
		annotationDistribution.put(ZygosityType.homozygote.getName(), gpService.getDistributionOfAnnotationsByMPTopLevel(ZygosityType.homozygote));
		annotationDistribution.put(ZygosityType.hemizygote.getName(), gpService.getDistributionOfAnnotationsByMPTopLevel(ZygosityType.hemizygote));
		String annotationDistributionChart = chartsProvider.generateAggregateCountByProcedureChart("1.2", 
			gpService.getAggregateCountXYBean(annotationDistribution), "Distribution of Phenotype Associations in IMPC", "", "Number of Lines", " lines", "distribution");
		
		Set<String> allPhenotypingCenters = as.getFacets(AlleleField.PHENOTYPING_CENTRE);
		TreeMap<String, TreeMap<String, Long>> phenotypingDistribution = new TreeMap<>();
		for (String center : allPhenotypingCenters){
			if (!center.equals("")){
				phenotypingDistribution.put(center, as.getStatusCountByPhenotypingCenter(center, AlleleField.PHENOTYPING_STATUS));
			}
		}
		String phenotypingDistributionChart = chartsProvider.generateAggregateCountByProcedureChart("1.2", 
		gpService.getAggregateCountXYBean(phenotypingDistribution), "Phenotyping Status by Center", "", "Number of Genes", " genes", "phenotypeStatusByCenterChart");
	
//		Set<String> allGenotypingCenters = as.getFacets(AlleleField.PRODUCTION_CENTER);
		
		
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
		model.addAttribute("genotypeStatusChart", chartProvider.getStatusColumnChart(as.getStatusCount(null, AlleleService.AlleleField.GENE_LATEST_MOUSE_STATUS), "Genotype Status Chart", "genotypeStatusChart" ));
		model.addAttribute("phenotypeStatusChart", chartProvider.getStatusColumnChart(as.getStatusCount(null, AlleleService.AlleleField.LATEST_PHENOTYPE_STATUS), "Phenotype Status Chart", "phenotypeStatusChart"));
		model.addAttribute("phenotypingDistributionChart", phenotypingDistributionChart);
		
		return null;
	}
}
