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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.ebi.phenotype.service.PhenotypeCenterService;
import uk.ac.ebi.phenotype.service.ProcedureBean;

import org.springframework.ui.Model;


@Controller
public class PhenotypeCenterProgressController {
	
	@Resource(name="phenotypeCenterService")
	@Autowired
	PhenotypeCenterService phenCenterProgress;
	
	@Resource(name="preQcPhenotypeCenterService")
	@Autowired
	PhenotypeCenterService preqQcPhenCenterProgress;
	
	@RequestMapping("/centerProgress")
	public String showPhenotypeCenterProgress( HttpServletRequest request, Model model){
		processPhenotypeCenterProgress(model);
		return "centerProgress";
	}
	
	
	/**
	 *
	 * @param response
	 * @param model
	 * @throws IOException
	 * @author tudose
	 */
	@RequestMapping("/reports/centerProgressCsv")
	@ResponseBody
	public void showPhenotypeCenterProgressCsv(HttpServletResponse response, Model model) throws IOException  {
			
	    String csvFileName = "PhenotypeCenterProgress.csv";
	 	try {
			List<String[]> centerProceduresPerStrain = phenCenterProgress.getCentersProgressByStrainCsv();
			ControllerUtils.writeAsCSV(centerProceduresPerStrain, csvFileName, response);
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	private void processPhenotypeCenterProgress(Model model) {
		Map<String, Map<String, List<ProcedureBean>>> centerDataMap=null;
		Map<String, Map<String, List<ProcedureBean>>> preQcCenterDataMap=null;
		try {
			centerDataMap = phenCenterProgress.getCentersProgressInformation();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		
		try {
			preQcCenterDataMap = preqQcPhenCenterProgress.getCentersProgressInformation();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		Map<String,JSONArray> centerDataJSON=new HashMap<>();
		Map<String,JSONArray> preQcCenterDataJSON=new HashMap<>();
		
		
		getPostOrPreQcData(centerDataMap, centerDataJSON);
		getPostOrPreQcData(preQcCenterDataMap, preQcCenterDataJSON);
		model.addAttribute("centerDataJSON", centerDataJSON);
		model.addAttribute("centerDataMap", centerDataMap);
		model.addAttribute("preQcCenterDataJSON", preQcCenterDataJSON);
		model.addAttribute("preQcCenterDataMap", preQcCenterDataMap);
	}

	private void getPostOrPreQcData(Map<String, Map<String, List<ProcedureBean>>> centerDataMap, Map<String, JSONArray> centerDataJSON) {
		
		for(String center:centerDataMap.keySet()){
			List<Pair> pairsList=new ArrayList<>();
			Map<String, List<ProcedureBean>> strainsToProcedures=centerDataMap.get(center);
			
			for(String strain: strainsToProcedures.keySet()){
				Pair pair=new Pair();
				pair.strain=strain;
				pair.number=strainsToProcedures.get(strain).size();
				pairsList.add(pair);
			}
			Collections.sort(pairsList);
			JSONArray centerContainer=new JSONArray();
			
			for(Pair pair: pairsList){
				JSONArray jsonPair=new JSONArray();
				jsonPair.put(pair.strain);
				jsonPair.put( pair.number);
				centerContainer.put(jsonPair);
			}
			System.out.println("center="+center+" data="+centerContainer);
			centerDataJSON.put(center, centerContainer);
		
		}
	}
	
	private class Pair implements Comparable{
		private String strain;
		private int number;
		@Override
		public int compareTo(Object other) {
			Pair otherPair=(Pair)other;
			if(this.number>otherPair.number){
				return -1;
			}else if (this.number==otherPair.number){
				return 0;
			}
			return 1;
		}
	}

}
