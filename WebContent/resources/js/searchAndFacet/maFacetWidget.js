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
	    	var fecetField = 'selected_top_level_ma_term';	 
	    	
	    	var queryParams = $.extend({}, {				
		    	'fq': MPI2.searchAndFacetConfig.facetParams.maFacet.fq,
				'rows': 0, // override default
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.field': fecetField,
				//'facet.field': 'annotated_or_inferred_higherLevelMaTermName',
				'facet.sort': 'index',						
				'q.option': 'AND',
				'q': self.options.data.hashParams.q}, MPI2.searchAndFacetConfig.commonSolrParams);
	    		    	
	    	$.ajax({	
	    		'url': solrUrl + '/ma/select',
	    		'data': queryParams,						
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {
	    			
	    			// update this if facet is loaded by redirected page, which does not use autocomplete
	    			//$('div#maFacet span.facetCount').attr({title: 'total number of unique MA terms'}).text(json.response.numFound);
	    				    			
	    	    	//var aTopLevelCount = json.facet_counts.facet_fields['annotated_or_inferred_higherLevelMaTermName'];
	    	    	var aTopLevelCount = json.facet_counts.facet_fields[fecetField];
	    	    	var maUlContainer = $("<ul></ul>");
	    	    	
	    	    	// selected top level MA terms
	    	    	for ( var i=0;  i<aTopLevelCount.length; i+=2 ){	    		
	    	    		
	    	    		var liContainer = $("<li></li>").attr({'class':'fcat'});	    	    	
	        		
	        			var count = aTopLevelCount[i+1];	        				
	        			var coreField = 'ma|'+ fecetField + '|' + aTopLevelCount[i] + '|' + count;	
	        			var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});
	        			var flabel = $('<span></span>').attr({'class':'flabel'}).text(aTopLevelCount[i]);
						var fcount = $('<span></span>').attr({'class':'fcount'}).text(count);
						liContainer.append(chkbox, flabel, fcount);
						maUlContainer.append(liContainer);	        			
	    	    	}    	
	    	    		    			
	    			// update all subfacet counts of this facet 
	        		$('div.flist li#ma > ul').append(maUlContainer);	        		        		
	        		
	        		$.fn.initFacetToggles('ma');
	        		
	        		$('li#ma li.fcat input').click(function(){	 
	        			
	        			// // highlight the item in facet	    			
	        			$(this).siblings('span.flabel').addClass('highlight');
	    				$.fn.composeSummaryFilters($(this), self.options.data.hashParams.q);
	    			});   
	        		
	        		/*--------------------------------------------------------------------------------------------------------------------------*/
	    	    	/* ------ when search page loads, the URL params are parsed to load dataTable and reconstruct filters, if applicable ------ */
	    	    	/*--------------------------------------------------------------------------------------------------------------------------*/	
	    	    	
	        		var oConf = self.options.data.hashParams;
	    	    	oConf.core = self.options.data.core;
	    	    	
	    	    	$.fn.parseUrl_constructFilters_loadDataTable(oConf);
	    			
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
