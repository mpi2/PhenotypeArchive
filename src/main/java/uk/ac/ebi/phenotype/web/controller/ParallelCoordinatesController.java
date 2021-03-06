/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package uk.ac.ebi.phenotype.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.service.ImpressService;
import uk.ac.ebi.phenotype.service.ObservationService;
import uk.ac.ebi.phenotype.service.StatisticalResultService;

@Controller
public class ParallelCoordinatesController {

	@Autowired 
	ObservationService os;
	
	@Autowired 
	StatisticalResultService srs;
	
	@Autowired
	PhenotypePipelineDAO pp;

	@RequestMapping(value="/parallel", method=RequestMethod.GET)
	public String getGraph(	@RequestParam(required = false, value = "procedure_id") List<String> procedureIds, Model model,	HttpServletRequest request,	RedirectAttributes attributes) 
	throws SolrServerException{
		
		if (procedureIds == null){
			model.addAttribute("procedure", "Clinical Blood Chemistry");
		}
		else {
			String data = srs.getGenotypeEffectFor(procedureIds , false);
			model.addAttribute("dataJs", data + ";");
			String procedures = "";
			for (String p : procedureIds){
				procedures += pp.getProcedureByMatchingStableId(p + "%").get(0).getName() + "<br/>";
			}
			
			model.addAttribute("procedure", procedures);
		}
//		String data = os.getMeansFor("IMPC_CBC_*", true);
//		System.out.println(data);
//		model.addAttribute("dataJs", data + ";");
		
		return "parallel";
	}
	
}
