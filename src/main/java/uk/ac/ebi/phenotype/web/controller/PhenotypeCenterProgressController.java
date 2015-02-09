package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
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
	
	@RequestMapping("/centerProgressCsv")
	@ResponseBody
	public void showPhenotypeCenterProgressCsv(HttpServletResponse response, Model model) throws IOException  {
		
		
	        String csvFileName = "PhenotypeCenterProgress.csv";
	 
	        response.setContentType("text/csv");
	 
	        // creates mock data
	        String headerKey = "Content-Disposition";
	        String headerValue = String.format("attachment; filename=\"%s\"",
	                csvFileName);
	        response.setHeader(headerKey, headerValue);
	 
	        //just work with post QC for file for Damian and Jules
	        Map<String,Map<String, List<ProcedureBean>>> centerDataMap=null;
			try {
				centerDataMap = phenCenterProgress.getCentersProgressInformation();
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String,JSONArray> centerDataJSON=new HashMap<>();
			getPostOrPreQcData(centerDataMap, centerDataJSON);
			
//	        Book book1 = new Book("Effective Java", "Java Best Practices",
//	                "Joshua Bloch", "Addision-Wesley", "0321356683", "05/08/2008",
//	                38);
//	 
//	        Book book2 = new Book("Head First Java", "Java for Beginners",
//	                "Kathy Sierra & Bert Bates", "O'Reilly Media", "0321356683",
//	                "02/09/2005", 30);
//	 
//	        Book book3 = new Book("Thinking in Java", "Java Core In-depth",
//	                "Bruce Eckel", "Prentice Hall", "0131872486", "02/26/2006", 45);
//	 
//	        Book book4 = new Book("Java Generics and Collections",
//	                "Comprehensive guide to generics and collections",
//	                "Naftalin & Philip Wadler", "O'Reilly Media", "0596527756",
//	                "10/24/2006", 27);
//	 
//	        List<Book> listBooks = Arrays.asList(book1, book2, book3, book4);
//	 
	        // uses the Super CSV API to generate CSV data from the model data
//	        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
//	                CsvPreference.STANDARD_PREFERENCE);
//	 
	        String[] header = { "Title", "Description", "Author", "Publisher",
	                "isbn", "PublishedDate", "Price" };
	        response.getWriter().append(centerDataJSON.toString());
//	        csvWriter.writeHeader(header);
	 
//	        for (Book aBook : listBooks) {
//	            csvWriter.write(aBook, header);
//	        }
	 
	        response.flushBuffer();
	        
	    }
		//return "centerProgressCsv";
	

	private void processPhenotypeCenterProgress(Model model) {
		Map<String, Map<String, List<ProcedureBean>>> centerDataMap=null;
		Map<String, Map<String, List<ProcedureBean>>> preQcCenterDataMap=null;
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
	}

	private void getPostOrPreQcData(
			Map<String, Map<String, List<ProcedureBean>>> centerDataMap,
			Map<String, JSONArray> centerDataJSON) {
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
