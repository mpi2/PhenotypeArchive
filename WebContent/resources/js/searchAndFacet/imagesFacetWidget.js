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
						
						var oHashParams = $.fn.parseHashString(window.location.hash.substring(1));
						
						// if no selected subfacet, load all results of this facet
						if ( caller.find('table#imagesFacetTbl td.highlight').size() == 0 ){							
							//window.location.hash = $.fn.stringifyJsonAsUrlParams(currHashParams);									
						}						
						else {
							// if there is selected subfacets: work out the url							
							if ( self.options.data.core != oHashParams.coreName ){															
							
								var fqFieldVals = {};
								
								caller.find('table#imagesFacetTbl td.highlight').each(function(){	
									var aVals = $(this).siblings('td').find('a').attr('class').split(":");
									var fqField = aVals[0];
									var val = aVals[1];
									
									if ( typeof fqFieldVals[fqField] === 'undefined' ){
										fqFieldVals[fqField] = [];										
									}									
									fqFieldVals[fqField].push(fqField + ':' + val);
								});					
								
								var fqStr = $.fn.compose_AndOrStr(fqFieldVals);
																
								// update hash tag so that we know there is hash change, which then triggers loadDataTable  
								if (self.options.data.q == '*:*'){
									window.location.hash = 'q=' + self.options.data.q + '&core=' +  solrCoreName + '&fq=' + fqStr + '&ftOpen=true';
								}
								else {
									window.location.hash = 'core=' +  solrCoreName + '&fq=' + fqStr;
								}
							}							
						}						
					}	
				}	
			});	
													
			// click on SUM facetCount to fetch results in grid
			//$('span.facetCount').click(function(){								
			caller.find('span.facetCount').click(function(){	
				if ( $(this).text() != '0' ){
					
					$.fn.setDefaultImgSwitcherConf();
										
					var solrCoreName = MPI2.searchAndFacetConfig.facetParams[facetDivId].solrCoreName;
					
					$.fn.removeFacetFilter(solrCoreName);
					
					// remove highlight from selected				
					$('table#imagesFacetTbl td').removeClass('highlight');
					
					var fqStr = MPI2.searchAndFacetConfig.facetParams[facetDivId].fq;
					console.log(fqStr);
					// update hash tag so that we know there is hash change, which then triggers loadDataTable  
					if (self.options.data.q == '*:*'){					
						window.location.hash = 'q=' + self.options.data.q + '&core=' +  solrCoreName + '&fq=' + fqStr;
					}
					else {
						window.location.hash = 'core=' +  solrCoreName + '&fq=' + fqStr;
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
  	    		+ "&facet.field=annotated_or_inferred_higherLevelMpTermName"
  	    		+ "&facet.field=annotated_or_inferred_higherLevelMaTermName"
  	    		+ "&facet.field=subtype"
  	    		    	
  	    	$.ajax({	
  	    		'url': solrUrl + '/images/select',
  	    		'data': paramStr,   //queryParams,						
  	    		'dataType': 'jsonp',
  	    		'jsonp': 'json.wrf',	    		
  	    		'success': function(json) {	 
  	    			
      	    		$('div#imagesFacet span.facetCount').attr({title: 'total number of unique images'}).html(json.response.numFound);
  	    		
  	    			var table = $("<table id='imagesFacetTbl' class='facetTable'></table>");
  	    			
  	    			var aFacetFields = json.facet_counts.facet_fields; // eg. expName, symbol..
  	    			
  	    			var aSubFacetNames = [];
  	    			
  	    			// do some sorting for facet names, but put Phenotype on front of list
  	    			for ( var facetName in aFacetFields ){ 	
  	    				if (facetName != 'annotated_or_inferred_higherLevelMpTermName' ){
  	    					aSubFacetNames.push(facetName);
  	    				}
  	    			}	
  	    			aSubFacetNames.sort();
  	    			aSubFacetNames.unshift('annotated_or_inferred_higherLevelMpTermName');
  	    			  	    			
  	    			var displayLabel = {
  	    					annotated_or_inferred_higherLevelMaTermName: 'Anatomy',
  	    								expName : 'Procedure',	    					            
  	    								annotated_or_inferred_higherLevelMpTermName: 'Phenotype',
  	    					            subtype: 'Gene'
  	    								};	    			    			    			
  	    				    			
  	    			//for ( var facetName in aFacetFields ){ 	   
  	    			for ( var n=0; n<aSubFacetNames.length; n++){
  	    				var facetName = aSubFacetNames[n];
  	    				
  	    				for ( var i=0; i<aFacetFields[facetName].length; i+=2){    					  					
  	    					
  	    					var fieldName   = aFacetFields[facetName][i];
  	    					var facetCount  = aFacetFields[facetName][i+1];  	    					
  	    					var catLabel    = displayLabel[facetName];
  	    					//console.log(fieldName + ' : '+ facetCount);
  	    					
  	    					var hiddenClass = facetName == 'annotated_or_inferred_higherLevelMpTermName' ? null : 'trHidden';  	    						
  	    					var tr = $('<tr></tr>').attr({'rel':fieldName, 'id':'topLevelImgTr'+i, 'class':'subFacet ' + hiddenClass + ' ' + facetName});
  	    					
  	    					//var tr = $('<tr></tr>').attr({'rel':fieldName, 'id':'topLevelImgTr'+i, 'class':'subFacet trHidden ' + facetName});
  	    					var displayName = facetName == 'annotated_or_inferred_higherLevelMpTermName' ? fieldName.replace(' phenotype', '') : fieldName;
  	    					var td1 = $('<td></td>').attr({'class': 'imgSubfacet', 'rel': facetCount}).text(displayName);
  	    				
  	    					var imgBaseUrl = baseUrl + "/images?";
  	    					
  		    	    		var params = "q=" + self.options.data.q;
  		    	    		//params += "&fq=annotationTermId:M*&q.option=AND&qf=" + queryParams.qf + "&defType=edismax&wt=json&fq=" + facetName + ":";	
  		    	    		// here we take all images - ie, not filtering on having annotations or not
  		    	    		params += "&q.option=AND&qf=" + queryParams.qf + "&defType=edismax&wt=json&fq=" + facetName + ":";	
  		    	    		params += '"' + fieldName + '"';
  		    	    				    	    			  
  		    	    		var fqClass = facetName + ":" + '"' + fieldName + '"';
  		    	    		
  		    	    		var imgUrl = imgBaseUrl + params;	
  		    	    		var catLabel2 = catLabel == 'Gene' ? 'gene subtype' : catLabel.toLowerCase();    	    		
  		    	    		var infos = "{params:\"" + encodeURI(params) 
  		    	    		          + "\", fullLink:\"" +  encodeURI(imgUrl) 
  		    	    		          + "\",imgType:\"" + catLabel2
  		    	    		         // + "\",facetParams:\"" + facetParams
  		    	    		          + "\",imgSubName:\"" + fieldName
  		    	    		          + "\", imgCount:\"" + facetCount
  		    	    		          + "\", solrCoreName:\"" + 'images' 
  		    	    	    		  +	"\", mode:\"" + 'imageGrid'
  		    	    		          + "\"}";		    	    		
  		    	    		   		
  		    	    		var a = $('<a></a>').attr({'rel':infos, 'class':fqClass}).text(facetCount);
  		    	    		var td2 = $('<td></td>').attr({'class': 'imgSubfacetCount'}).append(a);
  		    	    		
  		    	    		if ( i == 0 ){
  		    	    			
  	    						var catTr = $('<tr></tr>').attr({'class':'facetSubCat '+ facetName});  	
  	    						var collapeClass = (facetName == 'annotated_or_inferred_higherLevelMpTermName') ? 'unCollapse' : null;
  	    						
  	    						var catTd = $('<td></td>').attr({'colspan':3, 'class':collapeClass}).text(catLabel);
  	    						
  	    						catTr.append(catTd);
  	    						table.append(catTr); 
  	    					}	
  		    	    		
  		    	    		var coreField = 'images|'+ facetName + '|' + displayName + '|' + facetCount;	
  		        			var chkbox = $('<input></input>').attr({'type': 'checkbox', 'rel': coreField, 'class':facetName}); 		    	    			    		
  		        			var td0 = $('<td></td>').append(chkbox);
  		    	    		table.append(tr.append(td0, td1, td2));		    	    		
  	    				}
  	    			}	    				    	    	
  	    			self._displayImageFacet(json, 'images', 'imagesFacet', table);
  	    		
  	    			// update facet count when necessary
  	    			if ( $('ul#facetFilter li li a').size() != 0 ){
  	    				$.fn.fetchQueryResult(self.options.data.q, 'images');
  	    			}	
  	    			
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
  	    			
  	    			$.fn.setDefaultImgSwitcherConf();
  	    			
  	    			// uncheck all facet filter checkboxes 
        			$('table#imagesFacetTbl input').attr('checked', false);
        			
        			// remove all highlight
        			$('table#imagesFacetTbl td.imgSubfacet').removeClass('highlight');
        			
        			// also remove all filters for that facet container	
        			$.fn.removeFacetFilter('images');
        			
        			$(this).parent().parent().find('input').attr('checked', true);
        			$(this).parent().parent().find('td.imgSubfacet').addClass('highlight');
        			
        			$.fn.addFacetFilter($(this).parent().parent().find('input'), self.options.data.q);
  	    			        			
        			// update hash tag so that we know there is hash change, which then triggers loadDataTable
  	    			var oParams = eval( "(" + $(this).attr('rel') + ")" ); 			
  	    		 	    				    			  	    			
  	    			if (self.options.data.q == '*:*'){
  	    				window.location.hash = oParams.params + '&core=' + oParams.solrCoreName;
  	    			}
  	    			else {
  	    				window.location.hash = oParams.params.replace(/q=\b.*\b&/, '') + '&core=' + oParams.solrCoreName;
  	    			}  	    			
  	    		});	
  	    		
  	    		table.find('input').click(function(){	
  	    			
  	    			$.fn.setDefaultImgSwitcherConf();
  	    			
  	    			// highlight the item in facet
  	    			$(this).parent().parent().find('td.imgSubfacet').addClass('highlight');
  	    			$.fn.composeFacetFilterControl($(this), self.options.data.q);
  	    		});  
  	    		
  	    		// collapsable subfacet items
  	    		table.find('tr.facetSubCat').click(function(){
  	    		
  	    			var facetName = $(this).attr('class').replace('facetSubCat ',''); 
  	    			
  	    			if ( $(this).find('td').hasClass('unCollapse')){  	    			
  	    				// change arrow image
  	    				$(this).find('td').removeClass('unCollapse');
  	    				// hide all its members
	  	    			$(this).siblings('tr.'+facetName).addClass('trHidden');
  	    			}
  	    			else {  	    			
  	    				// refresh all to collapsed first
	  	    			//table.find('tr.subFacet').addClass('trHidden');
	  	    			//table.find('tr.facetSubCat td').removeClass('unCollapse'); 
  	    				
	  	    			// change arrow image and reveal all members of the clicked facet
	  	    			$(this).find('td').addClass('unCollapse');
	  	    			$(this).siblings('tr.'+facetName).removeClass('trHidden');
  	    			}
  	    		});
  	    		
  	    	}
  	    	
  	    	/*------------------------------------------------------------------------------------*/
	    	/* ------ when search page loads, the URL params are parsed to load dataTable  ------ */
	    	/*------------------------------------------------------------------------------------*/ 
  	    		
    		if ( self.options.data.fq.match(/.*/) ){ 	
    			$.fn.setDefaultImgSwitcherConf();  
    			//console.log('reload');
    			    			
    			var pageReload = true;  // this controls checking which subfacet to open (ie, show by priority)
    			var oHashParams = {};
	    		oHashParams.q = self.options.data.q;
	    		oHashParams.fq = self.options.data.fq;
	    		oHashParams.coreName = 'imagesFacet';
	    		$.fn.parseUrlForFacetCheckboxAndTermHighlight(oHashParams, pageReload);
	    		
	    		// now load dataTable    		
	    		$.fn.loadDataTable(oHashParams); 
    		}
  	    		
  	    	// when last facet is done
  	    	$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch);
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
	



