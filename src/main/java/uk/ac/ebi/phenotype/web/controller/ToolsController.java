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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.ebi.generic.util.Tools;

import javax.servlet.http.HttpServletRequest;


@Controller
public class ToolsController {
	
	
	/**
	 * tools page
	 * 
	 */

	@RequestMapping(value="/tools", method=RequestMethod.GET)
	public String loadToolsPage(
			@RequestParam(value = "core", required = false) String core,
			HttpServletRequest request, 
			Model model) {			
		
		String toolsHtml = composeToolBoxes(request);
		model.addAttribute("tools", toolsHtml);
			
		return "tools";
	}	
	
	
	private String composeToolBoxes(HttpServletRequest request){

		String hostName = request.getAttribute("mappedHostname").toString().replace("https:", "http:");
		String baseUrl = request.getAttribute("baseUrl") .toString();
		
		List<String> toolBoxes = new ArrayList<>();
		
		Map<String, String> toolSet = new HashMap<>(); 
		toolSet.put("alleleref", "IMPC/IKMC publications browser");
		toolSet.put("reports/gene2go", "GO annotations to phenotyped IMPC genes");
		
		Map<String, String> toolLabel = new HashMap<>(); 
		toolLabel.put("alleleref", "IMPC/IKMC publications browser");
		toolLabel.put("reports/gene2go", "GO annotations to phenotyped IMPC genes");
		
		Map<String, String> toolImg = new HashMap<>(); 
		toolImg.put("alleleref", baseUrl + "/img/pubmed_logo.jpg");
		toolImg.put("reports/gene2go", baseUrl + "/img/go_logo.png");
		
		Map<String, String> toolDesc = new HashMap<>();
		toolDesc.put("alleleref", "This is a table of IMPC/IKMC related publications. "
				+ "The interface contains a filter where users can type in keywords to search for related papers. "
				+ "The keyword is searched against allele symbol, paper title, Journal title, date of publication and grant agency."
				);
		toolDesc.put("reports/gene2go", "This is a tool to explore the mappings of phenotype completed and started genes in IMPC to GO terms "
				+"(automated electronic, curated computational and experimental). "
				+"It also helps to quickly find the IMPC phenotyped genes that GO has not yet annotated. "
				+"Users can also export the dataset as TSV or Excel format.");
		
		
		Iterator it = toolSet.entrySet().iterator();
	    while (it.hasNext()) {
	    	
	        Map.Entry pair = (Map.Entry)it.next();
	        String toolLink = (String) pair.getKey();
	        String toolName = (String) pair.getValue();
	        String toolAbout = "Info about this tool";

	        String infoImgLink = toolImg.get(toolLink);
	        
	        String trs = "";
	        trs += "<tr><td colspan=2 class='toolName'><a href='"+ hostName + baseUrl + "/" + toolLink+"'>"+toolName+"</a></td></tr>";
	        trs += "<tr><td><img class='toolImg' src='"+infoImgLink+"'></img></td>";
	       // trs += "<td><a href='"+ hostName + baseUrl + "/" + toolLink+"'>References using IKMC and IMPC resources</a></td></tr>";
	        trs += "<td class='toolDesc'>" + toolDesc.get(toolLink) + "</td></tr>";
	        
	        toolBoxes.add("<table class='tools'>" + trs + "</table>");
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
 		
		return StringUtils.join(toolBoxes, "");
	}
	
}
