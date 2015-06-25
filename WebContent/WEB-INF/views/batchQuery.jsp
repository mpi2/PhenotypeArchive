<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">IMPC dataset batch query</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/batchQuery">&nbsp;Batch query</a></jsp:attribute>
    <jsp:attribute name="header">
    
        <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
        
        <style type="text/css">

            div#tableTool {
                position: relative;
                top: -70px;
                right: 0px;
            }
           
            table.dataTable span.highlight {
                background-color: yellow;
                font-weight: bold;
                color: black;
            }
           
            .hideMe {
                display: none;
            }
            .showMe {
                display: block;
            }
            .alleleToggle {
                cursor: pointer;
                font-size: 11px;
                font-weight: bold;
            }
            div#saveTable {
                top: 34px;
                left: -25px;
            }
            div#toolBox {
                top: -58px;
                right: 35px;
            }
            #pasteList {
            	font-size: 12px;
            }
            .notes {
            	font-size: 10px;
            }
            #accordion {
            	font-size: 11px;
            	margin-bottom: 20px;
            	 background-color: white;
            	/*height: 80px;*/
            }
            #pastedList, #srcfile {
            	padding-left: 0;
            }
            #srcfile  {
            	border: 0;
            }
            .lbl {
            	font-size: 12px;
            	font-weight: bold;
            }
            div#query {
            	font-size: 11px !important;
            }
            td.idnote {
            	font-size: 12px;
            	padding-left: 20px;
            	height: 40px;
            	background-color: "#F2F2F2";
            }
            .cat {
            	font-weight: bold;
            	font-size:14px;
            	color: black;
            	padding: 5px;
            }
            .inner {
			   height: auto;
			   margin-bottom: 15px;
			  
			}
			div#sec2 {
				display: none;
			}
			.fl2 {
			    width: 52%;
			    float: right;
			    padding: 0 20px 20px 20px;
			}
			.fl1 {
			    float: none; /* not needed, just for clarification */
			    /* the next props are meant to keep this block independent from the other floated one */
			    width: 40%;
			    padding-right: 30px;
			    border-right: 1px solid #C1C1C1;
			}
			h6.bq {
				color: gray;
			}
			div#errBlock {
				margin-bottom: 10px;
				display: none;
				color: #8B0A50;
			}
			hr {
				color: #C1C1C1;
			}
			button#chkfields {
				margin-top: 10px;
				display: block;
			}
			table.dataTable {
			   overflow-x:scroll;
			   width:100%;
			   display:block;
			}
			input[type=checkbox] {
			   	height: 25px;
				vertical-align: middle;
			}
			div#tableTool {
				float: right;
				margin-top: 40px;
				clear: right;
			}
			#srchBlock {
				background-color: white;
			}
			div#infoBlock {
				margin-bottom: 10px;
			}
			form#dnld {
				margin: 0;
				padding: 0;
				border: none;
			}
			span#resubmit {
				font-size: 12px;
				padding-left: 20px;
				color: #8B0A50;
			}
			form#dnld {
            	padding: 5px;
            	border-radius: 5px;
			    background: #F2F2F2;
            }
            form#dnld button {
            	text-decoration: none;
            }
            table#dataInput td {
            	text-align: left;
            	vertical-align: middle;
            }
          	table#dataInput td.idnote {
          		padding-left: 8px;
          	} 
          	div.textright {
          		padding: 5px 0;
          	}
          	div.qtipimpc {
          		padding: 30px;
          		background-color: white;
          		border: 1px solid gray;
          		/*height: 250px;
          		overflow-x: hidden;*/
          	}
          	div.qtipimpc p {
          		font-size: 12px;
          		line-height: 20px;
          	}
          	div.tipSec {
          		font-weight: bold;
          		font-size: 14px;
          		padding: 5px;
          		background-color: #F2F2F2;
          	}
          	div#docTabs {
        		border: none;
        	}
        	ul.ui-tabs-nav {
        		border: none;
        		border-bottom: 1px solid #666;
        		background: none;
        	}
        	ul.ui-tabs-nav li:nth-child(1) {
        		font-size: 14px;
        	}
        	ul.ui-tabs-nav li a {
        		margin-bottom: -1px;
        		border: 1px solid #666;
        		font-size: 12px;
        	}
        	ul.ui-tabs-nav li a:hover {
        		color: white;
        		background-color: gray; 
        	}
        	a#ftp {
        		color: #0978a1 !important;
        		text-decoration: none;
        	}
          	
        </style>
        
        <script type='text/javascript'>
        
            $(document).ready(function () {
                'use strict';
             	// test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';
                
                var baseUrl = "${baseUrl}";
                var solrUrl = "${internalSolrUrl}";
                
                $('div#batchQryLink').hide(); // hide batchquery link for batchquery page
                
                
               	// initialze search example qTip with close button and proper positioning
               	var bqDoc = '<h3 id="bqdoc">How to use batchQuery</h3>'
               	
               		+ '<div id="docTabs">'
                	+ '<ul>'
                  	+ '<li><a href="#tabs-1">Interface</a></li>'
                  	+ '<li><a href="#tabs-2">Data fields</a></li>'
                	+ '</ul>'
                	+ '<div id="tabs-1">'
                	+ '<p>Query keywords can be either datatype-specific ID or IMPC marker symbol.<p>'
      				+ '<p>Simply click on one of the radio buttons on the left (the<b> Datatype Input panel</b>) to choose the datatype you want to search for.'
      				+ '</p>'
      				+ '<p>The data fields for the chosen datatype will be shown dynamically on the right (the <b>Customized Output panel</b>) and can be added/removed using checkboxes.'
      				+ '<p>'
      				+ '<p>The sample of results (<b>maximum of 10 records</b>) will be updated automatically after checking checkboxes.'
      				+ '<p>'
                	+ '</div>'
                	+ '<div id="tabs-2">'
                	+ '<p>The data fields in additional annotations of the customized output panel are based on their being annotated to a datatype of your search.</p>'
      				+ '<p>For example, an MP term is annotated to an IMPC gene via phenotypic observations or experiments.</p>'
      				+ '<p>A disease term (human disease) is annotated to an IMPC mouse phenotype via <a href="http://database.oxfordjournals.org/content/2013/bat025" target="_blank">Phenodigm</a>, which is a semantic approach to map between clinical features observed in humans and mouse phenotype annotations.</p>'
      				+ '<p>An HP term is mapped to an MP term using similar Phenodigm semantic approach.</p>'  
                	+ '</div>'
                 	+ '</div>';
               	
                $("a#bqdoc").qtip({            	   
                   	hide: false,
        			content: {
        				text: bqDoc,
        				title: {'button': 'close'}
        			},		 	
       			 	style: {
       			 		classes: 'qtipimpc',	 		
       			        tip: {corner: 'center top'}
       			    },
       			    position: {my: 'left top',
       			    		   adjust: {x: -350, y: 15}
       			    },
       			 	show: {
       					event: 'click' //override the default mouseover
       				},
       				events: {
       			        show: function(event, api) {
       			        	$('div#docTabs').tabs();
       			         	$('ul.ui-tabs-nav li a').click(function(){
       	            	   		$('ul.ui-tabs-nav li a').css({'border-bottom':'none', 'background-color':'#F4F4F4', 'border':'none'});
       	            	   		$(this).css({'border':'1px solid #666', 'border-bottom':'1px solid white', 'background-color':'white', 'color':'#666'});
       	               		});
       	               
       	               		$('ul.ui-tabs-nav li:nth-child(1) a').click();  // activate this by default
       			        }
       			    }
                });
                
                $( "#accordion" ).accordion();
                
                // reset to default when page loads
                $('input#gene').prop("checked", true) // check datatyep ID as gene by default 
                $('input#datatype').val("gene"); // default
                //$('div#fullDump').html("<input type='checkbox' id='fulldata' name='fullDump' value='gene'>Export full IMPC dataset via GENE identifiers");
                
                
                freezeDefaultCheckboxes();
                //chkboxAllert();  for now, don't want automatic resubmit each time a checkbox is clicked
                var currDataType  = false;
                
                toggleAllFields();
                
                // fetch dynamic data fields as checkboxes
                $('input.bq').click(function(){
                	if ( $(this).is(':checked') ){
                		
                		currDataType = $(this).attr('id');
                		
                		var currDataType2 = currDataType.toUpperCase().replace("_"," ");
                		
                		// assign to hidden field in fileupload section
                		$('input#datatype').val(currDataType);
                		
                		$('td.idnote').text($(this).attr("value"));
                		//console.log($(this).attr('id'));
                		var id = $(this).attr('id');
                		//$('div#fullDump').html("<input type='checkbox' id='fulldata' name='fullDump' value='" + id + "'>" + "Export full IMPC dataset via " + currDataType2 + " identifiers");
                		$('div#fullDump').html("Please refer to our FTP site");
                		// load dataset fields for selected datatype Id
                		$.ajax({
                        	url: baseUrl + '/batchquery2?core=' + currDataType,
                            success: function(htmlStr) {
                                //console.log('htmlStr');
                            	$('div#fieldList').html(htmlStr);
                            	freezeDefaultCheckboxes();
                            	toggleAllFields();
                            	//chkboxAllert();
                            },
                            error: function() {
                                window.alert('AJAX error trying to register interest');
                            }
                        });
                	} 
                });
                
                $('textarea#pastedList').val(''); // reset
                $('input#fileupload').val(''); // reset
                $('input#fulldata').attr('checked', false); // reset
                
            });
            
            function chkboxAllert() {
            	// resubmit automatically whenever checkbox is clicked
	        	$("div.fl2").find("input[class!='default']").click(function(){
	        		resubmit();
	        	});
            }
            function freezeDefaultCheckboxes(){
            	$('input.default').click(function(){
        			return false;
        		})
            }
            
            function toggleAllFields(){
                $('button#chkFields').click(function(){
                	if ( $(this).hasClass('checkAll') ){
                		$(this).removeClass('checkAll').html('Check all fields');
                		$("div.fl2").find("input[type='checkbox']").prop('checked', false);
                		$("div.fl2").find("input[class='default']").prop('checked', true);
                	}
                	else {
                		$(this).addClass('checkAll').html('Reset to default fields')
                		$("div.fl2").find("input[type='checkbox']").prop('checked', true);
                	}
                	
                	//resubmit();
            	});
            }
            
            function resubmit(){
            	$('div#accordion').find('form:visible').find("input[type='submit']").click();
            }
            
            function submitPastedList(){
	           
            	refreshResult(); // refresh first
            	
            	if ( $('textarea#pastedList').val() == ''){
            		alert('Please submit at least one identifier (ID/marker symbol).');
            	}
            	else { 
            		var currDataType = $('input.bq:checked').attr('id');
            		idList = parsePastedList($('textarea#pastedList').val(), currDataType);
            		
            		if ( idList !== false ){
            			
            			var fllist = fetchSelectedFieldList();
                     	var currDataType = $('input.bq:checked').attr('id');
                     	
                     	prepare_dataTable(fllist);
                     	
                     	var oConf = {};
                     	oConf.idlist = idList;
                     	oConf.fllist = fllist;
                     	oConf.corename = currDataType;
                     	
                     	fetchBatchQueryDataTable(oConf);
            		}
            	}
            	return false;
            }
            
            function refreshResult(){
            	$('div#infoBlock, div#errBlock, div#bqResult').html(''); // refresh first
            	$('div#infoBlock').html("Your datatype of search: " + $('input.bq:checked').attr('id').toUpperCase());
            }
            
            function uploadJqueryForm(){
            	
            	refreshResult(); // refresh first
            	
            	var currDataType = $('input.bq:checked').attr('id');
             	$('input#dtype').val(currDataType);
             	
            	if ( $('input#fileupload').val() == '' ){
            		alert("Please upload a file with a list of identifiers");
            	}
            	else {
	               	$('#bqResult').html('');
	               	
	               	$("#ajaxForm").ajaxForm({
	                	success:function(jsonStr) { 
	                      	//$('#bqResult').html(idList);
	                      	//console.log(jsonStr)
	                      	var j = JSON.parse(jsonStr);

	                      	if ( j.badIdList != ''){
	                      		$('div#errBlock').html("UPLOAD ERROR: unprocessed identifier(s): " + j.badIdList).show();
	                      	}
	                      		
	                		var fllist = fetchSelectedFieldList();
	                     	prepare_dataTable(fllist);
	                     	
	                     	var oConf = {};
	                     	oConf.idlist = j.goodIdList;
	                     	oConf.fllist = fllist;
	                     	oConf.corename = currDataType;
	                     	
	                     	fetchBatchQueryDataTable(oConf);
	                 	},
	                 	dataType:"text"
	               	}).submit();
            	}
            	return false; // so that the form can only be submitted via ajax
            }
            
            function fetchFullDataset(){
            	
            	 if ( $('input#fulldata').is(':checked') ){
            		refreshResult(); // refresh first 
            		
            		var fllist = fetchSelectedFieldList();
                 	var currDataType = $('input.bq:checked').attr('id') == 'marker_symbol' ? 'gene' : $('input.bq:checked').attr('id');
                 	
                 	prepare_dataTable(fllist);
                 	
                 	var oConf = {};
                 	oConf.idlist = '*';
                 	oConf.fllist = fllist;
                 	oConf.corename = currDataType;
                 	
                 	fetchBatchQueryDataTable(oConf);
                 }
                 else {
                 	alert ("Please tick the checkbox to fetch the full dataset");
                 }
                 return false;
            }
            
            function fetchSelectedFieldList(){
            	var fllist = [];
            	$("div#fieldList input:checked").each(function(){
            		fllist.push($(this).val());
            	});
            	return fllist.join(",");
            }
            
            function prepare_dataTable(fllist){
            	
            	var flList = fllist.split(',');
            	
            	var th = '';
            	for ( var i=0; i<flList.length; i++){
            		th += "<th>" + flList[i] + "</th>";
            	}
            	
            	var tableHeader = "<thead>" + th + "</thead>";
            	var tableCols = flList.length;
            	
                var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "batchq");
                
                $('div#bqResult').append(dTable);
            }
            
            function parsePastedList(val, dataType){
            	val = val.trim();
            	var aVals = val.split(/\n|,|\t|\s+/);
            	var aVals2 = [];
            	for ( var i=0; i<aVals.length; i++){
            		if ( aVals[i] == "" ){
            			continue;
            		}
            		var currId = aVals[i].toUpperCase().trim();
            		var errMsg = "ERROR - " + currId + " is not an expected " + dataType + " identifier. Please try changing the datatype input.";
            		
            		if ( dataType == 'disease' ){
            			if ( ! (currId.indexOf('OMIM') == 0 ||  
            				 currId.indexOf('ORPHANET') == 0 || 
            				 currId.indexOf('DECIPHER') == 0) ){
            			
                			alert(errMsg);
                			return false;
                		}
            		}
            		else if ( dataType == 'gene' && currId.indexOf('MGI:') != 0 ){
            			alert(errMsg);
            			return false;
            		}
            		else if ( dataType == 'ensembl' && currId.indexOf('ENSMUSG') != 0 ){
            			alert(errMsg);
            			return false;
            		}
            		else if ( (dataType == 'mp' || dataType == 'ma' || dataType == 'hp' ) && currId.indexOf(dataType.toUpperCase()) !== 0 ){
            			
                		alert(errMsg);
                		return false;
            		}
            		
            		aVals2.push('"' + currId + '"');
            	}
            	
        		return aVals2.join(",");
        	}
        	
            function fetchBatchQueryDataTable(oConf) {
            	
            	//var aDataTblCols = [0,1,2,3,4,5];
                var oTable = $('table#batchq').dataTable({
                    "bSort": true, // true is default 
                    "processing": true,
                    "paging": false,
                    //"serverSide": false,  // do not want sorting to be processed from server, false by default
                    "sDom": "<<'#exportSpinner'>l<f><'#tableTool'>r>tip",
                    "sPaginationType": "bootstrap",
                    "searchHighlight": true,
                    "iDisplayLength": 50,
                    "oLanguage": {
                        "sSearch": "Filter: ",
                        "sInfo": "Showing _START_ to _END_ of _TOTAL_ genes (10 records maximum, for complete dataset of your search, please use export buttons)"
                    },
                   /*  "columnDefs": [                
                        { "type": "alt-string", targets: 3 }   //4th col sorted using alt-string         
                    ], */
                    "aaSorting": [[ 0, "asc" ]],  // default sort column order
                    /*"aoColumns": [
                        {"bSearchable": true, "sType": "html", "bSortable": true},
                        {"bSearchable": true, "sType": "string", "bSortable": true},
                        {"bSearchable": true, "sType": "string", "bSortable": true},
                        {"bSearchable": true, "sType": "string", "bSortable": true},
                        {"bSearchable": true, "sType": "string", "bSortable": true},
                        {"bSearchable": false, "sType": "html", "bSortable": true}
                    ],*/
                    "fnDrawCallback": function (oSettings) {  // when dataTable is loaded

                    	$('div#sec2').show();
                    
                    	document.getElementById('sec2').scrollIntoView(true); // scrolls to results when datatable loads
                    
                    	var endPoint = baseUrl + '/bqExport';	
                    	
                        $('div#tableTool').html("<span id='expoWait'></span><form id='dnld' method='POST' action='" + endPoint + "'>"
                        		+ "<span class='export2'>Export as</span>"
                        		+ "<input name='coreName' value='' type='hidden' />"
                        		+ "<input name='fileType' value='' type='hidden' />"
                        		+ "<input name='gridFields' value='' type='hidden' />"
                        		+ "<input name='idList' value='' type='hidden' />"
                        		+ "<button class='tsv fa fa-download gridDump'>TSV</button>"
                        		+ " or<button class='xls fa fa-download gridDump'>XLS</button>"
                        		+ "</form>");
                   		
                   		$('button.gridDump').click(function(){
                    		
                    		var fllist = fetchSelectedFieldList();
                    		var errMsg = 'AJAX error trying to export dataset';
                    		var currDataType = $('input.bq:checked').attr('id');
                    		var idList = null;
                    		var fileType = $(this).hasClass('tsv') ? 'tsv' : 'xls';
                    		
                    		var formId = $('div#accordion').find('form:visible').attr('id');   
                    		
                    		if ( formId == 'ajaxForm' ){ 
                    			$("#ajaxForm").ajaxForm({
                    				url: baseUrl + '/batchQuery?dataType=' + currDataType,
            	                	success:function(jsonStr) { 
            	                		var j = JSON.parse(jsonStr);
            	                		idList = j.goodIdList;
            	                		doExport(currDataType, fileType, fllist, idList, true );
            	                 	},
            	                 	dataType:"text",
            	                 	type: 'POST',
            	               	}).submit();
                    		}
                    		else if ( formId == 'pastedIds' ){
                    			idList = parsePastedList($('textarea#pastedList').val(), currDataType);
                    			doExport(currDataType, fileType, fllist, idList);
                    		}
                    		else {
                    			idList = '*';
                    			doExport(currDataType, fileType, fllist, idList);
                    		}
                    		
                    		if (formId == 'ajaxForm' ){
                    			return false; 
                    		}
                   		});
                        
                        $('body').removeClass('footerToBottom'); 
                    },
                    "ajax": {
                        "url": baseUrl + "/dataTable_bq?",
                        "data": oConf,
                        "type": "POST",
                        "error": function() {
                            $('div.dataTables_processing').text("AJAX error trying to fetch your query");
                            $('td.dataTables_empty').text("");
                        }
                    }
                });
            }
            function doExport(currDataType, fileType, fllist, idList, isForm){
            	
            	$("form#dnld input[name='coreName']").val(currDataType);
        		$("form#dnld input[name='fileType']").val(fileType);
        		$("form#dnld input[name='gridFields']").val(fllist);
        		$("form#dnld input[name='idList']").val(idList);
        		
        		if ( isForm ) {
        			$("form#dnld").submit();
        		}
            }
            
        </script>
       
        <script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.form.js"></script>
        <script type='text/javascript' src='https://bartaz.github.io/sandbox.js/jquery.highlight.js'></script>  
        <script type='text/javascript' src='https://cdn.datatables.net/plug-ins/f2c75b7247b/features/searchHighlight/dataTables.searchHighlight.min.js'></script>  
        <script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>  

    </jsp:attribute>

    <jsp:attribute name="addToFooter">	
        <div class="region region-pinned">

        </div>		

    </jsp:attribute>

    <jsp:body>		
		<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">IMPC Dataset Batch Query</h1>
							 
						<div class="textright">
							<a id="bqdoc" class="">Help</a>						
						</div>	
						
						<div class="section">
							<!--  <h2 id="section-gostats" class="title ">IMPC Dataset Batch Query</h2>-->
							<div class='inner' id='srchBlock'>
								
								<div class='fl2'>
									
						     		<h6 class='bq'>Customized Output</h6>
						     		<div id='fieldList'>${outputFields}</div>
						     	</div>
						     	
							   	<div class='fl1'>
							   		<h6 class='bq'>Datatype Input</h6>
										<div id='query'>
											<table id='dataInput'><tr>
											<td><span class='cat'>ID:</span></td>
										  	<td><input type="radio" id="gene" value="MGI:106209" name="dataType" class='bq' checked="checked" >IMPC Gene
										  	<input type="radio" id="ensembl" value="ENSMUSG00000011257" name="dataType" class='bq'>Ensembl Gene
										  	<input type="radio" id="mp" value="MP:0001926" name="dataType" class='bq'>MP
										  	<input type="radio" id="hp" value="HP:0003119" name="dataType" class='bq'>HP<br>
										  	<input type="radio" id="disease" value="OMIM:100300 or ORPHANET:1409 or DECIPHER:38" name="dataType" class='bq'>OMIM / ORPHANET / DECIPHER
										  	<input type="radio" id="ma" value="MA:0000141" name="dataType" class='bq'>MA</td></tr>
										  	<tr><td><span class='cat'>Symbol:</span></td>
										  	<td><input type="radio" id="marker_symbol" value="Car4 or CAR4 (case insensitive). Synonym search supported" name="dataType" class='bq'>Marker Symbol</td>
										  	<tr><td><span class='cat'>Example:</span></td><td class='note idnote'>MGI:106209</td></tr>
										  	</table>
										  	
										  	<div id="accordion">
											  <p class='header'>Paste in your list</p>
											  <div>
											    <p>
											     <form id='pastedIds'>
											     	<textarea id='pastedList' rows="5" cols="50"></textarea> 
											 		<input type="submit" id="pastedlist" name="" value="Submit" onclick="return submitPastedList()" />
											 		<input type="reset" name="reset" value="Reset"><p>
											 		<p class='notes'>Supports space, comma, tab or new line separated identifier list</p>
											     	<p class='notes'>Please DO NOT submit a mix of identifiers from different datatypes</p>	
											   	 </form>
											    </p>
											  </div>
											  <p class='header'>Upload your list from file</p>
											  <div>
											  
											    <form id="ajaxForm" method="post" action="${baseUrl}/batchQuery" enctype="multipart/form-data">
													  <!-- File input -->    
													  <input name="fileupload" id="fileupload" type="file" /><br/>
													  <input name="dataType" id="dtype" value="" type="hidden" /><br/>
													  <input type="submit" id="upload" name="upload" value="Upload" onclick="return uploadJqueryForm()" />
													  <input type="reset" name="reset" value="Reset"><p>
													  <p class='notes'>Supports comma, tab or new line separated identifier list</p>
												      <p class='notes'>Please DO NOT submit a mix of identifiers from different datatypes</p>  
												</form>
												
											  </div>
											  <p class='header'>Full dataset</p>
											  <form>
											  	<div id='fullDump'></div>
											  	Please use our <a id='ftp' href='ftp://ftp.ebi.ac.uk/pub/databases/impc/' target='_blank'>FTP</a> site for large dataset.
											  	<!-- <input type="submit" id="fulldata" name="" value="Submit" onclick="return fetchFullDataset()" /><p> -->
											  </form>
											</div>
										  	
										</div>
										
								    </div>
						     	</div>	
						     		
								<div style="clear: both"></div>
								
							</div>
							
							</div><!-- end of section -->
							<div class="section" id="sec2">
								<h2 id="section-gotable" class="title ">Batch Query Result</h2>
								
								<div class="inner">
									<div id='infoBlock'></div>
									<div id='errBlock'></div>
								 	<div id='bqResult'></div>
								 	
								</div>	
							</div>	
						</div>
				</div>
			</div>
		</div>  

    </jsp:body>
</t:genericpage>

