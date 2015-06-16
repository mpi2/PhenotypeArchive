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
		Map<String, String> tools = new HashMap<>(); 
		tools.put("IMPC/IKMC publications", "alleleref");
		
		
		Iterator it = tools.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        String toolName = (String) pair.getKey();
	        String toolLink = (String) pair.getValue();
	        String toolAbout = "Info about this tool";
	        toolBoxes.add("<span class='toolName'>"+toolName+"</span>");  
	        String infoImgLink = "/sites/mousephenotype.org/files/images/IMPC%20magazine%20article%20image_Steve%20Brown_25_02_15.png";
	        toolBoxes.add("<img src='>"+infoImgLink+"'></img>");  
	        toolBoxes.add("<div class='toolLink'><a href='"+ hostName + baseUrl + "/" + toolLink+"'>References using IKMC and IMPC resources</a></div>");  
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
 		
		return StringUtils.join(toolBoxes, "");
	}
	
}
