package uk.ac.ebi.phenotype.ontology;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

@Service
public class PhenotypeSummaryDAOImpl implements PhenotypeSummaryDAO {

	public static SolrServer server = null;
	
	@Resource(name="globalConfiguration")
	private Map<String, String> config;
	
	public PhenotypeSummaryDAOImpl() throws MalformedURLException {
	}
	
	public PhenotypeSummaryDAOImpl(String solrBaseUrl) throws MalformedURLException {
		// Use system proxy if set
		if (System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyPort") != null) {
			String PROXY_HOST = System.getProperty("http.proxyHost");
			Integer PROXY_PORT = Integer.parseInt(System.getProperty("http.proxyPort"));
			HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT, "http");
			DefaultHttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			server = new HttpSolrServer(solrBaseUrl, client);
		} else {
			server = new HttpSolrServer(solrBaseUrl);
		}
	}
	
	public void instantiateSolrServer(){
		String solrBaseUrl = config.get("internalSolrUrl") + "/genotype-phenotype";
		if (System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyPort") != null) {
			String PROXY_HOST = System.getProperty("http.proxyHost");
			Integer PROXY_PORT = Integer.parseInt(System.getProperty("http.proxyPort"));
			HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT, "http");
			DefaultHttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			server = new HttpSolrServer(solrBaseUrl, client);
		} else {
			server = new HttpSolrServer(solrBaseUrl);
		}
	}

	@Override
	public HashMap<String, String> getTopLevelMPTerms(String gene) throws SolrServerException {
		HashMap<String,String> tl = new HashMap<String,String>(); 
		SolrDocumentList result = runQuery("marker_accession_id:" + gene.replace(":", "\\:"));
		if (result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				SolrDocument doc = result.get(i);
				if (doc.getFieldValue("top_level_mp_term_id") != null){
					tl.put((String) doc.getFieldValue("top_level_mp_term_id"), (String) doc.getFieldValue("top_level_mp_term_name"));
				}
				else { // it seems that when the term id is a top level term itself the top level term field 
					tl.put((String) doc.getFieldValue("mp_term_id"), (String) doc.getFieldValue("mp_term_name"));					
				}
			}
		}		
		return tl;
	}

	@Override
	public SolrDocumentList getPhenotypesForTopLevelTerm(String gene, String mpID) throws SolrServerException {	
		SolrDocumentList result = runQuery("marker_accession_id:" + gene.replace(":", "\\:") + " AND top_level_mp_term_id:" + mpID.replace(":", "\\:"));
		// mpID might be in mp_id instead of top level field
		if (result.size() == 0 || result == null)
			result = runQuery("marker_accession_id:" + gene.replace(":", "\\:") + " AND mp_term_id:" + mpID.replace(":", "\\:"));
		return result;
	}

	@Override
	public String getSexesRepresentationForPhenotypesSet(SolrDocumentList resp) {
		String resume = ""; 
		if (resp.size() > 0) {
			for (int i = 0; i < resp.size(); i++) {
				SolrDocument doc = resp.get(i);
				if ("male".equalsIgnoreCase((String) doc.getFieldValue("sex")))
					resume += "m";
				else if ("female".equalsIgnoreCase((String) doc.getFieldValue("sex")))
					resume += "f";
				if (resume.contains("m") && resume.contains("f")) // we can stop when we have both sexes already
					return "both sexes";
			}
			if (resume.contains("m") && !resume.contains("f")) 
				return "male";
			if (resume.contains("f") && !resume.contains("m")) 
				return "female";
		}	
		return null;
	}

	@Override
	public HashSet<String> getDataSourcesForPhenotypesSet(SolrDocumentList resp) {
		HashSet <String> data = new HashSet <String> ();
		if (resp.size() > 0) {
			for (int i = 0; i < resp.size(); i++) {
				SolrDocument doc = resp.get(i);
				data.add((String) doc.getFieldValue("resource_name"));
			}
		}	
		return data;
	}

	@Override
	public String getSummary(String gene) throws SolrServerException {
		HashMap<String, String> summary = getTopLevelMPTerms(gene);	
		String res = "Phenotype Summary based on automated MP annotations supported by experiments from ";
		res += getDataSourcesForPhenotypesSet(getPgehnotypes(gene));
		res +=" on knockout mouse models\n\n";
		String m = "";
		String f = "";
		String mf = ""; 
		
		for (String id: summary.keySet()){
			SolrDocumentList resp = getPhenotypesForTopLevelTerm(gene, id);
			String sex = getSexesRepresentationForPhenotypesSet(resp);
			if (sex.equals("male"))
				m += " " + summary.get(id) + " ("+id+"), in data from " + 
						getDataSourcesForPhenotypesSet(resp) + "\t" + "(" + resp.getNumFound() + ")\n";
			else if (sex.equals("female"))
				f += " " + summary.get(id) + " ("+id+"), in data from " + 
						getDataSourcesForPhenotypesSet(resp) + "\t" + "(" + resp.getNumFound() + ")\n";
			else mf += " " + summary.get(id) + " ("+id+"), in data from " + 
					getDataSourcesForPhenotypesSet(resp) + "\t" + "(" + resp.getNumFound() + ")\n";
	//		res += summary.get(id) + " ("+id+") appears in " +  sex + ", in data from " + 
	//				getDataSourcesForPhenotypesSet(resp) + "\t" + "(" + resp.getNumFound() + ")\n";
		}
		if (!mf.equalsIgnoreCase("")){
			res += "Both sexes have the following phenotypic abnormalities\n" + mf;
		}
		if (!f.equalsIgnoreCase("")){
			res += "Following phenotypic abnormalities occured in females only\n" + f;
		}
		if (!m.equalsIgnoreCase("")){
			res += "Following phenotypic abnormalities occured in males only\n" + m;
		}
		return res;
	}
	
	@Override
	public PhenotypeSummaryBySex getSummaryObjects(String gene) throws Exception{
		PhenotypeSummaryBySex res = new PhenotypeSummaryBySex();
		HashMap<String, String> summary = getTopLevelMPTerms(gene);		
		for (String id: summary.keySet()){
			SolrDocumentList resp = getPhenotypesForTopLevelTerm(gene, id);
			String sex = getSexesRepresentationForPhenotypesSet(resp);
			HashSet<String> ds = getDataSourcesForPhenotypesSet(resp);
			long n = resp.getNumFound();
			PhenotypeSummaryType phen = new PhenotypeSummaryType(id, summary.get(id), sex, n, ds);
			res.addPhenotye(phen);
		}
		return res;
	}
	
	private SolrDocumentList getPgehnotypes(String gene) throws SolrServerException{
		SolrDocumentList result = runQuery("marker_accession_id:" + gene.replace(":", "\\:"));
		return result;
	}
	
	private SolrDocumentList runQuery(String q) throws SolrServerException {
		String query = q.replaceAll("\\(", "\\\\(");
		query = query.replaceAll("\\)", "\\\\)");
		query = query.replaceAll("\\*", "\\\\*");
		query = query.replaceAll("\\-", "\\\\-");
		SolrQuery solrQuery = new SolrQuery();
		System.out.println("query solrj="+query);
		solrQuery.setQuery(query);
		solrQuery.setRows(1000000);
		System.out.println("solr query obj="+solrQuery);
		QueryResponse rsp = null;
		rsp = server.query(solrQuery);
		return rsp.getResults();
	}
	
	public SolrServer getServer(){
		return server;
	}
	
}
