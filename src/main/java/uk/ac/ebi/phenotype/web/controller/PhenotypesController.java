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
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DecimalFormat;
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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
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
import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.dao.OntologyTermDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummaryDAOReadOnly;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.Synonym;
import uk.ac.ebi.phenotype.stats.ChartData;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
import uk.ac.ebi.phenotype.stats.ExperimentService;
import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService;
import uk.ac.ebi.phenotype.stats.ObservationDTO;
import uk.ac.ebi.phenotype.stats.ObservationService;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalResultAndCharts;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalSet;
import uk.ac.ebi.phenotype.stats.timeseries.TimeSeriesChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.unidimensional.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.util.PhenotypeGeneSummaryDTO;
import uk.ac.ebi.phenotype.web.pojo.PhenotypeRow;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

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
		QueryResponse response=imagesSolrDao.getDocsForMpTerm(phenotype_id, 0, 6);
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
		
		model.addAttribute("overviewPhenCharts", getDataOverviewCharts(phenotype_id, model));
		
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

			PhenotypeRow pr = new PhenotypeRow();
			pr.setGene(pcs.getGene());
			pr.setAllele(pcs.getAllele());
			pr.setSexes(sex);
			pr.setPhenotypeTerm(pcs.getPhenotypeTerm());

			// zygosity representation depends on source of information
			// we need to know what the data source is so we can generate appropriate link on the page
			Datasource ds = pcs.getDatasource();

			// Defend in case the datasource is not loaded
			String dataSourceName = (ds != null) ? dataSourceName = ds.getName() : "";
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
			if(phenotypes.containsKey(pr)) {
				pr = phenotypes.get(pr);
				TreeSet<String> sexes = new TreeSet<String>();
				for (String s : pr.getSexes()) { sexes.add(s); }
				sexes.add(pcs.getSex().toString());
				pr.setSexes(new ArrayList<String>(sexes));
			}
			if (pcs.getPhenotypingCenter() != null)
				pr.setPhenotypingCenter(pcs.getPhenotypingCenter());
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
		
		List<String> parameters = pipelineDao.getParameterStableIdsByPhenotypeTerm(phenotype_id);
		// males & females	
		nominator = gpService.getGenesBy(phenotype_id, null).size();
 		total = os.getTestedGenes(phenotype_id, null, parameters);
 		pgs.setTotalPercentage(100*(float)nominator/(float)total);
		pgs.setTotalGenesAssociated(nominator);
		pgs.setTotalGenesTested(total);

		boolean display = (total > 0 && nominator > 0) ? true : false;
		pgs.setDisplay(display);		

		if (display){
			//females only
			nominator = gpService.getGenesBy(phenotype_id, "female").size();
			total = os.getTestedGenes(phenotype_id, "female", parameters);
			pgs.setFemalePercentage(100*(float)nominator/(float)total);
			pgs.setFemaleGenesAssociated(nominator);
			pgs.setFemaleGenesTested(total);

			//males only
			nominator = gpService.getGenesBy(phenotype_id, "male").size();
			total = os.getTestedGenes(phenotype_id, "male", parameters);
			pgs.setMalePercentage(100*(float)nominator/(float)total);
			pgs.setMaleGenesAssociated(nominator);
			pgs.setMaleGenesTested(total);
		}
		
		return pgs;
	}

	public List<ChartData> getDataOverviewCharts(String mpId, Model model) throws SolrServerException, IOException, URISyntaxException, SQLException{
		
		List<ChartData> chartsList = new ArrayList<>();//one object for each parameter
		List<String> parameters = pipelineDao.getParameterStableIdsByPhenotypeTerm(mpId);
		CategoricalChartAndTableProvider cctp = new CategoricalChartAndTableProvider();
		TimeSeriesChartAndTableProvider tstp = new TimeSeriesChartAndTableProvider();
		UnidimensionalChartAndTableProvider uctp = new UnidimensionalChartAndTableProvider();
		ArrayList<String> strains = new ArrayList<>();
		strains.add("MGI:2159965");
		strains.add("MGI:2164831");
		for (String parameter : parameters) {
			// get all genes associated with mpId because of parameter
			Parameter p = pipelineDao.getParameterByStableIdAndVersion(parameter, 1, 0);
			if(p != null && Utilities.checkType(p).equals(ObservationType.categorical)){
				List<String> genes = gpService.getGenesAssocByParamAndMp(parameter, mpId);
				if (genes.size() > 0){
					CategoricalSet controlSet = os.getCategories(parameter, null , "control", strains);
					controlSet.setName("Control");
					CategoricalSet mutantSet = os.getCategories(parameter, (ArrayList<String>) genes, "experimental", strains);
					mutantSet.setName("Mutant");
					chartsList.addAll(cctp.doCategoricalDataOverview(controlSet, mutantSet, model, parameter, p.getName()+" ("+parameter+")"));
				}
			}
			else if ( p != null && Utilities.checkType(p).equals(ObservationType.time_series)){
				List<String> genes = gpService.getGenesAssocByParamAndMp(parameter, mpId);
				if (genes.size() > 0){
					Map<String, List<DiscreteTimePoint>> data = os.getTimeSeriesMutantData(parameter, genes, strains);
					data.put("Control", os.getTimeSeriesControlData(parameter, strains));
					chartsList.add(tstp.doTimeSeriesOverviewData(data, p));
				}
			}
			else if ( p != null && Utilities.checkType(p).equals(ObservationType.unidimensional)){
//				System.out.println("getting unidimensional data for :  " + p);
				List<String> genes = gpService.getGenesAssocByParamAndMp(parameter, mpId);
//				System.out.println(" genes for " + p + " " + genes);
				if (genes.size() > 0){
					Map<String, List<Double>> map = os.getUnidimensionalData(p, genes, strains, "experimental");
					String chartTitle = "Mean " +  p.getName() + " (" + p.getStableId()+")";
					//chartsList.add(uctp.getHistogram(labels, data, chartTitle));
					chartsList.add(uctp.getStackedHistogram(map, chartTitle, p.getUnit()));
				}
			}
		}
		return chartsList;
	}
}
