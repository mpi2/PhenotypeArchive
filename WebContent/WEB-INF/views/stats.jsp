<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Stats for ${gene.name}</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/search#sort=marker_symbol asc&q=*:*&core=gene">Genes</a> <c:if
			test="${not empty gene.subtype.name }">&raquo; <a
				href='${baseUrl}/search#fq=marker_type:"${gene.subtype.name}"&q=*:*&core=gene'>${gene.subtype.name}</a>
		</c:if> &raquo; ${gene.symbol}</jsp:attribute>

	<jsp:attribute name="header">

	<script type="text/javascript">
		var gene_id = '${acc}';
	</script>

	<!--    extra header stuff goes here such as extra page specific javascript -->
	<!-- highcharts -->
<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>
 <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js'></script> 
<script src="${baseUrl}/js/charts/exporting.js"></script>
	

	

<style>
</style>

    </jsp:attribute>

	<jsp:body>
        
		<div class='topic'>Gene: ${gene.symbol}</div>
		<c:if test="${statsError}">
					<div class="alert alert-error">
							  				<strong>Error:</strong> A stats error occured - results on this page maybe incorrect.
					</div>
		</c:if>
	

<!-- time series charts here-->

<c:forEach var="timeChart" items="${timeSeriesChartsAndTables}" varStatus="timeLoop">
 <%-- ${loop.count  % 2} --%>
	<c:if test = "${timeLoop.count  % 2!=0}">
		<div class="row-fluid dataset">  
		 <div class="row-fluid ">
		 		<div class="container span6"><h4><!--  style="background-color:lightgrey;"  -->Allele -  <t:formatAllele>${timeSeriesMutantBiologicalModels[timeLoop.index].alleles[0].symbol}</t:formatAllele> <span class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${timeSeriesMutantBiologicalModels[timeLoop.index].geneticBackground}</span></h4>
		 		</div>
		 		 <c:if test="${fn:length(timeSeriesChartsAndTables) > (timeLoop.index+1)}">
		 		 <div class="container span6"><h4><!--  style="background-color:lightgrey;"  -->Allele -  <t:formatAllele>${timeSeriesMutantBiologicalModels[timeLoop.index+1].alleles[0].symbol}</t:formatAllele> <span class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${timeSeriesMutantBiologicalModels[timeLoop.index+1].geneticBackground}</span></h4>
		 		</div>
		 		</c:if>
 		</div>
 		<div class="row-fluid">
 	</c:if>
  				 <div class="container span6">
								<div id="timeChart${timeLoop.count}"
									style="min-width: 400px; height: 450px; margin: 0 auto">
								</div>
								<script type="text/javascript">
								${timeChart.chart}
								</script>
					</div>
<c:if test = "${(timeLoop.count % 2==0) || timeLoop.count eq fn:length(timeSeriesChartsAndTables)}">
		 </div>
		 </div>
</c:if>
</c:forEach>



<jsp:include page="categoricalStatsFrag.jsp"/>

<jsp:include page="unidimensionalStatsFrag.jsp"/>


		
 




    </jsp:body>
</t:genericpage>
