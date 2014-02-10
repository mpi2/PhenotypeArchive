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
		<div class='documentation'><a href='' class='generalPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
		<div class="row-fluid">
			<div class="container span12">
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
      								<div class="container span6">
      										<img src="${mediaBaseUrl}/${exampleImages.experimental.smallThumbnailFilePath}"/>
      										<c:forEach var="sangerSymbol" items="${exampleImages.experimental.sangerSymbol}" varStatus="symbolStatus">
												<c:if test="${not empty exampleImages.experimental.sangerSymbol}"><t:formatAllele>${sangerSymbol}</t:formatAllele><br /></c:if>
												</c:forEach>
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
		</div>
	</div>
	
		<c:if test="${not empty expressionImages && fn:length(expressionImages) !=0}">
	<div  class="section">
		<h2 class="title">Expression Images<i class="fa fa-question-circle pull-right"></i></h2>
			<div class="inner">		
						 <div class="accordion-group">
                        						<div class="accordion-heading">
                        						Expression Associated Images 
                        						</div>
								<div class="accordion-body">
								
		    					<ul>
                                                            
		    					<c:forEach var="doc" items="${expressionImages}">
                                                            <li class="span2">
									<t:imgdisplay img="${doc}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
                                                            </li>
                                                        </c:forEach>
                                                        
								</ul>
								<c:if test="${entry.count>5}">
                                        				<p class="textright">
								<a href='${baseUrl}/images?anatomy_id=${anatomy.accession}&fq=expName:Wholemount Expression'><i class="fa fa-caret-right"></i>show all ${entry.count} images</a>
								</p>
								</c:if>
							</div>
						</div>
					<!--  end of accordion -->
					</div>
				</div>
			
		</div>	
	</div>
	</c:if><!-- end of images lacz expression priority and xray maybe -->
	
	
		<div class="row-fluid dataset">
			<div class='documentation'><a href='' class='mpPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
			<h4 class="caption">Associated Phenotypes</h4>
			<div class="row-fluid">
				<div class="container span12">				
				<c:if test="${not empty anatomy.mpTerms}">
				<table class="table table-striped">
				<tbody>
					<tr>
						<%-- <td>MP Terms:</td> --%>
						<c:forEach items="${anatomy.mpTerms}" var="mpTerm" varStatus="mpStatus">
						<tr>
						<td><a href="${baseUrl}/phenotypes/${anatomy.mpIds[mpStatus.index]}">${mpTerm}</a></td><%-- <td>${mpTerm}</td> --%>
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
	<div class='documentation'><a href='' class='relatedMaPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
				<h4 class="caption">Explore</h4>
		<div class="row-fluid">				
				<div class="container span12">				
				<table class="table table-striped">
				<tbody>
				<tr>
						<td>Child Terms:</td>
						<td></td>
						<c:forEach items="${anatomy.childTerms}" var="childTerm" varStatus="childStatus">
						<tr>
						<td><a href="${baseUrl}/anatomy/${anatomy.childIds[childStatus.index]}">${anatomy.childIds[childStatus.index]}</a></td><td>${childTerm}</td>
						</tr>
						</c:forEach>
					</tr>
					</tbody>
					</table>
					
				</div>
		</div>
		
	</div><!-- end of images lacz expression priority and xray maybe -->
	
	<script>
		$(document).ready(function(){						
					
			$.fn.qTip({'pageName':'ma',
				'textAlign':'left',
				'tip':'topLeft'
			});	// bubble popup for brief panel documentation					
		});				
	</script>
	
</jsp:body>
	

</t:genericpage>
