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
																
						if (MPI2.searchAndFacetConfig.facetParams[facetDivId].subFacet_filter_params){						
							solrSrchParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].subFacet_filter_params;							
						}
		              
						solrSrchParams.q = self.options.data.q;									
											
						var hashParams = $.fn.parseHashString(window.location.hash.substring(1));
						
						currHashParams.q = self.options.data.q;
						currHashParams.core = solrCoreName;
						currHashParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
						
						// update hash
						if ( caller.find('table#geneFacetTbl td.highlight').size() == 0 ){
							window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);									
						}
						else {
							if ( self.options.data.core != hashParams.coreName ){
								self._loadFilteredDataTable();								
								/*var fqTextList = [];
								var displayedFilter = [];
								
								caller.find('table#geneFacetTbl td.highlight').each(function(){
									var fqText = $(this).text();
									
									if ( $(this).hasClass('geneStatus') ){
										fqTextList.push('status:"' + fqText + '"');
									}
									else if ( $(this).hasClass('geneSubtype') ){
										fqTextList.push('marker_type:"' + fqText + '"');
									}
									else if ( $(this).hasClass('phenotypingStatus') ){
										var fq = MPI2.searchAndFacetConfig.phenotypingStatuses[fqText].fq;
										var val = MPI2.searchAndFacetConfig.phenotypingStatuses[fqText].val;
										fqTextList.push(fq + ':"' + val + '"');
										fqText = val;
									}								
									
									displayedFilter.push(fqText);
								});
								
								var fqText = fqTextList.join(' OR ');
								
								var obj = {'fqStr'  : MPI2.searchAndFacetConfig.facetParams['geneFacet'].fq + ' AND (' + fqText + ')',
										   'filter' : displayedFilter.join(" OR "),
										   'chkbox' : null,
										   'multiFilter': true
										   }; 
						    										   			
								$.fn.fetchFilteredDataTable(obj, 'geneFacet', self.options.data.q);*/
							}							
						}
					}	
				}
			});				
						
			// click on SUM facetCount to fetch results in grid										
			caller.find('span.facetCount').click(function(event){
				
				if ( $(this).text() != '0' ){ 
					//console.log('facet count click');
					var gridName = MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName;
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
					var solrSrchParams = {}
					var hashParams = {};							
					
					$.fn.removeFacetFilter(solrCoreName);
					
					// remove highlight from selected 			
					$('table#geneFacetTbl td').removeClass('highlight');
											
					solrSrchParams = $.extend({},		
							MPI2.searchAndFacetConfig.commonSolrParams,
							MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams 
					);
					solrSrchParams.facetCount = $(this).text();
					solrSrchParams.q = self.options.data.q;									
									
					hashParams.q = self.options.data.q;
					hashParams.core = solrCoreName;
					hashParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
			 	   	
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
	    	//MPI2.searchAndFacetConfig.commonSolrParams.rows = 10;
	   
	    	//var queryParams = $.extend({},{		
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
	    	
	    	// update this if facet is loaded by redirected page, which does not use autocomplete
	    	//$('div#geneFacet span.facetCount').attr({title: 'total number of unique genes'}).text(numFound);
	    	
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
						var tr = $('<tr></tr>').attr({'class':'subFacet'});
						var td1 = $('<td></td>').attr({'class':'phenotypingStatus geneSubfacet', 'rel':count}).text(aPhenos[i]);
						var link = $('<a></a>').attr({'rel': phenotypingStatusVal, 'class': phenotypingStatusFq}).text(count);
						var td2 = $('<td></td>').attr({'class':'geneSubfacetCount', 'rel':phenotypingStatusVal}).append(link);
						table.append(tr.append(chkbox, td1, td2));
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
						var tr = $('<tr></tr>').attr({'class':'subFacet'});
						var td1 = $('<td></td>').attr({'class':'geneStatus geneSubfacet', 'rel':count}).text(status);
						var link = $('<a></a>').attr({'rel': status, 'class': 'status'}).text(count);
						var td2 = $('<td></td>').attr({'class':'geneSubfacetCount', 'rel':status}).append(link);
						table.append(tr.append(chkbox, td1, td2));						
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
					var tr = $('<tr></tr>').attr({'class':'geneSubTypeTr'});
					var td1 = $('<td></td>').attr({'class':'geneSubtype geneSubfacet', 'rel':count}).text(type);
					var link = $('<a></a>').attr({'rel': type, 'class': 'marker_type'}).text(count);
					var td2 = $('<td></td>').attr({'class':'geneSubfacetCount', 'rel':type}).append(link);					
					
					if ( type != 'unclassified gene' ){	
						table.append(tr.append(chkbox, td1, td2));
					}
					else {
						unclassifiedTr = tr.append(chkbox, td1, td2);
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
	    			$.fn.fetchFilteredDataTable($(this), 'geneFacet', self.options.data.q, 'facetFilter');	    			
	    				    			
	    			// uncheck all facet filter checkboxes 
	    			$('div#geneFacet').find('input').attr('checked', false);
	    			// also remove all filters for that facet container	
	    			$.fn.removeFacetFilter('gene');	
	    			$(this).parent().parent().find('input').attr('checked', true);			
	    			$.fn.addFacetFilter($(this).parent().parent().find('input'), self.options.data.q);
	    		});
	    		
	    		$('table#geneFacetTbl input').click(function(){
	    			
	    			// // highlight the item in facet	    			
	    			$(this).parent().find('td.geneSubfacet').addClass('highlight');
					$.fn.composeFacetFilterControl($(this), self.options.data.q);					
				});
    		}
	    	
	    	/*------------------------------------------------------------------------------------*/
	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	/*------------------------------------------------------------------------------------*/	    			    	
	    	
	    	if ( self.options.data.fq != undefined ){
	    			    		
	    	   /* if ( self.options.data.multiFilter ){
	    	    	self._checkSubfacet(self.options.data.fq);	    	    	
	    	    }*/
	    	  
	    		//var subFacet = self.options.data.fq.replace(/\w+:/, '').replace(/"/g,'');
	    		console.log('gene filtered: ');		    		
	    		//var obj = $('div#geneFacet div.facetCatList').find("table#geneFacetTbl td[rel='" + subFacet + "']").find('a');  	    
    	        // work out fq field(s) so that we can tich checkbox(es) for facet item(s)
	    		
	    		var aFields = MPI2.searchAndFacetConfig.facetParams['geneFacet'].subFacetFqFields;	        	
	    		$.fn.parseUrlForFacetCheckbox(self.options.data.q, self.options.data.fq, 'geneFacet', aFields);
	    		
	    		// now load dataTable	    		
	    		$.fn.loadDataTable(self.options.data.q, self.options.data.fq, 'geneFacet');
	    		
	    		//$.fn.fetchFilteredDataTable(obj, 'geneFacet', self.options.data.q);
	    	    
	    	}	    
	    	else if ( self.options.data.fq == undefined ){ 
	    			    		
	    		console.log('gene UNfiltered');
	    		var solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.commonSolrParams, MPI2.searchAndFacetConfig.facetParams['geneFacet'].filterParams);	    		
    			solrSrchParams.q = self.options.data.q; 
    			
    			solrSrchParams.coreName = 'gene'; // to work out breadkCrumb facet display    			
    			solrSrchParams.facetCount = self.options.data.facetCount;    			
    			$.fn.invokeFacetDataTable(solrSrchParams, 'geneFacet', MPI2.searchAndFacetConfig.facetParams['geneFacet'].gridName);  
	    	}
	    },	       
	    
	    _checkSubfacet: function(fqStr){
	    	
	    	var self = this;
	    	
	    	console.log('*'+fqStr+'*');
	    	var filters = fqStr.replace('marker_type:* -marker_type:"heritable phenotypic marker" AND ', '').replace(/^\(|\)$/g, '');
	    
	    	var aKw = filters.split(' OR ');
	    	for (var i=0; i<aKw.length; i++){
	    		var akeyVal = aKw[i].split(':');
	    		var classStr =  akeyVal[0];
	    		var relVal   =  akeyVal[1].replace(/^"|"$/g, '');
	    		
	    		$('table#geneFacetTbl td a').each(function(){	    			
	    			if ( $(this).hasClass(classStr) && $(this).attr('rel') == relVal ){	    			
	    				if ( classStr == 'marker_type' ){
	    					$(this).parent().siblings('td.geneSubtype').addClass('highlight');
	    				}
	    				else if ( classStr == 'status' ){
	    					$(this).parent().siblings('td.geneStatus').addClass('highlight');
	    				}
	    				else if ( classStr.indexOf('imits_phenotype_') != -1 ){	    				
	    					$(this).parent().siblings('td.phenotypingStatus').addClass('highlight'); 
	    				}	    				
	    				
	    				$(this).parent().siblings('input').prop('checked', true);
	    				//$.fn.composeFacetFilterControl(oChkbox, self.options.data.q);
	    			}
	    		});
	    	}   	
	    	//self._loadFilteredDataTable();
	    	
	    },	
	    
	    _loadFilteredDataTable: function(){	
	    	var self = this;	    	
			var fqTextList = [];
			var displayedFilter = [];
			
			$('table#geneFacetTbl td.highlight').each(function(){
				
				var fqText = $(this).text();
				console.log(fqText);
				if ( $(this).hasClass('geneStatus') ){
					fqTextList.push('status:"' + fqText + '"');
				}
				else if ( $(this).hasClass('geneSubtype') ){
					fqTextList.push('marker_type:"' + fqText + '"');
				}
				else if ( $(this).hasClass('phenotypingStatus') ){
					var fq = MPI2.searchAndFacetConfig.phenotypingStatuses[fqText].fq;
					var val = MPI2.searchAndFacetConfig.phenotypingStatuses[fqText].val;
					fqTextList.push(fq + ':"' + val + '"');
					fqText = val;
				}								
				
				displayedFilter.push(fqText);
			});
			
			var fqText = fqTextList.join(' OR ');
			
			var obj = {'fqStr'  : MPI2.searchAndFacetConfig.facetParams['geneFacet'].filterParams.fq + ' AND (' + fqText + ')',
					   'filter' : displayedFilter.join(" OR "),
					   'chkbox' : null,
					   'multiFilter': true
					   }; 
	    	console.log(obj);
	    	
			$.fn.fetchFilteredDataTable(obj, 'geneFacet', self.options.data.q);
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
	



