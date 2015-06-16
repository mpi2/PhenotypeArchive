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
package uk.ac.ebi.phenotype.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import uk.ac.ebi.phenotype.dao.HibernateDAO;
import uk.ac.ebi.phenotype.dao.HibernateDAOImpl;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public interface PhenotypeSummaryDAO {
	
	// returns one of {male, female, both sexess} for each set of phenotypes 
	public abstract String getSexesRepresentationForPhenotypesSet(
			SolrDocumentList resp);
	
	// Returns a string concatenation of all data sources for a given set
	public abstract HashSet<String> getDataSourcesForPhenotypesSet(
			SolrDocumentList resp);

	public abstract PhenotypeSummaryBySex getSummaryObjects(String gene) throws Exception;
	
	public abstract HashMap<ZygosityType, PhenotypeSummaryBySex> getSummaryObjectsByZygosity(String gene) throws Exception;
	
}
