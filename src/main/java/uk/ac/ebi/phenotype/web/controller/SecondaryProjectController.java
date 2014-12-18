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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
	public ResponseEntity<String> downloadSecondaryProjectData(@PathVariable String id, Model model) {

		logger.info("Downloading data for secondary project id=" + id);

		Map<String, Set<String>> mpterms = new HashMap<>();
		Map<String, Set<String>> hpterms = new HashMap<>();
		Map<String, Set<String>> humanterms = new HashMap<>();
		Map<String, Set<String>> diseaseterms = new HashMap<>();
		Map<String, String> mousesymbols = new HashMap<>();
		List<String> resp = new ArrayList<>();

		try {
			Set<String> accessions = sp.getAccessionsBySecondaryProjectId(id);

			Map<String, List<Map<String, String>>> getMpToHpTerms = IndexerMap.getMpToHpTerms(phenodigmCore);
			Map<String, GeneDTO> genes = geneService.getHumanOrthologsForGeneSet(accessions);

			// Gather all the phenotype calls by gene id
			Map<String, List<PhenotypeCallSummary>> pcss = new HashMap<>();
			for (PhenotypeCallSummary pcs : genotypePhenotypeService.getPhenotypeFacetResultByGenomicFeatures(accessions).getPhenotypeCallSummaries()) {
				if (!pcss.containsKey(pcs.getGene().getId().getAccession())) {
					pcss.put(pcs.getGene().getId().getAccession(), new ArrayList<PhenotypeCallSummary>());
				}
				pcss.get(pcs.getGene().getId().getAccession()).add(pcs);
			}

			for (String MGIID : accessions) {

				if (!mpterms.containsKey(MGIID)) {
					mpterms.put(MGIID, new HashSet<String>());
				}
				if (!humanterms.containsKey(MGIID)) {
					humanterms.put(MGIID, new HashSet<String>());
				}
				if (!diseaseterms.containsKey(MGIID)) {
					diseaseterms.put(MGIID, new HashSet<String>());
				}
				if (!hpterms.containsKey(MGIID)) {
					hpterms.put(MGIID, new HashSet<String>());
				}

				GeneDTO g = geneService.getGeneById(MGIID);
				if(g!=null) mousesymbols.put(MGIID, g.getMarkerSymbol());

				logger.info(" looking for mp terms for {}", MGIID);
				if (pcss.containsKey(MGIID)) {
					for (PhenotypeCallSummary pcs : pcss.get(MGIID)) {
						String mp = pcs.getPhenotypeTerm().getId().getAccession();
						mpterms.get(MGIID).add(mp);

						logger.info("  looking for hp terms for {}", mp);
						if (getMpToHpTerms.containsKey(mp)) {
							for (Map<String, String> hpTerm : getMpToHpTerms.get(mp)) {
								String hpid = hpTerm.get("hp_id");
								logger.info("   adding hp term {} for {}", hpid, mp);
								hpterms.get(MGIID).add(hpid);
							}
						}
					}
				} else if (genes.get(MGIID)!=null && genes.get(MGIID).getLatestPhenotypeStatus() != null && genes.get(MGIID).getLatestPhenotypeStatus().equalsIgnoreCase(GeneService.GeneFieldValue.PHENOTYPE_STATUS_COMPLETE)) {
					mpterms.get(MGIID).add("No phenotype calls");
				} else if (genes.get(MGIID)!=null && genes.get(MGIID).getLatestPhenotypeStatus() != null && genes.get(MGIID).getLatestPhenotypeStatus().equalsIgnoreCase(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED)) {
					mpterms.get(MGIID).add("PreQC data available");
				} else {
					mpterms.get(MGIID).add("No data");
				}

				logger.info(" looking for human symbols for {}", MGIID);
				if (genes.get(MGIID) != null && genes.get(MGIID).getHumanGeneSymbol() != null) {
					humanterms.get(MGIID).addAll(genes.get(MGIID).getHumanGeneSymbol());
					logger.info(" adding human symbols {} for {}", genes.get(MGIID).getHumanGeneSymbol(), MGIID);
				}

				if (genes.get(MGIID) != null && genes.get(MGIID).getDiseaseId() != null) {

					diseaseterms.get(MGIID).addAll(genes.get(MGIID).getDiseaseId());
				}

			}

			resp.add(StringUtils.join(Arrays.asList("MGI ID", "Mouse symbol", "Human symbol", "MP terms", "HP terms", "Disease associations"), "\t"));
			for (String MGIID : accessions) {
				List line = Arrays.asList(MGIID,
					mousesymbols.get(MGIID),
					StringUtils.join(humanterms.get(MGIID), ","),
					StringUtils.join(mpterms.get(MGIID), ","),
					StringUtils.join(hpterms.get(MGIID), ","),
					StringUtils.join(diseaseterms.get(MGIID), ",")
				);
				resp.add(StringUtils.join(line, "\t"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<String>(StringUtils.join(resp,"\n"), createResponseHeaders(), HttpStatus.OK);
	}

	private HttpHeaders createResponseHeaders(){
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_PLAIN);
		return responseHeaders;
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
