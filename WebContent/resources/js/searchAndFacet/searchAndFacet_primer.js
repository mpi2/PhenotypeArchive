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
		
	var oHashParams = {};
	
	$('span.facetCount').text(''); // default when page loads
	
	$('input#userInput').val('');  // clears input when pages loads
	
	// default search when search page loads
	if ( /search\/?$/.exec(location.href) ){
		// do default gene search by * when search page loads	
		oHashParams.q = '*:*';		
		$.fn.fetchSolrFacetCount(oHashParams);
	}	
	else if ( location.href.indexOf('/search#') != -1 ){		
		// load page based on url hash parameters		
		oHashParams = $.fn.parseHashString(window.location.hash.substring(1));	
		
		//MPI2.searchAndFacetConfig.pageLoad = true;
		console.log(oHashParams);
		
		/*if (oHashParams.facetName ){
			var pageReload = true;	
			oHashParams.widgetName = oHashParams.facetName + 'Facet';
			
    		$.fn.parseUrlForFacetCheckboxAndTermHighlight(oHashParams, pageReload);	 
		}*/
		//else {
			$.fn.fetchSolrFacetCount(oHashParams);
		//}
	}
	
	// search via ENTER
	$('input#userInput').keyup(function (e) {		
	    if (e.keyCode == 13) { // user hits enter
	    	
	    	var input = $('input#userInput').val();
	    	//console.log('user input search: ' + input);
	    	if (input == ''){
	    		document.location.href = baseUrl + '/search';
	    	}
	    	else {
	    		document.location.href = baseUrl + '/search?q=' + input; // handed over to hash change	    	
	    	}
	    }
	}).click(function(){
		$(this).val(''); // clears input 
	});
	
	// search via button click
	$('button#acSearch').click(function(){
		
		var input = $('input#userInput').val();
		//console.log('button search in search and facet= ' + input);
		if (input == ''){
    		document.location.href = baseUrl + '/search';
    	}
    	else {
    		document.location.href = baseUrl + '/search?q=' + input; // handed over to hash change 
    	}		
	});		

	
	// dynamically readjusted position of autosuggest dropdown list due to elastic design of page
	window.jQuery(window).resize(function(){
		var pos = window.jQuery('input#userInput').offset();     
		window.jQuery('ul.ui-autocomplete').css({'position':'absolute', 'top': pos.top + 26, 'left': pos.left}); 
	});
	
});