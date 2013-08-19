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
						if ( caller.find('table#gFacet td.highlight').size() == 0 ){
							window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);									
						}
						else {
							if ( self.options.data.core != hashParams.coreName ){
								var fqText = caller.find('table#gFacet td.highlight').text();
								if ( caller.find('table#gFacet td.highlight').hasClass('geneStatus') ){
									currHashParams.fq = 'status:"' + fqText +'"';
								}
								else if ( caller.find('table#gFacet td.highlight').hasClass('geneSubtype') ){
									currHashParams.fq = 'marker_type:"' + fqText +'"';
								}
								else if ( caller.find('table#gFacet td.highlight').hasClass('phenotypingStatus') ){									
									var fq = MPI2.searchAndFacetConfig.phenotypingStatuses[fqText].fq;
									var val = MPI2.searchAndFacetConfig.phenotypingStatuses[fqText].val;
									currHashParams.fq = fq + ':"' + val +'"';
								}
								window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);
								// reload dataTable
								self._reloadDataTableForHashUrl();
							}							
						}
						
						// dataTable code					
						//console.log('name: ' + MPI2.searchAndFacetConfig.facetParams[facetDivId].topLevelName);
						if ( $('table#'+ gridName).size() != 1 ){
							$.fn.invokeFacetDataTable(solrSrchParams, facetDivId, gridName);						
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
					
					// remove highlight from selected 			
					$('table#gFacet td').removeClass('highlight');
											
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
	    		
	    		var trs = '';
	    		// subfacet: IMPC Phenotype
	    		var phenoStatusTrCap = "<tr class='facetSubCat'><td colspan=2>IMPC Phenotyping Status</td></tr>";
	    		
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
	    		//console.log(pheno_count);
	    		var phenoStatusTr = '';
	    		var aPhenos = ['Complete', 'Started', 'Attempt Registered'];	    		
	    		for ( var i=0; i<aPhenos.length; i++ ){
					var phenotypingStatusFq = MPI2.searchAndFacetConfig.phenotypingStatuses[aPhenos[i]].fq;
					var phenotypingStatusVal = MPI2.searchAndFacetConfig.phenotypingStatuses[aPhenos[i]].val; 
					var count = pheno_count[aPhenos[i]];
					
					if ( count !== undefined ){						
						phenoStatusTr += "<tr><td class='phenotypingStatus geneSubfacet' rel=" + count + ">" + aPhenos[i] + "</td>"
							+ "    <td rel='" + phenotypingStatusVal + "' class='geneSubfacetCount'><a rel='" + phenotypingStatusVal + "' class='" + phenotypingStatusFq + "'>" + count + "</a></td>"
							+  "</tr>";
					}					
				}	
	    		if (phenoStatusTr != ''){
	    			trs += phenoStatusTrCap + phenoStatusTr;
	    		}
	    		
	    		// subfacet: IMPC Mouse Production
	    		trs += "<tr class='facetSubCat'><td colspan=2>IMPC Mouse Production Status</td></tr>";
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
						trs += "<tr><td class='geneStatus geneSubfacet' rel=" + count + ">" + status + "</td><td rel='" + status + "' class='geneSubfacetCount'><a rel='" + status + "' class='status'>" + count + "</a></td></tr>";
					}					
				}	    		
	    		
				// subfacet: IMPC gene subtype
	    		var unclassified_gene_subType;	    		
	    		trs += "<tr class='facetSubCat geneSubTypeTrCap'><td colspan=2>Subtype</td></tr>";
	    		var mkr_facets = json.facet_counts['facet_fields']['marker_type'];
	    		for ( var i=0; i<mkr_facets.length; i+=2 ){		    			
	    			//console.log( facets[i] + ' ' + facets[i+1]);
					var type = mkr_facets[i];
					var count = mkr_facets[i+1];			
					if ( type == 'unclassified gene' ){					
						//unclassified_gene_subType = "<tr class='geneSubTypeTr'><td class='geneSubtype geneSubfacet'>" + type + "</td><td rel='" + type + "' class='geneSubfacetCount'><a rel='" + type + "' class='subtype'>" + count + "</a></td></tr>";
						unclassified_gene_subType = "<tr class='geneSubTypeTr'><td class='geneSubtype geneSubfacet' rel="+ count + ">" + type + "</td><td rel='" + type + "' class='geneSubfacetCount'><a rel='" + type + "' class='marker_type'>" + count + "</a></td></tr>";
						
					}
					else {
						//trs += "<tr class='geneSubTypeTr'><td class='geneSubtype geneSubfacet'>" + type + "</td><td rel='" + type + "' class='geneSubfacetCount'><a rel='" + type + "' class='subtype'>" + count + "</a></td></tr>";
						trs += "<tr class='geneSubTypeTr'><td class='geneSubtype geneSubfacet' rel=" + count + ">" + type + "</td><td rel='" + type + "' class='geneSubfacetCount'><a rel='" + type + "' class='marker_type'>" + count + "</a></td></tr>";
					}
	    		} 
	    		if ( unclassified_gene_subType ){
	    			trs += unclassified_gene_subType
	    		}
	    		
	    		
	    		var table = "<table id='gFacet' class='facetTable'>" + trs + "</table>";				
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
	    		
	    		self._applyGeneGridResultFilterByMarkerSubFacet($('table#gFacet td.geneSubfacetCount a'));	    		
    		}
	    	
	    	/*------------------------------------------------------------------------------------*/
	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	/*------------------------------------------------------------------------------------*/	    			    	
	    	
	    	if ( self.options.data.fq != undefined ){	    		
	    		var subFacet = self.options.data.fq.replace(/\w+:/, '').replace(/"/g,'');
	    		//console.log('gene filtered: '+ subFacet);
	    		
	    		var obj = $('div#geneFacet div.facetCatList').find("table#gFacet td[rel='" + subFacet + "']").find('a');	    		
	    		$.fn.fetchFilteredDataTable(obj, 'geneFacet', self.options.data.q);
	    	}	    
	    	else if ( self.options.data.fq == undefined ){ 
	    			    		
	    		//console.log('gene UNfiltered');
	    		var solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.commonSolrParams, MPI2.searchAndFacetConfig.facetParams['geneFacet'].filterParams);	    		
    			solrSrchParams.q = self.options.data.q; 
    			
    			solrSrchParams.coreName = 'gene'; // to work out breadkCrumb facet display    			
    			solrSrchParams.facetCount = self.options.data.facetCount;    			
    			$.fn.invokeFacetDataTable(solrSrchParams, 'geneFacet', MPI2.searchAndFacetConfig.facetParams['geneFacet'].gridName);  
	    	}
	    },

		_applyGeneGridResultFilterByMarkerSubFacet: function(obj){
			var self = this;
			// subFacet result trigger	
			obj.live('click', function(){			
			//	obj.click(function(){	
				// invoke dataTable	via hash state with the 4th param
				// ie, it does not invoke dataTable directly but through hash change				
				$.fn.fetchFilteredDataTable($(this), 'geneFacet', self.options.data.q, 'facetFilter');
				
			});

			/*function singleClick(e) {
			    // do something, "this" will be the DOM element
				console.log('single')
				// invoke dataTable	via hash state with the 4th param
				// ie, it does not invoke dataTable directly but through hash change				
				$.fn.fetchFilteredDataTable($(this), 'geneFacet', self.options.data.q, 'facetFilter');
			}

			function doubleClick(e) {
			    // do something, "this" will be the DOM element
				console.log('double')
				$.fn.fetchFilteredDataTable($(this), 'geneFacet', self.options.data.q, 'facetFilter');
			}

			obj.live('click', function(e){
			    var that = this;
			    setTimeout(function() {
			        var dblclick = parseInt($(that).data('double'), 10);
			        if (dblclick > 0) {
			            $(that).data('double', dblclick-1);
			        } else {
			            singleClick.call(that, e);
			        }
			    }, 300);
			}).dblclick(function(e) {
			    $(this).data('double', 2);
			    doubleClick.call(this, e);
			});
			*/
			
		},
   
		_reloadDataTableForHashUrl: function(){
			var self = this;
			var hashParams = $.fn.parseHashString(window.location.hash.substring(1));
    	
    		if ( typeof hashParams.fq !== 'undefined' ){    			
    			var aKV = hashParams.fq.split(':');
    			var sClass = aKV[0];
    			var sRel = aKV[1].replace(/"/g,'');	    		
    			$.fn.fetchFilteredDataTable($('a[rel="' + sRel + '"]'), 'geneFacet', self.options.data.q);
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
	



