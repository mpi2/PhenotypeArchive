<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:genericpage>
<jsp:attribute name="title">Image Comparator2</jsp:attribute>
<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
 <jsp:attribute name="header">
<script src="${baseUrl}/js/imaging/imageComparator.js"></script>
<script>
var baseUrl="${baseUrl}";
var solrUrl='${solrUrl}';
var drupalBaseUrl = "${drupalBaseUrl}";
var mediaBaseUrl = "${mediaBaseUrl}";
var impcMediaBaseUrl="${impcMediaBaseUrl}";
</script>
</jsp:attribute>
 <jsp:body>
 <c:if  test="${fn:length(param['ctrImgId'])==0 && fn:length(param['expImgId'])==0}">
 <h1 class="title">No Images Selected - please select some experimental and/or control images to view.</h1>
 </c:if>
 
 <c:if test="${fn:length(param['ctrImgId'])>0 }">
 <div class="half">
<div  id="control" src="${impcMediaBaseUrl}/img_detail/${param['ctrImgId']}/">

<div  id="navControl" src="imageNavigator?controlOrExp=control"></div>
</div>
</div>
</c:if>

 <c:if test="${fn:length(param['expImgId'])>0 }">
<div class="half">
<div id="experimental">
<div id="expControl"></div>
</div>
</div>
</c:if>

<div class="region region-content">
                <div class="block">
                    <div class="content">
                    	<div class="section">
                               
    								<div class="caption">
    										<div class="btn" id="prev">< < Previous</div>
    										<div class="btn" id="next">Next > ></div>
    											<div id="annotations" name="annotations">
    											</div>
    								</div>
								
						</div>
					</div>
				</div>
</div>

</jsp:body>
</t:genericpage>