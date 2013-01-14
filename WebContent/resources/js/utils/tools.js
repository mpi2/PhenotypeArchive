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
 * tools.js: various tools used across the web application.
 * Use closure to safely write jQuery as $
 * as the closure creates a function with $ as parameter and is run immediately with the value
 * jQuery which gets mapped to $
 * 
 * Author: Chao-Kung Chen
 */
(function($){
	
	$.fn.ieCheck = function(){
		
		if ( $.browser.msie && $.browser.version < 8.0 ){		
			var msg = "<div id='noSupport'>Dear user:<p><p>It appears that you are using Internet Explorer 7 or earlier version.<p>To ensure that IMPC is supporting the best browsing features, functionalities and experiences, " +
				  "and considering the security issues of older IEs, we decided not to support IE7 and earlier versions.<p>We are sorry if this has caused your inconvenience.<p>Here is a list of supported browsers: " +
				  "<a href='http://www.mozilla.org'>Firefox</a>, <a href='http://www.google.com/chrome'>Google chrome</a>, <a href='http://support.apple.com/downloads/#internet'>Apple safari</a>.<p>" +
				  "IMPC team.</div>";
			
			$('div.navbar').siblings('div.container').html(msg);
			return false;
		}
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
    	var iconDiv = $('<div></div>').attr({'class': 'fileIcons'}).html(label);
    	for ( var f in oFormatSelector ){
    		var btn = $('<button></button>').attr({'class': oFormatSelector[f] + ' ' + conf['class']}).html(f);
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
    
    $.fn.fetchEmptyTable = function(theadStr, colNum, id){
    	
    	var table = $('<table></table>').attr({'id':id});
    	var thead = theadStr;
    	var tds = '';
    	for (var i=0; i<colNum; i++){
    		tds += "<td></td>";
    	}
    	var tbody = $('<tbody><tr>' + tds + '</tr></tbody>');	    	    	
    	table.append(thead, tbody);
    	return table;
    },	   	 
    $.fn.invokeDataTable = function(oInfos){   	   	
    	
    	var oDtable = $('table#' + oInfos.mode).dataTable({
    		"bSort" : false,
    		"bProcessing": true,
    		"bServerSide": true,	    		
    		//"sDom": "<'row-fluid'<'span6'><'span6'>>t<'row-fluid'<'span6'i><'span6'p>>",
    		"sDom": "<'row-fluid'<'#exportSpinner'><'#tableTool'>r>t<'row-fluid'<'span6'i><'span6'p>>",
    		/*"oTableTools": {
    			"sSwfPath": "/phenotype-archive/js/vendor/DataTables-1.9.4/extras/TableTools/media/swf/copy_csv_xls_pdf.swf",
    			"aButtons": [
    				"copy",    				
    				//"print",
    				{
    					"sExtends":    "collection",    				
    					"sButtonText": 'Save <span class="caret" />',
    					"aButtons":    [ "csv", "xls", "pdf" ]
    				}
    			]
    		},*/
			"sPaginationType": "bootstrap",
    		"fnServerParams": function ( aoData ) {
    			aoData.push(	    			 
    			    {"name": "solrParams",
    				 //"value": oInfos.params// + oInfos.facetParams
    				 "value": JSON.stringify(oInfos, null, 2)// + oInfos.facetParams
    				}	    
    			)		
    		},
    		"fnDrawCallback": function( oSettings ) {    		      
    		      $('a.interest').click(function(){
    		    	  var mgiId = $(this).attr('id');
    		    	  var label = $(this).text();
    		    	  var regBtn = $(this);
    		    	  $.ajax({
                          url: '/toggleflagfromjs/' + mgiId,                       
                          success: function (response) {
                        	  function endsWith(str, suffix) {
                        		    return str.indexOf(suffix, str.length - suffix.length) !== -1;
                        		}         if(response === 'null') {
                                  window.alert('Null error trying to register interest');
                              } 
                              else {  
                          
                                  if( label == 'Register interest' ) {
                                      regBtn.text('Unregister interest');
                                  } 
                                  else {
                                	  regBtn.text('Register interest');
                                  }                               
                              }                         
                          },
                          error: function () {
                              window.alert('AJAX error trying to register interest');                     
                          }
                      });
                      return false;    		    	  
    		      });
    		},
    		"sAjaxSource": oInfos.dataTablePath
    	}); 
    	    	
    	
    	 //console.log(window.location.hash.substring(1)); // string after '#'
    	 
    	/*var oTableTools = new TableTools( oDtable, {
	        "sSwfPath": "/phenotype-archive/js/vendor/DataTables-1.9.4/extras/TableTools/media/swf/copy_csv_xls_pdf.swf",
	        "aButtons": [
	        			"copy"	
	        ]
    	});*/    
    	    	
    	var saveTool = $("<div id='saveTable'></div>").html("Download data <img src='/phenotype-archive/img/floppy.png' />").corner("4px");    	
    	var toolBox = fetchSaveTableGui();
    	$('div.dataTables_processing').siblings('div#tableTool').append(saveTool, toolBox); 
    	$('div#saveTable').click(function(){
        		if ( $('div#toolBox').is(":visible")){
        			$('div#toolBox').hide();
        		}
        		else {
        			$('div#toolBox').show();
        			
        	    	var solrCoreName = oInfos.solrCoreName;
        	    	var iActivePage = $('div.dataTables_paginate li.active a').text();
        	    	
        	    	var iRowStart = iActivePage == 1 ? 0 : iActivePage*10-10;
        	    	var showImgView = $('div.gridTitle div#imgView').attr('rel') == 'imageView' ? true : false;  		
        	    	
        	    	initGridExporter({				
        					externalDbId: 5,				
        					rowStart: iRowStart,
        					solrCoreName: solrCoreName,        				
        					params: oInfos.params,
        					showImgView: showImgView,
        					gridFields: MPI2.searchAndFacetConfig.facetParams[solrCoreName].gridFields,
        					fileName: solrCoreName + '_table_dump'	
        			});        			
        		}        		
    	});	    	
    }
    
    function initGridExporter(conf){
    	
    	$('button.gridDump').click(function(){
    		
    		var classString = $(this).attr('class');    		
    		var fileType = $(this).text(); 
    		var dumpMode = $(this).attr('class').indexOf('all') != -1 ? 'all' : 'page';
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
    			if ( conf['solrCoreName'] == 'gene' ){
    				//url1 = MPI2.searchAndFacetConfig.solrBaseURL_bytemark + "gene/search?";
    				url1 = MPI2.searchAndFacetConfig.solrBaseURL_ebi + "gene/search?";
    			}
    			else {
    				url1 = MPI2.searchAndFacetConfig.solrBaseURL_ebi + conf['solrCoreName'] + "/select?";
    				paramStr += "&wt=json";
    			}
    			    		
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
    	}).corner('6px'); 	
    }

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
    
    	var div = $("<div id='toolBox'></div>").corner("4px");
    	div.append($("<div class='dataName'></div>").html("Current paginated entries in table"));    	
    	div.append($.fn.loadFileExporterUI({
			label: 'Save as:',
			formatSelector: {
				TSV: 'tsv_grid',
				XLS: 'xls_grid'	    			 					
			},
			'class': 'gridDump'
		}));
		div.append($("<div class='dataName'></div>").html("All entries in table")); 
		div.append($.fn.loadFileExporterUI({
			label: 'Save as:',
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
				 	//"bDeferRender": true,	
				 	//"sPaginationType": "full_numbers",
					"sDom": "<'row-fluid'<'#foundEntries'><'span6'f>r>t<'row-fluid'<'#tableShowAllLess'><'span6'p>>",
					"sPaginationType": "bootstrap",
				 	"bProcessing": true,
				 	"bSortClasses": false,	
				 	"oLanguage": {
				 		"sSearch": "Filter data in table:"
				 	}				 	
				 	//"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],				 
				 	//"aLengthMenu": [[1, 2, -1], [1, 2, "All"] ],
				 	//"iDisplayLength" : -1,
				 	//"aaSorting": [[0, "asc"], [1, "asc"]],
	    			// aoColumns should match all column in table	   		     
				};
				
		var oTbl = jqObj.dataTable($.extend({}, params, customConfig)).fnSearchHighlighting();
		return oTbl;
	}		
    $.fn.dataTableshowAllShowLess = function(oDataTbl, aDataTblCols, display){
    	    	
    	var rowFound = oDataTbl.fnSettings().aoData.length;
    	$('div#foundEntries').html("Total entries found: " + rowFound).addClass('span6');    	
    	
		$('div.dataTables_paginate').hide();
		    		
		$('div.dataTables_filter input').keyup(function(){
						
			if ( !$(this).val() ){								
				$('div.dataTables_paginate').hide();							
			}
			else {
				// use pagination as soon as users use filter
				$('div.dataTables_paginate').show();
			}
		});	
				
		var display = ( display == 'Show fewer entries' || !display ) ? 'Show all entries' : 'Show fewer entries';  		
			
		// show all/less toggle only appears when we have > 10 rows in table
		if ( rowFound > 10 ){			
			$('div#tableShowAllLess').html("<span>" + display + "</span>").addClass('span6')
			$.fn.reloadDataTable(oDataTbl, aDataTblCols, display);
		}
    }
    
    $.fn.reloadDataTable = function(oDataTbl, aDataTblCols, display){
		$('div#tableShowAllLess').click(function(){    			
			
			oDataTbl.fnSettings()._iDisplayLength = display == 'Show all entries' ? -1 : 10;			
			var selector = oDataTbl.selector;			
			
			display = display == 'Show all entries' ? 'Show fewer entries' : 'Show all entries';
			$(this).find('span').text(display);
			$(selector).dataTable().fnDraw();			
		});
    }   	    
	
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
 		//console.log(rowToggler);
 		return rowToggler;
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
                	//console.log(aData[j]);
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

