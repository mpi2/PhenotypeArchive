<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>File Not Found</title>
	
</head>

<body>
	<div id="wrap">
		<div class="container">
			<img src="${initParam['baseUrl']}/img/impc.jpg">
			<div class="page-header">
				<h1>Oops! An error has occurred.</h1>
			</div>
			<c:choose>
				<c:when test="${empty errorMessage}">
					<p class="lead">This could be due to, e.g.</p>
					<ul>
						<li>A mis-typed address</li>
						<li>An out-of-date bookmark</li>
						<li>The page no longer exists</li>
						<li>Technical issues</li>
					</ul>
				</c:when>
				<c:otherwise>
					<p class="lead">${errorMessage}</p>
				</c:otherwise>
			</c:choose>
			<br >
		</div>
    </div>

	<div id="footer">
		<div class="container">
			<p class="muted credit"><a href="${pageContext.request.contextPath}">Click here to search IMPC.</a></p>
		</div>
	</div>

   
</body>
</html>

