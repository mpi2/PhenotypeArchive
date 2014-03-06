package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService.GenotypePhenotypeField;

@Service
public class GeneService {
	
	private HttpSolrServer solr;
	private ArrayList<String> alleleTypes_mi;
	private ArrayList<String> alleleTypes_pa;

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());
	
	public GeneService(String solrUrl){
		solr = new HttpSolrServer(solrUrl);
		
		alleleTypes_mi = new ArrayList<>();
		alleleTypes_pa = new ArrayList<>();
		
		alleleTypes_mi.add("tm1");
		alleleTypes_mi.add("tm1a");
		alleleTypes_mi.add("tm1e");		
		
		alleleTypes_pa.add("tm1.1");
		alleleTypes_pa.add("tm1b");
		alleleTypes_pa.add("tm1e.1");
	}
	
	// returns ready formatted icons 
    public String getProductionStatusIcons(String geneId) throws SolrServerException{		
    	
        SolrQuery query = new SolrQuery();
        query.setQuery("mgi_accession_id:\"" + geneId + "\"");
        QueryResponse response = solr.query(query);
    	SolrDocument doc = response.getResults().get(0);
		String miceStatus = "";	
		try {		
						
			// mice production status
			
			// Mice: blue tm1.1/tm1b/tm1e.1 mice (depending on how many allele docs) 
			if ( doc.containsKey("pa_allele_type") ){
				// blue es cell status
				miceStatus += parseAlleleType(doc, "done", "B");
			}
			// Mice: blue tm1/tm1a/tm1e mice (depending on how many allele docs) 
			else if ( doc.containsKey("mi_allele_type") ){
				// blue es cell status
				miceStatus += parseAlleleType(doc, "done", "A");
			}
			else if ( doc.containsKey("es_allele_name") && doc.containsKey("gene_type_status")  ){
				if ( doc.getFieldValue("gene_type_status").toString().equals("Microinjection in progress") ){					
					// draw orange tm1/tm1a/tm1e mice with given alleles
					miceStatus += parseAlleleType(doc, "inprogress", "A");					
				}	
				else if (doc.getFieldValue("gene_type_status").toString().equals("") ){
					miceStatus += parseAlleleType(doc, "none", "A");  // mouse production planned	
				}
			}
			else if ( doc.containsKey("es_allele_name") && !doc.containsKey("gene_type_status") ){
				// grey mice status: 
				miceStatus += parseAlleleType(doc, "none", "A");  // mouse production planned	
			}			
			
		} catch (Exception e) {
			log.error("Error getting ES cell/Mice status");
			log.error(e.getLocalizedMessage());
		}
		
		return fetchEsCellStatus(doc) + miceStatus;
		
	}
    
    private String fetchEsCellStatus(SolrDocument doc){
		
		String mgiId = doc.getFieldValue("mgi_accession_id").toString();
				
		String esCellStatus = "";	
		
		try {	
			
			// ES cell production status
			if ( doc.containsKey("es_allele_name") ){
				// blue es cell status
				esCellStatus = "<a class='status done' href='' oldtitle='ES Cells produced' title=''>"
					   		 + " <span>ES cells</span>"
					   		 + "</a>";
			}
			else if ( !doc.containsKey("es_allele_name") && doc.containsKey("gene_type") ){		
				esCellStatus = "<span class='status inprogress' oldtitle='ES cells production in progress' title=''>"
						   	 +  "	<span>ES Cell</span>"
						   	 +  "</span>";
			}
		}	
		catch (Exception e) {
			log.error("Error getting ES cell/Mice status");
			log.error(e.getLocalizedMessage());
		}
			
		return esCellStatus; 
	}
    
	private String parseAlleleType(SolrDocument doc, String prodStatus, String type){		
		
		System.out.println("parseAlleleType with : \n \t\t" + prodStatus + "   " + type);
		
		String miceStr = "";			
		String hoverTxt = null;		
		if ( prodStatus.equals("done") ){
			hoverTxt = "Mice produced";			
		}
		else  if (prodStatus.equals("inprogress") ) {
			hoverTxt = "Mice production in progress";
		}
		else if ( prodStatus.equals("none") ){
			hoverTxt = "Mice production planned";
		}
		
		
		//tm1/tm1a/tm1e mice	
		if ( type.equals("A") ){	
			
			Map<String,Integer> seenMap = new HashMap<String,Integer>();	      
			seenMap.put("tm1", 0);
			seenMap.put("tm1a", 0);
			seenMap.put("tm1e", 0);			
			
			for (String alleleType : alleleTypes_mi){	
				
				String key = prodStatus.equals("inprogress") ? "es_allele_name" : "mi_allele_name";				
				
				ArrayList<String> alleleNames = (ArrayList<String>) doc.getFieldValue(key);
				for (String an : alleleNames) {				
					if ( an.contains(alleleType+"(") ){						
						seenMap.put(alleleType, seenMap.get(alleleType)+1);
						//tm1seen++;
						if ( seenMap.get(alleleType) == 1 ){
							miceStr += "<span class='status " + prodStatus + "' oldtitle='" + hoverTxt + "' title=''>"
								+  "	<span>Mice<br>" + alleleType + "</span>"
								+  "</span>";					
							break;
						}					
					}
				}	
			}	
		}
		//tm1.1/tm1b/tm1e.1 mice	
		else if ( type.equals("B") ){	
			
			Map<String,Integer> seenMap = new HashMap<String,Integer>();	      
			seenMap.put("tm1.1", 0);
			seenMap.put("tm1b", 0);
			seenMap.put("tm1e.1", 0);
			
			for (String alleleType : alleleTypes_pa){	
				ArrayList<String> alleleNames = (ArrayList<String>) doc.getFieldValue("pa_allele_name");
				for (String an : alleleNames) {				
					if ( an.contains(alleleType+"(") ){					
						seenMap.put(alleleType, seenMap.get(alleleType)+1);
						if ( seenMap.get(alleleType) == 1 ){
							miceStr += "<span class='status " + prodStatus + "' oldtitle='" + hoverTxt + "' title=''>"
									+  "	<span>Mice<br>" + alleleType + "</span>"
									+  "</span>";	
							break;
						}	
					}	
				}
			}	
		}		

		System.out.println("\t\t miceStr : " + miceStr);
		return miceStr;
	}
	
}
