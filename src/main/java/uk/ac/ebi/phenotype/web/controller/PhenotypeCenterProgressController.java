package uk.ac.ebi.phenotype.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.ebi.phenotype.service.PhenotypeCenterService;

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
		Map<String,Map<String, List<String>>> centerDataMap=null;
		Map<String,Map<String, List<String>>> preQcCenterDataMap=null;
		try {
			centerDataMap = phenCenterProgress.getCentersProgressInformation();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			preQcCenterDataMap = preqQcPhenCenterProgress.getCentersProgressInformation();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
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
		return "centerProgress";
	}

	private void getPostOrPreQcData(
			Map<String, Map<String, List<String>>> centerDataMap,
			Map<String, JSONArray> centerDataJSON) {
		for(String center:centerDataMap.keySet()){
			List<Pair> pairsList=new ArrayList<>();
			Map<String, List<String>> strainsToProcedures=centerDataMap.get(center);
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
