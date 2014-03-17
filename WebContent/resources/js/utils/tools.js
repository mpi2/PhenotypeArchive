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
	$.fn.checkAndHighlightSubfacetTerms = function(){
		if ( $('ul#facetFilter li.ftag a').size() != 0 ){	   
			console.log('about to update facet count via filter settings ...');
			console.log($(this).html());
			MPI2.searchAndFacetConfig.hasFilters = true;		    			
		}
	}
	$.fn.parseUrlFordTableAndFacetFiltering = function(thisWidget){
		var self = thisWidget;
		var facet = self.element.attr('id');	
		console.log(facet + ' widget loaded ...');
				
		var oHashParams;
		//console.log(MPI2.searchAndFacetConfig);
		if ( MPI2.searchAndFacetConfig.hasFilters ){
			MPI2.searchAndFacetConfig.hasFilters = false;
			oHashParams = $.fn.parseHashString(window.location.hash.substring(1));
			oHashParams.widgetName = oHashParams.facetName + 'Facet';
			if ( typeof oHashParams.q == 'undefined' ){
				oHashParams.q = window.location.search.replace('?q=', '');
			} 
		}
		else {
			self.options.data.hashParams.q = window.location.search == '' ? '*:*' : window.location.search.replace('?q=', '');	
			oHashParams = self.options.data.hashParams;
			if ( /search\/?$/.exec(location.href) ){
				oHashParams.coreName = 'gene';
			}			
		}		
		//console.log(oHashParams);	
		if ( oHashParams.coreName ){
			//$.fn.loadDataTable(oHashParams);					
		}
		else if (oHashParams.facetName ) {			
			
			// widget open is when a facet category is clicked 
			// and we don't want to refresh facet, just open it	
			var refreshFacet = true;		    		
			$.fn.parseUrlForFacetCheckboxAndTermHighlight(oHashParams, refreshFacet);
		}
		else if ( oHashParams.widgetName ){
			//$.fn.loadDataTable(oHashParams);	
			oHashParams.q = window.location.search.replace('?q=', '');
		}
		$.fn.loadDataTable(oHashParams);
	}	
	$.fn.initFacetToggles = function(facet){
		
		// toggle Main Categories
		$('div.flist li#' + facet + ' > .flabel').click(function() {			
			if ($(this).parent('.fmcat').hasClass('open')) {
				$(this).parent('.fmcat').removeClass('open');
			} 
			else {
				$('.fmcat').removeClass('open');
				$(this).parent('.fmcat').addClass('open');
			}
		});
		
		// kick start itself (when initialized as above) if not yet
		if ( ! $('div.flist li#' + facet).hasClass('open') ){
			$('div.flist li#' + facet + ' > .flabel').click();
		}			
		
		// toggle Categorie Sections
		$('div.flist li#' + facet).find('li.fcatsection:not(.inactive) .flabel').click(function() { 
			$(this).parent('.fcatsection').toggleClass('open'); 
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
				console.log(facet + ' widget expanded');
				
				MPI2.searchAndFacetConfig.widgetOpen = true;				
				
				var oHashParams = $.fn.parseHashString(window.location.hash.substring(1));
				
				// deals with user query				
				if ( window.location.search != '' ){
					oHashParams.q = decodeURI(window.location.search.replace('?q=', ''));					
					oHashParams.fq = typeof oHashParams.fq == 'undefined' ? MPI2.searchAndFacetConfig.facetParams[facet+'Facet'].filterParams.fq : oHashParams.fq;					
				}
				
				var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facet + 'Facet'].solrCoreName;				
				var mode = typeof oHashParams.facetName != 'undefined' ? '&facet=' : '&core=';	
				
				if ( typeof oHashParams.q == 'undefined' ){					
					var oHashParams = thisWidget.options.data.hashParams;							
					window.location.hash = 'fq=' + oHashParams.fq + mode +  solrCoreName;
				}
				else {					
					oHashParams.fq = $.fn.fieldNameMapping(oHashParams.fq, facet);	
										
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
	$.fn.fetchQueryResult = function(q, facet, fqStr){		
		
		// make sure field mapping in url is correct with selected facet
		fqStr = $.fn.fieldNameMapping(fqStr, facet);
		
		// now update dataTable	 
		window.location.hash = 'q=' + q + '&fq=' + fqStr + '&facet=' + facet;	 
	};
	
	$.fn.setFacetCounts = function(q, fqStr, facet){
		if ( q == '' ){
			q = '*:*';
		}
		//console.log(q + " -- " +  fqStr + " -- " + facet);	
		
		do_megaGene(q, fqStr);			
		do_megaMp(q, fqStr);
		
		if ( facet != 'images' && facet != 'pipeline' ){
			// no images/procedures are annotated to diesease
			do_megaDisease(q, fqStr);
		}
		else {
			$('div.flist li#disease span.fcount').text(0);
		}
		
		if ( facet == 'disease' ){
			do_megaMa(q, fqStr);
			$('div.flist li#pipeline span.fcount').text(0);
			$('div.flist li#images span.fcount').text(0);
		}
		else {
			do_megaMa(q, fqStr);
			do_megaPipeline(q, fqStr, facet);
			do_megaImages(q, fqStr, facet);			
		}			   	
		
	};	
	
	function do_megaGene(q, fqStr){
		
		var fqStr = $.fn.fieldNameMapping(fqStr, 'gene');
		
		var aFields = ['imits_phenotype_complete', 'imits_phenotype_started', 'imits_phenotype_status', 'status', 
         'marker_type'];
		var fecetFieldsStr = $.fn.fetchFecetFieldsStr(aFields);
		
        var paramStr = 'q=' + q + '&wt=json&defType=edismax&qf=auto_suggest';
        paramStr += '&fq=' + fqStr + ' AND ' + MPI2.searchAndFacetConfig.facetParams.geneFacet.fq + fecetFieldsStr;        
        //console.log('GENE: '+ paramStr);
        
        $.ajax({ 	
			'url': solrUrl + '/gene/select',    		
    		'data': paramStr,
    		'dataType': 'jsonp',
    		'jsonp': 'json.wrf',
    		'success': function(json) {
    			//console.log('gene');
    			//console.log(json);
    			
				var oFacets = json.facet_counts.facet_fields;					
				var selectorBase = "div.flist li#gene";
				_facetRefresh(json, selectorBase);				
				
				// collapse all subfacet first, then open the first one that has matches 
				$(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout');
								
				var foundMatch = {'phenotyping':0, 'production':0, 'marker_type':0};
				
				for (var n=0; n<aFields.length; n++){
					
					if ( aFields[n].match(/^imits_/) && oFacets[aFields[n]].length != 0 ){
						foundMatch.phenotyping++;
					}
					else if ( aFields[n]=='status' && oFacets.status.length != 0 ){
						foundMatch.production++;
					}
					else if ( aFields[n]=='marker_type' && oFacets.marker_type.length != 0 ) {
						foundMatch.marker_type++;
					}					
				}									
								
				// update subfacet counts with matches
				for (var i=0; i<oFacets.status.length; i=i+2){			
					var subFacetName = oFacets.status[i];
					$(selectorBase + ' li.production span.flabel').each(function(){
						if ( $(this).text() == subFacetName ){
							$(this).siblings('span.fcount').text(oFacets.status[i+1]);
						}
					});
				}	
				for (var i=0; i<oFacets.marker_type.length; i=i+2){
					//console.log(oFacets.marker_type[i] + oFacets.marker_type[i+1]);
					var subFacetName = oFacets.marker_type[i];					
					$(selectorBase + ' li.marker_type span.flabel').each(function(){
						if ( $(this).text() == subFacetName ){
							$(this).siblings('span.fcount').text(oFacets.marker_type[i+1]);
						}
					});
				}				
				for (var i=0; i<oFacets.imits_phenotype_complete.length; i=i+2){
					if (oFacets.imits_phenotype_complete[i] == '1'){
						$(selectorBase + ' li.phenotyping span.flabel').each(function(){
							if ( $(this).text() == 'Complete' ){
								$(this).siblings('span.fcount').text(oFacets.imits_phenotype_complete[i+1]);
							}
						});
					}
				}
				for (var i=0; i<oFacets.imits_phenotype_started.length; i=i+2){
					if (oFacets.imits_phenotype_started[i] == '1'){	 
						$(selectorBase + ' li.phenotyping span.flabel').each(function(){
							if ( $(this).text() == 'Started' ){
								$(this).siblings('span.fcount').text(oFacets.imits_phenotype_started[i+1]);
							}
						});
					}
				}
				for (var i=0; i<oFacets.imits_phenotype_status.length; i=i+2){
					//console.log('***** '+ oFacets.imits_phenotype_status[i]);
					if (oFacets.imits_phenotype_status[i] == 'Phenotype Attempt Registered'){	
						$(selectorBase + ' li.phenotyping span.flabel').each(function(){
							if ( $(this).text() == 'Attempt Registered' ){
								$(this).siblings('span.fcount').text(oFacets.imits_phenotype_status[i+1]);
							}
						});
					}
				}
					

				$.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);	
				//_tickFilterCheckBox('gene');
    		}
        });
	}
	
	function do_megaMp(q, fqStr){
		
		fqStr = $.fn.fieldNameMapping(fqStr, 'mp');		
		var fecetFieldsStr = $.fn.fetchFecetFieldsStr(['annotated_or_inferred_higherLevelMpTermName']);
	
		var paramStr = 'q=' + q + '&wt=json&defType=edismax&qf=auto_suggest';
        paramStr += '&fq=' + fqStr + fecetFieldsStr; 
		
		//console.log('MP: '+ paramStr);
		$.ajax({ 	
			'url': solrUrl + '/mp/select',    		
    		'data': paramStr,
    		'dataType': 'jsonp',
    		'jsonp': 'json.wrf',
    		'success': function(json) {
    			//console.log('mp: ');	
    			//console.log(json);
    			
    			// refresh phenotype facet
    			var oFacets = json.facet_counts.facet_fields;   				
    						
    			var selectorBase = "div.flist li#mp";
				_facetRefresh(json, selectorBase); 
				
				for (var i=0; i<oFacets.annotated_or_inferred_higherLevelMpTermName.length; i=i+2){    			
    				var facetName = oFacets.annotated_or_inferred_higherLevelMpTermName[i];    				   				   				
    				var facetCount = oFacets.annotated_or_inferred_higherLevelMpTermName[i+1];
    			
    				$(selectorBase + ' li.fcat input').each(function(){
    					var aTxt = $(this).attr('rel').split('|');    					
    					if ( aTxt[2] == facetName ){    					
    						$(this).siblings('span.fcount').text(facetCount);
    					}
    				});    						
    			}    			
    			// tick checkbox if found from filter list    
    			//_tickFilterCheckBox('mp');				
    		}
		});		
	}	
	
	function _tickFilterCheckBox(facet){
		// facet: eg, mp	
		$('ul#facetFilter li.'+ facet + ' ul li a').each(function(){
			var txt = $(this).attr('rel');
			console.log(txt);
			if ( txt.match(/human_data|mouse_data|_predicted/) ){
				console.log(txt);
				
			}
			else {				
				$('div.flist li.fcat input').each(function(){				
					if ( txt.indexOf($(this).attr('rel')) != -1 ){
						$(this).attr('checked', true);
						$(this).siblings('span.flabel').addClass('highlight');						
					} 
				});
			}			
		});
	}
	
	function do_megaDisease(q, fqStr){
		
		fqStr = $.fn.fieldNameMapping(fqStr, 'disease');		
		var fecetFieldsStr = $.fn.fetchFecetFieldsStr(['disease_classes','disease_source','human_curated','mouse_curated','impc_predicted','impc_predicted_in_locus','mgi_predicted','mgi_predicted_in_locus']);
				
		var paramStr = 'q=' + q + '&wt=json&defType=edismax&qf=auto_suggest';
        paramStr += '&fq=' + fqStr + fecetFieldsStr;
        
		//console.log('DISEASE: '+ paramStr + fecetFieldsStr);
				
		$.ajax({ 	
			'url': solrUrl + '/disease/select',
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
    			
    			//_tickFilterCheckBox('disease');	
    		}
		});		
	}
	
	function do_megaMa(q, fqStr){
		
		fqStr = $.fn.fieldNameMapping(fqStr, 'ma');		
		
		var fecetFieldsStr = $.fn.fetchFecetFieldsStr(['annotated_or_inferred_higherLevelMaTermName'])		
		var paramStr = 'q=' + q + '&wt=json&defType=edismax&qf=auto_suggest';
        paramStr += '&fq=' + fqStr + fecetFieldsStr;		
				
		//console.log('MA: '+ paramStr);
		$.ajax({ 	
			'url': solrUrl + '/ma/select',
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
				
				for (var i=0; i<oFacets.annotated_or_inferred_higherLevelMaTermName.length; i=i+2){    			
    				var facetName = oFacets.annotated_or_inferred_higherLevelMaTermName[i];    				   				   				
    				var facetCount = oFacets.annotated_or_inferred_higherLevelMaTermName[i+1];
    			
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
		
	function do_megaPipeline(q, fqStr){		
				
		// image expName <-> pipeline procedure stable id mapping		
		fqStr = $.fn.fieldNameMapping(fqStr, 'pipeline');		
				
		var fecetFieldsStr = $.fn.fetchFecetFieldsStr(['pipeline_name', 'pipe_proc_sid']);
		var paramStr = 'q=' + q + '&wt=json&defType=edismax&qf=auto_suggest';
        paramStr += '&fq=' + fqStr + fecetFieldsStr;		
		
		//console.log('PIPELINE: '+ paramStr);
		$.ajax({ 	
			'url': solrUrl + '/pipeline/select',
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

	function do_megaImages(q, fqStr){
		
		// image expName <-> pipeline procedure stable id mapping	
		fqStr = $.fn.fieldNameMapping(fqStr, 'images');		
		var fecetFieldsStr = $.fn.fetchFecetFieldsStr(['annotated_or_inferred_higherLevelMpTermName', 'annotated_or_inferred_higherLevelMaTermName', 'expName', 'subtype']);		
		
		var paramStr = 'q=' + q + '&wt=json&defType=edismax&qf=auto_suggest';
        paramStr += '&fq=' + fqStr + fecetFieldsStr;
       
		//console.log('IMAGES: '+ paramStr);
		$.ajax({ 	
			'url': solrUrl + '/images/select',
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
    			
    			var aSubFacets = {'annotated_or_inferred_higherLevelMpTermName':'Phenotype',
    							  'annotated_or_inferred_higherLevelMaTermName':'Anatomy',
    							  'expName':'Procedure',
    							  'subtype':'Gene'}; 
    			
    			
    			for ( var facetStr in aSubFacets ){    				
	    			for (var j=0; j<oFacets[facetStr].length; j=j+2){	    				
	    				
	    				var facetName = oFacets[facetStr][j];	    								   				
	    				var facetCount = oFacets[facetStr][j+1];	    				 				
	    				foundMatch[aSubFacets[facetStr]]++;
	    				
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
	
	/*$.fn.fqStrIgnore = function(fqStr, facet){
		if ( /disease_source:|disease_classes:|_predicted\w*:|_curated\w*:/.exec(fqStr) ){
			if ( facet == 'pipeline ' || facet == 'images' ){
				return false;
			}
			
		}
		
	}*/
	
	$.fn.fieldNameMapping = function(fqStr, facet){
			
		var oMapping;		
		
		if ( facet != 'images' ){			
			fqStr = fqStr.replace('OR symbol:', 'OR marker_symbol:');			
		}
		
			
		if ( fqStr.indexOf('procedure_stable_id:') != -1 && facet == 'images' ){		
			oMapping = MPI2.searchAndFacetConfig.procSid2ExpNameMapping;			
		}
		else if (fqStr.indexOf('expName:') != -1 && facet != 'images' ){						
			oMapping = MPI2.searchAndFacetConfig.expName2ProcSidMapping;
		}		
		
		for( var name in oMapping ){
			fqStr = fqStr.replace(name, oMapping[name]);
		}
	
		if ( fqStr.indexOf('marker_type:') != -1 && facet == 'images' ){		
				oMapping = MPI2.searchAndFacetConfig.markerType2SubTypeMapping;			
		}
		else if (fqStr.indexOf('subtype:') != -1 && facet != 'images' ){						
				oMapping = MPI2.searchAndFacetConfig.subType2MarkerTypeMapping;
		}
		
		for( var name in oMapping ){
			fqStr = fqStr.replace(name, oMapping[name]);
		}	
		
		if ( facet == 'gene'){
			fqStr = fqStr.replace(' AND selected_top_level_ma_term:*', '').replace(' AND ontology_subset:*', '').replace('OR symbol:', 'OR marker_symbol:');
			
		}
		else if (facet == 'images' ) {			
			fqStr = fqStr.replace(/( AND )?\(?ontology_subset:\*\)?/,'').replace(/( AND )?\(?selected_top_level_ma_term:\*\)?/,'');			                                                                        
		}		
		else if ( facet == 'ma' ){
			fqStr.replace(' AND (ontology_subset:*)','');
			if (fqStr.indexOf(' AND selected_top_level_ma_term:*') == -1 ){
				fqStr += ' AND selected_top_level_ma_term:*';
			}
		}
		else if ( facet == 'pipeline' ){
			fqStr = fqStr.replace(' AND selected_top_level_ma_term:*', '');
		}
		else if ( facet == 'mp' || facet == 'disease' ){
			fqStr = fqStr.replace(' AND selected_top_level_ma_term:*', '');	
			
			if (! /( AND )?\(?ontology_subset:\*\)?/.exec(fqStr) && facet == 'mp' ){	
				fqStr += ' AND ontology_subset:*';
			}	
		}
		
		return decodeURI(fqStr);	
	}
	
	$.fn.fetchFecetFieldsStr = function(aFacetFields){
		var facetFieldsStr = '';;
		for ( var i=0; i<aFacetFields.length; i++){
			facetFieldsStr += '&facet.field=' + aFacetFields[i];
		}
		return facetFieldsStr + "&facet=on&facet.limit=-1&facet.mincount=1&rows=0";
	}
	
	function _parse_facetCount_by_name(facetName, oFacets) {
		var counts = 0;
		var aTop_mp_term_ids = oFacets.top_mp_term_id;
		for ( var i=0; i<aTop_mp_term_ids.length; i=i+2){						
			if ( aTop_mp_term_ids[i].indexOf(facetName) != -1 ){
				var aStr = aTop_mp_term_ids[i].split('__');
				var top_mp_id = aStr[1];
				var aMps = oFacets.top2mp_term;
				for (var j=0; j<aMps.length; j=j+2){
					if ( aMps[j].indexOf(top_mp_id) != -1 ){
						counts++;
					}
				}
				return counts;				
			}
		}
	}
	
	$.fn.composeFacetFilterControl = function(oChkbox, q){	
	
		var labels = oChkbox.attr('rel').split("|");
		console.log(labels);
		var facet = labels[0];
		var field = labels[1];
		var value = labels[2];
		var thisLi = $('ul#facetFilter li.' + facet);
						
		if ( oChkbox.is(':checked') ){		
			
			if ( !$('div.ffilter').is(':visible') ){
				$('div.ffilter').show();			
			}
			
			// show filter facet caption
			thisLi.find('.fcap').show();
			
			// add filter
			$.fn.addFacetFilter(oChkbox, q);			
			updateFacetUrlTable(q, facet);	
		}
		else {	
			//console.log('uncheck ' + facet);
			// uncheck checkbox with matching value		
			$('ul#facetFilter li.ftag').each(function(){				
				if ( $(this).find('a').attr('rel') == oChkbox.attr('rel') ){					
					$(this).remove();					
					oChkbox.siblings('span.flabel').removeClass('highlight');					
				}
			});			
			
			// hide facet caption in filter if no filter for that facet is present
			$('ul#facetFilter li.has-sub').each(function(){
				if ( $(this).find('li.ftag').size() == 0 ){					
					$(this).find('fcap').hide();
				}				
			});  			
			
			// when all filters are unchecked, hide facet filter container and reload current url (for current facet)			
			if ( $('ul#facetFilter li.ftag').size() == 0 ){							
				$('ul#facetFilter li.has-sub').each(function(){
					$(this).find('ul').remove();
					$(this).find('.fcap').hide();
					$(this).hide();
				});
				
				var url;
				
				if ( window.location.search != '' ){
					//alert('search kw');
					// has search keyword
					url = baseUrl + '/search?q=' + q;
					window.history.pushState({},"", url);// change browser url
					//location.reload();	
				}
				else {
					// no search keyword					
					window.history.pushState({},"", baseUrl + '/search');
					//location.reload();
					//var fqStr = MPI2.searchAndFacetConfig.facetParams[facet+'Facet'].filterParams.fq;					
					//url = baseUrl + '/search#fq=' + fqStr + '&core=' + facet;
					
				}
				
				//window.history.pushState({},"", url);// change browser url
				location.reload();				
			}
			else {
				updateFacetUrlTable(q, facet);
			}
		}
	
		// update facet filter and compose solr query for result
		/*var fqStr = _composeFilterStr(facet);
		console.log(facet + ' :: ' + fqStr);
		$.fn.setFacetCounts(q, fqStr, facet);
		$.fn.fetchQueryResult(q, facet, fqStr);*/
		
	}	
	function updateFacetUrlTable(q, facet){
		// update facet filter and compose solr query for result
		var fqStr = _composeFilterStr(facet);		
		$.fn.setFacetCounts(q, fqStr, facet);
		$.fn.fetchQueryResult(q, facet, fqStr);
	}
	
	$.fn.removeFacetFilter = function(facet) { 
		$('div.ffilter').hide();
	    $('ul#facetFilter li.has-sub ul').remove();
	    //$('ul#facetFilter span.fcap').css('visibility','hidden');
	    $('ul#facetFilter span.fcap').hide();
	    
	    // uncheck all checkboxes/unhighlight           
	    $('div.flist li.fcat input').prop('checked', false);
	    $('div.flist li.fcat span.flabel').removeClass('highlight');	    	
	}
	$.fn.addFacetFilter = function(oChkbox, q){
		
		if ( !$('div.ffilter').is(':visible') ){
			$('div.ffilter').show();			
		}
		
		var labels = oChkbox.attr('rel').split('|');
		// add filter
		var facet = labels[0];
		var field = labels[1];
		var value = labels[2];
		var thisLi = $('ul#facetFilter li.' + facet);
				
		// show filter facet caption
		//thisLi.find('.fcap').show().css('visibility', 'visible');
		thisLi.find('.fcap').show();
		
		var display = MPI2.searchAndFacetConfig.facetFilterLabel[field];
		if ( value == 1 ){
			value = field == 'imits_phenotype_started' ? 'Started' : 'Yes';		
		}	
		
		var qValue = '"' + value + '"';
		//var filterTxt = ( facet == 'gene' || facet == 'images' || facet == 'disease' ) ? display + ' : ' + qValue : value;
		var filterTxt = value;
		if ( facet == 'gene' ){
			if ( value == 'Started'  ){
				filterTxt = 'phenotyping started'; 
			}
			else if ( value == 'Phenotype Attempt Registered' || field == 'status' || field == 'marker_type' ){
				filterTxt = value.toLowerCase();
			}
		}
		
		var pipelineName, a;
		
		if (facet == 'pipeline'){
			var names = filterTxt.split('___');					
			filterTxt = oChkbox.attr('class').replace(/_/g, ' ') + ' : ' + '"' + names[0] + '"';
		}
		if (facet == 'disease' && field.match(/_curated|_predicted/) ){
			filterTxt = MPI2.searchAndFacetConfig.facetFilterLabel[field]; 		
		}
		
		var a = $('<a></a>').attr({'rel':oChkbox.attr('rel')}).text(filterTxt.replace(/ phenotype$/, ''));		
		//var del = $('<img>').attr('src', baseUrl + '/img/scissors-15x15.png');
		var hiddenLabel = $("<span class='hidden'></span>").text(_composeFilterStr(facet, field, value));
		
		var filter = $('<li class="ftag"></li>').append(a, hiddenLabel);			
		
		add_uncheck_js(a, filter, oChkbox, q);
				
		if ( thisLi.find('ul').size() == 0 ){			
			var ul = $('<ul></ul>').html(filter);
			thisLi.append(ul);								
		}
		else if ( thisLi.find('ul').html() == '' ){
			thisLi.find('ul').append(filter)
		}
		else {
			// double check this filter not already exists: eg, check same filter as the exclusive subFacet did
			thisLi.find('ul li a').each(function(){
				if ($(this).text() != filterTxt){
					thisLi.find('ul').append(filter);
				}
			});								
		}
					
		thisLi.show();
		//$('ul#facetFilter li.none').hide();	
		
	}
	
	function _composeFilterStr(facet, field, value){	
		
		if ( arguments.length == 1 ){	
			
			var aStr = [];
			$('ul#facetFilter li li a').each(function(){				
				var aVals = $(this).attr('rel').split("|");		
				var fqField = aVals[1];
				var value =  aVals[2];
						
				if ( fqField == 'procedure_stable_id' ){
					var aV = value.split('___');
					value = aV[1]; // procedure stable id					
				}						
				
				aStr.push('(' + fqField + ':"' + value + '")');				
			});
			
			var fqStr = aStr.join(' AND ');			
			
			if ( fqStr.indexOf('annotated_or_inferred_higherLevelMxTermName') != -1 ){			
				fqStr += ' AND (ontology_subset:*)';
			}	
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
			
	function add_uncheck_js(oLia, filter, oChkbox, q) {
		oLia.click(function(){			
			oChkbox.attr("checked", false);			
			oChkbox.siblings('span.flabel').removeClass('highlight');
			filter.remove();
			$.fn.composeFacetFilterControl(oChkbox, q);
		});
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
    	var iconDiv = $('<p></p>').attr({'class': 'textright'}).html(label + " &nbsp;");
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
   
    $.fn.parseUrlForFacetCheckboxAndTermHighlight = function(oHashParams, refreshFacet){	
    	var self = this;
    	console.log('parsing url for filter and chkbox:');
    	//console.log(oHashParams);
    	var facet = oHashParams.widgetName;    	
    	var fqStr = oHashParams.fq;
    	
    	//fqStr = fqStr.replace(MPI2.searchAndFacetConfig.facetParams[facet].filterParams.fq, '').replace(/ AND /g, '');    
    	
    	facet = facet.replace('Facet','');
    	
    	//console.log(facet + ' ' + fqStr);
    	 
		// unhightlight/uncheck all facets
        $.fn.removeFacetFilter();
        
		var pat = '(\\b\\w*\\b):"([a-zA-Z0-9_\/ ]*)"';		
		var regex = new RegExp(pat, "gi");		    	
		var result;		
		var wantStr;
		
    	while ( result = regex.exec(fqStr) ) {  
    		
    		wantStr =  result[1] == 'procedure_stable_id' ? result[2] : result[1]+ '|' + result[2];    		  
    		//console.log('WANT: ' + wantStr);    		
    		var obj = $('div.flist li.fcat').find('input[rel*="'+wantStr+'"]');       		
    	    //console.log(obj);		
    		if (obj.length != 0 ){	 
    			//console.log(obj);
	    		// tick checkbox 
                obj.prop('checked', true);
                
                // highlight this facet term                    
                obj.siblings('.flabel').addClass('highlight');                  
                        
                // repopulate the filter                
                $.fn.addFacetFilter(obj, oHashParams.q);                 
    		}    
    		else {
    			var field = wantStr.replace(/\|.+/, '');
    			console.log(field);
    			var filterFacet = MPI2.searchAndFacetConfig.filterMapping[field].facet;
                console.log(filterFacet);
                if ( facet != filterFacet ){
                	console.log('need to do ' + filterFacet);
                	var oInput = $('<input>').attr({'type':'checkbox', 'rel': filterFacet + '|'+ wantStr});
                	console.log(oInput);
                	$.fn.addFacetFilter(oInput, oHashParams.q);
                }              
    		}
    	}    	
    	    	
    	if ( refreshFacet ){
    		var fqStr = _composeFilterStr(facet); // from filter block            
            $.fn.setFacetCounts(oHashParams.q, fqStr, facet); 
    	}
    	else {
    		//console.log('work here');
    		// open the right facet based on url
    		if ( ! $('div.flist li#' + facet).hasClass('open') ){
    			$('div.flist li#' + facet + ' > .flabel').click();
    		}
    	}
    }
    $.fn.parseUrlForFacetCheckboxAndTermHighlightOri = function(oHashParams, refreshFacet){	
    	var self = this;
    	console.log('parsing url for filter and chkbox');
    	var facet = oHashParams.widgetName;    	
    	var fqStr = oHashParams.fq;
    	fqStr = fqStr.replace(MPI2.searchAndFacetConfig.facetParams[facet].filterParams.fq, '').replace(/ AND /g, '');    	
    	facet = facet.replace('Facet','');
    	
    	//console.log(facet + ' ' + fqStr);
    	 
		// unhightlight/uncheck all facets
        $.fn.removeFacetFilter();
        
		var pat = '(\\b\\w*\\b):"([a-zA-Z0-9_\/ ]*)"';		
		var regex = new RegExp(pat, "gi");		    	
		var result;		
		var wantStr;
		
    	while ( result = regex.exec(fqStr) ) {    	
    		wantStr =  result[1] == 'procedure_stable_id' ? result[2] : result[1]+ '|' + result[2];    		  
    		//console.log('WANT: ' + wantStr);    		
    		var obj = $('div.flist li.fcat').find('input[rel*="'+wantStr+'"]');       		
    	    		
    		if (obj.length != 0 ){	    		
	    		// tick checkbox 
                obj.prop('checked', true);
                
                // highlight this facet term                    
                obj.siblings('.flabel').addClass('highlight');                  
                        
                // repopulate the filter                
                $.fn.addFacetFilter(obj, oHashParams.q);
    		}	
    		else {
    			wantStr =  result[1]+ '|' + result[2];
    			
    			// add other filter in list so that the facet count of uninitialized facets will be updated
    			    			
    			var oMapping;
    			if ( wantStr.match(/^procedure_stable_id\|.*/) ){
        			    				
    				var sid = wantStr.replace('procedure_stable_id|', '');
    				    				
    				// need to do ajax solr query to fetch for procedure name from procedure_id    				
    				$.ajax({ 	
    					'url': solrUrl + '/pipeline/select',
    					'data': 'q=procedure_stable_id:"' + sid + '"&fl=procedure_name,pipeline_name&rows=1&wt=json',
    					'dataType': 'jsonp',
    					'async': false,
    					'jsonp': 'json.wrf',
    					'success': function(json) {
    					
    						var procName = json.response.docs[0].procedure_name;
    						var pipeName = json.response.docs[0].pipeline_name;
    		    			//console.log(procName);
    						var relStr = 'pipeline|procedure_stable_id|' + procName + '___' + sid;
    		    			var obj = $('<input></input>').attr({'rel': relStr, 'class': pipeName.replace(/ /g, '_')});
    		    			$.fn.addFacetFilter(obj, oHashParams.q);  
    					}    					
    				});				
    			}    			
    			else {
    				if ( wantStr.match(/mortality\/aging/) || wantStr.match(/^annotated_or_inferred_higherLevelMxTermName|.+phenotype$/) ){    			
    					oMapping = MPI2.searchAndFacetConfig.filterMapping['mp'];
    				}
    				else if ( wantStr.match(/^annotated_or_inferred_higherLevelMpTermName/) ){    			
    					oMapping = MPI2.searchAndFacetConfig.filterMapping['imgMp'];
    				}
    				else if ( wantStr.match(/^annotated_or_inferred_higherLevelMaTermName/) ){    			
    					oMapping = MPI2.searchAndFacetConfig.filterMapping['imgMa'];
    				}    				
	    			else {
	    				var testStr = wantStr.replace(/\|.+$/, '');
	    			
	    				//console.log(testStr);
	    				oMapping = MPI2.searchAndFacetConfig.filterMapping[wantStr] ? MPI2.searchAndFacetConfig.filterMapping[wantStr] 
	    					: MPI2.searchAndFacetConfig.filterMapping[testStr];
	    			}
	    			
	    			
	    			var relStr = oMapping.facet + '|' + wantStr;    			
	    			
	    			var obj = $('<input></input>').attr({'rel': relStr, 'class': oMapping['class']});
	    			$.fn.addFacetFilter(obj, oHashParams.q);
    			}
    		}	
    		
    	} 
    	
		// Work out which subfacet needs to be open:
		// This is for gene / disease / pipeline and images cores where there are collapsed subfacets by default.
		// Ie, if a particular subfacet was open, we need to reopen it now when page reloads
    	// But ignore this bit if we are dealing with hash change in url (ie, not pageReload)
    	/*if ( typeof pageReload != 'undefined' ){  
    		console.log('do pagereload');    	
    		// collapse all subfacets first
    		var baseSelector = 'div.flist li#' + facet + ' li.fcatsection';
    		$(baseSelector).removeClass('open');
    		
    		for (var i=0; i<objList.length; i++ ){
    			var aList = objList[i].attr('rel').split('|');
    			$(baseSelector + '.' + aList[4]).addClass('open');
    		}
    		//_setFacetToOpen(objList, oHashParams);
    		//_openFacetWithFilter(objList);
    	}*/
    	
    	if ( refreshFacet ){
    		var fqStr = _composeFilterStr(facet); // from filter block            
            $.fn.setFacetCounts(oHashParams.q, fqStr, facet); 
    	}
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
    
    function _setFacetToOpen2(objList, oHashParams){
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
    			//fcatsection = 'annotated_or_inferred_higherLevelMpTermName';
    			fcatsection = 'mp';
    		}
    		//_arrowSwitch(fcatsection); 
    		
    		
    	}    	
    	
    }  
    
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
		oParams.fq = encodeURI(oHashParams.fq);
		
		oParams.q = oHashParams.q 
		oParams.rows = 10;
				
		
		if ( facetDivId == 'imagesFacet' ) {
			//oInfos.showImgView = true;	
			oHashParams.showImgView = true;	
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
		
		//console.log(oHashParams);
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
    
    $.fn.setDefaultImgSwitcherConf = function(){
    	var oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet;
    	oConf.imgViewSwitcherDisplay = 'Show Annotation View';
		oConf.viewLabel = 'Image View: lists annotations to an image';
		oConf.viewMode = 'imageView';
		oConf.showImgView = true;		 
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
    			
    			// bring in some control logic for image view switcher when dataTable is loaded
    			if ( oInfos.widgetName == 'imagesFacet' ){    				
    				$('span#imgViewSwitcher').click(function(){	
    		   			
    		   			var oConf = MPI2.searchAndFacetConfig.facetParams.imagesFacet;  
    		   			
    		   			if ( oConf.imgViewSwitcherDisplay == 'Show Annotation View'){
    		   			
    		   				oConf.imgViewSwitcherDisplay = 'Show Image View'; 
    		   				oConf.viewLabel = 'Annotation View: groups images by annotation';    		   				
    		   				oConf.viewMode = 'annotView';    		   				
    		   				oConf.showImgView = false;
    		   				oInfos.showImgView = false; 
    		   			}
    		   			else {
    		   				$.fn.setDefaultImgSwitcherConf(); 
    		   				oInfos.showImgView = true;   		   				
    		   			}
    		   			
    		   			_prepare_resultMsg_and_dTableSkeleton(oInfos);
    		   			
    		   			$.fn.invokeDataTable(oInfos);    		   					
    		   		});   
    			}  
    			    			
    			displayDataTypeResultCount(oInfos, this.fnSettings().fnRecordsTotal());
    			    			    			
    			// ie fix, as this style in CSS is not working for IE8 
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
    						function endsWith(str, suffix) {
    							return str.indexOf(suffix, str.length - suffix.length) !== -1;
                        	}
    						if(response === 'null') {
    							window.alert('Null error trying to register interest');
    						} 
    						else {    							
    							// 3 labels (before login is 'Interest')
    							if( label == 'Register interest' ) {
    								regBtn.text('Unregister interest');    								    								
    								regBtn.siblings('i').removeClass('fa-sign-in').addClass('fa-sign-out')
    									.parent().attr('oldtitle', 'Unregister interest')
    									.qtip({       			
    				    					style: { classes: 'qtipimpc flat' },
    				    					position: { my: 'top center', at: 'bottom center' },    					
    				    					content: { text: $(this).attr('oldtitle')}
    				    					});	// refresh tooltip    								
    							} 
    							else if (label == 'Unregister interest'){
    								regBtn.text('Register interest');    								
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
				MPI2.searchAndFacetConfig.lastImgCount = count;
				$('span#resultCount span#annotCount').text('');
				$('span#resultCount a').attr({'href':imgUrl}).text(txt);
			}			
			else {							
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
    	var saveTool = $("<div id='saveTable'></div>").html("<span class='fa fa-download'>&nbsp;Download</span>");//.corner("4px");    	
    	
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
    	    	var showImgView = $('div#resultMsg div#imgView').attr('rel') == 'imageView' ? true : false; 
    	    	    	    	
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
			
			url1 = MPI2.searchAndFacetConfig.solrBaseURL_ebi + conf['solrCoreName'] + "/select?";
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


/* API method to get paging information */
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
			} else if ( oPaging.iPage >= (oPaging.iTotalPages-iHalf) ) {
				iStart = oPaging.iTotalPages - iListLength + 1;
				iEnd = oPaging.iTotalPages;
			} else {
				iStart = oPaging.iPage - iHalf + 1;
				iEnd = iStart + iListLength - 1;
			}

			for ( i=0, iLen=an.length ; i<iLen ; i++ ) {
				// Remove the middle elements
				$('li:gt(0)', an[i]).filter(':not(:last)').remove();

				// Add the new list items and their event handlers
				for ( j=iStart ; j<=iEnd ; j++ ) {
					sClass = (j==oPaging.iPage+1) ? 'class="active"' : '';
					$('<li '+sClass+'><a href="#">'+j+'</a></li>')
						.insertBefore( $('li:last', an[i])[0] )
						.bind('click', function (e) {
							e.preventDefault();
							oSettings._iDisplayStart = (parseInt($('a', this).text(),10)-1) * oPaging.iLength;
							fnDraw( oSettings );
						} );
				}

				// Add / remove disabled classes from the static elements
				if ( oPaging.iPage === 0 ) {
					$('li:first', an[i]).addClass('disabled');
				} else {
					$('li:first', an[i]).removeClass('disabled');
				}

				if ( oPaging.iPage === oPaging.iTotalPages-1 || oPaging.iTotalPages === 0 ) {
					$('li:last', an[i]).addClass('disabled');
				} else {
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

