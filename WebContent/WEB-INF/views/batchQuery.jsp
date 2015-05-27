<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">IMPC dataset batch query</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/batchQuery">&nbsp;Batch query</a></jsp:attribute>
    <jsp:attribute name="header">
        <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
        <link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />
        <link href="${baseUrl}/css/searchPage.css" rel="stylesheet" />
        
        <style type="text/css">

            div#tableTool {
                position: relative;
                top: -70px;
                right: 0px;
            }
            div#alleleRef_filter {
            	float: left;
            	clear: right;
            }
            table.dataTable span.highlight {
                background-color: yellow;
                font-weight: bold;
                color: black;
            }
            table#alleleRef {
            	clear: left;
            }
            table#alleleRef th:first-child, table#alleleRef th:nth-child(2) {
                width: 150px !important;
            }
            table#alleleRef th:nth-child(3) {
                width: 80px !important;
            }
            table#alleleRef td {
                font-size: 14px !important;
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
            p.idnote {
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
        </style>
        
        <script type='text/javascript'>
        
            $(document).ready(function () {
                'use strict';
             	// test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';
                
                var baseUrl = "${baseUrl}";
                var solrUrl = "${internalSolrUrl};"
                
                $( "#accordion" ).accordion();
                
                // reset to default when page loads
                $('input#gene').prop("checked", true) // check datatyep ID as gene by default 
                $('input#datatype').val("gene"); // default
                $('div#fullDump').html("<input type='checkbox' id='fulldata' name='fullDump' value='gene'>Export full GENE dataset");
                freezeDefaultCheckboxes();
                chkboxAllert();
                var currDataType  = false;
                
                toggleAllFields();
                
                $('input.bq').click(function(){
                	if ( $(this).is(':checked') ){
                		
                		currDataType = $(this).attr('id');
                		
                		// assign to hidden field in fileupload section
                		$('input#datatype').val(currDataType);
                		
                		$('p.idnote').text("For example: " + $(this).attr("value"));
                		//console.log($(this).attr('id'));
                		var id = $(this).attr('id');
                		$('div#fullDump').html("<input type='checkbox' id='fulldata' name='fullDump' value='" + id + "'>" + "Export full " + $(this).attr('id').toUpperCase() + " dataset");
                		
                		// load dataset fields for selected datatype Id
                		$.ajax({
                        	url: baseUrl + '/batchquery2?core=' + currDataType,
                            success: function(htmlStr) {
                                //console.log('htmlStr');
                            	$('div#fieldList').html(htmlStr);
                            	freezeDefaultCheckboxes();
                            	toggleAllFields();
                            	chkboxAllert();
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
                	
                	resubmit();
            	});
            }
            
            function resubmit(){
            	$('div#accordion').find('form:visible').find("input[type='submit']").click();
            }
            
            function submitPastedList(){
	           
            	refreshResult(); // refresh first
            	
            	if ( $('textarea#pastedList').val() == ''){
            		alert('Please submit at least one ID.');
            	}
            	else { 
            		var currDataType = $('input.bq:checked').attr('id');
            		idList = parsePastedpastedList($('textarea#pastedList').val(), currDataType);
            		
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
            		alert("Please upload a file with a list of IDs");
            	}
            	else {
	               	$('#bqResult').html('');
	               	
	               	$("#ajaxForm").ajaxForm({
	                	success:function(jsonStr) { 
	                      	//$('#bqResult').html(idList);
	                      	//console.log(jsonStr)
	                      	var j = JSON.parse(jsonStr);

	                      	if ( j.badIdList != ''){
	                      		$('div#errBlock').html("UPLOAD ERROR: unprocessed ID(s): " + j.badIdList).show();
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
                 	var currDataType = $('input.bq:checked').attr('id');
                 	
                 	prepare_dataTable(fllist);
                 	
                 	var oConf = {};
                 	oConf.idlist = '*';
                 	oConf.fllist = fllist;
                 	oConf.corename = currDataType;
                 	
                 	fetchBatchQueryDataTable(oConf);
                 }
                 else {
                 	alert ("Please tick the checkbox to fetch the full datasete");
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
            
            function parsePastedpastedList(val, dataType){
            	var aVals = val.split(/\n|,|\t|\s+/);
            	var aVals2 = [];
            	for ( var i=0; i<aVals.length; i++){
            		
            		var currId = aVals[i].toUpperCase().trim();
            		
            		if ( dataType == 'disease' ){
            			if ( ! (currId.indexOf('OMIM') == 0 ||  
            				 currId.indexOf('ORPHANET') == 0 || 
            				 currId.indexOf('DECIPHER') == 0) ){
            			
                			alert("ERROR - " + currId + " is not a member of " + dataType + " datatype");
                			return false;
                		}
            		}
            		else if ( dataType == 'gene' && currId.indexOf('MGI') != 0 ){
            			alert("ERROR - " + currId + " is not a member of " + dataType + " datatype");
            			return false;
            		}
            		else if ( (dataType == 'mp' || dataType == 'hp') && currId.indexOf(dataType.toUpperCase()) !== 0 ){
            			
                		alert("ERROR - " + currId + " is not a member of " + dataType + " datatype");
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
                        "sSearch": "Filter: "
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
                    	var endPoint = baseUrl + '/bqExport';	
                    	
                        $('div#tableTool').html("<span id='expoWait'></span><form id='dnld' method='POST' action='" + endPoint + "'>"
                        		+ "<span class='export2'>Export as</span>"
                        		+ "<input name='coreName' value='' type='hidden' />"
                        		+ "<input name='fileType' value='' type='hidden' />"
                        		+ "<input name='gridFields' value='' type='hidden' />"
                        		+ "<input name='idList' value='' type='hidden' />"
                        		+ "<button class='tsv fa fa-download gridDump gridDump'>TSV</button>"
                        		+ " or<button class='xls fa fa-download gridDump gridDump'>XLS</button>"
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
                    			idList = parsePastedpastedList($('textarea#pastedList').val(), currDataType);
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
                        "type": "POST"
                    }
                });
            }
            function doExport(currDataType, fileType, fllist, idList, isForm){
            	
            	// not sure why w/0 submit() is working here?
            	$("form#dnld input[name='coreName']").val(currDataType);
        		$("form#dnld input[name='fileType']").val(fileType);
        		$("form#dnld input[name='gridFields']").val(fllist);
        		$("form#dnld input[name='idList']").val(idList);
        		
        		if ( isForm ) {
        			$("form#dnld").submit();  // due to ajax, we need to specifically say submit();
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
											<span class='cat'>ID:</span>
										  	<input type="radio" id="gene" value="MGI:106209" name="dataType" class='bq' checked="checked" >Gene
										  	<input type="radio" id="mp" value="MP:0001926" name="dataType" class='bq'>MP
										  	<input type="radio" id="disease" value="OMIM:100300 or ORPHANET:1409 or DECIPHER:38" name="dataType" class='bq'>OMIM / ORPHANET / DECIPHER
										  	<input type="radio" id="hp" value="HP:0000118" name="dataType" class='bq'>HP
                							<p class='note idnote'>For example: MGI:106209</p> <!--  default -->
										  	
										  	<div id="accordion">
											  <p class='header'>Paste in your list</p>
											  <div>
											    <p>
											     <form id='pastedIds'>
											     	<textarea id='pastedList' rows="5" cols="50"></textarea> 
											 		<input type="submit" id="pastedlist" name="" value="Submit" onclick="return submitPastedList()" />
											 		<input type="reset" name="reset" value="Reset"><p>
											 		<p class='notes'>Supports space, comma, tab or new line separated ID list</p>
											     	<p class='notes'>Please do not submit a mix of ids from different datatypes</p>	
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
													  <p class='notes'>Supports comma, tab or new line separated ID list</p>
												      <p class='notes'>Please do not submit a mix of ids from different datatypes</p>  
												</form>
												
											  </div>
											  <p class='header'>Full dataset</p>
											  <form>
											  	<div id='fullDump'></div>
											  	<input type="submit" id="fulldata" name="" value="Submit" onclick="return fetchFullDataset()" /><p>
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

