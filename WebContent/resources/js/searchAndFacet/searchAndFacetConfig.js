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

config.hideProcedures = true;

config.currentFq    = false;
config.matchedFacet = false;
config.lastImgSumCount = 0;

config.update = {};
config.update.filterObj = [];

config.update.mainFacetDone = false;
config.update.mainFacetDoneReset = false;
config.update.mainFacetNone = false;
config.update.rebuilt = false;

config.update.widgetOpen   = false;
config.update.pageReload   = false;
config.update.hashChange 	= false;
config.update.rebuildSummaryFilterCount = 0;
config.update.resetSummaryFacet = false;
config.update.filterAdded = false;
config.update.filterChange = false;
config.update.notFound = false;

config.searchSpin = "<img src='img/loading_small.gif' />";
config.spinner = "<img src='img/loading_small.gif' /> Processing search ...";
config.spinnerExport = "<img src='img/loading_small.gif' /> Processing data for export, please do not interrupt ... ";
config.endOfSearch = "Search result";

//custom 404 page does not know about baseUrl
var path = window.location.pathname.replace(/^\//,"");
path = '/' + path.substring(0, path.indexOf('/'));
//var trailingPath = '/searchAndFacet';
var trailingPath = '/search';
var trailingPathDataTable = '/dataTable';

config.pathname = typeof baseUrl == 'undefined' ? path + trailingPath : baseUrl + trailingPath;
config.dataTablePath = typeof baseUrl == 'undefined' ? path + trailingPathDataTable : baseUrl + trailingPathDataTable;


// on drupal side this is not available
if ( typeof solrUrl == 'undefined' ){
	solrUrl = '/data/solr';	
}

if ( typeof baseUrl == 'undefined' ){
	baseUrl = '/data';
}

if ( config.hideProcedures ){
	config.megaCores = ['gene', 'mp', 'disease', 'ma', 'images', 'impc_images'];
}
else {
	config.megaCores = ['gene', 'mp', 'disease', 'ma', 'pipeline', 'images', 'impc_images'];
}

config.geneStatuses = ['Phenotype Data Available',
               'Mice Produced',
               'Assigned for Mouse Production and Phenotyping',
               'ES Cells Produced',
               'Assigned for ES Cell Production',
               'Not Assigned for ES Cell Production'];

config.phenotypingStatuses = {
	'Complete':{'fq':'imits_phenotype_complete','val':'Phenotyping Complete'}, 
    'Started':{'fq':'imits_phenotype_started','val':'Phenotyping Started'}, 
    'Attempt Registered':{'fq':'imits_phenotype_status', 'val':'Phenotype Attempt Registered'},
    'Legacy':{'fq':'legacy_phenotype_status', 'val':'1'}
};
config.phenotypingVal2Field = {
		'Phenotyping Complete'        :'imits_phenotype_complete', 
	    'Phenotyping Started'         :'imits_phenotype_started', 
	    'Phenotype Attempt Registered':'imits_phenotype_status'
	};

config.phenotypingStatusFq2Label = {
		'imits_phenotype_complete' : 'Complete',
		'imits_phenotype_started'  : 'Started',
		'imits_phenotype_status'   : 'Attempt Registered'
};

config.summaryFilterVal2FqStr = {
		'Phenotyping Complete'         : 'latest_phenotype_status:"Phenotyping Complete"', 
	    'Phenotyping Started'          : 'latest_phenotype_status:"Phenotyping Started"', 
	    'Phenotype Attempt Registered' : 'latest_phenotype_status:"Phenotype Attempt Registered"'
};

/*

config.expName2ProcSidMapping = {
		'Dysmorphology' : 'IMPC_CSD*',
		'Eye Morphology' : 'IMPC_EYE*',
		'Flow Cytometry' : 'IMPC_FAC*',
		'Histology Slide' : 'IMPC_HIS*',
		'Wholemount Expression' : 'IMPC_ALZ*',
		'Xray' : 'IMPC_XRY*',				
		'expName:' : 'procedure_stable_id:'				
};	
config.procSid2ExpNameMapping = {
		'IMPC_CSD*' : 'Dysmorphology',
		'IMPC_EYE*' : 'Eye Morphology',
		'IMPC_FAC*' : 'Flow Cytometry',
		'IMPC_HIS*' : 'Histology Slide',
		'IMPC_ALZ*' : 'Wholemount Expression',
		'IMPC_XRY*' : 'Xray',				
		'procedure_stable_id:' : 'expName:' 				
};	
*/

config.qfield2facet = {
	'latest_phenotyping_centre'  : 'gene',	
	'latest_production_centre'   : 'gene',	
	'latest_phenotype_status'    : 'gene',
	'legacy_phenotype_status'    : 'gene',
	'status'                     : 'gene',
	'marker_type'                : 'gene',
	'top_level_mp_term'          : 'mp',
	'selected_top_level_ma_term' : 'ma',
	'procedure_stable_id'        : 'pipeline',
	
	'disease_classes' 			     : 'disease',
	'disease_source'			     : 'disease',
	'human_curated'                  : 'disease',
	'mouse_curated'                  : 'disease', 
	'impc_predicted_known_gene'      : 'disease',
	'mgi_predicted_known_gene'       : 'disease',
	'impc_predicted'                 : 'disease',
	'impc_novel_predicted_in_locus'  : 'disease',
	'mgi_predicted'                  : 'disease',
	'mgi_novel_predicted_in_locus'   : 'disease',
	
	'img_marker_type'                : 'images',
	'img_top_level_mp_term'          : 'images',
	'img_selected_top_level_ma_term' : 'images',
	'img_procedure_name'             : 'images',
	
	//'impcImg_marker_type'                : 'images',
	//'impcImg_top_level_mp_term'          : 'images',
	//'impcImg_selected_top_level_ma_term' : 'images',
	'impcImg_procedure_name'             : 'impc_images'
		
}
config.facetFilterLabel = {
	'phenotyping_center'         	 : 'phenotyping_center',
	'production_center'          	 : 'production_center',
	'imits_phenotype_complete'   	 : 'phenotyping',
	'imits_phenotype_started'    	 : 'phenotyping',
	'imits_phenotype_status'         : 'phenotyping',
	'status'                         : 'mouse_production_status',
	'marker_type'                    : 'gene_subtype',
	'top_level_mp_term'              : 'top_level_term',
	'selected_top_level_ma_term'     : 'top_level_term',
	'procedure_stable_id'            : 'procedure',
	'ma'					         : 'anatomy',
	'annotated_or_inferred_higherLevelMaTermName' : 'anatomy',
	'mp'      					     : 'phenotype',
	'annotatedHigherLevelMpTermName' : 'phenotype',
	'expName'                        : 'procedure',
	'subtype'                        : 'gene_subtype',	
	//'disease_classes' 			     : 'disease_classification',
	//'disease_source'			     : 'disease_source',
	'human_curated'                  : 'From human data (OMIM, Orphanet)',//'human_data',
	'mouse_curated'                  : 'From mouse data (MGI)', //'mouse_data',
	'impc_predicted_known_gene'      : 'From human data with IMPC prediction',
	'mgi_predicted_known_gene'       : 'From human data with MGI prediction',
	'impc_predicted'                 : 'From IMPC data',
	'impc_novel_predicted_in_locus'  : 'Novel IMPC prediction in linkage locus',
	'mgi_predicted'                  : 'From MGI data',
	'mgi_novel_predicted_in_locus'   : 'Novel MGI prediction in linkage locus'
};

var commonSolrParams = {					
		'qf': 'auto_suggest',
		'defType': 'edismax',
		'wt': 'json'
		//'rows': 0
};

config.coreQf = {
	gene     : "mgi_accession_id marker_symbol marker_name marker_synonym",
	mp       : "mp_id mp_term mp_term_synonym top_level_mp_id top_level_mp_term top_level_mp_term_synonym intermediate_mp_id intermediate_mp_term intermediate_mp_term_synonym child_mp_id child_mp_term child_mp_term_synonym",
	disease  : "disease_id disease_term disease_alts",
	ma 	     : "ma_id ma_term child_ma_term selected_top_level_ma_term",
	pipeline : "auto_suggest",
	images   : "auto_suggest",
	impc_images   : "auto_suggest",
}

config.commonSolrParams = commonSolrParams;

config.facetParams = {	
	geneFacet:      {
		type: 'genes',	
		name: 'Genes',
		subFacetFqFields: [
		                   'latest_phenotype_status',
		                   'legacy_phenotype_status',
		                   'status',
		                   'latest_production_centre',
		                   'latest_phenotyping_centre',
		                   'marker_type'],
		solrCoreName: 'gene',			 
		tableCols: 3, 	
		tableHeader: "<thead><th>Gene</th><th>Production Status</th><th>Phenotype Status</th><th></th></thead>",		
		fq: '*:*',
		//fq: 'marker_type:* -marker_type:"heritable phenotypic marker"',//undefined,
		//fq: 'marker_type:* -marker_type:"heritable phenotypic marker" (production_center:* AND phenotyping_center:*)',//undefined,
		//centerFq: 'marker_type:* -marker_type:"heritable phenotypic marker" AND (production_center:* AND phenotyping_center:*)',
		//qf: "marker_symbol^100.0 human_gene_symbol^90.0 marker_name^10.0 marker_synonym mgi_accession_id auto_suggest",
		qf:"auto_suggest",
		gridName: 'geneGrid',
		gridFields: '*', 
		//filterParams: {fq:'marker_type:* -marker_type:"heritable phenotypic marker" (production_center:* AND phenotyping_center:*)',	
		filterParams: {fq:'*:*',	
			      //qf:"marker_symbol^100.0 human_gene_symbol^90.0 marker_name^10.0 marker_synonym mgi_accession_id auto_suggest",
			      qf:"auto_suggest",
			      bq:'latest_phenotype_status:"Phenotyping Complete"^200 marker_type:"protein coding gene"^100'},
		srchParams : $.extend({},				
				 	commonSolrParams,
				 	{'fl': 'marker_symbol,mgi_accession_id,marker_synonym,marker_name,marker_type,human_gene_symbol,latest_es_cell_status,latest_production_status,latest_phenotype_status,status,es_cell_status,mouse_status,allele_name,legacy_phenotype_status'} 	 	
					),
		subFacet_filter_params: '', // set by widget on the fly
		breadCrumbLabel: 'Genes'		
	 },	 
	 pipelineFacet: {	
		 type: 'parameters',
		 name: 'Procedures',
		 subFacetFqFields: ['pipeline_name', 'pipe_proc_sid'], 		 
		 solrCoreName: 'pipeline',			
		 tableCols: 3, 
		 tableHeader: '<thead><th>Parameter</th><th>Procedure</th><th>Pipeline</th></thead>',		
		 fq: 'pipeline_stable_id:*', //"pipeline_stable_id:IMPC_001", 
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: 'parameter_name,procedure_name,pipeline_name',
		 gridName: 'pipelineGrid',	
		 //filterParams:{'fq': 'pipeline_stable_id:IMPC_001'},
		 breadCrumbLabel: 'Parameters',	
		 filterParams: {fq:'pipeline_stable_id:*'},	 
		 srchParams: $.extend({},				     
					commonSolrParams, {'fl': 'parameter_name,procedure_name,procedure_stable_key,pipeline_name'}   				
					)					
	 },	
	 mpFacet: {	
		 type: 'phenotypes',
		 name: 'Phenotypes',
		 subFacetFqFields: ['top_level_mp_term'],
		 solrCoreName: 'mp', 
		 tableCols: 3, 
		 tableHeader: '<thead><th>Phenotype</th><th>Definition</th><th>Phenotyping call(s)</th><th></th></thead>', 
		 subset: 'ontology_subset:*',
		 //fq: 'ontology_subset:*', 
		 fq: 'top_level_mp_term:*', 
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: 'ma_term,ma_id',
		 gridName: 'mpGrid',
		 topLevelName: '',
		 ontology: 'mp',
		 breadCrumbLabel: 'Phenotypes',		
		 //filterParams: {'fq': 'ontology_subset:IMPC_Terms'},
		 //filterParams: {'fq': 'ontology_subset:*'},
		 filterParams: {'fq': 'top_level_mp_term:*'},
		 srchParams: $.extend({},				
					commonSolrParams,	 	
					{'fl': 'mp_id,mp_term,mp_term_synonym,mp_definition,top_level_mp_term,top_mp_term_id,intermediate_mp_term,intermediate_mp_id,intermediate_mp_definition,hp_id,hp_term,postqc_calls'})
	 },	
	 maFacet: {			    	
		 type: 'tissues',
		 name: 'Anatomy',
		 subFacetFqFields: ['selected_top_level_ma_term'],
		 solrCoreName: 'ma', 
		 tableCols: 1, 
		 tableHeader: '<thead><th>Anatomy</th></thead>', 
		 subset: 'ontology_subset:IMPC_Terms',
		 fq: 'selected_top_level_ma_term:*', 
		 //fq: 'ontology_subset:IMPC_Terms AND selected_top_level_ma_term:*', 
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
		 //filterParams: {'fq': 'ontology_subset:IMPC_Terms AND selected_top_level_ma_term:*'},
		 filterParams: {'fq': 'selected_top_level_ma_term:*'},	
		 srchParams: $.extend({},
					commonSolrParams,
					{'fl' : 'ma_id,ma_term,ma_term_synonym,child_ma_id,child_ma_term,child_ma_idTerm,selected_top_level_ma_term,selected_top_level_ma_id'})		
	 },	 
	 diseaseFacet: {			    	
		 type: 'diseases',
		 name: 'Diseases',
		 subFacetFqFields: [
		                    'disease_source',
		                    'disease_classes',
		                    'human_curated',
		                    'mouse_curated', 
		                    'impc_predicted_known_gene', 
		                    'mgi_predicted_known_gene', 
		                    'impc_predicted', 
		                    'impc_novel_predicted_in_locus', 
		                    'mgi_predicted', 
		                    'mgi_novel_predicted_in_locus'],
		 solrCoreName: 'disease', 
		 tableCols: 1, 
		 tableHeader: '<thead><th>Disease</th><th>Source</th><th>Curated Genes</th><th><span class="main">Candidate Genes</span><span class="sub">by phenotype</span></th></thead>', 
		 subset: '',
		 qf: 'auto_suggest', 
		 fq: '*:*',
		 wt: 'json',
		 gridFields: 'disease_id,disease_term,disease_source,disease_classes,human_curated,mouse_curated,impc_predicted_known_gene,mgi_predicted_known_gene,impc_predicted,impc_novel_predicted_in_locus,mgi_predicted,mgi_novel_predicted_in_locus,marker_symbol,mgi_accession_id',
		 gridName: 'diseaseGrid',		
		 ontology: 'disease',
		 breadCrumbLabel: 'Diseases',		 
		 //filterParams: {'fq': "ontology_subset:IMPC_Terms AND selected_top_level_ma_term:*", 'fl': 'ma_id,ma_term,child_ma_id,child_ma_term,child_ma_idTerm,selected_top_level_ma_term,selected_top_level_ma_id'},
		 //filterParams: {'fq': 'type:disease'},
		 filterParams: {'fq': '*:*', 'fl': 'disease_id,disease_term,disease_source,disease_classes,human_curated,mouse_curated,impc_predicted_known_gene,mgi_predicted_known_gene,impc_predicted,impc_novel_predicted_in_locus,mgi_predicted,mgi_novel_predicted_in_locus,marker_symbol,mgi_accession_id'},
		 srchParams: $.extend({},
					commonSolrParams
					)		
	 },	
	 
	 imagesFacet: {		
		 type: 'images',
		 name: 'Images',
		 subFacetFqFields: ['procedure_name', 'top_level_mp_term', 'selected_top_level_ma_term', 'marker_type'],
		 solrCoreName: 'images',
		 tableCols: 2, 
		 tableHeader: '<thead><th>Name</th><th>Example Images</th></thead>', 
		 //fq: 'annotationTermId:M* OR expName:* OR symbol:*',	
		 //fq: 'top_level_mp_term:* OR selected_top_level_ma_term:* OR procedure_name:* OR marker_symbol:*',
		 fq: '*:*',
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: 'annotationTermId,annotationTermName,expName,symbol_gene,smallThumbnailFilePath,largeThumbnailFilePath',
		 gridName: 'imagesGrid',
		 topLevelName: '',
		 /*imgViewSwitcherDisplay: 'Show Annotation View',
		 viewLabel: 'Image View: lists annotations to an image',
		 viewMode: 'imageView',
		 showImgView: true,	*/
		 imgViewSwitcherDisplay: 'Show Image View',
		 viewLabel: 'Annotation View: groups images by annotation',
		 viewMode: 'annotView',
		 showImgView: false,		 
		 forceReloadImageDataTable: false,		 
		 breadCrumbLabel: 'Images',
		 filterParams: {'fl' : 'annotationTermId,annotationTermName,expName,symbol,symbol_gene,smallThumbnailFilePath,largeThumbnailFilePath'
			 	  //'fq' : "(top_level_mp_term:* OR selected_top_level_ma_term:* OR procedure_name:* OR marker_symbol:*)"
			 //'fq' : '*:*'
		 },	
	 	 srchParams: $.extend({},
				commonSolrParams				
				)
	 },
	 impc_imagesFacet: {		
		 type: 'impc_images',
		 name: 'impc_Images',
		 subFacetFqFields: ['procedure_name'],
		 solrCoreName: 'impc_images',
		 tableCols: 2, 
		 tableHeader: '<thead><th>Name</th><th>Example Images</th></thead>', 
		 //fq: 'annotationTermId:M* OR expName:* OR symbol:*',	
		 //fq: 'top_level_mp_term:* OR selected_top_level_ma_term:* OR procedure_name:* OR marker_symbol:*',
		 fq: '*:*',
		 qf: 'auto_suggest', 
		 defType: 'edismax',
		 wt: 'json',
		 gridFields: 'omero_id,procedure_name, gene_symbol, gene_accession_id, jpeg_url',
		 gridName: 'impc_imagesGrid',
		 topLevelName: '',
		 /*imgViewSwitcherDisplay: 'Show Annotation View',
		 viewLabel: 'Image View: lists annotations to an image',
		 viewMode: 'imageView',
		 showImgView: true,	*/
		 imgViewSwitcherDisplay: 'Show Image View',
		 viewLabel: 'Annotation View: groups images by annotation',
		 viewMode: 'annotView',
		 showImgView: false,		 
		 forceReloadImageDataTable: false,		 
		 breadCrumbLabel: 'IMPC_images',
		 filterParams: {'fl' : 'omero_id,procedure_name,gene_symbol,gene_accession_id,jpeg_url,parameter_association_name,parameter_association_value,allele_symbol'
			 	  //'fq' : "(top_level_mp_term:* OR selected_top_level_ma_term:* OR procedure_name:* OR marker_symbol:*)"
			 //'fq' : '*:*'
		 },	
	 	 srchParams: $.extend({},
				commonSolrParams				
				)
	 } 
	 
}; 




