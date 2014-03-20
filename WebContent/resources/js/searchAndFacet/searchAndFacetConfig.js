/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
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

if ( typeof $ === 'undefined'){
	$ = window.jQuery;
}

if(typeof(window.MPI2) === 'undefined') {
    window.MPI2 = {};
}

MPI2.searchAndFacetConfig = {};
var config = MPI2.searchAndFacetConfig;

config.widgetOpen = false;
config.hasFilters = false;

// on drupal side this is not available
if ( typeof solrUrl == 'undefined' ){
	solrUrl = '/data/solr';	
}

if ( typeof baseUrl == 'undefined' ){
	baseUrl = '/data';
}

config.cores = ['gene', 'mp', 'disease', 'ma', 'pipeline', 'images'];
config.restfulPrefix = {
		'gene' : 'genes',
		'mp'   : 'phenotypes',
		'ma'   : 'anatomy'		
};

config.geneStatuses = ['Phenotype Data Available',
               'Mice Produced',
               'Assigned for Mouse Production and Phenotyping',
               'ES Cells Produced',
               'Assigned for ES Cell Production',
               'Not Assigned for ES Cell Production'];

config.phenotypingStatuses = {
	'Complete':{'fq':'imits_phenotype_complete','val':1}, 
    'Started':{'fq':'imits_phenotype_started','val':1}, 
    'Attempt Registered':{'fq':'imits_phenotype_status', 'val':'Phenotype Attempt Registered'}
};

config.phenotypingStatusFq2Label = {
		'imits_phenotype_complete' : 'Complete',
		'imits_phenotype_started'  : 'Started',
		'imits_phenotype_status'   : 'Attempt Registered'
};

config.subType2MarkerTypeMapping = {
		'subtype' : 'marker_type'
}
config.markerType2SubTypeMapping = {
		'marker_type' : 'subtype'
}
config.expName2ProcSidMapping = {
		'Dysmorphology' : 'IMPC_CSD_002',
		'Eye Morphology' : 'IMPC_EYE_001',
		'Flow Cytometry' : 'IMPC_FAC_001',
		'Histology Slide' : 'IMPC_HIS_001',
		'Wholemount Expression' : 'IMPC_ALZ_001',
		'Xray' : 'IMPC_XRY_001',				
		'expName:' : 'procedure_stable_id:'				
};	
config.procSid2ExpNameMapping = {
		'IMPC_CSD_002' : 'Dysmorphology',
		'IMPC_EYE_001' : 'Eye Morphology',
		'IMPC_FAC_001' : 'Flow Cytometry',
		'IMPC_HIS_001' : 'Histology Slide',
		'IMPC_ALZ_001' : 'Wholemount Expression',
		'IMPC_XRY_001' : 'Xray',				
		'procedure_stable_id:' : 'expName:' 				
};	

config.facetFilterLabel = {
	'phenotyping_center'         : 'phenotyping_center',
	'production_center'          : 'production_center',
	//'imits_phenotype_complete'   : 'phenotyping_status',
	//'imits_phenotype_started'    : 'phenotyping_status',
	//'imits_phenotype_status'     : 'phenotyping_status',
	'imits_phenotype_complete'   : 'phenotyping',
	'imits_phenotype_started'    : 'phenotyping',
	'imits_phenotype_status'     : 'phenotyping',
	'status'                     : 'mouse_production_status',
	'marker_type'                : 'gene_subtype',
	'top_level_mp_term'          : 'top_level_term',
	'selected_top_level_ma_term' : 'top_level_term',
	'procedure_stable_id'        : 'procedure',
	'ma'					     : 'anatomy',
	'annotated_or_inferred_higherLevelMaTermName' : 'anatomy',
	'mp'      					 : 'phenotype',
	'annotated_or_inferred_higherLevelMpTermName' : 'phenotype',
	'expName'                    : 'procedure',
	'subtype'                    : 'gene_subtype',	
	'disease_classes' 			 : 'disease_classification',
	'disease_source'			 : 'disease_source',
	'human_curated'              : 'From human data (OMIM, Orphanet)',//'human_data',
	'mouse_curated'              : 'From mouse data (MGI)', //'mouse_data',
	'impc_predicted'             : 'From MGP data',//'IMPC_predicted',
	'impc_predicted_in_locus'    : 'From MGP data in linkage locus',//'IMPC_predicted_in_locus',
	'mgi_predicted'              : 'From MGI data',//'MGI_predicted',
	'mgi_predicted_in_locus'     : 'From MGI data in linkage locus',//'MGI_predicted_in_locus'	
};

