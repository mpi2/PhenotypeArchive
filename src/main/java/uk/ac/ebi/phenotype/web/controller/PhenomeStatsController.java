package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.dao.AlleleDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;
import uk.ac.ebi.phenotype.stats.ColorCodingPalette;
import uk.ac.ebi.phenotype.stats.Constants;
import uk.ac.ebi.phenotype.stats.graphs.PhenomeChartProvider;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;

@Controller
public class PhenomeStatsController {

	@Autowired
	private AlleleDAO alleleDao;
	
	@Autowired
	private PhenotypeCallSummaryDAO phenotypeCallSummaryDao;
	
	@Autowired
	GenotypePhenotypeService genotypePhenotypeService;
	
	@Resource(name="globalConfiguration")
	private Map<String, String> config;
	
	private PhenomeChartProvider phenomeChartProvider = new PhenomeChartProvider();	
	
	@RequestMapping(value="/phenome", method=RequestMethod.GET)
	public String getGraph(
		//@PathVariable String phenotype_id, 
		@RequestParam(required = true, value = "pipeline_stable_id") String pipelineStableId,
		@RequestParam(required = false, value = "phenotyping_center") String phenotypingCenter,
		Model model,
		HttpServletRequest request,
		RedirectAttributes attributes) throws SolrServerException, IOException, URISyntaxException, SQLException{

		PhenotypeFacetResult results = genotypePhenotypeService.getPhenotypeFacetResultByPhenotypingCenterAndPipeline(phenotypingCenter, pipelineStableId);
		
		ColorCodingPalette colorCoding = new ColorCodingPalette();

		colorCoding.generatePhenotypeCallSummaryColors(
				results.getPhenotypeCallSummaries(),
				ColorCodingPalette.NB_COLOR_MAX, 
				1, 
				Constants.SIGNIFICANT_P_VALUE);
		
		// generate a chart
		String chart = phenomeChartProvider.generatePhenomeChart(
				results.getPhenotypeCallSummaries(),
				phenotypingCenter,
				Constants.SIGNIFICANT_P_VALUE);
		
		model.addAttribute("phenotypeCalls", results.getPhenotypeCallSummaries());
		model.addAttribute("palette", colorCoding.getPalette());
		model.addAttribute("chart", chart);
		
		return null;
	}
}
