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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.hibernate.HibernateException;
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
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.data.imits.ColonyStatus;
import uk.ac.ebi.phenotype.data.imits.PhenotypeStatusDAO;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.Xref;
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
	private PhenotypeCallSummaryDAO phenoDAO;

	@Autowired
	SolrIndex solrIndex;

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
			RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException {

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
			
			PhenotypeStatusDAO psDao = new PhenotypeStatusDAO();
			//allColonyStatus = solrIndex.getGeneColonyStatus(acc);
			allColonyStatus = psDao.getColonyStatus(gene);
			
			/** check whether the phenotype has started */
			for (ColonyStatus st: allColonyStatus) {
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
		
		// This block collapses phenotype rows
		// phenotype term, allele, zygosity, and sex
		// sex is collapsed into a single column
		List<PhenotypeCallSummary> phenotypeList = new ArrayList<PhenotypeCallSummary>();
		try {
			phenotypeList = phenoDAO.getPhenotypeCallByAccession(acc, 3);
		} catch (HibernateException e) {
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

			// DO not include these for the gene datatable
			pr.setProcedure(pcs.getProcedure());
			pr.setParameter(pcs.getParameter());

			if(phenotypes.containsKey(pr)) {
				pr = phenotypes.get(pr);
				TreeSet<String> sexes = new TreeSet<String>();
				for (String s : pr.getSexes()) { sexes.add(s); }
				sexes.add(pcs.getSex().toString());
				pr.setSexes(new ArrayList<String>(sexes));
			}

			phenotypes.put(pr, pr);
		}
		model.addAttribute("phenotypes", new ArrayList<PhenotypeRow>(phenotypes.keySet()));

		model.addAttribute("gene", gene);
		model.addAttribute("request", request);
		model.addAttribute("acc", acc);

		/*
		 * FETCH Data in progress
		 */
		
		// crappy code to shoehorn the QC panel
		// uncomment if you want to test locally
		//proxy.setDebugSession("SSESSfbfcc940c73e911682a51bb9f1c59a76=y1tfAywDbKYLw_ROFZX6ysb0H9V3yKk7M9E2rfigei0");

// COMMENTED 2013-04-26 (since we are not showing the panel anyway		
//		URL url;
//		try {
//
//			DrupalHttpProxy proxy = new DrupalHttpProxy(request);
//			String drupalBaseUrl = (String) request.getAttribute("drupalBaseUrl");
//			url = new URL(drupalBaseUrl + "/phenotypes/" + acc);
//			String content = proxy.getContent(url);
//			
//			/* we know phenotype has started */
//			
//			if (phenotypeStarted) {
//				
//				model.addAttribute("bPreQC", new Boolean(content.contains("qc?")));
//				if (content.contains("qc?")) {
//					int beginIndex = content.indexOf("/qc?");
//					String sub1 = content.substring(beginIndex);
//					int endIndex = sub1.indexOf("'");
//					String sub2 = sub1.substring(0, endIndex);
//					model.addAttribute("qcLink", sub2);
//				}
//			}
//			model.addAttribute("bSangerLegacy", new Boolean(content.contains("sanger")));
//			if (content.contains("sanger")) {
//				int beginIndex = content.indexOf("http://www.sanger");
//				String sub1 = content.substring(beginIndex);
//				int endIndex = sub1.indexOf("'");
//				String sub2 = sub1.substring(0, endIndex);
//				model.addAttribute("sangerLegacyLink", sub2);
//			}
//			model.addAttribute("bEurophenomeLegacy", new Boolean(content.contains("europhenome")));
//			if (content.contains("europhenome")) {
//				// http://www.europhenome.org/databrowser/viewer.jsp?set=true&m=true&l=10035
//				int beginIndex = content.indexOf("http://www.europhenome.org");
//				String sub1 = content.substring(beginIndex);
//				int endIndex = sub1.indexOf("'");
//				String sub2 = sub1.substring(0, endIndex);
//				model.addAttribute("europhenomeLegacyLink", sub2);
//			}			
//		} catch (MalformedURLException e) {
//			log.error(e.getLocalizedMessage());
//		} catch (IOException e) {
//			log.error(e.getLocalizedMessage());
//		}

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
		log.debug("CHECK IKMC allele found : " + countIKMCAlleles);

		return "genes";
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
	private void registerInterestState(String acc, Model model){
		String registerInterestButtonString="Login to register interest";
		String registerButtonAnchor="/user/register";
		String id=acc;

		if (registerInterest.loggedIn()) {
			if (registerInterest.alreadyInterested(acc)) {
				registerInterestButtonString= "Unregister interest";
			} else {
				registerInterestButtonString= "Register interest";
			}
		}

		model.addAttribute("registerInterestButtonString", registerInterestButtonString);
		model.addAttribute("registerButtonAnchor", registerButtonAnchor);
		model.addAttribute("registerButtonId",id);
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
	
}
