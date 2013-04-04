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
    $.widget('MPI2.mpFacet', {
        
	    options: {},	
     
    	_create: function(){
    		// execute only once 	
    		var self = this;	
    		var facetDivId = self.element.attr('id');
    		var caller = self.element;
    		delete MPI2.searchAndFacetConfig.commonSolrParams.rows;    	   		  		
		
			caller.find('div.facetCat').click(function(){
				if ( caller.find('span.facetCount').text() != '0' ){
					
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
						
						solrSrchParams.q = self.options.data.q;									
						
						var hashParams = $.fn.parseHashString(window.location.hash.substring(1));
						
						currHashParams.q = self.options.data.q;
						currHashParams.core = solrCoreName;
						currHashParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
						
						// update hash
						if ( caller.find('table#mpFacet td.highlight').size() == 0 ){
							window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);									
						}
						else {
							if ( self.options.data.core != hashParams.coreName ){
								var fqText = caller.find('table#mpFacet td.highlight').text() + ' phenotype';								
								currHashParams.fq = 'ontology_subset:* AND top_level_mp_term:"' + fqText +'"';								
								window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);
								
								// reload dataTable							
								self._reloadDataTableForHashUrl(fqText);
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
			//$('span.facetCount').click(function(){								
			caller.find('span.facetCount').click(function(){	
				if ( $(this).text() != '0' ){
					var gridName = MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName;
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
					var solrSrchParams = {}
					var hashParams = {};							
					
					// remove highlight from selected 							
					$('table#mpFacet td').removeClass('highlight');
						solrSrchParams = $.extend({}, 
								MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams, 
								MPI2.searchAndFacetConfig.commonSolrParams);
						
					MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams.fq = "ontology_subset:*";
					
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
 	        	
	    // want to use _init instead of _create to allow the widget being invoked each time by same element
	    _init: function () {
			var self = this;
			
			self._initFacet();			
			$.fn.openFacet(self.options.data.core);			
	    },
	    
	    _initFacet: function(){
	    	var self = this;
	    	
	    	var queryParams = $.extend({}, {				
				'fq': 'ontology_subset:*',
				'rows': 0, // override default
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.field': 'top_level_mp_term',
				'facet.sort': 'index',						
				'q.option': 'AND',
				'q': self.options.data.q}, MPI2.searchAndFacetConfig.commonSolrParams);			
	    
	    	$.ajax({	
	    		'url': solrUrl + '/mp/select',
	    		'data': queryParams,						
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {
	    			
	    			// update this if facet is loaded by redirected page, which does not use autocomplete
	    			$('div#mpFacet span.facetCount').attr({title: 'total number of unique phenotype terms'}).text(json.response.numFound);
	    			
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
	    	    	
	    			self._displayOntologyFacet(json, 'mpFacet', table);	    			    			
	    		}		
	    	});		    	
	    },
	   
	    _displayOntologyFacet: function(json, facetDivId, table){	    	
	    	
	    	var self = this;
	    	var ontology = MPI2.searchAndFacetConfig.facetParams[facetDivId].ontology;	    		
	    	
	    	if (json.response.numFound == 0 ){	    		
    			table = null;
    		}	    			
    		$('div#'+facetDivId+ ' .facetCatList').html(table);
    		
    		$('table#'+ ontology + 'Facet td a').click(function(){      			
    			$.fn.fetchFilteredDataTable($(this), facetDivId, self.options.data.q);    			
    		});  
    		    		
    		// reload sidebar for hash state   		
	    	if ( self.options.data.fq != 'ontology_subset:*' ){
	    		//console.log('MP filtered');	    		
	    		var fqText = self.options.data.fq.replace('ontology_subset:* AND top_level_mp_term:', '').replace(/"/g, '');	    	
	    		$.fn.fetchFilteredDataTable($('a[rel="' + fqText + '"]'), 'mpFacet', self.options.data.q);	
	    	}
	    	else {//if ( self.options.data.core == 'mp' && self.options.data.fq && self.options.data.fq == 'ontology_subset:*' ){	    		
	    		var solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams['mpFacet'].filterParams, MPI2.searchAndFacetConfig.commonSolrParams);						
    			solrSrchParams.q = self.options.data.q;	
	    		$.fn.invokeFacetDataTable(solrSrchParams, 'mpFacet', MPI2.searchAndFacetConfig.facetParams['mpFacet'].gridName, self.options.data.q);	    		
	    	}
    		
    		// skip display subterms for now
    		// fetch and expand children of top level MP term
    		/*$('table#'+ ontology + 'Facet td.'+ontology+'TopLevel').click(function(){  
    			
    			var parent = $(thiconsole.log('make '+ core);		s).parent();
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
    			
    					var solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams.mpFacet.filterParams, MPI2.searchAndFacetConfig.commonSolrParams);	                   
    					solrSrchParams.q = self.options.data.q;
    					
    					solrSrchParams.fl = ontology+ '_id,'+ ontology + '_term,'+'ontology_subset';    					
    					solrSrchParams.sort = ontology + '_term asc';
    					solrSrchParams.solrBaseURL = solrBaseUrl;                  
    					console.log('make '+ core);		
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
		
	    _reloadDataTableForHashUrl: function(fqText){
	    	var self = this;
			//ontology_subset:* AND top_level_mp_term:"behavior/neurological phenotype"			   			
			$.fn.fetchFilteredDataTable($('a[rel="' + fqText + '"]'), 'mpFacet', self.options.data.q);
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
	



