<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Insert title here</title>


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<c:set var="domain">${pageContext.request.serverName}</c:set>

<link type='text/css' rel='stylesheet' href='http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/base/jquery-ui.css' />
<link type='text/css' rel='stylesheet' href="${baseUrl}/css/pheno.css"  />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/searchAndFacet.css' />	
<link type='text/css' rel='stylesheet' href="${baseUrl}/css/bootstrap.css"  />
<link type='text/css' rel='stylesheet' href="${baseUrl}/css/bootstrap-responsive.css"  />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/vendor/DataTables-1.9.4/jquery.dataTables.css' media='all' />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/vendor/DataTables-1.9.4/customDataTable.css' media='all' />
<link type='text/css' rel='stylesheet' href="${baseUrl}/css/custom.css"  />

<script type='text/javascript' src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js'></script>

</head>
</head>
<body>

<div class="accordion" id="accordion2">
   	<c:if test="${imageCount eq 0}"><h4>There are no images for ${queryTerms}</h4></c:if>
		<c:if test="${imageCount ne 0}"><h4>All images for ${queryTerms} (${imageCount} images)</h4></c:if>
		<div id="grid" class="container">
			<!-- <c:if test="${imageCount ne 0}"><h6 class="container">Page <fmt:formatNumber value="${(start+length)/length}" maxFractionDigits="0" /> (Images ${start+1}-<c:if test="${start+length gt imageCount}">${imageCount}</c:if><c:if test="${start+length le imageCount}">${start+length}</c:if>)</h6></c:if> -->
			<div class="row">
				<c:forEach var="image" items="${images}" varStatus="status">
				<div class="span3">
					<div class="thumbnail">
						<!-- image ID: ${image.id} -->
						<a href="${baseUrl}/media/images/${image.fullResolutionFilePath}"><img src="${baseUrl}/media/images/${image.smallThumbnailFilePath}"  onerror="imgError(this);" /></a>
						<div class="caption">
		       				<c:if test="${not empty image.genotype}">${image.genotype}<br /></c:if>
							<c:forEach var="annTermName" items="${image.annotationTermName}" varStatus="annStatus">
							<c:if test="${not empty image.annotationTermName}">${annTermName}<br /></c:if>
							</c:forEach>
						</div>
					</div>
				</div>
						<c:if test="${status.count % 4 eq 0}"></div><div class="row"></c:if>
				</c:forEach>
			</div>
		</div>
		
</div>

</body>
</html>