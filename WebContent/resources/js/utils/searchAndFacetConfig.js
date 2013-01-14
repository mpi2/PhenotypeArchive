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
 * 
 * searchAndFacetConfig: definition of variables for the search and facet 
 * see searchAndFacet directory
 * 
 * Author: Chao-Kung Chen
 * 
 */
$(document).ready(function(){ 
	
	MPI2.searchAndFacetConfig = {};
	var config = MPI2.searchAndFacetConfig;
	
	config.solrBaseURL_bytemark = drupalBaseUrl + '/bytemark/solr/';	
	config.solrBaseURL_ebi = drupalBaseUrl + '/mi/solr/';
	
	config.spinner = "<img src='img/loading_small.gif' /> Processing search ...";
	config.spinnerExport = "<img src='img/loading_small.gif' /> Processing data for export, please do not interrupt ... ";
	config.endOfSearch = "Search result";
	config.facetParams = {	
		 gene:      {
			 type: 'genes',
			 solrCoreName: 'gene', 
			 tableCols: 3, 
			 tableHeader: "<thead><th>Gene</th><th>Latest Status</th><th>Register for Updates</th></thead>",
			 gridName: 'geneGrid',
			 gridFields: 'marker_symbol,synonym,marker_name' // should include status soon
		 },
		 pipeline: {
			 type: 'procedures',
			 solrCoreName: 'pipeline', 
			 tableCols: 3, 
			 tableHeader: '<thead><th>Parameter</th><th>Procedure</th><th>Pipeline</th></thead>', 
			 fq: "pipeline_stable_id:IMPC_001", 
			 qf: 'auto_suggest', 
			 defType: 'edismax',
			 wt: 'json',
			 gridFields: 'parameter_name,procedure_name,pipeline_name'
		 },
		 mp: {
			 type: 'phenotypes',
			 solrCoreName: 'mp', 
			 tableCols: 2, 
			 tableHeader: '<thead><th>Phenotype</th><th>Definition</thead>', 
			 fq: "ontology_subset:*", 
			 qf: 'auto_suggest', 
			 defType: 'edismax',
			 wt: 'json',
			 gridFields: 'mp_term,mp_definition,mp_id,top_level_mp_term'
		 },
		 images:     {
			 type: 'images',
			 solrCoreName: 'images', 
			 tableCols: 2, 
			 tableHeader: '<thead><th>Name</th><th>Example Images</th></thead>', 
			 fq: "annotationTermId:M*", 
			 qf: 'auto_suggest', 
			 defType: 'edismax',
			 wt: 'json',
			 gridFields: 'annotationTermId,annotationTermName,expName,symbol_gene,smallThumbnailFilePath,largeThumbnailFilePath'
		 }					
	}; 
});

