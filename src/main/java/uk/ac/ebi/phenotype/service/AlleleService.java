/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
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

import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.util.PhenotypingStatusComparator;
import uk.ac.ebi.phenotype.util.ProductionStatusComparator;


public class AlleleService {


	private HttpSolrServer solr;
	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());
	
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
			String geneQuery = AlleleDTO.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneIds, " OR ").replace(":", "\\:") + ")";
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
//			System.out.println("--getStatusCount-- " + solr.getBaseURL() + "/select?" + solrQuery);
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
			String geneQuery = AlleleDTO.PHENOTYPING_CENTRE + ":\"" + center + "\"";
			solrQuery.setQuery(geneQuery);
		}else {
			solrQuery.setQuery("*:*");
		}
		
		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		try {
			solrQuery.addFacetField(statusField);
//			System.out.println("--getStatusCount-- " + solr.getBaseURL() + "/select?" + solrQuery);
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
	
	public TreeMap<String, Long> getStatusCountByProductionCenter(String center, String statusField) {

		TreeMap<String, Long> res = new TreeMap<>(new ProductionStatusComparator());
		SolrQuery solrQuery = new SolrQuery();
		QueryResponse solrResponse;
		
		if (center != null){
			String geneQuery = AlleleDTO.LATEST_PRODUCTION_CENTRE + ":\"" + center + "\"";
			solrQuery.setQuery(geneQuery);
		}else {
			solrQuery.setQuery("*:*");
		}
		
		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		try {
			solrQuery.addFacetField(statusField);
//			System.out.println("--getStatusCount-- " + solr.getBaseURL() + "/select?" + solrQuery);
			solrResponse = solr.query(solrQuery);
			for (Count c : solrResponse.getFacetField(statusField).getValues()) {
				// We don't want to show everything
				if (!c.getName().equals("")){
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
