package uk.ac.ebi.phenotype.web.controller;
 

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.generic.util.SolrIndex;


@Controller
public class GoTermController {
	
	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	@Autowired
	private SolrIndex solrIndex;
	
	@RequestMapping(value = "/gostats", method = RequestMethod.GET)
	public String statsTable(
			//@RequestParam(value = "q", required = false) String solrParams,
			//@RequestParam(value = "subfacet", required = false) String subfacet,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException  {

		Map<String, Map<String, JSONArray>> stats = solrIndex.getGO2ImpcGeneAnnotationStats();
		return createTable(stats);
	}
	
	public String createTable(Map<String,Map<String, JSONArray>> stats){
	
		StringBuilder builder = new StringBuilder();
		builder.append("<table border=\"1\" border-collapse=\"collapse\" cellspacing=\"1\" cellpadding=\"5\" style=\"empty-cells:show\">");
		builder.append("<tbody>");
		
		Iterator it = stats.entrySet().iterator();
		int counter = 0;
		while (it.hasNext()) {
			counter++;
	        Map.Entry pairs = (Map.Entry)it.next();
	        String phenoStatus = pairs.getKey().toString();
	        
	        //log.info(pairs.getKey() + " = " + pairs.getValue());
	        Map<String, Map<String, JSONArray>> annotCounts = (Map<String, Map<String, JSONArray>>) pairs.getValue();
	        it.remove(); // avoids a ConcurrentModificationException
	       
	        Iterator it2 = annotCounts.entrySet().iterator();
	     
	        while (it2.hasNext()) {
	        	
		        Map.Entry pairs2 = (Map.Entry)it2.next();
		        String annot = pairs2.getKey().toString();
		        JSONArray countList = (JSONArray) pairs2.getValue();
		        it2.remove(); // avoids a ConcurrentModificationException
		        
		        //log.info(pairs2.getKey() + " = " + pairs2.getValue());
		        
		        if ( annot.equals("w/o GO") ){
		        	builder.append("<tr>");
		        	builder.append("<td class='phenoStatus' rowspan=6>" + phenoStatus + "</td>");
		        	builder.append("<td>" + annot + "</td>");
		        	builder.append("<td colspan=2>" + countList.get(0) + "</td>");
		        	builder.append("</tr>");
		        }
		        else {
			       
			        for ( int i=0; i<countList.size(); i=i+2 ){
			        	builder.append("<tr>");
			        	if ( i == 0 ){
			        		builder.append("<td rowspan=5>" + annot + "</td>");
			        	}
			        	builder.append("<td>" + countList.get(i) + "</td>");
			        	builder.append("<td>" + countList.get(i+1) + "</td>");
			        	builder.append("</tr>");
			        }
		        }
	        }
		}
		
		builder.append("</tbody>");
		
		String htmlTable = builder.toString();
		log.info(htmlTable);
	
		/* table looks similar to this:
		Phenotyping Complete	w/o GO	222
								w/ GO	
										EXP	0
										IDA	212
										IGI	41
										IMP	109
										IPI	122
		Phenotyping Started	w/o GO	378
								w/ GO	
										EXP	1
										IDA	430
										IGI	95
										IMP	254
										IPI	222
		*/
		
		return htmlTable;
	}

}
