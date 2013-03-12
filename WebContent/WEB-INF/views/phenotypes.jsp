<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

	<jsp:attribute name="title">${param.phenotype_id} IMPC Phenotype Information</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&core=mp&fq=ontology_subset:*">Phenotypes</a> &raquo; ${phenotypeTerm.name}</jsp:attribute>

    <jsp:attribute name="header">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">		
		<link type='text/css' rel='stylesheet' href='${baseUrl}/css/mp.css' media='all' />
		<script src="${baseUrl}/js/general/toggle.js"></script>
    </jsp:attribute>

    <jsp:body>
		<div id='mpMain'>
			<div class='topic'></div>
			<div class="row-fluid dataset">
				<div class="container span12">
					<div id="ovLeft" class="span4"></div>
					<div id="ovRightPadding" class="span2"></div>
					<div id="ovRight" class="span4"><c:if test="${not empty images}"><img src="${mediaBaseUrl}/${images[0].largeThumbnailFilePath}"/></c:if></div>
					<div id="ovRightPaddingRight" class="span2"></div>
				</div>
			</div>      		
		</div>	
			
		<script type='text/javascript' src='${baseUrl}/js/vendor/jquery.corner.mini.js'></script>
		<script type='text/javascript' src='${baseUrl}/js/utils/collapseText.js'></script>	
		<script type='text/javascript' src='${baseUrl}/js/mp/mpPageWidget.js'></script>
		
		<!--  for images -->
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>		
		<script type='text/javascript' src='${baseUrl}/js/imaging/mp.js'></script>
		<script src="../js/imaging/imageUtils.js"></script>
		
		<script type='text/javascript'>			
			var phenotype_id = '${phenotype_id}';
			$('div#mpMain').mpPage({  		
				phenotype_id: phenotype_id,				
				showErrPage: function(event, data){
					window.location.href = data.url;
				}
			});
		</script>
		
		<c:if test="${not empty images && fn:length(images) !=0}">
		<div class="row-fluid dataset">
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
