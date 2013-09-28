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
    $.widget('MPI2.pipelineFacet', {
        
	    options: {},	
     
    	_create: function(){
    		
    		
    		// execute only once 	
    		var self = this;	
    		var facetDivId = self.element.attr('id');
    		var caller = self.element;
    		delete MPI2.searchAndFacetConfig.commonSolrParams.rows;    	   		  		
		
			caller.find('div.facetCat').click(function(){
				
				if ( caller.find('span.facetCount').text() != '0' ){
							
					//var gridName = MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName;
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
										
						
						// update hash
						if ( caller.find('table#pipelineFacetTbl td.highlight').size() == 0 ){						
							window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);									
						}
						else {
							// if there is selected subfacets: work out the url							
							if ( self.options.data.core != oHashParams.coreName ){															
							
								var fqFieldVals = {};
								
								caller.find('table#pipelineFacetTbl td.highlight').each(function(){									
									var val = $(this).siblings('td').find('a').attr('rel');								
									var fqField = 'procedure_stable_id';
									
									if ( typeof fqFieldVals[fqField] === 'undefined' ){
										fqFieldVals[fqField] = [];										
									}									
									fqFieldVals[fqField].push(fqField + ':"' + val + '"');
								});					
								
								var fqStr = $.fn.compose_AndOrStr(fqFieldVals);
								
								// update hash tag so that we know there is hash change, which then triggers loadDataTable  
			  	    			window.location.hash = 'q=' + self.options.data.q + '&core=' +  solrCoreName + '&fq=' + fqStr;	
							}							
						}			
						/*else {
							if ( self.options.data.core != hashParams.coreName ){
																
								var fqTextList = [];
								var displayedFilter = [];
								var facetCount = 0;
								caller.find('table#pipelineFacetTbl td.highlight').each(function(){									
									fqTextList.push('procedure_stable_id:' + $(this).siblings('td').find('a').attr('rel'));
									displayedFilter.push($(this).text());
									facetCount += $(this).siblings('td').find('a').text();
								});
								
								var fqText = fqTextList.join(' OR ');
								
								var obj = {'fqStr'  : MPI2.searchAndFacetConfig.facetParams['pipelineFacet'].fq + ' AND (' + fqText + ')',
										   'filter' : displayedFilter.join(" OR "),
										   'chkbox' : null,
										   'facetCount' : facetCount
										   }; 
						    										   			
								$.fn.fetchFilteredDataTable(obj, 'pipelineFacet', self.options.data.q);
							}							
						}*/						
					}	
				}								
			});	
													
			// click on SUM facetCount to fetch results in grid
			//$('span.facetCount').click(function(){								
			caller.find('span.facetCount').click(function(){	
				if ( $(this).text() != '0' ){	
					
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
					
					$.fn.removeFacetFilter(solrCoreName);
					
					// remove highlight from selected				
					$('table#pipelineFacetTbl td').removeClass('highlight');
					
					var fqStr = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
					
					// update hash tag so that we know there is hash change, which then triggers loadDataTable  
  	    			window.location.hash = 'q=' + self.options.data.q + '&core=' +  solrCoreName + '&fq=' + fqStr;
					
					/*var gridName = MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName;
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
					var solrSrchParams = {}
					var hashParams = {};				
					
					$.fn.removeFacetFilter(solrCoreName);
					
					// remove highlight from selected
					$('table#pipelineFacetTbl td[class^=procedure]').removeClass('highlight');	
					
					solrSrchParams = $.extend({}, 
							MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams, 
							MPI2.searchAndFacetConfig.commonSolrParams);	
					
					MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams.fq = "pipeline_stable_id:IMPC_001";	
					
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
					}	*/			
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
	    	var aProcedure_names = [];	    	
	    	  	
	    	var queryParams = $.extend({}, {	    		  		
	    		'fq': 'pipeline_stable_id:IMPC_001',				
				'rows': 500000,
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.field': 'procedure_name', //proc_param_name',
				'facet.sort': 'index',
				'fl': 'parameter_name,parameter_stable_key,parameter_stable_id,procedure_name,procedure_stable_key,procedure_stable_id',						
				'q': self.options.data.q}, MPI2.searchAndFacetConfig.commonSolrParams);	    		    	
	    	
	    	//console.log(queryParams);
	    	$.ajax({ 				 					
	    		'url': solrUrl + '/pipeline/select',
	    		'data': queryParams,
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) { 
	    			//console.log(json);
	    			
	    			// update this if facet is loaded by redirected page, which does not use autocomplete
	    			$('div#pipelineFacet .facetCount').attr({title: 'total number of unique parameter terms'}).text(json.response.numFound);
	        			        		
	    			var procedures_params = {};
	    			var facetCountSum = 0;
	    			
	    			var mappings = self._doNames2IdMapping(json.response);
	    			
	    			var procedureName2IdKey = mappings[0]; // stable_id
	    			var parameterName2IdKey = mappings[1]; // stabley_key
	    			
	    			//var facets = json.facet_counts['facet_fields']['proc_param_name'];	    			
	    			var facets = json.facet_counts['facet_fields']['procedure_name'];
	    			
	    			var table = $("<table id='pipelineFacetTbl' class='facetTable'></table>");
	        		var trCat = $('<tr></tr>').attr({'class':'facetSubCat'});
	        		table.append(trCat.append( $('<td></td>').attr({'colspan':3}).text('IMPC')));
	    			
	    			for ( var f=0; f<facets.length; f+=2 ){       			
	        			
	        			var procedure_name = facets[f];
	        			var paramCount = facets[f+1];
	        				        			
	        			var pClass = 'procedure'+f;
	        			var tr = $('<tr></tr>').attr({'class':'subFacet'});
	        			
	        			var coreField = 'pipeline|procedure_stable_id|' + procedure_name + '___' + procedureName2IdKey[procedure_name].stable_id + '|' + paramCount;	
	        			var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField});	        			
	        			
	        			var td1 = $('<td></td>').attr({'class': pClass, 'rel':paramCount});	        			
	        			var td2 = $('<td></td>');	        			        			
	        			var a = $('<a></a>').attr({'class':'paramCount', 'rel': procedureName2IdKey[procedure_name].stable_id}).text(paramCount);
	        			table.append(tr.append(chkbox, td1.text(procedure_name), td2.append(a)));
	        		} 			
	        		
	        		if (json.response.numFound == 0 ){
	        			table = null;
	        		}	    			
	        		$('div#pipelineFacet .facetCatList').html(table);
	        		
	        		$('table#pipelineFacetTbl td a.paramCount').click(function(){	        			
	        			//$.fn.fetchFilteredDataTable($(this), 'pipelineFacet', self.options.data.q, 'facetFilter');
	        			
	        			
	        			// also remove all filters for that facet container	
	        			//$.fn.removeFacetFilter('pipeline');	
	        			//$(this).parent().parent().find('input').attr('checked', true);
	        			//$.fn.addFacetFilter($(this).parent().parent().find('input'), self.options.data.q);/*
	        			
	        			// also remove all filters for that facet container	
	        			$.fn.removeFacetFilter('pipeline');
	        			// now update filter
	        			$.fn.addFacetFilter($(this).parent().parent().find('input'), self.options.data.q); 	        			
	        			
	        			// uncheck all facet filter checkboxes 
	        			$('table#pipelineFacetTbl input').attr('checked', false);
	        			// now check this checkbox
	        			$(this).parent().parent().find('input').attr('checked', true);
	        			
	        			// remove all highlight
	        			$('table#pipelineFacetTbl td[class^=procedure]').removeClass('highlight');
	        			// now highlight this one
	        			$(this).parent().parent().find('td[class^=procedure]').addClass('highlight');      			
	        			
	  	    			        			
	        			// update hash tag so that we know there is hash change, which then triggers loadDataTable	  	    			
	  	    			var fqStr = 'procedure_stable_id:"' + $(this).attr('rel')  + '"'; 
	  	    			window.location.hash = 'q=' +  self.options.data.q + '&fq=' + fqStr + '&core=pipeline'; 
	        		});
	        		
	        		$('table#pipelineFacetTbl input').click(function(){
	        			// highlight the item in facet
	        			$(this).parent().find('td[class^=procedure]').addClass('highlight');
	        			$.fn.composeFacetFilterControl($(this), self.options.data.q);
	        		});	        		       		
	        		
	        		/*------------------------------------------------------------------------------------*/
	    	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	    	/*------------------------------------------------------------------------------------*/	
	        		        		
	        		if ( self.options.data.fq.match(/pipeline_stable_id.+/) ){
	        			//console.log('1 pipeline UNfiltered');
	        			var solrSrchParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams['pipelineFacet'].filterParams, MPI2.searchAndFacetConfig.commonSolrParams);						
	        			solrSrchParams.q = self.options.data.q;	
	        			solrSrchParams.coreName = 'pipeline';  // to work out breadkCrumb facet display
	        			solrSrchParams.facetCount = self.options.data.facetCount;
	    	    		$.fn.invokeFacetDataTable(solrSrchParams, 'pipelineFacet', MPI2.searchAndFacetConfig.facetParams['pipelineFacet'].gridName, self.options.data.q);        					    	    	
	        		}
	        		else {
	        			//console.log('1 pipeline FILTERED');
	        			//var proc_sid = self.options.data.fq.replace('procedure_stable_id:', '');
	    	    		//var obj = $('div#pipelineFacet div.facetCatList').find("table#pipelineFacetTbl a[rel='" + proc_sid + "']"); 
	    	    		//$.fn.fetchFilteredDataTable(obj, 'pipelineFacet', self.options.data.q);
	        			
	        			var fields = ['procedure_stable_id'];	        	
	    	    		$.fn.parseUrlForFacetCheckbox(self.options.data.q, self.options.data.fq, 'pipelineFacet', fields);
	    	    	
	    	    		// now load dataTable	    		
	    	    		$.fn.loadDataTable(self.options.data.q, self.options.data.fq, 'pipelineFacet'); 
	        		}
	    		}	    		
	    	});	    	
	    },   
		    	    
	    _doNames2IdMapping: function(response){
	    	var nodes = response.docs;
	    	var procedureName2IdKey = {};
	    	var parameterName2IdKey = {};
	    	
	    	for( var n=0; n<nodes.length; n++){
	    		var node = nodes[n];	    		
	    			    		
	    		var procName = node.procedure_name;	    			    		
	    		var procSId  = node.procedure_stable_id;
	    		var procKey  = node.procedure_stable_key;
	    		
	    		var paramName = node.parameter_name;
	    		var paramSId  = node.parameter_stable_id;
	    		var paramKey  = node.parameter_stable_key;	    		
	    			    		
	    		if ( !procedureName2IdKey[procName] ){
	    			procedureName2IdKey[procName] = {};
	    		}
	    		if ( !parameterName2IdKey[paramName] ){
	    			parameterName2IdKey[paramName] = {};
	    		}
	    		procedureName2IdKey[procName] = {stable_id: procSId, stable_key: procKey};
	    		parameterName2IdKey[paramName] = {stable_id: paramSId, stable_key: paramKey};	    		
	    	}
	    	return [procedureName2IdKey, parameterName2IdKey];
	    },	     
	    
	    destroy: function () {    	   
	    	// does not generate selector class
    	    // if using jQuery UI 1.8.x
    	    $.Widget.prototype.destroy.call(this);
    	    // if using jQuery UI 1.9.x
    	    //this._destroy();
    	}  
    });
	
}(jQuery));	
	



