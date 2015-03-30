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
	<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" />
	<script type='text/javascript' src="${baseUrl}/js/vendor/jquery.sumoselect/jquery.sumoselect.js"></script> 
	<script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>  
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacetConfig.js'></script>
			  	
	<style type="text/css">
		.FP {background-color: #CC9999;}
		.F {background-color: #996699;}
		.P {background-color: #666699;}
		.nogo  {background-color: gray;}
		.FP, .F, .P, .nogo {color: white; display: inline; margin-left: 3px; padding: 1px 3px; width: 40px; text-align: center; border-radius: 4px; font-size: 12px;}
		span#legendBox {float: left;}
		.viewed {border: 2px solid black; padding: 5px;}
		div.fl1 {float: left;}
		div.fl1 {width: 28%;}
		div.fl2 {float: right; width: 70%;}
		table.goStats {width: 49%; float: left; margin: 0 0 0 5px;}
		#goLegend {margin: 10px 0 10px 0;}
		h2 {margin-top: 20px;}
		
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
    		color: black;
		}
		div#export input {
			font-size: 11px !important;
		}
		div#export  i {
			line-height: 20px;
		}
		span.msg {
			font-weight: bold;
			color: #5D478B;
		}
		div#caption {
			margin-top: 5px;
		}
		div#view {
			margin-top: 30px; float: right; font-size: 13px; padding-right: 15px; color: black;
		}
		p.gocat {
			padding: 5px 0 0px 5px; font-weight: bold; 
		}
		table.goTerm {
			border-top: 1px solid gray;
		}
		table.goTerm td {
			background-color: #F2F2F2;
			border-bottom: 1px solid gray;
		}
		label#collapse.grayout {
			color: gray;
		}
		span.export2 {
			font-size: 14px; color: #0978a1;
		}
		i.fa-info {
			float: right;
			margin-right: 5px;
		}
		div.qtip-content {
			background-color: white;
			border: 1px solid gray !important;
			
		}
		div.qtip-content ul {
			padding: 15px 15px 15px 30px;
		}
		div.qtip {
			border: none;
		}
		div.qtip-content ul li {
			font-size: 12px;
			padding: 4px 0;
			
		}
		div.qtip-content ul li {
			list-style: square;
		}
		div.goCat {
			margin: 15px 15px 0px 20px;
			font-weight: bold;
			font-size: 14px;
		}
		div.dataTables_length {
			margin-left: 0;
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
										  	<input type="checkbox" value="collapse" name="collapse"><label id='collapse'>Collapse dataset on evidence categories</label><br>
											<input type="radio" value="experimental" name="go"><label>Experimental</label><a class='goCatInfo fff'><i class='fa fa-info'></i></a><br>
											<input type="radio" value="curatedcomp" name="go"><label>Curated computational</label><a class='goCatInfo'><i class='fa fa-info'></i></a><br>
											<input type="radio" value="automated" name="go"><label>Automated electronic</label><a class='goCatInfo'><i class='fa fa-info'></i></a><br>
											<input type="radio" value="other" name="go"><label>Other</label><a class='goCatInfo'><i class='fa fa-info'></i></a><br>
											<input type="radio" value="nd" name="go"><label>No biological data available</label><a class='goCatInfo'><i class='fa fa-info'></i></a><br>
											<input type="radio" value="all" name="go"><label>All evidence categories</label><br>
											
											<p id='butts'><span class='export2'>Download</span><button class="tsv fa fa-download gridDump gridDump">TSV</button> or<button class="xls fa fa-download gridDump gridDump">XLS</button></p> 
										</div>
											
								    </div>
						     		
						     		<div class='fl2'>${goStatsTable}</div>
						     		<div id='view'>Click one of the gene number buttons above to explorer GO annotation data below</div>
						     		
									<div style="clear: both"></div>
								</div>
							
							</div><!-- end of section -->
							<div class="section">
								<h2 id="section-gotable" class="title ">Explore GO annotation data</h2>
								<div class="inner">
									<div id='box1'></div><!-- default placeholder -->
								</div>	
							</div>	
						</div>
				</div>
			</div>
		</div>
		
        
        <script type='text/javascript'>
        
        	var goBaseUrl = "http://geneontology.org/page/";
        	var oCatEvids = {
	        	"Experimental": "<ul>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "exp-inferred-experiment'>Inferred from Experiment (EXP)</a></li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "ida-inferred-direct-assay'>Inferred from Direct Assay (IDA)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "ipi-inferred-physical-interaction'>Inferred from Physical Interaction (IPI)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "imp-inferred-mutant-phenotype'>Inferred from Mutant Phenotype (IMP)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "igi-inferred-genetic-interaction'>Inferred from Genetic Interaction (IGI)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "iep-inferred-expression-pattern'>Inferred from Expression Pattern (IEP)</li>"
					+ "</ul>",
	        	"Curated computational": "<ul>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "iss-inferred-sequence-or-structural-similarity'>Inferred from Sequence or structural Similarity (ISS)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "iso-inferred-sequence-orthology'>Inferred from Sequence Orthology (ISO)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "isa-inferred-sequence-alignment'>Inferred from Sequence Alignment (ISA)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "ism-inferred-sequence-model'>Inferred from Sequence Model (ISM)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "igc-inferred-genomic-context'>Inferred from Genomic Context (IGC)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "iba-inferred-biological-aspect-ancestor'>Inferred from Biological aspect of Ancestor (IBA)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "ibd-inferred-biological-aspect-descendent'>Inferred from Biological aspect of Descendant (IBD)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "ikr-inferred-key-residues'>Inferred from Key Residues (IKR)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "ird-inferred-rapid-divergence'>Inferred from Rapid Divergence(IRD)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "rca-inferred-reviewed-computational-analysis'>Inferred from Reviewed Computational Analysis (RCA)</li>"
					+ "</ul>", 			
				"Automated electronic": "<ul>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "automatically-assigned-evidence-codes'>Inferred from Electronic Annotation (IEA)</li>"
					+ "</ul>", 	
				"Other": "<ul>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "tas-traceable-author-statement'>Traceable Author Statement (TAS)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "nas-non-traceable-author-statement'>Non-traceable Author Statement (NAS)</li>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "ic-inferred-curator'>Inferred by Curator (IC)</li>"
					+ "</ul>", 	
				"No biological data available": "<ul>"
					+ "<li><a target='_blank' href='" + goBaseUrl + "nd-no-biological-data-available'>No biological Data available (ND)</li>"
					+ "</ul>"
			};
		
       		$(document).ready(function(){
       			//var baseUrl = '//dev.mousephenotype.org/data';
       			//var baseUrl = 'http://localhost:8080/phenotype-archive';
       			var baseUrl = "${baseUrl}";
       			var solrUrl = "${internalSolrUrl};"
       			
   				$("a.goCatInfo").each(function(){
   					$(this).qtip({            	   
   				
	   		           	hide: false,
	   					content: {
	   						text: "<div class='goCat'>" + $(this).prev().text() + "</div>" + oCatEvids[$(this).prev().text()],
	   						title: {'button': 'close'}
	   					},		 	
	   				 	style: {
	   				 		classes: 'qtipimpc',			 		
	   				        tip: {corner: 'top left'}
	   				    },
	   				    position: {
	   				    	my: 'left',
	   				    	adjust: {x: 145, y: 50},
			 				//at: 'bottom right', // at the bottom right of...
	        				target: $("a.goCatInfo") // my target
	   				    },
	   				 	show: {
	   						event: 'click' //override the default mouseover
	   					}
   					}); 
   		        });
       			
       	      	var conf = {
					externalDbId: 1,
					fileType:'',
					fileName: '',
					solrCoreName: 'gene',
					params: '',
					gridFields: '',
					dumpMode: 'all',
					gocollapse: false,
					dogoterm: true
       		  	};

       	      	// want only phenotyping complete and started
				var commonQ = '(latest_phenotype_status:"Phenotyping Started" OR latest_phenotype_status:"Phenotyping Complete")';
				//var commonQ = 'latest_phenotype_status:*';
				
				// want only genes having mp calls
				var restParam = "&sort=marker_symbol asc&wt=json&fq=mp_id:*";
				//var restParam = "&sort=marker_symbol asc&wt=json";
				
				var exportUrl = baseUrl + '/export';      
				var rows = 9999999;
				
				$("input[name=go]").click( function(){
					if( $(this).val() == 'nogo' ) {
   		     	 		$("input[name=collapse]").prop('checked', false);
   		     	 		$('label#collapse').addClass('grayout');
					}
					else {
						$('label#collapse').removeClass('grayout');
					}
   		     	});
				$("input[name=collapse]").click( function(){
					if ($("input[name=go]:checked").val() == "nogo" ){
						return false;
					}
				});
				
       	      	// submit form dynamically
       	       	$('button').click(function(){
       	       		
       		      	conf.fileType = $(this).hasClass('tsv') ? 'tsv' : 'xls';
       		       	conf.fileName = 'go_dump';// + conf.fileType;

       		     	var	commonFl;
       		     	if ( $("input[name=collapse]").is(':checked') ){
       		     		commonFl = "evidCodeRank,mgi_accession_id,marker_symbol,go_count";
       		     		conf.gocollapse = true;
       		     	}	
       		     	else {
       		     		commonFl = "evidCodeRank,latest_phenotype_status,mgi_accession_id,marker_symbol,go_term_id,go_term_evid,go_term_domain,go_term_name,go_uniprot";
       		     	}
       		     	
       		       	//console.log(commonFl)
       		       	
       		       	var qryMap = {
       		        	"nogo" :        "q=" + commonQ + " AND -go_term_id:*&fl=mgi_accession_id,marker_symbol&rows=" + rows + restParam,
       		           	"experimental": "q=" + commonQ + " AND evidCodeRank:5&fl=" + commonFl + "&rows=" + rows + restParam, 
       		           	"curatedcomp":  "q=" + commonQ + " AND evidCodeRank:4&fl=" + commonFl + "&rows=" + rows + restParam, 
       		     		"automated":    "q=" + commonQ + " AND evidCodeRank:3&fl=" + commonFl + "&rows=" + rows + restParam, 
       		     		"other":        "q=" + commonQ + " AND evidCodeRank:2&fl=" + commonFl + "&rows=" + rows + restParam, 
       		     		"nd":           "q=" + commonQ + " AND evidCodeRank:1&fl=" + commonFl + "&rows=" + rows + restParam, 
       		      		"all":          "q=" + commonQ + " AND evidCodeRank:*&fl=" + commonFl + "&rows=" + rows + restParam, 
       		      		
       		       	};
       		       	
       		       	var sExp = $("input[name=go]:checked", '#export').val();
       		     	conf.params = qryMap[sExp];
       		     	
       		       	//console.log(conf.params);
       		       	
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
     	      	
				var paramStr = "wt=json&sort=marker_symbol asc&fq=mp_id:*"; 		
				var displayFl = "&fl=mgi_accession_id,marker_symbol,latest_phenotype_status,go_count";
				var downloadFl = "&fl=mgi_accession_id,marker_symbol,latest_phenotype_status,go_uniprot,evidCodeRank,go_term_id,go_term_name,go_term_evid,go_term_domain";
				var tableHeader = "<thead><th>Marker symbol</th><th>Phenotyping Status</th><th>GO annotated</th><th></th></thead>";		
				var tableCols = 4;
				
     			$('div.dlink').click(function(){
     				var thisButt = $(this); 
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
     					goDomain = 'molecular function AND biological process';
     				}
     				var msg = "DATASET&nbsp;&nbsp;&nbsp;<span class='msg'>status</span>: " + status + ", <span class='msg'>GO domain</span>: " + goDomain + ", <span class='msg'>GO evidence group</span>: " + goEvidCat;
     				
     				$('div.dlink').removeClass('viewed');
     				$(this).addClass("viewed");
     				var oInfos = {};
     				oInfos.params = paramStr + displayFl + $(this).attr('rel');
     				oInfos.qOri = $(this).attr('rel');
     				oInfos.solrCoreName = "gene";
     				oInfos.legacyOnly = false;
     				oInfos.evidRank = $(this).attr('id');
     				oInfos.mode = "gene2go";
     				oInfos.widgetName = 'geneFacet';
     				oInfos.coreName = 'gene';
     				oInfos.dogoterm = true;
     				
     				_refreshTable();
     				
	       	      	$('table#gene2go').dataTable({
	       	            "bSort": true,
	       	            "bProcessing": true,
	       	            "bServerSide": true,
	       	            //"sDom": "<lr><'#caption'>tip",
	       	         	"sDom": "<<'#exportSpinner'>l<'#tableTool'>r><'#caption'>tip",
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
	       	            	
	       	            	oInfos.params = oInfos.params = paramStr + downloadFl + thisButt.attr('rel');
	       	            	oInfos.gocollapse = false;
	       	            	$.fn.initDataTableDumpControl(oInfos);

	       	            	if (goDomain != 'not available'){
		       	            	var dTable = $(this).DataTable();
		       	            	$(this).find('tr td:nth-child(4)').css('cursor','pointer');
		       	            	
		       	            	$(this).find('tr td:nth-child(4)').click(function(){
		       	            		
		       	            		var oTr = $(this).parent();
		       	            		var oRow = dTable.row(oTr);
		       	            		
		       	            	 	if (oRow.child.isShown()) {
		       	            	 		oRow.child.hide();
		       	            	 		oTr.find('td > i.fa').removeClass('fa-minus-square');
		       	            	 	}
		       	            	 	else {
		       	            	 		fetchDetailedGoData(oTr, oRow);
		       	            		}
		       	            	});
	       	            	}
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
       		
       		function fetchDetailedGoData(oTr, oRow) {
       			
       		 	var uri = solrUrl + '/gene/select';
				$.ajax({
	                'url': solrUrl + '/gene/select',
	                'data': 'wt=json&q=marker_symbol:'+ oTr.find('td:first-child').text() + '&fl=mgi_accession_id,marker_symbol,latest_phenotype_status,go_term_id,go_term_evid,go_term_name,go_term_domain,go_uniprot',
	                'dataType': 'jsonp',
	                'jsonp': 'json.wrf',
	                'success': function (json) {
	       	        	
       	        	 	var oDoc = json.response.docs[0];
       	        	 	
       	        	 	var aGo_term_ids = oDoc.go_term_id;
       	        	 	
       	        	 	var goIdIndexes = {};
       	        	 	for( var i=0; i<oDoc.go_term_id.length; i++){
       	        	 		goIdIndexes[oDoc.go_term_id[i]+"_"+parseInt(i)] = i;  // so that same id becomes unambiguous
       	        	 	}
       	        	 	// sort by key
       	        	 	goIdIndexes = $.fn.sortJson(goIdIndexes);
       	        	 	
       	        	 	var aGo_uniprots = oDoc.go_uniprot;
       	        	 	var aGo_term_names = oDoc.go_term_name;
       	        	 	var aGo_term_evids = oDoc.go_term_evid;
       	        	 	var aGo_term_domains = oDoc.go_term_domain;
       	        	 	var aGo_uniprotAccs = oDoc.go_uniprot;
       	        	 
       	        	 	var goUniprotBaseUrl = "http://www.ebi.ac.uk/QuickGO/GProtein?ac=";
       	        	 	var goBaseUrl = "http://www.ebi.ac.uk/QuickGO/GTerm?id=";
       	        	 	
       	        	 	/* var go2uniprot = {}; // prepares for GO-uniprotAcc lookup: GO to uniprotAcc is one to many relationship
       	        	 	for ( var i=0; i<aGo_uniprots.length; i++ ){
       	        		 	var aParts = aGo_uniprots[i].split('__');
       	        		 	var goId = aParts[0];
       	        		 	var uniprotAcc = aParts[1];
       	        		 	if( ! (goId in go2uniprot) ){
       	        		 		go2uniprot[goId] = [];
       	        		 	}
       	        		 	go2uniprot[goId].push(uniprotAcc);
       	        	 	}
       	        	 	 */
       	        	 	var trs = null;
       	        	 	//var seenGo = {};
       	        	 	for ( goIdIndex in goIdIndexes ){
       	        	 		
       	        	 		var aParts = goIdIndex.split("_");
       	        	 		var goId = 	aParts[0];
   	        	 			var goIdIndex = aParts[1];
   	        	 			
   	        	 			/* var uniprotAcc = null;
   	        	 			
   	        	 			// work out which uniprotAcc for this GO
   	        	 			if ( ! (goId in seenGo) ){
   	        	 				seenGo[goId] = 0; // first time seen this GO
   	        	 				uniprotAcc = go2uniprot[goId][0]; 
   	        	 			}
   	        	 			else {
	   	        	 			seenGo[goId]++;
	   	        	 			var seen = seenGo[goId];
	   	        	 			uniprotAcc = go2uniprot[goId][seen]; 
   	        	 			} */
   	        	 			
   	        	 			var aParts = aGo_uniprotAccs[goIdIndex].split("__");
   	        	 			var uniprotAcc = aParts[1];
   	        	 			
       	        	 		var uniprotLink = "<a target='_blank' href='" + goUniprotBaseUrl + uniprotAcc + "'>" + uniprotAcc + "</a>";
       	        	 		var goLink = "<a target='_blank' href='" + goBaseUrl + goId + "'>" + goId + "</a>";
       	        	 		var td0 = "<td>" + uniprotLink + "</td>";
       	        	 		var td1 = "<td>" + goLink + "</td>";
       	        	 		var td2 = "<td>" + aGo_term_names[goIdIndex] + "</td>";
       	        	 		var td3 = "<td>" + aGo_term_evids[goIdIndex] + "</td>";
       	        	 		var td4 = "<td>" + aGo_term_domains[goIdIndex] + "</td>";
       	        	 		trs += "<tr>"+td0+td1+td2+td3+td4+"</tr>";
       	        	 	}
       	        	 	
       	        	 	var table = $("<table class='goTerm'></table>");
       	        	 	table.append('<thead><th>Uniprot protein</th><th>GO id</th><th>GO name</th><th>GO evidence</th><th>GO domain</th>');
       	        	 	table.append(trs);
						
						//add the response html to the target row
       	        		oRow.child(table).show();
       	        		oTr.find('td > i.fa').addClass('fa-minus-square');
       	    		},
       	    		'error': function(jqXHR, textStatus, errorThrown) {
       		    		oRow.child('Error fetching data ...').show();
       		    	}	
				});
       				
       		}
       		
       		
       		
       		
        </script>
		
		
		
	</jsp:body>
		
</t:genericpage>

