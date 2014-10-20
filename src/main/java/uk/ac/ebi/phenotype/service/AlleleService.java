package uk.ac.ebi.phenotype.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;

import uk.ac.ebi.phenotype.util.PhenotypingStatusComparator;


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
		public final static String PHENOTYPING_CENTRE = "phenotyping_centre";
		public final static String LATEST_PHENOTYPING_CENTER = "latest_phenotyping_centre";
		public final static String LATEST_PRODUCTION_CENTER = "latest_production_centre";
		public final static String PRODUCTION_CENTER = "production_centre";
		public static final String PHENOTYPING_STATUS = "phenotype_status";
		
	}	
	

	public AlleleService(String solrUrl) {
		solr = new HttpSolrServer(solrUrl);

	}
	
	/**
	 * 
	 * @param geneIds
	 * @return Number of genes (from the provided list) in each status of interest.
	 */
	public HashMap<String, Long> getStatusCount(Set<String> geneIds, String statusField) {

		HashMap<String, Long> res = new HashMap<>();
		SolrQuery solrQuery = new SolrQuery();
		QueryResponse solrResponse;
		
		if (geneIds != null){
			String geneQuery = AlleleField.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneIds, " OR ").replace(":", "\\:") + ")";
			System.out.println("geneQuery: " + geneQuery);
			solrQuery.setQuery(geneQuery);
		}else {
			solrQuery.setQuery("*:*");
		}
		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		try {
			solrQuery.addFacetField(statusField);
			System.out.println("--getStatusCount-- " + solr.getBaseURL() + "/select?" + solrQuery);
			solrResponse = solr.query(solrQuery);
			for (Count c : solrResponse.getFacetField(statusField).getValues()) {
				res.put(c.getName(), c.getCount());
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public TreeMap<String, Long> getStatusCountByPhenotypingCenter(String center, String statusField) {

		TreeMap<String, Long> res = new TreeMap<>(new PhenotypingStatusComparator());
		SolrQuery solrQuery = new SolrQuery();
		QueryResponse solrResponse;
		
		if (center != null){
			String geneQuery = AlleleField.PHENOTYPING_CENTRE + ":\"" + center + "\"";
			solrQuery.setQuery(geneQuery);
		}else {
			solrQuery.setQuery("*:*");
		}
		
		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		try {
			solrQuery.addFacetField(statusField);
			System.out.println("--getStatusCount-- " + solr.getBaseURL() + "/select?" + solrQuery);
			solrResponse = solr.query(solrQuery);
			for (Count c : solrResponse.getFacetField(statusField).getValues()) {
				// We don't want to show everything
				if (!(c.getName().equalsIgnoreCase("Cre Excision Started") || c.getName().equals(""))){
					res.put(c.getName(), c.getCount());
				}
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public Set<String> getFacets(String field){
		SolrQuery solrQuery = new SolrQuery();
		QueryResponse solrResponse;
		Set<String> res = new HashSet<>();
		solrQuery.setQuery("*:*");
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		solrQuery.addFacetField(field);
		System.out.println(this.getClass().getEnclosingMethod() + "   " + solr.getBaseURL() + "/select?" + solrQuery);
		try {
			solrResponse = solr.query(solrQuery);
			for (Count c : solrResponse.getFacetField(field).getValues()) {
				res.add(c.getName());
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return res;
	}
}
