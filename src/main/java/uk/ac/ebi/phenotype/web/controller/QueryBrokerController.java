/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

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
import uk.ac.ebi.phenotype.service.GeneService;


@Controller
public class QueryBrokerController {

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	@Autowired
	private SolrIndex solrIndex;
	
	@Autowired
	private GeneService geneService;
	
	@Resource(name="globalConfiguration")
	private Map<String, String> config;
	
	private String internalSolrUrl;
	private JSONObject jsonResponse = new JSONObject();
	
	// Use cache to manage queries for minimizing network traffic
	final int MAX_ENTRIES = 600;
	
	@SuppressWarnings("unchecked")
	Map<String, Object> cache = (Map<String, Object>) Collections.synchronizedMap(new LinkedHashMap<String, Object>(MAX_ENTRIES+1, .75F, true) {
		private static final long serialVersionUID = 1L;

		// This method is called just after a new entry has been added
	    public boolean removeEldestEntry(Map.Entry eldest) {
	        return size() > MAX_ENTRIES;
	    }
	});

	
	/**
	 * <p>
	 * Return multiple solr json responses from server to avoid multiple calls from client
	 * Using cache to further reduce queries to the SOLR server
	 * </p>
	 * 
	 * @param 
	 *
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */

	@RequestMapping(value = "/querybroker", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> jsons(
			@RequestParam(value = "q", required = true) String solrParams,
			@RequestParam(value = "subfacet", required = false) String subfacet,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException  {
		
		internalSolrUrl = request.getAttribute("internalSolrUrl").toString();
		
		JSONObject jParams = (JSONObject) JSONSerializer.toJSON(solrParams);
		
		createJsonResponse(subfacet, jParams);
		
		return new ResponseEntity<JSONObject>(jsonResponse, createResponseHeaders(), HttpStatus.CREATED);
	}
	
	private HttpHeaders createResponseHeaders(){
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}
	
	public void createJsonResponse(String subfacet, JSONObject jParams) throws IOException, URISyntaxException {
	
		Iterator cores = jParams.keys();
		
		while(cores.hasNext()) {
			
			String core  = (String) cores.next();
			String param = jParams.getString(core);
			String url   = internalSolrUrl + "/" + core + "/select?" + param; 
			
			String key = core+param;
			Object o = cache.get(key);
			
			if (o == null && !cache.containsKey(key)) {
			    // Object not in cache. If null is not a possible value in the cache,
			    // the call to cache.contains(key) is not needed
				JSONObject json = solrIndex.getResults(url);
				
				if ( subfacet == null ){
					int numFound = json.getJSONObject("response").getInt("numFound");
					this.jsonResponse.put(core, numFound);
					
					cache.put(key, numFound);
					System.out.println("####### Cache for main facet added");
				}
				else {
					JSONObject j = new JSONObject();
					j.put("response", json.getJSONObject("response"));
					j.put("facet_counts", json.getJSONObject("facet_counts"));
					this.jsonResponse.put(core, j);
					
					cache.put(key, j);
					System.out.println("****** Cache for subfacet added");
				}
			}
			else {
				this.jsonResponse.put(core, o);
				System.out.println("------ Using cache");
			}
		}	
	}
		
}	