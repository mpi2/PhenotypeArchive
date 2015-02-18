package uk.ac.ebi.phenotype.web.controller;
 

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.generic.util.SolrIndex;


@Controller
public class ExternalAnnotsController {
	
	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	@Autowired
	private SolrIndex solrIndex;
	
	@RequestMapping(value="/genefam", method=RequestMethod.GET)
	public String loadAutosuggestSearchFacetPage(
			@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "core", required = false) String core,
			@RequestParam(value = "fq", required = false) String fq,
			HttpServletRequest request, 
			Model model) throws IOException, URISyntaxException {		

		//Map<String, Map<String, Map<String, JSONArray>>> stats = solrIndex.getGO2ImpcGeneAnnotationStats();
		
		
		model.addAttribute("q", q);
		model.addAttribute("core", core);
		model.addAttribute("fq", fq);
		
		//String dataTableJson = solrIndex.getMgiGenesClansDataTable(request);
		String dataTableJson = solrIndex.getMgiGenesClansPlainTable(request);
		model.addAttribute("datatable", dataTableJson);
		System.out.println(dataTableJson);
		
		return "externalAnnots";
	}
	
	
	@RequestMapping(value = "/genego", method = RequestMethod.GET)
	//public ResponseEntity<String> statsTable(
	public String statsTable(
			//@RequestParam(value = "q", required = false) String solrParams,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException  {

		Map<String, Map<String, Map<String, JSONArray>>> stats = solrIndex.getGO2ImpcGeneAnnotationStats();
		//ResponseEntity<String> goTable = new ResponseEntity<String>(createTable(stats), createResponseHeaders(), HttpStatus.CREATED);
		List<String> data = createTable(stats);
		model.addAttribute("legend", data.get(0));
		model.addAttribute("goTable", data.get(1));
		
		return "Go";
	}
	
	private HttpHeaders createResponseHeaders(){
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_HTML);
		return responseHeaders;
	}
	
	public List<String> createTable(Map<String, Map<String, Map<String, JSONArray>>> stats){
	
		StringBuilder builder = new StringBuilder();
		String legend = "F = molecular function<br>P = biological process<br><div class='FP'>F or P</div><div class='F'>F</div><div class='P'>P</div>";
		
		Map<String, String> evidMap = new HashMap<>();
		evidMap.put("EXP", "Inferred from Experiment");
		evidMap.put("IDA", "Inferred from Direct Assay");
		evidMap.put("IGI", "Inferred from Genetic Interaction");
		evidMap.put("IMP", "Inferred from Mutant Phenotype");
		evidMap.put("IPI", "Inferred from Physical Interaction");
		evidMap.put("ISS", "Inferred from Sequence or structural Similarity");
		evidMap.put("ISO", "Inferred from Sequence Orthology");
		evidMap.put("ND", "No biological Data available");
		
		List<String> data = new ArrayList<>();
		data.add(legend);
		
		for ( String key : stats.keySet() ) {
		    
			builder.append("<table class='go'>");
			builder.append("<tbody>");
			
		    String phenoCount = " : " + stats.get(key).get("allPheno").get(key).get(0);
		    
		    builder.append("<tr>");
        	builder.append("<td class='phenoStatus' colspan=4>" + key + phenoCount + "</td>");
        	builder.append("</tr>");
		    
        	for ( String goMode : stats.get(key).keySet() ){

        		//System.out.println("GO MODE: " + goMode);
        		Map<String, List<String>> evidValDomain = new LinkedHashMap<>();
        		
            	Map<String, JSONArray> domainEvid = stats.get(key).get(goMode);
            	
            	Iterator itd = domainEvid.entrySet().iterator();
        		
    			while (itd.hasNext()) {
    				
    				Map.Entry pairs2 = (Map.Entry)itd.next();
    				String domain = pairs2.getKey().toString();
    	        
    		        //log.info(pairs2.getKey() + " = " + pairs2.getValue());
    		        JSONArray evids = (JSONArray) pairs2.getValue();
    		        itd.remove(); // avoids a ConcurrentModificationException
    			
    				for ( int i = 0; i<evids.size(); i=i+2 ){
    					int hasGoRowSpan= evids.size() / 2;
    				    int noGoRowSpan = hasGoRowSpan + 1;
    				    
    				    String currCell = "";
				        if ( goMode.equals("w/o GO") ){
				        	
				        	builder.append("<tr>");
				        	builder.append("<td>" + goMode + "</td>");
				        	builder.append("<td colspan=3>" + evids.get(0) + "</td>");
				        	builder.append("</tr>");
				        }
				        else if ( goMode.equals("w/  GO") ){
				        	String evidCode = evids.get(i).toString();
				        	if ( evidMap.containsKey(evidCode) ){
					        	List<String> cellVals = new ArrayList<>();
					        	
					        	if ( evidValDomain.get(evidCode) != null ){
					        		cellVals = evidValDomain.get(evidCode);
					        	}
					        	
					        	cellVals.add("<div class='" + domain + "'>" + evids.get(i+1).toString() + "</div>");
					        	evidValDomain.put(evidCode, cellVals);
				        	}
				        }
    				}
    			}
    			Iterator cell = evidValDomain.entrySet().iterator();
        		
    			while (cell.hasNext()) {
    				
    				Map.Entry pairs3 = (Map.Entry)cell.next();
    				String evidCode = pairs3.getKey().toString();
    				List<String> cellValLst = (List<String>) pairs3.getValue();
    				String cellVals = StringUtils.join(cellValLst, "");
    				builder.append("<tr>");
		        	builder.append("<td class='evidCode' title='" + evidMap.get(evidCode) + "'>" + evidCode + "</td>");
		        	builder.append("<td>" + cellVals + "</td>");
		        	builder.append("</tr>");
    		     
    			}
        	}
        	builder.append("</tbody></table>");
		}
		
		
		//builder.append("</tbody></table>");
		String htmlTable = builder.toString();
		data.add(htmlTable);
		//log.info(htmlTable);
	
		/* table looks similar to this:
		Phenotyping Complete
		w/o GO	171
		EXP	[0(F/P), 0(F), 0(P)]
		IDA	[189(F/P), 162(F), 162(P)]
		IGI	[45(F/P), 34(F), 45(P)]
		IMP	[119(F/P), 92(F), 119(P)]
		IPI	[131(F/P), 131(F), 104(P)]
		ISO	[25(F/P), 20(F), 22(P)]
		ISS	[119(F/P), 101(F), 106(P)]
		Phenotyping Started
		w/o GO	307
		EXP	[1(F/P), 1(F), 1(P)]
		IDA	[425(F/P), 347(F), 381(P)]
		IGI	[109(F/P), 89(F), 109(P)]
		IMP	[282(F/P), 207(F), 281(P)]
		IPI	[254(F/P), 254(F), 204(P)]
		ISO	[62(F/P), 54(F), 52(P)]
		ISS	[267(F/P), 219(F), 244(P)]
		*/
		
		return data;
	}

}
