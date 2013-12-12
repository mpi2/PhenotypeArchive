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
import uk.ac.ebi.phenotype.pojo.Xref;
import uk.ac.ebi.phenotype.util.PhenotypeCallSummaryDAOReadOnly;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.web.pojo.PhenotypeRow;


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
		model.addAttribute("phenotypeSummary", phenSummary.getSummary(acc));
		phenotypeSummaryObjects =  phenSummary.getSummaryObjects(acc);
		model.addAttribute("phenotypeSummaryObjects",phenotypeSummaryObjects);
		} catch (Exception e){
		e.printStackTrace();
		} 
		
		/**
		 * PHENOTYPE STATUS (IMITS BIOMART)
		 */
		
		List<ColonyStatus> allColonyStatus = new ArrayList<ColonyStatus>();
		boolean phenotypeStarted = false;
		String phenotypeStatus = null;
		
		model.addAttribute("isLive", new Boolean((String) request.getAttribute("liveSite")));
		
		try {
			/*
			 * TODO this should be done allele by allele
			 */
			
			//PhenotypeStatusDAO psDao = new SolrPhenotypeStatusDAOImpl();
			//allColonyStatus = solrIndex.getGeneColonyStatus(acc);
			allColonyStatus = psDao.getColonyStatus(gene);
			
			/** check whether the phenotype has started */
			for (ColonyStatus st: allColonyStatus) {
				//System.out.println("allcolony status="+st.getAlleleName()+" "+st.getAlleleType()+" "+st.getBackgroundStrain()+" "+st.getPhenotypeCenter()+" "+st.getPhenotypeStatus()+" "+st.getProductionStatus());
				if (st.getPhenotypeStarted() == 1 && st.getPhenotypeCompleted() == 0) {
					phenotypeStarted = true;
				}
				if (st.getPhenotypeCompleted() == 1) {
					phenotypeStatus = "Complete";
				} else
				if (st.getPhenotypeStarted() == 1 && (phenotypeStatus == null || !phenotypeStatus.equals("Complete"))) {
					phenotypeStatus = "Started";
				} else 
				if (st.getPhenotypeStatus().equals("Phenotype Attempt Registered") && (phenotypeStatus == null || !phenotypeStatus.equals("Complete") || !phenotypeStatus.equals("Started"))) {
						phenotypeStatus = "Attempt Registered";
					}
			}
			
			log.info("geneStatus="+geneStatus);//doesn't fail if null
				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			model.addAttribute("allColonyStatus", allColonyStatus);
			model.addAttribute("phenotypeStarted", new Boolean(phenotypeStarted));
			model.addAttribute("phenotypeStatus", phenotypeStatus);
		}
		
		List<Map<String,String>> constructs=new ArrayList<Map<String,String>>();
		Map<String, List<Map<String, String>>> providers=null;
		//http://ikmc.vm.bytemark.co.uk:8983/solr/allele/search?json.wrf=jQuery181021836050949059427_1369411113212&bq=product_type%3A%22ES+Cell%22%5E100+type%3Ami_attempt%5E10&q=mgi_accession_id%3AMGI%3A104874&start=0&rows=100&hl=true&wt=json&_=1369411113318
		//introduce a method here to get the table data for the constructs available and buy button.
		try {
			constructs=solrIndex.getProductionInfo(acc);
			//look at the constructs to find the order urls then store them in a map with the full alleleName as the key
			try {
				providers=this.getProviders(constructs);
				model.addAttribute("providers",providers);
			} catch (org.json.JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		model.addAttribute("constructs", constructs);
		

		
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
		
		processPhenotypes(acc, model, "");

		model.addAttribute("gene", gene);
		model.addAttribute("request", request);
		model.addAttribute("acc", acc);

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
		processPhenotypes(acc, model, queryString);

		return "PhenoFrag";
	}
	
	private Map<String, Map<String, Integer>> sortPhenFacets(Map<String, Map<String, Integer>> phenFacets){
		Map<String, Map<String, Integer>> sortPhenFacets = phenFacets;
		for (String key: phenFacets.keySet()){
			sortPhenFacets.put(key, new TreeMap<String, Integer>(phenFacets.get(key)));
		}
		return sortPhenFacets;
	}
	
	private void processPhenotypes(String acc, Model model, String queryString) throws IOException, URISyntaxException {
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
		Map<PhenotypeRow,PhenotypeRow> phenotypes = new HashMap<PhenotypeRow,PhenotypeRow>(); 
		
		for (PhenotypeCallSummary pcs : phenotypeList) {

			// Use a tree set to maintain an alphabetical order (Female, Male)
			List<String> sex = new ArrayList<String>();
			sex.add(pcs.getSex().toString());

			PhenotypeRow pr = new PhenotypeRow();
			pr.setAllele(pcs.getAllele());
			pr.setSexes(sex);
			pr.setPhenotypeTerm(pcs.getPhenotypeTerm());

			// zygosity representation depends on source of information
			// we need to know what the data source is so we can generate appropriate link on the page
			Datasource ds = pcs.getDatasource();
			String dataSourceName = "";

			// Defend in case the datasource is not loaded
			if (ds != null) {
				dataSourceName = ds.getName();
			}
			pr.setDataSourceName(dataSourceName);

			// this should be the fix but EuroPhenome is buggy
			String rawZygosity = (dataSourceName.equals("EuroPhenome")) ? 
					//Utilities.getZygosity(pcs.getZygosity()) : pcs.getZygosity().toString();
					"All" : pcs.getZygosity().toString();
			pr.setRawZygosity(rawZygosity);
			pr.setZygosity(pcs.getZygosity());
			pr.setProjectId(pcs.getExternalId());
			pr.setProcedure(pcs.getProcedure());
			pr.setParameter(pcs.getParameter());
			if (pcs.getPhenotypingCenter() != null)
				pr.setPhenotypingCenter(pcs.getPhenotypingCenter());
			if(phenotypes.containsKey(pr)) {
				pr = phenotypes.get(pr);
				TreeSet<String> sexes = new TreeSet<String>();
				for (String s : pr.getSexes()) { sexes.add(s); }
				sexes.add(pcs.getSex().toString());
				pr.setSexes(new ArrayList<String>(sexes));
			}

			phenotypes.put(pr, pr);
		}
		ArrayList<PhenotypeRow> l = new ArrayList<PhenotypeRow>(phenotypes.keySet());
		Collections.sort(l); // sort in alpha order by MP term name
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
					QueryResponse response = imagesSolrDao.getDocsForGeneWithFacetField(acc, "annotated_or_inferred_higherLevelMaTermName",value.getName(), "expName:\"Wholemount Expression\"", 0, 6);
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
	@RequestMapping("/genesEnu/{acc}")
	public String genesEnuFrag(
			@PathVariable String acc,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException {
		//just pass on any query string after the ? to the solr requesting object for now
//		String queryString=request.getQueryString();
//		processPhenotypes(acc, model, queryString);
		//send a request to their web service https://databases.apf.edu.au/mutations/snpRow/getSnpCount?mgiAccessionId=MGI:1935228
		String url="https://databases.apf.edu.au/mutations/snpRow/getSnpCount?mgiAccessionId="+acc;
		JSONObject result = JSONRestUtil.getResults(url);
		
		int count=result.getInt("count");
		//System.out.println("count="+count);

	model.addAttribute("makeEnuLink",count);

		return "genesEnuFrag";
	}
	
}
