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
						
						var oHashParams = $.fn.parseHashString(window.location.hash.substring(1));			
												
						oHashParams.fq = $.fn.fieldNameMapping(oHashParams.fq, 'pipeline');
						
						var mode = typeof oHashParams.facetName != 'undefined' ? '&facet=' : '&core=';												
						window.location.hash = 'q=' + oHashParams.q + '&fq=' + oHashParams.fq + mode +  solrCoreName;
					}	
				}								
			});	
			
			// click on SUM facetCount to fetch results in grid: deprecated	
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
	    		//'fq': 'pipeline_stable_id:IMPC_001',
	    		'fq': self.options.data.hashParams.fq,
				'rows': 0,
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,				
				'facet.sort': 'index',			
				//'fl': 'parameter_name,parameter_stable_key,parameter_stable_id,procedure_name,procedure_stable_key,procedure_stable_id',				
				'q': self.options.data.hashParams.q}, MPI2.searchAndFacetConfig.commonSolrParams);	    		    	
	    	
	    	//console.log(queryParams);
	    	console.log(self.options.data.hashParams.fq);
	    	var queryParamStr = $.fn.stringifyJsonAsUrlParams(queryParams)	    				
	    				+ '&facet.field=pipeline_name'
	    				+ '&facet.field=pipe_proc_sid';
	    	
	    	$.ajax({ 
	    		'url': solrUrl + '/pipeline/select',
	    		'data': queryParamStr,
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) { 
	    			console.log(json);
	    			
	    			// update this if facet is loaded by redirected page, which does not use autocomplete
	    			$('div#pipelineFacet .facetCount').attr({title: 'total number of unique parameter terms'}).text(json.response.numFound);
	        			        		
	    			var procedures_params = {};
	    			var facetCountSum = 0;	    			    					 			
	    				    			
	    			var plFacets = json.facet_counts['facet_fields']['pipeline_name'];	    			
	    			var prFacets = json.facet_counts['facet_fields']['pipe_proc_sid'];
	    			
	    			var table = $("<table id='pipelineFacetTbl' class='facetTable'></table>");
	        		
	    			var aImpc = [];
	    			
	        		for ( var p=0; p<plFacets.length; p+=2){
	        			var currPipe = plFacets[p];	
	        			var pipeClass = currPipe.replace(/ /g, '_');
	        			var trCat = $('<tr></tr>').attr({'class':'facetSubCat ' + pipeClass + 'Cap ' + pipeClass}).append( $('<td></td>').attr({'colspan':3}).text(currPipe));
	        			
	        			// place IMPc pipeline on top of list	        			
	        			if ( currPipe != 'IMPC Pipeline' ){
	        				table.append(trCat);
	        			}
	        			else {	        			
	        				aImpc.push(trCat);
	        			}
		        		
		        		for ( var f=0; f<prFacets.length; f+=2 ){ 		        			        			
		        			var aVals = prFacets[f].split('___');
		        			var pipeName = aVals[0];
		        			var procedure_name = aVals[1];
		        			var proSid = aVals[2];
		        			var paramCount = prFacets[f+1];
		        					        			
		        			if (pipeName == currPipe ){
			        		
		        				//console.log(pipeName + ' --- ' + procedure_name + ' --- '+ paramCount);
			        			//var pClass = 'procedure'+f + ' ' + procedureName2IdKey[procedure_name].stable_id;
			        			var pClass = 'procedure'+f + ' ' + proSid;
			        			var tr = $('<tr></tr>').attr({'class':'subFacet ' + pipeClass});		        		
			        			
			        			//var coreField = 'pipeline|procedure_stable_id|' + procedure_name + '___' + procedureName2IdKey[procedure_name].stable_id + '|' + paramCount;
			        			var coreField = 'pipeline|procedure_stable_id|' + procedure_name + '___' + proSid + '|' + paramCount;
			        			
			        			var chkbox = $('<input></input>').attr({'class': pipeClass, 'type': 'checkbox', 'rel': coreField});	        			
			        			var td0 = $('<td></td>').append(chkbox);
			        			var td1 = $('<td></td>').attr({'class': pClass, 'rel':paramCount});	        			
			        			var td2 = $('<td></td>');	        			        			
			        			//var a = $('<a></a>').attr({'class':'paramCount', 'rel': procedureName2IdKey[procedure_name].stable_id}).text(paramCount);			        			
			        			var a = $('<a></a>').attr({'class':'paramCount', 'rel': proSid}).text(paramCount);
			        			
			        			if ( currPipe != 'IMPC Pipeline' ){
			        				table.append(tr.append(td0, td1.text(procedure_name), td2.append(a)));
			        			}
			        			else {
			        				aImpc.push(tr.append(td0, td1.text(procedure_name), td2.append(a)));
			        			}	
		        			}	
		        			
		        		} 	
		        		table.prepend(aImpc);
	        		}
	        		if (json.response.numFound == 0 ){
	        			table = null;
	        		}	    			
	        		$('div#pipelineFacet .facetCatList').html(table);
	        		
	        		
	        		// all pipelines, except IMPC pipeline, are collapsed by default
		    		$('table#pipelineFacetTbl tr.facetSubCat').click(function(){
		    			
		    			var aClass = $(this).attr('class').split(' ');
		    			for (var i=0; i<aClass.length; i++){
		    				if ( aClass[i].indexOf('Cap') != -1 ){		    					
		    					var trClass = aClass[1].replace('Cap','');	 

				    			if ( $(this).find('td').hasClass('unCollapse')){				    			
				    				$('tr.subFacet').each(function(){
				    					if ( $(this).hasClass(trClass) ){
				    						$(this).hide();
				    					}
				    				});
				    				
				    				$(this).find('td').removeClass('unCollapse');
				    			}
				    			else {				    			
				    				$('tr.subFacet').each(function(){
				    					if ( $(this).hasClass(trClass) ){
				    						$(this).show();
				    					}
				    				});
				    						
				    				$(this).find('td').addClass('unCollapse');
				    			}
				    			break;
		    				}
		    			}	    			
		    			
		    		});	   
	        		
		    		// update facet count when filters applied
	    			if ( $('ul#facetFilter li li a').size() != 0 ){	    				
	    				$.fn.fetchQueryResult(self.options.data.hashParams.q, 'pipeline');
	    			}	        		
	        		
	        		
	        		$('table#pipelineFacetTbl input').click(function(){
	        			console.log('click.....');
	        			// highlight the item in facet
	        			$(this).parent().siblings('td[class^=procedure]').addClass('highlight');
	        			$.fn.composeFacetFilterControl($(this), self.options.data.hashParams.q);
	        		});	        		       		
	        		
	        		/*------------------------------------------------------------------------------------*/
	    	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	    	/*------------------------------------------------------------------------------------*/        		      		
	        		
	        		if ( self.options.data.hashParams.fq.match(/.*/) ){     
	        			var pageReload = true;  // this controls checking which subfacet to open (ie, show by priority) 
	        				        			
	        			var oHashParams = self.options.data.hashParams;
	        			console.log(oHashParams);
	    	    		$.fn.parseUrlForFacetCheckboxAndTermHighlight(oHashParams, pageReload);	    	    		
	    	    		// now load dataTable    		
	    	    		$.fn.loadDataTable(oHashParams);
	        		}
	    		}	    		
	    	});	    	
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
	



