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
						
						var oHashParams = $.fn.parseHashString(window.location.hash.substring(1));												
						oHashParams.fq = $.fn.fieldNameMapping(oHashParams.fq, 'mp');
						var mode = typeof oHashParams.facetName != 'undefined' ? '&facet=' : '&core=';
												
						if ( ! window.location.search.match(/q=/) ){
							window.location.hash = 'q=' + oHashParams.q + '&fq=' + oHashParams.fq + mode +  solrCoreName;
						}	
						else {
							window.location.hash = 'fq=' + oHashParams.fq + mode +  solrCoreName;
						}
					}	
				}	
			});	
								
			// click on SUM facetCount to fetch results in grid: deprecated											
			
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
				//'facet.field': 'top_level_mp_term',
				'facet.field': 'annotated_or_inferred_higherLevelMpTermName',
				'facet.sort': 'index',						
				'q.option': 'AND',
				'q': self.options.data.hashParams.q}, MPI2.searchAndFacetConfig.commonSolrParams);			
	    
	    	$.ajax({	
	    		'url': solrUrl + '/mp/select',
	    		'data': queryParams,						
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {
	    			
	    			// update this if facet is loaded by redirected page, which does not use autocomplete
	    			$('div#mpFacet span.facetCount').attr({title: 'total number of unique phenotype terms'}).text(json.response.numFound);
	    			
	    			var table = $("<table id='mpFacetTbl' class='facetTable'></table>");	    			
	    			
	    	    	//var aTopLevelCount = json.facet_counts.facet_fields['top_level_mp_term'];
	    	    	var aTopLevelCount = json.facet_counts.facet_fields['annotated_or_inferred_higherLevelMpTermName'];
	    	    	
	    	    	// top level MP terms
	    	    	for ( var i=0;  i<aTopLevelCount.length; i+=2 ){	    		
	    	    		
	        			var tr = $('<tr></tr>').attr({'rel':aTopLevelCount[i], 'id':'topLevelMpTr'+i});  
	        			
	        			var count = aTopLevelCount[i+1];						
	        			var coreField = 'mp|annotated_or_inferred_higherLevelMxTermName|' + aTopLevelCount[i] + '|' + count;
						var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});
						var td0 = $('<td></td>').append(chkbox);      			
	    	    		var td1 = $('<td></td>').attr({'class': 'mpTopLevel', 'rel': count}).text(aTopLevelCount[i].replace(' phenotype', ''));	    	    		   	    		
	    	    		
	    	    		var a = $('<a></a>').attr({'rel':aTopLevelCount[i]}).text(count);
	    	    		var td2 = $('<td></td>').attr({'class': 'mpTopLevelCount'}).append(a);
	    	    		table.append(tr.append(td0, td1, td2)); 
	        			
	    	    	}    	
	    	    	
	    			self._displayOntologyFacet(json, 'mpFacet', table);	 
	    			
	    			// update facet count when filters applied
	    			if ( $('ul#facetFilter li li a').size() != 0 ){	    			
	    				$.fn.fetchQueryResult(self.options.data.hashParams.q, 'mp');
	    			}	    			
	    		}		
	    	});		    	
	    },
	   
	    _displayOntologyFacet: function(json, facetDivId, table){	    	
	    	
	    	var self = this;
	    	
	    	if (json.response.numFound == 0 ){	    		
    			table = null;
    		}	    			
    		$('div#'+facetDivId+ ' .facetCatList').html(table);
    		    		
    		    		
    		$('table#mpFacetTbl input').click(function(){
    			// highlight the item in facet
    			$(this).parent().parent().find('td.mpTopLevel').addClass('highlight');
    			    			
				$.fn.composeFacetFilterControl($(this), self.options.data.hashParams.q);					
			});  
    		
    		/*------------------------------------------------------------------------------------*/
	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	/*------------------------------------------------------------------------------------*/	
    		if ( self.options.data.hashParams.fq.match(/.*/) ){   			
	    		
	    		var oHashParams = self.options.data.hashParams;
    			
	    		$.fn.parseUrlForFacetCheckboxAndTermHighlight(oHashParams);	    	    		
	    		// now load dataTable    		
	    		$.fn.loadDataTable(oHashParams);
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
	



