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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.hibernate.HibernateException;
import org.hibernate.exception.JDBCConnectionException;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import uk.ac.ebi.generic.util.RegisterInterestDrupalSolr;
import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.dao.DatasourceDAO;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryBySex;
import uk.ac.ebi.phenotype.ontology.PhenotypeSummaryDAO;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummaryDAOReadOnly;
import uk.ac.ebi.phenotype.pojo.Xref;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.ObservationService;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.web.pojo.DataTableRow;
import uk.ac.ebi.phenotype.web.pojo.GenePageTableRow;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmWebDao;
import uk.ac.sanger.phenodigm2.model.Gene;
import uk.ac.sanger.phenodigm2.model.GeneIdentifier;
import uk.ac.sanger.phenodigm2.web.DiseaseAssociationSummary;


@Controller
public class GenesController {

	private final Logger log = LoggerFactory.getLogger(GenesController.class);

	RegisterInterestDrupalSolr registerInterest;

	@Autowired
	private DatasourceDAO datasourceDao;
	
	@Autowired
	private GenomicFeatureDAO genesDao;

	@Autowired
	private ImagesSolrDao imagesSolrDao;

	@Autowired
	private PhenotypeCallSummaryDAOReadOnly phenoDAO;

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
	
	private static final int numberOfImagesToDisplay=5;


	/**
	 * Runs when the request missing an accession ID. This redirects to the
	 * search page which defaults to showing all genes in the list
	 */
	@RequestMapping("/genes")
	public String rootForward() {
		return "redirect:/search";
	}

	/**
	 * Prints out the request object
	 */
	@RequestMapping("/genes/print-request")
	public ResponseEntity<String> printRequest(HttpServletRequest request) {

		Enumeration<String> s = request.getHeaderNames();

		while (s.hasMoreElements()) {
			String header = (String) s.nextElement();
			Enumeration<String> headers = request.getHeaders(header);

			while (headers.hasMoreElements()) {				
				String actualHeader = (String) headers.nextElement();
				System.out.println("Header: " + header + ", Value: " +actualHeader);
			}
		}

		HttpHeaders resp = new HttpHeaders();
		resp.setContentType(MediaType.APPLICATION_JSON);

		return  new ResponseEntity<String>(request.toString(), resp, HttpStatus.CREATED);
	}

	

