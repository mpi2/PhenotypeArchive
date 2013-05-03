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
package uk.ac.ebi.phenotype.imaging.utils;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;

public class SolrUtils {

	private static Logger logger = Logger.getLogger(SolrUtils.class);

	public QueryResponse runSolrQuery(SolrServer server, String query,
			String facetName, String facetValue, List<String> filterQuerys,
			int start, int length) {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("defType","edismax");//add this to mean no escaping of : or extra string manipulation needed
		solrQuery.setQuery(query);
		solrQuery.setStart(start);
		solrQuery.setRows(length);
		// solrQuery.setFields("id");

		if(!facetName.equals("") && !facetValue.equals("")){
		String facetQuery = facetName + ":" + facetValue;
		solrQuery.addFilterQuery(facetQuery);
		logger.debug("facet name and val====" + facetQuery);
		}
		// query.addSortField( "price", SolrQuery.ORDER.asc );
		for (String filterQuery : filterQuerys) {
			if (filterQuery != "") {
				solrQuery.addFilterQuery(filterQuery);
			}
		}
		QueryResponse rsp = null;
		try {
			rsp = server.query(solrQuery);
		} catch (SolrServerException e) {
			logger.info(e.getLocalizedMessage());
		}
		logger.debug("uri=" + rsp.getRequestUrl());
		return rsp;
	}

	/**
	 * Method to handle spaces within queries for solr requests via solrj
	 * 
	 * @param id
	 */
	public static String processQuery(String id) {

		String processedId = id;

		// Quote the ID if it hasn't been already
		if (processedId.contains(":") && !processedId.contains("\\")) {
			processedId = processedId.replace(":", "\\:");
		}

		// put quotes around any query that contains spaces
		if (processedId.contains(" ")) {
			processedId = "\"" + processedId + "\"";
		}

		return processedId;
	}
}
