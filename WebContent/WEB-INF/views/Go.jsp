<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

	<jsp:attribute name="title">GO annotations to phenotyped IMPC genes</jsp:attribute>
	
	<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/gene2go">&nbsp;GO_reports</a></jsp:attribute>

	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>
	
	<jsp:attribute name="header">
	
	<link href="${baseUrl}/js/vendor/jquery.sumoselect/sumoselect.css" rel="stylesheet" />
	<link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />
	<script type='text/javascript' src="${baseUrl}/js/vendor/jquery.sumoselect/jquery.sumoselect.js"></script> 
			
	<style type="text/css">
		.FP {background-color: #CC9999;}
		.F {background-color: #996699;}
		.P {background-color: #666699;}
		.nogo  {background-color: gray;}
		.FP, .F, .P, .nogo {color: white; display: inline; margin-left: 3px; padding: 1px 3px; width: 40px; text-align: center; border-radius: 4px; font-size: 12px;}
		span#legendBox {float: left;}
		.viewed {border: 3px double black; padding: 5px;}
		div.fl1 {float: left;}
		div.fl1 {width: 28%;}
		div.fl2 {float: right; width: 69%;}
		table.goStats {width: 49%; float: left; margin: 0 0 0 5px;}
		#goLegend {margin: 10px 0 10px 0;}
		h2 {margin-top: 20px;}
		
		#goAnnots {border-left: 1px solid gray; margin-left: 25px;}
		p#butts {margin-top: 20px; padding-left: 7px;}
		table.go {width: auto; margin-left: 25px; float: left;}
		table td {background-color: white;}
		
		td.phenoStatus {background-color: #F2F2F2; font-weight: bold; padding: 5px 10px !important;}
		td {padding: 5px 10px;}
		div.dlink {cursor: pointer;}
		
		div#export {
			padding: 10px;
			margin-top: 10px;
			background-color: #F0F0F0;
			border: 1px solid gray;;
    		border-radius: 10px;
    		font-size: 12px;
		}
		div#export input {
			font-size: 11px !important;
		}
		span.msg {
			font-weight: bold;
			color: #5D478B;
		}
		div#caption {
			margin-top: 5px;
		}
		div#view {
			margin-top: 30px; float: right; font-size: 11px; padding-right: 10px;
		}
		p.gocat {
			padding: 5px 0 0px 5px; font-weight: bold; 
		}
		
	</style>

	
	</jsp:attribute>
	
	
	<jsp:body>
		<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">GO annotations to phenotyped IMPC genes</h1>	 
				
						<div class="section">
							<h2 id="section-gostats" class="title ">Brief GO annotation stats</h2>
							<div class='inner'>
								
							   	<div class='fl1'>
							   		${legend}
							   	   	<div style="clear: both"></div>
										
										<div id='export'>
										  	<input type="radio" value="nogo" name="go" class='go'>Genes w/o GO<br>
										  
										  	<p class='gocat'>Gene w/ GO in evidence categories:</p>
										  	<input type="checkbox" value="collapse" name="collapse">Collapse on evidence categories<br>
											<input type="radio" value="experimental" name="go">Experimental<br>
											<input type="radio" value="curatedcomp" name="go">Curated computational<br>
											<input type="radio" value="automated" name="go">Automated electronic<br>
											<input type="radio" value="nd" name="go">No biological data available<br>
											<input type="radio" value="all" name="go">All evidence categories<br>
											
											<p id='butts'>Export as:<button class="tsv fa fa-download gridDump gridDump">TSV</button> or<button class="xls fa fa-download gridDump gridDump">XLS</button></p> 
										</div>
											
								    </div>
						     		
						     		<div class='fl2'>${goStatsTable}</div>
						     		<div id='view'>Click one of the number buttons above to explorer GO annotation data below</div>
									<div style="clear: both"></div>
								</div>
							
							</div><!-- end of section -->
							<div class="section">
								<h2 id="section-gotable" class="title ">Explore GO annotation data</h2>
								<div class="inner">
									<div id='box1'>Click one of the number buttons above to view GO annotation data</div>
								</div>	
							</div>	
						</div>
				</div>
			</div>
		</div>
		
        
        <script type='text/javascript'>
       
       		$(document).ready(function(){
       			//var baseUrl = '//dev.mousephenotype.org/data';
       			//var baseUrl = 'http://localhost:8080/phenotype-archive';
       			var baseUrl = "${baseUrl}";
       	      	var conf = {
					externalDbId: 1,
					fileType:'',
					fileName: '',
					solrCoreName: 'gene',
					params: '',
					gridFields: '',
					dumpMode: 'all',
					goevids: '',
					dogoterm: true
       		  	};

				///var commonQ = '(latest_phenotype_status:"Phenotyping Started" OR latest_phenotype_status:"Phenotyping Complete")';
				var commonQ = 'latest_phenotype_status:*';
				var rows = 9999999;
				//var restParam = "&sort=marker_symbol asc&wt=json&fq=mp_id:*";
				var restParam = "&sort=marker_symbol asc&wt=json";
				var exportUrl = baseUrl + '/export';      

       	      	// submit form dynamically
       	       	$('button').click(function(){
       	       		
       		      	conf.fileType = $(this).hasClass('tsv') ? 'tsv' : 'xls';
       		       	conf.fileName = 'go_dump';// + conf.fileType;

       		     	
       		     	var	commonFl = $("input[name=collapse]").is(':checked') 
       		     				? "evidCodeRank,mgi_accession_id,marker_symbol"
       		     				: "evidCodeRank,latest_phenotype_status,mgi_accession_id,marker_symbol,go_term_id,go_term_evid,go_term_domain,go_term_name";
    		       	
       		       	console.log(commonFl)
       		       	var qryMap = {
       		        	"nogo" :        "q=" + commonQ + " AND -go_term_id:*&fl=mgi_accession_id,marker_symbol&rows=" + rows + restParam,
       		           	"experimental": "q=" + commonQ + " AND evidCodeRank:4&fl=" + commonFl + "&rows=" + rows + restParam, 
       		           	"curatedcomp":  "q=" + commonQ + " AND evidCodeRank:3&fl=" + commonFl + "&rows=" + rows + restParam, 
       		     		"automated":    "q=" + commonQ + " AND evidCodeRank:2&fl=" + commonFl + "&rows=" + rows + restParam, 
       		     		"nd":           "q=" + commonQ + " AND evidCodeRank:1&fl=" + commonFl + "&rows=" + rows + restParam, 
       		      		"all":          "q=" + commonQ + " AND evidCodeRank:*&fl=" + commonFl + "&rows=" + rows + restParam, 
       		      		
       		       	};
       		       	
       		       	var sExp = $("input[name=go]:checked", '#export').val();
       		     	conf.params = qryMap[sExp];
       		       	console.log(conf.params);
       		       	
       		       	if ( typeof sExp == 'undefined' ){
       		       		alert('Sorry, you need to choose one of the radio buttons to export data.');
       		       		return false;
       		       	}
       		       	
       		     	
       		     	conf.gridFields = commonFl;
       		       	
					var sInputs = '';
					var aParams = [];
					for (var k in conf) {
						aParams.push(k + "=" + conf[k]);
					   	sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>";
					}
					
					var form = "<form action='" + exportUrl + "' method=get>" + sInputs + "</form>";
					
					$(form).appendTo('body').submit().remove();
       	               
       	       	});
       	      	
       	     	var paramStr = "wt=json&sort=marker_symbol asc&fq=mp_id:*&fl=mgi_accession_id,marker_symbol,latest_phenotype_status,go_term_id,go_term_evid,go_term_name,go_term_domain"; 		
				var tableHeader = "<thead><th>Marker symbol</th><th>Phenotyping Status</th><th>GO id</th><th>GO evidence</th><th>GO name</th><th>GO domain</th></thead>";		
				var tableCols = 6;
     	      
     			$('div.dlink').click(function(){
     				var status = $(this).parent().siblings('td').attr('rel');
     				var goEvidCat = $(this).parent().siblings('td').text();
     				var goDomain = "not available";
     				if ( $(this).hasClass('F') ){
     					goDomain = 'molecular function';
     				}
     				else if ( $(this).hasClass('P') ){
     					goDomain = 'biological process';
     				}
     				else if ( $(this).hasClass('FP') ){
     					goDomain = 'molecular function OR biological process';
     				}
     				var msg = "DATASET&nbsp;&nbsp;&nbsp;<span class='msg'>status</span>: " + status + ", <span class='msg'>GO domain</span>: " + goDomain + ", <span class='msg'>GO evidence group</span>: " + goEvidCat;
     				
     				$('div.dlink').removeClass('viewed');
     				$(this).addClass("viewed");
     				var oInfos = {};
     				oInfos.params = paramStr + $(this).attr('rel');
     				oInfos.qOri = $(this).attr('rel');
     				oInfos.solrCoreName = "gene";
     				oInfos.legacyOnly = false;
     				oInfos.evidRank = $(this).attr('id');
     				oInfos.mode = "gene2go";
     				
     				_refreshTable();
     				
	       	      	$('table#gene2go').dataTable({
	       	            "bSort": true,
	       	            "bProcessing": true,
	       	            "bServerSide": true,
	       	            "sDom": "<lr><'#caption'>tip",
	       	            "sPaginationType": "bootstrap",
	       	         	"oLanguage": {
	       	          		"sLengthMenu": 'Show <select>'+
		       	            '<option value="10">10</option>'+
		       	            '<option value="30">30</option>'+
		       	            '<option value="50">50</option>'+
		       	            '</select> genes',
		       	         	"sInfo": "Showing _START_ to _END_ of _TOTAL_ genes"
	       	        	},
	       	            "fnDrawCallback": function(oSettings) {  // when dataTable is loaded
	       	            	// doing nothing for now
	       	            	
	       	            	$('div#caption').html(msg);
	       	            },
	       	            "sAjaxSource": baseUrl + '/dataTable',
	       	            "fnServerParams": function(aoData) {
	       	                aoData.push(
	       	                        {"name": "solrParams",
	       	                         "value": JSON.stringify(oInfos, null, 2)
	       	                        }
	       	                );
	       	            }
	       	        });
     			});	
       	      	
     			// load ND (F+P), phenotyping complete dataset by default in explore section
     			$('div#1').click(); 
     			
     			function _refreshTable(){
     				var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "gene2go");
         			$('div#box1').html('');
         			$('div#box1').append(dTable);
     			}
     			
       		});
        </script>
		
		
		
	</jsp:body>
		
</t:genericpage>

