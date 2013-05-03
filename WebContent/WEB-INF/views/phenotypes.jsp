<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

	<jsp:attribute name="title">${phenotype.id.accession} (${phenotype.name}) | IMPC Phenotype Information</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&core=mp&fq=ontology_subset:*">Phenotypes</a> &raquo; ${phenotype.name}</jsp:attribute>

    <jsp:attribute name="footer">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>
		<script type='text/javascript' src='${baseUrl}/js/imaging/mp.js'></script>
    </jsp:attribute>

    <jsp:body>

	<div class='topic'>Phenotype: ${phenotype.name}</div>

	<div class="row-fluid dataset">
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
						<td><a href="http://www.informatics.jax.org/marker/${phenotype.id.accession}">${phenotype.id.accession}</a></td>
					</tr>
				</tbody>
			</table>
			</div>
			
			<%-- There must not be any spaces --%>
			<div class="container span5" id="ovRight">
				<c:if test="${not empty images}"><img src="${mediaBaseUrl}/${images[0].largeThumbnailFilePath}"/></c:if>
			</div>
		</div>
	</div>

	<c:if test="${not empty phenotypes}">
	<div class="row-fluid dataset">	
		<h4 class="caption">Gene variants with this phenotype</h4>
		<div class="container">
			<table id="phenotypes" class="table table-striped">
				<thead>
					<tr>
						<th>Gene</th>
						<th>Allele</th>
						<th>Zygosity</th>
						<th>Sex</th>
						<th>Procedure/Parameter</th>
						<th>Source</th>
						<c:if test="${not isLive}">
						<th>Graph</th>
						</c:if>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
					<c:set var="europhenome_gender" value="Both-Split"/>
					<tr>
					<td><a href="${baseUrl}/genes/${phenotype.gene.id.accession}">${phenotype.gene.symbol}</a></td>
					<td><c:choose><c:when test="${fn:contains(phenotype.allele.id.accession, 'MGI')}"><a href="http://www.informatics.jax.org/accession/${phenotype.allele.id.accession}"><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></a></c:when><c:otherwise><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></c:otherwise></c:choose></td>
					<td>${phenotype.zygosity}</td>
					<td style="font-family:Verdana;font-weight:bold;">
						<c:set var="count" value="0" scope="page" />
						<c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/><c:if test="${sex == 'female'}"><c:set var="europhenome_gender" value="Female"/><img style="cursor:help;color:#D6247D;" rel="tooltip" data-placement="top" title="Female" alt="Female" src="${baseUrl}/img/icon-female.png" /></c:if><c:if test="${sex == 'male'}"><c:set var="europhenome_gender" value="Male"/><img style="cursor:help;color:#247DD6;margin-left:<c:if test="${count != 2}">16</c:if><c:if test="${count == 2}">4</c:if>px;" rel="tooltip" data-placement="top" title="Male" alt="Male" src="${baseUrl}/img/icon-male.png" /></c:if></c:forEach>
					</td>
					<td>${phenotype.procedure.name} ${phenotype.parameter.name}</td>
					<td>
					<c:choose>
					<c:when test="${phenotype.phenotypeLink eq ''}">
						${phenotype.dataSourceName}
					</c:when>
					<c:otherwise>
					<a href="${phenotype.phenotypeLink }">${phenotype.dataSourceName}</a>
					</c:otherwise>
					</c:choose>
					</td>
					<c:if test="${not isLive}">
					<td style="text-align:center"><c:if test="${phenotype.dataSourceName eq 'EuroPhenome' }"><a href="${baseUrl}/stats/genes/${phenotype.gene.id.accession}?parameterId=${phenotype.parameter.stableId}<c:if test="${fn:length(phenotype.sexes) eq 1}">&gender=${phenotype.sexes[0]}</c:if>&zygosity=${phenotype.zygosity}"><img src="${baseUrl}/img/icon_stats.png" alt="Graph" /></a></c:if></td>
					</c:if>
					</tr>
					</c:forEach>
				</tbody>
			</table>
			<script>
				$(document).ready(function(){						
					
					// use jquery DataTable for table searching/sorting/pagination
					var aDataTblCols = [0,1,2,3,4,5<c:if test="${not isLive}">,6</c:if>];
					var oDataTable = $.fn.initDataTable($('table#phenotypes'), {
						//"aaSorting": [[0, "asc"], [1, "asc"]],   			     
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
							<c:if test="${not isLive}">
						    , { "sType": "string", "bSortable" : false }
							</c:if>

						],
		   	    		"iDisplayLength": 10   // 10 rows as default 
					});

					$('[rel=tooltip]').tooltip();
												    		
		    		$.fn.dataTableshowAllShowLess(oDataTable, aDataTblCols, null);
		    		$('div#phenotypes_wrapper').append($.fn.loadFileExporterUI({
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
		    		
		    		
				});
			</script>
		</div>
	</div>
	</c:if>

	<c:if test="${not empty siblings and not empty go}">
	<div class="row-fluid dataset">	
		<h4 class="caption">Explore</h4>
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
	</c:if>
	
	<c:if test="${not empty images && fn:length(images) !=0}">
	<div class="row-fluid dataset">
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
