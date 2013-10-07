$(document).ready(function(){						

	$.fn.qTip('gene');	// bubble popup for brief panel documentation					
	//function to fire off a refresh of a table and it's dropdown filters

	var dropdownsList = new Array();
	// use jquery DataTable for table searching/sorting/pagination
	var aDataTblCols = [0,1,2,3,4,5,6];
	var oDataTable = $.fn.initDataTable($('table#phenotypes'), {
		"aoColumns": [
		              { "sType": "html", "mRender":function( data, type, full ) {
		            	  return (type === "filter") ? $(data).text() : data;
		              }},
		              { "sType": "html", "mRender":function( data, type, full ) {
		            	  return (type === "filter") ? $(data).text() : data;
		              }},
		              { "sType": "string"},
		              { "sType": "string"},
		              { "sType": "alt-string", "bSearchable" : false },
		              /*  { "sType": "string"}, */
		              { "sType": "html"}
		              , { "sType": "string", "bSortable" : false }

		              ],
		              "bDestroy": true,
		              "bFilter":false
	});

	$('[rel=tooltip]').tooltip();

	//$.fn.dataTableshowAllShowLess(oDataTable, aDataTblCols, null);
	$('div#exportIconsDiv').append($.fn.loadFileExporterUI({
		label: 'Export table as:',
		formatSelector: {
			TSV: 'tsv_phenoAssoc',
			XLS: 'xls_phenoAssoc'	    			 					
		},
		class: 'fileIcon'
	}));

	initFileExporter({
		mgiGeneId: '${gene.id.accession}',
		geneSymbol: '${gene.symbol}',
		externalDbId: 3,
		panel: 'phenoAssoc',
		fileName: 'phenotype_associations_of_gene-${gene.symbol}'
	});
	function initFileExporter(conf){

		$('button.fileIcon').click(function(){
			var fileType = $(this).text();
			var url = baseUrl + '/export';	    		
			var sInputs = '';
			for ( var k in conf ){
				sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>";	    			
			}
			sInputs += "<input type='text' name='fileType' value='" + fileType.toLowerCase() + "'>";

			$("<form action='"+ url + "' method=get>" + sInputs + "</form>").appendTo('body').submit().remove();    		
		}).corner('6px');	 
	}  


	function refreshPhenoTable(newUrl){
		//alert(newUrl);
		$.ajax({
			url: newUrl,
			cache: false
		}).done(function( html ) {
			$("#phenotypes_wrapper").html(html);//phenotypes wrapper div has been created by the original datatable so we need to replace this div with the new table and content
			var aDataTblCols = [0,1,2,3,4,5];
			var oDataTable = $.fn.initDataTable($('table#phenotypes'), {
				"aoColumns": [
				              { "sType": "html", "mRender":function( data, type, full ) {
				            	  return (type === "filter") ? $(data).text() : data;
				              }},
				              { "sType": "html", "mRender":function( data, type, full ) {
				            	  return (type === "filter") ? $(data).text() : data;
				              }},
				              { "sType": "string"},
				              { "sType": "alt-string", "bSearchable" : false },
				              /*  { "sType": "string"}, */
				              { "sType": "html"}
				              , { "sType": "string", "bSortable" : false }

				              ],
				              "bDestroy": true,
				              "bFilter":false
			});
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
	createDropdown(allDropdowns[0],"Source: All", allDropdowns);
	createDropdown(allDropdowns[1], "Procedure: All", allDropdowns);
	createDropdown(allDropdowns[2].sort(), "Gene: All", allDropdowns);


	function createDropdown(multipleSel, emptyText,  allDd){
		$(multipleSel).dropdownchecklist( { firstItemChecksAll: false, emptyText: emptyText, icon: {}, 
			minWidth: 150, onItemClick: function(checkbox, selector){
				var justChecked = checkbox.prop("checked");
				console.log("justChecked="+justChecked);
				console.log("checked="+ checkbox.val());
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
				// add current one and create dropdown object 
				dd1 = new Object();
				dd1.name = multipleSel.attr('id'); 
				dd1.array = values; // selected values
				dropdownsList[0] = dd1;
				
				var ddI  = 1; 
				for (var ii=0; ii<allDd.length; ii++) { 
					if ($(allDd[ii]).attr('id') != multipleSel.attr('id')) {
						console.log ("here " + allDd[ii].val() + " " + allDd[ii].attr('id'));
						dd = new Object();
						dd.name = allDd[ii].attr('id'); 
						dd.array = allDd[ii].val() || []; 
						dropdownsList[ddI++] = dd;
					}
				}
				console.log("call with " + dropdownsList.length);
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
		console.log("genesPhenFrag method (refreshGenesPhenoFrag) called with "+dropdownsList.length);
		var newUrl=rootUrl.replace("phenotypes", "geneVariantsWithPhenotypeTable");
		var output ='?';
		for (var it = 0; it < dropdownsList.length; it++){
			console.log(dropdownsList[it].array);
			if(dropdownsList[it].array.length==1){//if only one entry for this parameter then don't use brackets and or
				output+='&fq='+dropdownsList[it].name+':"'+dropdownsList[it].array+'"';
			} 
			if(dropdownsList[it].array.length>1)	{
				output+='&fq='+dropdownsList[it].name+':(\"' + dropdownsList[it].array.join("\"OR\"") + '\")';
			}			    			 
		}
		newUrl+=output;
		refreshPhenoTable(newUrl);
		return false;
	}
});


