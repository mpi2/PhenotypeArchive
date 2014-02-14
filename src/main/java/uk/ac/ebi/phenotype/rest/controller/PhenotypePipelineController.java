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
package uk.ac.ebi.phenotype.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.Pipeline;

@Controller("restfulPipelineController")
public class PhenotypePipelineController {

    private static final String XML_VIEW_NAME = "phenotypePipelines";

	@Autowired
	private PhenotypePipelineDAO dao;

	/**
	 * <p>Provide a model with a pipeline given an IMPRESS pipeline ID.</p>
	 * 
	 * @param stableId the stableId of the phenotypePipeline
	 * @param model the "implicit" model created by Spring MVC
	 */
	@RequestMapping(method=RequestMethod.GET, value="/phenotypePipeline/{stableId}")
	public ModelAndView getPhenotypePipeline(@PathVariable String stableId) {
		Pipeline gf = dao.getPhenotypePipelineByStableId(stableId);
		return new ModelAndView(XML_VIEW_NAME, "object", gf);
	}
}
