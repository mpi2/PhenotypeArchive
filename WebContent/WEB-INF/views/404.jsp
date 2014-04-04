<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	
	<jsp:attribute name="title">Page Not Found</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>	
	
	
	<jsp:body>
		<div class="region region-content">              
			<div class="block block-system">
				<div class="content">
					<div class="node">
						<h1>Oops! An error has occurred.</h1>
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
						<div class="clear"> </div>
		<div class="container">
			<p class="muted credit"><a href="${initParam['baseUrl']}/data/search">Click here to search IMPC.</a></p>
		</div>						
					</div>
				</div>
			</div>
		</div>
	</jsp:body>
	
</t:genericpage>

