/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.bean.StatisticalResultBean;
import uk.ac.ebi.phenotype.dao.AlleleDAO;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.dao.StatisticalResultDAO;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAO;
import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.ObservationService;
import uk.ac.ebi.phenotype.stats.ColorCodingPalette;
import uk.ac.ebi.phenotype.stats.graphs.PhenomeChartProvider;


@Controller
public class ExperimentsController {

	private final Logger log = LoggerFactory.getLogger(ExperimentsController.class);
	
	@Autowired
	private GenomicFeatureDAO genesDao;
	
	@Autowired
	private AlleleDAO alleleDao;

	@Autowired
	private PhenotypePipelineDAO pipelineDao;
	
	@Autowired
    private StatisticalResultDAO statisticalResultDAO;   
	
	@Autowired
	SolrIndex solrIndex;
	
	@Autowired
	private GeneService geneService;

	@Autowired
	private ObservationService observationService;
	
	@Autowired
	private PhenotypeSummaryDAO phenSummary;
	
	@Resource(name="globalConfiguration")
	private Map<String, String> config;
	
	private PhenomeChartProvider phenomeChartProvider = new PhenomeChartProvider();

	/**
	 * Runs when the request missing an accession ID. This redirects to the
	 * search page which defaults to showing all genes in the list
	 */
	@RequestMapping("/experiments/alleles")
	public String rootForward() {
		return "redirect:/search";
	}

	@RequestMapping("/experiments/alleles/{alleleAccession}")
	public String genes(
			@PathVariable String alleleAccession,
			@RequestParam(required = true, value = "phenotyping_center") String phenotypingCenter,
			@RequestParam(required = true, value = "pipeline_stable_id") String pipelineStableId,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException {
		
		Allele allele = alleleDao.getAlleleByAccession(alleleAccession);
		if (allele == null) {
			log.warn("Allele '" + alleleAccession + "' can't be found.");
		}
		
		Pipeline pipeline = pipelineDao.getPhenotypePipelineByStableId(pipelineStableId);
		
		List<Map<String,String>> mapList =  null;
		Map<String, List<StatisticalResultBean>> pvaluesMap = null;
		
		
		try {
			mapList = observationService.getDistinctParameterListByPipelineAlleleCenter(pipelineStableId, alleleAccession, phenotypingCenter, null);
			
			// get all p-values for this allele/center/pipeline
			 pvaluesMap = statisticalResultDAO.getPvaluesByAlleleAndPhenotypingCenterAndPipeline(
						alleleAccession, phenotypingCenter, pipelineStableId);
			
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ColorCodingPalette colorCoding = new ColorCodingPalette();
		double minimalPValue = 1.00E-4;
		colorCoding.generateColors(pvaluesMap, 9, 1, minimalPValue);
		
		String chart = phenomeChartProvider.generatePhenomeChart(
				alleleAccession, 
				pvaluesMap,
				minimalPValue,
				pipeline);
		
		model.addAttribute("mapList", mapList);
		model.addAttribute("pvaluesMap", pvaluesMap);
		model.addAttribute("palette", colorCoding.getPalette());
		model.addAttribute("chart", chart);
		model.addAttribute("phenotyping_center", phenotypingCenter);
		model.addAttribute("allele", allele);
		model.addAttribute("pipeline", pipeline);
		model.addAttribute("request", request);
		
		return "experiments";
	}
		
	/**
	 * Error handler for gene not found
	 * 
	 * @param exception
	 * @return redirect to error page
	 * 
	 */
	@ExceptionHandler(GenomicFeatureNotFoundException.class)
	public ModelAndView handleGenomicFeatureNotFoundException(GenomicFeatureNotFoundException exception) {
        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage",exception.getMessage());
        mv.addObject("acc",exception.getAcc());
        mv.addObject("type","MGI gene");
        mv.addObject("exampleURI", "/experiments/alleles/MGI:4436678?phenotyping_center=HMGU&pipeline_stable_id=ESLIM_001");
        return mv;
    }

	@ExceptionHandler(JDBCConnectionException.class)
	public ModelAndView handleJDBCConnectionException(JDBCConnectionException exception) {
        ModelAndView mv = new ModelAndView("uncaughtException");
        System.out.println(ExceptionUtils.getFullStackTrace(exception));
        mv.addObject("errorMessage", "An error occurred connecting to the database");
        return mv;
    }

	@ExceptionHandler(Exception.class)
	public ModelAndView handleGeneralException(Exception exception) {
        ModelAndView mv = new ModelAndView("uncaughtException");
        System.out.println(ExceptionUtils.getFullStackTrace(exception));
        return mv;
    }

    
}
