<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC allele paper references</jsp:attribute>
	
	<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/alleleref">&nbsp;Allele references</a></jsp:attribute>

	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>
	
	<jsp:attribute name="header">
	
	<link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />
	<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" />
	<script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>  
			  	
	<style type="text/css">
		h2#section-ref {
			margin-top: 20px;
		}
	
		
	</style>

	
	</jsp:attribute>
	
	
	<jsp:body>
		<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">IMPC paper references</h1>	 
				
						<div class="section">
							<h2 id="section-ref" class="title ">Allele, Pubmed id associations</h2>
							<div class='inner'>
								
							   ${datatable}
								
							
							</div><!-- end of section -->
							
						</div>
				</div>
			</div>
		</div>
		
        
        <script type='text/javascript'>
        
       		$(document).ready(function(){
       			//var baseUrl = '//dev.mousephenotype.org/data';
       			//var baseUrl = 'http://localhost:8080/phenotype-archive';
       			var baseUrl = "${baseUrl}";
       			var solrUrl = "${internalSolrUrl};"
       		
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

