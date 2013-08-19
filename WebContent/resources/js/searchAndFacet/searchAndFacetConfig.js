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


if(typeof(window.MPI2) === 'undefined') {
    window.MPI2 = {};
}
MPI2.buttCount = 0;

MPI2.searchAndFacetConfig = {};
var config = MPI2.searchAndFacetConfig;


// on drupal side this is not available
if ( typeof solrUrl == 'undefined' ){
	solrUrl = '/data/solr';
}

if ( typeof baseUrl == 'undefined' ){
	baseUrl = '/data';
}

config.lastParams = false;
config.cores = ['gene', 'mp', 'pipeline', 'images'];
config.restfulPrefix = {
		'gene' : 'genes',
		'mp'   : 'phenotypes'
};

config.geneStatuses = ['Phenotype Data Available',
               'Mice Produced',
               'Assigned for Mouse Production and Phenotyping',
               'ES Cells Produced',
               'Assigned for ES Cell Production',
               'Not Assigned for ES Cell Production'];

config.phenotypingStatuses = {'Complete':{'fq':'imits_phenotype_complete','val':1}, 
                              'Started':{'fq':'imits_phenotype_started','val':1}, 
                              'Attempt Registered':{'fq':'imits_phenotype_status', 'val':'Phenotype Attempt Registered'}};


                            
//config.solrBaseURL_bytemark = 'http://dev.mousephenotype.org/bytemark/solr/';
config.solrBaseURL_bytemark = solrUrl + '/';
config.solrBaseURL_ebi = solrUrl + '/';

config.searchSpin = "<img src='img/loading_small.gif' />";
config.spinner = "<img src='img/loading_small.gif' /> Processing search ...";
config.spinnerExport = "<img src='img/loading_small.gif' /> Processing data for export, please do not interrupt ... ";
config.endOfSearch = "Search result";

// custom 404 page does not know about baseUrl
var path = window.location.pathname.replace(/^\//,"");
path = '/' + path.substring(0, path.indexOf('/'));
//var trailingPath = '/searchAndFacet';
var trailingPath = '/search';
var trailingPathDataTable = '/dataTable';

config.pathname = typeof baseUrl == 'undefined' ? path + trailingPath : baseUrl + trailingPath;
config.dataTablePath = typeof baseUrl == 'undefined' ? path + trailingPathDataTable : baseUrl + trailingPathDataTable;

var commonSolrParams = {					
		'qf': 'auto_suggest',
		'defType': 'edismax',
		'wt': 'json',
		'rows': 0
};
config.commonSolrParams = commonSolrParams;

config.facetParams = {	
	geneFacet:      {
		type: 'genes',			
		solrCoreName: 'gene',			 
		tableCols: 3, 	
		tableHeader: "<thead><th>Gene</th><th>Mouse Production Status</th><th>Phenotyping Status</th><th>Register for Updates</th></thead>",
		fq: undefined,
		qf: "marker_symbol^100.0 marker_name^10.0 allele^10 marker_synonym mgi_accession_id auto_suggest",
		gridName: 'geneGrid',
		gridFields: 'marker_symbol,marker_synonym,marker_name,status', 
		filterParams: {fq:'marker_type:* -marker_type:"heritable phenotypic marker"',		 
			      qf:"marker_symbol^100.0 marker_name^10.0 marker_synonym mgi_accession_id auto_suggest",			     
			      bq:'marker_type:"protein coding gene"^100'},
		srchParams: $.extend({},				
				 	commonSolrParams,	 	
					{fq:'marker_type:* -marker_type:"heritable phenotypic marker"'}),
		subFacet_filter_params: '', // set by widget on the fly
		breadCrumbLabel: 'Genes'		
	 },	
	 pipelineFacet: {		
		 type: 'procedures',		 
		 solrCoreName: 'pipeline',			
		 tableCols: 3, 
		 tableHeader: '<thead><th>Parameter</th><th>Procedure</th><th>Pipeline</th></thead>', 
		 fq: "pipeline_stable_id:IMPC_001", 
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: 'parameter_name,procedure_name,pipeline_name',
		 gridName: 'pipelineGrid',	
		 filterParams:{'fq': 'pipeline_stable_id:IMPC_001'},
		 breadCrumbLabel: 'Procedures',		 
		 srchParams: $.extend({},
					commonSolrParams,    				
					{'fq': 'pipeline_stable_id:IMPC_001'})					
	 },	
	 mpFacet: {	
		 type: 'phenotypes',
		 solrCoreName: 'mp', 
		 tableCols: 2, 
		 tableHeader: '<thead><th>Phenotype</th><th>Definition</thead>', 
		 fq: "ontology_subset:*", 
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: 'mp_term,mp_definition,mp_id,top_level_mp_term',
		 gridName: 'mpGrid',
		 topLevelName: '',
		 ontology: 'mp',
		 breadCrumbLabel: 'Phenotypes',		
		 filterParams: {'fq': "ontology_subset:*", 'fl': 'mp_id,mp_term,mp_definition,top_level_mp_term'},
		 srchParams: $.extend({},				
					commonSolrParams,	 	
					{fq: 'ontology_subset:*'})
	 },	
	 maFacet: {	
		 type: 'tissues',
		 solrCoreName: 'ma', 
		 tableCols: '', 
		 tableHeader: '<thead><th></th><th></thead>', 
		 fq: '', 
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: '',
		 gridName: 'maGrid',
		 topLevelName: '',
		 ontology: 'ma',
		 filterParams: {},
		 srchParams: $.extend({},
					commonSolrParams)		
	 },	
	 imagesFacet: {		
		 type: 'images',
		 solrCoreName: 'images', 
		 tableCols: 2, 
		 tableHeader: '<thead><th>Name</th><th>Example Images</th></thead>', 
		 fq: 'annotationTermId:M* OR expName:* OR symbol:* OR higherLevelMaTermName:* OR higherLevelMpTermName:*',			 
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: 'annotationTermId,annotationTermName,expName,symbol_gene,smallThumbnailFilePath,largeThumbnailFilePath',
		 gridName: 'imagesGrid',
		 topLevelName: '',
		 imgViewSwitcherDisplay: 'Annotation View',
		 forceReloadImageDataTable: false,
		 showImgView: true,
		 breadCrumbLabel: 'Images',
		 filterParams: {//'fl' : 'annotationTermId,annotationTermName,expName,symbol,symbol_gene,smallThumbnailFilePath,largeThumbnailFilePath',
			 	  'fq' : "annotationTermId:M* OR expName:* OR symbol:* OR higherLevelMaTermName:* OR higherLevelMpTermName:*"},	
	 	 srchParams: $.extend({},
				commonSolrParams,				
				{'fl' : 'higherLevelMaTermName,higherLevelMpTermName,annotationTermId,annotationTermName,expName,symbol'})			
	 }
}; 
