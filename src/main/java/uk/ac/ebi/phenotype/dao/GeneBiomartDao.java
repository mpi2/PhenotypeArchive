package uk.ac.ebi.phenotype.dao;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;

import uk.ac.ebi.generic.util.SolrGeneResponseUtil;
import uk.ac.ebi.phenotype.web.util.DrupalHttpProxy;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

public class GeneBiomartDao implements GeneDao{

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());
	private String solrBaseUrl;
	private HttpProxy httpProxy;
	

	/**
	 * 
	 * @param solrBaseUrl
	 *            e.g. http://dev.mousephenotype.org/bytemark/solr
	 */
	public GeneBiomartDao(String solrBaseUrl) {
		this.solrBaseUrl = solrBaseUrl;
	}

	public String getGeneStatus(String accession) {

		// The proxy must have access to the current request because it will
		// send the DRUPAL cookie along
		this.httpProxy = new HttpProxy();

		String url = this.composeSolrUrlForSingleGene(accession);
		JSONObject jsonObject = requestGenesFromBiomart(url);
		JSONArray docs=jsonObject.getJSONObject("response").getJSONArray("docs");
		if (docs.size() > 1) {
			System.err
					.println("Error, Only expecting 1 document from an accession/gene request");
		}
		//String geneStatus = SolrGeneResponseUtil.deriveGeneStatus(docs.getJSONObject(0));
		String geneStatus=docs.getJSONObject(0).getString("status");
		log.debug("gene status=" + geneStatus);
		return geneStatus;
	}
	
	public Map<String, String> getGeneStatusFromMultipleGenes(String fullUrl) {
		Map<String, String> map=new HashMap<String,String>();
		JSONObject jsonObject = requestGenesFromBiomart(fullUrl);
		JSONArray docs=jsonObject.getJSONObject("response").getJSONArray("docs");
		for(int i=0; i<docs.size(); i++){
			JSONObject doc = docs.getJSONObject(i);
			String geneStatus = SolrGeneResponseUtil.deriveGeneStatus(doc);
			map.put(doc.getString("mgi_accession_id"), geneStatus);
			log.debug("gene status=" + geneStatus);
		}
		
		return map;
	}

	/**
	 * 
	 * @param fullSolrUrl - full solr url for what you want for this method e.g. http://dev.mousephenotype.org/bytemark/solr/gene/search?q=mgi_accession_id:MGI:2441870
	 * @return JSONObject so we can get any meta data associated with docs
	 */
	public JSONObject requestGenesFromBiomart(String fullSolrUrl) {
		log.debug("making request");
		JSONObject jsonObject =null;
		try {
			 jsonObject = processQueryToJson(fullSolrUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
		//return this.json.getJSONObject("response").getJSONArray("docs");
	}

	/**
	 * 
	 * @param accession
	 *            mgi accession e.g. MGI:2441870
	 * @return
	 */
	private String composeSolrUrlForSingleGene(String accession) {
		String url = this.solrBaseUrl + "/" + "gene" + "/select?"
				+ "q=mgi_accession_id:" + accession.replace(":", "\\:")+"&wt=json";
		// url should be something like this
		// http://dev.mousephenotype.org/bytemark/solr/gene/search?q=mgi_accession_id:MGI:2441870
		System.out.println("url for geneDao=" + url);
		return url;
	}

	private JSONObject processQueryToJson(String urlString) throws MalformedURLException {

		log.info("GETTING CONTENT FROM: "+urlString);

		String content = "";

		try {
			content = httpProxy.getContent(new URL(urlString));
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		} catch (URISyntaxException e) {
			log.error(e.getLocalizedMessage());
		}

		return (JSONObject) JSONSerializer.toJSON(content);
	}
}
