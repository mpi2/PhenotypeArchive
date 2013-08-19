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
 * imagesFacetWidget: makes SOLR call 
 * and displays the facet results on the left sidebar and dataTable on right.
 * 
 */
(function ($) {
	'use strict';
    $.widget('MPI2.imagesFacet', {
        
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
						
						solrSrchParams.facetCount = $(this).text();
						solrSrchParams.q = self.options.data.q;									
						
						var hashParams = $.fn.parseHashString(window.location.hash.substring(1));
						
						currHashParams.q = self.options.data.q;
						currHashParams.core = solrCoreName;
						currHashParams.fq = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
						
						// update hash
						if ( caller.find('table#imgFacet td.highlight').size() == 0 ){						
							window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);									
						}
						else {						
							if ( self.options.data.core != hashParams.coreName ){
								var fqText = caller.find('table#imgFacet td.highlight').siblings('td').find('a').attr('class');						
								currHashParams.fq = fqText;								
								window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);
								
								// reload dataTable							
								self._reloadDataTableForHashUrl(fqText);
							}							
						}			
												
						// dataTable code					
						//console.log('name: ' + MPI2.searchAndFacetConfig.facetParams[facetDivId].topLevelName);
						if ( $('table#'+ gridName).size() != 1 ){
							$.fn.invokeFacetDataTable(solrSrchParams, facetDivId, gridName, self.options.data.q);						
						}	
					}	
				}	
			});	
													
			// click on SUM facetCount to fetch results in grid
			//$('span.facetCount').click(function(){								
			caller.find('span.facetCount').click(function(){	
				if ( $(this).text() != '0' ){
					var gridName = MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName;
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
					var solrSrchParams = {}
					var hashParams = {};							
					
					// remove highlight from selected				
					$('table#imgFacet td').removeClass('highlight');
					solrSrchParams = $.extend({}, 
							MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams, 
							MPI2.searchAndFacetConfig.commonSolrParams);					
				
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
						$.fn.invokeFacetDataTable(solrSrchParams, facetDivId, gridName, self.options.data.q);							
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
  				'facet.sort': 'index',
  				'fl': 'annotationTermId,annotationTermName,expName,symbol',
  				//'fq': "annotationTermId:M* OR symbol_gene:*",  // images that have annotations only
  				'q.option': 'AND',				
  				'q': self.options.data.q 				
  				}, MPI2.searchAndFacetConfig.commonSolrParams);  	    	  	    	
  	    	
  	    	var paramStr = $.fn.stringifyJsonAsUrlParams(queryParams) 
  	    		+ "&facet.field=expName"
  	    		+ "&facet.field=higherLevelMaTermName"
  	    		+ "&facet.field=higherLevelMpTermName"
  	    		+ "&facet.field=subtype"
  	    		    	
  	    	$.ajax({	
  	    		'url': solrUrl + '/images/select',
  	    		'data': paramStr,   //queryParams,						
  	    		'dataType': 'jsonp',
  	    		'jsonp': 'json.wrf',	    		
  	    		'success': function(json) {	 
  	    			
      	    		$('div#imagesFacet span.facetCount').attr({title: 'total number of unique images'}).html(json.response.numFound);
  	    		
  	    			var table = $("<table id='imgFacet' class='facetTable'></table>");
  	    			
  	    			var aFacetFields = json.facet_counts.facet_fields; // eg. expName, symbol..
  	    			
  	    			var displayLabel = {
  	    								higherLevelMaTermName: 'Anatomy',
  	    								expName : 'Procedure',	    					            
  	    					            higherLevelMpTermName: 'Phenotype',
  	    					            subtype: 'Gene'
  	    								};	    			    			    			
  	    				    			
  	    			for ( var facetName in aFacetFields ){ 	    				
  	    				    				
  	    				for ( var i=0; i<aFacetFields[facetName].length; i+=2){    					  					
  	    					
  	    					var fieldName   = aFacetFields[facetName][i];
  	    					var facetCount  = aFacetFields[facetName][i+1];
  	    					var displayName = displayLabel[facetName];
  	    					//console.log(fieldName + ' : '+ facetCount);
  	    					
  	    					var tr = $('<tr></tr>').attr({'rel':fieldName, 'id':'topLevelImgTr'+i});
  	    					var td1 = $('<td></td>').attr({'class': 'imgExperiment', 'rel': facetCount}).text(fieldName);
  	    				
  	    					var imgBaseUrl = baseUrl + "/images?";
  	    					
  		    	    		var params = "q=" + self.options.data.q;
  		    	    		//params += "&fq=annotationTermId:M*&q.option=AND&qf=" + queryParams.qf + "&defType=edismax&wt=json&fq=" + facetName + ":";	
  		    	    		// here we take all images - ie, not filtering on having annotations or not
  		    	    		params += "&q.option=AND&qf=" + queryParams.qf + "&defType=edismax&wt=json&fq=" + facetName + ":";	
  		    	    		params += '"' + fieldName + '"';
  		    	    				    	    			  
  		    	    		var fqClass = facetName + ":" + '"' + fieldName + '"';
  		    	    		
  		    	    		var imgUrl = imgBaseUrl + params;	
  		    	    				    	    		
  		    	    		var infos = "{params:\"" + encodeURI(params) 
  		    	    		          + "\", fullLink:\"" +  encodeURI(imgUrl) 
  		    	    		          + "\",imgType:\"" + displayName 
  		    	    		         // + "\",facetParams:\"" + facetParams
  		    	    		          + "\",imgSubName:\"" + fieldName
  		    	    		          + "\", imgCount:\"" + facetCount
  		    	    		          + "\", solrCoreName:\"" + 'images' 
  		    	    	    		  +	"\", mode:\"" + 'imageGrid'
  		    	    		          + "\"}";		    	    		
  		    	    		   		
  		    	    		var a = $('<a></a>').attr({'rel':infos, 'class':fqClass}).text(facetCount);
  		    	    		var td2 = $('<td></td>').attr({'class': 'imgExperimentCount'}).append(a);
  		    	    		
  		    	    		if ( i == 0 ){
  	    						var catTr = $('<tr></tr>').attr({'class':'facetSubCat'});
  	    						var catLabel = displayLabel[facetName];
  	    						var catTd = $('<td></td>').attr({'colspan':2}).text(catLabel);
  	    						catTr.append(catTd);
  	    						table.append(catTr); 
  	    					}		    	    				    	    		
  		    	    		table.append(tr.append(td1, td2));		    	    		
  	    				}
  	    			}	    				    	    	
  	    			self._displayImageFacet(json, 'images', 'imagesFacet', table);			
  	    		}		
  	    	});	    	
  	    }, 
  	    	    
  	    _displayImageFacet: function(json, coreName, facetDivId, table){
  	    	var self = this;
  	    	
  	    	var solrBaseUrl = self.options.solrBaseURL_ebi + coreName + '/select';	    	
  	    	
  	    	if (json.response.numFound == 0 ){	    		
      			table = null;
      		}
  	    	else {
  	    		$('div#'+facetDivId+ ' .facetCatList').html(table);
  	    		table.find('td a').click(function(){	    			
  	    			// invoke filtered toplevel in dataTable
  	    			MPI2.searchAndFacetConfig.facetParams[facetDivId].showImgView = true; // default  	    			
  	    			$.fn.fetchFilteredDataTable($(this), facetDivId, self.options.data.q, 'facetFilter');
  	    		});	
  	    	}
  	    	
  	    	/*------------------------------------------------------------------------------------*/
	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	/*------------------------------------------------------------------------------------*/    		    
  	    	 	
  	    	if ( self.options.data.fq.match(/annotationTermId.+/)){
  	    		//console.log('UNfiltered images fq: ' + self.options.data.fq);
      			var solrSrchParams = $.extend({}, 
      					MPI2.searchAndFacetConfig.facetParams[facetDivId].filterParams, 
      					MPI2.searchAndFacetConfig.commonSolrParams);
      			
      			solrSrchParams.q = self.options.data.q;
      			solrSrchParams.coreName = 'images'; // to work out breadkCrumb facet display
      			solrSrchParams.facetCount = self.options.data.facetCount;
      			// for images, qf is either auto_suggest or text_search depending on query string
      			if ( solrSrchParams.q.indexOf('*') == -1 ){					
      				solrSrchParams.qf = 'text_search';
      			}					
  						
      			// load dataTable							
      			$.fn.invokeFacetDataTable(solrSrchParams, facetDivId, MPI2.searchAndFacetConfig.facetParams[facetDivId].gridName, true); 
      		}
  	    	else if ( self.options.data.fq.match(/expName.+|higherLevel.+|subtype.+/) ){
  	    		// imageView
  	    		//console.log('filtered images fq: ' + self.options.data.fq);
  	    		var obj = $('div#imagesFacet div.facetCatList').find("table#imgFacet a[class='" + self.options.data.fq + "']");
  	    		$.fn.fetchFilteredDataTable(obj, 'imagesFacet', self.options.data.q);
  	    	}       		
  	    	
  	    	// when last facet is done
  	    	$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch);
  	    },	
  	    
  	    _reloadDataTableForHashUrl: function(fqText){
	    	var self = this;	    	   	
	    	$.fn.fetchFilteredDataTable($('a[class="' + fqText + '"]'), 'imagesFacet', self.options.data.q);	    
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
	



