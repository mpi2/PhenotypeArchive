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
    $.widget('MPI2.geneFacet', {
    	
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
					
						// if no selected subfacet, load all results of this facet
						if ( caller.find('table#geneFacetTbl td.highlight').size() == 0 ){						
							//window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);									
						}	
						else {		
							// if there is selected subfacets: work out the url							
							if ( self.options.data.core != oHashParams.coreName ){															
							
								var fqFieldVals = {};
								
								caller.find('table#geneFacetTbl td.highlight').each(function(){									
									var val = $(this).siblings('td').find('a').attr('rel');								
									var fqField = $(this).siblings('td').find('a').attr('class');
									
									var fqFieldOri = fqField;
									fqField = fqField.indexOf('imits_phenotype') != -1 ? 'imits_phenotype' : fqField;
									if ( typeof fqFieldVals[fqField] === 'undefined' ){
										fqFieldVals[fqField] = [];										
									}		
																	
									fqFieldVals[fqField].push(fqFieldOri + ':"' + val + '"');
								});					
								
								var fqStr = $.fn.compose_AndOrStr(fqFieldVals);
							
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
				}
			});				
						
			// click on SUM facetCount to fetch results in grid										
			caller.find('span.facetCount').click(function(event){
				
				if ( $(this).text() != '0' ){
					
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
					
					$.fn.removeFacetFilter(solrCoreName);
					
					// remove highlight from selected				
					$('table#geneFacetTbl td').removeClass('highlight');
					
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
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.field': 'marker_type',				
				'facet.sort': 'count',					
	    		'q': self.options.data.q},
	    		MPI2.searchAndFacetConfig.commonSolrParams,
	    		MPI2.searchAndFacetConfig.facetParams.geneFacet.filterParams
	    	);    	   	
	    	
	    	var queryParamStr = $.fn.stringifyJsonAsUrlParams(queryParams) 
	    					  + '&facet.field=status'
	    	                  + '&facet.field=imits_phenotype_started' 
	    	                  + '&facet.field=imits_phenotype_complete'
	    	                  + '&facet.field=imits_phenotype_status';
	    	
	    	$.ajax({ 				 					
	    		'url': solrUrl + '/gene/select',
	    		'data': queryParamStr, 
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) {	    		
	    			self._displayGeneSubTypeFacet(json);	    				
	    		}		
	    	});	    	
	    },
	    
	    _displayGeneSubTypeFacet: function(json){	    	
	    	var self = this;
	    	var numFound = json.response.numFound;
	    		    	
	    	/*-------------------------------------------------------*/
	    	/* ------ displaying sidebar and update dataTable ------ */
	    	/*-------------------------------------------------------*/
	    	
	    	if (numFound > 0){
	    		
	    		// subfacet: IMPC mouse phenotyping status
	    		var table = $("<table id='geneFacetTbl' class='facetTable'></table>");	
	    		table.append($('<tr></tr>').attr({'class':'facetSubCat'}).append($('<td></td>').attr({'colspan':3}).text('IMPC Phenotyping Status')));
	    			    		
	    		var pheno_count = {};
	    		var aImitsPhenos = {'imits_phenotype_complete':'Complete', 
	    							'imits_phenotype_started':'Started', 
	    							'imits_phenotype_status':'Attempt Registered'};
	    		
	    		for (var key in aImitsPhenos ){	    			
	    			var phenoFieldList = json.facet_counts['facet_fields'][key];	    			
	    			if ( phenoFieldList.length != 0 ){
	    				
	    	    		for ( var j=0; j<phenoFieldList.length; j+=2 ){
	    	    			// skip status '0'	    	    		
	    	    			if ( phenoFieldList[j] == 'Phenotype Attempt Registered' || phenoFieldList[j] == 1 ){
	    						pheno_count[aImitsPhenos[key]] = phenoFieldList[j+1];
	    	    			}	    	    			
	    	    		} 
	    			}    			
	    		}
	    	
	    		var phenoStatusTr = '';
	    		var aPhenos = ['Complete', 'Started', 'Attempt Registered'];	    		
	    		for ( var i=0; i<aPhenos.length; i++ ){
					var phenotypingStatusFq = MPI2.searchAndFacetConfig.phenotypingStatuses[aPhenos[i]].fq;
					var phenotypingStatusVal = MPI2.searchAndFacetConfig.phenotypingStatuses[aPhenos[i]].val; 
					var count = pheno_count[aPhenos[i]];
					
					if ( count !== undefined ){	
						var coreField = 'gene|'+ phenotypingStatusFq + '|';					
						//var chkbox = "<input type=checkbox rel=" + coreField + phenotypingStatusVal + ">";	
						var chkbox = $('<input></input>').attr({'class':'phenotyping', 'type': 'checkbox', 'rel': coreField + phenotypingStatusVal + '|' + count});
						var td0 = $('<td></td>').append(chkbox);
						var tr = $('<tr></tr>').attr({'class':'subFacet'});						
						var td1 = $('<td></td>').attr({'class':'phenotypingStatus geneSubfacet', 'rel':count}).text(aPhenos[i]);
						var link = $('<a></a>').attr({'rel': phenotypingStatusVal, 'class': phenotypingStatusFq}).text(count);
						var td2 = $('<td></td>').attr({'class':'geneSubfacetCount', 'rel':phenotypingStatusVal}).append(link);
						table.append(tr.append(td0, td1, td2));
					}					
				}	    		
	    		
	    		// subfacet: IMPC mouse production status    		
	    		table.append($('<tr></tr>').attr({'class':'facetSubCat'}).append($('<td></td>').attr({'colspan':3}).text('IMPC Mouse Production Status')));
	    		
	    		var status_facets = json.facet_counts['facet_fields']['status'];
	    		var status_count = {};
	    		for ( var i=0; i<status_facets.length; i+=2 ){ 
					var type = status_facets[i];
					var count = status_facets[i+1];
					status_count[type] = count; 
	    		}    			
				
				for ( var i=0; i<MPI2.searchAndFacetConfig.geneStatuses.length; i++ ){
					var status = MPI2.searchAndFacetConfig.geneStatuses[i];
					var count = status_count[MPI2.searchAndFacetConfig.geneStatuses[i]];
					
					if ( count !== undefined ){
						var coreField = 'gene|status|';
						var chkbox = $('<input></input>').attr({'class':'production', 'type': 'checkbox', 'rel': coreField + status + '|' + count});
						var td0 = $('<td></td>').append(chkbox);
						var tr = $('<tr></tr>').attr({'class':'subFacet'});
						var td1 = $('<td></td>').attr({'class':'geneStatus geneSubfacet', 'rel':count}).text(status);
						var link = $('<a></a>').attr({'rel': status, 'class': 'status'}).text(count);
						var td2 = $('<td></td>').attr({'class':'geneSubfacetCount', 'rel':status}).append(link);
						table.append(tr.append(td0, td1, td2));						
					}					
				}	    		
	    		
				// subfacet: IMPC gene subtype
	    		var unclassified_gene_subType;	    		
	    		table.append($('<tr></tr>').attr({'class':'facetSubCat geneSubTypeTrCap'}).append($('<td></td>').attr({'colspan':3}).text('Subtype')));
	    			    		
	    		var mkr_facets = json.facet_counts['facet_fields']['marker_type'];
	    		var unclassifiedTr;
	    		for ( var i=0; i<mkr_facets.length; i+=2 ){		    			
	    			
					var type = mkr_facets[i];
					var count = mkr_facets[i+1];	
					var coreField = 'gene|marker_type|';						
					var chkbox = $('<input></input>').attr({'class':'subtype', 'type': 'checkbox', 'rel': coreField + type + '|' + count});
					var td0 = $('<td></td>').append(chkbox);
					var tr = $('<tr></tr>').attr({'class':'geneSubTypeTr'});
					var td1 = $('<td></td>').attr({'class':'geneSubtype geneSubfacet', 'rel':count}).text(type);
					var link = $('<a></a>').attr({'rel': type, 'class': 'marker_type'}).text(count);
					var td2 = $('<td></td>').attr({'class':'geneSubfacetCount', 'rel':type}).append(link);					
					
					if ( type != 'unclassified gene' ){	
						table.append(tr.append(td0, td1, td2));
					}
					else {
						unclassifiedTr = tr.append(td0, td1, td2);
					}					
	    		} 
	    		  
	    		if (unclassifiedTr){
	    			table.append(unclassifiedTr);
	    		}	    		
	    		
	    		//var table = "<table id='geneFacetTbl' class='facetTable'>" + trs + "</table>";				
	    		$('div#geneFacet div.facetCatList').html(table);
	    			
	    		// gene subtype is collapsed by default
	    		$('tr.geneSubTypeTrCap').toggle(
	    			function(){
	    				$('tr.geneSubTypeTr').show();
	    				$(this).find('td').addClass('unCollapse');
	    			},
	    			function(){
	    				$('tr.geneSubTypeTr').hide();
	    				$(this).find('td').removeClass('unCollapse');
	    			}
	    		);	    		    		
	    			    		
	    		$('table#geneFacetTbl td.geneSubfacetCount a').click(function(){
	    		
	    			// also remove all filters for that facet container	
	    			$.fn.removeFacetFilter('gene');
	    			// now update filter
	    			$.fn.addFacetFilter($(this).parent().parent().find('input'), self.options.data.q); 	        			
	    			
	    			// uncheck all facet filter checkboxes 
	    			$('table#geneFacetTbl input').attr('checked', false);
	    			// now check this checkbox
	    			$(this).parent().parent().find('input').attr('checked', true);
	    			
	    			// remove all highlight
	    			$('table#geneFacetTbl td.geneSubfacet').removeClass('highlight');
	    			// now highlight this one
	    			$(this).parent().parent().find('td.geneSubfacet').addClass('highlight');
		    			        			
	    			// update hash tag so that we know there is hash change, which then triggers loadDataTable	  	    			
		    		var fqStr = $(this).attr('class') + ':"' + $(this).attr('rel') + '"';
		    		
		    		if (self.options.data.q == '*:*'){
		    			window.location.hash = 'q=' +  self.options.data.q + '&fq=' + fqStr + '&core=gene';
		    		}
		    		else {
		    			window.location.hash = 'fq=' + fqStr + '&core=gene';
		    		}
	    		});  
	    		$('table#geneFacetTbl input').click(function(){
	    			
	    			// // highlight the item in facet	    			
	    			$(this).parent().find('td.geneSubfacet').addClass('highlight');
					$.fn.composeFacetFilterControl($(this), self.options.data.q);					
				});	    		
    		}
	    	
	    	/*--------------------------console.log('inside here');	----------------------------------------------------------*/
	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	/*------------------------------------------------------------------------------------*/	
	    	
	    	
	    	if ( self.options.data.fq.match(/.*/) ){	
	    		
	    		self.options.data.q = window.location.search == '' ? '*:*' : window.location.search.replace('?q=', '');
	    		
	    		$.fn.parseUrlForFacetCheckboxAndTermHighlight(self.options.data.q, self.options.data.fq, 'geneFacet');
	    		
	    		// now load dataTable    		
	    		$.fn.loadDataTable(self.options.data.q, self.options.data.fq, 'geneFacet'); 
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
	



