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
 * tools.js: various tools used across the web application.
 * Use closure to safely write jQuery as $
 * as the closure creates a function with $ as parameter and is run immediately with the value
 * jQuery which gets mapped to $
 * 
 * Author: Chao-Kung Chen
 */
(function($){		
	
	$.fn.parseUrl_constructFilters_loadDataTable = function(oConf){
		
		// if url contains solr fq (non-default ones) filters, parse them and tick checkbox of checkbox filters to create summary facet filters
		if ( oConf.fq.match(/(.*)/) && !oConf.noFq ){
			
			// only deals with facet=xxx, not core=xxx
			if ( typeof oConf.core == 'undefined' ){
				var aFqs  = oConf.fq.split(' AND ');
				var q     = oConf.q;
				
				for ( var i=0; i<aFqs.length; i++ ){
					var kv = aFqs[i].replace(/\(|\)|"/g,'');
					var aVals = kv.split(':');
					
					var qVal = aVals[1];
					var qField = aVals[0];
					
					if ( typeof MPI2.searchAndFacetConfig.qfield2facet[qField] ){
						//var kv = aFqs[i].replace(':','|').replace(/\(|\)|"/g,'');
						
						
						if ( qField == 'procedure_stable_id' ){
							kv = qVal;
						}
						else if (qField == 'latest_phenotype_status'){
							kv = MPI2.searchAndFacetConfig.phenotypingVal2Field[qVal];
						}
						
						var oInput = $('div.flist li.fcat').find('input[rel*="'+ kv +'"]');
						//if (oInput.length != 0 && !oInput.is(':checked') ){	
						if (oInput.length != 0 ){
							oInput.click(); // tick checkbox               
			    		}	
			    		else {
			    			var facet = MPI2.searchAndFacetConfig.qfield2facet[qField];
			    			var relStr = facet + '|' + qField + '|' + qVal;
			    			//console.log('hidden: '+ relStr);
			    			oInput = $('<input></input>').attr({'type':'checkbox','rel':relStr}).prop('checked', true);
			    			
			    			$.fn.composeSummaryFilters(oInput, q);
			    		}
					}
				}
			}
		}
		
		$.fn.loadDataTable(oConf);
	}
	
	$.fn.initFacetToggles = function(facet){			
		
		// toggle Main Categories
		/*$('div.flist li#' + facet + ' > .flabel').click(function() {			
			if ($(this).parent('.fmcat').hasClass('open')) {
				$(this).parent('.fmcat').removeClass('open');
			} 
			else {
				$('.fmcat').removeClass('open');
				$(this).parent('.fmcat').addClass('open');
			}
		});*/
		
		$('div.flist li#' + facet).click(function() {			
			if ($(this).hasClass('open')) {
				$(this).removeClass('open');
			} 
			else {
				$(this).removeClass('open');
				$(this).addClass('open');
			}
		});
		
		// kick start itself (when initialized as above) if not yet
		if ( ! $('div.flist li#' + facet).hasClass('open') ){
			$('div.flist li#' + facet + ' > .flabel').click();
		}		
		
		// toggle Categorie Sections
		/*$('div.flist li#' + facet).find('li.fcatsection:not(.inactive) .flabel').click(function() {			
			//$(this).parent('.fcatsection').toggleClass('open'); 
			alert('fcatsection');
		});*/
		
		
		
		$('div.flist li#' + facet).find('li.fcatsection:not(.inactive)').click(function(e) { 
		//$('div.flist li#' + facet).find('li.fcatsection').click(function(e) { 	
			// when subfacet opens, tick checkbox facet filter if there is matching summary facet filter (created from url on page load)
			if ($('ul#facetFilter li.'+ facet + ' li.ftag').size() != 0  ){
				$('ul#facetFilter li.ftag a').each(function(){
		    		var aVals = $(this).attr('rel').split('|');
		    		var ffacet  = aVals[0];
		    		var kv = aVals[1] + '|' + aVals[2];
		    		
		    		// tick only filters in opening facet
		    		if ( ffacet == facet ){
						$('div.flist li.fcat').find('input[rel*="'+ kv +'"]').prop('checked', true).siblings('.flabel').addClass('highlight'); 
		    		}	
				});	
			}
			
			e.stopPropagation();			
			$(this).toggleClass('open'); 

		});
		
		// make categories clickable (not only the checkbox itself)
		$('div.flist li#' + facet).find('li.fcat .flabel').click(function() {				
			$(this).prev('input').trigger('click');
		});			
	};
	
	$.fn.widgetExpand = function(thisWidget){
		
		var facet = thisWidget.element.attr('id');		
		var caller = thisWidget.element;    		
		delete MPI2.searchAndFacetConfig.commonSolrParams.rows;
	
		caller.click(function(){
			
			if ( caller.find('span.fcount').text() != 0 ){
				//console.log(facet + ' widget expanded : '+ MPI2.searchAndFacetConfig.widgetOpen);
				
				// close all other non-selected facets
				$('div.flist > ul li.fmcat').each(function(){
					if ( $(this).attr('id') != facet ){
						$(this).removeClass('open');
					}
				});	
				
				MPI2.searchAndFacetConfig.widgetOpen = true;
				
				var oHashParams = $.fn.parseHashString(window.location.hash.substring(1));
							
				if ( /search\/?$/.exec(location.href) ){
					// no search params					
					if ( typeof MPI2.searchAndFacetConfig.facetParams[facet+'Facet'].filterParams != 'undefined' ){
						oHashParams.fq = MPI2.searchAndFacetConfig.facetParams[facet+'Facet'].filterParams.fq;
					} 				
				}
				else if ( window.location.search != '' ){
					// deals with user query		
					oHashParams.q = decodeURI(window.location.search.replace('?q=', ''));
					
					// check if there is any filter checked, if not, we need to use default fq for the facet selected
					if ( $('ul#facetFilter li.ftag').size() == 0 ){
						oHashParams.fq = MPI2.searchAndFacetConfig.facetParams[facet+'Facet'].filterParams.fq;						
					}		
					
					oHashParams.fq = typeof oHashParams.fq == 'undefined' ? MPI2.searchAndFacetConfig.facetParams[facet+'Facet'].filterParams.fq : oHashParams.fq;					
					
				}
				
				// tick checkbox facet filter if there is matching summary facet filter (created from url on page load)
				if ($('ul#facetFilter li.'+ facet + ' li.ftag').size() != 0  ){
					$('ul#facetFilter li.ftag a').each(function(){
			    		var aVals = $(this).attr('rel').split('|');
			    		var ffacet  = aVals[0];
			    		var kv = aVals[1] + '|' + aVals[2];
			    		
			    		// tick only filters in opening facet
			    		if ( ffacet == facet ){
							$('div.flist li.fcat').find('input[rel*="'+ kv +'"]').prop('checked', true).siblings('.flabel').addClass('highlight'); 
			    		}	
					});	
				}
				
				var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facet + 'Facet'].solrCoreName;	
				
				// when we have filters to deal with, we there is &facet=xxx in the url
				var mode = typeof oHashParams.facetName != 'undefined' ? '&facet=' : '&core=';					
							
				//alert(mode);
				
				if ( typeof oHashParams.q == 'undefined' ){
					// no search kw
					if ( $('li.ftag').size() == 0 ){
						var oHashParams = thisWidget.options.data.hashParams;						
						window.location.hash = 'fq=' + oHashParams.fq + mode +  solrCoreName;											
					}
					else {					
						window.location.hash = 'fq=' + oHashParams.fq + mode +  solrCoreName;					
					}
				}
				else {						
					if ( ! window.location.search.match(/q=/) ){	
						window.location.hash = 'q=' + oHashParams.q + '&fq=' + oHashParams.fq + mode +  solrCoreName;
					}
					else {		
						window.location.hash = 'fq=' + oHashParams.fq + mode +  solrCoreName;
					}
				}	
			}
		});		
	};
	
	function _facetRefresh(json, selectorBase){			  			
							    			
		// refresh mp facet sum count				
		var fcount = json.response.numFound;
		$(selectorBase + ' > span.fcount').text(fcount);    			
		
		// set all subfacet counts to zero first and then update only those matching facets
		$(selectorBase).find('li.fcat span.fcount').each(function(){
			$(this).text('0');
		});					
	};
		
	$.fn.addFacetOpenCollapseLogic = function(foundMatch, selectorBase) {
		var firstMatch = 0;	
		
		for ( var sub in foundMatch ){			
			if ( foundMatch[sub] != 0 ) {		
				firstMatch++;
				if ( firstMatch == 1 ){					
					// open first subfacet w/ match					
					$(selectorBase + ' li.fcatsection.' + sub).addClass('open');
				}
				
				// remove grayout for other subfacet(s) with match
				$(selectorBase + ' li.fcatsection.' + sub).removeClass('grayout');
			}
		}		
	}
	
	
	$.fn.fetchFecetFieldsStr = function(aFacetFields){
		var facetFieldsStr = '';
		for ( var i=0; i<aFacetFields.length; i++){
			facetFieldsStr += '&facet.field=' + aFacetFields[i];
		}
		return facetFieldsStr + "&facet=on&facet.limit=-1&facet.mincount=1&rows=0";
	}
	$.fn.fetchFecetFieldsObj = function(aFacetFields, oParams){		
		var facetFields = [];
		for ( var i=0; i<aFacetFields.length; i++){
			facetFields.push(aFacetFields[i]);
		}
		oParams.facet='on';
		oParams['facet.limit']=-1;
		oParams['facet.mincount']=1;
		oParams['facet.field'] = facetFields.join(',');
		return oParams;
	}
	
	function FacetCountsUpdater(oConf){	
		
		
		var facet       = oConf.facet;
		var fqStr       = oConf.fqStr;
		var q           = oConf.q;
		var thisSolrUrl = solrUrl + '/' + facet + '/select'; 
		MPI2.searchAndFacetConfig.currentFq = fqStr;
		
		this.updateFacetCounts = function(){
			switch(facet) {
			    case 'gene':
			    { 	
		    		var oFields = {
		    					/*'imits_phenotype_complete':{'class': 'phenotyping', 'label':'Complete'}, 
		    					   'imits_phenotype_started':{'class': 'phenotyping', 'label':'Started'},  
		    					   'imits_phenotype_status':{'class': 'phenotyping', 'label':'Attempt Registered'},  */
		    					   'latest_phenotype_status':{'class': 'phenotyping', 'label':''},
		    		               'status':{'class':'production', 'label':''},		             
		    		               'latest_production_centre':{'class':'latest_production_centre','label':''},
		    		               'latest_phenotyping_centre':{'class':'latest_phenotyping_centre','label':''},
		    		               'marker_type':{'class':'marker_type', 'label':''}
		    		               };
		    		
		    		var aFields = [];
		    		for (var f in oFields ){
		    			aFields.push(f);
		    		}		
		    		var fecetFieldsStr = $.fn.fetchFecetFieldsStr(aFields);
		    		
		            var paramStr = 'q=' + q + '&wt=json&defType=edismax&qf=auto_suggest';        
		            paramStr += '&fq=' + fqStr + ' AND ' + MPI2.searchAndFacetConfig.facetParams.geneFacet.fq + fecetFieldsStr;
		                    
		            //console.log('GENE: '+ paramStr);
		            
		            $.ajax({ 	
		    			'url': thisSolrUrl,		
		        		'data': paramStr,
		        		'dataType': 'jsonp',
		        		'jsonp': 'json.wrf',
		        		'success': function(json) {
		        			//console.log(json);
		        			
		    				var oFacets = json.facet_counts.facet_fields;
		    			
		    				var selectorBase = "div.flist li#gene";
		    				_facetRefresh(json, selectorBase);				
		    				
		    				// collapse all subfacet first, then open the first one that has matches 
		    				$(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout');
		    								
		    				var foundMatch = {'phenotyping':0, 'production':0, 'latest_production_centre':0, 'latest_phenotyping_centre':0, 'marker_type':0};
		    				
		    				for (var n=0; n<aFields.length; n++){					
		    					if ( oFacets[aFields[n]].length != 0 ){						
		    						foundMatch[oFields[aFields[n]]['class']]++;
		    					}					
		    				}
		    			
		    				for ( var fld in oFacets ){
		    					for (var i=0; i<oFacets[fld].length; i=i+2){
		    						
		    						var subFacetName = oFacets[fld][i];
		    						
		    						if ( subFacetName != ''){ // skip solr field which value is an empty string
		    							var className = oFields[fld]['class'];
		    							
		    							if ( className != 'phenotyping' ){
		    								$(selectorBase + ' li.' + className + ' span.flabel').each(function(){							
		    									if ( $(this).text() == subFacetName ){
		    										$(this).siblings('span.fcount').text(oFacets[fld][i+1]);
		    									}
		    								});
		    							}	
		    							else {
		    								/*
		    								if (subFacetName == '1'){						
		    									$(selectorBase + ' li.' + className + ' span.flabel').each(function(){
		    										if ( $(this).text() == 'Complete' && fld == 'imits_phenotype_complete' ){
		    											$(this).siblings('span.fcount').text(oFacets[fld][i+1]);
		    										}
		    										else if ( $(this).text() == 'Started' && fld == 'imits_phenotype_started' ){
		    											$(this).siblings('span.fcount').text(oFacets[fld][i+1]);
		    										}
		    									});
		    								}
		    								else if (subFacetName == 'Phenotype Attempt Registered'){
		    									$(selectorBase + ' li.' + className + ' span.flabel').each(function(){
		    										if ( $(this).text() == 'Attempt Registered' ){
		    											$(this).siblings('span.fcount').text(oFacets[fld][i+1]);
		    										}
		    									});
		    								}*/
		    								
	    			    	    			if (subFacetName == 'Phenotype Attempt Registered' ||
	    			    	    				subFacetName == 'Phenotyping Started' ||
	    			    	    				subFacetName == 'Phenotyping Complete' ){
	    			    	    				
	    			    	    				$(selectorBase + ' li.fcat.' + className + ' span.flabel').each(function(){
	    			    	    					if (subFacetName == MPI2.searchAndFacetConfig.phenotypingStatuses[$(this).text()].val){
	    			    	    						$(this).siblings('span.fcount').text(oFacets[fld][i+1]);
	    			    	    					}
		    									});
	    			    	    			}
		    							}
		    						}
		    					}
		    				}
		    				
	
		    				$.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);				
		        		}
		            });
		    		
			    }	    	
			    break;
			    
			    case 'mp':
			    {
			    	var facetField = 'top_level_mp_term';
					var oParams = {};
					oParams.fq = fqStr;	
					oParams = $.fn.fetchFecetFieldsObj([facetField], oParams);
					oParams = $.extend({}, MPI2.searchAndFacetConfig.facetParams.mpFacet.srchParams,oParams);		
					oParams = $.fn.getSolrRelevanceParams('mp', q, oParams);
							        
					//console.log('MP: '+ $.fn.stringifyJsonAsUrlParams(oParams));
					$.ajax({ 	
						'url': thisSolrUrl,    		
			    		//'data': paramStr,
						'data': oParams,
			    		'dataType': 'jsonp',
			    		'jsonp': 'json.wrf',
			    		'success': function(json) {
			    			//console.log('mp: ');	
			    			//console.log(json);
			    			
			    			// refresh phenotype facet
			    			var oFacets = json.facet_counts.facet_fields;   				
			    						
			    			var selectorBase = "div.flist li#mp";
							_facetRefresh(json, selectorBase); 
							
			    			for (var i=0; i<oFacets[facetField].length; i=i+2){    			
			        				var facetName = oFacets[facetField][i];    				   				   				
			        				var facetCount = oFacets[facetField][i+1];
			        			
			    				$(selectorBase + ' li.fcat input').each(function(){
			    					var aTxt = $(this).attr('rel').split('|');    					
			    					if ( aTxt[2] == facetName ){    					
			    						$(this).siblings('span.fcount').text(facetCount);
			    					}
			    				});    						
			    			}   			
			    					
			    		}
					});		
			    }
			    break;
			    
			    case 'disease':
			    {
			    		
					var fecetFieldsStr = $.fn.fetchFecetFieldsStr(['disease_classes','disease_source','human_curated','mouse_curated','impc_predicted','impc_predicted_in_locus','mgi_predicted','mgi_predicted_in_locus']);
							
					var paramStr = 'q=' + q + '&wt=json&defType=edismax&qf=auto_suggest';
			        paramStr += '&fq=' + fqStr + fecetFieldsStr;
			        
					//console.log('DISEASE: '+ paramStr + fecetFieldsStr);
							
					$.ajax({ 	
						'url': thisSolrUrl,
			    		'data': paramStr + fecetFieldsStr,
			    		'dataType': 'jsonp',
			    		'jsonp': 'json.wrf',
			    		'success': function(json) {
			    			//console.log('disease: ');
			    			//console.log(json);
			    			    			
			    			// refresh disease facet
			    			var oFacets = json.facet_counts.facet_fields;
			    			var selectorBase = "div.flist li#disease";
							_facetRefresh(json, selectorBase); 
							
			    			// collapse all subfacet first, then open the first one that has matches 
							$(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout');
			    			    			
			    			// subfacets: source/classification/curated/predicted
							var foundMatch = {'disease_source':0, 'disease_classes':0, 'curated':0, 'predicted':0};
			    			
			    			var aSubFacets = ['disease_source','disease_classes','mouse_curated','human_curated','mgi_predicted','mgi_predicted_in_locus', 'impc_predicted','impc_predicted_in_locus'];
			    			for (var i=0; i<aSubFacets.length; i++){    				
			    				var subFacetName = aSubFacets[i];
			    				
			    				// do some accounting for matching subfacets
			    				if ( subFacetName.indexOf('curated') != -1 ) {
			    					for ( var cr=0; cr<oFacets[subFacetName].length; cr=cr+2){    						
			    						if ( oFacets[subFacetName][cr] == 'true' ){ 
			    							foundMatch.curated++;    				    							
			    						}
			    					}    					
			    				}
			    				else if ( subFacetName.indexOf('predicted') != -1 ){    					
			    					for ( var pr=0; pr<oFacets[subFacetName].length; pr=pr+2){
			    						if ( oFacets[subFacetName][pr] == 'true' ){ 					
			    							foundMatch.predicted++;    							 							
			    						}
			    					} 				
								}
			    				else if ( oFacets[subFacetName].length > 0 ) {      					
			    					foundMatch[subFacetName]++;
			    				}  				  				
			    				
			    				// update facet count
			    				for (var j=0; j<oFacets[subFacetName].length; j=j+2){
			    				
				    				var label = oFacets[subFacetName][j];    				 				   				
				    				var facetCount = oFacets[subFacetName][j+1];
				    				//console.log(label + ' ---:'+facetCount + ' >> ' + subFacetName);    				
				    				
				    				$(selectorBase + ' li.' + subFacetName).each(function(){	
				    					if (subFacetName.match(/_curated|_predicted/) && label =='true' ){
				    						$(this).find('span.fcount').text(facetCount);
				    					}
				    					else {
				    						if ( $(this).find('span.flabel').text() == label ){    					
				    							$(this).find('span.fcount').text(facetCount);
				    						}
				    					}
				    				});   	    				
			    				}   			
			    			}
			    			    
			    			$.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);    			
			    		}
					});		
			    }
		        break;
		        
			    case 'ma':
			    {
					var facetField = 'selected_top_level_ma_term';
					var fecetFieldsStr = $.fn.fetchFecetFieldsStr([facetField])
					var paramStr = 'q=' + q + '&wt=json&defType=edismax&qf=auto_suggest';
			        paramStr += '&fq=' + fqStr + fecetFieldsStr;		
							
					//console.log('MA: '+ paramStr);
					$.ajax({ 	
						'url': thisSolrUrl,
			    		'data': paramStr,
			    		'dataType': 'jsonp',
			    		'jsonp': 'json.wrf',
			    		'success': function(json) {
			    			//console.log('ma: ');	
			    			//console.log(json);			
			    			    			
			    			// refresh phenotype facet
			    			var oFacets = json.facet_counts.facet_fields;    			
			    			var selectorBase = "div.flist li#ma";
							_facetRefresh(json, selectorBase); 
							
			    			for (var i=0; i<oFacets[facetField].length; i=i+2){    			
			        				var facetName = oFacets[facetField][i];    				   				   				
			        				var facetCount = oFacets[facetField][i+1];
			    				$(selectorBase + ' li.fcat input').each(function(){
			    					var aTxt = $(this).attr('rel').split('|');    					
			    					if ( aTxt[2] == facetName ){    					
			    						$(this).siblings('span.fcount').text(facetCount);
			    					}
			    				});    						
			    			}    	   			
			    		}
					});		
			    }
			    break;
			    
			    case 'pipeline':
			    {
					
					var fecetFieldsStr = $.fn.fetchFecetFieldsStr(['pipeline_name', 'pipe_proc_sid']);
					var paramStr = 'q=' + q + '&wt=json&defType=edismax&qf=auto_suggest';
			        paramStr += '&fq=' + fqStr + fecetFieldsStr;		
					
					//console.log('PIPELINE: '+ paramStr);
					$.ajax({ 	
						'url': thisSolrUrl,
						'data': paramStr,
						'dataType': 'jsonp',
						'jsonp': 'json.wrf',
						'success': function(json) {
							//console.log('pipeline: ');	
							//console.log(json);			
							
							// refresh phenotype facet
							var oFacets = json.facet_counts.facet_fields;				
							var selectorBase = "div.flist li#pipeline";
							_facetRefresh(json, selectorBase); 
															
							// close/grayout all subfacets by default
			    			$(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout')
			    			
							var plFacets = json.facet_counts['facet_fields']['pipeline_name'];	    			
			    			var prFacets = json.facet_counts['facet_fields']['pipe_proc_sid'];
			    			
			    			// update pipeline parameter counts for rocedures
			    			for ( var p=0; p<plFacets.length; p+=2){
			        			var currPipe = plFacets[p];	
			        			var pipeClass = currPipe.replace(/ /g, '_');        			
			        			
				        		for ( var f=0; f<prFacets.length; f+=2 ){ 		        			        			
				        			var aVals = prFacets[f].split('___');
				        			var pipeName = aVals[0];
				        			var procedure_name = aVals[1];
				        			var proSid = aVals[2];
				        			var paramCount = prFacets[f+1];
				        					        			
				        			if (pipeName == currPipe ){	
				    					$(selectorBase + ' li.' + pipeClass).each(function(){	    					    					
					    					if ( $(this).find('span.flabel').text() == procedure_name ){    					
					    						$(this).find('span.fcount').text(paramCount);
					    					}
					    				}); 
				        			}
				        		}
			    			}			
									
			    			// open first subfacet with match and keep other subfacets with matches closed but remove grayout
			    			// subfacets w/o matches remain grayout
							for ( var j=0; j<plFacets.length; j=j+2){				
								var pipelineName = plFacets[j];					
								$(selectorBase + ' li.fcatsection > span.flabel').each(function(){						
									if ( $(this).text() == pipelineName ){
										if ( j == 0 ){
											$(this).parent().addClass('open');
										}
										$(this).parent().removeClass('grayout');
									}						
								});	
							}
						}
					});		
			    }
			    break;
			    
			    case 'images':
			    {
					//var fecetFieldsStr = $.fn.fetchFecetFieldsStr(['annotatedHigherLevelMpTermName', 'annotated_or_inferred_higherLevelMaTermName', 'expName', 'subtype']);		
					var fecetFieldsStr = $.fn.fetchFecetFieldsStr(['procedure_name','top_level_mp_term','selected_top_level_ma_term','marker_type']);
					var paramStr = 'q=' + q + '&wt=json&defType=edismax&qf=auto_suggest';
			        paramStr += '&fq=' + fqStr + fecetFieldsStr;
			       
					//console.log('IMAGES: '+ paramStr);
					$.ajax({ 	
						'url': thisSolrUrl,
			    		'data': paramStr,
			    		'dataType': 'jsonp',
			    		'jsonp': 'json.wrf',
			    		'success': function(json) {
			    			//console.log('images: ');	
			    			//console.log(json);			
			    			
			    			// refresh images facet
							var oFacets = json.facet_counts.facet_fields;				
							var selectorBase = "div.flist li#images";
							_facetRefresh(json, selectorBase); 
							
							// close/grayout all subfacets by default
			    			$(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout')    			  			
							var foundMatch = {'Phenotype':0, 'Anatomy':0, 'Procedure':0, 'Gene':0};
			    			
			    			var oSubFacets = {
			    							/*'annotatedHigherLevelMpTermName':'Phenotype',
			    							  'annotated_or_inferred_higherLevelMaTermName':'Anatomy',
			    							  'expName':'Procedure',
			    							  'subtype':'Gene'*/
			    							'top_level_mp_term': 'Phenotype',
			  	    						'procedure_name' : 'Procedure',	    					            
			  	    						'selected_top_level_ma_term': 'Anatomy',
			  	    						'marker_type': 'Gene'}; 
			    			
			    			
			    			for ( var facetStr in oSubFacets ){    				
				    			for (var j=0; j<oFacets[facetStr].length; j=j+2){	    				
				    				
				    				var facetName = oFacets[facetStr][j];	    								   				
				    				var facetCount = oFacets[facetStr][j+1];	    				 				
				    				foundMatch[oSubFacets[facetStr]]++;
				    				
				    				$(selectorBase + ' li.'+ facetStr).each(function(){
				    					var aData = $(this).find('input').attr('rel').split('|');	    				
				    					if ( aData[2] == facetName ){
				    						$(this).find('span.fcount').text(facetCount);
				    					}
				    				});	    				
				    			}	    			
			    			}
			    			$.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);
			    		}
					});		
			    }
			    break;
			    
			    default:
			          {}
			} 			
		};
	}
	
	$.fn.composeSummaryFilters = function(oChkbox, q){	
		
		var smfilter = new SummaryFilter(oChkbox, q);
		if ( oChkbox.is(':checked') ){	
			// when a new filter is added: facet counts and url will be updated
			smfilter.add(); 
		}
		else {
			// when an existing filter is removed: facet counts and url will be updated
			smfilter.remove();
		}
	};
	
	function SummaryFilter(oChkbox, q){
				
		var aVals = oChkbox.attr('rel').split("|");
		
		this.checkbox = oChkbox;
		this.q = q;
		this.facet  = aVals[0];
		this.qField = aVals[1];
		this.qValue = aVals[2];
		
		parseSummeryFacetFiltersForSolr_fq = function(){
			var aFilters = [];
			$('ul#facetFilter li.ftag a').each(function(){
	    		var aVals = $(this).attr('rel').split('|');
	    		var facet  = aVals[0];
	    		var qField = aVals[1];
	    		var qVal   = aVals[2];
	    		
	    		if (facet == 'gene' && qField.match(/^imits_/)){
	    			
	    			aFilters.push('(latest_phenotype_status:"' + qVal + '")');
	    		}
	    		else if (facet == 'pipeline' ){
	    			console.log( qField + ':"' + qVal )
	    			var aParts = qVal.split('___');
	    			qVal = aParts[1].replace(/"/g, '');
	    			aFilters.push('(' + qField + ':' + qVal + ')');
	    		}
	    		else {
	    			aFilters.push('(' + qField + ':"' + qVal + '")');
	    		}
	    	});
			return $.fn.getUnique(aFilters);
		};
		
		this.updateFacetCounts = function(solrFqStr){
			// for all mega cores
			
			var cores = MPI2.searchAndFacetConfig.megaCores;
		
			for ( var i=0; i<cores.length; i++ ){
				var oConf = {'facet':cores[i], 'fqStr':solrFqStr, 'q':q};
				var facetCountsUpdater = new FacetCountsUpdater(oConf);
				facetCountsUpdater.updateFacetCounts();
			}		
		};
		
		this.updateUrl = function(){
			
			// use AND as default operator for multiple filters
	    	var sSolrFilter = parseSummeryFacetFiltersForSolr_fq().join(' AND ');
	    	
	    	MPI2.searchAndFacetConfig.filterChange = true;
	    	window.location.hash = '#fq=' + sSolrFilter + '&facet=' + this.facet;

	    	return sSolrFilter;
		};
		
		this.remove = function(){
			var facet = this.facet;
			
			console.log('uncheck checkFilter of ' + facet + ' facet');
			MPI2.searchAndFacetConfig.filterChange = true;
			
			// uncheck checkbox with matching value		
			$('ul#facetFilter li.' + facet + ' li.ftag').each(function(){	
				
				if ( $(this).find('a').attr('rel') == oChkbox.attr('rel') ){
					// remove checkbox filter highlight
					oChkbox.siblings('span.flabel').removeClass('highlight');	
					
					// also remove its summary facet filter
					// just click itself, as each summary facet filter has a remove callback
					$(this).click(); 
				}
			});						
		};
		
		this.add = function(){
			//console.log('added Filter of ' + this.facet + ' facet');
			MPI2.searchAndFacetConfig.filterChange = true;
			
			var aVals = oChkbox.attr('rel').split("|");
			
			var facet  = this.facet;
			var qField = this.qField;
			var qValue = this.qValue;
			
			var thisLi = $('ul#facetFilter li.' + facet);
										
			if ( !$('div.ffilter').is(':visible') ){
				$('div.ffilter').show();			
			}
			
			// show filter facet caption
			thisLi.find('.fcap').show();
			
			//var display = MPI2.searchAndFacetConfig.facetFilterLabel[qField];
			
			if ( qValue == 1 ){
				/*if (qField == 'imits_phenotype_started'){
					qValue = 'Started'; 
				}
				else  if (qField == 'imits_phenotype_complete'){
					qValue = 'Complete'; 
				}*/
				
				qValue = 'Yes';	// some disease fields
				
			}	
			
			var filterTxt = qValue;
			if ( facet == 'gene' ){
				if ( qValue == 'Started'  ){
					filterTxt = 'phenotyping started'; 
				}
				if ( qValue == 'Complete'  ){
					filterTxt = 'phenotyping complete'; 
				}
				else if ( qValue == 'Phenotype Attempt Registered' || qField == 'status' || qField == 'marker_type' ){
					filterTxt = qValue.toLowerCase();
				}
				
				if ( qField == 'latest_production_centre' ){
					filterTxt = 'mice produced at ' + qValue;
				}
				else if ( qField == 'latest_phenotyping_centre' ){
					filterTxt = 'mice phenotyped at ' + qValue;
				}			
			}
			
			var pipelineName, a;
			
			if (facet == 'pipeline'){
				var names = filterTxt.split('___');					
				filterTxt = oChkbox.attr('class').replace(/_/g, ' ') + ' : ' + '"' + names[0] + '"';
			}
			if (facet == 'disease' && qField.match(/_curated|_predicted/) ){
				filterTxt = MPI2.searchAndFacetConfig.facetFilterLabel[qField]; 		
			}
			
			var a = $('<a></a>').attr({'rel':oChkbox.attr('rel')}).text(filterTxt.replace(/ phenotype$/, ''));		
			//var del = $('<img>').attr('src', baseUrl + '/img/scissors-15x15.png');
			
			var hiddenLabel = $("<span class='hidden'></span>").text(_composeFilterStr(facet, qField, qValue));
			this.filter = $('<li class="ftag"></li>').append(a, hiddenLabel);			
			
			var ul = $('<ul></ul>').html(this.filter);
			
			// add to summary list
			thisLi.append(ul);	
			thisLi.show();
			
			// update url when new filter is added
			var fqStr = this.updateUrl();
			this.updateFacetCounts(fqStr);
			
			// callback for uncheck sumary filter
			uncheck_summary_facet_filter(this);
		};	
	}

	function uncheck_summary_facet_filter(oFilter) {
		
		var oChkbox = oFilter.checkbox;
		var facet     = oFilter.facet;
		var q         = oFilter.q;
		var filter    = oFilter.filter
		
		filter.click(function(){
			
			// remove checkbox filter highlight			
			oChkbox.prop('checked', false).siblings('span.flabel').removeClass('highlight');			
			filter.remove();
			
			if ($('ul#facetFilter li.'+ facet + ' li.ftag').size() == 0  ){
				// remove caption
				$('ul#facetFilter li.'+ facet + ' span.fcap').hide();
			}
			
			// any other summary filters left?
			if ( $('ul#facetFilter li.ftag').size() == 0 ){
				
				// if there is no summary filter at all, refresh url	
				
				var url;
				var defaultFqStr = MPI2.searchAndFacetConfig.facetParams[facet+'Facet'].fq;
				
				if ( window.location.search != '' ){
					
					// has search keyword
					//url = baseUrl + '/search?q=' + q + '#fq='+ defaultFqStr + '&core='+facet;
					url = 'fq='+ defaultFqStr + '&core='+facet;
					//window.history.pushState({},"", url);// change browser url; not working with IE	
					//console.log('test: '+ url);
					window.location.hash = url; // also works with IE										
				}
				else {					
					// no search keyword
					
					// this is ok, but not working with IE
					//window.history.pushState({},"", baseUrl + '/search#fq='+defaultFqStr+'&core='+facet);
					
					// this also works with IE					
					window.location.hash = 'fq='+defaultFqStr+'&core='+facet;					
				}
				
				//window.history.pushState({},"", url);// change browser url
				location.reload();								
			}
			else {
				// if there is still summary filter: update url and facet counts
				var solrFqStr = oFilter.updateUrl();	
				oFilter.updateFacetCounts(solrFqStr);
			}
		});
	}		
	
	function _composeFilterStr(facet, field, value){	
		
		if ( arguments.length == 1 ){	
			
			var aStr = [];
			$('ul#facetFilter li li a').each(function(){				
				var aVals = $(this).attr('rel').split("|");		
				var fqField = aVals[1];
				var value =  aVals[2];					
				//console.log(fqField + ' --- '+ value);
				if ( fqField == 'procedure_stable_id' ){
					var aV = value.split('___');
					value = aV[1]; // procedure stable id	
					aStr.push('(' + fqField + ':' + value + ')');
				}
				
				else {
					aStr.push('(' + fqField + ':"' + value + '")');				
				}
			});
			
			var fqStr = aStr.join(' AND ');			
			
			return fqStr;
		}
		else {		
			
			if ( facet == 'gene' ){
				value = value == '1' ? 'Started' : value;						
			}
			else if ( facet == 'mp' || facet == 'ma' ){
				field = facet;			
				value = value.replace(/ phenotype$/, '');
			}
			else if ( facet == 'disease' ){
				value = value == 'true' ? 'Yes' : value;		
			}
			else if ( facet == 'pipeline' ){
				var aVals = value.split('___');
				value = aVals[0];
			}	
			else if ( facet == 'images' ){						
				value = value.replace(/ phenotype$/, '');
			}
			
			return MPI2.searchAndFacetConfig.facetFilterLabel[field] + ' : "' + value + '"';
		}		
	}
		
	
	$.fn.qTip = function(oConf){
		// pageName: gene | mp | ma

		// .documentation is applied to h2 and p
		$('.documentation a').each(function(){	
			// now use id instead of class for better css logic
			var key = $(this).attr('id');
			
			$(this).attr('href', MDOC[oConf.pageName][key+'DocUrl']);
			$(this).qtip({				
			 	content: {
			 		text: MDOC[oConf.pageName][key]			 					 		
			    },		 	
			 	style: {
			 		classes: 'qtipimpc',			 		
			        tip: {			           
			        	corner: typeof oConf.tip != undefined ? oConf.tip : 'top right'
			        }
			    },
			    position: {
			        my: typeof oConf.corner != undefined ? oConf.corner : 'right top'
			    }			   
			});	
		});
	}
	
	$.fn.setHashUrl = function(q, core){		
		var hashParams = {};
		hashParams.q = q;
		hashParams.core = core;
		hashParams.fq = MPI2.searchAndFacetConfig.facetParams[core + 'Facet'].fq;
		window.location.hash = $.fn.stringifyJsonAsUrlParams(hashParams);		
	}
	
	$.fn.updateBreadCrumb = function(coreName){
		var hashParams = $.fn.parseHashString(window.location.hash.substring(1));
		
		var breadcrumbBox = $('p.ikmcbreadcrumb');		
		var baseLinks = "<a href=" + drupalBaseUrl + ">Home</a> &raquo; <a href=" + baseUrl + "/search>Search</a> &raquo; ";
		
		if ( coreName && ! hashParams.coreName ){			
			hashParams.coreName = coreName;
			hashParams.fq = 'undefined';
		}	
		else if ( !coreName && !hashParams.q){		
			hashParams.q = "*:*";
			hashParams.coreName = 'gene';
			hashParams.fq = 'undefined';
		}		
		baseLinks += fetchFacetLink(hashParams);			
		breadcrumbBox.html(baseLinks);
	}
		
	function fetchFacetLink(hashParams){	
		var coreName = hashParams.coreName;
		var fq = MPI2.searchAndFacetConfig.facetParams[coreName+'Facet'].fq; // default for whole dataset of a facet
		var breadCrumbLabel = MPI2.searchAndFacetConfig.facetParams[coreName+'Facet'].breadCrumbLabel;		
		var url = encodeURI(baseUrl + "/search#q=*:*" + "&core=" + hashParams.coreName + "&fq=" + fq);
		return "<a href=" + url + ">" + breadCrumbLabel + "</a>";		
	}
	
	$.fn.openFacet = function(core){	    	
	
    	$('div.facetCatList').hide();
    	$('div.facetCat').removeClass('facetCatUp');
    	
    	// priority order of facet to be opened based on search result
    	if (core == 'gene'){	    		
    		$('div#geneFacet div.facetCatList').show();
    		$('div#geneFacet div.facetCat').addClass('facetCatUp'); 	  		
    	}	
    	else if (core == 'mp'){
    		$('div#mpFacet div.facetCatList').show();		
    		$('div#mpFacet div.facetCat').addClass('facetCatUp'); 
    	}
    	else if (core == 'ma'){
    		$('div#maFacet div.facetCatList').show();		
    		$('div#maFacet div.facetCat').addClass('facetCatUp'); 
    	}
    	else if (core == 'pipeline'){
    		$('div#pipelineFacet div.facetCatList').show();	
    		$('div#pipelineFacet div.facetCat').addClass('facetCatUp'); 
    	}
    	else if (core == 'images'){
    		$('div#imagesFacet div.facetCatList').show();	
    		$('div#imagesFacet div.facetCat').addClass('facetCatUp'); 
    	}	
    	else if (core == 'disease'){
    		$('div#diseaseFacet div.facetCatList').show();	
    		$('div#diseaseFacet div.facetCat').addClass('facetCatUp'); 
    	}    	
	}
		
	$.fn.ieCheck = function(){
				
		/*if ( $.browser.msie && $.browser.version < 8.0 ){		
			var msg = "<div id='noSupport'>Dear user:<p><p>It appears that you are using Internet Explorer 7 or earlier version.<p>To ensure that IMPC is supporting the best browsing features, functionalities and experiences, " +
				  "and considering the security issues of older IEs, we decided not to support IE7 and earlier versions.<p>We are sorry if this has caused your inconvenience.<p>Here is a list of supported browsers: " +
				  "<a href='http://www.mozilla.org'>Firefox</a>, <a href='http://www.google.com/chrome'>Google chrome</a>, <a href='http://support.apple.com/downloads/#internet'>Apple safari</a>.<p>" +
				  "IMPC team.</div>";
			
			$('div.navbar').siblings('div.container').html(msg);
			return false;
		}*/
		
		
		var ver = getInternetExplorerVersion();
		
	    if ( ver < 8.0 ){	        
	    	var msg = "<div id='noSupport'>Dear user:<p><p>It appears that you are using Internet Explorer 7 or earlier version.<p>To ensure that IMPC is supporting the best browsing features, functionalities and experiences, " +
			 			"and considering the security issues of older IEs, we decided not to support IE7 and earlier versions.<p>We are sorry if this has caused your inconvenience.<p>Here is a list of supported browsers: " +
						"<a href='http://www.mozilla.org'>Firefox</a>, <a href='http://www.google.com/chrome'>Google chrome</a>, <a href='http://support.apple.com/downloads/#internet'>Apple safari</a>.<p>" +
						"IMPC team.</div>";
	         
	        $('div.navbar').siblings('div.container').html(msg);
	        return false;
	    }
	}
	function getInternetExplorerVersion() {
		
		// Returns the version of IE or -1	
	
	   var rv = -1; // default 
	   if (navigator.appName == 'Microsoft Internet Explorer') {
	      var ua = navigator.userAgent;
	      var re  = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
	      if (re.exec(ua) != null)
	         rv = parseFloat(RegExp.$1);
	   }
	   return rv;
	}	
	
	// inverse simple JSON: eg, {a: 'one', b: 'two}
	// cannot do complicated nested associated array
	$.fn.inverseSimpleJSON = function(json){
		var newJson = {};
		for ( var i in json ){
			newJson[json[i]] = i;
		}
		return newJson;
	}
	
	$.fn.endsWith = function(str, suffix){		
	    return str.indexOf(suffix, str.length - suffix.length) !== -1;
	}
	
	$.fn.composeSelectUI = function(aFormats, selName){
    	var oSelect = $('<select></select>').attr({'name': selName});    	
    	
    	for( var i=0; i<aFormats.length; i++){
    		oSelect.append("<option>" + aFormats[i]);
    	}	    	
    	return oSelect;
    }
    
    $.fn.loadFileExporterUI = function(conf){
    	var oFormatSelector = conf.formatSelector;
    	var label = conf.label;
    	var textPos = conf.textPos;
    	var iconDiv = $('<p></p>').attr({'class': textPos}).html(label + " &nbsp;");
    	var it = 0 ;
    	for ( var f in oFormatSelector ){
    		if (it++ > 0)
    			$(iconDiv).append("&nbsp;or&nbsp;");
    		//var btn = $('<a href="#"></a>').attr({'class': oFormatSelector[f] + ' ' + conf['class']}).html("<i class=\"fa fa-download\"></i> " + f);    		
    		// changed to use button instead of <a> as this will follow the link and the download won't work when clicked - have tried return false, 
    		// but due to a couple of ajax down the road, I could not get it to work.
    		// The button is styled as the new design
    		var btn = $('<button></button>').attr({'class': oFormatSelector[f] + ' fa fa-download gridDump ' + conf['class']}).html(f);
    		
    		$(iconDiv).append(btn);
    	}
    	return iconDiv;
    }
    
    $.fn.stringifyJsonAsUrlParams = function(json){
    	
    	var aStr = [];
    	for( var i in json ){
    		aStr.push(i + '=' + json[i]);
    	}
    	return aStr.join("&");
    }
        
    $.fn.parseUrlString = function(sUrl){
    	
    	var params = {};
    	//var parts = decodeURI(sUrl).replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
    	var parts = decodeURI(sUrl).replace(/[?|#&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {	
    	       params[key] = value; //.replace(/"/g,'');
    	});
    	return params;
    }
    
    $.fn.parseHashString = function(sHash){
    	
    	var hashParams = {};
    	var aKV = decodeURI(sHash).split("&");    	  
    	var m;
    	for( var i=0; i<aKV.length; i++){
    	
    		if ( aKV[i].indexOf('core=') == 0 ){    			
    			m = aKV[i].match(/core=(.+)/);
    			hashParams.coreName = m[1];
    		}
    		else if ( aKV[i].indexOf('facet=') == 0 ){    			
    			m = aKV[i].match(/facet=(.+)/);
    			hashParams.facetName = m[1];
    		}
    		else if ( aKV[i].indexOf('gridSubTitle=') == 0 ){    			
    			m = aKV[i].match(/gridSubTitle=(.+)/); 
    			hashParams.gridSubTitle = m[1];
    		}
    		else if ( aKV[i].indexOf('q=') == 0 ){    			
    			m = aKV[i].match(/q=(.+)/);    			
    			if ( m === null ){
    				m = [];
    				m[1] = '*';    		
    			}    			
    			hashParams.q = m[1];
    		}
    		else if ( aKV[i].indexOf('fq=') == 0 ){  
    			
    			/*if ( aKV[i] == 'fq=' + MPI2.searchAndFacetConfig.facetParams.imagesFacet.fq
    				|| aKV[i].match(/fq=\(?marker_type:* -marker_type:"heritable phenotypic marker"\)?/) 
    				|| aKV[i].match(/fq=\(?annotationTermId:M* OR expName:* OR symbol:*.+\)?/) 
    				|| aKV[i].match(/fq=\(?annotated_or_inferred.+\)?/) 
    				|| aKV[i].match(/fq=\(?expName.+\)?|fq=\(?higherLevel.+\)?|fq=\(?subtype.+\)?/) 
    				|| aKV[i].match(/fq=ontology_subset:\* AND \(?top_level_mp_term.+\)?/)
    				|| aKV[i].match(/fq=ontology_subset:IMPC_Terms AND \(?selected_top_level_ma_term.+\)?/)
    				|| aKV[i].match(/fq=\(?top_level_mp_term.+\)?/)
    				|| aKV[i].match(/fq=\(?selected_top_level_ma_term.+\)?/)
    				|| aKV[i].match(/fq=\(?inferred_top_level_mp_term.+\)?/)
    				|| aKV[i].match(/fq=\({0,}production_center:.+\)?/)
    				|| aKV[i].match(/fq=\({0,}phenotyping_center:.+\)?/)
    				|| aKV[i].match(/fq=\(?ontology_subset:.+/)
    				|| aKV[i].match(/fq=\(?type:disease+/)
    				|| aKV[i].match(/fq=\(?disease_\w*:.+/)
    				|| aKV[i].match(/fq=\(?.+_curated:.+/)
    				|| aKV[i].match(/fq=\(?.+_predicted(_in_locus)?:.+/)
    				|| aKV[i].match(/\(?imits_phenotype.+\)?/)
    				|| aKV[i].match(/\(?marker_type.+\)?/)
    				|| aKV[i].match(/\(?status.+\)?/)
    				|| aKV[i].match(/\(?pipeline_stable_id.+\)?/)
    				|| aKV[i].match(/\(?procedure_stable_id.+\)?/)
    				){*/
    				hashParams.fq = aKV[i].replace('fq=','');    				
    			//}    			
    		}
    		/*else if ( aKV[i].indexOf('ftOpen') == 0 ){  
    			hashParams.ftOpen = true;
    		}*/
    		
    	}
    	
    	return hashParams;
    }
    
    $.fn.fetchEmptyTable = function(theadStr, colNum, id, pageReload){
    	
    	var table = $('<table></table>').attr({'id':id});
    	var thead = theadStr;
    	var tds = '';
    	for (var i=0; i<colNum; i++){
    		tds += "<td></td>";
    	}
    	var tbody = $('<tbody><tr>' + tds + '</tr></tbody>');	    	    	
    	table.append(thead, tbody);
    	return table;
    }    
   
   
    function _fetchProcedureNameById(sid){
    	$.ajax({ 	
			'url': solrUrl + '/pipeline/select',
			'data': 'q=procedure_stable_id:"' + sid + '"&fl=procedure_name&rows=1',
			'dataType': 'jsonp',
			'async': false,
			'jsonp': 'json.wrf',
			'success': function(json) {
				$('span#hiddenBox').html(json);
				return procName = json.response.numFound; 
			}
		});  
    }  
    
    /*function _setFacetToOpen2(objList, oHashParams){
    	var facet = oHashParams.widgetName;
    	if ( (facet == 'imagesFacet' || facet == 'geneFacet' || facet == 'diseaseFacet' || facet == 'pipelineFacet') && objList.length != 0){
	    	// first change arrow image to collapse and make all gene/images/disease/pipeline subfacets hidden
			//$('table#' + facet + 'Tbl').find('tr.subFacet').addClass('trHidden');
			//$('table#' + facet + 'Tbl').find('tr.facetSubCat td').removeClass('unCollapse');		
    	}
    	
    	var fcatsection;
    	if ( objList.length == 0 ) {
    		if ( facet == 'geneFacet' ){
    			// open gene phenotyping status subfacet by default  
    			fcatsection = 'phenotyping';
    		}
    		else if (facet == 'diseaseFacet' ){
    			// open disease source subfacet by default  
    			fcatsection = 'disease_source';
    		} 
    		else if (facet == 'pipelineFacet' ){    		
    			// open pipeline IMPC subfacet by default  
    			fcatsection = 'IMPC_Pipeline';
    		} 
    		else if (facet == 'imagesFacet' ){    		
    			// open pipeline IMPC subfacet by default  
    			//fcatsection = 'annotatedHigherLevelMpTermName';
    			fcatsection = 'mp';
    		}
    		//_arrowSwitch(fcatsection); 
    		
    		
    	}    	
    	
    }  
    */
    $.fn.concatFilters = function(operator){
		var aFilters = [];
		$('ul#facetFilter span.hidden').each(function(){
    		aFilters.push('(' + $(this).text() + ')');
    	});
    	return aFilters.join(' ' + operator + ' ');		
	}
  
    
    function _prepare_resultMsg_and_dTableSkeleton(oHashParams){
    	
    	var q = oHashParams.q;
    	var facetDivId = oHashParams.widgetName;
    	
    	var filterStr = $.fn.concatFilters('AND');    	
    	    	
    	var oVal = MPI2.searchAndFacetConfig.facetParams[facetDivId];
    	var dTable = $.fn.fetchEmptyTable(oVal.tableHeader, 
                oVal.tableCols, oVal.gridName);
    	
    	var imgViewSwitcher = '';
    	if ( facetDivId == 'imagesFacet' ){
    		imgViewSwitcher = _load_imgViewSwitcher(dTable, oVal);    		
    		$("div#resultMsg").prepend(imgViewSwitcher);
    	}    
    	var searchKw = " AND search keyword: ";		
		searchKw += q == '*:*' ? '""' : '"' + q + '"';	
		
		var dataCount = "Found <span id='resultCount'><span id='annotCount'></span><a></a></span>";    	
    	//var resultMsg = $("<div id='resultMsg'></div>").append(imgViewSwitcher, dataCount, ' for ' + filterStr + decodeURI(searchKw));    	
    	var resultMsg = $("<div id='resultMsg'></div>").append(imgViewSwitcher, dataCount);  	
    	$('div#mpi2-search').html('');
    	$('div#mpi2-search').append(resultMsg, dTable);    	
    }
    
    function convert_proc_id_2_name(userFqStr){
    	
    	var pat = '(\\b\\w*\\b):"([a-zA-Z0-9_]*)"';    
		var regex = new RegExp(pat, "gi");		    	
		var result;
		var fqFieldVals = {};
		
    	while ( result = regex.exec(userFqStr) ) {    		
    		var field = result[1];
    		var id = result[2];    		
    		
    		$('table#pipelineFacetTbl td a').each(function(){
    			if ( $(this).attr('rel') == id ){
    				var name = $(this).parent().siblings('td[class^=procedure]').text();
    				userFqStr = userFqStr.replace(id, name);
    			}
    		});	
    	}	
    	return userFqStr;
    		
    }
    
    $.fn.relabelFilterForUsers = function(fqStr, facetDivId){
    	
    	var oldStr = fqStr;
    	for ( var i in MPI2.searchAndFacetConfig.facetFilterLabel ){    
    		var regex = new RegExp('\\b'+i+'\\b', "gi");	
    		fqStr = fqStr.replace(regex, MPI2.searchAndFacetConfig.facetFilterLabel[i]);    		
    	}
    
    	//fqStr = fqStr.replace(/\"1\"/g, '"Started"');
    	fqStr = fqStr.replace(/\"1\"/g, function(){
    		return facetDivId == 'diseaseFacet' ? 'yes' : 'Started';    		
    	});    	 	
    	
    	return fqStr;    	
    }   
    
    $.fn.getSolrRelevanceParams = function(facet, q, oParams){
    	
    	var wildCardStr = /^\*\w*$|^\w*\*$|^\*\w*\*$/;
    	if ( facet == 'gene' ){
    		if ( q.match(/^MGI:\d*$/i) ){
    			oParams.q = q.toUpperCase();
    			oParams.qf = 'mgi_accession_id';				
    		}
    		else if ( q.match(wildCardStr) && q != '*:*'){	
				oParams.bq='marker_symbol:'     +q.replace(/\*/g,'')+'^1000'
						  +'human_gene_symbol:' +q.replace(/\*/g,'')+'^800'
						  +'marker_synonym:'    +q.replace(/\*/g,'')+'^700'
						  +'marker_name:'       +q.replace(/\*/g,'')+'^500';
			}	
    		else {
    			oParams.pf='marker_symbol^1000 human_gene_symbol^800 marker_synonym^700 marker_name^500'; 
    		}
    	}
    	if ( facet == 'mp' ){    		
			if ( q.match(/^MP:\d*$/i) ){
				oParams.q = q.toUpperCase();
				oParams.qf = 'mp_id';				
			}
			//else if ( q.match(/^\*\w*|\w*\*$|^\*\w*\*$/) && q != '*:*'){
			else if ( q.match(wildCardStr) && q != '*:*'){	
				oParams.bq='mp_term:'         +q.replace(/\*/g,'')+'^1000'
					      +'mp_term_synonym:' +q.replace(/\*/g,'')+'^500'
					      +'mp_definition:'   +q.replace(/\*/g,'')+'^100';				
			}			
			else {	
				// does not seem to take effect if complexphrase is in use
				oParams.pf='mp_term^1000 mp_term_synonym^500 mp_definition^100';					
			}	
    	}
    	else if ( facet == 'disease' ){
    		if ( q.match(wildCardStr) && q != '*:*'){	
				oParams.bq='disease_term:'             +q.replace(/\*/g,'')+'^1000'
						  +'disease_alts:'             +q.replace(/\*/g,'')+'^700'
						  +'disease_human_phenotypes:' +q.replace(/\*/g,'')+'^500'						  
						  +'disease_source:'           +q.replace(/\*/g,'')+'^200';
			}	
    		else {
    			oParams.pf='disease_term^1000 disease_alts^700 disease_human_phenotypes^500 disease_source^200'; 
    		}
    	}
    	if ( facet == 'ma' ){    		
			if ( q.match(/^MA:\d*$/i) ){
				oParams.q = q.toUpperCase();
				oParams.qf = 'ma_id';				
			}
			//else if ( q.match(/^\*\w*|\w*\*$|^\*\w*\*$/) && q != '*:*'){
			else if ( q.match(wildCardStr) && q != '*:*'){			
				oParams.bq='ma_term:'         +q.replace(/\*/g,'')+'^1000'
				          +'ma_term_synonym:' +q.replace(/\*/g,'')+'^500';			   			
			}			
			else {	
				// does not seem to take effect if complexphrase is in use
				oParams.pf='ma_term^1000 ma_term_synonym^500';					
			}	
    	}
    	if ( facet == 'pipeline' ){    		
			if ( q.match(wildCardStr) && q != '*:*'){	
				oParams.bq='parameter_name: '+q.replace(/\*/g,'')+'^1000'
					      +'procedure_name: '+q.replace(/\*/g,'')+'^500';				
			}			
			else {	
				// does not seem to take effect if complexphrase is in use
				oParams.pf='parameter_name^1000 procedure_name^500';					
			}	
    	}
    	if ( facet == 'images' ){    		
			if ( q.match(wildCardStr) && q != '*:*'){	
				oParams.bq='annotationTermName: '+q.replace(/\*/g,'')+'^500'
					      +'expName: '+q.replace(/\*/g,'')+'^500';
						  +'symbol: '+q.replace(/\*/g,'')+'^500';
			}			
			else {	
				// does not seem to take effect if complexphrase is in use
				oParams.pf='annotationTermName^500 expName^500 symbol^500';					
			}	
    	}
    	
    	// applied to all facets
    	//if ( q.match(/^(\w+?\*\s+?){1,}(\w+?\*)?$/) ){
    	if ( q.indexOf(' ') != -1 && q.indexOf('*') != -1 ){	
		 	// a slop of 15 should be enough to account for the usual word length of mp term: let's try this for now
		 	// NOTE: w/0 slop, the ranking is weird in many cases 			 
			oParams.q='{!complexphrase}auto_suggest:"' + q + '"~15'; 
    	}
    	return oParams;
    }
    
    $.fn.loadDataTable = function(oHashParams){
    	    
    	var facetDivId = oHashParams.widgetName;
    	
    	//console.log(oHashParams.q, oHashParams.fq, facetDivId);    	
    	_prepare_resultMsg_and_dTableSkeleton(oHashParams);
    	
    	var oVal = MPI2.searchAndFacetConfig.facetParams[facetDivId];
    	//var oInfos = {};
    	
		//oInfos.mode = oVal.gridName;	
    	oHashParams.mode = oVal.gridName;
		
		//oInfos.dataTablePath = MPI2.searchAndFacetConfig.dataTablePath;
    	oHashParams.dataTablePath = MPI2.searchAndFacetConfig.dataTablePath;
		
		var oParams = MPI2.searchAndFacetConfig.facetParams[facetDivId].srchParams;
		if ( typeof oHashParams.fq == 'undefined' ){
			// get default
			oHashParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
		}
		
		oParams.fq = encodeURI(oHashParams.fq);				
		oParams.rows = 10;
		
		oParams.hl = 'true';
    	oParams['hl.snippets']=100; // otherwise only one in each field is return, and 100 should be enough to catch all for synonyms field, etc    	    	
    	oParams['hl.fl'] = '*';    	
		
		// bq, qf, pf for solr result relevance 

    	if ( facetDivId == 'geneFacet' ){
    		oParams.qf = MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams.qf;
    	}
    	
		if ( facetDivId == 'mpFacet' ){
			oParams = $.fn.getSolrRelevanceParams('mp', oHashParams.q, oParams);
		}					
				
		if ( facetDivId == 'imagesFacet' ) {
			//oInfos.showImgView = true;	
			oHashParams.showImgView = false;			
		}
		
		//oInfos.params = $.fn.stringifyJsonAsUrlParams(oParams);// + facetQryStr;
		oHashParams.params = $.fn.stringifyJsonAsUrlParams(oParams);		
		
    	if ( typeof oHashParams.facetName == 'undefined' ){		
    		//oInfos.solrCoreName = oVal.solrCoreName;
    		oHashParams.solrCoreName = oVal.solrCoreName;
    		
    	}
    	else {
    		//oInfos.facetName = oHashParams.facetName; 
    		oHashParams.facetName = oHashParams.facetName; 
    	}   	   	 	
		
		$.fn.updateBreadCrumb(oVal.solrCoreName);		
		$.fn.openFacet(oVal.solrCoreName);	
				
		$.fn.invokeDataTable(oHashParams);
		
    }   
    function _load_imgViewSwitcher(oDTable){		 	   		
    	// toggles two types of views for images: annotation view, image view	 	   		
   		var viewLabel, imgViewSwitcherDisplay, viewMode;
   		 	   		
   		oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet;
   		
   		if ( oConf.showImgView ){
   			oDTable.find('th:nth-child(2)').text("Image");
   		}
   		else {
   			oDTable.find('th:nth-child(2)').text("Example Images");
   		}   		
   		
   		var imgViewSwitcher = $('<div></div>').attr({'id':'imgView','rel':oConf.viewMode}).html(
   			"<span id='imgViewSubTitle'>" + oConf.viewLabel + "</span>" +
   			"<span id='imgViewSwitcher'>" + oConf.imgViewSwitcherDisplay + "</span>");   		 		
   		   		
   		return imgViewSwitcher;
	} 	
    
    $.fn.setDefaultImgSwitcherConf_ori = function(){
    	var oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet;
    	oConf.imgViewSwitcherDisplay = 'Show Annotation View';
		oConf.viewLabel = 'Image View: lists annotations to an image';
		oConf.viewMode = 'imageView';
		oConf.showImgView = true;		 
    }
    $.fn.setDefaultImgSwitcherConf = function(){
    	var oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet;
    	oConf.imgViewSwitcherDisplay = 'Show Image View';
		oConf.viewLabel = 'Annotation View: groups images by annotation';
		oConf.viewMode = 'annotView';
		oConf.showImgView = false;		 
    }
    $.fn.invokeDataTable = function(oInfos){   	   	
    	
    	var oDtable = $('table#' + oInfos.mode).dataTable({
    		"bSort" : false,
    		"bProcessing": true,
    		"bServerSide": true,	    		
    		//"sDom": "<'row-fluid'<'span6'><'span6'>>t<'row-fluid'<'span6'i><'span6'p>>",
    		"sDom": "<<'#exportSpinner'><'#tableTool'>r>t<<ip>>",    		
			"sPaginationType": "bootstrap",		
    		"fnDrawCallback": function( oSettings ) {  // when dataTable is loaded
    			
    			//console.log(oDtable.fnGetData().length); // rows on current page
    			// bring in some control logic for image view switcher when dataTable is loaded
    			if ( oInfos.widgetName == 'imagesFacet' ){    				
    				$('span#imgViewSwitcher').click(function(){	
    		   			
    		   			var oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet;  
    		   			
    		   			/*if ( oConf.imgViewSwitcherDisplay == 'Show Annotation View'){
    		   			
    		   				oConf.imgViewSwitcherDisplay = 'Show Image View'; 
    		   				oConf.viewLabel = 'Annotation View: groups images by annotation';    		   				
    		   				oConf.viewMode = 'annotView';    		   				
    		   				oConf.showImgView = false;
    		   				oInfos.showImgView = false; 
    		   			}
    		   			else {
    		   				$.fn.setDefaultImgSwitcherConf(); 
    		   				oInfos.showImgView = true;   		   				
    		   			}*/
    		   			
    		   			if ( oConf.imgViewSwitcherDisplay == 'Show Image View'){
        		   			
    		   				oConf.imgViewSwitcherDisplay = 'Show Annotation View'; 
    		   				oConf.viewLabel = 'Image View: lists annotations to an image';    		   				
    		   				oConf.viewMode = 'imgView';    		   				
    		   				oConf.showImgView = true;
    		   				oInfos.showImgView = true; 
    		   			}
    		   			else {
    		   				$.fn.setDefaultImgSwitcherConf(); 
    		   				oInfos.showImgView = false;   		   				
    		   			}
    		   			
    		   			_prepare_resultMsg_and_dTableSkeleton(oInfos);
    		   			
    		   			$.fn.invokeDataTable(oInfos);    		   					
    		   		});   
    			}  
    			    			
    			displayDataTypeResultCount(oInfos, this.fnSettings().fnRecordsTotal());
    			    			    			
    			// IE fix, as this style in CSS is not working for IE8 
    			if ( $('table#geneGrid').size() == 1 ){
    				$('table#geneGrid th:nth-child(1)').width('45%');
    			}		
    			    			
    			$('a.interest').click(function(){
    				
    				var mgiId = $(this).attr('id');
    				var label = $(this).text();
    				var regBtn = $(this);  
    				
    				$.ajax({
    					url: '/toggleflagfromjs/' + mgiId,                       
    					success: function (response) {
    						console.log('success');
    						
    						if(response === 'null') {
    							window.alert('Null error trying to register interest');
    						} 
    						else {    							
    							// 3 labels (before login is 'Interest')    							
    							//compare using the actual raw character for &nbsp;
    							if( label == String.fromCharCode(160)+'Register interest' ) {    								
    								regBtn.text(String.fromCharCode(160) + 'Unregister interest');    								    								
    								regBtn.siblings('i').removeClass('fa-sign-in').addClass('fa-sign-out')
    									.parent().attr('oldtitle', 'Unregister interest')
    									.qtip({       			
    				    					style: { classes: 'qtipimpc flat' },
    				    					position: { my: 'top center', at: 'bottom center' },    					
    				    					content: { text: $(this).attr('oldtitle')}
    				    					});	// refresh tooltip    								
    							} 
    							else if (label == String.fromCharCode(160)+'Unregister interest'){    							
    								regBtn.text(String.fromCharCode(160) + 'Register interest');    								
    								regBtn.siblings('i').removeClass('fa-sign-out').addClass('fa-sign-in')
    									.parent().attr('oldtitle', 'Register interest')
    									.qtip({       			
    										style: { classes: 'qtipimpc flat' },
    										position: { my: 'top center', at: 'bottom center' },    					
    										content: { text: $(this).attr('oldtitle')}
				    						}); // refresh tooltip
    							}    							                           
    						}                         
                        },
                        error: function () {
                        	window.alert('AJAX error trying to register interest');                     
                        }
                    });
    				return false;    		    	  
    			});
    			
    			// applied when result page first loads
    			$('div.registerforinterest, td .status').each(function(){
    				$(this).qtip({       			
    					style: { classes: 'qtipimpc flat' },
    					position: { my: 'top center', at: 'bottom center' },    					
    					content: { text: $(this).attr('oldtitle')}
    				});	
    			});    			   			
    			
    			initDataTableDumpControl(oInfos);
    		},
    		"sAjaxSource": oInfos.dataTablePath,    		
    		"fnServerParams": function ( aoData ) {    			
    			aoData.push(	    			 
    			    {"name": "solrParams",    				
    				 "value": JSON.stringify(oInfos, null, 2)    			     	
    				}	    
    			)		
    		}    		
    		
    		/*"fnServerData": function ( sSource, aoData, fnCallback, oSettings) {
    			// Add some extra data to the sender     			
    			aoData.push(	    			 
        			    {"name": "solrParams",
        				 //"value": oInfos.params// + oInfos.facetParams
        				 "vMPI.dataTableLoadedalue": JSON.stringify(oInfos, null, 2)
        				}	    
        		);	
    			oSettings.jqXHR = $.ajax( {
    	               // "url": "http://ves-ebi-d0.ebi.ac.uk:8080/phenotype-archive-dev/dataTable",
    	                "data": aoDainvokeDataTableta,
    	                "success": fnCallback,
    	                "success": function(json){
    	                	fnCallback(json);
    	                },
    	                "dataType": "jsonp",
    	                "cache": false
    			} );
    			    			
    			$.getJSON( sSource, aoData, function (json) { 
    				//Do whatever additional processing you want on the callback, then tell DataTables 
    				console.log('CHK');
    				fnCallback(json);
    			} );
    			
    		}  */		
    	});  	    
    	    
    	/*var oTableTools = new TableTools( oDtable, {
	        "sSwfPath": "/phenotype-archive/js/vendor/DataTables-1.9.4/extras/TableTools/media/swf/copy_csv_xls_pdf.swf",
	        "aButtons": [
	        			"copy"	
	        ]
    	});*/   

    }  	 
    
    function displayDataTypeResultCount(oInfos, count) {	
      
    	//var sFacet = typeof oInfos.solrCoreName !== 'undefined' ? oInfos.solrCoreName+'Facet' : oInfos.facetName;
    	var sFacet = oInfos.widgetName;    	
		var dataType = MPI2.searchAndFacetConfig.facetParams[sFacet].type;
		dataType = count > 1 ? dataType : dataType.replace(/s$/, '');	
		
		var txt = count + ' ' + dataType;
		
		if ( sFacet == 'imagesFacet' ){
						
			var imgUrl = baseUrl + "/imagesb?" + oInfos.params;
			
			if ( MPI2.searchAndFacetConfig.facetParams.imagesFacet.showImgView ){
	   			// record img count, as in annotation view, the count is number of annotations and not images
				//MPI2.searchAndFacetConfig.lastImgCount = count;
				$('span#resultCount span#annotCount').text('');
				$('span#resultCount a').attr({'href':imgUrl}).text(txt);
			}			
			else {	
													
				MPI2.searchAndFacetConfig.lastImgCount = $('div.flist li#images > span.fcount').text();				
				
				$('span#annotCount').text(count + ' annotations / ');
				txt = MPI2.searchAndFacetConfig.lastImgCount + ' ' + dataType;			
				$('span#resultCount a').attr({'href':imgUrl}).text(txt);
			}			
			
			if ( count == 0 ){		
				$('span#resultCount a').removeAttr('href').css({'text-decoration':'none','cursor':'normal','color':'gray'});
				$('span#annotCount').text(  oInfos.showImgView ? '' : '0 annotation / ');
			}
		}
		else {
			$('span#resultCount a').css({'text-decoration':'none','color':'gray'}).text(txt);			
		}	
    }
    
    function initDataTableDumpControl(oInfos){
    
    	$('div#saveTable').remove();
    	$('div#toolBox').remove();
    
    	//var saveTool = $("<div id='saveTable'></div>").html("Download table <img src='"+baseUrl+"/img/floppy.png' />");//.corner("4px");    	
    	var saveTool = $("<div id='saveTable'></div>").html("<span class='fa fa-download'>&nbsp;<span id='dnld'>Download</span></span>");//.corner("4px");    	
    	
    	var toolBox = fetchSaveTableGui();
    	
    	$('div.dataTables_processing').siblings('div#tableTool').append(saveTool, toolBox); 
    	
    	$('div#saveTable').click(function(){
    		
        	if ( $('div#toolBox').is(":visible")){
    			$('div#toolBox').hide();
    		}
    		else {
    			$('div#toolBox').show();        			       			
    			
    			// browser-specific position fix    					
    		    if ( parseInt( getInternetExplorerVersion()) === 8 ){	        
    			//if ($.browser.msie  && parseInt($.browser.version, 10) === 8) {
    				$('div#toolBox').css({'top': '-30px', 'left': '65px'});
    			}    			
    	    	var solrCoreName = oInfos.widgetName.replace('Facet','');
    	    	var iActivePage = $('div.dataTables_paginate li.active a').text();
    	    	
    	    	var iRowStart = iActivePage == 1 ? 0 : iActivePage*10-10;
    	    	//console.log('start: '+ iRowStart);
    	    	var showImgView = $('div#resultMsg div#imgView').attr('rel') == 'imgView' ? true : false;    	    		    	
    	    	
    	    	$('button.gridDump').unbind('click');
    	    	$('button.gridDump').click(function(){  
    	    	
    	    		initGridExporter($(this), {        	    							
    					externalDbId: 5,				
    					rowStart: iRowStart,
    					solrCoreName: solrCoreName,        				
    					params: oInfos.params,
    					showImgView: showImgView,
    					gridFields: MPI2.searchAndFacetConfig.facetParams[oInfos.widgetName].gridFields,
    					fileName: solrCoreName + '_table_dump'	
    	    		});
    	    		
    	    	});//.corner('6px'); 
    		}        		
    	});
    }
    
    function initGridExporter(thisButt, conf){
    	
		var classString = thisButt.attr('class');    		
		var fileType = thisButt.text(); 
		var dumpMode = thisButt.attr('class').indexOf('all') != -1 ? 'all' : 'page';    
		
		var url = baseUrl + '/export';	
		var sInputs = '';
		var aParams = [];
		for ( var k in conf ){
			aParams.push(k + "=" + conf[k]); 
			sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>";	    			
		}
		sInputs += "<input type='text' name='fileType' value='" + fileType.toLowerCase() + "'>";
		sInputs += "<input type='text' name='dumpMode' value='" + dumpMode + "'>";
		
		var form = "<form action='"+ url + "' method=get>" + sInputs + "</form>";
		
		if (dumpMode == 'all'){ 
			
			var paramStr = conf['params'] + "&start=" + conf['rowStart'] + "&rows=0";    			
			var url1;
			
			url1 = solrUrl + '/' + conf['solrCoreName'] + "/select?";
			paramStr += "&wt=json";			
			    		
			$.ajax({            	    
    			url: url1,
        	    data: paramStr,
        	    dataType: 'jsonp',
        	    jsonp: 'json.wrf',
        	    timeout: 5000,
        	    success: function (json){ 
					// prewarn users if dataset is big
					if ( json.response.numFound > 3000 ){							
						//console.log(json.response.numFound);
						if ( confirm("Download big dataset would take a while, would you like to proceed?") ){
							_doDataExport(url, form);
						}
					}
					else {
						_doDataExport(url, form);
					}
        	    },
        	    error: function (jqXHR, textStatus, errorThrown) {        	             	        
        	        $('div#facetBrowser').html('Error fetching data ...');
        	    }        	    
			});			
		}
		else {
			_doDataExport(url, form);
		}

		$('div#toolBox').hide();
		
    }

    // NOTE that IE8 prevents from download if over https.
    // see http://support.microsoft.com/kb/2549423
    function _doDataExport(url, form){
    	$.ajax({
			type: 'GET',
			url: url,
			cache: false,
			data: $(form).serialize(),
			beforeSend:function(){				
				$('div#exportSpinner').html(MPI2.searchAndFacetConfig.spinnerExport);						
			},
			success:function(data){    				
				$(form).appendTo('body').submit().remove();				
				$('div#exportSpinner').html('');				
			},
			error:function(){
				//alert("Oops, there is error during data export..");
			}
		});    	
    }
    
    function fetchSaveTableGui(){
    	
    	var div = $("<div id='toolBox'></div>");//.corner("4px");
    	div.append($("<div class='dataName'></div>").html("Current paginated entries in table"));    	
    	div.append($.fn.loadFileExporterUI({
			label: 'Export as:',
			formatSelector: {
				TSV: 'tsv_grid',
				XLS: 'xls_grid'	    			 					
			},
			'class': 'gridDump'
		}));
		div.append($("<div class='dataName'></div>").html("All entries in table")); 
		div.append($.fn.loadFileExporterUI({
			label: 'Export as:',
			formatSelector: {
				TSV: 'tsv_all',
				XLS: 'xls_all'	    			 					
			},
			'class': 'gridDump'
		}));
		return div;
    }
       
	$.fn.initDataTable = function(jqObj, customConfig){
	
		// extend dataTable with naturalSort function
		/*jQuery.fn.dataTableExt.oSort['natural-asc']  = function(a,b) {
		    return naturalSort(a,b);
		};	 
		jQuery.fn.dataTableExt.oSort['natural-desc'] = function(a,b) {
		    return naturalSort(a,b) * -1;
		};*/
				
		var params = {	
//				"sDom": "<'row-fluid'<'#foundEntries'><'span6'f>r>t<'row-fluid'<'#tableShowAllLess'><'span6'p>>",				
//				 "bPaginate":true,
					"bLengthChange": false,
					"bSort": true,
					"bInfo": false,
					"bAutoWidth": false ,
	    		"iDisplayLength": 10000 , // 10 rows as default 
	    		"bRetrieve": true,
	    		/* "bDestroy": true, */
	    		"bFilter":false,
    		"sPaginationType": "bootstrap",
				};
//				console.log('calling tools datababe ini');
		var oTbl = jqObj.dataTable($.extend({}, params, customConfig)).fnSearchHighlighting();
		return oTbl;
	};		
//    $.fn.dataTableshowAllShowLess = function(oDataTbl, aDataTblCols, display){
//    	    	
//    	var rowFound = oDataTbl.fnSettings().aoData.length;
//    	$('div#foundEntries').html("Total entries found: " + rowFound).addClass('span6');    	
//    	
//		$('div.dataTables_paginate').hide();
//		    		
//		$('div.dataTables_filter input').keyup(function(){
//						
//			if ( !$(this).val() ){								
//				$('div.dataTables_paginate').hide();							
//			}
//			else {
//				// use pagination as soon as users use filter
//				$('div.dataTables_paginate').show();
//			}
//		});	
//				
//		var display = ( display == 'Show fewer entries' || !display ) ? 'Show all entries' : 'Show fewer entries';  		
//			
//		// show all/less toggle only appears when we have > 10 rows in table
//		if ( rowFound > 10 ){			
//			$('div#tableShowAllLess').html("<span>" + display + "</span>").addClass('span6')
//			$.fn.reloadDataTable(oDataTbl, aDataTblCols, display);
//		}
//    }
//    
//    $.fn.reloadDataTable = function(oDataTbl, aDataTblCols, display){
//		$('div#tableShowAllLess').click(function(){    			
//			
//			oDataTbl.fnSettings()._iDisplayLength = display == 'Show all entries' ? -1 : 10;			
//			var selector = oDataTbl.selector;			
//			
//			display = display == 'Show all entries' ? 'Show fewer entries' : 'Show all entries';
//			$(this).find('span').text(display);
//			$(selector).dataTable().fnDraw();			
//		});
//    } ; 	    
	
	function naturalSort (a, b) {
        // setup temp-scope variables for comparison evauluation
        var x = a.toString().toLowerCase() || '', y = b.toString().toLowerCase() || '',
                nC = String.fromCharCode(0),
                xN = x.replace(/([-]{0,1}[0-9.]{1,})/g, nC + '$1' + nC).split(nC),
                yN = y.replace(/([-]{0,1}[0-9.]{1,})/g, nC + '$1' + nC).split(nC),
                xD = (new Date(x)).getTime(), yD = (new Date(y)).getTime();
        // natural sorting of dates
        if ( xD && yD && xD < yD )
                return -1;
        else if ( xD && yD && xD > yD )
                return 1;
        // natural sorting through split numeric strings and default strings
        for ( var cLoc=0, numS = Math.max( xN.length, yN.length ); cLoc < numS; cLoc++ )
                if ( ( parseFloat( xN[cLoc] ) || xN[cLoc] ) < ( parseFloat( yN[cLoc] ) || yN[cLoc] ) )
                        return -1;
                else if ( ( parseFloat( xN[cLoc] ) || xN[cLoc] ) > ( parseFloat( yN[cLoc] ) || yN[cLoc] ) )
                        return 1;
        return 0;
	}
	
	// toggle showing first 10 / all rows in a table
	$.fn.toggleTableRows = function(oTable){
		var rowNum = $(oTable).find('tbody tr').length;
 		
 		var rowToggler;
 		if ( rowNum > 10 ){    			
 			$(oTable).find("tbody tr:gt(9):lt(" + rowNum+ ")").hide();
 			var txtShow10 = 'Show all '+ rowNum + ' records';
 			rowToggler = $('<span></span>').attr({'class':'rowToggler'}).text(txtShow10).toggle(
 				function(){
 					$(oTable).find("tbody tr:gt(9):lt(" + rowNum+ ")").show();
 					$(this).text('Show first 10 records');
 				},
 				function(){
 					$(oTable).find("tbody tr:gt(9):lt(" + rowNum+ ")").hide();
 					$(this).text(txtShow10);
 				}
 			);    			
 		}
 	
 		return rowToggler;
	}
	
	$.fn.inArray = function(item, list) {
	    var length = list.length;
	    for(var i=0; i<length; i++) {
	        if(list[i] == item) {	        	
	        	return true;
	        }
	    }
	 
	    return false;
	}
	
	// get unique element from array
	$.fn.getUnique = function(list){
		var u = {}, a = [];
		for(var i = 0, l = list.length; i < l; ++i){
			if(list[i] in u){
				continue;
			}	
			a.push(list[i]);
		    u[list[i]] = 1;	   
		}	
		return a;
	}	
		
	// tooltip
	$.fn.komp2_tooltip = function(options){
		var defaults = {
			title        : '',	
			color        : 'black',
			bgcolor      : '#F4F4F4',
			mozBr        : '4px', // -moz-border-radius
		    webkitBr     : '4px', // -webkit-border-radius
		    khtmlBr      : '4px', // -khtml-border-radius
		    borderRadius : '4px'  // border-radius		    	
		}	
		var o = $.extend(defaults, options);
	
		return this.each(function(){
			var oC = $(this);
			var sTitle = oC.attr('title');
			if ( sTitle ) {
				oC.removeAttr('title');
			}
			else if ( o.title != '' ){
				sTitle = o.title;
			}
			else if ( o.url != '' ){
				// do ajax call
				$.ajax({   					
 					url: o.url,  					
 					success:function(data){ 
 						sTitle = data;
 					}
				});
			}
			 
			oC.hover(
				function(event){
					$('<div id="tooltip" />').appendTo('body').text(sTitle).css(
						{		
						'max-width' : '150px',	
						'font-size' : '10px',
						border : '1px solid gray',
						padding : '3px 5px',
						color : o.color,
						'background-color' : o.bgcolor,
						'z-index' : 999,
						position : 'absolute',
						'-moz-border-radius' : o.mozBr,
					    '-webkit-border-radius' : o.webkitBr,
					    '-khtml-border-radius' : o.khtmlBr,
					    'border-radius' : o.borderRadius
						}).komp2_updatePosition(event);
				},
				function(event){
					$('div#tooltip').remove();
				}
			);
		});
	}
	
	$.fn.komp2_updatePosition = function(event){
		return this.each(function(){
			$('div#tooltip').css({
				left : event.pageX + 10,
				top : event.pageY + 15
			})			
		});
	}
	// end of tooltip

	$.fn.upperCaseFirstLetter = function(str){
	    return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
	}	
	
})(jQuery);
	

//HIGHLIGHT FCT
$.fn.dataTableExt.oApi.fnSearchHighlighting = function(oSettings) {
    // Initialize regex cache
	if (oSettings == null){
		//console.log('oSettings is null or undefined');
		//was failing if null so added this - but presumably this is needed on the search pages still?
	}else{

    oSettings.oPreviousSearch.oSearchCaches = {};
      
    oSettings.oApi._fnCallbackReg( oSettings, 'aoRowCallback', function( nRow, aData, iDisplayIndex, iDisplayIndexFull) {
        // Initialize search string array
        var searchStrings = [];
        var oApi = this.oApi;
        var cache = oSettings.oPreviousSearch.oSearchCaches;
        // Global search string
        // If there is a global search string, add it to the search string array
        if (oSettings.oPreviousSearch.sSearch) {
            searchStrings.push(oSettings.oPreviousSearch.sSearch);
        }
        // Individual column search option object
        // If there are individual column search strings, add them to the search string array
        if ((oSettings.aoPreSearchCols) && (oSettings.aoPreSearchCols.length > 0)) {
            for (var i in oSettings.aoPreSearchCols) {
                if (oSettings.aoPreSearchCols[i].sSearch) {
                searchStrings.push(oSettings.aoPreSearchCols[i].sSearch);
                }
            }
        }
        // Create the regex built from one or more search string and cache as necessary
        if (searchStrings.length > 0) {
            var sSregex = searchStrings.join("|");
            if (!cache[sSregex]) {
                // This regex will avoid in HTML matches
                cache[sSregex] = new RegExp("("+sSregex+")(?!([^<]+)?>)", 'i');
            }
            var regex = cache[sSregex];
        }
        // Loop through the rows/fields for matches
        $('td', nRow).each( function(i) {
        	
            // Take into account that ColVis may be in use
            var j = oApi._fnVisibleToColumnIndex( oSettings,i);
            // Only try to highlight if the cell is not empty or null
            if (aData[j]) {
                // If there is a search string try to match
                if ((typeof sSregex !== 'undefined') && (sSregex)) {                	
                    this.innerHTML = aData[j].replace( regex, function(matched) {
                        return "<span class='hit'>"+matched+"</span>";
                    });
                }
                // Otherwise reset to a clean string
                else {
                    this.innerHTML = aData[j];
                }
            }
        });
        return nRow;
    }, 'row-highlight');
    return this;
    
	}
};


/* API method to get paging information for style bootstrap */
$.fn.dataTableExt.oApi.fnPagingInfo = function ( oSettings ){		
	return {
		"iStart":         oSettings._iDisplayStart,
		"iEnd":           oSettings.fnDisplayEnd(),
		"iLength":        oSettings._iDisplayLength,
		"iTotal":         oSettings.fnRecordsTotal(),
		"iFilteredTotal": oSettings.fnRecordsDisplay(),
		"iPage":          Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength ),
		"iTotalPages":    Math.ceil( oSettings.fnRecordsDisplay() / oSettings._iDisplayLength )
	};
}

