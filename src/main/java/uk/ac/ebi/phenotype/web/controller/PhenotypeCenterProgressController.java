package uk.ac.ebi.phenotype.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@Autowired
	PhenotypeCenterService phenCenterProgress;
	
	@RequestMapping("/centerProgress")
	public String showPhenotypeCenterProgress( HttpServletRequest request, Model model){
		Map<String,Map<String, List<String>>> centerDataMap=null;
		try {
			centerDataMap = phenCenterProgress.getCentersProgressInformation();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String,JSONArray> centerDataJSON=new HashMap<>();
		for(String center:centerDataMap.keySet()){
			JSONArray centerContainer=new JSONArray();
			Map<String, List<String>> strainsToProcedures=centerDataMap.get(center);
			for(String strain: strainsToProcedures.keySet()){
			JSONArray pair=new JSONArray();
			pair.put(strain);
			pair.put(strainsToProcedures.get(strain).size());
			centerContainer.put(pair);
			}
			System.out.println("center="+center+" data="+centerContainer);
			centerDataJSON.put(center, centerContainer);
		}
		model.addAttribute("centerDataJSON", centerDataJSON);
		model.addAttribute("centerDataMap", centerDataMap);
		return "centerProgress";
	}

}
