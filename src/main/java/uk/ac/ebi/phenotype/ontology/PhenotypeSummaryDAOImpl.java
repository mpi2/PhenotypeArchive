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
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

@Service
public class PhenotypeSummaryDAOImpl implements PhenotypeSummaryDAO {
	
	@Resource(name="globalConfiguration")
	private Map<String, String> config;

	@Autowired
	private GenotypePhenotypeService gpService;
	
	public PhenotypeSummaryDAOImpl() throws MalformedURLException {
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
		HashMap<String, String> summary = gpService.getTopLevelMPTerms(gene);	
		String res = "Phenotype Summary based on automated MP annotations supported by experiments from ";
		res += getDataSourcesForPhenotypesSet(gpService.getPgehnotypes(gene));
		res +=" on knockout mouse models\n\n";
		String m = "";
		String f = "";
		String mf = ""; 
		
		for (String id: summary.keySet()){
			SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id);
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
		HashMap<String, String> summary = gpService.getTopLevelMPTerms(gene);		
		for (String id: summary.keySet()){
			SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id);
			String sex = getSexesRepresentationForPhenotypesSet(resp);
			HashSet<String> ds = getDataSourcesForPhenotypesSet(resp);
			long n = resp.getNumFound();
			PhenotypeSummaryType phen = new PhenotypeSummaryType(id, summary.get(id), sex, n, ds);
			res.addPhenotye(phen);
		}
		return res;
	}
			
}
