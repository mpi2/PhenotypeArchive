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
 * sideBarFacetWidget: based on the results retrieved by the autocompleteWidget
 * and displays the facet results on the left bar.
 * 
 */
(function ($) {
	'use strict';
    $.widget('MPI2.mpi2LeftSideBar', {
        
	    options: {	    	
    		mpAnnotSources: ['empress', 'mgi'],    		
    		solrBaseURL_bytemark: MPI2.searchAndFacetConfig.solrBaseURL_bytemark,			
			solrBaseURL_ebi: MPI2.searchAndFacetConfig.solrBaseURL_ebi,			
			commonParams: {
							'qf': 'auto_suggest',
				 			'defType': 'edismax',
				 			'wt': 'json'				 				 			
				 			}			
	    },    
			    
    	_create: function(){
    		// execute only once 	
    		var self = this;	
    		
			$('div.facetCat').click(function(){
				
				$('div.facetCat').removeClass('facetCatUp');
				if ( $(this).parent().siblings('.facetCatList').is(':visible') ){					
					$('div.facetCatList').hide(); // collapse all other facets                     
					$(this).parent().siblings('.facetCatList').hide(); // hide itself					
				}
				else {
					$('div.facetCatList').hide(); // collapse all other facets 
					$(this).parent().siblings('.facetCatList').show(); // show itself					
					$(this).addClass('facetCatUp');
					var facetDivId = $(this).parent().parent().attr('id');	
					var gridName = MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName;
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
					//var type = MPI2.searchAndFacetConfig.facetParams[facetDivId].type;
					var solrSrchParams = {};
					var hashParams = {};
					
					// also triggers SOP/gene/MP dataTable depending on what facet is clicked
					if (facetDivId == 'geneFacet'){
						solrSrchParams = $.extend({}, self.options.commonParams, 
								MPI2.searchAndFacetConfig.facetParams[facetDivId].params);
						
						if (self.options.geneSubFacet_filter_params){
							solrSrchParams.fq = self.options.geneSubFacet_filter_params;							
						}
	                }					
					else {						
						solrSrchParams = $.extend({}, self.options.commonParams, MPI2.searchAndFacetConfig.facetParams[facetDivId].params);						
					}
					
					solrSrchParams.q = self.options.data.q; 
					
					// for images, qf is either auto_suggest or text_search depending on query string
					if (facetDivId == 'imagesFacet' && solrSrchParams.q.indexOf('*') == -1 ){
						solrSrchParams.qf = 'text_search';
					}	
					
					hashParams.q = self.options.data.q;
					hashParams.core = solrCoreName;
					hashParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
					// hash state stuff										
					window.location.hash = $.fn.stringifyJsonAsUrlParams(hashParams);// + "&core=" + solrCoreName;
					
					// dataTable code					
					//console.log('name: ' + MPI2.searchAndFacetConfig.facetParams[facetDivId].topLevelName);
					if ( $('table#'+ gridName).size() != 1 ){
						self._invokeFacetDataTable(solrSrchParams, facetDivId, gridName);
					}	
				}								
			});	
						
			// click on facetCount to fetch results in grid
			$('span.facetCount').click(function(){								
		    	console.log('facet count') 
				var facetDivId = $(this).parent().parent().attr('id');
				var gridName = MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName;
				var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
				var solrSrchParams = {}
				var hashParams = {};
				
				// remove highlight from selected 
				if ( facetDivId == 'geneFacet' ){
					$('table#gFacet td').removeClass('highlight');
					if (self.options.geneSubFacet_filter_params){
						//solrSrchParams.fq = self.options.geneSubFacet_filter_params;
						solrSrchParams = $.extend({}, self.options.commonParams, 
								MPI2.searchAndFacetConfig.facetParams[facetDivId].params, 
								self.options.geneSubFacet_filter_params);
					}				
				}
				else if (facetDivId == 'pipelineFacet' ){
					$('table#pipeline td[class^=procedure]').removeClass('highlight');					
					solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams[facetDivId].params, self.options.commonParams);					
					MPI2.searchAndFacetConfig.facetParams[facetDivId].params.fq = "pipeline_stable_id:IMPC_001";					
				}	
				else if (facetDivId == 'mpFacet' ){				
					$('table#mpFacet td').removeClass('highlight');
					solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams[facetDivId].params, self.options.commonParams);
					MPI2.searchAndFacetConfig.facetParams[facetDivId].params.fq = "ontology_subset:*";					
				}
				else if (facetDivId == 'imagesFacet' ){    	    			
					$('table#imgFacet td').removeClass('highlight');
					solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams[facetDivId].params, self.options.commonParams);					
				}
				
				solrSrchParams.q = self.options.data.q;
								
				// for images, qf is either auto_suggest or text_search depending on query string
				if (facetDivId == 'imagesFacet' && solrSrchParams.q.indexOf('*') == -1 ){					
					solrSrchParams.qf = 'text_search';
				}					
								
				hashParams.q = self.options.data.q;
				hashParams.core = solrCoreName;
				hashParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
				
				// hash state stuff				   
				window.location.hash = $.fn.stringifyJsonAsUrlParams(hashParams);// + "&core=" + solrCoreName;
				
				// dataTable code							
				self._invokeFacetDataTable(solrSrchParams, facetDivId, gridName); 
								
			});	
    	},
    	    	
    	_applyFacetTopLevelFilter: function(facetDivId){
    		var self = this;  
    		var sTopLevelTerm = null;
    		var oSelectedTopLevel = $('div#'+ facetDivId + ' .facetTable td.highlight'); 
    		var obj = oSelectedTopLevel.siblings().find('a');    				    			
    		self._fetchFilteredDataTable(obj, facetDivId);     		
    	},
    	
    	 _invokeFacetDataTable: function(oSolrSrchParams, facetDivId, gridName, hashState){
    		
 	    	var self = this;
 	    	var oVal = MPI2.searchAndFacetConfig.facetParams[facetDivId];
 	    	if ( hashState ){
 	    		oVal.forceReloadImageDataTable = true;
 	    	}
 	    	
 	    	var sTopLevelTerm = oVal.topLevelName;
 	    	//console.log('load ' + facetDivId + ' -> ' + typeof sTopLevelTerm); 	   
 	    	//console.log(sTopLevelTerm == undefined);
	 	    if ( $('div#'+ facetDivId + ' .facetTable td.highlight').size() == 1 && sTopLevelTerm != '' && !oVal.forceReloadImageDataTable){
	 	    	// check if top level name in facet has been selected before for the current facet
	 	 	   	// if yes, filter top level set with the selected	 	    	
	 	    	self._applyFacetTopLevelFilter(facetDivId); 	    		
	 	    }
	 	    else if ( $('table#' + gridName).size() == 1 
	 	    		&&  $('div#'+ facetDivId + ' .facetTable td.highlight').size() == 0	 	    		
	 	    		&& sTopLevelTerm == undefined
	 	    		&& !oVal.forceReloadImageDataTable ){	
	 	    	//console.log('here');
	 	    	return;
	 	    }	 	    
	 	    else {	 	    	
	 	    	// no filtering
	 	    	oVal.topLevelName = ''; // back to default
	 	    	
		 	   	var oInfos = {};
		 		oInfos.params = $.fn.stringifyJsonAsUrlParams(oSolrSrchParams);
		 		oInfos.solrCoreName = oVal.solrCoreName;
		 		oInfos.mode = oVal.gridName;
		 		//oInfos.dataTablePath = baseUrl + '/dataTable';	
		 		oInfos.dataTablePath = MPI2.searchAndFacetConfig.dataTablePath;	 		
		 		
		 	   	var dTable = $('<table></table>').attr({'id':oInfos.mode});	    		    	
		 	   	var thead = oVal.tableHeader;
		 	   	var tds = '';
		 	   	for (var i=0; i<oVal.tableCols; i++){
		 	   		tds += "<td></td>";
		 	   	}
		 	   	var tbody = $('<tbody><tr>' + tds + '</tr></tbody>');
		 	   	dTable.append(thead, tbody);
		 	    	
		 	   	var title = $.fn.upperCaseFirstLetter(oVal.type);	    	
		 	   	var gridTitle = $('<div></div>').attr({'class':'gridTitle'}).html(title);
		 	   	
		 	   	$('div#mpi2-search').html('');	
		 	   	
		 	   	if (facetDivId == 'imagesFacet'){		 	   		
					
					// toggles two types of views for images: annotation view, image view
		 	   		var viewLabel, viewMode;
		 	   		if ( oVal.imgViewSwitcherDisplay == 'Annotation View' ){
		 	   			viewLabel = 'Image View: lists annotations to an image';
		 	   			viewMode = 'imageView';
		 	   		} 
		 	   		else {		 	   			
		 	   			viewLabel = 'Annotation View: groups images by annotation';
		 	   			viewMode = 'annotView';		 	   		
		 	   		}
		 	   		
		 	   		if (oVal.showImgView){
		 	   			dTable.find('th:nth-child(2)').text("Image");		 	   			
		 	   		}
		 	   		
		 	   		gridTitle.append($('<div></div>').attr({'id':'imgView','rel':viewMode}).html("<span id='imgViewSubTitle'>"		 	   		
		 	   				+ viewLabel 
		 	   				+ "</span><span id='imgViewSwitcher'>Show " 
		 	   				+ oVal.imgViewSwitcherDisplay + "</span>"));
		 	   		
		 	   		$('div#mpi2-search').append(gridTitle, dTable);
		 	   		
		 	   		oInfos.showImgView = oVal.showImgView;		 	   		
		 	   		$.fn.invokeDataTable(oInfos);
		 	   		
		 	   		$('span#imgViewSwitcher').click(function(){		 	   			
		 	   			if (oVal.imgViewSwitcherDisplay == 'Annotation View'){
		 	   				oVal.imgViewSwitcherDisplay = 'Image View';
		 	   				oVal.showImgView = false;		 	   				
		 	   			}
		 	   			else {
		 	   				oVal.imgViewSwitcherDisplay = 'Annotation View';		 	   				
		 	   				oVal.showImgView = true;
		 	   			}		 	   			
		 	   			
		 	   			oVal.forceReloadImageDataTable = true;
		 	   					 	   			
		 	   			if ( !oVal.showImgView ){
		 	   				delete(oSolrSrchParams['fq']); // remove default as specific fq will be added later depending on data type		 	   			
		 	   			}
		 	   			
		 	   			self._invokeFacetDataTable(oSolrSrchParams, facetDivId, gridName);
		 	   		})
		 	   	}
		 	   	else {		 	   			 	  
		 	   		$('div#mpi2-search').append(gridTitle, dTable);		 	   	
		 	   		$.fn.invokeDataTable(oInfos);
		 	   	}
	 	    } 	    	
 	    },
 	        	
	    // want to use _init instead of _create to allow the widget being called each time
	    _init: function () {
			var self = this;
			
	    	self._doGeneSubTypeFacet();	    	
	    	self._doMPFacet();
	    	//self._doMAFacet();
			self._doPipelineFacet();
			self._doImageFacet();
			
			self._openFacet();   
	    },
	    
	    _openFacet: function(){
	    	var self = this;
	    	
	    	$('div.facetCatList').hide();
	    	$('div.facetCat').removeClass('facetCatUp');
	    	
	    	var core = self.options.data.core;
	    	
	    	var subCat = self.options.data.subCat;
	    	
	    	// priority order of facet to be opened based on search result
	    	if (core == 'gene'){	    		
	    		$('div#geneFacet div.facetCatList').show();
	    		$('div#geneFacet div.facetCat').addClass('facetCatUp'); 	  		
	    	}	
	    	else if (core == 'mp'){
	    		$('div#mpFacet div.facetCatList').show();		
	    		$('div#mpFacet div.facetCat').addClass('facetCatUp'); 
	    	}
	    	else if (core == 'pipeline'){
	    		$('div#pipelineFacet div.facetCatList').show();	
	    		$('div#pipelineFacet div.facetCat').addClass('facetCatUp'); 
	    	}
	    	else if (core == 'images'){
	    		$('div#imagesFacet div.facetCatList').show();	
	    		$('div#imagesFacet div.facetCat').addClass('facetCatUp'); 
	    	}	    	
	    },	   
	    
		_doGeneSubTypeFacet: function(){
	    	var self = this;
	    	
			var solrURL = self.options.solrBaseURL_bytemark + 'gene/select';
	    	//var queryParams = $.extend({},{		
	    	var queryParams = $.extend({}, { 
				'rows': 0,
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.field': 'marker_type',				
				'facet.sort': 'count',					
	    		'q': self.options.data.q},
	    		self.options.commonParams,
	    		MPI2.searchAndFacetConfig.facetParams.geneFacet.params
	    	);
	    	
	    	var queryParamStr = $.fn.stringifyJsonAsUrlParams(queryParams) + '&facet.field=status';
	    	
	    	$.ajax({ 				 					
	    		'url': solrURL,
	    		'data': queryParamStr, 
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) { 
	    			self._displayGeneSubTypeFacet(json);	    				
	    		}		
	    	});	    	
	    },
	    
	    _displayGeneSubTypeFacet: function(json){
	    	
	    	var self = this;
	    	var numFound = json.response.numFound;
	    	
	    	// update this if facet is loaded by redirected page, which does not use autocomplete
	    	$('div#geneFacet span.facetCount').attr({title: 'total number of unique genes'}).text(numFound);
	    	
	    	/*-------------------------------------------------------*/
	    	/* ------ displaying sidebar and update dataTable ------ */
	    	/*-------------------------------------------------------*/
	    	
	    	if (numFound > 0){
	    		var unclassified_gene_subType;
	    		var trs = "<tr class='facetSubCat'><td colspan=2>Subtype</td></tr>";
	    		var mkr_facets = json.facet_counts['facet_fields']['marker_type'];
	    		for ( var i=0; i<mkr_facets.length; i+=2 ){		    			
	    			//console.log( facets[i] + ' ' + facets[i+1]);
					var type = mkr_facets[i];
					var count = mkr_facets[i+1];			
					if ( type == 'unclassified gene' ){					
						//unclassified_gene_subType = "<tr><td class='geneSubtype'>" + type + "</td><td rel='" + type + "' class='geneSubtypeCount'><a rel='" + type + "' class='subtype'>" + count + "</a></td></tr>";
						unclassified_gene_subType = "<tr><td class='geneSubtype geneSubfacet'>" + type + "</td><td rel='" + type + "' class='geneSubfacetCount'><a rel='" + type + "' class='subtype'>" + count + "</a></td></tr>";
						
						//unclassified_gene_subType = "<tr><td class='geneSubtype geneSubfacet'>" + type + "</td><td rel='subtype' class='geneSubfacetCount'><a rel='" + type + "'>" + count + "</a></td></tr>";	
					}
					else {
						//trs += "<tr><td class='geneSubtype'>" + type + "</td><td rel='" + type + "' class='geneSubtypeCount'><a rel='" + type + "' class='subtype'>" + count + "</a></td></tr>";
						trs += "<tr><td class='geneSubtype geneSubfacet'>" + type + "</td><td rel='" + type + "' class='geneSubfacetCount'><a rel='" + type + "' class='subtype'>" + count + "</a></td></tr>";
						
						//trs += "<tr><td class='geneSubtype geneSubfacet'>" + type + "</td><td rel='subtype' class='geneSubfacetCount'><a rel='" + type + "'>" + count + "</a></td></tr>";						
					}
	    		} 
	    		if ( unclassified_gene_subType ){
	    			trs += unclassified_gene_subType
	    		}
	    		
	    		trs += "<tr class='facetSubCat'><td colspan=2>Status</td></tr>";
	    		var status_facets = json.facet_counts['facet_fields']['status'];
	    		var status_count = {};
	    		for ( var i=0; i<status_facets.length; i+=2 ){		    			
	    			//console.log( facets[i] + ' ' + facets[i+1]);
					var type = status_facets[i];
					var count = status_facets[i+1];
					status_count[type] = count; 
	    		}    			
				
				for ( var i=0; i<MPI2.searchAndFacetConfig.geneStatuses.length; i++ ){
					var status = MPI2.searchAndFacetConfig.geneStatuses[i];
					var count = status_count[MPI2.searchAndFacetConfig.geneStatuses[i]];
					
					if ( count !== undefined ){
						//trs += "<tr><td class='geneStatus'>" + status + "</td><td rel='" + status + "' class='geneStatusCount'><a rel='" + status + "'>" + count + "</a></td></tr>";
						trs += "<tr><td class='geneStatus geneSubfacet'>" + status + "</td><td rel='" + status + "' class='geneSubfacetCount'><a rel='" + status + "' class='status'>" + count + "</a></td></tr>";
						
						//trs += "<tr><td class='geneStatus geneSubfacet'>" + status + "</td><td rel='status' class='geneSubfacetCount'><a rel='" + status + "'>" + count + "</a></td></tr>";
					}					
				}	    		
	    		
	    		var table = "<table id='gFacet' class='facetTable'>" + trs + "</table>";				
	    		$('div#geneFacet div.facetCatList').html(table);
	    		
	    		self._applyGeneGridResultFilterByMarkerSubFacet($('table#gFacet td.geneSubfacetCount a')); 
				//self._applyGeneGridResultFilterByMarkerSubType($('table#gFacet td.geneSubtypeCount a'));	
				//self._applyGeneGridResultFilterByMarkerSubType($('table#gFacet td.geneStatusCount a'));    		
    		}
	    	
	    	/*---------------------------------------------*/
	    	/* ------ Reload sidebar for hash state ------ */
	    	/*---------------------------------------------*/
	    	
	    	//console.log('hash str: ' + window.location.hash);
	    	//console.log(self.options.data.fq);
	    	//console.log(typeof self.options.data.fq);
	    	
	    	if ( self.options.data.core == 'gene' && self.options.data.fq != undefined  ){	    		
	    		//console.log('fq: '+ self.options.data.fq);
	    		var subFacet = self.options.data.fq.replace(/\w+:/, '').replace(/"/g,'');
	    		//console.log('gene filtered: '+ subFacet);
	    		var obj = $('div#geneFacet div.facetCatList').find("table#gFacet td[rel='" + subFacet + "']").find('a');	
	    			    			    		
	    		self._fetchFilteredDataTable(obj, 'geneFacet');
	    	}
	    	else if ( self.options.data.core == 'gene' && self.options.data.fq == undefined ){
	    		//console.log('gene UNfiltered');
	    		var solrSrchParams = $.extend({}, self.options.commonParams, MPI2.searchAndFacetConfig.facetParams['geneFacet'].params);
	    		//var hashParams = $.fn.parseHashString(window.location.hash.substring(1));
	    		//solrSrchParams.q = hashParams.q;
    			solrSrchParams.q = self.options.data.q;	
    			
	    		self._invokeFacetDataTable(solrSrchParams, 'geneFacet', MPI2.searchAndFacetConfig.facetParams['geneFacet'].gridName); 
	    	}
	    },

		_applyGeneGridResultFilterByMarkerSubFacet: function(obj){
			var self = this;

			obj.click(function(){
				// invoke dataTable				
				self._fetchFilteredDataTable($(this), 'geneFacet');			
			});
		},
		
		_doMAFacet: function(){
	    	var self = this;
	    	var solrURL = self.options.solrBaseURL_ebi + 'ma/select';	    	
	    	var queryParams = $.extend({}, {				
				'rows': 0,
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,				
				'facet.sort': 'index',					
				'q.option': 'AND',
				'q': self.options.data.q}, self.options.commonParams);
	    	
	    	var p = queryParams;
	    	var aSolrParams = [];
	    	for( var i in p ){        		
        		aSolrParams.push(i + '=' + p[i]);
        	}
	    	var params = aSolrParams.join('&') + '&facet.field=top_level_ma_term&facet.field=top_level_ma_term_part_of';
	    		    	
	    	$.ajax({	
	    		'url': solrURL,
	    		'data': params,						
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {	   
	    			$('div#tissueFacet span.facetCount').text(self.options.data.maFound);  
	    			self._makeMAFacetTable(json);			
	    		}		
	    	});		    	
	    },
	    
	    _makeMAFacetTable: function(json){
	    	var self = this;
	    	
	    	var numFound = json.response.numFound;  
	    	$('div#tissueFacet span.facetCount').text(numFound);
	    	
	    	var aTopLevelCount = self._processMAFacetJson(json);
	    	
	    	var table = $("<table id='maFacet' class='facetTable'></table>");
	    	
	    	// top level MA terms	    	
	    	var counter = 0;
	    	for ( var i in aTopLevelCount ){
	    		counter++;
    			var tr = $('<tr></tr>').attr({'rel':i, 'id':'topLevelMaTr'+counter});    			
	    		var td1 = $('<td></td>').attr({'class': 'maTopLevel'}).text(i);	    		
	    		
	    		var a = $('<a></a>').attr({'rel':i}).text(aTopLevelCount[i]);
	    		var td2 = $('<td></td>').attr({'class': 'maTopLevelCount'}).append(a);
	    		table.append(tr.append(td1, td2)); 
	    	}
			self._displayOntologyFacet(json, 'ma', 'tissueFacet', table);	
	    },
	    
	    _processMAFacetJson: function(json){
	    	var self = this;
	    	var aTopLevelCount = {};
	    	var aFields = ['top_level_ma_term_part_of','top_level_ma_term'];
	    	for (var i=0; i<aFields.length; i++){	    		
	    		var ff = json.facet_counts.facet_fields[aFields[i]];
	    			    	
	    		for ( var j=0; j<ff.length; j++ ){	    			
	    			if ( !aTopLevelCount[ff[j]] ){
	    				aTopLevelCount[ff[j]] = ff[j+1];
	    			}	
	    			else {
	    				if ( aTopLevelCount[ff[j]] < ff[j+1] ){
	    					aTopLevelCount[ff[j]] = ff[j+1]
	    				} 
	    			}
	    			j++;
	    		}
	    	}	
	    	return aTopLevelCount;    	
	    },
	    
		_doPipelineFacet: function(){
	    	var self = this;
	    	var aProcedure_names = [];	    	
	    	var solrURL = self.options.solrBaseURL_ebi + 'pipeline/select';	    	
	    	var queryParams = $.extend({}, {	    		  		
	    		'fq': 'pipeline_stable_id=IMPC_001',				
				'rows': 500000,
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.field': 'procedure_name', //proc_param_name',
				'facet.sort': 'index',
				'fl': 'parameter_name,parameter_stable_key,parameter_stable_id,procedure_name,procedure_stable_key,procedure_stable_id',						
				'q': self.options.data.q}, self.options.commonParams);	    		    	
	    	
	    	//console.log(queryParams);
	    	$.ajax({ 				 					
	    		'url': solrURL,
	    		'data': queryParams,
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) { 
	    			//console.log(json);
	    			
	    			// update this if facet is loaded by redirected page, which does not use autocomplete
	    			$('div#pipelineFacet .facetCount').attr({title: 'total number of unique parameter terms'}).text(json.response.numFound);
	        			        		
	    			var procedures_params = {};
	    			var facetCountSum = 0;
	    			
	    			var mappings = self._doNames2IdMapping(json.response);
	    			
	    			var procedureName2IdKey = mappings[0]; // stable_id
	    			var parameterName2IdKey = mappings[1]; // stabley_key
	    			
	    			//var facets = json.facet_counts['facet_fields']['proc_param_name'];	    			
	    			var facets = json.facet_counts['facet_fields']['procedure_name'];
	    			
	    			var table = $("<table id='pipeline' class='facetTable'></table>");
	        		var trCat = $('<tr></tr>').attr({'class':'facetSubCat'});
	        		table.append(trCat.append( $('<td></td>').attr({'colspan':2}).text('IMPC')));
	    			
	    			for ( var f=0; f<facets.length; f+=2 ){       			
	        			
	        			var procedure_name = facets[f];
	        			var paramCount = facets[f+1];
	        				        			
	        			var pClass = 'procedure'+f;
	        			var tr = $('<tr></tr>');
	        			var td1 = $('<td></td>').attr({'class': pClass});	        			
	        			var td2 = $('<td></td>');	        			        			
	        			var a = $('<a></a>').attr({'class':'paramCount', 'rel': procedureName2IdKey[procedure_name].stable_id}).text(paramCount);
	        			table.append(tr.append(td1.text(procedure_name), td2.append(a)));
	        		}	     
	    			
	    			/*
	        		for ( var f=0; f<facets.length; f+=2 ){	        			
	        			var names = facets[f].split('___');
	        			var procedure_name = names[0];
	        			
	        			console.log(procedureName2IdKey[procedure_name])
	        			var parameter_name = names[1];
	        			console.log(parameterName2IdKey[parameter_name])
	        			var count = facets[f+1];
	        				        				        				        			
	        			if ( !procedures_params[procedure_name] ){	        				
	        				procedures_params[procedure_name] = [];
	        			}	
	        			procedures_params[procedure_name].push({param_name : parameter_name,
	        													param_count: count});	        		
	        		}	      		       		
	        			        		
	        		//var table = $("<table id='pipeline' class='facetTable'><caption>IMPC</caption></table>");
	        		var table = $("<table id='pipeline' class='facetTable'></table>");
	        		var trCat = $('<tr></tr>').attr({'class':'facetSubCat'});
	        		table.append(trCat.append( $('<td></td>').attr({'colspan':2}).text('IMPC')));
	        		
	        		var counter=0;
	        		for ( var i in procedures_params){
	        			counter++;
	        			var procedureCount = procedures_params[i].length;
	        			var pClass = 'procedure'+counter;
	        			var tr = $('<tr></tr>');
	        			var td1 = $('<td></td>').attr({'class': pClass});
	        			var td2 = $('<td></td>');	        			        			
	        			var a = $('<a></a>').attr({'class':'paramCount', 'rel': procedureName2IdKey[i].stable_id}).text(procedureCount);
	        			table.append(tr.append(td1.text(i), td2.append(a)));
	        			
	        			// skip subterms for now
	        			for ( var j=0; j<procedures_params[i].length; j++ ){
	        				var pmClass = pClass+'_param';
	        				var tr = $('<tr></tr>').attr('class', pmClass);
	        				var oParamCount = procedures_params[i][j];
	        				//console.log('Parhttps://github.com/mpi2/mpi2_search/tree/devam: '+ oParamCount.param_name + ':'+ oParamCount.count);
	        				var a = $('<a></a>').attr({
	        					href: 'http://www.mousephenotype.org/impress/impress/listParameters/'	        						
	        						+ procedureName2IdKey[i].stable_key,	        					
	        					target: '_blank'
	        				}).text(oParamCount.param_name);	
	        				
	        				var td = $('<td></td>').attr({colspan: 2, rel: parameterName2IdKey[oParamCount.param_name].stable_key});
	        				table.append(tr.append(td.append(a)));	        				
	        			}	        			
	        		}  */		
	        		
	        		if (json.response.numFound == 0 ){
	        			table = null;
	        		}	    			
	        		$('div#pipelineFacet .facetCatList').html(table);					
	        		
	        		// skip toggle table inside a tr for subterms
	        		/*var regex = /procedure\d+/;
	        		$('table#pipeline td[class^=procedure]').toggle(
	        			function(){ 
	        				var match = regex.exec( $(this).attr('class') );
	        				var thisClass = match[0] ? match[0] : $(this).attr('class');	        				
	        				$(this).parent().siblings("tr." + thisClass + '_param').show();
	        			},
	        			function(){
	        				var match = regex.exec( $(this).attr('class') );
	        				var thisClass = match[0] ? match[0] : $(this).attr('class');	        				
	        				$(this).parent().siblings("tr." + thisClass + "_param").hide();
	        			}
	        		);
	        		*/
	        		
	        		$('table#pipeline td a.paramCount').click(function(){	
	        			self._fetchFilteredDataTable($(this), 'pipelineFacet');	        			
	        		});
	        		
	        		// reload sidebar for hash state	        		
	        		if ( self.options.data.core == 'pipeline' && self.options.data.fq.match(/pipeline_stable_id.+/) ){
	        			//console.log('1 pipeline UNfiltered');
	        			var solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams['pipelineFacet'].params, self.options.commonParams);						
	        			solrSrchParams.q = self.options.data.q;	
	    	    		self._invokeFacetDataTable(solrSrchParams, 'pipelineFacet', MPI2.searchAndFacetConfig.facetParams['pipelineFacet'].gridName);         			
	    	    	}
	        		else if ( self.options.data.core == 'pipeline' && self.options.data.fq.match(/procedure_stable_id.+/) ){
	        			//console.log('1 pipeline FILTERED');
	        			var proc_sid = self.options.data.fq.replace('procedure_stable_id:', '');
	    	    		var obj = $('div#pipelineFacet div.facetCatList').find("table#pipeline a[rel='" + proc_sid + "']"); 
	        			self._fetchFilteredDataTable(obj, 'pipelineFacet');	
	        		}
	    		}	    		
	    	});	    	
	    },   
	    	    
	    _doNames2IdMapping: function(response){
	    	var nodes = response.docs;
	    	var procedureName2IdKey = {};
	    	var parameterName2IdKey = {};
	    	
	    	for( var n=0; n<nodes.length; n++){
	    		var node = nodes[n];	    		
	    			    		
	    		var procName = node.procedure_name;	    			    		
	    		var procSId  = node.procedure_stable_id;
	    		var procKey  = node.procedure_stable_key;
	    		
	    		var paramName = node.parameter_name;
	    		var paramSId  = node.parameter_stable_id;
	    		var paramKey  = node.parameter_stable_key;	    		
	    			    		
	    		if ( !procedureName2IdKey[procName] ){
	    			procedureName2IdKey[procName] = {};
	    		}
	    		if ( !parameterName2IdKey[paramName] ){
	    			parameterName2IdKey[paramName] = {};
	    		}
	    		procedureName2IdKey[procName] = {stable_id: procSId, stable_key: procKey};
	    		parameterName2IdKey[paramName] = {stable_id: paramSId, stable_key: paramKey};	    		
	    	}
	    	return [procedureName2IdKey, parameterName2IdKey];
	    }, 

	    _doMPFacet: function(){
	    	var self = this;
	    	var solrURL = self.options.solrBaseURL_ebi + 'mp/select';
	    		    	
	    	var queryParams = $.extend({}, {				
				'fq': 'ontology_subset:*',
				'rows': 0, // override default
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.field': 'top_level_mp_term',
				'facet.sort': 'index',						
				'q.option': 'AND',
				'q': self.options.data.q}, self.options.commonParams);			
	    
	    	$.ajax({	
	    		'url': solrURL,
	    		'data': queryParams,						
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {
	    			
	    			// update this if facet is loaded by redirected page, which does not use autocomplete
	    			$('div#mpFacet span.facetCount').attr({title: 'total number of unique phenotype terms'}).text(json.response.numFound);
	    			
	    			var table = $("<table id='mpFacet' class='facetTable'></table>");	    			
	    			
	    	    	var aTopLevelCount = json.facet_counts.facet_fields['top_level_mp_term'];
	    	    
	    	    	// top level MP terms
	    	    	for ( var i=0;  i<aTopLevelCount.length; i+=2 ){	    		
	    	    		
	        			var tr = $('<tr></tr>').attr({'rel':aTopLevelCount[i], 'id':'topLevelMpTr'+i});  
	        			// remove trailing ' phenotype' in MP term
	        			
	    	    		var td1 = $('<td></td>').attr({'class': 'mpTopLevel'}).text(aTopLevelCount[i].replace(' phenotype', ''));	    	    		   	    		
	    	    		
	    	    		var a = $('<a></a>').attr({'rel':aTopLevelCount[i]}).text(aTopLevelCount[i+1]);
	    	    		var td2 = $('<td></td>').attr({'class': 'mpTopLevelCount'}).append(a);
	    	    		table.append(tr.append(td1, td2)); 
	        			
	    	    	}    	
	    	    	
	    			self._displayOntologyFacet(json, 'mpFacet', table);			
	    		}		
	    	});		    	
	    },
	   
	    _displayOntologyFacet: function(json, facetDivId, table){	    	
	    	
	    	var self = this;
	    	var ontology = MPI2.searchAndFacetConfig.facetParams[facetDivId].ontology;
	    	var solrBaseUrl = self.options.solrBaseURL_ebi + ontology + '/select';	    	
	    	
	    	if (json.response.numFound == 0 ){	    		
    			table = null;
    		}	    			
    		$('div#'+facetDivId+ ' .facetCatList').html(table);
    		
    		$('table#'+ ontology + 'Facet td a').click(function(){      			
    			self._fetchFilteredDataTable($(this), facetDivId);    			
    		});    		    		
    		
    		// reload sidebar for hash state   		
	    	if ( self.options.data.core == 'mp' && self.options.data.fq && self.options.data.fq != 'ontology_subset:*' ){
	    		//console.log('MP filtered');
	    		var sTermName = self.options.data.fq.replace('ontology_subset:* AND top_level_mp_term:', '').replace(/"/g, '');	    		
	    		var obj = $('div#' + facetDivId + ' div.facetCatList').find("table#" + facetDivId + " a[rel='" + sTermName + "']");	    		
	    		self._fetchFilteredDataTable(obj, facetDivId);
	    	}
	    	else if ( self.options.data.core == 'mp' && self.options.data.fq && self.options.data.fq == 'ontology_subset:*' ){
	    		//console.log('MP UNfiltered');
	    		var solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams['mpFacet'].params, self.options.commonParams);						
    			solrSrchParams.q = self.options.data.q;	
	    		self._invokeFacetDataTable(solrSrchParams, 'mpFacet', MPI2.searchAndFacetConfig.facetParams['mpFacet'].gridName);	    		
	    	}
    		
    		// skip display subterms for now
    		// fetch and expand children of top level MP term
    		/*$('table#'+ ontology + 'Facet td.'+ontology+'TopLevel').click(function(){  
    			
    			var parent = $(this).parent();
    			var children = $('tr[class^=' + $(this).parent().attr('id') +']');
    			
    			if ( parent.hasClass(ontology + 'TopExpanded') ){
    				children.hide();
    				parent.removeClass(ontology + 'TopExpanded');
    			}
    			else {
    				parent.addClass(ontology + 'TopExpanded');
    				
    				if ( children.size() == 0 )phenotype_call_summary{
    					
    					var topLevelOntoTerm = $(this).siblings('td').find('a').attr('rel');    				
    					var thisTable = $('table#'+ ontology+ 'Facet');
    			
    					var solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams.mpFacet.params, self.options.commonParams);	                   
    					solrSrchParams.q = self.options.data.q;
    					
    					solrSrchParams.fl = ontology+ '_id,'+ ontology + '_term,'+'ontology_subset';    					
    					solrSrchParams.sort = ontology + '_term asc';
    					solrSrchParams.solrBaseURL = solrBaseUrl;                  
    					
    					var solrSrchParamsStr = $.fn.stringifyJsonAsUrlParams(solrSrchParams);    					
    				
    					if ( ontology == 'ma' ){
    						solrSrchParamsStr += '&fq=top_level_'+ ontology + '_term:' + '"'+ topLevelOntoTerm + '"'
    					                      +  '&fq=top_level_'+ ontology + '_term_part_of:' + '"'+ topLevelOntoTerm + '"';    						
    					}	
    					else {
    						solrSrchParamsStr += '&fq=top_level_'+ ontology + '_term:' + '"'+ topLevelOntoTerm + '"'; 
    					} 
    					
    					$.ajax({    					
    						'url': solrBaseUrl + '?' + solrSrchParamsStr,    											
    						'dataType': 'jsonp',
    						'jsonp': 'json.wrf',
    						'success': function(json) {    							
    							if (json.response.numFound > 10 ){    							
    								self._display_subTerms_in_tabs(json, topLevelOntoTerm, thisTable, ontology);
    							}
    							else {
    								self._display_subTerms(json, topLevelOntoTerm, thisTable, ontology);
    							}    							
    						}		
    					});		
    				}
    				else {
    					// fetch children only once
    					children.show();
    					parent.addClass(ontology+ 'TopExpanded');
    				}
    			}
    		});  */  		
	    },
	    	   
	    _doImageFacet: function(){
	    
	    	var self = this;
	    	var solrURL = self.options.solrBaseURL_ebi + 'images/select';
	    		    	
	    	var queryParams = $.extend({}, {							
				'rows': 0,
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,				
				'facet.sort': 'index',
				'fl': 'annotationTermId,annotationTermName,expName,symbol',
				//'fq': "annotationTermId:M* OR symbol_gene:*",  // images that have annotations only
				'q.option': 'AND',				
				'q': self.options.data.q 				
				}, self.options.commonParams);
	    	
	    	// if users do not search with wildcard, we need to search by exact match
	    	/*if (queryParams.q.indexOf(" ") != -1 ){
	    		queryParams.qf = 'auto_suggest';	    		
	    	}  
	    	else if ( queryParams.q.indexOf('*') == -1 ){	    	
	    		queryParams.qf = 'text_search';	    		
	    	}	*/    		
	    	
	    	var paramStr = $.fn.stringifyJsonAsUrlParams(queryParams) 
	    		+ "&facet.field=expName"
	    		+ "&facet.field=higherLevelMaTermName"
	    		+ "&facet.field=higherLevelMpTermName"
	    		+ "&facet.field=subtype"
	    		    	
	    	$.ajax({	
	    		'url': solrURL,
	    		'data': paramStr,   //queryParams,						
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',	    		
	    		'success': function(json) {	 
	    			//console.log(json);	    			
	    			    	    		
    	    		$('div#imagesFacet span.facetCount').attr({title: 'total number of unique images'}).html(json.response.numFound);
	    		
	    			var table = $("<table id='imgFacet' class='facetTable'></table>");
	    			
	    			var aFacetFields = json.facet_counts.facet_fields; // eg. expName, symbol..
	    			
	    			var displayLabel = {
	    								higherLevelMaTermName: 'Anatomy',
	    								expName : 'Procedure',	    					            
	    					            higherLevelMpTermName: 'Phenotype',
	    					            subtype: 'Gene'
	    								};	    			    			    			
	    				    			
	    			for ( var facetName in aFacetFields ){ 	    				
	    				    				
	    				for ( var i=0; i<aFacetFields[facetName].length; i+=2){    					  					
	    					
	    					var fieldName   = aFacetFields[facetName][i];
	    					var facetCount  = aFacetFields[facetName][i+1];
	    					var displayName = displayLabel[facetName];
	    					//console.log(fieldName + ' : '+ facetCount);
	    					
	    					var tr = $('<tr></tr>').attr({'rel':fieldName, 'id':'topLevelImgTr'+i});
	    					var td1 = $('<td></td>').attr({'class': 'imgExperiment'}).text(fieldName);
	    				
	    					var imgBaseUrl = baseUrl + "/images?";
	    					
		    	    		var params = "q=" + self.options.data.q;
		    	    		//params += "&fq=annotationTermId:M*&q.option=AND&qf=" + queryParams.qf + "&defType=edismax&wt=json&fq=" + facetName + ":";	
		    	    		// here we take all images - ie, not filtering on having annotations or not
		    	    		params += "&q.option=AND&qf=" + queryParams.qf + "&defType=edismax&wt=json&fq=" + facetName + ":";	
		    	    		params += '"' + fieldName + '"';
		    	    				    	    			  
		    	    		var fqClass = facetName + ":" + '"' + fieldName + '"';
		    	    		
		    	    		var imgUrl = imgBaseUrl + params;	
		    	    				    	    		
		    	    		var infos = "{params:\"" + encodeURI(params) 
		    	    		          + "\", fullLink:\"" +  encodeURI(imgUrl) 
		    	    		          + "\",imgType:\"" + displayName 
		    	    		         // + "\",facetParams:\"" + facetParams
		    	    		          + "\",imgSubName:\"" + fieldName
		    	    		          + "\", imgCount:\"" + facetCount
		    	    		          + "\", solrCoreName:\"" + 'images' 
		    	    	    		  +	"\", mode:\"" + 'imageGrid'
		    	    		          + "\"}";		    	    		
		    	    		   		
		    	    		var a = $('<a></a>').attr({'rel':infos, 'class':fqClass}).text(facetCount);
		    	    		var td2 = $('<td></td>').attr({'class': 'imgExperimentCount'}).append(a);
		    	    		
		    	    		if ( i == 0 ){
	    						var catTr = $('<tr></tr>').attr({'class':'facetSubCat'});
	    						var catLabel = displayLabel[facetName];
	    						var catTd = $('<td></td>').attr({'colspan':2}).text(catLabel);
	    						catTr.append(catTd);
	    						table.append(catTr); 
	    					}		    	    				    	    		
		    	    		table.append(tr.append(td1, td2));		    	    		
	    				}
	    			}	    				    	    	
	    			self._displayImageFacet(json, 'images', 'imagesFacet', table);			
	    		}		
	    	});	    	
	    }, 
	    	    
	    _displayImageFacet: function(json, coreName, facetDivId, table){
	    	var self = this;
	    	//console.log(json)
	    	var solrBaseUrl = self.options.solrBaseURL_ebi + coreName + '/select';	    	
	    	
	    	if (json.response.numFound == 0 ){	    		
    			table = null;
    		}
	    	else {
	    		$('div#'+facetDivId+ ' .facetCatList').html(table);
	    		table.find('td a').click(function(){	    			
	    			// invoke filtered toplevel in dataTable
	    			MPI2.searchAndFacetConfig.facetParams[facetDivId].showImgView = true; // default
	    			self._fetchFilteredDataTable($(this), facetDivId);
	    		});	
	    	}
	    	
	    	// reload sidebar for hash state	    	
	    	if ( self.options.data.core == 'images' && self.options.data.fq.match(/annotationTermId.+/)){
	    		//console.log('UNfiltered images fq: ' + self.options.data.fq);
    			var solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams[facetDivId].params, self.options.commonParams);						
    			solrSrchParams.q = self.options.data.q;
			
    			// for images, qf is either auto_suggest or text_search depending on query string
    			if ( solrSrchParams.q.indexOf('*') == -1 ){					
    				solrSrchParams.qf = 'text_search';
    			}					
						
    			// load dataTable							
    			self._invokeFacetDataTable(solrSrchParams, facetDivId, MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName, true); 
    		}
	    	else if ( self.options.data.core == 'images' && self.options.data.fq.match(/expName.+|higherLevel.+|subtype.+/) ){	    
	    		//console.log('filtered images fq: ' + self.options.data.fq);
	    		var obj = $('div#imagesFacet div.facetCatList').find("table#imgFacet a[class='" + self.options.data.fq + "']");	    		
	    		self._fetchFilteredDataTable(obj, 'imagesFacet');
	    	}       		
	    	
	    	// when last facet is done
	    	$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch);
	    },	
	    
	    _fetchFilteredDataTable: function(obj, facetDivId){
	    	var self = this;	    	
	    	var topLevelName;			
			var oSolrSrchParams = {}
			var displayedTopLevelName;
			var type = MPI2.searchAndFacetConfig.facetParams[facetDivId].type;	
			var imgParamStr, imgFacetParams;
			var oVal = MPI2.searchAndFacetConfig.facetParams[facetDivId];
			var dTable = $.fn.fetchEmptyTable(oVal.tableHeader, 
                     oVal.tableCols, oVal.gridName); // skeleton to fill data from server			  	
			
			var title = $.fn.upperCaseFirstLetter(oVal.type);	    	
			var gridTitle = $('<div></div>').attr({'class':'gridTitle'}).html(title);			
			
			// highlight only currently selected top level 
	    	var ontology = oVal.ontology;
	    	if ( ontology ){
	    		topLevelName = obj.attr('rel'); 
	    		$('div#'+ facetDivId + ' table td.' + ontology + 'TopLevel').removeClass('highlight');
	    		obj.parent().siblings('td.'+ ontology + 'TopLevel').addClass('highlight');
	    		
	    		// MP top level term filter
	    		if ( ontology == 'mp' ){
					oSolrSrchParams = $.extend({}, oVal.params, self.options.commonParams);
					oSolrSrchParams.fq = "ontology_subset:* AND top_level_mp_term:\"" + topLevelName + "\"";
					displayedTopLevelName = $('<div></div>').attr({'class':'gridSubTitle'}).html('Top level term: ' + topLevelName.replace(' phenotype', '')); 	
				} 
	    	}	
	    	else if (facetDivId == 'geneFacet') {
	    		topLevelName = obj.attr('rel'); 
	    		
	    		$('table#gFacet td').removeClass('highlight');
	    		
	    		obj.parent().siblings('td.geneSubfacet').addClass('highlight');
								
				// Gene subtype filter
				var subFacetName = obj.attr('class') == 'subtype' ? 'marker_type' : 'status';			
				var subFacetFilter = subFacetName + ':"'  + topLevelName + '"';				
				self.options.geneSubFacet_filter_params = subFacetFilter;
								
				oSolrSrchParams = $.extend({}, self.options.commonParams, 
						MPI2.searchAndFacetConfig.facetParams[facetDivId].params);				
				oSolrSrchParams.fq = subFacetFilter;
								
				var label = $.fn.upperCaseFirstLetter(subFacetName).replace('_', ' ');				
				displayedTopLevelName = $('<div></div>').attr({'class':'gridSubTitle'}).html(label + ': ' + topLevelName); 
	    	}
	    	else if (facetDivId == 'pipelineFacet'){
	    		topLevelName = obj.parent().siblings('td[class^=procedure]').text(); 
	    		$('table#pipeline td[class^=procedure]').removeClass('highlight');
				obj.parent().siblings('td[class^=procedure]').addClass('highlight');
				var proc_stable_id = obj.attr('rel');   
	            oSolrSrchParams = $.extend({}, oVal, self.options.commonParams); 
	            oSolrSrchParams.fq = 'procedure_stable_id:' + proc_stable_id;	            
	              			
	            displayedTopLevelName = $('<div></div>').attr({'class':'gridSubTitle'}).html('Procedure: ' + topLevelName); 	
	    	}
	    	else if (facetDivId == 'imagesFacet'){
	    		
	    		var oInfos = eval("(" + obj.attr('rel') + ")");
	    		oSolrSrchParams.fq = obj.attr('class'); 
	    		
	    		topLevelName = oInfos.imgSubName;
	    			    		
	    		$('table#imgFacet td.imgExperiment').removeClass('highlight');
				obj.parent().siblings('td.imgExperiment').addClass('highlight'); 
											
				imgParamStr = oInfos.params;								
				
				var imgCountInfo = "<a href='" + oInfos.fullLink + "'> (view all " + oInfos.imgCount + " images)</a>";
				displayedTopLevelName = $('<div></div>').attr({'class':'gridSubTitle'}).html(oInfos.imgType + ' : ' + topLevelName + imgCountInfo);	
	    	
				// toggles two types of views for images: annotation view, image view	 	   		
	 	   		var viewLabel, imgViewSwitcherDisplay, viewMode;
	 	   		if ( oVal.showImgView ){
	 	   			dTable.find('th:nth-child(2)').text("Image");	
	 	   			viewLabel = 'Image View: lists annotations to an image';
	 	   			imgViewSwitcherDisplay = 'Show Annotation View'; 
	 	   			viewMode = 'imageView';
	 	   		}
	 	   		else {
	 	   			viewLabel = 'Annotation View: groups images by annotation';
	 	   			imgViewSwitcherDisplay = 'Show Image View'; 
	 	   			viewMode = 'annotView';
	 	   		}
	 	   		
	 	   		gridTitle.append($('<div></div>').attr({'id':'imgView', 'rel':viewMode}).html("<span id='imgViewSubTitle'>" 
	 	   				+ viewLabel 
	 	   				+ "</span><span id='imgViewSwitcher'>" 
	 	   				+ imgViewSwitcherDisplay + "</span>"));
	 	   		
	 	   		$('div#mpi2-search').html('');
	 	   		$('div#mpi2-search').append(gridTitle, displayedTopLevelName, dTable);
	 	   		
	 	   		// img view switcher control
	 	   		$('span#imgViewSwitcher').click(function(){	 	   		
	 	   			oVal.showImgView = oVal.showImgView ? false : true;	 	   			   			
	 	   			self._fetchFilteredDataTable(obj, facetDivId);
	 	   		});	    	
	    	}
	    		    	
	    	oVal.topLevelName = topLevelName;
			oSolrSrchParams.q = self.options.data.q;			
				
			var oInfos = {};
			oInfos.solrCoreName = oVal.solrCoreName;
 			oInfos.mode = oVal.gridName;
 			//oInfos.dataTablePath = baseUrl + '/dataTable';
 			oInfos.dataTablePath = MPI2.searchAndFacetConfig.dataTablePath;
 			if (facetDivId == 'imagesFacet'){
 				oInfos.params = imgParamStr; 				
 				oInfos.showImgView = oVal.showImgView; 				
 			}
 			else {
 				oInfos.params = $.fn.stringifyJsonAsUrlParams(oSolrSrchParams);
 				$('div#mpi2-search').html(''); 				
 				$('div#mpi2-search').append(gridTitle, displayedTopLevelName, dTable); 				
 			}			
 			
 			// hash state stuff	
 			var hashParams = {};
 			hashParams.q    = oSolrSrchParams.q;
 			hashParams.core = oVal.solrCoreName;
 			hashParams.fq   = oSolrSrchParams.fq;
 			//var hashParamStr = $.fn.stringifyJsonAsUrlParams(oSolrSrchParams) + "&core=" + oVal.solrCoreName;
			var hashParamStr = $.fn.stringifyJsonAsUrlParams(hashParams);			
						
			window.location.hash = hashParamStr;
						
			$.fn.invokeDataTable(oInfos);			
	    },
	    
	    /*_invokeDataTable: function(oInfos){
	    	//console.log(JSON.stringify(oInfos, null, 2));
	    	
	    	$('table#' + oInfos.mode).dataTable({
	    		"bJQueryUI": true,
	    		"bSort" : false,
	    		"bProcessing": true,
	    		"bServerSide": true,	    		
	    		"sDom": "<'row-fluid'lr>t<'row-fluid'<'span6'i><'span6'p>>",
	    		//"sDom": 'T<"clear">lfrtip',
				"sPaginationType": "bootstrap",
	    		"fnServerParams": function ( aoData ) {
	    			aoData.push(	    			 
	    			    {"name": "solrParams",
	    				 //"value": oInfos.params// + oInfos.facetParams
	    				 "value": JSON.stringify(oInfos, null, 2)// + oInfos.facetParams
	    				}	    
	    			)			
	    		},
	    		//"sDom": '<"H"Tfr>t<"F"ip>',
	    		"oTableTools": {
	    			"aButtons": [
	    				"copy", "csv", "xls", "pdf",
	    				{
	    					"sExtends":    "collection",
	    					"sButtonText": "Save",
	    					"aButtons":    [ "csv", "xls", "pdf" ]
	    				}
	    			]
	    		},
	    		 "oTableTools": {
	    	            "sSwfPath": "/swf/copy_csv_xls_pdf.swf"
	    	    },
	    		"sAjaxSource": baseUrl + '/dataTable'
	    	});	    	
	    },	*/       
	    	    	    
	    _display_subTerms_in_tabs: function(json, topLevelOntoTerm, thisTable, ontology){
	    	var self = this;
	    	
	    	var docs = json.response.docs;	    
	    	var tabLtrTerms = {};
	    	var tabLtr = null;
	    	
	    	// parse json for sub term hyperlink
	    	for( var i=0; i<docs.length; i++ ){
	    		var termId = docs[i][ontology+'_id'];
	    		var ontoTerm = docs[i][ontology+'_term'];
	    		//console.log(termId + ' -- '+ ontoTerm);	    		
	    		tabLtr = ontoTerm.substring(0,1);
	    		
	    		if ( ! tabLtrTerms[tabLtr] ){	    			
	    			 tabLtrTerms[tabLtr] = [];	    			
	    		}	    			    		
	    		
	    		var a = $('<a></a>').attr({'href':baseUrl+'/phenotypes/'+termId}).text(ontoTerm);
	    		tabLtrTerms[tabLtr].push(a); 
	    	}
	    	
	    	// create tabs html markup
	    	var tabBlk = $('<div></div>').attr({'id': ontology + 'Tab_' + topLevelOntoTerm});
	    	var ul     = $('<ul></ul>');	    	
	    
	    	var counter = 0;
	    	for( var i in tabLtrTerms ){
	    		counter++;	    	
	    		//var id = 'tab_' + i + '_' + topLevelOntoTerm;	 
	    		var a = $('<a></a>').attr({'href': 'ui-tabs-'+counter}).text(i.toUpperCase());	    	
	    		var li = $('<li></li>').append(a);
	    		ul.append(li);
	    	}
	    	tabBlk.append(ul);
	    		    	
	    	var previousTr = $('tr[rel="' + topLevelOntoTerm + '"]'); 
    		var tr = $('<tr></tr>').attr({'class': previousTr.attr('id')+'_sub'});	    		
    		tr.append($('<td></td>').attr({'colspan':2}).append(tabBlk));    		
    		
    		previousTr.after(tr);
    		
    		// make tabs
    		var tabs = tabBlk.tabs({
    			selected: 0,
        	 	cache   : true,
        	 	spinner : 'Loading'
    		});
    		    	  
    		// populate tab panel content
    		tabs.find('ul li a').each(function(){    			
    			var id = $(this).attr('href').replace('#','');  
    			var tabName = $(this).text().toLowerCase();
    			var panelContent = tabs.find('div#'+id);
    			
    	    	for ( var j=0; j<tabLtrTerms[tabName].length; j++){    	    		
    	    		panelContent.append($('<span></span>').html(tabLtrTerms[tabName][j]));    	    
    	    	} 
    		});   		
	    	
	    },
	    
	    _display_subTerms: function(json, topLevelOntoTerm, thisTable, ontology){   
	    	
	    	var self = this;
	    	
	    	var docs = json.response.docs;
	    	// need to reverse the order due to appending of tr with .after()
	    	// otherwise the order appears as 'desc' alphabetically
	    	for( var i=docs.length-1; i<docs.length; i-- ){
	    		var termId = docs[i][ontology+'_id'];
	    		var ontoTerm = docs[i][ontology+'_term'];
	    		//console.log(termId + ' -- '+ ontoTerm);
	    		var previousTr = $('tr[rel="' + topLevelOntoTerm + '"]'); 
	    		var tr = $('<tr></tr>').attr({'class': previousTr.attr('id')+'_sub'});	    		
	    		var a = $('<a></a>').attr({'href':baseUrl+'/phenotypes/'+termId}).text(ontoTerm);
	    		tr.append($('<td></td>').attr({'colspan':2}).append(a));
	    		
	    		previousTr.after(tr);
	    	}
	    },
	  
	    destroy: function () {    	   
	    	// does not generate selector class
    	    // if using jQuery UI 1.8.x
    	    $.Widget.prototype.destroy.call(this);
    	    // if using jQuery UI 1.9.x
    	    //this._destroy();
    	}  
    });
	
}(jQuery));	
	



