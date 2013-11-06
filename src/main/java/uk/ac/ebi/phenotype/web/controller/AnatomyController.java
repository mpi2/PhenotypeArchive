/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.generic.util.JSONImageUtils;
import uk.ac.ebi.generic.util.JSONMAUtils;
import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.dao.OntologyTermDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.util.PhenotypeCallSummaryDAOReadOnly;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.web.pojo.Anatomy;
import uk.ac.ebi.phenotype.web.pojo.PhenotypeRow;

@Controller
public class AnatomyController {

	private final Logger log = LoggerFactory.getLogger(AnatomyController.class);

	@Autowired
	private OntologyTermDAO ontoTermDao;

	// @Autowired
	// private PhenotypeCallSummaryDAO phenoDAO;
	@Autowired
	private PhenotypeCallSummaryDAOReadOnly phenoDAO;

	@Autowired
	private SolrIndex solrIndex;

	@Autowired
	private PhenotypePipelineDAO pipelineDao;

	@Autowired
	private ImagesSolrDao imagesSolrDao;

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	/**
	 * Phenotype controller loads information required for displaying the
	 * phenotype page or, in the case of an error, redirects to the error page
	 * 
	 * @param phenotype_id
	 *            the Mammalian phenotype id of the phenotype to display
	 * @return the name of the view to render, or redirect to search page on
	 *         error
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws SolrServerException
	 * 
	 */
	@RequestMapping(value = "/anatomy/{anatomy_id}", method = RequestMethod.GET)
	public String loadMaPage(@PathVariable String anatomy_id, Model model,
			HttpServletRequest request, RedirectAttributes attributes)
			throws SolrServerException, IOException, URISyntaxException {
		System.out.println("calling anatomy page");
		// http://www.informatics.jax.org/searches/AMA.cgi?id=MA:0002950
		// right eye
		Anatomy ma=JSONMAUtils.getMA(anatomy_id, config);
		model.addAttribute("anatomy", ma);
		Map<String, JSONObject> exampleImagesMap = getExampleImages(anatomy_id);

		model.addAttribute("exampleImages", exampleImagesMap);

		//get expression only images
		JSONObject maAssociatedExpressionImagesResponse = JSONImageUtils
				.getAnatomyAssociatedExpressionImages(anatomy_id, config);
		int numberExpressionImagesFound = JSONRestUtil
				.getNumberFoundFromJsonResponse(maAssociatedExpressionImagesResponse);
		JSONArray expressionImageDocs = maAssociatedExpressionImagesResponse.getJSONObject(
				"response").getJSONArray("docs");
		model.addAttribute("numberExpressionImagesFound", numberExpressionImagesFound);
		model.addAttribute("expressionImages", expressionImageDocs);
		
		return "anatomy";
	}

	/**
	 * Get control and experimental example images for the top of the MP page
	 * 
	 * @param accession
	 *            MP Accession
	 * @return map containing control and experimental images with those as keys
	 * @throws SolrServerException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private Map<String, JSONObject> getExampleImages(String accession)
			throws SolrServerException, IOException, URISyntaxException {
		System.out.println("calling get anatomy image with accession:"
				+ accession);
		if (accession.equals("MA:0002950")) {
			// right eye examlple images
			// map.put("control",
			// "https://dev.mousephenotype.org/data/media/images/1253/M01211663_00032438_download_tn_small.jpg");
			// 255874
			Map<String, JSONObject> map = solrIndex.getExampleImages(255874,
					76516);
			// getImageDoc(255874, solrDocumentList);
			return map;
			// map.put("experimental",
			// "https://www.mousephenotype.org/data/media/images/550/M00226962_00007295_download_tn_small.jpg");
			// 76516

		}
		// if no rule for this return empty map
		return Collections.emptyMap();
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView handleGenericException(Exception exception) {
		exception.printStackTrace();
		ModelAndView mv = new ModelAndView("identifierError");
		mv.addObject("errorMessage", exception.getMessage());
		mv.addObject("acc", "This");
		mv.addObject("type", "mouse anatomy");
		mv.addObject("exampleURI", "/anatomy/MA:0002950");
		return mv;
	}

}
