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
			//$.fn.openFacet(self.options.data.core);			
	    },
	    
	    _initFacet: function(){
	    	var self = this;
	    	
	    	$.fn.setCurrentFq();
//	    	var fq = MPI2.searchAndFacetConfig.currentFq ? MPI2.searchAndFacetConfig.currentFq
//	    			: self.options.data.hashParams.fq;
	    	var fq = $.fn.processCurrentFqFromUrl(self.options.data.core);
	    	
	    	var facetField = 'top_level_mp_term';
	    	var oParams = {};		
	        oParams = $.fn.getSolrRelevanceParams('mp', self.options.data.hashParams.q, oParams);

	        var queryParams = $.extend({}, {				
				'fq': fq,
				'rows': 0, // override default
				'facet': 'on',								
				//'facet.mincount': 1,  // want to also include zero ones
				'facet.limit': -1,
				'facet.field': facetField,
				//'facet.field': 'annotatedHigherLevelMpTermName',
				'facet.sort': 'index',						
				'q.option': 'AND'
				//'q' : $.fn.encodeQ(self.options.data.hashParams.q)
				}, MPI2.searchAndFacetConfig.commonSolrParams, oParams);			
	    	
	    	//console.log('MP WIDGET: '+ $.fn.stringifyJsonAsUrlParams(queryParams));
	    	var queryParamStr = $.fn.stringifyJsonAsUrlParams(queryParams);
	    	
	    	$.ajax({	
	    		'url': solrUrl + '/mp/select',
	    		'data': queryParamStr,						
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {
	    			//console.log(json);
	    				    	    	
	    	    	//var aTopLevelCount = json.facet_counts.facet_fields['annotatedHigherLevelMpTermName'];	 
	    	    	var aTopLevelCount = json.facet_counts.facet_fields[facetField];
	    	    	var mpUlContainer = $("<ul></ul>");
	    	    	var liContainer_viable  = null;
	    	    	var liContainer_fertile = null;
	    	    	
	    	    	// top level MP terms
	    	    	for ( var i=0;  i<aTopLevelCount.length; i+=2 ){	
	    	    		var topLevelName = aTopLevelCount[i];
	    	    		if ( topLevelName == 'mammalian phenotype'){
	    	    			continue;
	    	    		}
	        		
	        			var count = aTopLevelCount[i+1];	
	        			var isGrayout = count == 0 ? 'grayout' : '';
	        			
	        			var liContainer = $("<li></li>").attr({'class':'fcat'});
	        			liContainer.removeClass('grayout').addClass(isGrayout);
	        			
	        			//var coreField = 'mp|annotatedHigherLevelMpTermName|' + topLevelName + '|' + count;
	        			var coreField = 'mp|' + facetField + '|' + topLevelName + '|' + count;
						var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});
							    	    
	    	    		var flabel = $('<span></span>').attr({'class':'flabel'}).text(topLevelName.replace(' phenotype', ''));
						var fcount = $('<span></span>').attr({'class':'fcount'}).text(count);
						
						liContainer.append(chkbox, flabel, fcount);
						
						if ( topLevelName == 'reproductive system phenotype' ){
							flabel.addClass('fertility');
							liContainer_fertile = liContainer;
						}
						else if ( topLevelName == 'mortality/aging' ){
							flabel.addClass('viability');
							liContainer_viable = liContainer;
						}
						else {
							mpUlContainer.append(liContainer);
						}
	    	    	} 
	    	    	
	    	    	// move these 2 top level MPs to top of facet list
	    	    	mpUlContainer.prepend(liContainer_fertile);
	    	    	mpUlContainer.prepend(liContainer_viable);
	    	    	
	    			// update all subfacet counts of this facet 
	        		$('div.flist li#mp > ul').append(mpUlContainer); 
	        		
	        		$.fn.cursorUpdate('mp', 'not-allowed');
	        		
	        		$.fn.initFacetToggles('mp');
	        		
	        		$('li#mp li.fcat input').click(function(){	
	        			
	        			// // highlight the item in facet	    			
	        			$(this).siblings('span.flabel').addClass('highlight');
	        			MPI2.searchAndFacetConfig.update.filterAdded = true;
	    				$.fn.composeSummaryFilters($(this), self.options.data.hashParams.q);
	    			});  
	        		
		    		MPI2.searchAndFacetConfig.update.widgetOpen = false;
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
	



