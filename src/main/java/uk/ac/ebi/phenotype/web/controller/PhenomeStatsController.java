package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

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
import uk.ac.ebi.phenotype.stats.ColorCodingPalette;

@Controller
public class PhenomeStatsController {

	@Autowired
	private AlleleDAO alleleDao;
	
	@Autowired
	private PhenotypeCallSummaryDAO phenotypeCallSummaryDao;
	
	@RequestMapping(value="/phenome", method=RequestMethod.GET)
	public String getGraph(
		//@PathVariable String phenotype_id, 
		@RequestParam(required = true, value = "pipeline_stable_id") String pipelineStableId,
		@RequestParam(required = false, value = "phenotyping_center") String phenotypingCenter,
		Model model,
		HttpServletRequest request,
		RedirectAttributes attributes) throws SolrServerException, IOException, URISyntaxException, SQLException{

		List<PhenotypeCallSummary> phenotypeCalls = 
				phenotypeCallSummaryDao.getPhenotypeCallByPhenotypingCenterAndPipeline(phenotypingCenter, pipelineStableId);
		
		ColorCodingPalette colorCoding = new ColorCodingPalette();
		double minimalPValue = 1.00E-4;
		colorCoding.generatePhenotypeCallSummaryColors(phenotypeCalls, 9, 1, minimalPValue);
		model.addAttribute("phenotypeCalls", phenotypeCalls);
		model.addAttribute("palette", colorCoding.getPalette());		
		return null;
	}
}
