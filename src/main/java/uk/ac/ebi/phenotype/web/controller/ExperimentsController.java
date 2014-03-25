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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.antlr.grammar.v3.ANTLRv3Parser.finallyClause_return;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.hibernate.HibernateException;
import org.hibernate.exception.JDBCConnectionException;
import org.json.JSONArray;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.generic.util.RegisterInterestDrupalSolr;
import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.dao.DatasourceDAO;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.data.imits.ColonyStatus;
import uk.ac.ebi.phenotype.data.imits.PhenotypeStatusDAO;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryBySex;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAO;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummaryDAOReadOnly;
import uk.ac.ebi.phenotype.pojo.Xref;
import uk.ac.ebi.phenotype.stats.GeneService;
import uk.ac.ebi.phenotype.stats.ObservationService;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.web.pojo.PhenotypeRow;


@Controller
public class ExperimentsController {

	private final Logger log = LoggerFactory.getLogger(ExperimentsController.class);

	@Autowired
	private DatasourceDAO datasourceDao;
	
	@Autowired
	private GenomicFeatureDAO genesDao;

	@Autowired
	SolrIndex solrIndex;
	
	@Autowired
	private GeneService geneService;

	@Autowired
	private ObservationService observationService;
	
	@Autowired
	private PhenotypeSummaryDAO phenSummary;
	
	@Autowired
	@Qualifier("solr")
	PhenotypeStatusDAO psDao;

	@Resource(name="globalConfiguration")
	private Map<String, String> config;

	/**
	 * Runs when the request missing an accession ID. This redirects to the
	 * search page which defaults to showing all genes in the list
	 */
	@RequestMapping("/experiments/genes")
	public String rootForward() {
		return "redirect:/search";
	}

	@RequestMapping("/experiments/genes/{acc}")
	public String genes(
			@PathVariable String acc,
			@RequestParam(required = true, value = "phenotyping_center") String phenotypingCenter,
			@RequestParam(required = true, value = "pipeline_stable_id") String pipelineStableId,
			@RequestParam(required = true, value = "allele_accession") String alleleAccession,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException {
		
		// see if the gene exists first:
		GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
		if (gene == null) {
			log.warn("Gene status for " + acc + " can't be found.");
		}
		
		List<Map<String,String>> mapList =  null;
		
		try {
			mapList = observationService.getDistinctParameterListByPipelineAlleleCenter(pipelineStableId, alleleAccession, phenotypingCenter, null);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.addAttribute("mapList", mapList);
		model.addAttribute("phenotyping_center", phenotypingCenter);
		model.addAttribute("gene", gene);
		model.addAttribute("request", request);
		model.addAttribute("acc", acc);
		
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
        mv.addObject("exampleURI", "/experiments/genes/MGI:104874");
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