/* Bootstrap style pagination control */
$.extend( $.fn.dataTableExt.oPagination, {
	"bootstrap": {
		"fnInit": function( oSettings, nPaging, fnDraw ) {
			var oLang = oSettings.oLanguage.oPaginate;
			var fnClickHandler = function ( e ) {
				e.preventDefault();
				if ( oSettings.oApi._fnPageChange(oSettings, e.data.action) ) {
					fnDraw( oSettings );
				}
			};

			$(nPaging).addClass('pagination pagination-small').append(
					'<ul>'+
					'<li class="prev disabled"><a href="#">&larr; '+oLang.sPrevious+'</a></li>'+
					'<li class="next disabled"><a href="#">'+oLang.sNext+' &rarr; </a></li>'+
					'</ul>'
			);
			var els = $('a', nPaging);
			
			$(els[0]).bind( 'click.DT', { action: "previous" }, fnClickHandler );
			$(els[1]).bind( 'click.DT', { action: "next" }, fnClickHandler );
		},
		
		"fnUpdate": function ( oSettings, fnDraw ) {
				var iListLength = 5;
				var oPaging = oSettings.oInstance.fnPagingInfo();
				var an = oSettings.aanFeatures.p;
				var i, j, sClass, iStart, iEnd, iHalf=Math.floor(iListLength/2);

				if ( oPaging.iTotalPages < iListLength) {
					iStart = 1;
					iEnd = oPaging.iTotalPages;
				}
				else if ( oPaging.iPage <= iHalf ) {
					iStart = 1;
					iEnd = iListLength;
				} 
				else if ( oPaging.iPage >= (oPaging.iTotalPages-iHalf) ) {
					iStart = oPaging.iTotalPages - iListLength + 1;
					iEnd = oPaging.iTotalPages;
				} 
				else {
					iStart = oPaging.iPage - iHalf + 1;
					iEnd = iStart + iListLength - 1;
				}
								
				for ( i=0, iLen=an.length ; i<iLen ; i++ ) {
					
					// Remove the middle elements
					$('li:gt(0)', an[i]).filter(':not(:last)').remove();
	
					// Add the new list items and their event handlers
					
					// modified for IMPC to show last page with '...' in front of it
					// but omit '...' when last page is within last five pages
					var count = 0;
					for ( j=iStart ; j<=iEnd ; j++ ) {
						
						count++;
						sClass = (j==oPaging.iPage+1) ? 'class="active"' : '';
										
						if (j != oPaging.iTotalPages ){
											
							$('<li '+sClass+'><a href="#">'+j+'</a></li>')				
							.insertBefore( $('li:last', an[i])[0] )
							.bind('click', function (e) {
							e.preventDefault();
							oSettings._iDisplayStart = (parseInt($('a', this).text(),10)-1) * oPaging.iLength;
							fnDraw( oSettings );
							} );							
							
							if (count==5){
								$("<li><span class='ellipse'>...</span></li>")				
								.insertBefore( $('li:last', an[i])[0] );							
							
								$('<li><a href="#">'+oPaging.iTotalPages+'</a></li>')				
								.insertBefore( $('li:last', an[i])[0] ).bind('click', function (e) {
									e.preventDefault();
									oSettings._iDisplayStart = (parseInt($('a', this).text(),10)-1) * oPaging.iLength;
									fnDraw( oSettings )});							
							}
						}
									
						if (  count <= 5  && j == oPaging.iTotalPages ) {
							$('<li '+sClass+'><a href="#">'+oPaging.iTotalPages+'</a></li>')							
							.insertBefore( $('li:last', an[i])[0] ).bind('click', function (e) {								
								e.preventDefault();
								oSettings._iDisplayStart = (parseInt($('a', this).text(),10)-1) * oPaging.iLength;
								fnDraw( oSettings )});
						}
					}									
						
					// Add / remove disabled classes from the static elements
					if ( oPaging.iPage === 0 ) {
						$('li:first', an[i]).addClass('disabled');
					} 
					else {
						$('li:first', an[i]).removeClass('disabled');
					}
		
					if ( oPaging.iPage === oPaging.iTotalPages-1 || oPaging.iTotalPages === 0 ) {
						$('li:last', an[i]).addClass('disabled');
					} 
					else {
						$('li:last', an[i]).removeClass('disabled');
					}
				}
			}
		}
} );

