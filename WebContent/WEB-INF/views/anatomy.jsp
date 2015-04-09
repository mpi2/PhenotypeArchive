<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

	<jsp:attribute name="title">${anatomy.accession} (${anatomy.term}) | IMPC anatomy Information</jsp:attribute>
	 <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#fq=selected_top_level_ma_term:*&facet=ma">anatomy</a> &raquo; ${anatomy.term}</jsp:attribute>
<jsp:attribute name="header">
</jsp:attribute>
    <jsp:attribute name="addToFooter">
		<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Anatomy Term</a></li>
                <c:if test="${not empty anatomy.mpTerms}">
                	<li><a href="#associated-phenotypes">Associated Phenotypes</a></li>
                </c:if>
                <c:if test="${fn:length(anatomy.childTerms)>0 }">
                	<li><a href="#explore">Explore</a></li>
                </c:if>
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>
		
    </jsp:attribute>

                
    <jsp:body>
    
 <div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
								<h1 class="title" id="top">Anatomy Term: ${anatomy.term}</h1>
					
<c:if test="${empty expressionImages && fn:length(anatomy.childTerms)==0}">
	<div class="section">
		<div class=inner>
			<div class="alert alert-info">No data currently available	</div>
		</div>
	</div>
</c:if>
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
	
	<%-- spoke to terry and these need rethink in terms of MP associations <c:if test="${not empty anatomy.mpTerms}">
		<div class="section">
			<div class='documentation'><a href='' class='mpPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
			<h2 class="title" id="associated-phenotypes">Associated Phenotypes<i class="fa fa-question-circle pull-right"></i></h2>
			<div class="inner">
							
				
				<table>
				<tbody>
					<tr>
						<td>MP Terms:</td>
						<c:forEach items="${anatomy.mpTerms}" var="mpTerm" varStatus="mpStatus">
						<tr>
						<td><a href="${baseUrl}/phenotypes/${anatomy.mpIds[mpStatus.index]}">${mpTerm}</a></td><td>${mpTerm}</td>
						</tr>
						</c:forEach>
					</tr>
					</tbody>
					</table>
					
				
		</div>
		
	</div><!-- end of images lacz expression priority and xray maybe -->
	</c:if> --%>
	
	<c:if test="${fn:length(anatomy.childTerms)>0 }">
	<div class="section">
	<%-- <div class='documentation'><a href='' class='relatedMaPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div> --%>
		<h2 class="title" id="explore">Explore<i class="fa fa-question-circle pull-right"></i></h2>
				
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
	
					<div class="section"> 
						<h2 class="title">Expression data for ${anatomy.term}</h2>
						<div class="inner">
						<table id="genes" class="table tableSorter">
					    <thead>
					    <tr>
					        <th class="headerSort">Gene/Allele</th>
					        <th class="headerSort">Expression</th>
					        <th class="headerSort">Zygosity</th>
					        <th class="headerSort">Sex</th>
					        <th class="headerSort">Parameter</th>
					        <th class="headerSort">Phenotyping Center</th>
					        <th class="headerSort">Images</th>
					    </tr>
					    </thead>
					    <tbody>
					    <c:forEach var="row" items="${anatomyTable}" varStatus="status">
					        <c:set var="europhenome_gender" value="Both-Split"/>
					        <tr>
					            <td><a href="${baseUrl}/genes/${row.gene.id.accession}">${row.gene.symbol} </a><br/> <span class="smallerAlleleFont"><t:formatAllele>${row.allele.symbol}</t:formatAllele></span></td>
					            <td>${row.expression}</td>
					            <td>${row.zygosity}</td>
					            <td>
					                <c:set var="count" value="0" scope="page"/>
					                <c:forEach var="sex" items="${row.sexes}"><c:set var="count" value="${count + 1}" scope="page"/>
					                    <c:if test="${sex == 'female'}"><c:set var="europhenome_gender" value="Female"/>
					                        <img alt="Female" src="${baseUrl}/img/female.jpg"/>
					                    </c:if>
					                    <c:if test="${sex == 'male'}">
					                        <c:if test="${count != 2}"><img data-placement="top" src="${baseUrl}/img/empty.jpg"/></c:if>
					                        <c:set var="europhenome_gender" value="Male"/><img alt="Male" src="${baseUrl}/img/male.jpg"/>
					                    </c:if>
					                </c:forEach>
					            </td>				
					            <td>${row.parameter.name}</td>
					            <td>${row.phenotypingCenter} </td>
					            <td>placehold</td>				
					        </tr>
					    </c:forEach>
					    </tbody>
					</table>
		
				</div></div>
	
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
			
			var selectedFilters = "";
			var dropdownsList = new Array();
		    initGenePhenotypesTable();
		  function initGenePhenotypesTable(){
				var aDataTblCols = [0,1,2,3,4,5,6];
				$('table#genes').dataTable( {
						"aoColumns": [
						              { "sType": "html", "mRender":function( data, type, full ) {
						            	  return (type === "filter") ? $(data).text() : data;
						              }},
						              { "sType": "html", "mRender":function( data, type, full ) {
						            	  return (type === "filter") ? $(data).text() : data;
						              }},
						              { "sType": "string"},
						              { "sType": "string"},
						              { "sType": "string"},
						              { "sType": "string"},
						              { "sType": "html"}
						              ],
							"bDestroy": true,
							"bFilter":false,
							"bPaginate":true,
				      "sPaginationType": "bootstrap"
					});
		  }
		  
			// the number of columns should be kept in sync in the JSP
			var oDataTable = $.fn.initDataTable($('table#strainPhenome'), {
				"aoColumns": [
				              { "sType": "string" },		              
				              { "sType": "string" },
				              { "sType": "string" },		 
				              { "sType": "string" },
				              { "sType": "string" },
				              { "sType": "string" },
				              { "sType": "string", "bSortable" : false }

				              ],
				              "bDestroy": true,
				              "bFilter":false
			});
			
			function refreshPhenoTable(newUrl){
				$.ajax({
					url: newUrl,
					cache: false
				}).done(function( html ) {
					$("#genes_wrapper").html(html);
					initGenePhenotypesTable();
					alert('calling new table in anatomy.jsp');
				});
			}
	});				
	</script>
	
</jsp:body>
	

</t:genericpage>
