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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
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

import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.dao.OntologyTermDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummaryDAOReadOnly;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.Synonym;
import uk.ac.ebi.phenotype.service.ExperimentService;
import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;
import uk.ac.ebi.phenotype.service.MpService;
import uk.ac.ebi.phenotype.service.ObservationService;
import uk.ac.ebi.phenotype.util.ParameterToGeneMap;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.util.PhenotypeGeneSummaryDTO;
import uk.ac.ebi.phenotype.web.pojo.PhenotypeRow;

@Controller
public class PhenotypesController {

	private final Logger log = LoggerFactory.getLogger(PhenotypesController.class);


	@Autowired
	ObservationService os;

	@Autowired
	private OntologyTermDAO ontoTermDao;
	
	@Autowired
	private PhenotypeCallSummaryDAOReadOnly phenoDAO;

	@Autowired
	private SolrIndex solrIndex;
	
	@Autowired
	private PhenotypePipelineDAO pipelineDao;
	
	@Autowired
	private ImagesSolrDao imagesSolrDao;

	@Autowired
	private ExperimentService experimentService;

	@Autowired
	GenotypePhenotypeService gpService;
	
	@Autowired
	MpService mpService;
	
	@Autowired 
	ParameterToGeneMap ptgm;
	
	@Resource(name="globalConfiguration")
	private Map<String, String> config;
	
	private static final int numberOfImagesToDisplay=5;