//Set the classes that TableTools uses to something suitable for Bootstrap
/*$.extend( true, $.fn.DataTable.TableTools.classes, {
	"container": "btn-group",
	"buttons": {
		"normal": "btn",
		"disabled": "btn disabled"
	},
	"collection": {
		"container": "DTTT_dropdown dropdown-menu",
		"buttons": {
			"normal": "",
			"disabled": "disabled"
		}
	}
} );
*/
// Have the tableTools collection use a bootstrap compatible dropdown
$.extend( true, $.fn.DataTable.TableTools.DEFAULTS.oTags, {
	"collection": {
		"container": "ul",
		"button": "li",
		"liner": "a"
	}
} );

$.extend( $.fn.dataTableExt.oStdClasses, {
    "sWrapper": "dataTables_wrapper form-inline"
} );

// Sort image columns based on the content of the title tag
$.extend( $.fn.dataTableExt.oSort, {
    "alt-string-pre": function ( a ) {
        return a.match(/alt="(.*?)"/)[1].toLowerCase();
    },

    "alt-string-asc": function( a, b ) {
        return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    },

    "alt-string-desc": function(a,b) {
        return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    }
} ); 

//fix jQuery UIs autocomplete width
$.extend($.ui.autocomplete.prototype.options, {
	open: function(event, ui) {
		$(this).autocomplete("widget").css({
            "width": ($(this).width() + "px")
        });
    }
});