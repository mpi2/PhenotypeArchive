package uk.ac.ebi.phenotype.stats;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.web.util.HttpProxy;


@Service
public class GenotypePhenotypeService {

	
	private HttpSolrServer solr;
	
	public GenotypePhenotypeService(String baseUrl){
		solr = getSolrInstance(baseUrl);
	}
	
	public List<Group> getGenesBy(String phenotype_id, String sex) throws SolrServerException{
		//males only
		SolrQuery q = new SolrQuery()
		.setQuery("(mp_term_id:\"" + phenotype_id + "\" OR top_level_mp_term_id:\"" + phenotype_id + "\") AND (strain_accession_id:\"MGI:2159965\" OR strain_accession_id:\"MGI:2164831\")")
		.setFilterQueries("resource_fullname:EuroPhenome")
		.setRows(10000);
		q.set("group.field", "marker_symbol");
		q.set("group", true);
		if (sex != null){
			q.addFilterQuery("sex:" + sex);
		}
		QueryResponse results = solr.query(q);
		return results.getGroupResponse().getValues().get(0).getValues();
	}
	
	private HttpSolrServer getSolrInstance(String solrBaseUrl){
		Proxy proxy; 
		HttpSolrServer server = null;
		try {
			proxy = (new HttpProxy()).getProxy(new URL(solrBaseUrl));
			if (proxy != null) {
				DefaultHttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				server = new HttpSolrServer(solrBaseUrl, client);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if(server == null){
			server = new HttpSolrServer(solrBaseUrl);
		}
		return server;
	}
	
	public List<String> getGenesAssocByParamAndMp (String parameterStableId, String phenotype_id) throws SolrServerException{
		List<String> res = new ArrayList<String>();
		SolrQuery query = new SolrQuery()
		.setQuery("(mp_term_id:\"" + phenotype_id + "\" OR top_level_mp_term_id:\"" + phenotype_id + "\") AND (strain_accession_id:\"MGI:2159965\" OR strain_accession_id:\"MGI:2164831\") AND parameter_stable_id:\"" + parameterStableId+"\"")
		.setFilterQueries("resource_fullname:EuroPhenome")
		.setRows(10000);	
		query.set("group.field", "marker_accession_id");
		query.set("group", true);
		List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
		for (Group gr : groups){
			if (!res.contains((String)gr.getGroupValue())){
				res.add((String) gr.getGroupValue());
			}
		}
		return res;
	}
}
