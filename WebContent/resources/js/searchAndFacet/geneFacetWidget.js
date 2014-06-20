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
		
    $.widget('MPI2.geneFacet', {
    	
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
	    	    	
	    	/*var queryParams = $.extend({}, { 
				'rows': 0,
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,							
				'facet.sort': 'count',					
	    		'q': self.options.data.hashParams.q},
	    		MPI2.searchAndFacetConfig.commonSolrParams,
	    		MPI2.searchAndFacetConfig.facetParams.geneFacet.filterParams
	    	);    	   	
	    	*/
	    
	    	var fq = MPI2.searchAndFacetConfig.currentFq ? MPI2.searchAndFacetConfig.currentFq
	    			: self.options.data.hashParams.fq;
	    	
	    	var oParams = {};		
	        oParams = $.fn.getSolrRelevanceParams('gene', self.options.data.hashParams.q, oParams);
	    	
	    	var queryParams = $.extend({}, {				
				'fq': fq,
				'rows': 0, // override default
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,
				'facet.sort': 'count',						
				'q': self.options.data.hashParams.q}, MPI2.searchAndFacetConfig.commonSolrParams, oParams);	
	    	
	    	
	    	// facet on latest_phenotype_status 
	    	/*
	    	<int name="Phenotype Attempt Registered">1362</int>
	    	<int name="Phenotyping Started">586</int>
	    	<int name="Phenotyping Complete">288</int>
	    	*/
	    	
	    	var queryParamStr = $.fn.stringifyJsonAsUrlParams(queryParams) 
	    		  + '&facet.field=marker_type'
				  + '&facet.field=status'
				 // + '&facet.field=imits_phenotype_started' 
				 // + '&facet.field=imits_phenotype_complete'
				 // + '&facet.field=imits_phenotype_status'	
				  + '&facet.field=latest_phenotype_status'	// use this field instead of the above three
				  + '&facet.field=latest_production_centre'
				  + '&facet.field=latest_phenotyping_centre';
	    	
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
	    	//console.log(json);
	    	var self = this;
	    	var numFound = json.response.numFound;
	    	
	    	/*-------------------------------------------------------*/
	    	/* ------ displaying sidebar and update dataTable ------ */
	    	/*-------------------------------------------------------*/	    	  	
	    	var foundMatch = {'phenotyping':0,'production':0, 'latest_production_centre':0, 'latest_phenotyping_centre':0, 'marker_type':0};
	    	
	    	if (numFound > 0){
	    		
	    		// subfacet: IMPC mouse phenotyping status	    		
	    		var phenoStatusSect = $("<li class='fcatsection phenotyping' + ></li>");		 
	    		phenoStatusSect.append($('<span></span>').attr({'class':'flabel'}).text('IMPC Phenotyping Status'));	    		
	    		
	    		var pheno_count = {};
	    		
	    		var aImitsPhenos = {'Phenotyping Complete':'Complete', 
						'Phenotyping Started':'Started', 
						'Phenotype Attempt Registered':'Attempt Registered'};
	    		
	    		var phenoStatusFacetField = 'latest_phenotype_status';
	    		var phenoCount = 0;
	    		
    			var phenoFieldList = json.facet_counts['facet_fields'][phenoStatusFacetField];	
    			
    			if ( phenoFieldList.length != 0 ){
    				phenoCount = 1;
    				foundMatch.phenotyping++;
    	    		for ( var j=0; j<phenoFieldList.length; j+=2 ){
    	    			// only want these statuses	 
    	    			
    	    			var fieldName = phenoFieldList[j];
    	    			if (fieldName == 'Phenotype Attempt Registered' ||
    	    				fieldName == 'Phenotyping Started' ||
    	    				fieldName == 'Phenotyping Complete' ){
    	    				
    	    				pheno_count[aImitsPhenos[fieldName]] = phenoFieldList[j+1];
    	    			}
    	    		} 
    			} 
	    		
	    		var phenoUlContainer = $("<ul></ul>");
	    		
	    		var aPhenos = ['Complete', 'Started', 'Attempt Registered'];	    		
	    		for ( var i=0; i<aPhenos.length; i++ ){
					var phenotypingStatusFq = MPI2.searchAndFacetConfig.phenotypingStatuses[aPhenos[i]].fq;
					var phenotypingStatusVal = MPI2.searchAndFacetConfig.phenotypingStatuses[aPhenos[i]].val; 
					var count = pheno_count[aPhenos[i]];
									
					if ( count !== undefined ){
						
						var liContainer = $("<li></li>").attr({'class':'fcat phenotyping'});
						
						var coreField = 'gene|'+ phenotypingStatusFq + '|';						
						var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + phenotypingStatusVal + '|' + count + '|phenotyping'});
						var flabel = $('<span></span>').attr({'class':'flabel'}).text(aPhenos[i]);
						var fcount = $('<span></span>').attr({'class':'fcount'}).text(count);
						
						liContainer.append(chkbox, flabel, fcount);						
						phenoUlContainer.append(liContainer);
					}					
				}
	    		phenoStatusSect.append(phenoUlContainer);
	    		$('div.flist li#gene > ul').append(phenoStatusSect);	    		    				    		
	    		
	    		// subfacet: IMPC mouse production status   
	    		var prodStatusSect = $("<li class='fcatsection production'></li>");		 
	    		prodStatusSect.append($('<span></span>').attr({'class':'flabel'}).text('IMPC Mouse Production Status'));
	    		
	    		var status_facets = json.facet_counts['facet_fields']['status'];
	    		foundMatch.production = status_facets.length;
	    		var status_count = {};
	    		for ( var i=0; i<status_facets.length; i+=2 ){ 
					var type = status_facets[i];
					var count = status_facets[i+1];
					status_count[type] = count; 
	    		}    			
	    		
	    		var prodUlContainer = $("<ul></ul>");
	    		
	    		// status ordered in hierarchy
				for ( var i=0; i<MPI2.searchAndFacetConfig.geneStatuses.length; i++ ){
					var status = MPI2.searchAndFacetConfig.geneStatuses[i];
					var count = status_count[MPI2.searchAndFacetConfig.geneStatuses[i]];					
					
					if ( count !== undefined ){
						var liContainer = $("<li></li>").attr({'class':'fcat production'});
						
						var coreField = 'gene|status|';
						var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + status + '|' + count + '|production'});
						
						liContainer.append(chkbox);
						liContainer.append($('<span class="flabel">' +status + '</span>'));
						liContainer.append($('<span class="fcount">' + count + '</span>'));
						prodUlContainer.append(liContainer);
						
					}									
				}	
				prodStatusSect.append(prodUlContainer);
				$('div.flist li#gene > ul').append(prodStatusSect);						
				
				// subfacet: IMPC mouse production/phenotyping centers
				var centers = {
						'productionCenter' : {'facet':'latest_production_centre', 'label':'IMPC Mouse Production Center'},
						'phenotypingCenter' : {'facet':'latest_phenotyping_centre', 'label':'IMPC Mouse Phenotype Center'}
						};
				for ( var c in centers ){
				
					var centerSect = $("<li class='fcatsection " + centers[c].facet + "'></li>");					
		    		centerSect.append($('<span></span>').attr({'class':'flabel'}).text(centers[c].label));
		    		
		    		var center_facets = json.facet_counts['facet_fields'][centers[c].facet];
		    		foundMatch[centers[c].facet] = center_facets.length;
		    				    		
		    		var centerUlContainer = $("<ul></ul>");
		    		
		    		for ( var i=0; i<center_facets.length; i+=2 ){ 
						var center = center_facets[i];
						var count = center_facets[i+1];
						if ( center != '' ){ // skip solr field which value is an empty string
							var liContainer = $("<li></li>").attr({'class':'fcat '+ centers[c].facet});
							var coreField = 'gene|'+centers[c].facet+'|';
							var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + center + '|' + count + '|'+centers[c].facet});
							
							liContainer.append(chkbox);
							liContainer.append($('<span class="flabel">' + center + '</span>'));
							liContainer.append($('<span class="fcount">' + count + '</span>'));
							centerUlContainer.append(liContainer);		
						}
		    		}  
		    		
					centerSect.append(centerUlContainer);	
					$('div.flist li#gene > ul').append(centerSect);			
				}				
				
				// subfacet: IMPC gene subtype	    			
	    		var subTypeSect = $("<li class='fcatsection marker_type'></li>");		 
	    		subTypeSect.append($('<span></span>').attr({'class':'flabel'}).text('Subtype'));
	    		
	    		var mkr_facets = json.facet_counts['facet_fields']['marker_type'];
	    		foundMatch.marker_type = mkr_facets.length;
	    		var unclassified = [];
	    		var subTypeUlContainer = $("<ul></ul>");
	    		
	    		for ( var i=0; i<mkr_facets.length; i+=2 ){		    			
	    			var liContainer = $("<li></li>").attr({'class':'fcat marker_type'});
					var type = mkr_facets[i];
					
					var count = mkr_facets[i+1];	
					var coreField = 'gene|marker_type|';						
					var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + type + '|' + count + '|marker_type'});					
					var flabel = $('<span></span>').attr({'class':'flabel'}).text(type);
					var fcount = $('<span></span>').attr({'class':'fcount'}).text(count);
					
					if ( type != 'unclassified gene' && type != 'unclassified non-coding RNA gene' ){						
						liContainer.append(chkbox, flabel, fcount);
					}
					else {					
						unclassified.push(liContainer.append(chkbox, flabel, fcount));
					}	
					subTypeUlContainer.append(liContainer);				
	    		} 
	    			    		  
	    		if (unclassified.length > 0){	    		
	    			for ( var i=0; i<unclassified.length; i++ ){
	    				subTypeUlContainer.append(unclassified[i]);
	    			}
	    		}	    		
	    		subTypeSect.append(subTypeUlContainer);
	    		$('div.flist li#gene > ul').append(subTypeSect);
	    			    			    		
	    		var selectorBase = "div.flist li#gene";
	    		// collapse all subfacet first, then open the first one that has matches 
				$(selectorBase + ' li.fcatsection').removeClass('open').addClass('grayout');	    		
	    		$.fn.addFacetOpenCollapseLogic(foundMatch, selectorBase);
	    			    		
	    		$.fn.initFacetToggles('gene');
	    		
	    		$('li#gene li.fcat input').click(function(){	    			
	    			// // highlight the item in facet	    			
	    			$(this).siblings('span.flabel').addClass('highlight');
					$.fn.composeSummaryFilters($(this), self.options.data.hashParams.q);
				});	    		
    		}
	    	
	    	/*--------------------------------------------------------------------------------------------------------------------------*/
	    	/* ------ when search page loads, the URL params are parsed to load dataTable and reconstruct filters, if applicable ------ */
	    	/*--------------------------------------------------------------------------------------------------------------------------*/	
	    	//console.log('****page load for gene facet');
	    	
	    	var oConf = self.options.data.hashParams;
	    	oConf.core = self.options.data.core;
	    	
	    	//console.log(oConf);
	    	
	    	$.fn.parseUrl_constructFilters_loadDataTable(oConf);
	    		
	    	
	    },	       
	  
	    destroy: function () {    	   
	    	//this.element.empty();
	    	// does not generate selector class    	    
    	    //$.Widget.prototype.destroy.call(this);  // if using jQuery UI 1.8.x    	    
    	    this._destroy();                          // if using jQuery UI 1.9.x
    	}  
    });
	
}(jQuery));	
	



