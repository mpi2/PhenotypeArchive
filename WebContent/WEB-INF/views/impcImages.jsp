<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

    <jsp:attribute name="title">${queryTerms} IMPC Images Information b</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&facet=impc_images">IMPC Images</a> &raquo; Results</jsp:attribute>

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
        <script>$.autopager({link: '#next', content: '#grid'});</script>

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
                        <c:if test="${imageCount ne 0}"><h1 class="title" id="top">${imageCount} images for ${titleString}</h1></c:if>
                            <div  class="section">
                                <div class="inner">
                                    <div class="accordion-body" style="display: block">
                                        <div id="grid">
                                            <ul>
                                            <c:forEach var="image" items="${images}" varStatus="status">
                                                <t:impcimgdisplay2 img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}" pdfThumbnailUrl="${pdfThumbnailUrl}"></t:impcimgdisplay2>
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
                                            <li><a href='${baseUrl}/impcImages/images?start=${start-length}&length=${length}${qBaseStr}'>Prev</a></li>
                                            </c:if>

                                        <c:forEach begin="0" end="${imageCount}" step="${length}" var="i">
                                            <li <c:if test="${start eq i}">class="active"</c:if>>
                                                <a href='${baseUrl}/impcImages/images?start=<c:if test="${i ne 0}">${i-1}</c:if><c:if test="${i eq 0}">${i}</c:if>&length=${length}${qBaseStr}'><fmt:formatNumber value="${(i+length)/length}" maxFractionDigits="0" /></a>
                                                </li>
                                        </c:forEach>

                                        <c:if test="${start+length lt imageCount}">
                                            <li><a id="next" href='${baseUrl}/impcImages/images?start=${start+length}&length=${length}${qBaseStr}'>Next</a></li>
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

