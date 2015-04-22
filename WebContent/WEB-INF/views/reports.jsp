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
							<h1 class="title" id="top">IMPC Data Reports</h1>	 
				
							<div class="section">
								<div class="inner">
									<p> <a href="${baseUrl}/reports/dataOverview">Data Overview</a> </p>
									<p> <a href="${baseUrl}/reports/centerProgressCsv">Procedure Completeness </a> </p>
									<p> <a href="${baseUrl}/reports/viability">Viability</a> </p>
									<p> <a href="${baseUrl}/reports/fertility">Fertility report</a> </p>
									<p> <a href="${baseUrl}/reports/mpCallDistribution">Distribution of phenotype hits</a> </p>
									<p> <a href="${baseUrl}/reports/hitsPerLine">Hits per line</a> </p>
									<p> <a href="${baseUrl}/reports/hitsPerPP">Hits per parameter and procedure</a> </p>
									<p> <a href="${baseUrl}/reports/sexualDimorphism">Sexual Dimorphism No Body Weight </a> </p>
									<p> <a href="${baseUrl}/reports/sexualDimorphismWithBodyWeight">Sexual Dimorphism With Body Weight</a> </p>
									<p> <a href="${baseUrl}/reports/gene2go">GO annotations to phenotyped IMPC genes tool</a> </p>
									<p>	<a href="${baseUrl}/reports/getLaczSpreadsheet">LacZ Expression</a> </p>
									<p> <a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-3.0/csv/">All genotype-phenotype data</a></p>
									<p>	<a href="${baseUrl}/reports/getBmdIpdtt?param=IMPC_DXA_005_001">BMD stats (Bone Mineral Content excluding skull)</a></p>
									<p>	<a href="${baseUrl}/reports/getBmdIpdtt?param=IMPC_IPG_010_001">IpGTT stats (Fasted blood glucose concentration)</a></p>
									<p>	<a href="${baseUrl}/reports/getBmdIpdtt?param=IMPC_IPG_012_001">IpGTT stats (Area under the curve glucose response)</a></p>
								</div>
							</div>
							
					</div>
				</div>
			</div>
		</div>
		
		
		
	</jsp:body>
		
	</t:genericpage>
