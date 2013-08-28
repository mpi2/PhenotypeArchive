<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

	<jsp:attribute name="title">${anatomy.accession} (${anatomy.term}) | IMPC anatomy Information</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&core=ma&fq=ontology_subset:IMPC_Terms AND selected_top_level_ma_term:*">anatomy</a> &raquo; ${anatomy.term}</jsp:attribute>

<jsp:attribute name="header">
</jsp:attribute>
    <jsp:attribute name="footer">
		<script src="${baseUrl}/js/general/toggle.js"></script>
		
    </jsp:attribute>

    <jsp:body>

	<div class='topic'>Anatomy Term: ${anatomy.term}</div>

	<div class="row-fluid dataset">
		<div class="row-fluid">
			<div class="container span6">
			<table class="table table-striped">
				<tbody>
					<c:if test="${not empty anatomy}">
					<tr>
						<td>Anatomy:</td>
						<td>					
							<a href="${anatomy.mgiLinkString}">${anatomy.accession}</a>
						</td>
					</tr>
						<%-- <tr>
						<td>Definition:</td>
						<td>${anatomy.description}</td>
					</tr> --%>
					<tr>
						<td>Child Terms:</td>
						<td></td>
						<c:forEach items="${anatomy.childTerms}" var="childTerm" varStatus="childStatus">
						<tr>
						<td><a href="http://www.informatics.jax.org/searches/AMA.cgi?id=${anatomy.childIds[childStatus.index]}">${anatomy.childIds[childStatus.index]}</a></td><td>${childTerm}</td>
						</tr>
						</c:forEach>
					</tr>
					</c:if>
				</tbody>
			</table>
			</div>
			<div class="container span5" id="ovRight">
			<c:choose>
    				<c:when test="${not empty exampleImages}">
      				<div class="row-fluid">
      								<div class="container span6">
      										<img src="${mediaBaseUrl}/${exampleImages.control.smallThumbnailFilePath}"/>
      										Control
      								</div>

      				</div>
    				</c:when>
    				<c:otherwise>
      				 <c:if test="${not empty expressionImages}"><img src="${mediaBaseUrl}/${expressionImages[0].smallThumbnailFilePath}"/>Random MA related image</c:if>
      				  <c:if test="${(empty expressionImages) && (not empty images)}"><img src="${mediaBaseUrl}/${images[0].smallThumbnailFilePath}"/>Random MA related image</c:if>
    				</c:otherwise>
			</c:choose>
	
			</div>
		</div>
	</div>
	
	
		<c:if test="${not empty expressionImages && fn:length(expressionImages) !=0}">
	<div class="row-fluid dataset">
		<h4 class="caption">Expression Images</h4>
			<div class="row-fluid">
			<div class="container span12">
				<div class="accordion" id="accordion1">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse" data-target="#expression">Expression Associated Images <i class="icon-chevron-down  pull-left" ></i></a>
						</div>
						<div id="expression" class="accordion-body collapse in">
							<div class="accordion-inner">
								<a href="${baseUrl}/images?annotationTermId=${anatomy.accession}">[show all  ${numberExpressionImagesFound} images]</a>
		    					<ul>
		    					<c:forEach var="doc" items="${expressionImages}">
									<li class="span2"><a href="${mediaBaseUrl}/${doc.fullResolutionFilePath}"><img src="${mediaBaseUrl}/${doc.smallThumbnailFilePath}" /></a>
									<c:forEach var="maTerm" items="${doc.annotationTermName}" varStatus="loop">
										${maTerm}<c:if test="${!loop.last}"><br /></c:if>
									</c:forEach>
									<c:forEach var="sangerSymbol" items="${doc.sangerSymbol}" varStatus="symbolStatus">
						<c:if test="${not empty doc.sangerSymbol}"><t:formatAllele>${sangerSymbol}</t:formatAllele></c:if>
							</c:forEach>
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
	</div>
	</c:if><!-- end of images lacz expression priority and xray maybe -->
	
	<c:if test="${not empty images && fn:length(images) !=0}">
	<div class="row-fluid dataset">
		<h4 class="caption">Other MA Associated Images</h4>
			<div class="row-fluid">
			<div class="container span12">
				<div class="accordion" id="accordion2">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse" data-target="#pheno">Anatomy Associated Images <i class="icon-chevron-down  pull-left" ></i></a>
						</div>
						<div id="pheno" class="accordion-body collapse in">
							<div class="accordion-inner">
								<a href="${baseUrl}/images?annotationTermId=${anatomy.accession}">[show all  ${numberFound} images]</a>
		    					<ul>
		    					<c:forEach var="doc" items="${images}">
									<li class="span2"><a href="${mediaBaseUrl}/${doc.fullResolutionFilePath}"><img src="${mediaBaseUrl}/${doc.smallThumbnailFilePath}" /></a>
									<c:forEach var="maTerm" items="${doc.annotationTermName}" varStatus="loop">
										${maTerm}<c:if test="${!loop.last}"><br /></c:if>
									</c:forEach>
									<c:forEach var="sangerSymbol" items="${doc.sangerSymbol}" varStatus="symbolStatus">
						<c:if test="${not empty doc.sangerSymbol}"><t:formatAllele>${sangerSymbol}</t:formatAllele></c:if>
							</c:forEach>
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
	</div>
	</c:if><!-- end of images lacz expression priority and xray maybe -->
	
	
		<div class="row-fluid dataset">
			<div class="row-fluid">
				<div class="container span12">
				<h4 class="caption">Associated Phenotypes</h4>
				<c:if test="${not empty anatomy.mpTerms}">
				<table class="table table-striped">
				<tbody>
					<tr>
						<%-- <td>MP Terms:</td> --%>
						<c:forEach items="${anatomy.mpTerms}" var="mpTerm" varStatus="mpStatus">
						<tr>
						<td><a href="${baseUrl}/phenotypes/${mpTerm}">${mpTerm}</a></td><%-- <td>${mpTerm}</td> --%>
						</tr>
						</c:forEach>
					</tr>
					</tbody>
					</table>
					</c:if>
				</div>
		</div>
		
	</div><!-- end of images lacz expression priority and xray maybe -->
	
	
	<div class="row-fluid dataset">
		<div class="row-fluid">
				<div class="container span12">
				<h4 class="caption">Explore</h4>
				
				</div>
		</div>
		
	</div><!-- end of images lacz expression priority and xray maybe -->
	

</jsp:body>
	

</t:genericpage>
