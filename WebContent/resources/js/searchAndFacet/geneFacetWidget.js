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
    $.widget('MPI2.geneFacet', {
        
	    options: {},    
			    
    	_create: function(){
    		// execute only once 	
    		var self = this;
    		
    		var facetDivId = self.element.attr('id');
    		var caller = self.element;
    		delete MPI2.searchAndFacetConfig.commonSolrParams.rows;    	   		  		
		
			caller.find('div.facetCat').click(function(){
				if ( caller.find('span.facetCount').text() != '0' ){
					
					//console.log('facet click');
					var gridName = MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName;
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
					
					caller.parent().find('div.facetCat').removeClass('facetCatUp');					
					
					if ( caller.find('.facetCatList').is(':visible') ){									
						caller.parent().find('div.facetCatList').hide(); // collapse all other facets					
						caller.find('.facetCatList').hide(); // hide itself								
					}
					else {						
						caller.parent().find('div.facetCatList').hide(); // collapse all other facets 
						caller.find('.facetCatList').show(); // show itself					
						$(this).addClass('facetCatUp');						
						
						var solrSrchParams = {};
						var currHashParams = {};	
												
						solrSrchParams = $.extend({}, 
								MPI2.searchAndFacetConfig.commonSolrParams,
								MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams);
																
						if (MPI2.searchAndFacetConfig.facetParams[facetDivId].subFacet_filter_params){						
							solrSrchParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].subFacet_filter_params;							
						}
		              
						solrSrchParams.q = self.options.data.q;									
											
						var hashParams = $.fn.parseHashString(window.location.hash.substring(1));
						
						currHashParams.q = self.options.data.q;
						currHashParams.core = solrCoreName;
						currHashParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
						
						// update hash
						if ( caller.find('table#gFacet td.highlight').size() == 0 ){
							window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);									
						}
						else {
							if ( self.options.data.core != hashParams.coreName ){
								var fqText = caller.find('table#gFacet td.highlight').text();
								if ( caller.find('table#gFacet td.highlight').hasClass('geneStatus') ){
									currHashParams.fq = 'status:"' + fqText +'"';
								}
								else if ( caller.find('table#gFacet td.highlight').hasClass('geneSubtype') ){
									currHashParams.fq = 'marker_type:"' + fqText +'"';
								}	
								window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);
								// reload dataTable
								self._reloadDataTableForHashUrl();
							}							
						}
						
						// dataTable code					
						//console.log('name: ' + MPI2.searchAndFacetConfig.facetParams[facetDivId].topLevelName);
						if ( $('table#'+ gridName).size() != 1 ){
							$.fn.invokeFacetDataTable(solrSrchParams, facetDivId, gridName);						
						}	
					}	
				}
			});				
										
			// click on SUM facetCount to fetch results in grid										
			caller.find('span.facetCount').click(function(event){
				
				if ( $(this).text() != '0' ){ 
					//console.log('facet count click');
					var gridName = MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName;
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
					var solrSrchParams = {}
					var hashParams = {};							
					
					// remove highlight from selected 			
					$('table#gFacet td').removeClass('highlight');
											
					solrSrchParams = $.extend({},		
							MPI2.searchAndFacetConfig.commonSolrParams,
							MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams 
					);
					
					solrSrchParams.q = self.options.data.q;									
									
					hashParams.q = self.options.data.q;
					hashParams.core = solrCoreName;
					hashParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
					
					
					// hash state stuff				   
					window.location.hash = $.fn.stringifyJsonAsUrlParams(hashParams);// + "&core=" + solrCoreName;
						
					// dataTable code					
					$.fn.invokeFacetDataTable(solrSrchParams, facetDivId, gridName);
				}	
			});	
			
    	},
    	    	
    	_applyFacetTopLevelFilter: function(facetDivId){
    		var self = this;  
    		var sTopLevelTerm = null;
    		var oSelectedTopLevel = $('div#'+ facetDivId + ' .facetTable td.highlight'); 
    		var obj = oSelectedTopLevel.siblings().find('a');    				    			
    	
    		$.fn.fetchFilteredDataTable(obj, facetDivId, self.options.data.q);
    	},
 	        	
	    // want to use _init instead of _create to allow the widget being invoked each time by same element
	    _init: function () {
			var self = this;
						
			self._initFacet();			
			$.fn.openFacet(self.options.data.core); 
			
	    },
	    
		_initFacet: function(){
	    	var self = this;
	    	MPI2.searchAndFacetConfig.commonSolrParams.rows = 10;
	    
	    	//var queryParams = $.extend({},{		
	    	var queryParams = $.extend({}, { 
				'rows': 0,
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.field': 'marker_type',				
				'facet.sort': 'count',					
	    		'q': self.options.data.q},
	    		MPI2.searchAndFacetConfig.commonSolrParams,
	    		MPI2.searchAndFacetConfig.facetParams.geneFacet.filterParams
	    	);    	   	
	    	
	    	var queryParamStr = $.fn.stringifyJsonAsUrlParams(queryParams) + '&facet.field=status';
	    	    	
	    	$.ajax({ 				 					
	    		'url': solrUrl + '/gene/select',
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
	    	//$('div#geneFacet span.facetCount').attr({title: 'total number of unique genes'}).text(numFound);
	    	
	    	/*-------------------------------------------------------*/
	    	/* ------ displaying sidebar and update dataTable ------ */
	    	/*-------------------------------------------------------*/
	    	
	    	if (numFound > 0){
	    		
	    		var trs = "<tr class='facetSubCat'><td colspan=2>IMPC Status</td></tr>";
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
						trs += "<tr><td class='geneStatus geneSubfacet'>" + status + "</td><td rel='" + status + "' class='geneSubfacetCount'><a rel='" + status + "' class='status'>" + count + "</a></td></tr>";
					}					
				}	    		
	    		
	    		var unclassified_gene_subType;	    		
	    		trs += "<tr class='facetSubCat'><td colspan=2>Subtype</td></tr>";
	    		var mkr_facets = json.facet_counts['facet_fields']['marker_type'];
	    		for ( var i=0; i<mkr_facets.length; i+=2 ){		    			
	    			//console.log( facets[i] + ' ' + facets[i+1]);
					var type = mkr_facets[i];
					var count = mkr_facets[i+1];			
					if ( type == 'unclassified gene' ){					
						unclassified_gene_subType = "<tr><td class='geneSubtype geneSubfacet'>" + type + "</td><td rel='" + type + "' class='geneSubfacetCount'><a rel='" + type + "' class='subtype'>" + count + "</a></td></tr>";
					}
					else {
						trs += "<tr><td class='geneSubtype geneSubfacet'>" + type + "</td><td rel='" + type + "' class='geneSubfacetCount'><a rel='" + type + "' class='subtype'>" + count + "</a></td></tr>";
					}
	    		} 
	    		if ( unclassified_gene_subType ){
	    			trs += unclassified_gene_subType
	    		}
	    		
	    		
	    		var table = "<table id='gFacet' class='facetTable'>" + trs + "</table>";				
	    		$('div#geneFacet div.facetCatList').html(table);
	    			    		
	    		self._applyGeneGridResultFilterByMarkerSubFacet($('table#gFacet td.geneSubfacetCount a'));	    		
    		}
	    	
	    	/*------------------------------------------------------------------*/
	    	/* ------ load sidebar for hash state when widget is created ------ */
	    	/*------------------------------------------------------------------*/	    			    	
	    	
	    	if ( self.options.data.fq != undefined ){	    		
	    		//console.log('fq: '+ self.options.data.fq);
	    		var subFacet = self.options.data.fq.replace(/\w+:/, '').replace(/"/g,'');
	    		//console.log('gene filtered: '+ subFacet);
	    		var obj = $('div#geneFacet div.facetCatList').find("table#gFacet td[rel='" + subFacet + "']").find('a');
	    		$.fn.fetchFilteredDataTable(obj, 'geneFacet', self.options.data.q);
	    	}	    
	    	else if ( self.options.data.fq == undefined ){ 
	    		//console.log('gene UNfiltered');
	    		var solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.commonSolrParams, MPI2.searchAndFacetConfig.facetParams['geneFacet'].filterParams);
	    		//var hashParams = $.fn.parseHashString(window.location.hash.substring(1));
	    		//solrSrchParams.q = hashParams.q;
    			solrSrchParams.q = self.options.data.q;	    			
    			$.fn.invokeFacetDataTable(solrSrchParams, 'geneFacet', MPI2.searchAndFacetConfig.facetParams['geneFacet'].gridName);  
	    	}
	    },

		_applyGeneGridResultFilterByMarkerSubFacet: function(obj){
			var self = this;

			// subFacet result trigger	
			obj.click(function(){
				// invoke dataTable							
				$.fn.fetchFilteredDataTable($(this), 'geneFacet', self.options.data.q);
			});
		},
   
		_reloadDataTableForHashUrl: function(){
			var self = this;
			var hashParams = $.fn.parseHashString(window.location.hash.substring(1));
    	
    		if ( typeof hashParams.fq !== 'undefined' ){    			
    			var aKV = hashParams.fq.split(':');
    			var sClass = aKV[0];
    			var sRel = aKV[1].replace(/"/g,'');	    		
    			$.fn.fetchFilteredDataTable($('a[rel="' + sRel + '"]'), 'geneFacet', self.options.data.q);
    		}    		
		},
		
	    destroy: function () {    	   
	    	//this.element.empty();
	    	// does not generate selector class
    	    // if using jQuery UI 1.8.x
    	    $.Widget.prototype.destroy.call(this);    	
    	    // if using jQuery UI 1.9.x
    	    //this._destroy();
    	}  
    });
	
}(jQuery));	
	



