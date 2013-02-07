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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import uk.ac.ebi.generic.util.RegisterInterestDrupalSolr;
import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.dao.GeneDao;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.web.pojo.PhenotypeRow;
import uk.ac.ebi.phenotype.web.util.HttpProxy;


@Controller
public class GenesController implements BeanFactoryAware {

	private final Logger log = LoggerFactory.getLogger(GenesController.class);
	private BeanFactory bf;

	RegisterInterestDrupalSolr registerInterest;
	
	@Autowired
	private GeneDao geneBiomart;
	
	@Autowired
	private GenomicFeatureDAO genesDao;

	@Autowired
	private ImagesSolrDao imagesSolrDao;

	@Autowired
	private PhenotypeCallSummaryDAO phenoDAO;

	/**
	 * Runs when the request missing an accession ID. This redirects to the
	 * search page which defaults to showing all genes in the list
	 */
	@RequestMapping("/genes")
	public String rootForward() {
		return "redirect:/search";
	}

	@RequestMapping("/genes/{acc}")
	public String genes(
			@PathVariable String acc, 
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException {

		// Get the global application configuration
		@SuppressWarnings("unchecked")
		Map<String,String> config = (Map<String,String>) bf.getBean("globalConfiguration");
		
		// see if the gene exists first:
		GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
		if (gene == null) {
			throw new GenomicFeatureNotFoundException("Gene " + acc + " can't be found.", acc);
		}
		
		//get status from biomart- do we want a gene object to hold info such as status? at the moment we are using GenomicFeature..?
		String geneStatus = null;
		
		try {
			geneStatus = geneBiomart.getGeneStatus(acc);
		} catch (IndexOutOfBoundsException exception) {
			throw new GenomicFeatureNotFoundException("Gene " + acc + " can't be found.", acc);
		}
		
		model.addAttribute("geneStatus", geneStatus);
		System.out.println("geneStatus="+geneStatus);
		
		//code for assessing if the person is logged in and if so have they registered interest in this gene or not?
		registerInterest=new RegisterInterestDrupalSolr( config, request);
		//boolean  alreadyInterested=registerInterest.alreadyInterested(acc);
		this.registerInterestState(acc, model);

		getExperimentalImages(acc, model);
		getExpressionImages(acc, model);
		
		// This block collapses phenotype rows
		// phenotype term, allele, zygosity, and sex
		// sex is collapsed into a single column
		List<PhenotypeCallSummary> phenotypeList = phenoDAO.getPhenotypeCallByAccession(acc, 3);
		Map<PhenotypeRow,PhenotypeRow> phenotypes = new HashMap<PhenotypeRow,PhenotypeRow>(); 
		for (PhenotypeCallSummary pcs : phenotypeList) {

			// Use a tree set to maintain an alphabetical order
			Set<String> sex = new TreeSet<String>();
			sex.add(pcs.getSex().toString());

			PhenotypeRow pr = new PhenotypeRow();
			pr.setAllele(pcs.getAllele());
			pr.setSexes(sex);
			pr.setPhenotypeTerm(pcs.getPhenotypeTerm());
			pr.setZygosity(pcs.getZygosity());
			pr.setProjectId(pcs.getExternalId());
			pr.setProcedureId(pcs.getProcedure().getStableId());
			String parameterId = pcs.getParameter().getStableId();
			pr.setParameterId(parameterId.substring(0, parameterId.length()-4));

			if(phenotypes.containsKey(pr)) {
				pr = phenotypes.get(pr);
				pr.getSexes().add(pcs.getSex().toString());
			}
			phenotypes.put(pr, pr);
		}
		model.addAttribute("phenotypes", new ArrayList<PhenotypeRow>(phenotypes.keySet()));

		model.addAttribute("gene", gene);
		model.addAttribute("request", request);
		model.addAttribute("acc", acc);

		// crappy code to shoehorn the QC panel
		HttpProxy proxy = new HttpProxy();
		// uncomment if you want to test locally
		//proxy.setDebugSession("SSESSfbfcc940c73e911682a51bb9f1c59a76=y1tfAywDbKYLw_ROFZX6ysb0H9V3yKk7M9E2rfigei0");
		URL url;
		try {
			String drupalBaseUrl = (String) request.getAttribute("drupalBaseUrl");
			url = new URL(drupalBaseUrl + "/phenotypes/" + acc);
			String content = proxy.getContent(url);
			model.addAttribute("bPreQC", new Boolean(content.contains("qc?")));
			if (content.contains("qc?")) {
				int beginIndex = content.indexOf("/qc?");
				String sub1 = content.substring(beginIndex);
				int endIndex = sub1.indexOf("'");
				String sub2 = sub1.substring(0, endIndex);
				model.addAttribute("qcLink", sub2);
			}
			model.addAttribute("bSangerLegacy", new Boolean(content.contains("sanger")));
			if (content.contains("sanger")) {
				int beginIndex = content.indexOf("http://www.sanger");
				String sub1 = content.substring(beginIndex);
				int endIndex = sub1.indexOf("'");
				String sub2 = sub1.substring(0, endIndex);
				model.addAttribute("sangerLegacyLink", sub2);
			}
			model.addAttribute("bEurophenomeLegacy", new Boolean(content.contains("europhenome")));
			if (content.contains("europhenome")) {
				// http://www.europhenome.org/databrowser/viewer.jsp?set=true&m=true&l=10035
				int beginIndex = content.indexOf("http://www.europhenome.org");
				String sub1 = content.substring(beginIndex);
				int endIndex = sub1.indexOf("'");
				String sub2 = sub1.substring(0, endIndex);
				model.addAttribute("europhenomeLegacyLink", sub2);
			}			
		} catch (MalformedURLException e) {
			log.debug(e.getLocalizedMessage());
		} catch (IOException e) {
			log.debug(e.getLocalizedMessage());
		}
			
		// ES Cell and IKMC Allele check (Gautier)
		
		String solrCoreName = "allele";
		String mode = "ikmcAlleleGrid";
		int countIKMCAlleles = 0;
		if (gene != null) {
			SolrIndex solrIndex = new SolrIndex("allele_name:"+gene.getSymbol(), solrCoreName, mode, config);
			countIKMCAlleles = solrIndex.fetchNumFound();
		} else {
			attributes.addFlashAttribute("message", "Gene <b>" + acc + "</b> was not found. Please search for your gene of interest.");
			return "redirect:/search";
		}
		model.addAttribute("countIKMCAlleles", countIKMCAlleles);
		log.debug("CHECK IKMC allele found : " + countIKMCAlleles);

		return "genes";
	}

	private void getExpressionImages(String acc, Model model) {
		log.info("hi there: 1");
		QueryResponse solrExpressionR = imagesSolrDao.getExpressionFacetForGeneAccession(acc);
		if(solrExpressionR==null){
			System.err.println("no response from solr data source for acc="+acc);
			return;
		}
		log.info("hi there: 2");
		log.info("solrEpressionR: "+solrExpressionR);
		List<FacetField> expressionfacets = solrExpressionR.getFacetFields();
		Map<String, SolrDocumentList> facetToDocs = new HashMap<String, SolrDocumentList>();

		if (expressionfacets != null) {
		for (FacetField facet : expressionfacets) {
			if (facet.getValueCount() != 0) {
				for (Count value : facet.getValues()) {
					QueryResponse response = imagesSolrDao.getDocsForGeneWithFacetField(acc, "higherLevelMaTermName",value.getName(), "expName:\"Wholemount Expression\"", 0, 6);
					if(response != null) {
						facetToDocs.put(value.getName(), response.getResults());
					}
				}
			}

			model.addAttribute("expressionFacets", expressionfacets.get(0).getValues());
			model.addAttribute("expFacetToDocs", facetToDocs);
		}
		}
	}

	private void getExperimentalImages(String acc, Model model) {
		QueryResponse solrR = imagesSolrDao.getExperimentalFacetForGeneAccession(acc);
		if(solrR==null){
			System.err.println("no response from solr data source for acc="+acc);
			return;
		}
		List<FacetField> facets = solrR.getFacetFields();
		Map<String, SolrDocumentList> facetToDocs = new HashMap<String, SolrDocumentList>();
		List<Count> filteredCounts=new ArrayList<Count>();
		
		if (facets != null) {
			for (FacetField facet : facets) {
				if (facet.getValueCount() != 0) {
					//get rid of wholemount expression facet
					for(Count count: facets.get(0).getValues()){
						if(!count.getName().equals("Wholemount Expression")){
							filteredCounts.add(count);
						}
					}
					for (Count count : facet.getValues()) {
						if(!count.getName().equals("Wholemount Expression")){
						// get 5 images if available for this experiment type
						QueryResponse response = imagesSolrDao.getDocsForGeneWithFacetField(acc, "expName", count.getName(),"", 0, 6);
						if(response != null) {
							facetToDocs.put(count.getName(), response.getResults());
						}
					}
					}
				}

				model.addAttribute("solrFacets", filteredCounts);
				model.addAttribute("facetToDocs", facetToDocs);
			}
		}
	}

	/**
	 * required for implementing BeanFactoryAware
	 * 
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		this.bf=arg0;
	}
	
	
	private void registerInterestState(String acc, Model model){
		String registerInterestButtonString="";
		String registerButtonAnchor="";
		String id=acc;
		if (registerInterest.loggedIn()) {
			if (registerInterest.alreadyInterested(acc)) {
				//System.out.println("<a id='"+acc+"' href='' class='btn primary interest'>Unregister interest</a>");
				registerInterestButtonString= "Unregister interest";
				id=acc;
			} else {
				//System.out.println("<a id='"+acc+"' href='' class='btn primary interest'>Register interest</a>");
				registerInterestButtonString= "Register interest";
				id=acc;
			}
		} else {
			//System.out.println("<a href='/user/register' class='btn primary'>Login to register interest</a>");
			registerInterestButtonString= "Login to register interest";
			registerButtonAnchor="/user/register";
			
		}
		
		
		model.addAttribute("registerInterestButtonString", registerInterestButtonString);
		
		model.addAttribute("registerButtonAnchor", registerButtonAnchor);
		
	
			model.addAttribute("registerButtonId",id);
	
		

	}
	
	@ExceptionHandler(GenomicFeatureNotFoundException.class)
	public ModelAndView handleGenomicFeatureNotFoundException(GenomicFeatureNotFoundException exception) {
        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage",exception.getMessage());
        mv.addObject("acc",exception.getAcc());
        mv.addObject("type","MGI gene");
        mv.addObject("exampleURI", "/genes/MGI:104874");
        return mv;
    } 
	
	@RequestMapping("/identifierError")
	public String identifierError(
			@PathVariable String acc, 
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) {
		return "identifierError";
	}
	
/*	@ExceptionHandler(GenomicFeatureNotFoundException.class)
	public RedirectView handleGenomicFeatureNotFoundException(GenomicFeatureNotFoundException ex, HttpServletRequest request) throws IOException
	{
		RedirectView redirectView = new RedirectView("identifierError");
		redirectView.addStaticAttribute("errorMessage", ex.getMessage());
		return redirectView;
	}

	@RequestMapping(value = "/identifierError")
	public String errorRedirectPage(HttpServletRequest request, Model model, @RequestParam("errorMessage") String errorMessage)
	{
		model.addAttribute("errorMessage", errorMessage);
		return "identifierError";
	}
*/
	
	
}