config.filterMapping = {
		//gene
		/*'imits_phenotype_complete|1':{'class':'inprogressphenotyping', 'facet':'gene'},		
		'imits_phenotype_started|1' : {'class':'phenotyping', 'facet':'gene'},		
		'imits_phenotype_status|Phenotype Attempt Registered' : {'class':'phenotyping', 'facet':'gene'},
		'status|Mice Produced' : {'class':'production', 'facet':'gene'},		
		'status|Assigned for Mouse Production and Phenotyping' : {'class':'production', 'facet':'gene'},
		'status|ES Cells Produced' : {'class':'production', 'facet':'gene'},
		'status|Assigned for ES Cell Production' : {'class':'production', 'facet':'gene'},
		'status|Not Assigned for ES Cell Production' : {'class':'production', 'facet':'gene'},
		'marker_type' : {'class':'marker_type', 'facet':'gene'},*/
		
		'imits_phenotype_complete':{'class':'inprogressphenotyping', 'facet':'gene'},		
		'imits_phenotype_started' : {'class':'phenotyping', 'facet':'gene'},
		'imits_phenotype_status' : {'class':'phenotyping', 'facet':'gene'},
		'status' : {'class':'production', 'facet':'gene'},		
		'marker_type' : {'class':'marker_type', 'facet':'gene'},
		
		
		// mp, ma
		'mp' : {'class':'', 'facet':'mp'},
		'ma' : {'class':'', 'facet':'ma'},
		'annotated_or_inferred_higherLevelMxTermName' : {'class':'', 'facet':'mp'},
		
		// disease
		'disease_source' : {'class':'disease_source', 'facet':'disease'},
		'disease_classes' : {'class':'disease_classes', 'facet':'disease'},
		'human_curated' : {'class':'curated', 'facet':'disease'},
		'mouse_curated' : {'class':'curated', 'facet':'disease'},
		'impc_predicted' : {'class':'predicted', 'facet':'disease'},
		'impc_predicted_in_locus' : {'class':'predicted', 'facet':'disease'},
		'mgi_predicted' : {'class':'predicted', 'facet':'disease'},
		'mgi_predicted_in_locus' : {'class':'predicted', 'facet':'disease'},
		
		// pipeline: using ajax		
		
		// images
		'imgMp' : {'class':'annotated_or_inferred_higherLevelMpTermName', 'facet':'images'},
		'imgMa' : {'class':'annotated_or_inferred_higherLevelMaTermName', 'facet':'images'},
};

var megaFacetFields = ['status', 'imits_phenotype_complete', 'imits_phenotype_started', 'imits_phenotype_status', 
                       'mgi_accession_id', 'marker_type', 'top_level_mp_term', 'mp_term', 
                       'inferred_selected_top_level_ma_term', 'inferred_ma_term', 
                       'procedure_name'];
var facetFieldsStr = '';
for ( var i=0; i<megaFacetFields.length; i++){
	facetFieldsStr += '&facet.field=' + megaFacetFields[i];
}
var facetMod = "&facet=on&facet.limit=-1&facet.mincount=1";
config.mega = {};
config.mega.facetParams = facetMod + facetFieldsStr;
config.mega.Facets = {
	//'mpFacet' : '&facet.field=top_mp_term_id&facet.field=top2mp_id&facet.field=top2mp_term&facet.field=top2mp_def&facet.field=top2mp_idTermDef' + facetMod
	'mpFacet' : '&facet.field=top_mp_term_id&facet.field=top2mp_idTermDef&facet.field=mp_idTermDef' + facetMod
};
//MPI2.searchAndFacetConfig.mega.Facets
//console.log(config.mega.facetParams);
//config.solrBaseURL_bytemark = 'http://dev.mousephenotype.org/bytemark/solr/';
config.solrBaseURL_bytemark = solrUrl + '/';
config.solrBaseURL_ebi = solrUrl + '/';

