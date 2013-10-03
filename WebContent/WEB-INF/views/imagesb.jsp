<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="title">${queryTerms} IMPC Images Information</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&core=images&fq=annotationTermId:M* OR expName:* OR symbol:*">Images</a> &raquo; Results</jsp:attribute>
	
    <jsp:attribute name="header">
		<link href="${baseUrl}/css/imagemain.css" rel="stylesheet" />
		<link href="${baseUrl}/css/searchresults.css" rel="stylesheet" />
		<style>
		table th{border-bottom:1px solid #CDC8B1;}
		table tr:last-child th{border-bottom:none;}
		.thumbnail{margin-bottom:30px;}
		.thumbnail p{line-height:0.75em;}
		</style>

		<script src="${baseUrl}/js/vendor/jquery.autopager-1.0.0.js"></script>
		<script src="${baseUrl}/js/imaging/imageUtils.js"></script>
    </jsp:attribute>

	<jsp:attribute name="footer">
		<script>$.autopager({link: '#next',content: '#grid'});</script>
	</jsp:attribute>

    <jsp:body>
    <c:if test="${solrImagesError ne null}"><h4>There is an error the image index is down please contact the IMPC if this error persists</h4></c:if>
		<c:if test="${imageCount eq 0}"><h4>There are no images for ${breadcrumbTerms}</h4></c:if>
		<c:if test="${imageCount ne 0}"><h4><strong class=lead>${imageCount} images for ${breadcrumbText}</strong></h4></c:if>
		<div id="grid" class="container">
			<div class="row">
				<c:forEach var="image" items="${images}" varStatus="status">
				<%-- ${image } --%>
                                <div class="span3">
				<div class="thumbnail">
					<t:imgdisplay img="${image}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
				</div>
                                </div>
			<c:if test="${status.count % 4 eq 0}"></div><div class="row"></c:if>
				</c:forEach>
			</div>
		</div>

		<c:if test="${imageCount gt start+length || start ne 0}">
		<div id="pagination" class="pagination">
			<ul>
				<c:if test="${start eq 0}">
				<li class="disabled"><a>Prev</a></li>
				</c:if>
				<c:if test="${start ne 0}">
				<li><a href='${baseUrl}/imagesb?start=${start-length}&length=${length}&${q}'>Prev</a></li>
				</c:if>

				<c:forEach begin="0" end="${imageCount}" step="${length}" var="i">
				<li <c:if test="${start eq i}">class="active"</c:if>>
				<a href='${baseUrl}/imagesb?start=<c:if test="${i ne 0}">${i-1}</c:if><c:if test="${i eq 0}">${i}</c:if>&length=${length}&${q}'><fmt:formatNumber value="${(i+length)/length}" maxFractionDigits="0" /></a>
				</li>
				</c:forEach>

				<c:if test="${start+length lt imageCount}">
				<li><a id="next" href='${baseUrl}/imagesb?start=${start+length}&length=${length}&${q}'>Next</a></li>
				</c:if>
				<c:if test="${start+length gt imageCount}">
				<li class="disabled"><a>Next</a></li>
				</c:if>				
			</ul>
		</div>
		<script>$('div#pagination').hide();</script>
		</c:if>

    </jsp:body>	

</t:genericpage>

