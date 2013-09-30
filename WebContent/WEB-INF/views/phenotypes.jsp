<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

	<jsp:attribute name="title">${phenotype.id.accession} (${phenotype.name}) | IMPC Phenotype Information</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&core=mp&fq=ontology_subset:*">Phenotypes</a> &raquo; ${phenotype.name}</jsp:attribute>

<jsp:attribute name="header">
<link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.themeroller.css"/>
<link rel="stylesheet" type="text/css" href="${baseUrl}/css/custom.css"/>
</jsp:attribute>
    <jsp:attribute name="footer">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>
		<script type='text/javascript' src='${baseUrl}/js/imaging/mp.js'></script>
		<script src="${baseUrl}/js/general/toggle.js"></script>
		
    </jsp:attribute>

    <jsp:body>

	<div class='topic'>Phenotype: ${phenotype.name}</div>
				  
	<div class="row-fluid dataset">
		<div class='documentation'><a href='' class='generalPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>		    
		<div class="row-fluid">	
				<div class="container span12">
						<div class="row-fluid">
			<div class="container span6">
			<table class="table table-striped">
				<tbody>
					<tr>
						<td>Definition:</td>
						<td>${phenotype.description}</td>
					</tr>
					<c:if test="${not empty phenotype.synonyms}">
					<tr>
						<td>Synonym:</td>
						<td>
							<ul>
							<c:forEach var="synonym" items="${phenotype.synonyms}" varStatus="loop">
							<li>${synonym.symbol}</li>
							</c:forEach>
							</ul>
						</td>
					</tr>
					</c:if>
					<c:if test="${not empty procedures}">
					<tr>
						<td>Procedure:</td>
						<td>
							<ul>
							<c:forEach var="procedure" items="${procedures}" varStatus="loop">
							<li><a href="${drupalBaseUrl}/impress/impress/displaySOP/${procedure.stableKey}">${procedure.name} (${procedure.pipeline.name})</a></li>
							</c:forEach>
							</ul>
						</td>
					</tr>
					</c:if>
					<c:if test="${not empty anatomy}">
					<tr>
						<td>Anatomy:</td>
						<td>
							<ul>
							<c:forEach var="term" items="${anatomy}" varStatus="loop">
							<li><a href="http://informatics.jax.org/searches/AMA.cgi?id=${term.id.accession}">${term.name}</a></li>
							</c:forEach>
							</ul>
						</td>
					</tr>
					</c:if>
					<tr>
						<td>MGI MP browser:</td>
						<td><a href="http://www.informatics.jax.org/searches/Phat.cgi?id=${phenotype.id.accession}">${phenotype.id.accession}</a></td>
					</tr>
				</tbody>
			</table>
			</div>
			
			<%-- There must not be any spaces --%>
			<div class="container span5" id="ovRight">
			<c:choose>
    				<c:when test="${not empty exampleImages}">
      				<div class="row-fluid">
      								<div class="container span6">
      										<img src="${mediaBaseUrl}/${exampleImages.control.smallThumbnailFilePath}"/>
      										Control
      								</div>
      								<div class="container span6">
      											<img src="${mediaBaseUrl}/${exampleImages.experimental.smallThumbnailFilePath}"/>
      											<c:forEach var="sangerSymbol" items="${exampleImages.experimental.sangerSymbol}" varStatus="symbolStatus">
												<c:if test="${not empty exampleImages.experimental.sangerSymbol}"><t:formatAllele>${sangerSymbol}</t:formatAllele><br /></c:if>
												</c:forEach>
      								</div>
      						</div>
    				</c:when>
    				<c:otherwise>
      				 <c:if test="${not empty images}"><img src="${mediaBaseUrl}/${images[0].largeThumbnailFilePath}"/></c:if>
    				</c:otherwise>
			</c:choose>
	
			</div>
		</div>
	</div>
	</div>
</div>
	
