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
 * searchAndFacet_primer: entry point to handle url and load search results
 * 
 */
$(document).ready(function(){
	'use strict';	
	alert('reload');
	// back button will not see this js
	MPI2.searchAndFacetConfig.update.pageReload = true;
	
	//console.log('reload');
	var oHashParams = {};
	
	$('span.facetCount').text(''); // default when page loads
	
	$('input#s').val('');  // clears input when pages loads
		
	// default search when search page loads
	if ( /search\/?$/.exec(location.href) ){
		alert('default search load')
		// do default gene search by * when search page loads	
		oHashParams.q = '*:*';		
		//$.fn.fetchSolrFacetCount(oHashParams);
	}
	else if ( window.location.search == '?q=' ){
		// catches user hitting ENTER on search input box of homepage
		document.location.href = baseUrl + '/search';		
	}
	else if ( location.href.indexOf('/search?q=') != -1 
			|| location.href.indexOf('/search#q=*:*') != -1 
			|| location.href.indexOf('/search#q=*') != -1 
			|| location.href.indexOf('/search#fq=') != -1 ){   	
		
		// load page based on url hash parameters	
		
		oHashParams = $.fn.parseHashString(window.location.hash.substring(1));	
		if (typeof oHashParams.fq == 'undefined'){
			oHashParams.noFq = true;
		}
		//console.log(oHashParams);
		
		$.fn.fetchSolrFacetCount(oHashParams);	
	}
	else {
		// do not understand the url, redirect to error page
		document.location.href = baseUrl + '/404.jsp';		
	}
	
	// search via ENTER
	$('input#s').keyup(function (e) {		
	    if (e.keyCode == 13) { // user hits enter
	    	alert('enter: '+ MPI2.searchAndFacetConfig.matchedFacet)
	    	var input = $('input#s').val().trim();
	    	
	    	MPI2.searchAndFacetConfig.update.kwSearch = true;
	    	
	    	if (input == ''){
	    		document.location.href = baseUrl + '/search';
	    		
	    	}
	    	else if (! MPI2.searchAndFacetConfig.matchedFacet){
	    		alert('enter-2');
	    		//document.location.href = baseUrl + '/search?q=' + input;
	    		window.location.search = 'q=' + input;
	    	
	    		delete window.location.hash;
	    		var oHashParams = {};
				oHashParams.noFq = true;
				//$.fn.fetchSolrFacetCount(oHashParams); 
	    	}
	    	else {	
	    		alert('enter-3');
	    		//document.location.href = baseUrl + '/search?q=' + input + '#facet=' + MPI2.searchAndFacetConfig.matchedFacet; // handed over to hash change	    	
	    		window.location.search = 'q=' + input;
	    		window.location.hash = 'facet=' + MPI2.searchAndFacetConfig.matchedFacet;
	    	}
	    }
	});
});
