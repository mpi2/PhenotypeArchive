package uk.ac.ebi.generic.util;
import net.sf.json.JSONObject;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



public class AutosuggestIndexer {

	public static void main(String[] args) throws IOException, SolrServerException {
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("WebContent/WEB-INF/app-config.xml"); 
		Map<String, String> config = (Map<String, String>)applicationContext.getBean("globalConfiguration");
		
		String solrUrl = config.get("solrUrl");
		
		HttpSolrServer destServer = new HttpSolrServer(solrUrl + "/autosuggest");
 		destServer.deleteByQuery( "*:*" );
		
		final HashMap<String, String[]> coreFields = new HashMap<String, String[]>();
		
		String[] geneFields =    {"mgi_accession_id", "marker_symbol", "marker_name", "marker_synonym"};
		String[] mpFields =      {"mp_id", "mp_term", "mp_term_synonym"};
		String[] diseaseFields = {"disease_id", "disease_term", "disease_alts"};
		
		coreFields.put("gene",    geneFields);
		coreFields.put("mp",      mpFields);
		coreFields.put("disease", diseaseFields);
		
		for (Map.Entry<String, String[]> entry : coreFields.entrySet()) {
            String core = entry.getKey().toString();
            
            HttpSolrServer srcServer = new HttpSolrServer(solrUrl + "/" + core);
            
    		SolrQuery query = new SolrQuery();
			query.setQuery( "*:*" );
			query.setStart(0);
			query.setRows(99999999);
			
			// retreives wanted fields	
			query.setFields(entry.getValue());
			
			QueryResponse response = srcServer.query(query);
			SolrDocumentList results = response.getResults();
			
			for (int i = 0; i < results.size(); ++i) {
				System.out.println(results.get(i));
				
				SolrDocument srcDoc = results.get(i);
				
				Map<String, Object> docMap = srcDoc.getFieldValueMap();				
				
				for ( String fieldName : srcDoc.getFieldNames()){
					SolrInputDocument doc = new SolrInputDocument();
					System.out.println(fieldName);
					System.out.println(docMap.get(fieldName));			
					
					doc.addField("docType", core);
					doc.addField(fieldName, docMap.get(fieldName));
					destServer.add(doc);
					if(i%100==0) destServer.commit();  // periodically flush
				}				
		    }
	   
		    destServer.commit();
		}
	}
}
