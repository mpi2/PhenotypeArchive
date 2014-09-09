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
			//$.fn.openFacet(self.options.data.core);			
	    },
	    
		_initFacet: function(){			
	    	var self = this;
	    	
	    	$.fn.setCurrentFq();
//	    	var fq = MPI2.searchAndFacetConfig.currentFq ? MPI2.searchAndFacetConfig.currentFq
//	    			: self.options.data.hashParams.fq;
	    	var fq = $.fn.processCurrentFqFromUrl(self.options.data.core);
	    	
	    	var oParams = {};	
	        oParams = $.fn.getSolrRelevanceParams('disease', self.options.data.hashParams.q, oParams);
	        
	    	var queryParams = $.extend({}, {				
				'fq': fq,
				'rows': 0, // override default
				'type': 'disease',
				'facet': 'on',								
				//'facet.mincount': 1,  // want to also include zero ones
				'facet.limit': -1,
				'facet.sort': 'count'
				//'q' : self.options.data.hashParams.q
				}, MPI2.searchAndFacetConfig.commonSolrParams, oParams);			
	    	
	    	var queryParamStr = $.fn.stringifyJsonAsUrlParams(queryParams) 
	    					  + '&facet.field=disease_classes'
	    	                  + '&facet.field=impc_predicted'
	    	                  + '&facet.field=mgi_predicted'
	    	                  + '&facet.field=impc_predicted_in_locus'
	    	                  + '&facet.field=mgi_predicted_in_locus' 
	    	                  + '&facet.field=human_curated'
	    	                  + '&facet.field=mouse_curated'
	    	                  + '&facet.field=disease_source';	    	
	    	
	    	//console.log('DISEASE WIDGET: ' + queryParamStr);
	    	
	    	$.ajax({ 				 					
	    		'url': solrUrl + '/disease/select',	    		
	    		'data': queryParamStr, 
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {	 
	    			//console.log(MPI2.searchAndFacetConfig.currentFq);
	    			//console.log(json);
	    			self._displayDiseaseSubfacet(json);	    				
	    		}		
	    	});	    	
	    },
	    
	    _displayDiseaseSubfacet: function(json){
	    	
	    	var self = this;
	    	var numFound = json.response.numFound;
	    	var foundMatch = {'disease_source':0, 'disease_classes':0, 'curated':0, 'predicted':0};
	    	
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
	    			var thisFacetSect = $("<li class='fcatsection " + fq + "'></li>");		 
	    			thisFacetSect.append($('<span></span>').attr({'class':'flabel'}).text(label));	    			
	    			
	    			var unclassified;
	    			var thisUlContainer = $("<ul></ul>");
	    			
	    			for ( var i=0; i<aData.length; i=i+2 ){
	    				var liContainer = $("<li></li>").attr({'class':'fcat ' + fq});
	    				
		    			var subFacetName = aData[i];
		    			
		    			var count = aData[i+1];
		    			foundMatch[fq]++;
		    			
		    			var diseaseFq = fq;
		    			var coreField = 'disease|'+ diseaseFq + '|';		
		    			var trClass = fq+'Tr';
		    			var isGrayout = count == 0 ? 'grayout' : '';
		    			liContainer.removeClass('grayout').addClass(isGrayout);
		    			
						var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + subFacetName + '|' + count + '|' +fq});						
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
	    							   			'subfacets': {'impc_predicted':'From IMPC data','impc_predicted_in_locus':'From IMPC data in linkage locus',
	    							   				          'mgi_predicted':'From MGI data','mgi_predicted_in_locus':'From MGI data in linkage locus'}}};
	    		
	    		/* Damian requsted 
	    		 * 
	    		 Add two more facets to Diseases  > With Curated Gene Associations:
                “From human data with MGI prediction” - mgi_predicted_known_gene             
				“From human data with IMPC prediction” - impc_predicted_known_gene
 
	    		Change  Diseases  > With Predicted Gene Associations by Phenotype
	    		“From IMPC data in linkage locus” to “Novel IMPC prediction in linkage locus” - impc_novel_predicted_in_locus
	    		“From MGI data in linkage locus” to “Novel MGI prediction in linkage locus” - mgi_novel_predicted_in_locus
				
				deprecated the old fields (mgi_predicted_in_locus, impc_predicted_in_locus) 
				
				*/
	    		 
	    		/*var oSubFacets2 = {'curated': {'label':'With Curated Gene Associations', 
					   						   'subfacets':{'human_curated':'From human data (OMIM, Orphanet)', 
					   							   			'mouse_curated':'From mouse data (MGI)',
					   							   			'impc_predicted_known_gene':'From human data with IMPC prediction',
					   							   			'mgi_predicted_known_gene':'From human data with MGI prediction'}
					   						   },
					   			   'predicted':{'label':'With Predicted Gene Associations by Phenotype', 
			   									'subfacets': {'impc_predicted':'From IMPC data',
			   												  'impc_novel_predicted_in_locus':'Novel IMPC prediction in linkage locus',
			   												  'mgi_predicted':'From MGI data',
			   												  'mgi_novel_predicted_in_locus':'Novel MGI prediction in linkage locus'}
					   						   }
					   			};
	    		*/
	    		for ( var assoc in oSubFacets2 ){	    			
	    			var label = oSubFacets2[assoc].label;
	    			var thisFacetSect = $("<li class='fcatsection " + assoc + "'></li>");
	    			
	    			thisFacetSect.append($('<span></span>').attr({'class':'flabel'}).text(label));	    			
	    			    		
	    			var thisUlContainer = $("<ul></ul>");
	    			
	    			for ( var fq in oSubFacets2[assoc].subfacets ){
	    				var thisSubfacet = oSubFacets2[assoc].subfacets[fq]; 
		    			var aData = json.facet_counts['facet_fields'][fq];  		
			    		for ( var i=0; i<aData.length; i=i+2 ){
			    			
			    			var liContainer = $("<li></li>").attr({'class':'fcat ' + fq});
			    			var dPositive = aData[i];
			    			if ( dPositive == 'true' ){
				    			var count = aData[i+1];
				    			var isGrayout = count == 0 ? 'grayout' : '';
				    			
				    			liContainer.removeClass('grayout').addClass(isGrayout);
				    			
				    			foundMatch[assoc]++;
				    			
				    			var diseaseFq = fq;
				    			var coreField = 'disease|'+ diseaseFq + '|';		
								var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + 'true' + '|' + count + '|' + assoc});								
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
	    		
	    		// no actions allowed when facet count is zero
    			$.fn.cursorUpdate('disease', 'not-allowed');
    			
    			// disease_source is open and rest of disease subfacets are collapsed by default    			
    			$('div.flist li#disease > ul li:nth-child(1)').addClass('open');    			  						
	    		
    			var selectorBase = "div.flist li#disease";
	    		// collapse all subfacet first, then open the first one that has matches 
				$(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout');	    		
				$.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);
				
				// change cursor for grayout filter
    			$.fn.cursorUpdate('disease', 'not-allowed');
    			    			
    			$.fn.initFacetToggles('disease');
	    			    			    		
	    		$('li#disease li.fcat input').click(function(){	 
	    			
	    			// // highlight the item in facet	    			
	    			$(this).siblings('span.flabel').addClass('highlight');
	    			MPI2.searchAndFacetConfig.update.filterAdded = true;
					$.fn.composeSummaryFilters($(this), self.options.data.hashParams.q);
				});  

	    		MPI2.searchAndFacetConfig.update.widgetOpen = false;
    		}
	    },	       
	  
	    destroy: function () {    	   
	    	//this.element.empty();
	    	// does not generate selector class    	    
    	    //$.Widget.prototype.destroy.call(this);  // if using jQuery UI 1.8.x    	    
    	    this._destroy();                          // if using jQuery UI 1.9.x
    	}  
    });
	
}(jQuery));	
	



