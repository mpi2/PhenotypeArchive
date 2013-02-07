/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.imaging.springrest.images.dao;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import uk.ac.ebi.phenotype.imaging.utils.SolrUtils;

/**
 * Class that gets image data from the solr images index
 * @author jwarren
 */
public class ImagesSolrJ implements ImagesSolrDao {
	private static Logger logger = Logger.getLogger(ImagesSolrJ.class);
	private long numberFound;
	public static SolrServer server = null;
	
	public ImagesSolrJ(String solrBaseUrl) throws MalformedURLException {
		
		// Use system proxy if set
		if (System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyPort") != null) {

			String PROXY_HOST = System.getProperty("http.proxyHost");
			Integer PROXY_PORT = Integer.parseInt(System.getProperty("http.proxyPort"));
			HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT, "http");
			DefaultHttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			server = new HttpSolrServer(solrBaseUrl, client);

			logger.debug("Proxy Settings: " + System.getProperty("http.proxyHost") + " on port: " + System.getProperty("http.proxyPort"));
		} else {
			server = new HttpSolrServer(solrBaseUrl);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao#
	 * getNumberFound()
	 */
	@Override
	public long getNumberFound() {
		return numberFound;
	}

	private void setNumberFound(long numberFound) {
		this.numberFound = numberFound;
	}

	public static void main(String args[]) {
		// GetResultsFromSolr solr = new
		// GetResultsFromSolr("http://localhost:8983/solr/images/select/?q=text");
		// sanger.getSourcesWithTypesLikeKeywordsSearch("exon" );
		// solr.getSourcesForKeywordsSearch("exon");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao#
	 * getIdsForKeywordsSearch(java.lang.String, int, int)
	 */
	@Override
	public List<String> getIdsForKeywordsSearch(String query, int start, int length) {

		return this.getIds(query, start, length);
	}

	private List<String> getIds(String query, int start, int length) {

		SolrDocumentList result = runQuery(query, start, length);

		logger.debug("number found=" + result.getNumFound());
		this.setNumberFound(result.getNumFound());
		if (result.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (int i = 0; i < result.size(); i++) {
				SolrDocument doc = result.get(i);
				ids.add((String) doc.getFieldValue("id"));
			}
			return ids;

		}
		return Collections.emptyList();

	}

	private SolrDocumentList runQuery(String query, int start, int length) {

		SolrQuery solrQuery = new SolrQuery();
		// solrQuery.addFilterQuery("id");
		logger.debug("solr query=" + query);
		solrQuery.setQuery(query);
		solrQuery.setStart(start);
		solrQuery.setRows(length);
		solrQuery.setFields("id");
		// query.addSortField( "price", SolrQuery.ORDER.asc );
		QueryResponse rsp = null;
		try {
			rsp = server.query(solrQuery);
		} catch (SolrServerException e) {
			logger.info(e.getLocalizedMessage());
		}

		return rsp.getResults();
	}
	
	private QueryResponse runFacetQuery(String query, String facetField, int start, int length, String filterQuery) {

		SolrQuery solrQuery = new SolrQuery();
		// solrQuery.addFilterQuery("id");
		logger.debug("solr query=" + query);
		solrQuery.setQuery(query);
		solrQuery.setStart(start);
		solrQuery.setRows(5);
		solrQuery.setFacet(true);
		solrQuery.setFacetMinCount(1);
		solrQuery.addFacetField(facetField);
		if(filterQuery!=""){
		solrQuery.addFilterQuery(filterQuery);
		}
		//logger.debug("url="+solrQuery.);
		//solrQuery.setFields("id");
		// query.addSortField( "price", SolrQuery.ORDER.asc );
		QueryResponse rsp = null;
		try {
			rsp = server.query(solrQuery);
		} catch (SolrServerException e) {
			logger.info(e.getLocalizedMessage());
		}
		logger.debug(rsp);
		
		return rsp;
	}

	@Override
	public QueryResponse getExperimentalFacetForGeneAccession(String geneId) {
		//http://localhost:8983/solr/images/select/?q=accession:MGI\:1933365&rows=0&facet=true&facet.field=expName&facet.limit=-1&facet.mincount=1
		String processedGeneId = SolrUtils.processQuery(geneId);
		QueryResponse solrResp = this.runFacetQuery("accession:"+processedGeneId,"expName", 0,1, "");
		return solrResp;
	}
	
	@Override
	public QueryResponse getExpressionFacetForGeneAccession(String geneId) {
		//http://localhost:8983/solr/images/select/?q=accession:MGI\:1933365&rows=0&facet=true&facet.field=expName&facet.limit=-1&facet.mincount=1
		String processedGeneId = SolrUtils.processQuery(geneId);
		logger.debug("eventually gene id will be here and we'll need an extra filter");
		QueryResponse solrResp = this.runFacetQuery("expName:"+"\"Wholemount Expression\"","higherLevelMaTermName", 0,1, "accession:"+processedGeneId);
		//QueryResponse solrResp = this.runFacetQuery("accession:"+processedGeneId,"higherLevelMaTermName", 0,1);
		
		logger.debug("uri="+solrResp.getRequestUrl());
		return solrResp;
	}
	
	public QueryResponse getDocsForGeneWithFacetField(String geneId, String facetName, String facetValue, String filterQuery, int start, int length){

		SolrQuery solrQuery = new SolrQuery();
		// solrQuery.addFilterQuery("id");
		//System.out.println("query="+u);
		String processedGeneId = SolrUtils.processQuery(geneId);
		String query="accession:"+processedGeneId;
//		if(facetName.equals("higherLevelMaTermName")){
//			logger.debug("hack here to get rid of gene from expression request");
//			query="expName:"+"\"Wholemount Expression\"";
//		}
		
		
		//logger.debug("setting gene id to expression query not gene id until we have genes with expression=" + query);
		
		solrQuery.setQuery(query);
		solrQuery.setStart(start);
		solrQuery.setRows(length);
		//solrQuery.setFields("id");

		String facetQuery=facetName+":"+"\""+facetValue+"\"";//need to add quotes around so that spaces are allowed
		solrQuery.addFilterQuery(facetQuery);
		logger.debug("facet name and val===="+facetQuery);
		// query.addSortField( "price", SolrQuery.ORDER.asc );

		if(filterQuery!=""){
			solrQuery.addFilterQuery(filterQuery);
			}
		QueryResponse rsp = null;
		try {
			rsp = server.query(solrQuery);
		} catch (SolrServerException e) {
			logger.info(e.getLocalizedMessage());
		}
		logger.debug("uri="+rsp.getRequestUrl());
		return rsp;
	}
	
	private String processSpacesForSolr(String valueWithPosibleSpaces){
		//put quotes around any query that contains spaces
		if(valueWithPosibleSpaces.contains(" ")){
		if(valueWithPosibleSpaces.equals("*"))return valueWithPosibleSpaces;
				if(!valueWithPosibleSpaces.startsWith("\"") && !valueWithPosibleSpaces.endsWith("\"")){
					valueWithPosibleSpaces="\""+valueWithPosibleSpaces+"\"";
				}
				return valueWithPosibleSpaces;
		}return valueWithPosibleSpaces;
	}

	@Override
	public QueryResponse getDocsForMpTerm(String mpId, int start, int length) {

		SolrQuery solrQuery = new SolrQuery();
		// solrQuery.addFilterQuery("id");
		mpId=SolrUtils.processQuery(mpId);
		String query="annotationTermId:"+mpId;
		
		logger.debug("solr query=" + query);
		
		solrQuery.setQuery(query);
		solrQuery.setStart(start);
		solrQuery.setRows(length);
		//solrQuery.setFields("id");
//		String filterQuery=facetName+":"+"\""+facetValue+"\"";//need to add quotes around so that spaces are allowed
//		solrQuery.addFilterQuery(filterQuery);
//		logger.debug("facet name and val===="+filterQuery);
		// query.addSortField( "price", SolrQuery.ORDER.asc );
		QueryResponse rsp = null;
		try {
			rsp = server.query(solrQuery);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			logger.info(e.getLocalizedMessage());
		}
		logger.debug("uri="+rsp.getRequestUrl());
		return rsp;
	}

	
	
	
	
	
	@Override
	/**
	 * filterField e.g. annotationTerm:large ear
	 */
	public QueryResponse getFilteredDocsForQuery(String query, List<String> filterFields, String qf, String defType, int start, int length) {

		SolrQuery solrQuery = new SolrQuery();
		//if query field is set such as auto_suggest we need to add this to the search
		if(!qf.equals("")){
			solrQuery.set("qf", qf);
			System.out.println("added qf="+qf);
		}
		if(!defType.equals("")){
			solrQuery.set("defType", defType);
		}
		System.out.println("--------------query here="+query);
		solrQuery.setQuery(query);
		solrQuery.setStart(start);
		solrQuery.setRows(length);
		

		
			
			for(String fieldNValue:filterFields){
				String []colonStrings=fieldNValue.split(":");
				String filterField=colonStrings[0];
				String filterParam=fieldNValue.substring(fieldNValue.indexOf(":")+1, fieldNValue.length());
				//String processedExperiment=processQuery(filterParam);
				filterParam=processSpacesForSolr(filterParam);
				String fq=filterField+":"+filterParam;
				System.out.println("adding filter fieldNValue="+fq);
				
				solrQuery.addFilterQuery(fq);
			}

			System.out.println(solrQuery.toString());
		QueryResponse rsp = null;

		try {
			rsp = server.query(solrQuery);
		} catch (SolrServerException e) {
			logger.info(e.getLocalizedMessage());
		}
		return rsp;
	}


	

}
