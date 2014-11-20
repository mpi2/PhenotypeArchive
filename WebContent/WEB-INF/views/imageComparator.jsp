<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Image Comparator2</title>
<link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
<script>
var baseUrl="${baseUrl}";
var solrUrl='${solrUrl}';
var drupalBaseUrl = "${drupalBaseUrl}";
var mediaBaseUrl = "${mediaBaseUrl}";
var impcMediaBaseUrl="${impcMediaBaseUrl}";
</script>
</head>
 <c:if  test="${fn:length(param['ctrImgId'])==0 && fn:length(param['expImgId'])==0}">
 <h1 class="title">No Images Selected - please select some experimental and/or control images to view.</h1>
 <!-- <div class="section">
 <div class="inner"> 
 </div></div> -->
 </c:if>
 <c:choose>
 <c:when  test="${fn:length(param['ctrImgId'])>0 && fn:length(param['expImgId'])>0}">
  <frameset cols="50%,50%">
 </c:when>
 <c:otherwise>
 <frameset cols="100%">
  
 </c:otherwise>
 </c:choose>
 
 <c:if test="${fn:length(param['ctrImgId'])>0 }">
 <frameset rows=85%,15%>
<frame name="control" id="control" src="${impcMediaBaseUrl}/img_detail/${param['ctrImgId']}/" name="top">

<frame name="navControl" id="navControl" src="imageNavigator?controlOrExp=control"></frame>
</frameset>
</c:if>

 <c:if test="${fn:length(param['expImgId'])>0 }">
<frameset rows=85%,15%>
<frame name="experimental" id="experimental" src="${impcMediaBaseUrl}/img_detail/${param['expImgId']}/" name="top">
<frame name="expControl" id="expControl" src="imageNavigator?controlOrExp=experimental"></frame>
</frameset>
</c:if>

</frameset>



</html>