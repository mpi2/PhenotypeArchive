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
    $.widget('MPI2.pipelineFacet', {
        
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
	    	    	
	    	/*  	
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
	    	*/
	    	var fq = MPI2.searchAndFacetConfig.currentFq ? MPI2.searchAndFacetConfig.currentFq
	    			: self.options.data.hashParams.fq;
	    	
	    	var oParams = {};		
	        oParams = $.fn.getSolrRelevanceParams('pipeline', self.options.data.hashParams.q, oParams);
	    	
	    	var queryParams = $.extend({}, {				
				'fq': fq,
				'rows': 0, // override default
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.sort': 'index',						
				'q': self.options.data.hashParams.q}, MPI2.searchAndFacetConfig.commonSolrParams, oParams);			
	    	
	    	//console.log(queryParams);	    	
	    	var queryParamStr = $.fn.stringifyJsonAsUrlParams(queryParams)	    				
	    				+ '&facet.field=pipeline_name'
	    				+ '&facet.field=pipe_proc_sid';
	    	
	    	$.ajax({ 
	    		'url': solrUrl + '/pipeline/select',
	    		'data': queryParamStr,
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',
	    		'success': function(json) { 
	    			    			
	    			// update this if facet is loaded by redirected page, which does not use autocomplete
	    			//$('div#pipelineFacet .facetCount').attr({title: 'total number of unique parameter terms'}).text(json.response.numFound);
	        			    			
	    			var procedures_params = {};
	    			var facetCountSum = 0;	    			    					 			
	    				    			
	    			var plFacets = json.facet_counts['facet_fields']['pipeline_name'];	    			
	    			var prFacets = json.facet_counts['facet_fields']['pipe_proc_sid'];	    			  			
	        			    			
	    			var impc;
	    			
	        		for ( var p=0; p<plFacets.length; p+=2){
	        			var currPipe = plFacets[p];	
	        			var pipeClass = currPipe.replace(/ /g, '_');
	        			
	        			var thisFacetSect = $("<li class='fcatsection " + pipeClass + "'></li>");		 
		    			thisFacetSect.append($('<span></span>').attr({'class':'flabel'}).text(currPipe));		    			       			
		    			
		        		var thisUlContainer = $("<ul></ul>");
	    			
		        		var procedureChkboxLblCnt = {};		        		
		        		
		        		for ( var f=0; f<prFacets.length; f+=2 ){ 		        			        			
		        			var aVals = prFacets[f].split('___');
		        			var pipeName = aVals[0];
		        			var procedure_name = aVals[1];
		        			var proSid = aVals[2].replace(/_\d+$/, '*');
		        			var count = prFacets[f+1];
		        			
		        			//
		        			if ( typeof procedureChkboxLblCnt[pipeName] == 'undefined' ){
		        				procedureChkboxLblCnt[pipeName] = {};
		        			}
		        					        					        			
		        			if (pipeName == currPipe ){	
		        				//var pClass = 'procedure'+f + ' ' + proSid;
		        				var liContainer = $("<li></li>").attr({'class':'fcat ' + pipeClass});
		        				//console.log(pipeName + ' --- ' + procedure_name + ' --- '+ paramCount);		        			      		
			        			
			        			var coreField = 'pipeline|procedure_stable_id|' + procedure_name + '___' + proSid + '|' + count + '|' + pipeClass;
			        			
			        			var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField, 'class':pipeClass});			        			
			        			var flabel = $('<span></span>').attr({'class':'flabel'}).text(procedure_name);
								//var fcount = $('<span></span>').attr({'class':'fcount'}).text(count);
			        			
								if ( typeof procedureChkboxLblCnt[pipeName][procedure_name] == 'undefined' ){									
									procedureChkboxLblCnt[pipeName][procedure_name] = {};
									procedureChkboxLblCnt[pipeName][procedure_name].chkbox = chkbox;
									procedureChkboxLblCnt[pipeName][procedure_name].flabel = flabel;
									procedureChkboxLblCnt[pipeName][procedure_name].fcount = 0;
									procedureChkboxLblCnt[pipeName][procedure_name].pipeClass = pipeClass;
								}
								procedureChkboxLblCnt[pipeName][procedure_name].fcount += count;								
		        			}	
		        		}	        		   	
		        		
		        		for ( var pipeline in procedureChkboxLblCnt ){
		        			for ( var procedure_name in procedureChkboxLblCnt[pipeline] ){
		        				var pipeClass = procedureChkboxLblCnt[pipeline][procedure_name].pipeClass;
		        				
		        				var liContainer = $("<li></li>").attr({'class':'fcat ' + pipeClass});
		        				var chkbox = procedureChkboxLblCnt[pipeline][procedure_name].chkbox;
		        				var flabel = procedureChkboxLblCnt[pipeline][procedure_name].flabel;
		        				var fcount = $('<span></span>').attr({'class':'fcount'}).text(
		        					procedureChkboxLblCnt[pipeline][procedure_name].fcount);
		        				
		        				if ( pipeline != 'IMPC Pipeline' ){		        					
			        				liContainer.append(chkbox, flabel, fcount);			        				
			        			}
		        				else {
		        					impc = thisFacetSect.append(thisUlContainer.append(liContainer.append(chkbox, flabel, fcount)));
		        				}
		        				thisUlContainer.append(liContainer);
		        			}		        			
		        		}		        		
		        		
			    		thisFacetSect.append(thisUlContainer);
			    		$('div.flist li#pipeline > ul').append(thisFacetSect);		        		
	        		}
	        		
	        		if ( impc ){
	        			$('div.flist li#pipeline > ul').prepend(impc);
		    		}	        		     		

	    			// IMPC pipeline is open and rest of pipeline subfacets are collapsed by default    			
	    			$('div.flist li#pipeline > ul li:nth-child(1)').addClass('open');	    		
	    				    			
	    			$.fn.initFacetToggles('pipeline');	    			
	    			
		    		$('li#pipeline li.fcat input').click(function(){	    			
		    			// // highlight the item in facet	    			
		    			$(this).siblings('span.flabel').addClass('highlight');
						$.fn.composeSummaryFilters($(this), self.options.data.hashParams.q);
					});	  
		    		
		    		/*--------------------------------------------------------------------------------------------------------------------------*/
			    	/* ------ when search page loads, the URL params are parsed to load dataTable and reconstruct filters, if applicable ------ */
			    	/*--------------------------------------------------------------------------------------------------------------------------*/	
		    		
		    		var oConf = self.options.data.hashParams;
			    	oConf.core = self.options.data.core;
			    	
			    	$.fn.parseUrl_constructFilters_loadDataTable(oConf);
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
	



