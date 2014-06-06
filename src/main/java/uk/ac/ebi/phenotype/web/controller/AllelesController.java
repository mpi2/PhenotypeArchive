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
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.List;
import java.util.Map;
//import java.util.TreeMap;
//import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//import net.sf.json.JSONException;
//import net.sf.json.JSONObject;

//import org.apache.commons.lang.exception.ExceptionUtils;
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.response.FacetField;
//import org.apache.solr.client.solrj.response.FacetField.Count;
//import org.apache.solr.client.solrj.response.QueryResponse;
//import org.apache.solr.common.SolrDocumentList;
//import org.hibernate.HibernateException;
//import org.hibernate.exception.JDBCConnectionException;
//import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.generic.util.RegisterInterestDrupalSolr;
import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.dao.DatasourceDAO;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
//import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryBySex;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAO;
//import uk.ac.ebi.phenotype.pojo.Datasource;
//import uk.ac.ebi.phenotype.pojo.GenomicFeature;
//import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummaryDAOReadOnly;
//import uk.ac.ebi.phenotype.pojo.Xref;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.ObservationService;
//import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
//import uk.ac.ebi.phenotype.web.pojo.PhenotypeRow;
//import uk.ac.ebi.phenotype.web.pojo.PhenotypeRow.PhenotypeRowType;

@Controller
public class AllelesController {

	private final Logger log = LoggerFactory.getLogger(AllelesController.class);

//	RegisterInterestDrupalSolr registerInterest;
//
//	@Autowired
//	private DatasourceDAO datasourceDao;
//	
//	@Autowired
//	private GenomicFeatureDAO genesDao;
//
//	@Autowired
//	private ImagesSolrDao imagesSolrDao;
//
//	@Autowired
//	private PhenotypeCallSummaryDAOReadOnly phenoDAO;
//
//	@Autowired
//	SolrIndex solrIndex;
//	
//
//	@Autowired
//	private GeneService geneService;
//	
//	@Autowired
//	private ObservationService observationService;
//
//	@Autowired
//	private PhenotypeSummaryDAO phenSummary;
//	
//	@Resource(name="globalConfiguration")
//	private Map<String, String> config;
//	
//	private static final int numberOfImagesToDisplay=5;

	@RequestMapping("/alleles/{acc}")
	public String alleles(
			@PathVariable String acc,
			//@RequestParam(value="heatmap", required=false, defaultValue="false") Boolean showHeatmap,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException {

               // String thing = "the string thing!";
		//model.addAttribute("thing", "the string thing!");

                model.addAttribute("symbol", "Cib2<sup>tm1b(EUCOMM)Wtsi</sup>");
                model.addAttribute("type", "Cre-excised Reporter-tagged deletion (tm1b)");
                model.addAttribute("status", "There are mice for this allele");
                model.addAttribute("detailsLink", "http://www.sanger.ac.uk");
                model.addAttribute("orderLink", "http://www.sanger.ac.uk");
                model.addAttribute("genbank", "https://www.i-dcc.org/imits/targ_rep/alleles/11268/escell-clone-cre-genbank-file");
                
//                model.addAttribute("mutagenesis_blurb", "This gene has 1 wild type transcripts, of which 1 are protein-coding. Following removal of the floxed region, " +
//                "1 transcript is predicted to produce a truncated protein product of which 1 may be subject to non-sense mediated decay (NMD). The original allele for " +
//                "this mutation is of type 'Knockout First, Reporter-tagged insertion with conditional potential'. The table below shows the predicted structure of the gene " +
//                "transcripts after application of Flp and Cre (forming a 'Knockout-First, Post-Flp and Cre - Deletion, No Reporter' allele - more information on IKMC alleles " +
//                "can be found here). Click the 'view' button for each transcript to see the full prediction for that transcript."
//                );

                model.addAttribute("mutagenesis_blurb", "mutagenesis blurb");
                
		return "alleles";
	}
    
}
