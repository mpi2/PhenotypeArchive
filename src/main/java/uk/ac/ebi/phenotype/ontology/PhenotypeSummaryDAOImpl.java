package uk.ac.ebi.phenotype.ontology;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.PostQcService;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

@Service
public class PhenotypeSummaryDAOImpl implements PhenotypeSummaryDAO {
	
	@Resource(name="globalConfiguration")
	private Map<String, String> config;

	@Autowired
	@Qualifier("postqcService")
	private PostQcService gpService;
	
	public PhenotypeSummaryDAOImpl() throws MalformedURLException {
	}
	
	@Override
	public String getSexesRepresentationForPhenotypesSet(SolrDocumentList resp) {
		String resume = ""; 
		if (resp.size() > 0) {
			System.out.println("size > 0");
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
	public PhenotypeSummaryBySex getSummaryObjects(String gene)
	throws Exception {

		HashMap<String, String> summary = gpService.getTopLevelMPTerms(gene, null);
		PhenotypeSummaryBySex resSummary = new PhenotypeSummaryBySex();
		for (String id : summary.keySet()) {
			SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id, null);
			String sex = getSexesRepresentationForPhenotypesSet(resp);
			HashSet<String> ds = getDataSourcesForPhenotypesSet(resp);
			long n = resp.getNumFound();
			PhenotypeSummaryType phen = new PhenotypeSummaryType(id, summary.get(id), sex, n, ds);
			resSummary.addPhenotye(phen);
		}
		return resSummary;
	}
	

	@Override
	public HashMap<ZygosityType, PhenotypeSummaryBySex> getSummaryObjectsByZygosity(String gene) throws Exception {
		HashMap< ZygosityType, PhenotypeSummaryBySex> res =  new HashMap<>();
		for (ZygosityType zyg : ZygosityType.values()){
			PhenotypeSummaryBySex resSummary = new PhenotypeSummaryBySex();
			HashMap<String, String> summary = gpService.getTopLevelMPTerms(gene, zyg);	
			for (String id: summary.keySet()){
				SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id, zyg);
				String sex = getSexesRepresentationForPhenotypesSet(resp);
				HashSet<String> ds = getDataSourcesForPhenotypesSet(resp);
				long n = resp.getNumFound();
				PhenotypeSummaryType phen = new PhenotypeSummaryType(id, summary.get(id), sex, n, ds);
				resSummary.addPhenotye(phen);
			}
			if (resSummary.getTotalPhenotypesNumber() > 0){
				res.put(zyg, resSummary);
			}
		}
		return res;
	}
}
