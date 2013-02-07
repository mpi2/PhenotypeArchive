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

	var pathname = MPI2.searchAndFacetConfig.pathname;
	
	//console.log('start at primer js href: ' + window.location.href);
	//console.log('start at primer js hash: ' + window.location.hash);
		
	// auto-complete is always created
	// load total facetCount of all data types by default when searchAndFacet page loads
	window.jQuery('input#userInput').mpi2AutoComplete({
		
			solrBaseURL_bytemark: MPI2.searchAndFacetConfig.solrBaseURL_bytemark,			
			solrBaseURL_ebi: MPI2.searchAndFacetConfig.solrBaseURL_ebi,
			search_pathname: pathname,			        
			
			loadSideBar: function(event, data){					
				//console.log('loadSideBar: q: '+ data.q + ' core: ' + data.core + ' fq: ' + data.fq);
				// calls left side bar widget	
						
				window.jQuery('div#leftSideBar').mpi2LeftSideBar({  
					data: data, // key q: query string
			        geneGridElem: 'div#mpi2-search'			                                      
				});				
			},
			redirectedSearch: function(event, data){				
				// Make a hidden form on your page, and submit it here.
					
			    var form = "<form id='hiddenSrch' action='" + pathname + "' method='get'>"				
			    		 + "<input type='text' name='core' value='" + data.core + "'>"
			             + "<input type='text' name='fq' value=" + encodeURI(data.fq) + ">"			                                
			             + "<input type='text' name='q' value='" + data.q + "'>"
			             + "</form>";                     

			    window.jQuery('div#bannerSearch').append(form);
			    window.jQuery('form#hiddenSrch').hide().submit();			    
		   } 
		}).click(function(){
				window.jQuery(this).val('');
				//$('span.facetCount').text('');			
	}); 
	
	// dynamically readjusted position of autosuggest dropdown list due to elastic design of page
	window.jQuery(window).resize(function(){
		var pos = window.jQuery('input#userInput').offset();     
		window.jQuery('ul.ui-autocomplete').css({'position':'absolute', 'top': pos.top + 26, 'left': pos.left}); 
	});
	
});