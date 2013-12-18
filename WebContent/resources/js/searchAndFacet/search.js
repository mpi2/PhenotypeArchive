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
 * functions used for processing user input via SOLR calls. Process the response and 
 * displays the results as facets on the left bar.
 * 
 */

(function($){	
	var jsonBase = MPI2.searchAndFacetConfig.facetParams;
	
	$.fn.fetchSolrFacetCount = function(oUrlHashParams){		
		
		var q = oUrlHashParams.q ? oUrlHashParams.q : '*:*';
		var oFacets = {};
		oFacets.count = {};		
						
		jsonBase.geneFacet.srchParams.q = q;
		
	 	// facet types are done sequencially; starting from gene	 		
	    $.ajax({            	    
	    		url: solrUrl + '/gene/select',	       	
	       	    data: $.extend({}, jsonBase.geneFacet.srchParams, jsonBase.geneFacet.filterParams),
	       	    dataType: 'jsonp',
	       	    jsonp: 'json.wrf',
	       	    timeout: 5000,
	       	    success: function (geneResponse) {	       	    	
	       	    	$('div#geneFacet span.facetCount').html(MPI2.searchAndFacetConfig.searchSpin);
	       	    	oFacets.count.gene = geneResponse.response.numFound;	       	    	
	       	    	$('div#geneFacet span.facetCount').html(oFacets.count.gene);	       	    	
	       	    	_doMPAutoSuggest(geneResponse, q, oFacets);	            	    
	       	    },
	       	    error: function (jqXHR, textStatus, errorThrown) {	       	                	        
	       	        $('div#facetBrowser').html('Error fetching data ...');
	       	    }            	
	    });
	}
	
	function _doMPAutoSuggest(geneResponse, q, oFacets){		
		
		jsonBase.mpFacet.srchParams.q = q;
		
		$.ajax({
    	    url: solrUrl + '/mp/select',
    	    data: $.extend({}, jsonBase.mpFacet.srchParams, jsonBase.mpFacet.filterParams),
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 5000,
    	    success: function (mpResponse) { 
    	    	//console.log(mpResponse);
    	    	$('div#mpFacet span.facetCount').html(MPI2.searchAndFacetConfig.searchSpin);
       	    	oFacets.count.mp = mpResponse.response.numFound;
       	    	$('div#mpFacet span.facetCount').html(oFacets.count.mp);
       	    	
    	    	_doDiseaseAutoSuggest(geneResponse, mpResponse, q, oFacets);  
    	    },
    	    error: function (jqXHR, textStatus, errorThrown) {				         	        
				$('div#facetBrowser').html('Error fetching data ...');
			}        	    
		});  			
	}   	
	function _doDiseaseAutoSuggest(geneResponse, mpResponse, q, oFacets){
		
		jsonBase.diseaseFacet.srchParams.q = q;		
		jsonBase.diseaseFacet.srchParams.fq = jsonBase.diseaseFacet.fq;
		
		//console.log($.fn.stringifyJsonAsUrlParams(jsonBase.diseaseFacet.srchParams));
		$.ajax({    	  
    	    url: solrUrl + '/disease/select',	
    	    data: jsonBase.diseaseFacet.srchParams,
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 10000,
    	    success: function (diseaseResponse) { 	   	    	    		    	   	    	
    			
    	    	$('div#diseaseFacet span.facetCount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	oFacets.count.disease = diseaseResponse.response.numFound;
    	    	$('div#diseaseFacet span.facetCount').html(oFacets.count.disease);    	    	
    	    	_doTissueAutoSuggest(geneResponse, mpResponse, diseaseResponse, q, oFacets);    	    	
    	    },
			error: function (jqXHR, textStatus, errorThrown) {			       	        
				$('div#facetBrowser').html('Error fetching data ...');
			} 
		});
	}
	
	function _doTissueAutoSuggest(geneResponse, mpResponse, diseaseResponse, q, oFacets){
		jsonBase.maFacet.srchParams.q = q;	
		jsonBase.maFacet.srchParams.sort = 'ma_term asc';
		jsonBase.maFacet.srchParams.fq = jsonBase.maFacet.fq;
				
		$.ajax({
    	    url: solrUrl + '/ma/select',
    	    data: jsonBase.maFacet.srchParams,
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 10000,
    	    success: function (maResponse) {    	    	   	    	    		    	   	    	
    			
    	    	$('div#maFacet span.facetCount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	oFacets.count.ma = maResponse.response.numFound;
    	    	$('div#maFacet span.facetCount').html(oFacets.count.ma);
    	    	    	    	
    	    	_doPipelineAutoSuggest(geneResponse, mpResponse, diseaseResponse, maResponse, q, oFacets);
    	    },
			error: function (jqXHR, textStatus, errorThrown) {			       	        
				$('div#facetBrowser').html('Error fetching data ...');
			} 
		});
	}
		
	function _doPipelineAutoSuggest(geneResponse, mpResponse, diseaseResponse, maResponse, q, oFacets){
		
		jsonBase.pipelineFacet.srchParams.q = q;	
		
		$.ajax({
    	    url: solrUrl + '/pipeline/select',
    	    data: jsonBase.pipelineFacet.srchParams,
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 5000,
    	    success: function (pipelineResponse) {
    	    	
    	    	$('div#pipelineFacet span.facetCount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	oFacets.count.pipeline = pipelineResponse.response.numFound;
    	    	$('div#pipelineFacet span.facetCount').html(oFacets.count.pipeline);
    	    	
    	    	_doImageAutosuggest(geneResponse, mpResponse, diseaseResponse, maResponse, pipelineResponse, q, oFacets); 
    	    },
			error: function (jqXHR, textStatus, errorThrown) {			        	        
				$('div#facetBrowser').html('Error fetching data ...');
			} 
		});
	}
	
	function _doImageAutosuggest(geneResponse, mpResponse, diseaseResponse, maResponse, pipelineResponse, q, oFacets){
		
		jsonBase.imagesFacet.srchParams.q = q;	
		
		$.ajax({
    	    //url: solrUrl + '/images/select',
    	    url: 'http://localhost:8983/solr/images/select',
    	    data: jsonBase.imagesFacet.srchParams,
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 5000,
    	    success: function (imagesResponse) {  
    	    	$('div#imagesFacet span.facetCount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	oFacets.count.images = imagesResponse.response.numFound;
    	    	$('div#imagesFacet span.facetCount').html(oFacets.count.images);    	        
    	    
    	    	/* now check which core needs to be displayed by default in the order of 
    	    	 * gene -> mp -> ma -> pipeline -> images -> disease
    	    	 * ie, fetch facet full result for that facet and display only facet count for the rest of the facets 
    	    	 * Other facet results will be fetched on demand */
    	    	var hashParams = $.fn.parseHashString(window.location.hash.substring(1));
    	    	var coreName = hashParams.coreName ? hashParams.coreName : _setSearchMode(oFacets.count);
    	    	
    	    	$('div#facetBrowser').html('Search results ...');
    	    	
    	    	if ( ! coreName ){
    	    		// nothing found
    	    		$('div#userKeyword').html('Search keyword: ' + q + ' has returned no entry in the database');    	    	
    	    		$('div#mpi2-search').html('');
    	    		$('div.facetCatList').html('');
    	    		$('div.facetCat').removeClass('facetCatUp');	    	    		
    	    	}
    	    	else {    	    	    		
    	        	// remove all previous facet results before loading new facet results
    	        	$('div.facetCatList').html('');  
    	        	
    	        	var widgetName = coreName+'Facet';    	        				
    	        	window.jQuery('div#' + coreName + 'Facet')[widgetName]({
    					data: {	q: q, 
    							core: coreName, 
    							fq: hashParams.fq ? hashParams.fq : jsonBase[widgetName].fq,
    							qf: jsonBase[widgetName].qf,
    							facetCount: oFacets.count[coreName]
    							},
    			        geneGridElem: 'div#mpi2-search'			                                      
    				});
    	      	  	
    	        	// load none-zero facet results on demand    	        	
    	        	var aCores = MPI2.searchAndFacetConfig.cores;
    	        	
    	        	//delete active core, no need to invoke again  
    	        	
    	        	var index;// = aCores.indexOf(coreName);
    	        	for ( var i=0; i< aCores.length; i++){
    	        		if (aCores[i] == coreName ){
    	        			index = i;
    	        		}
    	        	}
    	        	aCores.splice(index, 1); // remove core that has the index result already in dataTable  
    	        	
    	        	for ( var i=0; i< aCores.length; i++){
    	        		var core = aCores[i];
    	        		if ( oFacets.count[core] != 0 ){    	        	
    	        			_prepareCores(core, q, oFacets);
    	        		}
    	        	}    	        		        	
    	    	}    	    	
    	    },
			error: function (jqXHR, textStatus, errorThrown) {			        	        
				$('div#facetBrowser').html('Error fetching data ...');
			} 
		});
	}
		
	function _prepareCores(core, q, oFacets){		
		
		var widgetName = core + 'Facet';		
		
		window.jQuery('div#' + core + 'Facet').click(function(){
		
			var $this = window.jQuery(this);
			
			// check widget has not been created			
			if ( typeof $this.data(widgetName) === 'undefined' ){
				var hashParams = {};
				
				//hashParams.q = q;					
				//hashParams.core = core;
				//hashParams.fq = jsonBase[core + 'Facet'].fq;
				
				//window.location.hash = $.fn.stringifyJsonAsUrlParams(hashParams);						
				
				if ( $this.find('.facetCatList').html() == '' && $this.find('span.facetCount').text() != '0' ){					
					$this[widgetName]({  
						data: {q: q, core: core, 
							fq: jsonBase[core + 'Facet'].fq,
							qf: jsonBase[core + 'Facet'].qf,
							facetCount: oFacets.count[core]
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