package uk.ac.ebi.phenotype.web.controller;
 

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
	
	@Resource(name = "globalConfiguration")
	private Map<String, String> config;
	
	@Autowired
	private SolrIndex solrIndex;
	
	@RequestMapping(value="/gene2fam", method=RequestMethod.GET)
	public String loadAutosuggestSearchFacetPage(
			@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "core", required = false) String core,
			@RequestParam(value = "fq", required = false) String fq,
			HttpServletRequest request, 
			Model model) throws IOException, URISyntaxException {		

//		model.addAttribute("q", q);
//		model.addAttribute("core", core);
//		model.addAttribute("fq", fq);
		
		//String dataTableJson = solrIndex.getMgiGenesClansDataTable(request);
		String dataTableJson = solrIndex.getMgiGenesClansPlainTable(request);
		model.addAttribute("datatable", dataTableJson);
		
		return "gene2pfam";
	}
	
	@RequestMapping(value = "/reports/gene2go", method = RequestMethod.GET)
	public String goStats(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException  {
		
		Map<String, Map<String, Map<String, JSONArray>>> stats = solrIndex.getGO2ImpcGeneAnnotationStats();
		List<String> data = createTable(stats);
		model.addAttribute("legend", data.get(0));
		model.addAttribute("goStatsTable", data.get(1));
		
		return "Go";
	}
	
	private HttpHeaders createResponseHeaders(){
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_HTML);
		return responseHeaders;
	}
	
	public List<String> createTable(Map<String, Map<String, Map<String, JSONArray>>> stats){

		StringBuilder builder = new StringBuilder();
		String legend = "F = molecular function<br>P = biological process<br><span id='legendBox'><div class='FP'>F and P</div><div class='F'>F</div><div class='P'>P</div></span>";
		
		Map<Integer, String> evidRankCat = SolrIndex.getGoEvidRankCategory();

		Map<String, String> domainMap = new HashMap<>();
		domainMap.put("F", "molecular_function");
		domainMap.put("P", "biological_process");
		domainMap.put("FP", "*");

		//String internalBaseSolrUrl = config.get("internalSolrUrl") + "/gene/select?;
		
		List<String> data = new ArrayList<>();
		data.add(legend);
		
		for ( String key : stats.keySet() ) {
		    
			builder.append("<table class='goStats'>");
			builder.append("<tbody>");
			
		    String phenoCount = " : " + stats.get(key).get("allPheno").get(key).get(0);
		    
		    builder.append("<tr>");
        	builder.append("<td class='phenoStatus' colspan=4>" + key + " genes" + phenoCount + "</td>");
        	builder.append("</tr>");
		    
        	for ( String goMode : stats.get(key).keySet() ){

        		//System.out.println("GO MODE: " + goMode);
        		Map<String, List<String>> evidValDomain = new LinkedHashMap<>();
        		
            	Map<String, JSONArray> domainEvid = stats.get(key).get(goMode);
            	
            	Iterator itd = domainEvid.entrySet().iterator();
        		
    			while (itd.hasNext()) {
    				
    				Map.Entry pairs2 = (Map.Entry)itd.next();
    				String domain = pairs2.getKey().toString();
    	        
    		        //System.out.println(pairs2.getKey() + " = " + pairs2.getValue());
    		        JSONArray evids = (JSONArray) pairs2.getValue();
    		        itd.remove(); // avoids a ConcurrentModificationException
    			
    		        String domainParam = "go_term_domain:" + domainMap.get(domain);
    		        if ( domain.equals("FP") ){
    		        	List<String> fqStrs = new ArrayList<>();
    		        	fqStrs.add("go_term_domain:\"" + domainMap.get("F") + "\"");
    		        	fqStrs.add("go_term_domain:\"" + domainMap.get("P") + "\"");
    		        	domainParam = StringUtils.join(fqStrs, " AND ");
    		        }
    		        
    				for ( int i = 0; i<evids.size(); i=i+2 ){
    					int hasGoRowSpan= evids.size() / 2;
    				    int noGoRowSpan = hasGoRowSpan + 1;
    				    
    				    String currCell = "";
				        if ( goMode.equals("w/o GO") ){
				        	
				        	String qParams = "&q=latest_phenotype_status:\"" 
				        			+ key 
				        			+ "\" AND -go_term_id:*"
				        			+ "&fq=mp_id:*";
				        	
				        	builder.append("<tr>");
				        	builder.append("<td rel='"+ key +"'>" + goMode + "</td>");
				        	builder.append("<td colspan=3><div id='nogo' class='dlink nogo' rel='" + qParams +"'>" + evids.get(0) + "</div></td>");
				        	builder.append("</tr>");
				        }
				        else if ( goMode.equals("w/  GO") ){
				        	String rank = evids.get(i).toString();
				        	
				        	if ( evidRankCat.containsKey(Integer.parseInt(rank)) ){
					        	List<String> cellVals = new ArrayList<>();
					        	
					        	if ( evidValDomain.get(rank) != null ){
					        		cellVals = evidValDomain.get(rank);
					        	}
					        	
					        	String qParams = "&q=latest_phenotype_status:\"" 
					        			+ key 
					        			+ "\" AND evidCodeRank:" 
					        			+ rank 
					        			+ " AND " 
					        			+ domainParam;
			    				
					        	String found = evids.get(i+1).toString();
					        	
					        	cellVals.add("<div id ='" + rank + "' class='dlink " + domain + "' rel='" + qParams  + "'>" + found + "</div>");
					        	evidValDomain.put(rank, cellVals);
				        	}
				        }
    				}
    			}
    			Iterator cell = evidValDomain.entrySet().iterator();
        		
    			while (cell.hasNext()) {
    				
    				Map.Entry pairs3 = (Map.Entry)cell.next();
    				String rank = pairs3.getKey().toString();
    				
    				List<String> cellValLst = (List<String>) pairs3.getValue();
    				String cellVals = StringUtils.join(cellValLst, "");
    				
    				builder.append("<tr>");
		        	builder.append("<td rel='" + key +"' class='evidCode'>" + evidRankCat.get(Integer.parseInt(rank)) + "</td>");
		        	builder.append("<td>" + cellVals + "</td>"); // counts
		        	builder.append("</tr>");
    		     
    			}
        	}
        	builder.append("</tbody></table>");
		}
		
		String htmlTable = builder.toString();
		data.add(htmlTable);
		
		return data;
	}

}
