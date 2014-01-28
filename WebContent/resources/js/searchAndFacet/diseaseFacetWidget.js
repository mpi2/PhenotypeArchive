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
    $.widget('MPI2.diseaseFacet', {
    	
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
				'type': 'disease',
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,								
				'facet.sort': 'count',					
	    		'q': self.options.data.hashParams.q},
	    		MPI2.searchAndFacetConfig.commonSolrParams,
	    		MPI2.searchAndFacetConfig.facetParams.diseaseFacet.filterParams
	    	);    	   	
	    	
	    	var queryParamStr = $.fn.stringifyJsonAsUrlParams(queryParams) 
	    					  + '&facet.field=disease_classes'
	    	                  + '&facet.field=impc_predicted'
	    	                  + '&facet.field=mgi_predicted'
	    	                  + '&facet.field=impc_predicted_in_locus'
	    	                  + '&facet.field=mgi_predicted_in_locus' 
	    	                  + '&facet.field=human_curated'
	    	                  + '&facet.field=mouse_curated'
	    	                  + '&facet.field=disease_source';	    	
	    	
	    	$.ajax({ 				 					
	    		'url': solrUrl + '/disease/select',	    		
	    		'data': queryParamStr, 
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {	  	    		
	    			self._displayDiseaseSubfacet(json);	    				
	    		}		
	    	});	    	
	    },
	    
	    _displayDiseaseSubfacet: function(json){	
	    
	    	var self = this;
	    	var numFound = json.response.numFound;
	    	
	    	/*-------------------------------------------------------*/
	    	/* ------ displaying sidebar and update dataTable ------ */
	    	/*-------------------------------------------------------*/
	    	
	    	if (numFound > 0){  				    		
	    			    		
	    		// Subfacets: disease classifications/sources
	    		var oSubFacets1 = {'disease_source':'Sources', 'disease_classes':'Classifications'};  
	    		for ( var fq in oSubFacets1 ){	    			
	    		    var label = oSubFacets1[fq];
	    			var aData = json.facet_counts['facet_fields'][fq];
	    		
	    			//table.append($('<tr></tr>').attr({'class':'facetSubCat '+ trCap + ' ' + fq}).append($('<td></td>').attr({'colspan':3}).text(label)));
	    			var thisFacetSect = $("<li class='fcatsection'></li>");		 
	    			thisFacetSect.append($('<span></span>').attr({'class':'flabel'}).text(label));	    			
	    			
	    			var unclassified;
	    			var thisUlContainer = $("<ul></ul>");
	    			
	    			for ( var i=0; i<aData.length; i=i+2 ){
	    				var liContainer = $("<li></li>").attr({'class':'fcat ' + fq});
	    				
		    			var subFacetName = aData[i];
		    			
		    			var count = aData[i+1];
		    			
		    			var diseaseFq = fq;
		    			var coreField = 'disease|'+ diseaseFq + '|';		
		    			var trClass = fq+'Tr';
						var chkbox = $('<input></input>').attr({'class': fq, 'type': 'checkbox', 'rel': coreField + subFacetName + '|' + count});						
						var flabel = $('<span></span>').attr({'class':'flabel'}).text(subFacetName);
						var fcount = $('<span></span>').attr({'class':'fcount'}).text(count);
						
						if ( subFacetName != 'unclassified' ){							
							liContainer.append(chkbox, flabel, fcount);
						}
						else {							
							unclassified = liContainer.append(chkbox, flabel, fcount);
						}
						thisUlContainer.append(liContainer);	
		    		}
		    		
		    		if ( fq == 'disease_classes' && unclassified){
		    			thisUlContainer.append(unclassified);
		    		}	
		    		thisFacetSect.append(thisUlContainer);
		    		$('div.flist li#disease > ul').append(thisFacetSect);
	    		}
	    		
	    		// Subfacets: curated/predicted gene associations
	    		var oSubFacets2 = {'curated': {'label':'With Curated Gene Associations', 
	    									   'subfacets':{'human_curated':'From human data (OMIM, Orphanet)', 'mouse_curated':'From mouse data (MGI)'}},
	    						   'predicted':{'label':'With Predicted Gene Associations by Phenotype', 
	    							   			'subfacets': {'impc_predicted':'From MGP data','impc_predicted_in_locus':'From MGP data in linkage locus',
	    							   				          'mgi_predicted':'From MGI data','mgi_predicted_in_locus':'From MGI data in linkage locus'}}};
	    			    		
	    		for ( var assoc in oSubFacets2 ){	    			
	    			var label = oSubFacets2[assoc].label;
	    			var thisFacetSect = $("<li class='fcatsection'></li>");
	    			
	    			thisFacetSect.append($('<span></span>').attr({'class':'flabel'}).text(label));	    			
	    			    		
	    			var thisUlContainer = $("<ul></ul>");
	    			
	    			for ( var fq in oSubFacets2[assoc].subfacets ){
	    				var thisSubfacet = oSubFacets2[assoc].subfacets[fq]; 
		    			var aData = json.facet_counts['facet_fields'][fq];  		
			    		for ( var i=0; i<aData.length; i=i+2 ){
			    			
			    			var liContainer = $("<li></li>").attr({'class':'fcat ' + fq});
			    			var dPositive = aData[i];
			    			
			    			if ( dPositive == '1'){
				    			var count = aData[i+1];
				    			var diseaseFq = fq;
				    			var coreField = 'disease|'+ diseaseFq + '|';		
							    
								var chkbox = $('<input></input>').attr({'class':assoc, 'type': 'checkbox', 'rel': coreField + '1' + '|' + count});								
								var flabel = $('<span></span>').attr({'class':'flabel'}).text(thisSubfacet);
								var fcount = $('<span></span>').attr({'class':'fcount'}).text(count);
								liContainer.append(chkbox, flabel, fcount);	
								thisUlContainer.append(liContainer);							
			    			}				    			
			    		}
			    		thisFacetSect.append(thisUlContainer);
			    		$('div.flist li#disease > ul').append(thisFacetSect);
	    			}	
	    		}	    		    		
	    		   			
    			// disease_source is open and rest of disease subfacets are collapsed by default    			
    			$('div.flist li#disease > ul li:nth-child(1)').addClass('open');    			  						
	    		
    			$.fn.initFacetToggles('disease');
    			
	    		$('li#disease li.fcat input').click(function(){	    			
	    			// // highlight the item in facet	    			
	    			$(this).siblings('span.flabel').addClass('highlight');
					$.fn.composeFacetFilterControl($(this), self.options.data.hashParams.q);					
				});	  
    			
    			// update facet count when filters applied
    			if ( $('ul#facetFilter li li a').size() != 0 ){
    				$.fn.fetchQueryResult(self.options.data.hashParams.q, 'disease');
    			}    	  		
    		}
	    	
	    	/*--------------------------console.log('inside here');	----------------------------------------------------------*/
	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	/*------------------------------------------------------------------------------------*/	
	    	
	    	
	    	if ( self.options.data.hashParams.fq.match(/.*/) ){	
	    		
	    		self.options.data.hashParams.q = window.location.search == '' ? '*:*' : window.location.search.replace('?q=', '');
	    		
	    		var pageReload = true;  // this controls checking which subfacet to open (ie, show by priority)  	
	    		
	    		var oHashParams = self.options.data.hashParams;
    			
	    		$.fn.parseUrlForFacetCheckboxAndTermHighlight(oHashParams, pageReload);	    	    		
	    		// now load dataTable    		
	    		$.fn.loadDataTable(oHashParams);
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
	