	@RequestMapping("/genes/{acc}")
	public String genes(
			@PathVariable String acc,
			@RequestParam(value="heatmap", required=false, defaultValue="false") Boolean showHeatmap,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException {

		processGeneRequest(acc, model, request);

		return "genes";
	}

	private void processGeneRequest(String acc, Model model,
			HttpServletRequest request) throws GenomicFeatureNotFoundException,
			URISyntaxException, IOException {
		// see if the gene exists first:
		GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
		if (gene == null) {
			log.warn("Gene status for " + acc + " can't be found.");
			throw new GenomicFeatureNotFoundException("Gene " + acc + " can't be found.", acc);
		}	
		
		/**
		 * PRODUCTION STATUS (SOLR)
		 */
		String geneStatus = null;
		
		try {
			
			geneStatus = solrIndex.getGeneStatus(acc);
			model.addAttribute("geneStatus", geneStatus);//if gene status is null then the jsp declares a warning message at status div

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException exception) {
			throw new GenomicFeatureNotFoundException("Gene " + acc + " can't be found.", acc);
		}

		/**
		* Phenotype Summary
		*/

		PhenotypeSummaryBySex phenotypeSummaryObjects = null;

		try {
//		model.addAttribute("phenotypeSummary", phenSummary.getSummary(acc));
		phenotypeSummaryObjects =  phenSummary.getSummaryObjects(acc);
		model.addAttribute("phenotypeSummaryObjects",phenotypeSummaryObjects);
		} catch (Exception e){
		e.printStackTrace();
		} 

		model.addAttribute("isLive", new Boolean((String) request.getAttribute("liveSite")));
		
		model.addAttribute("phenotypeStarted", geneService.checkPhenotypeStarted( acc ));

		
		// Get list of triplets of pipeline, allele acc, phenotyping center
		// to link to an experiment page will all data
		try {
			
			List<Map<String,String>> dataMapList = observationService.getDistinctPipelineAlleleCenterListByGeneAccession(acc);
			model.addAttribute("dataMapList", dataMapList);
			
		} catch (SolrServerException e2) {
			e2.printStackTrace();
		}
	
			
		//code for assessing if the person is logged in and if so have they registered interest in this gene or not?
		registerInterest = new RegisterInterestDrupalSolr( config, request);
		this.registerInterestState(acc, model);

		try {
			getExperimentalImages(acc, model);
			getExpressionImages(acc, model);
		} catch (SolrServerException e1) {
			e1.printStackTrace();
			log.info("images solr not available");
			model.addAttribute("imageErrors","Something is wrong Images are not being returned when normally they would");
		}
		
		processPhenotypes(acc, model, "", request);

		model.addAttribute("gene", gene);
		model.addAttribute("request", request);
		model.addAttribute("acc", acc);

                //add in the disease predictions from phenodigm
                processDisease(acc, model);
                
		// ES Cell and IKMC Allele check (Gautier)
		
		String solrCoreName = "allele";
		String mode = "ikmcAlleleGrid";
		int countIKMCAlleles =0;
		boolean ikmcError = false;
		
		try {
			countIKMCAlleles = solrIndex.getNumFound("allele_name:"+gene.getSymbol(), solrCoreName, mode, "");
		}catch (Exception e) {
			model.addAttribute("countIKMCAllelesError", Boolean.TRUE);
			e.printStackTrace();
		}
		
		model.addAttribute("countIKMCAlleles", countIKMCAlleles);
		log.debug("CHECK IKMC allele error : " + ikmcError);
		log.debug("CHECK IKMC allele found : " + countIKMCAlleles);
		
		String prodStatusIcons = "Neither production nor phenotyping status available ";
		
		try {

			Map<String, String> prod = geneService.getProductionStatus(acc);
			prodStatusIcons = ( prod.get("icons").equalsIgnoreCase("") ) ? prodStatusIcons : prod.get("icons") ;
			model.addAttribute("orderPossible" , prod.get("orderPossible"));
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		model.addAttribute("prodStatusIcons" , prodStatusIcons);
	}

	/**
	 * @throws IOException 
	 */
	@RequestMapping("/genesPhenoFrag/{acc}")
	public String genesPhenoFrag(
			@PathVariable String acc,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException {
		//just pass on any query string after the ? to the solr requesting object for now
		String queryString=request.getQueryString();
		processPhenotypes(acc, model, queryString, request);

		return "PhenoFrag";
	}
	
	private Map<String, Map<String, Integer>> sortPhenFacets(Map<String, Map<String, Integer>> phenFacets){
		Map<String, Map<String, Integer>> sortPhenFacets = phenFacets;
		for (String key: phenFacets.keySet()){
			sortPhenFacets.put(key, new TreeMap<String, Integer>(phenFacets.get(key)));
		}
		return sortPhenFacets;
	}
	
	private void processPhenotypes(String acc, Model model, String queryString, HttpServletRequest request) throws IOException, URISyntaxException {
		//facet field example for project name and higher level mp term with gene as query : http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype/select/?q=marker_accession_id:MGI:98373&rows=100&version=2.2&start=0&indent=on&defType=edismax&wt=json&facet.field=project_name&facet.field=top_level_mp_term_name&facet=true		//top_level_mp_term_name
		if(queryString==null){
			queryString="";
		}
		// This block collapses phenotype rows
		// phenotype term, allele, zygosity, and sex
		// sex is collapsed into a single column
		List<PhenotypeCallSummary> phenotypeList = new ArrayList<PhenotypeCallSummary>();
		PhenotypeFacetResult phenoResult=null;
		try {

			phenoResult = phenoDAO.getPhenotypeCallByGeneAccessionAndFilter(acc, queryString);
			phenotypeList=phenoResult.getPhenotypeCallSummaries();

			Map<String, Map<String, Integer>> phenoFacets = phenoResult.getFacetResults();
			// sort facets first			
			model.addAttribute("phenoFacets", sortPhenFacets(phenoFacets));

		} catch (HibernateException|JSONException e) {
			log.error("ERROR GETTING PHENOTYPE LIST");
			e.printStackTrace();
			phenotypeList = new ArrayList<PhenotypeCallSummary>();
		}
		
		
		// This is a map because we need to support lookups
		Map<DataTableRow,DataTableRow> phenotypes = new HashMap<DataTableRow,DataTableRow>(); 
		
		for (PhenotypeCallSummary pcs : phenotypeList) {
			DataTableRow pr = new GenePageTableRow( pcs, request.getAttribute("baseUrl").toString());
                        
			// Collapse rows on sex
			if(phenotypes.containsKey(pr)) {
				pr = phenotypes.get(pr);
				TreeSet<String> sexes = new TreeSet<String>();
				for (String s : pr.getSexes()) { sexes.add(s); }
				sexes.add(pcs.getSex().toString());
				pr.setSexes(new ArrayList<String>(sexes));
			}
			
			phenotypes.put(pr, pr);
		}
		ArrayList<GenePageTableRow> l = new ArrayList(phenotypes.keySet());
		Collections.sort(l);
		model.addAttribute("phenotypes", l);

	}
	
	private Map<String, List<Map<String, String>>> getProviders(
		List<Map<String, String>> constructs) throws org.json.JSONException {
		
		Map<String, List<Map<String, String>>> nameToProvider=new HashMap<String,List< Map<String, String>>>(); 
		for(Map<String, String> construct: constructs){
			List<Map<String,String>> listOfProvidersForAllele=new ArrayList<Map<String,String>>();
			String alleleName=construct.get("alleleName");
			String providers=construct.get("providers");
			String orderFromUrls=construct.get("orderFromUrls");
			//providers are a json array so lets get the data out and put it in our java structure
			JSONArray providerArray=new JSONArray(providers);
			JSONArray urlArray=new JSONArray(orderFromUrls);
			//only seen single ones of these so far???
			for(int i=0; i<providerArray.length(); i++){
				String provider=providerArray.getString(i);
				String url=urlArray.getString(i);
				Map<String, String> providerAndUrl=new HashMap<String, String>();
				providerAndUrl.put("provider", provider);
				providerAndUrl.put("url", url);
				listOfProvidersForAllele.add(providerAndUrl);
				nameToProvider.put(alleleName, listOfProvidersForAllele);
			}
		}
		return nameToProvider;
	}

	/**
	 * Get the first 5 wholemount expression images if available
	 *  
	 * @param acc the gene to get the images for
	 * @param model the model to add the images to
	 * @throws SolrServerException 
	 */
	private void getExpressionImages(String acc, Model model) throws SolrServerException {

		QueryResponse solrExpressionR = imagesSolrDao.getExpressionFacetForGeneAccession(acc);
		if(solrExpressionR==null){
			log.error("no response from solr data source for acc="+acc);
			return;
		}

		List<FacetField> expressionfacets = solrExpressionR.getFacetFields();
		if (expressionfacets == null) {
			log.error("no expression facets from solr data source for acc="+acc);
			return;
		}

		Map<String, SolrDocumentList> facetToDocs = new HashMap<String, SolrDocumentList>();

		for (FacetField facet : expressionfacets) {
			if (facet.getValueCount() != 0) {
				for (Count value : facet.getValues()) {
					QueryResponse response = imagesSolrDao.getDocsForGeneWithFacetField(acc, "annotated_or_inferred_higherLevelMaTermName",value.getName(), "expName:\"Wholemount Expression\"", 0, numberOfImagesToDisplay);
					if(response != null) {
						facetToDocs.put(value.getName(), response.getResults());
					}
				}
			}
			model.addAttribute("expressionFacets", expressionfacets.get(0).getValues());
			model.addAttribute("expFacetToDocs", facetToDocs);
		}
	}

	/**
	 * Get the first 5 images for aall but the wholemount expression images
	 * if available
	 *  
	 * @param acc the gene to get the images for
	 * @param model the model to add the images to
	 * @throws SolrServerException 
	 */
	private void getExperimentalImages(String acc, Model model) throws SolrServerException {

		QueryResponse solrR = imagesSolrDao.getExperimentalFacetForGeneAccession(acc);
		if(solrR==null){
			log.error("no response from solr data source for acc="+acc);
			return;
		}

		List<FacetField> facets = solrR.getFacetFields();
		if(facets==null){
			log.error("no facets from solr data source for acc="+acc);
			return;
		}

		Map<String, SolrDocumentList> facetToDocs = new HashMap<String, SolrDocumentList>();
		List<Count> filteredCounts=new ArrayList<Count>();
		
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
						QueryResponse response = imagesSolrDao.getDocsForGeneWithFacetField(acc, "expName", count.getName(),"", 0, numberOfImagesToDisplay);
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
	

	/**
	 * Add to the model the registered/unregistered interest labels
	 * 
	 * @param acc
	 * @param model
	 */
	private void registerInterestState(String acc, Model model) {
		String registerInterestButtonString = "";
		String registerButtonAnchor = "";
		String id = acc;

		if (registerInterest.loggedIn()) {
			if (registerInterest.alreadyInterested(acc)) {
				registerInterestButtonString = "Unregister interest";
				id = acc;
			} else {
				registerInterestButtonString = "Register interest";
				id = acc;
			}
		} else {
			registerInterestButtonString = "Login to register interest";
			registerButtonAnchor = "/user/register";

		}

		model.addAttribute("registerInterestButtonString", registerInterestButtonString);
		model.addAttribute("registerButtonAnchor", registerButtonAnchor);
		model.addAttribute("registerButtonId", id);
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
        mv.addObject("exampleURI", "/genes/MGI:104874");
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


	/**
	 * Display an identifier error page
	 */
	@RequestMapping("/identifierError")
	public String identifierError(
			@PathVariable String acc, 
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) {
		return "identifierError";
	}
	

	/**
	 * @throws IOException 
	 */
	@RequestMapping("/genomeBrowser/{acc}")
	public String genomeBrowser(
			@PathVariable String acc,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException {
		System.out.println("genome browser called");
		GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
		System.out.println("gene in browser="+gene);
		if (gene == null) {
			log.warn("Gene status for " + acc + " can't be found.");
		}	
		Datasource ensembl = datasourceDao.getDatasourceByShortName("Ensembl");
		Datasource vega = datasourceDao.getDatasourceByShortName("VEGA");
		Datasource ncbi = datasourceDao.getDatasourceByShortName("EntrezGene");
		Datasource ccds = datasourceDao.getDatasourceByShortName("cCDS");

		List<String> ensemblIds = new ArrayList<String>();
		List<String> vegaIds = new ArrayList<String>();
		List<String> ncbiIds = new ArrayList<String>();
		List<String> ccdsIds = new ArrayList<String>();

		List<Xref> xrefs = gene.getXrefs();
		for(Xref xref:xrefs) {
			if (xref.getXrefDatabaseId() == ensembl.getId()) {
				ensemblIds.add(xref.getXrefAccession());
			} else if (xref.getXrefDatabaseId() == vega.getId()) {
				vegaIds.add(xref.getXrefAccession());
			} else if (xref.getXrefDatabaseId() == ncbi.getId()) {
				ncbiIds.add(xref.getXrefAccession());
			} else if (xref.getXrefDatabaseId() == ccds.getId()) {
				ccdsIds.add(xref.getXrefAccession());
			}
		}

		model.addAttribute("ensemblIds", ensemblIds);
		model.addAttribute("vegaIds", vegaIds);
		model.addAttribute("ncbiIds", ncbiIds);
		model.addAttribute("ccdsIds", ccdsIds);

		model.addAttribute("gene",gene);
		return "genomeBrowser";
	}
	
    @RequestMapping("/genesAllele/{acc}")
    public String genesAllele(
       		@PathVariable String acc,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException {
                
        List<Map<String, String>> constructs = solrIndex.getGeneAlleleInfo(acc);

        model.addAttribute("alleleProducts", constructs);
	    return "genesAllele";  
        }   

    @Autowired
    private PhenoDigmWebDao phenoDigmDao;
    private final double rawScoreCutoff = 1.97;
    
    /**
     * Adds disease-related info to the model from Phenodigm.
     * @param acc
     * @param model 
     */
    private void processDisease(String acc, Model model) {
        String mgiId = acc;
        log.info("Adding disease info to gene page {}", mgiId);
        model.addAttribute("mgiId", mgiId);
        GeneIdentifier geneIdentifier = new GeneIdentifier(mgiId, mgiId);
        
        Gene gene = phenoDigmDao.getGene(geneIdentifier);
        log.info("Found Gene: " + gene);
        if (gene != null) {
            model.addAttribute("geneIdentifier", gene.getOrthologGeneId());
            model.addAttribute("humanOrtholog", gene.getHumanGeneId());
            log.info("Found gene: {} {}", gene.getOrthologGeneId().getCompoundIdentifier(), gene.getOrthologGeneId().getGeneSymbol());
        } else {
            model.addAttribute("geneIdentifier", geneIdentifier);
            log.info("No human ortholog found for gene: {}", geneIdentifier);                
        }
        
        log.info("{} - getting disease-gene associations using cutoff {}", geneIdentifier, rawScoreCutoff);
        List<DiseaseAssociationSummary> diseaseAssociationSummarys = phenoDigmDao.getGeneToDiseaseAssociationSummaries(geneIdentifier, rawScoreCutoff);
        log.info("{} - recieved {} disease-gene associations", geneIdentifier, diseaseAssociationSummarys.size());

//        List<DiseaseAssociationSummary> knownDiseaseAssociationSummaries = new ArrayList<>();
//        //add the known association summaries to a dedicated list for the top panel
//        for (DiseaseAssociationSummary diseaseAssociationSummary : diseaseAssociationSummarys) {
//            AssociationSummary associationSummary = diseaseAssociationSummary.getAssociationSummary();
//            if (associationSummary.isAssociatedInHuman()) {
//               knownDiseaseAssociationSummaries.add(diseaseAssociationSummary);
//            }
//        }
//        model.addAttribute("knownDiseaseAssociationSummaries", knownDiseaseAssociationSummaries);                   
        model.addAttribute("diseaseAssociations", diseaseAssociationSummarys);
        
        log.info("Added {} disease associations for gene {} to model", diseaseAssociationSummarys.size(), mgiId);
    }
    
}