<div class="row-fluid dataset">
    <div class='documentation'><a href='' class='relatedMpPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
    <h4 class="caption">Gene variants with this phenotype</h4>
	<div class="row-fluid">	    	
		<div class="container span12">			
			<div class="row-fluid" id="phenotypesDiv">	
				<div class="container span12">
				<c:forEach var="filterParameters" items="${paramValues.fq}">
			${filterParameters}
			</c:forEach>
					<c:if test="${not empty phenotypes}">
						<form id="target" action="www.google.com">
								<c:forEach var="phenoFacet" items="${phenoFacets}" varStatus="phenoFacetStatus">
										<select id="${phenoFacet.key}" class="impcdropdown" multiple="multiple" title="Filter on ${phenoFacet.key}">
											<option>All</option>
											<c:forEach var="facet" items="${phenoFacet.value}">
												<option>${facet.key}</option>
											</c:forEach>
										</select> 
								</c:forEach>
								<input type="submit" class='btn primary' value="Filter" />
						</form>
						<jsp:include page="geneVariantsWithPhenotypeTable.jsp"></jsp:include>
<div id="exportIconsDiv"></div>
				</div>
				<script>
					$(document).ready(function(){						
						
						$.fn.qTip('mp');	// bubble popup for brief panel documentation	
						
						//var oDataTable = $('table#phenotypes').dataTable();
						//oDataTable.fnDestroy();//clean up the previous datatable from the calling page
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
							{ "sType": "alt-string", "bSearchable" : false },
						    { "sType": "string"},
						    { "sType": "html"}
						    , { "sType": "string", "bSortable" : false }

						],
						"bDestroy": true,
						"bFilter":false,
		   	    		"iDisplayLength": 10000   // 10 rows as default 
					});

					$('[rel=tooltip]').tooltip();
					//$("#phenotypes_filter").hide();
												    		
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
						mpId: '${phenotype.id.accession}',
						mpTerm: '${phenotype.name}',
						externalDbId: 5,  //mp_db_id in phenotype_call_summary table
						panel: 'geneVariants',
						fileName: 'gene_variants_of_MP-${fn:replace(phenotype.name, " ", "_")}'	
					});
		    		function initFileExporter(conf){
		    			
		    	    	$('button.fileIcon').click(function(){
		    	    		var classString = $(this).attr('class');	    		
		    	    		//var controller = classString.substr(0, classString.indexOf(" "));
		    	    		
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
					
			    		//stuff for dropdown tick boxes here
			    		$("#resource_fullname").dropdownchecklist( { firstItemChecksAll: true, emptyText: "Projects: All", icon: {}, minWidth: 150 } );
			    		$("#procedure_name").dropdownchecklist( { firstItemChecksAll: true, emptyText: "Procedure: All", icon: {} , minWidth: 150} );
			    		// $("select[multiple]").bsmSelect();
			    		//if filter parameters are already set then we need to set them as selected in the dropdowns
			    		var previousParams=$("#filterParams").html();
			    		//alert('previous='+previousParams);
				$('#target').submit(function() {
			  var rootUrl=window.location.href;
			    			 // alert(rootUrl);
			    			  var newUrl=rootUrl.replace("phenotypes", "geneVariantsWithPhenotypeTable");//change the new url to point to the fragment view in the controller
			    			// alert( $("option:selected").parent().attr("id"));
			    			 var output ='?';
			    			//http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype/select/?q=marker_accession_id:MGI:98373&rows=100&version=2.2&start=0&indent=on&defType=edismax&wt=json&facet=true&facet.field=project_name&facet.field=top_level_mp_term_name&fq=top_level_mp_term_name:(%22vision/eye%20phenotype%22%20OR%20%22craniofacial%20phenotype%22)
			    			var array1=$("#resource_fullname").val() || [];
			    			if(array1.length==1){//if only one entry for this parameter then don't use brackets and or
			    				 output+='&fq=resource_fullname:"'+array1[0]+'"';
			    			} 
							if(array1.length>1)	{
			    				output+='&fq=resource_fullname:(';//note " before and after value for solr handle spaces
			    			 		for(var i=0; i<array1.length; i++){
			    						 
			    							 //if(i==0)output+=' " ';
			    						 output+='"'+array1[i]+'"';
			    						 if(i<array1.length-1){
			    							 output+=' OR ';
			    						 }else{
			    							 output+=')';
			    						 }
			    						 //console.log('logging='+array1[i]);
			    			 }
			    		}
			    			 var output2 ='';//='"'+ ($("#top_level_mp_term_name").val() || []).join('"&fq=top_level_mp_term_name:"')+'"';
			    			 var array2=$("#procedure_name").val() || [];
			    				if(array2.length==1){//if only one entry for this parameter then don't use brackets and or
				    				 output+='&fq=procedure_name:"'+array2[0]+'"';
				    			}
			    				if(array2.length>1){
				    				output+='&fq=procedure_name:(';//note " before and after value for solr handle spaces
			    			 			for(var i=0; i<array2.length; i++){
			    			 				 output+='"'+array2[i]+'"';
				    						 if(i<array2.length-1){
				    							 output+=' OR ';
				    						 }else{
				    							 output+=')';
				    						 }
			    			 		}
			    			 }
			    			 newUrl+=output+output2;
			    			 //alert(newUrl);
			    			  $.ajax({
			    				  url: newUrl,
			    				  cache: false
			    				}).done(function( html ) {
			    				  $("#phenotypes_wrapper").html(html);
			    				  
			    				  
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
			  							{ "sType": "alt-string", "bSearchable" : false },
			  						    { "sType": "string"},
			  						    { "sType": "html"}
			  						    , { "sType": "string", "bSortable" : false }

			  						],
			  						"bDestroy": true,
			  						"bFilter":false,
			  		   	    		"iDisplayLength": 10000   // 10 rows as default 
			  					});			  
			    				});
			    			  return false;
			    			});
					});
				</script>
	
	</c:if>
	<c:if test="${empty phenotypes}">
					<div class="alert alert-info">You'll see EuroPhenome phenotype data when available. You'll find links to the Wellcome Trust Sanger Institute mouse portal when appropriate.</div>
				</c:if>
				</div>
			</div>
	</div>
