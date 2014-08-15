package uk.ac.ebi.phenotype.service;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;

import uk.ac.ebi.phenotype.service.dto.GeneDTO;


public class AlleleService {


	private HttpSolrServer solr;

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());
	
	public static final class AlleleField {
		public final static String GENE_LATEST_EC_CELL_STATUS = "gene_latest_es_cell_status";
		public final static String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
		public final static String GENE_LATEST_MOUSE_STATUS = "gene_latest_mouse_status";
		public final static String MGI_ACCESSION_ID = "mgi_accession_id";
		public final static String MARKER_SYMBOL = "marker_symbol";
		public final static String TOP_LEVEL_MP_ID="top_level_mp_id";
	}
	
	
	

	public AlleleService(String solrUrl) {
		solr = new HttpSolrServer(solrUrl);

	}
	
	/**
	 * 
	 * @param geneIds
	 * @return Number of genes (from the provided list) in each status of interest.
	 */
	
	
	/*
	 * 
<lst name="gene_latest_es_cell_status">
<int name="No ES Cell Production">31822</int>
<int name="ES Cell Targeting Confirmed">15154</int>
<int name="ES Cell Production in Progress">1530</int>


<lst name="latest_phenotype_status">
<int name="">46140</int>
<int name="Phenotype Attempt Registered">1427</int>
<int name="Phenotyping Started">506</int>
<int name="Phenotyping Complete">441</int>
<int name="Cre Excision Started">4</int>


<lst name="gene_latest_mouse_status">
<int name="">43035</int>
<int name="Genotype confirmed">2464</int>
<int name="Cre Excision Complete">1448</int>
<int name="Chimeras obtained">1094</int>
<int name="Micro-injection in progress">247</int>
<int name="Cre Excision Started">230</int>

	 */
	public HashMap<String, Long> getStatusCount(Set<String> geneIds, String statusField){
		
		HashMap<String, Long> res = new HashMap<>();
		
		// build query for these genes
		String geneQuery = AlleleField.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneIds, " OR ").replace(":", "\\:") + ")";
		System.out.println("geneQuery: " + geneQuery);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(geneQuery)
			.setRows(1)
			.setFacet(true);
		QueryResponse solrResponse;
		try {
			// add facet for latest_project_status 
			solrQuery.addFacetField(statusField);
			System.out.println("---- " + solr.getBaseURL() + "/select?" + solrQuery);
			solrResponse = solr.query(solrQuery);
			// put all values in the hash
			for (Count c : solrResponse.getFacetField(statusField).getValues()){
				res.put(c.getName(), c.getCount());
			}
			
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		return res;
	}
}