	/**
	 * Phenotype controller loads information required for displaying 
	 * the phenotype page or, in the case of an error, redirects to the 
	 * error page
	 * 
	 * @param phenotype_id the Mammalian phenotype id of the phenotype to display
	 * @return the name of the view to render, or redirect to search page on error
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws SolrServerException 
	 * @throws SQLException 
	 * 
	 */
	@RequestMapping(value="/phenotypes/{phenotype_id}", method=RequestMethod.GET)
	public String loadMpPage(
			@PathVariable String phenotype_id, 
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {
		
		OntologyTerm oTerm = ontoTermDao.getOntologyTermByAccessionAndDatabaseId(phenotype_id, 5);
		
		if (oTerm == null) {
			throw new OntologyTermNotFoundException("", phenotype_id);
		}
		

//		OntologyTerm oTerm=new OntologyTerm();
//		DatasourceEntityId dId=new DatasourceEntityId();
//		dId.setAccession(phenotype_id);
//		dId.setDatabaseId(5);
//		oTerm.setId(dId);
			
		Set<OntologyTerm> anatomyTerms = new HashSet<OntologyTerm>();
		Set<OntologyTerm> mpSiblings = new HashSet<OntologyTerm>();
		Set<OntologyTerm> goTerms = new HashSet<OntologyTerm>();
		Set<Synonym> synonymTerms = new HashSet<Synonym>();
		
		try {

			JSONObject mpData = solrIndex
				.getMpData(phenotype_id)
				.getJSONObject("response")
				.getJSONArray("docs")
				.getJSONObject(0);
			JSONArray terms;
			
			if (mpData.containsKey("ontology_subset")){
				model.addAttribute("isImpcTerm", mpData.getJSONArray("ontology_subset").contains("IMPC_Terms"));
			}
			else {
				model.addAttribute("isImpcTerm", false);
			}
			
			if (mpData.containsKey("mp_term")) {
				String term = mpData.getString("mp_term");
				oTerm.setName(term);
			}
			
			if (mpData.containsKey("mp_definition")) {
				String definition = mpData.getString("mp_definition");
				oTerm.setDescription(definition);
			}
			
			if (mpData.containsKey("mp_term_synonym")) {
				JSONArray syonymsArray = mpData.getJSONArray("mp_term_synonym");
				for(Object syn:syonymsArray) {
					String synm = (String) syn;
					Synonym synonym=new Synonym();
					synonym.setSymbol(synm);
					synonymTerms.add(synonym);
				}
			}
			
			if (mpData.containsKey("ma_id")) {
				terms = mpData.getJSONArray("ma_id");
				for (Object maObj : terms) {
					String id = (String) maObj;
					anatomyTerms.add(ontoTermDao.getOntologyTermByAccessionAndDatabaseId(id, 8));
				}
			}

			if (mpData.containsKey("sibling_mp_id")) {
				terms = mpData.getJSONArray("sibling_mp_id");
				for (Object obj : terms) {
					String id = (String) obj;
					if( ! id.equals(phenotype_id)) {
						mpSiblings.add(ontoTermDao.getOntologyTermByAccessionAndDatabaseId(id, 5));
					}
				}
			}

			if (mpData.containsKey("go_id")) {
				terms = mpData.getJSONArray("go_id");
				for (Object obj : terms) {
					String id = (String) obj;
					goTerms.add(ontoTermDao.getOntologyTermByAccessionAndDatabaseId(id, 11));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			anatomyTerms = new HashSet<OntologyTerm>();
			mpSiblings = new HashSet<OntologyTerm>();
		}
		
		model.addAttribute("anatomy", anatomyTerms);
		model.addAttribute("go", goTerms);
		model.addAttribute("siblings", mpSiblings);
		model.addAttribute("synonyms", synonymTerms);
		// Query the images for this phenotype
		QueryResponse response=imagesSolrDao.getDocsForMpTerm(phenotype_id, 0, numberOfImagesToDisplay);
		model.addAttribute("numberFound", response.getResults().getNumFound());
		model.addAttribute("images", response.getResults());
		//get a string, image map instead?
		Map<String,JSONObject> exampleImagesMap=getExampleImages(phenotype_id);
		model.addAttribute("exampleImages",exampleImagesMap);

		processPhenotypes(phenotype_id, "", model);

		model.addAttribute("isLive", new Boolean((String) request.getAttribute("liveSite")));
		
		TreeSet<Procedure> procedures = new TreeSet<Procedure>(pipelineDao.getProceduresByOntologyTerm(oTerm));
		model.addAttribute("phenotype", oTerm);
		model.addAttribute("procedures", procedures);
		model.addAttribute("genePercentage", getPercentages(phenotype_id));
		
		model.addAttribute("parametersAssociated", getParameters(phenotype_id));
		//TODO move all getDataOverviewCharts to the OverviewChartsController
//		model.addAttribute("overviewPhenCharts", getDataOverviewCharts(phenotype_id, model));
		
		return "phenotypes";
	}
		
	private Map<String, Map<String, Integer>> sortPhenFacets(Map<String, Map<String, Integer>> phenFacets){
		Map<String, Map<String, Integer>> sortPhenFacets = phenFacets;
		for (String key: phenFacets.keySet()){
			sortPhenFacets.put(key, new TreeMap<String, Integer>(phenFacets.get(key)));
		}
		return sortPhenFacets;
	}

	private void processPhenotypes(String phenotype_id, String filter, Model model) throws IOException, URISyntaxException {
		// This block collapses phenotype rows
		// phenotype term, allele, zygosity, and sex
		// sex is collapsed into a single column
		List<PhenotypeCallSummary> phenotypeList = new ArrayList<PhenotypeCallSummary>();
		try {
			PhenotypeFacetResult phenoResult = phenoDAO.getPhenotypeCallByMPAccessionAndFilter(phenotype_id, filter);
			phenotypeList=phenoResult.getPhenotypeCallSummaries();
			
			Map<String, Map<String, Integer>> phenoFacets = phenoResult.getFacetResults();
			// sort facet values so that they will look nicer in the drop-down lists.
			phenoFacets = sortPhenFacets(phenoFacets);
			
			model.addAttribute("phenoFacets", phenoFacets);
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

			PhenotypeRow pr = new PhenotypeRow( pcs, config.get("baseUrl"));
			
			// Collapse rows on sex
			if(phenotypes.containsKey(pr)) {
				pr = phenotypes.get(pr);
				TreeSet<String> sexes = new TreeSet<String>();
				for (String s : pr.getSexes()) { sexes.add(s); }
				sexes.add(pcs.getSex().toString());
				pr.setSexes(new ArrayList<String>(sexes));
			}
			
			if(pr.getParameter() != null && pr.getProcedure()!= null) {		
				//if(pr.getGene().getSymbol().equals("Dll1")){
				phenotypes.put(pr, pr);
				//}
			}
		}
		
		List <PhenotypeRow> list=new ArrayList<PhenotypeRow>(phenotypes.keySet());
		Collections.sort(list);
		//Map names=new 
		for(PhenotypeRow pr: list){
			if(pr.getGene().getSymbol().equals("Dll1"))System.out.println("phenotype row="+pr);
		}
		model.addAttribute("phenotypes", new ArrayList<PhenotypeRow>(phenotypes.keySet()));	
//		System.out.println("\n\n" + solrIndex
//				.getMpData(phenotype_id)
//				.getJSONObject("response") );
		JSONObject mpData = solrIndex
				.getMpData(phenotype_id)
				.getJSONObject("response")
				.getJSONArray("docs")
				.getJSONObject(0);
		if (mpData.containsKey("ontology_subset")){
			model.addAttribute("isImpcTerm", mpData.getJSONArray("ontology_subset").contains("IMPC_Terms"));
		}
		else {
			model.addAttribute("isImpcTerm", false);
		}
		
	}	
	
	@ExceptionHandler(OntologyTermNotFoundException.class)
	public ModelAndView handleOntologyTermNotFoundException(OntologyTermNotFoundException exception) {
        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage",exception.getMessage());
        mv.addObject("acc",exception.getAcc());
        mv.addObject("type","mammalian phenotype");
        mv.addObject("exampleURI", "/phenotypes/MP:0000585");
        return mv;
    } 
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleGenericException(Exception exception) {
		exception.printStackTrace();
        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage",exception.getMessage());
        mv.addObject("acc","");
        mv.addObject("type","mammalian phenotype");
        mv.addObject("exampleURI", "/phenotypes/MP:0000585");
        return mv;
    } 
	
	@RequestMapping("/geneVariantsWithPhenotypeTable/{acc}")
	public String geneVariantsWithPhenotypeTable(
			@PathVariable String acc,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException {
		//just pass on any query string after the ? to the solr requesting object for now
		String queryString=request.getQueryString();
		processPhenotypes(acc, queryString, model);

		return "geneVariantsWithPhenotypeTable";
	}
	
	/**
	 * Get control and experimental example images for the top of the MP page
	 * @param accession MP Accession
	 * @return map containing control and experimental images with those as keys
	 * @throws SolrServerException 
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	private Map<String, JSONObject> getExampleImages(String accession) throws SolrServerException, IOException, URISyntaxException{
		
		if(accession.equals("MP:0001304")) {
			//cataracts examlple images
			//map.put("control", "https://dev.mousephenotype.org/data/media/images/1253/M01211663_00032438_download_tn_small.jpg");
			//255874
			Map<String, JSONObject> map= solrIndex.getExampleImages(255874, 76516);
					//getImageDoc(255874, solrDocumentList);
			return map;
			//map.put("experimental", "https://www.mousephenotype.org/data/media/images/550/M00226962_00007295_download_tn_small.jpg");
			//76516	
			
		}else 
		if(accession.equals("MP:0001139")) {
			//abnormal vagina
			//map.put("control", "https://dev.mousephenotype.org/data/media/images/0/WebUpload842_tn_small.jpg");
			//275206
			//map.put("experimental", "https://www.mousephenotype.org/data/media/images/905/M00880970_00027885_download_full.jpg");
			//160584
			Map<String, JSONObject> map= solrIndex.getExampleImages(275206, 160584);
			//getImageDoc(255874, solrDocumentList);
	return map;
				
		}else if(accession.equals("MP:0010098") || accession.equals("MP:0005103")) {
			//abnormal retinal blood vessel pattern
			//MP: abnormal retinal pigmentation
			//map.put("control", "https://dev.mousephenotype.org/data/media/images/0/M01356510_00034871_download_tn_small.jpg");
			//267671
			//map.put("experimental", "https://www.mousephenotype.org/data/media/images/0/M01418062__download_tn_small.jpg");
			//273416
			Map<String, JSONObject> map= solrIndex.getExampleImages(267671, 273416);
			//getImageDoc(255874, solrDocumentList);
	return map;
		}else if(accession.equals("MP:0000585") ) {
			//MP: Kinked tail
			//map.put("control", "https://dev.mousephenotype.org/data/media/images/0/WebUpload498_tn_small.jpg");
			//262634
			//map.put("experimental", "https://dev.mousephenotype.org/data/media/images/899/M00512959_00019280_download_tn_small.jpg");
			//159255
			Map<String, JSONObject> map= solrIndex.getExampleImages(262634, 159255);
			//getImageDoc(255874, solrDocumentList);
	return map;
		}
//if no rule for this return empty map
	return Collections.emptyMap();
	}
	
	public PhenotypeGeneSummaryDTO getPercentages(String phenotype_id) throws SolrServerException{ // <sex, percentage>
		PhenotypeGeneSummaryDTO pgs = new PhenotypeGeneSummaryDTO();
		
		int total = 0;
		int nominator = 0;
		
		List<String> parameters = new ArrayList<>(getParameterStableIdsByPhenotypeAndChildren(phenotype_id));
		// males & females	
		nominator = gpService.getGenesBy(phenotype_id, null).size();
		Long time = System.currentTimeMillis();
 		
 		Set<String> genesTotal1 = os.getTestedGenes(null, parameters);
 		Set<String> genesTotal2 = ptgm.getTestedGenesByParameterSex(parameters, null, os);
 		total = genesTotal2 .size();
 		genesTotal2.removeAll(genesTotal1);
// 		System.out.println("\nLeft with " + genesTotal2.size() + "\n" + genesTotal2);
 		
//		total = ptgm.getTestedGenesByParameterSex(parameters, null, os).size();
 		Long totalTime = System.currentTimeMillis() - time;
 		System.out.println("\tComputed tested genes in " + totalTime);
 		pgs.setTotalPercentage(100*(float)nominator/(float)total);
		pgs.setTotalGenesAssociated(nominator);
		pgs.setTotalGenesTested(total);
		boolean display = (total > 0 && nominator > 0) ? true : false;
		pgs.setDisplay(display);		

		List<String> genesFemalePhenotype = new ArrayList<>();
		List<String> genesMalePhenotype = new ArrayList<>();
		List<String> genesBothPhenotype = new ArrayList<>();
		
		if (display){
			//females only
			for (Group g : gpService.getGenesBy(phenotype_id, "female")){
				genesFemalePhenotype.add((String)g.getGroupValue());
			}
			nominator = genesFemalePhenotype.size();
			time = System.currentTimeMillis();
//			total = os.getTestedGenes("female", parameters).size();
			total = ptgm.getTestedGenesByParameterSex(parameters, SexType.female, os).size();
	 		System.out.println("\tComputed tested genes for females in " + (System.currentTimeMillis() - time));
	 		totalTime +=  System.currentTimeMillis() - time;
			pgs.setFemalePercentage(100*(float)nominator/(float)total);
			pgs.setFemaleGenesAssociated(nominator);
			pgs.setFemaleGenesTested(total);

			//males only
			for(Group g : gpService.getGenesBy(phenotype_id, "male")){			
				genesMalePhenotype.add(g.getGroupValue()) ;
			}
			nominator = genesMalePhenotype.size();
			time = System.currentTimeMillis();
//			total = os.getTestedGenes("male", parameters).size();
			total = ptgm.getTestedGenesByParameterSex(parameters, SexType.male, os).size();
	 		System.out.println("\tComputed tested genes for males in " + (System.currentTimeMillis() - time));
	 		totalTime +=  System.currentTimeMillis() - time;
			pgs.setMalePercentage(100*(float)nominator/(float)total);
			pgs.setMaleGenesAssociated(nominator);
			pgs.setMaleGenesTested(total);
		}
		
		genesBothPhenotype = new ArrayList<String>(genesFemalePhenotype);
		genesBothPhenotype.retainAll(genesMalePhenotype);
		// We want genes with phenotype ONLY in females (not in males too
		genesFemalePhenotype.removeAll(genesBothPhenotype);
		// same for males
		genesMalePhenotype.removeAll(genesBothPhenotype);
		pgs.setBothNumber(genesBothPhenotype.size());
		pgs.setFemaleOnlyNumber(genesFemalePhenotype.size());
		pgs.setMaleOnlyNumber(genesMalePhenotype.size());
		pgs.fillPieChartCode();
		
		System.out.println("\tTotal time for tested genes queries: " + totalTime);
//		System.out.println(">Parameters \n " + parameters);
		
		return pgs;
	}
	
	public Map<String, String> getParameters(String mpId) throws SolrServerException {
		Map<String, String> res = new HashMap<String, String>();
		List<String> paramIds = new ArrayList(getParameterStableIdsByPhenotypeAndChildren(mpId));
		Collections.sort(paramIds);
		for (String param : paramIds){
			if (gpService.getGenesAssocByParamAndMp(param, mpId).size() > 0){
				Parameter p =  pipelineDao.getParameterByStableId(param);
				res.put(param,p.getName());
			}
		}
		return res;
	}
	
	/**
	 * 
	 * @param mpTermId
	 * @return List of all parameters associated to the mp term or any of it's children (based on the slim only)
	 */
	public HashSet<String> getParameterStableIdsByPhenotypeAndChildren(String mpTermId) {
		HashSet<String> res = new HashSet<>();
		ArrayList<String> mpIds;
		try {
			mpIds = mpService.getChildrenFor(mpTermId);
			res.addAll(pipelineDao.getParameterStableIdsByPhenotypeTerm(mpTermId));
			for(String mp : mpIds){
				res.addAll(pipelineDao.getParameterStableIdsByPhenotypeTerm(mp));
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return res;
	}
}
