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
package uk.ac.ebi.phenotype.pojo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;



public interface PhenotypeCallSummarySolr {

	public PhenotypeFacetResult getPhenotypeCallByGeneAccession(String accId) throws IOException, URISyntaxException;
	
	public PhenotypeFacetResult getPhenotypeCallByGeneAccessionAndFilter(String accId, String filterString) throws IOException, URISyntaxException;
	
	public PhenotypeFacetResult getPreQcPhenotypeCallByGeneAccessionAndFilter(String accId, String filterString) throws IOException, URISyntaxException;

	public PhenotypeFacetResult getPhenotypeCallByMPAccession(
			String phenotype_id) throws IOException, URISyntaxException;
	
	public PhenotypeFacetResult getPhenotypeCallByMPAccessionAndFilter(
		String phenotype_id, String filter) throws IOException, URISyntaxException;
	
	public PhenotypeFacetResult getPreQcPhenotypeCallByMPAccessionAndFilter(
		String phenotype_id, String filter) throws IOException, URISyntaxException;

	public List<? extends StatisticalResult> getStatisticalResultFor(String accession, String parameterStableId, ObservationType observationType, String strainAccession, String alleleAccession)throws IOException, URISyntaxException;
	
}
