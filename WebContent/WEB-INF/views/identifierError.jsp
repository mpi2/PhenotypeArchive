<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Identifier Error</title>
	
</head>

<body>
	<div id="wrap">
		<div class="container">
			<img src="${baseUrl}/img/impc.jpg">
			<div class="page-header">
				<h2>Oops! ${acc} is not a valid ${type} identifier.</h2>
			</div>
			<p class="lead">Example of a valid page:</p>
			<ul>
				<li><a href="${baseUrl}${exampleURI}">${baseUrl}${exampleURI}</a></li>
			</ul>
		</div>
    </div>

	<div id="footer">
		<div class="container">
			<p class="muted credit"><a href="${pageContext.request.contextPath}">Click here to search IMPC.</a></p>
		</div>
	</div>

	<script type='text/javascript' src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	
</body>
</html>