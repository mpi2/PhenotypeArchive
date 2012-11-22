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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;

@Controller
public class GenomicFeatureController {

	private GenomicFeatureDAO genomicFeatureDAO;
	
	/**
	 * Creates a new GenomicFeatureController with a given genomicFeature manager.
	 */
	@Autowired 
	public GenomicFeatureController(GenomicFeatureDAO genomicFeatureDAO) {
		this.genomicFeatureDAO = genomicFeatureDAO;
	}
	
	/**
	 * <p>Provide a model with an genomicFeature for the genomicFeature detail page.</p>
	 * 
	 * @param id the id of the genomicFeature
	 * @param model the "implicit" model created by Spring MVC
	 */
	@RequestMapping("/genomicFeatureDetails")
	public String genomicFeatureDetails(@RequestParam("acc") String acc, Model model) {		
		model.addAttribute("genomicFeature", genomicFeatureDAO.getGenomicFeatureByAccession(acc));
		return "genomicFeatureDetails";
	}
	
}
