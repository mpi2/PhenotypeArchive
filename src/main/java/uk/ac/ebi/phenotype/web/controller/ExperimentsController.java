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
import uk.ac.ebi.phenotype.chart.ColorCodingPalette;
import uk.ac.ebi.phenotype.chart.Constants;
import uk.ac.ebi.phenotype.chart.PhenomeChartProvider;
import uk.ac.ebi.phenotype.dao.AlleleDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.service.ImpressService;
import uk.ac.ebi.phenotype.service.ObservationService;
import uk.ac.ebi.phenotype.service.StatisticalResultService;
import uk.ac.ebi.phenotype.solr.indexer.beans.ImpressBean;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class ExperimentsController {

	private final Logger log = LoggerFactory.getLogger(ExperimentsController.class);
	
	@Autowired
	private AlleleDAO alleleDao;

	@Autowired
	private PhenotypePipelineDAO pipelineDao;
	
	@Autowired
	SolrIndex solrIndex;

	@Autowired
	private StatisticalResultService srService;
	
	@Autowired
	private ImpressService impressService;

	@Autowired
	private ObservationService observationService;
	
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
			@RequestParam(required = false, value = "phenotyping_center") String phenotypingCenter,
			@RequestParam(required = false, value = "pipeline_stable_id") String pipelineStableId,
			@RequestParam(required = false, value = "procedure_stable_id") String procedureStableId,
			@RequestParam(required = false, value = "resource") ArrayList<String> resource,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) 
	throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException, SolrServerException {

		Allele allele = alleleDao.getAlleleByAccession(alleleAccession);
		List<ImpressBean> pipelines = new ArrayList<>();
		Map<String, List<StatisticalResultBean>> pvaluesMap = null;
		List<String> procedureStableIds = null;
		List<String> truncatedStableIds = null;

		if (allele == null) {
			log.warn("Allele '" + alleleAccession + "' can't be found.");
		}
				
		if (pipelineStableId == null){
			pipelines = observationService.getPipelines(alleleAccession, phenotypingCenter, resource);
		} else {
			pipelines = new ArrayList<>();
			pipelines.add(impressService.getPipeline(pipelineStableId));
		}
				
		// check whether there is a procedure id, and if so if it's truncated or not
		// The reason is a procedure can have multiple versions.
		if (procedureStableId != null) {
			List<Procedure> procedures = pipelineDao.getProcedureByMatchingStableId(procedureStableId);
			truncatedStableIds = new ArrayList();
			truncatedStableIds.add(procedureStableId);
			if (procedures != null && procedures.size() > 0) {
				procedureStableIds = new ArrayList<String>();
				for (Procedure procedure: procedures) {
					procedureStableIds.add(procedure.getStableId());
				}
			}
		}
		
		try {
			// get all p-values for this allele/center/pipeline
			pvaluesMap = new HashMap<String, List<StatisticalResultBean>>();
			Map<String, List<String>> parametersByProcedure = new HashMap<>();
			
			for (ImpressBean pipeline : pipelines){
				pvaluesMap.putAll(srService.getPvaluesByAlleleAndPhenotypingCenterAndPipeline(alleleAccession, phenotypingCenter, pipeline.getStableId(), truncatedStableIds, resource));
				if (resource != null){
					for (String res : resource){
						parametersByProcedure.putAll(srService.getParametersToProcedureMap(res, phenotypingCenter, pipeline.getStableId()));
					}
				} else {
					parametersByProcedure.putAll(srService.getParametersToProcedureMap(null, phenotypingCenter, pipeline.getStableId()));
				}
			}	
			
			ColorCodingPalette colorCoding = new ColorCodingPalette();
			colorCoding.generateColors(	pvaluesMap,	ColorCodingPalette.NB_COLOR_MAX, 1,	Constants.SIGNIFICANT_P_VALUE);
			
			String chart = phenomeChartProvider.generatePvaluesOverviewChart(allele, pvaluesMap, Constants.SIGNIFICANT_P_VALUE, parametersByProcedure, phenotypingCenter);
			
			model.addAttribute("palette", colorCoding.getPalette());
			model.addAttribute("chart", chart);
		
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	
		model.addAttribute("pvaluesMap", pvaluesMap);
		model.addAttribute("phenotyping_center", phenotypingCenter);
		model.addAttribute("allele", allele);
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
