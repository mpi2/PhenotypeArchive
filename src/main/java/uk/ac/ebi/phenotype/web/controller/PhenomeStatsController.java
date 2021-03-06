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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.chart.ColorCodingPalette;
import uk.ac.ebi.phenotype.chart.Constants;
import uk.ac.ebi.phenotype.chart.PhenomeChartProvider;
import uk.ac.ebi.phenotype.dao.AlleleDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.service.PostQcService;
import uk.ac.ebi.phenotype.service.StatisticalResultService;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;

@Controller
public class PhenomeStatsController {

	@Autowired
	private AlleleDAO alleleDao;
	
	@Autowired
	private PhenotypeCallSummaryDAO phenotypeCallSummaryDao;
	
	@Autowired
	@Qualifier("postqcService")
	PostQcService genotypePhenotypeService;
	

	@Autowired
	StatisticalResultService srService;
	
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
		String chart = phenomeChartProvider.generatePhenomeChartByPhenotype(
				results.getPhenotypeCallSummaries(),
				phenotypingCenter,
				Constants.SIGNIFICANT_P_VALUE);
		
		model.addAttribute("phenotypeCalls", results.getPhenotypeCallSummaries());
		model.addAttribute("palette", colorCoding.getPalette());
		model.addAttribute("chart", chart);
		
		return null;
	}
}
