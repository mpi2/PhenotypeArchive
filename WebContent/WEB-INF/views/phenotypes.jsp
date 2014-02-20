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
		<script type='text/javascript' src="${baseUrl}/js/general/dropDownPhenPage.js"></script>
	</jsp:attribute>


	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>
<jsp:attribute name="addToFooter">
	<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Phenotype</a></li>
                <c:if test="${genePercentage.getDisplay()}">
                		<li><a href="#data-summary">Phenotype Association Stats</a></li>
                </c:if>
                <li><a href="#gene-variants">Gene Variants</a></li><!-- message comes up in this section so dont' check here -->
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>
</jsp:attribute>
	<jsp:body>

	<div class="region region-content">
		<div class="node node-gene">
			<h1 class="title" id="top">Phenotype: ${phenotype.name} </h1>	  
				<div class="section">
					<div class="inner">
						<c:if test="${not empty phenotype.description}">
							<p class="with-label"> <span class="label"> Definition</span> ${phenotype.description} </p>
						</c:if>
						<c:if test="${not empty synonyms}">
							<p class="with-label"> <span class="label">Synonyms</span>
								<c:forEach var="synonym" items="${synonyms}" varStatus="loop">
									${synonym.symbol}
									<c:if test="${!loop.last}">, &nbsp;</c:if>
								</c:forEach>
							</p>
						</c:if>
						<c:if test="${not empty procedures}">
							<div class="with-label"> <span class="label">Procedure</span>
								<ul>
									<c:forEach var="procedure" items="${procedures}" varStatus="firstLoop">
 										<c:forEach var="pipeline" items="${procedure.pipelines}" varStatus="loop">
  											<li><a href="${drupalBaseUrl}/impress/impress/displaySOP/${procedure.stableKey}">${procedure.name} (${pipeline.name})</a></li>
	 											<c:if test="${firstLoop.count==3 && !(firstLoop.last && loop.last)}"><p ><a id='show_other_procedures'><i class="fa fa-caret-right"></i> more procedures</a></p> <div id="other_procedures"></c:if>
												<c:if test="${firstLoop.last && loop.last && fn:length(procedures) >3}"></div></c:if>
										</c:forEach>
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
				
				<c:if test="${genePercentage.getDisplay()}">
					<div class="section collapsed open">
						<h2 class="title" id="data-summary">Phenotype associations stats <span class="documentation" ><a href='' id='phenotypeStatsPanel' class="fa fa-question-circle pull-right"></a></span> </h2>
						<div class="inner">					
							<!-- Phenotype Assoc. summary -->
							<div class="half">
								<p> <span class="muchbigger">${genePercentage.getTotalPercentage()}%</span> of tested genes with null mutations on a B6N genetic background have a phenotype association to ${phenotype.name} (source: EuroPhenome)
									(${genePercentage.getTotalGenesAssociated()}/${genePercentage.getTotalGenesTested()}) </p>
								<p class="padleft"><span class="bigger">${genePercentage.getFemalePercentage()}%</span> females (${genePercentage.getFemaleGenesAssociated()}/${genePercentage.getFemaleGenesTested()}) </p>
								<p class="padleft"><span class="bigger">${genePercentage.getMalePercentage()}%</span> males (${genePercentage.getMaleGenesAssociated()}/${genePercentage.getMaleGenesTested()}) 	</p>
							</div>					
							<!-- Graphs -->
							<c:if test="${parametersAssociated.size() > 0}">
								<div id="chartsHalf" class="half">
	
								<div id="chart-container">
									<div id="single-chart-div" class="oChart" parameter="${parametersAssociated.get(0)}" mp="${phenotype.id.accession}">
									</div>
									<div id="spinner-overview-charts"><i class="fa fa-refresh fa-spin"></i></div>
								</div>
								<c:if test="${parametersAssociated.size() > 1}">
									<ul>
										<c:forEach var="assocParam" items="${parametersAssociated}" varStatus="loop">
											<li> <a id='list${loop.index}' href="#" onclick="ajaxToBe('${phenotype.id.accession}', '${assocParam}');">${assocParam}</a></li>
										</c:forEach>
									</ul>
								</c:if>
								<div id='chartFilters'></div>
							</div>
						</c:if>
						<div class="clear"></div>
					</div>
				</div>
			</c:if>
						
			
				<div class="section">
				
			    <h2 class="title" id="gene-variants">Gene variants with ${phenotype.name} 	
			    <span class="documentation" ><a href='' id='relatedMpPanel' class="fa fa-question-circle pull-right"></a></span> 
			    </h2>
				   
					<div class="inner">	 
						<div id="phenotypesDiv">	
							<div class="container span12">
								<c:forEach var="filterParameters" items="${paramValues.fq}">
									${filterParameters}
								</c:forEach>
								<c:if test="${not empty phenotypes}">
									<form class="tablefiltering no-style" id="target" action="www.google.com">
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
											
											<div class="clear"></div>
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
	
			<c:if test="${not empty images && fn:length(images) !=0}">
				<div class="row-fluid dataset">
					<h4 class="caption">Images <span class="documentation" ><a href='' id='imagePanel' class="fa fa-question-circle pull-right"></a></span></h4>
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
	</div>
</div>
</jsp:body>

</t:genericpage>


