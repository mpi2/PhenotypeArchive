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

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.chart.Constants;
import uk.ac.ebi.phenotype.chart.PhenomeChartProvider;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.dao.SecondaryProjectDAO;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.service.AlleleService;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.PostQcService;
import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;
import uk.ac.ebi.phenotype.solr.indexer.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;


@Controller
public class SecondaryProjectController {

	private final Logger logger = LoggerFactory.getLogger(SecondaryProjectController.class);

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;
	
	@Autowired 
	AlleleService as;
	
	@Autowired 
	@Qualifier("postqcService")
	PostQcService genotypePhenotypeService;

	@Autowired
	GeneService geneService;


	@Autowired 
	UnidimensionalChartAndTableProvider chartProvider;
	
	@Autowired 
	SecondaryProjectDAO sp;

	@Autowired
	@Qualifier("solrServer")
	SolrServer phenodigmCore;

	private PhenomeChartProvider phenomeChartProvider = new PhenomeChartProvider();

	@RequestMapping(value = "/secondaryproject/{id}/download", method = RequestMethod.GET)
	public ResponseEntity<String> downloadSecondaryProjectData(@PathVariable String id, Model model)
		throws SolrServerException, IOException, URISyntaxException, SQLException, IndexerException {

		logger.info("Downloading data for secondary project id=" + id);

		Set<String> accessions = sp.getAccessionsBySecondaryProjectId(id);

		List<PhenotypeCallSummary> results = genotypePhenotypeService.getPhenotypeFacetResultByGenomicFeatures(accessions).getPhenotypeCallSummaries();

		Map<String, Set<String>> mpterms = new HashMap<>();
		Map<String, Set<String>> hpterms = new HashMap<>();
		Map<String, Set<String>> humanterms = new HashMap<>();
		Map<String, Set<String>> diseaseterms = new HashMap<>();

		Map<String, List<Map<String, String>>> getMpToHpTerms = IndexerMap.getMpToHpTerms(phenodigmCore);
		Map<String, GeneDTO> genes = geneService.getHumanOrthologsForGeneSet(accessions);


		List<String> resp = new ArrayList<>();

		for(PhenotypeCallSummary pcs : results) {
			String MGIID = pcs.getGene().getId().getAccession();

			if ( ! mpterms.containsKey(MGIID)) {
				mpterms.put(MGIID, new HashSet<String>());
			}
			mpterms.get(MGIID).add(pcs.getPhenotypeTerm().getId().getAccession());

			for (Map<String, String> hpTerm : getMpToHpTerms.get(pcs.getPhenotypeTerm().getId().getAccession())) {
				String hpid = hpTerm.get("hp_id");
				if ( ! hpterms.containsKey(MGIID)) {
					hpterms.put(MGIID, new HashSet<String>());
				}
				hpterms.get(MGIID).add(hpid);
			}

			if ( ! humanterms.containsKey(MGIID)) {
				humanterms.put(MGIID, new HashSet<String>());
			}
			humanterms.get(MGIID).addAll(genes.get(MGIID).getHumanGeneSymbol());

			if ( ! diseaseterms.containsKey(MGIID)) {
				diseaseterms.put(MGIID, new HashSet<String>());
			}
			diseaseterms.get(MGIID).addAll(genes.get(MGIID).getDiseaseId());

			List line = Arrays.asList(MGIID,
				StringUtils.join(humanterms.get(MGIID),","),
				StringUtils.join(mpterms.get(MGIID),","),
				StringUtils.join(hpterms.get(MGIID), ","),
				StringUtils.join(diseaseterms.get(MGIID), ",")
			);
			resp.add(StringUtils.join(line, "\t"));
		}

		return new ResponseEntity<String>(StringUtils.join(resp,"\n"), HttpStatus.OK);
	}


	@RequestMapping(value = "/secondaryproject/{id}", method = RequestMethod.GET)
	public String loadSeondaryProjectPage(@PathVariable String id, Model model,
			HttpServletRequest request, RedirectAttributes attributes)
			throws SolrServerException, IOException, URISyntaxException {
		System.out.println("calling secondary project id="+id);
		
		Set<String> accessions;
		
		try {
			accessions = sp.getAccessionsBySecondaryProjectId(id);
			model.addAttribute("genotypeStatusChart", chartProvider.getStatusColumnChart(as.getStatusCount(accessions, AlleleDTO.GENE_LATEST_MOUSE_STATUS), "Genotype Status Chart", "genotypeStatusChart" ));
			model.addAttribute("phenotypeStatusChart", chartProvider.getStatusColumnChart(as.getStatusCount(accessions, AlleleDTO.LATEST_PHENOTYPE_STATUS), "Phenotype Status Chart", "phenotypeStatusChart"));
			
			List<PhenotypeCallSummary> results = genotypePhenotypeService.getPhenotypeFacetResultByGenomicFeatures(accessions).getPhenotypeCallSummaries();
			String chart = phenomeChartProvider.generatePhenomeChartByGenes(
					results,
					null,
					Constants.SIGNIFICANT_P_VALUE);
			
			model.addAttribute("chart", chart);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "idg";
	}




}
