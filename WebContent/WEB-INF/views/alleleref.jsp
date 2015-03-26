
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>


	<jsp:attribute name="title">IMPC allele paper references</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/alleleref">&nbsp;Allele references</a></jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="header">
	
		<link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />
		<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" />
		<script type='text/javascript' src='https://bartaz.github.io/sandbox.js/jquery.highlight.js'></script>  
		<script type='text/javascript' src='https://cdn.datatables.net/plug-ins/f2c75b7247b/features/searchHighlight/dataTables.searchHighlight.min.js'></script>  
		<script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>  
				  	
		<style type="text/css">
			h1#top {
				margin: 20px 0 50px 0;
			}
			div#alleleRef_filter {
				float: right;
			}
			form#allele {
				position: absolute;
				top: 220px;
				width: auto;
				font-size: 12px;
				padding: 0 0 0 5px;
				background-color: #f2f2f2;
				display: none;
			}
			input[type=password] {
       			width: 100px;     
   			} 
   			div#butt {
   				margin: 15px 0;
   			}
   			button.edit {
   				color: white;
   				background-color: #993333;
   			}
   			div#tableTool {
   				position: absolute;
   				top: 180px;
   				right: 20px;
   				
   			}
   			table.dataTable span.highlight {
			  background-color: yellow;
			  font-weight: bold;
			  color: black;
			}
			
		</style>
	</jsp:attribute>

	<jsp:attribute name="addToFooter">	
		<div class="region region-pinned">
	       
	    </div>		
	
	</jsp:attribute>

    <jsp:body>		
				
		<div class="region region-content">
			<div class="block block-system">
				<div class='content'>
				<div class="node node-gene">
						<h1 class="title" id="top">IMPC allele references</h1>	 
				
					<div id='formBox'>
						<span></span>
						<form id='allele'>
	                      Enter passcode to switch to Edit mode: <input size='10' type='password' name='passcode'>
	                    </form>
                    </div>
                    <div id='butt'><button class='login'>Edit</button></div>
					<div class="clear"></div>
					<!-- facet filter block -->								
					<!-- container to display dataTable -->									
					<div class="HomepageTable" id="alleleRef"></div>	
				</div>				
				</div>
			</div>
		</div>		       

        <script type='text/javascript'>
        
        $(document).ready(function(){
   			'use strict';	
   			
   			//var baseUrl = '//dev.mousephenotype.org/data';
   			//var baseUrl = 'http://localhost:8080/phenotype-archive';
   			var baseUrl = "${baseUrl}";
   			var solrUrl = "${internalSolrUrl};"
   			
			$('button[class=login]').click(function(){
				if ( ! $(this).hasClass('edit') ) {
					$('#formBox span').text("");
					if ( $('form#allele').is(":visible") ){
						$('form#allele').hide();
					}
					else {
						$('formBox span').text("");
						$('form#allele').show();
					}
				}
				else {
					$(this).removeClass('edit').text('Edit');
					$('#formBox span').text("You are now out of editing mode...");
					var oTable = $('table#alleleRef').dataTable();
        			oTable.fnStandingRedraw();
				}
        	});
			
			$('form#allele').submit(function(){
				
				var passcode = $('form input[type=password]').val();
              	$.ajax({
              		method: "post",
                	url: baseUrl + "/alleleRefLogin?passcode="+passcode,
                	success: function(response) {
                		// verifying passcode
                		// boolean response
                		if ( response ){
                			$('button').addClass('edit').text("Stop editing")
                			$('form#allele').hide();
                			$('#formBox span').text("You are now in editing mode...");
                			var oTable = $('table#alleleRef').dataTable();
                			oTable.fnStandingRedraw();
                		}
                		else {
                			alert("Passcode incorrect. Please try again");
                		}
                	},
              	 	error: function() {
                     window.alert('AJAX error trying to verify passcode');
              	 	} 
               	});
              	return false;
			});
   			
   			
   			var tableHeader = "<thead><th>Reviewed</th><th>Allele symbol</th><th>PMID</th><th>Date of publication</th><th>Grant id</th><th>Grant agency</th><th>Grant acronym</th><th>Paper link</th></thead>";		
			var tableCols = 8;
			
			var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "alleleRef");
			$('div#alleleRef').append(dTable);
			
			var oConf = {};
			oConf.doAlleleRef = true;
			oConf.iDisplayLength = 10;
			oConf.iDisplayStart = 0;
				
			fetchAlleleRefDataTable(oConf);
   		});
   		
        function fetchAlleleRefDataTable(oConf) {
       
   		  	var oTable = $('table#alleleRef').dataTable({
   	            "bSort": true,
   	        	"processing": true,
   	        	"serverSide": true,
   	            //"sDom": "<lr><'#caption'>tip",
   	         	"sDom": "<<'#exportSpinner'>l<f><'#tableTool'>r>tip",
   	            "sPaginationType": "bootstrap",
   	            "searchHighlight": true,
   	         	"oLanguage": {
   	          		"sLengthMenu": 'Show <select>'+
       	            '<option value="10">10</option>'+
       	            '<option value="30">30</option>'+
       	            '<option value="50">50</option>'+
       	            '</select> allele records',
       	         	"sInfo": "Showing _START_ to _END_ of _TOTAL_ alleles records",
       	         	"sSearch": "Filter: "
   	        	},
   	        	"aoColumns": [{ "bSearchable": false },
   	        	              { "bSearchable": true },
   	        	           	  { "bSearchable": true },
	        	              { "bSearchable": true },
	        	              { "bSearchable": true },
	        	              { "bSearchable": true },
   	        	              { "bSearchable": true },
   	        	              { "bSearchable": false }
   	        	              ],
   	            "fnDrawCallback": function(oSettings) {  // when dataTable is loaded
   	            	
   	            	// download tool
   	            	oConf.externalDbId = 1;
   	            	oConf.fileType = '';
   	            	oConf.fileName = 'impc_allele_references';
   	            	oConf.doAlleleRef = true;
   	            	oConf.legacyOnly = false;
   	            	oConf.filterStr = $(".dataTables_filter input").val();
   	            	$.fn.initDataTableDumpControl(oConf);
   	            	
   	            	
   	            	if ( $('button').hasClass('edit')) { 
	   	            	// POST
	   	            	var thisRow = $(this);
	   	            	var dbid = parseInt($(this).find('tr td:nth-child(3) span').attr('id'));
	   	            	$(this).find('tr td:nth-child(2)').attr('id', dbid).css({'cursor':'pointer'}); // set id for the key in POST
	   	            	$(this).find('tr td:nth-child(2)').editable(baseUrl + '/dataTableAlleleRef', {
	   	                 "callback": function( sValue, y ) {
	   	                     	$(this).text(sValue);
	   	                  		$(this).parent().find('td:first-child').text('yes');
	   	                 },
	   	                 "event": "click",
	   	                 "height": "18px",
	   	                 "width": "350px"
	   	             	});
	   	            	$(this).find('tr td:nth-child(2)').bind('click', function(){
	   	            		//console.log($(this).html()); 
	   	            		// a form is created on the fly by jeditable
	   	            		// change that value for user to save typing as this value 
	   	            		// will be 'yes'
	   	            		$(this).find('form').css('padding','2px'); 
	   	            		$(this).find('form input[name=value]').val("");
	   	            	}).mouseover(function(){
	   	            		$(this).css({'border':'1px solid gray'});
	   	            	}).mouseout(function(){
	   	            		$(this).css({'border':'none'});
	   	            	});
   	            	}
   	            },
   	            "sAjaxSource": baseUrl + '/dataTableAlleleRef',
   	            "fnServerParams": function(aoData) {
   	                aoData.push(
   	                        {"name": "doAlleleRef",
   	                         "value": JSON.stringify(oConf, null, 2)
   	                        }
   	                );
   	            }
   	        });
   		  	
   		  	
   		  
        }
        
        
        </script>
       		
       		
       		  	
       			
           		
       		
       		
       
		
	</jsp:body>
		
</t:genericpage>

