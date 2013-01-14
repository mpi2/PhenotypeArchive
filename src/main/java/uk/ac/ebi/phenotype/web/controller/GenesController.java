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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.web.pojo.PhenotypeRow;
import uk.ac.ebi.phenotype.web.util.DrupalHttpProxy;


@Controller
public class GenesController implements BeanFactoryAware {

	private final Logger log = LoggerFactory.getLogger(GenesController.class);
	private BeanFactory bf;

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
			RedirectAttributes attributes) {

		// Get the global application configuration
		@SuppressWarnings("unchecked")
		Map<String,String> config = (Map<String,String>) bf.getBean("globalConfiguration");

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

		GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
		model.addAttribute("gene", gene);
		model.addAttribute("request", request);
		model.addAttribute("acc", acc);

		// crappy code to shoehorn the QC panel
		DrupalHttpProxy proxy = new DrupalHttpProxy(request);
		// uncomment if you want to test locally
		//proxy.setDebugSession("SSESSfbfcc940c73e911682a51bb9f1c59a76=y1tfAywDbKYLw_ROFZX6ysb0H9V3yKk7M9E2rfigei0");
		URL url;
		try {
			String drupalBaseUrl = (String) request.getAttribute("drupalBaseUrl");
			url = new URL(drupalBaseUrl + "/phenotypes/" + acc);
			String drupalContent = proxy.getContent(url);
			model.addAttribute("bPreQC", new Boolean(drupalContent.contains("qc?")));
			if (drupalContent.contains("qc?")) {
				int beginIndex = drupalContent.indexOf("/qc?");
				String sub1 = drupalContent.substring(beginIndex);
				int endIndex = sub1.indexOf("'");
				String sub2 = sub1.substring(0, endIndex);
				model.addAttribute("qcLink", sub2);
			}
			model.addAttribute("bSangerLegacy", new Boolean(drupalContent.contains("sanger")));
			if (drupalContent.contains("sanger")) {
				int beginIndex = drupalContent.indexOf("http://www.sanger");
				String sub1 = drupalContent.substring(beginIndex);
				int endIndex = sub1.indexOf("'");
				String sub2 = sub1.substring(0, endIndex);
				model.addAttribute("sangerLegacyLink", sub2);
			}
			model.addAttribute("bEurophenomeLegacy", new Boolean(drupalContent.contains("europhenome")));
			if (drupalContent.contains("europhenome")) {
				// http://www.europhenome.org/databrowser/viewer.jsp?set=true&m=true&l=10035
				int beginIndex = drupalContent.indexOf("http://www.europhenome.org");
				String sub1 = drupalContent.substring(beginIndex);
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
}
