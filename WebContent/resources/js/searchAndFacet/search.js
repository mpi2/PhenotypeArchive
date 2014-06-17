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
	
	function _updateFacetCount(facet, facetResponse, facetMode){		
		//var num = facetMode ? '' : facetResponse.response.numFound;
		num = facetResponse.response.numFound;		
		$('div.flist li#' + facet + ' span.fcount').html(num);		
	}
	
	var jsonBase = MPI2.searchAndFacetConfig.facetParams;
	
	$.fn.fetchSolrFacetCount = function(oUrlHashParams){		
		//console.log(oUrlHashParams);	
		var q = oUrlHashParams.q;
		
		// for match text highlighting
		/*var hlParams = {};
		hlParams.hl = 'true';
		hlParams['hl.snippets']=100; // otherwise only one in each field is return, and 100 should be enough to catch all for synonyms field, etc    	    	
		hlParams['hl.fl'] = '*';    		
		*/
		if ( typeof q == 'undefined' ){
			// check search kw						
			if ( window.location.search != '' ){				
				q = window.location.search.replace(/&.+/, '').replace('?q=','');				
				q = q.replace(/\+/g, ' ');				
				$('input#s').val(decodeURI(q));				
			}
			else {
				q = '*:*';
			}
		}
						
		q = decodeURI(q);	
		
		var facetMode = oUrlHashParams.facetName;
				
		var oFacets = {};
		oFacets.count = {};	
		if ( typeof oUrlHashParams.fq != 'undefined' ){
			jsonBase.geneFacet.filterParams = {'fq': oUrlHashParams.fq};
		}
		
		jsonBase.geneFacet.srchParams.q = q;
		//console.log($.extend({}, jsonBase.geneFacet.srchParams, jsonBase.geneFacet.filterParams)); 
	 	// facet types are done sequencially; starting from gene		
	    $.ajax({            	    
	    		url: solrUrl + '/gene/select',	    		
	       	   // data: $.extend({}, jsonBase.geneFacet.srchParams, oUrlHashParams.fq ? jsonBase.geneFacet.filterParams = {'fq': oUrlHashParams.fq} : jsonBase.geneFacet.filterParams),
	    		data: $.extend({}, jsonBase.geneFacet.srchParams, jsonBase.geneFacet.filterParams),
	       	    dataType: 'jsonp',
	       	    jsonp: 'json.wrf',
	       	    timeout: 5000,
	       	    success: function (geneResponse) {	  
	       	    	//console.log(geneResponse);
	       	    	$('div.flist li#gene span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
	       	    	oFacets.count.gene = geneResponse.response.numFound;	
	       	    	_updateFacetCount('gene', geneResponse, facetMode);	       	    	
	       	    	_doMPAutoSuggest(geneResponse, q, oFacets, facetMode, oUrlHashParams);	            	    
	       	    },
	       	    error: function (jqXHR, textStatus, errorThrown) {	       	                	        
	       	        $('div#facetSrchMsg').html('Error fetching data ...');
	       	    }           	
	    });
	}
	
	function _doMPAutoSuggest(geneResponse, q, oFacets, facetMode, oUrlHashParams){		
				
		jsonBase.mpFacet.srchParams.q = q;		
		if ( typeof oUrlHashParams.fq != 'undefined' ){
			jsonBase.mpFacet.filterParams = {'fq': oUrlHashParams.fq};
		}
		
		var oParams = {};		
        oParams = $.fn.getSolrRelevanceParams('mp', q, oParams);				
		//console.log($.extend({}, jsonBase.mpFacet.srchParams, jsonBase.mpFacet.filterParams, oParams));		
		
		$.ajax({
    	    url: solrUrl + '/mp/select',
    	    data: $.extend({}, jsonBase.mpFacet.srchParams, jsonBase.mpFacet.filterParams, oParams),
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 5000,
    	    success: function (mpResponse) { 
    	    	//console.log(mpResponse);
    	    	$('div.flist li#mp span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
       	    	oFacets.count.mp = mpResponse.response.numFound;       	    	
       	    	_updateFacetCount('mp', mpResponse, facetMode);	 
    	    	_doDiseaseAutoSuggest(geneResponse, mpResponse, q, oFacets, facetMode, oUrlHashParams);  
    	    },
    	    error: function (jqXHR, textStatus, errorThrown) {				         	        
				$('div#facetSrchMsg').html('Error fetching data ...');
			}        	    
		});  			
	}   	
	function _doDiseaseAutoSuggest(geneResponse, mpResponse, q, oFacets, facetMode, oUrlHashParams){
		
		jsonBase.diseaseFacet.srchParams.q = q;		
		if ( typeof oUrlHashParams.fq != 'undefined' ){
			jsonBase.diseaseFacet.filterParams = {'fq': oUrlHashParams.fq};
		}	
		//console.log($.fn.stringifyJsonAsUrlParams(jsonBase.diseaseFacet.srchParams));
		$.ajax({    	  
    	    url: solrUrl + '/disease/select',	
    	    data: $.extend({}, jsonBase.diseaseFacet.srchParams, jsonBase.diseaseFacet.filterParams),
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 10000,
    	    success: function (diseaseResponse) { 
    	    	$('div.flist li#disease span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	oFacets.count.disease = diseaseResponse.response.numFound;    	    	
    	    	_updateFacetCount('disease', diseaseResponse, facetMode);	 
    	    	_doTissueAutoSuggest(geneResponse, mpResponse, diseaseResponse, q, oFacets, facetMode, oUrlHashParams);    	    	
    	    },
			error: function (jqXHR, textStatus, errorThrown) {			       	        
				$('div#facetSrchMsg').html('Error fetching data ...');
			} 
		});
	}
	
	function _doTissueAutoSuggest(geneResponse, mpResponse, diseaseResponse, q, oFacets, facetMode, oUrlHashParams){
		jsonBase.maFacet.srchParams.q = q;	
		jsonBase.maFacet.srchParams.sort = 'ma_term asc';
		if ( typeof oUrlHashParams.fq != 'undefined' ){
			jsonBase.maFacet.filterParams = {'fq': oUrlHashParams.fq};
		}
		
		$.ajax({
    	    url: solrUrl + '/ma/select',    	    
    	    data: $.extend({}, jsonBase.maFacet.srchParams, jsonBase.maFacet.filterParams),
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 10000,
    	    success: function (maResponse) {    	    	   	    	    		    	   	    	
    			
    	    	$('div.flist li#ma span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	oFacets.count.ma = maResponse.response.numFound;    	    	
    	    	_updateFacetCount('ma', maResponse, facetMode);	     	    	
    	    	_doPipelineAutoSuggest(geneResponse, mpResponse, diseaseResponse, maResponse, q, oFacets, facetMode, oUrlHashParams);
    	    },
			error: function (jqXHR, textStatus, errorThrown) {			       	        
				$('div#facetSrchMsg').html('Error fetching data ...');
			} 
		});
	}
		
	function _doPipelineAutoSuggest(geneResponse, mpResponse, diseaseResponse, maResponse, q, oFacets, facetMode, oUrlHashParams){
		
		jsonBase.pipelineFacet.srchParams.q = q;		
		if ( typeof oUrlHashParams.fq != 'undefined' ){
			jsonBase.pipelineFacet.filterParams = {'fq': oUrlHashParams.fq};
		}
		$.ajax({
    	    url: solrUrl + '/pipeline/select',    	   
    	    data: $.extend({}, jsonBase.pipelineFacet.srchParams, jsonBase.pipelineFacet.filterParams),
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 5000,
    	    success: function (pipelineResponse) {    	    	
    	    	$('div.flist li#pipeline span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	oFacets.count.pipeline = pipelineResponse.response.numFound;    	    	
    	    	_updateFacetCount('pipeline', pipelineResponse, facetMode);	 
    	    	_doImageAutosuggest(geneResponse, mpResponse, diseaseResponse, maResponse, pipelineResponse, q, oFacets, facetMode, oUrlHashParams); 
    	    },
			error: function (jqXHR, textStatus, errorThrown) {			        	        
				$('div#facetSrchMsg').html('Error fetching data ...');
			} 
		});
	}
	
	function _doImageAutosuggest(geneResponse, mpResponse, diseaseResponse, maResponse, pipelineResponse, q, oFacets, facetMode, oUrlHashParams){
		
		jsonBase.imagesFacet.srchParams.q = q;		
		if ( typeof oUrlHashParams.fq != 'undefined' ){
			jsonBase.imagesFacet.filterParams = {'fq': oUrlHashParams.fq};
		}
		//console.log($.fn.stringifyJsonAsUrlParams($.extend({}, jsonBase.imagesFacet.srchParams, jsonBase.imagesFacet.filterParams)));
		
		$.ajax({
    	    url: solrUrl + '/images/select',   
    	    data: $.extend({}, jsonBase.imagesFacet.srchParams, jsonBase.imagesFacet.filterParams),
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 5000,
    	    success: function (imagesResponse) {  
    	    	$('div.flist li#images span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	oFacets.count.images = imagesResponse.response.numFound;    	    	
    	    	_updateFacetCount('images', imagesResponse, facetMode);	 
    	    
    	    	/* now check which core needs to be displayed by default in the order of 
    	    	 * gene -> mp -> ma -> pipeline -> images -> disease
    	    	 * ie, fetch facet full result for that facet and display only facet count for the rest of the facets 
    	    	 * Other facet results will be fetched on demand */
    	    	
    	    	var hashParams = $.fn.parseHashString(window.location.hash.substring(1));    	    	
    	    	    	    	
    	    	var coreName, facetName;
    	    	
    	    	if ( hashParams.coreName ){
    	    		coreName = hashParams.coreName;
    	    	}
    	    	else if (hashParams.facetName ){
    	    		facetName = hashParams.facetName;    	    		
    	    	}
    	    	else if ( facetMode ){    	    		
    	    		facetName = facetMode;    	    		
    	    	}
    	    	else {
    	    		coreName = _setSearchMode(oFacets.count);
    	    	}    	    	   	    	
    	    	
    	    	$('div#facetSrchMsg').html('&nbsp;');
    	    	
    	    	if ( ! coreName && ! facetName ){
    	    		// nothing found    	    		   	    	
    	    		$('div#mpi2-search').html('INFO: Search keyword "' + decodeURI(q) + '" has returned no entry in the database');    	    	    	    		
    	    	}
    	    	else {    	    	    		
    	        	// remove all previous facet results before loading new facet results
    	    		var thisCore = coreName ? coreName : facetName; 
    	        	$('li.fmcat > ul').html(''); 
    	        	
    	        	//var widgetName = coreName+'Facet'; 
    	        	var widgetName = thisCore+'Facet';    
    	        	hashParams.fq = hashParams.fq ? hashParams.fq : jsonBase[widgetName].fq;    	        	    	        	
    	        	hashParams.widgetName = widgetName;
    	        	hashParams.q = q;
    	        	hashParams.noFq = oUrlHashParams.noFq;
    	        	
    	        	window.jQuery('li#' + thisCore)[widgetName]({
    					data: {	   							 
    							core: coreName,    							
    							//qf: jsonBase[widgetName].qf,
    							facetCount: oFacets.count[thisCore],
    							hashParams: hashParams
    							},
    			        geneGridElem: 'div#mpi2-search'			                                      
    				});
    	      	  	
    	        	// load none-zero facet results on demand    	        	
    	        	var aCores = MPI2.searchAndFacetConfig.megaCores;
    	        	
    	        	//delete active core, no need to invoke again  
    	        	
    	        	var index;// = aCores.indexOf(coreName);
    	        	for ( var i=0; i< aCores.length; i++){
    	        		if (aCores[i] == thisCore ){
    	        			index = i;
    	        		}
    	        	}
    	        	aCores.splice(index, 1); // remove core that has the index result already in dataTable  
    	        	
    	        	for ( var i=0; i< aCores.length; i++){
    	        		var core = aCores[i];
    	        		if ( oFacets.count[core] != 0 ){    	        	
    	        			_prepareCores(core, q, oFacets, oUrlHashParams.fq, facetMode);
    	        		}
    	        	} 
    	        	// restore the spliced core when done
    	        	aCores.push(thisCore);
    	    	}   	    	
    	    },
			error: function (jqXHR, textStatus, errorThrown) {			        	        
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
	        	
				//if ( $this.find('.facetCatList').html() == '' && $this.find('span.facetCount').text() != '0' ){
				if ( $this.find('ul').html() == '' && $this.find('span.fcount').text() != '0' ){	
					$this[widgetName]({  
						data: {							 
							core: core,							
							//qf: jsonBase[core + 'Facet'].qf,
							facetCount: oFacets.count[core],
							hashParams: hashParams
							},
							geneGridElem: 'div#mpi2-search'							
					});					
				}
			}			
		});
	}
	
	function _setSearchMode(oCounts){
				
		
		// priority order of facet to be opened based on search result
		if ( oCounts.gene != 0 ){			
			return 'gene';
		}			
		else if ( oCounts.mp != 0){				
			return 'mp';			
		}  
		else if ( oCounts.disease != 0 ){    			
			return 'disease';						
		}
		else if ( oCounts.ma != 0){				
			return 'ma';			
		} 
		else if ( oCounts.pipeline != 0 ){    			
			return 'pipeline';						
		}
		else if ( oCounts.images != 0 ){    			
			return 'images';						
		}				
		else {
			return false; // nothing found
		}
	}	
	
	
})(jQuery);
