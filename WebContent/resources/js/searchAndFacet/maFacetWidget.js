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
 * maFacetWidget: based on the results retrieved by the autocompleteWidget
 * and displays the facet results on the left bar.
 * 
 */
(function ($) {
	'use strict';
    $.widget('MPI2.maFacet', {
        
	    options: {},	
	    _create: function(){
    		// execute only once 	
    		
    		var self = this;	
    		var facetDivId = self.element.attr('id');
    		var caller = self.element;
    		delete MPI2.searchAndFacetConfig.commonSolrParams.rows;    	   		  		
		
			caller.find('div.facetCat').click(function(){
				//console.log('facetCatClick');
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
						if ( caller.find('table#maFacetTbl td.highlight').size() == 0 ){							
							solrSrchParams.facetCount = $(this).siblings('span.facetCount').text();							
							window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);									
						}
						else {
							if ( self.options.data.core != hashParams.coreName ){
																
								var fqTextList = [];
								var displayedFilter = [];
								
								caller.find('table#maFacetTbl td.highlight').each(function(){
									fqTextList.push('selected_top_level_ma_term:"' + $(this).text() + '"');
									displayedFilter.push($(this).text());
								});
								
								var fqText = fqTextList.join(' OR ');
								
								var obj = {'fqStr'  : 'ontology_subset:IMPC_Terms AND (' + fqText + ')',
										   'filter' : displayedFilter.join(" OR "),
										   'chkbox' : null
										   }; 
						    	
								$.fn.fetchFilteredDataTable(obj, 'maFacet', self.options.data.q);
							}							
						}						
					}
				}	
			});	
									
			// click on SUM facetCount to fetch results in grid										
			caller.find('span.facetCount').click(function(){	
				
				if ( $(this).text() != '0' ){
					var gridName = MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName;
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
					var solrSrchParams = {}
					var hashParams = {};	
					
					$.fn.removeFacetFilter(solrCoreName);
					
					// remove highlight from selected 							
					$('table#maFacetTbl td').removeClass('highlight');
					
					solrSrchParams = $.extend({}, 
							MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams, 
							MPI2.searchAndFacetConfig.commonSolrParams);					
				
					solrSrchParams.facetCount = $(this).text();
					solrSrchParams.q = self.options.data.q;	
					solrSrchParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
					
					hashParams.q = self.options.data.q;
					hashParams.core = solrCoreName;
					hashParams.fq = solrSrchParams.fq;
					
					// hash state stuff				   
					window.location.hash = $.fn.stringifyJsonAsUrlParams(hashParams);// + "&core=" + solrCoreName;
										
					// only invoke dataTable when there is hash change in url
					// otherwise we are at same page, so no action taken
					
					if (MPI2.setHashChange == 1){						
						MPI2.setHashChange = 0;		
						
						//$.fn.updateFacetAndDataTableDisplay($.fn.stringifyJsonAsUrlParams(hashParams));	
						// invoke dataTable	via hash state with the 4th param
						// ie, it does not invoke dataTable directly but through hash change
						
						$.fn.invokeFacetDataTable(solrSrchParams, facetDivId, gridName);							
					}
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
		    	'fq': MPI2.searchAndFacetConfig.facetParams.maFacet.filterParams.fq,
				'rows': 0, // override default
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.field': 'selected_top_level_ma_term',
				'facet.sort': 'index',						
				'q.option': 'AND',
				'q': self.options.data.q}, MPI2.searchAndFacetConfig.commonSolrParams);
	    		    	
	    	$.ajax({	
	    		'url': solrUrl + '/ma/select',
	    		'data': queryParams,						
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {
	    			
	    			// update this if facet is loaded by redirected page, which does not use autocomplete
	    			$('div#maFacet span.facetCount').attr({title: 'total number of unique MA terms'}).text(json.response.numFound);
	    			
	    			var table = $("<table id='maFacetTbl' class='facetTable'></table>");	    			
	    			
	    	    	var aTopLevelCount = json.facet_counts.facet_fields['selected_top_level_ma_term'];
	    	    
	    	    	// selected top level MA terms
	    	    	for ( var i=0;  i<aTopLevelCount.length; i+=2 ){	    		
	    	    		
	        			var tr = $('<tr></tr>').attr({'rel':aTopLevelCount[i], 'id':'topLevelMaTr'+i}); 
	        			var count = aTopLevelCount[i+1];
	        			var coreField = 'ma|selected_top_level_ma_term|' + aTopLevelCount[i] + '|' + count;	
	        			var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});
	        			
	    	    		var td1 = $('<td></td>').attr({'class': 'maTopLevel', 'rel': count}).text(aTopLevelCount[i]);	    	    		   	    		
	    	    		
	    	    		var a = $('<a></a>').attr({'rel':aTopLevelCount[i]}).text(count);
	    	    		var td2 = $('<td></td>').attr({'class': 'maTopLevelCount'}).append(a);
	    	    		table.append(tr.append(chkbox, td1, td2)); 	        			
	    	    	}    	
	    	    	
	    			self._displayOntologyFacet(json, 'maFacet', table);	    			    			
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
    		
    		$('table#'+ ontology + 'FacetTbl td a').click(function(){
    			$.fn.fetchFilteredDataTable($(this), facetDivId, self.options.data.q, 'facetFilter');
    			
    			// uncheck all facet filter checkboxes 
    			$('table#'+ ontology + 'FacetTbl input').attr('checked', false);
    			// also remove all filters for that facet container	
    			$.fn.removeFacetFilter('ma');
    			$(this).parent().parent().find('input').attr('checked', true);
    			$.fn.addFacetFilter($(this).parent().parent().find('input'), self.options.data.q);    			
    		});  
    		    		
    		$('table#'+ ontology + 'FacetTbl input').click(function(){
    			// highlight the item in facet
    			$(this).parent().find('td.maTopLevel').addClass('highlight');
    			    			
				$.fn.composeFacetFilterControl($(this), self.options.data.q);					
			});   		
    		    		   
    		/*------------------------------------------------------------------------------------*/
	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	/*------------------------------------------------------------------------------------*/	
    		    		
	    	if ( self.options.data.fq != 'ontology_subset:IMPC_Terms AND selected_top_level_ma_term:*' ){
	    		console.log('MA filtered');	    		
	    		//var fqText = self.options.data.fq.replace('ontology_subset:IMPC_Terms AND selected_top_level_ma_term:', '').replace(/"/g, '');	    	
	    		//$.fn.fetchFilteredDataTable($('a[rel="' + fqText + '"]'), 'maFacet', self.options.data.q);
	    		
	    		var fields = ['selected_top_level_ma_term'];	        	
	    		$.fn.parseUrlForFacetCheckbox(self.options.data.q, self.options.data.fq, 'maFacet', fields);
	    	
	    		// now load dataTable	    		
	    		$.fn.loadDataTable(self.options.data.q, self.options.data.fq, 'maFacet'); 
	    	}	    	
	    	else {
	    		//console.log('MA unFiltered');
	    		var solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams['maFacet'].filterParams, MPI2.searchAndFacetConfig.commonSolrParams);						
    			solrSrchParams.q = self.options.data.q;
    			solrSrchParams.coreName = 'ma'; // to work out breadkCrumb facet display
    			solrSrchParams.facetCount = self.options.data.facetCount;
	    		$.fn.invokeFacetDataTable(solrSrchParams, 'maFacet', MPI2.searchAndFacetConfig.facetParams['maFacet'].gridName, self.options.data.q);	    		
	    	}    		
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
	



