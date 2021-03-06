$(document).ready(function(){						
	
	// bubble popup for brief panel documentation
	$.fn.qTip({
		'pageName': 'phenotypes',	
		'tip': 'top right',
		'corner' : 'right top'
	});

    initPhenoDataTable();
	removeFilterSelects();
	
	function initPhenoDataTable(){
            var aDataTblCols = [0,1,2,3,4,5,6,7,8];
            $('table#phenotypes').dataTable( {
           // 	$.fn.initDataTable($('table#phenotypes'), {
            	"aoColumns": [
            	              { "sType": "html", "mRender":function( data, type, full ) {
            	            	  return (type === "filter") ? $(data).text() : data;
            	              }},
            	              { "sType": "string"},
            	              { "sType": "string"},
            	              { "sType": "html", "mRender":function( data, type, full ) {
            	            	  return (type === "filter") ? $(data).text() : data;
            	              }},
            	              { "sType": "string"},
            	              { "sType": "string"},
            	              { "sType": "html"},
                              { "sType": "allnumeric", "aTargets": [ 3 ] },
                              { "sType": "string", "bSortable" : false }
                      ],
                "aaSorting": [[ 7, 'asc' ]],//sort by the p value on init

        		"bDestroy": true,
        		"bFilter":false,
        		"bPaginate":true,
                "sPaginationType": "bootstrap"
            });
        }
	
	function removeFilterSelects(){ // Remove selected options when going back to the page
		$("option:selected").removeAttr("selected");
	};
	
	// AJAX calls for the overview charts
	$('.oChart').each(function(i, obj) {
		$( '#spinner-overview-charts' ).show();
		var mp = $(this).attr('mp');
		var id = $(this).attr('parameter');
		var chartUrl = document.URL.split("/phenotypes/")[0];
		chartUrl += "/overviewCharts/" + mp + "?parameter_id=" + id;
		console.log("request uri = " + chartUrl);	
		$.ajax({
		  url: chartUrl,
		  cache: false
		})
		.done(function( html ) {
			$( '#spinner-overview-charts' ).hide();
			$( '#single-chart-div' ).html( html );		
			$( '#single-chart-div' ).attr("parameter", id);
		});
	});	 
	
	$( "#show_other_procedures" ).click(function() {
		$( "#other_procedures" ).toggle( "slow", function() {
		// Animation complete.
		 });
	});
	
	//function to fire off a refresh of a table and it's dropdown filters
	var selectedFilters = "";
	var dropdownsList = new Array();
	// use jquery DataTable for table searching/sorting/pagination
	
	//$.fn.dataTableshowAllShowLess(oDataTable, aDataTblCols, null);
	$('div#exportIconsDiv').html($.fn.loadFileExporterUI({
		label: 'Export table as:',
		textPos : "textright",
		formatSelector: {
			TSV: 'tsv_phenoAssoc',
			XLS: 'xls_phenoAssoc'	    			 					
		},
		class: 'fileIcon exportButton'
	}));

	var mpId = window.location.href.split("/")[window.location.href.split("/").length-1];
	mpId = mpId.split("#")[0];
	var windowLocation = window.location; 
        
	initFileExporter();

	function initFileExporter() {
		var conf = {
			mpId: "\"" + mpId+ "\"",
			externalDbId: 3,
			fileName: 'gene_variants_with_phen_'+mpId.replace(/:/g,'_'),
			solrCoreName: 'genotype-phenotype',
			dumpMode: 'all',
			baseUrl: windowLocation,
			page:"phenotype",
			params: ""
           };
            
            var exportObj = buildExportUrl(conf);                                   // Build the export url, page url, and form strings.
            $('div#exportIconsDiv').attr("data-exporturl", exportObj.exportUrl);    // Initialize the url.
            // WARNING NOTE: FILTER CHANGES DO NOT UPDATE data-exporturl; THUS, THE data-exporturl VALUE WILL BE OUT-OF-SYNC SHOULD
            // THE USER CHANGE FILTERS. THIS WILL LIKELY RESULT IN A HARD-TO-FIND BUG.
            // RECOMMENDATION: ANY FILTER CHANGES SHOULD TRIGGER AN UPDATE OF THE data-exporturl.
            
            $('button.fileIcon').click(function() {
                var exportObj = buildExportUrl(conf, $(this).text());                       // Build the export url, page url, and form strings.
                $('div#exportIconsDiv').attr("data-exporturl", exportObj.exportUrl);        // Update the url in case the filters changed.
                _doDataExport(exportObj.url, exportObj.form);
            }); 
	}
        
    function buildExportUrl(conf, fileType) {
       if (fileType === undefined){
            fileType = '';
       }
       var url = baseUrl + '/export';	 
       var sInputs = '';
       for ( var k in conf ){
            if (k === "params"){
                 sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + selectedFilters + "'>";
            }
            else {
                 sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>";
            }
       }
       sInputs += "<input type='text' name='fileType' value='" + fileType.toLowerCase() + "'>";
       var form = $("<form action='"+ url + "' method=get>" + sInputs + "</form>");	
       var exportUrl = url + '?' + $(form).serialize();
            
       var retVal = new Object();
       retVal.url = url;
       retVal.form = form;
       retVal.exportUrl = exportUrl;
       return retVal;
   }

    function _doDataExport(url, form){				
		$(form).appendTo('body').submit().remove();
	}
	function refreshPhenoTable(newUrl){
		//alert(newUrl);
		$.ajax({
			url: newUrl,
			cache: false
		}).done(function( html ) {
                    console.log('refreshPhenotable called');
			$("#phenotypes_wrapper").html(html);//phenotypes wrapper div has been created by the original datatable so we need to replace this div with the new table and content
			initPhenoDataTable();
		});
	}
	//http://stackoverflow.com/questions/5990386/datatables-search-box-outside-datatable
	//to move the input text or reassign the div that does it and hide the other one??
	//put filtering in another text field than the default so we can position it with the other controls like dropdown ajax filters for project etc

	//stuff for dropdown tick boxes here
	var allDropdowns = new Array();
	allDropdowns[0] = $('#resource_fullname');
	allDropdowns[1] = $('#procedure_name');
	allDropdowns[2] = $('#marker_symbol');
	allDropdowns[3] = $('#mp_term_name');
	createDropdown(allDropdowns[3].sort(), "Phenotype: All", allDropdowns);
	createDropdown(allDropdowns[0],"Source: All", allDropdowns);
	createDropdown(allDropdowns[1], "Procedure: All", allDropdowns);
	createDropdown(allDropdowns[2].sort(), "Gene: All", allDropdowns);
	
	function createDropdown(multipleSel, emptyText,  allDd){
		console.log("called phen createDropdown "+ multipleSel);
		$(multipleSel).dropdownchecklist( { firstItemChecksAll: false, emptyText: emptyText, icon: {}, 
			minWidth: 150, onItemClick: function(checkbox, selector){
				var justChecked = checkbox.prop("checked");
//				console.log("justChecked="+justChecked);
//				console.log("checked="+ checkbox.val());
				var values = [];
				for(var  i=0; i < selector.options.length; i++ ) {
					if (selector.options[i].selected && (selector.options[i].value != "")) {
						values .push(selector.options[i].value);
					}
				}

				if(justChecked){				    		 
					values.push( checkbox.val());
				}else{//just unchecked value is in the array so we remove it as already ticked
					var index = $.inArray(checkbox.val(), values);
					values.splice(index, 1);
				}  
				
				console.log("values="+values );
				// add current one and create drop down object 
				dd1 = new Object();
				dd1.name = multipleSel.attr('id'); 
				dd1.array = values; // selected values
				
				dropdownsList[0] = dd1;
				
				var ddI  = 1; 
				for (var ii=0; ii<allDd.length; ii++) { 
					if ($(allDd[ii]).attr('id') != multipleSel.attr('id')) {
//						console.log ("here " + allDd[ii].val() + " " + allDd[ii].attr('id'));
						dd = new Object();
						dd.name = allDd[ii].attr('id'); 
						dd.array = allDd[ii].val() || []; 
						dropdownsList[ddI++] = dd;
					}
				}
//				console.log("call with " + dropdownsList.length);
				refreshGenesPhenoFrag(dropdownsList);
			}, textFormatFunction: function(options) {
				var selectedOptions = options.filter(":selected");
		        var countOfSelected = selectedOptions.size();
		        var size = options.size();
		        var text = "";
		        if (size > 1){
		        	options.each(function() {
	                    if ($(this).prop("selected")) {
	                        if ( text != "" ) { text += ", "; }
	                        /* NOTE use of .html versus .text, which can screw up ampersands for IE */
	                        var optCss = $(this).attr('style');
	                        var tempspan = $('<span/>');
	                        tempspan.html( $(this).html() );
	                        if ( optCss == null ) {
	                                text += tempspan.html();
	                        } else {
	                                tempspan.attr('style',optCss);
	                                text += $("<span/>").append(tempspan).html();
	                        }
	                    }
	                });
		        }
		        switch(countOfSelected) {
		           case 0: return emptyText;
		           case 1: return selectedOptions.text();
		           case options.size(): return emptyText;
		           default: return text;
		        }
			}
		} );
	}
	
	//if filter parameters are already set then we need to set them as selected in the dropdowns
	var previousParams=$("#filterParams").html();
	
	function refreshGenesPhenoFrag(dropdownsList) {
		var rootUrl=window.location.href;
		var newUrl=rootUrl.replace("phenotypes", "geneVariantsWithPhenotypeTable").split("#")[0];
		var output ='?';
		selectedFilters = "";
		for (var it = 0; it < dropdownsList.length; it++){
			if(dropdownsList[it].array.length == 1){//if only one entry for this parameter then don't use brackets and or
				selectedFilters += '&fq=' + dropdownsList[it].name + ':"' + dropdownsList[it].array+'"';
			} 
			if(dropdownsList[it].array.length > 1)	{
				selectedFilters += '&fq='+dropdownsList[it].name+':(\"' + dropdownsList[it].array.join("\"OR\"") + '\")';
			}			    			 
		}
		newUrl+=output + selectedFilters;
		refreshPhenoTable(newUrl);
        console.log('refresh genes PhenoFrag called woth new url='+newUrl);
		return false;
	}
});


function ajaxToBe(phenotype, parameter){
	$( '#spinner-overview-charts' ).show();
	var chartUrl = document.URL.split("/phenotypes/")[0];
	chartUrl += "/overviewCharts/" + phenotype + "?parameter_id=" + parameter;
	$.ajax({
	  url: chartUrl,
	  cache: false
	})
	.done(function( html ) {
		$( '#spinner-overview-charts' ).hide();
		$( '#single-chart-div' ).html( html );
		$( '#single-chart-div' ).attr("parameter", parameter);
	});
	
}



/* new sorting functions */
//http://datatables.net/forums/discussion/5894/datatable-sorting-scientific-notation
jQuery.fn.dataTableExt.oSort['allnumeric-asc']  = function(a,b) {
          var x = parseFloat(a);
          var y = parseFloat(b);
          return ((x < y) ? -1 : ((x > y) ?  1 : 0));
        };
 
jQuery.fn.dataTableExt.oSort['allnumeric-desc']  = function(a,b) {
          var x = parseFloat(a);
          var y = parseFloat(b);
          return ((x < y) ? 1 : ((x > y) ?  -1 : 0));
        };
 