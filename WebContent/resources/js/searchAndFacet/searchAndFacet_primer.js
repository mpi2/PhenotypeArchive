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
 * searchAndFacet_primer: callbacks for the autocomplete and sidebar widgets
 * see: autocompleteWidget.js and sideBarFacetWidget.js
 * 
 */
$(document).ready(function(){
	'use strict';	

	$('span.facetCount').text(''); // default when page loads
	
	// custom 404 page does not know about baseUrl
	var path = window.location.pathname.replace(/^\//,"");
	path = '/' + path.substring(0, path.indexOf('/'));
	var trailingPath = '/search';
	var trailingPathDataTable = '/dataTable';
	
	var pathname = typeof baseUrl == 'undefined' ? path + trailingPath : baseUrl + trailingPath;
	var dataTablePath = typeof baseUrl == 'undefined' ? path + trailingPathDataTable : baseUrl + trailingPathDataTable;
	
	// auto-complete is always created
	// load total facetCount of all data types by default when searchAndFacet page loads
	window.jQuery('input#userInput').mpi2AutoComplete({
		
			search_pathname: pathname,			        
			loadDataTable: function(event, data){				
				// calls Sanger search grid
				
				// do not use qf: 'auto_suggest', defType: 'edismax' for gene, as the config is different at Sanger				
				var facetParams = MPI2.searchAndFacetConfig.facetParams;
								
				if ( facetParams[data.type] && data.type != 'gene'){
					data.fq = facetParams[data.type].fq;
					data.qf = facetParams[data.type].qf;
					data.defType = facetParams[data.type].defType;
					data.wt = facetParams[data.type].wt;
				}
			
			    //var solrParams = data.explaination ? {q:data.queryString, explaination: data.explaination} : {q:data.queryString};
				if ( data.type != 'undefined' ){
					// invoke sanger grid
					//window.jQuery('#mpi2-search').trigger('search', [{type: data.type, solrParams: data}]);
					
					// invoke dataTable
					//console.log(data);
					
					var oInfos = {};
			 		oInfos.params = $.fn.stringifyJsonAsUrlParams(data);
			 		
			 		oInfos.solrCoreName = facetParams[data.type].solrCoreName;
			 		oInfos.mode = facetParams[data.type].solrCoreName + 'Grid';
			 		oInfos.dataTablePath = dataTablePath;
			 		
			 		var dTable = $.fn.fetchEmptyTable(facetParams[data.type].tableHeader, facetParams[data.type].cols, oInfos.mode);
			 	   			 	    	
			 	   	var title = $.fn.upperCaseFirstLetter(facetParams[data.type].type);	    	
			 	   	var gridTitle = $('<div></div>').attr({'class':'gridTitle'}).html(title);
			 	 
			 	   	$('div#mpi2-search').html('');		 	  
			 	   	$('div#mpi2-search').append(gridTitle, dTable);		 	    	   	
			 	   	
			 	   	$.fn.invokeDataTable(oInfos); 	
			 	   
				}			   	
			},
			loadSideBar: function(event, data){
				// calls left side bar widget				
				window.jQuery('div#leftSideBar').mpi2LeftSideBar({  
					data: data, // key q: query string
			        geneGridElem: 'div#mpi2-search'			                                      
				});				
			},
			redirectedSearch: function(event, data){				
				// Make a hidden form on your page, and submit it here.
			    var form = "<form id='hiddenSrch' action='" + pathname + "' method='post'>"
			    		 + "<input type='text' name='type' value='" + data.type + "'>"
			             + "<input type='text' name='geneFound' value=" + data.geneFound + ">"			                                
			             + "<input type='text' name='queryString' value='" + data.q + "'>"
			             + "</form>";                     

			    window.jQuery('div#bannerSearch').append(form);
			    window.jQuery('form#hiddenSrch').hide().submit();                    
			    } 
		}).click(function(){
				window.jQuery(this).val('');
				$('span.facetCount').text(''); 
	}); 	
	
	// dynamically readjusted position of autosuggest dropdown list due to elastic design of page
	window.jQuery(window).resize(function(){
		var pos = window.jQuery('input#userInput').offset();     
		window.jQuery('ul.ui-autocomplete').css({'position':'absolute', 'top': pos.top + 26, 'left': pos.left}); 
	});
	
});