config.searchSpin = "<img src='img/loading_small.gif' />";
config.spinner = "<img src='img/loading_small.gif' /> Processing search ...";
config.spinnerExport = "<img src='img/loading_sm_tickFilterCheckBox('mp');	all.gif' /> Processing data for export, please do not interrupt ... ";
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
		subFacetFqFields: ['imits_phenotype_started', 'imits_phenotype_complete', 'imits_phenotype_status', 'status', 'marker_type'],			
		solrCoreName: 'gene',			 
		tableCols: 3, 	
		tableHeader: "<thead><th>Gene</th><th>Production Status</th><th>Phenotype Status</th><th></th></thead>",
		fq: 'marker_type:* -marker_type:"heritable phenotypic marker"',//undefined,
		//fq: 'marker_type:* -marker_type:"heritable phenotypic marker" (production_center:* AND phenotyping_center:*)',//undefined,
		//centerFq: 'marker_type:* -marker_type:"heritable phenotypic marker" AND (production_center:* AND phenotyping_center:*)',
		qf: "marker_symbol^100.0 human_gene_symbol^90.0 marker_name^10.0 allele^10 marker_synonym mgi_accession_id auto_suggest",
		gridName: 'geneGrid',
		gridFields: 'marker_symbol,marker_synonym,marker_name,status,human_gene_symbol', 
		//filterParams: {fq:'marker_type:* -marker_type:"heritable phenotypic marker" (production_center:* AND phenotyping_center:*)',	
		filterParams: {fq:'marker_type:* -marker_type:"heritable phenotypic marker"',	
			      qf:"marker_symbol^100.0 human_gene_symbol^90.0 marker_name^10.0 marker_synonym mgi_accession_id auto_suggest",			     
			      bq:'marker_type:"protein coding gene"^100'},
		srchParams : $.extend({},				
				 	commonSolrParams	 	
					),
		subFacet_filter_params: '', // set by widget on the fly
		breadCrumbLabel: 'Genes'		
	 },	
	 pipelineFacet: {	
		 type: 'parameters',
		 subFacetFqFields: 'procedure_stable_id', 		 
		 solrCoreName: 'pipeline',			
		 tableCols: 3, 
		 tableHeader: '<thead><th>Parameter</th><th>Procedure</th><th>Pipeline</th></thead>',		
		 fq: "pipeline_stable_id:*", //"pipeline_stable_id:IMPC_001", 
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: 'parameter_name,procedure_name,pipeline_name',
		 gridName: 'pipelineGrid',	
		 //filterParams:{'fq': 'pipeline_stable_id:IMPC_001'},
		 breadCrumbLabel: 'Parameters',	
		 filterParams: {fq:'pipeline_stable_id:*'},	 
		 srchParams: $.extend({},				     
					commonSolrParams    				
					)					
	 },	
	 mpFacet: {	
		 type: 'phenotypes',
		 subFacetFqFields: 'top_level_mp_term',
		 solrCoreName: 'mp', 
		 tableCols: 2, 
		 tableHeader: '<thead><th>Phenotype</th><th>Definition</th></thead>', 
		 subset: 'ontology_subset:*',
		 fq: 'ontology_subset:*', 
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: 'ma_term,ma_id',
		 gridName: 'mpGrid',
		 topLevelName: '',
		 ontology: 'mp',
		 breadCrumbLabel: 'Phenotypes',		
		 //filterParams: {'fq': 'ontology_subset:IMPC_Terms'},
		 filterParams: {'fq': 'ontology_subset:*'},
		 srchParams: $.extend({},				
					commonSolrParams,	 	
					{'fl': 'mp_id,mp_term,mp_definition,top_level_mp_term,top_mp_term_id'})
	 },	
	 maFacet: {			    	
		 type: 'tissues',
		 subFacetFqFields: 'selected_top_level_ma_term',
		 solrCoreName: 'ma', 
		 tableCols: 1, 
		 tableHeader: '<thead><th>Anatomy</th></thead>', 
		 subset: 'ontology_subset:IMPC_Terms',
		 fq: 'ontology_subset:IMPC_Terms AND selected_top_level_ma_term:*', 
		 //fq: 'ontology_subset:IMPC_Terms',
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: '',
		 gridName: 'maGrid',
		 topLevelName: '',
		 ontology: 'ma',
		 breadCrumbLabel: 'Anatomy',		 
		 //filterParams: {'fq': "ontology_subset:IMPC_Terms AND selected_top_level_ma_term:*", 'fl': 'ma_id,ma_term,child_ma_id,child_ma_term,child_ma_idTerm,selected_top_level_ma_term,selected_top_level_ma_id'},
		 filterParams: {'fq': 'ontology_subset:IMPC_Terms'},		 
		 srchParams: $.extend({},
					commonSolrParams,
					{'fl' : 'ma_id,ma_term,child_ma_id,child_ma_term,child_ma_idTerm,selected_top_level_ma_term,selected_top_level_ma_id'})		
	 },	 
	 diseaseFacet: {			    	
		 type: 'diseases',
		 subFacetFqFields: '',
		 solrCoreName: 'disease', 
		 tableCols: 1, 
		 tableHeader: '<thead><th>Disease</th><th>Source</th><th>Curated Genes</th><th><span class="main">Candidate Genes</span><span class="sub">by phenotype</span></th></thead>', 
		 //tableHeader: '<thead><th>Disease</th><th>Source</th><th>Curated Genes</th><th>Candidate Genes</th></thead>', 
		 subset: '',
		 fq: 'type:disease',		
		 wt: 'json',
		 gridFields: 'disease_id,disease_term,disease_source,human_curated,mouse_curated,impc_predicted,impc_predicted_in_locus,mgi_predicted,mgi_predicted_in_locus',
		 gridName: 'diseaseGrid',		
		 ontology: 'disease',
		 breadCrumbLabel: 'Diseases',		 
		 //filterParams: {'fq': "ontology_subset:IMPC_Terms AND selected_top_level_ma_term:*", 'fl': 'ma_id,ma_term,child_ma_id,child_ma_term,child_ma_idTerm,selected_top_level_ma_term,selected_top_level_ma_id'},
		 filterParams: {'fq': 'type:disease'},		 
		 srchParams: $.extend({},
					commonSolrParams
					)		
	 },	
	 
	 imagesFacet: {		
		 type: 'images',		 
		 subFacetFqFields: ['expName', 'annotated_or_inferred_higherLevelMaTermName', 'annotated_or_inferred_higherLevelMpTermName', 'subtype'],
		 solrCoreName: 'images',
		 tableCols: 2, 
		 tableHeader: '<thead><th>Name</th><th>Example Images</th></thead>', 
		 fq: 'annotationTermId:M* OR expName:* OR symbol:* OR annotated_or_inferred_higherLevelMaTermName:* OR annotated_or_inferred_higherLevelMpTermName:*',		
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: 'annotationTermId,annotationTermName,expName,symbol_gene,smallThumbnailFilePath,largeThumbnailFilePath',
		 gridName: 'imagesGrid',
		 topLevelName: '',
		 imgViewSwitcherDisplay: 'Show Annotation View',
		 viewLabel: 'Image View: lists annotations to an image',
		 viewMode: 'imageView',
		 forceReloadImageDataTable: false,
		 showImgView: true,		 
		 breadCrumbLabel: 'Images',
		 filterParams: {//'fl' : 'annotationTermId,annotationTermName,expName,symbol,symbol_gene,smallThumbnailFilePath,largeThumbnailFilePath',
			 	  'fq' : "(annotationTermId:M* OR expName:* OR symbol:* OR annotated_or_inferred_higherLevelMaTermName:* OR annotated_or_inferred_higherLevelMpTermName:*)"},	
	 	 srchParams: $.extend({},
				commonSolrParams				
				)
	 } 
	 
}; 
