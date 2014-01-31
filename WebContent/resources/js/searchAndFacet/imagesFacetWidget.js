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
 * imagesFacetWidget: makes SOLR call 
 * and displays the facet results on the left sidebar and dataTable on right.
 * 
 */
(function ($) {
	'use strict';
    $.widget('MPI2.imagesFacet', {
        
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
  	    		    	
  	    	var queryParams = $.extend({}, {							
  				'rows': 0,
  				'facet': 'on',								
  				'facet.mincount': 1,
  				'facet.limit': -1,				
  				'facet.sort': 'index',
  				'fl': 'annotationTermId,annotationTermName,expName,symbol',
  				//'fq': "annotationTermId:M* OR symbol_gene:*",  // images that have annotations only
  				'q.option': 'AND',				
  				'q': self.options.data.hashParams.q 				
  				}, MPI2.searchAndFacetConfig.commonSolrParams);  	    	  	    	
  	    	
  	    	var paramStr = $.fn.stringifyJsonAsUrlParams(queryParams) 
  	    		+ "&facet.field=expName"
  	    		+ "&facet.field=annotated_or_inferred_higherLevelMpTermName"
  	    		+ "&facet.field=annotated_or_inferred_higherLevelMaTermName"
  	    		+ "&facet.field=subtype"
  	    		    	
  	    	$.ajax({	
  	    		'url': solrUrl + '/images/select',  	    		
  	    		'data': paramStr,  				
  	    		'dataType': 'jsonp',
  	    		'jsonp': 'json.wrf',	    		
  	    		'success': function(json) {  	    			    			
  	    			
  	    			var aFacetFields = json.facet_counts.facet_fields; // eg. expName, symbol..
  	    			
  	    			var aSubFacetNames = [];
  	    			
  	    			// do some sorting for facet names, but put Phenotype subfacet on front of list
  	    			for ( var facetName in aFacetFields ){ 	
  	    				if (facetName != 'annotated_or_inferred_higherLevelMpTermName' ){
  	    					aSubFacetNames.push(facetName);
  	    				}
  	    			}	
  	    			aSubFacetNames.sort();
  	    			aSubFacetNames.unshift('annotated_or_inferred_higherLevelMpTermName');
  	    			  	    			
  	    			var displayLabel = {
  	    								annotated_or_inferred_higherLevelMaTermName: 'Anatomy',
  	    								expName : 'Procedure',	    					            
  	    								annotated_or_inferred_higherLevelMpTermName: 'Phenotype',
  	    					            subtype: 'Gene'
  	    								};	    			    			    			
  	    			    			
  	    			   
  	    			for ( var n=0; n<aSubFacetNames.length; n++){
  	    				var facetName = aSubFacetNames[n];
  	    				var label = displayLabel[facetName];  	    				
  	    				
  	    				var thisFacetSect = $("<li class='fcatsection " + label + "'></li>");		 
  	    				thisFacetSect.append($('<span></span>').attr({'class':'flabel'}).text(label));
  	    				
  	    				var thisUlContainer = $("<ul></ul>");
  	    					
  	    				for ( var i=0; i<aFacetFields[facetName].length; i+=2){    					  					
  	    					
  	    					var liContainer = $("<li></li>").attr({'class':'fcat ' + facetName});
  	    					
  	    					var fieldName  = aFacetFields[facetName][i];  	    					
  	    					var facetCount = aFacetFields[facetName][i+1];  	    					
  	    					var label      = displayLabel[facetName];
  	    					
  		    	    		var coreField = 'images|'+ facetName + '|' + fieldName + '|' + facetCount + '|' + label;	
  		        			var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField}); 	
  		        			var flabel = $('<span></span>').attr({'class':'flabel'}).text(fieldName.replace(' phenotype', ''));
  							var fcount = $('<span></span>').attr({'class':'fcount'}).text(facetCount);
  							thisUlContainer.append(liContainer.append(chkbox, flabel, fcount));  							
  	    				}
  	    				
  	    				thisFacetSect.append(thisUlContainer);
  	    				$('div.flist li#images > ul').append(thisFacetSect);
  	    			}	    			
  	    			
  	    			// open first subfacet by default
  	    			$('div.flist li#images li.fcatsection:nth-child(1)').addClass('open');	
  	    			
  	    			$.fn.initFacetToggles('images');
  	    			//$('div.flist li#images > .flabel').click();
  	    			
  	    			// update facet count when filters applied
  	    			if ( $('ul#facetFilter li li a').size() != 0 ){
  	    				$.fn.fetchQueryResult(self.options.data.hashParams.q, 'images');
  	    			} 
  	    			
	  	      		$('li#images li.fcat input').click(function(){	    			
	  	      			// // highlight the item in facet	    			
	  	      			$(this).siblings('span.flabel').addClass('highlight');
	  	  				$.fn.composeFacetFilterControl($(this), self.options.data.hashParams.q);					
	  	  			});
	  	      		
	  	    	    /*------------------------------------------------------------------------------------*/
	  	  	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	  	  	    	/*------------------------------------------------------------------------------------*/ 
	  	    	    		
	  	      		if ( self.options.data.hashParams.fq.match(/.*/) ){ 	
	  	      			$.fn.setDefaultImgSwitcherConf();  
	  	      			
	  	      			var pageReload = true;  // this controls checking which subfacet to open (ie, show by priority) 		
	  	  	    		
	  	  	    		var oHashParams = self.options.data.hashParams;
	  	      			
	  	  	    		$.fn.parseUrlForFacetCheckboxAndTermHighlight(oHashParams, pageReload);	    	    		
	  	  	    		// now load dataTable    		
	  	  	    		$.fn.loadDataTable(oHashParams);
	  	      		}
	  	    	    		
  	    	    	// when last facet is done
  	    	    	$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch);  	    			
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
	



