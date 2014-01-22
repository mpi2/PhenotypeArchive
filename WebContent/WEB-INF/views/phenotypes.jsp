<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage>

	<jsp:attribute name="title">${phenotype.id.accession} (${phenotype.name}) | IMPC Phenotype Information</jsp:attribute>
	
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&core=mp&fq=ontology_subset:*">Phenotypes</a> &raquo; ${phenotype.name}</jsp:attribute>

	<jsp:attribute name="header">
	<!-- CSS Local Imports -->
		<!-- link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.themeroller.css" />
		<link rel="stylesheet" type="text/css" href="${baseUrl}/css/custom.css" />
		<style>
			.ui-dropdownchecklist-selector > .ui-icon {margin-top:4px;}
			.ui-dropdownchecklist-text {padding:2px;margin:0;}
		</style-->
	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js'></script>
		<script type='text/javascript' src='${baseUrl}/js/imaging/mp.js'></script>
		<script type='text/javascript' src="${baseUrl}/js/general/dropDownPhenPage.js"></script>
		<script type='text/javascript' src="${baseUrl}/js/general/toggle.js"></script>
  </jsp:attribute>

	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

	<jsp:body>

	<div class="region region-content">
		<div class="node node-gene">
			<h1 class="title">Phenotype: ${phenotype.name}</h1>	  
				<div class="section">
					<div class="inner">
						<div class='documentation'>
							<a href='' class='generalPanel'><img src="${baseUrl}/img/info_20x20.png" /></a>
						</div>		    
						<c:if test="${not empty phenotype.description}">
							<p class="with-label no-margin"> <span class="label"> Definition</span> ${phenotype.description} </p>
						</c:if>
						<c:if test="${not empty synonyms}">
							<p class="with-label no-margin"> <span class="label">Synonyms</span>
								<c:forEach var="synonym" items="${synonyms}" varStatus="loop">
									${synonym.symbol}
									<c:if test="${!loop.last}">, &nbsp;</c:if>
								</c:forEach>
							</p>
						</c:if>
						<c:if test="${not empty procedures}">
							<div class="with-label"> <span class="label">Procedure</span>
								<ul>
									<c:forEach var="procedure" items="${procedures}" varStatus="loop">
											<li><a href="${drupalBaseUrl}/impress/impress/displaySOP/${procedure.stableKey}">${procedure.name} (${procedure.pipeline.name})</a></li>
									</c:forEach>
								</ul>
							</div>
						</c:if>
							<c:if test="${not empty anatomy}">
							<div class="with-label"> <span class="label">Anatomy</span>
								<ul>
									<c:forEach var="term" items="${anatomy}" varStatus="loop">
										<li><a href="http://informatics.jax.org/searches/AMA.cgi?id=${term.id.accession}">${term.name}</a></li>
									</c:forEach>
								</ul>
							</div>
						</c:if>
						<p class="with-label"><span class="label">MGI MP browser</span><a href="http://www.informatics.jax.org/searches/Phat.cgi?id=${phenotype.id.accession}">${phenotype.id.accession}</a></p>
					</div>
				</div>
				
				<c:if test="${genePercentage.getDisplay() || overviewPhenCharts.size()>0}">
					<div class="section collapsed">
					<h2 class="title" id="data-summary">Phenotype associations stats</h2>
					<div class='documentation'>
						<a href='' class='phenotypeStatsPanel'><img	src="${baseUrl}/img/info_20x20.png" /></a>
					</div>									
					<div class="inner" style="display: block;">					
						<!-- Phenotype Assoc. summary -->
						<div class="half">
							<p> <span class="muchbigger">${genePercentage.getTotalPercentage()}%</span> of tested genes with null mutations on a B6N genetic background have a phenotype association to ${phenotype.name} (source: EuroPhenome)
								(${genePercentage.getTotalGenesAssociated()}/${genePercentage.getTotalGenesTested()}) </p>
							<p class="padleft"><span class="bigger">${genePercentage.getFemalePercentage()}%</span> females (${genePercentage.getFemaleGenesAssociated()}/${genePercentage.getFemaleGenesTested()}) </p>
							<p class="padleft"><span class="bigger">${genePercentage.getMalePercentage()}%</span> males (${genePercentage.getMaleGenesAssociated()}/${genePercentage.getMaleGenesTested()}) 	</p>
						</div>					
						<!-- Graphs -->
						<c:if test="${overviewPhenCharts.size()>0}">
							<div class="half">
							<div class="graphfilters">
								<div class="graphfilter">
									<div class="filtertype">Center</div>
									<div class="filteroptions">
									<ul>
										<li id="center1">CenterA</li>
										<li id="center2"> CenterB</li> 
									</ul>
									</div>
								</div>
								<div class="graphfilter">
									<div class="filtertype">Sex</div>
									<div class="filteroptions">
									<ul>
										<li id="maleFilter">Male</li>
										<li id="femaleFilter"> Female</li> 
									</ul>
									</div>
								</div>
							</div>
							<!-- c:forEach var="categoricalResultAndCharts" items="${overviewPhenCharts}" varStatus="experimentLoop"-->
							<div class="row-fluid">
					 				<!-- c:forEach var="categoricalChartDataObject" items="${overviewPhenCharts.get(0)}" varStatus="chartLoop"-->
					  				 	<div class="container span6">
													<div id="${overviewPhenCharts.get(0).getId()}"
														style="min-width: 400px; height: 400px; margin: 0 auto">
													</div>
					   								<script type="text/javascript">
					   								${overviewPhenCharts.get(0).getChart()}
					   							</script>
										</div>
	 								<!-- /c:forEach-->
								</div>
							<!-- /c:forEach-->
						</div>
					</c:if>
				<div class="clear"></div>
				</div>
				</div>
			</c:if>
						
		<div class="section">	
		<div class="inner"></div>
				<c:choose>
	    				<c:when test="${not empty exampleImages}">
		      				<div class="row-fluid">
								<div class="container span6">
									<img src="${mediaBaseUrl}/${exampleImages.control.smallThumbnailFilePath}" />
									Control
	   							</div>
	   							<div class="container span6">
	   								<img src="getSolrInstance/${exampleImages.experimental.smallThumbnailFilePath}" />
	   								<c:forEach var="sangerSymbol" items="${exampleImages.experimental.sangerSymbol}" varStatus="symbolStatus">
										<c:if test="${not empty exampleImages.experimental.sangerSymbol}">
											<t:formatAllele>${sangerSymbol}</t:formatAllele><br />
										</c:if>
									</c:forEach>
								</div>
	      					</div>
	    				</c:when>
	    				<c:otherwise>
		      				<c:if test="${not empty images}">
								<img src="${mediaBaseUrl}/${images[0].largeThumbnailFilePath}" />
							</c:if>
	    				</c:otherwise>
				</c:choose>
				</div>
		</div>
	
	
	
	<div class="section collapsed open">
		<div class='documentation'>
			<a href='' class='relatedMpPanel'><img src="${baseUrl}/img/info_20x20.png" /></a>
		</div>

    <h2 class="title">Gene variants with ${phenotype.name}</h2>
	   
		<div class="inner" style="display:block;">	 
			<div id="phenotypesDiv">	
				<div class="container span12">
					<c:forEach var="filterParameters" items="${paramValues.fq}">
						${filterParameters}
					</c:forEach>
					<c:if test="${not empty phenotypes}">
						<form id="target" action="www.google.com">
								<c:forEach var="phenoFacet" items="${phenoFacets}"
										varStatus="phenoFacetStatus">
										<c:if
											test="${!isImpcTerm || !(phenoFacet.key eq 'mp_term_name')}">
										<select id="${phenoFacet.key}" class="impcdropdown"
												multiple="multiple" title="Filter on ${phenoFacet.key}">
											<c:forEach var="facet" items="${phenoFacet.value}">
												<option>${facet.key}</option>
											</c:forEach>
										</select> 
										</c:if>
								</c:forEach>
						</form>
						<jsp:include page="geneVariantsWithPhenotypeTable.jsp">
							<jsp:param name="isImpcTerm" value="${isImpcTerm}"/>
						</jsp:include>
						<div id="exportIconsDiv"></div>					
				</c:if>
				</div>
				<c:if test="${empty phenotypes}">
					<div class="alert alert-info">Phenotype associations to genes and alleles will be available once data has completed quality control.</div>
				</c:if>
			</div>
		</div>


	<c:if test="${not empty siblings or not empty go}">
	
	<div class="row-fluid dataset">

	    <div class='documentation'>
			<a href='' class='relatedMpPanel'><img src="${baseUrl}/img/info_20x20.png" /></a>
		</div>

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

		<div class='documentation'>
			<a href='' class='imagePanel'><img src="${baseUrl}/img/info_20x20.png" /></a>
		</div>

		<h4 class="caption">Images</h4>
			<div class="row-fluid">
			<div class="container span12">
				<div class="accordion" id="accordion1">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse" data-target="#pheno">Phenotype Associated Images <i class="icon-chevron-down pull-left"></i></a>
						</div>
						<div id="pheno" class="accordion-body collapse in">
							<div class="accordion-inner">
								<a href="${baseUrl}/images?phenotype_id=${phenotype_id}">[show all  ${numberFound} images]</a>
		    					<ul>
		    					<c:forEach var="doc" items="${images}">
									<li class="span2"><a
													href="${mediaBaseUrl}/${doc.fullResolutionFilePath}"><img
														src="${mediaBaseUrl}/${doc.smallThumbnailFilePath}" /></a>
									<c:forEach var="maTerm" items="${doc.annotationTermName}"
														varStatus="loop">
										${maTerm}<c:if test="${!loop.last}">
															<br />
														</c:if>
									</c:forEach>
									<c:if test="${not empty doc.genotype}">
														<br />${doc.genotype}</c:if>
									<c:if test="${not empty doc.genotype}">
														<br />${doc.gender}</c:if>
									<c:if test="${not empty doc.institute}">
														<c:forEach var="org" items="${doc.institute}">
															<br />${ org}</c:forEach>
													</c:if> 
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
	</div>
			<!--  end of phenotype box section -->
	</c:if>
</div>
    </jsp:body>

</t:genericpage>
