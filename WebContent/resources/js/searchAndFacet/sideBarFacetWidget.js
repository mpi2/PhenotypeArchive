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
			//solrBaseURL_bytemark:'http://ikmc.vm.bytemark.co.uk:8983/solr/',
    		solrBaseURL_bytemark: drupalBaseUrl + '/bytemark/solr/',
    		//solrBaseURL_bytemark:'https://beta.mousephenotype.org/bytemark/solr/',
			//solrBaseURL_bytemark:'http://beta.mousephenotype.org/mi/solr/',
			//solrBaseURL_ebi: 'https://beta.mousephenotype.org/mi/solr/',
			solrBaseURL_ebi: drupalBaseUrl + '/mi/solr/',
			commonParams: {
							'qf': 'auto_suggest',
				 			'defType': 'edismax',
				 			'wt': 'json'
				 			//'start' : 0				 			
				 			},
			facetId2SearchType: {
								geneFacet: {type: 'genes', 
											params: {'sort': "marker_symbol asc"},
											tableHeader: "<thead><th>Gene</th><th>Latest Status</th><th>Register for Updates</th></thead>",											
											tableCols: 3,
											solrCoreName: 'gene',
											gridName: 'geneGrid'				 			
								},
								pipelineFacet: {type: 'procedures', 
												params:{'fq': 'pipeline_stable_id:IMPC_001'},
												tableHeader: '<thead><th>Parameter</th><th>Procedure</th><th>Pipeline</th></thead>',
												tableCols: 3,
												solrCoreName: 'pipeline',
												gridName: 'pipelineGrid'	
   								},
   								phenotypeFacet: {type: 'phenotypes', 
   												 params: {'fq': "ontology_subset:*",
   														  'fl': 'mp_id,mp_term,mp_definition,top_level_mp_term'},
   												 tableHeader: '<thead><th>Phenotype</th><th>Definition</thead>',
   												 tableCols: 2,
   												 solrCoreName: 'mp',
   												 gridName: 'mpGrid',
   												 topLevelName: '',
   												 ontology: 'mp'
   								},
   								imageFacet:	{type: 'images', 
   											 params: {'fl' : 'annotationTermId,annotationTermName,expName,symbol,symbol_gene,smallThumbnailFilePath,largeThumbnailFilePath'},
   												    // 'fq' : "annotationTermId:M* OR expName:* OR symbol:*"},
   											 tableHeader: '<thead><th>Annotation</th><th>Example Image</th></thead>',   											
   											 tableCols: 2,   											
   	      									 solrCoreName: 'images',
   	      									 gridName: 'imagesGrid',
   	      									 topLevelName: '',
   	      									 imgViewSwitcherDisplay: 'Annotation View',
   	      									 forceReloadImageDataTable: false,
   	      									 showImgView: true
   								}   											 
			}
	    },    
		
	    _openFacet: function(){
	    	var self = this;
	    	
	    	$('div.facetCatList').hide();
	    	
	    	var mode = self.options.data.type;
	    	var facetDivId = mode + 'Facet';
	    	var gridName = self.options.facetId2SearchType[facetDivId].gridName;
	    	var solrSrchParams = {};
	    	
	    	// priority order of facet to be opened based on search result
	    	if (mode == 'gene'){	    		
	    		$('div#geneFacet div.facetCatList').show(); // open by default
	    		if (self.options.marker_type_filter_params){
	    			solrSrchParams.fq = self.options.marker_type_filter_params;
	    		}
	    	}	
	    	else if (mode == 'mp'){
	    		$('div#phenotypeFacet div.facetCatList').show(); // open by default			
	    	}
	    	else if (mode == 'pipeline'){
	    		$('div#pipelineFacet div.facetCatList').show(); // open by default			
	    	}
	    	else if (mode == 'images'){
	    		$('div#imageFacet div.facetCatList').show(); // open by default			
	    	}
	    	var type = self.options.facetId2SearchType[facetDivId].type;			
							
			// also triggers SOP/gene/MP grid depending on what facet is open	
	    	/*if ( mode != 'gene'){
	    		solrSrchParams = $.extend({}, self.options.facetId2SearchType[facetDivId].params, self.options.commonParams);
	    	}
			solrSrchParams.q = self.options.data.q; 
			
			// for images, qf is either auto_suggest or text_search depending on query string
			if (facetDivId == 'imageFacet' && solrSrchParams.q.indexOf('*') == -1 ){
				solrSrchParams.qf = 'text_search';
			}	
			
			// dataTable		
			if ( $('table#'+ gridName).size() != 1 ){
				self._invokeFacetDataTable(solrSrchParams, facetDivId, gridName);
			}	*/
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
					var gridName = self.options.facetId2SearchType[facetDivId].gridName;
					var type = self.options.facetId2SearchType[facetDivId].type;
					var solrSrchParams = {};
									
					// also triggers SOP/gene/MP grid depending on what facet is clicked
					if (facetDivId == 'geneFacet'){
						if (self.options.marker_type_filter_params){
							solrSrchParams.fq = self.options.marker_type_filter_params;
						}
	                }					
					else {						
						solrSrchParams = $.extend({}, self.options.facetId2SearchType[facetDivId].params, self.options.commonParams);						
					}
					
					solrSrchParams.q = self.options.data.q; 
					
					// for images, qf is either auto_suggest or text_search depending on query string
					if (facetDivId == 'imageFacet' && solrSrchParams.q.indexOf('*') == -1 ){
						solrSrchParams.qf = 'text_search';
					}	
					
					// sanger grid code
					//$(self.options.geneGridElem).trigger('search', [{type: type, solrParams: solrSrchParams }]);
					// end of sanger grid code
					
					// dataTable code					
					//console.log('name: ' + self.options.facetId2SearchType[facetDivId].topLevelName);
					if ( $('table#'+ gridName).size() != 1 ){
						self._invokeFacetDataTable(solrSrchParams, facetDivId, gridName);
					}	
				}								
			});	
						
			// click on facetCount to fetch results in grid
			$('span.facetCount').click(function(){						
				
				var facetDivId = $(this).parent().parent().attr('id');
				var gridName = self.options.facetId2SearchType[facetDivId].gridName;			
				var solrSrchParams = {}
								
				// remove highlight from selected 
				if ( facetDivId == 'geneFacet' ){
					$('table#gFacet td').removeClass('highlight');					
				}
				else if (facetDivId == 'pipelineFacet' ){
					$('table#pipeline td[class^=procedure]').removeClass('highlight');					
					solrSrchParams = $.extend({}, self.options.facetId2SearchType[facetDivId].params, self.options.commonParams);
					self.options.facetId2SearchType[facetDivId].params.fq = "pipeline_stable_id:IMPC_001";					
				}	
				else if (facetDivId == 'phenotypeFacet' ){				
					$('table#mpFacet td').removeClass('highlight');
					solrSrchParams = $.extend({}, self.options.facetId2SearchType[facetDivId].params, self.options.commonParams);
					self.options.facetId2SearchType[facetDivId].params.fq = "ontology_subset:*";					
				}
				else if (facetDivId == 'imageFacet' ){    	    			
					$('table#imgFacet td').removeClass('highlight');
					solrSrchParams = $.extend({}, self.options.facetId2SearchType[facetDivId].params, self.options.commonParams);					
				}
				
				solrSrchParams.q = self.options.data.q;
				
				// for images, qf is either auto_suggest or text_search depending on query string
				if (facetDivId == 'imageFacet' && solrSrchParams.q.indexOf('*') == -1 ){
					solrSrchParams.qf = 'text_search';
				}					
				
				var type = self.options.facetId2SearchType[facetDivId].type;
				
				// sanger grid code
				//$(self.options.geneGridElem).trigger('search', [{type: type, solrParams: solrSrchParams}]);
				// end of sanger grid code
				
				// dataTable code							
				self._invokeFacetDataTable(solrSrchParams, facetDivId, gridName); 
								
			});	
    	},
    	  
    	_applyFacetTopLevelFilter: function(facetDivId){
    		var self = this;self._openFacet();   
    		var sTopLevelTerm = null;
    		var oSelectedTopLevel = $('div#'+ facetDivId + ' .facetTable td.highlight'); 
    		var obj = oSelectedTopLevel.siblings().find('a');    				    			
    		self._fetchFilteredDataTable(obj, facetDivId);     		
    	},
    	
    	 _invokeFacetDataTable: function(oSolrSrchParams, facetDivId,  gridName){
 	    	var self = this;
 	    	var oVal = self.options.facetId2SearchType[facetDivId];
 	    	var sTopLevelTerm = oVal.topLevelName; 	    	   	
 	    	
 	    	//console.log('load ' + facetDivId + ' -> ' + sTopLevelTerm); 	   
 	    	
	 	    if ( $('div#'+ facetDivId + ' .facetTable td.highlight').size() == 1 && sTopLevelTerm != '' && !oVal.forceReloadImageDataTable){
	 	    	// check if top level name in facet has been selected before for the current facet
	 	 	   	// if yes, filter top level set with the selected	 	    	
	 	    	self._applyFacetTopLevelFilter(facetDivId); 	    		
	 	    }
	 	    else if ( $('table#' + gridName).size() == 1 
	 	    		&&  $('div#'+ facetDivId + ' .facetTable td.highlight').size() == 0  
	 	    		&& sTopLevelTerm == '' 
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
		 		oInfos.dataTablePath = baseUrl + '/dataTable';
		 		
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
		 	   	
		 	   	if (facetDivId == 'imageFacet'){		 	   		
					
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
	    _init : function () {
			var self = this;
			
	    	self._doGeneSubTypeFacet();	    	
	    	self._doMPFacet();
	    	//self._doMAFacet();
			self._doPipelineFacet();
			self._doImageFacet();
			self._openFacet();   
	    },

		_doGeneSubTypeFacet: function(){
	    	var self = this;
	    	
			var solrURL = self.options.solrBaseURL_bytemark + 'gene/search';
	    	var queryParams = $.extend({},{				
				'rows': 0,
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.field': 'marker_type_str',  
				'facet.sort': 'count',
				'q': self.options.data.q}, self.options.commonParams);	
	    	
	    	$.ajax({ 				 					
	    		'url': solrURL,
	    		'data': queryParams,
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {	 	    			
	    			//console.log(json);					  
	    			self._displayGeneSubTypeFacet(json);	    				
	    		}		
	    	});	    	
	    },
	    
	    _displayGeneSubTypeFacet: function(json){
	    	var self = this;
	    	var numFound = json.response.numFound;
	    	
	    	// update this if facet is loaded by redirected page, which does not use autocomplete
	    	$('div#geneFacet span.facetCount').attr({title: 'total number of unique genes'}).text(numFound);
	    	
	    	if (numFound > 0){
	    		var unclassified_gene_subType;
	    		var trs = "<tr class='facetSubCat'><td colspan=2>Subtype</td></tr>";
	    		var facets = json.facet_counts['facet_fields']['marker_type_str'];
	    		for ( var i=0; i<facets.length; i+=2 ){		    			
	    			//console.log( facets[i] + ' ' + facets[i+1]);
					var type = facets[i];
					var count = facets[i+1];			
					if ( type == 'unclassified gene' ){					
						unclassified_gene_subType = "<tr><td class='geneSubtype'>" + type + "</td><td rel='" + type + "' class='geneSubtypeCount'><a rel='" + type + "'>" + count + "</a></td></tr>";
					}
					else {
						trs += "<tr><td class='geneSubtype'>" + type + "</td><td rel='" + type + "' class='geneSubtypeCount'><a rel='" + type + "'>" + count + "</a></td></tr>";
					}
	    		} 
	    		if ( unclassified_gene_subType ){
	    			trs += unclassified_gene_subType
	    		}
	    		
	    		var table = "<table id='gFacet' class='facetTable'>" + trs + "</table>";				
	    		$('div#geneFacet div.facetCatList').html(table);
	    		
				self._applyGeneGridResultFilterByMarkerSubType($('table#gFacet td.geneSubtypeCount a'));	    		
    		}	    	
	    },

		_applyGeneGridResultFilterByMarkerSubType: function(obj){
			var self = this;

			obj.click(function(){
				// invoke dataTable
				self._fetchFilteredDataTable($(this), 'geneFacet');
				
				/*
				// for sanger grid
				$('table#gFacet td').removeClass('highlight');
				$(this).parent().siblings('td.geneSubtype').addClass('highlight');
				
				var marker_subType = obj.attr('rel');
				var q = self.options.data.q;              
                var subTypeFilter = "marker_type_str:(\"" + marker_subType + "\")";
				self.options.marker_type_filter_params = subTypeFilter;
				
				// refresh geneGrid with selected marker_subtype
				var callerElem = $(self.options.geneGridElem);				
				callerElem.trigger('search', [{type: 'gene', 
											   solrParams: {q: self.options.data.q, fq: subTypeFilter}
											  }
											 ]); 	
				*/
				
				
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
	        			
	        			/*$('table#pipeline td[class^=procedure]').removeClass('highlight');
	        			$(this).parent().siblings('td[class^=procedure]').addClass('highlight');
	        			var proc_stable_id = $(this).attr('rel');   
	                    var solrSrchParams = $.extend({}, self.options.facetId2SearchType.pipelineFacet.params, self.options.commonParams);	                   
	                    solrSrchParams.q = self.options.data.q;
	                    solrSrchParams.fq = 'procedure_stable_id:' + proc_stable_id;	
	                    
	                    // sanger grid code
	                    $(self.options.geneGridElem).trigger('search', [{type: 'parameter', solrParams: solrSrchParams }]);
	                    */	 
	        		});	        		
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
	    			$('div#phenotypeFacet span.facetCount').attr({title: 'total number of unique phenotype terms'}).text(json.response.numFound);
	    			
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
	    	    	
	    			self._displayOntologyFacet(json, 'phenotypeFacet', table);			
	    		}		
	    	});		    	
	    },
	   
	    _displayOntologyFacet: function(json, facetDivId, table){	    	
	    	
	    	var self = this;
	    	var ontology = self.options.facetId2SearchType[facetDivId].ontology;
	    	var solrBaseUrl = self.options.solrBaseURL_ebi + ontology + '/select';	    	
	    	
	    	if (json.response.numFound == 0 ){	    		
    			table = null;
    		}	    			
    		$('div#'+facetDivId+ ' .facetCatList').html(table);
    		
    		$('table#'+ ontology + 'Facet td a').click(function(){      			
    			self._fetchFilteredDataTable($(this), facetDivId);    			
    		});    		    		
    		
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
    			
    					var solrSrchParams = $.extend({}, self.options.facetId2SearchType.phenotypeFacet.params, self.options.commonParams);	                   
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
	    	if (queryParams.q.indexOf(" ") != -1 ){
	    		queryParams.qf = 'auto_suggest';	    		
	    	}  
	    	else if ( queryParams.q.indexOf('*') == -1 ){	    	
	    		queryParams.qf = 'text_search';	    		
	    	}	    		
	    	
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
	    			    	    		
    	    		$('div#imageFacet span.facetCount').attr({title: 'total number of unique images'}).html(json.response.numFound);
	    		
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
		    	    		   		
		    	    		var a = $('<a></a>').attr({'rel':infos}).text(facetCount);
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
	    			self._displayImageFacet(json, 'images', 'imageFacet', table);			
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
	    			self.options.facetId2SearchType[facetDivId].showImgView = true; // default
	    			self._fetchFilteredDataTable($(this), facetDivId);
	    		});	
	    	}
	    	
	    	$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch);
	    },	
	    
	    _fetchFilteredDataTable: function(obj, facetDivId){
	    	var self = this;	    	
	    	var topLevelName;			
			var oSolrSrchParams = {}
			var displayedTopLevelName;
			var type = self.options.facetId2SearchType[facetDivId].type;	
			var imgParamStr, imgFacetParams;
			var oVal = self.options.facetId2SearchType[facetDivId];
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
				obj.parent().siblings('td.geneSubtype').addClass('highlight');
				
				// Gene subtype filter
				var subTypeFilter = 'marker_type_str:"' + topLevelName + '"';
				self.options.marker_type_filter_params = subTypeFilter;
				oSolrSrchParams.fq = subTypeFilter;
				displayedTopLevelName = $('<div></div>').attr({'class':'gridSubTitle'}).html('Subtype: ' + topLevelName); 
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
	    	else if (facetDivId == 'imageFacet'){
	    		
	    		var oInfos = eval("(" + obj.attr('rel') + ")");
	    		topLevelName = oInfos.imgSubName;
	    			    		
	    		$('table#imgFacet td.imgExperiment').removeClass('highlight');
				obj.parent().siblings('td.imgExperiment').addClass('highlight'); 
											
				imgParamStr = oInfos.params;								
				
				var imgCountInfo = "<a href='" + oInfos.fullLink + "' target='_blank'> (view all " + oInfos.imgCount + " images)</a>";
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
 			oInfos.dataTablePath = baseUrl + '/dataTable';
 			if (facetDivId == 'imageFacet'){
 				oInfos.params = imgParamStr; 				
 				oInfos.showImgView = oVal.showImgView; 				
 			}
 			else {
 				oInfos.params = $.fn.stringifyJsonAsUrlParams(oSolrSrchParams);
 				$('div#mpi2-search').html('');
 				$('div#mpi2-search').append(gridTitle, displayedTopLevelName, dTable);	
 			}			
 			
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
	    		
	    		var a = $('<a></a>').attr({'href':baseUrl+'/phenotypes/'+termId, 'target':'_blank'}).text(ontoTerm);
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
	    		var a = $('<a></a>').attr({'href':baseUrl+'/phenotypes/'+termId, 'target':'_blank'}).text(ontoTerm);
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
	



