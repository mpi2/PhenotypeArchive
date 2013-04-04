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
 * mpPageWidget: create each section of the phenotype page.
 * 
 */
(function ($) {
	'use strict';
    $.widget('MPI2.mpPage', {
        	
	    options: {
	    	solrParams: {
	    			'qf': 'auto_suggest',	    	
	    			'defType': 'edismax',
	    			'rows': 50000,	    			
	    			'wt': 'json',	
	    			'sort': 'mp_term asc',
	    			'q.option': 'AND',	    			
	    	}
	    },

    	_create: function(){
    		// execute only once 	
    		var self = this;  
    		self.options.solrParams.q = self.options.phenotype_id;   
    		
    		$.ajax({	
	    		'url': solrUrl + '/mp/select',	    		
	    		'data': self.options.solrParams,	    		
	    		'dataType': 'jsonp',
	    		'jsonp': 'json.wrf',	    	
	    		'success': function(json) {	    			
	    			self._composeMpPage(json);	    			
	    		} 		
	    	});	
    	},
    	
	    // want to use _init instead of _create to allow the widget being called each time
	    _init : function () {
			var self = this;
	    },
	    
	    _composeMpPage: function(json){
	    	var self = this;
	    	
	    	var oDoc = json.response.docs[0]; 
	    		    	
	    	$('div#mpMain .topic').html('Phenotype: ' + oDoc['mp_term']);
	    	
	    	var sectionFields = {
	    			'overview': {caption: '',
	    						 indexFields: [	
	    						  {'mp_definition'   : 'Definiton'},	    						
	    						  {'mp_term_synonym' : 'Synonym'},
	    						  {'procedure_name'  : {display: 'Procedure',
	    			            	 				    baseUrl: 'http://www.mousephenotype.org/impress/impress/displaySOP/'	    			            	 				  
	    						  					   }
	    						  },
	    						  {'ma_term'         : {display: 'Anatomy',
	    							  					baseUrl: 'http://informatics.jax.org/searches/AMA.cgi?id='
	    						  						}
	    						  },	    						
	    			              {'mp_id'           : {display: 'MGI&nbsp;MP&nbsp;browser',
	    			            	                    baseUrl: 'http://www.informatics.jax.org/searches/Phat.cgi?id='
	    			            	 				   }
	    						  }
	    						 ]
	    			},
	    			'parameters': {caption: 'Gene variants with this phenotype',
	    				           indexFields: [{'marker_symbol': {display: 'Gene',
	    				        	                                baseUrl: baseUrl+'/genes/',
	    				        	                                acc    : 'mgi_accession_id'
	    				        	                               }},
	    				        	             {'allele_symbol': {display: 'Allele Symbol',
	    				        	            	                baseUrl: 'http://www.informatics.jax.org/searchtool/Search.do?query=',
	    				        	            	                acc    : 'allele_id' 
	    				        	                               }},
	    				        	             {'zygosity': {display: 'Zygosity'}}, 
	    				        	             {'sex': {display: 'Sex'}},	    				        	              
	    				        	             {'procedure_name': {display: 'Procedure',
	    				        	            	                 baseUrl: 'http://www.mousephenotype.org/impress/impress/displaySOP/',
	    				        	            	                 acc    : 'procedure_stable_key' 
	    				        	            	 			  }},
	    				        	             {'external_id': {display: 'Data',
	    				        	            	                 baseUrl: 'http://www.europhenome.org/databrowser/viewer.jsp?set=true&m=true&zygosity=All&compareLines=View+Data',
	    				        	            	                 acc    : '' 
	    				        	            	 			  }},	 			  
	    				        	             /*{'parameter_name': {display: 'Parameter',
		    				        	            	             baseUrl: 'http://www.mousephenotype.org/impress/impress/listParameters/',
		    				        	            	             acc    : 'parameter_stable_key' 	 
		    				        	            	 		  }}*/
	    				        	            ], 
	    			},
	    			'others': {caption: 'Explore',
	    					   indexFields: [{go_id: {display: 'Gene&nbsp;Ontology',
	    						                      baseUrl: 'http://amigo.geneontology.org/cgi-bin/amigo/term_details?term='
	    					                         }},	    					                
	    						             {sibling_mp_term: {display: 'Related&nbsp;Phenotypes',
	    						            	                baseUrl: baseUrl+'/phenotypes/'
	    						                               }}
	    					   				]	    					            
	    			}			  
	    	};		

	    	for ( var i in sectionFields ){
	    		switch(i){	    			
	    			case 'overview':
	    				self._makeOverviewBlk(oDoc, sectionFields, i);	    				
	    				break;
	    			case 'parameters':	    				
	    				self._makeParametersBlk(oDoc, sectionFields, i);	    				
	    				break;
	    			case 'others':
	    				self._makeOthersBlk(oDoc, sectionFields, i);
	    				break;
	    			default:
	    				//console.log('default');
	    		}	    		
	    	}	    	
	    	
	    },	 
	    
	    _composeTr: function(fieldKey, fieldVal, className){
	    	var self = this;
	    	
	    	var tr = $('<tr></tr>');
	    	className = className ? className : 'fVal';
	    	
	    	var td1 = $('<td></td>').html(fieldKey);
	    	var	td2 = $('<td></td>').attr({'class':className}).html(fieldVal);	    	
	    	
	    	tr.append(td1, td2);
	    	return tr;
	    },
	    
	    _composeTr2: function(aData){
	    	var self = this;
	    	    	
	    	var tr = $('<tr></tr>');
	    	
	    	for( var i=0; i<aData.length; i++ ){	    		
	    		tr.append($('<td></td>').attr({'class':aData[i].className}).html(aData[i].val));	    
	    	}	    
	    
	    	return tr;
	    },
	    
	    _composeHeaders: function(aHeaders){
	    	var thead = $('<thead></thead>').attr({'class':'highlight'});
	    	for( var i=0; i<aHeaders.length; i++){
	    		var td = $('<th></th>').html(aHeaders[i]);
	    		thead.append(td);
	    	}
	    	return thead;
	    },
	    
	    _makeOverviewBlk: function(oDoc, sectionFields, field){
	    	var self = this;	    	  		

	    	/*var leftBlk   = $('<div></div>').attr({'id':'ovLeft', 'class':'span4'});	    	
	    	var rightBlk  = $('<div></div>').attr({'id':'ovRight', 'class':'span8'}).html('');
	    	
	    	var ovWrapper = $('<div></div>').attr({'class':'container span12'}).append(leftBlk, rightBlk);	    	
	    	var ovBlock   = $('<div></div>').attr({'class':'row-fluid dataset'}).append(ovWrapper);	    	
	    	*/
	    	var table = $('<table></table>').attr({'id': 'mpOverviewTable', 'class':'mpTable table'});	    				
	    	table.append($('<caption></caption>').html(sectionFields[field].caption));
	    	
	    	for ( var f=0; f< sectionFields[field].indexFields.length; f++ ){
	    		var obj = sectionFields[field].indexFields[f];
	    		for ( var key in obj ){

	    			//console.log('key: '+ key);
	    			if ( oDoc[key] ){	    				
	    				if ( key == 'mp_id' ){
	    					// link to MGI phenotype browser
	    					var a = $('<a></a>').attr({'href':obj[key].baseUrl + oDoc[key]}).html(oDoc[key]);	    				
	    					table.append(self._composeTr(obj[key].display, a));
	    				}
	    				else if ( key == 'ma_term' ) {
	    					// anatomy
	    					var maTerms = $('<ul></ul>').attr({'class':'itemBox'});
	    					for (var ma=0; ma<oDoc[key].length; ma++ ){
	    						var li = $('<li></li>');
	    						var a = $('<a></a>').attr({'href':obj[key].baseUrl + oDoc['ma_id'][ma]}).html(oDoc[key][ma]);
	    						li.append(a);
	    						maTerms.append(li);	    								
	    					}
	    					
	    					maTerms = self._assignListClass(maTerms);	
	    					table.append(self._composeTr(obj[key].display, maTerms));	    					
	    				}
	    				else if ( key == 'procedure_name' ) {
	    					var procTerms = $('<ul></ul>').attr({'class':'itemBox'});
	    					var seen = {};
	    					var counter = 0;
	    					for (var p=0; p<oDoc[key].length; p++ ){	    						
	    						var pipeline_name        = oDoc['pipeline_name'][p];	    						
	    						var pipeline_stable_id   = oDoc['pipeline_stable_id'][p];
	    						var pipeline_stable_key  = oDoc['pipeline_stable_key'][p];
	    						var procedure_name       = oDoc['procedure_name'][p];
	    						var procedure_stable_id  = oDoc['procedure_stable_id'][p];
	    						var procedure_stable_key = oDoc['procedure_stable_key'][p];
	    						
	    						if ( !seen[procedure_stable_id] ){
	    							counter++;
	    							seen[procedure_stable_id] = 1;
	    							seen[procedure_stable_id][pipeline_stable_id] = 1;   						
	    						
	    							var url = obj[key].baseUrl + procedure_stable_key;	
	    							var title = 'Pipeline: ' + pipeline_name;
	    							
	    							var li = $('<li></li>');
	    							var a = $('<a></a>').attr({'href':url,	    													 
	    													   'rel': pipeline_name	    													  
	    													   }).html(procedure_name + ' (' + pipeline_name + ')');
	    							li.append(a);
	    							procTerms.append(li);
	    						}	
	    					}
	    					procTerms = self._assignListClass(procTerms);	    					
	    					table.append(self._composeTr(obj[key].display, procTerms));	    					
	    				}
	    				else if ( key == 'mp_term_synonym' ) {	    					
	    					var synonyms = $('<ul></ul>').attr({'class':'itemBox'});
	    					for (var s=0; s<oDoc[key].length; s++ ){
	    						var synonym = $('<li></li>').html(oDoc[key][s]);
	    						synonyms.append(synonym);
	    					}
	    					
	    					synonyms = self._assignListClass(synonyms);
	    					table.append(self._composeTr(obj[key], synonyms));	
	    				}	
	    				else if ( key == 'mp_definition' ){
	    					var className = 'mpDef';	    					
	    					table.append(self._composeTr(obj[key], oDoc[key], className));	    										
	    				}	    				
	    			}
	    		}
	    	}
	    	//leftBlk.append(table);	    	
			//self.element.append(ovBlock);
			$('div#ovLeft').html(table);
	    	
			// toggle long mp definition
			if ($('table td.mpDef').text().length > 200 ){
				$('table td.mpDef').collapseText({'size': 100});
			}			
	    },    
	    
	    _makeParametersBlk: function(oDoc, sectionFields, field){
	    	var self = this;
	    	//console.log(oDoc);	    	
	    	var pBlk = $('<div></div>').attr({'class':'row-fluid dataset'});
	    	var caption = "<h4 class='caption'>" + sectionFields[field].caption + "</h4>";	    	
	    	var inner = $('<div></div>').attr({'class':'container span12'});
	    	//var regex = /(.+_\d*_\d*_\d*)_\d*/; // for parsing impress parameter stable id: only want first 3 digit sets
	    	
	    	inner.append(caption);
	    	
	    	// table exporter UI
	    	//inner.append('Export table as ', $.fn.composeSelectUI(['select a format ...', 'CSV', 'Excel', 'PDF'], 'geneVariantExport'));	
	    		    	
	    	// check number of rows for a gene	    	
	    	var rows;
	    	var ths = '';
	    	//var trs = '';
	    	
	    	var rowValsSex = {};
	    	var geneSexParamSid = {};
	    	
	    	if ( oDoc.marker_symbol ){
	    		var rows = oDoc.marker_symbol.length;	    	
	    		
	    		for (var r=0; r<rows; r++ ){
	    			var aConcatColVals = [];	
	    			var sSexColVals = null;	    				    			
	    				    			
	    			//var tds = '';	    			
	    			
	    			for ( var i=0; i< sectionFields[field].indexFields.length; i++ ){
	    				var obj = sectionFields[field].indexFields[i]; // marker, allele, zygosity, sex, procedure, parameter..
	    				    				
	    				for ( var key in obj ){	
	    					//console.log('field: ' + key);
	    					//console.log('display: ' + obj[key].display);
	    				
	    					if ( r == 0 ){ 
	    						// table column header	    					
	    						ths += "<th>" + obj[key].display + "</th>";
	    					} 
	    					
	    					var oColVal = {};	
	    					oColVal.className = '';    					
	    					var value = '-';
	    					oColVal.val = value;
	    					
	    					var url = null;
	    					
	    					if ( oDoc[key] ){
	    						
	    						if ( key == 'sex' ){
	    							sSexColVals = oDoc[key][r];
	    						}
	    						
	    						// value of each column except data col
	    						if ( oDoc[key][r] && key != 'external_id'  ){	    							
	    							value = self._superscriptify(oDoc[key][r]);
	    							oColVal.val = value;
	    						}	    						
	    						
	    						// overwrite value of some cols
	    						if ( obj[key].baseUrl ){
	    							
	    							if ( obj[key].display == 'Allele Symbol' ){ 
	    								if ( typeof oDoc[obj[key].acc] !== 'undefined' && typeof oDoc[obj[key].acc][r] !== 'undefined' ){
	    									if ( oDoc[obj[key].acc][r].indexOf('MGI:') == -1 ){
	    										// do nothing for non-MGI allele id for now (eg. EUROCRAP...)	    										
	    									}
	    									else {
	    										url = obj[key].baseUrl + oDoc[obj[key].acc][r];	    								
	    										oColVal.val = "<a href='" + url + "'>" + value + "</a>";
	    									}
	    								} 
	    							}	    							
	    							else if ( obj[key].display == 'Data' ){
	    							
	    								var external_id = oDoc[key][r];
	    								var procedure_sid = oDoc['procedure_stable_id'][r]; 
	    								//var match = regex.exec(oDoc['parameter_stable_id'][r]);
	    								//var parameter_sid = match[1];	
	    								var parameter_sid = oDoc['parameter_stable_id'][r];
	    								
	    								var sex = $.fn.upperCaseFirstLetter(oDoc['sex'][r]);	    								
	    								//"&x=" + sex +
	    								//"&pid_" + parameter_sid + "=on"
	    								var params = "&l="+ external_id + "&p=" + procedure_sid +"&pid_" + parameter_sid + "=on";
	    								var url = obj[key].baseUrl + params;
	    								var src = 'Europhenome';	    								
	    								oColVal.val = "<a href='" + url + "'>" + src + "</a>";	
	    								if ( !geneSexParamSid[oDoc.marker_symbol[r]] ){
	    									geneSexParamSid[oDoc.marker_symbol[r]] = {};
	    									geneSexParamSid[oDoc.marker_symbol[r]]['male'] = [];
	    									geneSexParamSid[oDoc.marker_symbol[r]]['female'] = [];
	    								} 
	    							}
	    							else {
	    								//console.log('key:' + key);
	    								url = obj[key].baseUrl + oDoc[obj[key].acc][r];	    								
	    								oColVal.val = "<a href='" + url + "'>" + value + "</a>";
	    								//oColVal.val = value;
	    							}	    							
	    						}	
	    							    						
	    						if ( key != 'sex' ){	    							
	    							aConcatColVals.push(oColVal.val);
	    						}
	    					}	    					
	    					//tds += "<td>" + oColVal.val + "</td>";    						    					
	    				}	    				
	    			}
	    			
	    			if ( ! rowValsSex[aConcatColVals.join("___")] ){
	    				rowValsSex[aConcatColVals.join("___")] = {};    			
						rowValsSex[aConcatColVals.join("___")].sex = [];
	    			}	    			
					rowValsSex[aConcatColVals.join("___")].sex.push(sSexColVals);					
					//console.log(oDoc.marker_symbol[r] + ' *** ' + aConcatColVals.join("___") + ' >> sex: ' + sSexColVals);
	    			//trs += "<tr>" + tds + "</tr>";	    			
	    		}	    
	    		
	    		var TRS = '';
	    		for( var k in rowValsSex){
	    			//console.log( k );
	    			//console.log( k + ': '+ rowValsSex[k].sex);
	    			//console.log( rowValsSex[k].sex.length );
	    			var tds = '';	    			
	    			var aColVals = k.split('___');	
	    			var sThisSex = null;
	    			var parermterIds = null;
	    			
	    			for( var i=0; i<aColVals.length; i++){
	    				//console.log(1 + ': '+ aColVals[0]);
	    				//console.log(aColVals[4]);
	    				if ( i !== 4 ){ // data link
	    					tds += "<td>" + aColVals[i] + "</td>";
	    				}
	    				if ( i == 2 ){
	    					
	    					var gsymbol = $(aColVals[0]).text();
	    					var genders = '';
	    					var seenSex = {};
	    					seenSex.sex = null;  
	    					
	    					var aSex = $.fn.getUnique(rowValsSex[k].sex).sort();
	    					var sexClass = aSex[0];
	    					if ( aSex.length > 1 ){
	    						sexClass = 'bothSex';	    						
	    					}
	    					   					
	    					sThisSex = sexClass;
	    					
	    					for( var g=0; g<aSex.length; g++){
	    						
	    						var sex = aSex[g];
	    						if ( seenSex.sex != sex ){
	    							seenSex.sex = sex; 
	    							genders += "<img src='" + baseUrl+ "/img/icon-"+ sex + ".png'/>";
	    							
	    							var psids = $.fn.getUnique(geneSexParamSid[gsymbol][sex]);
	    							for (var s=0; s<psids.length; s++){	    								
	    								parermterIds += "&pid_" + psids[s] + "=on";
	    							}
	    						}	
	    					} 
	    					
	    					tds += "<td><span class='gender' alt='" + sexClass + "'>" + genders + "</span></td>";
	    				}	
	    				if ( i == 4 ){
	    					var aLink = $(aColVals[i]);	    					
	    					var sexParam = sThisSex == 'bothSex' ? "&x=Both-Split" : "&x=" + $.fn.upperCaseFirstLetter(sThisSex);
	    					if (parermterIds){
	    						tds += "<td><a href='" + aLink.attr('href') + sexParam + parermterIds + "'>Europhenome</a></td>";
	    					}
	    					else {
	    						tds += "<td><a href='" + aLink.attr('href') + sexParam + "'>Europhenome</a></td>";
	    					}	
	    				}
	    			}
	    			TRS += "<tr>" + tds + "</tr>"; 
	    		}    		   	
	    		
	    		// jquery dataTable is not working with table elements created via object
	    		// but ok with strings
	    		var variantsTable = "<table class='mpTable table' id='geneVariants'>"
	    						  + "<thead>" + ths + "</thead>"
	    						  + "<tbody>" + TRS + "</tbody></table>";
	    		                  
	    		pBlk.append(inner.append(variantsTable));
	    			    			    			    		
	    		var mgiBaseUrl = "http://www.informatics.jax.org/javawi2/servlet/WIFetch?page=mpAnnotSummary&id=";
	    		var mgiPheno2GenoUrl = mgiBaseUrl + oDoc.mp_id;
	    		var linkText = "See other genotypes curated from the literature";
	    		var mgiGenotypeLink = $('<a></a>').attr({'href':mgiPheno2GenoUrl}).text(linkText);	    		
	    		var infoDiv = $('<div></div>').attr({'id':'pheno2geno'}).html(mgiGenotypeLink);	    		   		
	    		
	    		pBlk.append(infoDiv);	
	    		
	    		pBlk.append($.fn.loadFileExporterUI({
	    			label: 'Export table as:',
	    			formatSelector: {
	    				TSV: 'tsv_geneVariants',
	    				XLS: 'xls_geneVariants'	    			 					
	    			},
	    			'class': 'fileIcon'
	    		}));	    		
	    		
	    		self.element.append(pBlk);	
	    			    		
	    		var dataTblCols = [0,1,2,3,4,5];
	    		var oDataTbl = $.fn.initDataTable($('table#geneVariants'), {	
	    			//"aaSorting": [[0, "asc"], [1, "asc"]],			     
   			     	
	    			// aoColumns should match all column in table	
				    "aoColumns": [
						{ "sType": "html", "mRender":function( data, type, full ) {
					        return (type === "filter") ? $(data).text() : data;
					    }},
						{ "sType": "html", "mRender":function( data, type, full ) {
					        return (type === "filter") ? $(data).text() : data;
					    }},
					    { "sType": "string"},
						{ "sType": "alt-string", "bSearchable" : false },
						{"sType": "html", "mRender":function( data, type, full ) {
					        return (type === "filter") ? $(data).text() : data;
					    }},
		                {"sType": "html", "mRender":function( data, type, full ) {
					        return (type === "filter") ? $(data).text() : data;
					    }}
					], 					
					"iDisplayLength": 10   // 10 rows as default 
   				});	  		    			
	    				    		
	    		$.fn.dataTableshowAllShowLess(oDataTbl, dataTblCols, null);
	    		
	    		// apply Js to file exporter icons
	    		self._initFileExporter({
					mpId: oDoc.mp_id,
					mpTerm: oDoc.mp_term,
					externalDbId: 5,  //mp_db_id in phenotype_call_summary table
					panel: 'geneVariants',
					fileName: 'gene_variants_of_MP-' + oDoc.mp_term.replace(/ /g, '_')	
				});	    		    		
	    	}
	    },
	    
	    _initFileExporter: function(conf){
	    	$('button.fileIcon').click(function(){
	    		var classString = $(this).attr('class');	    		
	    		//var controller = classString.substr(0, classString.indexOf(" "));
	    		//console.log(controller);
	    		var fileType = $(this).text();
	    		var url = baseUrl + '/export';	    		
	    		var sInputs = '';
	    		for ( var k in conf ){
	    			sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>";	    			
	    		}
	    		sInputs += "<input type='text' name='fileType' value='" + fileType.toLowerCase() + "'>";
	    		
	    		$("<form action='"+ url + "' method=get>" + sInputs + "</form>").appendTo('body').submit().remove();    		
	    	}).corner('6px');	 	
	    },	    
	   
	   /*_doFileExporter: function(conf){
	    	conf.obj.change(function(){
	    		alert($(this).find(":selected").text());
	    		var fileType = $(this).find(":selected").text();
	    		var url = baseUrl + '/' + fileType.toLowerCase();	    		
	    		var sInputs = '';
	    		for ( var k in conf.params ){
	    			sInputs += "<input type='text' name='" + k + "' value='" + conf.params[k] + "'>";
	    		}
	    		$("<form action='"+ url + "' method=get>" + sInputs + "</form>").appendTo('body').submit().remove();	    		
	    	});	
	    },*/
	    
	    _superscriptify: function(str){
	    	var self = this;		    	
	    	return str.replace('<', '&lt;').replace('>', '&gt;').replace('&lt;', '<sup>').replace('&gt;','</sup>');	    	
	    },
	    
	    _makeOthersBlk: function(oDoc, sectionFields, field){
	    	//console.log(oDoc);
	    	var self = this;
	    	var pBlk = $('<div></div>').attr({'class':'row-fluid dataset'});
	    	var inner = $('<div></div>').attr({'class':'container span12'});
	    	var table = $('<table></table>').attr({'class':'mpTable table'});
	    	
	    	table.append($('<caption></caption>').html(sectionFields[field].caption));
	    	//console.log(sectionFields[field].indexFields);	    	
	    	var hasAnnotation = null;
	    	
	    	for ( var i=0; i< sectionFields[field].indexFields.length; i++ ){
	    		var obj = sectionFields[field].indexFields[i];
	    		for ( var key in obj ){
					
	    			//console.log('key: '+ key);
	    			if ( oDoc[key] ){	    				
	    					    				
	    				var t = null;
	    				var seen_id = {};	   				    				
	    				var siblings = [];
	    				var siblingsKV = {};
	    				
	    				var terms = $('<ul></ul>').attr({'class':'itemBox'});
	    				for ( t=0; t<oDoc[key].length; t++ ){
	    					//console.log('mp term: '+ oDoc[key][t]);
	    					var id;
	    					if ( key == 'go_id' ){
	    						id = oDoc[key][t];
	    					}
	    					else if ( key == 'sibling_mp_term' ){
	    						id = oDoc['sibling_mp_id'][t];
	    					}
	    					
	    					// skip itself in the silblings list and duplicates
	    					if ( id != self.options.phenotype_id && seen_id[id] != 1 ){
	    						//console.log('filtered id: ' + id);	   
	    						hasAnnotation = 1;
	    						siblings.push(oDoc[key][t]);
	    						siblingsKV[oDoc[key][t]] = id;	
	    					}	
	    					seen_id[id] = 1; // accounting of seen phenotype_ids
	    				}
	    				
	    				// sorted list of sibling MP terms
	    				siblings.sort();	
	    				
	    				for(var j=0; j<siblings.length; j++){
	    					
    						t = j;
							var url = obj[key].baseUrl + siblingsKV[siblings[j]];
							var a = $('<a></a>').attr({'href':url}).html(siblings[j]);
					
							var liClass = j > 10 ? 'longList' : null;		    					
							var li = $('<li></li>').attr({'class':liClass});	
							li.append(a);
							terms.append(li);
    					}	 
	    					    				
	    				if (t > 10){
	    					var toggleButton = $('<button></button>').attr({'class':'listToggle'}).text('see full list');
	    					terms.append(toggleButton);
	    				}
	    				
	    				// only display this section if there is data	    				
	    				terms = self._assignListClass(terms);	    				
	    				table.append(self._composeTr(obj[key].display, terms));
	    			}
	    		}	    		
	    	}
	    	
	    	if (hasAnnotation){
	    		self.element.append(pBlk.append(inner.append(table)));
	    	}
	    	
	    	if ( $('button.listToggle').size() > 0 ){
	    		$('button.listToggle').toggle(
	    			function(){
	    				$('li.longList').show();
	    				$(this).text('show fewer items').addClass('showLess');	    			
	    			},
	    			function(){
	    				$('li.longList').hide();
	    				$(this).text('see full list').removeClass('showLess');
	    			}
	    		);
	    	}	
	    },
	    
	    _assignListClass: function(obj){
	    	if ( obj.find('li').size() > 1 ){
				obj.find('li').each(function(){
					$(this).addClass('listItem');
				});
			} 
			else {
				obj.find('li').addClass('singleton');
			}
	    	return obj;
	    },
	    
	    
	    destroy: function(){
			// revert to initial state
			// eg. this.element.removeClass('collapseText');
			
			// call the base destroy function
			$.Widget.prototype.destroy.call( this );
		}    
    });
	
}(jQuery));	
	