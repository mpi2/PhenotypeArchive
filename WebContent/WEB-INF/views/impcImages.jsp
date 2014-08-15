<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	 <jsp:attribute name="title">${queryTerms} IMPC Images Information b</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#fq=annotationTermId:M* OR expName:* OR symbol:* OR annotated_or_inferred_higherLevelMaTermName:* OR annotatedHigherLevelMpTermName:*&core=images">Images</a> &raquo; Results</jsp:attribute>
	
    <jsp:attribute name="header">
		
		<style>
		table th{border-bottom:1px solid #CDC8B1;}
		table tr:last-child th{border-bottom:none;}
		.thumbnail{margin-bottom:30px;}
		.thumbnail p{line-height:0.75em;}
		</style>

		<script src="${baseUrl}/js/vendor/jquery.autopager-1.0.0.js"></script>
		<script src="${baseUrl}/js/imaging/imageUtils.js"></script>
    </jsp:attribute>


	<jsp:attribute name="addToFooter">
		<script>$.autopager({link: '#next',content: '#grid'});</script>
		
		<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
            	<c:if test="${imageCount ne 0}">
                	<li><a href="#top">Images</a></li>
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
    
   <c:if test="${solrImagesError ne null}"><h4>There is an error the image index is down please contact the IMPC if this error persists</h4></c:if>
		<c:if test="${imageCount eq 0}"><h4>There are no images for ${breadcrumbText}</h4></c:if>
		<c:if test="${imageCount ne 0}"><h1 class="title" id="top">${imageCount} images for ${breadcrumbText}</h1></c:if>
<div  class="section">
		<div class="inner">
                <div class="accordion-body" style="display: block">
                         <div id="grid">
								<ul>
										<c:forEach var="image" items="${images}" varStatus="status">
											<li>
											 <!-- <img id="752" src="http://172.22.68.222:4080/webgateway/render_thumbnail/1278/200" alt="image" title="30910.bmp" style="width: 200px;"> -->
													<%-- <a href="http://172.22.68.222:4080/webgateway/render_image/${image.omeroId}">
													<img id="${image.omeroId}" src="http://172.22.68.222:4080/webgateway/render_thumbnail/${image.omeroId}/96" alt="image" title="${image.omeroId}" style="width: 96px;">
													</a> --%>
													<t:impcimgdisplay img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay>
											</li>
										</c:forEach>
								</ul>
							</div>
				</div>
			</div>

		<c:if test="${imageCount gt start+length || start ne 0}">
		<div id="pagination" class="pagination">
			<ul>
				<c:if test="${start eq 0}">
				<li class="disabled"><a>Prev</a></li>
				</c:if>
				<c:if test="${start ne 0}">
				<li><a href='${baseUrl}/impcImages?start=${start-length}&length=${length}&${q}'>Prev</a></li>
				</c:if>

				<c:forEach begin="0" end="${imageCount}" step="${length}" var="i">
				<li <c:if test="${start eq i}">class="active"</c:if>>
				<a href='${baseUrl}/impcImages?start=<c:if test="${i ne 0}">${i-1}</c:if><c:if test="${i eq 0}">${i}</c:if>&length=${length}&${q}'><fmt:formatNumber value="${(i+length)/length}" maxFractionDigits="0" /></a>
				</li>
				</c:forEach>

				<c:if test="${start+length lt imageCount}">
				<li><a id="next" href='${baseUrl}/impcImages?start=${start+length}&length=${length}&${q}'>Next</a></li>
				</c:if>
				<c:if test="${start+length gt imageCount}">
				<li class="disabled"><a>Next</a></li>
				</c:if>				
			</ul>
		</div>
		<script>$('div#pagination').hide();</script>
		</c:if>
</div>
</div>
</div>
</div>
</div>
    </jsp:body>	

</t:genericpage>

