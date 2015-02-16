<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Data Reports</jsp:attribute>
	
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Reports</jsp:attribute>

	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>
	
	<jsp:body>
			<div class="region region-content">
				<div class="block block-system">
					<div class="content">
						<div class="node node-gene">
							<h1 class="title" id="top">IMPC Release Notes</h1>	 
				
							<div class="section">
								<div class="inner">
									<p>	<a href="${baseUrl}/getLaczSpreadsheet">LacZ Expression</a> </p>
									<p> <a href="${baseUrl}/centerProgressCsv">Procedure Completeness</a> </p>
									<p> <a href="${baseUrl}/genego">GO annotations to IMPC genes</a> </p>
								</div>
							</div>
							
					</div>
				</div>
			</div>
		</div>
		
		
		
	</jsp:body>
		
	</t:genericpage>
