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
	    	    	
	    	var queryParams = $.extend({}, { 
				'rows': 0,
				'facet': 'on',								
				'facet.mincount': 1,
				'facet.limit': -1,							
				'facet.sort': 'count',					
	    		'q': self.options.data.hashParams.q},
	    		MPI2.searchAndFacetConfig.commonSolrParams,
	    		MPI2.searchAndFacetConfig.facetParams.geneFacet.filterParams
	    	);    	   	
	    	
	    	var queryParamStr = $.fn.stringifyJsonAsUrlParams(queryParams) 
	    		  + '&facet.field=marker_type'
				  + '&facet.field=status'
				  + '&facet.field=imits_phenotype_started' 
				  + '&facet.field=imits_phenotype_complete'
				  + '&facet.field=imits_phenotype_status';
				  //+ '&facet.field=production_center'
				  //+ '&facet.field=phenotyping_center'
	    	
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
	    	console.log(json);
	    	var self = this;
	    	var numFound = json.response.numFound;
	    	
	    	/*-------------------------------------------------------*/
	    	/* ------ displaying sidebar and update dataTable ------ */
	    	/*-------------------------------------------------------*/	    	  	
	    	var oSums = {'phenoStatus':0,'prodStatus':0,'geneSubType':0};
	    	
	    	if (numFound > 0){
	    		
	    		// subfacet: IMPC mouse phenotyping status	    		
	    		var phenoStatusSect = $("<li class='fcatsection phenotyping' + ></li>");		 
	    		phenoStatusSect.append($('<span></span>').attr({'class':'flabel'}).text('IMPC Phenotyping Status'));	    		
	    		
	    		var pheno_count = {};
	    		var aImitsPhenos = {'imits_phenotype_complete':'Complete', 
	    							'imits_phenotype_started':'Started', 
	    							'imits_phenotype_status':'Attempt Registered'};
	    		
	    		var phenoCount = 0;
	    		for (var key in aImitsPhenos ){	    			
	    			var phenoFieldList = json.facet_counts['facet_fields'][key];	
	    			
	    			if ( phenoFieldList.length != 0 ){
	    				phenoCount = 1;
	    				oSums.phenoStatus += phenoFieldList.length;
	    	    		for ( var j=0; j<phenoFieldList.length; j+=2 ){
	    	    			// skip status '0'	    	    			
	    	    			if ( phenoFieldList[j] == 'Phenotype Attempt Registered' || phenoFieldList[j] == 1 ){
	    						pheno_count[aImitsPhenos[key]] = phenoFieldList[j+1];
	    	    			}	    	    			
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
	    		
	    		// subfacet: IMPC mouse phenotyping centers
	    		/*table.append($('<tr></tr>').attr({'class':'facetSubCat phenoCenterTrCap'}).append($('<td></td>').attr({'colspan':3}).text('IMPC Phenotyping Center')));
	    		var phenoCenterFq = 'phenotyping_centersubFacetName';
	    		var phenoCenterList = json.facet_counts['facet_fields'][phenoCenterFq];	    			
    			if ( phenoCenterList.length != 0 ){    				
    	    		for ( var i=0; i<phenoCenterList.length; i+=2 ){
    	    			var center = phenoCenterList[i];
    	    			var count = phenoCenterList[i+1];
    	    			
    	    			var coreField = 'gene|'+ phenoCenterFq + '|';	
    	    			var chkbox = $('<input></input>').attr({'class':'phenoCenter', 'type': 'checkbox', 'rel': coreField + center + '|' + count});
						var td0 = $('<td></td>').append(chkbox);
						var tr = $('<tr></tr>').attr({'class':'subFacet phenoCenterTr'});						
						var td1 = $('<td></td>').attr({'class':'phenoCenter geneSubfacet', 'rel':count}).text(center);
						var link = $('<a></a>').attr({'rel': center, 'class': phenoCenterFq}).text(count);
						var td2 = $('<td></td>').attr({'class':'geneSubfacetCount', 'rel':center}).append(link);
						table.append(tr.append(td0, td1, td2));   	    			
    	    		}    	    		
    			}*/	    			    		
	    		
	    		// subfacet: IMPC mouse production status   
	    		var prodStatusSect = $("<li class='fcatsection production'></li>");		 
	    		prodStatusSect.append($('<span></span>').attr({'class':'flabel'}).text('IMPC Mouse Production Status'));
	    		
	    		var status_facets = json.facet_counts['facet_fields']['status'];
	    		oSums.prodStatus = status_facets.length;
	    		var status_count = {};
	    		for ( var i=0; i<status_facets.length; i+=2 ){ 
					var type = status_facets[i];
					var count = status_facets[i+1];
					status_count[type] = count; 
	    		}    			
	    		
	    		var prodUlContainer = $("<ul></ul>");
	    		
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
				
				// subfacet: IMPC mouse production centers
	    		/*table.append($('<tr></tr>').attr({'class':'facetSubCat prodCenterTrCap'}).append($('<td></td>').attr({'colspan':3}).text('IMPC Mouse Production Center')));
	    		var prodCenterFq = 'production_center';
	    		var prodCenterList = json.facet_counts['facet_fields'][prodCenterFq];	    			
    			if ( prodCenterList.length != 0 ){    				
    	    		for ( var i=0; i<prodCenterList.length; i+=2 ){
    	    			var center = prodCenterList[i];
    	    			var count = prodCenterList[i+1];
    	    			
    	    			var coreField = 'gene|'+ prodCenterFq + '|';	
    	    			var chkbox = $('<input></input>').attr({'class':'prodCenter', 'type': 'checkbox', 'rel': coreField + center + '|' + count});
						var td0 = $('<td></td>').append(chkbox);
			     

			var tr = $('<tr></tr>').attr({'class':'subFacet prodCenterTr'});						
						var td1 = $('<td></td>').attr({'class':'prodCenter ge#q=*:*&fq=(imits_phenotype_started:"1") AND (status:"Mice Produced")&facet=geneneSubfacet', 'rel':count}).text(center);
						var link = $('<a></a>').attr({'rel': center, 'class': prodCenterFq}).text(count);
						var td2 = $('<td></td>').attr({subFacetName'class':'geneSubfacetCount', 'rel':center}).append(link);
						table.append(tr.append(td0, td1, td2));   	    			
    	    		}    	    		
    			}*/
    			
				// subfacet: IMPC gene subtype	    			
	    		var subTypeSect = $("<li class='fcatsection marker_type'></li>");		 
	    		subTypeSect.append($('<span></span>').attr({'class':'flabel'}).text('Subtype'));
	    		
	    		var mkr_facets = json.facet_counts['facet_fields']['marker_type'];
	    		oSums.geneSubType = mkr_facets.length;
	    		var unclassified;
	    		var subTypeUlContainer = $("<ul></ul>");
	    		
	    		for ( var i=0; i<mkr_facets.length; i+=2 ){		    			
	    			var liContainer = $("<li></li>").attr({'class':'fcat marker_type'});
					var type = mkr_facets[i];
					var count = mkr_facets[i+1];	
					var coreField = 'gene|marker_type|';						
					var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField + type + '|' + count + '|marker_type'});					
					var flabel = $('<span></span>').attr({'class':'flabel'}).text(type);
					var fcount = $('<span></span>').attr({'class':'fcount'}).text(count);
					
					if ( type != 'unclassified gene' ){						
						liContainer.append(chkbox, flabel, fcount);
					}
					else {					
						unclassified = liContainer.append(chkbox, flabel, fcount);
					}	
					subTypeUlContainer.append(liContainer);				
	    		} 
	    			    		  
	    		if (unclassified){	    		
	    			subTypeUlContainer.append(unclassified);
	    		}	    		
	    		subTypeSect.append(subTypeUlContainer);
	    		
	    		// update all subfacet counts of this facet 
	    		$('div.flist li#gene > ul').append(phenoStatusSect, prodStatusSect, subTypeSect);	    		
	    		
	    		// phenoStatus subFacet is open by default	
	    		if ( phenoCount != 0 ){
	    			$('div.flist li#gene > ul li:nth-child(1)').addClass('open');
	    		}
	    		else {
	    			$('div.flist li#gene > ul li:nth-child(2)').addClass('open');
	    		}
	    		
	    		$.fn.initFacetToggles('gene');
	    		
	    		// when facet widget is open, flag it so that we know there are existing filters 
    			// that need to be checked and highlighted
    			$.fn.checkAndHighlightSubfacetTerms();	    		
	    		
	    		$('li#gene li.fcat input').click(function(){	    			
	    			// // highlight the item in facet	    			
	    			$(this).siblings('span.flabel').addClass('highlight');
					$.fn.composeFacetFilterControl($(this), self.options.data.hashParams.q);					
				});	    		
    		}
	    	
	    	/*------------------------------------------------------------------------------------*/
	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	/*------------------------------------------------------------------------------------*/	

	    	if ( self.options.data.hashParams.fq.match(/.*/) ){	
	    		$.fn.parseUrlFordTableAndFacetFiltering(self);	    		
    		}
	    },	       
	  
	    destroy: function () {    	   
	    	//this.element.empty();
	    	// does not generate selector class    	    
    	    //$.Widget.prototype.destroy.call(this);  // if using jQuery UI 1.8.x    	    
    	    this._destroy();                          // if using jQuery UI 1.9.x
    	}  
    });
	
}(jQuery));	
	



