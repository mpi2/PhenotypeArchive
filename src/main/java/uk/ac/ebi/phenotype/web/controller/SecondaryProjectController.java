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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.chart.ColorCodingPalette;
import uk.ac.ebi.phenotype.chart.Constants;
import uk.ac.ebi.phenotype.chart.PhenomeChartProvider;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.dao.SecondaryProjectDAO;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.service.AlleleService;
import uk.ac.ebi.phenotype.service.PostQcService;


@Controller
public class SecondaryProjectController {

	private final Logger log = LoggerFactory.getLogger(SecondaryProjectController.class);

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;
	
	@Autowired 
	AlleleService as;
	
	@Autowired 
	@Qualifier("postqcService")
	PostQcService genotypePhenotypeService;
	
	@Autowired 
	UnidimensionalChartAndTableProvider chartProvider;
	
	@Autowired 
	SecondaryProjectDAO sp;
	

	private PhenomeChartProvider phenomeChartProvider = new PhenomeChartProvider();	
	
	@RequestMapping(value = "/secondaryproject/{id}", method = RequestMethod.GET)
	public String loadSeondaryProjectPage(@PathVariable String id, Model model,
			HttpServletRequest request, RedirectAttributes attributes)
			throws SolrServerException, IOException, URISyntaxException {
		System.out.println("calling secondary project id="+id);
		
		Set<String> accessions;
		
		try {
			accessions = sp.getAccessionsBySecondaryProjectId(id);
			model.addAttribute("genotypeStatusChart", 
				chartProvider.getStatusColumnChart(as.getStatusCount(accessions, AlleleService.AlleleField.GENE_LATEST_MOUSE_STATUS), "Genotype Status Chart", "genotypeStatusChart" ));
			model.addAttribute("phenotypeStatusChart", 
				chartProvider.getStatusColumnChart(as.getStatusCount(accessions, AlleleService.AlleleField.LATEST_PHENOTYPE_STATUS), "Phenotype Status Chart", "phenotypeStatusChart"));
			
			List<PhenotypeCallSummary> results = genotypePhenotypeService.getPhenotypeFacetResultByGenomicFeatures(accessions).getPhenotypeCallSummaries();
			
			System.out.println("LIST LENGTH " + results.size());
			
			// generate a chart
			String chart = phenomeChartProvider.generatePhenomeChartByGenes(
					results,
					null,
					Constants.SIGNIFICANT_P_VALUE);
			
	//		model.addAttribute("phenotypeCalls", results);
	//		model.addAttribute("palette", colorCoding.getPalette());
			model.addAttribute("chart", chart);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "idg";
	}

	
	
}
