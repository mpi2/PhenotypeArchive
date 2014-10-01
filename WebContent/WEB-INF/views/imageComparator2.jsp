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

</head>
 
 
 <frameset cols="50%,50%">
 <frameset rows=90%,10%>
<frame name="control" id="control" src="http://ves-ebi-cf/omero/webgateway/img_detail/5817/" name="top">

<frame src="imageControlNavigator"></frame>
</frameset>
<frameset rows=90%,10%>
<frame name="experimental" id="experimental" src="http://ves-ebi-cf/omero/webgateway/img_detail/5817/" name="top">
<frame src="imageExperimentalNavigator"></frame>
</frameset>
</frameset>



</html>