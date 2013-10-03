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
		<script src="${baseUrl}/js/general/dropDownPhenPage.js"></script>
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
											
											<c:forEach var="facet" items="${phenoFacet.value}">
												<option>${facet.key}</option>
											</c:forEach>
										</select> 
								</c:forEach>
						</form>
						<jsp:include page="geneVariantsWithPhenotypeTable.jsp"></jsp:include>
<div id="exportIconsDiv"></div>
					
			</c:if>
	</div>
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
