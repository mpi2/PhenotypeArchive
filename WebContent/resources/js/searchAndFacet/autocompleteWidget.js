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
 * autocompleteWidget: controls the user input and do all the faceting accounting.
 * 
 */
(function ($) {
    'use strict';

    if(typeof(window.MPI2) === 'undefined') {
        window.MPI2 = {};
    }    
    MPI2.AutoComplete = {};    
	MPI2.AutoComplete.mapping = {};
	
    $.widget('MPI2.mpi2AutoComplete', $.ui.autocomplete, {	

    	options: {
    		source: function () {
				this.sourceCallback.apply(this, arguments);				
			},
			loadWaiting: "<img src='img/loading_small.gif' />",
			grouppingId : 'mgi_accession_id',
			geneSearchFields: ["marker_symbol", "mgi_accession_id", "marker_name", "marker_synonym"],
			commonQryParams: {					
	    			'qf': 'auto_suggest',
	    			'defType': 'edismax',
	    			'wt': 'json',	    		
	    			'rows': 4 // limit display in AC dropdown list for performance & practicality	    				    		
	    	},	
	    	queryParams_gene: {							
	    			//'group':'on',					
	    			//'group.field':'mgi_accession_id', 
	    			//'sort': 'marker_symbol desc',
	    			//'hl': 'on',
	    			//'hl.field': 'marker_name, marker_synonym',
	    			'fq': 'marker_type:* -marker_type:"heritable phenotypic marker"',	    			
	    			'fl':"marker_name,marker_synonym,marker_symbol,mgi_accession_id"},
			srcLabel : { // what appears to the user in the AC dropdown list, ie, how a term is prefixed for a particular solr field
					marker_symbol     : 'Gene Symbol',
					marker_name       : 'Gene Name',
					marker_synonym    : 'Gene Synonym',					
					mgi_accession_id  : 'MGI ID', 
					mp_id             : 'MP ID',
					mp_term           : 'MP Term', 
					mp_term_synonym   : 'MP Term Synonym',
					ma_id             : 'MA ID',
			 		ma_term           : 'MA Term', 
					ma_term_synonym   : 'MA Term Synonym',
					allele            : 'Allele Symbol',
					procedure_name    : 'Procedure Name',
					parameter_name    : 'Parameter Name',
					annotationTermName: 'Image Annotation',
					expName           : 'Image Experiment',
					symbol            : 'Image symbol'					
			},
			facetTypeParams : {	
					gene     : {type: 'gene', core: 'gene', fq: undefined},
					mp       : {type: "phenotype", core: 'mp', fq: "ontology_subset:*", fl: "mp_id,mp_term,mp_definition,top_level_mp_term"},
					pipeline : {type: "parameter", core: 'pipeline', fq: "pipeline_stable_id:IMPC_001"},
					images   : {type: "image", core: 'images', fq: "annotationTermId:M* OR expName:* OR symbol:*"}					
			},	
			fq: null, // default
			//solrBaseURL_bytemark: MPI2.searchAndFacetConfig.solrBaseURL_bytemark,			
			//solrBaseURL_ebi: MPI2.searchAndFacetConfig.solrBaseURL_ebi,
			mouseSelected: 0,	
            minLength: 1,
            homePage: false,           
            delay: 300,  
            doneSourceCall: 0,           
			facets: ['geneFacet', 'phenotypeFacet', 'tissueFacet', 'pipelineFacet', 'imageFacet'],			
            acList: [],            	  		       
			select: function(event, ui) {				
				//console.log(ui.item.value);   
				var thisWidget = $(this).data().mpi2AutoComplete; // this widget
				thisWidget.options.mouseSelected = 1;
				
				thisWidget._inputValMappingForCallBack(ui.item.value);
			},			
			close: function(event, ui){  // close dropdown list
	 			//nothing to do for now
	 		},
			focus: function(event, ui){ // when mouseover a term in dropdown list
				// display item under cursor when moving mouse 
				var self = $(this).data().mpi2AutoComplete;
				self.term = self.options.srchKeyWord; // ensures user input from last query is replaced by the latest 
				self.element.val(ui.item.value);
			}			
        },
        
		_inputValMappingForCallBack: function(input, srchBtn){
			
			var self = this;	
									
			var termVal = input.replace(/^(.+)\s(:)\s(.+)/, '$3');			
			var displayField = input.replace(/^(.+)\s(:)\s(.+)/, '$1');
			var solrField = input.replace(/^(.+)\s(:)\s(.+)/, '$1').replace(/ /g, '_').toLowerCase();	
			
			var solrQStr = input;
			var solrParams= null;
			var doSideBar = true;
			
			//console.log('input: '+ input + ' --- field: ' + solrField + ' --- qry str: ' + solrQStr);					
			
			if ( srchBtn ){			
				solrQStr = termVal;	
				if ( termVal == '*' ){					
					// change to search page
					window.location.href = baseUrl;					
				}				
			}
			else if ( MPI2.AutoComplete.mapping[termVal.toLowerCase()] && MPI2.AutoComplete.mapping[termVal.toLowerCase()].indexOf('MGI:') != -1 ){	
				// MGI id	
				doSideBar = false;
				var geneId = MPI2.AutoComplete.mapping[termVal.toLowerCase()];				
				solrQStr = self.options.grouppingId + ':"' + geneId.replace(/:/g,"\\:") + '"';	
				//console.log('MOUSE1: '+  ' -- ' + ' termVal: ' + termVal + ' : '+ geneId );
								
				// jump straight to gene page
				window.location.href = baseUrl + '/genes/' + geneId;	
				return;
			}	
			else if (input.indexOf(':') != -1 ) {
				//console.log('MOUSE2: '+  ' -- ' + ' termVal: ' + termVal);
				// user should have selected from list a term other than gene (Id/name/synonym)					
			
				// change to field names used in images index
				if ( solrField == 'image_annotation' ){
					solrField = 'annotationTermName';
				}	
				else if ( solrField == 'image_experiment'){
					solrField = 'expName';
				}
				else if ( solrField == 'image_symbol'){
					solrField = 'symbol';
				}
				else if ( solrField.indexOf('mp_') != -1 ){					
					// jump straight to mp page
					var mpId = MPI2.AutoComplete.mapping[termVal.toLowerCase()];
					window.location.href = baseUrl + '/phenotypes/' + mpId;	
					return;
				}				
				solrQStr = solrField + ':' + '"' + termVal + '"';										
			}	
						
			if ( srchBtn ){							
				var pathname = window.location.pathname;			
				
				if ( pathname != self.options.search_pathname ){				
					self.options.searchMode = 'gene'; //default				
					self._trigger("redirectedSearch", null, { q: solrQStr, core: self.options.searchMode, 
						fq: self.options.facetTypeParams[self.options.searchMode].fq });
				}				
			}
						
			window.location.hash = 'q=' + solrQStr + "&core=" + self.options.searchMode 
		                     + '&fq=' + self.options.facetTypeParams[self.options.searchMode].fq;			
					
			$('div#userKeyword').html('Search keyword: ' + input);
					
			//console.log('Gene: '+ self.options.geneFound + ' - mp: '+ self.options.mpFound + ' - pipeline: '+ self.options.pipelineFound + ' - img: '+ self.options.imagesFound);
			if ( !self.options.homePage && doSideBar ){	
				self._trigger("loadSideBar", null, { q: solrQStr, core: self.options.searchMode, fq: self.options.fq });
			}
			else {
				$('div#facetBrowser').html(MPI2.searchAndFacetConfig.spinner);
			}
		},
		
		_addHitEnterBeforeDropDownListOpensEvent: function(){
			var self = this;			
			var suppressKeyPress;
			this.element.bind( "keydown.autocomplete", function( event ) {
				
				suppressKeyPress = false;
				var keyCode = $.ui.keyCode;
				//alert('keycode: ' + keyCode);
				switch( event.keyCode ) {				
				case keyCode.ENTER:
				case keyCode.NUMPAD_ENTER:
					// when user hits ENTER before menu is open					
					if ( !self.menu.active ) {
						var term = self.element.val();
						if ( term == '' ){		            	
							self.term = '*';
		            	}						
						self.options.hitEnterBeforeDropDownListOpensVal = 1;
						self.term = term;						
					}					
				}
			});
		},
				
        _create : function () {        	    	
        	
            var self = this;  
            self.element.val(self._showSearchMsg());                    
            self._addHitEnterBeforeDropDownListOpensEvent(); 
            	
            self.element.bind('keyup', function(e) {
            //self.element.keypress(function(e) { 
            	// remembers what user has just typed
            	self.options.srchKeyWord = self.element.val();
            	
            	// when input text becomes empty string (ie, due to deletion)
            	/*if ( self.element.val() == '' ){
            		            		
            		for( var i=0; i<facetDivs.length; i++ ){
            			$('div#' + facetDivs[i] + ' span.facetCount').text(''); 					 
						$('div#' + facetDivs[i] + ' div.facetCatList').html('');
					}           			
            	} */ 
            	            	            	
            	if ( e.which == 13) {  // catches IE     	
            		self.close();   	
            		
            		$('div#facetBrowser').html(MPI2.searchAndFacetConfig.spinner);
            		
            		// need to distinguish between enter on the input box and enter on the drop down list
            		// ie, users use keyboard, instead of mouse, to navigate the list and hit enter to choose a term
            		if (self.options.mouseSelected == 0 ){                    	
            			// use the value in the input box for query 
                    			
            			if (self.options.hitEnterBeforeDropDownListOpensVal == 1){
            				//console.log('hitEnterBeforeDropDownListOpens');	
            				// sourceCallback() is automatically called when dropdown list is open
            				// so we need to call it now to simulate dropdown list open          				
            			        				
            				self.sourceCallback(self); // ajax!!                    		
            			}  
            		}
            		else {            		
            			$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch);
            		}
            	}            	
            }); 
           
            // if search is not coming from redirected page, data is not defined
            // and we want to loading data in sidebar           
            if ( typeof data == 'undefined' ){  
        		//$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch);
               	self.sourceCallback(self);      
        	} 
                        
            $('button#acSearch').click(function(){ 
            	//console.log('check input: ' + self.element.val());
            	if ( self.term === undefined || self.element.val() == self._showSearchMsg() ){            	
            		self.term = "*";
            	}
            	//console.log('check query: ' + self.term);
            	self.element.val(self._showSearchMsg());
				self._inputValMappingForCallBack(self.term, true); 
            });

            var facetDivs = self.options.facets;			

            // refresh facet counts and facet tables
            self.element.click(function(){
            	self.term = undefined;            	      	
            }); 
            
            self.element.mouseover(function(){            	
            	// display what user has just typed
            	// self.options.srchKeyWord is to override last search (which is default)
            	self.term = self.options.srchKeyWord;
            });
            
            $.ui.autocomplete.prototype._create.apply(this);			
        },   

        _showSearchMsg: function(){			
			//return 'Search genes, SOP, MP, images by MGI ID, gene symbol, synonym or name';
			return ''; // in case users want to have them back
		},                     
        
       /* _setOption: function (key, value) {
            switch(key) {
            case 'solrBaseURL_bytemark':
                this.options.solrBaseURL_bytemark = value;
                break;
            }
            $.ui.autocomplete.prototype._setOption.apply(this, arguments);
        },*/
                
		// loop thru each item in the list
        // and highlight the match string
		_renderItem: function( ul, item ) { 
			//self.options.dropdownList
 			// highlight the matching characters in string 		
 		 	var term = this.term.split(' ').join('|'); 		 	
 		 	var sep = ' : ';
 		 	var vals = item.label.split(sep); 		 
 		 	
 			var qStr = term.replace(/\*$/g, ""); // need to remove wildcard at end first so that regex will work
 		 	//qStr = qStr.replace(/\*/g, "\\w+");
 			qStr = qStr.replace(/\(/g, "\\(");
 			qStr = qStr.replace(/\)/g, "\\)"); 			
 			 			
 			var re = new RegExp("(" + qStr + ")", "gi") ;
 			//var t = item.label.replace(re,"<b>$1</b>");
 			var t = vals[1].replace(re,"<b>$1</b>");
 			
 			if ( t.indexOf("<b>") > -1 ){
 				return $( "<li></li>" )
 		    		.data( "item.autocomplete", item )
 		    		.append( "<a>" + vals[0] + sep + t + "</a>" )
 		    		.appendTo( ul ); 			 				
 			} 	
		},
		
		_parseJson: function(json, sQuery, sDataType, sDivId, aFields){			
			var self = this;
			var matchesFound = json.response.numFound;
			//console.log('FOUND: ' + sDataType + ' found: ' + matchesFound);			
						
			self.options[sDataType + 'Found'] = matchesFound;
		
			//$('div#' + sDivId + ' span.facetCount').text(matchesFound);
			$('div#' + sDivId + ' .facetCatList').html(''); 
									
			var list = [];
			var docs = json.response.docs;
			for ( var d=0; d<docs.length; d++ ){	
				for( var f=0; f<aFields.length; f++){
					var fld = aFields[f];
					var val = docs[d][fld];
					//console.log('field: ' + fld + ' for ' + val);
					
					if ( fld != 'annotationTermName' && fld != 'expName' && fld != 'symbol' ){
						
						if ( (fld == 'mp_term_synonym' || fld == 'ma_term_synonym' 
							  || fld == 'annotationTermName' || fld == 'symbol' ) && val ){
							var aVals = docs[d][fld];
													
							for ( var v=0; v<aVals.length; v++ ){							
								var thisVal = aVals[v];
								
								if ( fld == 'mp_term_synonym'  ){
									MPI2.AutoComplete.mapping[thisVal.toLowerCase()] = docs[d]['mp_id'];
								}
								
								if ( thisVal.toLowerCase().indexOf(sQuery) != -1 || sQuery.indexOf('*') != -1 ){
									list.push(self.options.srcLabel[fld] + ' : ' + thisVal);
								}	
							}						
						}								
						else if ( val ){
							//console.log(typeof val + ' -- '+  val.toString());
							if ( typeof val == 'object' ){
								val = val.toString();
							} 
							if ( fld == 'mp_term'  ){
								MPI2.AutoComplete.mapping[val.toLowerCase()] = docs[d]['mp_id'];
							}
							if ( val.toLowerCase().indexOf(sQuery) != -1 || sQuery.indexOf('*') != -1 ){				
								list.push(self.options.srcLabel[fld] + ' : ' + val);
							}
						}	
					}
				}
			}
					
			self.options.acList = self.options.acList.concat(self._getUnique(list));	
		},
		
		_parseGeneGroupedJson: function (json, sQuery, sDataType, sDivId, aFields) {
		
			var self = this;
			
			var matchesFound = json.response.numFound;
			//console.log('FOUND: ' + sDataType + ' found: ' + matchesFound);			
						
			self.options[sDataType + 'Found'] = matchesFound;
		
			//$('div#' + sDivId + ' span.facetCount').text(matchesFound);
			$('div#' + sDivId + ' .facetCatList').html(''); 
									
			var list = [];
			var docs = json.response.docs;
			
			for ( var d=0; d<docs.length; d++ ){	
				for( var f=0; f<aFields.length; f++){
					var fld = aFields[f];
					var val = docs[d][fld];
					//console.log('0: field: ' + fld + ' for ' + val);
					var geneId = docs[d]['mgi_accession_id'];	
					if ( fld == 'marker_name' || fld == 'marker_synonym' ){//} || fld == 'mp_id' || fld == 'mp_term' || fld == 'mp_term_s ynonym' ){
						var aVals = docs[d][fld];
						if ( aVals ){
							for ( var v=0; v<aVals.length; v++ ){						
								var thisVal = aVals[v];
								
								//alert(thisVal + ': '+ typeof thisVal);
								// only want indexed terms that have string match to query keyword
								
								if ( thisVal.toLowerCase().indexOf(sQuery) != -1 || sQuery.indexOf('*') != -1 ){    							
									
									if ( fld == 'marker_name' || fld == 'marker_synonym' ){
										
										MPI2.AutoComplete.mapping[thisVal.toLowerCase()] = geneId;        									
									} 
									list.push(self.options.srcLabel[fld] + " : " +  thisVal);
								}							
							}
						}
					}
					else {        						
						if ( val.toLowerCase().indexOf(sQuery) != -1 || sQuery.indexOf('*') != -1 ){        						
							//console.log('2: '+ fld + ' : ' + val + ' id: ' + geneId);
							MPI2.AutoComplete.mapping[val.toLowerCase()] = geneId;        										
							list.push(self.options.srcLabel[fld] + " : " +  val);
						}	
					}
				}
				
			}
			
           	self.options.acList = self._getUnique(list);             			
        },	
        
        _getUnique: function (list) {
        	var u = {}, a = [];
        	for(var i = 0, l = list.length; i < l; ++i){
        		if(list[i] in u){
        			continue;
        		}
        		a.push(list[i]);
        		u[list[i]] = 1;
        	}
        	return a;
        },	
        
		_setSearchMode: function(oCounts){
			var self = this;			
			//console.log('check counts: ' );
			//console.log(oCounts);
			
			// priority order of facet to be opened based on search result
			if ( oCounts.geneFound != 0 ){
				return 'gene';
			}			
			else if ( oCounts.mpFound != 0){				
				return 'mp';			
			}    		
    		else if ( oCounts.pipelineFound != 0 ){    			
    			return 'pipeline';						
			}	
    		else if ( oCounts.imagesFound != 0 ){    			
    			return 'images';						
			}
    		else {
    			return 'gene'; // default
    		}
		},	

        sourceCallback: function (request, response) {
        	var self = this; 
        	
        	if ( self.options.doneSourceCall ){  
        		//console.log('checkpoint');
        		self._doCallBacks();
        	}
        	        	
        	self.options.mouseSelected = 0; // important to distinguish between mouse select and keyborad select
                    	
        	var q;        	      		
        	if ( request.term === undefined ){
        		q = '*';
        	}
        	else {
        		q = request.term.replace(/^\s+|\s+$/g, ""); // trim away leading/trailing spaces        	
        		q = q.toLowerCase(); // so that capitalized search would work as solr analyzer uses only lowercase
        	}	
        	if ( q == '*' ){ 
        		q = '*:*'; // when user types *              
        	}	        		
        				
 	    	self.options.queryParams_gene.q = q; 
 	    	
 	    	var homepage = location.href.match(/org\/$/);
 	    	if (homepage !== null ){
 	    		//   alert('home page');
 	    		self.options.homePage = true;                        
 	    	}

 	    	
 	    	if ( location.href.indexOf('/search?') == -1 ) { 	    	
 	    		var queryParams = $.extend({},
 	    			self.options.commonQryParams,
 	    			MPI2.searchAndFacetConfig.facetParams.geneFacet.params, 
 	    			{'q': q}); 
 	    		 	    		 	    		
 	    		// facet types are done sequencially; starting from gene
 	    		
	        	$.ajax({            	    
	        			url: self.options.solrBaseURL_bytemark + 'gene/select',
	            	    data: queryParams,
	            	    dataType: 'jsonp',
	            	    jsonp: 'json.wrf',
	            	    timeout: 5000,
	            	    success: function (geneSolrResponse) {
	            	    	self._doPipelineAutoSuggest(geneSolrResponse, q, response);	            	    
	            	    },
	            	    error: function (jqXHR, textStatus, errorThrown) {
	            	        //response('AJAX error');            	        
	            	        $('div#facetBrowser').html('Error fetching data ...');
	            	    }            	
	        	});
 	    	}
 	    	else {
 	    		
 	    		// from redirect, so skip faceting 
 	    		self.element.val(self._showSearchMsg());     
 	    		$('div#leftSideBar').parent().parent().html('');
 	    		
 	    		var urlParams = $.fn.parseUrlString(location.href);
    			
    			self.term = urlParams.q;
    			self.options.searchMode = urlParams.core;    					
    			
    			// replace url with hash and reload to convert redirected GET page into hash state
    			document.location.href = 'search' + '#q=' + urlParams.q + '&core=' + urlParams.core + '&fq=' + urlParams.fq; 
 	    	}
    	},
    	
    	_checkSingletonForRedirect: function(json, query){
            var self = this;
            var g = json.grouped[self.options.grouppingId];

            if (g.matches == 1){
                    self._parseGeneGroupedJson(json, query);                    
                    return MPI2.AutoComplete.mapping[query.toLowerCase()];
            }
    	},

    	_doPipelineAutoSuggest: function(geneSolrResponse, q, response){
    		
    		var self = this;
    		var queryParams = $.extend({},{    				
    			'fq': 'pipeline_stable_id=IMPC_001', 
    			'q': q}, self.options.commonQryParams);   		
    		    		
    		//console.log(queryParams);
    		$.ajax({
        	    url: self.options.solrBaseURL_ebi + 'pipeline/select',
        	    data: queryParams,
        	    dataType: 'jsonp',
        	    jsonp: 'json.wrf',
        	    timeout: 5000,
        	    success: function (sopSolrResponse) {
        	    	self._doTissueAutoSuggest(geneSolrResponse, sopSolrResponse, q, response); 
        	    },
    			error: function (jqXHR, textStatus, errorThrown) {
    				//response('AJAX error');            	        
    				$('div#facetBrowser').html('Error fetching data ...');
    			} 
    		});
    	},	
    	
    	_doTissueAutoSuggest: function(geneSolrResponse, sopSolrResponse, q, response){
			
    		var self = this;
    		var queryParams = {    			   			    			
    			'qf': 'auto_suggest',
    			'defType': 'edismax',
    			'wt': 'json',
    			'rows': 4,
    			'q': q
    		};
    		$.ajax({
        	    url: self.options.solrBaseURL_ebi + 'ma/select',
        	    data: queryParams,
        	    dataType: 'jsonp',
        	    jsonp: 'json.wrf',
        	    timeout: 10000,
        	    success: function (maSolrResponse) {
        	    	self._doImageAutosuggest(geneSolrResponse, sopSolrResponse, maSolrResponse, q, response); 
        	    },
    			error: function (jqXHR, textStatus, errorThrown) {
    				//response('AJAX error');            	        
    				$('div#facetBrowser').html('Error fetching data ...');
    			} 
    		});
    	},
    	
    	_doImageAutosuggest: function(geneSolrResponse, sopSolrResponse, maSolrResponse, q, response){
    		var self = this;
    		var queryParams = {    			   			    			
    			'qf': 'auto_suggest',
    			'defType': 'edismax',
    			'wt': 'json',
    			'rows': 4,    			
    			//'fq' : "annotationTermId:M* OR expName=* OR symbol:*", 
    			'fl' : 'higherLevelMaTermName,higherLevelMpTermName,annotationTermId,annotationTermName,expName,symbol',
    			'q': q    			
    		};
    		
    		// if users do not search with wildcard, we need to search by exact match
    		/*if (queryParams.q.indexOf(" ") != -1 ){
	    		queryParams.qf = 'auto_suggest';	    		
	    	}  
	    	else if ( queryParams.q.indexOf('*') == -1 ){	    	
	    		queryParams.qf = 'text_search';	    		
	    	}*/
    		
    		$.ajax({
        	    url: self.options.solrBaseURL_ebi + 'images/select',
        	    data: queryParams,
        	    dataType: 'jsonp',
        	    jsonp: 'json.wrf',
        	    timeout: 10000,
        	    success: function (imgSolrResponse) {        	    	
        	    	self._doMPAutoSuggest(geneSolrResponse, sopSolrResponse, maSolrResponse, imgSolrResponse, q, response); 
        	    },
    			error: function (jqXHR, textStatus, errorThrown) {
    				//response('AJAX error');            	        
    				$('div#facetBrowser').html('Error fetching data ...');
    			} 
    		});
    	},
    	
		_doMPAutoSuggest: function(geneSolrResponse, sopSolrResponse, maSolrResponse, imgSolrResponse, q, response){
    		
    		var self = this;
    		var queryParams = {			
    	    	    // if using jQuery UI 1.8.x
    			'qf': 'auto_suggest',
    			'defType': 'edismax',
    			'fq': 'ontology_subset:*',
    			'wt': 'json',    			
    			'rows': 4,    			
    			'q': q
    		};
    		    		
    		$.ajax({
        	    url: self.options.solrBaseURL_ebi + 'mp/select',
        	    data: queryParams,
        	    dataType: 'jsonp',
        	    jsonp: 'json.wrf',
        	    timeout: 10000,
        	    success: function (mpSolrResponse) {
        	    	        	    	
        	    	q = q.replace(/\*$/g, ""); // need to remove trailing * 
        	    	
        	    	// all JSONs from each solr query are parsed in one go here
        	    	$('div#geneFacet span.facetCount').html(self.options.loadWaiting);
        	    	$('div#mpFacet span.facetCount').html(self.options.loadWaiting);
        	    	$('div#pipelineFacet span.facetCount').html(self.options.loadWaiting);
        	    	$('div#imagesFacet span.facetCount').html(self.options.loadWaiting);
        	    	
        	    	//self._parseGeneGroupedJson(geneSolrResponse, q);  
        	    	self._parseGeneGroupedJson(geneSolrResponse, q, 'gene', 'geneFacet', self.options.geneSearchFields);
        	    	self._parseJson(sopSolrResponse, q, 'pipeline', 'pipelineFacet', ['parameter_name', 'procedure_name']);
        	    	self._parseJson(mpSolrResponse, q, 'mp', 'mpFacet', ['mp_id', 'mp_term', 'mp_term_synonym']);
        	    	self._parseJson(imgSolrResponse, q, 'images', 'imagesFacet', ['annotationTermName', 'expName', 'symbol']);        	    	
        	    	// hide for now
        	    	//self._parseJson(maSolrResponse, q, 'ma', 'tissueFacet', ['ma_id', 'ma_term', 'ma_term_synonym']);
        	    	
        	    	/*console.log('geneFound: ' + self.options.geneFound + 
        	    			    ' - mpFound: ' + self.options.mpFound + 
        	    			    ' - maFound: ' + self.options.maFound + 
        	    			    ' - pipelineFound: ' + self.options.pipelineFound + 
        	    			    ' - imagesFound: ' + self.options.imagesFound
        	    				);*/
        	    	self.options.searchMode = self._setSearchMode({	geneFound: self.options.geneFound, 
        	    													mpFound: self.options.mpFound, 
        	    													pipelineFound: self.options.pipelineFound,
        	    													imagesFound: self.options.imagesFound
        	    													});        	    	       	    	
        	    	
        	    	self.options.doneSourceCall = 1;
        	    	
        	    	//console.log('doneSouceCall: '+ self.options.doneSourceCall);
        	    	if ( response ){        	    		
        	    		// response is defined only after dropdown list is open
        	    		// all other key events do not trigger opening dropdown list
        	    		//response(self.options.acList);        	    		
        	    		response(self.options.acList.slice(0,4)); // return only first 4 terms in the list for now
        	    	}
        	    	       	    	
        	    	self._doCallBacks();         	    	
        	    },
        	    error: function (jqXHR, textStatus, errorThrown) {
    				//response('AJAX error');            	        
    				$('div#facetBrowser').html('Error fetching data ...');
    			}        	    
    		});    			
    	},    	
    	    
    	_isSingleton: function(){
    		var self = this;
    		var sum = 0;
    		for (var i=0; i< MPI2.searchAndFacetConfig.cores.length; i++ ){  
    			//console.log(self.options[MPI2.searchAndFacetConfig.cores[i]+'Found']);
    			sum += self.options[MPI2.searchAndFacetConfig.cores[i]+'Found'];
    		}
    		return sum == 1 ? true : false;
    	},
    	
    	_doCallBacks: function(){
    		    		
    		var self = this;
    		self.options.doneSourceCall = 0; // refresh
    		
    		if ( self.term === undefined || self.term == '' ){
    			self.term = '*:*';
    		}    		
    		    		
    		var params = self.options.facetTypeParams[self.options.searchMode];    		    		
    		params.q = self.term;  
    		
    		$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch);    		
    		// only Enter event will fire and not other keyup/down events

    		if ( (window.location.pathname != self.options.search_pathname && self.options.hitEnterBeforeDropDownListOpensVal == 1) ){ 
    			//console.log('1: redirect chk hash: ' + window.location.hash); 
    			// when users hit enter on inpubox and the result returns only 1 result
        		// go straight to mp/gene page
    			/*if ( (self.options.searchMode == 'gene' || self.options.searchMode == 'mp') && self._isSingleton() ){ 
        			var acc = MPI2.AutoComplete.mapping[self.term.toLowerCase()];        			
        			window.location.href = baseUrl + '/' + MPI2.searchAndFacetConfig.restfulPrefix[self.options.searchMode] + '/' +  acc;        		
        		}*/
    			//else {
    				self._trigger("redirectedSearch", null, { q: self.term, core: self.options.searchMode, 
    					fq: MPI2.searchAndFacetConfig.facetParams[self.options.searchMode+'Facet'].fq });    				
    			//}
    		}
    		else if ( self.options.hitEnterBeforeDropDownListOpensVal== 1){  
    			
    			// when users hit enter on inpubox and the result returns only 1 result
        		// go straight to mp/gene page
        		//console.log('2: '+ self.options.searchMode);
        		/*if ( (self.options.searchMode == 'gene' || self.options.searchMode == 'mp') && self._isSingleton() ){ 
        			var acc = MPI2.AutoComplete.mapping[self.term.toLowerCase()];
        			window.location.href = baseUrl + '/' + MPI2.searchAndFacetConfig.restfulPrefix[self.options.searchMode] + '/' +  acc; 
        			return;
        		}*/
        		//else {    			
        			//console.log('fq check: ' + MPI2.searchAndFacetConfig.facetParams[self.options.searchMode+'Facet'].fq);
        			window.location.hash = 'q=' + self.term + '&core=' + self.options.searchMode 
        			+ '&fq=' + MPI2.searchAndFacetConfig.facetParams[self.options.searchMode+'Facet'].fq;        			
        		//}
    		} 		
    		   		    
    		//console.log('check url: '+ window.location.href);
    		
    		if ( window.location.hash != '' ){
    			// search page with hash state in url
    			//console.log('hash: '+ window.location.hash);
    			
    			var coreName;
    			var hashParams = {};
    			if ( window.location.hash == '#' ){
    				// take care of IE
    				coreName = 'gene';
    				hashParams.q = self.term;
    			}
    			else {
    				//console.log(window.location.hash.substring(1));
    				hashParams = $.fn.parseHashString(window.location.hash.substring(1));    			
    				coreName = hashParams.coreName;    			
    			}
    			params = MPI2.searchAndFacetConfig.facetParams[coreName+'Facet'].params;
    			    			
    			self.term = hashParams.q;
    			params.q = self.term;
    			params.core = coreName;
    			
    			$('div#userKeyword').html('Search keyword: ' + self.term);	
    			    			
    			self.options.fq = hashParams.fq;		    			
    			self.options.searchMode = coreName;    	
    			self.options.doDataTable = false;
    			//console.log('TEST: '+ hashParams.fq);
    		}    		
    	        	
    		if ( !self.options.homePage ){   			
    			// when loadSideBar is done, dataTable will be loaded based on search result    		
    			self._trigger("loadSideBar", null, {    						
    			        q: self.term, core: self.options.searchMode, fq: self.options.fq,
    			        qf: MPI2.searchAndFacetConfig.facetParams[self.options.searchMode+'Facet'].qf
    			});				
    		}
    		
    	},	
    	
    	destroy: function () {
    	    this.element.removeClass('ui-autocomplete-input');

    	    // if using jQuery UI 1.8.x
    	    $.Widget.prototype.destroy.call(this);
    	    // if using jQuery UI 1.9.x
    	    //this._destroy();
    	}
    	
    });    
}(jQuery));