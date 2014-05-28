package uk.ac.ebi.generic.util;
import net.sf.json.JSONObject;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MegaCoreIndexer {

	public static void main(String[] args) throws IOException, SolrServerException {
		
		HttpSolrServer destServer = new HttpSolrServer("http://localhost:8983/solr/autosuggest");
 		destServer.deleteByQuery( "*:*" );
		
		
		//String[] cores = {"gene","mp","disease","ma","pipeline","images"};
		final HashMap<String, String[]> coreFields = new HashMap<String, String[]>();
		
		String[] geneFields =    {"mgi_accession_id", "marker_symbol", "marker_name", "marker_synonym"};
		String[] mpFields =      {"mp_id", "mp_term", "mp_term_synonym"};
		String[] diseaseFields = {"disease_id", "disease_term", "disease_alts"};
		
		coreFields.put("gene",    geneFields);
		coreFields.put("mp",      mpFields);
		coreFields.put("disease", diseaseFields);
		
		for (Map.Entry<String, String[]> entry : coreFields.entrySet()) {
            String core = entry.getKey().toString();
            
            HttpSolrServer srcServer = new HttpSolrServer("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/" + core);
            
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
				
				//SolrInputDocument doc = new SolrInputDocument();
				
				for ( String fieldName : srcDoc.getFieldNames()){
					SolrInputDocument doc = new SolrInputDocument();
					System.out.println(fieldName);
					System.out.println(docMap.get(fieldName));			
					
					doc.addField("docType", core);
					doc.addField(fieldName, docMap.get(fieldName));
					destServer.add(doc);
					if(i%100==0) destServer.commit();  // periodically flush
				}
				//destServer.add(doc);
				//if(i%100==0) destServer.commit();  // periodically flush
		    }
	   
		    destServer.commit();
            
		}
		/*for ( String core : cores ){
		
			HttpSolrServer srcServer = new HttpSolrServer("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/" + core);
			
			HttpSolrServer destServer = new HttpSolrServer("http://localhost:8983/solr/mega");
			destServer.deleteByQuery( "*:*" );
			
			SolrQuery query = new SolrQuery();
			query.setQuery( "*:*" );
			// retreives all fields	
			query.setFields("*");
			//query.setFields("dataType", "mp_id","mp_term","mp_term_synonym","mp_definition","top_level_mp_id");
			//query.addFilterQuery("cat:electronics","store:amazon.com");
			query.setStart(0);
			query.setRows(99999999);
			//query.set("defType", "edismax");
	
			QueryResponse response = srcServer.query(query);
			SolrDocumentList results = response.getResults();
			
			for (int i = 0; i < results.size(); ++i) {
				System.out.println(results.get(i));
				SolrDocument srcDoc = results.get(i);
				
				Map<String, Object> docMap = srcDoc.getFieldValueMap();
				
				SolrInputDocument doc = new SolrInputDocument();
				
				for ( String fieldName : srcDoc.getFieldNames()){
					if ( !fieldName.equals("_version_") ){
						System.out.println(fieldName);
						System.out.println(docMap.get(fieldName));			
					
						doc.addField(fieldName, docMap.get(fieldName));
					}
				}
				destServer.add(doc);
				if(i%100==0) destServer.commit();  // periodically flush
		    }
	   
		    destServer.commit();
		}*/
	}
}
