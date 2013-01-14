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
			searchFields: ["marker_symbol", "mgi_accession_id_key", "marker_name", "synonym", "marker_synonym", "allele_synonym"],
			commonQryParams: {					
	    			'qf': 'auto_suggest',
	    			'defType': 'edismax',
	    			'wt': 'json',
	    			'start': 0,
	    			'rows': 4 // limit display in AC dropdown list for performance & practicality	    				    		
	    	},	
	    	queryParams_gene: {							
	    			'group':'on',					
	    			'group.field':'mgi_accession_id', 
	    			'sort': 'marker_symbol desc',
	    			'hl': 'on',
	    			'hl.field': 'marker_name,synonym',
	    			'fq': 'marker_type_str:* -marker_type_str:"heritable phenotypic marker"', 
	    			'fl':"marker_name,synonym,marker_symbol,mgi_accession_id,mp_id,mp_term,mp_term_synonym,allele"},			
			srcLabel : { // what appears to the user in the AC dropdown list, ie, how a term is prefixed for a particular solr field
					marker_symbol     : 'Gene Symbol',
					marker_name       : 'Gene Name',
					marker_synonym    : 'Gene Synonym',
					synonym           : 'Gene Synonym',
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
					gene     : {type: 'gene'},
					mp       : {type: "phenotype", fq: "ontology_subset:*", fl: "mp_id,mp_term,mp_definition,top_level_mp_term"},
					pipeline : {type: "parameter", fq: "pipeline_stable_id:IMPC_001"},
					images   : {type: "image", fq: "annotationTermId:M* OR symbol:* OR expName:*"}					
			},		 						
			//solrBaseURL_bytemark:'http://ikmc.vm.bytemark.co.uk:8983/solr/', // working
			solrBaseURL_bytemark: drupalBaseUrl + '/bytemark/solr/',
			//solrBaseURL_bytemark:'https://beta.mousephenotype.org/mi/solr/', // not working
			solrBaseURL_ebi: drupalBaseUrl + '/mi/solr/', // working
			mouseSelected: 0,	
            minLength: 1,
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
			focus: function(){ // when mouseover a term in dropdown list				
				//nothing to do for now					
			}			
        },
        
		_inputValMappingForCallBack: function(input, srchBtn){
			
			var self = this;			
			$('div#facetBrowser').html(MPI2.searchAndFacetConfig.spinner);
			
			var termVal = input.replace(/^(.+)\s(:)\s(.+)/, '$3');
			//console.log("input: "+ termVal);
			var displayField = input.replace(/^(.+)\s(:)\s(.+)/, '$1');
			var solrField = input.replace(/^(.+)\s(:)\s(.+)/, '$1').replace(/ /g, '_').toLowerCase();	
			
			var solrQStr = input;
			var solrParams= null;
			
			//console.log('input: '+ input + ' --- field: ' + solrField + ' --- qry str: ' + solrQStr);
			
			var geneFound;
			
			if ( srchBtn ){			
				solrQStr = termVal;				
			}
			else if ( MPI2.AutoComplete.mapping[termVal] ){	
				// MGI id	
				geneFound = 1;				
				
				var geneId = MPI2.AutoComplete.mapping[termVal];				console.log(MPI2.searchAndFacetConfig.spinner);     	
				solrQStr = self.options.grouppingId + ':"' + geneId.replace(/:/g,"\\:") + '"';				
				//solrParams = self._makeSolrURLParams(solrQStr);				
				//console.log('MOUSE1: '+  ' -- ' + ' termVal: ' + termVal);	
			}	
			else if (input.indexOf(':') != -1 ) {
				
				// user should have selected from list a term other than gene (Id/name/synonym)					
				geneFound = 0;
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
								
				solrQStr = solrField + ':' + '"' + termVal + '"';
				//solrParams = self._makeSolrURLParams(solrQStr);							
				
				//console.log('MOUSE2 : '+ solrQStr);	
			}	
						
			var pathname = window.location.pathname;						
			if ( pathname != self.options.search_pathname ){	
				
				self._trigger("redirectedSearch", null, { q: solrQStr, type: self.options.searchMode, explaination: input, 
														  geneFound: geneFound													 
														  });
			}						
			
			self._trigger("loadSideBar", null, { q: solrQStr, type: self.options.searchMode });			
			self._trigger("loadDataTable", null, { q: solrQStr, type: self.options.searchMode, 
					explaination: input, geneFound: geneFound });	
			
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
            	//console.log('key up..');	
            	
            	// when input text becomes empty string (ie, due to deletion)
            	if ( self.element.val() == '' ){
            		$('img.facetInfo').hide();
            		
            		for( var i=0; i<facetDivs.length; i++ ){
            			$('div#' + facetDivs[i] + ' span.facetCount').text(''); 					 
						$('div#' + facetDivs[i] + ' div.facetCatList').html('');
					}           			
            	}           	           	
            		
            	if (e.keyCode == 13) {            	
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
            				self.options.doDataTable = true;            				
            				self.sourceCallback(self); // ajax!!                    		
            			}  
            		}
            		else {            		
            			$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch);
            		}
            	}  
            	else {            
            		self.options.doDataTable = false;
            	}
            }); 
           
            // test if coming from redirected page, if yes, data is already defined.
            // Ie, no default loading of all data in facet 
            if ( typeof data == 'undefined'){              	
        		self.options.doDataTable = true;   
        		//$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch);
               	self.sourceCallback(self);      
        	}              
            
            $('button#acSearch').click(function(){            	
            	if ( self.term == undefined ){            	
            		self.term = "*";
            	}            	     
				self._inputValMappingForCallBack(self.term, true); 
            });

            var facetDivs = self.options.facets;			

            // refresh facet counts and facet tables
            self.element.click(function(){
            	self.term = undefined; 		
            	$('img.facetInfo').hide();
            	
				for( var i=0; i<facetDivs.length; i++ ){
					$('div#' + facetDivs[i] + ' span.facetCount').text('');
					$('div#' + facetDivs[i] + ' div.facetCatList').html('');	
				}            	
            });      
           
            $.ui.autocomplete.prototype._create.apply(this);			
        },   

        _showSearchMsg: function(){
			//return 'Search genes, MP terms, SOP by MGI/MP ID, gene symbol, synonym or name';
			return 'Search genes, SOP, MP, images by MGI ID, gene symbol, synonym or name';
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
			
			//console.log(json);
			self.options[sDataType + 'Found'] = matchesFound;
		
			$('div#' + sDivId + ' span.facetCount').text(matchesFound);
			$('div#' + sDivId + ' .facetCatList').html(''); 
									
			var list = [];
			var docs = json.response.docs;
			for ( var d=0; d<docs.length; d++ ){	
				for( var f=0; f<aFields.length; f++){
					var fld = aFields[f];
					var val = docs[d][fld];
					//console.log('field: ' + fld + ' for ' + val);
					if ( (fld == 'mp_term_synonym' || fld == 'ma_term_synonym' 
						  || fld == 'annotationTermName' || fld == 'symbol' ) && val ){
						var aVals = docs[d][fld];
						
						for ( var v=0; v<aVals.length; v++ ){						
							var thisVal = aVals[v];
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
						if ( val.toLowerCase().indexOf(sQuery) != -1 || sQuery.indexOf('*') != -1 ){				
							list.push(self.options.srcLabel[fld] + ' : ' + val);
						}
					}				
				}
			}
						
			self.options.acList = self.options.acList.concat(self._getUnique(list));	
		},
		
		_parseGeneGroupedJson: function (json, query) {
			var self = this;              
			//console.log('query: '+ query);				
			
           	var g = json.grouped[self.options.grouppingId]; 
           	var maxRow = json.responseHeader.params.rows;
           	var matchesFound = g.matches;
			//console.log('FOUND: gene found: '+ matchesFound);
           	self.options.geneFound = matchesFound;   

           	$('div#geneFacet span.facetCount').text(matchesFound);
			$('div#geneFacet .facetCatList').html(''); 

           	var groups   = g.groups;
           	var aFields  = self.options.searchFields;	
           	var srcLabel = self.options.srcLabel;
           	var list     = [];
           	           	
           	for ( var i=0; i<groups.length; i++){
        		//var geneId = groups[i].groupValue;
        		        		
        		var docs = groups[i].doclist.docs;
        		for ( var d=0; d<docs.length; d++ ){	
        			for ( var f=0; f<aFields.length; f++ ){
        				if ( docs[d][aFields[f]] ){	
							var geneId = docs[d][self.options.grouppingId];			
        					var fld = aFields[f];
        					var val = docs[d][fld];		
        					//console.log('field: '+ fld + ' -- val: ' + val + ' : ' + typeof val);
        					// marker_synonym, mp_id, mp_term, mp_term_synonym are all multivalued
        					if ( fld == 'marker_name' || fld == 'marker_synonym' || fld == 'synonym' || fld == 'allele_synonym' ){//} || fld == 'mp_id' || fld == 'mp_term' || fld == 'mp_term_synonym' ){
        						var aVals = docs[d][fld];
        						for ( var v=0; v<aVals.length; v++ ){						
        							var thisVal = aVals[v];
        							
									//alert(thisVal + ': '+ typeof thisVal);
        							// only want indexed terms that have string match to query keyword
									
        							if ( thisVal.toLowerCase().indexOf(query) != -1 || query.indexOf('*') != -1 ){    							
        								
        								if (fld == 'marker_name' || fld == 'synonym' || fld == 'marker_synonym' || fld == 'allele_synonym'){
        									MPI2.AutoComplete.mapping[thisVal] = geneId;        									
        								} 
        								list.push(srcLabel[fld] + " : " +  thisVal);
        							}							
        						}
        					}
        					else {        						
        						if ( val.toLowerCase().indexOf(query) != -1 || query.indexOf('*') != -1 ){        						
									//console.log(fld + ' : ' + val + ' id: ' + geneId);
        							MPI2.AutoComplete.mapping[val] = geneId;        										
        							list.push(srcLabel[fld] + " : " +  val);
        						}	
        					}
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
					
			// priority order of facet to be opened based on search result
			if ( oCounts.geneFound != 0 ){
				return 'gene';
			}			
			else if ( oCounts.mpFound != 0){				
				return 'mp';			
			}    		
    		else if ( oCounts.sopFound != 0 ){    			
    			return 'pipeline';						
			}	
    		else if ( oCounts.imgFound != 0 ){    			
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
			
			//console.log($.extend({}, self.options.queryParams_gene, self.options.commonQryParams));
 	    	// facet types are done sequencially; starting from gene
        	$.ajax({            	    
        			url: self.options.solrBaseURL_bytemark + 'gene/search',
            	    data: self.options.queryParams_gene,
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
    		if (queryParams.q.indexOf(" ") != -1 ){
	    		queryParams.qf = 'auto_suggest';	    		
	    	}  
	    	else if ( queryParams.q.indexOf('*') == -1 ){	    	
	    		queryParams.qf = 'text_search';	    		
	    	}	    	
	    	    		    		
    		//console.log(queryParams);
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
        	    	$('div#phenotypeFacet span.facetCount').html(self.options.loadWaiting);
        	    	$('div#pipelineFacet span.facetCount').html(self.options.loadWaiting);
        	    	$('div#imageFacet span.facetCount').html(self.options.loadWaiting);
        	    	
        	    	self._parseGeneGroupedJson(geneSolrResponse, q);  
        	    	self._parseJson(sopSolrResponse, q, 'sop', 'pipelineFacet', ['parameter_name', 'procedure_name']);
        	    	self._parseJson(mpSolrResponse, q, 'mp', 'phenotypeFacet', ['mp_id', 'mp_term', 'mp_term_synonym']);
        	    	self._parseJson(imgSolrResponse, q, 'img', 'imageFacet', ['annotationTermName', 'expName', 'symbol']);        	    	
        	    	// hide for now
        	    	//self._parseJson(maSolrResponse, q, 'ma', 'tissueFacet', ['ma_id', 'ma_term', 'ma_term_synonym']);
        	    	
        	    	/*console.log('geneFound: ' + self.options.geneFound + 
        	    			    ' - mpFound: ' + self.options.mpFound + 
        	    			    ' - maFound: ' + self.options.maFound + 
        	    			    ' - sopFound: ' + self.options.sopFound + 
        	    			    ' - imgFound: ' + self.options.imgFound
        	    				);*/
        	    	self.options.searchMode = self._setSearchMode({	geneFound: self.options.geneFound, 
        	    													mpFound: self.options.mpFound, 
        	    													sopFound: self.options.sopFound,
        	    													imgFound: self.options.imgFound
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
    	    	
    	_doCallBacks: function(){
    		
    		var self = this;
    		self.options.doneSourceCall = 0; // refresh
    		
    		if ( self.term === undefined || self.term == '' ){
    			self.term = '*:*';
    		}
    		
    		$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch); 
    		
    		// only Enter event will fire and not other keyup/down events
    		if ( window.location.pathname != self.options.search_pathname && self.options.hitEnterBeforeDropDownListOpensVal == 1 ){    			
    			self._trigger("redirectedSearch", null, { q: self.term, 
    													  type: self.options.searchMode, 
    				                                      geneFound: self.options.geneFound    				                                    
    				                                      });
    		}
    		  		
    		var params = self.options.facetTypeParams[self.options.searchMode];    		    		
    		params.q = self.term;	
    		
    		//console.log("check self.term: "+ self.term);
    		
    		// loadSideBar reacts to all non-enter keyup events. Ie, typing in input box triggers changes in facet 
    		// but will not load dataTable
			self._trigger("loadSideBar", null, {				
    			//geneFound: self.options.geneFound,				
    			q: self.term, type: self.options.searchMode																					   
    		});    
			// loadDataTable reacts to 'enter', 'select' and 'search button'
			if ( self.options.doDataTable ){	
				//console.log('do datatable');
	    		self._trigger("loadDataTable", null, params);
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