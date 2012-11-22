/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.util.SolrIndex;
import uk.ac.ebi.phenotype.web.pojo.PhenotypeRow;
import uk.ac.ebi.phenotype.web.util.DrupalHttpProxy;


@Controller
public class GenesController implements BeanFactoryAware {

	private BeanFactory bf;

	@Autowired
	private GenomicFeatureDAO genesDao;

	@Autowired
	private ImagesSolrDao imagesSolrDao;

	@Autowired
	private PhenotypeCallSummaryDAO phenoDAO;
	
	String drupalBaseUrl;
	
	@RequestMapping("/geneDetails")
	public String geneDetails(@RequestParam("gene_id") String acc, Model model, HttpServletRequest request) {

		getExperimentalImages(acc, model);

		// add expression info here as well
		getExpressionImages(acc, model);
		
		List<PhenotypeCallSummary> phenotypeList = phenoDAO.getPhenotypeCallByAccession(acc, 3);

		Map<PhenotypeRow,PhenotypeRow> phenotypes = new HashMap<PhenotypeRow,PhenotypeRow>(); 

		// This block collapses phenotype rows
		// phenotype term, allele, zygosity, and sex 
		// sex is collapsed into a single column
		for (final PhenotypeCallSummary pcs : phenotypeList) {

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
		// crappy code to shoehorn the QC panel
		DrupalHttpProxy proxy = new DrupalHttpProxy(request);
		// uncomment if you want to test locally
		//proxy.setDebugSession("SSESSfbfcc940c73e911682a51bb9f1c59a76=baZqFdKRZNUb4YfinhPVrgOh7yytLaH5rIquy562MVQ");
		URL url;
		try {
			drupalBaseUrl = (String) request.getAttribute("drupalBaseUrl");
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
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		// ES Cell and IKMC Allele check (Gautier)
		
		Map config = (Map) bf.getBean("globalConfiguration");
		String solrCoreName = "allele";
		String mode = "ikmcAlleleGrid";
		SolrIndex solrIndex = new SolrIndex("allele_name:"+gene.getSymbol(), solrCoreName, mode, config);
		model.addAttribute("countIKMCAlleles", solrIndex.fetchNumFound());
		System.out.println("CHECK IKMC allele found : " + solrIndex.fetchNumFound());
		
		return "geneDetails";
	}

	private void getExpressionImages(String acc, Model model) {
		QueryResponse solrExpressionR = imagesSolrDao.getExpressionFacetForGeneAccession(acc);
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
	
	@Override
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		this.bf=arg0;
		
	}
}
