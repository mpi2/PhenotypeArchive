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
    
 <div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
								<h1 class="title">Anatomy Term: ${anatomy.term}</h1>
					

		<c:if test="${not empty expressionImages && fn:length(expressionImages) !=0}">
	<div  class="section">
		<!-- <h2 class="title">Expression Images<i class="fa fa-question-circle pull-right"></i></h2> -->
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
								<c:if test="${numberExpressionImagesFound>5}">
                                        				<p class="textright">
								<a href='${baseUrl}/images?anatomy_id=${anatomy.accession}&fq=expName:Wholemount Expression'><i class="fa fa-caret-right"></i>show all ${numberExpressionImagesFound} images</a>
								</p>
								</c:if>
							</div>
						</div>
					<!--  end of accordion -->
					</div>
	</div>
	</c:if><!-- end of images lacz expression priority and xray maybe -->
	
	<c:if test="${not empty anatomy.mpTerms}">
		<div class="section">
			<%-- <div class='documentation'><a href='' class='mpPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div> --%>
			<h2 class="title">Associated Phenotypes<i class="fa fa-question-circle pull-right"></i></h2>
			<div class="inner">
							
				
				<table>
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
					
				
		</div>
		
	</div><!-- end of images lacz expression priority and xray maybe -->
	</c:if>
	
	<c:if test="${fn:length(anatomy.childTerms)>0 }">
	<div class="section">
	<%-- <div class='documentation'><a href='' class='relatedMaPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div> --%>
		<h2 class="title">Explore<i class="fa fa-question-circle pull-right"></i></h2>
				
		<div class="inner">				
					
				<table>
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
		
	</div><!-- end of anatomy explore panel-->
	</c:if>
		</div>
		</div>
	</div>
</div>
		
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
