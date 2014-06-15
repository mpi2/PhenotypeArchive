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
    		$.fn.widgetExpand(this);    		
    	},
 	        	
	    // want to use _init instead of _create to allow the widget being invoked each time by same element
	    _init: function () {
			var self = this;
			
			self._initFacet();			
			$.fn.openFacet(self.options.data.core);			
	    },
	    
	    _initFacet: function(){
	    	var self = this;
	    	var fq = self.options.data.hashParams.fq;
	    	var facetField = 'top_level_mp_term';
	    	var oParams = {};		
	        oParams = $.fn.getSolrRelevanceParams('mp', self.options.data.hashParams.q, oParams);
	    	
	    	var queryParams = $.extend({}, {				
				'fq': fq,
				'rows': 0, // override default
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.field': facetField,
				//'facet.field': 'annotatedHigherLevelMpTermName',
				'facet.sort': 'index',						
				'q.option': 'AND',
				'q': self.options.data.hashParams.q}, MPI2.searchAndFacetConfig.commonSolrParams, oParams);			
	    
	    	$.ajax({	
	    		'url': solrUrl + '/mp/select',
	    		'data': queryParams,						
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {
	    			//console.log(json);
	    				    	    	
	    	    	//var aTopLevelCount = json.facet_counts.facet_fields['annotatedHigherLevelMpTermName'];	 
	    	    	var aTopLevelCount = json.facet_counts.facet_fields[facetField];
	    	    	var mpUlContainer = $("<ul></ul>");
	    	    	
	    	    	// top level MP terms
	    	    	for ( var i=0;  i<aTopLevelCount.length; i+=2 ){	    		
	    	    		if ( aTopLevelCount[i] == 'mammalian phenotype'){
	    	    			continue;
	    	    		}
	    	    		var liContainer = $("<li></li>").attr({'class':'fcat'});
	        				        		
	        			var count = aTopLevelCount[i+1];						
	        			//var coreField = 'mp|annotatedHigherLevelMpTermName|' + aTopLevelCount[i] + '|' + count;
	        			var coreField = 'mp|' + facetField + '|' + aTopLevelCount[i] + '|' + count;
						var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});
							    	    		
	    	    		var flabel = $('<span></span>').attr({'class':'flabel'}).text(aTopLevelCount[i].replace(' phenotype', ''));
						var fcount = $('<span></span>').attr({'class':'fcount'}).text(count);
						liContainer.append(chkbox, flabel, fcount);
						mpUlContainer.append(liContainer);
	    	    	}    		    	    	
	    	    		    			 
	    			// update all subfacet counts of this facet 
	        		$('div.flist li#mp > ul').append(mpUlContainer);  
	        		$.fn.initFacetToggles('mp');
	        		
	        		$('li#mp li.fcat input').click(function(){	
	        			$('div.flist li#mp').click();
	        			// // highlight the item in facet	    			
	        			$(this).siblings('span.flabel').addClass('highlight');
	    				$.fn.composeSummaryFilters($(this), self.options.data.hashParams.q);
	    			});    		
	        		
	        		/*--------------------------------------------------------------------------------------------------------------------------*/
	    	    	/* ------ when search page loads, the URL params are parsed to load dataTable and reconstruct filters, if applicable ------ */
	    	    	/*--------------------------------------------------------------------------------------------------------------------------*/	
	    	    	
	    	    	$.fn.parseUrl_consturctFilters_loadDataTable(self);			   			
	    		}		
	    	});		    	
	    },	   
	    
	    destroy: function () {    	   
	    	//this.element.empty();
	    	// does not generate selector class    	    
    	    //$.Widget.prototype.destroy.call(this);  // if using jQuery UI 1.8.x    	    
    	    this._destroy();                          // if using jQuery UI 1.9.x
    	}  
    });
	
}(jQuery));	
	



