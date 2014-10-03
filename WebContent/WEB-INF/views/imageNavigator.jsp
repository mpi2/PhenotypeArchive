<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<HTML>
<HEAD>
<TITLE>A document with anchors with specific targets</TITLE>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
<script src="${baseUrl}/js/imaging/imageComparator.js"></script>
<script type="text/javascript">
var mediaBaseUrl = "${mediaBaseUrl}";
console.log(mediaBaseUrl);
var solrUrl='${solrUrl}';
</script>
<link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
</HEAD>
<BODY>
 	<A id="prev">Previous</A>.
    <A id="next">Next</A>.
    
    
</BODY>
</HTML>