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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

	/**
	 * redirect calls to the base url to the search page
	 * 
	 * @return
	 */
	@RequestMapping("/index.html")
	public String rootForward() {
		return "redirect:/searchAndFacet";
	}

	/**
	 * <p>
	 * Simply takes us to the generic search/data faceting page.
	 * </p>
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/searchAndFacet")
	public String loadAutosuggestSearchFacetPage(
			@RequestParam(value = "queryString", required = false) String queryString,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "geneFound", required = false) String geneFound,
			Model model) {

		System.out.println(queryString);
		model.addAttribute("queryString", queryString);
		model.addAttribute("queryType", type);
		model.addAttribute("queryGeneFound", geneFound);

		return "searchAndFacet";
	}
}
