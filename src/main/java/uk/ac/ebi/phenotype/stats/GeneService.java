package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public GeneService() {

	}

	public GeneService(String solrUrl) {
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

	private String derivePhenotypingStatus(SolrDocument doc) {

		String field = "latest_phenotype_status";
		try {
			// Phenotyping complete
			if (doc.containsKey(field)
					&& !doc.getFirstValue(field).toString().equals("")) {
				String val = doc.getFirstValue(field).toString();
				if (val.equals("Phenotyping Started")
						|| val.equals("Phenotyping Complete")) {
					return "available";
				}
			}

			// for legacy data: indexed through experiment core (so not want
			// Sanger Gene or Allele cores)
			if (doc.containsKey("hasQc")) {
				return "QCed data available";
			}
		} catch (Exception e) {
			log.error("Error getting phenotyping status");
			log.error(e.getLocalizedMessage());
		}

		return "";
	}

	// returns ready formatted icons
	public Map<String, String> getProductionStatus(String geneId) throws SolrServerException{		
    	
        SolrQuery query = new SolrQuery();
        query.setQuery("mgi_accession_id:\"" + geneId + "\"");
        QueryResponse response = solr.query(query);
    	SolrDocument doc = response.getResults().get(0);
		String miceStatus = "";					
		String esCellStatus = "";	
		String phenStatus = "";
		Boolean order = false;
		
		try {		
			
			/* ******** phenotype status ******** */
			phenStatus = derivePhenotypingStatus(doc).equals("") ? "" : "<a class='status done' title='Scroll down for phenotype associations.'><span>phenotype data available</span></a>";
			
			/* ******** mice production status ******** */
			
			String patternStr = "(tm.*)\\(.+\\).+"; // allele name pattern
			Pattern pattern = Pattern.compile(patternStr);
			
			// Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele docs) 
			if ( doc.containsKey("mouse_status") ){
							
				ArrayList<String> alleleNames = (ArrayList<String>) doc.getFieldValue("allele_name");
				ArrayList<String> mouseStatus = (ArrayList<String>) doc.getFieldValue("mouse_status");
							
				for ( int i=0; i< mouseStatus.size(); i++ ) {		
					String mouseStatusStr = mouseStatus.get(i).toString();	
								
					if ( mouseStatusStr.equals("Mice Produced") ){
						String alleleName = alleleNames.get(i).toString();						
						Matcher matcher = pattern.matcher(alleleName);
						if (matcher.find()) {
							String alleleType = matcher.group(1);						
							miceStatus += "<span class='status done' title='" + mouseStatusStr + "' >"
									+  "	<span>Mice<br>" + alleleType + "</span>"
									+  "</span>";
						}
						order = true;
					}
					else if (mouseStatusStr.equals("Assigned for Mouse Production and Phenotyping") ){
						String alleleName = alleleNames.get(i).toString();						
						Matcher matcher = pattern.matcher(alleleName);
						if (matcher.find()) {
							String alleleType = matcher.group(1);						
							miceStatus += "<span class='status inprogress' title='Mice production in progress' >"
									+  "	<span>Mice<br>" + alleleType + "</span>"
									+  "</span>";
						}						
					}					
				}	
				// if no mice status found but there is already allele produced, mark it as "mice produced planned"
				for ( int j=0; j< alleleNames.size(); j++ ) {
					String alleleName = alleleNames.get(j).toString();
					if ( !alleleName.equals("") && !alleleName.equals("None") && mouseStatus.get(j).toString().equals("") ){	
						Matcher matcher = pattern.matcher(alleleName);
						if (matcher.find()) {
							String alleleType = matcher.group(1);						
							miceStatus += "<span class='status none' title='Mice production planned' >"
									+  "	<span>Mice<br>" + alleleType + "</span>"
									+  "</span>";
						}	
					}						
				}
			
			}
			
			/* ******** ES cell production status  ******** */
						
			String field = "latest_es_cell_status"; 
			if ( doc.containsKey(field)  ){
				// blue es cell status				
				String text = doc.getFirstValue(field).toString();
				if ( text.equals("ES Cell Targeting Confirmed") ){
						esCellStatus = "<a class='status done' href='' title='ES Cells produced' >"
						   		 + " <span>ES cells</span>"
						   		 + "</a>";
						order = true;
				}
				else if ( text.equals("ES Cell Production in Progress") ){
						esCellStatus = "<span class='status inprogress' title='ES cells production in progress' >"
							   	 +  "	<span>ES Cell</span>"
							   	 +  "</span>";
				}
				
			}
			
		} catch (Exception e) {
			log.error("Error getting ES cell/Mice status");
			log.error(e.getLocalizedMessage());
		}
		HashMap<String, String> res =  new HashMap<>();
		res.put("icons", esCellStatus + miceStatus + phenStatus);
		res.put("orderPossible", order.toString());
		return res;
		
	}

	private String parseAlleleType(SolrDocument doc, String prodStatus,
			String type) {

		System.out.println("parseAlleleType with : \n \t\t" + prodStatus
				+ "   " + type);

		String miceStr = "";
		String hoverTxt = null;
		if (prodStatus.equals("done")) {
			hoverTxt = "Mice produced";
		} else if (prodStatus.equals("inprogress")) {
			hoverTxt = "Mice production in progress";
		} else if (prodStatus.equals("none")) {
			hoverTxt = "Mice production planned";
		}

		// tm1/tm1a/tm1e mice
		if (type.equals("A")) {

			Map<String, Integer> seenMap = new HashMap<String, Integer>();
			seenMap.put("tm1", 0);
			seenMap.put("tm1a", 0);
			seenMap.put("tm1e", 0);

			for (String alleleType : alleleTypes_mi) {

				String key = prodStatus.equals("inprogress") ? "es_allele_name"
						: "mi_allele_name";

				ArrayList<String> alleleNames = (ArrayList<String>) doc
						.getFieldValue(key);
				for (String an : alleleNames) {
					if (an.contains(alleleType + "(")) {
						seenMap.put(alleleType, seenMap.get(alleleType) + 1);
						// tm1seen++;
						if (seenMap.get(alleleType) == 1) {
							miceStr += "<span class='status " + prodStatus
									+ "' title='" + hoverTxt + "' >"
									+ "	<span>Mice<br>" + alleleType
									+ "</span>" + "</span>";
							break;
						}
					}
				}
			}
		}
		// tm1.1/tm1b/tm1e.1 mice
		else if (type.equals("B")) {

			Map<String, Integer> seenMap = new HashMap<String, Integer>();
			seenMap.put("tm1.1", 0);
			seenMap.put("tm1b", 0);
			seenMap.put("tm1e.1", 0);

			for (String alleleType : alleleTypes_pa) {
				ArrayList<String> alleleNames = (ArrayList<String>) doc
						.getFieldValue("pa_allele_name");
				for (String an : alleleNames) {
					if (an.contains(alleleType + "(")) {
						seenMap.put(alleleType, seenMap.get(alleleType) + 1);
						if (seenMap.get(alleleType) == 1) {
							miceStr += "<span class='status " + prodStatus
									+ "' title='" + hoverTxt + "' >"
									+ "	<span>Mice<br>" + alleleType
									+ "</span>" + "</span>";
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
