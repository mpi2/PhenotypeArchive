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
 * functions used for processing user input via SOLR calls. Process the response and 
 * displays the results as facets on the left bar.
 * 
 */

(function($){	
	var jsonBase = MPI2.searchAndFacetConfig.facetParams;
	
	function _updateMainFacetCount(facet, numFound, facetMode){		
		
		$('div.flist li#' + facet + ' span.fcount').html(numFound);		
		
		var freezeMode = numFound == 0 ? true : false;
		$.fn.freezeFacet($('li#' + facet + '.fmcat'), freezeMode);	
	}
	
	function _getParams(oUrlParams){
		
		var coreQry = {};
		
		var aCores = MPI2.searchAndFacetConfig.megaCores;
		for ( var i=0; i<aCores.length; i++ ){
			var core = aCores[i];  
			var facet = core + 'Facet';
			var params = $.extend({}, jsonBase[facet].srchParams, jsonBase[facet].filterParams);
			delete params.fl;
			params.fq = $.fn.getCurrentFq(core).replace(/img_/g,'');
			
			if ( typeof oUrlParams.qf != 'undefined' ){
				params.qf = oUrlParams.qf; 
			}
			params.q = oUrlParams.q; // encoded
			//console.log(facet + ' --- ' + $.fn.stringifyJsonAsUrlParams(params));
			coreQry[core] = $.fn.stringifyJsonAsUrlParams(params);
		}
		//console.log(JSON.stringify(coreQry));
		return JSON.stringify(coreQry);
	}
	
	$.fn.fetchSolrFacetCount = function(oUrlParams){		
		
		/* ---- q for SOLR --- */ 
		var q = oUrlParams.q;
		
		if ( typeof q == 'undefined' ){
			// check search kw	
			q = window.location.search != '' ? $.fn.fetchQueryStr() : '*:*';
		}
		
		// q to display in input box
		var qDisplay = q == '*:*'  ? '' : decodeURIComponent(q);
		qDisplay = qDisplay.replace(/\\/, '');  // unescape for display
		$('input#s').val(qDisplay); 	

		// q to search SOLR
		q = $.fn.process_q(q); 
		oUrlParams.q  = q;

		var facetMode = oUrlParams.facetName;
		var oFacets = {};
		oFacets.count = {};	
		
		// one query to solr and get back 6 sets of json each of which corresponds to one of the 6 cores
	    $.ajax({url: baseUrl + '/querybroker',
	    	data: {'q' : _getParams(oUrlParams)},
	    	type: 'post',
	    	async: false,
	    	success: function(facetCountJson) {
	    		//console.log(facetCountJson);
	    		MPI2.searchAndFacetConfig.update.mainFacetDone = true;
	    		
	    		oFacets.count = facetCountJson;
	    		//console.log(oFacets.count.gene);
	    		
	    		for ( var facet in facetCountJson ){
	    			_updateMainFacetCount(facet, facetCountJson[facet], facetMode);
	    		}
	    		
	    		$('div#facetSrchMsg').html('&nbsp;');

	    		
	    		var firstCoreWithResult = $.fn.setSearchMode(oFacets.count); 
	    		
    	    	if ( ! firstCoreWithResult ){
    	    		// nothing found   
    	    		MPI2.searchAndFacetConfig.update.mainFacetNone = true;
    	    		
    	    	}
    	    	else {    	    	    		
    	        	// remove all previous facet results before loading new facet results

    	    		var defaultCore; 
    	    		
    	    		if ( typeof facetMode == 'undefined' || oFacets.count[facetMode] == 0 ){
    	    			defaultCore = firstCoreWithResult;
        	    	}
    	    		else {
    	    			defaultCore = facetMode;
    	    		}
    	    		
    	    		$('li.fmcat > ul').html(''); 
    	    		
    	        	var widgetName = defaultCore+'Facet';    
    	        	oUrlParams.fq = oUrlParams.fq ? oUrlParams.fq : jsonBase[widgetName].fq; 
    	        	oUrlParams.oriFq = oUrlParams.oriFq ? oUrlParams.oriFq : jsonBase[widgetName].fq; 
    	        	oUrlParams.widgetName = widgetName;
    	        	
    	        	window.jQuery('li#' + defaultCore)[widgetName]({
    					data: {	   							 
    							core: defaultCore,    							
    							facetCount: oFacets.count[defaultCore],
    							hashParams: oUrlParams
    							},
    			        geneGridElem: 'div#mpi2-search'			                                      
    				});
    	      	  	
    	        	// load none-zero facet results on demand    	        	
    	        	var aCores = MPI2.searchAndFacetConfig.megaCores;
    	        	
    	        	//delete active core, no need to invoke again  
    	        	var index;// = aCores.indexOf(coreName);
    	        	for ( var i=0; i< aCores.length; i++){
    	        		if (aCores[i] == defaultCore ){
    	        			index = i;
    	        		}
    	        	}
    	        	aCores.splice(index, 1); // remove core that has the index result already in dataTable  
    	        	
    	        	for ( var i=0; i< aCores.length; i++){
    	        		var core = aCores[i];
    	        		_prepareCores(core, oUrlParams.q, oFacets, oUrlParams.fq, facetMode);
    	        	} 
    	        	// restore the spliced core when done
    	        	aCores.push(defaultCore);
    	    	}   	    
	    	},
	    	error: function(jqXHR, textStatus, errorThrown) {
	    		$('div#facetSrchMsg').html('Error fetching data ...');
	    	}
	    });
	}
	
	function _prepareCores(core, q, oFacets, fq, facetMode){		
		var widgetName = core + 'Facet';		
		
		window.jQuery('li#' + core).click(function(){
		
			var $this = window.jQuery(this);
			
			// check widget has not been created			
			if ( typeof $this.data(widgetName) === 'undefined' ){
				var hashParams = {};						
				
				// core or facet?
				if ( typeof facetMode != 'undefined' ){
					hashParams.fq = fq ? fq : jsonBase[widgetName].fq; 
				}
				else {
					hashParams.fq = jsonBase[widgetName].fq; 
				}
				
	        	hashParams.widgetName = widgetName;
	        	hashParams.q = q;

	        	if ( $this.find('ul').html() == '' && $this.find('span.fcount').text() != '0' ){	
					$this[widgetName]({  
						data: {							 
							core: core,							
							facetCount: oFacets.count[core],
							hashParams: hashParams
							},
							geneGridElem: 'div#mpi2-search'							
					});					
				}
			}			
		});
	}
	
})(jQuery);