</div>


	<c:if test="${not empty siblings or not empty go}">
	
	<div class="row-fluid dataset">
	    <div class='documentation'><a href='' class='relatedMpPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
	    <h4 class="caption">Explore</h4>
		<div class="row-fluid">
			<div class="container span12">		
		<div class="container">
			<table class="table table-striped">
				<tbody>
					<c:if test="${not empty siblings}">
					<tr>
						<td>Related Phenotypes:</td>
						<td>
							<ul>
							<c:forEach var="term" items="${siblings}" varStatus="loop">
							<li><a href="${baseUrl}/phenotypes/${term.id.accession}">${term.name}</a></li>
							</c:forEach>
							</ul>
						</td>
					</tr>
					</c:if>
					<c:if test="${not empty go}">
					<tr>
						<td>Gene Ontology:</td>
						<td>
							<ul>
							<c:forEach var="term" items="${go}" varStatus="loop">
							<li><a href="http://amigo.geneontology.org/cgi-bin/amigo/term_details?term=${term.id.accession}">${term.name} (${term.id.accession})</a></li>
							</c:forEach>
							</ul>
						</td>
					</tr>
					</c:if>
				</tbody>
			</table>
		</div>
	</div>
	</div>
	</div>
	</c:if>
	
	<c:if test="${not empty images && fn:length(images) !=0}">
	<div class="row-fluid dataset">
		<div class='documentation'><a href='' class='imagePanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
		<h4 class="caption">Images</h4>
			<div class="row-fluid">
			<div class="container span12">
				<div class="accordion" id="accordion1">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse" data-target="#pheno">Phenotype Associated Images <i class="icon-chevron-down  pull-left" ></i></a>
						</div>
						<div id="pheno" class="accordion-body collapse in">
							<div class="accordion-inner">
								<a href="${baseUrl}/images?phenotype_id=${phenotype_id}">[show all  ${numberFound} images]</a>
		    					<ul>
		    					<c:forEach var="doc" items="${images}">
									<li class="span2"><a href="${mediaBaseUrl}/${doc.fullResolutionFilePath}"><img src="${mediaBaseUrl}/${doc.smallThumbnailFilePath}" /></a>
									<c:forEach var="maTerm" items="${doc.annotationTermName}" varStatus="loop">
										${maTerm}<c:if test="${!loop.last}"><br /></c:if>
									</c:forEach>
									<c:if test="${not empty doc.genotype}"><br />${doc.genotype}</c:if>
									<c:if test="${not empty doc.genotype}"><br />${doc.gender}</c:if>
									<c:if test="${not empty doc.institute}"><c:forEach var="org" items="${doc.institute}"><br />${ org}</c:forEach></c:if> 
									</li>
								</c:forEach>
								</ul>
							</div>
						</div>
					<!--  end of accordion -->
					</div>
				</div>
			</div>
		</div>	
	</div><!--  end of phenotype box section -->
	</c:if>

    </jsp:body>

</t:genericpage>
