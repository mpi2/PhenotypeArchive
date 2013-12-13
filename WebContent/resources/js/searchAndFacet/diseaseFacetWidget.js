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
					
						var currHashParams = {};						
						currHashParams.q = self.options.data.q;
						currHashParams.core = solrCoreName;
						currHashParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq; //default
										
						var oHashParams = $.fn.parseHashString(window.location.hash.substring(1));
					
						if ( typeof oHashParams.facetName != 'undefined'){
							window.location.hash = 'q=' + oHashParams.q + '&fq=' + oHashParams.fq + '&facet=' +  solrCoreName; 
						}
						else {
							// if no selected subfacet, load all results of this facet
							if ( caller.find('table#diseaseFacetTbl td.highlight').size() == 0 ){						
								//window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);									
							}	
							else {		
								// if there is selected subfacets: work out the url							
								if ( self.options.data.core != oHashParams.coreName ){															
								
									var fqFieldVals = {};
									
									caller.find('table#diseaseFacetTbl td.highlight').each(function(){									
										var val = $(this).siblings('td').find('a').attr('rel');								
										var fqField = 'top_level_mp_term';
										
										if ( typeof fqFieldVals[fqField] === 'undefined' ){
											fqFieldVals[fqField] = [];										
										}									
										fqFieldVals[fqField].push(fqField + ':"' + val + '"');
									});					
									
									var fqStr = MPI2.searchAndFacetConfig.facetParams[facetDivId].subset + ' AND ' + $.fn.compose_AndOrStr(fqFieldVals);
								
				  	    			// update hash tag so that we know there is hash change, which then triggers loadDataTable 	
									if (self.options.data.q == '*:*'){
										window.location.hash = 'q=' + self.options.data.q + '&core=' +  solrCoreName + '&fq=' + fqStr;
									}
									else {
										window.location.hash = 'core=' +  solrCoreName + '&fq=' + fqStr;
									}
								}	
							}
						}	
						/*
						// if no selected subfacet, load all results of this facet
						if ( caller.find('table#diseaseFacetTbl td.highlight').size() == 0 ){						
							//window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);									
						}	
						else {		
							// if there is selected subfacets: work out the url							
							if ( self.options.data.core != oHashParams.coreName ){															
							
								var fqFieldVals = {};
								
								caller.find('table#diseaseFacetTbl td.highlight').each(function(){									
									var val = $(this).siblings('td').find('a').attr('rel');								
									var fqField = $(this).siblings('td').find('a').attr('class');
									var qry;
									var fqFieldOri = fqField;									
									
									qry = fqFieldOri + ':"' + val + '"';									
									
									fqFieldVals[fqField].push(qry);
								});					
								console.log(fqFieldVals);
								var fqStr = $.fn.compose_AndOrStr(fqFieldVals);
							
			  	    			// update hash tag so that we know there is hash change, which then triggers loadDataTable 	
								if (self.options.data.q == '*:*'){
									window.location.hash = 'q=' + self.options.data.q + '&core=' +  solrCoreName + '&fq=' + fqStr;
								}
								else {
									window.location.hash = 'core=' +  solrCoreName + '&fq=' + fqStr;
								}
							}	
						}	*/
					}
				}
			});				
						
			// click on SUM facetCount to fetch results in grid										
			caller.find('span.facetCount').click(function(event){
				
				if ( $(this).text() != '0' ){
				
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;								
					
					$.fn.removeFacetFilter(solrCoreName);
					
					// remove highlight from selected				
					$('table#diseaseFacetTbl td').removeClass('highlight');
					
					var fqStr = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
					
					// update hash tag so that we know there is hash change, which then triggers loadDataTable  
					if (self.options.data.q == '*:*'){
						window.location.hash = 'q=' + self.options.data.q + '&core=' +  solrCoreName + '&fq=' + fqStr;
					}
					else {
						window.location.hash = 'core=' +  solrCoreName + '&fq=' + fqStr;
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
				'rows': 0,
				'type': 'disease',
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,								
				'facet.sort': 'count',					
	    		'q': self.options.data.q},
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
	    		
	    		var table = $("<table id='diseaseFacetTbl' class='facetTable'></table>");	
	    		
	    		// Subfacets: disease classifications/sources
	    		var oSubFacets1 = {'disease_source':'Sources', 'disease_classes':'Classifications'};  
	    		for ( var fq in oSubFacets1 ){	    			
	    		    var label = oSubFacets1[fq];
	    			var aData = json.facet_counts['facet_fields'][fq];
	    			var trCap = fq+'TrCap';
	    			table.append($('<tr></tr>').attr({'class':'facetSubCat '+ trCap + ' ' + fq}).append($('<td></td>').attr({'colspan':3}).text(label)));
    				    		
	    			var unclassifiedTr;
	    		
	    			for ( var i=0; i<aData.length; i=i+2 ){
		    			var subFacetName = aData[i];
		    			
		    			var count = aData[i+1];
		    			
		    			var diseaseFq = fq;
		    			var coreField = 'disease|'+ diseaseFq + '|';		
		    			var trClass = fq+'Tr';
						var chkbox = $('<input></input>').attr({'class': fq, 'type': 'checkbox', 'rel': coreField + subFacetName + '|' + count});
						var td0 = $('<td></td>').append(chkbox);
						var tr = $('<tr></tr>').attr({'class':'subFacet ' + trClass + ' ' + fq});						
						var td1 = $('<td></td>').attr({'class':'dClass diseaseSubfacet ' + fq, 'rel':count}).text(subFacetName);
						var link = $('<a></a>').attr({'rel': subFacetName, 'class': diseaseFq}).text(count);
						var td2 = $('<td></td>').attr({'class':'diseaseSubfacetCount ' + fq, 'rel':subFacetName}).append(link);						
						
						if ( subFacetName != 'unclassified' ){	
							table.append(tr.append(td0, td1, td2));
						}
						else {
							unclassifiedTr = tr.append(td0, td1, td2);
						}
		    		}
		    		
		    		if ( thisSubfacet == 'disease_classes' && unclassifiedTr){
		    			table.append(unclassifiedTr);
		    		}	 	
	    		}
	    		
	    		// Subfacets: curated/predicted gene associations
	    		var oSubFacets2 = {'curated': {'label':'With Curated Gene Associations', 
	    									   'subfacets':{'human_curated':'From human data (OMIM, Orphanet)', 'mouse_curated':'From mouse data (MGI)'}},
	    						   'predicted':{'label':'With Predicted Gene Associations by Phenotype', 
	    							   			'subfacets': {'impc_predicted':'From MGP data','impc_predicted_in_locus':'From MGP data in linkage locus',
	    							   				          'mgi_predicted':'From MGI data','mgi_predicted_in_locus':'From MGI data in linkage locus'}}};
	    			    		
	    		for ( var assoc in oSubFacets2 ){	    			
	    			var label = oSubFacets2[assoc].label;
	    			var trCapClass = assoc+'TrCap';
	    			table.append($('<tr></tr>').attr({'class':'facetSubCat ' + trCapClass + ' ' + assoc}).append($('<td></td>').attr({'colspan':3}).text(label)));
	    			
	    			for ( var fq in oSubFacets2[assoc].subfacets ){
	    				var thisSubfacet = oSubFacets2[assoc].subfacets[fq]; 
		    			var aData = json.facet_counts['facet_fields'][fq];  		
			    		for ( var i=0; i<aData.length; i=i+2 ){
			    			var dPositive = aData[i];
			    			if ( dPositive == '1'){
				    			var count = aData[i+1];
				    			var diseaseFq = fq;
				    			var coreField = 'disease|'+ diseaseFq + '|';		
							    
								var chkbox = $('<input></input>').attr({'class':assoc, 'type': 'checkbox', 'rel': coreField + '1' + '|' + count});
								var td0 = $('<td></td>').append(chkbox);
								var trClass = assoc+'Tr';
								var tr = $('<tr></tr>').attr({'class':'subFacet ' + trClass + ' ' + assoc});						
								var td1 = $('<td></td>').attr({'class':trClass + ' ' + fq + ' diseaseSubfacet', 'rel':count}).text(thisSubfacet);
								var link = $('<a></a>').attr({'rel': '1', 'class': diseaseFq}).text(count);
								var td2 = $('<td></td>').attr({'class':'diseaseSubfacetCount', 'rel':'1'}).append(link);					
								
								table.append(tr.append(td0, td1, td2));
			    			}	
			    		}	
	    			}	
	    		}
	    			    					
	    		$('div#diseaseFacet div.facetCatList').html(table);
	    		
	    		// update facet count when necessary
    			if ( $('ul#facetFilter li li a').size() != 0 ){
    				$.fn.fetchQueryResult(self.options.data.q, 'disease');
    			}		
	    		
	    		// disease_source is open and rest of disease subfacets are collapsed by default
	    		$('tr.disease_sourceTrCap, tr.disease_classesTrCap, tr.curatedTrCap, tr.predictedTrCap').click(function(){
	    		
	    			var aClass = $(this).attr('class').split(' ');
	    			var trClass = aClass[1].replace('Cap','');	    				    			
	    			
	    			if ( $(this).find('td').hasClass('unCollapse')){				
	    				$('tr.' + trClass).hide();
	    				$(this).find('td').removeClass('unCollapse');
	    			}
	    			else {	    			
	    				$('tr.' + trClass).show();	    				
	    				$(this).find('td').addClass('unCollapse');
	    			}
	    		});	    		    		
	    			
	    		
	    		$('table#diseaseFacetTbl td.diseaseSubfacetCount a').click(function(){
	    		
	    			// also remove all filters for that facet container	
	    			$.fn.removeFacetFilter('disease');
	    			// now update filter
	    			$.fn.addFacetFilter($(this).parent().parent().find('input'), self.options.data.q); 	        			
	    			
	    			// uncheck all facet filter checkboxes 
	    			$('table#diseaseFacetTbl input').attr('checked', false);
	    			// now check this checkbox
	    			$(this).parent().parent().find('input').attr('checked', true);
	    			
	    			// remove all highlight
	    			$('table#diseaseFacetTbl td.diseaseSubfacet').removeClass('highlight');
	    			// now highlight this one
	    			$(this).parent().parent().find('td.diseaseSubfacet').addClass('highlight');
		    			        			
	    			// update hash tag so that we know there is hash change, which then triggers loadDataTable	  	    			
		    		var fqStr = $(this).attr('class') + ':"' + $(this).attr('rel') + '"' + " AND " + MPI2.searchAndFacetConfig.facetParams['diseaseFacet'].fq;	    			    		    		
		    		
		    		if (self.options.data.q == '*:*'){
		    			window.location.hash = 'q=' +  self.options.data.q + '&fq=' + fqStr + '&core=disease';
		    		}
		    		else {
		    			window.location.hash = 'fq=' + fqStr + '&core=disease';
		    		}
	    		});  
	    		$('table#diseaseFacetTbl input').click(function(){
	    			
	    			// // highlight the item in facet	    			
	    			$(this).parent().parent().find('td.diseaseSubfacet').addClass('highlight');
					$.fn.composeFacetFilterControl($(this), self.options.data.q);					
				});	    		
    		}
	    	
	    	/*--------------------------console.log('inside here');	----------------------------------------------------------*/
	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	/*------------------------------------------------------------------------------------*/	
	    	
	    	
	    	if ( self.options.data.fq.match(/.*/) ){	
	    		
	    		self.options.data.q = window.location.search == '' ? '*:*' : window.location.search.replace('?q=', '');
	    		
	    		var pageReload = true;  // this controls checking which subfacet to open (ie, show by priority)
	    	
	    		var oHashParams = {};
	    		oHashParams.q = self.options.data.q;
	    		oHashParams.fq = self.options.data.fq;
	    		oHashParams.coreName = 'diseaseFacet';
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
	



