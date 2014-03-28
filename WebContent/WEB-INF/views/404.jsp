<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	
	<jsp:attribute name="title">Gene details for ${gene.name}</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>	
	
	<jsp:body>
		<div class="region region-content">              
			<div class="block block-system">
				<div class="content">
					<div class="node">
						<h1>Oops! This page could not be found.</h1>
						<p class="lead">This could be due to, eg.</p>
						<ul>
							<li>A mis-typed address</li>
							<li>An out-of-date bookmark</li>
							<li>The page no longer exists</li>        
						</ul>
						<p><a href="${pageContext.request.contextPath}">Click here to search IMPC.</a></p>
						<div class="clear"> </div>
					</div>
				</div>
			</div>
		</div>
	</jsp:body>
	
</t:genericpage>
