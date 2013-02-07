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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.dao.CategoricalStatisticsDAO;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.Parameter;

@Controller
public class StatsController implements BeanFactoryAware {

	private final Logger log = LoggerFactory.getLogger(StatsController.class);
	private BeanFactory bf;

	@Autowired
	private GenomicFeatureDAO genesDao;

	@Autowired
	private CategoricalStatisticsDAO categoricalStatsDao;

	@Autowired
	private PhenotypeCallSummaryDAO phenoDAO;

	/**
	 * Runs when the request missing an accession ID. This redirects to the
	 * search page which defaults to showing all genes in the list
	 */
	@RequestMapping("/stats")
	public String rootForward() {
		return "redirect:/search";
	}

	@RequestMapping("/stats/{acc}/{parameter}")
	public String genes(@PathVariable String acc,
			@PathVariable String parameter, Model model,
			HttpServletRequest request, RedirectAttributes attributes) {

		// Get the global application configuration
		@SuppressWarnings("unchecked")
		Map<String, String> config = (Map<String, String>) bf
				.getBean("globalConfiguration");

		System.out.println("parameter id=" + parameter);
		System.out.println("acc=" + acc);
		GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
		model.addAttribute("gene", gene);
		model.addAttribute("request", request);
		model.addAttribute("acc", acc);

//		List<PhenotypeCallSummary> phenotypeList = phenoDAO
//				.getPhenotypeCallByAccession(acc, 3);
//		Map<PhenotypeRow, PhenotypeRow> phenotypes = new HashMap<PhenotypeRow, PhenotypeRow>();
//		// for testing purposes to get many params for testing will not override
//		// parameter id provided by url obviously later.
//		for (PhenotypeCallSummary pcs : phenotypeList) {
//			Parameter param = pcs.getParameter();
//			addCategoricalDataToModel(model, gene, param);
//
//		}

		// categorical stats stuff

		return "stats";
	}

	private void addCategoricalDataToModel(Model model, GenomicFeature gene,
			Parameter param) {
		
		
		
		
		List <String> catagories=categoricalStatsDao.getCategories(param);
		for(String category: catagories){
			System.out.println(category);
		}
		

//		for (SexType sexType : SexType.values()) {//do all options for male and female
//			for (ZygosityType zygosityType : ZygosityType.values()) {//do all options for zygocity
//				List<String> categories = categoricalStatsDao
//						.getControlCategories(sexType,
//								zygosityType, param);
//				model.addAttribute(sexType.name() + "Categories", categories);
//				for (String category : categories) {//do each category
//					Long count=categoricalStatsDao.countControl(sexType,
//							zygosityType, param, category);
//					model.addAttribute(sexType.name()+zygosityType.name()+category, count);
//				}
//			}
//		}
		System.out.println(model);
	}

	/**
	 * required for implementing BeanFactoryAware
	 * 
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		this.bf = arg0;
	}
}
