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
	
	function _updateFacetCount(facet, facetResponse, facetMode){		
		//var num = facetMode ? '' : facetResponse.response.numFound;
		
		num = facetResponse.response.numFound;	
		
		$('div.flist li#' + facet + ' span.fcount').html(num);		
		
		var freezeMode = num == 0 ? true : false;
		$.fn.freezeFacet($('li#' + facet + '.fmcat'), freezeMode);	
	}
	
	function _getParams(facet, oUrlParams){
		facet += 'Facet';
	
		var params = $.extend({}, jsonBase[facet].srchParams, jsonBase[facet].filterParams);
		
		if ( typeof oUrlParams.qf != 'undefined' ){
			params.qf = oUrlParams.qf; 
		}
		params.q = oUrlParams.q; // encoded
		//console.log(facet + ' --- ' + $.fn.stringifyJsonAsUrlParams(params));
		return $.fn.stringifyJsonAsUrlParams(params); // pass in as string, ie, encoded
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
		qDisplay = qDisplay.replace(/\\/g, '');  // unescape for display
		$('input#s').val(qDisplay); 	

		// q to search SOLR
		q = $.fn.process_q(q); 
		oUrlParams.q  = q;

		//console.log('encoded q: ' + oUrlParams.q)
		/* ---- end of q for SOLR --- */
		
		
		/* ---- fq for SOLR --- */
		if ( typeof oUrlParams.fq != 'undefined' ){
			oUrlParams.oriFq = oUrlParams.fq;
			oUrlParams.fq = oUrlParams.fq.replace(/img_/g, '');
			
			if (  typeof oUrlParams.coreName == 'undefined' ){
				jsonBase.geneFacet.filterParams = {'fq': oUrlParams.fq};
			}
		}
		/* ---- end of fq for SOLR --- */

		
		var facetMode = oUrlParams.facetName;
		var oFacets = {};
		oFacets.count = {};	
		
		// facet types are done sequencially; starting from gene		
	    $.ajax({            	    
	    		url: solrUrl + '/gene/select',	    		
	    		data: _getParams('gene', oUrlParams),
	       	    dataType: 'jsonp',
	       	    jsonp: 'json.wrf',
	       	    timeout: 5000,
	       	    success: function (geneResponse) {
	       	    	//console.log('gene');
	       	    	//console.log(geneResponse);
	       	    	$('div.flist li#gene span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
	       	    	oFacets.count.gene = geneResponse.response.numFound;	
	       	    	_updateFacetCount('gene', geneResponse, facetMode);	       	    	
	       	    	_doMPAutoSuggest(geneResponse, oFacets, facetMode, oUrlParams);	            	    
	       	    },
	       	    error: function (jqXHR, textStatus, errorThrown) {	       	                	        
	       	        $('div#facetSrchMsg').html('Error fetching data ...');
	       	    }           	
	    });
	}
	
	function _doMPAutoSuggest(geneResponse, oFacets, facetMode, oUrlParams){		
				
		if ( typeof oUrlParams.fq != 'undefined' && typeof oUrlParams.coreName == 'undefined' ){
			jsonBase.mpFacet.filterParams = {'fq': oUrlParams.fq};
		}
		
		$.ajax({
    	    url: solrUrl + '/mp/select',
    	    data: _getParams('mp', oUrlParams),
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 5000,
    	    success: function (mpResponse) { 
    	    	//console.log('mp');
    	    	//console.log(mpResponse);
    	    	$('div.flist li#mp span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
       	    	oFacets.count.mp = mpResponse.response.numFound;       	    	
       	    	_updateFacetCount('mp', mpResponse, facetMode);	 
    	    	_doDiseaseAutoSuggest(geneResponse, mpResponse, oFacets, facetMode, oUrlParams);  
    	    },
    	    error: function (jqXHR, textStatus, errorThrown) {				         	        
				$('div#facetSrchMsg').html('Error fetching data ...');
			}        	    
		});  			
	}   	
	function _doDiseaseAutoSuggest(geneResponse, mpResponse, oFacets, facetMode, oUrlParams){
		
		if ( typeof oUrlParams.fq != 'undefined' && typeof oUrlParams.coreName == 'undefined' ){
			jsonBase.diseaseFacet.filterParams = {'fq': oUrlParams.fq};
		}	
		
		$.ajax({    	  
    	    url: solrUrl + '/disease/select',	
    	    data: _getParams('disease', oUrlParams),
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 10000,
    	    success: function (diseaseResponse) { 
    	    	//console.log('disease');
    	    	//console.log(diseaseResponse);
    	    	$('div.flist li#disease span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	oFacets.count.disease = diseaseResponse.response.numFound;    	    	
    	    	_updateFacetCount('disease', diseaseResponse, facetMode);	 
    	    	_doTissueAutoSuggest(geneResponse, mpResponse, diseaseResponse, oFacets, facetMode, oUrlParams);    	    	
    	    },
			error: function (jqXHR, textStatus, errorThrown) {			       	        
				$('div#facetSrchMsg').html('Error fetching data ...');
			} 
		});
	}
	
	function _doTissueAutoSuggest(geneResponse, mpResponse, diseaseResponse, oFacets, facetMode, oUrlParams){
		
		jsonBase.maFacet.srchParams.sort = 'ma_term asc';
		
		if ( typeof oUrlParams.fq != 'undefined' && typeof oUrlParams.coreName == 'undefined' ){
			jsonBase.maFacet.filterParams = {'fq': oUrlParams.fq};
		}
		
		$.ajax({
    	    url: solrUrl + '/ma/select',    	    
    	    data: _getParams('ma', oUrlParams),
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 10000,
    	    success: function (maResponse) { 
    	    	//console.log('ma');
    	    	//console.log(maResponse);
    	    	$('div.flist li#ma span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	oFacets.count.ma = maResponse.response.numFound;    	    	
    	    	_updateFacetCount('ma', maResponse, facetMode);	     	    	
    	    	_doPipelineAutoSuggest(geneResponse, mpResponse, diseaseResponse, maResponse, oFacets, facetMode, oUrlParams);
 
    	    },
			error: function (jqXHR, textStatus, errorThrown) {			       	        
				$('div#facetSrchMsg').html('Error fetching data ...');
			} 
		});
	}
		
	function _doPipelineAutoSuggest(geneResponse, mpResponse, diseaseResponse, maResponse, oFacets, facetMode, oUrlParams){
		
		if ( typeof oUrlParams.fq != 'undefined' && typeof oUrlParams.coreName == 'undefined' ){
			jsonBase.pipelineFacet.filterParams = {'fq': oUrlParams.fq};
		}
		
		$.ajax({
    	    url: solrUrl + '/pipeline/select',    	   
    	    data: _getParams('pipeline', oUrlParams),
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 5000,
    	    success: function (pipelineResponse) {  
    	    	//console.log('pipeline');
    	    	//console.log(pipelineResponse);
    	    	$('div.flist li#pipeline span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	oFacets.count.pipeline = pipelineResponse.response.numFound;    	    	
    	    	_updateFacetCount('pipeline', pipelineResponse, facetMode);	 
    	    	_doImageAutosuggest(geneResponse, mpResponse, diseaseResponse, maResponse, pipelineResponse, oFacets, facetMode, oUrlParams); 
    	    },
			error: function (jqXHR, textStatus, errorThrown) {			        	        
				$('div#facetSrchMsg').html('Error fetching data ...');
			} 
		});
	}
	
	function _doImageAutosuggest(geneResponse, mpResponse, diseaseResponse, maResponse, pipelineResponse, oFacets, facetMode, oUrlParams){
		
		if ( typeof oUrlParams.fq != 'undefined' && typeof oUrlParams.coreName == 'undefined' ){
			jsonBase.imagesFacet.filterParams = {'fq': oUrlParams.fq};
		}
		
		$.ajax({
    	    url: solrUrl + '/images/select',   
    	    data: _getParams('images', oUrlParams),
    	    dataType: 'jsonp',
    	    jsonp: 'json.wrf',
    	    timeout: 5000,
    	    success: function (imagesResponse) {  
    	    	//console.log('images');
    	    	//console.log(imagesResponse);
    	    	
    	    	$('div.flist li#images span.fcount').html(MPI2.searchAndFacetConfig.searchSpin);
    	    	
    	    	oFacets.count.images = imagesResponse.response.numFound;    	    	
    	    	_updateFacetCount('images', imagesResponse, facetMode);	 
    	    
    	    	/* now check which core needs to be displayed by default in the order of 
    	    	 * gene -> mp -> ma -> pipeline -> images -> disease
    	    	 * ie, fetch facet full result for that facet and display only facet count for the rest of the facets 
    	    	 * Other facet results will be fetched on demand */
    	    	
    	    	$('div#facetSrchMsg').html('&nbsp;');

    	    	if ( ! _setSearchMode(oFacets.count) ){
    	    		// nothing found   
    	    		$.fn.showNotFoundMsg();
    	    		
    	    		$('ul#facetFilter li.ftag a').each(function(){
    					$(this).click(function(){
    						var field = $(this).attr('rel').split('|')[1];
    						var val   = $(this).attr('rel').split('|')[2];
    						var solrField = MPI2.searchAndFacetConfig.summaryFilterVal2FqStr[field]; // label conversion
    						
    						var fqStr = typeof solrField == 'undefined' ? field + ':' + $.fn.dquote(val) : solrField + $.fn.dquote(val); 
    						$(this).remove();
    						$.fn.resetUrlFqStr(fqStr); 
    					})
    				});	
    	    		
    	    	}
    	    	else {    	    	    		
    	        	// remove all previous facet results before loading new facet results

    	    		var defaultCore; 
    	    		var firstCoreWithResult = _setSearchMode(oFacets.count); 
    	    		
    	    		
    	    		if ( typeof facetMode == 'undefined' || oFacets.count[facetMode] == 0 ){
    	    			defaultCore =firstCoreWithResult;
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
