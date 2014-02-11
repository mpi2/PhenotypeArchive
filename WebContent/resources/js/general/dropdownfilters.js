$(document).ready(function(){						

	// bubble popup for brief panel documentation
	$.fn.qTip({
		'pageName': 'gene',
		'textAlign': 'left',
		'tip': 'topRight'
	});							
	
    //function to fire off a refresh of a table and it's dropdown filters

	var selectedFilters = "";
	var dropdownsList = new Array();

	/* var oDataTable = $('table#phenotypes').dataTable();
						oDataTable.fnDestroy();  */
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

	//$('[rel=tooltip]').tooltip();
	//$.fn.dataTableshowAllShowLess(oDataTable, aDataTblCols, null);
	
	$('div#exportIconsDiv').append($.fn.loadFileExporterUI({
		label: 'Export table as:',
		formatSelector: {
			TSV: 'tsv_phenoAssoc',
			XLS: 'xls_phenoAssoc'	    			 					
		},
		class: 'fileIcon exportButton'
	}));

	var mgiGeneId = window.location.href.split("/")[window.location.href.split("/").length-1];
	var windowLocation = window.location;

	initFileExporter({
		mgiGeneId: mgiGeneId,
		externalDbId: 3,
		fileName: 'phenotype_associations_for_'+mgiGeneId.replace(/:/g,'_'),
		solrCoreName: 'genotype-phenotype',
		dumpMode: 'all',
		baseUrl: windowLocation,
		page:"gene",
		gridFields: 'marker_symbol,allele_symbol,zygosity,sex,procedure_name,resource_fullname,parameter_stable_id,phenotyping_center,marker_accession_id, parameter_name,parameter_name,mp_term_name',
		params: "qf=auto_suggest&defType=edismax&wt=json&rows=100000&q=*:*&fq=marker_accession_id:\"" + mgiGeneId +"\""
	});

	function initFileExporter(conf){
		$('button.fileIcon').click(function(){
			var fileType = $(this).text();
			var url = baseUrl + '/export';	 
			var sInputs = '';
			for ( var k in conf ){
				if (k == "params")
					sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + selectedFilters + "'>";	 
				else 
					sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>"; 
			}
			sInputs += "<input type='text' name='fileType' value='" + fileType.toLowerCase() + "'>";
			var form = $("<form action='"+ url + "' method=get>" + sInputs + "</form>");		
			_doDataExport(url, form);
		}); 
	}  

	function _doDataExport(url, form){
		$.ajax({
			type: 'GET',
			url: url,
			cache: false,
			data: $(form).serialize(),
			success:function(data){    				
				$(form).appendTo('body').submit().remove();
			},
			error:function(){
				//alert("Oops, there is error during data export..");
			}
		});
	}

	function refreshPhenoTable(newUrl){
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
			//alert('calling new table in genes.jsp');
		});
	}
	//http://stackoverflow.com/questions/5990386/datatables-search-box-outside-datatable
	//to move the input text or reassign the div that does it and hide the other one??
	//put filtering in another text field than the default so we can position it with the other controls like dropdown ajax filters for project etc
	
	//stuff for dropdown tick boxes here
	var allDropdowns = new Array();
	allDropdowns[0] = $('#top_level_mp_term_name');
	allDropdowns[1] = $('#resource_fullname');
	createDropdown(allDropdowns[0],"Top level MP: All", allDropdowns);
	createDropdown(allDropdowns[1], "Source: All", allDropdowns);

	function createDropdown(multipleSel, emptyText,  allDd){
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
				// add current one and create dropdown object 
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
	var previousParams = $("#filterParams").html();
	
	function refreshGenesPhenoFrag(dropdownsList) {
		var rootUrl=window.location.href;
		var newUrl=rootUrl.replace("genes", "genesPhenoFrag");
		var output ='?';
		selectedFilters = "";
		for (var it = 0; it < dropdownsList.length; it++){
//			console.log(dropdownsList[it].array);
			if(dropdownsList[it].array.length == 1){//if only one entry for this parameter then don't use brackets and or
				output += '&fq=' + dropdownsList[it].name + ':"' + dropdownsList[it].array+'"';
				selectedFilters += '+AND+' + dropdownsList[it].name + ':"' + dropdownsList[it].array+'"';
			} 
			if(dropdownsList[it].array.length > 1)	{
				output += '&fq='+dropdownsList[it].name+':(\"' + dropdownsList[it].array.join("\"OR\"") + '\")';
				selectedFilters += '+AND+'+dropdownsList[it].name+':(\"' + dropdownsList[it].array.join("\"OR\"") + '\")'; 
			}			    			 
		}
		newUrl+=output;
		refreshPhenoTable(newUrl);
		return false;
	}
	
	$(".filterTrigger").click(function() {
		//Do stuff when clicked
		//the id is set as the field to be filtered on
		//set the value of the current id of the trigger
		
		var filter=$(this).attr("id").replace("phenIconsBox_", "");
		var values = filter.split(" or ");
//		console.log ("filterTrigger" + values);
		$(allDropdowns[0]).val(values);
		$(allDropdowns[0]).dropdownchecklist("refresh");
		$(allDropdowns[1]).val([]);
		$(allDropdowns[1]).dropdownchecklist("refresh");
		var dropdownsList = new Array(); 
		
		var dd1 = new Object();
		dd1.name = allDropdowns[0].attr("id");
		dd1.array = new Array; // selected values
		dd1.array = values;
		dropdownsList[0] = dd1;
		
		var dd2 = new Object();
		dd2.name = allDropdowns[1].attr("id");
		dd2.array = []; //set array for second dropdown to empty so we get the same 
		dropdownsList[1] = dd2;

		refreshGenesPhenoFrag(dropdownsList);
	});

});
